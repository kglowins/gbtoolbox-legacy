package com.github.kglowins.gbtoolbox.kindergarten.io.dream3d;

import com.github.kglowins.gbtoolbox.kindergarten.util.tuples.DoubleTriple;
import com.github.kglowins.gbtoolbox.kindergarten.util.tuples.IntPair;
import com.github.kglowins.gbtoolbox.kindergarten.util.tuples.LongTriple;
import com.github.kglowins.gbtoolbox.utils.EulerAngles;
import com.github.kglowins.gbtoolbox.utils.UnitVector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;
import javax.swing.SwingWorker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import static com.github.kglowins.gbtoolbox.kindergarten.io.dream3d.Dream3DFile.pointGroupFromCrystalStructure;


@Slf4j
public class Dream3DToGbdatImporter extends SwingWorker<Void, Void> {

    public static final String STATUS_MESSAGE_PROPERTY = "statusMessage";
    private static final String WHITESPACE = "\\s+";

    private Dream3DFile dream3DFile;
    private Dream3DDataSetPaths dataSetPaths;
    private String outputFilePath;
    private Dream3DImportSettings importSettings;

    @Getter
    private int[] phaseCrystalStructures;
    @Getter
    private int numberOfPhases;
    @Getter
    private int[] grainPhases;
    @Getter
    private int numberOfGrains;
    @Getter
    private int[] numberOfGrainsPerPhase;

    @Getter
    private DescriptiveStatistics faceStatistics;

    @Getter
    private int numberOfSurfaceGrains;
    @Getter
    private int numberOfSurfaceFaces;
    @Getter
    private int numberOfNonInteriorFaces;

    private float[][] nodeCoordinates;
    private int numberOfOmittedFaces;

    private Dream3DToGbdatImporter(Dream3DFile dream3DFile, Dream3DDataSetPaths dataSetPaths) throws Exception {
        this.dream3DFile = dream3DFile;
        this.dataSetPaths = dataSetPaths;

        phaseCrystalStructures = (int[]) dream3DFile.readDataSet(dataSetPaths.getPhaseCrystalStructures());
        numberOfPhases = phaseCrystalStructures.length;
        grainPhases = (int[]) dream3DFile.readDataSet(dataSetPaths.getGrainPhases());
        numberOfGrains = grainPhases.length;
        numberOfGrainsPerPhase = getNumberOfGrainsPerPhase(numberOfPhases, numberOfGrains, grainPhases);

        log.info("Number of phases: {}", numberOfPhases);
        log.info("Number of grains: {}", numberOfGrains);
        IntStream.range(0, numberOfPhases).forEach(phaseId ->
            log.info("Phase #{}: {} grains, symmetry {}", phaseId, numberOfGrainsPerPhase[phaseId],
                phaseCrystalStructures[phaseId]));
    }

    public static Dream3DToGbdatImporter from(Dream3DFile dream3DFile, Dream3DDataSetPaths dataSetPaths) throws Exception {
        return new Dream3DToGbdatImporter(dream3DFile, dataSetPaths);
    }

    public Dream3DToGbdatImporter to(String outputFilePath, Dream3DImportSettings importSettings) {
        this.outputFilePath = outputFilePath;
        this.importSettings = importSettings;
        return this;
    }

    @Override
    protected Void doInBackground() throws Exception {
        firePropertyChange(STATUS_MESSAGE_PROPERTY, "", "Reading Dream.3D File");

        byte[] surfaceGrains = (byte[]) dream3DFile.readDataSet(dataSetPaths.getSurfaceGrains());
        numberOfSurfaceGrains = getNumberOfSurfaceGrains(surfaceGrains);

        EulerAngles[] grainEulerAngles = readGrainEulerAngles(dataSetPaths.getGrainEulerAngles(), numberOfGrains);

        double[] faceAreas = (double[]) dream3DFile.readDataSet(dataSetPaths.getFaceAreas());
        int numberOfFaces = faceAreas.length;

        List<IntPair> faceGrainIds = readFaceGrainIds(dataSetPaths.getFaceGrainIds(), numberOfFaces);

        UnitVector[] faceNormals = readFaceNormals(dataSetPaths.getFaceNormals(), numberOfFaces);

        byte[] nodeTypes = (byte[]) dream3DFile.readDataSet(dataSetPaths.getNodeTypes());

        List<LongTriple> nodesPerFace = readNodesPerFace(dataSetPaths.getFaceNodes(), numberOfFaces);

        Map<IntPair, Set<Integer>> grainIdsFaceIds = groupFacesByGrainIds(numberOfFaces, faceGrainIds,
            importSettings, nodeTypes, nodesPerFace, surfaceGrains);

       // log.info("Grouping faces by grain ids took: {} millis.", currentTimeMillis() - startTime);
        log.info("Number of boundaries: {}", grainIdsFaceIds.size());
        faceStatistics = new DescriptiveStatistics();
        grainIdsFaceIds.forEach((grainIds, faceIds) -> faceStatistics.addValue(faceIds.size()));
        log.info("Min. number of faces: {}", faceStatistics.getMin());
        log.info("Max. number of faces: {}", faceStatistics.getMax());
        log.info("Mean number of faces: {}", faceStatistics.getMean());
        log.info("Standard deviation: {}", faceStatistics.getStandardDeviation());
        log.info("Median: {}", faceStatistics.getPercentile(50));
        log.info("Skewness: {}", faceStatistics.getSkewness());
        log.info("Kurtosis: {}", faceStatistics.getKurtosis());

        //for simplification
        nodeCoordinates = readNodeCoordinates(dataSetPaths.getNodeCoordinates());
        List<DoubleTriple> vtkVertices = new ArrayList<>();
        List<Triple<Integer, Integer, Integer>> vtkTriangleVertices = new ArrayList<>();
        List<IntPair> vtkGrainIds = new ArrayList<>();
        List<Integer> vtkBoundaryIds = new ArrayList<>();

        log.debug("isSimplifyMesh = {}", importSettings.isSimplifyMesh());
        double numberOfBoundaries = 0d;
        int facesSaved = 0;
        int statusUpdateInterval = Math.max(1, (numberOfFaces - numberOfOmittedFaces) / 50);


        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df7 = new DecimalFormat("0.#######", otherSymbols);
        DecimalFormat df4 = new DecimalFormat("0.####", otherSymbols);

        PrintWriter wrt = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePath)));
        wrt.println("# This file was created by GBToolbox");
        wrt.println("# It contains boundary parameters imported from DREAM.3D output files");
        wrt.println("EXP");

        switch(pointGroupFromCrystalStructure(phaseCrystalStructures[importSettings.getPhaseId()])) {
            case M3M: wrt.println("m-3m"); break;
            case _6MMM: wrt.println("6/mmm"); break;
            case MMM: wrt.println("mmm"); break;
            default: throw new IOException("Something went wrong with selection of the point group");
        }
        wrt.println("L_PHI1 L_PHI L_PHI2 R_PHI1 R_PHI R_PHI2 ZENITH AZIMUTH CORRELAT AREA");




            int boundaryId = 0;
            for (Entry<IntPair, Set<Integer>> boundary : grainIdsFaceIds.entrySet()) {
                int leftGrainId = boundary.getKey().getLeft();
                int rightGrainId = boundary.getKey().getRight();
                Set<Integer> faceIds = boundary.getValue();

                if (!importSettings.isSimplifyMesh()) {

                    for (int faceId : faceIds) {
                        if (isCancelled()) {
                            return null;
                        }
                        if (facesSaved % statusUpdateInterval == 0) {
                            updateProgress("Saving Faces", facesSaved, numberOfFaces - numberOfOmittedFaces);
                        }
                        numberOfBoundaries += 1d / (double) faceIds.size();

                        wrt.println(
                            df4.format( Math.toDegrees( grainEulerAngles[leftGrainId].phi1())) + " "+
                                df4.format( Math.toDegrees( grainEulerAngles[leftGrainId].Phi()))+ " "+
                                df4.format( Math.toDegrees( grainEulerAngles[leftGrainId].phi2()))+ " "+

                                df4.format( Math.toDegrees( grainEulerAngles[rightGrainId].phi1())) + " "+
                                df4.format( Math.toDegrees( grainEulerAngles[rightGrainId].Phi()))+ " "+
                                df4.format( Math.toDegrees( grainEulerAngles[rightGrainId].phi2()))+ " "+

                                df4.format( Math.toDegrees( faceNormals[faceId].zenith()))+ " "+
                                df4.format( Math.toDegrees( faceNormals[faceId].azimuth()))+ " "+

                                faceIds.size() + " " +

                                df7.format(faceAreas[faceId]) );

                        facesSaved++;
                    }
                } else { // when isSimplifyMesh is true
                    boundaryId++;
                    generateInputForQSlim(faceIds, nodesPerFace);
                    runQSlim(faceIds.size(), importSettings);

                    int numberOfFacesAfterSimplification = getNumberOfFacesAfterSimplification();
                    log.debug("before {} after {}, {} {}", faceIds.size(), numberOfFacesAfterSimplification, importSettings.getSimplificationRate(), importSettings.getFacesLowerLimit());

                    BufferedReader reader = new BufferedReader(new FileReader("QSlimOutput.smf"));
                    String line;
                    List<DoubleTriple> vertexCoords = new ArrayList<>();

                    int lastVtkSize = vtkVertices.size();


                    while ((line = reader.readLine()) != null) {
                        String[] words = line.trim().split(WHITESPACE);

                        if ("v".equals(words[0])) {
                            double x = Double.parseDouble(words[1]);
                            double y = Double.parseDouble(words[2]);
                            double z = Double.parseDouble(words[3]);
                            vertexCoords.add(DoubleTriple.of(x, y, z));
                            vtkVertices.add(DoubleTriple.of(x, y, z));
                        }

                        if ("f".equals(words[0])) {
                            try {
                                int n1 = Integer.parseInt(words[1]) - 1;
                                int n2 = Integer.parseInt(words[2]) - 1;
                                int n3 = Integer.parseInt(words[3]) - 1;

                                final double x1 = vertexCoords.get(n1).getX();
                                final double y1 = vertexCoords.get(n1).getY();
                                final double z1 = vertexCoords.get(n1).getZ();

                                final double x2 = vertexCoords.get(n2).getX();
                                final double y2 = vertexCoords.get(n2).getX();
                                final double z2 = vertexCoords.get(n2).getZ();

                                final double x3 = vertexCoords.get(n3).getX();
                                final double y3 = vertexCoords.get(n3).getY();
                                final double z3 = vertexCoords.get(n3).getZ();

                                final double X = y3* (z1 - z2) + y1* (z2 - z3) + y2* (-z1 + z3);
                                final double Y = x3* (-z1 + z2) + x2* (z1 - z3) + x1* (-z2 + z3);
                                final double Z = x3* (y1 - y2) + x1* (y2 - y3) + x2* (-y1 + y3);

                                final double area = 0.5d * Math.sqrt(X*X + Y*Y + Z*Z);

                                final UnitVector v1 = new UnitVector();
                                final UnitVector v2 = new UnitVector();
                                v1.set( x2-x1, y2-y1, z2-z1 );
                                v2.set( x3-x1, y3-y1, z3-z1 );

                                v1.cross(v2);

                                vtkTriangleVertices.add(Triple.of(lastVtkSize+n1, lastVtkSize+n2, lastVtkSize+n3));
                                vtkGrainIds.add(IntPair.of(leftGrainId, rightGrainId));
                                vtkBoundaryIds.add(boundaryId);

                                wrt.println(
                                    df4.format( Math.toDegrees( grainEulerAngles[leftGrainId].phi1())) + " "+
                                        df4.format( Math.toDegrees( grainEulerAngles[leftGrainId].Phi()))+ " "+
                                        df4.format( Math.toDegrees( grainEulerAngles[leftGrainId].phi2()))+ " "+

                                        df4.format( Math.toDegrees( grainEulerAngles[rightGrainId].phi1())) + " "+
                                        df4.format( Math.toDegrees( grainEulerAngles[rightGrainId].Phi()))+ " "+
                                        df4.format( Math.toDegrees( grainEulerAngles[rightGrainId].phi2()))+ " "+

                                        df4.format( Math.toDegrees( v1.zenith()))+ " "+
                                        df4.format( Math.toDegrees( v1.azimuth()))+ " "+

                                        numberOfFacesAfterSimplification + " " +

                                        df7.format(area) );

                                facesSaved++;

                            } catch(IllegalArgumentException e) {
                                log.error("", e);
                                // trianglesSkipped++;
                                continue;
                            }
                        }

                    }
                    reader.close();
                }
            }
            updateProgress("Saving Faces", ". Import completed", numberOfFaces - numberOfOmittedFaces, numberOfFaces - numberOfOmittedFaces);
        wrt.close();


        if (importSettings.isSimplifyMesh()) {
            writeVtk(vtkVertices, vtkTriangleVertices, vtkGrainIds, vtkBoundaryIds);
        }

        //log.info("Saving boundaries to csv file took: {} millis.", currentTimeMillis() - startTime);
        log.info("Number of boundaries (cross-check): {}", (int) numberOfBoundaries);
        log.info("Number of faces: {}", numberOfFaces);
        log.info("Number of non-interior faces: {}", numberOfNonInteriorFaces);
        log.info("Number of surface grains: {}", numberOfSurfaceGrains);
        log.info("Number of surface faces: {}", numberOfSurfaceFaces);
        return null;

    }

    private int[] getNumberOfGrainsPerPhase(int numberOfPhases, int numberOfGrains, int[] grainPhases) {
        int[] grainsPerPhase = IntStream.range(0, numberOfPhases).map(phaseId -> 0).toArray();
        IntStream.range(0, numberOfGrains).forEach(grainId -> {
            int grainPhaseId = grainPhases[grainId];
            grainsPerPhase[grainPhaseId]++;
        });
        return grainsPerPhase;
    }

    private int getNumberOfSurfaceGrains(byte[] surfaceGrains) {
        int surfaceGrainsCounter = 0;
        for (byte b : surfaceGrains) {
            surfaceGrainsCounter += b;
        }
        return surfaceGrainsCounter;
    }

    private EulerAngles[] readGrainEulerAngles(String grainEulerAnglesPath, int numberOfGrains) throws Exception {
        float[] rawGrainEulerAngles = (float[]) dream3DFile.readDataSet(grainEulerAnglesPath);
        EulerAngles[] grainEulerAngles = new EulerAngles[numberOfGrains];
        IntStream.range(0, numberOfGrains).forEach(grainId -> {
            grainEulerAngles[grainId] = new EulerAngles();
            int idTimes3 = 3 * grainId;
            grainEulerAngles[grainId].set(
                rawGrainEulerAngles[idTimes3],
                rawGrainEulerAngles[idTimes3 + 1],
                rawGrainEulerAngles[idTimes3 + 2]);
        });
        return grainEulerAngles;
    }

    private List<IntPair> readFaceGrainIds(String faceGrainIdsPath, int numberOfFaces) throws Exception {
        int[] rawFaceGrainIds = (int[]) dream3DFile.readDataSet(faceGrainIdsPath);
        List<IntPair> faceGrainIds = new ArrayList<>(numberOfFaces);
        IntStream.range(0, numberOfFaces).forEach(faceId -> {
            int idTimes2 = 2 * faceId;
            faceGrainIds.add(IntPair.of(rawFaceGrainIds[idTimes2], rawFaceGrainIds[idTimes2 + 1]));
        });
        return faceGrainIds;
    }

    private UnitVector[] readFaceNormals(String faceNormalsPath, int numberOfFaces) throws Exception {
        double[] rawFaceNormals = (double[]) dream3DFile.readDataSet(faceNormalsPath);
        UnitVector[] faceNormals = new UnitVector[numberOfFaces];
        IntStream.range(0, numberOfFaces).forEach(faceId -> {
            int idTimes3 = 3 * faceId;
            faceNormals[faceId] = new UnitVector();
            faceNormals[faceId].set(
                rawFaceNormals[idTimes3],
                rawFaceNormals[idTimes3 + 1],
                rawFaceNormals[idTimes3 + 2]);
        });
        return faceNormals;
    }

    private List<LongTriple> readNodesPerFace(String nodesPerFacePath, int numberOfFaces) throws Exception {
        long[] rawNodesPerFace = (long[]) dream3DFile.readDataSet(nodesPerFacePath);
        List<LongTriple> nodesPerFace = new ArrayList<>(numberOfFaces);
        IntStream.range(0, numberOfFaces).forEach(faceId -> {
            int idTimes3 = 3 * faceId;
            nodesPerFace.add(LongTriple.of(rawNodesPerFace[idTimes3], rawNodesPerFace[idTimes3 + 1],
                rawNodesPerFace[idTimes3 + 2]));
        });
        return nodesPerFace;
    }

    private Map<IntPair, Set<Integer>> groupFacesByGrainIds(int numberOfFaces, List<IntPair> faceGrainIds,
        Dream3DImportSettings settings, byte[] nodeTypes, List<LongTriple> nodesPerFace, byte[] surfaceGrains) {

        Map<IntPair, Set<Integer>> grainsIdsToFaceIds = new HashMap<>();
        numberOfNonInteriorFaces = 0;
        numberOfSurfaceFaces = 0;
        numberOfOmittedFaces = 0;
        int statusUpdateInterval = Math.max(1, numberOfFaces / 50);

        for (int faceId = 0; faceId < numberOfFaces; faceId++) {
            if (isCancelled()) {
                break;
            }
            if (faceId % statusUpdateInterval == 0) {
                updateProgress("Grouping Faces by Grain Ids", faceId, numberOfFaces);
            }

            if (areOfSelectedPhase(faceGrainIds.get(faceId), grainPhases, settings.getPhaseId())) {
                int leftGrainId = faceGrainIds.get(faceId).getLeft();
                int rightGrainId = faceGrainIds.get(faceId).getRight();
                if (settings.isSkipTripleLines() && !isInteriorFace(nodeTypes, nodesPerFace.get(faceId))) {
                    numberOfNonInteriorFaces++;
                    numberOfOmittedFaces++;
                    continue;
                }
                if (settings.isSkipSurfaceGrains()
                    && areBothGrainsOnSurface(surfaceGrains, leftGrainId, rightGrainId)) {
                    numberOfSurfaceFaces++;
                    numberOfOmittedFaces++;
                    continue;
                }
                IntPair adjacentGrainIds = IntPair.ordered(leftGrainId, rightGrainId);
                if (!grainsIdsToFaceIds.containsKey(adjacentGrainIds)) {
                    grainsIdsToFaceIds.put(adjacentGrainIds, new HashSet<>());
                }
                grainsIdsToFaceIds.get(adjacentGrainIds).add(faceId);
            } else {
                numberOfOmittedFaces++;
            }
        }
        updateProgress("Grouping Faces by Grain Ids", numberOfFaces, numberOfFaces);
        log.debug("Number of omitted faces = {}", numberOfOmittedFaces);
        return grainsIdsToFaceIds;
    }

    private boolean areOfSelectedPhase(IntPair faceGrainIds, int[] grainPhases, int selectedPhase) {
        if (isOuterSurface(faceGrainIds)) {
            return false;
        }
        int leftGrainPhase = grainPhases[faceGrainIds.getLeft()];
        int rightGrainPhase = grainPhases[faceGrainIds.getRight()];
        return (leftGrainPhase == rightGrainPhase) && leftGrainPhase == selectedPhase;
    }

    private boolean isOuterSurface(IntPair faceGrainIds) {
        return faceGrainIds.getLeft() < 0 || faceGrainIds.getRight() < 0;
    }

    private boolean areBothGrainsOnSurface(byte[] surfaceGrains, int leftGrainId, int rightGrainId) {
        return surfaceGrains[leftGrainId] == 1 && surfaceGrains[rightGrainId] == 1;
    }

    private boolean isInteriorFace(byte[] nodeTypes, LongTriple nodesPerFace) {
        return nodeTypes[nodesPerFace.getLeft().intValue()] == 2
            && nodeTypes[nodesPerFace.getMiddle().intValue()] == 2
            && nodeTypes[nodesPerFace.getRight().intValue()] == 2;
    }

    private float[][] readNodeCoordinates(String nodeCoordinatesPath) throws Exception {
        float[] rawNodeCoordinates = (float[]) dream3DFile.readDataSet(nodeCoordinatesPath);
        float[][] coordinates = new float[rawNodeCoordinates.length / 3][3];
        IntStream.range(0, rawNodeCoordinates.length / 3).forEach(nodeId -> {
            int idTimes3 = 3 * nodeId;
            coordinates[nodeId][0] = rawNodeCoordinates[idTimes3];
            coordinates[nodeId][1] = rawNodeCoordinates[idTimes3 + 1];
            coordinates[nodeId][2] = rawNodeCoordinates[idTimes3 + 2];
        });
        log.debug("Read coordinates of {} nodes", coordinates.length);
        return coordinates;
    }

    private void generateInputForQSlim(Set<Integer> faceIds, List<LongTriple> nodesPerFace) throws IOException {
        // Path dream3DDirPath = dream3DFile.toPath().getParent();
        File qSlimInput = new File("QSlimInput.smf");//Paths.get(dream3DDirPath.toString(), "QSlimInput.smf").toFile();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(qSlimInput)));
        DecimalFormat df = new DecimalFormat("0.####", new DecimalFormatSymbols(Locale.US));

        Map<Long, Integer> nodeIdSequence = new HashMap<>();
        int sequence = -1;
        for (int faceId : faceIds) {
            Long nodeIdLeft = nodesPerFace.get(faceId).getLeft();
            Long nodeIdMiddle = nodesPerFace.get(faceId).getMiddle();
            Long nodeIdRight = nodesPerFace.get(faceId).getRight();
            if (!nodeIdSequence.containsKey(nodeIdLeft)) {
                nodeIdSequence.put(nodeIdLeft, ++sequence);
            }
            if (!nodeIdSequence.containsKey(nodeIdMiddle)) {
                nodeIdSequence.put(nodeIdMiddle, ++sequence);
            }
            if (!nodeIdSequence.containsKey(nodeIdRight)) {
                nodeIdSequence.put(nodeIdRight, ++sequence);
            }
        }

        int[] sequenceMapping = new int[nodeIdSequence.size()];
        nodeIdSequence.forEach((nodeId, index) -> {
            sequenceMapping[index] = nodeId.intValue();
        });

        for (int index = 0; index < sequenceMapping.length; index++) {
            int nodeId = sequenceMapping[index];
            writer.println(String.format("v %s %s %s",
                df.format(nodeCoordinates[nodeId][0]),
                df.format(nodeCoordinates[nodeId][1]),
                df.format(nodeCoordinates[nodeId][2])));
        }

        faceIds.forEach(faceId -> {
            Long nodeIdLeft = nodesPerFace.get(faceId).getLeft();
            Long nodeIdMiddle = nodesPerFace.get(faceId).getMiddle();
            Long nodeIdRight = nodesPerFace.get(faceId).getRight();
            writer.println(String.format("f %d %d %d",
                nodeIdSequence.get(nodeIdLeft) + 1,
                nodeIdSequence.get(nodeIdMiddle) + 1,
                nodeIdSequence.get(nodeIdRight) + 1));
        });
        writer.close();
    }

    private void runQSlim(int numberOfTriangles, Dream3DImportSettings importSettings) {
        try {
            int targetedNumberOfTriangles = (numberOfTriangles < importSettings.getFacesLowerLimit())
                ? numberOfTriangles
                : Math.max((int) (importSettings.getSimplificationRate() * numberOfTriangles), importSettings.getFacesLowerLimit());
            String[] command = new String[]{"QSlim.exe",
                "-O", "2",
                "-B", "999999999",
                "-W", "0",
                "-M", "smf",
                "-t", String.valueOf(targetedNumberOfTriangles),
                "-o", "QSlimOutput.smf",
                "QSlimInput.smf"
            };
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while (inputStream.readLine() != null) {
            }
            inputStream.close();
            while (errorStream.readLine() != null) {
            }
            errorStream.close();
            process.waitFor();
        }
        catch (Exception e) {
            log.error("Error during mesh simplification", e);
        }
    }

    private int getNumberOfFacesAfterSimplification(/*String qSlimOutputPath*/) { //TODO
        int numberOfTriangles = 0;
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader("QSlimOutput.smf"))) {
            while ((line = reader.readLine()) != null)   {
                String[] words = line.trim().split(WHITESPACE);
                if ("f".equals(words[0])) {
                    numberOfTriangles++;
                }
            }
        } catch (IOException e) {
            log.error("Failed to read QSlimOutput");
        }
        return numberOfTriangles;
    }

    private void writeVtk(List<DoubleTriple> vtkVertices, List<Triple<Integer, Integer, Integer>> vtkTriangleVertices,
        List<IntPair> vtkGrainIds, List<Integer> vtkBoundaryIds) {

        DecimalFormat df = new DecimalFormat("0.####", new DecimalFormatSymbols(Locale.US));

        try (PrintWriter vtk = new PrintWriter(new BufferedWriter(new FileWriter("QSlim.vtk")))) {

            vtk.println("# vtk DataFile Version 2.0");
            vtk.println(
                "Data set from QSlim/GBToolbox with simplified triangular mesh of boundary network");
            vtk.println("ASCII");
            vtk.println("DATASET POLYDATA");
            vtk.println("POINTS " + vtkVertices.size() + " float");

            vtkVertices.forEach(vertex -> vtk.println(df.format(vertex.getX()) + " "
                + df.format(vertex.getY()) + " " + df.format(vertex.getZ())));

            vtk.println(
                "POLYGONS " + vtkTriangleVertices.size() + " " + (4 * vtkTriangleVertices.size()));

            vtkTriangleVertices.forEach(triangle -> vtk.println(
                "3 " + triangle.getLeft() + " " + triangle.getMiddle() + " " + triangle.getRight()));

      /*
      	vtkGrains.println("POLYGONS " + (2*vtkTrisN1.size()) +" "+ (8*vtkTrisN1.size()));

						for(int i =0; i < vtkTrisN1.size(); i++)
						{
							vtkGrains.println("3 " + df4.format(vtkTrisN1.get(i)) + " " + df4.format(vtkTrisN2.get(i)) + " " +df4.format(vtkTrisN3.get(i)) );
							vtkGrains.println("3 " + df4.format(vtkTrisN1.get(i)) + " " + df4.format(vtkTrisN2.get(i)) + " " +df4.format(vtkTrisN3.get(i)) );
						}

						vtkGrains.println("CELL_DATA " + (2*vtkTrisN1.size()));
						vtkGrains.println("SCALARS GrainID int 1");
						vtkGrains.println("LOOKUP_TABLE default");

						for(int i = 0; i < vtkTrisN1.size(); i++)
						{
							vtkGrains.println(vtkGrainID.get(i));
							vtkGrains.println(vtkGrainID2.get(i));
						}
						vtkGrains.println("SCALARS BoundaryID int 1");
						vtkGrains.println("LOOKUP_TABLE default");

						for(int i = 0; i < vtkTrisN1.size(); i++)
						{
							vtkGrains.println(vtkBoundaryID.get(i));
							vtkGrains.println(vtkBoundaryID.get(i));
						}
       */

            vtk.println("CELL_DATA " + vtkBoundaryIds.size());
            vtk.println("SCALARS BoundaryID int 1");
            vtk.println("LOOKUP_TABLE default");

            vtkBoundaryIds.forEach(boundaryId -> vtk.println(boundaryId));

        } catch (IOException e) {
            log.error("", e);
        }
    }

    private void updateProgress(String message, int processed, int total) {
        updateProgress(message, "", processed, total);
    }

    private void updateProgress(String message, String suffix, int processed, int total) {
        String statusMessage = String.format("%s: %d/%d%s", message, processed, total, suffix);
        firePropertyChange(STATUS_MESSAGE_PROPERTY, "", statusMessage);
    }
}

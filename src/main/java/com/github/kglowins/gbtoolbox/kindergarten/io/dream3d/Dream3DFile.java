package com.github.kglowins.gbtoolbox.kindergarten.io.dream3d;

import com.github.kglowins.gbtoolbox.enums.PointGroup;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;

import static com.github.kglowins.gbtoolbox.enums.PointGroup.M3M;
import static com.github.kglowins.gbtoolbox.enums.PointGroup.MMM;
import static com.github.kglowins.gbtoolbox.enums.PointGroup._6MMM;
import static ncsa.hdf.object.FileFormat.FILE_TYPE_HDF5;
import static ncsa.hdf.object.FileFormat.READ;
import static ncsa.hdf.object.FileFormat.getFileFormat;

public class Dream3DFile {

    private FileFormat fileFormat;

    public static final Map<Integer, PointGroup> CRYSTAL_STRUCTURE_POINT_GROUP_MAP
        = new ImmutableMap.Builder<Integer, PointGroup>()
        .put(0, _6MMM)
        .put(1, M3M)
        .put(2, MMM)
        .build();

    private Dream3DFile(String dream3DFilePath) throws Exception {
        fileFormat = getFileFormat(FILE_TYPE_HDF5);
        fileFormat = fileFormat.createInstance(dream3DFilePath, READ);
        fileFormat.open();
    }

    public static Dream3DFile open(String dream3DFilePath) throws Exception {
        return new Dream3DFile(dream3DFilePath);
    }

    public static PointGroup pointGroupFromCrystalStructure(int crystalStructureId) {
        return CRYSTAL_STRUCTURE_POINT_GROUP_MAP.get(crystalStructureId);
    }

    public Dream3DDataSetPaths tryGuessDataSetPaths() {
        return Dream3DDataSetPaths.builder()
            .phaseCrystalStructures(tryMatchDefaultPath("CrystalStructures"))
            .grainPhases(tryMatchDefaultPath("Grain Data/Phases"))
            .surfaceGrains(tryMatchDefaultPath("SurfaceFeatures"))
            .grainEulerAngles(tryMatchDefaultPath("AvgEulerAngles"))
            .faceNormals(tryMatchDefaultPath("FaceNormals"))
            .faceGrainIds(tryMatchDefaultPath("FaceLabels"))
            .faceAreas(tryMatchDefaultPath("FaceAreas"))
            .nodeTypes(tryMatchDefaultPath("NodeType"))
            .faceNodes(tryMatchDefaultPath("SharedTriList"))
            .nodeCoordinates(tryMatchDefaultPath("SharedVertexList"))
            .build();
    }

    public Object readDataSet(String dataSetPath) throws Exception {
        Dataset dataset = (Dataset) fileFormat.get(dataSetPath);
        return dataset.read();
    }

    public Set<String> getDataSetPaths() {
        Set<String> dataSetPaths = new LinkedHashSet<>();
        Group root = (Group) ((DefaultMutableTreeNode) fileFormat.getRootNode()).getUserObject();
        walkGroupAndSavePaths(root, dataSetPaths);
        return dataSetPaths;
    }

    private static void walkGroupAndSavePaths(Group group, Set<String> dataSetPaths) {
        group.getMemberList().forEach(member -> {
            if (member instanceof Group) {
                Group subgroup = (Group) member;
                walkGroupAndSavePaths(subgroup, dataSetPaths);
            } else {
                String dataSetPath = member.getFullName();
                if (!dataSetPath.contains("Pipeline")) {
                    dataSetPaths.add(member.getFullName());
                }
            }
        });
    }

    private String tryMatchDefaultPath(String defaultPathSubstring) {
        return getDataSetPaths().stream()
            .filter(path -> path.contains(defaultPathSubstring))
            .findFirst().orElse("");
    }
}

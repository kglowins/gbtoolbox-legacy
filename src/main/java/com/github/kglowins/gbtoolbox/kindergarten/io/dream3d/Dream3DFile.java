package com.github.kglowins.gbtoolbox.kindergarten.io.dream3d;

import static com.github.kglowins.gbtoolbox.enums.PointGroup.M3M;
import static com.github.kglowins.gbtoolbox.enums.PointGroup.MMM;
import static com.github.kglowins.gbtoolbox.enums.PointGroup._6MMM;

import com.github.kglowins.gbtoolbox.enums.PointGroup;
import com.google.common.collect.ImmutableMap;
import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


public class Dream3DFile {

    private File dreamFile;

    public static final Map<Long, PointGroup> CRYSTAL_STRUCTURE_POINT_GROUP_MAP
        = new ImmutableMap.Builder<Long, PointGroup>()
        .put(0L, _6MMM)
        .put(1L, M3M)
        .put(2L, MMM)
        .build();

    private Dream3DFile(String dream3DFilePath) {
        dreamFile = new File(dream3DFilePath);
    }

    public HdfFile openHdf(){
        return new HdfFile(dreamFile);
    }

    public static Dream3DFile init(String dream3DFilePath) {
        return new Dream3DFile(dream3DFilePath);
    }

    public static PointGroup pointGroupFromCrystalStructure(long crystalStructureId) {
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

    public Object readDataSet(String dataSetPath) {
        try (HdfFile hdfFile = openHdf()) {
            Dataset dataset = hdfFile.getDatasetByPath(dataSetPath);
            return dataset.getData();
        }
    }

    public Set<String> getDataSetPaths() {
        Set<String> dataSetPaths = new LinkedHashSet<>();
        try (HdfFile hdfFile = openHdf()) {
            walkGroupAndSavePaths(hdfFile, dataSetPaths);
        }
        return dataSetPaths;
    }

    private static void walkGroupAndSavePaths(Group group, Set<String> dataSetPaths) {
        for (Node node : group) {
            String nodePath = node.getPath();
            if (!nodePath.contains("Pipeline")) {
                dataSetPaths.add(node.getPath());
            }
            if (node instanceof Group) {
                walkGroupAndSavePaths((Group) node, dataSetPaths);
            }
        }
    }

    private String tryMatchDefaultPath(String defaultPathSubstring) {
        return getDataSetPaths().stream()
            .filter(path -> path.contains(defaultPathSubstring))
            .findFirst().orElse("");
    }
}

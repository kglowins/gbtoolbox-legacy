package com.github.kglowins.gbtoolbox.kindergarten.io.dream3d;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Dream3DDataSetPaths {
    private String phaseCrystalStructures;
    private String grainPhases;
    private String surfaceGrains;
    private String grainEulerAngles;
    private String faceNormals;
    private String faceGrainIds;
    private String faceAreas;
    private String nodeTypes;
    private String faceNodes;
    private String nodeCoordinates;
}

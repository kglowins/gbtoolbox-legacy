package com.github.kglowins.gbtoolbox.kindergarten.io.dream3d;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Dream3DImportSettings {
  private int phaseId;
  private boolean skipSurfaceGrains;
  private boolean skipTripleLines;

  private boolean simplifyMesh;
  private String qSlimPath;
  private double simplificationRate;
  private int facesLowerLimit;
}
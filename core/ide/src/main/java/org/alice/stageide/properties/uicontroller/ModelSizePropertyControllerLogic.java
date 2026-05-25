package org.alice.stageide.properties.uicontroller;

import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.Dimension3;

final class ModelSizePropertyControllerLogic {
  enum SourceAxis {
    WIDTH,
    HEIGHT,
    DEPTH,
    NONE
  }

  static final class ResizerConfiguration {
    final boolean hasLinkAll;
    final boolean hasLinkXY;
    final boolean hasLinkXZ;
    final boolean hasLinkYZ;
    final boolean hasX;
    final boolean hasY;
    final boolean hasZ;
    final boolean hasIndependentX;
    final boolean hasIndependentY;
    final boolean hasIndependentZ;
    final boolean enableLinkAll;
    final boolean enableLinkXY;
    final boolean enableLinkXZ;
    final boolean enableLinkYZ;

    private ResizerConfiguration(boolean hasLinkAll, boolean hasLinkXY, boolean hasLinkXZ, boolean hasLinkYZ,
                                 boolean hasX, boolean hasY, boolean hasZ,
                                 boolean hasIndependentX, boolean hasIndependentY, boolean hasIndependentZ) {
      this.hasLinkAll = hasLinkAll;
      this.hasLinkXY = hasLinkXY;
      this.hasLinkXZ = hasLinkXZ;
      this.hasLinkYZ = hasLinkYZ;
      this.hasX = hasX;
      this.hasY = hasY;
      this.hasZ = hasZ;
      this.hasIndependentX = hasIndependentX;
      this.hasIndependentY = hasIndependentY;
      this.hasIndependentZ = hasIndependentZ;
      this.enableLinkAll = (hasIndependentX && hasLinkYZ) || (hasIndependentY && hasLinkXZ) || (hasIndependentZ && hasLinkXY) || (hasIndependentX && hasIndependentY && hasIndependentZ);
      this.enableLinkXY = hasIndependentX && hasIndependentY;
      this.enableLinkXZ = hasIndependentX && hasIndependentZ;
      this.enableLinkYZ = hasIndependentY && hasIndependentZ;
    }

    boolean initialXyValue() {
      return hasLinkXY && (!hasLinkAll || !enableLinkXY);
    }

    boolean initialXzValue() {
      return hasLinkXZ && (!hasLinkAll || !enableLinkXZ);
    }

    boolean initialYzValue() {
      return hasLinkYZ && (!hasLinkAll || !enableLinkYZ);
    }
  }

  private ModelSizePropertyControllerLogic() {
    throw new AssertionError();
  }

  static ResizerConfiguration analyzeResizers(Iterable<Resizer> resizers) {
    boolean hasLinkAll = false;
    boolean hasLinkXY = false;
    boolean hasLinkXZ = false;
    boolean hasLinkYZ = false;
    boolean hasX = false;
    boolean hasY = false;
    boolean hasZ = false;
    boolean hasIndependentX = false;
    boolean hasIndependentY = false;
    boolean hasIndependentZ = false;

    for (Resizer r : resizers) {
      if (r == Resizer.UNIFORM) {
        hasLinkAll = true;
        hasX = true;
        hasY = true;
        hasZ = true;
      } else if (r == Resizer.XY_PLANE) {
        hasLinkXY = true;
        hasX = true;
        hasY = true;
      } else if (r == Resizer.XZ_PLANE) {
        hasLinkXZ = true;
        hasX = true;
        hasZ = true;
      } else if (r == Resizer.YZ_PLANE) {
        hasLinkYZ = true;
        hasY = true;
        hasZ = true;
      } else if (r == Resizer.X_AXIS) {
        hasX = true;
        hasIndependentX = true;
      } else if (r == Resizer.Y_AXIS) {
        hasY = true;
        hasIndependentY = true;
      } else if (r == Resizer.Z_AXIS) {
        hasZ = true;
        hasIndependentZ = true;
      }
    }
    return new ResizerConfiguration(hasLinkAll, hasLinkXY, hasLinkXZ, hasLinkYZ, hasX, hasY, hasZ, hasIndependentX, hasIndependentY, hasIndependentZ);
  }

  static SourceAxis resolveSourceAxis(boolean widthSource, boolean heightSource, boolean depthSource) {
    if (widthSource) {
      return SourceAxis.WIDTH;
    }
    if (heightSource) {
      return SourceAxis.HEIGHT;
    }
    if (depthSource) {
      return SourceAxis.DEPTH;
    }
    return SourceAxis.NONE;
  }

  static boolean shouldOfferResetButton(boolean jointedModel, boolean billboard) {
    return jointedModel || billboard;
  }

  static Dimension3 computeSizeFromUi(Dimension3 desiredSize, Dimension3 currentSize, SourceAxis sourceAxis,
                                      boolean linkAll, boolean linkXY, boolean linkXZ, boolean linkYZ) {
    double width = desiredSize.x();
    double height = desiredSize.y();
    double depth = desiredSize.z();
    if (sourceAxis == SourceAxis.WIDTH) {
      double relativeXScale = width / currentSize.x();
      if (linkAll) {
        height = relativeXScale * currentSize.y();
        depth = relativeXScale * currentSize.z();
      } else if (linkXY) {
        height = relativeXScale * currentSize.y();
      } else if (linkXZ) {
        depth = relativeXScale * currentSize.z();
      }
    } else if (sourceAxis == SourceAxis.HEIGHT) {
      double relativeYScale = height / currentSize.y();
      if (linkAll) {
        width = relativeYScale * currentSize.x();
        depth = relativeYScale * currentSize.z();
      } else if (linkXY) {
        width = relativeYScale * currentSize.x();
      } else if (linkYZ) {
        depth = relativeYScale * currentSize.z();
      }
    } else if (sourceAxis == SourceAxis.DEPTH) {
      double relativeZScale = depth / currentSize.z();
      if (linkAll) {
        width = relativeZScale * currentSize.x();
        height = relativeZScale * currentSize.y();
      } else if (linkXZ) {
        width = relativeZScale * currentSize.x();
      } else if (linkYZ) {
        height = relativeZScale * currentSize.y();
      }
    }
    return new Dimension3(width, height, depth);
  }

  static Dimension3 clampNegativeScale(Dimension3 oldScale, Dimension3 newScale) {
    if (!newScale.hasNegativeComponents()) {
      return newScale;
    }
    double maxDim = Math.max(oldScale.x(), Math.max(oldScale.y(), oldScale.z()));
    return oldScale.times(0.01 / maxDim);
  }
}

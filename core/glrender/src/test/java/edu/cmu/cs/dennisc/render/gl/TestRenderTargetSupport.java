package edu.cmu.cs.dennisc.render.gl;

import com.jogamp.opengl.GLAutoDrawable;
import edu.cmu.cs.dennisc.render.RenderCapabilities;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AngleInDegrees;

import java.awt.Dimension;

final class TestRenderTargetSupport {
  private TestRenderTargetSupport() {
  }

  static final class TestRenderTarget extends GlrRenderTarget {
    private Dimension surfaceSize;
    private int repaintCount;
    private GLAutoDrawable drawable;

    TestRenderTarget(Dimension surfaceSize) {
      super(new RenderCapabilities.Builder().stencilBits(8).build());
      this.surfaceSize = surfaceSize;
    }

    @Override
    public void repaint() {
      this.repaintCount++;
    }

    @Override
    public Dimension getSurfaceSize() {
      return this.surfaceSize;
    }

    void setSurfaceSize(Dimension surfaceSize) {
      this.surfaceSize = surfaceSize;
    }

    int getRepaintCount() {
      return this.repaintCount;
    }

    void setDrawable(GLAutoDrawable drawable) {
      this.drawable = drawable;
    }

    @Override
    public GLAutoDrawable getGLAutoDrawable() {
      return this.drawable;
    }
  }

  static SymmetricPerspectiveCamera perspectiveCamera() {
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.verticalViewingAngle.setValue(new AngleInDegrees(60));
    camera.horizontalViewingAngle.setValue(new AngleInDegrees(60));
    camera.nearClippingPlaneDistance.setValue(1.0);
    camera.farClippingPlaneDistance.setValue(100.0);
    return camera;
  }
}

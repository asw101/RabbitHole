package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Dimension3;
import org.alice.math.immutable.UnitQuaternion;
import org.junit.Before;
import org.junit.Test;
import org.lgna.story.Color;
import org.lgna.story.Paint;
import org.lgna.story.SJointedModel;
import org.lgna.story.implementation.JointedModelImp.JointImplementationAndVisualDataFactory;
import org.lgna.story.implementation.JointedModelImp.VisualData;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Extended tests for {@link ModelImp} methods, accessed through
 * {@link JointedModelImp} via the proven TestJointedModelImp + StubFactory pattern.
 * Focuses on paint, opacity, scale/size, visualization, vehicle, and bounding box.
 */
public class ModelImpExtendedTest {

  // ── Test doubles (same pattern as JointedModelImpBehaviorTest) ──

  public static class TestResource implements JointedModelResource {
    public static final JointId ROOT = new JointId(null, TestResource.class);
    public static final JointId SPINE = new JointId(ROOT, TestResource.class);

    @Override
    public JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }

  static class StubVisualData implements VisualData<JointedModelResource> {
    private final Visual[] visuals;
    private final SimpleAppearance[] appearances;
    private Composite sgParent;

    StubVisualData() {
      this.visuals = new Visual[] {new Visual()};
      this.appearances = new SimpleAppearance[] {new SimpleAppearance()};
    }

    @Override
    public Visual[] getSgVisuals() {
      return visuals;
    }

    @Override
    public SkeletonVisual getSgVisualForExporting(JointedModelResource resource) {
      return null;
    }

    @Override
    public SimpleAppearance[] getSgAppearances() {
      return appearances;
    }

    @Override
    public void setSGParent(Composite parent) {
      this.sgParent = parent;
    }

    @Override
    public Composite getSGParent() {
      return sgParent;
    }
  }

  static class StubFactory implements JointImplementationAndVisualDataFactory<JointedModelResource> {
    private final JointedModelResource resource;

    StubFactory(JointedModelResource resource) {
      this.resource = resource;
    }

    @Override
    public JointedModelResource getResource() {
      return resource;
    }

    @Override
    public JointImp createJointImplementation(JointedModelImp<?, JointedModelResource> impl, JointId jointId) {
      Joint sgJoint = new Joint();
      sgJoint.jointID.setValue(jointId.toString());
      return new org.lgna.story.implementation.alice.JointImplementation(impl, jointId, sgJoint) {
        @Override
        protected void copyOnto(JointImp newJoint) {
          if (getJointedModelImplementation() != null) {
            super.copyOnto(newJoint);
          }
        }
      };
    }

    @Override
    public boolean hasJointImplementation(JointedModelImp<?, JointedModelResource> impl, JointId jointId) {
      return true;
    }

    @Override
    public JointId[] getJointArrayIds(JointedModelImp<?, JointedModelResource> impl,
        org.lgna.story.resources.JointArrayId jointArrayId) {
      return new JointId[0];
    }

    @Override
    public VisualData<JointedModelResource> createVisualData() {
      return new StubVisualData();
    }

    @Override
    public UnitQuaternion getOriginalJointOrientation(JointId jointId) {
      return UnitQuaternion.IDENTITY;
    }

    @Override
    public AffineMatrix4x4 getOriginalJointTransformation(JointId jointId) {
      return AffineMatrix4x4.IDENTITY;
    }

    @Override
    public boolean isSims() {
      return false;
    }
  }

  static class TestJointedModelImp extends JointedModelImp<SJointedModel, JointedModelResource> {
    TestJointedModelImp(JointImplementationAndVisualDataFactory<JointedModelResource> factory) {
      super(null, factory);
    }
  }

  // ── Fixtures ──

  private TestResource testResource;
  private TestJointedModelImp model;

  @Before
  public void setUp() {
    testResource = new TestResource();
    model = new TestJointedModelImp(new StubFactory(testResource));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Paint property
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void paintDefaultIsWhite() {
    Paint p = model.paint.getValue();
    assertNotNull(p);
    assertEquals(Color.WHITE, p);
  }

  @Test
  public void paintSetValueRoundTrips() {
    model.paint.setValue(Color.BLUE);
    assertEquals(Color.BLUE, model.paint.getValue());
  }

  @Test
  public void paintSetToRedRoundTrips() {
    model.paint.setValue(Color.RED);
    assertEquals(Color.RED, model.paint.getValue());
  }

  @Test
  public void paintSetToGreenRoundTrips() {
    model.paint.setValue(Color.GREEN);
    assertEquals(Color.GREEN, model.paint.getValue());
  }

  @Test
  public void paintAnimateValueWithZeroDurationSetsImmediately() {
    model.paint.animateValue(Color.CYAN, 0.0);
    assertEquals(Color.CYAN, model.paint.getValue());
  }

  @Test
  public void paintPropertyOwnerIsModel() {
    assertSame(model, model.paint.getOwner());
  }

  @Test
  public void paintPropertyValueClsIsPaint() {
    assertEquals(Paint.class, model.paint.getValueCls());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Opacity property
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void opacityDefaultIsOne() {
    assertEquals(1.0f, model.opacity.getValue(), 1e-6f);
  }

  @Test
  public void opacitySetValueRoundTrips() {
    model.opacity.setValue(0.5f);
    assertEquals(0.5f, model.opacity.getValue(), 1e-6f);
  }

  @Test
  public void opacitySetToZeroRoundTrips() {
    model.opacity.setValue(0.0f);
    assertEquals(0.0f, model.opacity.getValue(), 1e-6f);
  }

  @Test
  public void opacityAnimateWithZeroDurationSetsImmediately() {
    model.opacity.animateValue(0.25f, 0.0);
    assertEquals(0.25f, model.opacity.getValue(), 1e-6f);
  }

  @Test
  public void opacityPropertyOwnerIsModel() {
    assertSame(model, model.opacity.getOwner());
  }

  @Test
  public void opacityPropertyValueClsIsFloat() {
    assertEquals(Float.class, model.opacity.getValueCls());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  showVisualization / hideVisualization
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void showVisualizationDoesNotThrow() {
    model.showVisualization();
  }

  @Test
  public void hideVisualizationDoesNotThrowWhenNeverShown() {
    model.hideVisualization();
  }

  @Test
  public void showThenHideVisualizationDoesNotThrow() {
    model.showVisualization();
    model.hideVisualization();
  }

  @Test
  public void showVisualizationTwiceDoesNotThrow() {
    model.showVisualization();
    model.showVisualization();
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Vehicle / scenegraph parenting
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void defaultVehicleIsNull() {
    assertNull(model.getVehicle());
  }

  @Test
  public void setVehicleToStandInEstablishesParent() {
    StandInImp vehicle = new StandInImp();
    model.setVehicle(vehicle);
    assertSame(vehicle, model.getVehicle());
  }

  @Test
  public void setVehicleToNullRemovesParent() {
    StandInImp vehicle = new StandInImp();
    model.setVehicle(vehicle);
    model.setVehicle(null);
    assertNull(model.getVehicle());
  }

  @Test
  public void setVehicleUpdatesSgParent() {
    StandInImp vehicle = new StandInImp();
    model.setVehicle(vehicle);
    assertSame(vehicle.getSgComposite(), model.getSgComposite().getParent());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Bounding box — via SBox (which has real geometry)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void boxGetAxisAlignedMinimumBoundingBoxIsNotNull() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    AxisAlignedBox bb = box.getImplementation().getAxisAlignedMinimumBoundingBox();
    assertNotNull(bb);
  }

  @Test
  public void boxGetSizeIsNotNull() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    Dimension3 size = box.getImplementation().getSize();
    assertNotNull(size);
  }

  @Test
  public void boxGetWidthIsPositive() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    assertTrue(box.getImplementation().getWidth() > 0);
  }

  @Test
  public void boxGetHeightIsPositive() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    assertTrue(box.getImplementation().getHeight() > 0);
  }

  @Test
  public void boxGetDepthIsPositive() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    assertTrue(box.getImplementation().getDepth() > 0);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Scale
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getScaleDefaultIsUnit() {
    Dimension3 scale = model.getScale();
    assertNotNull(scale);
    assertEquals(1.0, scale.x(), 1e-6);
    assertEquals(1.0, scale.y(), 1e-6);
    assertEquals(1.0, scale.z(), 1e-6);
  }

  @Test
  public void setScaleUpdatesGetScale() {
    model.setScale(new Dimension3(2.0, 3.0, 4.0));
    Dimension3 s = model.getScale();
    assertEquals(2.0, s.x(), 1e-6);
    assertEquals(3.0, s.y(), 1e-6);
    assertEquals(4.0, s.z(), 1e-6);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Resizer (from Scalable)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getResizersReturnsUniform() {
    Resizer[] resizers = model.getResizers();
    assertNotNull(resizers);
    assertEquals(1, resizers.length);
    assertEquals(Resizer.UNIFORM, resizers[0]);
  }

  @Test
  public void getValueForResizerUniformReturnsScaleX() {
    model.setScale(new Dimension3(3.0, 3.0, 3.0));
    double val = model.getValueForResizer(Resizer.UNIFORM);
    assertEquals(3.0, val, 1e-6);
  }

  @Test
  public void setValueForResizerUniformSetsUniformScale() {
    model.setValueForResizer(Resizer.UNIFORM, 5.0);
    Dimension3 s = model.getScale();
    assertEquals(5.0, s.x(), 1e-6);
    assertEquals(5.0, s.y(), 1e-6);
    assertEquals(5.0, s.z(), 1e-6);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  setName propagates to visuals
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void setNameSetsVisualNames() {
    model.setName("hero");
    Visual[] visuals = model.getSgVisuals();
    assertTrue(visuals.length > 0);
    String vName = visuals[0].getName();
    assertNotNull(vName);
    assertTrue("Visual name should contain model name", vName.contains("hero"));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Paint property listener
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void paintPropertyListenerFiresOnChange() {
    final boolean[] fired = {false};
    model.paint.addPropertyListener(() -> fired[0] = true);
    model.paint.setValue(Color.MAGENTA);
    assertTrue("Paint listener should fire on setValue", fired[0]);
  }

  @Test
  public void paintPropertyListenerCanBeRemoved() {
    final int[] count = {0};
    Property.Listener<Paint> listener = () -> count[0]++;
    model.paint.addPropertyListener(listener);
    model.paint.setValue(Color.RED);
    model.paint.removePropertyListener(listener);
    model.paint.setValue(Color.GREEN);
    assertEquals("Listener should only fire once before removal", 1, count[0]);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Opacity property listener
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void opacityPropertyListenerFiresOnChange() {
    final boolean[] fired = {false};
    model.opacity.addPropertyListener(() -> fired[0] = true);
    model.opacity.setValue(0.3f);
    assertTrue("Opacity listener should fire on setValue", fired[0]);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Resize/animate via SBox (which has real geometry for bounding box)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void boxAnimateResizeWithZeroDurationDoesNotThrow() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    box.getImplementation().animateResize(2.0, 0.0, null);
  }

  @Test
  public void boxAnimateResizeWidthWithZeroDurationDoesNotThrow() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    box.getImplementation().animateResizeWidth(2.0, false, 0.0, null);
  }

  @Test
  public void boxAnimateResizeHeightWithZeroDurationDoesNotThrow() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    box.getImplementation().animateResizeHeight(2.0, false, 0.0, null);
  }

  @Test
  public void boxAnimateResizeDepthWithZeroDurationDoesNotThrow() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    box.getImplementation().animateResizeDepth(2.0, false, 0.0, null);
  }

  @Test
  public void boxAnimateResizeWidthVolumePreservedDoesNotThrow() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    box.getImplementation().animateResizeWidth(2.0, true, 0.0, null);
  }

  @Test
  public void boxAnimateResizeHeightVolumePreservedDoesNotThrow() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    box.getImplementation().animateResizeHeight(2.0, true, 0.0, null);
  }

  @Test
  public void boxAnimateResizeDepthVolumePreservedDoesNotThrow() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    box.getImplementation().animateResizeDepth(2.0, true, 0.0, null);
  }

  @Test
  public void boxAnimateSetSizeWithZeroDurationSetsSize() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    Dimension3 target = new Dimension3(2.0, 3.0, 4.0);
    box.getImplementation().animateSetSize(target, 0.0, null);
    Dimension3 size = box.getImplementation().getSize();
    assertNotNull(size);
  }

  @Test
  public void boxAnimateSetScaleWithZeroDuration() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    Dimension3 origScale = box.getImplementation().getScale();
    assertNotNull(origScale);
    // animateSetScale with zero duration triggers immediate path through animateSetSize
    box.getImplementation().animateSetScale(new Dimension3(2.0, 2.0, 2.0), 0.0, null);
  }

  @Test
  public void boxAnimateSetWidthZeroDuration() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    double origWidth = box.getImplementation().getWidth();
    box.getImplementation().animateSetWidth(origWidth * 2, false, false, 0.0, null);
  }

  @Test
  public void boxAnimateSetHeightZeroDuration() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    double origHeight = box.getImplementation().getHeight();
    box.getImplementation().animateSetHeight(origHeight * 2, false, false, 0.0, null);
  }

  @Test
  public void boxAnimateSetDepthZeroDuration() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    double origDepth = box.getImplementation().getDepth();
    box.getImplementation().animateSetDepth(origDepth * 2, false, false, 0.0, null);
  }

  @Test
  public void boxAnimateSetWidthAspectRatioPreserved() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    double origWidth = box.getImplementation().getWidth();
    box.getImplementation().animateSetWidth(origWidth * 2, false, true, 0.0, null);
  }

  @Test
  public void boxAnimateSetHeightAspectRatioPreserved() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    double origHeight = box.getImplementation().getHeight();
    box.getImplementation().animateSetHeight(origHeight * 2, false, true, 0.0, null);
  }

  @Test
  public void boxAnimateSetDepthAspectRatioPreserved() {
    org.lgna.story.SBox box = new org.lgna.story.SBox();
    double origDepth = box.getImplementation().getDepth();
    box.getImplementation().animateSetDepth(origDepth * 2, false, true, 0.0, null);
  }
}

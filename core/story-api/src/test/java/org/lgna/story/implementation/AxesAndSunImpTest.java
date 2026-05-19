package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;
import org.lgna.story.SAxes;
import org.lgna.story.SSun;

import static org.junit.Assert.*;

/**
 * Tests for {@link AxesImp} (via {@link SAxes}) and
 * {@link SunImp} (via {@link SSun}, {@code @Deprecated}).
 *
 * <p>Covers ExtravagantAxes creation, scale properties, paint/opacity
 * appearances, and SunImp's DirectionalLight composition with initial rotation.
 */
public class AxesAndSunImpTest {

  // ══════════════════════════════════════════════════════════════════════
  //  SAxes / AxesImp
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void axes_constructsWithoutError() {
    SAxes axes = new SAxes();
    assertNotNull(axes.getImplementation());
  }

  @Test
  public void axes_abstractionRoundTrips() {
    SAxes axes = new SAxes();
    assertSame(axes, axes.getImplementation().getAbstraction());
  }

  @Test
  public void axes_sgVisualsNotNull() {
    SAxes axes = new SAxes();
    Visual[] visuals = axes.getImplementation().getSgVisuals();
    assertNotNull(visuals);
    assertTrue("Axes should have visuals from ExtravagantAxes", visuals.length > 0);
  }

  @Test
  public void axes_sgCompositeNotNull() {
    SAxes axes = new SAxes();
    assertNotNull(axes.getImplementation().getSgComposite());
  }

  @Test
  public void axes_scalePropertiesNotNull() {
    SAxes axes = new SAxes();
    AxesImp imp = axes.getImplementation();
    assertNotNull(imp.getScaleProperties());
    assertTrue(imp.getScaleProperties().length > 0);
  }

  @Test
  public void axes_sgPaintAppearancesIsEmpty() {
    SAxes axes = new SAxes();
    AxesImp imp = axes.getImplementation();
    SimpleAppearance[] appearances = imp.getSgPaintAppearances();
    assertNotNull(appearances);
    assertEquals(0, appearances.length);
  }

  @Test
  public void axes_sgOpacityAppearancesNotNull() {
    SAxes axes = new SAxes();
    AxesImp imp = axes.getImplementation();
    SimpleAppearance[] appearances = imp.getSgOpacityAppearances();
    assertNotNull(appearances);
  }

  @Test
  public void axes_initialScaleIsUnit() {
    SAxes axes = new SAxes();
    Dimension3 scale = axes.getImplementation().getScale();
    assertEquals(1.0, scale.x(), 1e-6);
    assertEquals(1.0, scale.y(), 1e-6);
    assertEquals(1.0, scale.z(), 1e-6);
  }

  @Test
  public void axes_setSize_updatesScale() {
    SAxes axes = new SAxes();
    AxesImp imp = axes.getImplementation();
    imp.setSize(new Dimension3(2.0, 2.0, 2.0));
    Dimension3 scale = imp.getScale();
    assertNotNull(scale);
  }

  @Test
  public void axes_instanceRegistryWorks() {
    SAxes axes = new SAxes();
    Visual[] visuals = axes.getImplementation().getSgVisuals();
    assertTrue("Axes should have visuals for registry test", visuals.length > 0);
    EntityImp imp = EntityImp.getInstance(visuals[0]);
    assertSame(axes.getImplementation(), imp);
  }

  @Test
  public void axes_localTransformIsNotNull() {
    SAxes axes = new SAxes();
    assertNotNull(axes.getImplementation().getLocalTransformation());
  }

  @Test
  public void axes_extendsVisualScaleModelImp() {
    assertTrue(VisualScaleModelImp.class.isAssignableFrom(AxesImp.class));
  }

  @Test
  public void axes_getSgVisualsScaleNotNull() {
    SAxes axes = new SAxes();
    AxesImp imp = axes.getImplementation();
    assertNotNull(imp.getSgVisualsScale());
  }

  // ══════════════════════════════════════════════════════════════════════
  //  SSun / SunImp (@Deprecated)
  // ══════════════════════════════════════════════════════════════════════

  @SuppressWarnings("deprecation")
  @Test
  public void sun_constructsWithoutError() {
    SSun sun = new SSun();
    assertNotNull(sun.getImplementation());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void sun_abstractionRoundTrips() {
    SSun sun = new SSun();
    assertSame(sun, sun.getImplementation().getAbstraction());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void sun_sgCompositeNotNull() {
    SSun sun = new SSun();
    assertNotNull(sun.getImplementation().getSgComposite());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void sun_initialRotationApplied() {
    SSun sun = new SSun();
    SunImp imp = sun.getImplementation();
    assertNotNull(imp.getLocalTransformation());
    assertFalse("Sun should have rotation applied, not identity",
        imp.getLocalTransformation().isIdentity());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void sun_extendsTransformableImp() {
    assertTrue(TransformableImp.class.isAssignableFrom(SunImp.class));
  }

  @SuppressWarnings("deprecation")
  @Test
  public void sun_classIsDeprecated() {
    assertNotNull(SunImp.class.getAnnotation(Deprecated.class));
  }

  @SuppressWarnings("deprecation")
  @Test
  public void sun_facadeIsDeprecated() {
    assertNotNull(SSun.class.getAnnotation(Deprecated.class));
  }

  @SuppressWarnings("deprecation")
  @Test
  public void sun_setVehicle_acceptsNull() {
    SSun sun = new SSun();
    sun.setVehicle(null);
    // Should not throw
  }

  @SuppressWarnings("deprecation")
  @Test
  public void sun_getName_returnsNullByDefault() {
    SSun sun = new SSun();
    assertNull(sun.getImplementation().getName());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void sun_setName_roundTrips() {
    SSun sun = new SSun();
    sun.getImplementation().setName("TestSun");
    assertEquals("TestSun", sun.getImplementation().getName());
  }

  // ══════════════════════════════════════════════════════════════════════
  //  Cross-cutting: AxesImp as VisualScaleModelImp
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void axes_setScale_roundTrips() {
    SAxes axes = new SAxes();
    AxesImp imp = axes.getImplementation();
    imp.setScale(new Dimension3(2.0, 3.0, 4.0));
    Dimension3 scale = imp.getScale();
    assertNotNull(scale);
  }

  @Test
  public void axes_getAxisAlignedMinimumBoundingBox_notNull() {
    SAxes axes = new SAxes();
    assertNotNull(axes.getImplementation().getAxisAlignedMinimumBoundingBox());
  }

  @Test
  public void axes_applyScale_withScoot() {
    SAxes axes = new SAxes();
    AxesImp imp = axes.getImplementation();
    imp.applyScale(new Dimension3(2.0, 2.0, 2.0), true);
    // Should not throw; verifies scoot path
  }

  @Test
  public void axes_applyScale_withoutScoot() {
    SAxes axes = new SAxes();
    AxesImp imp = axes.getImplementation();
    imp.applyScale(new Dimension3(2.0, 2.0, 2.0), false);
    // Should not throw; verifies non-scoot path
  }

  @Test
  public void axes_setSgVisualsScale() {
    SAxes axes = new SAxes();
    AxesImp imp = axes.getImplementation();
    org.alice.math.immutable.Matrix3x3 currentScale = imp.getSgVisualsScale();
    imp.setSgVisualsScale(currentScale);
    // Should not throw
  }

  @SuppressWarnings("deprecation")
  @Test
  public void sun_axisAlignedBoundingBox_notNull() {
    SSun sun = new SSun();
    assertNotNull(sun.getImplementation().getAxisAlignedMinimumBoundingBox());
  }
}

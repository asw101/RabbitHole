package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Visual;
import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;
import org.lgna.story.SBox;
import org.lgna.story.SBillboard;
import org.lgna.story.SSphere;
import org.lgna.story.STextModel;

import java.lang.reflect.Method;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Tests for the visual scale hierarchy:
 * {@link SingleVisualModelImp}, {@link VisualScaleModelImp},
 * {@link SimpleModelImp}, and {@link PropertyOwnerImp}.
 *
 * <p>Uses concrete shapes (Box, Sphere) and Billboard/TextModel as
 * representative instances to exercise hierarchy behavior.
 */
public class VisualScaleHierarchyTest {

  // ══════════════════════════════════════════════════════════════════════
  //  Class hierarchy verification
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void shapeImp_extendsSingleVisualModelImp() {
    assertTrue(SimpleModelImp.class.isAssignableFrom(ShapeImp.class));
  }

  @Test
  public void simpleModelImp_extendsSingleVisualModelImp() {
    assertTrue(SingleVisualModelImp.class.isAssignableFrom(SimpleModelImp.class));
  }

  @Test
  public void singleVisualModelImp_extendsVisualScaleModelImp() {
    assertTrue(VisualScaleModelImp.class.isAssignableFrom(SingleVisualModelImp.class));
  }

  @Test
  public void visualScaleModelImp_extendsModelImp() {
    assertTrue(ModelImp.class.isAssignableFrom(VisualScaleModelImp.class));
  }

  @Test
  public void modelImp_extendsTransformableImp() {
    assertTrue(TransformableImp.class.isAssignableFrom(ModelImp.class));
  }

  @Test
  public void billboardImp_extendsVisualScaleModelImp() {
    assertTrue(VisualScaleModelImp.class.isAssignableFrom(BillboardImp.class));
  }

  @Test
  public void textModelImp_extendsSimpleModelImp() {
    assertTrue(SimpleModelImp.class.isAssignableFrom(TextModelImp.class));
  }

  @Test
  public void boxImp_extendsShapeImp() {
    assertTrue(ShapeImp.class.isAssignableFrom(BoxImp.class));
  }

  @Test
  public void sphereImp_extendsShapeImp() {
    assertTrue(ShapeImp.class.isAssignableFrom(SphereImp.class));
  }

  @Test
  public void propertyOwnerImp_isAbstract() {
    assertTrue(Modifier.isAbstract(PropertyOwnerImp.class.getModifiers()));
  }

  @Test
  public void visualScaleModelImp_isAbstract() {
    assertTrue(Modifier.isAbstract(VisualScaleModelImp.class.getModifiers()));
  }

  @Test
  public void singleVisualModelImp_isAbstract() {
    assertTrue(Modifier.isAbstract(SingleVisualModelImp.class.getModifiers()));
  }

  @Test
  public void simpleModelImp_isAbstract() {
    assertTrue(Modifier.isAbstract(SimpleModelImp.class.getModifiers()));
  }

  // ══════════════════════════════════════════════════════════════════════
  //  SingleVisualModelImp behavior (via BoxImp)
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void box_sgVisuals_exactlyOne() {
    SBox box = new SBox();
    assertEquals(1, box.getImplementation().getSgVisuals().length);
  }

  @Test
  public void box_sgVisual_hasGeometry() {
    SBox box = new SBox();
    Visual v = box.getImplementation().getSgVisuals()[0];
    assertNotNull(v.getGeometry());
  }

  @Test
  public void box_getScale_returnsNonNull() {
    SBox box = new SBox();
    assertNotNull(box.getImplementation().getScale());
  }

  @Test
  public void box_setScale_roundTrips() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    imp.setSize(new Dimension3(2.0, 3.0, 4.0));
    Dimension3 size = imp.getSize();
    assertEquals(2.0, size.x(), 1e-6);
    assertEquals(3.0, size.y(), 1e-6);
    assertEquals(4.0, size.z(), 1e-6);
  }

  @Test
  public void box_getSize_returnsNonNull() {
    SBox box = new SBox();
    assertNotNull(box.getImplementation().getSize());
  }

  @Test
  public void box_paint_property_exists() {
    SBox box = new SBox();
    assertNotNull(box.getImplementation().paint);
  }

  @Test
  public void box_opacity_property_exists() {
    SBox box = new SBox();
    assertNotNull(box.getImplementation().opacity);
  }

  // ══════════════════════════════════════════════════════════════════════
  //  VisualScaleModelImp behavior (via BillboardImp)
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void billboard_getScale_returnsNonNull() {
    SBillboard bb = new SBillboard();
    assertNotNull(bb.getImplementation().getScale());
  }

  @Test
  public void billboard_setScale_roundTrips() {
    SBillboard bb = new SBillboard();
    bb.getImplementation().setScale(new Dimension3(3.0, 2.0, 1.0));
    Dimension3 scale = bb.getImplementation().getScale();
    assertEquals(3.0, scale.x(), 1e-6);
    assertEquals(2.0, scale.y(), 1e-6);
  }

  @Test
  public void billboard_getSize_afterSetSize() {
    SBillboard bb = new SBillboard();
    bb.getImplementation().setSize(new Dimension3(5.0, 3.0, 1.0));
    Dimension3 size = bb.getImplementation().getSize();
    assertNotNull(size);
  }

  // ══════════════════════════════════════════════════════════════════════
  //  SimpleModelImp behavior (via TextModelImp)
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_sgVisuals_exactlyOne() {
    STextModel model = new STextModel();
    assertEquals(1, model.getImplementation().getSgVisuals().length);
  }

  @Test
  public void textModel_getScale_returnsNonNull() {
    STextModel model = new STextModel();
    assertNotNull(model.getImplementation().getScale());
  }

  @Test
  public void textModel_paint_property_exists() {
    STextModel model = new STextModel();
    assertNotNull(model.getImplementation().paint);
  }

  @Test
  public void textModel_opacity_property_exists() {
    STextModel model = new STextModel();
    assertNotNull(model.getImplementation().opacity);
  }

  // ══════════════════════════════════════════════════════════════════════
  //  PropertyOwnerImp behavior
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void entityImp_declaresGetPropertyForAbstractionGetter() {
    boolean found = Arrays.stream(EntityImp.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getPropertyForAbstractionGetter"));
    assertTrue("EntityImp should declare getPropertyForAbstractionGetter", found);
  }

  @Test
  public void entityImp_getPropertyForAbstractionGetter_signatureCorrect() throws Exception {
    Method m = EntityImp.class.getMethod("getPropertyForAbstractionGetter", Method.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ══════════════════════════════════════════════════════════════════════
  //  Cross-cutting: EntityImp registry through hierarchy
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void sphere_instanceRegistryWorks() {
    SSphere sphere = new SSphere();
    Visual v = sphere.getImplementation().getSgVisuals()[0];
    EntityImp found = EntityImp.getInstance(v);
    assertSame(sphere.getImplementation(), found);
  }

  @Test
  public void textModel_instanceRegistryWorks() {
    STextModel model = new STextModel();
    Visual v = model.getImplementation().getSgVisuals()[0];
    EntityImp found = EntityImp.getInstance(v);
    assertSame(model.getImplementation(), found);
  }
}

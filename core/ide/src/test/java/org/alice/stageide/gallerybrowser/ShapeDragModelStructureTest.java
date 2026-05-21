package org.alice.stageide.gallerybrowser;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for gallery browser shape drag model classes.
 */
public class ShapeDragModelStructureTest {

  private static final String SHAPES_PKG = "org.alice.stageide.gallerybrowser.shapes.";

  // ---- ShapeDragModel ----

  @Test
  public void shapeDragModel_classIsAccessible() throws ClassNotFoundException {
    Class<?> cls = Class.forName(SHAPES_PKG + "ShapeDragModel");
    assertNotNull(cls);
  }

  @Test
  public void shapeDragModel_isPublic() throws ClassNotFoundException {
    Class<?> cls = Class.forName(SHAPES_PKG + "ShapeDragModel");
    assertTrue(Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void shapeDragModel_isAbstract() throws ClassNotFoundException {
    Class<?> cls = Class.forName(SHAPES_PKG + "ShapeDragModel");
    assertTrue(Modifier.isAbstract(cls.getModifiers()));
  }

  // ---- BoxDragModel ----

  @Test
  public void boxDragModel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName(SHAPES_PKG + "BoxDragModel"));
  }

  @Test
  public void boxDragModel_extendsShapeDragModel() throws ClassNotFoundException {
    Class<?> shape = Class.forName(SHAPES_PKG + "ShapeDragModel");
    Class<?> box = Class.forName(SHAPES_PKG + "BoxDragModel");
    assertTrue(shape.isAssignableFrom(box));
  }

  // ---- SphereDragModel ----

  @Test
  public void sphereDragModel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName(SHAPES_PKG + "SphereDragModel"));
  }

  @Test
  public void sphereDragModel_extendsShapeDragModel() throws ClassNotFoundException {
    Class<?> shape = Class.forName(SHAPES_PKG + "ShapeDragModel");
    Class<?> sphere = Class.forName(SHAPES_PKG + "SphereDragModel");
    assertTrue(shape.isAssignableFrom(sphere));
  }

  // ---- CylinderDragModel ----

  @Test
  public void cylinderDragModel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName(SHAPES_PKG + "CylinderDragModel"));
  }

  @Test
  public void cylinderDragModel_extendsShapeDragModel() throws ClassNotFoundException {
    Class<?> shape = Class.forName(SHAPES_PKG + "ShapeDragModel");
    Class<?> cylinder = Class.forName(SHAPES_PKG + "CylinderDragModel");
    assertTrue(shape.isAssignableFrom(cylinder));
  }

  // ---- ConeDragModel ----

  @Test
  public void coneDragModel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName(SHAPES_PKG + "ConeDragModel"));
  }

  @Test
  public void coneDragModel_extendsShapeDragModel() throws ClassNotFoundException {
    Class<?> shape = Class.forName(SHAPES_PKG + "ShapeDragModel");
    Class<?> cone = Class.forName(SHAPES_PKG + "ConeDragModel");
    assertTrue(shape.isAssignableFrom(cone));
  }

  // ---- DiscDragModel ----

  @Test
  public void discDragModel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName(SHAPES_PKG + "DiscDragModel"));
  }

  @Test
  public void discDragModel_extendsShapeDragModel() throws ClassNotFoundException {
    Class<?> shape = Class.forName(SHAPES_PKG + "ShapeDragModel");
    Class<?> disc = Class.forName(SHAPES_PKG + "DiscDragModel");
    assertTrue(shape.isAssignableFrom(disc));
  }

  // ---- TorusDragModel ----

  @Test
  public void torusDragModel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName(SHAPES_PKG + "TorusDragModel"));
  }

  @Test
  public void torusDragModel_extendsShapeDragModel() throws ClassNotFoundException {
    Class<?> shape = Class.forName(SHAPES_PKG + "ShapeDragModel");
    Class<?> torus = Class.forName(SHAPES_PKG + "TorusDragModel");
    assertTrue(shape.isAssignableFrom(torus));
  }

  // ---- AxesDragModel ----

  @Test
  public void axesDragModel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName(SHAPES_PKG + "AxesDragModel"));
  }

  // ---- BillboardDragModel ----

  @Test
  public void billboardDragModel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName(SHAPES_PKG + "BillboardDragModel"));
  }

  // ---- TextModelDragModel ----

  @Test
  public void textModelDragModel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName(SHAPES_PKG + "TextModelDragModel"));
  }

  // ---- GroundDragModel ----

  @Test
  public void groundDragModel_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName(SHAPES_PKG + "GroundDragModel"));
  }

  // ---- all shape models are concrete ----

  @Test
  public void allConcreteShapeModels_areNotAbstract() throws ClassNotFoundException {
    String[] concreteModels = {
      "BoxDragModel", "SphereDragModel", "CylinderDragModel",
      "ConeDragModel", "DiscDragModel", "TorusDragModel",
      "AxesDragModel", "BillboardDragModel", "TextModelDragModel", "GroundDragModel"
    };
    for (String name : concreteModels) {
      Class<?> cls = Class.forName(SHAPES_PKG + name);
      assertFalse(name + " should not be abstract", Modifier.isAbstract(cls.getModifiers()));
    }
  }

  // ---- all shape models are public ----

  @Test
  public void allShapeModels_arePublic() throws ClassNotFoundException {
    String[] models = {
      "ShapeDragModel", "BoxDragModel", "SphereDragModel", "CylinderDragModel",
      "ConeDragModel", "DiscDragModel", "TorusDragModel",
      "AxesDragModel", "BillboardDragModel", "TextModelDragModel", "GroundDragModel"
    };
    for (String name : models) {
      Class<?> cls = Class.forName(SHAPES_PKG + name);
      assertTrue(name + " should be public", Modifier.isPublic(cls.getModifiers()));
    }
  }

  // ---- all concrete models share same base ----

  @Test
  public void allConcreteModels_shareShapeDragModelBase() throws ClassNotFoundException {
    Class<?> base = Class.forName(SHAPES_PKG + "ShapeDragModel");
    String[] concreteModels = {
      "BoxDragModel", "SphereDragModel", "CylinderDragModel",
      "ConeDragModel", "DiscDragModel", "TorusDragModel"
    };
    for (String name : concreteModels) {
      Class<?> cls = Class.forName(SHAPES_PKG + name);
      assertTrue(name + " should extend ShapeDragModel", base.isAssignableFrom(cls));
    }
  }
}

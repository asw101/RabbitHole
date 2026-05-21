package org.alice.stageide.ast.declaration;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for Add*ManagedFieldComposite classes.
 * These composites handle adding different types of scene objects.
 */
public class AddManagedFieldCompositeStructureTest {

  // ---- AddResourceKeyManagedFieldComposite ----

  @Test
  public void addResourceKeyManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddResourceKeyManagedFieldComposite.class);
  }

  @Test
  public void addResourceKeyManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddResourceKeyManagedFieldComposite.class.getModifiers()));
  }

  @Test
  public void addResourceKeyManagedFieldComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(AddResourceKeyManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddModelManagedFieldComposite ----

  @Test
  public void addModelManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddModelManagedFieldComposite.class);
  }

  @Test
  public void addModelManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddModelManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddBoxManagedFieldComposite ----

  @Test
  public void addBoxManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddBoxManagedFieldComposite.class);
  }

  @Test
  public void addBoxManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddBoxManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddSphereManagedFieldComposite ----

  @Test
  public void addSphereManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddSphereManagedFieldComposite.class);
  }

  @Test
  public void addSphereManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddSphereManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddCylinderManagedFieldComposite ----

  @Test
  public void addCylinderManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddCylinderManagedFieldComposite.class);
  }

  @Test
  public void addCylinderManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddCylinderManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddConeManagedFieldComposite ----

  @Test
  public void addConeManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddConeManagedFieldComposite.class);
  }

  @Test
  public void addConeManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddConeManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddDiscManagedFieldComposite ----

  @Test
  public void addDiscManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddDiscManagedFieldComposite.class);
  }

  @Test
  public void addDiscManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddDiscManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddTorusManagedFieldComposite ----

  @Test
  public void addTorusManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddTorusManagedFieldComposite.class);
  }

  @Test
  public void addTorusManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddTorusManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddBillboardManagedFieldComposite ----

  @Test
  public void addBillboardManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddBillboardManagedFieldComposite.class);
  }

  @Test
  public void addBillboardManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddBillboardManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddAxesManagedFieldComposite ----

  @Test
  public void addAxesManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddAxesManagedFieldComposite.class);
  }

  @Test
  public void addAxesManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddAxesManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddGroundManagedFieldComposite ----

  @Test
  public void addGroundManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddGroundManagedFieldComposite.class);
  }

  @Test
  public void addGroundManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddGroundManagedFieldComposite.class.getModifiers()));
  }

  // ---- AddTextModelManagedFieldOperationComposite ----

  @Test
  public void addTextModelManagedFieldOperationComposite_classIsAccessible() {
    assertNotNull(AddTextModelManagedFieldOperationComposite.class);
  }

  @Test
  public void addTextModelManagedFieldOperationComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddTextModelManagedFieldOperationComposite.class.getModifiers()));
  }

  // ---- AddCopiedManagedFieldComposite ----

  @Test
  public void addCopiedManagedFieldComposite_classIsAccessible() {
    assertNotNull(AddCopiedManagedFieldComposite.class);
  }

  @Test
  public void addCopiedManagedFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddCopiedManagedFieldComposite.class.getModifiers()));
  }

  // ---- All shape composites are concrete ----

  @Test
  public void allShapeComposites_areNotAbstract() {
    Class<?>[] classes = {
      AddBoxManagedFieldComposite.class, AddSphereManagedFieldComposite.class,
      AddCylinderManagedFieldComposite.class, AddConeManagedFieldComposite.class,
      AddDiscManagedFieldComposite.class, AddTorusManagedFieldComposite.class,
      AddBillboardManagedFieldComposite.class, AddAxesManagedFieldComposite.class,
      AddGroundManagedFieldComposite.class, AddTextModelManagedFieldOperationComposite.class
    };
    for (Class<?> cls : classes) {
      assertFalse(cls.getSimpleName() + " should be concrete",
        Modifier.isAbstract(cls.getModifiers()));
    }
  }

  // ---- All composites are public ----

  @Test
  public void allComposites_arePublic() {
    Class<?>[] classes = {
      AddResourceKeyManagedFieldComposite.class, AddModelManagedFieldComposite.class,
      AddBoxManagedFieldComposite.class, AddSphereManagedFieldComposite.class,
      AddCylinderManagedFieldComposite.class, AddConeManagedFieldComposite.class,
      AddDiscManagedFieldComposite.class, AddTorusManagedFieldComposite.class,
      AddBillboardManagedFieldComposite.class, AddAxesManagedFieldComposite.class,
      AddGroundManagedFieldComposite.class, AddCopiedManagedFieldComposite.class,
      AddTextModelManagedFieldOperationComposite.class
    };
    for (Class<?> cls : classes) {
      assertTrue(cls.getSimpleName() + " should be public",
        Modifier.isPublic(cls.getModifiers()));
    }
  }
}

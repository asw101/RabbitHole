package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link PredeterminedMenuModel} — abstract menu model that
 * receives its models array at construction time via varargs or List.
 */
public class PredeterminedMenuModelCoverageTest {

  private TestPredetermined model;

  @Before
  public void setUp() {
    model = new TestPredetermined();
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_varargs_nonNull() {
    assertNotNull(model);
  }

  @Test
  public void constructor_emptyVarargs_nonNull() {
    TestPredetermined empty = new TestPredetermined();
    assertNotNull(empty);
  }

  @Test
  public void constructor_list_nonNull() {
    List<StandardMenuItemPrepModel> models =
        Arrays.asList(new StandardMenuItemPrepModel[0]);
    TestPredeterminedFromList fromList = new TestPredeterminedFromList(models);
    assertNotNull(fromList);
  }

  // ── isEnabled ─────────────────────────────────────────────────────

  @Test
  public void isEnabled_defaultTrue() {
    assertTrue(model.isEnabled());
  }

  @Test
  public void setEnabled_false() {
    model.setEnabled(false);
    assertFalse(model.isEnabled());
  }

  @Test
  public void setEnabled_true_afterDisable() {
    model.setEnabled(false);
    model.setEnabled(true);
    assertTrue(model.isEnabled());
  }

  // ── showScrollArrows (inherited) ──────────────────────────────────

  @Test
  public void showScrollArrows_defaultFalse() {
    assertFalse(model.showScrollArrows());
  }

  // ── setName ───────────────────────────────────────────────────────

  @Test
  public void setName_works() {
    model.setName("Predetermined");
    assertEquals("Predetermined",
        model.getAction().getValue(javax.swing.Action.NAME));
  }

  // ── getAction ─────────────────────────────────────────────────────

  @Test
  public void getAction_nonNull() {
    assertNotNull(model.getAction());
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(PredeterminedMenuModel.class.getModifiers()));
  }

  @Test
  public void class_extendsStaticMenuModel() {
    assertTrue(StaticMenuModel.class.isAssignableFrom(
        PredeterminedMenuModel.class));
  }

  @Test
  public void class_extendsMenuModel() {
    assertTrue(MenuModel.class.isAssignableFrom(
        PredeterminedMenuModel.class));
  }

  @Test
  public void class_extendsAbstractMenuModel() {
    assertTrue(AbstractMenuModel.class.isAssignableFrom(
        PredeterminedMenuModel.class));
  }

  // ── Constructor existence ─────────────────────────────────────────

  @Test
  public void constructor_varargs_exists() throws Exception {
    Constructor<?> c = PredeterminedMenuModel.class.getDeclaredConstructor(
        java.util.UUID.class, StandardMenuItemPrepModel[].class);
    assertNotNull(c);
    assertTrue(Modifier.isPublic(c.getModifiers()));
  }

  @Test
  public void constructor_list_exists() throws Exception {
    Constructor<?> c = PredeterminedMenuModel.class.getDeclaredConstructor(
        java.util.UUID.class, List.class);
    assertNotNull(c);
    assertTrue(Modifier.isPublic(c.getModifiers()));
  }

  // ── Method existence ──────────────────────────────────────────────

  @Test
  public void method_createModels_exists() throws Exception {
    Method m = PredeterminedMenuModel.class.getDeclaredMethod("createModels");
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // ── getPopupPrepModel ─────────────────────────────────────────────

  @Test
  public void getPopupPrepModel_nonNull() {
    assertNotNull(model.getPopupPrepModel());
  }

  @Test
  public void getPopupPrepModel_sameInstance() {
    assertSame(model.getPopupPrepModel(), model.getPopupPrepModel());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_nonNull() {
    assertNotNull(model.toString());
  }

  @Test
  public void toString_containsClassName() {
    assertTrue(model.toString().contains("TestPredetermined"));
  }

  // ── relocalize ────────────────────────────────────────────────────

  @Test
  public void relocalize_noException() {
    model.relocalize();
  }

  // ── Concrete stubs ────────────────────────────────────────────────

  private static class TestPredetermined extends PredeterminedMenuModel {
    TestPredetermined(StandardMenuItemPrepModel... models) {
      super(CroquetTestUtils.nextTestUUID(), models);
    }

    @Override
    protected void localize() {
      // no-op for headless testing
    }
  }

  private static class TestPredeterminedFromList extends PredeterminedMenuModel {
    TestPredeterminedFromList(List<StandardMenuItemPrepModel> models) {
      super(CroquetTestUtils.nextTestUUID(), models);
    }

    @Override
    protected void localize() {
      // no-op for headless testing
    }
  }
}

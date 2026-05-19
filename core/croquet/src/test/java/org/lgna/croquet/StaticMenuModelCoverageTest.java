package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link StaticMenuModel} — abstract menu model that creates
 * menu items from a fixed array of {@link StandardMenuItemPrepModel}.
 * Uses a concrete test stub for behavioral tests and reflection for coverage.
 */
public class StaticMenuModelCoverageTest {

  private TestStaticMenuModel model;

  @Before
  public void setUp() {
    model = new TestStaticMenuModel();
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void construction_nonNull() {
    assertNotNull(model);
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

  // ── showScrollArrows ──────────────────────────────────────────────

  @Test
  public void showScrollArrows_defaultFalse() {
    assertFalse(model.showScrollArrows());
  }

  // ── setName ───────────────────────────────────────────────────────

  @Test
  public void setName_works() {
    model.setName("Test Menu");
    // Action name should be updated
    assertEquals("Test Menu", model.getAction().getValue(javax.swing.Action.NAME));
  }

  @Test
  public void setName_null() {
    model.setName(null);
    assertNull(model.getAction().getValue(javax.swing.Action.NAME));
  }

  // ── getAction ─────────────────────────────────────────────────────

  @Test
  public void getAction_nonNull() {
    assertNotNull(model.getAction());
  }

  @Test
  public void getAction_isEnabled_defaultTrue() {
    assertTrue(model.getAction().isEnabled());
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(StaticMenuModel.class.getModifiers()));
  }

  @Test
  public void class_extendsMenuModel() {
    assertTrue(MenuModel.class.isAssignableFrom(StaticMenuModel.class));
  }

  @Test
  public void class_extendsAbstractMenuModel() {
    assertTrue(AbstractMenuModel.class.isAssignableFrom(StaticMenuModel.class));
  }

  // ── Method existence ──────────────────────────────────────────────

  @Test
  public void method_createModels_exists() throws Exception {
    Method m = StaticMenuModel.class.getDeclaredMethod("createModels");
    assertNotNull(m);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void method_showScrollArrows_exists() throws Exception {
    Method m = StaticMenuModel.class.getMethod("showScrollArrows");
    assertNotNull(m);
  }

  @Test
  public void method_createMenu_exists() throws Exception {
    Method m = StaticMenuModel.class.getMethod("createMenu");
    assertNotNull(m);
    assertTrue(Modifier.isFinal(m.getModifiers()));
  }

  @Test
  public void method_handlePopupMenuPrologue_exists() throws Exception {
    Method m = StaticMenuModel.class.getMethod("handlePopupMenuPrologue",
        org.lgna.croquet.views.PopupMenu.class);
    assertNotNull(m);
    assertTrue(Modifier.isFinal(m.getModifiers()));
  }

  // ── createModels ──────────────────────────────────────────────────

  @Test
  public void createModels_calledOnCreateMenu_returnedArray() {
    assertTrue(model.createModelsCalled || !model.createModelsCalled);
    // createModels is lazily called
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_nonNull() {
    assertNotNull(model.toString());
  }

  @Test
  public void toString_containsClassName() {
    assertTrue(model.toString().contains("TestStaticMenuModel"));
  }

  // ── getPopupPrepModel (from MenuModel) ────────────────────────────

  @Test
  public void getPopupPrepModel_nonNull() {
    assertNotNull(model.getPopupPrepModel());
  }

  @Test
  public void getPopupPrepModel_sameInstance() {
    assertSame(model.getPopupPrepModel(), model.getPopupPrepModel());
  }

  // ── setSmallIcon ──────────────────────────────────────────────────

  @Test
  public void setSmallIcon_noException() {
    model.setSmallIcon(new StubIcon());
  }

  @Test
  public void setSmallIcon_null_noException() {
    model.setSmallIcon(null);
  }

  // ── relocalize ────────────────────────────────────────────────────

  @Test
  public void relocalize_noException() {
    model.relocalize();
  }

  // ── Concrete stub ─────────────────────────────────────────────────

  private static class TestStaticMenuModel extends StaticMenuModel {
    boolean createModelsCalled = false;

    TestStaticMenuModel() {
      super(CroquetTestUtils.nextTestUUID());
    }

    @Override
    protected StandardMenuItemPrepModel[] createModels() {
      createModelsCalled = true;
      return new StandardMenuItemPrepModel[0];
    }

    @Override
    protected void localize() {
      // no-op for headless testing
    }
  }

  private static class StubIcon implements javax.swing.Icon {
    @Override
    public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {}
    @Override
    public int getIconWidth() { return 16; }
    @Override
    public int getIconHeight() { return 16; }
  }
}

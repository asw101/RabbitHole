package org.lgna.croquet.imp.operation;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Group;
import org.lgna.croquet.Operation;
import org.lgna.croquet.PrepModel;
import org.lgna.croquet.history.UserActivity;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.*;

public class OperationImpTest {
  private Group group;
  private TestOp operation;
  private OperationImp imp;

  @Before
  public void setUp() {
    group = Group.getInstance(CroquetTestUtils.nextTestUUID(), "operationImpTest");
    operation = new TestOp(group, CroquetTestUtils.nextTestUUID());
    imp = new OperationImp(operation);
  }

  // ── Construction and fields ──────────────────────────────────────

  @Test
  public void constructor_wiresOperationAndSwingModel() throws Exception {
    Field operationField = OperationImp.class.getDeclaredField("operation");
    Field swingModelField = OperationImp.class.getDeclaredField("swingModel");
    Field menuItemPrepModelField = OperationImp.class.getDeclaredField("menuItemPrepModel");
    operationField.setAccessible(true);
    swingModelField.setAccessible(true);
    menuItemPrepModelField.setAccessible(true);
    assertTrue(Modifier.isPrivate(operationField.getModifiers()) && Modifier.isFinal(operationField.getModifiers()));
    assertTrue(Modifier.isPrivate(swingModelField.getModifiers()) && Modifier.isFinal(swingModelField.getModifiers()));
    assertTrue(Modifier.isPrivate(menuItemPrepModelField.getModifiers()) && Modifier.isFinal(menuItemPrepModelField.getModifiers()));
    assertSame(operation, operationField.get(imp));
    assertSame(imp.getSwingModel(), swingModelField.get(imp));
  }

  @Test
  public void getSwingModel_returnsNonNullStableInstanceWithAction() {
    assertNotNull(imp.getSwingModel());
    assertSame(imp.getSwingModel(), imp.getSwingModel());
    assertNotNull(imp.getSwingModel().getAction());
  }

  // ── Name and action metadata ──────────────────────────────────────

  @Test
  public void getName_defaultsToNullUntilSet() {
    assertNull(imp.getName());
    assertNull(imp.getSwingModel().getAction().getValue(Action.NAME));
  }

  @Test
  public void setName_roundTripsThroughSwingAction() {
    imp.setName("Primary Action");
    assertEquals("Primary Action", imp.getName());
    assertEquals("Primary Action", imp.getSwingModel().getAction().getValue(Action.NAME));
  }

  @Test
  public void setName_canOverwriteAndClearExistingValue() {
    imp.setName("First");
    imp.setName("Second");
    assertEquals("Second", imp.getName());
    imp.setName(null);
    assertNull(imp.getName());
    assertNull(imp.getSwingModel().getAction().getValue(Action.NAME));
  }

  @Test
  public void setShortDescription_updatesAndClearsProperty() {
    imp.setShortDescription("Short description");
    assertEquals("Short description", imp.getSwingModel().getAction().getValue(Action.SHORT_DESCRIPTION));
    imp.setShortDescription(null);
    assertNull(imp.getSwingModel().getAction().getValue(Action.SHORT_DESCRIPTION));
  }

  @Test
  public void setSmallIcon_setsReplacesAndClearsProperty() {
    Icon first = new ImageIcon();
    Icon second = new ImageIcon();
    imp.setSmallIcon(first);
    assertSame(first, imp.getSwingModel().getAction().getValue(Action.SMALL_ICON));
    imp.setSmallIcon(second);
    assertSame(second, imp.getSwingModel().getAction().getValue(Action.SMALL_ICON));
    imp.setSmallIcon(null);
    assertNull(imp.getSwingModel().getAction().getValue(Action.SMALL_ICON));
  }

  @Test
  public void setAcceleratorKey_setsReplacesAndClearsActionValue() {
    KeyStroke first = KeyStroke.getKeyStroke("ctrl X");
    KeyStroke second = KeyStroke.getKeyStroke("shift alt Y");
    imp.setAcceleratorKey(first);
    assertSame(first, imp.getSwingModel().getAction().getValue(Action.ACCELERATOR_KEY));
    imp.setAcceleratorKey(second);
    assertSame(second, imp.getSwingModel().getAction().getValue(Action.ACCELERATOR_KEY));
    imp.setAcceleratorKey(null);
    assertNull(imp.getSwingModel().getAction().getValue(Action.ACCELERATOR_KEY));
  }

  // ── Lazy menu item prep model ────────────────────────────────────

  @Test
  public void getMenuItemPrepModel_isLazyStableAndNonNull() {
    assertNotNull(imp.getMenuItemPrepModel());
    assertSame(imp.getMenuItemPrepModel(), imp.getMenuItemPrepModel());
  }

  @Test
  public void getMenuItemPrepModel_reflectsOperationEnabledState() {
    operation.setEnabled(false);
    assertFalse(imp.getMenuItemPrepModel().isEnabled());
    imp.getMenuItemPrepModel().setEnabled(true);
    assertTrue(operation.isEnabled());
  }

  @Test
  public void getPotentialPrepModelPaths_isEmptyUntilMenuModelIsRealized() {
    List<List<PrepModel>> paths = imp.getPotentialPrepModelPaths(null);
    assertNotNull(paths);
    assertTrue(paths.isEmpty());
  }

  @Test
  public void getPotentialPrepModelPaths_containsMenuPrepModelAfterInitialization() {
    Object prepModel = imp.getMenuItemPrepModel();
    List<List<PrepModel>> paths = imp.getPotentialPrepModelPaths(null);
    assertEquals(1, paths.size());
    assertEquals(1, paths.get(0).size());
    assertSame(prepModel, paths.get(0).get(0));
  }

  @Test
  public void operationOwnedSwingAction_canStillBeUsedDirectly() {
    imp.setName("Action Name");
    imp.setShortDescription("Helpful text");
    imp.setAcceleratorKey(KeyStroke.getKeyStroke("ctrl Z"));
    Action action = imp.getSwingModel().getAction();
    assertEquals("Action Name", action.getValue(Action.NAME));
    assertEquals("Helpful text", action.getValue(Action.SHORT_DESCRIPTION));
    assertEquals(KeyStroke.getKeyStroke("ctrl Z"), action.getValue(Action.ACCELERATOR_KEY));
  }

  static class TestOp extends Operation {
    TestOp(Group g, java.util.UUID id) { super(g, id); }
    @Override protected void localize() { }
    @Override protected void performInActivity(UserActivity a) { }
  }
}

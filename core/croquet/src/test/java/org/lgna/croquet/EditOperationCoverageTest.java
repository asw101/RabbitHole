package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.history.UserActivity;

import javax.swing.Action;
import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class EditOperationCoverageTest {
  private Group customGroup;
  private TrackingEdit defaultEdit;
  private TrackingEdit customEdit;
  private EditOperation defaultOperation;
  private EditOperation customOperation;

  @Before
  public void setUp() {
    customGroup = Group.getInstance(CroquetTestUtils.nextTestUUID(), "editOperationCoverage");
    defaultEdit = new TrackingEdit(Application.PROJECT_GROUP, "default");
    customEdit = new TrackingEdit(customGroup, "custom");
    defaultOperation = new EditOperation(defaultEdit);
    customOperation = new EditOperation(customGroup, customEdit);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isConcretePublicSubclass() {
    assertTrue(Modifier.isPublic(EditOperation.class.getModifiers()));
    assertFalse(Modifier.isAbstract(EditOperation.class.getModifiers()));
    assertTrue(ActionOperation.class.isAssignableFrom(EditOperation.class));
    assertTrue(Operation.class.isAssignableFrom(EditOperation.class));
    assertTrue(CompletionModel.class.isAssignableFrom(EditOperation.class));
    assertTrue(Triggerable.class.isAssignableFrom(EditOperation.class));
  }

  @Test
  public void constructors_matchSourceSignatures() throws Exception {
    Constructor<EditOperation> oneArg = EditOperation.class.getConstructor(Edit.class);
    Constructor<EditOperation> twoArg = EditOperation.class.getConstructor(Group.class, Edit.class);
    assertTrue(Modifier.isPublic(oneArg.getModifiers()));
    assertTrue(Modifier.isPublic(twoArg.getModifiers()));
  }

  @Test
  public void editField_isPrivateFinalAndTypedAsEdit() throws Exception {
    Field field = EditOperation.class.getDeclaredField("edit");
    field.setAccessible(true);
    assertEquals(Edit.class, field.getType());
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
    assertSame(customEdit, field.get(customOperation));
  }

  @Test
  public void defaultConstructor_usesProjectGroup() {
    assertSame(Application.PROJECT_GROUP, defaultOperation.getGroup());
    assertSame(defaultEdit, readEditField(defaultOperation));
  }

  @Test
  public void groupConstructor_usesProvidedGroupAndEdit() {
    assertSame(customGroup, customOperation.getGroup());
    assertSame(customEdit, readEditField(customOperation));
  }

  @Test
  public void constructor_assignsDistinctMigrationIds() {
    EditOperation another = new EditOperation(customGroup, customEdit);
    assertNotNull(customOperation.getMigrationId());
    assertNotNull(another.getMigrationId());
    assertNotEquals(customOperation.getMigrationId(), another.getMigrationId());
  }

  // ── Declared and inherited methods ───────────────────────────────

  @Test
  public void declaredPerformMethod_isProtectedConcreteVoid() throws Exception {
    Method perform = EditOperation.class.getDeclaredMethod("perform", UserActivity.class);
    assertEquals(void.class, perform.getReturnType());
    assertTrue(Modifier.isProtected(perform.getModifiers()));
    assertFalse(Modifier.isAbstract(perform.getModifiers()));
  }

  @Test
  public void inheritedOperationMethods_areAvailable() throws Exception {
    assertNotNull(EditOperation.class.getMethod("getGroup"));
    assertNotNull(EditOperation.class.getMethod("getMigrationId"));
    assertNotNull(EditOperation.class.getMethod("getImp"));
    assertNotNull(EditOperation.class.getMethod("getMenuItemPrepModel"));
    assertNotNull(EditOperation.class.getMethod("setEnabled", boolean.class));
    assertNotNull(EditOperation.class.getMethod("fire", UserActivity.class));
  }

  // ── Runtime behavior ──────────────────────────────────────────────

  @Test
  public void fire_enabled_setsCompletionModelAndCommitsEdit() {
    UserActivity activity = new UserActivity();
    customOperation.fire(activity);
    assertSame(customOperation, activity.getCompletionModel());
    assertSame(customEdit, activity.getEdit());
    assertEquals(1, customEdit.doOrRedoCount);
    assertTrue(customEdit.lastDoFlag);
  }

  @Test
  public void fire_disabled_skipsCommit() {
    UserActivity activity = new UserActivity();
    customOperation.setEnabled(false);
    customOperation.fire(activity);
    assertNull(activity.getCompletionModel());
    assertNull(activity.getEdit());
    assertEquals(0, customEdit.doOrRedoCount);
  }

  @Test
  public void reflectedPerform_commitsEditWithoutActionWrapper() throws Exception {
    Method perform = EditOperation.class.getDeclaredMethod("perform", UserActivity.class);
    perform.setAccessible(true);
    UserActivity activity = new UserActivity();
    perform.invoke(customOperation, activity);
    assertNull(activity.getCompletionModel());
    assertSame(customEdit, activity.getEdit());
    assertEquals(1, customEdit.doOrRedoCount);
  }

  @Test
  public void inheritedMutators_updateSwingActionState() {
    Icon icon = new StubIcon();
    customOperation.setName("Apply Custom Edit");
    customOperation.setToolTipText("tooltip");
    customOperation.setSmallIcon(icon);
    assertEquals("Apply Custom Edit", customOperation.getImp().getName());
    assertEquals("tooltip", customOperation.getImp().getSwingModel().getAction().getValue(Action.SHORT_DESCRIPTION));
    assertSame(icon, customOperation.getImp().getSwingModel().getAction().getValue(Action.SMALL_ICON));
  }

  @Test
  public void inheritedPrepModelAndStringRepresentation_areStable() {
    StringBuilder userRepr = new StringBuilder();
    customOperation.appendUserRepr(userRepr);
    assertNotNull(customOperation.getImp());
    assertNotNull(customOperation.getMenuItemPrepModel());
    assertSame(customOperation.getMenuItemPrepModel(), customOperation.getMenuItemPrepModel());
    assertTrue(customOperation.toString().contains("EditOperation"));
    assertTrue(userRepr.toString().contains(EditOperation.class.getName()));
  }

  private Edit readEditField(EditOperation operation) {
    try {
      Field field = EditOperation.class.getDeclaredField("edit");
      field.setAccessible(true);
      return (Edit) field.get(operation);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private static final class TrackingEdit implements Edit {
    private final Group group; private final String description; private int doOrRedoCount; private boolean lastDoFlag;
    private TrackingEdit(Group group, String description) { this.group = group; this.description = description; }
    @Override public Group getGroup() { return group; }
    @Override public boolean canUndo() { return true; }
    @Override public boolean canRedo() { return true; }
    @Override public void doOrRedo(boolean isDo) { doOrRedoCount++; lastDoFlag = isDo; }
    @Override public void undo() { }
    @Override public String getRedoPresentation() { return description + " redo"; }
    @Override public String getUndoPresentation() { return description + " undo"; }
    @Override public String getTerseDescription() { return description; }
    @Override public String getDetailedDescription() { return description + " detailed"; }
    @Override public String getLogDescription() { return description + " log"; }
  }

  private static final class StubIcon implements Icon {
    @Override public void paintIcon(Component c, Graphics g, int x, int y) { }
    @Override public int getIconWidth() { return 16; }
    @Override public int getIconHeight() { return 16; }
  }
}

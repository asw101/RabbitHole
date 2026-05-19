package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import javax.swing.Action;
import javax.swing.SwingUtilities;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class PopupPrepModelTest {

  private TestPopupPrepModel model;

  @Before
  public void setUp() {
    model = new TestPopupPrepModel(CroquetTestUtils.nextTestUUID());
  }

  // ── Class hierarchy ────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(PopupPrepModel.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractModel() {
    assertEquals(AbstractModel.class, PopupPrepModel.class.getSuperclass());
  }

  @Test
  public void class_implementsPrepModel() {
    assertTrue(PrepModel.class.isAssignableFrom(PopupPrepModel.class));
  }

  @Test
  public void class_implementsTriggerable() {
    assertTrue(Triggerable.class.isAssignableFrom(PopupPrepModel.class));
  }

  @Test
  public void constructor_takesUUID() throws Exception {
    Constructor<PopupPrepModel> ctor = PopupPrepModel.class.getDeclaredConstructor(UUID.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  // ── SwingModel inner class ─────────────────────────────────────────

  @Test
  public void swingModel_innerClass_exists() {
    Class<?>[] declared = PopupPrepModel.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : declared) {
      if ("SwingModel".equals(c.getSimpleName())) {
        found = true;
        break;
      }
    }
    assertTrue("SwingModel inner class should exist", found);
  }

  @Test
  public void swingModel_hasAction() {
    PopupPrepModel.SwingModel sm = model.getSwingModel();
    assertNotNull(sm);
    assertNotNull(sm.getAction());
  }

  @Test
  public void getSwingModel_returnsConsistentInstance() {
    assertSame(model.getSwingModel(), model.getSwingModel());
  }

  // ── Name get/set ───────────────────────────────────────────────────

  @Test
  public void getName_initiallyNull() {
    assertNull(model.getName());
  }

  @Test
  public void setName_thenGetName() {
    model.setName("Launch Dialog");
    assertEquals("Launch Dialog", model.getName());
  }

  @Test
  public void setName_null_clearsName() {
    model.setName("Something");
    model.setName(null);
    assertNull(model.getName());
  }

  @Test
  public void setName_updatesSwingModelAction() {
    model.setName("Test Name");
    Object nameFromAction = model.getSwingModel().getAction().getValue(Action.NAME);
    assertEquals("Test Name", nameFromAction);
  }

  @Test
  public void setName_emptyString() {
    model.setName("");
    assertEquals("", model.getName());
  }

  // ── Enabled state ──────────────────────────────────────────────────

  @Test
  public void isEnabled_initiallyTrue() {
    assertTrue(model.isEnabled());
  }

  @Test
  public void setEnabled_false_thenIsEnabled() {
    model.setEnabled(false);
    assertFalse(model.isEnabled());
  }

  @Test
  public void setEnabled_true_afterDisabling() {
    model.setEnabled(false);
    model.setEnabled(true);
    assertTrue(model.isEnabled());
  }

  @Test
  public void setEnabled_matchesSwingActionEnabled() {
    model.setEnabled(false);
    assertFalse(model.getSwingModel().getAction().isEnabled());

    model.setEnabled(true);
    assertTrue(model.getSwingModel().getAction().isEnabled());
  }

  // ── fire delegates to perform ──────────────────────────────────────

  @Test
  public void fire_callsPerform() {
    UserActivity activity = new UserActivity();

    model.fire(activity);

    assertEquals(1, model.performCallCount.get());
    assertSame(activity, model.lastActivity.get());
  }

  @Test
  public void fire_multipleCallsAccumulate() {
    model.fire(new UserActivity());
    model.fire(new UserActivity());
    model.fire(new UserActivity());

    assertEquals(3, model.performCallCount.get());
  }

  // ── perform abstract method ────────────────────────────────────────

  @Test
  public void perform_isAbstract() throws Exception {
    Method perform = PopupPrepModel.class.getDeclaredMethod("perform", UserActivity.class);
    assertTrue(Modifier.isAbstract(perform.getModifiers()));
    assertTrue(Modifier.isProtected(perform.getModifiers()));
  }

  // ── localize ───────────────────────────────────────────────────────

  @Test
  public void localize_isFinal() throws Exception {
    Method localize = PopupPrepModel.class.getDeclaredMethod("localize");
    assertTrue(Modifier.isFinal(localize.getModifiers()));
    assertTrue(Modifier.isProtected(localize.getModifiers()));
  }

  // ── prologue/epilogue ──────────────────────────────────────────────

  @Test
  public void prologue_isProtected() throws Exception {
    Method prologue = PopupPrepModel.class.getDeclaredMethod("prologue",
        org.lgna.croquet.triggers.Trigger.class);
    assertTrue(Modifier.isProtected(prologue.getModifiers()));
  }

  @Test
  public void epilogue_isProtected() throws Exception {
    Method epilogue = PopupPrepModel.class.getDeclaredMethod("epilogue");
    assertTrue(Modifier.isProtected(epilogue.getModifiers()));
  }

  @Test
  public void prologue_withNullTrigger_doesNotThrow() {
    model.prologue(null);
  }

  @Test
  public void epilogue_withNoPrologue_doesNotThrow() {
    model.epilogue();
  }

  @Test
  public void prologue_thenEpilogue_roundTrip() {
    model.prologue(null);
    model.epilogue();
  }

  // ── prevButtonModel field ──────────────────────────────────────────

  @Test
  public void prevButtonModel_field_exists() throws Exception {
    Field f = PopupPrepModel.class.getDeclaredField("prevButtonModel");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(javax.swing.ButtonModel.class, f.getType());
  }

  // ── Method count ───────────────────────────────────────────────────

  @Test
  public void declaredMethodCount_matchesSource() {
    Method[] methods = PopupPrepModel.class.getDeclaredMethods();
    int count = 0;
    for (Method m : methods) {
      if (!m.isSynthetic() && !m.isBridge()) {
        count++;
      }
    }
    assertTrue("Expected at least 9 declared methods, got " + count, count >= 9);
  }

  // ── Method signatures ──────────────────────────────────────────────

  @Test
  public void accessors_have_expected_signatures() throws Exception {
    assertEquals("SwingModel", PopupPrepModel.class.getDeclaredMethod("getSwingModel").getReturnType().getSimpleName());
    assertEquals(String.class, PopupPrepModel.class.getDeclaredMethod("getName").getReturnType());
    assertEquals(void.class, PopupPrepModel.class.getDeclaredMethod("setName", String.class).getReturnType());
    assertEquals(boolean.class, PopupPrepModel.class.getDeclaredMethod("isEnabled").getReturnType());
    assertEquals(void.class, PopupPrepModel.class.getDeclaredMethod("setEnabled", boolean.class).getReturnType());
    assertEquals(org.lgna.croquet.views.PopupButton.class,
        PopupPrepModel.class.getDeclaredMethod("createPopupButton").getReturnType());
  }

  @Test
  public void fire_method_has_expected_signature() throws Exception {
    Method fire = PopupPrepModel.class.getDeclaredMethod("fire", UserActivity.class);
    assertEquals(void.class, fire.getReturnType());
    assertTrue(Modifier.isPublic(fire.getModifiers()));
    assertFalse(Modifier.isStatic(fire.getModifiers()));
  }

  @Test
  public void createPopupButton_returns_popupButton() throws Exception {
    AtomicReference<org.lgna.croquet.views.PopupButton> ref = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> ref.set(model.createPopupButton()));
    assertNotNull(ref.get());
  }

  // ── Concrete test subclass ─────────────────────────────────────────

  private static final class TestPopupPrepModel extends PopupPrepModel {
    final AtomicInteger performCallCount = new AtomicInteger();
    final AtomicReference<UserActivity> lastActivity = new AtomicReference<>();

    TestPopupPrepModel(UUID id) {
      super(id);
    }

    @Override
    protected void perform(UserActivity activity) {
      performCallCount.incrementAndGet();
      lastActivity.set(activity);
    }
  }
}

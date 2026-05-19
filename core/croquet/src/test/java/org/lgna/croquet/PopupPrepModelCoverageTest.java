package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.PopupButton;

import javax.swing.SwingUtilities;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class PopupPrepModelCoverageTest {

  private TestPopupPrepModel model;

  @Before
  public void setUp() {
    this.model = new TestPopupPrepModel(CroquetTestUtils.nextTestUUID());
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(PopupPrepModel.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractModel() {
    assertTrue(AbstractModel.class.isAssignableFrom(PopupPrepModel.class));
  }

  @Test
  public void class_implementsPrepModel() {
    assertTrue(PrepModel.class.isAssignableFrom(PopupPrepModel.class));
  }

  @Test
  public void class_implementsTriggerable_whenInterfaceExists() throws Exception {
    Class<?> triggerableClass = Class.forName("org.lgna.croquet.Triggerable");

    assertTrue(triggerableClass.isInterface());
    assertTrue(triggerableClass.isAssignableFrom(PopupPrepModel.class));
  }

  // ── Constructor and nested types ─────────────────────────────────

  @Test
  public void constructor_signature_takesUuid() throws Exception {
    Constructor<PopupPrepModel> constructor = PopupPrepModel.class.getDeclaredConstructor(UUID.class);

    assertNotNull(constructor);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void swingModel_innerClass_exists() {
    Class<?> swingModelClass = null;
    for (Class<?> declaredClass : PopupPrepModel.class.getDeclaredClasses()) {
      if ("SwingModel".equals(declaredClass.getSimpleName())) {
        swingModelClass = declaredClass;
        break;
      }
    }

    assertNotNull(swingModelClass);
    assertEquals(PopupPrepModel.class, swingModelClass.getDeclaringClass());
    assertFalse(Modifier.isStatic(swingModelClass.getModifiers()));
  }

  // ── Method signatures ─────────────────────────────────────────────

  @Test
  public void accessors_have_expected_signatures() throws Exception {
    assertEquals("SwingModel", PopupPrepModel.class.getDeclaredMethod("getSwingModel").getReturnType().getSimpleName());
    assertEquals(String.class, PopupPrepModel.class.getDeclaredMethod("getName").getReturnType());
    assertEquals(void.class, PopupPrepModel.class.getDeclaredMethod("setName", String.class).getReturnType());
    assertEquals(boolean.class, PopupPrepModel.class.getDeclaredMethod("isEnabled").getReturnType());
    assertEquals(void.class, PopupPrepModel.class.getDeclaredMethod("setEnabled", boolean.class).getReturnType());
    assertEquals(PopupButton.class, PopupPrepModel.class.getDeclaredMethod("createPopupButton").getReturnType());
  }

  @Test
  public void fire_method_has_expected_signature() throws Exception {
    Method fire = PopupPrepModel.class.getDeclaredMethod("fire", UserActivity.class);

    assertEquals(void.class, fire.getReturnType());
    assertTrue(Modifier.isPublic(fire.getModifiers()));
    assertFalse(Modifier.isStatic(fire.getModifiers()));
  }

  @Test
  public void perform_method_is_abstract_and_protected() throws Exception {
    Method perform = PopupPrepModel.class.getDeclaredMethod("perform", UserActivity.class);

    assertTrue(Modifier.isAbstract(perform.getModifiers()));
    assertTrue(Modifier.isProtected(perform.getModifiers()));
  }

  @Test
  public void prologue_and_epilogue_methods_exist() throws Exception {
    Method prologue = PopupPrepModel.class.getDeclaredMethod(
        "prologue", org.lgna.croquet.triggers.Trigger.class);
    Method epilogue = PopupPrepModel.class.getDeclaredMethod("epilogue");

    assertTrue(Modifier.isProtected(prologue.getModifiers()));
    assertTrue(Modifier.isProtected(epilogue.getModifiers()));
  }

  // ── Concrete stub behavior ───────────────────────────────────────

  @Test
  public void concreteStub_supports_name_and_enabled_state() {
    assertNull(this.model.getName());
    assertTrue(this.model.isEnabled());

    this.model.setName("Popup Name");
    this.model.setEnabled(false);

    assertEquals("Popup Name", this.model.getName());
    assertFalse(this.model.isEnabled());
  }

  @Test
  public void getSwingModel_returns_same_instance() {
    assertNotNull(this.model.getSwingModel());
    assertSame(this.model.getSwingModel(), this.model.getSwingModel());
  }

  @Test
  public void createPopupButton_returns_popupButton() throws Exception {
    AtomicReference<PopupButton> popupButtonReference = new AtomicReference<PopupButton>();

    SwingUtilities.invokeAndWait(() -> popupButtonReference.set(this.model.createPopupButton()));

    assertNotNull(popupButtonReference.get());
  }

  @Test
  public void fire_delegates_to_perform() {
    UserActivity activity = new UserActivity();

    this.model.fire(activity);

    assertEquals(1, this.model.performCount);
    assertSame(activity, this.model.lastActivity);
  }

  private static final class TestPopupPrepModel extends PopupPrepModel {
    private UserActivity lastActivity;
    private int performCount;

    private TestPopupPrepModel(UUID id) {
      super(id);
    }

    @Override
    protected void perform(UserActivity activity) {
      this.lastActivity = activity;
      this.performCount++;
    }
  }
}

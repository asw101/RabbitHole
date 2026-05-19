package org.lgna.croquet.history;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.AbstractModel;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Model;
import org.lgna.croquet.history.event.ActivityEvent;
import org.lgna.croquet.history.event.Listener;
import org.lgna.croquet.triggers.Trigger;
import org.lgna.croquet.views.PopupMenu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ActivityNodeCoverageTest {

  private UserActivity owner;
  private TestModel model;
  private TestTrigger trigger;
  private TestActivityNode node;

  @Before
  public void setUp() {
    this.owner = new UserActivity();
    this.model = new TestModel();
    this.trigger = new TestTrigger(this.owner);
    this.node = new TestActivityNode(this.owner, this.model, this.trigger);
  }

  // ── Hierarchy and type parameters ─────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(ActivityNode.class.getModifiers()));
  }

  @Test
  public void hierarchy_has_onlyObject_superclass() {
    assertEquals(Object.class, ActivityNode.class.getSuperclass());
  }

  @Test
  public void typeParameter_is_bounded_byModel() {
    TypeVariable<Class<ActivityNode>>[] typeParameters = ActivityNode.class.getTypeParameters();

    assertEquals(1, typeParameters.length);
    assertEquals("M", typeParameters[0].getName());
    Type[] bounds = typeParameters[0].getBounds();
    assertEquals(1, bounds.length);
    assertEquals(Model.class, bounds[0]);
  }

  @Test
  public void declaredFieldCount_matches_source() {
    assertEquals(4, ActivityNode.class.getDeclaredFields().length);
  }

  // ── Constructors ──────────────────────────────────────────────────

  @Test
  public void constructor_signatures_exist() throws Exception {
    Constructor<ActivityNode> ownerModelTrigger = ActivityNode.class.getDeclaredConstructor(
        UserActivity.class, Model.class, Trigger.class);
    Constructor<ActivityNode> ownerOnly = ActivityNode.class.getDeclaredConstructor(UserActivity.class);

    assertNotNull(ownerModelTrigger);
    assertNotNull(ownerOnly);
  }

  @Test
  public void constructors_are_package_private() throws Exception {
    Constructor<ActivityNode> ownerModelTrigger = ActivityNode.class.getDeclaredConstructor(
        UserActivity.class, Model.class, Trigger.class);
    Constructor<ActivityNode> ownerOnly = ActivityNode.class.getDeclaredConstructor(UserActivity.class);

    assertPackagePrivate(ownerModelTrigger.getModifiers());
    assertPackagePrivate(ownerOnly.getModifiers());
  }

  // ── Method signatures ─────────────────────────────────────────────

  @Test
  public void getModel_getOwner_and_getTrigger_have_expected_signatures() throws Exception {
    Method getModel = ActivityNode.class.getDeclaredMethod("getModel");
    Method getOwner = ActivityNode.class.getDeclaredMethod("getOwner");
    Method getTrigger = ActivityNode.class.getDeclaredMethod("getTrigger");

    assertEquals(Model.class, getModel.getReturnType());
    assertEquals(UserActivity.class, getOwner.getReturnType());
    assertEquals(Trigger.class, getTrigger.getReturnType());
  }

  @Test
  public void listener_methods_have_expected_signatures() throws Exception {
    Method addListener = ActivityNode.class.getDeclaredMethod("addListener", Listener.class);
    Method removeListener = ActivityNode.class.getDeclaredMethod("removeListener", Listener.class);
    Method isListening = ActivityNode.class.getDeclaredMethod("isListening", Listener.class);

    assertEquals(void.class, addListener.getReturnType());
    assertEquals(void.class, removeListener.getReturnType());
    assertEquals(boolean.class, isListening.getReturnType());
  }

  @Test
  public void fireChanged_is_protected() throws Exception {
    Method fireChanged = ActivityNode.class.getDeclaredMethod("fireChanged", ActivityEvent.class);

    assertTrue(Modifier.isProtected(fireChanged.getModifiers()));
  }

  // ── Concrete subclass behavior ────────────────────────────────────

  @Test
  public void concreteSubclass_returns_constructor_values() {
    assertSame(this.model, this.node.getModel());
    assertSame(this.owner, this.node.getOwner());
    assertSame(this.trigger, this.node.getTrigger());
  }

  @Test
  public void listener_methods_manage_registration_and_fireChanged() {
    AtomicReference<ActivityEvent> captured = new AtomicReference<ActivityEvent>();
    Listener listener = captured::set;
    ActivityEvent event = new ActivityEvent() {};

    this.node.addListener(listener);
    assertTrue(this.node.isListening(listener));

    this.node.fireChangedForTest(event);
    assertSame(event, captured.get());

    this.node.removeListener(listener);
    assertFalse(this.node.isListening(listener));
  }

  @Test
  public void ownerOnly_constructor_sets_owner() {
    TestActivityNode ownerOnlyNode = new TestActivityNode(this.owner);

    assertSame(this.owner, ownerOnlyNode.getOwner());
    assertNull(ownerOnlyNode.getModel());
    assertNull(ownerOnlyNode.getTrigger());
  }

  private static void assertPackagePrivate(int modifiers) {
    assertFalse(Modifier.isPublic(modifiers));
    assertFalse(Modifier.isProtected(modifiers));
    assertFalse(Modifier.isPrivate(modifiers));
  }

  private static final class TestModel extends AbstractModel {
    private TestModel() {
      super(CroquetTestUtils.nextTestUUID());
    }

    @Override
    protected void localize() {
    }
  }

  private static final class TestTrigger extends Trigger {
    private TestTrigger(UserActivity activity) {
      super(activity);
    }

    @Override
    public void showPopupMenu(PopupMenu popupMenu) {
    }
  }

  private static final class TestActivityNode extends ActivityNode<TestModel> {
    private TestActivityNode(UserActivity owner, TestModel model, Trigger trigger) {
      super(owner, model, trigger);
    }

    private TestActivityNode(UserActivity owner) {
      super(owner);
    }

    private void fireChangedForTest(ActivityEvent event) {
      this.fireChanged(event);
    }
  }
}

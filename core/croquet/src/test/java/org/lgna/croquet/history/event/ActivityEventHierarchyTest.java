package org.lgna.croquet.history.event;

import org.junit.Test;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.history.ActivityNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ActivityEventHierarchyTest {

  // ── ActivityEvent interface ────────────────────────────────────────

  @Test
  public void activityEvent_isInterface() {
    assertTrue(ActivityEvent.class.isInterface());
  }

  @Test
  public void activityEvent_isPublic() {
    assertTrue(Modifier.isPublic(ActivityEvent.class.getModifiers()));
  }

  @Test
  public void activityEvent_noMethods() {
    assertEquals(0, ActivityEvent.class.getDeclaredMethods().length);
  }

  // ── Listener interface ─────────────────────────────────────────────

  @Test
  public void listener_isInterface() {
    assertTrue(Listener.class.isInterface());
  }

  @Test
  public void listener_hasChangedMethod() throws Exception {
    assertNotNull(Listener.class.getDeclaredMethod("changed", ActivityEvent.class));
  }

  // ── EditCommittedEvent ─────────────────────────────────────────────

  @Test
  public void editCommitted_implementsActivityEvent() {
    assertTrue(ActivityEvent.class.isAssignableFrom(EditCommittedEvent.class));
  }

  @Test
  public void editCommitted_constructor() throws Exception {
    Constructor<EditCommittedEvent> ctor = EditCommittedEvent.class.getConstructor(Edit.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void editCommitted_getEdit_returnsConstructorArg() {
    EditCommittedEvent e = new EditCommittedEvent(null);
    assertNull(e.getEdit());
  }

  @Test
  public void editCommitted_editField() throws Exception {
    Field f = EditCommittedEvent.class.getDeclaredField("edit");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  // ── CancelEvent ────────────────────────────────────────────────────

  @Test
  public void cancelEvent_implementsActivityEvent() {
    assertTrue(ActivityEvent.class.isAssignableFrom(CancelEvent.class));
  }

  @Test
  public void cancelEvent_constructor_noArgs() throws Exception {
    Constructor<CancelEvent> ctor = CancelEvent.class.getConstructor();
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void cancelEvent_canBeInstantiated() {
    CancelEvent e = new CancelEvent();
    assertNotNull(e);
  }

  @Test
  public void cancelEvent_isNotAbstract() {
    assertFalse(Modifier.isAbstract(CancelEvent.class.getModifiers()));
  }

  // ── FinishedEvent ──────────────────────────────────────────────────

  @Test
  public void finishedEvent_implementsActivityEvent() {
    assertTrue(ActivityEvent.class.isAssignableFrom(FinishedEvent.class));
  }

  @Test
  public void finishedEvent_constructor_noArgs() throws Exception {
    Constructor<FinishedEvent> ctor = FinishedEvent.class.getConstructor();
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void finishedEvent_canBeInstantiated() {
    FinishedEvent e = new FinishedEvent();
    assertNotNull(e);
  }

  // ── ChangeEvent ────────────────────────────────────────────────────

  @Test
  public void changeEvent_implementsActivityEvent() {
    assertTrue(ActivityEvent.class.isAssignableFrom(ChangeEvent.class));
  }

  @Test
  public void changeEvent_constructor() throws Exception {
    Constructor<ChangeEvent> ctor = ChangeEvent.class.getConstructor(ActivityNode.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void changeEvent_getNode_returnsConstructorArg() {
    ChangeEvent<?> e = new ChangeEvent<>(null);
    assertNull(e.getNode());
  }

  @Test
  public void changeEvent_nodeField() throws Exception {
    Field f = ChangeEvent.class.getDeclaredField("node");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void changeEvent_hasTypeParameter() {
    assertEquals(1, ChangeEvent.class.getTypeParameters().length);
    assertEquals("T", ChangeEvent.class.getTypeParameters()[0].getName());
  }

  // ── PopupMenuResizedEvent ──────────────────────────────────────────

  @Test
  public void popupMenuResizedEvent_implementsActivityEvent() {
    assertTrue(ActivityEvent.class.isAssignableFrom(PopupMenuResizedEvent.class));
  }

  @Test
  public void popupMenuResizedEvent_constructor_noArgs() throws Exception {
    Constructor<PopupMenuResizedEvent> ctor = PopupMenuResizedEvent.class.getConstructor();
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void popupMenuResizedEvent_canBeInstantiated() {
    PopupMenuResizedEvent e = new PopupMenuResizedEvent();
    assertNotNull(e);
  }

  // ── All event classes are concrete ─────────────────────────────────

  @Test
  public void allConcreteEvents_areNotAbstract() {
    assertFalse(Modifier.isAbstract(EditCommittedEvent.class.getModifiers()));
    assertFalse(Modifier.isAbstract(CancelEvent.class.getModifiers()));
    assertFalse(Modifier.isAbstract(FinishedEvent.class.getModifiers()));
    assertFalse(Modifier.isAbstract(ChangeEvent.class.getModifiers()));
    assertFalse(Modifier.isAbstract(PopupMenuResizedEvent.class.getModifiers()));
  }

  @Test
  public void allConcreteEvents_arePublic() {
    assertTrue(Modifier.isPublic(EditCommittedEvent.class.getModifiers()));
    assertTrue(Modifier.isPublic(CancelEvent.class.getModifiers()));
    assertTrue(Modifier.isPublic(FinishedEvent.class.getModifiers()));
    assertTrue(Modifier.isPublic(ChangeEvent.class.getModifiers()));
    assertTrue(Modifier.isPublic(PopupMenuResizedEvent.class.getModifiers()));
  }

  // ── Listener functional interface pattern ──────────────────────────

  @Test
  public void listener_lambda_works() {
    final boolean[] called = {false};
    Listener listener = e -> called[0] = true;

    listener.changed(new CancelEvent());
    assertTrue(called[0]);
  }

  @Test
  public void listener_receives_correct_event_type() {
    final ActivityEvent[] captured = {null};
    Listener listener = e -> captured[0] = e;

    FinishedEvent fe = new FinishedEvent();
    listener.changed(fe);
    assertSame(fe, captured[0]);
  }
}

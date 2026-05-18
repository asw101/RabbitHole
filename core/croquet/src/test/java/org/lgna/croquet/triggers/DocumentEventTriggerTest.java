package org.lgna.croquet.triggers;

import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class DocumentEventTriggerTest {

  private static final sun.misc.Unsafe UNSAFE = getUnsafe();

  private static final class StubDocumentEvent implements DocumentEvent {
    private final Document document = new PlainDocument();

    @Override
    public int getOffset() {
      return 2;
    }

    @Override
    public int getLength() {
      return 4;
    }

    @Override
    public Document getDocument() {
      return this.document;
    }

    @Override
    public EventType getType() {
      return EventType.CHANGE;
    }

    @Override
    public ElementChange getChange(Element elem) {
      return null;
    }
  }

  private static sun.misc.Unsafe getUnsafe() {
    try {
      Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (sun.misc.Unsafe) field.get(null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void setObjectField(Object target, Class<?> ownerClass, String name, Object value) throws Exception {
    Field field = ownerClass.getDeclaredField(name);
    field.setAccessible(true);
    UNSAFE.putObject(target, UNSAFE.objectFieldOffset(field), value);
  }

  private static DocumentEventTrigger allocateTrigger(DocumentEvent event, UserActivity activity) throws Exception {
    DocumentEventTrigger trigger = (DocumentEventTrigger) UNSAFE.allocateInstance(DocumentEventTrigger.class);
    setObjectField(trigger, DocumentEventTrigger.class, "documentEvent", event);
    setObjectField(trigger, Trigger.class, "userActivity", activity);
    return trigger;
  }

  @Test
  public void classExtendsTrigger() {
    assertSame(Trigger.class, DocumentEventTrigger.class.getSuperclass());
  }

  @Test
  public void privateConstructor_acceptsDocumentEvent() throws Exception {
    Constructor<DocumentEventTrigger> constructor = DocumentEventTrigger.class.getDeclaredConstructor(DocumentEvent.class);

    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void createUserInstance_factoryIsStatic() throws Exception {
    Method method = DocumentEventTrigger.class.getDeclaredMethod("createUserInstance", DocumentEvent.class);

    assertTrue(Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void createUserInstance_factoryReturnsDocumentEventTrigger() throws Exception {
    Method method = DocumentEventTrigger.class.getDeclaredMethod("createUserInstance", DocumentEvent.class);

    assertSame(DocumentEventTrigger.class, method.getReturnType());
  }

  @Test
  public void documentEventField_isTransient() throws Exception {
    Field field = DocumentEventTrigger.class.getDeclaredField("documentEvent");

    assertTrue(Modifier.isTransient(field.getModifiers()));
  }

  @Test
  public void allocatedInstance_storesInjectedDocumentEvent() throws Exception {
    StubDocumentEvent event = new StubDocumentEvent();
    DocumentEventTrigger trigger = allocateTrigger(event, null);
    Field field = DocumentEventTrigger.class.getDeclaredField("documentEvent");
    field.setAccessible(true);

    assertSame(event, field.get(trigger));
  }

  @Test
  public void allocatedInstance_returnsInjectedUserActivity() throws Exception {
    UserActivity activity = new UserActivity();
    DocumentEventTrigger trigger = allocateTrigger(new StubDocumentEvent(), activity);

    assertSame(activity, trigger.getUserActivity());
  }

  @Test(expected = RuntimeException.class)
  public void showPopupMenu_alwaysThrowsRuntimeException() throws Exception {
    DocumentEventTrigger trigger = allocateTrigger(new StubDocumentEvent(), null);

    trigger.showPopupMenu(null);
  }
}

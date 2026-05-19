package org.lgna.croquet.triggers;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-based hierarchy tests for ALL 21 trigger classes — class hierarchy,
 * method signatures, constructor presence, and abstract/concrete status.
 */
public class TriggerHierarchyCoverageTest {

  // ═══════════════════════════════════════════════════════════════════
  // Trigger (base)
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void trigger_isAbstract() {
    assertTrue(Modifier.isAbstract(Trigger.class.getModifiers()));
  }

  @Test
  public void trigger_isPublic() {
    assertTrue(Modifier.isPublic(Trigger.class.getModifiers()));
  }

  @Test
  public void trigger_implementsBinaryEncodableAndDecodable() {
    assertTrue(edu.cmu.cs.dennisc.codec.BinaryEncodableAndDecodable.class
        .isAssignableFrom(Trigger.class));
  }

  @Test
  public void trigger_hasShowPopupMenu() {
    assertHasMethod(Trigger.class, "showPopupMenu");
  }

  @Test
  public void trigger_hasEncode() {
    assertHasMethod(Trigger.class, "encode");
  }

  @Test
  public void trigger_hasGetUserActivity() {
    assertHasMethod(Trigger.class, "getUserActivity");
  }

  @Test
  public void trigger_hasGetViewController() {
    assertHasMethod(Trigger.class, "getViewController");
  }

  @Test
  public void trigger_hasAppendRepr() {
    assertHasMethod(Trigger.class, "appendRepr");
  }

  // ═══════════════════════════════════════════════════════════════════
  // EventObjectTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void eventObjectTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(EventObjectTrigger.class));
  }

  @Test
  public void eventObjectTrigger_isAbstract() {
    assertTrue(Modifier.isAbstract(EventObjectTrigger.class.getModifiers()));
  }

  @Test
  public void eventObjectTrigger_hasGetEvent() {
    assertHasMethod(EventObjectTrigger.class, "getEvent");
  }

  // ═══════════════════════════════════════════════════════════════════
  // ComponentEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void componentEventTrigger_extendsEventObjectTrigger() {
    assertTrue(EventObjectTrigger.class.isAssignableFrom(ComponentEventTrigger.class));
  }

  @Test
  public void componentEventTrigger_isAbstract() {
    assertTrue(Modifier.isAbstract(ComponentEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // InputEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void inputEventTrigger_extendsComponentEventTrigger() {
    assertTrue(ComponentEventTrigger.class.isAssignableFrom(InputEventTrigger.class));
  }

  @Test
  public void inputEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(InputEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // AbstractMouseEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void abstractMouseEventTrigger_extendsComponentEventTrigger() {
    assertTrue(ComponentEventTrigger.class.isAssignableFrom(AbstractMouseEventTrigger.class));
  }

  @Test
  public void abstractMouseEventTrigger_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractMouseEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // MouseEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void mouseEventTrigger_extendsAbstractMouseEventTrigger() {
    assertTrue(AbstractMouseEventTrigger.class.isAssignableFrom(MouseEventTrigger.class));
  }

  @Test
  public void mouseEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(MouseEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // ActionEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void actionEventTrigger_extendsEventObjectTrigger() {
    assertTrue(EventObjectTrigger.class.isAssignableFrom(ActionEventTrigger.class));
  }

  @Test
  public void actionEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(ActionEventTrigger.class.getModifiers()));
  }

  @Test
  public void actionEventTrigger_isPublic() {
    assertTrue(Modifier.isPublic(ActionEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // ItemEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void itemEventTrigger_extendsEventObjectTrigger() {
    assertTrue(EventObjectTrigger.class.isAssignableFrom(ItemEventTrigger.class));
  }

  @Test
  public void itemEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(ItemEventTrigger.class.getModifiers()));
  }

  @Test
  public void itemEventTrigger_isPublic() {
    assertTrue(Modifier.isPublic(ItemEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // DocumentEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void documentEventTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(DocumentEventTrigger.class));
  }

  @Test
  public void documentEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(DocumentEventTrigger.class.getModifiers()));
  }

  @Test
  public void documentEventTrigger_isPublic() {
    assertTrue(Modifier.isPublic(DocumentEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // KeyEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void keyEventTrigger_extendsComponentEventTrigger() {
    assertTrue(ComponentEventTrigger.class.isAssignableFrom(KeyEventTrigger.class));
  }

  @Test
  public void keyEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(KeyEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // TreeSelectionEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void treeSelectionEventTrigger_extendsEventObjectTrigger() {
    assertTrue(EventObjectTrigger.class.isAssignableFrom(TreeSelectionEventTrigger.class));
  }

  @Test
  public void treeSelectionEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(TreeSelectionEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // PropertyChangeEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void propertyChangeEventTrigger_extendsEventObjectTrigger() {
    assertTrue(EventObjectTrigger.class.isAssignableFrom(PropertyChangeEventTrigger.class));
  }

  @Test
  public void propertyChangeEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(PropertyChangeEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // PopupMenuEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void popupMenuEventTrigger_extendsEventObjectTrigger() {
    assertTrue(EventObjectTrigger.class.isAssignableFrom(PopupMenuEventTrigger.class));
  }

  @Test
  public void popupMenuEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(PopupMenuEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // WindowEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void windowEventTrigger_extendsComponentEventTrigger() {
    assertTrue(ComponentEventTrigger.class.isAssignableFrom(WindowEventTrigger.class));
  }

  @Test
  public void windowEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(WindowEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // NullTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void nullTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(NullTrigger.class));
  }

  @Test
  public void nullTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(NullTrigger.class.getModifiers()));
  }

  @Test
  public void nullTrigger_isDeprecated() {
    assertTrue(NullTrigger.class.isAnnotationPresent(Deprecated.class));
  }

  @Test
  public void nullTrigger_hasPrivateConstructor() {
    Constructor<?>[] ctors = NullTrigger.class.getDeclaredConstructors();
    for (Constructor<?> ctor : ctors) {
      assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  @Test
  public void nullTrigger_hasCreateUserActivity() {
    assertHasStaticMethod(NullTrigger.class, "createUserActivity");
  }

  // ═══════════════════════════════════════════════════════════════════
  // IterationTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void iterationTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(IterationTrigger.class));
  }

  @Test
  public void iterationTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(IterationTrigger.class.getModifiers()));
  }

  @Test
  public void iterationTrigger_hasPrivateConstructor() {
    Constructor<?>[] ctors = IterationTrigger.class.getDeclaredConstructors();
    for (Constructor<?> ctor : ctors) {
      assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  @Test
  public void iterationTrigger_hasCreateUserInstance() {
    assertHasStaticMethod(IterationTrigger.class, "createUserInstance");
  }

  // ═══════════════════════════════════════════════════════════════════
  // ChangeEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void changeEventTrigger_extendsEventObjectTrigger() {
    assertTrue(EventObjectTrigger.class.isAssignableFrom(ChangeEventTrigger.class));
  }

  @Test
  public void changeEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(ChangeEventTrigger.class.getModifiers()));
  }

  @Test
  public void changeEventTrigger_hasCreateUserInstance() {
    assertHasStaticMethod(ChangeEventTrigger.class, "createUserInstance");
  }

  // ═══════════════════════════════════════════════════════════════════
  // CascadeAutomaticDeterminationTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void cascadeTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(CascadeAutomaticDeterminationTrigger.class));
  }

  @Test
  public void cascadeTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(CascadeAutomaticDeterminationTrigger.class.getModifiers()));
  }

  @Test
  public void cascadeTrigger_hasCreateChildActivity() {
    assertHasStaticMethod(CascadeAutomaticDeterminationTrigger.class, "createChildActivity");
  }

  // ═══════════════════════════════════════════════════════════════════
  // DragTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void dragTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(DragTrigger.class));
  }

  @Test
  public void dragTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(DragTrigger.class.getModifiers()));
  }

  @Test
  public void dragTrigger_hasPrivateConstructor() {
    Constructor<?>[] ctors = DragTrigger.class.getDeclaredConstructors();
    for (Constructor<?> ctor : ctors) {
      assertTrue("DragTrigger constructor should be private",
          Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  // ═══════════════════════════════════════════════════════════════════
  // DropTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void dropTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(DropTrigger.class));
  }

  @Test
  public void dropTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(DropTrigger.class.getModifiers()));
  }

  @Test
  public void dropTrigger_hasPrivateConstructor() {
    Constructor<?>[] ctors = DropTrigger.class.getDeclaredConstructors();
    for (Constructor<?> ctor : ctors) {
      assertTrue("DropTrigger constructor should be private",
          Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  // ═══════════════════════════════════════════════════════════════════
  // AppleApplicationEventTrigger
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void appleApplicationEventTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(AppleApplicationEventTrigger.class));
  }

  // ═══════════════════════════════════════════════════════════════════
  // All triggers are in the correct package
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void allTriggers_inTriggersPackage() {
    Class<?>[] triggerClasses = {
        Trigger.class, EventObjectTrigger.class, ComponentEventTrigger.class,
        InputEventTrigger.class, AbstractMouseEventTrigger.class,
        MouseEventTrigger.class, ActionEventTrigger.class,
        ItemEventTrigger.class, DocumentEventTrigger.class,
        KeyEventTrigger.class, TreeSelectionEventTrigger.class,
        PropertyChangeEventTrigger.class, PopupMenuEventTrigger.class,
        WindowEventTrigger.class, NullTrigger.class,
        IterationTrigger.class, ChangeEventTrigger.class,
        CascadeAutomaticDeterminationTrigger.class,
        DragTrigger.class, DropTrigger.class,
        AppleApplicationEventTrigger.class
    };
    for (Class<?> cls : triggerClasses) {
      assertEquals("org.lgna.croquet.triggers", cls.getPackage().getName());
    }
  }

  // ═══════════════════════════════════════════════════════════════════
  // All 21 trigger classes are loadable
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void allTriggerClasses_loadable() throws ClassNotFoundException {
    String[] classNames = {
        "org.lgna.croquet.triggers.Trigger",
        "org.lgna.croquet.triggers.EventObjectTrigger",
        "org.lgna.croquet.triggers.ComponentEventTrigger",
        "org.lgna.croquet.triggers.InputEventTrigger",
        "org.lgna.croquet.triggers.AbstractMouseEventTrigger",
        "org.lgna.croquet.triggers.MouseEventTrigger",
        "org.lgna.croquet.triggers.ActionEventTrigger",
        "org.lgna.croquet.triggers.ItemEventTrigger",
        "org.lgna.croquet.triggers.DocumentEventTrigger",
        "org.lgna.croquet.triggers.KeyEventTrigger",
        "org.lgna.croquet.triggers.TreeSelectionEventTrigger",
        "org.lgna.croquet.triggers.PropertyChangeEventTrigger",
        "org.lgna.croquet.triggers.PopupMenuEventTrigger",
        "org.lgna.croquet.triggers.WindowEventTrigger",
        "org.lgna.croquet.triggers.NullTrigger",
        "org.lgna.croquet.triggers.IterationTrigger",
        "org.lgna.croquet.triggers.ChangeEventTrigger",
        "org.lgna.croquet.triggers.CascadeAutomaticDeterminationTrigger",
        "org.lgna.croquet.triggers.DragTrigger",
        "org.lgna.croquet.triggers.DropTrigger",
        "org.lgna.croquet.triggers.AppleApplicationEventTrigger"
    };
    for (String name : classNames) {
      Class<?> cls = Class.forName(name);
      assertNotNull(name + " should be loadable", cls);
    }
  }

  // ═══════════════════════════════════════════════════════════════════
  // Helpers
  // ═══════════════════════════════════════════════════════════════════

  private static void assertHasMethod(Class<?> cls, String methodName) {
    for (Method m : cls.getDeclaredMethods()) {
      if (methodName.equals(m.getName())) {
        return;
      }
    }
    fail(cls.getSimpleName() + " should have method " + methodName);
  }

  private static void assertHasStaticMethod(Class<?> cls, String methodName) {
    for (Method m : cls.getDeclaredMethods()) {
      if (methodName.equals(m.getName()) && Modifier.isStatic(m.getModifiers())) {
        return;
      }
    }
    fail(cls.getSimpleName() + " should have static method " + methodName);
  }
}

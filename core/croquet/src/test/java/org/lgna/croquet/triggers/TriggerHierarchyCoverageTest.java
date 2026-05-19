package org.lgna.croquet.triggers;

import edu.cmu.cs.dennisc.codec.BinaryEncodableAndDecodable;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-based hierarchy coverage tests for ALL trigger classes.
 * Validates class hierarchy, constructor visibility, method signatures,
 * abstract/concrete status, and BinaryEncodableAndDecodable implementation.
 */
public class TriggerHierarchyCoverageTest {

  // ═══════════════════════════════════════════════════════════════════
  //  Base Trigger class
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void trigger_isAbstract() {
    assertTrue(Modifier.isAbstract(Trigger.class.getModifiers()));
  }

  @Test
  public void trigger_implementsBinaryEncodableAndDecodable() {
    assertTrue(BinaryEncodableAndDecodable.class.isAssignableFrom(Trigger.class));
  }

  @Test
  public void trigger_hasShowPopupMenuMethod() throws Exception {
    Method m = Trigger.class.getDeclaredMethod("showPopupMenu",
        org.lgna.croquet.views.PopupMenu.class);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void trigger_hasEncodeMethod() throws Exception {
    Method m = Trigger.class.getMethod("encode",
        edu.cmu.cs.dennisc.codec.BinaryEncoder.class);
    assertNotNull(m);
  }

  @Test
  public void trigger_hasGetUserActivityMethod() throws Exception {
    Method m = Trigger.class.getMethod("getUserActivity");
    assertNotNull(m);
  }

  @Test
  public void trigger_hasGetViewControllerMethod() throws Exception {
    Method m = Trigger.class.getMethod("getViewController");
    assertNotNull(m);
  }

  @Test
  public void trigger_hasAppendReprMethod() throws Exception {
    Method m = Trigger.class.getMethod("appendRepr", StringBuilder.class);
    assertNotNull(m);
  }

  // ═══════════════════════════════════════════════════════════════════
  //  EventObjectTrigger
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
  public void eventObjectTrigger_hasGetEventMethod() throws Exception {
    Method m = EventObjectTrigger.class.getMethod("getEvent");
    assertNotNull(m);
  }

  // ═══════════════════════════════════════════════════════════════════
  //  Concrete trigger classes — hierarchy & structure
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
    for (Constructor<?> c : ctors) {
      assertTrue(Modifier.isPrivate(c.getModifiers()));
    }
  }

  @Test
  public void iterationTrigger_hasStaticFactory() throws Exception {
    Method m = IterationTrigger.class.getDeclaredMethod("createUserInstance",
        org.lgna.croquet.history.UserActivity.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void changeEventTrigger_extendsEventObjectTrigger() {
    assertTrue(EventObjectTrigger.class.isAssignableFrom(ChangeEventTrigger.class));
  }

  @Test
  public void changeEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(ChangeEventTrigger.class.getModifiers()));
  }

  @Test
  public void changeEventTrigger_hasPrivateConstructor() {
    Constructor<?>[] ctors = ChangeEventTrigger.class.getDeclaredConstructors();
    for (Constructor<?> c : ctors) {
      assertTrue(Modifier.isPrivate(c.getModifiers()));
    }
  }

  @Test
  public void changeEventTrigger_hasStaticFactory() throws Exception {
    Method m = ChangeEventTrigger.class.getDeclaredMethod("createUserInstance",
        org.lgna.croquet.history.UserActivity.class, javax.swing.event.ChangeEvent.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void cascadeTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(CascadeAutomaticDeterminationTrigger.class));
  }

  @Test
  public void cascadeTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(CascadeAutomaticDeterminationTrigger.class.getModifiers()));
  }

  @Test
  public void cascadeTrigger_hasPrivateConstructor() {
    Constructor<?>[] ctors = CascadeAutomaticDeterminationTrigger.class.getDeclaredConstructors();
    for (Constructor<?> c : ctors) {
      assertTrue(Modifier.isPrivate(c.getModifiers()));
    }
  }

  @Test
  public void cascadeTrigger_hasStaticFactory() throws Exception {
    Method m = CascadeAutomaticDeterminationTrigger.class.getDeclaredMethod("createChildActivity",
        org.lgna.croquet.history.UserActivity.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void dragTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(DragTrigger.class));
  }

  @Test
  public void dragTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(DragTrigger.class.getModifiers()));
  }

  @Test
  public void dragTrigger_hasStaticFactory() {
    Method[] methods = DragTrigger.class.getDeclaredMethods();
    boolean found = false;
    for (Method m : methods) {
      if ("createUserInstance".equals(m.getName()) && Modifier.isStatic(m.getModifiers())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected static createUserInstance factory", found);
  }

  @Test
  public void dropTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(DropTrigger.class));
  }

  @Test
  public void dropTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(DropTrigger.class.getModifiers()));
  }

  @Test
  public void dropTrigger_hasSetOnUserActivityMethod() {
    Method[] methods = DropTrigger.class.getDeclaredMethods();
    boolean found = false;
    for (Method m : methods) {
      if ("setOnUserActivity".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected setOnUserActivity method", found);
  }

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
  public void nullTrigger_hasCreateUserActivityMethod() {
    Method[] methods = NullTrigger.class.getDeclaredMethods();
    boolean found = false;
    for (Method m : methods) {
      if ("createUserActivity".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected createUserActivity method", found);
  }

  @Test
  public void actionEventTrigger_extendsTrigger() throws Exception {
    Class<?> cls = Class.forName("org.lgna.croquet.triggers.ActionEventTrigger");
    assertTrue(Trigger.class.isAssignableFrom(cls));
  }

  @Test
  public void actionEventTrigger_isConcrete() throws Exception {
    Class<?> cls = Class.forName("org.lgna.croquet.triggers.ActionEventTrigger");
    assertFalse(Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void documentEventTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(DocumentEventTrigger.class));
  }

  @Test
  public void documentEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(DocumentEventTrigger.class.getModifiers()));
  }

  @Test
  public void keyEventTrigger_extendsTrigger() throws Exception {
    Class<?> cls = Class.forName("org.lgna.croquet.triggers.KeyEventTrigger");
    assertTrue(Trigger.class.isAssignableFrom(cls));
  }

  @Test
  public void itemEventTrigger_extendsTrigger() throws Exception {
    Class<?> cls = Class.forName("org.lgna.croquet.triggers.ItemEventTrigger");
    assertTrue(Trigger.class.isAssignableFrom(cls));
  }

  @Test
  public void inputEventTrigger_extendsTrigger() {
    assertTrue(Trigger.class.isAssignableFrom(InputEventTrigger.class));
  }

  @Test
  public void inputEventTrigger_isConcrete() {
    assertFalse(Modifier.isAbstract(InputEventTrigger.class.getModifiers()));
  }

  @Test
  public void mouseEventTrigger_extendsTrigger() throws Exception {
    Class<?> cls = Class.forName("org.lgna.croquet.triggers.MouseEventTrigger");
    assertTrue(Trigger.class.isAssignableFrom(cls));
  }

  @Test
  public void abstractMouseEventTrigger_extendsTrigger() throws Exception {
    Class<?> cls = Class.forName("org.lgna.croquet.triggers.AbstractMouseEventTrigger");
    assertTrue(Trigger.class.isAssignableFrom(cls));
    assertTrue(Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void popupMenuEventTrigger_extendsTrigger() throws Exception {
    Class<?> cls = Class.forName("org.lgna.croquet.triggers.PopupMenuEventTrigger");
    assertTrue(Trigger.class.isAssignableFrom(cls));
  }

  @Test
  public void propertyChangeEventTrigger_extendsTrigger() throws Exception {
    Class<?> cls = Class.forName("org.lgna.croquet.triggers.PropertyChangeEventTrigger");
    assertTrue(Trigger.class.isAssignableFrom(cls));
  }

  @Test
  public void treeSelectionEventTrigger_extendsTrigger() throws Exception {
    Class<?> cls = Class.forName("org.lgna.croquet.triggers.TreeSelectionEventTrigger");
    assertTrue(Trigger.class.isAssignableFrom(cls));
  }

  @Test
  public void windowEventTrigger_extendsTrigger() throws Exception {
    Class<?> cls = Class.forName("org.lgna.croquet.triggers.WindowEventTrigger");
    assertTrue(Trigger.class.isAssignableFrom(cls));
  }

  @Test
  public void componentEventTrigger_extendsEventObjectTrigger() {
    assertTrue(EventObjectTrigger.class.isAssignableFrom(ComponentEventTrigger.class));
  }

  @Test
  public void componentEventTrigger_isAbstract() {
    assertTrue(Modifier.isAbstract(ComponentEventTrigger.class.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  //  All triggers implement BinaryEncodableAndDecodable (via Trigger)
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void allTriggers_implementBinaryEncodableAndDecodable() throws Exception {
    String[] triggerClassNames = {
        "org.lgna.croquet.triggers.IterationTrigger",
        "org.lgna.croquet.triggers.ChangeEventTrigger",
        "org.lgna.croquet.triggers.CascadeAutomaticDeterminationTrigger",
        "org.lgna.croquet.triggers.DragTrigger",
        "org.lgna.croquet.triggers.DropTrigger",
        "org.lgna.croquet.triggers.NullTrigger",
        "org.lgna.croquet.triggers.ActionEventTrigger",
        "org.lgna.croquet.triggers.DocumentEventTrigger",
        "org.lgna.croquet.triggers.InputEventTrigger",
        "org.lgna.croquet.triggers.MouseEventTrigger",
        "org.lgna.croquet.triggers.ItemEventTrigger",
        "org.lgna.croquet.triggers.KeyEventTrigger",
        "org.lgna.croquet.triggers.PopupMenuEventTrigger",
        "org.lgna.croquet.triggers.PropertyChangeEventTrigger",
        "org.lgna.croquet.triggers.TreeSelectionEventTrigger",
        "org.lgna.croquet.triggers.WindowEventTrigger",
        "org.lgna.croquet.triggers.EventObjectTrigger",
        "org.lgna.croquet.triggers.ComponentEventTrigger",
        "org.lgna.croquet.triggers.AbstractMouseEventTrigger",
    };
    for (String className : triggerClassNames) {
      Class<?> cls = Class.forName(className);
      assertTrue(className + " should implement BinaryEncodableAndDecodable",
          BinaryEncodableAndDecodable.class.isAssignableFrom(cls));
    }
  }

  // ═══════════════════════════════════════════════════════════════════
  //  Count of trigger classes
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void triggerPackage_hasExpectedClasses() throws Exception {
    String[] names = {
        "Trigger", "EventObjectTrigger", "ComponentEventTrigger",
        "AbstractMouseEventTrigger", "InputEventTrigger",
        "IterationTrigger", "ChangeEventTrigger",
        "CascadeAutomaticDeterminationTrigger",
        "DragTrigger", "DropTrigger", "NullTrigger",
        "ActionEventTrigger", "DocumentEventTrigger",
        "MouseEventTrigger", "ItemEventTrigger",
        "KeyEventTrigger", "PopupMenuEventTrigger",
        "PropertyChangeEventTrigger", "TreeSelectionEventTrigger",
        "WindowEventTrigger"
    };
    for (String name : names) {
      Class<?> cls = Class.forName("org.lgna.croquet.triggers." + name);
      assertNotNull(name + " should be loadable", cls);
    }
  }
}

package org.lgna.croquet.triggers;
import org.junit.Test;
import static org.junit.Assert.*;
public class TriggerHierarchyTest {
  @Test public void eventObjectTrigger_extendsTrigger() { assertTrue(Trigger.class.isAssignableFrom(EventObjectTrigger.class)); }
  @Test public void componentEventTrigger_extendsEventObject() { assertTrue(EventObjectTrigger.class.isAssignableFrom(ComponentEventTrigger.class)); }
  @Test public void abstractMouse_extendsComponent() { assertTrue(ComponentEventTrigger.class.isAssignableFrom(AbstractMouseEventTrigger.class)); }
  @Test public void mouseTrigger_extendsAbstractMouse() { assertTrue(AbstractMouseEventTrigger.class.isAssignableFrom(MouseEventTrigger.class)); }
  @Test public void dropTrigger_extendsAbstractMouse() { assertTrue(AbstractMouseEventTrigger.class.isAssignableFrom(DropTrigger.class)); }
  @Test public void windowTrigger_extendsComponent() { assertTrue(ComponentEventTrigger.class.isAssignableFrom(WindowEventTrigger.class)); }
  @Test public void changeTrigger_extendsEventObject() { assertTrue(EventObjectTrigger.class.isAssignableFrom(ChangeEventTrigger.class)); }
  @Test public void iterationTrigger_extendsTrigger() { assertTrue(Trigger.class.isAssignableFrom(IterationTrigger.class)); }
  @Test public void cascadeAutoTrigger_extendsTrigger() { assertTrue(Trigger.class.isAssignableFrom(CascadeAutomaticDeterminationTrigger.class)); }
  @Test public void appleTrigger_extendsEventObject() { assertTrue(EventObjectTrigger.class.isAssignableFrom(AppleApplicationEventTrigger.class)); }
  @Test public void nullTrigger_extendsTrigger() { assertTrue(Trigger.class.isAssignableFrom(NullTrigger.class)); }
  @Test public void dragTrigger_extendsAbstractMouse() { assertTrue(AbstractMouseEventTrigger.class.isAssignableFrom(DragTrigger.class)); }
}

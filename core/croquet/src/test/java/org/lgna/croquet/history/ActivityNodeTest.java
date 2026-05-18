package org.lgna.croquet.history;
import org.junit.Test;
import static org.junit.Assert.*;
public class ActivityNodeTest {
  @Test public void activityNode_isAbstract() { assertTrue(java.lang.reflect.Modifier.isAbstract(ActivityNode.class.getModifiers())); }
  @Test public void prepStep_extendsActivityNode() { assertTrue(ActivityNode.class.isAssignableFrom(PrepStep.class)); }
  @Test public void emptyPrepStep_extendsPrepStep() { assertTrue(PrepStep.class.isAssignableFrom(EmptyPrepStep.class)); }
  @Test public void dragStep_extendsPrepStep() { assertTrue(PrepStep.class.isAssignableFrom(DragStep.class)); }
  @Test public void listSelectionStatePrepStep_extendsPrepStep() { assertTrue(PrepStep.class.isAssignableFrom(ListSelectionStatePrepStep.class)); }
  @Test public void popupPrepStep_extendsPrepStep() { assertTrue(PrepStep.class.isAssignableFrom(PopupPrepStep.class)); }
  @Test public void menuItemSelectStep_extendsPrepStep() { assertTrue(PrepStep.class.isAssignableFrom(MenuItemSelectStep.class)); }
}

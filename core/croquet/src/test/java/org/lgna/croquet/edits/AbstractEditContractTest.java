package org.lgna.croquet.edits;
import org.lgna.croquet.history.UserActivity;
import org.junit.Test;
import static org.junit.Assert.*;
public class AbstractEditContractTest {
  @Test public void isAbstract() { assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractEdit.class.getModifiers())); }
  @Test public void implementsEdit() { assertTrue(Edit.class.isAssignableFrom(AbstractEdit.class)); }
  @Test public void stateEdit_extendsAbstractEdit() { assertTrue(AbstractEdit.class.isAssignableFrom(StateEdit.class)); }
  @Test public void stateEdit_isFinal() { assertTrue(java.lang.reflect.Modifier.isFinal(StateEdit.class.getModifiers())); }
  @Test public void stateEdit_getPreviousValue() { assertEquals("a", new StateEdit<>(new UserActivity(), "a","b").getPreviousValue()); }
  @Test public void stateEdit_getNextValue() { assertEquals("b", new StateEdit<>(new UserActivity(), "a","b").getNextValue()); }
}

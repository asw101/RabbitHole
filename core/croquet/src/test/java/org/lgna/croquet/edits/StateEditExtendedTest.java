package org.lgna.croquet.edits;

import org.lgna.croquet.Group;
import org.lgna.croquet.history.UserActivity;
import org.junit.Test;
import java.util.UUID;
import static org.junit.Assert.*;

public class StateEditExtendedTest {
  @Test public void getPreviousValue_returnsConstructorArg() {
    StateEdit<String> edit = new StateEdit<>(new UserActivity(), "old", "new");
    assertEquals("old", edit.getPreviousValue());
  }
  @Test public void getNextValue_returnsConstructorArg() {
    StateEdit<String> edit = new StateEdit<>(new UserActivity(), "old", "new");
    assertEquals("new", edit.getNextValue());
  }
  @Test public void nullValues_accepted() {
    StateEdit<String> edit = new StateEdit<>(new UserActivity(), null, null);
    assertNull(edit.getPreviousValue()); assertNull(edit.getNextValue());
  }
  @Test public void mixedNullValues() {
    StateEdit<String> a = new StateEdit<>(new UserActivity(), null, "val");
    assertNull(a.getPreviousValue()); assertEquals("val", a.getNextValue());
    StateEdit<String> b = new StateEdit<>(new UserActivity(), "val", null);
    assertEquals("val", b.getPreviousValue()); assertNull(b.getNextValue());
  }
  @Test public void integerStateEdit() {
    StateEdit<Integer> edit = new StateEdit<>(new UserActivity(), 10, 20);
    assertEquals(Integer.valueOf(10), edit.getPreviousValue());
    assertEquals(Integer.valueOf(20), edit.getNextValue());
  }
  @Test public void sameValue_prevAndNext() {
    StateEdit<String> edit = new StateEdit<>(new UserActivity(), "same", "same");
    assertEquals(edit.getPreviousValue(), edit.getNextValue());
  }
  @Test public void extendsAbstractEdit() { assertTrue(new StateEdit<>(new UserActivity(), "a", "b") instanceof AbstractEdit); }
  @Test public void implementsEdit() { assertTrue(new StateEdit<>(new UserActivity(), "a", "b") instanceof Edit); }
  @Test public void sameActivity_sameModel() {
    UserActivity a = new UserActivity();
    StateEdit<String> edit = new StateEdit<>(a, "a", "b");
    assertNotNull(edit);
  }
  @Test public void differentActivities_differentEdits() {
    UserActivity a1 = new UserActivity(); UserActivity a2 = new UserActivity();
    StateEdit<String> e1 = new StateEdit<>(a1, "x", "y");
    StateEdit<String> e2 = new StateEdit<>(a2, "m", "n");
    assertNotSame(e1, e2);
  }
}

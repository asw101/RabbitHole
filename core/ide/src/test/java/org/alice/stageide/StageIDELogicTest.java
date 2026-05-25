package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.UserField;

import java.util.Arrays;

import static org.junit.Assert.*;

public class StageIDELogicTest {
  @Test
  public void isDropDownDesiredBlocksKnownSuppressionCases() {
    assertFalse(StageIDELogic.isDropDownDesired(true, true, false, false, false, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, true, false, false, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, false, true, false, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, false, false, true, false));
    assertFalse(StageIDELogic.isDropDownDesired(true, false, false, false, false, true));
    assertTrue(StageIDELogic.isDropDownDesired(true, false, false, false, false, false));
  }

  @Test
  public void findManagedSceneFieldPrefersLastManagedDesiredField() {
    UserField first = new UserField("first", Object.class);
    first.managementLevel.setValue(ManagementLevel.MANAGED);
    UserField middle = new UserField("middle", Object.class);
    middle.managementLevel.setValue(ManagementLevel.NONE);
    UserField last = new UserField("last", Object.class);
    last.managementLevel.setValue(ManagementLevel.MANAGED);

    UserField selected = StageIDELogic.findManagedSceneField(
        Arrays.asList(first, middle, last),
        field -> !"first".equals(field.getName()));

    assertSame(last, selected);
  }

  @Test
  public void findManagedSceneFieldReturnsNullWhenNoManagedFieldMatches() {
    UserField unmanaged = new UserField("plain", Object.class);
    unmanaged.managementLevel.setValue(ManagementLevel.NONE);

    assertNull(StageIDELogic.findManagedSceneField(Arrays.asList(unmanaged), field -> true));
  }
}

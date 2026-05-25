package org.alice.ide;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.UserLocal;

import static org.junit.Assert.*;

public class IDEDeepBehaviorTest {
  @Test
  public void isDropDownDesiredRejectsOnlySuppressedKinds() {
    assertTrue(IDELogic.isDropDownDesired(false, false, false));
    assertFalse(IDELogic.isDropDownDesired(true, false, false));
    assertFalse(IDELogic.isDropDownDesired(false, true, false));
    assertFalse(IDELogic.isDropDownDesired(false, false, true));
  }

  @Test
  public void getAncestorFindsNearestMatchingParent() {
    LocalDeclarationStatement statement = new LocalDeclarationStatement(new UserLocal("value", Object.class, false), null);
    BlockStatement block = new BlockStatement();
    block.statements.add(statement);

    assertSame(block, IDELogic.getAncestor(statement, BlockStatement.class));
    assertNull(IDELogic.getAncestor(block, LocalDeclarationStatement.class));
  }
}

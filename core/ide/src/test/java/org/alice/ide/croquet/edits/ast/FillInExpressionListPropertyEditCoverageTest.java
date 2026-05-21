package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.StringLiteral;

import static org.junit.Assert.*;

public class FillInExpressionListPropertyEditCoverageTest {
  @Test
  public void terseDescription_includesTransitionArrow() {
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, new StringLiteral("before"), new StringLiteral("after"));
    assertTrue(edit.getTerseDescription().contains("===>"));
  }

  @Test
  public void detailedDescription_containsClassName() {
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, new IntegerLiteral(1), new IntegerLiteral(2));
    assertTrue(edit.getDetailedDescription().contains(FillInExpressionListPropertyEdit.class.getName()));
  }

  @Test
  public void redoPresentation_usesRedoPrefix() {
    FillInExpressionListPropertyEdit edit = new FillInExpressionListPropertyEdit(null, new IntegerLiteral(1), new IntegerLiteral(2));
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }
}

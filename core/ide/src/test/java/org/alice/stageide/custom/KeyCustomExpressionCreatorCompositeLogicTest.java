package org.alice.stageide.custom;

import org.junit.Test;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;

import static org.junit.Assert.*;

public class KeyCustomExpressionCreatorCompositeLogicTest {
  @Test
  public void createValueBuildsFieldAccessForSelectedKey() {
    Expression expression = KeyCustomExpressionCreatorCompositeLogic.createValue(org.lgna.story.Key.SPACE);

    assertTrue(expression instanceof FieldAccess);
    assertEquals(org.lgna.story.Key.SPACE, KeyCustomExpressionCreatorCompositeLogic.decodeSelectedKey(expression));
  }

  @Test
  public void createValueAndStatusHandleMissingSelection() {
    assertNull(KeyCustomExpressionCreatorCompositeLogic.createValue(null));
    assertFalse(KeyCustomExpressionCreatorCompositeLogic.hasSelectedKey(null));
    assertTrue(KeyCustomExpressionCreatorCompositeLogic.hasSelectedKey(org.lgna.story.Key.ENTER));
  }

  @Test
  public void decodeSelectedKeyIgnoresUnrelatedExpressions() {
    assertNull(KeyCustomExpressionCreatorCompositeLogic.decodeSelectedKey(new DoubleLiteral(1.0)));
  }
}

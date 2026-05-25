package org.alice.ide.x.components;

import org.junit.Test;

import java.awt.Insets;

import static org.junit.Assert.assertEquals;

public class StatementListPropertyViewHelperTest {
  @Test
  public void createInsetsPreservesExplicitBottomPadding() {
    Insets insets = StatementListPropertyViewHelper.createInsets(12, 3, 7, false, false, false);

    assertEquals(new Insets(StatementListPropertyView.INTRASTICIAL_PAD, 3, 12, 0), insets);
  }

  @Test
  public void createInsetsAddsDefaultBottomPaddingForElseAndLoopBodies() {
    Insets insets = StatementListPropertyViewHelper.createInsets(0, 2, 6, true, false, false);

    assertEquals(new Insets(StatementListPropertyView.INTRASTICIAL_PAD, 2, 8, 6), insets);
  }

  @Test
  public void getBoxLayoutPadUsesFontHeightOnlyForJavaDoTogether() {
    assertEquals(27, StatementListPropertyViewHelper.getBoxLayoutPad(true, true, 19));
    assertEquals(StatementListPropertyView.INTRASTICIAL_PAD, StatementListPropertyViewHelper.getBoxLayoutPad(true, false, 19));
    assertEquals(StatementListPropertyView.INTRASTICIAL_PAD, StatementListPropertyViewHelper.getBoxLayoutPad(false, true, 19));
  }
}

package org.alice.ide.ast.type.merge.croquet.views;

import org.junit.Test;

import javax.swing.JButton;
import java.awt.Rectangle;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MembersViewLogicBehaviorTest {
  @Test
  public void createDifferentToolTipsDescribeTheSpecificConflict() {
    String methodText = MembersViewLogic.createDifferentSignatureToolText("from import", "move", true);
    String fieldText = MembersViewLogic.createDifferentImplementationToolText("from import", "color", false);

    assertTrue(methodText.contains("\"move\""));
    assertTrue(methodText.contains("different signatures"));
    assertTrue(fieldText.contains("\"color\""));
    assertTrue(fieldText.contains("different initializers"));
  }

  @Test
  public void createIdenticalToolTextReportsThatNoActionIsRequired() {
    String text = MembersViewLogic.createIdenticalToolText("from import", "move");

    assertTrue(text.contains("are identical"));
    assertTrue(text.contains("No action is required"));
  }

  @Test
  public void getRowBoundsReturnsEmptyRectangleForEmptyRowsAndUnionForPopulatedRows() {
    JButton first = new JButton();
    first.setBounds(0, 10, 20, 10);
    JButton second = new JButton();
    second.setBounds(25, 12, 15, 6);

    assertEquals(new Rectangle(0, 0, 0, 0), MembersViewLogic.getRowBounds(List.of()));
    assertEquals(new Rectangle(0, 10, 40, 10), MembersViewLogic.getRowBounds(List.of(first, second)));
  }
}

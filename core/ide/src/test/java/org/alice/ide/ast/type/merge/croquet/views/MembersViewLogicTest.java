package org.alice.ide.ast.type.merge.croquet.views;

import org.junit.Test;

import javax.swing.JButton;
import java.awt.Rectangle;
import java.util.List;

import static org.junit.Assert.*;

public class MembersViewLogicTest {
  @Test
  public void createDifferentSignatureToolTextVariesByMemberKind() {
    assertTrue(MembersViewLogic.createDifferentSignatureToolText("from import", "move", true).contains("different signatures"));
    assertTrue(MembersViewLogic.createDifferentSignatureToolText("from import", "color", false).contains("different value classes"));
  }

  @Test
  public void createIdenticalToolTextIncludesNoActionMessage() {
    assertTrue(MembersViewLogic.createIdenticalToolText("from import", "move").contains("No action is required"));
  }

  @Test
  public void getRowBoundsUnionsComponentBounds() {
    JButton first = new JButton();
    first.setBounds(0, 10, 20, 10);
    JButton second = new JButton();
    second.setBounds(25, 12, 15, 6);

    Rectangle bounds = MembersViewLogic.getRowBounds(List.of(first, second));

    assertEquals(new Rectangle(0, 10, 40, 10), bounds);
  }
}

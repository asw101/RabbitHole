package org.alice.ide.codeeditor;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.ConstructorInvocationStatement;
import org.lgna.project.ast.SuperConstructorInvocationStatement;

import java.awt.Rectangle;

import static org.junit.Assert.*;

public class CodeEditorLogicTest {
  private static StatementListPropertyPaneInfo pane(int x, int y, int width, int height) {
    return new StatementListPropertyPaneInfo(null, new Rectangle(x, y, width, height));
  }

  @Test
  public void getLeadingConstructorInvocationReturnsConstructorStatementForConstructorBodies() {
    ConstructorInvocationStatement invocation = new SuperConstructorInvocationStatement();
    ConstructorBlockStatement body = new ConstructorBlockStatement(invocation);

    assertSame(invocation, CodeEditorLogic.getLeadingConstructorInvocation(body));
  }

  @Test
  public void getLeadingConstructorInvocationReturnsNullForRegularBlocks() {
    assertNull(CodeEditorLogic.getLeadingConstructorInvocation(new BlockStatement()));
  }

  @Test
  public void capMinimumUsesLargestSiblingBottomBelowCurrentY() {
    StatementListPropertyPaneInfo[] infos = {
        pane(0, 0, 100, 10),
        pane(0, 15, 100, 5),
        pane(0, 50, 100, 10)
    };

    assertEquals(20, CodeEditorLogic.capMinimum(0, 30, infos, 2));
  }

  @Test
  public void capMaximumUsesSmallestSiblingTopAboveCurrentBottom() {
    StatementListPropertyPaneInfo[] infos = {
        pane(0, 0, 100, 10),
        pane(0, 40, 100, 10),
        pane(0, 60, 100, 10)
    };

    assertEquals(40, CodeEditorLogic.capMaximum(80, 25, infos, 0));
  }
}

package org.alice.ide.x;

import org.alice.ide.IDE;
import org.alice.ide.ast.draganddrop.statement.StatementDragModel;
import org.alice.ide.common.AbstractStatementPane;
import org.alice.ide.croquet.models.ast.StatementContextMenu;
import org.junit.Test;
import org.lgna.project.ast.Comment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class DialogAndPreviewAstI18nFactoryBehaviorTest {
  @Test
  public void dialogFactoryUsesIdeInheritGroup() throws Exception {
    Field groupField = MutableAstI18nFactory.class.getDeclaredField("group");
    groupField.setAccessible(true);

    assertSame(IDE.INHERIT_GROUP, groupField.get(DialogAstI18nFactory.getInstance()));
  }

  @Test
  public void dialogFactoryStatementPaneKeepsDragModelAndAddsContextMenu() {
    Comment statement = new Comment("note");
    StatementDragModel dragModel = StatementDragModel.getInstance(statement);

    AbstractStatementPane pane = DialogAstI18nFactory.getInstance().createStatementPane(dragModel, statement, null);

    assertSame(dragModel, pane.getModel());
    assertSame(statement, pane.getStatement());
    assertSame(StatementContextMenu.getInstance(statement).getPopupPrepModel(), pane.getPopupPrepModel());
  }

  @Test
  public void previewFactoryFallbackTypeIsNull() throws Exception {
    Method method = PreviewAstI18nFactory.class.getDeclaredMethod("getFallBackTypeForThisExpression");
    method.setAccessible(true);

    assertNull(method.invoke(PreviewAstI18nFactory.getInstance()));
  }

  @Test
  public void previewFactoryStatementPaneDiscardsDragModelAndContextMenu() {
    Comment statement = new Comment("preview");
    StatementDragModel dragModel = StatementDragModel.getInstance(statement);

    AbstractStatementPane pane = PreviewAstI18nFactory.getInstance().createStatementPane(dragModel, statement, null);

    assertNull(pane.getModel());
    assertSame(statement, pane.getStatement());
    assertNull(pane.getPopupPrepModel());
  }
}

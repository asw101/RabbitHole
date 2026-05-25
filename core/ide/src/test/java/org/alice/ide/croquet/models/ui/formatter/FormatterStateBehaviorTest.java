package org.alice.ide.croquet.models.ui.formatter;

import org.alice.ide.formatter.AliceFormatter;
import org.alice.ide.formatter.JavaFormatter;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class FormatterStateBehaviorTest {

  @Test
  public void availableFormatters_remainAliceThenJava() {
    FormatterState state = FormatterState.getInstance();

    assertEquals(2, state.getItemCount());
    assertSame(AliceFormatter.getInstance(), state.getItemAt(0));
    assertSame(JavaFormatter.getInstance(), state.getItemAt(1));
  }

  @Test
  public void javaFlag_matchesCurrentSelection_andFormattersProduceDistinctHeaders() {
    FormatterState state = FormatterState.getInstance();
    UserMethod function = new UserMethod("score", String.class, new UserParameter[0], new BlockStatement());

    assertEquals(state.getValue() == JavaFormatter.getInstance(), FormatterState.isJava());
    assertEquals("</getReturnType()/> </getName()/> ( </getParameters()/> ) {",
        JavaFormatter.getInstance().getHeaderTextForCode(function));
    assertEquals("declare </getReturnType()/> function </getName()/> </getParameters()/>",
        AliceFormatter.getInstance().getHeaderTextForCode(function));
  }
}

package org.alice.ide.croquet.models.html;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.code.ProcessableNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class HtmlEncoderDeepBehaviorTest {
  @Test
  public void isClassEmptyReturnsFalseForIncludedUserAuthoredMethods() {
    UserMethod method = new UserMethod("step", void.class, new UserParameter[0], new BlockStatement());
    Map<String, List<ProcessableNode>> sections = Map.of("procedures", List.of(method));

    assertFalse(HtmlEncoderLogic.isClassEmpty(sections, Set.of("generated")));
  }

  @Test
  public void getListenerInvocationIgnoresNonInvocationExpressionStatements() {
    assertNull(HtmlEncoderLogic.getListenerInvocation(new ExpressionStatement(new ThisExpression())));
  }
}

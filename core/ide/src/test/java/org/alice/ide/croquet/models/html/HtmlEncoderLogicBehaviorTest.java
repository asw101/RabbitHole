package org.alice.ide.croquet.models.html;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.code.ProcessableNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HtmlEncoderLogicBehaviorTest {
  private static UserMethod method(String name, ManagementLevel managementLevel) {
    UserMethod method = new UserMethod(name, void.class, new UserParameter[0], new BlockStatement());
    method.managementLevel.setValue(managementLevel);
    return method;
  }

  @Test
  public void isClassEmptyIgnoresGeneratedMethodsButRejectsUserAuthoredMethods() {
    Map<String, List<ProcessableNode>> generatedOnly = Map.of(
        "procedures", List.of(method("generated", ManagementLevel.GENERATED)));
    Map<String, List<ProcessableNode>> withUserMethod = Map.of(
        "procedures", List.of(method("move", ManagementLevel.NONE)));

    assertTrue(HtmlEncoderLogic.isClassEmpty(generatedOnly, Set.of()));
    assertFalse(HtmlEncoderLogic.isClassEmpty(withUserMethod, Set.of()));
  }

  @Test
  public void isClassEmptySkipsSectionsMarkedToSkip() {
    Map<String, List<ProcessableNode>> sections = Map.of(
        "generated", List.of(method("move", ManagementLevel.NONE)),
        "procedures", List.<ProcessableNode>of());

    assertTrue(HtmlEncoderLogic.isClassEmpty(sections, Set.of("generated")));
  }

  @Test
  public void getListenerInvocationExtractsMethodInvocationFromExpressionStatementsOnly() {
    MethodInvocation invocation = new MethodInvocation();

    assertSame(invocation, HtmlEncoderLogic.getListenerInvocation(new ExpressionStatement(invocation)));
    assertNull(HtmlEncoderLogic.getListenerInvocation(new BlockStatement()));
  }
}

package org.alice.ide.croquet.models.html;

import org.junit.Test;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.code.ProcessableNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class HtmlEncoderLogicTest {
  @Test
  public void isSectionToIncludeHonorsSkippedKeys() {
    assertFalse(HtmlEncoderLogic.isSectionToInclude("generated", Set.of("generated")));
    assertTrue(HtmlEncoderLogic.isSectionToInclude("procedures", Set.of("generated")));
  }

  @Test
  public void isClassEmptyIgnoresGeneratedMethodsInSkippedSections() {
    UserMethod generated = new UserMethod("generated", void.class, new UserParameter[0], new BlockStatement());
    generated.managementLevel.setValue(org.lgna.project.ast.ManagementLevel.GENERATED);
    Map<String, List<ProcessableNode>> sections = Map.of("procedures", List.of(generated), "generated", List.of());

    assertTrue(HtmlEncoderLogic.isClassEmpty(sections, Set.of("generated")));
  }

  @Test
  public void getListenerInvocationReturnsMethodInvocationOnly() {
    MethodInvocation invocation = new MethodInvocation();
    assertSame(invocation, HtmlEncoderLogic.getListenerInvocation(new ExpressionStatement(invocation)));
    assertNull(HtmlEncoderLogic.getListenerInvocation(new org.lgna.project.ast.BlockStatement()));
  }
}

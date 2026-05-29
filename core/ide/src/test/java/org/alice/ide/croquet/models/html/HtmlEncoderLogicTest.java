package org.alice.ide.croquet.models.html;

import org.junit.Test;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.code.ProcessableNode;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class HtmlEncoderLogicTest {

  private static final Set<String> SKIP = Set.of("ConstructorSection", "GettersAndSettersSection");

  @Test
  public void isSectionToIncludeHonorsSkippedKeys() {
    assertFalse(HtmlEncoderLogic.isSectionToInclude("ConstructorSection", SKIP));
    assertFalse(HtmlEncoderLogic.isSectionToInclude("GettersAndSettersSection", SKIP));
    assertTrue(HtmlEncoderLogic.isSectionToInclude("MethodSection", SKIP));
    assertTrue(HtmlEncoderLogic.isSectionToInclude("FieldSection", SKIP));
  }

  @Test
  public void isClassEmpty_allEmpty() {
    Map<String, List<ProcessableNode>> sections = new LinkedHashMap<>();
    sections.put("MethodSection", Collections.emptyList());
    assertTrue(HtmlEncoderLogic.isClassEmpty(sections, SKIP));
  }

  @Test
  public void isClassEmpty_onlySkippedSectionsPopulated() {
    UserMethod m = new UserMethod();
    Map<String, List<ProcessableNode>> sections = new LinkedHashMap<>();
    sections.put("ConstructorSection", List.of(m));
    sections.put("MethodSection", Collections.emptyList());
    assertTrue(HtmlEncoderLogic.isClassEmpty(sections, SKIP));
  }

  @Test
  public void isClassEmpty_generatedMethodsOnly() {
    UserMethod generated = new UserMethod("gen", void.class, new UserParameter[0], new BlockStatement());
    generated.managementLevel.setValue(ManagementLevel.GENERATED);
    Map<String, List<ProcessableNode>> sections = new LinkedHashMap<>();
    sections.put("MethodSection", List.of(generated));
    assertTrue(HtmlEncoderLogic.isClassEmpty(sections, SKIP));
  }

  @Test
  public void isClassEmpty_nonGeneratedMethod() {
    UserMethod manual = new UserMethod("doStuff", void.class, new UserParameter[0], new BlockStatement());
    manual.managementLevel.setValue(ManagementLevel.NONE);
    Map<String, List<ProcessableNode>> sections = new LinkedHashMap<>();
    sections.put("MethodSection", List.of(manual));
    assertFalse(HtmlEncoderLogic.isClassEmpty(sections, SKIP));
  }

  @Test
  public void shouldSkipMethod_generated() {
    UserMethod m = new UserMethod();
    m.managementLevel.setValue(ManagementLevel.GENERATED);
    assertTrue(HtmlEncoderLogic.shouldSkipMethod(m));
  }

  @Test
  public void shouldSkipMethod_staticMain() {
    UserMethod m = new UserMethod();
    m.managementLevel.setValue(ManagementLevel.NONE);
    m.name.setValue("main");
    m.isStatic.setValue(true);
    assertTrue(HtmlEncoderLogic.shouldSkipMethod(m));
  }

  @Test
  public void shouldSkipMethod_normalMethod() {
    UserMethod m = new UserMethod();
    m.managementLevel.setValue(ManagementLevel.NONE);
    m.name.setValue("doWork");
    assertFalse(HtmlEncoderLogic.shouldSkipMethod(m));
  }

  @Test
  public void isListenerArgument_null() {
    assertFalse(HtmlEncoderLogic.isListenerArgument(null));
  }

  @Test
  public void getRequiredListenerArgument_null() {
    assertNull(HtmlEncoderLogic.getRequiredListenerArgument(null));
  }

  @Test
  public void getUserLambda_null() {
    assertNull(HtmlEncoderLogic.getUserLambda(null));
  }

  @Test
  public void getListenerInvocation_expressionStatement() {
    MethodInvocation invocation = new MethodInvocation();
    assertSame(invocation, HtmlEncoderLogic.getListenerInvocation(new ExpressionStatement(invocation)));
  }

  @Test
  public void getListenerInvocation_blockStatement() {
    assertNull(HtmlEncoderLogic.getListenerInvocation(new BlockStatement()));
  }

  @Test
  public void getListenerInvocation_null() {
    assertNull(HtmlEncoderLogic.getListenerInvocation(null));
  }
}

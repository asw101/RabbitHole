package org.alice.ide.x;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class AstI18nFactoryLogicTest {
  @Test
  public void constructorThrowsAssertionError() throws Exception {
    java.lang.reflect.Constructor<AstI18nFactoryLogic> constructor = AstI18nFactoryLogic.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail("Expected AssertionError");
    } catch (java.lang.reflect.InvocationTargetException exception) {
      assertTrue(exception.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void getComponentKindRecognizesSpecialCases() {
    UserMethod method = new UserMethod("wave", void.class, new UserParameter[0], new BlockStatement());
    SimpleArgument argument = new SimpleArgument(new UserParameter("amount", Number.class), new DoubleLiteral(1.0));

    assertEquals(AstI18nFactoryLogic.ComponentKind.DECLARATION_NAME, AstI18nFactoryLogic.getComponentKind(method, "getName"));
    assertEquals(AstI18nFactoryLogic.ComponentKind.PARAMETER_NAME, AstI18nFactoryLogic.getComponentKind(argument, "getParameterNameText"));
    assertEquals(AstI18nFactoryLogic.ComponentKind.TYPE, AstI18nFactoryLogic.getComponentKind(JavaConstructor.getInstance(String.class), "getDeclaringType"));
    assertEquals(AstI18nFactoryLogic.ComponentKind.TYPE, AstI18nFactoryLogic.getComponentKind(method, "getReturnType"));
    assertEquals(AstI18nFactoryLogic.ComponentKind.PARAMETERS, AstI18nFactoryLogic.getComponentKind(method, "getParameters"));
    assertEquals(AstI18nFactoryLogic.ComponentKind.LABEL, AstI18nFactoryLogic.getComponentKind(method, "toString"));
  }

  @Test
  public void getLabelTextUsesTypeNamesAndNullSafety() {
    assertEquals("String", AstI18nFactoryLogic.getLabelText(JavaType.getInstance(String.class)));
    assertEquals("42", AstI18nFactoryLogic.getLabelText(42));
    assertNull(AstI18nFactoryLogic.getLabelText(null));
  }

  @Test
  public void getLabelTextFallsBackToToStringForArbitraryObjects() {
    AtomicInteger value = new AtomicInteger(7);

    assertEquals("7", AstI18nFactoryLogic.getLabelText(value));
  }

  @Test
  public void getComponentKindOnlyTreatsSpecialMethodNamesAsSpecialCases() {
    UserMethod method = new UserMethod("wave", void.class, new UserParameter[0], new BlockStatement());
    SimpleArgument argument = new SimpleArgument(new UserParameter("amount", Number.class), new DoubleLiteral(1.0));

    assertEquals(AstI18nFactoryLogic.ComponentKind.LABEL, AstI18nFactoryLogic.getComponentKind(method, "hashCode"));
    assertEquals(AstI18nFactoryLogic.ComponentKind.LABEL, AstI18nFactoryLogic.getComponentKind(argument, "getName"));
  }

  @Test
  public void getLocalPropertyKindRecognizesTrackedLocals() {
    assertEquals(AstI18nFactoryLogic.LocalPropertyKind.LOCAL_DECLARATION, AstI18nFactoryLogic.getLocalPropertyKind("local", 2));
    assertEquals(AstI18nFactoryLogic.LocalPropertyKind.LOCAL, AstI18nFactoryLogic.getLocalPropertyKind("item", 1));
    assertEquals(AstI18nFactoryLogic.LocalPropertyKind.NONE, AstI18nFactoryLogic.getLocalPropertyKind("field", 1));
    assertEquals(AstI18nFactoryLogic.LocalPropertyKind.NONE, AstI18nFactoryLogic.getLocalPropertyKind("local", 0));
  }

  @Test
  public void getLocalPropertyKindRecognizesAllSupportedLocalAliases() {
    assertEquals(AstI18nFactoryLogic.LocalPropertyKind.LOCAL, AstI18nFactoryLogic.getLocalPropertyKind("variable", 1));
    assertEquals(AstI18nFactoryLogic.LocalPropertyKind.LOCAL_DECLARATION, AstI18nFactoryLogic.getLocalPropertyKind("constant", 2));
    assertEquals(AstI18nFactoryLogic.LocalPropertyKind.NONE, AstI18nFactoryLogic.getLocalPropertyKind("constant", 3));
  }

  @Test
  public void createPoseBuilderPrefixTextUsesTypeName() {
    assertEquals("new String(...).", AstI18nFactoryLogic.createPoseBuilderPrefixText(JavaType.getInstance(String.class)));
  }
}

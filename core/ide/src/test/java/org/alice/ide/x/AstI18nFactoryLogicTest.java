package org.alice.ide.x;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class AstI18nFactoryLogicTest {
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
}

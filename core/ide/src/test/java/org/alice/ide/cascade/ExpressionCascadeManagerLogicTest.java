package org.alice.ide.cascade;

import edu.cmu.cs.dennisc.java.util.Lists;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserParameter;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class ExpressionCascadeManagerLogicTest {
  @Test
  public void collectAccessibleLocalsIncludesEarlierDeclarations() {
    UserLocal first = new UserLocal("first", Object.class, false);
    UserLocal second = new UserLocal("second", Object.class, false);
    BlockStatement block = new BlockStatement();
    block.statements.add(new LocalDeclarationStatement(first, null));
    block.statements.add(new LocalDeclarationStatement(second, null));

    LinkedList<UserLocal> locals = ExpressionCascadeManagerLogic.collectAccessibleLocalsForBlockAndIndex(Lists.newLinkedList(), block, 2);

    assertEquals(2, locals.size());
    assertSame(second, locals.get(0));
    assertSame(first, locals.get(1));
  }

  @Test
  public void resolveFillInDescriptorRecognizesSpecializedExpressions() {
    UserField field = new UserField("ship", Object.class);
    UserParameter parameter = new UserParameter("message", Object.class);
    UserLocal local = new UserLocal("count", Object.class, false);

    assertEquals(ExpressionCascadeManagerLogic.Kind.THIS, ExpressionCascadeManagerLogic.resolveFillInDescriptor(new ThisExpression()).getKind());
    assertEquals(ExpressionCascadeManagerLogic.Kind.THIS_FIELD, ExpressionCascadeManagerLogic.resolveFillInDescriptor(new FieldAccess(new ThisExpression(), field)).getKind());
    assertEquals(ExpressionCascadeManagerLogic.Kind.PARAMETER, ExpressionCascadeManagerLogic.resolveFillInDescriptor(new ParameterAccess(parameter)).getKind());
    assertEquals(ExpressionCascadeManagerLogic.Kind.LOCAL, ExpressionCascadeManagerLogic.resolveFillInDescriptor(new LocalAccess(local)).getKind());
  }

  @Test
  public void resolveFillInDescriptorFallsBackForNonThisFieldAccess() {
    UserField field = new UserField("ship", Object.class);
    FieldAccess access = new FieldAccess(new org.lgna.project.ast.MethodInvocation(new ThisExpression(), JavaMethod.getInstance(Object.class, "toString")), field);

    assertEquals(ExpressionCascadeManagerLogic.Kind.SIMPLE, ExpressionCascadeManagerLogic.resolveFillInDescriptor(access).getKind());
  }
}

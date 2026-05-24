package org.alice.ide.cascade;

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserLocal;

import java.util.LinkedList;

final class ExpressionCascadeManagerLogic {
  enum Kind {
    THIS,
    THIS_FIELD,
    PARAMETER,
    LOCAL,
    SIMPLE
  }

  static final class FillInDescriptor {
    private final Kind kind;
    private final FieldAccess fieldAccess;
    private final ParameterAccess parameterAccess;
    private final LocalAccess localAccess;

    private FillInDescriptor(Kind kind, FieldAccess fieldAccess, ParameterAccess parameterAccess, LocalAccess localAccess) {
      this.kind = kind;
      this.fieldAccess = fieldAccess;
      this.parameterAccess = parameterAccess;
      this.localAccess = localAccess;
    }

    static FillInDescriptor forThis() {
      return new FillInDescriptor(Kind.THIS, null, null, null);
    }

    static FillInDescriptor forThisField(FieldAccess fieldAccess) {
      return new FillInDescriptor(Kind.THIS_FIELD, fieldAccess, null, null);
    }

    static FillInDescriptor forParameter(ParameterAccess parameterAccess) {
      return new FillInDescriptor(Kind.PARAMETER, null, parameterAccess, null);
    }

    static FillInDescriptor forLocal(LocalAccess localAccess) {
      return new FillInDescriptor(Kind.LOCAL, null, null, localAccess);
    }

    static FillInDescriptor simple() {
      return new FillInDescriptor(Kind.SIMPLE, null, null, null);
    }

    Kind getKind() {
      return this.kind;
    }

    FieldAccess getFieldAccess() {
      return this.fieldAccess;
    }

    ParameterAccess getParameterAccess() {
      return this.parameterAccess;
    }

    LocalAccess getLocalAccess() {
      return this.localAccess;
    }
  }

  private ExpressionCascadeManagerLogic() {
    throw new AssertionError();
  }

  static LinkedList<UserLocal> collectAccessibleLocalsForBlockAndIndex(LinkedList<UserLocal> rv, BlockStatement blockStatement, int index) {
    while (index >= 1) {
      index--;
      if (index >= blockStatement.statements.size()) {
        try {
          throw new IndexOutOfBoundsException(index + " " + blockStatement.statements.size());
        } catch (IndexOutOfBoundsException ioobe) {
          Logger.throwable(ioobe);
        }
        index = blockStatement.statements.size();
      } else {
        Statement statementI = blockStatement.statements.get(index);
        if (statementI instanceof LocalDeclarationStatement localDeclarationStatement) {
          rv.add(localDeclarationStatement.local.getValue());
        }
      }
    }
    return rv;
  }

  static FillInDescriptor resolveFillInDescriptor(Expression expression) {
    if (expression instanceof ThisExpression) {
      return FillInDescriptor.forThis();
    }
    if (expression instanceof FieldAccess fieldAccess && fieldAccess.expression.getValue() instanceof ThisExpression) {
      return FillInDescriptor.forThisField(fieldAccess);
    }
    if (expression instanceof ParameterAccess parameterAccess) {
      return FillInDescriptor.forParameter(parameterAccess);
    }
    if (expression instanceof LocalAccess localAccess) {
      return FillInDescriptor.forLocal(localAccess);
    }
    return FillInDescriptor.simple();
  }
}

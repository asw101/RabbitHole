package org.alice.tweedle.unlinked;

import org.alice.tweedle.*;
import org.alice.tweedle.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

class ParseHelpers {

  static List<TweedleStatement> collectBlockStatements(List<TweedleParser.BlockStatementContext> contexts) {
    StatementVisitor statementVisitor = new StatementVisitor();
    return contexts.stream().map(stmt -> stmt.accept(statementVisitor)).collect(toList());
  }

  static Map<String, TweedleExpression> visitLabeledArguments(TweedleParser.LabeledExpressionListContext context) {
    if (context == null) {
      return Collections.emptyMap();
    }

    List<TweedleParser.LabeledExpressionContext> argumentContexts = context.labeledExpression();
    Map<String, TweedleExpression> arguments = new HashMap<>(argumentContexts.size());
    final ExpressionVisitor visitor = new ExpressionVisitor();
    for (TweedleParser.LabeledExpressionContext arg : argumentContexts) {
      TweedleExpression argValue = arg.expression().accept(visitor);
      arguments.put(arg.IDENTIFIER().getText(), argValue);
    }
    return arguments;
  }

  static List<TweedleExpression> visitUnlabeledArguments(TweedleParser.UnlabeledExpressionListContext listContext, ExpressionVisitor expressionVisitor) {
    return listContext == null ? new ArrayList<>() : listContext.expression().stream().map(a -> a.accept(expressionVisitor)).collect(toList());
  }

  static TweedleType getTypeOrVoid(TweedleParser.TypeTypeOrVoidContext context) {
    if (context.VOID() != null) {
      return TweedleVoidType.VOID;
    }
    return getType(context.typeType());
  }

  static TweedleType getType(TweedleParser.TypeTypeContext context) {
    TweedleType baseType = context.classType() != null ? getTypeReference(context.classType().getText()) : getPrimitiveType(context.primitiveType().getText());
    if (context.getChildCount() > 1 && baseType != null) {
      return new TweedleArrayType(baseType);
    }
    return baseType;
  }

  static TweedleTypeReference getTypeReference(String typeName) {
    return new TweedleTypeReference(typeName);
  }

  static TweedlePrimitiveType getPrimitiveType(String typeName) {
    for (TweedlePrimitiveType prim : TweedleTypes.PRIMITIVE_TYPES) {
      if (prim.getName().equals(typeName)) {
        return prim;
      }
    }
    return null;
  }
}

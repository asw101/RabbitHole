package org.alice.tweedle.unlinked;

import org.alice.tweedle.*;
import org.alice.tweedle.ast.*;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

class ExpressionVisitor extends TweedleParserBaseVisitor<TweedleExpression> {

  private final TweedleUnlinkedParser parser;
  private TweedleType expectedType;
  private final boolean allowPrimitiveNull;

  ExpressionVisitor(TweedleUnlinkedParser parser) {
    this(parser, null);
  }

  ExpressionVisitor(TweedleUnlinkedParser parser, TweedleType expectedType) {
    this(parser, expectedType, false);
  }

  ExpressionVisitor(TweedleUnlinkedParser parser, TweedleType expectedType, boolean allowPrimitiveNull) {
    this.parser = parser;
    this.expectedType = expectedType;
    this.allowPrimitiveNull = allowPrimitiveNull;
  }

  @Override
  public TweedleExpression visitPrimary(TweedleParser.PrimaryContext context) {
    if (context.expression() != null) {
      return this.visitExpression(context.expression());
    }
    if (context.THIS() != null) {
      return new ThisExpression();
    }
    if (context.IDENTIFIER() != null) {
      return new IdentifierReference(context.IDENTIFIER().getText());
    }

    return super.visitPrimary(context);
  }

  @Override
  public TweedleExpression visitLiteral(TweedleParser.LiteralContext context) {
    org.antlr.v4.runtime.tree.TerminalNode wholeNumber = context.DECIMAL_LITERAL();
    if (wholeNumber != null) {
      int value = Integer.parseInt(wholeNumber.getSymbol().getText());
      return TweedleTypes.WHOLE_NUMBER.createValue(value);
    }

    org.antlr.v4.runtime.tree.TerminalNode flt = context.FLOAT_LITERAL();
    if (flt != null) {
      double value = Double.parseDouble(flt.getSymbol().getText());
      return TweedleTypes.DECIMAL_NUMBER.createValue(value);
    }

    if (context.NULL_LITERAL() != null) {
      return TweedleNull.NULL;
    }

    org.antlr.v4.runtime.tree.TerminalNode bool = context.BOOL_LITERAL();
    if (bool != null) {
      boolean value = Boolean.parseBoolean(bool.getSymbol().getText());
      return TweedleTypes.BOOLEAN.createValue(value);
    }

    org.antlr.v4.runtime.tree.TerminalNode str = context.STRING_LITERAL();
    if (str != null) {
      final String quotedString = str.getSymbol().getText();
      return TweedleTypes.TEXT_STRING.createValue(quotedString.substring(1, quotedString.length() - 1));
    }

    return super.visitLiteral(context);
  }

  @Override
  public TweedleExpression visitExpression(TweedleParser.ExpressionContext context) {
    TweedleExpression expression = buildExpression(context);
    if (!isExpectedTypeCompatible(expression)) {
      throw new RuntimeException("Had been expecting expression of type " + expectedType + ", but it is typed as " + expression.getType());
    }
    return expression;
  }

  private boolean isExpectedTypeCompatible(TweedleExpression expression) {
    if (expectedType == null || expression == null || expression.getType() == null) {
      return true;
    }
    if (expression instanceof TweedleNull) {
      return expectedType == TweedleTypes.TEXT_STRING
          || !(expectedType instanceof TweedlePrimitiveType<?>)
          || allowPrimitiveNull;
    }
    return expectedType.willAcceptValueOfType(expression.getType()) || expression.getType().willAcceptValueOfType(expectedType);
  }

  private TweedleExpression buildExpression(TweedleParser.ExpressionContext context) {
    org.antlr.v4.runtime.Token prefix = context.prefix;
    org.antlr.v4.runtime.Token operation = context.bop;

    if (prefix != null) {
      switch (prefix.getText()) {
      case "+":
        return getFirstExpression(TweedleTypes.NUMBER, context);
      case "-":
        return getNegativeOfExpression(getFirstExpression(TweedleTypes.NUMBER, context));
      case "!":
        return new LogicalNotExpression(getFirstExpression(TweedleTypes.BOOLEAN, context));
      default:
        throw new RuntimeException("Unrecognized prefix operation: " + prefix.getText());
      }
    } else if (operation != null) {
      switch (operation.getText()) {
      case ".":
        return fieldOrMethodRef(context);
      case "==":
        return binaryExpression(EqualToExpression::new, null, context);
      case "!=":
        return binaryExpression(NotEqualToExpression::new, null, context);
      case "..":
        return binaryExpression(StringConcatenationExpression::new, null, context);
      case "*":
        return binaryExpression(MultiplicationExpression::new, TweedleTypes.NUMBER, context);
      case "/":
        return binaryExpression(DivisionExpression::new, TweedleTypes.NUMBER, context);
      case "%":
        return binaryExpression(ModuloExpression::new, TweedleTypes.WHOLE_NUMBER, context);
      case "+":
        return binaryExpression(AdditionExpression::new, TweedleTypes.NUMBER, context);
      case "-":
        return binaryExpression(SubtractionExpression::new, TweedleTypes.NUMBER, context);
      case "<=":
        return binaryExpression(LessThanOrEqualExpression::new, TweedleTypes.NUMBER, context);
      case ">=":
        return binaryExpression(GreaterThanOrEqualExpression::new, TweedleTypes.NUMBER, context);
      case ">":
        return binaryExpression(GreaterThanExpression::new, TweedleTypes.NUMBER, context);
      case "<":
        return binaryExpression(LessThanExpression::new, TweedleTypes.NUMBER, context);
      case "&&":
        return binaryExpression(LogicalAndExpression::new, TweedleTypes.BOOLEAN, context);
      case "||":
        return binaryExpression(LogicalOrExpression::new, TweedleTypes.BOOLEAN, context);
      case "<-":
        List<TweedleExpression> expressions = getTypedExpressions(context, null);
        return new AssignmentExpression(expressions.getFirst(), expressions.get(1));
      default:
        throw new RuntimeException("No such operation as " + operation.getText());
      }
    } else if (context.bracket != null) {
      TweedleExpression array = context.expression(0).accept(new ExpressionVisitor(parser, new TweedleArrayType()));
      TweedleExpression index = context.expression(1).accept(new ExpressionVisitor(parser, TweedleTypes.WHOLE_NUMBER));
      return new ArrayIndexExpression(((TweedleArrayType) array.getType()).getValueType(), array, index);
    } else if (context.lambdaCall() != null) {
      TweedleExpression lambdaSourceExp = getFirstExpression(null, context);
      if (context.lambdaCall().unlabeledExpressionList() == null) {
        return new LambdaEvaluation(lambdaSourceExp);
      } else {
        final List<TweedleExpression> elements = parser.visitUnlabeledArguments(context.lambdaCall().unlabeledExpressionList(), new ExpressionVisitor(parser));
        return new LambdaEvaluation(lambdaSourceExp, elements);
      }
    }

    return visitChildren(context);
  }

  @Override
  public TweedleExpression visitLambdaExpression(TweedleParser.LambdaExpressionContext ctx) {
    List<TweedleRequiredParameter> parameters = lambdaParameters(ctx.lambdaParameters());
    List<TweedleStatement> stmts = parser.collectBlockStatements(ctx.block().blockStatement());
    return new LambdaExpression(parameters, stmts);
  }

  private List<TweedleRequiredParameter> lambdaParameters(TweedleParser.LambdaParametersContext context) {
    return context.requiredParameter().stream().map(field -> new TweedleRequiredParameter(parser.getType(field.typeType()), field.variableDeclaratorId().IDENTIFIER().getText())).collect(toList());
  }

  @Override
  public TweedleExpression visitMethodCall(TweedleParser.MethodCallContext ctx) {
    return new MethodCallExpression(new ThisExpression(), ctx.IDENTIFIER().getText(), parser.visitLabeledArguments(ctx.labeledExpressionList()), false);
  }

  public @Override
  TweedleExpression visitSuperSuffix(TweedleParser.SuperSuffixContext context) {
    if (context.IDENTIFIER() != null) {
      if (context.arguments() != null) {
        return new MethodCallExpression(new SuperExpression(), context.IDENTIFIER().getText(), parser.visitLabeledArguments(context.arguments().labeledExpressionList()));
      } else {
        return new FieldAccess(new SuperExpression(), context.IDENTIFIER().getText());
      }
    } else if (context.arguments() != null) {
      return new Instantiation(new SuperExpression(), parser.visitLabeledArguments(context.arguments().labeledExpressionList()));
    }
    throw new RuntimeException("Super suffix could not be constructed.");
  }

  public @Override
  TweedleExpression visitCreator(TweedleParser.CreatorContext context) {
    String typeName = context.createdName().getText();
    final TweedleParser.ArrayCreatorRestContext arrayDetails = context.arrayCreatorRest();
    if (arrayDetails != null) {
      TweedlePrimitiveType prim = parser.getPrimitiveType(typeName);
      TweedleType memberType = prim == null ? parser.getTypeReference(typeName) : prim;
      TweedleArrayType arrayType = new TweedleArrayType(memberType);
      if (arrayDetails.arrayInitializer() != null) {
        final List<TweedleExpression> elements = parser.visitUnlabeledArguments(arrayDetails.arrayInitializer().unlabeledExpressionList(), new ExpressionVisitor(parser, memberType));
        return new TweedleArrayInitializer(arrayType, elements);
      } else {
        return new TweedleArrayInitializer(arrayType, arrayDetails.expression().accept(new ExpressionVisitor(parser)));
      }
    } else {
      TweedleTypeReference typeRef = parser.getTypeReference(typeName);
      TweedleParser.LabeledExpressionListContext argsContext = context.classCreatorRest().arguments().labeledExpressionList();
      Map<String, TweedleExpression> arguments = parser.visitLabeledArguments(argsContext);
      return new Instantiation(typeRef, arguments);
    }
  }

  private TweedleExpression fieldOrMethodRef(TweedleParser.ExpressionContext context) {
    TweedleExpression target = context.expression(0).accept(new ExpressionVisitor(parser));
    if (context.IDENTIFIER() != null) {
      return new FieldAccess(target, context.IDENTIFIER().getText());
    }
    if (context.methodCall() != null) {
      return new MethodCallExpression(
          target,
          context.methodCall().IDENTIFIER().getText(),
          parser.visitLabeledArguments(context.methodCall().labeledExpressionList()));
    }
    throw new RuntimeException("Unexpected details on context " + context);
  }

  private TweedleExpression getNegativeOfExpression(TweedleExpression exp) {
    final NegativeExpression negativeExpression = new NegativeExpression(exp);
    if (exp instanceof TweedlePrimitiveValue) {
      return negativeExpression.evaluate(null);
    }
    return negativeExpression;
  }

  private TweedleExpression getFirstExpression(TweedlePrimitiveType type, TweedleParser.ExpressionContext context) {
    return getTypedExpressions(context, type).getFirst();
  }

  private TweedleExpression binaryExpression(BinaryConstructor constructor, TweedlePrimitiveType type, TweedleParser.ExpressionContext context) {
    List<TweedleExpression> expressions = getTypedExpressions(context, type);
    return constructor.newBinExp(expressions.getFirst(), expressions.get(1));
  }

  private List<TweedleExpression> getTypedExpressions(TweedleParser.ExpressionContext context, TweedleType type) {
    final ExpressionVisitor visitor = new ExpressionVisitor(parser, type);
    return context.expression().stream().map(exp -> exp.accept(visitor)).collect(toList());
  }
}

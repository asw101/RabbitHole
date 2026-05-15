package org.alice.tweedle.unlinked;

import org.alice.tweedle.*;
import org.alice.tweedle.ast.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.alice.tweedle.unlinked.ParseHelpers.*;

class ExpressionVisitor extends TweedleParserBaseVisitor<TweedleExpression> {
  private TweedleType expectedType;
  private final boolean allowPrimitiveNull;

  ExpressionVisitor() {
    this(null);
  }

  ExpressionVisitor(TweedleType expectedType) {
    this(expectedType, false);
  }

  ExpressionVisitor(TweedleType expectedType, boolean allowPrimitiveNull) {
    this.expectedType = expectedType;
    this.allowPrimitiveNull = allowPrimitiveNull;
  }

  @Override
  public TweedleExpression visitPrimary(TweedleParser.PrimaryContext context) {
    if (context.expression() != null) {
      // Parenthesized child expression
      return this.visitExpression(context.expression());
    }
    if (context.THIS() != null) {
      return new ThisExpression();
    }
    if (context.IDENTIFIER() != null) {
      return new IdentifierReference(context.IDENTIFIER().getText());
    }
    // TODO parse super superSuffix

    // Visit children to handle literals
    return super.visitPrimary(context);
  }

  @Override
  public TweedleExpression visitLiteral(TweedleParser.LiteralContext context) {
    TerminalNode wholeNumber = context.DECIMAL_LITERAL();
    if (wholeNumber != null) {
      int value = Integer.parseInt(wholeNumber.getSymbol().getText());
      return TweedleTypes.WHOLE_NUMBER.createValue(value);
    }

    TerminalNode flt = context.FLOAT_LITERAL();
    if (flt != null) {
      double value = Double.parseDouble(flt.getSymbol().getText());
      return TweedleTypes.DECIMAL_NUMBER.createValue(value);
    }

    if (context.NULL_LITERAL() != null) {
      return TweedleNull.NULL;
    }

    TerminalNode bool = context.BOOL_LITERAL();
    if (bool != null) {
      boolean value = Boolean.parseBoolean(bool.getSymbol().getText());
      return TweedleTypes.BOOLEAN.createValue(value);
    }

    TerminalNode str = context.STRING_LITERAL();
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
    Token prefix = context.prefix;
    Token operation = context.bop;

    if (prefix != null) {
      switch (prefix.getText()) {
      case "+":
        // A positive number, or at least not changing the sign. Send along child.
        return getFirstExpression(TweedleTypes.NUMBER, context);
      case "-":
        // A negative number, or a sign flip. Send along negated child.
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
        return binaryExpression(EqualToExpression::new, null, context); //XxY=>B
      case "!=":
        return binaryExpression(NotEqualToExpression::new, null, context); //XxY=>B
      case "..":
        return binaryExpression(StringConcatenationExpression::new, null, context); //XxY=>B
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
      TweedleExpression array = context.expression(0).accept(new ExpressionVisitor(new TweedleArrayType()));
      TweedleExpression index = context.expression(1).accept(new ExpressionVisitor(TweedleTypes.WHOLE_NUMBER));
      return new ArrayIndexExpression(((TweedleArrayType) array.getType()).getValueType(), array, index);
    } else if (context.lambdaCall() != null) {
      TweedleExpression lambdaSourceExp = getFirstExpression(null, context);
      if (context.lambdaCall().unlabeledExpressionList() == null) {
        return new LambdaEvaluation(lambdaSourceExp);
      } else {
        final List<TweedleExpression> elements = visitUnlabeledArguments(context.lambdaCall().unlabeledExpressionList(), new ExpressionVisitor());
        return new LambdaEvaluation(lambdaSourceExp, elements);
      }
    }

    // This will handle primary & lambda
    return visitChildren(context);
  }

  @Override
  public TweedleExpression visitLambdaExpression(TweedleParser.LambdaExpressionContext ctx) {
    List<TweedleRequiredParameter> parameters = lambdaParameters(ctx.lambdaParameters());
    List<TweedleStatement> stmts = collectBlockStatements(ctx.block().blockStatement());
    return new LambdaExpression(parameters, stmts);
  }

  private List<TweedleRequiredParameter> lambdaParameters(TweedleParser.LambdaParametersContext context) {
    return context.requiredParameter().stream().map(field -> new TweedleRequiredParameter(getType(field.typeType()), field.variableDeclaratorId().IDENTIFIER().getText())).collect(toList());
  }

  @Override
  public TweedleExpression visitMethodCall(TweedleParser.MethodCallContext ctx) {

    return new MethodCallExpression(new ThisExpression(), ctx.IDENTIFIER().getText(), visitLabeledArguments(ctx.labeledExpressionList()), false);
  }

  public @Override
  TweedleExpression visitSuperSuffix(TweedleParser.SuperSuffixContext context) {

    if (context.IDENTIFIER() != null) {
      if (context.arguments() != null) {
        return new MethodCallExpression(new SuperExpression(), context.IDENTIFIER().getText(), visitLabeledArguments(context.arguments().labeledExpressionList()));
      } else {
        return new FieldAccess(new SuperExpression(), context.IDENTIFIER().getText());
      }
    } else if (context.arguments() != null) {
      return new Instantiation(new SuperExpression(), visitLabeledArguments(context.arguments().labeledExpressionList()));
    }
    throw new RuntimeException("Super suffix could not be constructed.");
  }

  public @Override
  TweedleExpression visitCreator(TweedleParser.CreatorContext context) {
    String typeName = context.createdName().getText();
    final TweedleParser.ArrayCreatorRestContext arrayDetails = context.arrayCreatorRest();
    if (arrayDetails != null) {
      TweedlePrimitiveType prim = getPrimitiveType(typeName);
      TweedleType memberType = prim == null ? getTypeReference(typeName) : prim;
      TweedleArrayType arrayType = new TweedleArrayType(memberType);
      if (arrayDetails.arrayInitializer() != null) {
        final List<TweedleExpression> elements = visitUnlabeledArguments(arrayDetails.arrayInitializer().unlabeledExpressionList(), new ExpressionVisitor(memberType));
        return new TweedleArrayInitializer(arrayType, elements);
      } else {
        return new TweedleArrayInitializer(arrayType, arrayDetails.expression().accept(new ExpressionVisitor()));
      }
    } else {
      TweedleTypeReference typeRef = getTypeReference(typeName);
      TweedleParser.LabeledExpressionListContext argsContext = context.classCreatorRest().arguments().labeledExpressionList();
      Map<String, TweedleExpression> arguments = visitLabeledArguments(argsContext);
      return new Instantiation(typeRef, arguments);
    }
  }

  private TweedleExpression fieldOrMethodRef(TweedleParser.ExpressionContext context) {
    // Use untyped expression visitor for target
    TweedleExpression target = context.expression(0).accept(new ExpressionVisitor());
    if (context.IDENTIFIER() != null) {
      return new FieldAccess(target, context.IDENTIFIER().getText());
    }
    if (context.methodCall() != null) {
      return new MethodCallExpression(
          target,
          context.methodCall().IDENTIFIER().getText(),
          visitLabeledArguments(context.methodCall().labeledExpressionList()));
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
    final ExpressionVisitor visitor = new ExpressionVisitor(type);
    return context.expression().stream().map(exp -> exp.accept(visitor)).collect(toList());
  }
}

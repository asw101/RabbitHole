package org.alice.tweedle.unlinked;

import org.alice.tweedle.*;
import org.alice.tweedle.ast.*;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class TweedleUnlinkedParser {

  private static final Map<String, TweedlePrimitiveType> PRIMITIVE_TYPE_MAP =
      java.util.Arrays.stream(TweedleTypes.PRIMITIVE_TYPES)
          .collect(toMap(TweedlePrimitiveType::getName, identity()));

  public TweedleType parseType(String sourceForType) {
    return new TypeVisitor().visit(tweedleParserForSource(sourceForType).typeDeclaration());
  }

  TweedleStatement parseStatement(String sourceForExpression) {
    return new StatementVisitor(this).visit(tweedleParserForSource(sourceForExpression).blockStatement());
  }

  TweedleExpression parseExpression(String sourceForExpression) {
    return new ExpressionVisitor(this).visit(tweedleParserForSource(sourceForExpression).expression());
  }

  private TweedleParser tweedleParserForSource(String source) {
    CharStream charStream = CharStreams.fromString(source);
    TweedleLexer lexer = new TweedleLexer(charStream);
    TokenStream tokens = new CommonTokenStream(lexer);
    return new TweedleParser(tokens);
  }

  private class ClassBody {
    public List<TweedleField> properties = new ArrayList<>();
    public List<TweedleMethod> methods = new ArrayList<>();
    public List<TweedleConstructor> constructors = new ArrayList<>();
  }

  private class TypeVisitor extends TweedleParserBaseVisitor<TweedleType> {

    private ClassBody body;
    private ClassBodyDeclarationVisitor cbdVisitor;

    @Override
    public TweedleType visitTypeDeclaration(TweedleParser.TypeDeclarationContext context) {
      body = new ClassBody();
      cbdVisitor = new ClassBodyDeclarationVisitor(body);
      return super.visitChildren(context);
    }

    @Override
    public TweedleType visitClassDeclaration(TweedleParser.ClassDeclarationContext ctx) {
      ctx.classBody().classBodyDeclaration().forEach(cbd -> cbd.accept(cbdVisitor));
      String className = ctx.identifier().getText();
      if (null != ctx.EXTENDS()) {
        String superclass = ctx.typeType().classType().getText();
        return new TweedleClass(className, superclass, body.properties, body.methods, body.constructors);
      } else {
        return new TweedleClass(className, body.properties, body.methods, body.constructors);
      }
    }

    @Override
    public TweedleType visitEnumDeclaration(TweedleParser.EnumDeclarationContext ctx) {

      Map<String, TweedleEnumValue> values = new HashMap<>();
      ctx.enumConstants().enumConstant().forEach(enumConst -> {
        String name = enumConst.identifier().getText();
        Map<String, TweedleExpression> arguments = null;
        if (enumConst.arguments() != null) {
          arguments = visitLabeledArguments(enumConst.arguments().labeledExpressionList());
        }
        TweedleEnumValue value = new TweedleEnumValue(null, name, arguments);
        values.put(name, value);
      });
      if (ctx.enumBodyDeclarations() != null) {
        ctx.enumBodyDeclarations().classBodyDeclaration().forEach(cbd -> cbd.accept(cbdVisitor));
      }
      return new TweedleEnum(ctx.identifier().getText(), values, body.properties, body.methods, body.constructors);
    }
  }

  private class ClassBodyDeclarationVisitor extends TweedleParserBaseVisitor<Void> {
    private final ClassBody body;

    ClassBodyDeclarationVisitor(ClassBody body) {
      this.body = body;
    }

    @Override
    public Void visitMethodDeclaration(TweedleParser.MethodDeclarationContext ctx) {
      return null;
    }

    @Override
    public Void visitClassBodyDeclaration(TweedleParser.ClassBodyDeclarationContext context) {
      TweedleParser.MemberDeclarationContext memberDec = context.memberDeclaration();
      if (memberDec != null) {
        List<String> modifiers = context.classModifier().stream().map(mod -> (mod.STATIC() != null) ? "static" : null).collect(toList());
        memberDec.accept(new MemberDeclarationVisitor(body, modifiers));
      }
      return super.visitClassBodyDeclaration(context);
    }
  }

  private class MemberDeclarationVisitor extends TweedleParserBaseVisitor<Void> {
    private ClassBody body;
    private List<String> modifiers;

    MemberDeclarationVisitor(ClassBody body, List<String> modifiers) {
      this.body = body;
      this.modifiers = modifiers;
    }

    @Override
    public Void visitFieldDeclaration(TweedleParser.FieldDeclarationContext context) {
      TweedleType type = getType(context.typeType());
      final String name = context.variableDeclarator().variableDeclaratorId().IDENTIFIER().getText();
      TweedleField property;
      if (context.variableDeclarator().variableInitializer() != null) {
        ExpressionVisitor initVisitor = new ExpressionVisitor(TweedleUnlinkedParser.this, type, true);
        TweedleExpression init = context.variableDeclarator().variableInitializer().accept(initVisitor);
        property = new TweedleField(modifiers, type, name, init);
      } else {
        property = new TweedleField(modifiers, type, name);
      }
      body.properties.add(property);
      return super.visitFieldDeclaration(context);
    }

    @Override
    public Void visitMethodDeclaration(TweedleParser.MethodDeclarationContext context) {
      TweedleMethod method = new TweedleMethod(modifiers, getTypeOrVoid(context.typeTypeOrVoid()), context.IDENTIFIER().getText(), requiredParameters(context.formalParameters()), optionalParameters(context.formalParameters()), collectBlockStatements(context.methodBody().block().blockStatement()));
      body.methods.add(method);
      return super.visitMethodDeclaration(context);
    }

    @Override
    public Void visitConstructorDeclaration(TweedleParser.ConstructorDeclarationContext context) {
      TweedleConstructor constructor = new TweedleConstructor(getTypeReference(context.IDENTIFIER().getText()), context.IDENTIFIER().getText(), requiredParameters(context.formalParameters()), optionalParameters(context.formalParameters()), collectBlockStatements(context.constructorBody.blockStatement()));
      body.constructors.add(constructor);
      return super.visitConstructorDeclaration(context);
    }

    private List<TweedleRequiredParameter> requiredParameters(TweedleParser.FormalParametersContext context) {
      if (context.formalParameterList() == null || context.formalParameterList().requiredParameter() == null) {
        return new ArrayList<>();
      }
      return context.formalParameterList().requiredParameter().stream().map(field -> new TweedleRequiredParameter(getType(field.typeType()), field.variableDeclaratorId().IDENTIFIER().getText())).collect(toList());
    }

    private List<TweedleOptionalParameter> optionalParameters(TweedleParser.FormalParametersContext context) {
      if (context.formalParameterList() == null || context.formalParameterList().optionalParameter() == null) {
        return new ArrayList<>();
      }
      return context.formalParameterList().optionalParameter().stream().map(field -> {
        final TweedleType type = getType(field.typeType());
        final String varName = field.variableDeclaratorId().IDENTIFIER().getText();
        return new TweedleOptionalParameter(type, varName, field.accept(new ExpressionVisitor(TweedleUnlinkedParser.this, type)));
      }).collect(toList());
    }
  }

  List<TweedleStatement> collectBlockStatements(List<TweedleParser.BlockStatementContext> contexts) {
    StatementVisitor statementVisitor = new StatementVisitor(this);
    return contexts.stream().map(stmt -> stmt.accept(statementVisitor)).collect(toList());
  }

  Map<String, TweedleExpression> visitLabeledArguments(TweedleParser.LabeledExpressionListContext context) {
    if (context == null) {
      return Collections.emptyMap();
    }

    List<TweedleParser.LabeledExpressionContext> argumentContexts = context.labeledExpression();
    Map<String, TweedleExpression> arguments = new HashMap<>(argumentContexts.size());
    final ExpressionVisitor visitor = new ExpressionVisitor(this);
    for (TweedleParser.LabeledExpressionContext arg : argumentContexts) {
      TweedleExpression argValue = arg.expression().accept(visitor);
      arguments.put(arg.IDENTIFIER().getText(), argValue);
    }
    return arguments;
  }

  List<TweedleExpression> visitUnlabeledArguments(TweedleParser.UnlabeledExpressionListContext listContext, ExpressionVisitor expressionVisitor) {
    return listContext == null ? Collections.emptyList() : listContext.expression().stream().map(a -> a.accept(expressionVisitor)).collect(toList());
  }

  TweedleType getTypeOrVoid(TweedleParser.TypeTypeOrVoidContext context) {
    if (context.VOID() != null) {
      return TweedleVoidType.VOID;
    }
    return getType(context.typeType());
  }

  TweedleType getType(TweedleParser.TypeTypeContext context) {
    TweedleType baseType = context.classType() != null ? getTypeReference(context.classType().getText()) : getPrimitiveType(context.primitiveType().getText());
    if (context.getChildCount() > 1 && baseType != null) {
      return new TweedleArrayType(baseType);
    }
    return baseType;
  }

  TweedleTypeReference getTypeReference(String typeName) {
    return new TweedleTypeReference(typeName);
  }

  TweedlePrimitiveType getPrimitiveType(String typeName) {
    return PRIMITIVE_TYPE_MAP.get(typeName);
  }

}

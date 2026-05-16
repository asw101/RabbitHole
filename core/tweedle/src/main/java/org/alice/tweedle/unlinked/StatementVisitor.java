package org.alice.tweedle.unlinked;

import org.alice.tweedle.*;
import org.alice.tweedle.ast.*;

import java.util.ArrayList;
import java.util.List;

class StatementVisitor extends TweedleParserBaseVisitor<TweedleStatement> {

  private final TweedleUnlinkedParser parser;

  StatementVisitor(TweedleUnlinkedParser parser) {
    this.parser = parser;
  }

  @Override
  public TweedleStatement visitLocalVariableDeclaration(TweedleParser.LocalVariableDeclarationContext context) {
    TweedleType type = parser.getType(context.typeType());
    final String name = context.variableDeclarator().variableDeclaratorId().IDENTIFIER().getText();
    TweedleLocalVariable decl;
    if (context.variableDeclarator().variableInitializer() != null) {
      ExpressionVisitor initVisitor = new ExpressionVisitor(parser, type);
      TweedleExpression init = context.variableDeclarator().variableInitializer().accept(initVisitor);
      decl = new TweedleLocalVariable(type, name, init);
    } else {
      decl = new TweedleLocalVariable(type, name);
    }
    return new LocalVariableDeclaration(context.CONSTANT() != null, decl);
  }

  @Override
  public TweedleStatement visitBlockStatement(TweedleParser.BlockStatementContext context) {
    if (context.NODE_DISABLE() != null) {
      TweedleStatement stmt = context.blockStatement().accept(this);
      stmt.disable();
      return stmt;
    }
    if (context.localVariableDeclaration() != null) {
      return context.localVariableDeclaration().accept(this);
    }
    return super.visitBlockStatement(context);
  }

  @Override
  public TweedleStatement visitStatement(TweedleParser.StatementContext context) {
    if (context.COUNT_UP_TO() != null) {
      return new CountUpLoop(context.IDENTIFIER().getText(), context.expression().accept(new ExpressionVisitor(parser, TweedleTypes.WHOLE_NUMBER)), parser.collectBlockStatements(context.block(0).blockStatement()));
    }
    if (context.IF() != null) {
      TweedleExpression condition = context.parExpression().expression().accept(new ExpressionVisitor(parser, TweedleTypes.BOOLEAN));
      List<TweedleStatement> thenBlock = parser.collectBlockStatements(context.block(0).blockStatement());
      List<TweedleStatement> elseBlock = context.ELSE() != null ? parser.collectBlockStatements(context.block(1).blockStatement()) : new ArrayList<>();
      return new ConditionalStatement(condition, thenBlock, elseBlock);
    }
    if (context.forControl() != null) {
      final TweedleType valueType = parser.getType(context.forControl().typeType());
      final TweedleArrayType arrayType = new TweedleArrayType(valueType);
      final TweedleLocalVariable loopVar = new TweedleLocalVariable(valueType, context.forControl().variableDeclaratorId().getText());
      final TweedleExpression loopValues = context.forControl().expression().accept(new ExpressionVisitor(parser, arrayType));
      final List<TweedleStatement> statements = parser.collectBlockStatements(context.block(0).blockStatement());
      if (context.FOR_EACH() != null) {
        return new ForEachLoop(loopVar, loopValues, statements);
      }
      if (context.EACH_TOGETHER() != null) {
        return new ForEachTogether(loopVar, loopValues, statements);
      }
      throw new RuntimeException("Found a forControl in a statement where it was not expected: " + context);
    }
    if (context.WHILE() != null) {
      return new WhileLoop(context.parExpression().expression().accept(new ExpressionVisitor(parser, TweedleTypes.BOOLEAN)), parser.collectBlockStatements(context.block(0).blockStatement()));
    }
    if (context.DO_IN_ORDER() != null) {
      return new DoInOrder(parser.collectBlockStatements(context.block(0).blockStatement()));
    }
    if (context.DO_TOGETHER() != null) {
      return new DoTogether(parser.collectBlockStatements(context.block(0).blockStatement()));
    }
    if (context.RETURN() != null) {
      if (context.expression() != null) {
        return new ReturnStatement(context.expression().accept(new ExpressionVisitor(parser)));
      } else {
        return new ReturnStatement();
      }
    }
    TweedleParser.ExpressionContext expContext = context.statementExpression;
    if (expContext != null) {
      return new ExpressionStatement(context.expression().accept(new ExpressionVisitor(parser)));
    }
    throw new RuntimeException("Found a statement that was not expected: " + context);
  }
}

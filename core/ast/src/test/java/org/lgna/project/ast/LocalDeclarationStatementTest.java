package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.code.CodeOrganizer;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class LocalDeclarationStatementTest {
  @Test
  public void initializerExpressionTypeTracksDeclaredLocalType() {
    UserLocal local = new UserLocal("count", Integer.class, true);
    LocalDeclarationStatement statement = new LocalDeclarationStatement(local, new IntegerLiteral(1));

    assertSame(local, statement.local.getValue());
    assertSame(local.getValueType(), statement.initializer.getExpressionType());
  }

  @Test
  public void processDispatchesToAstProcessorHook() {
    UserLocal local = new UserLocal("message", String.class, true);
    LocalDeclarationStatement statement = new LocalDeclarationStatement(local, new StringLiteral("hi"));
    AtomicReference<LocalDeclarationStatement> processed = new AtomicReference<LocalDeclarationStatement>();

    statement.process(new AstProcessor() {
      @Override
      public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
        return null;
      }

      @Override
      public void processLocalDeclaration(LocalDeclarationStatement stmt) {
        processed.set(stmt);
      }
    });

    assertSame(statement, processed.get());
  }

  @Test
  public void userLocalWithoutNameGeneratesNameFromParentStatement() {
    UserLocal local = new UserLocal(null, JavaType.getInstance(String.class), false);
    LocalDeclarationStatement statement = new LocalDeclarationStatement(local, new StringLiteral("hello"));

    assertEquals("unusedName", local.getValidName());
    assertSame(statement, local.getParent());
  }
}

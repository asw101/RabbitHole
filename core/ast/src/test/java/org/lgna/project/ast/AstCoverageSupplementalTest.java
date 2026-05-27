package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.code.CodeOrganizer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AstCoverageSupplementalTest {
  @Test
  public void constructorBlockDefaultsToSuperConstructorAndDispatchesToProcessor() {
    ConstructorBlockStatement block = new ConstructorBlockStatement();
    AtomicInteger constructorBlocks = new AtomicInteger();

    block.process(processor(
        constructorBlocks,
        new AtomicInteger(),
        new AtomicInteger(),
        new AtomicReference<>(),
        new AtomicInteger()
    ));

    assertTrue(block.constructorInvocationStatement.getValue() instanceof SuperConstructorInvocationStatement);
    assertEquals(1, constructorBlocks.get());
  }

  @Test
  public void explicitConstructorInvocationsDispatchCorrectProcessorHooks() {
    ThisConstructorInvocationStatement thisConstructor = new ThisConstructorInvocationStatement();
    SuperConstructorInvocationStatement superConstructor = new SuperConstructorInvocationStatement();
    AtomicInteger thisCount = new AtomicInteger();
    AtomicInteger superCount = new AtomicInteger();

    AstProcessor processor = new AstProcessor() {
      @Override
      public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
        return null;
      }

      @Override
      public void processThisConstructor(ThisConstructorInvocationStatement thisCon) {
        thisCount.incrementAndGet();
      }

      @Override
      public void processSuperConstructor(SuperConstructorInvocationStatement supCon) {
        superCount.incrementAndGet();
      }
    };

    thisConstructor.process(processor);
    superConstructor.process(processor);

    assertEquals(1, thisCount.get());
    assertEquals(1, superCount.get());
  }

  @Test
  public void commentAndNullLiteralDispatchExpectedProcessorHooks() {
    Comment comment = new Comment("hello world");
    NullLiteral literal = new NullLiteral();
    AtomicReference<String> commentText = new AtomicReference<>();
    AtomicInteger nullCount = new AtomicInteger();

    AstProcessor processor = new AstProcessor() {
      @Override
      public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
        return null;
      }

      @Override
      public void processMultiLineComment(String comment) {
        commentText.set(comment);
      }

      @Override
      public void processNull() {
        nullCount.incrementAndGet();
      }
    };

    comment.process(processor);
    literal.process(processor);

    assertEquals("hello world", commentText.get());
    assertFalse(comment.isEnabledNonCommment());
    assertSame(JavaType.OBJECT_TYPE, literal.getType());
    assertTrue(literal.isValid());
    assertEquals(1, nullCount.get());
  }

  private static AstProcessor processor(
      AtomicInteger constructorBlocks,
      AtomicInteger thisCount,
      AtomicInteger superCount,
      AtomicReference<String> commentText,
      AtomicInteger nullCount
  ) {
    return new AstProcessor() {
      @Override
      public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
        return null;
      }

      @Override
      public void processConstructorBlock(ConstructorBlockStatement constructor) {
        constructorBlocks.incrementAndGet();
      }

      @Override
      public void processThisConstructor(ThisConstructorInvocationStatement thisCon) {
        thisCount.incrementAndGet();
      }

      @Override
      public void processSuperConstructor(SuperConstructorInvocationStatement supCon) {
        superCount.incrementAndGet();
      }

      @Override
      public void processMultiLineComment(String comment) {
        commentText.set(comment);
      }

      @Override
      public void processNull() {
        nullCount.incrementAndGet();
      }
    };
  }
}

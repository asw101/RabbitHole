/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.lgna.project.ast;

import org.lgna.common.EachInTogetherRunnable;
import org.lgna.common.ThreadUtilities;

/**
 * Package-private delegate for processDoTogether and processEachInTogether
 * emission in {@link JavaCodeGenerator}.
 *
 * Extracted from JavaCodeGenerator to reduce file size while preserving
 * identical concurrency code-generation behaviour.
 */
class JavaConcurrencyEmitter {

  private static final JavaType THREAD_UTILITIES_TYPE = JavaType.getInstance(ThreadUtilities.class);
  private static final JavaMethod DO_TOGETHER_METHOD = THREAD_UTILITIES_TYPE.getDeclaredMethod("doTogether", Runnable[].class);
  private static final JavaMethod EACH_IN_TOGETHER_METHOD = THREAD_UTILITIES_TYPE.getDeclaredMethod("eachInTogether", EachInTogetherRunnable.class, Object[].class);
  private static final JavaType EACH_IN_TOGETHER_RUNNABLE_TYPE = JavaType.getInstance(EachInTogetherRunnable.class);

  private final JavaCodeGenerator generator;

  JavaConcurrencyEmitter(JavaCodeGenerator generator) {
    this.generator = generator;
  }

  void processDoTogether(DoTogether doTogether) {
    TypeExpression target = new TypeExpression(THREAD_UTILITIES_TYPE);
    generator.appendTargetAndMethodName(target, DO_TOGETHER_METHOD);
    generator.appendString("(");
    String prefix = "";
    for (Statement statement : doTogether.body.getValue().statements) {
      generator.appendString(prefix);
      if (generator.isLambdaSupported()) {
        generator.appendString("()->{");
      } else {
        generator.appendString("new Runnable(){public void run(){");
      }
      if (statement instanceof DoInOrder doInOrder) {
        BlockStatement blockStatement = doInOrder.body.getValue();
        for (Statement subStatement : blockStatement.statements) {
          generator.appendStatement(subStatement);
        }
      } else {
        generator.appendStatement(statement);
      }
      if (generator.isLambdaSupported()) {
        generator.appendString("}");
      } else {
        generator.appendString("}}");
      }
      prefix = ",";
    }
    generator.appendString(");");
  }

  void processEachInTogether(AbstractEachInTogether eachInTogether) {
    TypeExpression target = new TypeExpression(THREAD_UTILITIES_TYPE);
    generator.appendTargetAndMethodName(target, EACH_IN_TOGETHER_METHOD);
    generator.appendString("(");

    UserLocal itemValue = eachInTogether.item.getValue();
    AbstractType<?, ?, ?> itemType = itemValue.getValueType();
    if (generator.isLambdaSupported()) {
      generator.appendString("(");
      generator.processTypeName(itemType);
      generator.appendSpace();
      generator.appendString(itemValue.getName());
      generator.appendString(")->");
    } else {
      generator.appendString("new ");
      generator.processTypeName(EACH_IN_TOGETHER_RUNNABLE_TYPE);
      generator.appendString("<");
      generator.processTypeName(itemType);
      generator.appendString(">() { public void run(");
      generator.processTypeName(itemType);
      generator.appendSpace();
      generator.appendString(itemValue.getName());
      generator.appendString(")");
    }
    generator.appendStatement(eachInTogether.body.getValue());
    if (!generator.isLambdaSupported()) {
      generator.appendString("}");
    }
    Expression arrayOrIterableExpression = eachInTogether.getArrayOrIterableProperty().getValue();
    if (arrayOrIterableExpression instanceof ArrayInstanceCreation arrayInstanceCreation) {
      for (Expression variableLengthExpression : arrayInstanceCreation.expressions) {
        generator.appendString(",");
        generator.processExpression(variableLengthExpression);
      }
    } else {
      generator.appendString(",");
      generator.processExpression(arrayOrIterableExpression);
    }
    generator.appendString(");");
  }
}

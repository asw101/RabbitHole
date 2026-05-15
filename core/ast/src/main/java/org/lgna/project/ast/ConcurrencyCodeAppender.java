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

import edu.cmu.cs.dennisc.java.util.ResourceBundleUtilities;
import org.lgna.common.EachInTogetherRunnable;
import org.lgna.common.ThreadUtilities;

import java.util.MissingResourceException;

/**
 * Package-private delegate that generates Java code for concurrency statements
 * (do-in-order, do-together, each-in-together) for {@link JavaCodeGenerator}.
 */
class ConcurrencyCodeAppender {

  void appendDoInOrder(DoInOrder doInOrder, JavaCodeGenerator gen) {
    gen.openBlock();
    try {
      final String doInOrderName = ResourceBundleUtilities.getStringFromSimpleNames(
          doInOrder.getClass(), "org.alice.ide.controlflow.Templates");
      gen.appendSingleLineComment(doInOrderName);
    } catch (MissingResourceException mre) {
      System.out.println("No resource bundle setup to localize do in order.");
    }
    BlockStatement blockStatement = doInOrder.body.getValue();
    for (Statement subStatement : blockStatement.statements) {
      gen.appendStatement(subStatement);
    }
    gen.closeBlock();
  }

  void appendDoTogether(DoTogether doTogether, JavaCodeGenerator gen) {
    JavaType threadUtilitiesType = JavaType.getInstance(ThreadUtilities.class);
    JavaMethod doTogetherMethod = threadUtilitiesType.getDeclaredMethod("doTogether", Runnable[].class);
    TypeExpression target = new TypeExpression(threadUtilitiesType);
    gen.appendTargetAndMethodName(target, doTogetherMethod);
    gen.appendString("(");
    String prefix = "";
    for (Statement statement : doTogether.body.getValue().statements) {
      gen.appendString(prefix);
      if (gen.isLambdaSupported()) {
        gen.appendString("()->{");
      } else {
        gen.appendString("new Runnable(){public void run(){");
      }
      if (statement instanceof DoInOrder doInOrder) {
        BlockStatement blockStatement = doInOrder.body.getValue();
        for (Statement subStatement : blockStatement.statements) {
          gen.appendStatement(subStatement);
        }
      } else {
        gen.appendStatement(statement);
      }
      if (gen.isLambdaSupported()) {
        gen.appendString("}");
      } else {
        gen.appendString("}}");
      }
      prefix = ",";
    }
    gen.appendString(");");
  }

  void appendEachInTogether(AbstractEachInTogether eachInTogether, JavaCodeGenerator gen) {
    JavaType threadUtilitiesType = JavaType.getInstance(ThreadUtilities.class);
    JavaMethod eachInTogetherMethod = threadUtilitiesType.getDeclaredMethod(
        "eachInTogether", EachInTogetherRunnable.class, Object[].class);
    TypeExpression target = new TypeExpression(threadUtilitiesType);
    gen.appendTargetAndMethodName(target, eachInTogetherMethod);
    gen.appendString("(");

    UserLocal itemValue = eachInTogether.item.getValue();
    AbstractType<?, ?, ?> itemType = itemValue.getValueType();
    if (gen.isLambdaSupported()) {
      gen.appendString("(");
      gen.processTypeName(itemType);
      gen.appendSpace();
      gen.appendString(itemValue.getName());
      gen.appendString(")->");
    } else {
      gen.appendString("new ");
      gen.processTypeName(JavaType.getInstance(EachInTogetherRunnable.class));
      gen.appendString("<");
      gen.processTypeName(itemType);
      gen.appendString(">() { public void run(");
      gen.processTypeName(itemType);
      gen.appendSpace();
      gen.appendString(itemValue.getName());
      gen.appendString(")");
    }
    gen.appendStatement(eachInTogether.body.getValue());
    if (!gen.isLambdaSupported()) {
      gen.appendString("}");
    }
    Expression arrayOrIterableExpression = eachInTogether.getArrayOrIterableProperty().getValue();
    if (arrayOrIterableExpression instanceof ArrayInstanceCreation arrayInstanceCreation) {
      for (Expression variableLengthExpression : arrayInstanceCreation.expressions) {
        gen.appendString(",");
        gen.processExpression(variableLengthExpression);
      }
    } else {
      gen.appendString(",");
      gen.processExpression(arrayOrIterableExpression);
    }
    gen.appendString(");");
  }
}

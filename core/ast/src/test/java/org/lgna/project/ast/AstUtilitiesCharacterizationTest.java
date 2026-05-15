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

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Characterization test: verifies the public API surface of AstUtilities
 * is unchanged after extracting delegates.
 */
public class AstUtilitiesCharacterizationTest {

  private static final Set<String> EXPECTED_PUBLIC_METHODS = Set.of(
      "createCopy", "isKeywordExpression", "getJavaKeyedArgumentSubArgument0Expression",
      "getDeclaredPersistentPropertyGetters", "getPersistentPropertyGetters", "getSetterForGetter",
      "createMethod", "createFunction", "createProcedure", "createType",
      "createDoInOrder", "createDoTogether", "createComment", "createLocalDeclarationStatement",
      "createCountLoop", "createWhileLoop", "createConditionalStatement",
      "createForEachInArrayLoop", "createEachInArrayTogether",
      "createStaticMethodInvocation", "createStaticFieldAccess",
      "createNextMethodInvocation", "completeMethodInvocation",
      "createMethodInvocation", "createMethodInvocationStatement",
      "createTypeExpression", "createInstanceCreation", "createArrayInstanceCreation",
      "lookupMethod", "createReturnStatement",
      "createFieldAssignment", "createFieldAssignmentStatement",
      "createFieldArrayAssignment", "createFieldArrayAssignmentStatement",
      "createLocalAssignment", "createLocalAssignmentStatement",
      "createLocalArrayAssignment", "createLocalArrayAssignmentStatement",
      "createParameterArrayAssignment", "createParameterArrayAssignmentStatement",
      "createStringConcatenation", "removeParameter", "addParameter",
      "getParameterValueTypes", "getSingleAbstractMethod",
      "createUserLambda", "createLambdaExpression",
      "isAddEventListenerMethodInvocationStatement", "getKeywordFactoryType",
      "getOverridenMethod", "getAllInvokedMethods",
      "fixRequiredArgumentsIfNecessary", "getNamedUserTypes",
      "getDeclaringTypeIfMemberOrTypeItselfIfType", "getAllMethods"
  );

  @Test
  public void publicApiSurfaceIsPreserved() {
    Set<String> actual = Arrays.stream(AstUtilities.class.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toCollection(TreeSet::new));
    for (String expected : EXPECTED_PUBLIC_METHODS) {
      assertTrue("Missing public method: " + expected, actual.contains(expected));
    }
  }

  @Test
  public void allPublicMethodsAreStatic() {
    for (Method m : AstUtilities.class.getDeclaredMethods()) {
      if (Modifier.isPublic(m.getModifiers())) {
        assertTrue(m.getName() + " should be static", Modifier.isStatic(m.getModifiers()));
      }
    }
  }

  @Test
  public void delegateClassesArePackagePrivate() {
    assertFalse("ExpressionFactory should be package-private",
        Modifier.isPublic(ExpressionFactory.class.getModifiers()));
    assertFalse("AssignmentFactory should be package-private",
        Modifier.isPublic(AssignmentFactory.class.getModifiers()));
    assertFalse("TypeAnalysisHelper should be package-private",
        Modifier.isPublic(TypeAnalysisHelper.class.getModifiers()));
  }

  @Test
  public void createProcedureReturnsUserMethodWithVoidReturn() {
    UserMethod proc = AstUtilities.createProcedure("doSomething");
    assertNotNull(proc);
    assertEquals("doSomething", proc.getName());
    assertEquals(JavaType.VOID_TYPE, proc.getReturnType());
  }

  @Test
  public void createDoInOrderReturnsNonNull() {
    DoInOrder node = AstUtilities.createDoInOrder();
    assertNotNull(node);
    assertNotNull(node.body.getValue());
  }

  @Test
  public void createCommentReturnsNonNull() {
    assertNotNull(AstUtilities.createComment());
  }

  @Test
  public void createTypeReturnsNamedUserType() {
    JavaType superType = JavaType.getInstance(Object.class);
    NamedUserType type = AstUtilities.createType("TestType", superType);
    assertNotNull(type);
    assertEquals("TestType", type.name.getValue());
    assertEquals(superType, type.superType.getValue());
    assertFalse("Should have at least one constructor", type.constructors.isEmpty());
  }
}

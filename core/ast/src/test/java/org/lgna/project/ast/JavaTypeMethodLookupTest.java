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
import org.lgna.project.annotations.MethodTemplate;
import org.lgna.project.annotations.Visibility;

import java.util.List;

import static org.junit.Assert.*;

/**
 * TDD contract tests for JavaTypeMethodLookup.
 * Tests 1–4 and 6 are characterization tests exercising the existing JavaType
 * public API — they pass before and after extraction and guard against
 * behavioral regression.
 * Tests 5 and 7 exercise JavaTypeMethodLookup directly — they will FAIL
 * until JavaTypeMethodLookup is created.
 */
public class JavaTypeMethodLookupTest {

  // ── Characterization tests via JavaType public API ─────────────────

  @Test
  public void getDeclaredMethods_returnsNonEmpty() {
    JavaType stringType = JavaType.getInstance(String.class);
    List<JavaMethod> methods = stringType.getDeclaredMethods();
    assertFalse("String should have declared methods", methods.isEmpty());
  }

  @Test
  public void getDeclaredMethods_containsExpectedMethod() {
    JavaType stringType = JavaType.getInstance(String.class);
    List<JavaMethod> methods = stringType.getDeclaredMethods();
    boolean found = methods.stream().anyMatch(m -> "length".equals(m.getName()));
    assertTrue("String methods should include 'length'", found);
  }

  @Test
  public void getDeclaredMethods_isUnmodifiable() {
    JavaType stringType = JavaType.getInstance(String.class);
    List<JavaMethod> methods = stringType.getDeclaredMethods();
    try {
      methods.add(null);
      fail("Returned list should be unmodifiable");
    } catch (UnsupportedOperationException expected) {
      // correct behavior
    }
  }

  @Test
  public void getDeclaredMethods_excludesPrivateMethods() {
    JavaType stringType = JavaType.getInstance(String.class);
    List<JavaMethod> methods = stringType.getDeclaredMethods();
    for (JavaMethod m : methods) {
      java.lang.reflect.Method mthd = m.getMethodReflectionProxy().getReification();
      assertNotNull("Reflected method should resolve", mthd);
      int mod = mthd.getModifiers();
      assertFalse("Method " + m.getName() + " should not be private",
          java.lang.reflect.Modifier.isPrivate(mod));
    }
  }

  @Test
  public void getDeclaredMethods_calledTwiceReturnsSameInstance() {
    JavaType stringType = JavaType.getInstance(String.class);
    List<JavaMethod> first = stringType.getDeclaredMethods();
    List<JavaMethod> second = stringType.getDeclaredMethods();
    assertSame("Lazy caching should return same list instance", first, second);
  }

  // ── TDD contract tests for JavaTypeMethodLookup ────────────────────

  @Test
  public void buildMethodList_returnsEmptyForNullClass() {
    List<JavaMethod> result = JavaTypeMethodLookup.buildMethodList(null);
    assertNotNull("Should return empty list, not null", result);
    assertTrue("Should be empty for null class", result.isEmpty());
  }

  /**
   * Test fixture with @MethodTemplate overloads that should be chain-linked.
   * doAction(String, int) is the longest (isFollowedByLongerMethod=false by default).
   * doAction(String) is shorter (isFollowedByLongerMethod=true).
   * The chain-linking logic in buildMethodList should wire:
   *   longer.nextShorterInChain → shorter
   *   shorter.nextLongerInChain → longer
   */
  public static class ChainFixture {
    @MethodTemplate(visibility = Visibility.PRIME_TIME)
    public void doAction(String a, int b) {}

    @MethodTemplate(visibility = Visibility.PRIME_TIME, isFollowedByLongerMethod = true)
    public void doAction(String a) {}
  }

  @Test
  public void buildMethodList_wiresMethodTemplateChains() {
    List<JavaMethod> methods = JavaTypeMethodLookup.buildMethodList(ChainFixture.class);

    JavaMethod longer = null;
    JavaMethod shorter = null;
    for (JavaMethod m : methods) {
      if ("doAction".equals(m.getName())) {
        java.lang.reflect.Method mthd = m.getMethodReflectionProxy().getReification();
        if (mthd.getParameterCount() == 2) {
          longer = m;
        } else if (mthd.getParameterCount() == 1) {
          shorter = m;
        }
      }
    }
    assertNotNull("Should find doAction(String, int)", longer);
    assertNotNull("Should find doAction(String)", shorter);
    assertSame("Longer's nextShorterInChain should point to shorter",
        shorter, longer.getNextShorterInChain());
    assertSame("Shorter's nextLongerInChain should point to longer",
        longer, shorter.getNextLongerInChain());
  }
}

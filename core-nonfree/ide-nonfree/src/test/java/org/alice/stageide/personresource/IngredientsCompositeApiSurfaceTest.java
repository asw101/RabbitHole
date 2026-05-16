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
package org.alice.stageide.personresource;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * API surface guard — verifies that all 22 public methods on
 * IngredientsComposite exist with the expected parameter types and
 * return types. This test catches accidental signature changes or
 * method removals during the extraction refactoring.
 *
 * This test uses reflection only and does not instantiate IngredientsComposite
 * (which would require a Croquet context).
 */
public class IngredientsCompositeApiSurfaceTest {

  /**
   * Expected public method signatures declared directly on IngredientsComposite
   * (not inherited framework overrides like handlePreActivation/handlePostDeactivation).
   *
   * Format: "methodName(paramTypes) -> returnType"
   */
  private static final Map<String, MethodSignature> EXPECTED_METHODS = new LinkedHashMap<>();

  static {
    // State accessors
    expect("getRandomize", new Class<?>[]{}, "org.lgna.croquet.Operation");
    expect("getLifeStageState", new Class<?>[]{}, "org.lgna.croquet.ImmutableDataSingleSelectListState");
    expect("getGenderState", new Class<?>[]{}, "org.lgna.croquet.ImmutableDataSingleSelectListState");
    expect("getBaseFaceState", new Class<?>[]{}, "org.lgna.croquet.ImmutableDataSingleSelectListState");
    expect("getSkinColorState", new Class<?>[]{}, "org.alice.stageide.personresource.SkinColorState");
    expect("getHairHatStyleState", new Class<?>[]{}, "org.lgna.croquet.RefreshableDataSingleSelectListState");
    expect("getHairColorNameState", new Class<?>[]{}, "org.lgna.croquet.RefreshableDataSingleSelectListState");
    expect("getBaseEyeColorState", new Class<?>[]{}, "org.lgna.croquet.ImmutableDataSingleSelectListState");
    expect("getFullBodyOutfitState", new Class<?>[]{}, "org.lgna.croquet.RefreshableDataSingleSelectListState");
    expect("getTopPieceState", new Class<?>[]{}, "org.lgna.croquet.RefreshableDataSingleSelectListState");
    expect("getBottomPieceState", new Class<?>[]{}, "org.lgna.croquet.RefreshableDataSingleSelectListState");
    expect("getObesityLevelState", new Class<?>[]{}, "org.lgna.croquet.BoundedDoubleState");
    expect("getBodyHeadHairTabState", new Class<?>[]{}, "org.lgna.croquet.ImmutableDataTabState");

    // Tab accessors
    expect("getBodyTab", new Class<?>[]{}, "org.alice.stageide.personresource.FullBodyOutfitTabComposite");
    expect("getHairTab", new Class<?>[]{}, "org.alice.stageide.personresource.HairTabComposite");
    expect("getFaceTab", new Class<?>[]{}, "org.alice.stageide.personresource.FaceTabComposite");

    // Skin tone
    expect("getClosestBaseSkinTone", new Class<?>[]{}, "org.lgna.story.resources.sims2.BaseSkinTone");

    // Hair resolution (delegation method for HairListCellRenderer)
    expect("getHairForHairHatStyle",
        new Class<?>[]{org.alice.stageide.personresource.data.HairHatStyle.class},
        "org.lgna.story.resources.sims2.Hair");

    // Atomic transaction management
    expect("pushAtomic", new Class<?>[]{}, "void");
    expect("popAtomic", new Class<?>[]{}, "void");

    // Resource creation/restoration
    expect("createResourceFromStates", new Class<?>[]{}, "org.lgna.story.resources.sims2.PersonResource");
    expect("setStates",
        new Class<?>[]{org.lgna.story.resources.sims2.PersonResource.class},
        "void");
  }

  @Test
  public void publicMethodSignaturesAreStable() {
    Class<?> clazz = IngredientsComposite.class;

    for (Map.Entry<String, MethodSignature> entry : EXPECTED_METHODS.entrySet()) {
      String methodName = entry.getKey();
      MethodSignature expected = entry.getValue();

      Method method;
      try {
        method = clazz.getMethod(methodName, expected.parameterTypes);
      } catch (NoSuchMethodException e) {
        fail("Missing public method: " + methodName + "("
            + formatTypes(expected.parameterTypes) + ")"
            + " — was it accidentally removed or signature-changed?");
        return;
      }

      assertTrue("Method " + methodName + " should be public",
          Modifier.isPublic(method.getModifiers()));

      String actualReturnType = method.getReturnType().getName();
      assertEquals("Return type mismatch for " + methodName + "()",
          expected.returnTypeName, actualReturnType);
    }
  }

  @Test
  public void publicMethodCount() {
    // Count all public methods declared directly on IngredientsComposite
    // (excluding inherited methods from Object and framework superclass)
    long declaredPublicCount = Arrays.stream(IngredientsComposite.class.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .count();

    // We expect exactly 22 public methods (may include framework overrides
    // like handlePreActivation/handlePostDeactivation which are declared as
    // overrides). Allowing >= 22 to accommodate framework overrides.
    assertTrue("Expected at least 22 declared public methods on IngredientsComposite, found " + declaredPublicCount,
        declaredPublicCount >= 22);
  }

  // --- Helpers ---

  private static void expect(String name, Class<?>[] params, String returnType) {
    EXPECTED_METHODS.put(name, new MethodSignature(params, returnType));
  }

  private static String formatTypes(Class<?>[] types) {
    return Arrays.stream(types).map(Class::getSimpleName).collect(Collectors.joining(", "));
  }

  private static class MethodSignature {
    final Class<?>[] parameterTypes;
    final String returnTypeName;

    MethodSignature(Class<?>[] parameterTypes, String returnTypeName) {
      this.parameterTypes = parameterTypes;
      this.returnTypeName = returnTypeName;
    }
  }
}

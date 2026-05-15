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
package org.alice.interact.handle;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Structural validation tests for the ManipulationHandle3D inner-class and
 * geometry-method extraction.
 *
 * Verifies:
 * - ManipulationHandle3D.java is under 500 lines after extraction
 * - Extracted files exist as separate top-level classes
 * - No remnant inner class declarations remain in ManipulationHandle3D
 * - Correct package declarations and visibility on extracted files
 * - CMU BSD copyright header preserved on all new files
 * - NOT_3D_HANDLE_CRITERION backward-compatibility alias remains
 * - Removed methods (getScalable, invertParentScale, getObjectScale) are gone
 */
public class ManipulationHandle3DExtractionTest {

  private static final String SRC_DIR = findSourceDir();

  private static String findSourceDir() {
    // Maven Surefire runs from module basedir (core/story-api/)
    File moduleLocal = new File("src/main/java/org/alice/interact/handle/");
    if (moduleLocal.isDirectory()) {
      return moduleLocal.getPath() + File.separator;
    }
    // Fallback: running from project root
    File projectRoot = new File("core/story-api/src/main/java/org/alice/interact/handle/");
    if (projectRoot.isDirectory()) {
      return projectRoot.getPath() + File.separator;
    }
    return moduleLocal.getPath() + File.separator;
  }

  // ===== Extracted file existence =====

  @Test
  public void handleGeometryHelperFileExists() {
    File file = new File(SRC_DIR + "HandleGeometryHelper.java");
    assertTrue("HandleGeometryHelper.java should exist as a separate file", file.exists());
  }

  @Test
  public void not3dHandleCriterionFileExists() {
    File file = new File(SRC_DIR + "Not3dHandleCriterion.java");
    assertTrue("Not3dHandleCriterion.java should exist as a separate file", file.exists());
  }

  @Test
  public void doubleInterruptibleAnimationFileExists() {
    File file = new File(SRC_DIR + "DoubleInterruptibleAnimation.java");
    assertTrue("DoubleInterruptibleAnimation.java should exist as a separate file", file.exists());
  }

  @Test
  public void color4fInterruptibleAnimationFileExists() {
    File file = new File(SRC_DIR + "Color4fInterruptibleAnimation.java");
    assertTrue("Color4fInterruptibleAnimation.java should exist as a separate file", file.exists());
  }

  // ===== ManipulationHandle3D line count =====

  @Test
  public void manipulationHandle3DIsUnder500Lines() throws IOException {
    File file = new File(SRC_DIR + "ManipulationHandle3D.java");
    assertTrue("ManipulationHandle3D.java should exist", file.exists());

    int lineCount = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      while (reader.readLine() != null) {
        lineCount++;
      }
    }
    assertTrue("ManipulationHandle3D.java should be under 500 lines, but was " + lineCount,
        lineCount < 500);
  }

  // ===== No remnant inner class declarations =====

  @Test
  public void noDoubleInterruptibleAnimationInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "ManipulationHandle3D.java");
    assertFalse("ManipulationHandle3D should not contain inner 'class DoubleInterruptibleAnimation'",
        content.contains("class DoubleInterruptibleAnimation"));
  }

  @Test
  public void noColor4fInterruptibleAnimationInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "ManipulationHandle3D.java");
    assertFalse("ManipulationHandle3D should not contain inner 'class Color4fInterruptibleAnimation'",
        content.contains("class Color4fInterruptibleAnimation"));
  }

  @Test
  public void noAnonymousCriterionInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "ManipulationHandle3D.java");
    assertFalse("ManipulationHandle3D should not contain anonymous 'new Criterion<Component>()'",
        content.contains("new Criterion<Component>()"));
  }

  // ===== Removed private methods =====

  @Test
  public void noGetScalableMethod() throws IOException {
    String content = readFile(SRC_DIR + "ManipulationHandle3D.java");
    assertFalse("ManipulationHandle3D should not contain private getScalable method",
        content.contains("private Scalable getScalable("));
  }

  @Test
  public void noInvertParentScaleMethod() throws IOException {
    String content = readFile(SRC_DIR + "ManipulationHandle3D.java");
    assertFalse("ManipulationHandle3D should not contain private invertParentScale method",
        content.contains("private void invertParentScale("));
  }

  @Test
  public void noGetObjectScaleMethod() throws IOException {
    String content = readFile(SRC_DIR + "ManipulationHandle3D.java");
    assertFalse("ManipulationHandle3D should not contain protected getObjectScale method",
        content.contains("protected double getObjectScale()"));
  }

  // ===== Backward compatibility: NOT_3D_HANDLE_CRITERION alias still present =====

  @Test
  public void not3dHandleCriterionAliasPresent() throws IOException {
    String content = readFile(SRC_DIR + "ManipulationHandle3D.java");
    assertTrue("ManipulationHandle3D should still expose NOT_3D_HANDLE_CRITERION field",
        content.contains("NOT_3D_HANDLE_CRITERION"));
  }

  // ===== Correct package declarations =====

  @Test
  public void handleGeometryHelperHasCorrectPackage() throws IOException {
    String content = readFile(SRC_DIR + "HandleGeometryHelper.java");
    assertTrue("HandleGeometryHelper.java should have correct package",
        content.contains("package org.alice.interact.handle;"));
  }

  @Test
  public void not3dHandleCriterionHasCorrectPackage() throws IOException {
    String content = readFile(SRC_DIR + "Not3dHandleCriterion.java");
    assertTrue("Not3dHandleCriterion.java should have correct package",
        content.contains("package org.alice.interact.handle;"));
  }

  @Test
  public void doubleInterruptibleAnimationHasCorrectPackage() throws IOException {
    String content = readFile(SRC_DIR + "DoubleInterruptibleAnimation.java");
    assertTrue("DoubleInterruptibleAnimation.java should have correct package",
        content.contains("package org.alice.interact.handle;"));
  }

  @Test
  public void color4fInterruptibleAnimationHasCorrectPackage() throws IOException {
    String content = readFile(SRC_DIR + "Color4fInterruptibleAnimation.java");
    assertTrue("Color4fInterruptibleAnimation.java should have correct package",
        content.contains("package org.alice.interact.handle;"));
  }

  // ===== Correct visibility =====

  @Test
  public void handleGeometryHelperIsPackagePrivate() throws IOException {
    String content = readFile(SRC_DIR + "HandleGeometryHelper.java");
    assertFalse("HandleGeometryHelper should be package-private (final class), not public",
        content.contains("public class HandleGeometryHelper")
            || content.contains("public final class HandleGeometryHelper"));
    assertTrue("HandleGeometryHelper should be declared as final class",
        content.contains("final class HandleGeometryHelper"));
  }

  @Test
  public void not3dHandleCriterionIsPackagePrivate() throws IOException {
    String content = readFile(SRC_DIR + "Not3dHandleCriterion.java");
    assertFalse("Not3dHandleCriterion should be package-private, not public",
        content.contains("public class Not3dHandleCriterion"));
    assertTrue("Not3dHandleCriterion should implement Criterion<Component>",
        content.contains("implements Criterion<Component>"));
  }

  @Test
  public void doubleInterruptibleAnimationIsPackagePrivate() throws IOException {
    String content = readFile(SRC_DIR + "DoubleInterruptibleAnimation.java");
    assertFalse("DoubleInterruptibleAnimation should be package-private (all usages are within the same package)",
        content.contains("public abstract class DoubleInterruptibleAnimation"));
    assertTrue("DoubleInterruptibleAnimation should be abstract",
        content.contains("abstract class DoubleInterruptibleAnimation"));
  }

  @Test
  public void color4fInterruptibleAnimationIsPackagePrivate() throws IOException {
    String content = readFile(SRC_DIR + "Color4fInterruptibleAnimation.java");
    assertFalse("Color4fInterruptibleAnimation should be package-private, not public",
        content.contains("public abstract class Color4fInterruptibleAnimation")
            || content.contains("public class Color4fInterruptibleAnimation"));
  }

  // ===== HandleGeometryHelper has static methods =====

  @Test
  public void handleGeometryHelperHasGetTransformationForAxis() throws IOException {
    String content = readFile(SRC_DIR + "HandleGeometryHelper.java");
    assertTrue("HandleGeometryHelper should contain getTransformationForAxis method",
        content.contains("static AffineMatrix4x4 getTransformationForAxis("));
  }

  @Test
  public void handleGeometryHelperHasGetManipulatedObjectBox() throws IOException {
    String content = readFile(SRC_DIR + "HandleGeometryHelper.java");
    assertTrue("HandleGeometryHelper should contain getManipulatedObjectBox method",
        content.contains("static AxisAlignedBox getManipulatedObjectBox("));
  }

  @Test
  public void handleGeometryHelperHasComputeObjectScale() throws IOException {
    String content = readFile(SRC_DIR + "HandleGeometryHelper.java");
    assertTrue("HandleGeometryHelper should contain computeObjectScale method",
        content.contains("static double computeObjectScale("));
  }

  @Test
  public void handleGeometryHelperHasCalculateCameraRelativeOpacity() throws IOException {
    String content = readFile(SRC_DIR + "HandleGeometryHelper.java");
    assertTrue("HandleGeometryHelper should contain calculateCameraRelativeOpacity method",
        content.contains("static float calculateCameraRelativeOpacity("));
  }

  @Test
  public void handleGeometryHelperHasInvertParentScale() throws IOException {
    String content = readFile(SRC_DIR + "HandleGeometryHelper.java");
    assertTrue("HandleGeometryHelper should contain invertParentScale method",
        content.contains("static AffineMatrix4x4 invertParentScale("));
  }

  @Test
  public void handleGeometryHelperHasGetScalable() throws IOException {
    String content = readFile(SRC_DIR + "HandleGeometryHelper.java");
    assertTrue("HandleGeometryHelper should contain getScalable method",
        content.contains("static Scalable getScalable("));
  }

  // ===== ManipulationHandle3D delegates to HandleGeometryHelper =====

  @Test
  public void manipulationHandle3DUsesHandleGeometryHelper() throws IOException {
    String content = readFile(SRC_DIR + "ManipulationHandle3D.java");
    assertTrue("ManipulationHandle3D should delegate to HandleGeometryHelper",
        content.contains("HandleGeometryHelper."));
  }

  // ===== CMU BSD copyright header preserved =====

  @Test
  public void handleGeometryHelperHasCopyrightHeader() throws IOException {
    String content = readFile(SRC_DIR + "HandleGeometryHelper.java");
    assertTrue("HandleGeometryHelper.java should have CMU BSD copyright header",
        content.contains("Carnegie Mellon University"));
  }

  @Test
  public void not3dHandleCriterionHasCopyrightHeader() throws IOException {
    String content = readFile(SRC_DIR + "Not3dHandleCriterion.java");
    assertTrue("Not3dHandleCriterion.java should have CMU BSD copyright header",
        content.contains("Carnegie Mellon University"));
  }

  @Test
  public void doubleInterruptibleAnimationHasCopyrightHeader() throws IOException {
    String content = readFile(SRC_DIR + "DoubleInterruptibleAnimation.java");
    assertTrue("DoubleInterruptibleAnimation.java should have CMU BSD copyright header",
        content.contains("Carnegie Mellon University"));
  }

  @Test
  public void color4fInterruptibleAnimationHasCopyrightHeader() throws IOException {
    String content = readFile(SRC_DIR + "Color4fInterruptibleAnimation.java");
    assertTrue("Color4fInterruptibleAnimation.java should have CMU BSD copyright header",
        content.contains("Carnegie Mellon University"));
  }

  // ===== DoubleInterruptibleAnimation extends DoubleAnimation =====

  @Test
  public void doubleInterruptibleAnimationExtendsDoubleAnimation() throws IOException {
    String content = readFile(SRC_DIR + "DoubleInterruptibleAnimation.java");
    assertTrue("DoubleInterruptibleAnimation should extend DoubleAnimation",
        content.contains("extends DoubleAnimation"));
  }

  // ===== Color4fInterruptibleAnimation extends Color4fAnimation =====

  @Test
  public void color4fInterruptibleAnimationExtendsColor4fAnimation() throws IOException {
    String content = readFile(SRC_DIR + "Color4fInterruptibleAnimation.java");
    assertTrue("Color4fInterruptibleAnimation should extend Color4fAnimation",
        content.contains("extends Color4fAnimation"));
  }

  // ===== Not3dHandleCriterion has accept method =====

  @Test
  public void not3dHandleCriterionHasAcceptMethod() throws IOException {
    String content = readFile(SRC_DIR + "Not3dHandleCriterion.java");
    assertTrue("Not3dHandleCriterion should have accept(Component) method",
        content.contains("boolean accept(Component"));
  }

  // ===== HandleGeometryHelper has no instance state =====

  @Test
  public void handleGeometryHelperHasPrivateConstructor() throws IOException {
    String content = readFile(SRC_DIR + "HandleGeometryHelper.java");
    assertTrue("HandleGeometryHelper should have a private constructor to prevent instantiation",
        content.contains("private HandleGeometryHelper()"));
  }

  // ===== Helper =====

  private static String readFile(String path) throws IOException {
    File file = new File(path);
    if (!file.exists()) {
      fail("File does not exist: " + path);
    }
    StringBuilder sb = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append('\n');
      }
    }
    return sb.toString();
  }
}

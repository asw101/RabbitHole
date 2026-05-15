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

package org.lgna.story.implementation;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Structural validation tests for the TransformAnimator inner-class extraction.
 * Verifies:
 *   - TransformAnimator.java is under 500 lines
 *   - Extracted classes exist as separate files
 *   - No remnant inner class declarations remain in TransformAnimator
 *   - The data.subject → data.getSubject() fix is applied
 */
public class TransformAnimatorExtractionTest {

  private static final String SRC_DIR = findSourceDir();

  private static String findSourceDir() {
    // Maven Surefire runs from module basedir (core/story-api/)
    File moduleLocal = new File("src/main/java/org/lgna/story/implementation/");
    if (moduleLocal.isDirectory()) {
      return moduleLocal.getPath() + File.separator;
    }
    // Fallback: running from project root
    File projectRoot = new File("core/story-api/src/main/java/org/lgna/story/implementation/");
    if (projectRoot.isDirectory()) {
      return projectRoot.getPath() + File.separator;
    }
    // Last resort: return the module-local path and let tests fail with a clear message
    return moduleLocal.getPath() + File.separator;
  }

  // ===== File existence =====

  @Test
  public void orientationDataFileExists() {
    File file = new File(SRC_DIR + "OrientationData.java");
    assertTrue("OrientationData.java should exist as a separate file", file.exists());
  }

  @Test
  public void smoothPositionAnimationsFileExists() {
    File file = new File(SRC_DIR + "SmoothPositionAnimations.java");
    assertTrue("SmoothPositionAnimations.java should exist as a separate file", file.exists());
  }

  @Test
  public void placeAnimationFileExists() {
    File file = new File(SRC_DIR + "PlaceAnimation.java");
    assertTrue("PlaceAnimation.java should exist as a separate file", file.exists());
  }

  // ===== TransformAnimator line count =====

  @Test
  public void transformAnimatorIsUnder500Lines() throws IOException {
    File file = new File(SRC_DIR + "TransformAnimator.java");
    assertTrue("TransformAnimator.java should exist", file.exists());

    int lineCount = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      while (reader.readLine() != null) {
        lineCount++;
      }
    }
    assertTrue("TransformAnimator.java should be under 500 lines, but was " + lineCount,
        lineCount < 500);
  }

  // ===== No remnant inner class declarations =====

  @Test
  public void transformAnimatorHasNoOrientationDataInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "TransformAnimator.java");
    assertFalse("TransformAnimator should not contain 'class OrientationData'",
        content.contains("class OrientationData"));
  }

  @Test
  public void transformAnimatorHasNoPreSetOrientationDataInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "TransformAnimator.java");
    assertFalse("TransformAnimator should not contain 'class PreSetOrientationData'",
        content.contains("class PreSetOrientationData"));
  }

  @Test
  public void transformAnimatorHasNoLocalOrientationDataInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "TransformAnimator.java");
    assertFalse("TransformAnimator should not contain 'class LocalOrientationData'",
        content.contains("class LocalOrientationData"));
  }

  @Test
  public void transformAnimatorHasNoTurnToFaceOrientationDataInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "TransformAnimator.java");
    assertFalse("TransformAnimator should not contain 'class TurnToFaceOrientationData'",
        content.contains("class TurnToFaceOrientationData"));
  }

  @Test
  public void transformAnimatorHasNoOrientToUprightDataInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "TransformAnimator.java");
    assertFalse("TransformAnimator should not contain 'class OrientToUprightData'",
        content.contains("class OrientToUprightData"));
  }

  @Test
  public void transformAnimatorHasNoOrientToPointAtDataInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "TransformAnimator.java");
    assertFalse("TransformAnimator should not contain 'class OrientToPointAtData'",
        content.contains("class OrientToPointAtData"));
  }

  @Test
  public void transformAnimatorHasNoSmoothAffineMatrix4x4AnimationInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "TransformAnimator.java");
    assertFalse("TransformAnimator should not contain 'class SmoothAffineMatrix4x4Animation'",
        content.contains("class SmoothAffineMatrix4x4Animation"));
  }

  @Test
  public void transformAnimatorHasNoSmoothPositionAnimationInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "TransformAnimator.java");
    assertFalse("TransformAnimator should not contain 'class SmoothPositionAnimation'",
        content.contains("class SmoothPositionAnimation"));
  }

  @Test
  public void transformAnimatorHasNoPlaceAnimationInnerClass() throws IOException {
    String content = readFile(SRC_DIR + "TransformAnimator.java");
    assertFalse("TransformAnimator should not contain 'class PlaceAnimation'",
        content.contains("class PlaceAnimation"));
  }

  // ===== data.subject → data.getSubject() fix =====

  @Test
  public void transformAnimatorDoesNotAccessDataSubjectDirectly() throws IOException {
    String content = readFile(SRC_DIR + "TransformAnimator.java");
    assertFalse("TransformAnimator should use data.getSubject() not data.subject",
        content.contains("data.subject"));
  }

  // ===== Extracted files have package declaration =====

  @Test
  public void orientationDataHasCorrectPackage() throws IOException {
    String content = readFile(SRC_DIR + "OrientationData.java");
    assertTrue("OrientationData.java should have correct package declaration",
        content.contains("package org.lgna.story.implementation;"));
  }

  @Test
  public void smoothPositionAnimationsHasCorrectPackage() throws IOException {
    String content = readFile(SRC_DIR + "SmoothPositionAnimations.java");
    assertTrue("SmoothPositionAnimations.java should have correct package declaration",
        content.contains("package org.lgna.story.implementation;"));
  }

  @Test
  public void placeAnimationHasCorrectPackage() throws IOException {
    String content = readFile(SRC_DIR + "PlaceAnimation.java");
    assertTrue("PlaceAnimation.java should have correct package declaration",
        content.contains("package org.lgna.story.implementation;"));
  }

  // ===== Extracted files are package-private (no public class) =====

  @Test
  public void orientationDataIsNotPublic() throws IOException {
    String content = readFile(SRC_DIR + "OrientationData.java");
    assertFalse("OrientationData class should be package-private, not public",
        content.contains("public class OrientationData") || content.contains("public abstract class OrientationData"));
  }

  @Test
  public void smoothPositionAnimationsClassesAreNotPublic() throws IOException {
    String content = readFile(SRC_DIR + "SmoothPositionAnimations.java");
    assertFalse("SmoothAffineMatrix4x4Animation should be package-private",
        content.contains("public class SmoothAffineMatrix4x4Animation") || content.contains("public abstract class SmoothAffineMatrix4x4Animation"));
    assertFalse("SmoothPositionAnimation should be package-private",
        content.contains("public class SmoothPositionAnimation"));
  }

  @Test
  public void placeAnimationIsNotPublic() throws IOException {
    String content = readFile(SRC_DIR + "PlaceAnimation.java");
    assertFalse("PlaceAnimation should be package-private, not public",
        content.contains("public class PlaceAnimation"));
  }

  // ===== Copyright header preserved =====

  @Test
  public void orientationDataHasCopyrightHeader() throws IOException {
    String content = readFile(SRC_DIR + "OrientationData.java");
    assertTrue("OrientationData.java should have CMU BSD copyright header",
        content.contains("Carnegie Mellon University"));
  }

  @Test
  public void smoothPositionAnimationsHasCopyrightHeader() throws IOException {
    String content = readFile(SRC_DIR + "SmoothPositionAnimations.java");
    assertTrue("SmoothPositionAnimations.java should have CMU BSD copyright header",
        content.contains("Carnegie Mellon University"));
  }

  @Test
  public void placeAnimationHasCopyrightHeader() throws IOException {
    String content = readFile(SRC_DIR + "PlaceAnimation.java");
    assertTrue("PlaceAnimation.java should have CMU BSD copyright header",
        content.contains("Carnegie Mellon University"));
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

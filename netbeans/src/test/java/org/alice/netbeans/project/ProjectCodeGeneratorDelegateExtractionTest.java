/*******************************************************************************
 * Copyright (c) 2006, 2016, Carnegie Mellon University. All rights reserved.
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

package org.alice.netbeans.project;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;

import static org.junit.Assert.*;

/**
 * Characterization tests for the ProjectCodeGenerator delegate extraction.
 * Verifies that the public API surface and generated launcher content
 * are identical after extracting LauncherTemplate, GenerationValidator,
 * and CodeFormatter into package-private delegates.
 */
public class ProjectCodeGeneratorDelegateExtractionTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void generateLauncherProducesExpectedFileName() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    File launcherFile = new File(sourceDir, "AliceJavaFXLauncher.java");
    assertTrue("Launcher file should exist", launcherFile.exists());
  }

  @Test
  public void generatedLauncherContainsClassName() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    String content = Files.readString(new File(sourceDir, "AliceJavaFXLauncher.java").toPath());
    assertTrue("Should contain class declaration",
        content.contains("public class AliceJavaFXLauncher extends Application"));
  }

  @Test
  public void generatedLauncherContainsEvidencePrefix() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    String content = Files.readString(new File(sourceDir, "AliceJavaFXLauncher.java").toPath());
    assertTrue("Should contain EVIDENCE_PREFIX",
        content.contains("ALICE_LAUNCHER_EVIDENCE"));
  }

  @Test
  public void generatedLauncherContainsNoGoPrefix() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    String content = Files.readString(new File(sourceDir, "AliceJavaFXLauncher.java").toPath());
    assertTrue("Should contain NO_GO_PREFIX",
        content.contains("ALICE_LAUNCHER_NO_GO"));
  }

  @Test
  public void generatedLauncherContainsRenderObservationPrefix() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    String content = Files.readString(new File(sourceDir, "AliceJavaFXLauncher.java").toPath());
    assertTrue("Should contain RENDER_OBSERVATION_PREFIX",
        content.contains("ALICE_LAUNCHER_RENDER_OBSERVATION"));
  }

  @Test
  public void generatedLauncherContainsMainMethod() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    String content = Files.readString(new File(sourceDir, "AliceJavaFXLauncher.java").toPath());
    assertTrue("Should contain main method",
        content.contains("public static void main(final String[] args)"));
  }

  @Test
  public void generatedLauncherContainsProgramMainDelegation() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    String content = Files.readString(new File(sourceDir, "AliceJavaFXLauncher.java").toPath());
    assertTrue("Should delegate to Program.main",
        content.contains("Program.main(startingArgs)"));
  }

  @Test
  public void generatedLauncherContainsObservationMarkerColor() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    String content = Files.readString(new File(sourceDir, "AliceJavaFXLauncher.java").toPath());
    assertTrue("Should contain marker color rgb(32,96,160)",
        content.contains("Color.rgb(32, 96, 160)"));
  }

  @Test
  public void generatedLauncherContainsPixelObservationInnerClass() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    String content = Files.readString(new File(sourceDir, "AliceJavaFXLauncher.java").toPath());
    assertTrue("Should contain PixelObservation inner class",
        content.contains("private static final class PixelObservation"));
  }

  @Test
  public void generatedLauncherContainsJsonEscaping() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    String content = Files.readString(new File(sourceDir, "AliceJavaFXLauncher.java").toPath());
    assertTrue("Should contain escapeJson method",
        content.contains("private static String escapeJson(String value)"));
  }

  @Test
  public void generatedLauncherContainsSchemaVersion() throws Exception {
    File sourceDir = temporaryFolder.newFolder("src");
    ProjectCodeGenerator.generateLauncher(sourceDir);
    String content = Files.readString(new File(sourceDir, "AliceJavaFXLauncher.java").toPath());
    assertTrue("Should contain schema version",
        content.contains("alice.launcher.render-observation/v1"));
  }

  @Test
  public void publicApiGenerateCodeMethodExists() throws Exception {
    Method method = ProjectCodeGenerator.class.getMethod(
        "generateCode", File.class, File.class,
        org.netbeans.api.progress.ProgressHandle.class);
    assertTrue("generateCode should be public",
        Modifier.isPublic(method.getModifiers()));
    assertTrue("generateCode should be static",
        Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void launcherTemplateFileNameConstantMatchesExpected() {
    assertEquals("AliceJavaFXLauncher.java", LauncherTemplate.FILE_NAME);
  }

  @Test
  public void launcherTemplateContentIsNonEmpty() {
    assertNotNull("FILE_CONTENT should not be null", LauncherTemplate.FILE_CONTENT);
    assertFalse("FILE_CONTENT should not be empty", LauncherTemplate.FILE_CONTENT.isEmpty());
  }

  @Test
  public void launcherTemplateDelegateProducesSameResultAsProjectCodeGenerator() throws Exception {
    File sourceDir1 = temporaryFolder.newFolder("src1");
    File sourceDir2 = temporaryFolder.newFolder("src2");

    ProjectCodeGenerator.generateLauncher(sourceDir1);
    LauncherTemplate.generate(sourceDir2);

    String fromProjectCodeGenerator = Files.readString(new File(sourceDir1, "AliceJavaFXLauncher.java").toPath());
    String fromLauncherTemplate = Files.readString(new File(sourceDir2, "AliceJavaFXLauncher.java").toPath());

    assertEquals("Delegate should produce identical content", fromProjectCodeGenerator, fromLauncherTemplate);
  }
}

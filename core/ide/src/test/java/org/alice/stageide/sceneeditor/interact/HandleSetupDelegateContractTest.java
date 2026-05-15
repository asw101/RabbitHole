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
package org.alice.stageide.sceneeditor.interact;

import org.alice.interact.DragAdapter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * TDD contract tests for the HandleSetupDelegate extraction (issue #685).
 *
 * Verifies:
 *   - HandleSetupDelegate class exists and is package-private
 *   - setupHandles is a static method accepting DragAdapter
 *   - GlobalDragAdapter line count is under 500
 *   - Unused Color4f and Resizer imports removed from GlobalDragAdapter
 *   - Private setupHandles() method removed from GlobalDragAdapter
 *   - GlobalDragAdapter delegates to HandleSetupDelegate.setupHandles(this)
 *   - HandleSetupDelegate has correct copyright header
 *
 * Pure reflection + source analysis — no GUI, no singleton instantiation.
 * These tests FAIL until the extraction is implemented.
 */
public class HandleSetupDelegateContractTest {

  private static final String INTERACT_PKG = "org.alice.stageide.sceneeditor.interact";
  private static final String DELEGATE_FQCN = INTERACT_PKG + ".HandleSetupDelegate";
  private static final String GDA_FQCN = INTERACT_PKG + ".GlobalDragAdapter";

  private static final Path SRC_DIR = findSourceDir();
  private static final Path GDA_SRC = SRC_DIR.resolve("GlobalDragAdapter.java");
  private static final Path DELEGATE_SRC = SRC_DIR.resolve("HandleSetupDelegate.java");

  private static Class<?> delegateClazz;
  private static Class<?> gdaClazz;

  @BeforeClass
  public static void loadClasses() {
    try {
      delegateClazz = Class.forName(DELEGATE_FQCN);
    } catch (ClassNotFoundException e) {
      fail("HandleSetupDelegate class not found — extraction not yet implemented: " + e.getMessage());
    }
    try {
      gdaClazz = Class.forName(GDA_FQCN);
    } catch (ClassNotFoundException e) {
      fail("GlobalDragAdapter class not found: " + e.getMessage());
    }
  }

  // ── HandleSetupDelegate file existence ──────────────────────────

  @Test
  public void delegateFileExists() {
    assertTrue("HandleSetupDelegate.java must exist at " + DELEGATE_SRC,
        Files.exists(DELEGATE_SRC));
  }

  // ── HandleSetupDelegate is package-private ──────────────────────

  @Test
  public void delegateClassIsPackagePrivate() {
    int modifiers = delegateClazz.getModifiers();
    assertFalse("HandleSetupDelegate must not be public",
        Modifier.isPublic(modifiers));
    assertFalse("HandleSetupDelegate must not be protected",
        Modifier.isProtected(modifiers));
    assertFalse("HandleSetupDelegate must not be private",
        Modifier.isPrivate(modifiers));
  }

  // ── setupHandles method structure ───────────────────────────────

  @Test
  public void setupHandlesMethodExists() {
    Method method = findSetupHandlesMethod();
    assertNotNull("HandleSetupDelegate must have a setupHandles method accepting DragAdapter",
        method);
  }

  @Test
  public void setupHandlesMethodIsStatic() {
    Method method = findSetupHandlesMethod();
    assertNotNull("setupHandles method not found", method);
    assertTrue("setupHandles must be static",
        Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void setupHandlesMethodAcceptsDragAdapter() {
    Method method = findSetupHandlesMethod();
    assertNotNull("setupHandles method not found", method);
    Class<?>[] params = method.getParameterTypes();
    assertEquals("setupHandles must accept exactly one parameter", 1, params.length);
    assertEquals("setupHandles parameter must be DragAdapter",
        DragAdapter.class, params[0]);
  }

  @Test
  public void setupHandlesMethodReturnsVoid() {
    Method method = findSetupHandlesMethod();
    assertNotNull("setupHandles method not found", method);
    assertEquals("setupHandles must return void",
        void.class, method.getReturnType());
  }

  // ── GlobalDragAdapter line count ────────────────────────────────

  @Test
  public void globalDragAdapterLineCount_under500() throws IOException {
    assertTrue("GlobalDragAdapter source not found at: " + GDA_SRC,
        Files.exists(GDA_SRC));
    long lineCount = Files.lines(GDA_SRC).count();
    assertTrue("GlobalDragAdapter must be under 500 lines, found " + lineCount,
        lineCount < 500);
  }

  // ── Private setupHandles removed from GlobalDragAdapter ─────────

  @Test
  public void gdaNoPrivateSetupHandlesMethod() {
    Method[] methods = gdaClazz.getDeclaredMethods();
    for (Method m : methods) {
      if ("setupHandles".equals(m.getName()) && m.getParameterCount() == 0) {
        fail("GlobalDragAdapter must not have a private setupHandles() method — " +
             "it should be extracted to HandleSetupDelegate");
      }
    }
  }

  // ── Unused imports removed from GlobalDragAdapter ───────────────

  @Test
  public void gdaNoColor4fImport() throws IOException {
    Set<String> imports = readImports(GDA_SRC);
    assertFalse("GlobalDragAdapter should not import Color4f after extraction",
        imports.stream().anyMatch(i -> i.contains("Color4f")));
  }

  @Test
  public void gdaNoResizerImport() throws IOException {
    Set<String> imports = readImports(GDA_SRC);
    assertFalse("GlobalDragAdapter should not import Resizer after extraction",
        imports.stream().anyMatch(i -> i.contains("Resizer")));
  }

  // ── GlobalDragAdapter delegates to HandleSetupDelegate ──────────

  @Test
  public void gdaSourceContainsDelegateCall() throws IOException {
    assertTrue("GlobalDragAdapter source not found", Files.exists(GDA_SRC));
    String source = new String(Files.readAllBytes(GDA_SRC));
    assertTrue("GlobalDragAdapter must call HandleSetupDelegate.setupHandles(this)",
        source.contains("HandleSetupDelegate.setupHandles(this)"));
  }

  // ── HandleSetupDelegate has copyright header ────────────────────

  @Test
  public void delegateHasCopyrightHeader() throws IOException {
    assertTrue("HandleSetupDelegate source not found", Files.exists(DELEGATE_SRC));
    List<String> lines = Files.readAllLines(DELEGATE_SRC);
    assertFalse("HandleSetupDelegate must not be empty", lines.isEmpty());
    assertTrue("HandleSetupDelegate must start with copyright header",
        lines.get(0).contains("Copyright") || lines.get(0).contains("/***"));
  }

  // ── HandleSetupDelegate is in the correct package ───────────────

  @Test
  public void delegateIsInInteractPackage() {
    assertEquals("HandleSetupDelegate must be in the interact package",
        INTERACT_PKG, delegateClazz.getPackage().getName());
  }

  // ── HandleSetupDelegate source contains Color4f import ──────────

  @Test
  public void delegateImportsColor4f() throws IOException {
    Set<String> imports = readImports(DELEGATE_SRC);
    assertTrue("HandleSetupDelegate must import Color4f (used for handle colors)",
        imports.stream().anyMatch(i -> i.contains("Color4f")));
  }

  // ── HandleSetupDelegate source contains Resizer import ──────────

  @Test
  public void delegateImportsResizer() throws IOException {
    Set<String> imports = readImports(DELEGATE_SRC);
    assertTrue("HandleSetupDelegate must import Resizer (used for scale handles)",
        imports.stream().anyMatch(i -> i.contains("Resizer")));
  }

  // ── Helpers ─────────────────────────────────────────────────────

  private Method findSetupHandlesMethod() {
    try {
      return delegateClazz.getDeclaredMethod("setupHandles", DragAdapter.class);
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

  private static Set<String> readImports(Path file) throws IOException {
    return Files.lines(file)
        .filter(line -> line.startsWith("import "))
        .collect(Collectors.toSet());
  }

  private static Path findSourceDir() {
    Path candidate = Paths.get(
        "core/ide/src/main/java/org/alice/stageide/sceneeditor/interact");
    if (Files.isDirectory(candidate)) {
      return candidate;
    }
    candidate = Paths.get(System.getProperty("user.dir"))
        .resolve("core/ide/src/main/java/org/alice/stageide/sceneeditor/interact");
    return candidate;
  }
}

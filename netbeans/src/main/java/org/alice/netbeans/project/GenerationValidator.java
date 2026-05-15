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

import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.AbstractPackage;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.resource.ResourcesTypeWrapper;

import javax.lang.model.SourceVersion;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates generated file destinations and Java identifiers.
 * Extracted from {@link ProjectCodeGenerator} to reduce file size.
 */
final class GenerationValidator {

  static void ensureDestinationFilesAreAvailable(
      File sourceRootDirectory,
      Path sourceRoot,
      Set<NamedUserType> namedUserTypes,
      Set<org.lgna.common.Resource> resources,
      ResourcesTypeWrapper resourcesTypeWrapper) throws IOException {
    Set<String> generatedSourceNames = new HashSet<>();
    Set<Path> generatedOutputPaths = new HashSet<>();

    generatedSourceNames.add(LauncherTemplate.FILE_NAME);
    addGeneratedOutputPath(generatedOutputPaths, new File(sourceRootDirectory, LauncherTemplate.FILE_NAME), sourceRoot);

    if (resourcesTypeWrapper != null) {
      for (org.lgna.common.Resource resource : resources) {
        addGeneratedOutputPath(
            generatedOutputPaths,
            new File(sourceRootDirectory, resourcesTypeWrapper.getResourcePathForResource(resource)),
            sourceRoot);
      }
    }

    for (NamedUserType type : namedUserTypes) {
      File file = getJavaSourceFileForType(sourceRootDirectory, sourceRoot, type);
      if (!generatedSourceNames.add(file.getName())) {
        throw new IOException("Duplicate generated Java source file: " + file.getName());
      }
      validateUserAuthoredJavaIdentifiers(type);
      addGeneratedOutputPath(generatedOutputPaths, file, sourceRoot);
    }

    for (Path generatedOutputPath : generatedOutputPaths) {
      if (generatedOutputPath.toFile().exists()) {
        throw new IOException("Generated destination already exists: " + generatedOutputPath);
      }
    }
  }

  static File getJavaSourceFileForType(File sourceRootDirectory, Path sourceRoot, NamedUserType type) throws IOException {
    String typeName = type.getName();
    validateJavaIdentifier(typeName, "Unsafe Alice type name for Java source generation");

    File file = new File(sourceRootDirectory, typeName + ".java").getCanonicalFile();
    if (!file.toPath().startsWith(sourceRoot)) {
      throw new IOException("Generated Java source path escapes source directory: " + file);
    }
    return file;
  }

  private static void addGeneratedOutputPath(Set<Path> generatedOutputPaths, File file, Path sourceRoot) throws IOException {
    Path generatedOutputPath = file.getCanonicalFile().toPath();
    if (!generatedOutputPath.startsWith(sourceRoot)) {
      throw new IOException("Generated output path escapes source directory: " + generatedOutputPath);
    }
    if (!generatedOutputPaths.add(generatedOutputPath)) {
      throw new IOException("Duplicate generated output file: " + sourceRoot.relativize(generatedOutputPath));
    }
  }

  private static void validateUserAuthoredJavaIdentifiers(NamedUserType type) throws IOException {
    for (AbstractDeclaration declaration : type.createDeclarationSet()) {
      if (declaration.isUserAuthored()
          && !(declaration instanceof AbstractPackage)
          && (declaration.getNamePropertyIfItExists() != null)
          && (declaration.getName() != null)) {
        validateJavaIdentifier(
            declaration.getName(),
            "Unsafe Alice declaration name for Java source generation");
      }
    }
  }

  private static void validateJavaIdentifier(String identifier, String description) throws IOException {
    if ((identifier == null) || !SourceVersion.isIdentifier(identifier) || SourceVersion.isKeyword(identifier)) {
      throw new IOException(description + ": " + identifier);
    }
  }

  private GenerationValidator() {
  }
}

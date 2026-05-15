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

import edu.cmu.cs.dennisc.java.io.TextFileUtilities;
import org.lgna.project.Project;
import org.lgna.project.VersionNotSupportedException;
import org.lgna.project.ast.JavaCodeGenerator;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.io.IoUtilities;
import org.lgna.project.resource.ResourcesTypeWrapper;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;
import org.lgna.story.ast.JavaCodeUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Generates Java source code from an Alice project file.
 *
 * <p>Delegates to package-private helpers:
 * <ul>
 *   <li>{@link LauncherTemplate} – JavaFX launcher template and generation</li>
 *   <li>{@link GenerationValidator} – destination/identifier validation</li>
 *   <li>{@link CodeFormatter} – NetBeans source formatting</li>
 * </ul>
 *
 * @author Dennis Cosgrove
 */
public class ProjectCodeGenerator {

  static void progress(ProgressHandle progressHandle, String prefix, FileObject fileObject, int workUnit) {
    if (progressHandle != null) {
      progressHandle.progress(prefix + fileObject.getNameExt(), workUnit);
    }
  }

  public static Collection<FileObject> generateCode(File aliceProjectFile, File javaSrcDirectory, ProgressHandle progressHandle) throws IOException, VersionNotSupportedException {
    return generateCode(aliceProjectFile, javaSrcDirectory, progressHandle, true);
  }

  static Collection<FileObject> generateCode(
      File aliceProjectFile,
      File javaSrcDirectory,
      ProgressHandle progressHandle,
      boolean formatGeneratedFiles) throws IOException, VersionNotSupportedException {
    Project aliceProject = IoUtilities.readProject(aliceProjectFile);
    JavaCodeGenerator.Builder javaCodeGeneratorBuilder = JavaCodeUtilities.createJavaCodeGeneratorBuilder();

    Set<NamedUserType> namedUserTypes = aliceProject.getNamedUserTypes();
    final Set<org.lgna.common.Resource> resources = aliceProject.getResources();
    File sourceRootDirectory = javaSrcDirectory.getCanonicalFile();
    Path sourceRoot = sourceRootDirectory.toPath();
    ResourcesTypeWrapper resourcesTypeWrapper = null;
    if (!resources.isEmpty()) {
      resourcesTypeWrapper = new ResourcesTypeWrapper(resources);
      namedUserTypes.add(resourcesTypeWrapper.getType());
    }
    List<FileObject> filesToOpen = new ArrayList<>(namedUserTypes.size() + 1);
    List<FileObject> fileObjectsToFormat = new ArrayList<>(namedUserTypes.size());

    GenerationValidator.ensureDestinationFilesAreAvailable(sourceRootDirectory, sourceRoot, namedUserTypes, resources, resourcesTypeWrapper);

    if (!resources.isEmpty()) {
      writeResources(javaSrcDirectory, resources, resourcesTypeWrapper);
    }

    if (progressHandle != null) {
      progressHandle.switchToDeterminate(namedUserTypes.size());
    }
    int createWorkUnit = 0;
    for (NamedUserType type : namedUserTypes) {
      File file = GenerationValidator.getJavaSourceFileForType(sourceRootDirectory, sourceRoot, type);
      final NetbeansJavaCodeGenerator generator = new NetbeansJavaCodeGenerator(javaCodeGeneratorBuilder);
      type.process(generator);
      String code = generator.getText();
      boolean isMarkedForOpen = shouldOpenType(type);

      TextFileUtilities.write(file, code);
      FileObject fileObject = FileUtil.toFileObject(file);
      fileObjectsToFormat.add(fileObject);

      if (isMarkedForOpen) {
        filesToOpen.add(fileObject);
      }
      progress(progressHandle, "create: ", fileObject, createWorkUnit);
      createWorkUnit++;
    }

    FileObject fileObject = generateLauncher(javaSrcDirectory);
    filesToOpen.add(fileObject);
    progress(progressHandle, "create: ", fileObject, createWorkUnit);

    if (formatGeneratedFiles) {
      CodeFormatter.formatGeneratedFiles(fileObjectsToFormat, progressHandle);
    }
    return filesToOpen;
  }

  static FileObject generateLauncher(File javaSrcDirectory) {
    return LauncherTemplate.generate(javaSrcDirectory);
  }

  private static boolean shouldOpenType(NamedUserType type) {
    if (type.isAssignableTo(SProgram.class)) {
      return false;
    }
    if (type.isAssignableTo(SScene.class)) {
      return true;
    }
    for (UserMethod method : type.methods) {
      if (method.managementLevel.getValue() == ManagementLevel.NONE) {
        return true;
      }
    }
    return false;
  }

  private static void writeResources(
      File javaSrcDirectory,
      Set<org.lgna.common.Resource> resources,
      ResourcesTypeWrapper resourcesTypeWrapper) throws IOException {
    FileObject javaSrcDirectoryFileObject = FileUtil.toFileObject(javaSrcDirectory);
    if (javaSrcDirectoryFileObject == null || !javaSrcDirectoryFileObject.isFolder()) {
      throw new IOException("Java source directory is not available: " + javaSrcDirectory);
    }
    for (org.lgna.common.Resource resource : resources) {
      final String dstPath = resourcesTypeWrapper.getResourcePathForResource(resource);
      FileObject f = FileUtil.createData(javaSrcDirectoryFileObject, dstPath);
      if (f == null) {
        throw new IOException("Unable to create resource file: " + dstPath);
      }

      FileLock lock = f.lock();
      try {
        try (OutputStream os = f.getOutputStream(lock)) {
          os.write(resource.getData());
        }
      } finally {
        lock.releaseLock();
      }
    }
  }
}

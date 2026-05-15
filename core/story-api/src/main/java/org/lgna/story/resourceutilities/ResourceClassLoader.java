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
package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.java.io.FileUtilities;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import org.lgna.story.implementation.alice.AliceResourceClassUtilities;
import org.lgna.story.resources.ModelResource;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Static utility for discovering and loading {@link ModelResource} classes
 * from jar files and directory trees. Extracted from
 * {@link StorytellingResources} to separate class-loading concerns from
 * gallery management.
 */
public final class ResourceClassLoader {

  private ResourceClassLoader() {
  }

  /**
   * Value class bundling loaded classes with the {@link URLClassLoader}s
   * created during loading. Callers retain the classloaders for subsequent
   * resource lookups.
   */
  public static final class LoadResult {
    private final List<Class<? extends ModelResource>> classes;
    private final List<URLClassLoader> classLoaders;

    LoadResult(List<Class<? extends ModelResource>> classes,
               List<URLClassLoader> classLoaders) {
      this.classes = classes;
      this.classLoaders = classLoaders;
    }

    public List<Class<? extends ModelResource>> classes() {
      return classes;
    }

    public List<URLClassLoader> classLoaders() {
      return classLoaders;
    }

    public boolean isEmpty() {
      return classes.isEmpty();
    }
  }

  static String getAliceResourceClassName(String resourcePath) {
    String className = resourcePath.replace('/', '.');
    className = className.replace('\\', '.');
    int lastDot = className.lastIndexOf(".");
    String baseName = className.substring(0, lastDot);
    if (baseName.startsWith(".")) {
      baseName = baseName.substring(1);
    }
    baseName += AliceResourceClassUtilities.RESOURCE_SUFFIX;
    return baseName;
  }

  public static Map<File, List<String>> getClassNamesFromResources(File... resourceFiles) {
    HashMap<File, List<String>> rv = new HashMap<>();
    for (File resourceFile : resourceFiles) {
      try {
        if (resourceFile.isDirectory()) {
          File[] xmlFiles = FileUtilities.listDescendants(resourceFile, "xml");
          for (File xmlFile : xmlFiles) {
            if (!xmlFile.getName().contains("$")) {
              String relativePath = xmlFile.getAbsolutePath().substring(resourceFile.getAbsolutePath().length());
              String baseName = getAliceResourceClassName(relativePath);
              rv.computeIfAbsent(resourceFile, k -> new LinkedList<>()).add(baseName);
            }
          }
        } else {
          ZipFile zip = new ZipFile(resourceFile);
          Enumeration<? extends ZipEntry> entries = zip.entries();
          while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".xml") && !entry.getName().contains("$")) {
              String baseName = getAliceResourceClassName(entry.getName());
              rv.computeIfAbsent(resourceFile, k -> new LinkedList<>()).add(baseName);
            }
          }
        }
      } catch (Exception e) {
        Logger.severe("Error reading resource file: " + resourceFile, e);
      }
    }
    return rv;
  }

  public static LoadResult loadClassesFromResourceFiles(List<String> classNames, File... resourceFiles) {
    List<Class<? extends ModelResource>> classes = new LinkedList<>();
    List<URLClassLoader> classLoaders = new ArrayList<>();
    try {
      URL[] urlArray = new URL[resourceFiles.length];
      for (int i = 0; i < resourceFiles.length; i++) {
        urlArray[i] = resourceFiles[i].toURI().toURL();
      }
      URLClassLoader cl = new URLClassLoader(urlArray, ClassLoader.getSystemClassLoader());
      for (String className : classNames) {
        try {
          Class<?> cls = Class.forName(className);
          if (ModelResource.class.isAssignableFrom(cls)) {
            classes.add((Class<? extends ModelResource>) cls);
          }
        } catch (Throwable cnfe) {
          try {
            Class<?> cls = ClassLoader.getSystemClassLoader().loadClass(className);
            if (ModelResource.class.isAssignableFrom(cls)) {
              classes.add((Class<? extends ModelResource>) cls);
            }
          } catch (ClassNotFoundException cnfe2) {
            Logger.severe("FAILED TO LOAD GALLERY CLASS: " + className);
          }
        }
      }
      classLoaders.add(cl);
    } catch (Exception e) {
      Logger.severe("Error loading resource files", e);
    }
    return new LoadResult(classes, classLoaders);
  }

  public static LoadResult getAndLoadModelResourceClasses(List<File> resourcePaths) {
    List<File> resourceFiles = new ArrayList<>();
    for (File modelPath : resourcePaths) {
      if (modelPath.exists()) {
        if (modelPath.isDirectory()) {
          Collections.addAll(resourceFiles, FileUtilities.listFiles(modelPath, "jar"));
          Collections.addAll(resourceFiles, FileUtilities.listDirectories(modelPath));
        } else {
          resourceFiles.add(modelPath);
        }
      }
    }
    if (resourceFiles.isEmpty()) {
      return new LoadResult(new LinkedList<>(), new ArrayList<>());
    }
    File[] resourceFileArray = resourceFiles.toArray(new File[0]);
    List<String> classNames = getClassNamesFromResourceFiles(resourceFileArray);
    return loadClassesFromResourceFiles(classNames, resourceFileArray);
  }

  static List<String> getClassNamesFromResourceFiles(File... resourceFiles) {
    List<String> classNames = new LinkedList<>();
    Map<File, List<String>> classNameMap = getClassNamesFromResources(resourceFiles);
    for (Map.Entry<File, List<String>> entry : classNameMap.entrySet()) {
      classNames.addAll(entry.getValue());
    }
    return classNames;
  }
}

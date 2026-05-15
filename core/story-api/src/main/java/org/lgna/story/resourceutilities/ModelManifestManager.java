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
import org.alice.tweedle.file.ManifestEncoderDecoder;
import org.alice.tweedle.file.ModelManifest;
import org.lgna.story.implementation.StoryApiDirectoryUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages lazy-loaded caches of {@link ModelManifest} instances parsed from
 * JSON model files in the user gallery and internal model directories.
 * Extracted from {@link StorytellingResources} to separate manifest
 * management from class loading and gallery path resolution.
 */
public final class ModelManifestManager {

  private List<ModelManifest> userGalleryModelManifests = null;
  private List<ModelManifest> internalModelManifests = null;

  public List<File> getDynamicModelFiles(File... directoriesToSearch) {
    List<File> dynamicModelFiles = new ArrayList<>();
    for (File directory : directoriesToSearch) {
      if (directory.isDirectory()) {
        File[] modelFiles = FileUtilities.listDescendants(directory, "json");
        dynamicModelFiles.addAll(Arrays.asList(modelFiles));
      }
    }
    return dynamicModelFiles;
  }

  public List<ModelManifest> findAndLoadUserGalleryResources() {
    if (this.userGalleryModelManifests == null) {
      this.userGalleryModelManifests = new ArrayList<>();
      File userGalleryDirectory = StoryApiDirectoryUtilities.getUserGalleryDirectory();
      List<File> dynamicModelFiles = getDynamicModelFiles(userGalleryDirectory);
      for (File modelFile : dynamicModelFiles) {
        ModelManifest modelManifest = manifestFor(modelFile);
        if (modelManifest != null) {
          this.userGalleryModelManifests.add(modelManifest);
        }
      }
    }
    return this.userGalleryModelManifests;
  }

  public List<ModelManifest> findAndLoadInternalResources() {
    if (internalModelManifests == null) {
      internalModelManifests = new ArrayList<>();
      File internalModelsDirectory = StoryApiDirectoryUtilities.getInternalModelsDirectory();
      List<File> dynamicModelFiles = getDynamicModelFiles(internalModelsDirectory);
      for (File modelFile : dynamicModelFiles) {
        ModelManifest modelManifest = manifestFor(modelFile);
        if (modelManifest != null) {
          internalModelManifests.add(modelManifest);
        }
      }
    }
    return internalModelManifests;
  }

  ModelManifest manifestFor(File modelFile) {
    try {
      String fileContent = new String(Files.readAllBytes(Path.of(modelFile.toURI())));
      ModelManifest modelManifest = ManifestEncoderDecoder.fromJson(fileContent, ModelManifest.class);
      if (modelManifest != null) {
        modelManifest.setRootFile(modelFile.getParentFile());
      }
      return modelManifest;
    } catch (IOException e) {
      Logger.warning("Error loading model data from " + modelFile);
      return null;
    }
  }

  public List<ModelManifest> findNewUserGalleryResources() {
    if (userGalleryModelManifests != null) {
      List<ModelManifest> newModelManifests = new ArrayList<>();
      File userGalleryDirectory = StoryApiDirectoryUtilities.getUserGalleryDirectory();
      List<File> dynamicModelFiles = getDynamicModelFiles(userGalleryDirectory);
      for (File modelFile : dynamicModelFiles) {
        ModelManifest modelManifest = manifestFor(modelFile);
        if (modelManifest != null && manifestIsNew(modelManifest)) {
          userGalleryModelManifests.add(modelManifest);
          newModelManifests.add(modelManifest);
        }
      }
      return newModelManifests;
    }
    return null;
  }

  private boolean manifestIsNew(ModelManifest newManifest) {
    for (ModelManifest oldManifest : userGalleryModelManifests) {
      if (oldManifest.getName().equals(newManifest.getName())) {
        return false;
      }
    }
    return true;
  }

  public ModelManifest getModelManifest(String modelName) {
    findAndLoadUserGalleryResources();
    for (ModelManifest modelManifest : this.userGalleryModelManifests) {
      if (modelManifest.getName().equals(modelName)) {
        return modelManifest;
      }
    }
    return null;
  }

  public ModelManifest getInternalModelManifest(String modelName) {
    findAndLoadInternalResources();
    for (ModelManifest modelManifest : internalModelManifests) {
      if (modelManifest.getName().equals(modelName)) {
        return modelManifest;
      }
    }
    return null;
  }
}

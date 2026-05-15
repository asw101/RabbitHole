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

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.xml.XMLUtilities;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.tweedle.file.Manifest;
import org.alice.tweedle.file.ModelManifest;
import org.alice.tweedle.file.StructureReference;
import org.lgna.project.ProjectVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.time.Year;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dculyba
 *
 */
public class ModelResourceInfo {
  private final AxisAlignedBox boundingBox;
  private final int creationYear;
  private final String creator;
  private final String resourceName;
  private final String modelName;
  private final String textureName;
  private final boolean isDeprecated;
  private final Boolean placeOnGround;
  private ModelResourceInfo parentInfo = null;
  private final List<ModelResourceInfo> subResources = new LinkedList<>();

  private final String[] tags;
  private final String[] groupTags;
  private final String[] themeTags;

  public ModelResourceInfo(ModelResourceInfo parent, String resourceName, String creator, int creationYear, AxisAlignedBox boundingBox, String[] tags, String[] groupTags, String[] themeTags, String modelName, String textureName, boolean isDeprecated, boolean placeOnGround) {
    this(parent, resourceName, creator, creationYear, boundingBox, tags, groupTags, themeTags, modelName, textureName, isDeprecated, Boolean.valueOf(placeOnGround));
  }

  ModelResourceInfo(ModelResourceInfo parent, String resourceName, String creator, int creationYear, AxisAlignedBox boundingBox, String[] tags, String[] groupTags, String[] themeTags, String modelName, String textureName, boolean isDeprecated, Boolean placeOnGround) {
    this.parentInfo = parent;
    this.resourceName = resourceName;
    this.creator = creator;
    this.creationYear = creationYear;
    this.boundingBox = boundingBox;
    this.tags = tags;
    this.textureName = textureName;
    this.modelName = modelName;
    this.groupTags = groupTags;
    this.themeTags = themeTags;
    this.isDeprecated = isDeprecated;
    this.placeOnGround = placeOnGround;
  }

  public ModelResourceInfo createShallowCopy() {
    return new ModelResourceInfo(null, resourceName, creator, creationYear, boundingBox, tags, groupTags, themeTags, modelName, textureName, isDeprecated, placeOnGround);
  }

  public ModelResourceInfo(Document xmlDoc) {
    Element modelElement = xmlDoc.getDocumentElement();
    if (!modelElement.getNodeName().equals("AliceModel")) {
      modelElement = XMLUtilities.getSingleChildElementByTagName(xmlDoc.getDocumentElement(), "AliceModel");
    }
    assert modelElement != null;
    List<Element> bboxNodeList = ModelResourceXmlParser.getImmediateChildElementsByTagName(modelElement, "BoundingBox");
    this.boundingBox = bboxNodeList.isEmpty()
        ? AxisAlignedBox.Empty
        : ModelResourceXmlParser.getBoundingBoxFromXML(bboxNodeList.getFirst());
    this.modelName = modelElement.getAttribute("name");
    this.creator = modelElement.getAttribute("creator");
    int creationYearTemp = -1;
    try {
      creationYearTemp = Integer.parseInt(modelElement.getAttribute("creationYear"));
    } catch (NumberFormatException ignored) {
    }
    this.creationYear = creationYearTemp;

    this.isDeprecated = Boolean.parseBoolean(modelElement.getAttribute("deprecated"));

    this.placeOnGround = Boolean.parseBoolean(modelElement.getAttribute("placeOnGround"));

    this.tags = ModelResourceXmlParser.getResourceTags(modelElement, "Tags", "Tag");
    this.groupTags = ModelResourceXmlParser.getResourceTags(modelElement, "GroupTags", "GroupTag");
    this.themeTags = ModelResourceXmlParser.getResourceTags(modelElement, "ThemeTags", "ThemeTag");

    List<Element> subResourcesList = ModelResourceXmlParser.getImmediateChildElementsByTagName(modelElement, "Resource");
    for (Element subResourceElement : subResourcesList) {
      ModelResourceInfo subResource = ModelResourceXmlParser.getSubResourceFromXML(subResourceElement, this);
      if (subResource != null) {
        subResources.add(subResource);
      } else {
        Logger.severe("Failed to make sub resource for " + subResourceElement);
      }
    }
    this.textureName = null;
    this.resourceName = null;
    this.parentInfo = null;
  }

  public void addSubResource(ModelResourceInfo subResource) {
    subResources.add(subResource);
    subResource.parentInfo = this;
  }

  public AxisAlignedBox getBoundingBox() {
    if ((this.boundingBox == null) && (parentInfo != null)) {
      return this.parentInfo.boundingBox;
    }
    return boundingBox;
  }

  public int getCreationYear() {
    if ((this.creationYear == -1) && (parentInfo != null)) {
      return this.parentInfo.creationYear;
    }
    return creationYear;
  }

  public String getCreator() {
    if ((this.creator == null) && (parentInfo != null)) {
      return this.parentInfo.creator;
    }
    return creator;
  }

  public String getResourceName() {
    if ((this.resourceName == null) && (parentInfo != null)) {
      return this.parentInfo.resourceName;
    }
    return resourceName;
  }

  public String getModelName() {
    if ((this.modelName == null) && (parentInfo != null)) {
      return this.parentInfo.modelName;
    }
    return modelName;
  }

  public String getTextureName() {
    if ((this.textureName == null) && (parentInfo != null)) {
      return this.parentInfo.textureName;
    }
    return textureName;
  }

  public boolean getPlaceOnGround() {
    if ((this.placeOnGround == null) && (parentInfo != null)) {
      return this.parentInfo.getPlaceOnGround();
    }
    return Boolean.TRUE.equals(this.placeOnGround);
  }

  public String[] getTags() {
    if (this.parentInfo != null) {
      String[] allTags = new String[this.tags.length + this.parentInfo.tags.length];
      if (this.parentInfo.tags.length > 0) {
        System.arraycopy(this.parentInfo.tags, 0, allTags, 0, this.parentInfo.tags.length);
      }
      if (this.tags.length > 0) {
        System.arraycopy(this.tags, 0, allTags, this.parentInfo.tags.length, this.tags.length);
      }
      return allTags;
    }
    return tags;
  }

  public String[] getGroupTags() {
    if (this.parentInfo != null) {
      String[] allTags = new String[this.groupTags.length + this.parentInfo.groupTags.length];
      if (this.parentInfo.groupTags.length > 0) {
        System.arraycopy(this.parentInfo.groupTags, 0, allTags, 0, this.parentInfo.groupTags.length);
      }
      if (this.groupTags.length > 0) {
        System.arraycopy(this.groupTags, 0, allTags, this.parentInfo.groupTags.length, this.groupTags.length);
      }
      return allTags;
    }
    return groupTags;
  }

  public String[] getThemeTags() {
    if (this.parentInfo != null) {
      String[] allTags = new String[this.themeTags.length + this.parentInfo.themeTags.length];
      if (this.parentInfo.themeTags.length > 0) {
        System.arraycopy(this.parentInfo.themeTags, 0, allTags, 0, this.parentInfo.themeTags.length);
      }
      if (this.themeTags.length > 0) {
        System.arraycopy(this.themeTags, 0, allTags, this.parentInfo.themeTags.length, this.themeTags.length);
      }
      return allTags;
    }
    return themeTags;
  }

  private List<ModelResourceInfo> getSubResources() {
    return subResources;
  }

  private boolean matchesModelAndTexture(String modelName, String textureName) {
    String thisModel = getModelName();
    String thisTexture = getTextureName();
    return ((thisModel != null) && thisModel.equalsIgnoreCase(modelName) && (thisTexture != null) && thisTexture.equalsIgnoreCase(textureName));
  }

  private boolean matchesResource(String resourceName) {
    String resource = getResourceName();
    return (resource != null) && resource.equalsIgnoreCase(resourceName);
  }

  public ModelResourceInfo getSubResource(String modelName, String textureName) {
    for (ModelResourceInfo mri : this.subResources) {
      if (mri.matchesModelAndTexture(modelName, textureName)) {
        return mri;
      }
    }
    for (ModelResourceInfo subResource : this.subResources) {
      if (subResource.matchesModel(modelName)) {
        return subResource;
      }
    }
    return null;
  }

  private boolean matchesModel(String modelName) {
    String thisModel = getModelName();
    return thisModel != null && thisModel.equalsIgnoreCase(modelName);
  }

  public ModelResourceInfo getSubResource(String resourceName) {
    for (ModelResourceInfo mri : this.subResources) {
      if (mri.matchesResource(resourceName)) {
        return mri;
      }
    }
    return null;
  }

  public ModelResourceInfo getParent() {
    return parentInfo;
  }

  public ModelManifest createModelManifest() {
    ModelManifest manifest = new ModelManifest();

    manifest.description.name = getModelName();
    manifest.description.icon = getModelName() + ".png";
    manifest.description.tags.addAll(Arrays.asList(getTags()));
    manifest.description.themeTags.addAll(Arrays.asList(getThemeTags()));
    manifest.description.groupTags.addAll(Arrays.asList(getGroupTags()));

    manifest.provenance.aliceVersion = ProjectVersion.getCurrentVersion().toString();
    manifest.provenance.created = getCreationYear() < 0 ? ZonedDateTime.now() : Year.of(getCreationYear());
    manifest.provenance.creator = getCreator();

    manifest.metadata.identifier.name = getModelName();
    manifest.metadata.identifier.type = Manifest.ProjectType.Model;
    manifest.metadata.formatVersion = "0.1";

    manifest.placeOnGround = getPlaceOnGround();
    manifest.boundingBox = createManifestBoundingBox();

    //Add the structures, textures, and model variants
    for (ModelResourceInfo subResource : getSubResources()) {
      subResource.addModelVariantInfo(manifest);
    }

    return manifest;
  }

  private ModelManifest.BoundingBox createManifestBoundingBox() {
    if (getBoundingBox() == null) {
      return null;
    }
    ModelManifest.BoundingBox boundingBox = new ModelManifest.BoundingBox();
    boundingBox.max = getBoundingBox().maximum().asFloatList();
    boundingBox.min = getBoundingBox().minimum().asFloatList();
    return boundingBox;
  }

  private void addModelVariantInfo(ModelManifest manifest) {
    StructureReference structure = createStructureReference();
    ModelManifest.TextureSet textureSet = createTextureSet();
    //Only add the structures and textures if they aren't already in the manifest
    //Multiple resources may reference the same structure or texture so repeats are likely
    if (manifest.getStructure(structure.name) == null) {
      manifest.resources.add(structure);
    }
    if (manifest.getTextureSet(textureSet.name) == null) {
      manifest.textureSets.add(textureSet);
    }
    //Add the model variant. Model variants are all unique.
    ModelManifest.ModelVariant modelVariant = createModelVariant(structure, textureSet);
    manifest.models.add(modelVariant);
  }

  private ModelManifest.ModelVariant createModelVariant(StructureReference structure, ModelManifest.TextureSet texture) {
    ModelManifest.ModelVariant modelVariant = new ModelManifest.ModelVariant();
    modelVariant.name = getResourceName();
    modelVariant.structure = structure.name;
    modelVariant.textureSet = texture.name;
    modelVariant.icon = getResourceName() + ".png";
    return modelVariant;
  }

  private ModelManifest.TextureSet createTextureSet() {
    ModelManifest.TextureSet textureReference = new ModelManifest.TextureSet();
    textureReference.name = getTextureReferenceName();
    return textureReference;
  }

  private StructureReference createStructureReference() {
    StructureReference structureReference = new StructureReference();
    structureReference.boundingBox = createManifestBoundingBox();
    structureReference.name = getTextureReferenceName();
    return structureReference;
  }

  private String getTextureReferenceName() {
    //Textures sets are model specific, so must prepend the model name
    String texture = getTextureName();
    return ((texture == null) || texture.isBlank()) ? getModelName() : getModelName() + "_" + texture;
  }

}

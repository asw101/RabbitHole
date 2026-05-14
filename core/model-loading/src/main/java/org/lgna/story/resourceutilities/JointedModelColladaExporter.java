/*******************************************************************************
 * Copyright (c) 2006, 2018, Carnegie Mellon University. All rights reserved.
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
import edu.cmu.cs.dennisc.java.util.zip.DataSource;
import edu.cmu.cs.dennisc.scenegraph.*;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.alice.tweedle.file.ImageReference;
import org.alice.tweedle.file.ModelManifest;
import org.lgna.common.resources.ImageResource;
import org.lgna.project.io.JointedModelExporter;
import org.lgna.story.implementation.ImageFactory;
import org.lgna.story.implementation.JointedModelImp.VisualData;
import org.lgna.story.implementation.alice.AliceResourceUtilities;
import org.lgna.story.resources.ImplementationAndVisualType;import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resourceutilities.exporterutils.collada.*;
import org.lgna.story.resourceutilities.exporterutils.collada.COLLADA.Scene;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Dave Culyba
 */
public class JointedModelColladaExporter implements JointedModelExporter {

  private static final Logger logger = Logger.getLogger(JointedModelColladaExporter.class.getName());
  private static final String COLLADA_EXTENSION = "dae";
  private static final String IMAGE_EXTENSION = "png";

  private static final JAXBContext JAXB_CONTEXT;
  static {
    try {
      JAXB_CONTEXT = JAXBContext.newInstance("org.lgna.story.resourceutilities.exporterutils.collada");
    } catch (JAXBException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private final ObjectFactory factory;
  private final SkeletonVisual visual;
  private final ModelManifest.ModelVariant modelVariant;
  private final String modelName;
  private final String resourcePath;
  private final Map<String, String> renamedJoints;

  private final HashMap<edu.cmu.cs.dennisc.scenegraph.Geometry, String> meshNameMap = new HashMap<>();
  private final HashMap<Integer, String> materialNameMap = new HashMap<>();
  private final HashMap<Integer, TexturedAppearance> textureAppearanceMap = new HashMap<>();

  private final ColladaJointExtractor jointExtractor;
  private final ColladaMeshProcessor meshProcessor;
  private final ColladaParser parser;

  public JointedModelColladaExporter(SkeletonVisual sv, ModelManifest.ModelVariant modelVariant, String modelName, String resourcePath, Map<String, String> renamedJoints) {
    this.factory = new ObjectFactory();
    this.visual = sv;
    this.modelVariant = modelVariant;
    this.modelName = modelName;
    this.resourcePath = resourcePath;
    this.renamedJoints = renamedJoints;
    initializeMeshNameMap();
    initializeMaterialNameMap();
    this.jointExtractor = new ColladaJointExtractor(factory, renamedJoints);
    this.meshProcessor = new ColladaMeshProcessor(factory, meshNameMap, materialNameMap);
    this.parser = new ColladaParser(factory);
  }

  public JointedModelColladaExporter(SkeletonVisual sv, ModelManifest.ModelVariant modelVariant, String modelName) {
    this(sv, modelVariant, modelName, "", Collections.emptyMap());
  }

  public String getUserJointIdentifier(String jointIdentifier) {
    return jointExtractor.getUserJointIdentifier(jointIdentifier);
  }

  // ── Name map initialization ───────────────────────────────────

  private void addMeshToNameMap(edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh) {
    String meshName = sgMesh.getName();
    if ((meshName == null) || (meshName.length() == 0)) {
      meshName = "mesh" + meshNameMap.size();
    }
    meshNameMap.put(sgMesh, meshName);
  }

  private void initializeMeshNameMap() {
    meshNameMap.clear();
    for (edu.cmu.cs.dennisc.scenegraph.Geometry g : visual.geometries.getValue()) {
      if (g instanceof edu.cmu.cs.dennisc.scenegraph.Mesh mesh) {
        addMeshToNameMap(mesh);
      }
    }
    for (WeightedMesh wm : visual.weightedMeshes.getValue()) {
      addMeshToNameMap(wm);
    }
  }

  private void initializeMaterialNameMap() {
    materialNameMap.clear();
    textureAppearanceMap.clear();
    for (TexturedAppearance texture : visual.textures.getValue()) {
      Integer id = texture.textureId.getValue();
      materialNameMap.put(id, "material_" + id);
      textureAppearanceMap.put(id, texture);
    }
  }

  // ── Naming helpers (used by parser callbacks and public API) ──

  private String getFullResourceName() {
    return modelVariant == null ? modelName : modelVariant.textureSet;
  }

  private String getImageNameForIndex(Integer index) {
    return materialNameMap.get(index) + "_diffuseMap";
  }

  private String getExternallyUniqueImageNameForID(Integer id) {
    return getFullResourceName() + "_" + getImageNameForIndex(id);
  }

  private String getImageFileNameForIndex(Integer index) {
    return getExternallyUniqueImageNameForID(index) + "." + IMAGE_EXTENSION;
  }

  private String getImageIDForIndex(Integer index) {
    return getImageNameForIndex(index) + "-image";
  }

  private String getMaterialIDForIndex(Integer index) {
    return materialNameMap.get(index) + "_shader";
  }

  private String getEffectIDForIndex(Integer index) {
    return materialNameMap.get(index) + "_fx";
  }

  // ── COLLADA assembly (delegates to extracted classes) ──────────

  private void createAndAddMeshComponents(COLLADA collada, VisualScene visualScene) {
    LibraryGeometries lg = factory.createLibraryGeometries();
    for (edu.cmu.cs.dennisc.scenegraph.Geometry g : visual.geometries.getValue()) {
      if (g instanceof edu.cmu.cs.dennisc.scenegraph.Mesh sgMesh) {
        meshProcessor.addGeometriesForMesh(lg.getGeometry(), sgMesh);
        meshProcessor.addVisualSceneNodesForMesh(visualScene.getNode(), sgMesh);
      }
    }
    for (WeightedMesh sgWM : visual.weightedMeshes.getValue()) {
      meshProcessor.addGeometriesForMesh(lg.getGeometry(), sgWM);
    }
    collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(lg);

    LibraryControllers lc = factory.createLibraryControllers();
    for (WeightedMesh sgWM : visual.weightedMeshes.getValue()) {
      meshProcessor.addControllersForMesh(lc.getController(), sgWM, this::getUserJointIdentifier);
      meshProcessor.addVisualSceneNodesForWeightedMesh(visualScene.getNode(), sgWM);
    }
    collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(lc);
  }

  protected COLLADA createCollada() {
    COLLADA collada = factory.createCOLLADA();
    collada.setVersion("1.4.1");
    collada.setAsset(parser.createAsset());

    String sceneName = getFullResourceName();
    VisualScene visualScene = factory.createVisualScene();
    visualScene.setId(sceneName);
    visualScene.setName(sceneName);

    Node skeletonNodes = jointExtractor.createSkeletonNodes(visual);
    visualScene.getNode().add(skeletonNodes);

    Scene scene = factory.createCOLLADAScene();
    InstanceWithExtra sceneInstance = factory.createInstanceWithExtra();
    sceneInstance.setUrl("#" + visualScene.getId());
    scene.setInstanceVisualScene(sceneInstance);
    collada.setScene(scene);

    parser.createAndAddTextureComponents(collada, visual, materialNameMap,
        this::getImageNameForIndex, this::getExternallyUniqueImageNameForID,
        this::getImageFileNameForIndex, this::getImageIDForIndex,
        this::getMaterialIDForIndex, this::getEffectIDForIndex);
    createAndAddMeshComponents(collada, visualScene);

    LibraryVisualScenes lvs = factory.createLibraryVisualScenes();
    lvs.getVisualScene().add(visualScene);
    collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(lvs);

    return collada;
  }

  // ── I/O: COLLADA writing and texture handling ─────────────────

  public void writeCollada(OutputStream os) throws IOException {
    COLLADA collada = createCollada();
    try {
      final Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(collada, os);
    } catch (JAXBException e) {
      throw new IOException(e);
    }
  }

  private static BufferedImage createFlippedImage(BufferedImage image) {
    AffineTransform at = new AffineTransform();
    at.concatenate(AffineTransform.getScaleInstance(1, -1));
    at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
    BufferedImage flippedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = flippedImage.createGraphics();
    g.transform(at);
    g.drawImage(image, 0, 0, null);
    g.dispose();
    return flippedImage;
  }

  private static void writeTexture(TexturedAppearance texture, OutputStream os) throws IOException {
    BufferedImageTexture bufferedTexture = (BufferedImageTexture) texture.diffuseColorTexture.getValue();
    BufferedImage flippedImage = createFlippedImage(bufferedTexture.getBufferedImage());
    ImageIO.write(flippedImage, IMAGE_EXTENSION, os);
  }

  private String getColladaFileName() {
    return getFullResourceName() + "." + COLLADA_EXTENSION;
  }

  // ── Public API (JointedModelExporter) ─────────────────────────

  public List<String> getTextureFileNames() {
    List<String> textureFileNames = new ArrayList<>();
    for (TexturedAppearance texture : visual.textures.getValue()) {
      Integer materialIndex = texture.textureId.getValue();
      textureFileNames.add(getImageFileNameForIndex(materialIndex));
    }
    return textureFileNames;
  }

  public Map<Integer, String> createTextureIdToImageMap() {
    Map<Integer, String> textureIdToFileMap = new HashMap<>();
    for (TexturedAppearance texture : visual.textures.getValue()) {
      Integer textureID = texture.textureId.getValue();
      textureIdToFileMap.put(textureID, getExternallyUniqueImageNameForID(textureID));
    }
    return textureIdToFileMap;
  }

  private TexturedAppearance getTextureAppearance(Integer textureId) {
    return textureAppearanceMap.get(textureId);
  }

  public ImageResource createImageResourceForTexture(Integer textureId) throws IOException {
    TexturedAppearance texturedAppearance = getTextureAppearance(textureId);
    BufferedImageTexture bufferedTexture = (BufferedImageTexture) texturedAppearance.diffuseColorTexture.getValue();
    return ImageFactory.createImageResource(bufferedTexture.getBufferedImage(), getImageFileNameForIndex(textureId));
  }

  public Integer getTextureIdForName(String textureName) {
    String baseTextureName = FileUtilities.getBaseName(textureName);
    for (TexturedAppearance texture : visual.textures.getValue()) {
      Integer textureID = texture.textureId.getValue();
      String toCheck = getExternallyUniqueImageNameForID(textureID);
      if (baseTextureName.equals(toCheck)) {
        return textureID;
      }
    }
    return -1;
  }

  @Override
  public DataSource createStructureDataSource() {
    final String name = resourcePath + "/" + getColladaFileName();
    return ModelExportDataSources.create(name, this::writeCollada);
  }

  @Override
  public String getStructureFileName(DataSource structureDataSource) {
    return ModelExportDataSources.structureFileNameRelativeTo(resourcePath, structureDataSource);
  }

  @Override
  public String getStructureExtension() {
    return IMAGE_EXTENSION;
  }

  @Override
  public void addImageDataSources(List<DataSource> dataSources, ModelManifest modelManifest, Map<Integer, String> resourceMap) throws IOException {
    addTextureIds(resourceMap);
    for (DataSource imageDataSource : createImageDataSources()) {
      if (!dataSources.contains(imageDataSource)) {
        Integer imageId = getTextureIdForName(imageDataSource.getName());
        ImageReference imageReference = new ImageReference(createImageResourceForTexture(imageId));
        imageReference.name = resourceMap.get(imageId);
        modelManifest.resources.add(imageReference);
        dataSources.add(imageDataSource);
      }
    }
  }

  private void addTextureIds(Map<Integer, String> resourceMap) {
    for (TexturedAppearance texture : visual.textures.getValue()) {
      Integer textureID = texture.textureId.getValue();
      resourceMap.put(textureID, getExternallyUniqueImageNameForID(textureID));
    }
  }

  public List<DataSource> createImageDataSources() {
    List<DataSource> dataSources = new ArrayList<>();
    for (TexturedAppearance texture : visual.textures.getValue()) {
      if (texture.diffuseColorTexture.getValue() != null) {
        Integer materialIndex = texture.textureId.getValue();
        final String textureName = resourcePath + "/" + getImageFileNameForIndex(materialIndex);
        DataSource dataSource = ModelExportDataSources.create(textureName, os -> writeTexture(texture, os));
        dataSources.add(dataSource);
      }
    }
    return dataSources;
  }

  public List<File> saveTexturesToDirectory(File directory) {
    List<File> textureFiles = new ArrayList<>();
    for (TexturedAppearance texture : visual.textures.getValue()) {
      File textureFile = new File(directory, getImageFileNameForIndex(texture.textureId.getValue()));
      try {
        FileUtilities.createParentDirectoriesIfNecessary(textureFile);
        textureFile.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(textureFile)) {
          writeTexture(texture, fos);
        }
        textureFiles.add(textureFile);
      } catch (IOException e) {
        logger.log(Level.WARNING, "Failed to save texture: " + textureFile.getName(), e);
      }
    }
    return textureFiles;
  }

  // ── Local testing code ────────────────────────────────────────

  private File saveColladaToDirectory(File directory) throws IOException {
    File colladaOutputFile = new File(directory, getColladaFileName());
    writeCollada(new FileOutputStream(colladaOutputFile));
    return colladaOutputFile;
  }

  private static List<File> exportAliceModelToDir(JointedModelColladaExporter exporter, File rootDir) throws IOException {
    List<File> outputFiles = new ArrayList<>();
    outputFiles.add(exporter.saveColladaToDirectory(rootDir));
    outputFiles.addAll(exporter.saveTexturesToDirectory(rootDir));
    return outputFiles;
  }

  private static SkeletonVisual loadAliceModel(JointedModelResource resource) {
    VisualData<JointedModelResource> v = ImplementationAndVisualType.ALICE.getFactory(resource).createVisualData();
    SkeletonVisual sv = (SkeletonVisual) v.getSgVisuals()[0];
    return sv;
  }

  private static List<File> exportAliceModelResourceToDir(JointedModelResource modelResource, File rootDir) throws IOException {
    ModelResourceInfo modelInfo = AliceResourceUtilities.getModelResourceInfo(modelResource.getClass(), modelResource.toString());
    SkeletonVisual sgSkeletonVisual = loadAliceModel(modelResource);
    ModelManifest modelManifest = modelInfo.createModelManifest();
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(sgSkeletonVisual, modelManifest.models.getFirst(), modelManifest.description.name);
    return exportAliceModelToDir(exporter, rootDir);
  }
}

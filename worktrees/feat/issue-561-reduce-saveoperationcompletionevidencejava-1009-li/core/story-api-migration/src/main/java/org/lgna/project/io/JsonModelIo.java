package org.lgna.project.io;

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.java.util.zip.ByteArrayDataSource;
import edu.cmu.cs.dennisc.java.util.zip.DataSource;
import edu.cmu.cs.dennisc.scenegraph.*;
import org.alice.tweedle.file.*;
import org.lgna.story.implementation.ImageFactory;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.implementation.alice.AliceResourceClassUtilities;
import org.lgna.story.implementation.alice.AliceResourceUtilities;
import org.lgna.story.resources.*;
import org.lgna.story.resourceutilities.JointedModelAliceExporter;
import org.lgna.story.resourceutilities.JointedModelColladaExporter;
import org.lgna.story.resourceutilities.JointedModelGltfExporter;
import org.lgna.story.resourceutilities.ModelResourceInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

public class JsonModelIo extends DataSourceIo {

  private static final boolean NORMALIZE_WEIGHTS = true;
  private static final boolean GENERATE_BACKFACES = true;

  protected ModelManifest modelManifest;
  protected Set<JointedModelResource> modelResources;
  private List<SkeletonVisual> skeletonVisuals;
  private List<BufferedImage> thumbnails;
  private final ExportFormat exportFormat;
  private final Map<String, String> renamedJoints = new HashMap<>();

  public enum ExportFormat {
    COLLADA() {
      @Override
      public JointedModelExporter exporter(JsonModelIo modelIo, SkeletonVisual sv, ModelManifest.ModelVariant modelVariant, String resourcePath) {
        return new JointedModelColladaExporter(sv, modelVariant, modelIo.modelManifest.description.name, resourcePath, modelIo.renamedJoints);
      }
    },
    GLTF() {
      @Override
      public JointedModelExporter exporter(JsonModelIo modelIo, SkeletonVisual sv, ModelManifest.ModelVariant modelVariant, String resourcePath) {
        return new JointedModelGltfExporter(sv, modelVariant, modelIo.modelManifest.description.name, resourcePath, modelIo.renamedJoints);
      }
    },
    ALICE() {
      @Override
      public JointedModelExporter exporter(JsonModelIo modelIo, SkeletonVisual sv, ModelManifest.ModelVariant modelVariant, String resourcePath) {
        return new JointedModelAliceExporter(sv, modelVariant, resourcePath);
      }
    };
    public abstract JointedModelExporter exporter(JsonModelIo modelIo, SkeletonVisual sv, ModelManifest.ModelVariant modelVariant, String resourcePath);
  }

  protected JsonModelIo(ExportFormat exportFormat) {
    this.exportFormat = exportFormat;
  }

  JsonModelIo(ModelManifest modelManifest, SkeletonVisual skeletonVisual, BufferedImage thumbnail, ExportFormat exportFormat) {
    this(exportFormat);
    this.modelManifest = modelManifest;
    this.skeletonVisuals = new ArrayList<>();
    this.skeletonVisuals.add(skeletonVisual);

    this.thumbnails = new ArrayList<>();
    this.thumbnails.add(thumbnail);
  }

  JsonModelIo(Set<JointedModelResource> modelResources, ExportFormat exportFormat) {
    this(exportFormat);
    this.modelManifest = createModelManifestFromEnums(modelResources);
    this.modelResources = modelResources;
  }

  JsonModelIo(DynamicResource<?, ?> dynamicResource, ExportFormat exportFormat) {
    this(exportFormat);
    this.modelResources = Collections.singleton(dynamicResource);
    this.modelManifest = dynamicResource.getModelManifest().copyForExport();
    modelManifest.additionalJoints
        .stream()
        .map(joint -> joint.name)
        .filter(name -> !"root".equalsIgnoreCase(name))
        .forEach(name -> renamedJoints.put(name, "u_" + name));
  }

  //AliceResourcesUtilities.getTextureResourceName returns null for resources that use "default" texture names
  //Catch this and return the default texture name
  private static String getTextureName(JointedModelResource modelResource) {
    String textureName = AliceResourceUtilities.getTextureResourceName(modelResource);
    if (textureName.length() == 0) {
      textureName = AliceResourceUtilities.getDefaultTextureEnumName(modelResource.toString());
    }
    return textureName;
  }

  private static Set<JointedModelResource> createUniqueResourceSet(Collection<JointedModelResource> modelResources) {
    if (modelResources == null || modelResources.isEmpty()) {
      throw new IllegalArgumentException("At least one model resource is required to create a model manifest");
    }
    Set<JointedModelResource> uniqueResources = new LinkedHashSet<>();
    for (JointedModelResource modelResource : modelResources) {
      if (modelResource == null) {
        throw new IllegalArgumentException("Model resources must not contain null entries");
      }
      uniqueResources.add(modelResource);
    }
    return uniqueResources;
  }

  //Build a new ModelResourceInfo that includes only the resources in the modelResources list
  private static ModelResourceInfo createModelResourceInfo(Collection<JointedModelResource> modelResources) {
    Set<JointedModelResource> uniqueResources = createUniqueResourceSet(modelResources);
    //Get the ModelResourceInfo from the first resource in the list.
    //The get the parent info for this ModelResourceInfo. This will be the ModelResourceInfo that represents the model class.
    ModelResource firstResource = uniqueResources.iterator().next();
    ModelResourceInfo resourceInfo = AliceResourceUtilities.getModelResourceInfo(firstResource.getClass(), firstResource.toString());
    if (resourceInfo == null || resourceInfo.getParent() == null) {
      throw new IllegalStateException("No model resource metadata found for " + firstResource.getClass().getName() + " " + firstResource);
    }
    ModelResourceInfo rootInfo = resourceInfo.getParent();
    return copyResourceInfo(uniqueResources, rootInfo);
  }

  private static ModelResourceInfo copyResourceInfo(Iterable<JointedModelResource> modelResources, ModelResourceInfo rootInfo) {
    //Make a copy of the rootInfo and then go through all the passed in modelResources and add ModelResourceInfos for them
    ModelResourceInfo toReturn = rootInfo.createShallowCopy();
    for (JointedModelResource modelResource : modelResources) {
      if (modelResource == null) {
        throw new IllegalArgumentException("Model resources must not contain null entries");
      }
      String visualName = AliceResourceUtilities.getVisualResourceName(modelResource);
      String textureName = getTextureName(modelResource);
      ModelResourceInfo subResource = rootInfo.getSubResource(visualName, textureName);
      if (subResource == null) {
        throw new IllegalStateException("No model resource metadata found for " + modelResource.getClass().getName() + " " + modelResource);
      }
      ModelResourceInfo newSubResource = subResource.createShallowCopy();
      toReturn.addSubResource(newSubResource);
    }
    return toReturn;
  }

  //Build a new ModelResourceInfo that includes only the resources in the modelResources list
  private static ModelManifest createModelManifestFromEnums(Set<JointedModelResource> modelResources) {
    ModelResourceInfo modelInfo = createModelResourceInfo(modelResources);
    //Get the ModelResourceInfo from the first resource found.
    //Then get the parent info for this ModelResourceInfo. This will be the ModelResourceInfo that represents the model class.
    JointedModelResource firstResource = modelResources.iterator().next();

    ModelManifest modelManifest = modelInfo.createModelManifest();
    //Alice resources are enums that implement the base resource interfaces. For instance, the Alien implements the BipedResource interface
    Class superClass = firstResource.getClass().getInterfaces()[0];
    String parentClassName = AliceResourceClassUtilities.getAliceClassName(superClass);
    modelManifest.parentClass = parentClassName;
    //If this model is defined by JointedModelResources, then add the model data from that
    //We use the first resource because the poses are defined on the resource enum, not on each resource instance
    ModelManifestResourceData.addModelDataFromResource(modelManifest, firstResource);

    return modelManifest;
  }

  private JointedModelResource getResourceForVariant(ModelManifest.ModelVariant modelVariant) {
    if (modelResources != null) {
      if ("DEFAULT".equals(modelVariant.name) && modelResources.size() == 1) {
        return modelResources.iterator().next();
      }
      for (JointedModelResource resource : modelResources) {
        if (modelVariant.name.equals(resource.toString())) {
          return resource;
        }
      }
    }
    return null;
  }

  private Mesh createFlippedMesh(Mesh toFlip) {
    Mesh newMesh = toFlip.createCopy();
    newMesh.invertNormals();
    newMesh.invertIndices();
    newMesh.setName(toFlip.getName() + "_flipped");
    return newMesh;
  }

  private SkeletonVisual getVisualForModelVariant(ModelManifest.ModelVariant modelVariant) {
    if (skeletonVisuals != null) {
      for (SkeletonVisual visual : skeletonVisuals) {
        if (visual.getName().equals(modelVariant.structure)) {
          return visual;
        }
      }
      Logger.warning("Could not find matching visual for " + modelVariant.structure + ". Returning first skeleton visual in list.");
      return skeletonVisuals.getFirst();
    } else {
      JointedModelResource modelResource = getResourceForVariant(modelVariant);
      if (modelResource == null) {
        return null;
      }
      final JointedModelImp.JointImplementationAndVisualDataFactory<JointedModelResource> factory = modelResource.getImplementationAndVisualFactory();
      return getSkeletonVisual(factory.createVisualData(), modelResource);
    }
  }

  protected SkeletonVisual getSkeletonVisual(JointedModelImp.VisualData<JointedModelResource> v, JointedModelResource modelResource) {
    SkeletonVisual sv = v.getSgVisualForExporting(modelResource);
    //Make sure meshes have a name
    int meshCount = 0;
    for (Geometry g : sv.geometries.getValue()) {
      if ((g instanceof Mesh) && g.getName() == null) {
        g.setName("mesh" + meshCount++);
      }
    }
    int weightedMeshCount = 0;
    for (WeightedMesh m : sv.weightedMeshes.getValue()) {
      if (m.getName() == null) {
        m.setName("weightedMesh" + weightedMeshCount++);
      }
    }
    if (GENERATE_BACKFACES) {
      List<Geometry> backfaceMeshes = new LinkedList<>();
      for (Geometry g : sv.geometries.getValue()) {
        if ((g instanceof Mesh mesh) && !mesh.cullBackfaces.getValue()) {
          backfaceMeshes.add(createFlippedMesh(mesh));
        }
      }
      if (!backfaceMeshes.isEmpty()) {
        for (int i = 0; i < sv.geometries.getLength(); i++) {
          backfaceMeshes.add(i, sv.geometries.getValue()[i]);
        }
        sv.geometries.setValue(backfaceMeshes.toArray(new Geometry[backfaceMeshes.size()]));
      }
      List<WeightedMesh> backfaceWeightedMeshes = new LinkedList<>();
      for (WeightedMesh m : sv.weightedMeshes.getValue()) {
        if (!m.cullBackfaces.getValue()) {
          backfaceWeightedMeshes.add((WeightedMesh) createFlippedMesh(m));
        }
      }
      if (!backfaceWeightedMeshes.isEmpty()) {
        for (int i = 0; i < sv.weightedMeshes.getLength(); i++) {
          backfaceWeightedMeshes.add(i, sv.weightedMeshes.getValue()[i]);
        }
        sv.weightedMeshes.setValue(backfaceWeightedMeshes.toArray(new WeightedMesh[backfaceWeightedMeshes.size()]));
      }
    }
    if (NORMALIZE_WEIGHTS) {
      sv.normalizeWeightedMeshes();
    }
    return sv;
  }

  private BufferedImage getThumbnailImageForModelVariant(ModelManifest.ModelVariant modelVariant) {
    JointedModelResource modelResource = getResourceForVariant(modelVariant);
    if (modelResource != null) {
      return getThumbnail(modelResource, modelVariant.name);
    }
    return null;
  }

  public static BufferedImage getThumbnail(ModelResource modelResource, String modelVariant) {
    URL resourceURL = modelResource.getThumbnailUrl(modelVariant);
    if (resourceURL == null) {
      Logger.warning("Cannot load thumbnail for", modelResource, modelVariant);
    } else {
      try {
        return ImageIO.read(resourceURL);
      } catch (IOException e) {
        Logger.throwable(e, "Cannot load thumbnail for", modelResource, modelVariant);
      }
    }
    return null;
  }

  private String getModelName() {
    return modelManifest.getName();
  }

  public ModelReference createModelReference(String basePath) {
    ModelReference modelReference = new ModelReference();
    modelReference.name = getModelName();
    modelReference.format = "json";
    modelReference.file = basePath + "/" + getModelName() + "/" + getModelName() + ".json";

    return modelReference;
  }

  private void addModelVariantDataSources(List<DataSource> dataSources, SkeletonVisual sv, ModelManifest.ModelVariant modelVariant, String resourcePath) throws IOException {
    JointedModelExporter exporter = exportFormat.exporter(this, sv, modelVariant, resourcePath);

    addBoundsForJoints(sv.skeleton.getValue(), modelManifest);

    final Map<Integer, String> resources = modelManifest.getTextureSet(modelVariant.textureSet).idToResourceMap;
    exporter.addImageDataSources(dataSources, modelManifest, resources);

    DataSource structureDataSource = exporter.createStructureDataSource();

    //Only add new model files to the list to be written
    if (!dataSources.contains(structureDataSource)) {
      //Link manifest entries to the files created by the exporter
      StructureReference structureReference = modelManifest.getStructure(modelVariant.structure);
      structureReference.file = exporter.getStructureFileName(structureDataSource);
      structureReference.format = exporter.getStructureExtension();
      finishStructureReference(structureReference, sv);
      dataSources.add(structureDataSource);
    }
  }

  // Hook to be overridden
  protected void finishStructureReference(StructureReference structureReference, SkeletonVisual sv) {
  }

  private void addBoundsForJoints(Joint root, ModelManifest manifest) {
    if (root == null) {
      return;
    }
    root.visitJoints((joint) -> manifest.addBoundsForJoint(getJointNameForOutput(joint.jointID.getValue()), joint.getBoundingBox(false)));
  }

  private String getJointNameForOutput(String jointName) {
    return renamedJoints.getOrDefault(jointName, jointName);
  }

  public List<DataSource> createDataSources(String baseModelPath) throws IOException {
    List<DataSource> dataToWrite = new ArrayList<>();

    //Model resources and manifest go into a folder named the model name
    String resourcePath = baseModelPath + "/" + getModelName();
    for (ModelManifest.ModelVariant modelVariant : modelManifest.models) {
      SkeletonVisual sv = getVisualForModelVariant(modelVariant);
      if (sv == null) {
        throw new IOException("Unable to create visual for model variant " + modelVariant.name + " in " + getModelName());
      }
      addModelVariantDataSources(dataToWrite, sv, modelVariant, resourcePath);
      //Add DataSources for the thumbnails if possible
      BufferedImage variantThumbnail = getThumbnailImageForModelVariant(modelVariant);
      if (variantThumbnail == null && skeletonVisuals != null) {
        variantThumbnail = thumbnails.get(skeletonVisuals.indexOf(sv));
      }
      if (variantThumbnail != null) {
        String thumbnailName = modelVariant.name + ".png";
        modelVariant.icon = thumbnailName;
        DataSource thumbnailDataSource = createAndAddImageDataSource(variantThumbnail, resourcePath, thumbnailName);
        dataToWrite.add(thumbnailDataSource);
      }
    }

    BufferedImage thumbnailImage;
    if (this.thumbnails != null) {
      thumbnailImage = this.thumbnails.getFirst();
    } else {
      thumbnailImage = getThumbnailImageForModelVariant(modelManifest.models.getFirst());
    }
    if (thumbnailImage != null) {
      String classIconName = getModelName() + "_cls.png";
      DataSource thumbnailDataSource = createAndAddImageDataSource(thumbnailImage, resourcePath, classIconName);
      dataToWrite.add(thumbnailDataSource);
      modelManifest.description.icon = classIconName;
    }
    //The model manifest goes in the base model path directory
    dataToWrite.add(new ByteArrayDataSource(
        resourcePath + "/" + getModelName() + ".json",
        ManifestEncoderDecoder.toJson(modelManifest)));
    return dataToWrite;
  }

  private DataSource createAndAddImageDataSource(BufferedImage image, String resourcePath, String imageName) throws IOException {
    DataSource imageDataSource = createPNGImageDataSource(resourcePath + "/" + imageName, image);
    ImageReference imageReference = new ImageReference(ImageFactory.createImageResource(image, imageName));
    imageReference.name = imageName;
    modelManifest.resources.add(imageReference);
    return imageDataSource;
  }

  private static DataSource createAliceStructureDataSource(final String fileName, final SkeletonVisual sv) {
    return new DataSource() {
      @Override
      public String getName() {
        return fileName;
      }

      @Override
      public void write(OutputStream os) throws IOException {
        AliceResourceUtilities.encodeVisual(sv, os);
      }
    };
  }

  private static DataSource createPNGImageDataSource(final String fileName, final BufferedImage image) {
    return new DataSource() {
      @Override
      public String getName() {
        return fileName;
      }

      @Override
      public void write(OutputStream os) throws IOException {
        ImageIO.write(image, "png", os);
      }
    };
  }

  public void writeModel(OutputStream os, String baseModelPath) throws IOException {
    List<DataSource> dataToWrite = createDataSources(baseModelPath);
    writeDataSources(os, dataToWrite);
  }

  public void writeModel(OutputStream os, List<JointedModelResource> modelResources) throws IOException {
    this.modelResources = createUniqueResourceSet(modelResources);
    this.skeletonVisuals = null;
    this.thumbnails = null;
    this.renamedJoints.clear();
    this.modelManifest = createModelManifestFromEnums(this.modelResources);
    writeModel(os, "models");
  }

}

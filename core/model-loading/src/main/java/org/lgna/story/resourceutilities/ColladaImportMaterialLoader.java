package org.lgna.story.resourceutilities;

import com.dddviewr.collada.Collada;
import com.dddviewr.collada.effects.Effect;
import com.dddviewr.collada.effects.NewParam;
import com.dddviewr.collada.images.Image;
import com.dddviewr.collada.materials.InstanceEffect;
import com.dddviewr.collada.materials.LibraryMaterials;
import com.dddviewr.collada.materials.Material;
import edu.cmu.cs.dennisc.image.ImageUtilities;
import edu.cmu.cs.dennisc.scenegraph.Mesh;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import edu.cmu.cs.dennisc.texture.Texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads textures and materials from a COLLADA model into Alice scene-graph appearances.
 * Package-private delegate extracted from {@link JointedModelColladaImporter}.
 */
class ColladaImportMaterialLoader {

  private static final Logger LOGGER = Logger.getLogger(ColladaImportMaterialLoader.class.getName());

  private final Logger logger;

  ColladaImportMaterialLoader(Logger logger) {
    this.logger = logger;
  }

  List<TexturedAppearance> createAliceMaterialsFromCollada(Collada colladaModel, File rootPath, List<Mesh> aliceMeshes) throws ModelLoadingException {
    List<TexturedAppearance> textureAppearances = new LinkedList<>();
    final LibraryMaterials libraryMaterials = colladaModel.getLibraryMaterials();
    if (libraryMaterials != null) {
      List<Material> materials = libraryMaterials.getMaterials();
      for (Material material : materials) {
        int index = ColladaImportMeshBuilder.getMaterialIndex(material.getId(), colladaModel);
        boolean isUsed = false;
        for (Mesh aliceMesh : aliceMeshes) {
          if (aliceMesh.textureId.getValue() == index) {
            isUsed = true;
            break;
          }
        }
        if (isUsed) {
          Image image = getColladaImageForMaterial(material, colladaModel);
          if (image == null) {
            throw new ModelLoadingException("Error loading material " + material.getId() + ": No valid image found.");
          }
          BufferedImage bufferedImage;
          try {
            File imageFile = resolveTextureFileName(image.getInitFrom(), rootPath);
            if (imageFile == null) {
              throw new ModelLoadingException("Error loading texture: File '" + image.getInitFrom() + "' not found.");
            }
            bufferedImage = ImageUtilities.read(imageFile);
            if (bufferedImage == null) {
              throw new ModelLoadingException("Error loading texture: File '" + image.getInitFrom() + "' not readable.");
            }
            bufferedImage = ImageUtilities.stretchToPowersOfTwo(bufferedImage);
          } catch (IOException e) {
            throw new ModelLoadingException("Error loading texture: " + image.getInitFrom() + " not found.", e);
          } catch (RuntimeException e) {
            throw new ModelLoadingException("Error loading texture: " + image.getInitFrom() + ".\n" + e.getMessage(), e);
          }
          TexturedAppearance m_sgAppearance = new TexturedAppearance();
          m_sgAppearance.diffuseColorTexture.setValue(getAliceTexture(bufferedImage));
          m_sgAppearance.textureId.setValue(index);
          textureAppearances.add(m_sgAppearance);
        } else {
          logger.log(Level.WARNING, "Loading materials: Skipping unreferenced material " + material.getId());
        }
      }
    }
    if (textureAppearances.isEmpty()) {
      throw new ModelLoadingException("No supported materials found. Alice models must have a texture.");
    }
    return textureAppearances;
  }

  static File resolveTextureFileName(String textureFileName, File rootPath) {
    if (textureFileName.startsWith("file://")) {
      textureFileName = textureFileName.substring(7);
    } else if (textureFileName.startsWith("file:///")) {
      textureFileName = textureFileName.substring(8);
    }
    File textureFile = new File(textureFileName);
    if (textureFile.exists()) {
      return textureFile;
    }
    File localFile = new File(rootPath, textureFile.getName());
    if (localFile.exists()) {
      return localFile;
    }
    return null;
  }

  Image getColladaImageForMaterial(Material material, Collada colladaModel) {
    InstanceEffect ie = material.getInstanceEffect();
    Effect effect = colladaModel.findEffect(ie.getUrl());
    if (effect == null) {
      logger.warning("Error loading material '" + material.getId() + "': No effect found for url '" + ie.getUrl() + "'");
      return null;
    }
    if (effect.getEffectMaterial() == null) {
      logger.warning("Error loading material '" + material.getId() + "': No effect material found for '" + effect.getId() + "'");
      return null;
    } else if (effect.getEffectMaterial().getDiffuse() == null) {
      logger.warning("Error loading material '" + material.getId() + "': No diffuse value found for effect '" + effect.getId() + "'");
      return null;
    } else if (effect.getEffectMaterial().getDiffuse().getTexture() == null) {
      logger.warning("Error loading material '" + material.getId() + "': No diffuse texture found for effect '" + effect.getId() + "'");
      return null;
    } else if (effect.getEffectMaterial().getDiffuse().getTexture().getTexture() == null) {
      logger.warning("Error loading material '" + material.getId() + "': No diffuse texture value found for effect '" + effect.getId() + "'");
      return null;
    }
    String textureId = effect.getEffectMaterial().getDiffuse().getTexture().getTexture();

    NewParam textureParam = effect.findNewParam(textureId);
    while (textureParam != null) {
      if (textureParam.getSurface() != null) {
        textureId = textureParam.getSurface().getInitFrom();
        textureParam = null;
        break;
      } else if (textureParam.getSampler2D() != null) {
        textureParam = effect.findNewParam(textureParam.getSampler2D().getSource());
      } else {
        textureParam = null;
      }
    }

    return colladaModel.findImage(textureId);
  }

  static Texture getAliceTexture(BufferedImage image) {
    BufferedImageTexture aliceTexture = new BufferedImageTexture();
    int type;
    if (image.getTransparency() == BufferedImage.OPAQUE) {
      type = BufferedImage.TYPE_3BYTE_BGR;
    } else {
      type = BufferedImage.TYPE_4BYTE_ABGR;
    }
    BufferedImage tex;
    try {
      tex = new BufferedImage(image.getWidth(), image.getHeight(), type);
    } catch (IllegalArgumentException e) {
      LOGGER.log(Level.WARNING, "Cannot create Alice texture for image dimensions "
          + image.getWidth() + "x" + image.getHeight(), e);
      return null;
    }
    int imageWidth = image.getWidth();
    int[] tmpData = new int[imageWidth];
    int row = 0;
    for (int y = image.getHeight() - 1; y >= 0; y--) {
      image.getRGB(0, row++, imageWidth, 1, tmpData, 0, imageWidth);
      tex.setRGB(0, y, imageWidth, 1, tmpData, 0, imageWidth);
    }
    aliceTexture.setBufferedImage(tex);
    return aliceTexture;
  }
}

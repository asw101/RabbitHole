package org.lgna.story.resourceutilities;

import com.dddviewr.collada.Collada;
import com.dddviewr.collada.effects.Effect;
import com.dddviewr.collada.effects.EffectAttribute;
import com.dddviewr.collada.effects.Lambert;
import com.dddviewr.collada.effects.LibraryEffects;
import com.dddviewr.collada.effects.NewParam;
import com.dddviewr.collada.effects.Sampler2D;
import com.dddviewr.collada.effects.Surface;
import com.dddviewr.collada.effects.Texture;
import com.dddviewr.collada.images.Image;
import com.dddviewr.collada.images.LibraryImages;
import com.dddviewr.collada.materials.InstanceEffect;
import com.dddviewr.collada.materials.LibraryMaterials;
import com.dddviewr.collada.materials.Material;
import edu.cmu.cs.dennisc.scenegraph.Mesh;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class ColladaImportMaterialLoaderTest {
  @Test
  public void resolveTextureFileNameFindsAbsoluteAndRootRelativeFiles() throws Exception {
    File root = createRootDir("resolve-texture");
    File textureFile = writeTexture(root, "texture.png");

    assertEquals(
        textureFile.getCanonicalFile(),
        ColladaImportMaterialLoader.resolveTextureFileName(
            textureFile.getAbsolutePath(), root
        ).getCanonicalFile()
    );
    assertEquals(
        textureFile.getCanonicalFile(),
        ColladaImportMaterialLoader.resolveTextureFileName(
            "texture.png", root
        ).getCanonicalFile()
    );
  }

  @Test
  public void getColladaImageForMaterialResolvesSamplerChains() {
    Collada collada = createColladaWithMaterial("texture.png", true);
    Material material = collada.getLibraryMaterials().getMaterials().get(0);
    ColladaImportMaterialLoader loader = new ColladaImportMaterialLoader(Logger.getLogger("test"));

    Image image = loader.getColladaImageForMaterial(material, collada);

    assertNotNull(image);
    assertEquals("texture.png", image.getInitFrom());
  }

  @Test
  public void createAliceMaterialsFromColladaLoadsReferencedTextures() throws Exception {
    File root = createRootDir("load-materials");
    writeTexture(root, "texture.png");
    writeTexture(root, "unused.png");

    Collada collada = createColladaWithTwoMaterials();
    Mesh mesh = new Mesh();
    mesh.textureId.setValue(0);
    List<TexturedAppearance> appearances = new ColladaImportMaterialLoader(Logger.getLogger("test"))
        .createAliceMaterialsFromCollada(collada, root, Collections.singletonList(mesh));

    assertEquals(1, appearances.size());
    assertEquals(0, appearances.get(0).textureId.getValue().intValue());
    assertNotNull(appearances.get(0).diffuseColorTexture.getValue());
  }

  @Test
  public void createAliceMaterialsFromColladaThrowsWhenImageCannotBeResolved() throws Exception {
    Collada collada = createColladaWithMaterial("missing.png", false);
    Mesh mesh = new Mesh();
    mesh.textureId.setValue(0);
    ColladaImportMaterialLoader loader = new ColladaImportMaterialLoader(Logger.getLogger("test"));

    try {
      loader.createAliceMaterialsFromCollada(
          collada,
          createRootDir("missing-material"),
          Collections.singletonList(mesh)
      );
      fail("Expected missing image to fail");
    } catch (ModelLoadingException exception) {
      assertTrue(
          exception.getMessage().contains("No valid image found")
              || exception.getMessage().contains("not found")
      );
    }
  }

  private static Collada createColladaWithTwoMaterials() {
    Collada collada = new Collada();

    LibraryImages images = new LibraryImages();
    Image texture = new Image("image0", "image0");
    texture.setInitFrom("texture.png");
    images.addImage(texture);
    Image unused = new Image("image1", "image1");
    unused.setInitFrom("unused.png");
    images.addImage(unused);
    collada.setLibraryImages(images);

    LibraryEffects effects = new LibraryEffects();
    effects.addEffect(createEffect("effect0", "sampler0", "surface0", "image0"));
    effects.addEffect(createEffect("effect1", "sampler1", "surface1", "image1"));
    collada.setLibraryEffects(effects);

    LibraryMaterials materials = new LibraryMaterials();
    Material used = new Material("mat0", "mat0");
    used.setInstanceEffect(new InstanceEffect("#effect0"));
    materials.addMaterial(used);
    Material skipped = new Material("mat1", "mat1");
    skipped.setInstanceEffect(new InstanceEffect("#effect1"));
    materials.addMaterial(skipped);
    collada.setLibraryMaterials(materials);
    return collada;
  }

  private static Collada createColladaWithMaterial(String initFrom, boolean addImage) {
    Collada collada = new Collada();

    if (addImage) {
      LibraryImages images = new LibraryImages();
      Image image = new Image("image0", "image0");
      image.setInitFrom(initFrom);
      images.addImage(image);
      collada.setLibraryImages(images);
    }

    LibraryEffects effects = new LibraryEffects();
    effects.addEffect(createEffect("effect0", "sampler0", "surface0", "image0"));
    collada.setLibraryEffects(effects);

    LibraryMaterials materials = new LibraryMaterials();
    Material material = new Material("mat0", "mat0");
    material.setInstanceEffect(new InstanceEffect("#effect0"));
    materials.addMaterial(material);
    collada.setLibraryMaterials(materials);
    return collada;
  }

  private static Effect createEffect(String effectId, String samplerId, String surfaceId, String imageId) {
    Effect effect = new Effect(effectId);
    Lambert lambert = new Lambert();
    EffectAttribute diffuse = new EffectAttribute("diffuse");
    diffuse.setTexture(new Texture(samplerId, "UVMap"));
    lambert.setDiffuse(diffuse);
    effect.setEffectMaterial(lambert);

    NewParam sampler = new NewParam(samplerId);
    Sampler2D sampler2D = new Sampler2D();
    sampler2D.setSource(surfaceId);
    sampler.setSampler2D(sampler2D);
    effect.addNewParam(sampler);

    NewParam surface = new NewParam(surfaceId);
    Surface textureSurface = new Surface("2D");
    textureSurface.setInitFrom(imageId);
    surface.setSurface(textureSurface);
    effect.addNewParam(surface);
    return effect;
  }

  private static File createRootDir(String name) {
    File root = new File("target/collada-material-loader/" + name);
    root.mkdirs();
    return root;
  }

  private static File writeTexture(File root, String fileName) throws Exception {
    File file = new File(root, fileName);
    BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
    ImageIO.write(image, "png", file);
    return file;
  }
}

package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.lgna.story.resourceutilities.exporterutils.collada.Asset;
import org.lgna.story.resourceutilities.exporterutils.collada.COLLADA;
import org.lgna.story.resourceutilities.exporterutils.collada.Effect;
import org.lgna.story.resourceutilities.exporterutils.collada.ObjectFactory;
import org.lgna.story.resourceutilities.exporterutils.collada.UpAxisType;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ColladaParserTargetTest {
  @Test
  public void createAssetIncludesColladaMetadata() {
    ColladaParser parser = new ColladaParser(new ObjectFactory());

    Asset asset = parser.createAsset();

    assertNotNull(asset.getCreated());
    assertNotNull(asset.getModified());
    assertNotNull(asset.getUnit());
    assertEquals("meter", asset.getUnit().getName());
    assertEquals(1.0, asset.getUnit().getMeter(), 0.0001);
    assertEquals(UpAxisType.Y_UP, asset.getUpAxis());
  }

  @Test
  public void createEffectBuildsProfilesForColorAndTexturedMaterials() {
    ColladaParser parser = new ColladaParser(new ObjectFactory());
    Map<Integer, String> materialNames = new HashMap<Integer, String>();
    materialNames.put(0, "material_0");

    TexturedAppearance colorOnly = new TexturedAppearance();
    colorOnly.textureId.setValue(0);
    Effect colorEffect = parser.createEffect(colorOnly, materialNames);

    TexturedAppearance textured = new TexturedAppearance();
    textured.textureId.setValue(0);
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB));
    textured.diffuseColorTexture.setValue(texture);
    textured.isDiffuseColorTextureAlphaBlended.setValue(true);
    Effect texturedEffect = parser.createEffect(textured, materialNames);

    assertEquals("material_0_fx", colorEffect.getId());
    assertFalse(colorEffect.getFxProfileAbstract().isEmpty());
    assertFalse(texturedEffect.getFxProfileAbstract().isEmpty());
  }

  @Test
  public void createAndAddTextureComponentsRegistersLibrariesForTexturedAppearance() {
    ObjectFactory factory = new ObjectFactory();
    ColladaParser parser = new ColladaParser(factory);
    COLLADA collada = factory.createCOLLADA();
    SkeletonVisual visual = new SkeletonVisual();

    TexturedAppearance appearance = new TexturedAppearance();
    appearance.textureId.setValue(0);
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB));
    appearance.diffuseColorTexture.setValue(texture);
    visual.textures.setValue(new TexturedAppearance[]{appearance});

    Map<Integer, String> materialNames = new HashMap<Integer, String>();
    materialNames.put(0, "material_0");
    parser.createAndAddTextureComponents(
        collada,
        visual,
        materialNames,
        idx -> "material_0_diffuseMap",
        idx -> "external_material_0_diffuseMap",
        idx -> "material_0_diffuseMap.png",
        idx -> "material_0_diffuseMap-image",
        idx -> "material_0_shader",
        idx -> "material_0_fx"
    );

    assertEquals(3, collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().size());
  }
}

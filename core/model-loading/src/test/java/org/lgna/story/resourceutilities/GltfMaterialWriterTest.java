package org.lgna.story.resourceutilities;

import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Material;
import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.junit.After;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;

import static org.junit.Assert.*;

public class GltfMaterialWriterTest {
  private final Path outputDirectory = Paths.get("target", "gltf-material-writer-test");

  @After
  public void cleanUp() throws IOException {
    if (Files.exists(outputDirectory)) {
      Files.walk(outputDirectory)
          .sorted(Comparator.reverseOrder())
          .forEach(path -> {
            try {
              Files.delete(path);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
    }
  }

  @Test
  public void createAndAddTextureComponentsWritesImagesAndMaterials() throws Exception {
    Files.createDirectories(outputDirectory);

    TexturedAppearance textured = new TexturedAppearance();
    textured.textureId.setValue(4);
    textured.diffuseColor.setValue(new Color4f(0.25f, 0.5f, 0.75f, 0.5f));
    textured.opacity.setValue(0.8f);
    textured.emissiveColor.setValue(new Color4f(0.1f, 0.2f, 0.3f, 1.0f));
    textured.isDiffuseColorTextureAlphaBlended.setValue(true);
    BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 0, 0x80FFFFFF);
    BufferedImageTexture imageTexture = new BufferedImageTexture();
    imageTexture.setBufferedImage(image);
    textured.diffuseColorTexture.setValue(imageTexture);

    TexturedAppearance translucent = new TexturedAppearance();
    translucent.textureId.setValue(5);
    translucent.opacity.setValue(0.25f);

    TexturedAppearance opaque = new TexturedAppearance();
    opaque.textureId.setValue(6);

    GlTF gltf = new GlTF();
    GltfMaterialWriter writer = new GltfMaterialWriter("robot");
    Map<Integer, Integer> textureMaterialMap = writer.createAndAddTextureComponents(
        outputDirectory, gltf, new TexturedAppearance[]{textured, translucent, opaque});

    assertEquals("robot_material_4_diffuseMap.png", writer.getImageFileName(4));
    assertTrue(Files.exists(outputDirectory.resolve("robot_material_4_diffuseMap.png")));
    assertEquals(3, textureMaterialMap.size());
    assertEquals(Integer.valueOf(0), textureMaterialMap.get(4));
    assertEquals(Integer.valueOf(1), textureMaterialMap.get(5));
    assertEquals(Integer.valueOf(2), textureMaterialMap.get(6));

    assertEquals(1, gltf.getImages().size());
    assertEquals(1, gltf.getTextures().size());
    assertEquals(3, gltf.getMaterials().size());

    Material texturedMaterial = gltf.getMaterials().get(0);
    assertEquals("BLEND", texturedMaterial.getAlphaMode());
    assertArrayEquals(new float[]{0.25f, 0.5f, 0.75f, 0.4f},
        texturedMaterial.getPbrMetallicRoughness().getBaseColorFactor(), 0.000001f);
    assertArrayEquals(new float[]{0.1f, 0.2f, 0.3f}, texturedMaterial.getEmissiveFactor(), 0.000001f);
    assertEquals(Integer.valueOf(0), texturedMaterial.getPbrMetallicRoughness().getBaseColorTexture().getIndex());

    Material translucentMaterial = gltf.getMaterials().get(1);
    assertEquals("BLEND", translucentMaterial.getAlphaMode());
    assertArrayEquals(new float[]{1.0f, 1.0f, 1.0f, 0.25f},
        translucentMaterial.getPbrMetallicRoughness().getBaseColorFactor(), 0.000001f);

    Material opaqueMaterial = gltf.getMaterials().get(2);
    assertEquals(new Material().defaultAlphaMode(), opaqueMaterial.getAlphaMode());
    assertNull(opaqueMaterial.getPbrMetallicRoughness().getBaseColorFactor());
  }

  @Test
  public void writeTextureProducesPngBytes() throws Exception {
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    GltfMaterialWriter.writeTexture(image, outputStream);

    byte[] bytes = outputStream.toByteArray();
    assertTrue(bytes.length > 8);
    assertEquals((byte) 0x89, bytes[0]);
    assertEquals((byte) 'P', bytes[1]);
    assertEquals((byte) 'N', bytes[2]);
    assertEquals((byte) 'G', bytes[3]);
  }
}

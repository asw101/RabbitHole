package org.lgna.story.resourceutilities;

import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Material;
import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GltfMaterialWriterTest {
  @Test
  public void createAndAddTextureComponentsWritesImagesAndTransparentMaterials() throws Exception {
    GlTF gltf = new GlTF();
    Path root = ExporterTestFixtures.workDir("gltf-material-writer");
    TexturedAppearance texture = ExporterTestFixtures.texture(7, true, true);
    texture.diffuseColor.setValue(new Color4f(0.25f, 0.5f, 0.75f, 0.5f));
    texture.emissiveColor.setValue(new Color4f(0.1f, 0.2f, 0.3f, 1.0f));
    texture.opacity.setValue(0.5f);

    GltfMaterialWriter writer = new GltfMaterialWriter("robot");
    Map<Integer, Integer> materialMap = writer.createAndAddTextureComponents(root, gltf, new TexturedAppearance[]{texture});
    Material material = gltf.getMaterials().get(0);

    assertEquals(Integer.valueOf(0), materialMap.get(7));
    assertEquals("robot_material_7_diffuseMap.png", gltf.getImages().get(0).getUri());
    assertTrue(Files.isRegularFile(root.resolve("robot_material_7_diffuseMap.png")));
    assertArrayEquals(new float[]{0.25f, 0.5f, 0.75f, 0.25f}, material.getPbrMetallicRoughness().getBaseColorFactor(), 0.0001f);
    assertArrayEquals(new float[]{0.1f, 0.2f, 0.3f}, material.getEmissiveFactor(), 0.0001f);
    assertEquals("BLEND", material.getAlphaMode());
  }

  @Test
  public void writeTextureProducesPngSignature() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    GltfMaterialWriter.writeTexture(new java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_ARGB), outputStream);

    byte[] bytes = outputStream.toByteArray();
    assertNotNull(bytes);
    assertTrue(bytes.length > 8);
    assertEquals((byte) 0x89, bytes[0]);
    assertEquals((byte) 'P', bytes[1]);
    assertEquals((byte) 'N', bytes[2]);
    assertEquals((byte) 'G', bytes[3]);
  }
}

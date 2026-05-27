package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.BlendShape;
import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.Mesh;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.tweedle.file.ModelManifest;

import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

final class ExporterTestFixtures {
  private ExporterTestFixtures() {
  }

  static SkeletonVisual createVisual(boolean includeTextureImages) {
    SkeletonVisual visual = new SkeletonVisual();
    Joint root = joint("ROOT");
    Joint joint1 = joint("JOINT1");
    Joint joint2 = joint("JOINT2");
    Joint joint3 = joint("JOINT3");
    Joint joint4 = joint("JOINT4");
    Joint joint5 = joint("JOINT5");
    joint1.setParent(root);
    joint2.setParent(root);
    joint3.setParent(root);
    joint4.setParent(root);
    joint5.setParent(root);
    visual.skeleton.setValue(root);

    Mesh staticMesh = createStaticMesh();
    WeightedMesh weightedMesh = createWeightedMesh(root);
    BlendShape blendShape = new BlendShape(0);
    blendShape.vertexBuffer = DoubleBuffer.wrap(new double[]{0.0, 0.0, 0.1, 1.0, 0.0, 0.1, 0.0, 1.0, 0.1});
    blendShape.normalBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f});
    visual.blendShapes.put(weightedMesh, Collections.singletonList(blendShape));

    visual.geometries.setValue(new edu.cmu.cs.dennisc.scenegraph.Geometry[]{staticMesh});
    visual.weightedMeshes.setValue(new WeightedMesh[]{weightedMesh});
    visual.textures.setValue(new TexturedAppearance[]{
        texture(0, includeTextureImages, true),
        texture(1, includeTextureImages, false),
        texture(2, includeTextureImages, false)
    });
    return visual;
  }

  static TexturedAppearance texture(int textureId, boolean includeImage, boolean translucent) {
    TexturedAppearance texture = new TexturedAppearance();
    texture.textureId.setValue(textureId);
    texture.opacity.setValue(1.0f);
    if (includeImage) {
      BufferedImage image = new BufferedImage(2, 2, translucent ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
      image.setRGB(0, 0, translucent ? 0x44FF0000 : 0x00FF00);
      image.setRGB(1, 0, translucent ? 0xFFFFFFFF : 0x0000FF);
      image.setRGB(0, 1, translucent ? 0x8800FF00 : 0xFF0000);
      image.setRGB(1, 1, translucent ? 0x220000FF : 0x00FFFF);
      BufferedImageTexture bufferedImageTexture = new BufferedImageTexture();
      bufferedImageTexture.setBufferedImage(image);
      texture.diffuseColorTexture.setValue(bufferedImageTexture);
      texture.isDiffuseColorTextureAlphaBlended.setValue(translucent);
    }
    return texture;
  }

  static ModelManifest.ModelVariant createVariant(String structure, String textureSet) {
    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.name = "DEFAULT";
    variant.structure = structure;
    variant.textureSet = textureSet;
    return variant;
  }

  static Path workDir(String name) {
    try {
      Path dir = Path.of("target", "test-work", name);
      Files.createDirectories(dir);
      return dir;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Joint joint(String id) {
    Joint joint = new Joint();
    joint.jointID.setValue(id);
    joint.setName(id);
    joint.localTransformation.setValue(AffineMatrix4x4.IDENTITY);
    return joint;
  }

  private static Mesh createStaticMesh() {
    Mesh mesh = new Mesh();
    mesh.setName("staticMesh");
    mesh.textureId.setValue(99);
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[]{
        0.0, 0.0, 0.0,
        1.0, 0.0, 0.0,
        0.0, 1.0, 0.0,
        1.0, 1.0, 0.0,
        2.0, 1.0, 0.0,
        1.0, 2.0, 0.0
    }));
    mesh.normalBuffer.setValue(FloatBuffer.wrap(new float[]{
        0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f,
        0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f
    }));
    mesh.textCoordBuffer.setValue(FloatBuffer.wrap(new float[]{
        0f, 0f, 1f, 0f, 0f, 1f,
        0f, 0f, 1f, 0f, 0f, 1f
    }));
    mesh.indexBuffer.setValue(IntBuffer.wrap(new int[]{0, 1, 2, 3, 4, 5}));
    Collections.addAll(mesh.textureIdArray, 0, 0, 0, 1, 1, 1);
    return mesh;
  }

  private static WeightedMesh createWeightedMesh(Joint root) {
    WeightedMesh mesh = new WeightedMesh();
    mesh.setName("weightedMesh");
    mesh.textureId.setValue(2);
    mesh.skeleton.setValue(root);
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[]{
        0.0, 0.0, 0.0,
        1.0, 0.0, 0.0,
        0.0, 1.0, 0.0
    }));
    mesh.normalBuffer.setValue(FloatBuffer.wrap(new float[]{
        0f, 0f, 1f,
        0f, 0f, 1f,
        0f, 0f, 1f
    }));
    mesh.textCoordBuffer.setValue(FloatBuffer.wrap(new float[]{
        0f, 0f,
        1f, 0f,
        0f, 1f
    }));
    mesh.indexBuffer.setValue(IntBuffer.wrap(new int[]{0, 1, 2}));

    WeightInfo weightInfo = new WeightInfo();
    weightInfo.addReference("ROOT", pair(0.10f, 0.0f, 0.0f));
    weightInfo.addReference("JOINT1", pair(0.20f, 0.30f, 0.0f));
    weightInfo.addReference("JOINT2", pair(0.25f, 0.0f, 0.0f));
    weightInfo.addReference("JOINT3", pair(0.15f, 0.0f, 0.0f));
    weightInfo.addReference("JOINT4", pair(0.17f, 0.0f, 0.0f));
    weightInfo.addReference("JOINT5", pair(0.13f, 0.0f, 0.0f));
    mesh.weightInfo.setValue(weightInfo);
    return mesh;
  }

  private static InverseAbsoluteTransformationWeightsPair pair(float a, float b, float c) {
    return InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[]{a, b, c}, AffineMatrix4x4.IDENTITY);
  }
}

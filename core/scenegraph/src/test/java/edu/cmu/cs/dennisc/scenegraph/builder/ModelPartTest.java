package edu.cmu.cs.dennisc.scenegraph.builder;

import edu.cmu.cs.dennisc.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix3x3;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ModelPartTest {
  private static final double EPSILON = 1.0e-6;

  @Test
  public void newInstanceBuildPreservesHierarchyGeometryAndTexture() {
    Transformable source = createSourceHierarchy();
    IndexedTriangleArray rootGeometry = (IndexedTriangleArray) ((Visual) source.getComponentAt(0)).getGeometry();
    IndexedTriangleArray childGeometry = (IndexedTriangleArray) ((Visual) ((Transformable) source.getComponentAt(1)).getComponentAt(0)).getGeometry();
    BufferedImageTexture texture = (BufferedImageTexture) ((TexturedAppearance) ((Visual) source.getComponentAt(0)).frontFacingAppearance.getValue()).diffuseColorTexture.getValue();
    Set<edu.cmu.cs.dennisc.scenegraph.Geometry> geometries = new LinkedHashSet<>();
    Set<BufferedImageTexture> textures = new LinkedHashSet<>();

    ModelPart part = ModelPart.newInstance(source, geometries, textures);

    assertEquals(2, geometries.size());
    assertEquals(1, textures.size());
    assertEquals(2.0, rootGeometry.vertices.getValue()[0].position.x(), EPSILON);
    assertEquals(3.0, rootGeometry.vertices.getValue()[0].position.y(), EPSILON);
    assertEquals(4.0, rootGeometry.vertices.getValue()[0].position.z(), EPSILON);

    Transformable rebuilt = part.build();
    assertEquals(2, rebuilt.getComponentCount());
    assertEquals(1.0, rebuilt.localTransformation.getValue().translation().x(), EPSILON);
    assertSame(rootGeometry, ((Visual) rebuilt.getComponentAt(0)).getGeometry());
    assertSame(texture, ((TexturedAppearance) ((Visual) rebuilt.getComponentAt(0)).frontFacingAppearance.getValue()).diffuseColorTexture.getValue());
    assertSame(childGeometry, ((Visual) ((Transformable) rebuilt.getComponentAt(1)).getComponentAt(0)).getGeometry());
  }

  @Test
  public void replaceGeometriesUpdatesBuiltHierarchyRecursively() {
    Transformable source = createSourceHierarchy();
    IndexedTriangleArray originalGeometry = (IndexedTriangleArray) ((Visual) source.getComponentAt(0)).getGeometry();
    IndexedTriangleArray childGeometry = (IndexedTriangleArray) ((Visual) ((Transformable) source.getComponentAt(1)).getComponentAt(0)).getGeometry();
    IndexedTriangleArray replacementGeometry = createTriangle(new Point3(9.0, 9.0, 9.0));
    Set<edu.cmu.cs.dennisc.scenegraph.Geometry> geometries = new LinkedHashSet<>();
    Set<BufferedImageTexture> textures = new LinkedHashSet<>();
    ModelPart part = ModelPart.newInstance(source, geometries, textures);

    part.replaceGeometries(Map.of(originalGeometry, replacementGeometry, childGeometry, childGeometry));

    Transformable rebuilt = part.build();
    assertSame(replacementGeometry, ((Visual) rebuilt.getComponentAt(0)).getGeometry());
  }

  private static Transformable createSourceHierarchy() {
    BufferedImageTexture texture = createTexture();
    Transformable root = new Transformable();
    root.setName("root");
    root.localTransformation.setValue(AffineMatrix4x4.createTranslation(1.0, 2.0, 3.0));
    root.addComponent(createVisual(createTriangle(new Point3(1.0, 1.0, 1.0)), texture, Matrix3x3.create(2.0, 0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 4.0)));

    Transformable child = new Transformable();
    child.setName("child");
    child.localTransformation.setValue(AffineMatrix4x4.createTranslation(-1.0, 0.0, 1.0));
    child.addComponent(createVisual(createTriangle(new Point3(0.0, 0.0, 0.0)), texture, Matrix3x3.IDENTITY));
    root.addComponent(child);
    return root;
  }

  private static Visual createVisual(IndexedTriangleArray geometry, BufferedImageTexture texture, Matrix3x3 scale) {
    Visual visual = new Visual();
    TexturedAppearance appearance = new TexturedAppearance();
    appearance.setDiffuseColorTexture(texture);
    visual.frontFacingAppearance.setValue(appearance);
    visual.setGeometry(geometry);
    visual.scale.setValue(scale);
    return visual;
  }

  private static IndexedTriangleArray createTriangle(Point3 origin) {
    IndexedTriangleArray geometry = new IndexedTriangleArray();
    geometry.vertices.setValue(new Vertex[] {
        Vertex.createXYZ(origin),
        Vertex.createXYZ(origin.x() + 1.0, origin.y(), origin.z()),
        Vertex.createXYZ(origin.x(), origin.y() + 1.0, origin.z())
    });
    return geometry;
  }

  private static BufferedImageTexture createTexture() {
    BufferedImageTexture texture = new BufferedImageTexture();
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 0, 0xFF336699);
    texture.setBufferedImage(image);
    return texture;
  }
}

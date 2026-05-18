package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.pattern.event.ReleaseEvent;
import edu.cmu.cs.dennisc.pattern.event.ReleaseListener;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.ClippedZPlane;
import org.alice.math.immutable.Dimension3;
import org.alice.math.immutable.Matrix3x3;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ScenegraphSupplementalCoverageTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void textureMeshAssociationTracksMeshesAndReleasesResources() {
    TexturedAppearance appearance = new TexturedAppearance();
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    appearance.setDiffuseColorTexture(texture);

    WeightedMesh mesh = new WeightedMesh();
    mesh.weightInfo.setValue(new WeightInfo());
    mesh.vertexBuffer.setValue(new double[]{0, 0, 0});

    TextureMeshAssociation association = new TextureMeshAssociation(appearance);
    association.addMesh(mesh);

    final List<Object> released = new ArrayList<Object>();
    ReleaseListener listener = new ReleaseListener() {
      @Override
      public void releasing(ReleaseEvent e) {
      }

      @Override
      public void released(ReleaseEvent e) {
        released.add(e.getTypedSource());
      }
    };
    appearance.addReleaseListener(listener);
    mesh.addReleaseListener(listener);

    assertSame(appearance, association.getAppearance());
    assertSame(mesh, association.getMesh(0));
    assertEquals(1, association.meshCount());
    assertEquals(1, association.getMeshes().size());

    association.release();
    assertTrue(released.contains(appearance));
    assertTrue(released.contains(mesh));
  }

  @Test
  public void transformableVisualDelegatesParentTransformAndBoundingBox() {
    TransformableVisual visual = new TransformableVisual();
    Sphere sphere = new Sphere();
    sphere.radius.setValue(1.0);
    visual.setGeometry(sphere);

    Transformable parent = new Transformable();
    visual.setParent(parent);
    assertSame(parent, visual.getParent());

    Transformable transform = new Transformable();
    transform.localTransformation.setValue(AffineMatrix4x4.createTranslation(10, 20, 30));
    visual.setTransform(transform);
    assertEquals(10.0, visual.getTransformable().getLocalTransformation().translation().x(), EPSILON);

    AxisAlignedBox box = visual.getAxisAlignedMinimumBoundingBox();
    assertEquals(new Point3(9, 19, 29), box.minimum());
    assertEquals(new Point3(11, 21, 31), box.maximum());
  }

  @Test
  public void scalableStandInSpriteAndShapeExposeCurrentBehavior() {
    Scalable scalable = new Scalable();
    scalable.scale.setValue(new Dimension3(2, 3, 4));
    assertEquals(2.0, scalable.getAbsoluteTransformation().orientation().right().x(), EPSILON);
    assertEquals(3.0, scalable.getAbsoluteTransformation().orientation().up().y(), EPSILON);
    assertEquals(4.0, scalable.getAbsoluteTransformation().orientation().backward().z(), EPSILON);

    StandIn standIn = new StandIn();
    Scene vehicle = new Scene();
    standIn.setVehicle(vehicle);
    standIn.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));
    assertSame(vehicle, standIn.getVehicle());
    assertEquals(3.0, standIn.getLocalTransformation().translation().z(), EPSILON);

    Sprite sprite = new Sprite();
    sprite.radius.setValue(2.0);
    AxisAlignedBox spriteBounds = sprite.getAxisAlignedMinimumBoundingBox();
    assertEquals(new Point3(-2, -2, 0), spriteBounds.minimum());
    assertEquals(new Point3(2, 2, 0), spriteBounds.maximum());

    Shape shape = new Shape() {
      @Override
      protected AxisAlignedBox updateBoundingBox() {
        return AxisAlignedBox.NaN;
      }
    };
    try {
      shape.transform(Matrix4x4.IDENTITY);
      fail("shape.transform should throw");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("TODO"));
    }
    try {
      shape.getPlane();
      fail("shape.getPlane should throw");
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("TODO"));
    }
  }

  @Test
  public void simplePropertyOnlyScenegraphClassesRetainDefaults() {
    Background background = new Background();
    ExponentialFog exponentialFog = new ExponentialFog();
    ExponentialSquaredFog exponentialSquaredFog = new ExponentialSquaredFog();
    OrthographicCamera orthographicCamera = new OrthographicCamera();
    Sprite sprite = new Sprite();

    assertEquals(Color4f.WHITE, background.color.getValue());
    assertEquals(1.0, exponentialFog.density.getValue(), EPSILON);
    assertEquals(1.0, exponentialSquaredFog.density.getValue(), EPSILON);
    assertEquals(ClippedZPlane.DEFAULT, orthographicCamera.picturePlane.getValue());
    assertEquals(0.5, sprite.radius.getValue(), EPSILON);
  }
}

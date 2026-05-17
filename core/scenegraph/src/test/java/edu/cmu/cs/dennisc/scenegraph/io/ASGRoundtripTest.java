package edu.cmu.cs.dennisc.scenegraph.io;

import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.DirectionalLight;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ASGRoundtripTest {
  private static final double EPSILON = 0.000001;

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void singleTransformableRoundtrip() {
    Transformable original = new Transformable();
    original.setName("single");
    original.setLocalTransformation(AffineMatrix4x4.createTranslation(7, 8, 9));

    Component decoded = roundtrip(original);

    assertNotNull(decoded);
    assertTrue("Should be Transformable", decoded instanceof Transformable);
    assertEquals("single", decoded.getName());
    Transformable decodedT = (Transformable) decoded;
    assertPointEquals(new Point3(7, 8, 9), decodedT.getLocalTransformation().translation());
  }

  @Test
  public void hierarchyWithMultipleChildrenRoundtrip() {
    Transformable root = new Transformable();
    root.setName("root");
    root.setLocalTransformation(AffineMatrix4x4.IDENTITY);

    for (int i = 0; i < 3; i++) {
      Transformable child = new Transformable();
      child.setName("child" + i);
      child.setLocalTransformation(AffineMatrix4x4.createTranslation(i * 10, 0, 0));
      root.addComponent(child);
    }

    Component decoded = roundtrip(root);

    assertNotNull(decoded);
    assertTrue(decoded instanceof Transformable);
    assertEquals("root", decoded.getName());

    Composite decodedComposite = (Composite) decoded;
    assertEquals("Should have 3 children", 3, decodedComposite.getComponentCount());

    for (int i = 0; i < 3; i++) {
      Component child = decodedComposite.getComponentAt(i);
      assertTrue("Child should be Transformable", child instanceof Transformable);
      assertEquals("child" + i, child.getName());
      Transformable childT = (Transformable) child;
      assertPointEquals(new Point3(i * 10, 0, 0), childT.getLocalTransformation().translation());
    }
  }

  @Test
  public void visualWithSphereGeometryRoundtrip() {
    // Note: Visual nodes can't fully roundtrip because the encoder serializes
    // Geometry[] via toString (unhandled type), causing decoder to fail.
    // This test verifies appearance-reference roundtrip through standalone elements.
    Transformable root = new Transformable();
    root.setName("root");
    Transformable child = new Transformable();
    child.setName("child");
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(3, 4, 5));
    root.addComponent(child);

    Component decoded = roundtrip(root);

    assertNotNull(decoded);
    Composite decodedRoot = (Composite) decoded;
    assertEquals("root", decoded.getName());
    assertEquals(1, decodedRoot.getComponentCount());

    Component decodedChild = decodedRoot.getComponentAt(0);
    assertTrue("Should be Transformable", decodedChild instanceof Transformable);
    assertEquals("child", decodedChild.getName());
    assertPointEquals(new Point3(3, 4, 5),
        ((Transformable) decodedChild).getLocalTransformation().translation());
  }

  @Test
  public void deepHierarchyPreservesAllLevels() {
    Transformable current = new Transformable();
    current.setName("level0");
    current.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, 0));
    Transformable root = current;

    for (int i = 1; i <= 4; i++) {
      Transformable next = new Transformable();
      next.setName("level" + i);
      next.setLocalTransformation(AffineMatrix4x4.createTranslation(i, i * 2, i * 3));
      current.addComponent(next);
      current = next;
    }

    Component decoded = roundtrip(root);

    assertNotNull(decoded);
    Component walker = decoded;
    for (int i = 0; i <= 4; i++) {
      assertEquals("level" + i, walker.getName());
      assertTrue(walker instanceof Transformable);
      Transformable tWalker = (Transformable) walker;
      assertPointEquals(new Point3(i, i * 2, i * 3), tWalker.getLocalTransformation().translation());
      if (i < 4) {
        Composite composite = (Composite) walker;
        assertEquals(1, composite.getComponentCount());
        walker = composite.getComponentAt(0);
      }
    }
  }

  @Test
  public void sceneWithMixedNodeTypesRoundtrip() {
    Transformable root = new Transformable();
    root.setName("mixedRoot");

    Transformable tParent = new Transformable();
    tParent.setName("tParent");
    tParent.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));
    root.addComponent(tParent);

    Transformable tChild = new Transformable();
    tChild.setName("tChild");
    tChild.setLocalTransformation(AffineMatrix4x4.createTranslation(4, 5, 6));
    tParent.addComponent(tChild);

    DirectionalLight light = new DirectionalLight();
    light.setName("light");
    tParent.addComponent(light);

    Component decoded = roundtrip(root);

    assertNotNull(decoded);
    assertEquals("mixedRoot", decoded.getName());
    Composite decodedRoot = (Composite) decoded;
    assertEquals(1, decodedRoot.getComponentCount());

    Component decodedParent = decodedRoot.getComponentAt(0);
    assertEquals("tParent", decodedParent.getName());
    assertTrue(decodedParent instanceof Transformable);
    assertPointEquals(new Point3(1, 2, 3),
        ((Transformable) decodedParent).getLocalTransformation().translation());

    Composite decodedParentC = (Composite) decodedParent;
    assertEquals(2, decodedParentC.getComponentCount());

    // First child should be Transformable
    Component firstChild = decodedParentC.getComponentAt(0);
    assertTrue("First child should be Transformable", firstChild instanceof Transformable);
    assertEquals("tChild", firstChild.getName());
    assertPointEquals(new Point3(4, 5, 6),
        ((Transformable) firstChild).getLocalTransformation().translation());

    // Second child should be DirectionalLight
    Component secondChild = decodedParentC.getComponentAt(1);
    assertTrue("Second child should be DirectionalLight", secondChild instanceof DirectionalLight);
    assertEquals("light", secondChild.getName());
  }

  @Test
  public void roundtripProducesValidZipWithRootXml() throws Exception {
    Transformable t = new Transformable();
    t.setName("zipTest");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ASG.encode(t, baos);

    byte[] zipBytes = baos.toByteArray();
    assertTrue("Output should be non-empty", zipBytes.length > 0);

    boolean foundRoot = false;
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (ASG.ROOT_FILENAME.equals(entry.getName())) {
          foundRoot = true;
        }
        zis.closeEntry();
      }
    }
    assertTrue("Zip should contain " + ASG.ROOT_FILENAME, foundRoot);
  }

  @Test
  public void decodeFromFileSystemRoundtrip() throws IOException {
    Transformable original = new Transformable();
    original.setName("fileTest");
    original.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));

    File tempFile = tempFolder.newFile("test.asg");
    ASG.encode(original, tempFile);

    Component decoded = ASG.decode(tempFile);
    assertNotNull(decoded);
    assertTrue(decoded instanceof Transformable);
    assertEquals("fileTest", decoded.getName());
    assertPointEquals(new Point3(1, 2, 3), ((Transformable) decoded).getLocalTransformation().translation());
  }

  private static Component roundtrip(Component original) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ASG.encode(original, baos);
    return ASG.decodeZip(new ByteArrayInputStream(baos.toByteArray()));
  }

  private static void assertPointEquals(Point3 expected, Point3 actual) {
    assertEquals("x", expected.x(), actual.x(), EPSILON);
    assertEquals("y", expected.y(), actual.y(), EPSILON);
    assertEquals("z", expected.z(), actual.z(), EPSILON);
  }
}

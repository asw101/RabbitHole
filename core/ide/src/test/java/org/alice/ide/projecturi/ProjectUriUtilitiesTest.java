package org.alice.ide.projecturi;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

public class ProjectUriUtilitiesTest {

  @Test
  public void projectSnapshotWithUri() {
    URI uri = URI.create("file:///nonexistent/project.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri);
    assertNotNull(snapshot);
    assertEquals(uri, snapshot.getUri());
    assertTrue(snapshot.hasUri());
  }

  @Test
  public void projectSnapshotGetText() {
    URI uri = URI.create("file:///some/path/MyProject.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri);
    assertNotNull(snapshot.getText());
  }

  @Test
  public void projectSnapshotGetIcon() {
    URI uri = URI.create("file:///nonexistent/test.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri);
    assertNotNull(snapshot.getIcon());
  }

  @Test
  public void projectSnapshotIsVrProjectDefault() {
    URI uri = URI.create("file:///nonexistent/test.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri);
    assertFalse(snapshot.isVrProject());
  }

  @Test
  public void projectSnapshotEquality() {
    URI uri1 = URI.create("file:///test/project.a3p");
    URI uri2 = URI.create("file:///test/project.a3p");
    ProjectSnapshot snap1 = new ProjectSnapshot(uri1);
    ProjectSnapshot snap2 = new ProjectSnapshot(uri2);
    assertEquals(snap1, snap2);
    assertEquals(snap1.hashCode(), snap2.hashCode());
  }

  @Test
  public void projectSnapshotInequality() {
    URI uri1 = URI.create("file:///test/project1.a3p");
    URI uri2 = URI.create("file:///test/project2.a3p");
    ProjectSnapshot snap1 = new ProjectSnapshot(uri1);
    ProjectSnapshot snap2 = new ProjectSnapshot(uri2);
    assertNotEquals(snap1, snap2);
  }

  @Test
  public void projectSnapshotEqualsSelf() {
    URI uri = URI.create("file:///test/project.a3p");
    ProjectSnapshot snap = new ProjectSnapshot(uri);
    assertEquals(snap, snap);
  }

  @Test
  public void projectSnapshotNotEqualsNull() {
    URI uri = URI.create("file:///test/project.a3p");
    ProjectSnapshot snap = new ProjectSnapshot(uri);
    assertNotEquals(null, snap);
  }

  @Test
  public void projectSnapshotNotEqualsOtherType() {
    URI uri = URI.create("file:///test/project.a3p");
    ProjectSnapshot snap = new ProjectSnapshot(uri);
    assertNotEquals("string", snap);
  }

  @Test
  public void projectSnapshotWithTextAndIcon() {
    URI uri = URI.create("file:///test/proj.a3p");
    javax.swing.Icon icon = new javax.swing.ImageIcon();
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "Test Project", icon);
    assertEquals("Test Project", snapshot.getText());
    assertSame(icon, snapshot.getIcon());
    assertFalse(snapshot.isVrProject());
  }

  @Test
  public void projectSnapshotHasValidUriForNonBlankScheme() {
    URI uri = URI.create("file:///test/proj.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri);
    assertFalse(snapshot.hasValidUri());
  }

  @Test
  public void projectSnapshotGetUriFragment() {
    URI uri = URI.create("file:///test/proj.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri);
    assertNull(snapshot.getUriFragment());
  }

  @Test
  public void projectSnapshotThumbnailNull() {
    URI uri = URI.create("file:///nonexistent/test.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri);
    assertNull(snapshot.getThumbnail());
  }

  @Test
  public void projectSnapshotRelativeUri() {
    URI uri = URI.create("relative/path/test.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri);
    assertNotNull(snapshot);
    assertEquals("relative/path/test.a3p", snapshot.getText());
  }
}

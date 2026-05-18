package org.alice.ide.projecturi;

import org.alice.stageide.openprojectpane.models.TemplateUriState;
import org.junit.Test;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Extended tests for {@link ProjectSnapshot}.
 *
 * <p>Covers {@code hasValidUri()}, {@code isVrProject()} variants,
 * the three-argument constructor, null URI handling, equality/hashCode
 * edge cases, and the {@code getUriFragment()} method.
 */
public class ProjectSnapshotExtendedTest {

  // ── hasValidUri ───────────────────────────────────────────────────

  @Test
  public void hasValidUriReturnsTrueForGenSchemeUri() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "body", null);
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertTrue(snapshot.hasValidUri());
  }

  @Test
  public void hasValidUriReturnsFalseForFileSchemeUri() {
    URI uri = URI.create("file:///some/path.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertFalse(snapshot.hasValidUri());
  }

  @Test
  public void hasValidUriReturnsFalseForHttpSchemeUri() {
    URI uri = URI.create("http://example.com/path.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertFalse(snapshot.hasValidUri());
  }

  @Test
  public void hasValidUriReturnsFalseForNullUri() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    assertFalse(snapshot.hasValidUri());
  }

  @Test
  public void hasValidUriReturnsFalseForOpaqueUri() {
    URI uri = URI.create("mailto:user@example.com");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertFalse(snapshot.hasValidUri());
  }

  // ── hasUri ────────────────────────────────────────────────────────

  @Test
  public void hasUriReturnsTrueForNonNullUri() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertTrue(snapshot.hasUri());
  }

  @Test
  public void hasUriReturnsFalseForNullUri() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    assertFalse(snapshot.hasUri());
  }

  // ── isVrProject ───────────────────────────────────────────────────

  @Test
  public void isVrProjectReturnsFalseForWindowCamera() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertFalse(snapshot.isVrProject());
  }

  @Test
  public void isVrProjectReturnsFalseForNullSceneCamera() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    // sceneCamera defaults to null for null-URI constructor
    assertFalse(snapshot.isVrProject());
  }

  // ── Three-argument constructor ────────────────────────────────────

  @Test
  public void threeArgConstructorSetsFields() {
    URI uri = URI.create("file:///virtual/test.a3p");
    Icon icon = new ImageIcon();
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "My Project", icon);

    assertEquals(uri, snapshot.getUri());
    assertEquals("My Project", snapshot.getText());
    assertSame(icon, snapshot.getIcon());
  }

  @Test
  public void threeArgConstructorSetsCameraToWindowCamera() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertFalse("Default camera should be WindowCamera, not VR",
        snapshot.isVrProject());
  }

  @Test
  public void threeArgConstructorWithNullIcon() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", null);
    assertNull(snapshot.getIcon());
  }

  @Test
  public void threeArgConstructorWithNullText() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), null, new ImageIcon());
    assertNull(snapshot.getText());
  }

  @Test
  public void threeArgConstructorWithEmptyText() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "", new ImageIcon());
    assertEquals("", snapshot.getText());
  }

  // ── null URI handling ─────────────────────────────────────────────

  @Test
  public void nullUriConstructorSetsAllFieldsSafely() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    assertNull(snapshot.getUri());
    assertEquals("test", snapshot.getText());
    assertNull(snapshot.getIcon());
    assertNull(snapshot.getThumbnail());
    assertFalse(snapshot.hasUri());
    assertFalse(snapshot.hasValidUri());
    assertFalse(snapshot.isVrProject());
  }

  // ── getThumbnail ──────────────────────────────────────────────────

  @Test
  public void getThumbnailDefaultsToNull() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertNull(snapshot.getThumbnail());
  }

  // ── getUriFragment ────────────────────────────────────────────────

  @Test
  public void getUriFragmentReturnsNullForNonGenScheme() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p#SomeFragment"), "test", new ImageIcon());
    assertNull(snapshot.getUriFragment());
  }

  @Test
  public void getUriFragmentReturnsNullForNullUri() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    assertNull(snapshot.getUriFragment());
  }

  @Test
  public void getUriFragmentReturnsNullForGenSchemeWithNoFragment() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "body", null);
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertNull(snapshot.getUriFragment());
  }

  // ── equals edge cases ─────────────────────────────────────────────

  @Test
  public void equalsReturnsTrueForSameUri() {
    URI uri = URI.create("file:///test.a3p");
    ProjectSnapshot a = new ProjectSnapshot(uri, "one", new ImageIcon());
    ProjectSnapshot b = new ProjectSnapshot(uri, "two", new ImageIcon());
    assertEquals(a, b);
  }

  @Test
  public void equalsReturnsFalseForDifferentUri() {
    ProjectSnapshot a = new ProjectSnapshot(
        URI.create("file:///one.a3p"), "one", new ImageIcon());
    ProjectSnapshot b = new ProjectSnapshot(
        URI.create("file:///two.a3p"), "two", new ImageIcon());
    assertNotEquals(a, b);
  }

  @Test
  public void equalsReturnsTrueForSameObject() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertEquals(snapshot, snapshot);
  }

  @Test
  public void equalsReturnsFalseForNull() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertNotEquals(snapshot, null);
  }

  @Test
  public void equalsReturnsFalseForDifferentType() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertNotEquals(snapshot, "not a snapshot");
  }

  @Test
  public void equalsReturnsFalseForDifferentObjectType() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertNotEquals(snapshot, Integer.valueOf(42));
  }

  @Test
  public void equalsWithDifferentTextSameUriIsEqual() {
    URI uri = URI.create("file:///shared.a3p");
    ProjectSnapshot a = new ProjectSnapshot(uri, "Text A", new ImageIcon());
    ProjectSnapshot b = new ProjectSnapshot(uri, "Text B", new ImageIcon());
    assertEquals("Equality based on URI only", a, b);
  }

  @Test
  public void equalsWithDifferentIconSameUriIsEqual() {
    URI uri = URI.create("file:///shared.a3p");
    ProjectSnapshot a = new ProjectSnapshot(uri, "test", new ImageIcon());
    ProjectSnapshot b = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertEquals("Equality based on URI only", a, b);
  }

  // ── hashCode edge cases ───────────────────────────────────────────

  @Test
  public void hashCodeSameUriSameHash() {
    URI uri = URI.create("file:///test.a3p");
    ProjectSnapshot a = new ProjectSnapshot(uri, "one", new ImageIcon());
    ProjectSnapshot b = new ProjectSnapshot(uri, "two", new ImageIcon());
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void hashCodeDifferentUriTypicallyDifferentHash() {
    ProjectSnapshot a = new ProjectSnapshot(
        URI.create("file:///one.a3p"), "one", new ImageIcon());
    ProjectSnapshot b = new ProjectSnapshot(
        URI.create("file:///two.a3p"), "two", new ImageIcon());
    // Not guaranteed but very likely
    assertNotEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void hashCodeNullUriReturnsConstant() {
    ProjectSnapshot a = new ProjectSnapshot(null, "one", null);
    ProjectSnapshot b = new ProjectSnapshot(null, "two", null);
    assertEquals(a.hashCode(), b.hashCode());
    assertEquals(217, a.hashCode());
  }

  @Test
  public void hashCodeConsistentAcrossMultipleCalls() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    int hash1 = snapshot.hashCode();
    int hash2 = snapshot.hashCode();
    assertEquals(hash1, hash2);
  }

  // ── URI-related getters ───────────────────────────────────────────

  @Test
  public void getUriReturnsConstructedUri() {
    URI uri = URI.create("file:///virtual/test.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertEquals(uri, snapshot.getUri());
  }

  @Test
  public void getUriReturnsNullForNullConstruction() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    assertNull(snapshot.getUri());
  }

  // ── Text and Icon getters ─────────────────────────────────────────

  @Test
  public void getTextReturnsConstructedText() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "My Project", new ImageIcon());
    assertEquals("My Project", snapshot.getText());
  }

  @Test
  public void getIconReturnsConstructedIcon() {
    Icon icon = new ImageIcon();
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", icon);
    assertSame(icon, snapshot.getIcon());
  }

  // ── Structural checks ────────────────────────────────────────────

  @Test
  public void classIsNotAbstract() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(
        ProjectSnapshot.class.getModifiers()));
  }

  @Test
  public void classIsPublic() {
    assertTrue(java.lang.reflect.Modifier.isPublic(
        ProjectSnapshot.class.getModifiers()));
  }
}

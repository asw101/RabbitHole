package org.alice.ide.projecturi;

import org.alice.stageide.openprojectpane.models.TemplateUriState;
import org.junit.Test;
import org.lgna.project.Project;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Extended tests for {@link ProjectSnapshot} covering gaps not addressed
 * by {@link ProjectSnapshotTest}:
 * <ul>
 *   <li>{@code hasValidUri()} — scheme-based validation</li>
 *   <li>{@code getUriFragment()} — Template enum resolution</li>
 *   <li>Null-URI equals/hashCode edge cases</li>
 *   <li>VR camera type via reflection</li>
 * </ul>
 *
 * <p>JUnit 4, headless. Uses 3-arg constructor to avoid ZIP I/O.
 */
public class ProjectSnapshotExtendedTest {

  // ════════════════════════════════════════════════════════════════
  // hasValidUri tests
  // ════════════════════════════════════════════════════════════════

  @Test
  public void hasValidUri_genScheme_true() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "specific", "/path", "GRASS");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertTrue("gen: scheme makes hasValidUri true", snapshot.hasValidUri());
  }

  @Test
  public void hasValidUri_fileScheme_false() {
    URI uri = URI.create("file:///virtual/test.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertFalse("file: scheme makes hasValidUri false", snapshot.hasValidUri());
  }

  @Test
  public void hasValidUri_httpScheme_false() {
    URI uri = URI.create("http://example.com/test.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertFalse("http: scheme makes hasValidUri false", snapshot.hasValidUri());
  }

  @Test
  public void hasValidUri_nullUri_false() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    assertFalse("null URI makes hasValidUri false", snapshot.hasValidUri());
  }

  @Test
  public void hasValidUri_opaqueGenUri_true() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "//host/path", "SNOW");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertTrue("Opaque gen: URI still valid", snapshot.hasValidUri());
  }

  @Test
  public void hasValidUri_emptyScheme_false() {
    // URI with no scheme parses as relative
    URI uri = URI.create("test.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertFalse("No scheme makes hasValidUri false", snapshot.hasValidUri());
  }

  // ════════════════════════════════════════════════════════════════
  // getUriFragment tests
  // ════════════════════════════════════════════════════════════════

  @Test
  public void getUriFragment_validTemplate_returnsTemplate() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "specific", "/path", "GRASS");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertEquals(TemplateUriState.Template.GRASS, snapshot.getUriFragment());
  }

  @Test
  public void getUriFragment_snowTemplate_returnsSnow() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "specific", "/path", "SNOW");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertEquals(TemplateUriState.Template.SNOW, snapshot.getUriFragment());
  }

  @Test
  public void getUriFragment_moonTemplate_returnsMoon() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "specific", "/path", "MOON");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertEquals(TemplateUriState.Template.MOON, snapshot.getUriFragment());
  }

  @Test
  public void getUriFragment_roomTemplate_returnsRoom() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "specific", "/path", "ROOM");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertEquals(TemplateUriState.Template.ROOM, snapshot.getUriFragment());
  }

  @Test
  public void getUriFragment_nullFragment_returnsNull() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "specific", "/path", null);
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertNull("Null fragment returns null", snapshot.getUriFragment());
  }

  @Test
  public void getUriFragment_invalidUri_returnsNull() {
    URI uri = URI.create("file:///virtual/test.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertNull("Non-gen scheme returns null", snapshot.getUriFragment());
  }

  @Test
  public void getUriFragment_nullUri_returnsNull() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    assertNull("Null URI returns null", snapshot.getUriFragment());
  }

  @Test(expected = IllegalArgumentException.class)
  public void getUriFragment_unknownTemplateFragment_throwsIAE() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "specific", "/path", "NONEXISTENT_TEMPLATE");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    snapshot.getUriFragment(); // Should throw IllegalArgumentException from valueOf
  }

  // ════════════════════════════════════════════════════════════════
  // Null URI equals/hashCode edge cases
  // ════════════════════════════════════════════════════════════════

  @Test
  public void equals_nullUri_equalsOtherNullUri() {
    ProjectSnapshot first = new ProjectSnapshot(null, "one", null);
    ProjectSnapshot second = new ProjectSnapshot(null, "two", null);
    // This tests the production code's behavior: uri == ps.uri evaluates to
    // null == null which is true, so they should be equal
    assertEquals(first, second);
  }

  @Test
  public void equals_nullUri_notEqualsNonNullUri() {
    ProjectSnapshot withUri = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "with", new ImageIcon());
    ProjectSnapshot withoutUri = new ProjectSnapshot(null, "without", null);
    assertNotEquals(withUri, withoutUri);
  }

  @Test
  public void hashCode_nullUri_returns217() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    assertEquals("Null URI hashCode sentinel", 217, snapshot.hashCode());
  }

  @Test
  public void hashCode_nullUri_consistent() {
    ProjectSnapshot first = new ProjectSnapshot(null, "one", null);
    ProjectSnapshot second = new ProjectSnapshot(null, "two", null);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  public void equals_null_returnsFalse() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertNotEquals(snapshot, null);
  }

  // ════════════════════════════════════════════════════════════════
  // 3-arg constructor sets sceneCamera to WindowCamera
  // ════════════════════════════════════════════════════════════════

  @Test
  public void threeArgConstructor_setsWindowCamera() throws Exception {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    Field sceneCameraField = ProjectSnapshot.class.getDeclaredField("sceneCamera");
    sceneCameraField.setAccessible(true);
    Project.SceneCameraType camera = (Project.SceneCameraType) sceneCameraField.get(snapshot);
    assertEquals(Project.SceneCameraType.WindowCamera, camera);
  }

  @Test
  public void threeArgConstructor_setsText() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "My Project", new ImageIcon());
    assertEquals("My Project", snapshot.getText());
  }

  @Test
  public void threeArgConstructor_setsIcon() {
    Icon icon = new ImageIcon();
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", icon);
    assertEquals(icon, snapshot.getIcon());
  }

  @Test
  public void threeArgConstructor_thumbnailIsNull() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertNull(snapshot.getThumbnail());
  }

  // ════════════════════════════════════════════════════════════════
  // isVrProject via reflection
  // ════════════════════════════════════════════════════════════════

  @Test
  public void isVrProject_windowCamera_false() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertFalse(snapshot.isVrProject());
  }

  @Test
  public void isVrProject_vrCamera_true() throws Exception {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    Field sceneCameraField = ProjectSnapshot.class.getDeclaredField("sceneCamera");
    sceneCameraField.setAccessible(true);
    sceneCameraField.set(snapshot, Project.SceneCameraType.VRHeadset);
    assertTrue(snapshot.isVrProject());
  }

  // ════════════════════════════════════════════════════════════════
  // hasUri additional edge cases
  // ════════════════════════════════════════════════════════════════

  @Test
  public void hasUri_afterThreeArgWithUri_true() {
    ProjectSnapshot snapshot = new ProjectSnapshot(
        URI.create("file:///test.a3p"), "test", new ImageIcon());
    assertTrue(snapshot.hasUri());
  }

  @Test
  public void hasUri_afterThreeArgWithNull_false() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    assertFalse(snapshot.hasUri());
  }

  // ════════════════════════════════════════════════════════════════
  // equals contract completeness
  // ════════════════════════════════════════════════════════════════

  @Test
  public void equals_reflexive() {
    URI uri = URI.create("file:///test.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertEquals(snapshot, snapshot);
  }

  @Test
  public void equals_symmetric() {
    URI uri = URI.create("file:///test.a3p");
    ProjectSnapshot a = new ProjectSnapshot(uri, "one", new ImageIcon());
    ProjectSnapshot b = new ProjectSnapshot(uri, "two", new ImageIcon());
    assertEquals(a, b);
    assertEquals(b, a);
  }

  @Test
  public void equals_transitive() {
    URI uri = URI.create("file:///test.a3p");
    ProjectSnapshot a = new ProjectSnapshot(uri, "one", new ImageIcon());
    ProjectSnapshot b = new ProjectSnapshot(uri, "two", new ImageIcon());
    ProjectSnapshot c = new ProjectSnapshot(uri, "three", new ImageIcon());
    assertEquals(a, b);
    assertEquals(b, c);
    assertEquals(a, c);
  }

  @Test
  public void equals_differentTextSameUri_equal() {
    URI uri = URI.create("file:///same.a3p");
    ProjectSnapshot a = new ProjectSnapshot(uri, "Text A", new ImageIcon());
    ProjectSnapshot b = new ProjectSnapshot(uri, "Text B", new ImageIcon());
    assertEquals("Same URI means equal regardless of text", a, b);
  }

  @Test
  public void equals_differentIconSameUri_equal() {
    URI uri = URI.create("file:///same.a3p");
    ProjectSnapshot a = new ProjectSnapshot(uri, "test", new ImageIcon());
    ProjectSnapshot b = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertEquals("Same URI means equal regardless of icon", a, b);
  }

  // ════════════════════════════════════════════════════════════════
  // getUri edge cases
  // ════════════════════════════════════════════════════════════════

  @Test
  public void getUri_nullUri_returnsNull() {
    ProjectSnapshot snapshot = new ProjectSnapshot(null, "test", null);
    assertNull(snapshot.getUri());
  }

  @Test
  public void getUri_returnsExactUri() {
    URI uri = URI.create("file:///exact.a3p");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    assertEquals(uri, snapshot.getUri());
  }

  // ════════════════════════════════════════════════════════════════
  // Template enum coverage
  // ════════════════════════════════════════════════════════════════

  @Test
  public void templateEnum_allValues_existInEnum() {
    TemplateUriState.Template[] templates = TemplateUriState.Template.values();
    assertTrue("Template enum has entries", templates.length > 0);
    // Verify a sampling of expected values
    assertNotNull(TemplateUriState.Template.valueOf("GRASS"));
    assertNotNull(TemplateUriState.Template.valueOf("SNOW"));
    assertNotNull(TemplateUriState.Template.valueOf("MOON"));
    assertNotNull(TemplateUriState.Template.valueOf("MARS"));
    assertNotNull(TemplateUriState.Template.valueOf("ROOM"));
    assertNotNull(TemplateUriState.Template.valueOf("DESERT"));
  }

  @Test
  public void templateEnum_getSurfaceAppearance_delegatesToGetUriFragment() throws URISyntaxException {
    URI uri = new URI(TemplateUriState.BLANK_SCHEME, "specific", "/path", "GRASS");
    ProjectSnapshot snapshot = new ProjectSnapshot(uri, "test", new ImageIcon());
    TemplateUriState.Template result = TemplateUriState.Template.getSurfaceAppearance(snapshot);
    assertEquals(TemplateUriState.Template.GRASS, result);
  }

  @Test
  public void blankScheme_isGen() {
    assertEquals("gen", TemplateUriState.BLANK_SCHEME);
  }
}

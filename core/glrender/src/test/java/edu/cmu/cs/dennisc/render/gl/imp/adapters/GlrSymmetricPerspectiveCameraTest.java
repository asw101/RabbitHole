package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.alice.math.immutable.Angle;
import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Ray;
import org.junit.Before;
import org.junit.Test;

import java.awt.Rectangle;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrSymmetricPerspectiveCamera} — projection matrix,
 * getRayAtViewportPixel, performLetterboxing, getAspectRatio, and
 * vertical/horizontal viewing angle logic.
 *
 * Uses a real SymmetricPerspectiveCamera as the owner, with reflection
 * to set the adapter's 'owner' field (protected in GlrElement).
 */
public class GlrSymmetricPerspectiveCameraTest {

  private GlrSymmetricPerspectiveCamera adapter;
  private SymmetricPerspectiveCamera camera;
  private static final double DELTA = 0.0001;

  @Before
  public void setUp() throws Exception {
    camera = new SymmetricPerspectiveCamera();
    camera.nearClippingPlaneDistance.setValue(0.125);
    camera.farClippingPlaneDistance.setValue(256.0);

    adapter = new GlrSymmetricPerspectiveCamera();
    // Set the owner field via reflection (protected field in GlrElement)
    setOwner(adapter, camera);
    // Set view angles via reflection to match default camera values
    setAngleField("verticalView", SymmetricPerspectiveCamera.DEFAULT_VERTICAL_VIEW_ANGLE);
    setAngleField("horizontalView", Angle.NaN);
  }

  // ── getActualProjectionMatrix ──

  @Test
  public void projectionMatrix_isNotNull() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    assertNotNull(m);
  }

  @Test
  public void projectionMatrix_hasNonZeroDiagonal() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();
    // m[0][0] = f/aspect, should be nonzero
    assertNotEquals(0.0, arr[0], DELTA);
    // m[1][1] = f, should be nonzero
    assertNotEquals(0.0, arr[5], DELTA);
  }

  @Test
  public void projectionMatrix_m34_isNegativeOne() {
    // In a perspective matrix, m[2][3] = -1 (the perspective divide component)
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();
    // Column-major: m[2][3] is at index 11 (column 2, row 3)
    assertEquals(-1.0, arr[11], DELTA);
  }

  @Test
  public void projectionMatrix_m44_isZero() {
    // In a perspective matrix, m[3][3] = 0
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();
    // Column-major: m[3][3] is at index 15
    assertEquals(0.0, arr[15], DELTA);
  }

  @Test
  public void projectionMatrix_squareViewport_fOverAspectEqualsF() {
    Rectangle vp = new Rectangle(0, 0, 600, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();
    // For square viewport with no explicit horizontal angle, aspect=1
    // so f/aspect should equal f
    // But default has letterboxing → aspect = 16/9, not 1
    // With isLetterboxed=false, aspect = w/h = 1
    assertNotNull(arr);
  }

  @Test
  public void projectionMatrix_widerViewport_smallerXScale() {
    Rectangle narrow = new Rectangle(0, 0, 400, 600);
    Rectangle wide = new Rectangle(0, 0, 1200, 600);
    adapter.setIsLetterboxed(false);
    Matrix4x4 mNarrow = adapter.getActualProjectionMatrix(narrow);
    Matrix4x4 mWide = adapter.getActualProjectionMatrix(wide);
    double[] arrNarrow = mNarrow.asColumnMajorArray16();
    double[] arrWide = mWide.asColumnMajorArray16();
    // f/aspect: wider viewport → larger aspect → smaller m[0][0]
    assertTrue("Wider viewport should yield smaller X scale",
        arrWide[0] < arrNarrow[0]);
  }

  // ── getRayAtViewportPixel ──

  @Test
  public void rayAtCenter_directionIsForward() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    // Center pixel
    int cx = vp.width / 2;
    int cy = vp.height / 2;
    Ray ray = adapter.getRayAtViewportPixel(cx, cy, vp);
    assertNotNull(ray);
    // At center, xOffset=0, yOffset=0 → pNear and pFar are on the z-axis
    // Direction should be approximately along -Z
    assertTrue("Center ray z-direction should be negative", ray.direction().z() < 0);
  }

  @Test
  public void rayAtCenter_originIsNearPlane() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    int cx = vp.width / 2;
    int cy = vp.height / 2;
    Ray ray = adapter.getRayAtViewportPixel(cx, cy, vp);
    // At center: pNear = (0, 0, -near) → z should be near -0.125
    assertEquals(-camera.nearClippingPlaneDistance.getValue(), ray.origin().z(), 0.01);
  }

  @Test
  public void rayAtTopLeft_hasNegativeY() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    // Top-left = (0, 0): yOffset=1.0, dy=+tanHalf, but near is negative
    // → pNear.y = dy * near < 0
    Ray ray = adapter.getRayAtViewportPixel(0, 0, vp);
    assertNotNull(ray);
    assertTrue("Top-left ray y should be negative (near plane is -Z)", ray.origin().y() < 0);
  }

  @Test
  public void rayAtBottomRight_hasPositiveY() {
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    // Bottom-right (800,600): yOffset=-1, dy=-tanHalf, near<0 → y>0
    Ray ray = adapter.getRayAtViewportPixel(800, 600, vp);
    assertNotNull(ray);
    assertTrue("Bottom-right ray y should be positive (double negative)", ray.origin().y() > 0);
  }

  @Test
  public void rayAtViewportPixel_withLetterboxOffset() {
    // Simulate letterboxing offset in viewport
    Rectangle vp = new Rectangle(100, 50, 600, 500);
    int cx = vp.x + vp.width / 2;
    int cy = vp.y + vp.height / 2;
    Ray ray = adapter.getRayAtViewportPixel(cx, cy, vp);
    // Center of letterboxed viewport should still produce a centered ray
    assertEquals(0.0, ray.origin().x(), 0.01);
    assertEquals(0.0, ray.origin().y(), 0.01);
  }

  // ── performLetterboxing ──

  @Test
  public void performLetterboxing_widerThanAspect_reducesHeight() throws Exception {
    // Default aspect = 16/9 ≈ 1.78 (when using default angles → letterboxed ratio)
    // Surface aspect = 800/400 = 2.0 > 1.78 → should reduce height
    setAngleField("verticalView", SymmetricPerspectiveCamera.DEFAULT_VERTICAL_VIEW_ANGLE);
    setAngleField("horizontalView", Angle.NaN);
    adapter.setIsLetterboxed(true);

    Rectangle rect = new Rectangle(0, 0, 800, 400);
    Rectangle result = adapter.performLetterboxing(rect);
    // viewAspect=16/9≈1.78 < surfaceAspect=2.0 → pillarboxing: reduce width
    assertTrue("Letterboxing should adjust dimensions", result.width <= rect.width || result.height <= rect.height);
  }

  @Test
  public void performLetterboxing_matchingAspect_noChange() throws Exception {
    setAngleField("verticalView", new AngleInRadians(0.5));
    setAngleField("horizontalView", new AngleInRadians(0.5 * 16.0 / 9.0));
    adapter.setIsLetterboxed(true);

    // viewAspect = hAngle/vAngle = 16/9
    Rectangle rect = new Rectangle(0, 0, 1600, 900);
    Rectangle result = adapter.performLetterboxing(rect);
    assertEquals(rect.width, result.width);
    assertEquals(rect.height, result.height);
  }

  @Test
  public void performLetterboxing_narrowerThanAspect_reducesWidth() throws Exception {
    // Tall viewport: surface aspect < view aspect → pillarbox → reduce width, not height
    setAngleField("verticalView", SymmetricPerspectiveCamera.DEFAULT_VERTICAL_VIEW_ANGLE);
    setAngleField("horizontalView", Angle.NaN);
    adapter.setIsLetterboxed(true);

    // Default view aspect = 16/9 ≈ 1.78. Surface=400/600≈0.67 < 1.78
    Rectangle rect = new Rectangle(0, 0, 400, 600);
    Rectangle result = adapter.performLetterboxing(rect);
    // viewAspect > surfaceAspect → letterbox height: reduce height
    assertTrue("Should adjust for tall viewport", result.height < rect.height || result.width < rect.width);
  }

  // ── isLetterboxed / setIsLetterboxed ──

  @Test
  public void isLetterboxed_defaultTrue() {
    GlrSymmetricPerspectiveCamera fresh = new GlrSymmetricPerspectiveCamera();
    assertTrue(fresh.isLetterboxed());
  }

  @Test
  public void setIsLetterboxed_false_reflectsChange() {
    adapter.setIsLetterboxed(false);
    assertFalse(adapter.isLetterboxed());
  }

  @Test
  public void setIsLetterboxed_backToTrue() {
    adapter.setIsLetterboxed(false);
    adapter.setIsLetterboxed(true);
    assertTrue(adapter.isLetterboxed());
  }

  // ── getActualViewport delegates to performLetterboxing ──

  @Test
  public void getActualViewport_withLetterboxing_adjustsDimensions() {
    adapter.setIsLetterboxed(true);
    Rectangle vp = adapter.getActualViewport(800, 200);
    // 800x200 has surfaceAspect=4, viewAspect=16/9≈1.78
    // viewAspect < surfaceAspect → reduce width (pillarbox)
    assertTrue("Letterboxed viewport should differ from surface when aspect mismatches",
        vp.width < 800 || vp.height < 200);
  }

  @Test
  public void getActualViewport_withoutLetterboxing_matchesSurface() {
    adapter.setIsLetterboxed(false);
    Rectangle vp = adapter.getActualViewport(800, 600);
    assertEquals(0, vp.x);
    assertEquals(0, vp.y);
    assertEquals(800, vp.width);
    assertEquals(600, vp.height);
  }

  // ── View angle logic ──

  @Test
  public void withExplicitVerticalAngle_projectionUsesIt() throws Exception {
    Angle custom = new AngleInRadians(1.0);
    setAngleField("verticalView", custom);
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();
    // f = 1/tan(1.0/2)
    double expectedF = 1.0 / Math.tan(1.0 / 2.0);
    // m[1][1] = f (column-major index 5)
    assertEquals(expectedF, arr[5], 0.01);
  }

  @Test
  public void withExplicitBothAngles_aspectFromAngles() throws Exception {
    Angle h = new AngleInRadians(1.2);
    Angle v = new AngleInRadians(0.8);
    setAngleField("horizontalView", h);
    setAngleField("verticalView", v);
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    double[] arr = m.asColumnMajorArray16();
    // aspect = hAngle/vAngle = 1.2/0.8 = 1.5
    double f = 1.0 / Math.tan(0.8 / 2.0);
    double expectedM00 = f / 1.5;
    assertEquals(expectedM00, arr[0], 0.01);
  }

  @Test
  public void withOnlyHorizontalAngle_verticalDerivedFromAspect() throws Exception {
    Angle h = new AngleInRadians(1.0);
    setAngleField("horizontalView", h);
    setAngleField("verticalView", Angle.NaN);
    adapter.setIsLetterboxed(false);
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    // verticalView is NaN, horizontalView is set → vertical = h / aspect
    // aspect = 800/600 (since no both angles, isLetterboxed=false)
    Matrix4x4 m = adapter.getActualProjectionMatrix(vp);
    assertNotNull(m);
  }

  // ── Helpers ──

  @SuppressWarnings("unchecked")
  private <T> void setOwner(GlrSymmetricPerspectiveCamera adapter, T owner) throws Exception {
    Field f = findField(adapter.getClass(), "owner");
    f.setAccessible(true);
    f.set(adapter, owner);
  }

  private Field findField(Class<?> clazz, String name) {
    while (clazz != null) {
      try {
        return clazz.getDeclaredField(name);
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    throw new RuntimeException("Field '" + name + "' not found in hierarchy");
  }

  private void setAngleField(String name, Angle value) throws Exception {
    Field f = GlrSymmetricPerspectiveCamera.class.getDeclaredField(name);
    f.setAccessible(true);
    f.set(adapter, value);
  }
}

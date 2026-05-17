package edu.cmu.cs.dennisc.scenegraph.qa;

import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix3x3;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QualityAssuranceTest {

  // Use zero-vector orientation to trigger mending (zero vectors pass the isNaN check
  // in setLocalTransformation, but trigger isOrientationMendingRequired)
  private static final OrthogonalMatrix3x3 BAD_ORIENTATION =
      new OrthogonalMatrix3x3(Vector3.ZERO, Vector3.POSITIVE_Y_AXIS, Vector3.POSITIVE_Z_AXIS);

  private static void setTransformationBypassingCheck(Transformable t, AffineMatrix4x4 value) {
    // Use the property directly to bypass the isNaN validation in setLocalTransformation
    t.localTransformation.setValue(value);
  }

  @Test
  public void inspectNullReturnsEmptyList() {
    List<Problem> problems = QualityAssuranceUtilities.inspect(null);
    assertNotNull(problems);
    assertTrue(problems.isEmpty());
  }

  @Test
  public void inspectHealthyTransformableReturnsNoProblems() {
    Transformable t = new Transformable();
    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    List<Problem> problems = QualityAssuranceUtilities.inspect(t);
    assertTrue("Healthy transformable should have no problems", problems.isEmpty());
  }

  @Test
  public void inspectTransformableWithZeroOrientationReportsProblem() {
    Transformable t = new Transformable();
    t.setLocalTransformation(new AffineMatrix4x4(BAD_ORIENTATION, new Point3(0, 0, 0)));
    List<Problem> problems = QualityAssuranceUtilities.inspect(t);
    assertEquals("Zero-vector orientation should be flagged", 1, problems.size());
  }

  @Test
  public void inspectTransformableWithNaNOrientationReportsProblem() {
    Transformable t = new Transformable();
    OrthogonalMatrix3x3 nanOrientation = new OrthogonalMatrix3x3(Vector3.NaN, Vector3.NaN, Vector3.NaN);
    setTransformationBypassingCheck(t, new AffineMatrix4x4(nanOrientation, new Point3(0, 0, 0)));
    List<Problem> problems = QualityAssuranceUtilities.inspect(t);
    assertEquals("NaN orientation should be flagged", 1, problems.size());
  }

  @Test
  public void inspectTransformableWithNaNTranslationReportsProblem() {
    Transformable t = new Transformable();
    setTransformationBypassingCheck(t, new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, Point3.NaN));
    List<Problem> problems = QualityAssuranceUtilities.inspect(t);
    assertEquals("NaN translation should be flagged", 1, problems.size());
  }

  @Test
  public void inspectVisualWithNaNScaleReportsProblem() {
    Transformable parent = new Transformable();
    Visual v = new Visual();
    Matrix3x3 nanScale = Matrix3x3.create(Vector3.NaN, Vector3.NaN, Vector3.NaN);
    v.scale.setValue(nanScale);
    v.setParent(parent);
    List<Problem> problems = QualityAssuranceUtilities.inspect(parent);
    assertEquals("NaN scale should be flagged", 1, problems.size());
  }

  @Test
  public void inspectVisualWithIdentityScaleReturnsNoProblems() {
    Transformable parent = new Transformable();
    Visual v = new Visual();
    v.scale.setValue(Matrix3x3.IDENTITY);
    v.setParent(parent);
    List<Problem> problems = QualityAssuranceUtilities.inspect(parent);
    assertTrue("Identity scale should have no problems", problems.isEmpty());
  }

  @Test
  public void inspectNestedCompositeFindsDeepProblems() {
    Transformable root = new Transformable();
    Transformable child = new Transformable();
    child.setLocalTransformation(new AffineMatrix4x4(BAD_ORIENTATION, new Point3(0, 0, 0)));
    child.setParent(root);
    List<Problem> problems = QualityAssuranceUtilities.inspect(root);
    assertEquals("Deep problem should be found", 1, problems.size());
  }

  @Test
  public void mendFixesBadOrientation() {
    Transformable t = new Transformable();
    OrthogonalMatrix3x3 nanOrientation = new OrthogonalMatrix3x3(Vector3.NaN, Vector3.NaN, Vector3.NaN);
    setTransformationBypassingCheck(t, new AffineMatrix4x4(nanOrientation, new Point3(1, 2, 3)));

    Mender mender = joint -> AffineMatrix4x4.IDENTITY;

    QualityAssuranceUtilities.inspectAndMendIfNecessary(t, mender);
    AffineMatrix4x4 fixed = t.getLocalTransformation();
    assertEquals(OrthogonalMatrix3x3.IDENTITY, fixed.orientation());
    assertEquals(1.0, fixed.translation().x(), 0.001);
  }

  @Test
  public void mendFixesBadTranslation() {
    Transformable t = new Transformable();
    setTransformationBypassingCheck(t, new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, Point3.NaN));

    Mender mender = joint -> AffineMatrix4x4.IDENTITY;

    QualityAssuranceUtilities.inspectAndMendIfNecessary(t, mender);
    AffineMatrix4x4 fixed = t.getLocalTransformation();
    assertEquals(0.0, fixed.translation().x(), 0.001);
  }

  @Test
  public void mendFixesBadScale() {
    Transformable parent = new Transformable();
    Visual v = new Visual();
    Matrix3x3 nanScale = Matrix3x3.create(Vector3.NaN, Vector3.NaN, Vector3.NaN);
    v.scale.setValue(nanScale);
    v.setParent(parent);

    Mender mender = joint -> AffineMatrix4x4.IDENTITY;

    QualityAssuranceUtilities.inspectAndMendIfNecessary(parent, mender);
    assertEquals(Matrix3x3.IDENTITY, v.scale.getValue());
  }

  @Test
  public void badLocalTransformationToStringContainsClassName() {
    Transformable t = new Transformable();
    BadLocalTransformation blt = new BadLocalTransformation(t, true, false);
    String s = blt.toString();
    assertNotNull(s);
    assertTrue(s.contains("BadLocalTransformation"));
  }

  @Test
  public void badScaleToStringContainsClassName() {
    Visual v = new Visual();
    Matrix3x3 nanScale = Matrix3x3.create(Vector3.NaN, Vector3.NaN, Vector3.NaN);
    v.scale.setValue(nanScale);
    BadScale bs = new BadScale(v);
    String s = bs.toString();
    assertNotNull(s);
    assertTrue(s.contains("BadScale"));
  }
}

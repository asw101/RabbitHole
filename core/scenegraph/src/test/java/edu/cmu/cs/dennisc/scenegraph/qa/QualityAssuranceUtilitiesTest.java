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
import static org.junit.Assert.assertTrue;

public class QualityAssuranceUtilitiesTest {
  private static final OrthogonalMatrix3x3 BAD_ORIENTATION =
      new OrthogonalMatrix3x3(Vector3.ZERO, Vector3.POSITIVE_Y_AXIS, Vector3.POSITIVE_Z_AXIS);

  private static void setTransformationBypassingCheck(Transformable transformable, AffineMatrix4x4 value) {
    transformable.localTransformation.setValue(value);
  }

  @Test
  public void inspectCollectsTransformationAndScaleProblemsAcrossHierarchy() {
    Transformable root = new Transformable();
    Transformable child = new Transformable();
    child.setLocalTransformation(new AffineMatrix4x4(BAD_ORIENTATION, new Point3(0, 0, 0)));
    child.setParent(root);
    Visual visual = new Visual();
    visual.scale.setValue(Matrix3x3.create(Vector3.NaN, Vector3.NaN, Vector3.NaN));
    visual.setParent(child);

    List<Problem> problems = QualityAssuranceUtilities.inspect(root);

    assertEquals(2, problems.size());
    assertTrue(problems.stream().anyMatch(problem -> problem instanceof BadLocalTransformation));
    assertTrue(problems.stream().anyMatch(problem -> problem instanceof BadScale));
  }

  @Test
  public void inspectAndMendIfNecessaryRepairsNestedTransformationAndScaleProblems() {
    Transformable root = new Transformable();
    Transformable child = new Transformable();
    setTransformationBypassingCheck(child, new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, Point3.NaN));
    child.setParent(root);
    Visual visual = new Visual();
    visual.scale.setValue(Matrix3x3.create(Vector3.NaN, Vector3.NaN, Vector3.NaN));
    visual.setParent(child);

    QualityAssuranceUtilities.inspectAndMendIfNecessary(root, joint -> AffineMatrix4x4.IDENTITY);

    assertEquals(0.0, child.getLocalTransformation().translation().x(), 0.000001);
    assertEquals(Matrix3x3.IDENTITY, visual.scale.getValue());
  }
}

package edu.cmu.cs.dennisc.math.rigidbody;

import org.junit.Test;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;

import static org.junit.Assert.*;

public class TranslationFunctionTest {

  // Concrete subclass for testing
  static class GravityFunction extends TranslationFunction<TranslationDerivative> {
    private final Vector3 gravity;

    GravityFunction(Vector3 gravity) {
      this.gravity = gravity;
    }

    @Override
    protected Vector3 getForce(double t) {
      return gravity.times(getMass());
    }
  }

  @Test
  public void initialState_defaultValues() {
    GravityFunction f = new GravityFunction(new Vector3(0, -9.8, 0));
    assertEquals(Point3.ORIGIN, f.getTranslation());
    assertEquals(Vector3.ZERO, f.getMomentum());
    assertEquals(Vector3.ZERO, f.getVelocity());
    assertEquals(1.0, f.getMass(), 1e-10);
  }

  @Test
  public void setTranslation() {
    GravityFunction f = new GravityFunction(Vector3.ZERO);
    Point3 p = new Point3(1, 2, 3);
    f.setTranslation(p);
    assertEquals(p, f.getTranslation());
  }

  @Test
  public void setMomentum() {
    GravityFunction f = new GravityFunction(Vector3.ZERO);
    Vector3 m = new Vector3(10, 20, 30);
    f.setMomentum(m);
    assertEquals(m, f.getMomentum());
  }

  @Test
  public void setVelocity() {
    GravityFunction f = new GravityFunction(Vector3.ZERO);
    Vector3 v = new Vector3(5, 10, 15);
    f.setVelocity(v);
    assertEquals(v, f.getVelocity());
  }

  @Test
  public void setMass() {
    GravityFunction f = new GravityFunction(Vector3.ZERO);
    f.setMass(5.0);
    assertEquals(5.0, f.getMass(), 1e-10);
  }

  @Test
  public void evaluate_returnsDerivative() {
    GravityFunction f = new GravityFunction(new Vector3(0, -9.8, 0));
    f.setVelocity(new Vector3(1, 0, 0));
    TranslationDerivative d = f.evaluate(0.0);
    assertNotNull(d);
    assertEquals(1.0, d.velocity.x(), 1e-10);
    assertEquals(0.0, d.velocity.y(), 1e-10);
    assertEquals(-9.8, d.force.y(), 1e-10);
  }

  @Test
  public void update_velocityFromMomentum() {
    GravityFunction f = new GravityFunction(Vector3.ZERO);
    f.setMass(2.0);
    f.setMomentum(new Vector3(10, 20, 30));
    f.update();
    assertEquals(5.0, f.getVelocity().x(), 1e-10);
    assertEquals(10.0, f.getVelocity().y(), 1e-10);
    assertEquals(15.0, f.getVelocity().z(), 1e-10);
  }

  @Test
  public void update_rk4() {
    GravityFunction f = new GravityFunction(new Vector3(0, -10, 0));
    f.setVelocity(new Vector3(1, 0, 0));
    TranslationDerivative a = f.evaluate(0.0);
    TranslationDerivative b = f.evaluate(0.0, 0.5, a);
    TranslationDerivative c = f.evaluate(0.0, 0.5, b);
    TranslationDerivative d = f.evaluate(0.0, 1.0, c);
    f.update(a, b, c, d, 1.0);
    // After RK4 step, translation should have changed
    assertNotEquals(Point3.ORIGIN, f.getTranslation());
  }
}

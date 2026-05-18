package org.alice.ide.cascade.fillerinners;

import org.junit.Test;
import org.lgna.project.annotations.IntegerValueDetails;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

/**
 * Tests for cascade filler inner classes:
 * ExpressionFillerInner, BooleanFillerInner, IntegerFillerInner,
 * DoubleFillerInner, StringFillerInner, AbstractNumberFillerInner.
 */
public class CascadeFillerInnersTest {

  // -- ExpressionFillerInner base ----------------------------------------

  @Test
  public void booleanFillerInner_isAssignableToBoolean() {
    BooleanFillerInner filler = new BooleanFillerInner();
    assertTrue(filler.isAssignableTo(JavaType.BOOLEAN_OBJECT_TYPE));
  }

  @Test
  public void booleanFillerInner_notAssignableToString() {
    BooleanFillerInner filler = new BooleanFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.getInstance(String.class)));
  }

  @Test
  public void booleanFillerInner_addRelationalType() {
    BooleanFillerInner filler = new BooleanFillerInner();
    filler.addRelationalType(JavaType.getInstance(Double.class));
    // no exception means success; relational types list is updated
  }

  // -- IntegerFillerInner ------------------------------------------------

  @Test
  public void integerFillerInner_isAssignableToInteger() {
    IntegerFillerInner filler = new IntegerFillerInner();
    assertTrue(filler.isAssignableTo(JavaType.getInstance(Integer.class)));
  }

  @Test
  public void integerFillerInner_getLiterals_defaults() {
    int[] literals = IntegerFillerInner.getLiterals(null);
    assertNotNull(literals);
    assertArrayEquals(new int[]{0, 1, 2, 3}, literals);
  }

  @Test
  public void integerFillerInner_notAssignableToString() {
    IntegerFillerInner filler = new IntegerFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.getInstance(String.class)));
  }

  // -- DoubleFillerInner -------------------------------------------------

  @Test
  public void doubleFillerInner_isAssignableToDouble() {
    DoubleFillerInner filler = new DoubleFillerInner();
    assertTrue(filler.isAssignableTo(JavaType.getInstance(Double.class)));
  }

  @Test
  public void doubleFillerInner_getLiterals_defaults() {
    double[] literals = DoubleFillerInner.getLiterals(null);
    assertNotNull(literals);
    assertEquals(6, literals.length);
    assertEquals(0.0, literals[0], 0.001);
    assertEquals(0.25, literals[1], 0.001);
    assertEquals(0.5, literals[2], 0.001);
    assertEquals(1.0, literals[3], 0.001);
    assertEquals(2.0, literals[4], 0.001);
    assertEquals(10.0, literals[5], 0.001);
  }

  // -- StringFillerInner -------------------------------------------------

  @Test
  public void stringFillerInner_isAssignableToString() {
    StringFillerInner filler = new StringFillerInner();
    assertTrue(filler.isAssignableTo(JavaType.getInstance(String.class)));
  }

  @Test
  public void stringFillerInner_getLiterals() {
    String[] literals = StringFillerInner.getLiterals();
    assertNotNull(literals);
    assertEquals(1, literals.length);
    assertEquals("hello", literals[0]);
  }

  @Test
  public void stringFillerInner_notAssignableToInteger() {
    StringFillerInner filler = new StringFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.getInstance(Integer.class)));
  }

  // -- ExpressionFillerInner class hierarchy checks -----------------------

  @Test
  public void booleanFillerInner_extendsExpressionFillerInner() {
    assertTrue(ExpressionFillerInner.class.isAssignableFrom(BooleanFillerInner.class));
  }

  @Test
  public void integerFillerInner_extendsAbstractNumberFillerInner() {
    assertTrue(AbstractNumberFillerInner.class.isAssignableFrom(IntegerFillerInner.class));
  }

  @Test
  public void doubleFillerInner_extendsAbstractNumberFillerInner() {
    assertTrue(AbstractNumberFillerInner.class.isAssignableFrom(DoubleFillerInner.class));
  }

  @Test
  public void abstractNumberFillerInner_extendsExpressionFillerInner() {
    assertTrue(ExpressionFillerInner.class.isAssignableFrom(AbstractNumberFillerInner.class));
  }

  // -- Additional filler inners class-loading ----------------------------

  @Test
  public void poseFillerInner_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.cascade.fillerinners.PoseFillerInner");
  }

  @Test
  public void audioResourceFillerInner_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.cascade.fillerinners.AudioResourceFillerInner");
  }

  @Test
  public void imageResourceFillerInner_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.cascade.fillerinners.ImageResourceFillerInner");
  }

  @Test
  public void resourceFillerInner_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.cascade.fillerinners.ResourceFillerInner");
  }

  @Test
  public void constantsOwningFillerInner_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.cascade.fillerinners.ConstantsOwningFillerInner");
  }
}

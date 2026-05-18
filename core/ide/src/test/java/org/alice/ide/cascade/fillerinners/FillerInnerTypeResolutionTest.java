package org.alice.ide.cascade.fillerinners;

import org.lgna.project.annotations.IntegerValueDetails;
import org.lgna.project.annotations.NumberValueDetails;
import org.lgna.project.ast.JavaType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Deep tests for filler inner classes — focused on type assignability,
 * literal defaults, and cross-type interactions.
 */
public class FillerInnerTypeResolutionTest {

  // ---- BooleanFillerInner type checks ----

  @Test
  public void booleanFiller_isAssignableTo_objectType() {
    BooleanFillerInner filler = new BooleanFillerInner();
    assertTrue("Boolean should be assignable to Object", filler.isAssignableTo(JavaType.OBJECT_TYPE));
  }

  @Test
  public void booleanFiller_notAssignableTo_doubleType() {
    BooleanFillerInner filler = new BooleanFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.DOUBLE_OBJECT_TYPE));
  }

  @Test
  public void booleanFiller_notAssignableTo_integerType() {
    BooleanFillerInner filler = new BooleanFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.INTEGER_OBJECT_TYPE));
  }

  @Test
  public void booleanFiller_addMultipleRelationalTypes_doesNotThrow() {
    // Smoke test: verifies no exception when adding multiple relational types
    BooleanFillerInner filler = new BooleanFillerInner();
    filler.addRelationalType(JavaType.DOUBLE_OBJECT_TYPE);
    filler.addRelationalType(JavaType.INTEGER_OBJECT_TYPE);
    filler.addRelationalType(JavaType.STRING_TYPE);
  }

  // ---- IntegerFillerInner type checks ----

  @Test
  public void integerFiller_isAssignableTo_numberType() {
    IntegerFillerInner filler = new IntegerFillerInner();
    assertTrue("Integer should be assignable to Number",
        filler.isAssignableTo(JavaType.getInstance(Number.class)));
  }

  @Test
  public void integerFiller_isAssignableTo_objectType() {
    IntegerFillerInner filler = new IntegerFillerInner();
    assertTrue(filler.isAssignableTo(JavaType.OBJECT_TYPE));
  }

  @Test
  public void integerFiller_notAssignableTo_booleanType() {
    IntegerFillerInner filler = new IntegerFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.BOOLEAN_OBJECT_TYPE));
  }

  @Test
  public void integerFiller_getLiterals_withNullDetails_returnsDefaults() {
    int[] literals = IntegerFillerInner.getLiterals(null);
    assertArrayEquals(new int[]{0, 1, 2, 3}, literals);
  }

  @Test
  public void integerFiller_getLiterals_withCustomDetails_returnsCustom() {
    IntegerValueDetails details = new IntegerValueDetails() {
      @Override
      public int[] getLiterals() { return new int[]{10, 20, 30}; }
      @Override
      public Integer getMinimumValue() { return 0; }
      @Override
      public Integer getMaximumValue() { return 100; }
      @Override
      public Class<Integer> getSupportedCls() { return Integer.class; }
    };
    int[] literals = IntegerFillerInner.getLiterals(details);
    assertArrayEquals(new int[]{10, 20, 30}, literals);
  }

  // ---- DoubleFillerInner type checks ----

  @Test
  public void doubleFiller_isAssignableTo_numberType() {
    DoubleFillerInner filler = new DoubleFillerInner();
    assertTrue("Double should be assignable to Number",
        filler.isAssignableTo(JavaType.getInstance(Number.class)));
  }

  @Test
  public void doubleFiller_isAssignableTo_objectType() {
    DoubleFillerInner filler = new DoubleFillerInner();
    assertTrue(filler.isAssignableTo(JavaType.OBJECT_TYPE));
  }

  @Test
  public void doubleFiller_notAssignableTo_booleanType() {
    DoubleFillerInner filler = new DoubleFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.BOOLEAN_OBJECT_TYPE));
  }

  @Test
  public void doubleFiller_getLiterals_withNullDetails_returnsDefaults() {
    double[] literals = DoubleFillerInner.getLiterals(null);
    assertEquals(6, literals.length);
    assertEquals(0.0, literals[0], 0.0001);
    assertEquals(0.25, literals[1], 0.0001);
    assertEquals(0.5, literals[2], 0.0001);
    assertEquals(1.0, literals[3], 0.0001);
    assertEquals(2.0, literals[4], 0.0001);
    assertEquals(10.0, literals[5], 0.0001);
  }

  @Test
  public void doubleFiller_getLiterals_withCustomDetails_returnsCustom() {
    NumberValueDetails details = new NumberValueDetails() {
      @Override
      public double[] getLiterals() { return new double[]{0.1, 0.9}; }
      @Override
      public Double getMinimumValue() { return 0.0; }
      @Override
      public Double getMaximumValue() { return 1.0; }
      @Override
      public Class<Number> getSupportedCls() { return Number.class; }
    };
    double[] literals = DoubleFillerInner.getLiterals(details);
    assertArrayEquals(new double[]{0.1, 0.9}, literals, 0.0001);
  }

  // ---- StringFillerInner type checks ----

  @Test
  public void stringFiller_isAssignableTo_objectType() {
    StringFillerInner filler = new StringFillerInner();
    assertTrue(filler.isAssignableTo(JavaType.OBJECT_TYPE));
  }

  @Test
  public void stringFiller_notAssignableTo_numberType() {
    StringFillerInner filler = new StringFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.getInstance(Number.class)));
  }

  @Test
  public void stringFiller_getLiterals_returnsHello() {
    String[] literals = StringFillerInner.getLiterals();
    assertEquals(1, literals.length);
    assertEquals("hello", literals[0]);
  }

  @Test
  public void stringFiller_notAssignableTo_doubleType() {
    StringFillerInner filler = new StringFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.DOUBLE_OBJECT_TYPE));
  }

  // ---- Cross-type assignability to ensure no accidental overlap ----

  @Test
  public void integerFiller_notAssignableTo_stringType() {
    IntegerFillerInner filler = new IntegerFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.STRING_TYPE));
  }

  @Test
  public void doubleFiller_notAssignableTo_stringType() {
    DoubleFillerInner filler = new DoubleFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.STRING_TYPE));
  }

  @Test
  public void booleanFiller_notAssignableTo_numberType() {
    BooleanFillerInner filler = new BooleanFillerInner();
    assertFalse(filler.isAssignableTo(JavaType.getInstance(Number.class)));
  }

  // ---- ConstantsOwningFillerInner ----

  @Test
  public void constantsOwningFiller_sameType_returnsSameInstance() {
    ConstantsOwningFillerInner a = ConstantsOwningFillerInner.getInstance(JavaType.STRING_TYPE);
    ConstantsOwningFillerInner b = ConstantsOwningFillerInner.getInstance(JavaType.STRING_TYPE);
    assertSame("Should be cached singleton", a, b);
  }

  @Test
  public void constantsOwningFiller_differentTypes_returnsDifferentInstances() {
    ConstantsOwningFillerInner a = ConstantsOwningFillerInner.getInstance(JavaType.STRING_TYPE);
    ConstantsOwningFillerInner b = ConstantsOwningFillerInner.getInstance(JavaType.DOUBLE_OBJECT_TYPE);
    assertNotSame(a, b);
  }

  @Test
  public void constantsOwningFiller_byClass_returnsSameAsJavaType() {
    ConstantsOwningFillerInner a = ConstantsOwningFillerInner.getInstance(String.class);
    ConstantsOwningFillerInner b = ConstantsOwningFillerInner.getInstance(JavaType.STRING_TYPE);
    assertSame(a, b);
  }

  // ---- Hierarchy checks ----

  @Test
  public void stringFillerInner_extendsExpressionFillerInner() {
    assertTrue(ExpressionFillerInner.class.isAssignableFrom(StringFillerInner.class));
  }

  @Test
  public void constantsOwningFillerInner_extendsExpressionFillerInner() {
    assertTrue(ExpressionFillerInner.class.isAssignableFrom(ConstantsOwningFillerInner.class));
  }

  @Test
  public void resourceFillerInner_extendsExpressionFillerInner() {
    assertTrue(ExpressionFillerInner.class.isAssignableFrom(ResourceFillerInner.class));
  }
}

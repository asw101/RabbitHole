package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.story.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for {@link StoryTypeComparator} — ordering and contract.
 */
public class StoryTypeComparatorExtendedTest {

  @Test
  public void singleton_isNotNull() {
    assertNotNull(StoryTypeComparator.SINGLETON);
  }

  // ---- primitive/known type ordering ----

  @Test
  public void compare_booleanBeforeDouble() {
    assertTrue(StoryTypeComparator.SINGLETON.compare(
        JavaType.BOOLEAN_OBJECT_TYPE, JavaType.DOUBLE_OBJECT_TYPE) < 0);
  }

  @Test
  public void compare_doubleBeforeInteger() {
    assertTrue(StoryTypeComparator.SINGLETON.compare(
        JavaType.DOUBLE_OBJECT_TYPE, JavaType.INTEGER_OBJECT_TYPE) < 0);
  }

  @Test
  public void compare_integerBeforeString() {
    assertTrue(StoryTypeComparator.SINGLETON.compare(
        JavaType.INTEGER_OBJECT_TYPE, JavaType.STRING_TYPE) < 0);
  }

  @Test
  public void compare_stringBeforeSThing() {
    assertTrue(StoryTypeComparator.SINGLETON.compare(
        JavaType.STRING_TYPE, JavaType.getInstance(SThing.class)) < 0);
  }

  @Test
  public void compare_sThingBeforeColor() {
    assertTrue(StoryTypeComparator.SINGLETON.compare(
        JavaType.getInstance(SThing.class), JavaType.getInstance(Color.class)) < 0);
  }

  @Test
  public void primitiveTypes_orderedCorrectly() {
    List<AbstractType<?, ?, ?>> types = new ArrayList<>();
    types.add(JavaType.STRING_TYPE);
    types.add(JavaType.BOOLEAN_OBJECT_TYPE);
    types.add(JavaType.INTEGER_OBJECT_TYPE);
    types.add(JavaType.DOUBLE_OBJECT_TYPE);
    Collections.sort(types, StoryTypeComparator.SINGLETON);
    assertEquals(JavaType.BOOLEAN_OBJECT_TYPE, types.get(0));
    assertEquals(JavaType.DOUBLE_OBJECT_TYPE, types.get(1));
    assertEquals(JavaType.INTEGER_OBJECT_TYPE, types.get(2));
    assertEquals(JavaType.STRING_TYPE, types.get(3));
  }

  // ---- story types ----

  @Test
  public void colorBeforePaint() {
    assertTrue(StoryTypeComparator.SINGLETON.compare(
        JavaType.getInstance(Color.class), JavaType.getInstance(Paint.class)) < 0);
  }

  @Test
  public void positionBeforeOrientation() {
    assertTrue(StoryTypeComparator.SINGLETON.compare(
        JavaType.getInstance(Position.class), JavaType.getInstance(Orientation.class)) < 0);
  }

  @Test
  public void orientationBeforeVantagePoint() {
    assertTrue(StoryTypeComparator.SINGLETON.compare(
        JavaType.getInstance(Orientation.class), JavaType.getInstance(VantagePoint.class)) < 0);
  }

  @Test
  public void sjoint_hasHighPriority() {
    assertTrue(StoryTypeComparator.SINGLETON.compare(
        JavaType.getInstance(SJoint.class), JavaType.STRING_TYPE) > 0);
  }

  // ---- unknown types and contract ----

  @Test
  public void compare_primitiveBeforeUnknown() {
    assertTrue(StoryTypeComparator.SINGLETON.compare(
        JavaType.BOOLEAN_OBJECT_TYPE, JavaType.getInstance(Runnable.class)) < 0);
  }

  @Test
  public void compare_unknownTypes_comparesByName() {
    AbstractType<?, ?, ?> typeA = JavaType.getInstance(Runnable.class);
    AbstractType<?, ?, ?> typeB = JavaType.getInstance(Comparable.class);
    int result = StoryTypeComparator.SINGLETON.compare(typeA, typeB);
    int expected = typeA.getName().compareTo(typeB.getName());
    assertEquals(Integer.signum(expected), Integer.signum(result));
  }

  @Test
  public void compare_sameType_returnsZero() {
    assertEquals(0, StoryTypeComparator.SINGLETON.compare(
        JavaType.BOOLEAN_OBJECT_TYPE, JavaType.BOOLEAN_OBJECT_TYPE));
  }

  @Test
  public void compare_isAntiSymmetric() {
    AbstractType<?, ?, ?> a = JavaType.BOOLEAN_OBJECT_TYPE;
    AbstractType<?, ?, ?> b = JavaType.STRING_TYPE;
    int ab = StoryTypeComparator.SINGLETON.compare(a, b);
    int ba = StoryTypeComparator.SINGLETON.compare(b, a);
    assertEquals(-Integer.signum(ab), Integer.signum(ba));
  }

  @Test
  public void compare_transitivity() {
    AbstractType<?, ?, ?> a = JavaType.BOOLEAN_OBJECT_TYPE;
    AbstractType<?, ?, ?> b = JavaType.getInstance(SThing.class);
    AbstractType<?, ?, ?> c = JavaType.getInstance(Color.class);
    int ab = StoryTypeComparator.SINGLETON.compare(a, b);
    int bc = StoryTypeComparator.SINGLETON.compare(b, c);
    int ac = StoryTypeComparator.SINGLETON.compare(a, c);
    if (ab < 0 && bc < 0) {
      assertTrue(ac < 0);
    }
  }
}

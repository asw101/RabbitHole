package edu.cmu.cs.dennisc.java.lang;

import edu.cmu.cs.dennisc.pattern.Criterion;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class EnumUtilitiesTest {

  // Private test enums for multi-class tests
  private enum Color { RED, GREEN, BLUE }
  private enum Size { SMALL, MEDIUM, LARGE, EXTRA_LARGE }
  private enum Direction { NORTH, SOUTH, EAST, WEST }
  private enum Empty {}
  private enum Single { ONLY }

  // --- getFld ---

  @Test
  public void getFld_returnsCorrectField() {
    Field field = EnumUtilities.getFld(Thread.State.NEW);
    assertNotNull(field);
    assertEquals("NEW", field.getName());
  }

  @Test
  public void getFld_null_returnsNull() {
    assertNull(EnumUtilities.getFld(null));
  }

  @Test
  public void getFld_anotherEnumValue() {
    Field field = EnumUtilities.getFld(Thread.State.RUNNABLE);
    assertNotNull(field);
    assertEquals("RUNNABLE", field.getName());
  }

  @Test
  public void getFld_allThreadStates() {
    for (Thread.State state : Thread.State.values()) {
      Field field = EnumUtilities.getFld(state);
      assertNotNull("Field should not be null for " + state, field);
      assertEquals(state.name(), field.getName());
    }
  }

  @Test
  public void getFld_customEnum_color() {
    Field field = EnumUtilities.getFld(Color.RED);
    assertNotNull(field);
    assertEquals("RED", field.getName());
  }

  @Test
  public void getFld_customEnum_allColors() {
    for (Color c : Color.values()) {
      Field field = EnumUtilities.getFld(c);
      assertNotNull(field);
      assertEquals(c.name(), field.getName());
    }
  }

  @Test
  public void getFld_customEnum_size() {
    Field field = EnumUtilities.getFld(Size.EXTRA_LARGE);
    assertNotNull(field);
    assertEquals("EXTRA_LARGE", field.getName());
  }

  @Test
  public void getFld_customEnum_direction() {
    for (Direction d : Direction.values()) {
      Field field = EnumUtilities.getFld(d);
      assertNotNull(field);
      assertEquals(d.name(), field.getName());
    }
  }

  @Test
  public void getFld_singleValueEnum() {
    Field field = EnumUtilities.getFld(Single.ONLY);
    assertNotNull(field);
    assertEquals("ONLY", field.getName());
  }

  @Test
  public void getFld_fieldBelongsToDeclaringClass() {
    Field field = EnumUtilities.getFld(Color.GREEN);
    assertNotNull(field);
    assertEquals(Color.class, field.getDeclaringClass());
  }

  @Test
  public void getFld_fieldIsEnumType() {
    Field field = EnumUtilities.getFld(Size.MEDIUM);
    assertNotNull(field);
    assertTrue(field.isEnumConstant());
  }

  @Test
  public void getFld_consecutiveCalls_returnSameFieldName() {
    Field f1 = EnumUtilities.getFld(Color.BLUE);
    Field f2 = EnumUtilities.getFld(Color.BLUE);
    assertEquals(f1.getName(), f2.getName());
  }

  // --- getEnumConstants ---

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_nullCriterion_returnsAll() {
    Class<? extends Thread.State>[] classes = new Class[] {Thread.State.class};
    List<Thread.State> result = EnumUtilities.getEnumConstants(classes, null);
    assertEquals(Thread.State.values().length, result.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_withCriterion_filters() {
    Class<? extends Thread.State>[] classes = new Class[] {Thread.State.class};
    List<Thread.State> result = EnumUtilities.getEnumConstants(classes, e -> e == Thread.State.NEW);
    assertEquals(1, result.size());
    assertEquals(Thread.State.NEW, result.get(0));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_emptyClassArray_returnsEmptyList() {
    Class<?>[] classes = new Class[0];
    List<?> result = EnumUtilities.getEnumConstants(classes, null);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_criterionRejectsAll_returnsEmptyList() {
    Class<? extends Color>[] classes = new Class[] {Color.class};
    List<Color> result = EnumUtilities.getEnumConstants(classes, e -> false);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_criterionAcceptsAll_returnsAll() {
    Class<? extends Color>[] classes = new Class[] {Color.class};
    List<Color> result = EnumUtilities.getEnumConstants(classes, e -> true);
    assertEquals(3, result.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_multipleEnumClasses_combinesAll() {
    Class<? extends Enum>[] classes = new Class[] {Color.class, Size.class};
    List<Enum> result = EnumUtilities.getEnumConstants(classes, null);
    assertEquals(Color.values().length + Size.values().length, result.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_multipleClasses_withCriterion() {
    Class<? extends Enum>[] classes = new Class[] {Color.class, Direction.class};
    List<Enum> result = EnumUtilities.getEnumConstants(classes,
        e -> e.name().length() <= 4);
    // RED, BLUE from Color; EAST, WEST from Direction
    assertEquals(4, result.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_threeClasses_noFilter() {
    Class<? extends Enum>[] classes = new Class[] {Color.class, Size.class, Direction.class};
    List<Enum> result = EnumUtilities.getEnumConstants(classes, null);
    assertEquals(3 + 4 + 4, result.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_preservesOrder() {
    Class<? extends Color>[] classes = new Class[] {Color.class};
    List<Color> result = EnumUtilities.getEnumConstants(classes, null);
    assertEquals(Color.RED, result.get(0));
    assertEquals(Color.GREEN, result.get(1));
    assertEquals(Color.BLUE, result.get(2));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_multiClass_preservesCrossClassOrder() {
    Class<? extends Enum>[] classes = new Class[] {Color.class, Direction.class};
    List<Enum> result = EnumUtilities.getEnumConstants(classes, null);
    // Color values first, then Direction values
    assertEquals(Color.RED, result.get(0));
    assertEquals(Color.GREEN, result.get(1));
    assertEquals(Color.BLUE, result.get(2));
    assertEquals(Direction.NORTH, result.get(3));
    assertEquals(Direction.SOUTH, result.get(4));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_singleValueEnum_returnsOne() {
    Class<? extends Single>[] classes = new Class[] {Single.class};
    List<Single> result = EnumUtilities.getEnumConstants(classes, null);
    assertEquals(1, result.size());
    assertEquals(Single.ONLY, result.get(0));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_sameClassTwice_duplicatesResults() {
    Class<? extends Color>[] classes = new Class[] {Color.class, Color.class};
    List<Color> result = EnumUtilities.getEnumConstants(classes, null);
    assertEquals(6, result.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_criterionOnName_filtersCorrectly() {
    Class<? extends Size>[] classes = new Class[] {Size.class};
    List<Size> result = EnumUtilities.getEnumConstants(classes,
        e -> e.name().startsWith("S"));
    assertEquals(2, result.size());
    assertTrue(result.contains(Size.SMALL));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_criterionOnOrdinal_filtersCorrectly() {
    Class<? extends Color>[] classes = new Class[] {Color.class};
    List<Color> result = EnumUtilities.getEnumConstants(classes,
        e -> ((Enum<?>) e).ordinal() > 0);
    assertEquals(2, result.size());
    assertFalse(result.contains(Color.RED));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_threadStateWithCriterion_terminatedOnly() {
    Class<? extends Thread.State>[] classes = new Class[] {Thread.State.class};
    List<Thread.State> result = EnumUtilities.getEnumConstants(classes,
        e -> e == Thread.State.TERMINATED);
    assertEquals(1, result.size());
    assertEquals(Thread.State.TERMINATED, result.get(0));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_resultIsModifiable() {
    Class<? extends Color>[] classes = new Class[] {Color.class};
    List<Color> result = EnumUtilities.getEnumConstants(classes, null);
    assertEquals(3, result.size());
    result.add(Color.RED); // LinkedList should allow adding
    assertEquals(4, result.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_multipleClasses_criterionFiltersSome() {
    Class<? extends Enum>[] classes = new Class[] {Color.class, Size.class, Direction.class};
    Criterion<Enum> criterion = e -> e.name().contains("A");
    List<Enum> result = EnumUtilities.getEnumConstants(classes, criterion);
    // LARGE, EXTRA_LARGE from Size; EAST from Direction
    for (Enum e : result) {
      assertTrue("Expected name containing 'A': " + e.name(), e.name().contains("A"));
    }
    assertTrue("Expected at least 3 matches", result.size() >= 3);
  }
}

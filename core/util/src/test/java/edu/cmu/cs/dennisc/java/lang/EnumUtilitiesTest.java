package edu.cmu.cs.dennisc.java.lang;

import edu.cmu.cs.dennisc.pattern.Criterion;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EnumUtilitiesTest {

  private enum Color {
    RED,
    GREEN,
    BLUE
  }

  private enum Size {
    SMALL,
    MEDIUM,
    LARGE,
    EXTRA_LARGE
  }

  private enum Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST
  }

  private enum Empty {
  }

  private enum Single {
    ONLY
  }

  private List<Enum<?>> getEnumConstants(Criterion<Enum<?>> criterion, Class<?>... classes) {
    return (List) EnumUtilities.getEnumConstants((Class[]) classes, (Criterion) criterion);
  }

  private List<String> names(List<? extends Enum<?>> values) {
    List<String> result = new ArrayList<String>();
    for (Enum<?> value : values) {
      result.add(value.name());
    }
    return result;
  }

  private void assertEnumFieldDetails(Field field, String expectedName, Class<?> expectedType) {
    assertNotNull(field);
    assertEquals(expectedName, field.getName());
    assertEquals(expectedType, field.getDeclaringClass());
    assertEquals(expectedType, field.getType());
    assertTrue(field.isEnumConstant());
  }

  private void assertEnumFieldModifiers(Field field) {
    int modifiers = field.getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertTrue(Modifier.isStatic(modifiers));
    assertTrue(Modifier.isFinal(modifiers));
  }

  @Test
  public void getFldNullReturnsNull() {
    Field field = EnumUtilities.getFld(null);
    assertNull(field);
  }

  @Test
  public void getFldForRedReturnsExpectedField() {
    Field field = EnumUtilities.getFld(Color.RED);
    assertEnumFieldDetails(field, "RED", Color.class);
    assertEnumFieldModifiers(field);
  }

  @Test
  public void getFldForGreenReturnsExpectedField() {
    Field field = EnumUtilities.getFld(Color.GREEN);
    assertEnumFieldDetails(field, "GREEN", Color.class);
    assertEnumFieldModifiers(field);
  }

  @Test
  public void getFldForBlueReturnsExpectedField() {
    Field field = EnumUtilities.getFld(Color.BLUE);
    assertEnumFieldDetails(field, "BLUE", Color.class);
    assertEnumFieldModifiers(field);
  }

  @Test
  public void getFldForSmallReturnsExpectedField() {
    Field field = EnumUtilities.getFld(Size.SMALL);
    assertEnumFieldDetails(field, "SMALL", Size.class);
    assertEnumFieldModifiers(field);
  }

  @Test
  public void getFldForExtra_LargeReturnsExpectedField() {
    Field field = EnumUtilities.getFld(Size.EXTRA_LARGE);
    assertEnumFieldDetails(field, "EXTRA_LARGE", Size.class);
    assertEnumFieldModifiers(field);
  }

  @Test
  public void getFldForNorthReturnsExpectedField() {
    Field field = EnumUtilities.getFld(Direction.NORTH);
    assertEnumFieldDetails(field, "NORTH", Direction.class);
    assertEnumFieldModifiers(field);
  }

  @Test
  public void getFldForWestReturnsExpectedField() {
    Field field = EnumUtilities.getFld(Direction.WEST);
    assertEnumFieldDetails(field, "WEST", Direction.class);
    assertEnumFieldModifiers(field);
  }

  @Test
  public void getFldForOnlyReturnsExpectedField() {
    Field field = EnumUtilities.getFld(Single.ONLY);
    assertEnumFieldDetails(field, "ONLY", Single.class);
    assertEnumFieldModifiers(field);
  }

  @Test
  public void getFldForSecondsReturnsExpectedField() {
    Field field = EnumUtilities.getFld(TimeUnit.SECONDS);
    assertEnumFieldDetails(field, "SECONDS", TimeUnit.class);
    assertEnumFieldModifiers(field);
  }

  @Test
  public void getFldForDaysReturnsExpectedField() {
    Field field = EnumUtilities.getFld(TimeUnit.DAYS);
    assertEnumFieldDetails(field, "DAYS", TimeUnit.class);
    assertEnumFieldModifiers(field);
  }

  @Test
  public void getEnumConstantsNullCriterionReturnsAllColors() {
    List<Enum<?>> result = getEnumConstants(null, Color.class);
    assertEquals(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE), result);
    assertEquals(Arrays.asList("RED", "GREEN", "BLUE"), names(result));
    assertTrue(result instanceof LinkedList);
  }

  @Test
  public void getEnumConstantsNullCriterionCombinesMultipleClasses() {
    List<Enum<?>> result = getEnumConstants(null, Color.class, Size.class, Direction.class);
    assertEquals(11, result.size());
    assertEquals(Arrays.asList("RED", "GREEN", "BLUE", "SMALL", "MEDIUM", "LARGE", "EXTRA_LARGE", "NORTH", "SOUTH", "EAST", "WEST"), names(result));
    assertEquals(Color.RED, result.get(0));
    assertEquals(Direction.WEST, result.get(result.size() - 1));
  }

  @Test
  public void getEnumConstantsEmptyClassArrayReturnsEmptyLinkedList() {
    List<Enum<?>> result = getEnumConstants(null);
    assertNotNull(result);
    assertTrue(result.isEmpty());
    assertTrue(result instanceof LinkedList);
  }

  @Test
  public void getEnumConstantsCriterionRejectsAllValues() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return false;
      }
    }, Color.class, Size.class);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getEnumConstantsCriterionAcceptsAllValues() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return true;
      }
    }, Direction.class);
    assertEquals(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST), result);
  }

  @Test
  public void getEnumConstantsCriterionFiltersByNamePrefix() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value.name().startsWith("E");
      }
    }, Size.class, Direction.class);
    assertEquals(Arrays.asList(Size.EXTRA_LARGE, Direction.EAST), result);
  }

  @Test
  public void getEnumConstantsCriterionFiltersByOrdinal() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value.ordinal() % 2 == 0;
      }
    }, Color.class, Size.class);
    assertEquals(Arrays.asList(Color.RED, Color.BLUE, Size.SMALL, Size.LARGE), result);
  }

  @Test
  public void getEnumConstantsPreservesDeclarationOrderAcrossClasses() {
    List<Enum<?>> result = getEnumConstants(null, Direction.class, Single.class, Color.class);
    assertEquals(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Single.ONLY, Color.RED, Color.GREEN, Color.BLUE), result);
    assertEquals(Arrays.asList("NORTH", "SOUTH", "EAST", "WEST", "ONLY", "RED", "GREEN", "BLUE"), names(result));
  }

  @Test
  public void getEnumConstantsDuplicatedClassDuplicatesValues() {
    List<Enum<?>> result = getEnumConstants(null, Single.class, Single.class, Color.class);
    assertEquals(5, result.size());
    assertEquals(Arrays.asList("ONLY", "ONLY", "RED", "GREEN", "BLUE"), names(result));
    assertEquals(Single.ONLY, result.get(0));
    assertEquals(Single.ONLY, result.get(1));
  }

  @Test
  public void getEnumConstantsEmptyEnumContributesNoValues() {
    List<Enum<?>> result = getEnumConstants(null, Empty.class, Single.class);
    assertEquals(1, result.size());
    assertEquals(Single.ONLY, result.get(0));
    assertEquals(Arrays.asList("ONLY"), names(result));
  }

  @Test
  public void getEnumConstantsResultIsMutableLinkedList() {
    List<Enum<?>> result = getEnumConstants(null, Color.class);
    assertTrue(result instanceof LinkedList);
    result.add(Single.ONLY);
    assertEquals(4, result.size());
    assertEquals(Single.ONLY, result.get(3));
  }

  @Test
  public void getEnumConstantsManyClassesAggregateExpectedCount() {
    List<Enum<?>> result = getEnumConstants(null, Color.class, Size.class, Direction.class, Empty.class, Single.class, TimeUnit.class);
    int expected = Color.values().length + Size.values().length + Direction.values().length + Empty.values().length + Single.values().length + TimeUnit.values().length;
    assertEquals(expected, result.size());
    assertEquals(Color.RED, result.get(0));
    assertEquals(TimeUnit.DAYS, result.get(result.size() - 1));
  }

  @Test
  public void getEnumConstantsCriterionMatchesSpecificName() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return "MEDIUM".equals(value.name());
      }
    }, Size.class, Direction.class);
    assertEquals(1, result.size());
    assertEquals(Size.MEDIUM, result.get(0));
  }

  @Test
  public void getEnumConstantsCriterionMatchesOrdinalZeroAcrossClasses() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value.ordinal() == 0;
      }
    }, Color.class, Size.class, Direction.class, Single.class);
    assertEquals(Arrays.asList(Color.RED, Size.SMALL, Direction.NORTH, Single.ONLY), result);
  }

  @Test
  public void getEnumConstantsCriterionMatchesLongNamesOnly() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value.name().length() > 5;
      }
    }, Color.class, Size.class, Direction.class);
    assertEquals(Arrays.asList(Size.MEDIUM, Size.EXTRA_LARGE), result);
  }

  @Test
  public void getEnumConstantsWorksWithTimeUnitAndCustomEnums() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value.name().contains("E");
      }
    }, TimeUnit.class, Color.class);
    assertTrue(result.contains(TimeUnit.SECONDS));
    assertTrue(result.contains(TimeUnit.MINUTES));
    assertTrue(result.contains(Color.GREEN));
  }

  @Test
  public void getEnumConstantsSingleClassDuplicatedPreservesOrder() {
    List<Enum<?>> result = getEnumConstants(null, Direction.class, Direction.class);
    assertEquals(8, result.size());
    assertEquals(Arrays.asList("NORTH", "SOUTH", "EAST", "WEST", "NORTH", "SOUTH", "EAST", "WEST"), names(result));
  }

  @Test
  public void getEnumConstantsReturnsNewListForEachCall() {
    List<Enum<?>> first = getEnumConstants(null, Color.class);
    List<Enum<?>> second = getEnumConstants(null, Color.class);
    assertNotSame(first, second);
    first.add(Single.ONLY);
    assertEquals(3, second.size());
  }

  @Test
  public void getEnumConstantsCanFilterSingleValueEnum() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value == Single.ONLY;
      }
    }, Single.class);
    assertEquals(1, result.size());
    assertEquals(Single.ONLY, result.get(0));
  }

  @Test
  public void getEnumConstantsCanRejectSingleValueEnum() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value != Single.ONLY;
      }
    }, Single.class);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getEnumConstantsAcceptsOnlyValuesEndingInT() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value.name().endsWith("T");
      }
    }, Direction.class, Size.class);
    assertEquals(Arrays.asList(Direction.EAST, Direction.WEST), result);
  }

  @Test
  public void getEnumConstantsMaintainsValuesWhenAppendingLater() {
    List<Enum<?>> result = getEnumConstants(null, Empty.class, Color.class, Single.class);
    result.add(Direction.SOUTH);
    assertEquals(Arrays.asList("RED", "GREEN", "BLUE", "ONLY", "SOUTH"), names(result));
  }

  @Test
  public void getEnumConstantsSupportsClassOrderColorThenDirection() {
    List<Enum<?>> result = getEnumConstants(null, Color.class, Direction.class);
    assertEquals(Color.RED, result.get(0));
    assertEquals(Color.BLUE, result.get(2));
    assertEquals(Direction.NORTH, result.get(3));
    assertEquals(Direction.WEST, result.get(6));
  }

  @Test
  public void getEnumConstantsSupportsClassOrderDirectionThenColor() {
    List<Enum<?>> result = getEnumConstants(null, Direction.class, Color.class);
    assertEquals(Direction.NORTH, result.get(0));
    assertEquals(Direction.WEST, result.get(3));
    assertEquals(Color.RED, result.get(4));
    assertEquals(Color.BLUE, result.get(6));
  }

  @Test
  public void getEnumConstantsAcceptsMixedPredicateAcrossManyClasses() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value.name().contains("A") || value.ordinal() == 1;
      }
    }, Color.class, Size.class, Direction.class);
    assertTrue(result.contains(Color.GREEN));
    assertTrue(result.contains(Size.MEDIUM));
    assertTrue(result.contains(Direction.EAST));
  }

  @Test
  public void getEnumConstantsCriterionCanInspectDeclaringClass() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value.getDeclaringClass() == Direction.class;
      }
    }, Color.class, Direction.class, Size.class);
    assertEquals(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST), result);
  }

  @Test
  public void getEnumConstantsReturnsAllTimeUnitConstantsWithNullCriterion() {
    List<Enum<?>> result = getEnumConstants(null, TimeUnit.class);
    assertEquals(TimeUnit.values().length, result.size());
    assertEquals(TimeUnit.NANOSECONDS, result.get(0));
    assertEquals(TimeUnit.DAYS, result.get(result.size() - 1));
  }

  @Test
  public void getEnumConstantsWithEmptyEnumAndEmptyCriterionStillReturnsEmpty() {
    List<Enum<?>> result = getEnumConstants(new Criterion<Enum<?>>() {
      @Override
      public boolean accept(Enum<?> value) {
        return value.name().length() > 10;
      }
    }, Empty.class);
    assertTrue(result.isEmpty());
    assertTrue(result instanceof LinkedList);
  }

}

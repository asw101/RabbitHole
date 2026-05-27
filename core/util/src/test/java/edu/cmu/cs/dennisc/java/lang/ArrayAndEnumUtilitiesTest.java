package edu.cmu.cs.dennisc.java.lang;

import edu.cmu.cs.dennisc.pattern.Criterion;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ArrayAndEnumUtilitiesTest {
  private enum DemoAlpha {
    ALPHA,
    BETA,
  }

  private enum DemoBeta {
    BRAVO,
    CHARLIE,
  }

  @Test
  public void arrayUtilitiesBuildAndReverseArrays() {
    String[] values = { "one", "two", "three" };
    ArrayUtilities.reverseInPlace(values);

    assertArrayEquals(new String[] { "three", "two", "one" }, values);
    assertArrayEquals(new String[] { "alpha", "beta", "gamma" }, ArrayUtilities.concat(String.class, "alpha", "beta", "gamma"));
    assertArrayEquals(new Integer[] { 1, 2, 3, 4 }, ArrayUtilities.concatArrays(Integer.class, new Integer[] { 1, 2 }, new Integer[] { 3, 4 }));
    assertArrayEquals(new Integer[] { 7, 8 }, ArrayUtilities.createArray(Arrays.asList(7, 8), Integer.class, true));
    assertEquals("[1, 2, 3]", ArrayUtilities.toString(new int[] { 1, 2, 3 }));
    assertNull(ArrayUtilities.concatArrays(Integer.class));
  }

  @Test
  public void enumUtilitiesExposeFieldsAndFilterConstants() {
    Field field = EnumUtilities.getFld(DemoAlpha.BETA);
    Class<? extends Enum<?>>[] enumClasses = new Class[] { DemoAlpha.class, DemoBeta.class };
    Criterion<Enum<?>> namesWithB = value -> value.name().startsWith("B");
    List<Enum<?>> filtered = EnumUtilities.getEnumConstants(enumClasses, namesWithB);

    assertNotNull(field);
    assertEquals("BETA", field.getName());
    assertNull(EnumUtilities.getFld(null));
    assertEquals(Arrays.asList(DemoAlpha.BETA, DemoBeta.BRAVO), filtered);
    assertEquals(Collections.singletonList(DemoBeta.CHARLIE), EnumUtilities.getEnumConstants(new Class[] { DemoBeta.class }, value -> value == DemoBeta.CHARLIE));
  }
}

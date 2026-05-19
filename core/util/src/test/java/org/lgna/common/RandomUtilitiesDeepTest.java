package org.lgna.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class RandomUtilitiesDeepTest {

  @Test
  public void nextDoubleInRange_negativeRange() {
    for (int i = 0; i < 50; i++) {
      double value = RandomUtilities.nextDoubleInRange(-10.0, -5.0);
      assertTrue(value >= -10.0 && value <= -5.0);
    }
  }

  @Test
  public void nextDoubleInRange_largeRange() {
    for (int i = 0; i < 50; i++) {
      double value = RandomUtilities.nextDoubleInRange(0, 1000000);
      assertTrue(value >= 0 && value <= 1000000);
    }
  }

  @Test
  public void nextIntegerFrom0ToNExclusive_nEquals1_alwaysZero() {
    for (int i = 0; i < 20; i++) {
      assertEquals(0, (int) RandomUtilities.nextIntegerFrom0ToNExclusive(1));
    }
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void nextIntegerFrom0ToNExclusive_zeroThrows() {
    RandomUtilities.nextIntegerFrom0ToNExclusive(0);
  }

  @Test
  public void nextIntegerFromAToBExclusive_adjacent_alwaysA() {
    for (int i = 0; i < 20; i++) {
      assertEquals(5, (int) RandomUtilities.nextIntegerFromAToBExclusive(5, 6));
    }
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void nextIntegerFromAToBExclusive_equalThrows() {
    RandomUtilities.nextIntegerFromAToBExclusive(5, 5);
  }

  @Test
  public void nextIntegerFromAToBInclusive_negativeRange() {
    for (int i = 0; i < 50; i++) {
      int result = RandomUtilities.nextIntegerFromAToBInclusive(-10, -5);
      assertTrue(result >= -10 && result <= -5);
    }
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void nextIntegerFromAToBInclusive_reversedThrows() {
    RandomUtilities.nextIntegerFromAToBInclusive(10, 5);
  }

  @Test
  public void getRandomValueFrom_singleIntegerElement() {
    assertEquals(Integer.valueOf(42), RandomUtilities.getRandomValueFrom(new Integer[] { 42 }));
  }

  @Test
  public void getRandomValueFrom_emptyArrayReturnsNull() {
    assertNull(RandomUtilities.getRandomValueFrom(new String[0]));
  }

  @Test(expected = LgnaIllegalArgumentException.class)
  public void getRandomValueFrom_nullArrayThrows() {
    RandomUtilities.getRandomValueFrom(null);
  }

  @Test
  public void getRandomEnumConstant_returnsValidConstant() {
    for (int i = 0; i < 20; i++) {
      assertNotNull(RandomUtilities.getRandomEnumConstant(TestEnum.class));
    }
  }

  @Test
  public void distribution_nextIntegerFrom0ToNExclusive() {
    int[] counts = new int[3];

    for (int i = 0; i < 300; i++) {
      counts[RandomUtilities.nextIntegerFrom0ToNExclusive(3)]++;
    }

    for (int i = 0; i < 3; i++) {
      assertTrue(counts[i] > 0);
    }
  }

  @Test
  public void nextIntegerFromAToBExclusive_largeRange() {
    for (int i = 0; i < 50; i++) {
      int result = RandomUtilities.nextIntegerFromAToBExclusive(0, 1000);
      assertTrue(result >= 0 && result < 1000);
    }
  }

  @Test
  public void nextIntegerFromAToBInclusive_sameValue() {
    for (int i = 0; i < 10; i++) {
      assertEquals(42, (int) RandomUtilities.nextIntegerFromAToBInclusive(42, 42));
    }
  }

  @Test
  public void nextDouble_alwaysBetween0And1() {
    for (int i = 0; i < 100; i++) {
      double value = RandomUtilities.nextDouble();
      assertTrue(value >= 0.0 && value < 1.0);
    }
  }

  @Test
  public void nextDoubleInRange_zeroWidth() {
    assertEquals(5.0, RandomUtilities.nextDoubleInRange(5.0, 5.0), 1e-10);
  }

  private enum TestEnum {
    A, B, C, D
  }
}

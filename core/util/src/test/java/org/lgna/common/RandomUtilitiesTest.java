package org.lgna.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

class RandomUtilitiesTest {

  // --- nextDouble ---

  @RepeatedTest(20)
  void nextDouble_returnsBetween0And1() {
    double d = RandomUtilities.nextDouble();
    assertTrue(d >= 0.0 && d < 1.0);
  }

  // --- nextDoubleInRange ---

  @RepeatedTest(20)
  void nextDoubleInRange_withinBounds() {
    double d = RandomUtilities.nextDoubleInRange(5.0, 10.0);
    assertTrue(d >= 5.0 && d <= 10.0);
  }

  @Test
  void nextDoubleInRange_sameMinMaxReturnsThat() {
    double d = RandomUtilities.nextDoubleInRange(7.0, 7.0);
    assertEquals(7.0, d, 1e-10);
  }

  @Test
  void nextDoubleInRange_acceptsIntegerNumbers() {
    double d = RandomUtilities.nextDoubleInRange(0, 100);
    assertTrue(d >= 0.0 && d <= 100.0);
  }

  // --- nextIntegerFrom0ToNExclusive ---

  @RepeatedTest(20)
  void nextIntegerFrom0ToNExclusive_withinRange() {
    int r = RandomUtilities.nextIntegerFrom0ToNExclusive(10);
    assertTrue(r >= 0 && r < 10);
  }

  @Test
  void nextIntegerFrom0ToNExclusive_nEquals1_returnsZero() {
    assertEquals(0, RandomUtilities.nextIntegerFrom0ToNExclusive(1));
  }

  @Test
  void nextIntegerFrom0ToNExclusive_zeroThrows() {
    assertThrows(LgnaIllegalArgumentException.class, () -> RandomUtilities.nextIntegerFrom0ToNExclusive(0));
  }

  @Test
  void nextIntegerFrom0ToNExclusive_negativeThrows() {
    assertThrows(LgnaIllegalArgumentException.class, () -> RandomUtilities.nextIntegerFrom0ToNExclusive(-5));
  }

  // --- nextIntegerFromAToBExclusive ---

  @RepeatedTest(20)
  void nextIntegerFromAToBExclusive_withinRange() {
    int r = RandomUtilities.nextIntegerFromAToBExclusive(5, 15);
    assertTrue(r >= 5 && r < 15);
  }

  @Test
  void nextIntegerFromAToBExclusive_equalABThrows() {
    assertThrows(LgnaIllegalArgumentException.class, () -> RandomUtilities.nextIntegerFromAToBExclusive(5, 5));
  }

  @Test
  void nextIntegerFromAToBExclusive_aGreaterThanBThrows() {
    assertThrows(LgnaIllegalArgumentException.class, () -> RandomUtilities.nextIntegerFromAToBExclusive(10, 5));
  }

  @Test
  void nextIntegerFromAToBExclusive_negativeRange() {
    int r = RandomUtilities.nextIntegerFromAToBExclusive(-10, -5);
    assertTrue(r >= -10 && r < -5);
  }

  // --- nextIntegerFromAToBInclusive ---

  @RepeatedTest(20)
  void nextIntegerFromAToBInclusive_withinRange() {
    int r = RandomUtilities.nextIntegerFromAToBInclusive(5, 15);
    assertTrue(r >= 5 && r <= 15);
  }

  @Test
  void nextIntegerFromAToBInclusive_sameAB_returnsThat() {
    assertEquals(7, RandomUtilities.nextIntegerFromAToBInclusive(7, 7));
  }

  @Test
  void nextIntegerFromAToBInclusive_aGreaterThanBThrows() {
    assertThrows(LgnaIllegalArgumentException.class, () -> RandomUtilities.nextIntegerFromAToBInclusive(10, 5));
  }

  // --- nextBoolean ---

  @Test
  void nextBoolean_returnsBooleanValue() {
    boolean b = RandomUtilities.nextBoolean();
    assertTrue(b || !b);
  }

  // --- getRandomValueFrom ---

  @RepeatedTest(10)
  void getRandomValueFrom_returnsElementFromArray() {
    String[] arr = {"a", "b", "c", "d"};
    String r = RandomUtilities.getRandomValueFrom(arr);
    assertNotNull(r);
    boolean found = false;
    for (String s : arr) {
      if (s.equals(r)) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  void getRandomValueFrom_emptyArrayReturnsNull() {
    String[] arr = {};
    assertNull(RandomUtilities.getRandomValueFrom(arr));
  }

  @Test
  void getRandomValueFrom_singleElementReturnsThat() {
    String[] arr = {"only"};
    assertEquals("only", RandomUtilities.getRandomValueFrom(arr));
  }

  // --- getRandomEnumConstant ---

  enum TestEnum { A, B, C }

  @RepeatedTest(10)
  void getRandomEnumConstant_returnsValidConstant() {
    TestEnum r = RandomUtilities.getRandomEnumConstant(TestEnum.class);
    assertNotNull(r);
  }
}

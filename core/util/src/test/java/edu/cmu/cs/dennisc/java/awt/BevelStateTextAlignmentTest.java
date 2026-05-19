package edu.cmu.cs.dennisc.java.awt;

import org.junit.Test;

import static org.junit.Assert.*;

public class BevelStateTextAlignmentTest {

  @Test
  public void bevelState_hasThreeValues() {
    assertEquals(3, BevelState.values().length);
  }

  @Test
  public void bevelState_raised() {
    assertEquals("RAISED", BevelState.RAISED.name());
  }

  @Test
  public void bevelState_flush() {
    assertEquals("FLUSH", BevelState.FLUSH.name());
  }

  @Test
  public void bevelState_sunken() {
    assertEquals("SUNKEN", BevelState.SUNKEN.name());
  }

  @Test
  public void bevelState_valueOf() {
    assertSame(BevelState.RAISED, BevelState.valueOf("RAISED"));
    assertSame(BevelState.FLUSH, BevelState.valueOf("FLUSH"));
    assertSame(BevelState.SUNKEN, BevelState.valueOf("SUNKEN"));
  }

  @Test
  public void bevelState_ordinals() {
    assertEquals(0, BevelState.RAISED.ordinal());
    assertEquals(1, BevelState.FLUSH.ordinal());
    assertEquals(2, BevelState.SUNKEN.ordinal());
  }

  @Test
  public void bevelState_valuesAreInDeclarationOrder() {
    BevelState[] values = BevelState.values();

    assertSame(BevelState.RAISED, values[0]);
    assertSame(BevelState.FLUSH, values[1]);
    assertSame(BevelState.SUNKEN, values[2]);
  }

  @Test
  public void bevelState_toStringMatchesName() {
    for (BevelState bevelState : BevelState.values()) {
      assertEquals(bevelState.name(), bevelState.toString());
    }
  }

  @Test
  public void textAlignment_hasThreeValues() {
    assertEquals(3, TextAlignment.values().length);
  }

  @Test
  public void textAlignment_leading() {
    assertEquals("LEADING", TextAlignment.LEADING.name());
  }

  @Test
  public void textAlignment_center() {
    assertEquals("CENTER", TextAlignment.CENTER.name());
  }

  @Test
  public void textAlignment_trailing() {
    assertEquals("TRAILING", TextAlignment.TRAILING.name());
  }

  @Test
  public void textAlignment_valueOf() {
    assertSame(TextAlignment.LEADING, TextAlignment.valueOf("LEADING"));
    assertSame(TextAlignment.CENTER, TextAlignment.valueOf("CENTER"));
    assertSame(TextAlignment.TRAILING, TextAlignment.valueOf("TRAILING"));
  }

  @Test
  public void textAlignment_ordinals() {
    assertEquals(0, TextAlignment.LEADING.ordinal());
    assertEquals(1, TextAlignment.CENTER.ordinal());
    assertEquals(2, TextAlignment.TRAILING.ordinal());
  }

  @Test
  public void textAlignment_valuesAreInDeclarationOrder() {
    TextAlignment[] values = TextAlignment.values();

    assertSame(TextAlignment.LEADING, values[0]);
    assertSame(TextAlignment.CENTER, values[1]);
    assertSame(TextAlignment.TRAILING, values[2]);
  }

  @Test
  public void textAlignment_toStringMatchesName() {
    for (TextAlignment textAlignment : TextAlignment.values()) {
      assertEquals(textAlignment.name(), textAlignment.toString());
    }
  }
}

package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Expanded tests for {@link BooleanState} — deeper branch coverage for
 * icon/text accessors, listener ordering, and edge cases.
 */
public class BooleanStateExpandedTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-e020-ffffffffffff"), "boolExpTest");

  private TestBooleanState state;

  @Before
  public void setUp() {
    state = new TestBooleanState(TEST_GROUP, false);
    CroquetTestUtils.removeItemListeners(state);
  }

  // ── Multiple value changes ───────────────────────────────────────

  @Test
  public void rapidChanges_trackCorrectly() {
    for (int i = 0; i < 10; i++) {
      state.setValueTransactionlessly(i % 2 == 0);
    }
    // i=0: true, i=1: false, ... i=9: 9%2=1 => false
    assertFalse(state.getValue());
  }

  @Test
  public void rapidChanges_listenerCountsCorrectly() {
    java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger(0);
    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override public void changing(State<Boolean> s, Boolean prev, Boolean next) {}
      @Override public void changed(State<Boolean> s, Boolean prev, Boolean next) { count.incrementAndGet(); }
    });
    state.setValueTransactionlessly(true);  // false->true
    state.setValueTransactionlessly(false); // true->false
    state.setValueTransactionlessly(true);  // false->true
    assertEquals(3, count.get());
  }

  // ── Text accessors with null ─────────────────────────────────────

  @Test
  public void getTrueText_initiallyNull() {
    assertNull(state.getTrueText());
  }

  @Test
  public void getFalseText_initiallyNull() {
    assertNull(state.getFalseText());
  }

  @Test
  public void setTextForBothTrueAndFalse_null() {
    state.setTextForBothTrueAndFalse(null);
    assertNull(state.getTrueText());
    assertNull(state.getFalseText());
  }

  // ── getTextFor with texts set ────────────────────────────────────

  @Test
  public void getTextFor_true_afterSetBoth() {
    state.setTextForBothTrueAndFalse("Toggle");
    assertEquals("Toggle", state.getTextFor(true));
    assertEquals("Toggle", state.getTextFor(false));
  }

  @Test
  public void getTextFor_differentTexts() {
    state.setTextForTrueAndTextForFalse("Yes", "No");
    assertEquals("Yes", state.getTextFor(true));
    assertEquals("No", state.getTextFor(false));
  }

  // ── Icon accessors ───────────────────────────────────────────────

  @Test
  public void setIcons_thenGetIconFor() {
    javax.swing.Icon tIcon = new javax.swing.ImageIcon();
    javax.swing.Icon fIcon = new javax.swing.ImageIcon();
    state.setIconForTrueAndIconForFalse(tIcon, fIcon);
    assertSame(tIcon, state.getIconFor(true));
    assertSame(fIcon, state.getIconFor(false));
  }

  @Test
  public void setIcons_null_icons() {
    state.setIconForTrueAndIconForFalse(null, null);
    assertNull(state.getIconFor(true));
    assertNull(state.getIconFor(false));
  }

  // ── New-school listener with adjusting ───────────────────────────

  @Test
  public void newSchoolListener_receivesEvent() {
    AtomicReference<org.lgna.croquet.event.ValueEvent<Boolean>> captured = new AtomicReference<>();
    state.addNewSchoolValueListener(captured::set);
    state.setValueTransactionlessly(true);
    assertNotNull(captured.get());
    assertEquals(Boolean.TRUE, captured.get().getNextValue());
  }

  // ── changeValueFromEdit ──────────────────────────────────────────

  @Test
  public void changeValueFromEdit_doesNotFireTwice() {
    java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger(0);
    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override public void changing(State<Boolean> s, Boolean prev, Boolean next) {}
      @Override public void changed(State<Boolean> s, Boolean prev, Boolean next) { count.incrementAndGet(); }
    });
    state.changeValueFromEdit(true);
    assertEquals(1, count.get());
  }

  // ── Enabled state ────────────────────────────────────────────────

  @Test
  public void setEnabled_togglesCorrectly() {
    assertTrue(state.isEnabled());
    state.setEnabled(false);
    assertFalse(state.isEnabled());
    state.setEnabled(true);
    assertTrue(state.isEnabled());
  }

  // ── appendRepresentation edge cases ──────────────────────────────

  @Test
  public void appendRepresentation_true_appends() {
    StringBuilder sb = new StringBuilder("prefix:");
    state.appendRepresentation(sb, true);
    assertEquals("prefix:true", sb.toString());
  }

  @Test
  public void appendRepresentation_false_appends() {
    StringBuilder sb = new StringBuilder("prefix:");
    state.appendRepresentation(sb, false);
    assertEquals("prefix:false", sb.toString());
  }

  // ── getMigrationId ───────────────────────────────────────────────

  @Test
  public void getMigrationId_consistentAcrossCalls() {
    Object id1 = state.getMigrationId();
    Object id2 = state.getMigrationId();
    assertEquals(id1, id2);
  }

  // ── ButtonModel sync detail ──────────────────────────────────────

  @Test
  public void buttonModel_syncsOnSetValue() {
    assertFalse(state.getImp().getSwingModel().getButtonModel().isSelected());
    state.setValueTransactionlessly(true);
    assertTrue(state.getImp().getSwingModel().getButtonModel().isSelected());
  }
}

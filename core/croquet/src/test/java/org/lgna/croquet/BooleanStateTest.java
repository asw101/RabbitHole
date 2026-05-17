package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import javax.swing.DefaultButtonModel;
import java.awt.event.ItemListener;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for {@link BooleanState} — boolean toggle state that synchronizes
 * with a Swing ButtonModel. Covers value get/set, toggle, text/icon
 * accessors, enabled state, and listener dispatch.
 *
 * <p>The ItemListener is removed in setUp to avoid the
 * Application.getActiveInstance() dependency chain.</p>
 */
public class BooleanStateTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-0002-ffffffffffff"), "boolTest");

  private TestBooleanState state;

  @Before
  public void setUp() {
    state = new TestBooleanState(TEST_GROUP, false);
    removeItemListeners(state);
  }

  private static void removeItemListeners(TestBooleanState s) {
    DefaultButtonModel bm = (DefaultButtonModel) s.getImp().getSwingModel().getButtonModel();
    for (ItemListener il : bm.getItemListeners()) {
      bm.removeItemListener(il);
    }
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialFalse() {
    assertFalse(state.getValue());
  }

  @Test
  public void constructor_setsInitialTrue() {
    TestBooleanState trueState = new TestBooleanState(TEST_GROUP, true);
    removeItemListeners(trueState);
    assertTrue(trueState.getValue());
  }

  // ── setValueTransactionlessly ─────────────────────────────────────

  @Test
  public void setValueTransactionlessly_toTrue() {
    state.setValueTransactionlessly(true);
    assertTrue(state.getValue());
  }

  @Test
  public void setValueTransactionlessly_backToFalse() {
    state.setValueTransactionlessly(true);
    state.setValueTransactionlessly(false);
    assertFalse(state.getValue());
  }

  // ── ButtonModel sync ──────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_syncsButtonModel() {
    state.setValueTransactionlessly(true);
    assertTrue(state.getImp().getSwingModel().getButtonModel().isSelected());
  }

  @Test
  public void setValueTransactionlessly_false_syncsButtonModel() {
    state.setValueTransactionlessly(true);
    state.setValueTransactionlessly(false);
    assertFalse(state.getImp().getSwingModel().getButtonModel().isSelected());
  }

  // ── getImp / getSwingModel ────────────────────────────────────────

  @Test
  public void getImp_returnsNonNull() {
    assertNotNull(state.getImp());
  }

  @Test
  public void getSwingModel_returnsNonNull() {
    assertNotNull(state.getImp().getSwingModel());
  }

  @Test
  public void getButtonModel_returnsNonNull() {
    assertNotNull(state.getImp().getSwingModel().getButtonModel());
  }

  // ── isEnabled / setEnabled ────────────────────────────────────────

  @Test
  public void isEnabled_defaultTrue() {
    assertTrue(state.isEnabled());
  }

  @Test
  public void setEnabled_false_updatesState() {
    state.setEnabled(false);
    assertFalse(state.isEnabled());
  }

  @Test
  public void setEnabled_true_afterFalse() {
    state.setEnabled(false);
    state.setEnabled(true);
    assertTrue(state.isEnabled());
  }

  // ── Text for true/false ───────────────────────────────────────────

  @Test
  public void setTextForBothTrueAndFalse_setsIdenticalText() {
    state.setTextForBothTrueAndFalse("Toggle");
    assertEquals("Toggle", state.getTrueText());
    assertEquals("Toggle", state.getFalseText());
  }

  @Test
  public void setTextForTrueAndFalse_setsDifferentText() {
    state.setTextForTrueAndTextForFalse("On", "Off");
    assertEquals("On", state.getTrueText());
    assertEquals("Off", state.getFalseText());
  }

  @Test
  public void getTextFor_true_returnsTrueText() {
    state.setTextForTrueAndTextForFalse("On", "Off");
    assertEquals("On", state.getTextFor(true));
  }

  @Test
  public void getTextFor_false_returnsFalseText() {
    state.setTextForTrueAndTextForFalse("On", "Off");
    assertEquals("Off", state.getTextFor(false));
  }

  // ── Icon for true/false ───────────────────────────────────────────

  @Test
  public void trueIcon_initiallyNull() {
    assertNull(state.getTrueIcon());
  }

  @Test
  public void falseIcon_initiallyNull() {
    assertNull(state.getFalseIcon());
  }

  @Test
  public void getIconFor_true_returnsTrueIcon() {
    assertNull(state.getIconFor(true));
  }

  @Test
  public void getIconFor_false_returnsFalseIcon() {
    assertNull(state.getIconFor(false));
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_appendsBooleanValue() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, true);
    assertEquals("true", sb.toString());
  }

  @Test
  public void appendRepresentation_appendsFalse() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, false);
    assertEquals("false", sb.toString());
  }

  // ── Old-school value listener ─────────────────────────────────────

  @Test
  public void valueListener_firesOnChange() {
    AtomicReference<Boolean> prevRef = new AtomicReference<>();
    AtomicReference<Boolean> nextRef = new AtomicReference<>();
    State.ValueListener<Boolean> listener = new State.ValueListener<Boolean>() {
      @Override
      public void changing(State<Boolean> s, Boolean prev, Boolean next) {}

      @Override
      public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        prevRef.set(prev);
        nextRef.set(next);
      }
    };
    state.addValueListener(listener);
    state.setValueTransactionlessly(true);
    assertEquals(Boolean.FALSE, prevRef.get());
    assertEquals(Boolean.TRUE, nextRef.get());
  }

  @Test
  public void valueListener_removedDoesNotFire() {
    AtomicReference<Boolean> captured = new AtomicReference<>();
    State.ValueListener<Boolean> listener = new State.ValueListener<Boolean>() {
      @Override
      public void changing(State<Boolean> s, Boolean prev, Boolean next) {}

      @Override
      public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        captured.set(next);
      }
    };
    state.addValueListener(listener);
    state.removeValueListener(listener);
    state.setValueTransactionlessly(true);
    assertNull(captured.get());
  }

  // ── New-school value listener ─────────────────────────────────────

  @Test
  public void newSchoolListener_firesOnChange() {
    AtomicReference<Boolean> captured = new AtomicReference<>();
    org.lgna.croquet.event.ValueListener<Boolean> listener = e -> captured.set(e.getNextValue());
    state.addNewSchoolValueListener(listener);
    state.setValueTransactionlessly(true);
    assertEquals(Boolean.TRUE, captured.get());
  }

  // ── changeValueFromEdit ───────────────────────────────────────────

  @Test
  public void changeValueFromEdit_updatesValue() {
    state.changeValueFromEdit(true);
    assertTrue(state.getValue());
  }

  // ── setToolTipText ────────────────────────────────────────────────

  @Test
  public void setToolTipText_doesNotThrow() {
    state.setToolTipText("tooltip");
    // No exception — just sets Action.SHORT_DESCRIPTION
  }

  // ── getPotentialPrepModelPaths ─────────────────────────────────────

  @Test
  public void getPotentialPrepModelPaths_returnsNonNull() {
    assertNotNull(state.getPotentialPrepModelPaths(null));
  }

  // ── Toggle sequence ───────────────────────────────────────────────

  @Test
  public void toggleSequence_falseToTrueToFalse() {
    assertFalse(state.getValue());
    state.setValueTransactionlessly(true);
    assertTrue(state.getValue());
    state.setValueTransactionlessly(false);
    assertFalse(state.getValue());
  }

  // ── addAndInvokeValueListener ───────────────────────────────────

  @Test
  public void addAndInvokeValueListener_firesImmediately() {
    AtomicReference<Boolean> captured = new AtomicReference<>();
    state.addAndInvokeValueListener(new State.ValueListener<Boolean>() {
      @Override
      public void changing(State<Boolean> s, Boolean prev, Boolean next) {}

      @Override
      public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        captured.set(next);
      }
    });
    assertEquals(Boolean.FALSE, captured.get());
  }

  // ── addAndInvokeNewSchoolValueListener ────────────────────────────

  @Test
  public void addAndInvokeNewSchoolListener_firesImmediately() {
    AtomicReference<Boolean> captured = new AtomicReference<>();
    state.addAndInvokeNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    assertEquals(Boolean.FALSE, captured.get());
  }

  // ── changing callback ─────────────────────────────────────────────

  @Test
  public void changingCallback_firesBeforeChanged() {
    java.util.List<String> order = new java.util.ArrayList<>();
    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override
      public void changing(State<Boolean> s, Boolean prev, Boolean next) {
        order.add("changing");
      }

      @Override
      public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        order.add("changed");
      }
    });
    state.setValueTransactionlessly(true);
    assertEquals(2, order.size());
    assertEquals("changing", order.get(0));
    assertEquals("changed", order.get(1));
  }

  // ── setIconForTrueAndIconForFalse ─────────────────────────────────

  @Test
  public void setIconForTrueAndIconForFalse_setsIcons() {
    javax.swing.Icon trueIcon = new javax.swing.ImageIcon();
    javax.swing.Icon falseIcon = new javax.swing.ImageIcon();
    state.setIconForTrueAndIconForFalse(trueIcon, falseIcon);
    assertSame(trueIcon, state.getTrueIcon());
    assertSame(falseIcon, state.getFalseIcon());
  }

  @Test
  public void getIconFor_afterSetIcons() {
    javax.swing.Icon trueIcon = new javax.swing.ImageIcon();
    javax.swing.Icon falseIcon = new javax.swing.ImageIcon();
    state.setIconForTrueAndIconForFalse(trueIcon, falseIcon);
    assertSame(trueIcon, state.getIconFor(true));
    assertSame(falseIcon, state.getIconFor(false));
  }

  // ── initializeIfNecessary ─────────────────────────────────────────

  @Test
  public void initializeIfNecessary_doesNotThrow() {
    state.initializeIfNecessary();
  }

  // ── getMigrationId ────────────────────────────────────────────────

  @Test
  public void getMigrationId_returnsNonNull() {
    assertNotNull(state.getMigrationId());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestBooleanState extends BooleanState {
    TestBooleanState(Group group, boolean initialValue) {
      super(group, UUID.randomUUID(), initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestBooleanState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }
}

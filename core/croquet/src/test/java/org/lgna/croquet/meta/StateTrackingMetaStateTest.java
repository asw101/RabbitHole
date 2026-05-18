package org.lgna.croquet.meta;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Group;
import org.lgna.croquet.PrepModel;
import org.lgna.croquet.State;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.event.ValueEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class StateTrackingMetaStateTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-7751-ffffffffffff"), "trackingMeta");

  private TestStringState state;
  private UppercaseMetaState metaState;

  @Before
  public void setUp() {
    state = new TestStringState("alpha");
    metaState = new UppercaseMetaState(state);
  }

  @Test
  public void getValue_reflectsInitialDerivedState() {
    assertEquals("ALPHA", metaState.getValue());
  }

  @Test
  public void underlyingStateChange_updatesDerivedValue() {
    state.setValueTransactionlessly("bravo");

    assertEquals("BRAVO", metaState.getValue());
  }

  @Test
  public void derivedListener_receivesPreviousAndNextValues() {
    List<ValueEvent<String>> events = new ArrayList<>();
    metaState.addValueListener(events::add);

    state.setValueTransactionlessly("bravo");

    assertEquals(1, events.size());
    assertEquals("ALPHA", events.get(0).getPreviousValue());
    assertEquals("BRAVO", events.get(0).getNextValue());
  }

  @Test
  public void derivedListener_doesNotFireWhenUnderlyingChangeKeepsDerivedValueSame() {
    List<ValueEvent<String>> events = new ArrayList<>();
    metaState.addValueListener(events::add);

    state.setValueTransactionlessly("AlPhA");

    assertTrue(events.isEmpty());
    assertEquals("ALPHA", metaState.getValue());
  }

  @Test
  public void addAndInvokeValueListener_reportsCurrentDerivedValueImmediately() {
    List<ValueEvent<String>> events = new ArrayList<>();

    metaState.addAndInvokeValueListener(events::add);

    assertEquals(1, events.size());
    assertFalse(events.get(0).isPreviousValueValid());
    assertEquals("ALPHA", events.get(0).getNextValue());
  }

  @Test
  public void multipleDerivedChanges_fireForEachDistinctDerivedValue() {
    List<ValueEvent<String>> events = new ArrayList<>();
    metaState.addValueListener(events::add);

    state.setValueTransactionlessly("bravo");
    state.setValueTransactionlessly("charlie");

    assertEquals(2, events.size());
    assertEquals("BRAVO", events.get(0).getNextValue());
    assertEquals("CHARLIE", events.get(1).getNextValue());
  }

  @Test
  public void nullToValue_transitionFiresDerivedChange() {
    TestStringState nullableState = new TestStringState(null);
    UppercaseMetaState nullableMeta = new UppercaseMetaState(nullableState);
    List<ValueEvent<String>> events = new ArrayList<>();
    nullableMeta.addValueListener(events::add);

    nullableState.setValueTransactionlessly("delta");

    assertEquals(1, events.size());
    assertNull(events.get(0).getPreviousValue());
    assertEquals("DELTA", events.get(0).getNextValue());
  }

  @Test
  public void valueToNull_transitionFiresDerivedChange() {
    List<ValueEvent<String>> events = new ArrayList<>();
    metaState.addValueListener(events::add);

    state.setValueTransactionlessly(null);

    assertEquals(1, events.size());
    assertEquals("ALPHA", events.get(0).getPreviousValue());
    assertNull(events.get(0).getNextValue());
  }

  private static final class UppercaseMetaState extends StateTrackingMetaState<String, String> {
    private UppercaseMetaState(State<String> state) {
      super(state);
    }

    @Override
    protected String getValue(State<String> state) {
      String value = state.getValue();
      return value != null ? value.toUpperCase() : null;
    }
  }

  private static final class TestStringState extends State<String> {
    private String swingValue;

    private TestStringState(String initialValue) {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID(), initialValue);
      this.swingValue = initialValue;
    }

    @Override
    public String decodeValue(BinaryDecoder binaryDecoder) {
      return binaryDecoder.decodeString();
    }

    @Override
    public void encodeValue(BinaryEncoder binaryEncoder, String value) {
      binaryEncoder.encode(value);
    }

    @Override
    public void appendRepresentation(StringBuilder sb, String value) {
      sb.append(value);
    }

    @Override
    public List<List<PrepModel>> getPotentialPrepModelPaths(Edit edit) {
      return Collections.emptyList();
    }

    @Override
    protected String getSwingValue() {
      return this.swingValue;
    }

    @Override
    protected void setSwingValue(String nextValue) {
      this.swingValue = nextValue;
    }

    @Override
    protected void localize() {
    }
  }
}

package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.edits.Edit;

import javax.swing.Action;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class ItemStateTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-7753-ffffffffffff"), "itemState");

  private String alpha;
  private String beta;
  private TestItemState state;

  @Before
  public void setUp() {
    alpha = new String("alpha");
    beta = new String("beta");
    state = new TestItemState(alpha);
  }

  @Test
  public void getItemCodec_returnsConfiguredCodec() {
    assertSame(CroquetTestUtils.STRING_CODEC, state.getItemCodec());
  }

  @Test
  public void encodeDecode_roundTripsThroughConfiguredCodec() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();

    state.encodeValue(encoder, beta);

    assertEquals(beta, state.decodeValue(encoder.createDecoder()));
  }

  @Test
  public void appendRepresentation_delegatesToCodec() {
    StringBuilder sb = new StringBuilder();

    state.appendRepresentation(sb, beta);

    assertEquals("beta", sb.toString());
  }

  @Test
  public void appendUserRepr_usesCurrentValueRepresentation() {
    StringBuilder sb = new StringBuilder();

    state.appendUserRepr(sb);

    assertEquals("alpha", sb.toString());
  }

  @Test
  public void getItemSelectedState_sameCallable_returnsCachedState() {
    Callable<String> callable = () -> alpha;

    assertSame(state.getItemSelectedState(callable), state.getItemSelectedState(callable));
  }

  @Test
  public void getItemSelectedState_initialSelection_matchesCurrentValue() {
    BooleanState selectedState = state.getItemSelectedState(alpha);
    CroquetTestUtils.removeItemListeners(selectedState);

    assertTrue(selectedState.getValue());
  }

  @Test
  public void changingParentValue_updatesCachedItemSelectedSwingModels() {
    BooleanState selectedAlpha = state.getItemSelectedState(alpha);
    BooleanState selectedBeta = state.getItemSelectedState(beta);
    CroquetTestUtils.removeItemListeners(selectedAlpha);
    CroquetTestUtils.removeItemListeners(selectedBeta);

    state.setValueTransactionlessly(beta);

    assertFalse(selectedAlpha.getImp().getSwingModel().getButtonModel().isSelected());
    assertTrue(selectedBeta.getImp().getSwingModel().getButtonModel().isSelected());
  }

  @Test
  public void getItemSelectionOperation_sameCallable_returnsCachedOperation() {
    Callable<String> callable = () -> alpha;

    assertSame(state.getItemSelectionOperation(callable), state.getItemSelectionOperation(callable));
  }

  @Test
  public void getItemSelectionOperation_localizesNameFromItemRepresentation() {
    Operation operation = state.getItemSelectionOperation(alpha);

    operation.initializeIfNecessary();

    assertEquals("alpha", operation.getImp().getSwingModel().getAction().getValue(Action.NAME));
  }

  @Test
  public void alternateLocalizationItemSelectionOperation_usesEditName() {
    Operation operation = state.getAlternateLocalizationItemSelectionOperation(alpha);

    operation.initializeIfNecessary();

    assertEquals("Edit", operation.getImp().getSwingModel().getAction().getValue(Action.NAME));
  }

  private static final class TestItemState extends ItemState<String> {
    private String swingValue;

    private TestItemState(String initialValue) {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID(), initialValue, CroquetTestUtils.STRING_CODEC);
      this.swingValue = initialValue;
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

package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;

import javax.swing.DefaultButtonModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import java.awt.event.ItemListener;

/**
 * Shared test utilities for croquet unit tests.
 *
 * <p>Centralizes Swing listener removal helpers that decouple state objects
 * from the {@code Application.getActiveInstance()} dependency chain, and
 * provides a reusable {@link ItemCodec} for String-based tests.</p>
 */
public final class CroquetTestUtils {

  private CroquetTestUtils() {}

  /**
   * Removes all ItemListeners from a BooleanState's ButtonModel.
   * Prevents Application.getActiveInstance() calls during headless testing.
   */
  public static void removeItemListeners(BooleanState state) {
    DefaultButtonModel bm = (DefaultButtonModel) state.getImp().getSwingModel().getButtonModel();
    for (ItemListener il : bm.getItemListeners()) {
      bm.removeItemListener(il);
    }
  }

  /**
   * Removes all ChangeListeners from a BoundedNumberState's SpinnerModel.
   * Prevents Application.getActiveInstance() calls during headless testing.
   */
  public static void removeSpinnerChangeListeners(BoundedNumberState<?> state) {
    SpinnerNumberModel spinner = state.getSwingModel().getSpinnerModel();
    for (ChangeListener cl : spinner.getChangeListeners()) {
      spinner.removeChangeListener(cl);
    }
  }

  /**
   * Removes all DocumentListeners from a StringState's Document.
   * Prevents Application.getActiveInstance() calls during headless testing.
   */
  public static void removeDocumentListeners(StringState state) {
    Document doc = state.getSwingModel().getDocument();
    for (DocumentListener dl : ((AbstractDocument) doc).getDocumentListeners()) {
      doc.removeDocumentListener(dl);
    }
  }

  /**
   * Removes all ListSelectionListeners from a SingleSelectListState's model.
   * Prevents NullTrigger → Application.getActiveInstance() calls during headless testing.
   */
  public static void removeListSelectionListeners(SingleSelectListState<?, ?> state) {
    DefaultListSelectionModel lsm =
        (DefaultListSelectionModel) state.getSwingModel().getListSelectionModel();
    for (ListSelectionListener l : lsm.getListSelectionListeners()) {
      lsm.removeListSelectionListener(l);
    }
  }

  /**
   * Minimal {@link ItemCodec} for String values, shared across list-data
   * and list-state tests.
   */
  public static final ItemCodec<String> STRING_CODEC = new ItemCodec<String>() {
    @Override
    public Class<String> getValueClass() {
      return String.class;
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
  };
}

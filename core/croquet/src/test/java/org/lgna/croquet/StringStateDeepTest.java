package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.views.PasswordField;
import org.lgna.croquet.views.SubduedTextField;
import org.lgna.croquet.views.TextArea;
import org.lgna.croquet.views.TextField;

import javax.swing.text.Document;
import java.util.UUID;

import static org.junit.Assert.*;

public class StringStateDeepTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-7752-ffffffffffff"), "stringDeep");

  private TestStringState state;

  @Before
  public void setUp() {
    state = new TestStringState("seed");
    CroquetTestUtils.removeDocumentListeners(state);
  }

  @Test
  public void createTextField_installsSharedDocument() {
    TextField field = state.createTextField();

    assertSame(state.getSwingModel().getDocument(), field.getAwtComponent().getDocument());
  }

  @Test
  public void createPasswordField_installsSharedDocument() {
    PasswordField field = state.createPasswordField();

    assertSame(state.getSwingModel().getDocument(), field.getAwtComponent().getDocument());
  }

  @Test
  public void createTextArea_installsSharedDocument() {
    TextArea area = state.createTextArea();

    assertSame(state.getSwingModel().getDocument(), area.getAwtComponent().getDocument());
  }

  @Test
  public void createSubduedTextField_installsSharedDocument() {
    SubduedTextField field = state.createSubduedTextField();

    assertSame(state.getSwingModel().getDocument(), field.getAwtComponent().getDocument());
  }

  @Test
  public void setEnabled_false_propagatesToInstalledComponents() {
    TextField textField = state.createTextField();
    PasswordField passwordField = state.createPasswordField();
    TextArea textArea = state.createTextArea();

    state.setEnabled(false);

    assertFalse(textField.getAwtComponent().isEnabled());
    assertFalse(passwordField.getAwtComponent().isEnabled());
    assertFalse(textArea.getAwtComponent().isEnabled());
  }

  @Test
  public void setEnabled_true_restoresInstalledComponents() {
    TextField textField = state.createTextField();
    PasswordField passwordField = state.createPasswordField();
    TextArea textArea = state.createTextArea();
    state.setEnabled(false);

    state.setEnabled(true);

    assertTrue(textField.getAwtComponent().isEnabled());
    assertTrue(passwordField.getAwtComponent().isEnabled());
    assertTrue(textArea.getAwtComponent().isEnabled());
  }

  @Test
  public void setTextForBlankCondition_afterInitialization_isAppliedToMostNewComponents() {
    state.initializeIfNecessary();
    state.setTextForBlankCondition("Enter value");

    TextField textField = state.createTextField();
    PasswordField passwordField = state.createPasswordField();
    TextArea textArea = state.createTextArea();
    SubduedTextField subdued = state.createSubduedTextField();

    assertEquals("Enter value", textField.getAwtComponent().getTextForBlankCondition());
    assertEquals("Enter value", passwordField.getAwtComponent().getTextForBlankCondition());
    assertEquals("Enter value", textArea.getAwtComponent().getTextForBlankCondition());
    assertNull(subdued.getAwtComponent().getTextForBlankCondition());
  }

  @Test
  public void setValueTransactionlessly_updatesInstalledComponentText() {
    TextField textField = state.createTextField();
    TextArea textArea = state.createTextArea();

    state.setValueTransactionlessly("updated");

    assertEquals("updated", textField.getAwtComponent().getText());
    assertEquals("updated", textArea.getAwtComponent().getText());
  }

  @Test
  public void manualDocumentMutation_afterRemovingListeners_doesNotChangeModelValue() throws Exception {
    TextField field = state.createTextField();
    Document document = state.getSwingModel().getDocument();

    document.remove(0, document.getLength());
    document.insertString(0, "manual", null);

    assertEquals("seed", state.getValue());
    assertEquals("manual", field.getAwtComponent().getText());
  }

  private static final class TestStringState extends StringState {
    private TestStringState(String initialValue) {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID(), initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestStringState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "deep";
    }
  }
}

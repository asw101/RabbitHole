package org.lgna.croquet.views;

import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.PlainStringValue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LabeledFormRowBehaviorTest {

  @Test
  public void createFromLabelUsesProvidedLeadingLabelAndGrowingTrailingConstraint() {
    Label label = new Label("Name");
    Label trailing = new Label("Value");
    LabeledFormRow row = LabeledFormRow.createFromLabel(label, trailing);
    RecordingFormPanel panel = new RecordingFormPanel();

    row.addComponents(panel);

    assertSame(label, panel.components.get(0));
    assertSame(trailing, panel.components.get(1));
    assertEquals("", panel.constraints.get(0));
    assertEquals("growx, wrap", panel.constraints.get(1));
  }

  @Test
  public void plainStringValueCreatesTrailingAlignedLabelWithVerticalConstraint() {
    TestPlainStringValue stringValue = new TestPlainStringValue("Project");
    Label trailing = new Label("Editor");
    LabeledFormRow row = new LabeledFormRow(stringValue, trailing, VerticalAlignment.TOP, false);
    RecordingFormPanel panel = new RecordingFormPanel();

    row.addComponents(panel);

    SwingComponentView<?> leading = panel.components.get(0);
    assertTrue(leading instanceof AbstractLabel);
    assertEquals(javax.swing.SwingConstants.TRAILING, ((AbstractLabel) leading).getAwtComponent().getHorizontalAlignment());
    assertEquals("aligny top", panel.constraints.get(0));
    assertEquals("wrap", panel.constraints.get(1));
  }

  @Test
  public void bottomAlignedStringValueUsesBottomConstraintAndGrowxTrailing() {
    TestPlainStringValue stringValue = new TestPlainStringValue("Status");
    Label trailing = new Label("Ready");
    LabeledFormRow row = new LabeledFormRow(stringValue, trailing, VerticalAlignment.BOTTOM, true);
    RecordingFormPanel panel = new RecordingFormPanel();

    row.addComponents(panel);

    assertEquals("aligny bottom", panel.constraints.get(0));
    assertEquals("growx, wrap", panel.constraints.get(1));
  }

  private static final class RecordingFormPanel extends FormPanel {
    private final List<SwingComponentView<?>> components = new ArrayList<SwingComponentView<?>>();
    private final List<String> constraints = new ArrayList<String>();

    @Override
    protected void appendRows(List<LabeledFormRow> rows) {
    }

    @Override
    public void addComponent(AwtComponentView<?> component, String constraint) {
      components.add((SwingComponentView<?>) component);
      constraints.add(constraint);
    }
  }

  private static final class TestPlainStringValue extends PlainStringValue {
    private TestPlainStringValue(String text) {
      super(CroquetTestUtils.nextTestUUID());
      this.setText(text);
    }

    @Override
    protected void localize() {
    }
  }
}

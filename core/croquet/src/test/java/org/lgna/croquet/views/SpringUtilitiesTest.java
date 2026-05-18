package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link SpringUtilities} — static helper methods for creating
 * labeled rows and laying out SpringLayout grids.
 *
 * <p>All methods use Swing components that work headlessly without
 * requiring an Application instance.</p>
 */
public class SpringUtilitiesTest {

  // ── createTrailingLabel ───────────────────────────────────────────

  @Test
  public void createTrailingLabel_returnsNonNull() {
    AwtComponentView<?> label = SpringUtilities.createTrailingLabel("Name");
    assertNotNull(label);
  }

  @Test
  public void createTrailingLabel_isLabelInstance() {
    AwtComponentView<?> label = SpringUtilities.createTrailingLabel("Name");
    assertTrue(label instanceof Label);
  }

  @Test
  public void createTrailingLabel_setsText() {
    AwtComponentView<?> label = SpringUtilities.createTrailingLabel("Name");
    JLabel jLabel = (JLabel) label.getAwtComponent();
    assertEquals("Name", jLabel.getText());
  }

  @Test
  public void createTrailingLabel_setsTrailingAlignment() {
    AwtComponentView<?> label = SpringUtilities.createTrailingLabel("Name");
    JLabel jLabel = (JLabel) label.getAwtComponent();
    assertEquals(javax.swing.SwingConstants.TRAILING, jLabel.getHorizontalAlignment());
  }

  @Test
  public void createTrailingLabel_emptyText() {
    AwtComponentView<?> label = SpringUtilities.createTrailingLabel("");
    JLabel jLabel = (JLabel) label.getAwtComponent();
    assertEquals("", jLabel.getText());
  }

  // ── createTrailingTopLabel ────────────────────────────────────────

  @Test
  public void createTrailingTopLabel_returnsNonNull() {
    AwtComponentView<?> label = SpringUtilities.createTrailingTopLabel("Field");
    assertNotNull(label);
  }

  @Test
  public void createTrailingTopLabel_setsText() {
    AwtComponentView<?> label = SpringUtilities.createTrailingTopLabel("Field");
    JLabel jLabel = (JLabel) label.getAwtComponent();
    assertEquals("Field", jLabel.getText());
  }

  @Test
  public void createTrailingTopLabel_setsTrailingAlignment() {
    AwtComponentView<?> label = SpringUtilities.createTrailingTopLabel("Field");
    JLabel jLabel = (JLabel) label.getAwtComponent();
    assertEquals(javax.swing.SwingConstants.TRAILING, jLabel.getHorizontalAlignment());
  }

  @Test
  public void createTrailingTopLabel_setsTopAlignment() {
    AwtComponentView<?> label = SpringUtilities.createTrailingTopLabel("Field");
    JLabel jLabel = (JLabel) label.getAwtComponent();
    assertEquals(javax.swing.SwingConstants.TOP, jLabel.getVerticalAlignment());
  }

  @Test
  public void createTrailingTopLabel_setsBorder() {
    AwtComponentView<?> label = SpringUtilities.createTrailingTopLabel("Field");
    JLabel jLabel = (JLabel) label.getAwtComponent();
    assertNotNull("Border should be set", jLabel.getBorder());
  }

  // ── createRow ─────────────────────────────────────────────────────

  @Test
  public void createRow_returnsInputArray() {
    Label a = new Label("A");
    Label b = new Label("B");
    AwtComponentView<?>[] result = SpringUtilities.createRow(a, b);
    assertSame(a, result[0]);
    assertSame(b, result[1]);
  }

  @Test
  public void createRow_replacesNullWithBox() {
    Label a = new Label("A");
    AwtComponentView<?>[] result = SpringUtilities.createRow(a, null);
    assertSame(a, result[0]);
    assertNotNull("Null entries should be replaced", result[1]);
  }

  @Test
  public void createRow_allNulls_allReplaced() {
    AwtComponentView<?>[] result = SpringUtilities.createRow(null, null, null);
    for (AwtComponentView<?> component : result) {
      assertNotNull(component);
    }
  }

  @Test
  public void createRow_singleComponent() {
    Label a = new Label("A");
    AwtComponentView<?>[] result = SpringUtilities.createRow(a);
    assertEquals(1, result.length);
    assertSame(a, result[0]);
  }

  // ── createLabeledRow ──────────────────────────────────────────────

  @Test
  public void createLabeledRow_addsLabelAsFirstElement() {
    Label comp = new Label("value");
    AwtComponentView<?>[] row = SpringUtilities.createLabeledRow("Label:", comp);
    assertEquals(2, row.length);
    assertTrue("First element should be a Label", row[0] instanceof Label);
  }

  @Test
  public void createLabeledRow_firstElementHasLabelText() {
    Label comp = new Label("value");
    AwtComponentView<?>[] row = SpringUtilities.createLabeledRow("Name:", comp);
    JLabel jLabel = (JLabel) row[0].getAwtComponent();
    assertEquals("Name:", jLabel.getText());
  }

  @Test
  public void createLabeledRow_preservesComponents() {
    Label c1 = new Label("v1");
    Label c2 = new Label("v2");
    AwtComponentView<?>[] row = SpringUtilities.createLabeledRow("L:", c1, c2);
    assertEquals(3, row.length);
    assertSame(c1, row[1]);
    assertSame(c2, row[2]);
  }

  @Test
  public void createLabeledRow_replacesNullComponents() {
    AwtComponentView<?>[] row = SpringUtilities.createLabeledRow("L:", (AwtComponentView<?>) null);
    assertEquals(2, row.length);
    assertNotNull(row[1]);
  }

  // ── createTopLabeledRow ───────────────────────────────────────────

  @Test
  public void createTopLabeledRow_addsTopLabelAsFirstElement() {
    Label comp = new Label("value");
    AwtComponentView<?>[] row = SpringUtilities.createTopLabeledRow("Label:", comp);
    assertEquals(2, row.length);
    assertTrue("First element should be a Label", row[0] instanceof Label);
  }

  @Test
  public void createTopLabeledRow_firstElementHasTopAlignment() {
    Label comp = new Label("value");
    AwtComponentView<?>[] row = SpringUtilities.createTopLabeledRow("Label:", comp);
    JLabel jLabel = (JLabel) row[0].getAwtComponent();
    assertEquals(javax.swing.SwingConstants.TOP, jLabel.getVerticalAlignment());
  }

  @Test
  public void createTopLabeledRow_preservesComponents() {
    Label c1 = new Label("v1");
    AwtComponentView<?>[] row = SpringUtilities.createTopLabeledRow("L:", c1);
    assertEquals(2, row.length);
    assertSame(c1, row[1]);
  }

  @Test
  public void createTopLabeledRow_multipleComponents() {
    Label c1 = new Label("v1");
    Label c2 = new Label("v2");
    Label c3 = new Label("v3");
    AwtComponentView<?>[] row = SpringUtilities.createTopLabeledRow("L:", c1, c2, c3);
    assertEquals(4, row.length);
  }

  // ── springItUpANotch ──────────────────────────────────────────────

  @Test
  public void springItUpANotch_singleRow_returnsSamePanel() {
    TestSpringPanel panel = new TestSpringPanel();
    Label c1 = new Label("A");
    Label c2 = new Label("B");
    List<AwtComponentView<?>[]> rows = new ArrayList<>();
    rows.add(new AwtComponentView<?>[]{c1, c2});

    SpringPanel result = SpringUtilities.springItUpANotch(panel, rows, 4, 4);
    assertSame(panel, result);
  }

  @Test
  public void springItUpANotch_singleRowSingleColumn() {
    TestSpringPanel panel = new TestSpringPanel();
    Label c1 = new Label("A");
    List<AwtComponentView<?>[]> rows = new ArrayList<>();
    rows.add(new AwtComponentView<?>[]{c1});

    SpringPanel result = SpringUtilities.springItUpANotch(panel, rows, 0, 0);
    assertNotNull(result);
    // Verify the component was added
    assertTrue(panel.getAwtComponent().getComponentCount() > 0);
  }

  @Test
  public void springItUpANotch_multipleRows_addsAllComponents() {
    TestSpringPanel panel = new TestSpringPanel();
    Label c1 = new Label("A");
    Label c2 = new Label("B");
    Label c3 = new Label("C");
    Label c4 = new Label("D");
    List<AwtComponentView<?>[]> rows = new ArrayList<>();
    rows.add(new AwtComponentView<?>[]{c1, c2});
    rows.add(new AwtComponentView<?>[]{c3, c4});

    SpringUtilities.springItUpANotch(panel, rows, 6, 6);
    assertEquals(4, panel.getAwtComponent().getComponentCount());
  }

  @Test
  public void springItUpANotch_setsConstraintsOnComponents() {
    TestSpringPanel panel = new TestSpringPanel();
    Label c1 = new Label("A");
    Label c2 = new Label("B");
    List<AwtComponentView<?>[]> rows = new ArrayList<>();
    rows.add(new AwtComponentView<?>[]{c1, c2});

    SpringUtilities.springItUpANotch(panel, rows, 4, 4);
    SpringLayout layout = (SpringLayout) panel.getAwtComponent().getLayout();
    SpringLayout.Constraints constraints = layout.getConstraints(c1.getAwtComponent());
    assertNotNull(constraints);
  }

  @Test
  public void springItUpANotch_largePadding() {
    TestSpringPanel panel = new TestSpringPanel();
    Label c1 = new Label("A");
    Label c2 = new Label("B");
    List<AwtComponentView<?>[]> rows = new ArrayList<>();
    rows.add(new AwtComponentView<?>[]{c1, c2});

    SpringUtilities.springItUpANotch(panel, rows, 100, 100);
    assertEquals(2, panel.getAwtComponent().getComponentCount());
  }

  @Test
  public void springItUpANotch_withZeroPadding() {
    TestSpringPanel panel = new TestSpringPanel();
    Label c1 = new Label("A");
    Label c2 = new Label("B");
    Label c3 = new Label("C");
    Label c4 = new Label("D");
    List<AwtComponentView<?>[]> rows = new ArrayList<>();
    rows.add(new AwtComponentView<?>[]{c1, c2});
    rows.add(new AwtComponentView<?>[]{c3, c4});

    SpringPanel result = SpringUtilities.springItUpANotch(panel, rows, 0, 0);
    assertNotNull(result);
  }

  @Test
  public void springItUpANotch_threeByThreeGrid() {
    TestSpringPanel panel = new TestSpringPanel();
    List<AwtComponentView<?>[]> rows = new ArrayList<>();
    for (int r = 0; r < 3; r++) {
      AwtComponentView<?>[] row = new AwtComponentView<?>[3];
      for (int c = 0; c < 3; c++) {
        row[c] = new Label("R" + r + "C" + c);
      }
      rows.add(row);
    }

    SpringUtilities.springItUpANotch(panel, rows, 8, 8);
    assertEquals(9, panel.getAwtComponent().getComponentCount());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  /**
   * Concrete SpringPanel for testing — SpringPanel is abstract.
   */
  static class TestSpringPanel extends SpringPanel {
    // No additional implementation needed; SpringPanel has all we need.
  }
}

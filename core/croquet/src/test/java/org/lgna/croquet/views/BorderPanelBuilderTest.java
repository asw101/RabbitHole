package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.JPanel;
import java.awt.BorderLayout;

import static org.junit.Assert.*;

/**
 * Tests for {@link BorderPanel} and its {@link BorderPanel.Builder} inner class.
 *
 * <p>The Builder provides a fluent API for constructing BorderPanel instances
 * with components placed in the five BorderLayout regions. All tests work
 * headlessly without an Application instance.</p>
 */
public class BorderPanelBuilderTest {

  // ── Builder creation ──────────────────────────────────────────────

  @Test
  public void builder_createsNonNull() {
    BorderPanel.Builder builder = new BorderPanel.Builder();
    assertNotNull(builder);
  }

  @Test
  public void builder_buildEmptyPanel() {
    BorderPanel panel = new BorderPanel.Builder().build();
    assertNotNull(panel);
  }

  // ── Builder center() ──────────────────────────────────────────────

  @Test
  public void builder_center_setsComponent() {
    Label center = new Label("Center");
    BorderPanel panel = new BorderPanel.Builder().center(center).build();
    assertSame(center, panel.getCenterComponent());
  }

  // ── Builder pageStart() ───────────────────────────────────────────

  @Test
  public void builder_pageStart_setsComponent() {
    Label top = new Label("Top");
    BorderPanel panel = new BorderPanel.Builder().pageStart(top).build();
    assertSame(top, panel.getPageStartComponent());
  }

  // ── Builder pageEnd() ─────────────────────────────────────────────

  @Test
  public void builder_pageEnd_setsComponent() {
    Label bottom = new Label("Bottom");
    BorderPanel panel = new BorderPanel.Builder().pageEnd(bottom).build();
    assertSame(bottom, panel.getPageEndComponent());
  }

  // ── Builder lineStart() ───────────────────────────────────────────

  @Test
  public void builder_lineStart_setsComponent() {
    Label left = new Label("Left");
    BorderPanel panel = new BorderPanel.Builder().lineStart(left).build();
    assertSame(left, panel.getLineStartComponent());
  }

  // ── Builder lineEnd() ─────────────────────────────────────────────

  @Test
  public void builder_lineEnd_setsComponent() {
    Label right = new Label("Right");
    BorderPanel panel = new BorderPanel.Builder().lineEnd(right).build();
    assertSame(right, panel.getLineEndComponent());
  }

  // ── Builder hgap/vgap ─────────────────────────────────────────────

  @Test
  public void builder_hgap_setsGap() {
    BorderPanel panel = new BorderPanel.Builder().hgap(10).build();
    BorderLayout layout = (BorderLayout) panel.getAwtComponent().getLayout();
    assertEquals(10, layout.getHgap());
  }

  @Test
  public void builder_vgap_setsGap() {
    BorderPanel panel = new BorderPanel.Builder().vgap(15).build();
    BorderLayout layout = (BorderLayout) panel.getAwtComponent().getLayout();
    assertEquals(15, layout.getVgap());
  }

  @Test
  public void builder_hgapAndVgap_setBothGaps() {
    BorderPanel panel = new BorderPanel.Builder().hgap(5).vgap(8).build();
    BorderLayout layout = (BorderLayout) panel.getAwtComponent().getLayout();
    assertEquals(5, layout.getHgap());
    assertEquals(8, layout.getVgap());
  }

  // ── Builder fluent chaining ───────────────────────────────────────

  @Test
  public void builder_allPositions_setsAllComponents() {
    Label center = new Label("C");
    Label top = new Label("T");
    Label bottom = new Label("B");
    Label left = new Label("L");
    Label right = new Label("R");

    BorderPanel panel = new BorderPanel.Builder()
        .center(center)
        .pageStart(top)
        .pageEnd(bottom)
        .lineStart(left)
        .lineEnd(right)
        .hgap(4)
        .vgap(4)
        .build();

    assertSame(center, panel.getCenterComponent());
    assertSame(top, panel.getPageStartComponent());
    assertSame(bottom, panel.getPageEndComponent());
    assertSame(left, panel.getLineStartComponent());
    assertSame(right, panel.getLineEndComponent());
  }

  @Test
  public void builder_chainingReturnsBuilder() {
    Label comp = new Label("X");
    BorderPanel.Builder builder = new BorderPanel.Builder();
    assertSame(builder, builder.center(comp));
  }

  @Test
  public void builder_hgap_returnsBuilder() {
    BorderPanel.Builder builder = new BorderPanel.Builder();
    assertSame(builder, builder.hgap(5));
  }

  @Test
  public void builder_vgap_returnsBuilder() {
    BorderPanel.Builder builder = new BorderPanel.Builder();
    assertSame(builder, builder.vgap(5));
  }

  // ── BorderPanel constructors ──────────────────────────────────────

  @Test
  public void defaultConstructor_createsPanel() {
    BorderPanel panel = new BorderPanel();
    assertNotNull(panel);
    assertNotNull(panel.getAwtComponent());
  }

  @Test
  public void gapConstructor_setsGaps() {
    BorderPanel panel = new BorderPanel(12, 14);
    BorderLayout layout = (BorderLayout) panel.getAwtComponent().getLayout();
    assertEquals(12, layout.getHgap());
    assertEquals(14, layout.getVgap());
  }

  @Test
  public void defaultConstructor_hasZeroGaps() {
    BorderPanel panel = new BorderPanel();
    BorderLayout layout = (BorderLayout) panel.getAwtComponent().getLayout();
    assertEquals(0, layout.getHgap());
    assertEquals(0, layout.getVgap());
  }

  // ── BorderPanel.Constraint enum ───────────────────────────────────

  @Test
  public void constraint_CENTER_hasCorrectInternal() {
    assertEquals(BorderLayout.CENTER, BorderPanel.Constraint.CENTER.getInternal());
  }

  @Test
  public void constraint_PAGE_START_hasCorrectInternal() {
    assertEquals(BorderLayout.PAGE_START, BorderPanel.Constraint.PAGE_START.getInternal());
  }

  @Test
  public void constraint_PAGE_END_hasCorrectInternal() {
    assertEquals(BorderLayout.PAGE_END, BorderPanel.Constraint.PAGE_END.getInternal());
  }

  @Test
  public void constraint_LINE_START_hasCorrectInternal() {
    assertEquals(BorderLayout.LINE_START, BorderPanel.Constraint.LINE_START.getInternal());
  }

  @Test
  public void constraint_LINE_END_hasCorrectInternal() {
    assertEquals(BorderLayout.LINE_END, BorderPanel.Constraint.LINE_END.getInternal());
  }

  // ── addComponent / getComponent ───────────────────────────────────

  @Test
  public void addComponent_center_retrieval() {
    BorderPanel panel = new BorderPanel();
    Label c = new Label("C");
    panel.addCenterComponent(c);
    assertSame(c, panel.getCenterComponent());
  }

  @Test
  public void addComponent_pageStart_retrieval() {
    BorderPanel panel = new BorderPanel();
    Label c = new Label("T");
    panel.addPageStartComponent(c);
    assertSame(c, panel.getPageStartComponent());
  }

  @Test
  public void addComponent_pageEnd_retrieval() {
    BorderPanel panel = new BorderPanel();
    Label c = new Label("B");
    panel.addPageEndComponent(c);
    assertSame(c, panel.getPageEndComponent());
  }

  @Test
  public void addComponent_lineStart_retrieval() {
    BorderPanel panel = new BorderPanel();
    Label c = new Label("L");
    panel.addLineStartComponent(c);
    assertSame(c, panel.getLineStartComponent());
  }

  @Test
  public void addComponent_lineEnd_retrieval() {
    BorderPanel panel = new BorderPanel();
    Label c = new Label("R");
    panel.addLineEndComponent(c);
    assertSame(c, panel.getLineEndComponent());
  }

  @Test
  public void getComponent_emptyPosition_returnsNull() {
    BorderPanel panel = new BorderPanel();
    assertNull(panel.getCenterComponent());
    assertNull(panel.getPageStartComponent());
    assertNull(panel.getPageEndComponent());
    assertNull(panel.getLineStartComponent());
    assertNull(panel.getLineEndComponent());
  }

  @Test
  public void getComponent_constraint_returnsCorrectComponent() {
    BorderPanel panel = new BorderPanel();
    Label c = new Label("X");
    panel.addComponent(c, BorderPanel.Constraint.CENTER);
    assertSame(c, panel.getComponent(BorderPanel.Constraint.CENTER));
  }

  // ── Layout type ───────────────────────────────────────────────────

  @Test
  public void panel_usesBorderLayout() {
    BorderPanel panel = new BorderPanel();
    assertTrue(panel.getAwtComponent().getLayout() instanceof BorderLayout);
  }
}

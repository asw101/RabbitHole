package edu.cmu.cs.dennisc.java.awt;

import edu.cmu.cs.dennisc.pattern.Criterion;
import edu.cmu.cs.dennisc.pattern.HowMuch;
import org.junit.Test;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import static org.junit.Assert.*;

public class ComponentUtilitiesTest {

  private static class TrackingPanel extends JPanel {
    private final Dimension preferredSize = new Dimension();
    private int doLayoutCalls;
    private int invalidateCalls;
    private int validateCalls;
    private int revalidateCalls;

    private TrackingPanel(int width, int height) {
      preferredSize.setSize(width, height);
    }

    @Override
    public Dimension getPreferredSize() {
      return preferredSize;
    }

    @Override
    public void doLayout() {
      doLayoutCalls++;
      super.doLayout();
    }

    @Override
    public void invalidate() {
      invalidateCalls++;
      super.invalidate();
    }

    @Override
    public void validate() {
      validateCalls++;
      super.validate();
    }

    @Override
    public void revalidate() {
      revalidateCalls++;
      super.revalidate();
    }

    private void resetCounts() {
      doLayoutCalls = 0;
      invalidateCalls = 0;
      validateCalls = 0;
      revalidateCalls = 0;
    }
  }

  private static class TrackingLeaf extends JComponent {
    private final Dimension preferredSize = new Dimension();
    private int doLayoutCalls;
    private int invalidateCalls;
    private int validateCalls;
    private int revalidateCalls;

    private TrackingLeaf(int width, int height) {
      preferredSize.setSize(width, height);
    }

    @Override
    public Dimension getPreferredSize() {
      return preferredSize;
    }

    @Override
    public void doLayout() {
      doLayoutCalls++;
      super.doLayout();
    }

    @Override
    public void invalidate() {
      invalidateCalls++;
      super.invalidate();
    }

    @Override
    public void validate() {
      validateCalls++;
      super.validate();
    }

    @Override
    public void revalidate() {
      revalidateCalls++;
      super.revalidate();
    }

    private void resetCounts() {
      doLayoutCalls = 0;
      invalidateCalls = 0;
      validateCalls = 0;
      revalidateCalls = 0;
    }
  }

  private static class SearchTree {
    private final JPanel root = new JPanel(null);
    private final JButton directButton = new JButton("direct");
    private final JPanel nestedPanel = new JPanel(null);
    private final JButton nestedButton = new JButton("nested");

    private SearchTree() {
      directButton.setName("directButton");
      nestedButton.setName("nestedButton");
      nestedPanel.setName("nestedPanel");
      directButton.setBounds(10, 20, 30, 15);
      nestedPanel.setBounds(40, 70, 60, 40);
      nestedButton.setBounds(5, 6, 30, 15);
      root.add(directButton);
      root.add(nestedPanel);
      nestedPanel.add(nestedButton);
    }
  }

  private static class TrackingTree {
    private final TrackingPanel root = new TrackingPanel(100, 80);
    private final TrackingPanel childPanel = new TrackingPanel(60, 40);
    private final TrackingLeaf childLeaf = new TrackingLeaf(20, 10);
    private final TrackingLeaf grandchildLeaf = new TrackingLeaf(30, 15);

    private TrackingTree() {
      root.setLayout(null);
      childPanel.setLayout(null);
      childLeaf.setBounds(1, 2, 3, 4);
      childPanel.add(grandchildLeaf);
      root.add(childPanel);
      root.add(childLeaf);
      resetCounts();
    }

    private void resetCounts() {
      root.resetCounts();
      childPanel.resetCounts();
      childLeaf.resetCounts();
      grandchildLeaf.resetCounts();
    }
  }

  private static Criterion<Component> nameCriterion(final String name) {
    return new Criterion<Component>() {
      @Override
      public boolean accept(Component component) {
        return name.equals(component.getName());
      }
    };
  }

  @Test
  public void makeStandOut_onJComponent_setsBorderOpaqueAndBackground() {
    JButton button = new JButton("test");

    ComponentUtilities.makeStandOut(button);

    assertNotNull(button.getBorder());
    assertTrue(button.isOpaque());
    assertEquals(Color.GREEN, button.getBackground());
  }

  @Test
  public void makeStandOut_onPlainComponent_setsBackground() {
    Component component = new Component() {
    };

    ComponentUtilities.makeStandOut(component);

    assertEquals(Color.GREEN, component.getBackground());
  }

  @Test
  public void convertPoint_intCoordinates_translatesBetweenComponents() {
    SearchTree tree = new SearchTree();

    Point result = ComponentUtilities.convertPoint(tree.directButton, 5, 7, tree.nestedPanel);

    assertEquals(new Point(-25, -43), result);
  }

  @Test
  public void convertPoint_pointOverload_matchesIntOverload() {
    SearchTree tree = new SearchTree();

    Point result = ComponentUtilities.convertPoint(tree.directButton, new Point(5, 7), tree.nestedPanel);

    assertEquals(new Point(-25, -43), result);
  }

  @Test
  public void convertRectangle_translatesOriginAndPreservesSize() {
    SearchTree tree = new SearchTree();

    Rectangle result = ComponentUtilities.convertRectangle(tree.directButton, new Rectangle(5, 7, 9, 11), tree.nestedPanel);

    assertEquals(new Rectangle(-25, -43, 9, 11), result);
  }

  @Test
  public void findFirstMatch_whenRootMatches_returnsRoot() {
    SearchTree tree = new SearchTree();

    JPanel result = ComponentUtilities.findFirstMatch(tree.root, JPanel.class);

    assertSame(tree.root, result);
  }

  @Test
  public void findFirstMatch_returnsFirstMatchingDescendant() {
    SearchTree tree = new SearchTree();

    JButton result = ComponentUtilities.findFirstMatch(tree.root, JButton.class);

    assertSame(tree.directButton, result);
  }

  @Test
  public void findAllMatches_componentAndDescendants_returnsAllMatchingComponents() {
    SearchTree tree = new SearchTree();

    List<JButton> result = ComponentUtilities.findAllMatches(tree.root, JButton.class);

    assertEquals(2, result.size());
    assertSame(tree.directButton, result.get(0));
    assertSame(tree.nestedButton, result.get(1));
  }

  @Test
  public void findAllMatches_childrenOnly_excludesGrandchildren() {
    SearchTree tree = new SearchTree();

    List<JButton> result = ComponentUtilities.findAllMatches(tree.root, HowMuch.CHILDREN_ONLY, JButton.class);

    assertEquals(1, result.size());
    assertSame(tree.directButton, result.get(0));
  }

  @Test
  public void findAllMatches_withCriterion_filtersMatches() {
    SearchTree tree = new SearchTree();

    List<JButton> result = ComponentUtilities.findAllMatches(tree.root, HowMuch.COMPONENT_AND_DESCENDANTS, JButton.class, nameCriterion("nestedButton"));

    assertEquals(1, result.size());
    assertSame(tree.nestedButton, result.get(0));
  }

  @Test
  public void findFirstAncestor_excludingComponent_returnsParent() {
    SearchTree tree = new SearchTree();

    JPanel result = ComponentUtilities.findFirstAncestor(tree.nestedButton, false, JPanel.class);

    assertSame(tree.nestedPanel, result);
  }

  @Test
  public void findFirstAncestor_includingComponent_canReturnSelf() {
    SearchTree tree = new SearchTree();

    JPanel result = ComponentUtilities.findFirstAncestor(tree.nestedPanel, true, JPanel.class);

    assertSame(tree.nestedPanel, result);
  }

  @Test
  public void doLayoutTree_callsDoLayoutRecursively() {
    TrackingTree tree = new TrackingTree();

    ComponentUtilities.doLayoutTree(tree.root);

    assertTrue(tree.root.doLayoutCalls > 0);
    assertTrue(tree.childPanel.doLayoutCalls > 0);
    assertTrue(tree.childLeaf.doLayoutCalls > 0);
    assertTrue(tree.grandchildLeaf.doLayoutCalls > 0);
  }

  @Test
  public void setSizeToPreferredSizeTree_setsSizesRecursively() {
    TrackingTree tree = new TrackingTree();

    ComponentUtilities.setSizeToPreferredSizeTree(tree.root);

    assertEquals(tree.root.getPreferredSize(), tree.root.getSize());
    assertEquals(tree.childPanel.getPreferredSize(), tree.childPanel.getSize());
    assertEquals(tree.childLeaf.getPreferredSize(), tree.childLeaf.getSize());
    assertEquals(tree.grandchildLeaf.getPreferredSize(), tree.grandchildLeaf.getSize());
  }

  @Test
  public void invalidateTree_callsInvalidateRecursively() {
    TrackingTree tree = new TrackingTree();

    ComponentUtilities.invalidateTree(tree.root);

    assertTrue(tree.root.invalidateCalls > 0);
    assertTrue(tree.childPanel.invalidateCalls > 0);
    assertTrue(tree.childLeaf.invalidateCalls > 0);
    assertTrue(tree.grandchildLeaf.invalidateCalls > 0);
  }

  @Test
  public void validateTree_callsValidateRecursively() {
    TrackingTree tree = new TrackingTree();

    ComponentUtilities.validateTree(tree.root);

    assertTrue(tree.root.validateCalls > 0);
    assertTrue(tree.childPanel.validateCalls > 0);
    assertTrue(tree.childLeaf.validateCalls > 0);
    assertTrue(tree.grandchildLeaf.validateCalls > 0);
  }

  @Test
  public void revalidateTree_callsRevalidateOnJComponentsRecursively() {
    TrackingTree tree = new TrackingTree();

    ComponentUtilities.revalidateTree(tree.root);

    assertTrue(tree.root.revalidateCalls > 0);
    assertTrue(tree.childPanel.revalidateCalls > 0);
    assertTrue(tree.childLeaf.revalidateCalls > 0);
    assertTrue(tree.grandchildLeaf.revalidateCalls > 0);
  }

  @Test
  public void getRootJFrame_withoutFrame_returnsNull() {
    JFrame frame = ComponentUtilities.getRootJFrame(new JPanel());

    assertNull(frame);
  }
}

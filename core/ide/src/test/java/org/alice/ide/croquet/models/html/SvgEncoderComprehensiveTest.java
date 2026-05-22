package org.alice.ide.croquet.models.html;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGIDGenerator;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.views.SwingComponentView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class SvgEncoderComprehensiveTest {

  private Document document;
  private Element parent;

  @Before
  public void setUp() throws Exception {
    document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    parent = document.createElement("div");
    document.appendChild(parent);
  }

  @Test
  public void svgEncoder_isPackagePrivateConcreteClass() {
    assertFalse(Modifier.isPublic(SvgEncoder.class.getModifiers()));
    assertFalse(Modifier.isAbstract(SvgEncoder.class.getModifiers()));
  }

  @Test
  public void svgEncoder_hasSinglePackagePrivateConstructor() {
    Constructor<?>[] constructors = SvgEncoder.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertEquals(Document.class, constructors[0].getParameterTypes()[0]);
    assertFalse(Modifier.isPublic(constructors[0].getModifiers()));
  }

  @Test
  public void getActiveSvg_isNullBeforeLifecycleStarts() {
    assertNull(new SvgEncoder(document).getActiveSVG());
  }

  @Test
  public void pushSvg_makesActiveSvgAvailableDuringCallback() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> assertNotNull(encoder.getActiveSVG()));
  }

  @Test
  public void pushSvg_clearsActiveSvgAfterCallbackCompletes() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> { });
    assertNull(encoder.getActiveSVG());
  }

  @Test
  public void pushSvg_appendsSingleSvgChild() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> { });
    assertEquals(1, parent.getChildNodes().getLength());
  }

  @Test
  public void pushSvg_appendsSvgTag() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> { });
    assertEquals("svg", ((Element) parent.getFirstChild()).getTagName());
  }

  @Test
  public void pushSvg_setsExpectedCssClass() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> { });
    assertEquals("alice-generated-svg", ((Element) parent.getFirstChild()).getAttribute("class"));
  }

  @Test
  public void pushSvg_initialCanvasStartsAtZeroSize() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> {
      SVGGraphics2D svg = encoder.getActiveSVG();
      assertEquals(0, svg.getSVGCanvasSize().width);
      assertEquals(0, svg.getSVGCanvasSize().height);
    });
  }

  @Test(expected = RuntimeException.class)
  public void pushSvg_rejectsNestedSvgCreation() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> encoder.pushSvg(parent, () -> { }));
  }

  @Test
  public void addToSvg_requiresActiveSvg() {
    SvgEncoder encoder = new SvgEncoder(document);
    try {
      encoder.addToSvg(new TestPanelView(5, 5));
      fail("Expected runtime exception");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("SVG"));
    }
  }

  @Test
  public void addToSvg_usesPreferredWidth() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> {
      invokeOnEdt(() -> encoder.addToSvg(new TestPanelView(80, 25)));
      assertEquals(80, encoder.getActiveSVG().getSVGCanvasSize().width);
    });
  }

  @Test
  public void addToSvg_usesPreferredHeight() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> {
      invokeOnEdt(() -> encoder.addToSvg(new TestPanelView(80, 25)));
      assertEquals(25, encoder.getActiveSVG().getSVGCanvasSize().height);
    });
  }

  @Test
  public void addToSvg_maintainsMaximumWidthAcrossMultipleComponents() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> {
      invokeOnEdt(() -> encoder.addToSvg(new TestPanelView(40, 90)));
      invokeOnEdt(() -> encoder.addToSvg(new TestPanelView(120, 30)));
      assertEquals(120, encoder.getActiveSVG().getSVGCanvasSize().width);
    });
  }

  @Test
  public void addToSvg_maintainsMaximumHeightAcrossMultipleComponents() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> {
      invokeOnEdt(() -> encoder.addToSvg(new TestPanelView(40, 90)));
      invokeOnEdt(() -> encoder.addToSvg(new TestPanelView(120, 30)));
      assertEquals(90, encoder.getActiveSVG().getSVGCanvasSize().height);
    });
  }

  @Test
  public void addToSvg_acceptsZeroSizeComponent() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> {
      invokeOnEdt(() -> encoder.addToSvg(new TestPanelView(0, 0)));
      assertEquals(new Dimension(0, 0), encoder.getActiveSVG().getSVGCanvasSize());
    });
  }

  @Test
  public void pushSvg_canAppendDirectlyToDocumentNode() throws Exception {
    Document localDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    SvgEncoder encoder = new SvgEncoder(localDocument);
    encoder.pushSvg(localDocument, () -> { });
    assertNotNull(localDocument.getDocumentElement());
    assertEquals("svg", localDocument.getDocumentElement().getTagName());
  }

  @Test
  public void pushSvg_runsCallbackBeforeAppendingSvgRoot() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> assertEquals(0, parent.getChildNodes().getLength()));
    assertEquals(1, parent.getChildNodes().getLength());
  }

  @Test
  public void pushSvg_canBeReusedAcrossMultipleRenderPasses() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> invokeOnEdt(() -> encoder.addToSvg(new TestPanelView(10, 10))));
    encoder.pushSvg(parent, () -> invokeOnEdt(() -> encoder.addToSvg(new TestPanelView(20, 20))));
    assertEquals(2, parent.getChildNodes().getLength());
  }

  @Test
  public void idGenerator_isSharedAcrossSuccessiveSvgDocuments() {
    SvgEncoder encoder = new SvgEncoder(document);
    AtomicReference<SVGIDGenerator> first = new AtomicReference<>();
    AtomicReference<SVGIDGenerator> second = new AtomicReference<>();
    encoder.pushSvg(parent, () -> first.set(encoder.getActiveSVG().getGeneratorContext().getIDGenerator()));
    encoder.pushSvg(parent, () -> second.set(encoder.getActiveSVG().getGeneratorContext().getIDGenerator()));
    assertSame(first.get(), second.get());
  }

  @Test
  public void addToSvg_paintsWithoutLeavingActiveSvgNullDuringCallback() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> {
      invokeOnEdt(() -> encoder.addToSvg(new TestPanelView(15, 18)));
      assertNotNull(encoder.getActiveSVG());
    });
  }

  @Test
  public void appendedSvgElement_remainsAfterLifecycleCompletes() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parent, () -> { });
    assertSame(parent.getFirstChild(), parent.getChildNodes().item(0));
  }

  private static void invokeOnEdt(Runnable runnable) {
    try {
      SwingUtilities.invokeAndWait(runnable);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static final class TestPanelView extends SwingComponentView<JPanel> {
    private final Dimension preferredSize;

    private TestPanelView(int width, int height) {
      this.preferredSize = new Dimension(width, height);
    }

    @Override
    protected JPanel createAwtComponent() {
      JPanel panel = new JPanel();
      panel.setPreferredSize(preferredSize);
      panel.setSize(preferredSize);
      return panel;
    }
  }
}

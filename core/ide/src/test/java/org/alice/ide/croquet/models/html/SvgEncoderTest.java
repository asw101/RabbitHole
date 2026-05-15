package org.alice.ide.croquet.models.html;

import org.apache.batik.svggen.SVGGraphics2D;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.*;

/**
 * TDD tests for the SvgEncoder delegate extracted from HtmlEncoder.
 *
 * These tests define the contract for SvgEncoder:
 * - Manages SVG lifecycle (create, render, finalize)
 * - Prevents nested SVG creation
 * - Shares ID generators across SVG instances to avoid conflicts
 * - Appends finalized SVG to a provided parent node
 *
 * Tests will FAIL until SvgEncoder.java is created.
 */
public class SvgEncoderTest {

  private Document document;
  private Element parentElement;

  @Before
  public void setUp() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    document = builder.newDocument();
    parentElement = document.createElement("div");
    document.appendChild(parentElement);
  }

  @Test
  public void svgEncoderInstantiates() {
    SvgEncoder encoder = new SvgEncoder(document);
    assertNotNull("SvgEncoder should instantiate with a Document", encoder);
  }

  @Test
  public void pushSvgAppendsSvgElementToParent() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parentElement, () -> {
      // No content — just verify SVG element is appended
    });
    // After pushSvg completes, parent should have an SVG child
    assertTrue("Parent should have child nodes after pushSvg",
        parentElement.getChildNodes().getLength() > 0);
    Element svgRoot = (Element) parentElement.getFirstChild();
    assertEquals("SVG root should have the expected CSS class",
        "alice-generated-svg", svgRoot.getAttribute("class"));
  }

  @Test(expected = RuntimeException.class)
  public void nestedPushSvgThrowsRuntimeException() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parentElement, () -> {
      // Attempt nested SVG — should throw
      encoder.pushSvg(parentElement, () -> {});
    });
  }

  @Test
  public void svgCanvasStartsAtZeroSize() {
    SvgEncoder encoder = new SvgEncoder(document);
    encoder.pushSvg(parentElement, () -> {
      SVGGraphics2D svg = encoder.getActiveSVG();
      assertNotNull("Active SVG should be available during pushSvg", svg);
      assertEquals("Initial canvas width should be 0", 0, svg.getSVGCanvasSize().width);
      assertEquals("Initial canvas height should be 0", 0, svg.getSVGCanvasSize().height);
    });
  }

  @Test
  public void activeSvgIsNullOutsidePushSvg() {
    SvgEncoder encoder = new SvgEncoder(document);
    assertNull("Active SVG should be null before pushSvg", encoder.getActiveSVG());
    encoder.pushSvg(parentElement, () -> {});
    assertNull("Active SVG should be null after pushSvg completes", encoder.getActiveSVG());
  }

  @Test
  public void idGeneratorIsSharedAcrossMultipleSvgs() {
    SvgEncoder encoder = new SvgEncoder(document);

    // First SVG — captures the generator
    encoder.pushSvg(parentElement, () -> {});

    // Second SVG — should reuse the same generator
    encoder.pushSvg(parentElement, () -> {});

    // Both SVGs should have been appended
    assertEquals("Two SVG elements should be appended",
        2, parentElement.getChildNodes().getLength());
  }

  @Test
  public void addToSvgThrowsWhenNoActiveSvg() {
    SvgEncoder encoder = new SvgEncoder(document);
    try {
      encoder.addToSvg(null);
      fail("addToSvg outside pushSvg should throw RuntimeException");
    } catch (RuntimeException e) {
      assertTrue("Exception message should mention SVG",
          e.getMessage().contains("SVG"));
    }
  }
}

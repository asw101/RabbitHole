package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.property.event.PropertyListener;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.awt.Font;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TextTest {

  @Test
  public void defaultTextIsEmpty() {
    Text text = new Text();
    assertEquals("", text.text.getValue());
  }

  @Test
  public void defaultFontIsNotNull() {
    Text text = new Text();
    assertNotNull(text.font.getValue());
  }

  @Test
  public void defaultDepthIsQuarter() {
    Text text = new Text();
    assertEquals(0.25, text.depth.getValue(), 0.001);
  }

  @Test
  public void setTextUpdatesValue() {
    Text text = new Text();
    text.text.setValue("Hello");
    assertEquals("Hello", text.text.getValue());
  }

  @Test
  public void setFontUpdatesValue() {
    Text text = new Text();
    Font f = new Font("SansSerif", Font.BOLD, 24);
    text.font.setValue(f);
    assertEquals(f, text.font.getValue());
  }

  @Test
  public void setDepthUpdatesValue() {
    Text text = new Text();
    text.depth.setValue(0.5);
    assertEquals(0.5, text.depth.getValue(), 0.001);
  }

  @Test
  public void getGlyphVectorIsNotNull() {
    Text text = new Text();
    assertNotNull(text.getGlyphVector());
  }

  @Test
  public void textWithContentHasBoundingBox() {
    Text text = new Text();
    text.text.setValue("Test");
    AxisAlignedBox bbox = text.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
  }

  @Test
  public void getAlignmentOffsetWithDefaultAlignments() {
    Text text = new Text();
    text.text.setValue("Hello World");
    Vector3 offset = text.getAlignmentOffset();
    assertNotNull(offset);
  }

  @Test
  public void leftToRightAlignmentDefaults() {
    Text text = new Text();
    assertEquals(LeftToRightAlignment.ALIGN_CENTER_OF_LEFT_AND_RIGHT,
        text.leftToRightAlignment.getValue());
  }

  @Test
  public void topToBottomAlignmentDefaults() {
    Text text = new Text();
    assertEquals(TopToBottomAlignment.ALIGN_BASELINE,
        text.topToBottomAlignment.getValue());
  }

  @Test
  public void frontToBackAlignmentDefaults() {
    Text text = new Text();
    assertEquals(FrontToBackAlignment.ALIGN_CENTER_OF_FRONT_AND_BACK,
        text.frontToBackAlignment.getValue());
  }

  @Test
  public void changingLeftToRightAlignmentInvalidatesBounds() {
    Text text = new Text();
    text.text.setValue("ABC");
    text.getAxisAlignedMinimumBoundingBox(); // prime cache
    text.leftToRightAlignment.setValue(LeftToRightAlignment.ALIGN_LEFT);
    assertNotNull(text.getAxisAlignedMinimumBoundingBox());
  }

  @Test
  public void changingTopToBottomAlignmentInvalidatesBounds() {
    Text text = new Text();
    text.text.setValue("ABC");
    text.getAxisAlignedMinimumBoundingBox();
    text.topToBottomAlignment.setValue(TopToBottomAlignment.ALIGN_TOP);
    assertNotNull(text.getAxisAlignedMinimumBoundingBox());
  }

  @Test
  public void changingFrontToBackAlignmentInvalidatesBounds() {
    Text text = new Text();
    text.text.setValue("ABC");
    text.getAxisAlignedMinimumBoundingBox();
    text.frontToBackAlignment.setValue(FrontToBackAlignment.ALIGN_FRONT);
    assertNotNull(text.getAxisAlignedMinimumBoundingBox());
  }

  @Test
  public void alignLeftOffset() {
    Text text = new Text();
    text.text.setValue("Test");
    text.leftToRightAlignment.setValue(LeftToRightAlignment.ALIGN_LEFT);
    Vector3 offset = text.getAlignmentOffset();
    assertNotNull(offset);
  }

  @Test
  public void alignRightOffset() {
    Text text = new Text();
    text.text.setValue("Test");
    text.leftToRightAlignment.setValue(LeftToRightAlignment.ALIGN_RIGHT);
    Vector3 offset = text.getAlignmentOffset();
    assertNotNull(offset);
  }

  @Test
  public void alignTopOffset() {
    Text text = new Text();
    text.text.setValue("Test");
    text.topToBottomAlignment.setValue(TopToBottomAlignment.ALIGN_TOP);
    Vector3 offset = text.getAlignmentOffset();
    assertNotNull(offset);
  }

  @Test
  public void alignBottomOffset() {
    Text text = new Text();
    text.text.setValue("Test");
    text.topToBottomAlignment.setValue(TopToBottomAlignment.ALIGN_BOTTOM);
    Vector3 offset = text.getAlignmentOffset();
    assertNotNull(offset);
  }

  @Test
  public void alignBaselineOffset() {
    Text text = new Text();
    text.text.setValue("Test");
    text.topToBottomAlignment.setValue(TopToBottomAlignment.ALIGN_BASELINE);
    Vector3 offset = text.getAlignmentOffset();
    assertNotNull(offset);
  }

  @Test
  public void alignCenterTopBaselineOffset() {
    Text text = new Text();
    text.text.setValue("Test");
    text.topToBottomAlignment.setValue(TopToBottomAlignment.ALIGN_CENTER_OF_TOP_AND_BASELINE);
    Vector3 offset = text.getAlignmentOffset();
    assertNotNull(offset);
  }

  @Test
  public void alignFrontOffset() {
    Text text = new Text();
    text.text.setValue("Test");
    text.frontToBackAlignment.setValue(FrontToBackAlignment.ALIGN_FRONT);
    Vector3 offset = text.getAlignmentOffset();
    assertEquals(0.0, offset.z(), 0.001);
  }

  @Test
  public void alignBackOffset() {
    Text text = new Text();
    text.text.setValue("Test");
    text.frontToBackAlignment.setValue(FrontToBackAlignment.ALIGN_BACK);
    Vector3 offset = text.getAlignmentOffset();
    assertEquals(-0.25, offset.z(), 0.001);
  }

  @Test(expected = RuntimeException.class)
  public void getPlaneThrowsRuntimeException() {
    Text text = new Text();
    text.getPlane();
  }

  @Test(expected = RuntimeException.class)
  public void transformThrowsRuntimeException() {
    Text text = new Text();
    text.transform(null);
  }

  @Test
  public void settingAlignmentToSameValueDoesNotFireEvent() {
    Text text = new Text();
    AtomicInteger events = new AtomicInteger();
    PropertyListener listener = e -> events.incrementAndGet();
    text.leftToRightAlignment.addPropertyListener(listener);
    text.topToBottomAlignment.addPropertyListener(listener);
    text.frontToBackAlignment.addPropertyListener(listener);

    // Setting to existing defaults should not fire events
    text.leftToRightAlignment.setValue(LeftToRightAlignment.ALIGN_CENTER_OF_LEFT_AND_RIGHT);
    text.topToBottomAlignment.setValue(TopToBottomAlignment.ALIGN_BASELINE);
    text.frontToBackAlignment.setValue(FrontToBackAlignment.ALIGN_CENTER_OF_FRONT_AND_BACK);

    assertEquals("No property events should fire for same-value set", 0, events.get());
  }
}

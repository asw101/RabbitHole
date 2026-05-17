package org.lgna.story;

import org.lgna.common.resources.AudioResource;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.FlyerResource;
import org.lgna.story.resources.QuadrupedResource;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for value objects in the org.lgna.story package:
 * Color, Position, Orientation, VantagePoint, AnimationStyle, Duration, Scale, Size, Font.
 * These are simple immutable/value classes — all headless-safe.
 */
public class ValueObjectsTest {

  // ══════════════════════════════════════════════════════════════════════════
  //  Color
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void colorStaticConstantsAreNotNull() {
    assertNotNull(Color.BLACK);
    assertNotNull(Color.WHITE);
    assertNotNull(Color.RED);
    assertNotNull(Color.GREEN);
    assertNotNull(Color.BLUE);
    assertNotNull(Color.YELLOW);
    assertNotNull(Color.CYAN);
    assertNotNull(Color.MAGENTA);
    assertNotNull(Color.ORANGE);
    assertNotNull(Color.PINK);
    assertNotNull(Color.GRAY);
    assertNotNull(Color.DARK_GRAY);
    assertNotNull(Color.LIGHT_GRAY);
    assertNotNull(Color.LIGHT_BLUE);
    assertNotNull(Color.DARK_BLUE);
    assertNotNull(Color.PURPLE);
    assertNotNull(Color.BROWN);
  }

  @Test
  public void colorConstructorRGB() {
    Color c = new Color(0.5, 0.6, 0.7);
    assertEquals(0.5, c.getRed(), 1e-3);
    assertEquals(0.6, c.getGreen(), 1e-3);
    assertEquals(0.7, c.getBlue(), 1e-3);
  }

  @Test
  public void colorGetRedGreenBlue() {
    assertEquals(1.0, Color.RED.getRed(), 1e-3);
    assertEquals(0.0, Color.RED.getGreen(), 1e-3);
    assertEquals(0.0, Color.RED.getBlue(), 1e-3);
  }

  @Test
  public void colorEqualsItself() {
    assertTrue(Color.RED.equals(Color.RED));
  }

  @Test
  public void colorEqualsSameValue() {
    Color a = new Color(0.5, 0.5, 0.5);
    Color b = new Color(0.5, 0.5, 0.5);
    assertEquals(a, b);
  }

  @Test
  public void colorNotEqualsDifferentValue() {
    assertFalse(Color.RED.equals(Color.BLUE));
  }

  @Test
  public void colorNotEqualsNonColor() {
    assertFalse(Color.RED.equals("red"));
  }

  @Test
  public void colorHashCodeConsistent() {
    assertEquals(Color.RED.hashCode(), Color.RED.hashCode());
  }

  @Test
  public void colorToColor4fIsNotNull() {
    assertNotNull(Color.RED.toColor4f());
  }

  @Test
  public void colorToAwtColorIsNotNull() {
    assertNotNull(Color.RED.toAwtColor());
  }

  @Test
  public void colorRgbStringContainsComponents() {
    String s = Color.RED.rgbString();
    assertNotNull(s);
    assertTrue(s.contains("Color("));
  }

  @Test
  public void colorInterpolateMidpoint() {
    Color mid = Color.BLACK.interpolateTo(Color.WHITE, 0.5);
    assertNotNull(mid);
    assertEquals(0.5, mid.getRed(), 0.1);
  }

  @Test
  public void colorInterpolateZeroReturnsSelf() {
    Color result = Color.RED.interpolateTo(Color.BLUE, 0.0);
    assertEquals(1.0, result.getRed(), 1e-3);
  }

  @Test
  public void colorInterpolateOneReturnsTarget() {
    Color result = Color.RED.interpolateTo(Color.BLUE, 1.0);
    assertEquals(1.0, result.getBlue(), 1e-3);
  }

  @Test
  public void colorGetColor4fOrWhiteReturnsColorForColor() {
    edu.cmu.cs.dennisc.color.Color4f c4f = Color.getColor4fOrWhite(Color.RED);
    assertNotNull(c4f);
    assertEquals(1.0f, c4f.red, 1e-3f);
  }

  @Test
  public void colorGetColor4fOrWhiteReturnsWhiteForNull() {
    edu.cmu.cs.dennisc.color.Color4f c4f = Color.getColor4fOrWhite(null);
    assertNotNull(c4f);
  }

  @Test
  public void colorApplyToAwtColor() {
    java.awt.Color result = Color.RED.applyTo(java.awt.Color.WHITE);
    assertNotNull(result);
  }

  @Test
  public void colorCreateInstanceFromAwtColor() {
    Color c = Color.createInstance(java.awt.Color.BLUE);
    assertNotNull(c);
    assertEquals(0.0, c.getRed(), 1e-3);
    assertEquals(0.0, c.getGreen(), 1e-3);
    assertEquals(1.0, c.getBlue(), 1e-3);
  }

  @Test
  public void colorCreateInstanceFromNullAwtReturnsNull() {
    assertNull(Color.createInstance((java.awt.Color) null));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Position
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void positionConstructorRightUpForward() {
    Position pos = new Position(1, 2, 3);
    assertNotNull(pos);
  }

  @Test
  public void positionGetRight() {
    Position pos = new Position(5, 0, 0);
    assertEquals(5.0, pos.getRight(), 1e-6);
  }

  @Test
  public void positionGetUp() {
    Position pos = new Position(0, 5, 0);
    assertEquals(5.0, pos.getUp(), 1e-6);
  }

  @Test
  public void positionGetBackward() {
    Position pos = new Position(0, 0, 5);
    assertEquals(5.0, pos.getBackward(), 1e-6);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Orientation
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void orientationConstructorXYZW() {
    Orientation o = new Orientation(0, 0, 0, 1);
    assertNotNull(o);
  }

  @Test
  public void orientationAsMatrix3x3() {
    Orientation o = new Orientation(0, 0, 0, 1);
    assertNotNull(o.asMatrix3x3());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  VantagePoint
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void vantagePointFromOrientationAndPosition() {
    Orientation o = new Orientation(0, 0, 0, 1);
    Position p = new Position(1, 2, 3);
    VantagePoint vp = new VantagePoint(o, p);
    assertNotNull(vp);
  }

  @Test
  public void vantagePointIdentityIsNotNull() {
    assertNotNull(VantagePoint.IDENTITY);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  AnimationStyle
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void animationStyleValuesExist() {
    assertNotNull(AnimationStyle.BEGIN_AND_END_GENTLY);
    assertNotNull(AnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY);
    assertNotNull(AnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY);
    assertNotNull(AnimationStyle.BEGIN_AND_END_ABRUPTLY);
  }

  @Test
  public void animationStyleGetInternalReturnsNonNull() {
    assertNotNull(AnimationStyle.BEGIN_AND_END_GENTLY.getInternal());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Duration
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void durationConstructor() {
    Duration d = new Duration(2.5);
    assertNotNull(d);
  }

  @Test
  public void durationGetValue() {
    Duration d = new Duration(3.14);
    assertEquals(3.14, Duration.getValue(new Duration[] {d}), 1e-9);
  }

  @Test
  public void durationGetValueDefaultIsOne() {
    assertEquals(1.0, Duration.getValue(new Duration[] {}), 1e-2);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Scale
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void scaleConstructor() {
    Scale s = new Scale(2, 3, 4);
    assertNotNull(s);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Size
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void sizeConstructor() {
    Size s = new Size(1, 2, 3);
    assertNotNull(s);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Font
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void fontConstructorFromAwtFont() {
    Font f = new Font(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
    assertNotNull(f);
  }

  @Test
  public void fontGetAsAWTFontIsNotNull() {
    Font f = new Font(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
    assertNotNull(f.getAsAWTFont());
  }

  @Test
  public void fontDeriveScaledFont() {
    Font f = new Font(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
    Font scaled = f.deriveScaledFont(2.0f);
    assertNotNull(scaled);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Direction enums
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void moveDirectionValues() {
    MoveDirection[] dirs = MoveDirection.values();
    assertTrue(dirs.length >= 6);
    for (MoveDirection d : dirs) {
      assertNotNull(d.name());
    }
  }

  @Test
  public void turnDirectionValues() {
    TurnDirection[] dirs = TurnDirection.values();
    assertTrue(dirs.length >= 2);
  }

  @Test
  public void rollDirectionValues() {
    RollDirection[] dirs = RollDirection.values();
    assertTrue(dirs.length >= 2);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  TextStyle and PathStyle enums
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void textStyleValues() {
    assertNotNull(TextStyle.values());
    assertTrue(TextStyle.values().length > 0);
  }

  @Test
  public void pathStyleValues() {
    assertNotNull(PathStyle.values());
    assertTrue(PathStyle.values().length > 0);
  }

  @Test
  public void setDimensionPolicyValues() {
    assertNotNull(SetDimensionPolicy.values());
    assertTrue(SetDimensionPolicy.values().length > 0);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Key enum
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void keyEnumValuesExist() {
    org.lgna.story.Key[] keys = org.lgna.story.Key.values();
    assertTrue(keys.length > 20);
  }

  @Test
  public void keyEnumHasLetterKeys() {
    assertNotNull(org.lgna.story.Key.A);
    assertNotNull(org.lgna.story.Key.Z);
  }

  @Test
  public void keyEnumHasArrowKeys() {
    assertNotNull(org.lgna.story.Key.UP);
    assertNotNull(org.lgna.story.Key.DOWN);
    assertNotNull(org.lgna.story.Key.LEFT);
    assertNotNull(org.lgna.story.Key.RIGHT);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  MultipleEventPolicy enum
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void multipleEventPolicyValues() {
    assertNotNull(MultipleEventPolicy.values());
    assertTrue(MultipleEventPolicy.values().length >= 3);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SpatialRelation enum
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void spatialRelationValues() {
    assertNotNull(SpatialRelation.values());
    assertTrue(SpatialRelation.values().length > 0);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Position additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void positionEquals() {
    Position a = new Position(1, 2, 3);
    Position b = new Position(1, 2, 3);
    assertEquals(a, b);
  }

  @Test
  public void positionNotEquals() {
    Position a = new Position(1, 2, 3);
    Position b = new Position(4, 5, 6);
    assertFalse(a.equals(b));
  }

  @Test
  public void positionNotEqualsNonPosition() {
    assertFalse(new Position(1, 2, 3).equals("not a position"));
  }

  @Test
  public void positionHashCodeConsistent() {
    Position a = new Position(1, 2, 3);
    Position b = new Position(1, 2, 3);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void positionAsPoint() {
    Position pos = new Position(1, 2, 3);
    assertNotNull(pos.asPoint());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Orientation additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void orientationDefaultConstructor() {
    Orientation o = new Orientation();
    assertNotNull(o);
  }

  @Test
  public void orientationEquals() {
    Orientation a = new Orientation(0, 0, 0, 1);
    Orientation b = new Orientation(0, 0, 0, 1);
    assertEquals(a, b);
  }

  @Test
  public void orientationNotEqualsNonOrientation() {
    assertFalse(new Orientation(0, 0, 0, 1).equals("not"));
  }

  @Test
  public void orientationHashCodeConsistent() {
    Orientation a = new Orientation(0, 0, 0, 1);
    assertEquals(a.hashCode(), a.hashCode());
  }

  @Test
  public void orientationAsUnitQuaternion() {
    Orientation o = new Orientation(0, 0, 0, 1);
    assertNotNull(o.asUnitQuaternion());
  }

  @Test
  public void orientationAsEulerAngles() {
    Orientation o = new Orientation(0, 0, 0, 1);
    assertNotNull(o.asEulerAngles());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  VantagePoint additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void vantagePointIdentityExists() {
    assertNotNull(VantagePoint.IDENTITY);
  }

  @Test
  public void vantagePointGetOrientation() {
    Orientation o = new Orientation(0, 0, 0, 1);
    Position p = new Position(1, 2, 3);
    VantagePoint vp = new VantagePoint(o, p);
    assertNotNull(vp.getOrientation());
  }

  @Test
  public void vantagePointGetPosition() {
    Orientation o = new Orientation(0, 0, 0, 1);
    Position p = new Position(1, 2, 3);
    VantagePoint vp = new VantagePoint(o, p);
    assertNotNull(vp.getPosition());
  }

  @Test
  public void vantagePointEquals() {
    Orientation o = new Orientation(0, 0, 0, 1);
    Position p = new Position(1, 2, 3);
    VantagePoint a = new VantagePoint(o, p);
    VantagePoint b = new VantagePoint(o, p);
    assertEquals(a, b);
  }

  @Test
  public void vantagePointNotEqualsNonVantagePoint() {
    Orientation o = new Orientation(0, 0, 0, 1);
    Position p = new Position(1, 2, 3);
    VantagePoint vp = new VantagePoint(o, p);
    assertFalse(vp.equals("not"));
  }

  @Test
  public void vantagePointHashCodeConsistent() {
    Orientation o = new Orientation(0, 0, 0, 1);
    Position p = new Position(1, 2, 3);
    VantagePoint vp = new VantagePoint(o, p);
    assertEquals(vp.hashCode(), vp.hashCode());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Scale additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void scaleGetLeftToRight() {
    Scale s = new Scale(2, 3, 4);
    assertEquals(2.0, s.getLeftToRight(), 1e-6);
  }

  @Test
  public void scaleGetBottomToTop() {
    Scale s = new Scale(2, 3, 4);
    assertEquals(3.0, s.getBottomToTop(), 1e-6);
  }

  @Test
  public void scaleGetFrontToBack() {
    Scale s = new Scale(2, 3, 4);
    assertEquals(4.0, s.getFrontToBack(), 1e-6);
  }

  @Test
  public void scaleIdentity() {
    assertNotNull(Scale.IDENTITY);
  }

  @Test
  public void scaleEquals() {
    Scale a = new Scale(1, 2, 3);
    Scale b = new Scale(1, 2, 3);
    assertEquals(a, b);
  }

  @Test
  public void scaleHashCodeConsistent() {
    Scale s = new Scale(1, 2, 3);
    assertEquals(s.hashCode(), s.hashCode());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Size additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void sizeGetLeftToRight() {
    Size s = new Size(5, 6, 7);
    assertEquals(5.0, s.getLeftToRight(), 1e-6);
  }

  @Test
  public void sizeGetBottomToTop() {
    Size s = new Size(5, 6, 7);
    assertEquals(6.0, s.getBottomToTop(), 1e-6);
  }

  @Test
  public void sizeGetFrontToBack() {
    Size s = new Size(5, 6, 7);
    assertEquals(7.0, s.getFrontToBack(), 1e-6);
  }

  @Test
  public void sizeEquals() {
    Size a = new Size(1, 2, 3);
    Size b = new Size(1, 2, 3);
    assertEquals(a, b);
  }

  @Test
  public void sizeHashCode() {
    Size a = new Size(1, 2, 3);
    Size b = new Size(1, 2, 3);
    assertEquals(a.hashCode(), b.hashCode());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Font additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void fontDeriveSizeFont() {
    Font f = new Font(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
    Font sized = f.deriveSizeFont(24.0f);
    assertNotNull(sized);
  }

  @Test
  public void fontGetFamily() {
    Font f = new Font(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
    assertNotNull(f.getFamily());
  }

  @Test
  public void fontGetWeight() {
    Font f = new Font(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
    assertNotNull(f.getWeight());
  }

  @Test
  public void fontGetPosture() {
    Font f = new Font(new java.awt.Font("SansSerif", java.awt.Font.ITALIC, 12));
    assertNotNull(f.getPosture());
  }

  @Test
  public void fontGetSize() {
    Font f = new Font(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
    assertNotNull(f.getSize());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  TextStyle additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void textStyleSpecificConstants() {
    assertNotNull(TextStyle.PLAIN);
    assertNotNull(TextStyle.BOLD);
    assertNotNull(TextStyle.ITALIC);
  }

  @Test
  public void textStyleGetDefaultValue() {
    assertNotNull(TextStyle.getDefaultValue());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  PathStyle additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void pathStyleSpecificConstants() {
    assertNotNull(PathStyle.BEE_LINE);
    assertNotNull(PathStyle.SMOOTH);
  }

  @Test
  public void pathStyleGetDefaultValue() {
    assertNotNull(PathStyle.getDefaultValue());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SetDimensionPolicy additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void setDimensionPolicySpecificConstants() {
    assertNotNull(SetDimensionPolicy.PRESERVE_VOLUME);
    assertNotNull(SetDimensionPolicy.PRESERVE_ASPECT_RATIO);
    assertNotNull(SetDimensionPolicy.PRESERVE_NOTHING);
  }

  @Test
  public void setDimensionPolicyGetDefaultValue() {
    assertNotNull(SetDimensionPolicy.getDefaultValue());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  AnimationStyle additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void animationStyleGetDefaultValue() {
    assertNotNull(AnimationStyle.getDefaultValue());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Duration additional
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void durationGetDefaultValue() {
    assertNotNull(Duration.getDefaultValue());
  }

  @Test
  public void durationMake() {
    Duration d = Duration.make(5.0);
    assertNotNull(d);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  IsVolumePreserved
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void isVolumePreservedConstructionTrue() {
    IsVolumePreserved ivp = new IsVolumePreserved(true);
    assertNotNull(ivp);
  }

  @Test
  public void isVolumePreservedConstructionFalse() {
    IsVolumePreserved ivp = new IsVolumePreserved(false);
    assertNotNull(ivp);
  }

  @Test
  public void isVolumePreservedMake() {
    IsVolumePreserved ivp = IsVolumePreserved.make(true);
    assertNotNull(ivp);
  }

  @Test
  public void isVolumePreservedGetDefaultValue() {
    assertNotNull(IsVolumePreserved.getDefaultValue());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  AlongAxisOffset
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void alongAxisOffsetConstruction() {
    AlongAxisOffset aao = new AlongAxisOffset(0.5);
    assertNotNull(aao);
  }

  @Test
  public void alongAxisOffsetMake() {
    AlongAxisOffset aao = AlongAxisOffset.make(1.5);
    assertNotNull(aao);
  }

  @Test
  public void alongAxisOffsetGetDefaultValue() {
    assertNotNull(AlongAxisOffset.getDefaultValue());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  BubblePosition
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void bubblePositionConstants() {
    assertNotNull(BubblePosition.AUTOMATIC);
    assertNotNull(BubblePosition.LEFT);
    assertNotNull(BubblePosition.CENTER);
    assertNotNull(BubblePosition.RIGHT);
  }

  @Test
  public void bubblePositionGetDefaultValue() {
    assertNotNull(BubblePosition.getDefaultValue());
  }

  @Test
  public void bubblePositionValuesCount() {
    assertEquals(4, BubblePosition.values().length);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SetOfVisuals
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void setOfVisualsEmptyConstruction() {
    SetOfVisuals sov = new SetOfVisuals();
    assertNotNull(sov);
  }

  @Test
  public void setOfVisualsGetDefaultValue() {
    assertNotNull(SetOfVisuals.getDefaultValue());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  AudioSource
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void audioSourceConstructionSingleArg() {
    AudioResource ar = new AudioResource(UUID.randomUUID());
    AudioSource as = new AudioSource(ar);
    assertNotNull(as);
  }

  @Test
  public void audioSourceGetVolume() {
    AudioResource ar = new AudioResource(UUID.randomUUID());
    AudioSource as = new AudioSource(ar);
    assertNotNull(as.getVolume());
  }

  @Test
  public void audioSourceGetStartTime() {
    AudioResource ar = new AudioResource(UUID.randomUUID());
    AudioSource as = new AudioSource(ar);
    assertNotNull(as.getStartTime());
  }

  @Test
  public void audioSourceGetStopTime() {
    AudioResource ar = new AudioResource(UUID.randomUUID());
    AudioSource as = new AudioSource(ar);
    assertNotNull(as.getStopTime());
  }

  @Test
  public void audioSourceConstructionWithVolume() {
    AudioResource ar = new AudioResource(UUID.randomUUID());
    AudioSource as = new AudioSource(ar, 0.8);
    assertEquals(0.8, as.getVolume(), 1e-6);
  }

  @Test
  public void audioSourceConstructionWithVolumeAndStart() {
    AudioResource ar = new AudioResource(UUID.randomUUID());
    AudioSource as = new AudioSource(ar, 1.0, 2.5);
    assertEquals(1.0, as.getVolume(), 1e-6);
    assertEquals(2.5, as.getStartTime(), 1e-6);
  }

  @Test
  public void audioSourceConstructionFullArgs() {
    AudioResource ar = new AudioResource(UUID.randomUUID());
    AudioSource as = new AudioSource(ar, 0.5, 1.0, 3.0);
    assertEquals(0.5, as.getVolume(), 1e-6);
    assertEquals(1.0, as.getStartTime(), 1e-6);
    assertEquals(3.0, as.getStopTime(), 1e-6);
  }

  @Test
  public void audioSourceDefaultVolumeCheck() {
    assertTrue(AudioSource.isWithinReasonableEpsilonOfDefaultVolume(1.0));
  }

  @Test
  public void audioSourceDefaultStartTimeCheck() {
    assertTrue(AudioSource.isWithinReasonableEpsilonOfDefaultStartTime(0.0));
  }

  @Test
  public void audioSourceDefaultStopTimeCheck() {
    assertTrue(AudioSource.isDefaultStopTime_aka_NaN(Double.NaN));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  TimerFrequency
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void timerFrequencyConstruction() {
    TimerFrequency tf = new TimerFrequency(1.0);
    assertNotNull(tf);
    assertEquals(1.0, tf.getFrequency(), 1e-9);
  }

  @Test
  public void timerFrequencyASAP() {
    assertNotNull(TimerFrequency.ASAP);
  }

  @Test
  public void timerFrequencyMake() {
    TimerFrequency tf = TimerFrequency.make(2.5);
    assertNotNull(tf);
    assertEquals(2.5, tf.getFrequency(), 1e-9);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  BipedPoseBuilder
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void bipedPoseBuilderConstruction() {
    BipedPoseBuilder builder = new BipedPoseBuilder();
    assertNotNull(builder);
  }

  @Test
  public void bipedPoseBuilderJointReturnsThis() {
    BipedPoseBuilder builder = new BipedPoseBuilder();
    Orientation o = new Orientation(0, 0, 0, 1);
    BipedPoseBuilder result = builder.joint(BipedResource.LEFT_HIP, o);
    assertSame(builder, result);
  }

  @Test
  public void bipedPoseBuilderJointWithQuaternionComponents() {
    BipedPoseBuilder builder = new BipedPoseBuilder();
    BipedPoseBuilder result = builder.joint(BipedResource.LEFT_HIP, 0, 0, 0, 1);
    assertSame(builder, result);
  }

  @Test
  public void bipedPoseBuilderNamedJointMethods() {
    BipedPoseBuilder builder = new BipedPoseBuilder();
    Orientation o = new Orientation(0, 0, 0, 1);
    assertSame(builder, builder.rightShoulder(o));
    assertSame(builder, builder.rightElbow(o));
    assertSame(builder, builder.rightWrist(o));
    assertSame(builder, builder.leftShoulder(o));
    assertSame(builder, builder.leftElbow(o));
    assertSame(builder, builder.leftWrist(o));
    assertSame(builder, builder.rightHip(o));
    assertSame(builder, builder.rightKnee(o));
    assertSame(builder, builder.rightAnkle(o));
    assertSame(builder, builder.leftHip(o));
    assertSame(builder, builder.leftKnee(o));
    assertSame(builder, builder.leftAnkle(o));
    assertSame(builder, builder.rightClavicle(o));
    assertSame(builder, builder.leftClavicle(o));
  }

  @Test
  public void bipedPoseBuilderBuild() {
    BipedPoseBuilder builder = new BipedPoseBuilder();
    builder.joint(BipedResource.LEFT_HIP, new Orientation(0, 0, 0, 1));
    assertNotNull(builder.build());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  QuadrupedPoseBuilder
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void quadrupedPoseBuilderConstruction() {
    QuadrupedPoseBuilder builder = new QuadrupedPoseBuilder();
    assertNotNull(builder);
  }

  @Test
  public void quadrupedPoseBuilderJoint() {
    QuadrupedPoseBuilder builder = new QuadrupedPoseBuilder();
    Orientation o = new Orientation(0, 0, 0, 1);
    assertSame(builder, builder.joint(QuadrupedResource.FRONT_LEFT_SHOULDER, o));
  }

  @Test
  public void quadrupedPoseBuilderJointWithQuaternionComponents() {
    QuadrupedPoseBuilder builder = new QuadrupedPoseBuilder();
    assertSame(builder, builder.joint(QuadrupedResource.FRONT_LEFT_SHOULDER, 0, 0, 0, 1));
  }

  @Test
  public void quadrupedPoseBuilderNamedJointMethods() {
    QuadrupedPoseBuilder builder = new QuadrupedPoseBuilder();
    Orientation o = new Orientation(0, 0, 0, 1);
    assertSame(builder, builder.frontRightShoulder(o));
    assertSame(builder, builder.frontRightKnee(o));
    assertSame(builder, builder.frontRightAnkle(o));
    assertSame(builder, builder.frontLeftShoulder(o));
    assertSame(builder, builder.frontLeftKnee(o));
    assertSame(builder, builder.frontLeftAnkle(o));
    assertSame(builder, builder.backRightHip(o));
    assertSame(builder, builder.backRightKnee(o));
    assertSame(builder, builder.backRightAnkle(o));
    assertSame(builder, builder.backLeftHip(o));
    assertSame(builder, builder.backLeftKnee(o));
    assertSame(builder, builder.backLeftAnkle(o));
  }

  @Test
  public void quadrupedPoseBuilderBuild() {
    QuadrupedPoseBuilder builder = new QuadrupedPoseBuilder();
    builder.joint(QuadrupedResource.FRONT_LEFT_SHOULDER, new Orientation(0, 0, 0, 1));
    assertNotNull(builder.build());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  FlyerPoseBuilder
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void flyerPoseBuilderConstruction() {
    FlyerPoseBuilder builder = new FlyerPoseBuilder();
    assertNotNull(builder);
  }

  @Test
  public void flyerPoseBuilderJoint() {
    FlyerPoseBuilder builder = new FlyerPoseBuilder();
    Orientation o = new Orientation(0, 0, 0, 1);
    assertSame(builder, builder.joint(FlyerResource.LEFT_WING_SHOULDER, o));
  }

  @Test
  public void flyerPoseBuilderJointWithQuaternionComponents() {
    FlyerPoseBuilder builder = new FlyerPoseBuilder();
    assertSame(builder, builder.joint(FlyerResource.LEFT_WING_SHOULDER, 0, 0, 0, 1));
  }

  @Test
  public void flyerPoseBuilderNamedJointMethods() {
    FlyerPoseBuilder builder = new FlyerPoseBuilder();
    Orientation o = new Orientation(0, 0, 0, 1);
    assertSame(builder, builder.rightWingShoulder(o));
    assertSame(builder, builder.rightWingElbow(o));
    assertSame(builder, builder.rightWingWrist(o));
    assertSame(builder, builder.leftWingShoulder(o));
    assertSame(builder, builder.leftWingElbow(o));
    assertSame(builder, builder.leftWingWrist(o));
    assertSame(builder, builder.rightHip(o));
    assertSame(builder, builder.rightKnee(o));
    assertSame(builder, builder.rightAnkle(o));
    assertSame(builder, builder.leftHip(o));
    assertSame(builder, builder.leftKnee(o));
    assertSame(builder, builder.leftAnkle(o));
  }

  @Test
  public void flyerPoseBuilderBuild() {
    FlyerPoseBuilder builder = new FlyerPoseBuilder();
    builder.joint(FlyerResource.LEFT_WING_SHOULDER, new Orientation(0, 0, 0, 1));
    assertNotNull(builder.build());
  }
}

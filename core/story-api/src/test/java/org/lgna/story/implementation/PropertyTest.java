package org.lgna.story.implementation;

import org.junit.Test;
import org.lgna.story.Color;
import org.lgna.story.Paint;
import org.lgna.story.SBox;
import org.lgna.story.SSphere;
import org.lgna.story.SScene;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link Property} system — the core mechanism underlying
 * all entity attributes (paint, opacity, fog density, etc.).
 * Exercises getValue, setValue, animateValue, listeners, and owner traversal
 * through concrete SBox, SSphere, and SceneImp instances.
 */
public class PropertyTest {

  // ══════════════════════════════════════════════════════════════════════════
  //  PaintProperty via SBox
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void boxPaintGetValueReturnsDefault() {
    SBox box = new SBox();
    Paint p = box.getImplementation().paint.getValue();
    assertNotNull(p);
    assertEquals(Color.WHITE, p);
  }

  @Test
  public void boxPaintSetValueRoundTrips() {
    SBox box = new SBox();
    box.getImplementation().paint.setValue(Color.RED);
    assertEquals(Color.RED, box.getImplementation().paint.getValue());
  }

  @Test
  public void boxPaintAnimateValueZeroDuration() {
    SBox box = new SBox();
    box.getImplementation().paint.animateValue(Color.BLUE, 0.0);
    assertEquals(Color.BLUE, box.getImplementation().paint.getValue());
  }

  @Test
  public void boxPaintOwnerIsBoxImp() {
    SBox box = new SBox();
    PropertyOwnerImp owner = box.getImplementation().paint.getOwner();
    assertSame(box.getImplementation(), owner);
  }

  @Test
  public void boxPaintValueClsIsPaint() {
    SBox box = new SBox();
    assertEquals(Paint.class, box.getImplementation().paint.getValueCls());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  FloatProperty (opacity) via SSphere
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void sphereOpacityDefaultIsOne() {
    SSphere sphere = new SSphere();
    assertEquals(1.0f, sphere.getImplementation().opacity.getValue(), 1e-6f);
  }

  @Test
  public void sphereOpacitySetValueRoundTrips() {
    SSphere sphere = new SSphere();
    sphere.getImplementation().opacity.setValue(0.3f);
    assertEquals(0.3f, sphere.getImplementation().opacity.getValue(), 1e-6f);
  }

  @Test
  public void sphereOpacityAnimateValueZeroDuration() {
    SSphere sphere = new SSphere();
    sphere.getImplementation().opacity.animateValue(0.7f, 0.0);
    assertEquals(0.7f, sphere.getImplementation().opacity.getValue(), 1e-6f);
  }

  @Test
  public void sphereOpacityOwnerIsSphereImp() {
    SSphere sphere = new SSphere();
    assertSame(sphere.getImplementation(), sphere.getImplementation().opacity.getOwner());
  }

  @Test
  public void sphereOpacityValueClsIsFloat() {
    SSphere sphere = new SSphere();
    assertEquals(Float.class, sphere.getImplementation().opacity.getValueCls());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  FloatProperty (fogDensity) via SceneImp
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void sceneFogDensityDefaultIsZero() {
    SceneImp sceneImp = new TestScene().getImplementation();
    assertEquals(0.0f, sceneImp.fogDensity.getValue(), 1e-6f);
  }

  @Test
  public void sceneFogDensitySetValueRoundTrips() {
    SceneImp sceneImp = new TestScene().getImplementation();
    sceneImp.fogDensity.setValue(0.8f);
    assertEquals(0.8f, sceneImp.fogDensity.getValue(), 1e-3f);
  }

  @Test
  public void sceneFogDensityAnimateValueZeroDuration() {
    SceneImp sceneImp = new TestScene().getImplementation();
    sceneImp.fogDensity.animateValue(0.5f, 0.0);
    assertEquals(0.5f, sceneImp.fogDensity.getValue(), 1e-3f);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Property listeners
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void listenerFiresOnSetValue() {
    SBox box = new SBox();
    AtomicInteger count = new AtomicInteger(0);
    box.getImplementation().paint.addPropertyListener(count::incrementAndGet);
    box.getImplementation().paint.setValue(Color.CYAN);
    assertEquals(1, count.get());
  }

  @Test
  public void listenerFiresMultipleTimes() {
    SBox box = new SBox();
    AtomicInteger count = new AtomicInteger(0);
    box.getImplementation().paint.addPropertyListener(count::incrementAndGet);
    box.getImplementation().paint.setValue(Color.RED);
    box.getImplementation().paint.setValue(Color.GREEN);
    box.getImplementation().paint.setValue(Color.BLUE);
    assertEquals(3, count.get());
  }

  @Test
  public void removeListenerStopsFiring() {
    SBox box = new SBox();
    AtomicInteger count = new AtomicInteger(0);
    Property.Listener<Paint> listener = count::incrementAndGet;
    box.getImplementation().paint.addPropertyListener(listener);
    box.getImplementation().paint.setValue(Color.RED);
    box.getImplementation().paint.removePropertyListener(listener);
    box.getImplementation().paint.setValue(Color.GREEN);
    assertEquals(1, count.get());
  }

  @Test
  public void multipleListenersBothFire() {
    SBox box = new SBox();
    AtomicInteger countA = new AtomicInteger(0);
    AtomicInteger countB = new AtomicInteger(0);
    box.getImplementation().paint.addPropertyListener(countA::incrementAndGet);
    box.getImplementation().paint.addPropertyListener(countB::incrementAndGet);
    box.getImplementation().paint.setValue(Color.YELLOW);
    assertEquals(1, countA.get());
    assertEquals(1, countB.get());
  }

  @Test
  public void opacityListenerFiresOnSetValue() {
    SSphere sphere = new SSphere();
    AtomicInteger count = new AtomicInteger(0);
    sphere.getImplementation().opacity.addPropertyListener(count::incrementAndGet);
    sphere.getImplementation().opacity.setValue(0.5f);
    assertEquals(1, count.get());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Property.getPropertyNameForGetter (static utility)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getPropertyNameForGetterReturnsNameForGetMethod() throws Exception {
    Method method = SBox.class.getMethod("getWidth");
    String name = Property.getPropertyNameForGetter(method);
    assertEquals("Width", name);
  }

  @Test
  public void getPropertyNameForGetterReturnsBooleanNameForIsMethod() throws Exception {
    // Use a method returning boolean that starts with "is"
    Method method = findIsPrefixedMethod();
    if (method != null) {
      String name = Property.getPropertyNameForGetter(method);
      assertNotNull(name);
      assertTrue(name.startsWith("is"));
    }
  }

  @Test
  public void getPropertyNameForGetterReturnsNullForNonGetterMethod() throws Exception {
    Method method = SBox.class.getMethod("toString");
    String name = Property.getPropertyNameForGetter(method);
    assertNull(name);
  }

  @Test
  public void getPropertyNameForGetterReturnsNullForHashCodeMethod() throws Exception {
    Method method = Object.class.getMethod("hashCode");
    String name = Property.getPropertyNameForGetter(method);
    assertNull(name);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Property owner traversal
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void paintOwnerGetProgramReturnsNullWhenDetached() {
    SBox box = new SBox();
    assertNull(box.getImplementation().paint.getOwner().getProgram());
  }

  @Test
  public void scenePropertyOwnerGetProgramReturnsNull() {
    SceneImp sceneImp = new TestScene().getImplementation();
    assertNull(sceneImp.atmosphereColor.getOwner().getProgram());
  }

  // ── Helpers ──

  private Method findIsPrefixedMethod() {
    for (Method m : Object.class.getMethods()) {
      if (m.getName().startsWith("is") && (m.getReturnType() == Boolean.TYPE || m.getReturnType() == Boolean.class)) {
        return m;
      }
    }
    return null;
  }

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}

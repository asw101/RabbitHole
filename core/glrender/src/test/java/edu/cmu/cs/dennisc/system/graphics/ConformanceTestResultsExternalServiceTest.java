package edu.cmu.cs.dennisc.system.graphics;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * External service integration tests for {@link ConformanceTestResults}.
 * Verifies the singleton pattern, inner class structure, and accessor
 * contracts without requiring a live GL context.
 */
public class ConformanceTestResultsExternalServiceTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  // ── Singleton enum pattern ────────────────────────────────────────

  @Test
  public void class_isEnum() {
    assertTrue(ConformanceTestResults.class.isEnum());
  }

  @Test
  public void singleton_isSameInstance() {
    assertSame(ConformanceTestResults.SINGLETON, ConformanceTestResults.SINGLETON);
  }

  @Test
  public void singleton_enumHasExactlyOneConstant() {
    assertEquals(1, ConformanceTestResults.values().length);
  }

  @Test
  public void singleton_valueOfName() {
    assertSame(ConformanceTestResults.SINGLETON,
        ConformanceTestResults.valueOf("SINGLETON"));
  }

  // ── SharedDetails inner class ─────────────────────────────────────

  @Test
  public void sharedDetails_classExists() {
    assertNotNull(ConformanceTestResults.SharedDetails.class);
  }

  @Test
  public void sharedDetails_isPublicStatic() {
    int mods = ConformanceTestResults.SharedDetails.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertTrue(Modifier.isStatic(mods));
  }

  @Test
  public void sharedDetails_hasVersionField() throws Exception {
    Field f = ConformanceTestResults.SharedDetails.class.getDeclaredField("version");
    f.setAccessible(true);
    assertEquals(String.class, f.getType());
  }

  @Test
  public void sharedDetails_hasVendorField() throws Exception {
    Field f = ConformanceTestResults.SharedDetails.class.getDeclaredField("vendor");
    f.setAccessible(true);
    assertEquals(String.class, f.getType());
  }

  @Test
  public void sharedDetails_hasRendererField() throws Exception {
    Field f = ConformanceTestResults.SharedDetails.class.getDeclaredField("renderer");
    f.setAccessible(true);
    assertEquals(String.class, f.getType());
  }

  @Test
  public void sharedDetails_hasExtensionsField() throws Exception {
    Field f = ConformanceTestResults.SharedDetails.class.getDeclaredField("extensions");
    f.setAccessible(true);
    assertEquals(String[].class, f.getType());
  }

  @Test
  public void sharedDetails_hasGetVersion() throws Exception {
    Method m = ConformanceTestResults.SharedDetails.class.getDeclaredMethod("getVersion");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(String.class, m.getReturnType());
  }

  @Test
  public void sharedDetails_hasGetVendor() throws Exception {
    Method m = ConformanceTestResults.SharedDetails.class.getDeclaredMethod("getVendor");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(String.class, m.getReturnType());
  }

  @Test
  public void sharedDetails_hasGetRenderer() throws Exception {
    Method m = ConformanceTestResults.SharedDetails.class.getDeclaredMethod("getRenderer");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(String.class, m.getReturnType());
  }

  @Test
  public void sharedDetails_hasGetExtensions() throws Exception {
    Method m = ConformanceTestResults.SharedDetails.class.getDeclaredMethod("getExtensions");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(String[].class, m.getReturnType());
  }

  // ── PickDetails inner class ───────────────────────────────────────

  @Test
  public void pickDetails_classExists() {
    assertNotNull(ConformanceTestResults.PickDetails.class);
  }

  @Test
  public void pickDetails_isAbstract() {
    assertTrue(Modifier.isAbstract(ConformanceTestResults.PickDetails.class.getModifiers()));
  }

  @Test
  public void pickDetails_isPublicStatic() {
    int mods = ConformanceTestResults.PickDetails.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertTrue(Modifier.isStatic(mods));
  }

  @Test
  public void pickDetails_hasIsPickFunctioningCorrectly() throws Exception {
    Method m = ConformanceTestResults.PickDetails.class.getDeclaredMethod("isPickFunctioningCorrectly");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void pickDetails_hasFishyPickValueConstant() throws Exception {
    Field f = ConformanceTestResults.PickDetails.class.getDeclaredField("FISHY_PICK_VALUE");
    f.setAccessible(true);
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    long val = f.getLong(null);
    assertEquals(0x80000000L, val);
  }

  // ── SynchronousPickDetails inner class ────────────────────────────

  @Test
  public void synchronousPickDetails_classExists() {
    assertNotNull(ConformanceTestResults.SynchronousPickDetails.class);
  }

  @Test
  public void synchronousPickDetails_extendsPickDetails() {
    assertTrue(ConformanceTestResults.PickDetails.class.isAssignableFrom(
        ConformanceTestResults.SynchronousPickDetails.class));
  }

  @Test
  public void synchronousPickDetails_isFinal() {
    assertTrue(Modifier.isFinal(ConformanceTestResults.SynchronousPickDetails.class.getModifiers()));
  }

  @Test
  public void synchronousPickDetails_hasHardwareAccelFields() throws Exception {
    ConformanceTestResults.SynchronousPickDetails.class.getDeclaredField(
        "isReportingPickCanBeHardwareAccelerated");
    ConformanceTestResults.SynchronousPickDetails.class.getDeclaredField(
        "isPickActuallyHardwareAccelerated");
  }

  @Test
  public void synchronousPickDetails_hasIsReportingMethod() throws Exception {
    Method m = ConformanceTestResults.SynchronousPickDetails.class.getDeclaredMethod(
        "isReportingPickCanBeHardwareAccelerated");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void synchronousPickDetails_hasIsActuallyMethod() throws Exception {
    Method m = ConformanceTestResults.SynchronousPickDetails.class.getDeclaredMethod(
        "isPickActuallyHardwareAccelerated");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  // ── AsynchronousPickDetails inner class ───────────────────────────

  @Test
  public void asynchronousPickDetails_classExists() {
    assertNotNull(ConformanceTestResults.AsynchronousPickDetails.class);
  }

  @Test
  public void asynchronousPickDetails_extendsPickDetails() {
    assertTrue(ConformanceTestResults.PickDetails.class.isAssignableFrom(
        ConformanceTestResults.AsynchronousPickDetails.class));
  }

  @Test
  public void asynchronousPickDetails_isFinal() {
    assertTrue(Modifier.isFinal(ConformanceTestResults.AsynchronousPickDetails.class.getModifiers()));
  }

  // ── Accessor methods on SINGLETON ─────────────────────────────────

  @Test
  public void getSharedDetails_methodExists() throws Exception {
    Method m = ConformanceTestResults.class.getDeclaredMethod("getSharedDetails");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getSynchronousPickDetails_methodExists() throws Exception {
    Method m = ConformanceTestResults.class.getDeclaredMethod("getSynchronousPickDetails");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getAsynchronousPickDetails_methodExists() throws Exception {
    Method m = ConformanceTestResults.class.getDeclaredMethod("getAsynchronousPickDetails");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ── Update methods ────────────────────────────────────────────────

  @Test
  public void updateRenderInformationIfNecessary_methodExists() throws Exception {
    Method m = ConformanceTestResults.class.getDeclaredMethod(
        "updateRenderInformationIfNecessary", com.jogamp.opengl.GL.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void updateSynchronousPickInformationIfNecessary_methodExists() throws Exception {
    Method m = ConformanceTestResults.class.getDeclaredMethod(
        "updateSynchronousPickInformationIfNecessary",
        com.jogamp.opengl.GL2.class, boolean.class, boolean.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void updateAsynchronousPickInformationIfNecessary_methodExists() throws Exception {
    Method m = ConformanceTestResults.class.getDeclaredMethod(
        "updateAsynchronousPickInformationIfNecessary",
        com.jogamp.opengl.GL2.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ── convertZValueToLong / convertZValueToFloat (private helpers) ──

  @Test
  public void convertZValueToLong_methodExists() throws Exception {
    Method m = ConformanceTestResults.PickDetails.class.getDeclaredMethod(
        "convertZValueToLong", int.class);
    m.setAccessible(true);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(long.class, m.getReturnType());
  }

  @Test
  public void convertZValueToFloat_methodExists() throws Exception {
    Method m = ConformanceTestResults.PickDetails.class.getDeclaredMethod(
        "convertZValueToFloat", long.class);
    m.setAccessible(true);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(float.class, m.getReturnType());
  }

  @Test
  public void convertZValueToLong_zero_returnsZero() throws Exception {
    Method m = ConformanceTestResults.PickDetails.class.getDeclaredMethod(
        "convertZValueToLong", int.class);
    m.setAccessible(true);
    assertEquals(0L, m.invoke(null, 0));
  }

  @Test
  public void convertZValueToLong_negativeOne_returnsMaxUnsigned() throws Exception {
    Method m = ConformanceTestResults.PickDetails.class.getDeclaredMethod(
        "convertZValueToLong", int.class);
    m.setAccessible(true);
    long result = (long) m.invoke(null, -1);
    assertEquals(edu.cmu.cs.dennisc.render.gl.imp.PickContext.MAX_UNSIGNED_INTEGER, result);
  }

  @Test
  public void convertZValueToFloat_zero_returnsZero() throws Exception {
    Method m = ConformanceTestResults.PickDetails.class.getDeclaredMethod(
        "convertZValueToFloat", long.class);
    m.setAccessible(true);
    assertEquals(0.0f, (float) m.invoke(null, 0L), 0.0001f);
  }

  @Test
  public void convertZValueToFloat_maxUnsigned_returnsOne() throws Exception {
    Method m = ConformanceTestResults.PickDetails.class.getDeclaredMethod(
        "convertZValueToFloat", long.class);
    m.setAccessible(true);
    float result = (float) m.invoke(null,
        edu.cmu.cs.dennisc.render.gl.imp.PickContext.MAX_UNSIGNED_INTEGER);
    assertEquals(1.0f, result, 0.0001f);
  }
}

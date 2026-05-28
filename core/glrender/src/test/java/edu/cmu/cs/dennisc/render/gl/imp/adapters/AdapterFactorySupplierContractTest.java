package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.AmbientLight;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Cylinder;
import edu.cmu.cs.dennisc.scenegraph.DirectionalLight;
import edu.cmu.cs.dennisc.scenegraph.Element;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.Layer;
import edu.cmu.cs.dennisc.scenegraph.Mesh;
import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.Sphere;
import edu.cmu.cs.dennisc.scenegraph.SpotLight;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.Torus;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * TDD contract tests for PR #830: AdapterFactory Supplier migration.
 *
 * These tests verify the behavioral contracts that MUST be preserved when
 * replacing reflection-based adapter instantiation (s_classToAdapterClassMap +
 * Class.newInstance()) with a Supplier&lt;T&gt; map (s_supplierMap).
 *
 * FAILS on develop: AdapterFactory uses reflection via s_classToAdapterClassMap;
 *   s_supplierMap field doesn't exist, register() takes Class not Supplier.
 * PASSES after PR #830 merge: Supplier-based registration and creation.
 */
public class AdapterFactorySupplierContractTest {

  // ── Supplier map existence (FAILS on develop: field doesn't exist) ──

  @Test
  public void supplierMapField_exists() throws Exception {
    Field field = AdapterFactory.class.getDeclaredField("s_supplierMap");
    assertNotNull("AdapterFactory must have s_supplierMap field after PR #830", field);
  }

  @Test
  public void supplierMapField_isMapType() throws Exception {
    Field field = AdapterFactory.class.getDeclaredField("s_supplierMap");
    assertTrue("s_supplierMap must be a Map", Map.class.isAssignableFrom(field.getType()));
  }

  @Test
  public void supplierMap_isPopulated() throws Exception {
    Map<?, ?> map = getSupplierMap();
    assertNotNull(map);
    assertTrue("s_supplierMap must have registrations", map.size() > 0);
  }

  @Test
  public void supplierMap_containsCoreScenegraphTypes() throws Exception {
    Map<?, ?> map = getSupplierMap();

    Class<?>[] requiredTypes = {
        Scene.class, Transformable.class, Visual.class,
        AmbientLight.class, DirectionalLight.class, SpotLight.class,
        SymmetricPerspectiveCamera.class, OrthographicCamera.class,
        Box.class, Sphere.class, Cylinder.class, Torus.class,
        Joint.class, Layer.class, TexturedAppearance.class,
        Mesh.class, BufferedImageTexture.class
    };

    for (Class<?> type : requiredTypes) {
      Object supplier = map.get(type);
      assertNotNull("Missing Supplier registration for " + type.getSimpleName(), supplier);
      assertTrue(type.getSimpleName() + " must be registered as Supplier",
          supplier instanceof Supplier);
    }
  }

  @Test
  public void supplierMap_hasAtLeast30Registrations() throws Exception {
    Map<?, ?> map = getSupplierMap();
    assertTrue("Must have at least 30 Supplier registrations (was " + map.size() + ")",
        map.size() >= 30);
  }

  // ── Register method signature (FAILS on develop: takes Class, not Supplier) ──

  @Test
  public void register_acceptsSupplierParameter() throws Exception {
    java.lang.reflect.Method registerMethod = null;
    for (java.lang.reflect.Method m : AdapterFactory.class.getDeclaredMethods()) {
      if ("register".equals(m.getName())) {
        Class<?>[] params = m.getParameterTypes();
        if (params.length == 2 && params[1] == Supplier.class) {
          registerMethod = m;
          break;
        }
      }
    }
    assertNotNull("register() must accept Supplier<> parameter after PR #830", registerMethod);
  }

  // ── Old reflection map removed (FAILS on develop: field exists) ───

  @Test
  public void classToAdapterClassMap_removed() {
    try {
      AdapterFactory.class.getDeclaredField("s_classToAdapterClassMap");
      fail("s_classToAdapterClassMap should be removed after PR #830");
    } catch (NoSuchFieldException expected) {
      // Good — reflection-based map was removed
    }
  }

  // ── Adapter creation behavioral contracts (pass on both) ──────────

  @Test
  public void getAdapterForScene_returnsGlrScene() {
    Scene scene = new Scene();
    GlrElement<?> adapter = AdapterFactory.getAdapterFor((Element) scene);
    assertNotNull(adapter);
    assertTrue("Scene adapter must be GlrScene", adapter instanceof GlrScene);
  }

  @Test
  public void getAdapterForTransformable_returnsGlrTransformable() {
    Transformable t = new Transformable();
    GlrElement<?> adapter = AdapterFactory.getAdapterFor((Element) t);
    assertNotNull(adapter);
    assertTrue("Transformable adapter must be GlrTransformable",
        adapter instanceof GlrTransformable);
  }

  @Test
  public void getAdapterFor_sameElement_returnsCachedInstance() {
    Scene scene = new Scene();
    GlrElement<?> first = AdapterFactory.getAdapterFor((Element) scene);
    GlrElement<?> second = AdapterFactory.getAdapterFor((Element) scene);
    assertSame("Same element must return same adapter", first, second);
  }

  @Test
  public void getAdapterFor_differentElements_returnsDifferentAdapters() {
    Scene s1 = new Scene();
    Scene s2 = new Scene();
    GlrElement<?> a1 = AdapterFactory.getAdapterFor((Element) s1);
    GlrElement<?> a2 = AdapterFactory.getAdapterFor((Element) s2);
    assertNotSame("Different elements must have different adapters", a1, a2);
  }

  @Test
  public void getAdapterFor_null_returnsNull() {
    GlrElement<?> adapter = AdapterFactory.getAdapterFor((Element) null);
    assertNull(adapter);
  }

  @Test
  public void adapter_ownerPointsBackToOriginalElement() {
    Scene scene = new Scene();
    GlrElement<?> adapter = AdapterFactory.getAdapterFor((Element) scene);
    assertNotNull(adapter);
    assertSame("Adapter owner must be the original element", scene, adapter.getOwner());
  }

  @Test
  public void getAdapterFor_bufferedImageTexture_returnsNonNull() {
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB));
    GlrObject<?> adapter = AdapterFactory.getAdapterFor(texture);
    assertNotNull("BufferedImageTexture must get an adapter via Supplier", adapter);
  }

  // ── Thread safety contract ────────────────────────────────────────

  @Test
  public void concurrentCreation_sameElement_returnsSameAdapter() throws Exception {
    Scene scene = new Scene();
    GlrElement<?>[] results = new GlrElement<?>[20];
    Thread[] threads = new Thread[20];

    for (int i = 0; i < 20; i++) {
      final int idx = i;
      threads[i] = new Thread(() -> results[idx] = AdapterFactory.getAdapterFor((Element) scene));
      threads[i].start();
    }
    for (Thread t : threads) {
      t.join();
    }
    for (int i = 1; i < 20; i++) {
      assertSame("Concurrent access must return same adapter", results[0], results[i]);
    }
  }

  private static Map<?, ?> getSupplierMap() throws Exception {
    Field field = AdapterFactory.class.getDeclaredField("s_supplierMap");
    field.setAccessible(true);
    return (Map<?, ?>) field.get(null);
  }
}

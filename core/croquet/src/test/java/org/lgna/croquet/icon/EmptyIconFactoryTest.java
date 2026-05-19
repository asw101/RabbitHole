package org.lgna.croquet.icon;

import org.junit.Before;
import org.junit.Test;

import javax.swing.Icon;
import java.awt.Dimension;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class EmptyIconFactoryTest {

  private EmptyIconFactory factory;
  private Method createIconMethod;
  private Method widthMethod;
  private Method heightMethod;

  @Before
  public void setUp() throws Exception {
    factory = EmptyIconFactory.getInstance();
    createIconMethod = EmptyIconFactory.class.getDeclaredMethod("createIcon", Dimension.class);
    createIconMethod.setAccessible(true);
    widthMethod = EmptyIconFactory.class.getDeclaredMethod("getDefaultSizeForWidth", int.class);
    widthMethod.setAccessible(true);
    heightMethod = EmptyIconFactory.class.getDeclaredMethod("getDefaultSizeForHeight", int.class);
    heightMethod.setAccessible(true);
  }

  // ── Singleton ──────────────────────────────────────────────────────

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(factory);
  }

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(factory, EmptyIconFactory.getInstance());
  }

  @Test
  public void singleton_threadSafety_returnsSameInstanceAcrossCalls() throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(6);
    try {
      List<Callable<EmptyIconFactory>> tasks = new ArrayList<Callable<EmptyIconFactory>>();
      for (int i = 0; i < 12; i++) {
        tasks.add(new Callable<EmptyIconFactory>() {
          @Override
          public EmptyIconFactory call() {
            return EmptyIconFactory.getInstance();
          }
        });
      }

      for (Future<EmptyIconFactory> future : executor.invokeAll(tasks)) {
        assertSame(factory, future.get());
      }
    } finally {
      executor.shutdownNow();
    }
  }

  // ── Hierarchy ──────────────────────────────────────────────────────

  @Test
  public void class_extendsResolutionIndependentIconFactory() {
    assertEquals(ResolutionIndependentIconFactory.class, EmptyIconFactory.class.getSuperclass());
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(Modifier.isAbstract(EmptyIconFactory.class.getModifiers()));
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(EmptyIconFactory.class.getModifiers()));
  }

  // ── Default size helpers ───────────────────────────────────────────

  @Test
  public void getDefaultSizeForWidth_viaReflection() throws Exception {
    Dimension size = (Dimension) widthMethod.invoke(factory, 24);

    assertEquals(24, size.width);
    assertEquals(0, size.height);
  }

  @Test
  public void getDefaultSizeForHeight_viaReflection() throws Exception {
    Dimension size = (Dimension) heightMethod.invoke(factory, 18);

    assertEquals(0, size.width);
    assertEquals(18, size.height);
  }

  // ── Icon creation ──────────────────────────────────────────────────

  @Test
  public void createIcon_viaReflection_createsValidIcon() throws Exception {
    Icon icon = (Icon) createIconMethod.invoke(factory, new Dimension(16, 10));

    assertNotNull(icon);
  }

  @Test
  public void icon_dimensionsMatchRequestedSize() throws Exception {
    Icon icon = (Icon) createIconMethod.invoke(factory, new Dimension(13, 17));

    assertEquals(13, icon.getIconWidth());
    assertEquals(17, icon.getIconHeight());
  }
}

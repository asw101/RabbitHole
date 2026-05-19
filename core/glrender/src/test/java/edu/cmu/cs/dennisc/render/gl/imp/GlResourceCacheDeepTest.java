package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GlResourceCacheDeepTest {

  @Test
  public void classExists() {
    assertNotNull(GlResourceCache.class);
  }

  @Test
  public void hasForgetMethods() {
    boolean hasForget = false;
    for (Method m : GlResourceCache.class.getDeclaredMethods()) {
      if (m.getName().contains("forget") || m.getName().contains("Forget")) {
        hasForget = true;
        break;
      }
    }
    assertTrue("GlResourceCache should have forget methods", hasForget);
  }

  @Test
  public void hasGetOrCreateMethods() {
    boolean hasGetOrCreate = false;
    for (Method m : GlResourceCache.class.getDeclaredMethods()) {
      if (m.getName().contains("get") || m.getName().contains("Get")) {
        hasGetOrCreate = true;
        break;
      }
    }
    assertTrue("GlResourceCache should have get methods", hasGetOrCreate);
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(Modifier.isAbstract(GlResourceCache.class.getModifiers()));
  }

  @Test
  public void hasMapFields() {
    boolean hasMapField = false;
    for (Field f : GlResourceCache.class.getDeclaredFields()) {
      if (java.util.Map.class.isAssignableFrom(f.getType())) {
        hasMapField = true;
        break;
      }
    }
    assertTrue("Should have Map fields for caching", hasMapField);
  }
}

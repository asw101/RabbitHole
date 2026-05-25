package edu.cmu.cs.dennisc.render.gl.imp;



import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GetUtilitiesStructureTest {



  @Test
  public void getBoolean_isPublicStatic() throws Exception {
    Method m = GetUtilities.class.getMethod("getBoolean", com.jogamp.opengl.GL.class, int.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void getInteger_isPublicStatic() throws Exception {
    Method m = GetUtilities.class.getMethod("getInteger", com.jogamp.opengl.GL.class, int.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void getFloat_isPublicStatic() throws Exception {
    Method m = GetUtilities.class.getMethod("getFloat", com.jogamp.opengl.GL.class, int.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(float.class, m.getReturnType());
  }

  @Test
  public void getDouble_isPublicStatic() throws Exception {
    Method m = GetUtilities.class.getMethod("getDouble", com.jogamp.opengl.GL2.class, int.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(double.class, m.getReturnType());
  }

  @Test
  public void getTexParameterInteger_isPublicStatic() throws Exception {
    Method m = GetUtilities.class.getMethod("getTexParameterInteger", com.jogamp.opengl.GL.class, int.class, int.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void getTexParameterFloat_isPublicStatic() throws Exception {
    Method m = GetUtilities.class.getMethod("getTexParameterFloat", com.jogamp.opengl.GL.class, int.class, int.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void totalMethodCount() {
    int count = 0;
    for (Method m : GetUtilities.class.getDeclaredMethods()) {
      if (Modifier.isPublic(m.getModifiers())) {
        count++;
      }
    }
    assertEquals(6, count);
  }
}

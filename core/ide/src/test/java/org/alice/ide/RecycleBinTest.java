package org.alice.ide;

import org.junit.Test;
import org.lgna.croquet.DropReceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

public class RecycleBinTest {
  @Test
  public void recycleBinExposesSingleEnumSingleton() {
    assertSame(RecycleBin.SINGLETON, RecycleBin.valueOf("SINGLETON"));
    assertArrayEquals(new RecycleBin[]{RecycleBin.SINGLETON}, RecycleBin.values());
  }

  @Test
  public void recycleBinDropReceptorMethodIsPublicInstanceMethod() throws Exception {
    Method method = RecycleBin.class.getMethod("getDropReceptor");

    assertEquals(DropReceptor.class, method.getReturnType());
    assertFalse(Modifier.isStatic(method.getModifiers()));
  }
}

package org.alice.ide.cascade.fillerinners;

import org.junit.Test;
import org.lgna.croquet.CascadeFillIn;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ResourceFillerInnerStructureTest {
  @Test
  public void classIsPublicAbstractAndExtendsExpressionFillerInner() {
    assertTrue(Modifier.isPublic(ResourceFillerInner.class.getModifiers()));
    assertTrue(Modifier.isAbstract(ResourceFillerInner.class.getModifiers()));
    assertEquals(ExpressionFillerInner.class, ResourceFillerInner.class.getSuperclass());
  }

  @Test
  public void constructorAcceptsResourceClass() throws Exception {
    Constructor<ResourceFillerInner> constructor = ResourceFillerInner.class.getDeclaredConstructor(Class.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void abstractTemplateMethodsUseCascadeFillIns() throws Exception {
    Method expressionFillIn = ResourceFillerInner.class.getDeclaredMethod("getResourceExpressionFillIn", org.lgna.common.Resource.class);
    Method importFillIn = ResourceFillerInner.class.getDeclaredMethod("getImportNewResourceFillIn");

    assertEquals(CascadeFillIn.class, expressionFillIn.getReturnType());
    assertEquals(CascadeFillIn.class, importFillIn.getReturnType());
    assertTrue(Modifier.isAbstract(expressionFillIn.getModifiers()));
    assertTrue(Modifier.isAbstract(importFillIn.getModifiers()));
  }
}

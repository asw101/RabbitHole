package org.alice.ide.perspectives.noproject;

import org.junit.Test;
import org.lgna.croquet.SimpleComposite;
import org.lgna.croquet.views.CompositeView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class MainCompositeStructureTest {
  @Test
  public void classExtendsSimpleComposite() {
    assertEquals(SimpleComposite.class, MainComposite.class.getSuperclass());
    assertTrue(Modifier.isPublic(MainComposite.class.getModifiers()));
  }

  @Test
  public void constructorAcceptsProjectDocumentFrame() throws Exception {
    Constructor<MainComposite> constructor = MainComposite.class.getConstructor(org.alice.ide.ProjectDocumentFrame.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void createViewMethodReturnsCompositeView() throws Exception {
    Method method = MainComposite.class.getDeclaredMethod("createView");
    assertEquals(CompositeView.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));

    MainComposite composite = new MainComposite(null);
    method.setAccessible(true);
    Object view = method.invoke(composite);
    assertNotNull(view);
    assertEquals(org.lgna.croquet.views.BorderPanel.class, view.getClass());
  }
}

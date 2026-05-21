package org.alice.ide.perspectives.noproject;

import org.junit.Test;
import org.lgna.croquet.AbstractPerspective;
import org.lgna.croquet.Composite;
import org.lgna.croquet.ToolBarComposite;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class NoProjectPerspectiveStructureTest {
  @Test
  public void classExtendsAbstractPerspective() {
    assertEquals(AbstractPerspective.class, NoProjectPerspective.class.getSuperclass());
    assertTrue(Modifier.isPublic(NoProjectPerspective.class.getModifiers()));
  }

  @Test
  public void constructorAcceptsProjectDocumentFrame() throws Exception {
    Constructor<NoProjectPerspective> constructor = NoProjectPerspective.class.getConstructor(org.alice.ide.ProjectDocumentFrame.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void accessorsReturnExpectedTypes() throws Exception {
    Method menuBar = NoProjectPerspective.class.getMethod("getMenuBarComposite");
    Method toolbar = NoProjectPerspective.class.getMethod("getToolBarComposite");
    Method main = NoProjectPerspective.class.getMethod("getMainComposite");

    assertEquals(org.lgna.croquet.MenuBarComposite.class, menuBar.getReturnType());
    assertEquals(ToolBarComposite.class, toolbar.getReturnType());
    assertEquals(Composite.class, main.getReturnType());
  }
}

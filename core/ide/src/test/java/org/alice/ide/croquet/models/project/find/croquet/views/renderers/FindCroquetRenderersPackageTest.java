package org.alice.ide.croquet.models.project.find.croquet.views.renderers;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class FindCroquetRenderersPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.project.find.croquet.views.renderers", SearchReferencesTreeCellRenderer.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.project.find.croquet.views.renderers", SearchResultListCellRenderer.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(SearchReferencesTreeCellRenderer.class.getModifiers()));
    assertFalse(Modifier.isInterface(SearchResultListCellRenderer.class.getModifiers()));
  }
}

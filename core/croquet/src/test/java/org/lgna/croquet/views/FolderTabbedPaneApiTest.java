package org.lgna.croquet.views;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Characterization test ensuring the public API of {@link FolderTabbedPane}
 * remains unchanged after the inner-class extraction refactor.
 */
public class FolderTabbedPaneApiTest {

  private final Class<?> clazz = FolderTabbedPane.class;

  @Test
  public void extendsCardBasedTabbedPane() {
    assertEquals("FolderTabbedPane must extend CardBasedTabbedPane",
        "CardBasedTabbedPane", clazz.getSuperclass().getSimpleName());
  }

  @Test
  public void hasTypeParameterBoundedByTabComposite() {
    TypeVariable<?>[] params = clazz.getTypeParameters();
    assertEquals("Expect exactly one type parameter", 1, params.length);
    assertEquals("E", params[0].getName());
    String bound = params[0].getBounds()[0].getTypeName();
    assertTrue("E must be bounded by TabComposite<?>", bound.contains("TabComposite"));
  }

  @Test
  public void publicMethodSignaturesPreserved() {
    Set<String> expected = new TreeSet<>(Arrays.asList(
        "setHeaderLeadingComponent",
        "setHeaderTrailingComponent",
        "setBackgroundColor",
        "setForegroundColor"
    ));

    Set<String> actual = Arrays.stream(clazz.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toCollection(TreeSet::new));

    for (String name : expected) {
      assertTrue("Public method '" + name + "' must exist", actual.contains(name));
    }
  }

  @Test
  public void constructorAcceptsTabState() throws NoSuchMethodException {
    var ctor = clazz.getDeclaredConstructors();
    boolean found = Arrays.stream(ctor).anyMatch(c -> {
      var paramTypes = c.getParameterTypes();
      return paramTypes.length == 1 && paramTypes[0].getSimpleName().equals("TabState");
    });
    assertTrue("Constructor(TabState) must exist", found);
  }

  @Test
  public void protectedMethodCreateTitleButtonExists() {
    boolean found = Arrays.stream(clazz.getDeclaredMethods())
        .filter(m -> Modifier.isProtected(m.getModifiers()))
        .anyMatch(m -> m.getName().equals("createTitleButton"));
    assertTrue("Protected createTitleButton must exist", found);
  }

  @Test
  public void protectedMethodCreateTitlesPanelExists() {
    boolean found = Arrays.stream(clazz.getDeclaredMethods())
        .filter(m -> Modifier.isProtected(m.getModifiers()))
        .anyMatch(m -> m.getName().equals("createTitlesPanel"));
    assertTrue("Protected createTitlesPanel must exist", found);
  }

  @Test
  public void delegateClassesArePackagePrivate() {
    for (String name : Arrays.asList(
        "org.lgna.croquet.views.FolderTabTitleUI",
        "org.lgna.croquet.views.JFolderTabTitle",
        "org.lgna.croquet.views.FolderTitlesPanel",
        "org.lgna.croquet.views.TabScrollListener")) {
      try {
        Class<?> delegate = Class.forName(name);
        assertFalse(name + " must not be public",
            Modifier.isPublic(delegate.getModifiers()));
      } catch (ClassNotFoundException e) {
        fail("Delegate class " + name + " must exist on classpath");
      }
    }
  }
}

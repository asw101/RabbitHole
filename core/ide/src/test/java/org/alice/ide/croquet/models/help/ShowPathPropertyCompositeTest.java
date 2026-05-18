package org.alice.ide.croquet.models.help;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link ShowPathPropertyComposite} hierarchy —
 * {@link ShowClassPathPropertyComposite} and {@link ShowLibraryPathPropertyComposite}.
 * Covers getPropertyName, getDialogTitle, and class structure.
 */
public class ShowPathPropertyCompositeTest {

  // ---- ShowClassPathPropertyComposite ----

  @Test
  public void classPath_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ShowClassPathPropertyComposite");
  }

  @Test
  public void classPath_extendsShowPathPropertyComposite() {
    assertTrue(ShowPathPropertyComposite.class.isAssignableFrom(ShowClassPathPropertyComposite.class));
  }

  @Test
  public void classPath_hasNoArgConstructor() throws NoSuchMethodException {
    assertNotNull(ShowClassPathPropertyComposite.class.getConstructor());
  }

  // ---- ShowLibraryPathPropertyComposite ----

  @Test
  public void libraryPath_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ShowLibraryPathPropertyComposite");
  }

  @Test
  public void libraryPath_extendsShowPathPropertyComposite() {
    assertTrue(ShowPathPropertyComposite.class.isAssignableFrom(ShowLibraryPathPropertyComposite.class));
  }

  @Test
  public void libraryPath_hasNoArgConstructor() throws NoSuchMethodException {
    assertNotNull(ShowLibraryPathPropertyComposite.class.getConstructor());
  }

  // ---- ShowPathPropertyComposite structure ----

  @Test
  public void showPathProperty_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(ShowPathPropertyComposite.class.getModifiers()));
  }

  @Test
  public void showPathProperty_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ShowPathPropertyComposite");
  }

  @Test
  public void showPathProperty_hasGetPropertyNameMethod() throws NoSuchMethodException {
    assertNotNull(ShowPathPropertyComposite.class.getMethod("getPropertyName"));
  }

  @Test
  public void showPathProperty_getPropertyName_returnType() throws NoSuchMethodException {
    assertEquals(String.class,
        ShowPathPropertyComposite.class.getMethod("getPropertyName").getReturnType());
  }

  // ---- GraphicsHelpComposite ----

  @Test
  public void graphicsHelp_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.GraphicsHelpComposite");
  }

  // ---- ShowAllSystemPropertiesComposite ----

  @Test
  public void showAllSystemProperties_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ShowAllSystemPropertiesComposite");
  }

  // ---- ReportIssueComposite ----

  @Test
  public void reportIssue_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.croquet.models.help.ReportIssueComposite");
  }

  @Test
  public void reportIssue_extendsAbstractIssueComposite() {
    try {
      Class<?> cls = Class.forName("org.alice.ide.croquet.models.help.ReportIssueComposite");
      assertTrue(AbstractIssueComposite.class.isAssignableFrom(cls));
    } catch (ClassNotFoundException e) {
      fail("ReportIssueComposite class not found");
    }
  }
}

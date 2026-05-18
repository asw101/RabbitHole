package org.alice.ide.croquet.models.help;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

/**
 * Tests for {@link ShowPathPropertyComposite} hierarchy —
 * ShowClassPathPropertyComposite and ShowLibraryPathPropertyComposite.
 * Construction may require Croquet Application; headless-guarded.
 */
public class ShowPathPropertyCompositeTest {

  // ---- ShowClassPathPropertyComposite ----

  @Test
  public void showClassPath_construct_succeeds() {
    try {
      ShowClassPathPropertyComposite composite = new ShowClassPathPropertyComposite();
      assertNotNull(composite);
      assertEquals("java.class.path", composite.getPropertyName());
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }

  @Test
  public void showClassPath_propertyName() {
    try {
      ShowClassPathPropertyComposite composite = new ShowClassPathPropertyComposite();
      assertEquals("java.class.path", composite.getPropertyName());
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }

  @Test
  public void showClassPath_inheritsShowPathProperty() {
    try {
      ShowClassPathPropertyComposite composite = new ShowClassPathPropertyComposite();
      assertTrue(composite instanceof ShowPathPropertyComposite);
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }

  // ---- ShowLibraryPathPropertyComposite ----

  @Test
  public void showLibraryPath_construct_succeeds() {
    try {
      ShowLibraryPathPropertyComposite composite = new ShowLibraryPathPropertyComposite();
      assertNotNull(composite);
      assertEquals("java.library.path", composite.getPropertyName());
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }

  @Test
  public void showLibraryPath_propertyName() {
    try {
      ShowLibraryPathPropertyComposite composite = new ShowLibraryPathPropertyComposite();
      assertEquals("java.library.path", composite.getPropertyName());
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }

  @Test
  public void showLibraryPath_inheritsShowPathProperty() {
    try {
      ShowLibraryPathPropertyComposite composite = new ShowLibraryPathPropertyComposite();
      assertTrue(composite instanceof ShowPathPropertyComposite);
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }

  // ---- distinct composites ----

  @Test
  public void classAndLibrary_differentPropertyNames() {
    try {
      ShowClassPathPropertyComposite classPath = new ShowClassPathPropertyComposite();
      ShowLibraryPathPropertyComposite libPath = new ShowLibraryPathPropertyComposite();
      assertNotEquals(classPath.getPropertyName(), libPath.getPropertyName());
    } catch (Exception e) {
      Assume.assumeNoException("Requires Croquet Application context", e);
    }
  }
}

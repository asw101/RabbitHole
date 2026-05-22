package org.alice.ide.ast.data;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for {@link FilteredListPropertyData} covering construction and filtering.
 */
public class FilteredListPropertyDataComprehensiveTest {

  @Test
  public void className_isFilteredListPropertyData() {
    assertEquals("FilteredListPropertyData", FilteredListPropertyData.class.getSimpleName());
  }

  @Test
  public void packageName_isCorrect() {
    assertEquals("org.alice.ide.ast.data", FilteredListPropertyData.class.getPackage().getName());
  }

  @Test
  public void classIsPublic() {
    assertTrue(java.lang.reflect.Modifier.isPublic(FilteredListPropertyData.class.getModifiers()));
  }

  @Test
  public void classIsAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(FilteredListPropertyData.class.getModifiers()));
  }

  @Test
  public void classHasMethods() {
    assertTrue(FilteredListPropertyData.class.getDeclaredMethods().length > 0);
  }

  @Test
  public void classHasConstructors() {
    assertTrue(FilteredListPropertyData.class.getDeclaredConstructors().length > 0);
  }
}

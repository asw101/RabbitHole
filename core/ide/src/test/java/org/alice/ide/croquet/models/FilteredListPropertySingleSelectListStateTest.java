package org.alice.ide.croquet.models;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class FilteredListPropertySingleSelectListStateTest {
  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(FilteredListPropertySingleSelectListState.class.getModifiers()));
  }
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(FilteredListPropertySingleSelectListState.class.getModifiers()));
  }
  @Test
  public void extendsRefreshableDataSingleSelectListState() {
    assertTrue(org.lgna.croquet.RefreshableDataSingleSelectListState.class
        .isAssignableFrom(FilteredListPropertySingleSelectListState.class));
  }
}

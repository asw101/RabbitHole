package org.alice.ide.ast.export.type;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class TypeSummaryDataSourceTest {
  @Test
  public void filenameConstantIsTypeSummaryXml() {
    assertEquals("typeSummary.xml", TypeSummaryDataSource.FILENAME);
  }
  @Test
  public void isPublicConcrete() {
    int mods = TypeSummaryDataSource.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertFalse(Modifier.isAbstract(mods));
  }
}

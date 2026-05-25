package org.alice.ide.formatter;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserMethod;

import static org.junit.Assert.assertEquals;

public class AliceFormatterAdditionalTest {
  private final AliceFormatter formatter = AliceFormatter.getInstance();

  @Test
  public void getTextForType_nullUsesNullToken() {
    assertEquals(formatter.getTextForNull(), formatter.getTextForType(null));
  }

  @Test
  public void getTextForType_unknownJavaTypeFallsBackToTypeName() {
    assertEquals("StringBuilder", formatter.getTextForType(JavaType.getInstance(StringBuilder.class)));
  }

  @Test
  public void getTextForGetAndSet_useKeywordFallbacks() {
    assertEquals("get", formatter.getTextForGet());
    assertEquals("set", formatter.getTextForSet());
  }

  @Test
  public void getNameForDeclaration_preservesUnknownUserMethodName() {
    UserMethod method = new UserMethod();
    method.name.setValue("spinAroundFast");

    assertEquals("spinAroundFast", formatter.getNameForDeclaration(method));
  }
}

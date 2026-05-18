package org.alice.ide.formatter;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserCode;
import org.lgna.project.ast.UserMethod;

import static org.junit.Assert.*;

public class FormatterTest {

  private static final class TestFormatter extends Formatter {
    private TestFormatter() {
      super("Test");
    }

    @Override
    public String getHeaderTextForCode(UserCode code) {
      return "header";
    }

    @Override
    public String getTrailerTextForCode(UserCode code) {
      return "trailer";
    }

    @Override
    protected String localizeName(String key, String name) {
      return "loc:" + name;
    }

    @Override
    public boolean isTypeExpressionDesired() {
      return true;
    }

    @Override
    public String getTextForThis() {
      return "this-text";
    }

    @Override
    public String getTextForNull() {
      return "null-text";
    }

    @Override
    public String getFinalText() {
      return "final-text";
    }

    @Override
    protected String getClassesFormat() {
      return "%s classes";
    }

    @Override
    public String getNewFormat() {
      return "new %s(%s)";
    }
  }

  @Test
  public void toString_returnsRepresentation() {
    assertEquals("Test", new TestFormatter().toString());
  }

  @Test
  public void getTextForType_usesNullTextForNullType() {
    assertEquals("null-text", new TestFormatter().getTextForType(null));
  }

  @Test
  public void getNameForDeclaration_usesLocalizationHook() {
    UserMethod method = new UserMethod();
    method.name.setValue("walk");
    assertEquals("loc:walk", new TestFormatter().getNameForDeclaration(method));
  }

  @Test
  public void subclassProvidedHeaderAndTrailer_areReturned() {
    TestFormatter formatter = new TestFormatter();
    UserMethod method = new UserMethod();
    method.returnType.setValue(JavaType.VOID_TYPE);

    assertEquals("header", formatter.getHeaderTextForCode(method));
    assertEquals("trailer", formatter.getTrailerTextForCode(method));
  }

  @Test
  public void getNewFormat_preservesFormatterSpecificTemplate() {
    assertEquals("new %s(%s)", new TestFormatter().getNewFormat());
  }
}

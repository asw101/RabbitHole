package org.alice.ide.formatter;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link AliceFormatter} — Alice-style code formatting via singleton.
 * Tests only bundle-independent methods to avoid resource bundle classpath issues.
 */
public class AliceFormatterTest {

  private final AliceFormatter formatter = AliceFormatter.getInstance();

  // ---- singleton ----

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(AliceFormatter.getInstance(), AliceFormatter.getInstance());
  }

  @Test
  public void getInstance_isNotNull() {
    assertNotNull(AliceFormatter.getInstance());
  }

  // ---- isTypeExpressionDesired ----

  @Test
  public void isTypeExpressionDesired_returnsFalse() {
    assertFalse(formatter.isTypeExpressionDesired());
  }

  // ---- getTrailerTextForCode ----

  @Test
  public void getTrailerTextForCode_returnsNull() {
    UserMethod method = new UserMethod();
    method.name.setValue("test");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    assertNull(formatter.getTrailerTextForCode(method));
  }

  // ---- toString ----

  @Test
  public void toString_returnsAlice() {
    assertEquals("Alice", formatter.toString());
  }

  // ---- getHeaderTextForCode ----

  @Test
  public void getHeaderTextForCode_procedure_returnsNonEmptyTemplate() {
    UserMethod method = new UserMethod();
    method.name.setValue("myProcedure");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    String header = formatter.getHeaderTextForCode(method);
    assertNotNull(header);
    assertFalse(header.isEmpty());
  }

  // ---- getTextForNull/This ----

  @Test
  public void getTextForNull_returnsNonNull() {
    String text = formatter.getTextForNull();
    assertNotNull(text);
  }

  @Test
  public void getTextForThis_returnsNonNull() {
    String text = formatter.getTextForThis();
    assertNotNull(text);
  }

  // ---- getFinalText ----

  @Test
  public void getFinalText_returnsNonNull() {
    String text = formatter.getFinalText();
    assertNotNull(text);
  }

  // ---- getNewFormat ----

  @Test
  public void getNewFormat_containsPlaceholders() {
    String fmt = formatter.getNewFormat();
    assertNotNull(fmt);
    assertTrue(fmt.contains("%s"));
  }
}

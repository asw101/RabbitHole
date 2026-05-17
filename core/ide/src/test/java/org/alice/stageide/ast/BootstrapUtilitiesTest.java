package org.alice.stageide.ast;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link BootstrapUtilities} — public constant and utility access.
 */
public class BootstrapUtilitiesTest {

  @Test
  public void myFirstProcedureName_isCorrect() {
    assertEquals("myFirstMethod", BootstrapUtilities.MY_FIRST_PROCEDURE_NAME);
  }

  @Test
  public void myFirstProcedureName_isNotEmpty() {
    assertFalse(BootstrapUtilities.MY_FIRST_PROCEDURE_NAME.isEmpty());
  }

  @Test
  public void myFirstProcedureName_isValidIdentifier() {
    String name = BootstrapUtilities.MY_FIRST_PROCEDURE_NAME;
    assertTrue(Character.isJavaIdentifierStart(name.charAt(0)));
    for (int i = 1; i < name.length(); i++) {
      assertTrue(Character.isJavaIdentifierPart(name.charAt(i)));
    }
  }
}

package org.alice.ide.formatter;

import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.UserMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AliceFormatterBehaviorTest {
  private final AliceFormatter formatter = AliceFormatter.getInstance();

  @Test
  public void getHeaderTextForCodeDistinguishesProceduresFunctionsAndConstructors() {
    UserMethod procedure = AstUtilities.createProcedure("walk");
    UserMethod function = AstUtilities.createFunction("measure", String.class);

    assertEquals("declare procedure </getName()/> </getParameters()/>", formatter.getHeaderTextForCode(procedure));
    assertEquals("declare </getReturnType()/> function </getName()/> </getParameters()/>", formatter.getHeaderTextForCode(function));
    assertEquals("declare constructor </getParameters()/>", formatter.getHeaderTextForCode(new NamedUserConstructor()));
  }

  @Test
  public void aliceFormatterDoesNotUseClosingTrailersAndKeepsDistinctTextTokens() {
    assertNull(formatter.getTrailerTextForCode(AstUtilities.createProcedure("walk")));
    assertNotEquals(formatter.getTextForThis(), formatter.getTextForNull());
    assertFalse(formatter.getFinalText().isEmpty());
  }

  @Test
  public void getNewFormatInterpolatesTheRequestedTypeAndArguments() {
    String formatted = formatter.getNewFormat().formatted("Car", "driver");

    assertTrue(formatted.contains("Car"));
    assertTrue(formatted.contains("driver"));
    assertTrue(formatted.contains("("));
  }
}

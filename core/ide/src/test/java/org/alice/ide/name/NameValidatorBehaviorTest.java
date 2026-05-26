package org.alice.ide.name;

import org.junit.Test;

import static org.junit.Assert.*;

public class NameValidatorBehaviorTest {
  private static final class StubNameValidator extends NameValidator {
    private final boolean valid;
    private final boolean available;

    private StubNameValidator(boolean valid, boolean available) {
      this.valid = valid;
      this.available = available;
    }

    @Override
    public boolean isNameValid(String name) {
      return this.valid;
    }

    @Override
    public boolean isNameAvailable(String name) {
      return this.available;
    }
  }

  @Test
  public void invalidNameExplanationIncludesQuotedName() {
    String explanation = new StubNameValidator(false, true)
        .getExplanationIfOkButtonShouldBeDisabled("bad name");

    assertNotNull(explanation);
    assertTrue(explanation.contains("\"bad name\""));
    assertTrue(explanation.contains("is not a valid name"));
  }

  @Test
  public void unavailableNameExplanationIncludesQuotedName() {
    String explanation = new StubNameValidator(true, false)
        .getExplanationIfOkButtonShouldBeDisabled("occupiedName");

    assertNotNull(explanation);
    assertTrue(explanation.contains("\"occupiedName\""));
    assertTrue(explanation.contains("is not available"));
  }

  @Test
  public void nullNameExplanationUsesLiteralNullText() {
    String explanation = new StubNameValidator(false, true)
        .getExplanationIfOkButtonShouldBeDisabled(null);

    assertNotNull(explanation);
    assertTrue(explanation.contains("\"null\""));
  }

  @Test
  public void replacementCharactersRemainLiteralInExplanation() {
    String trickyName = "player$1\\hero";

    String explanation = new StubNameValidator(false, true)
        .getExplanationIfOkButtonShouldBeDisabled(trickyName);

    assertNotNull(explanation);
    assertTrue(explanation.contains("\"player$1\\hero\""));
  }

  @Test
  public void validAndAvailableNameEnablesOkButton() {
    assertNull(new StubNameValidator(true, true)
        .getExplanationIfOkButtonShouldBeDisabled("readyName"));
  }
}

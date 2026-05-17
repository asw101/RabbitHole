package org.alice.ide.name.validators;

import org.junit.Test;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

/**
 * Tests for {@link NodeNameValidator} — Java identifier validation
 * via {@link org.lgna.project.ast.StaticAnalysisUtilities#isValidIdentifier}.
 */
public class NodeNameValidatorTest {

  private NodeNameValidator createValidator() {
    NamedUserType node = new NamedUserType();
    node.name.setValue("TestType");
    node.superType.setValue(JavaType.getInstance(Object.class));
    return new TestNodeNameValidator(node);
  }

  // Concrete subclass since NodeNameValidator is abstract
  private static class TestNodeNameValidator extends NodeNameValidator {
    TestNodeNameValidator(org.lgna.project.ast.Node node) {
      super(node);
    }

    @Override
    public boolean isNameAvailable(String name) {
      return true; // always available for testing
    }
  }

  // ---- isNameValid ----

  @Test
  public void isNameValid_simpleIdentifier_true() {
    assertTrue(createValidator().isNameValid("myVariable"));
  }

  @Test
  public void isNameValid_singleChar_true() {
    assertTrue(createValidator().isNameValid("x"));
  }

  @Test
  public void isNameValid_underscoreStart_true() {
    assertTrue(createValidator().isNameValid("_private"));
  }

  @Test
  public void isNameValid_camelCase_true() {
    assertTrue(createValidator().isNameValid("myLongVariableName"));
  }

  @Test
  public void isNameValid_emptyString_false() {
    assertFalse(createValidator().isNameValid(""));
  }

  @Test
  public void isNameValid_null_false() {
    assertFalse(createValidator().isNameValid(null));
  }

  @Test
  public void isNameValid_startsWithDigit_false() {
    assertFalse(createValidator().isNameValid("1abc"));
  }

  @Test
  public void isNameValid_containsSpace_false() {
    assertFalse(createValidator().isNameValid("my variable"));
  }

  @Test
  public void isNameValid_containsDash_false() {
    assertFalse(createValidator().isNameValid("my-variable"));
  }

  @Test
  public void isNameValid_containsDot_false() {
    assertFalse(createValidator().isNameValid("my.variable"));
  }

  // ---- getNode ----

  @Test
  public void getNode_returnsConstructorNode() {
    NamedUserType node = new NamedUserType();
    node.name.setValue("X");
    node.superType.setValue(JavaType.getInstance(Object.class));
    NodeNameValidator validator = new TestNodeNameValidator(node);
    assertSame(node, validator.getNode());
  }
}

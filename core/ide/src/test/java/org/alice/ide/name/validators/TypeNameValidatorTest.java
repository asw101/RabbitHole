package org.alice.ide.name.validators;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeNameValidatorTest {
  private static boolean invokeBoolean(TypeNameValidator validator, String methodName, String value)
      throws Exception {
    Method method = TypeNameValidator.class.getDeclaredMethod(methodName, String.class);
    method.setAccessible(true);
    return (Boolean) method.invoke(validator, value);
  }

  @Test
  public void isSystemClassName_acceptsStoryThingTypes() throws Exception {
    assertTrue(invokeBoolean(new TypeNameValidator(), "isSystemClassName", "SThing"));
  }

  @Test
  public void isSystemClassName_rejectsUnknownStoryTypes() throws Exception {
    assertFalse(invokeBoolean(new TypeNameValidator(), "isSystemClassName", "DefinitelyMissingStoryType"));
  }

  @Test
  public void isAliceClassName_reusesStoryTypeLookupWithSPrefix() throws Exception {
    TypeNameValidator validator = new TypeNameValidator();

    assertTrue(invokeBoolean(validator, "isAliceClassName", "Thing"));
    assertFalse(invokeBoolean(validator, "isAliceClassName", "DefinitelyMissing"));
  }
}

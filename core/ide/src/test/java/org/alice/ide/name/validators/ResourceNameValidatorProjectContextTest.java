package org.alice.ide.name.validators;

import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResourceNameValidatorProjectContextTest extends ProjectContextTestCase {
  @Test
  public void duplicateResourceNameIsUnavailableInLoadedProject() {
    ResourceNameValidator validator = new ResourceNameValidator();
    validator.setResource(fixture.audioResource);

    assertFalse(validator.isNameAvailable(fixture.imageResource.getName()));
    assertTrue(validator.isNameAvailable("fresh-resource.wav"));
  }
}

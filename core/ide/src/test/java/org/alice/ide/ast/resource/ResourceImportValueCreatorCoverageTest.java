package org.alice.ide.ast.resource;

import org.junit.Test;
import org.lgna.common.resources.AudioResource;
import org.lgna.croquet.importer.Importer;
import org.lgna.project.ast.ResourceExpression;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.*;

public class ResourceImportValueCreatorCoverageTest {
  private static final class StubImporter extends Importer<AudioResource> {
    private StubImporter() {
      super(new File("."), (dir, name) -> false, Collections.singleton("wav"));
    }

    @Override
    protected AudioResource createFromFile(File file) throws IOException {
      return new AudioResource(UUID.randomUUID());
    }
  }

  private static final class TestResourceImportValueCreator extends ResourceImportValueCreator<AudioResource> {
    private TestResourceImportValueCreator() {
      super(UUID.fromString("22222222-2222-2222-2222-222222222222"), new StubImporter(), AudioResource.class);
    }

    private ResourceExpression create(AudioResource resource) {
      return this.createValueFromImportedValue(resource);
    }
  }

  @Test
  public void createValueFromImportedValue_wrapsResourceInExpression() {
    AudioResource resource = new AudioResource(UUID.randomUUID());
    ResourceExpression expression = new TestResourceImportValueCreator().create(resource);
    assertSame(resource, expression.resource.getValue());
  }

  @Test
  public void createValueFromImportedValue_preservesResourceClass() {
    AudioResource resource = new AudioResource(UUID.randomUUID());
    ResourceExpression expression = new TestResourceImportValueCreator().create(resource);
    assertEquals(org.lgna.project.ast.JavaType.getInstance(AudioResource.class), expression.type.getValue());
  }

  @Test
  public void creatorIsAnImportValueCreatorSubclass() {
    assertTrue(TestResourceImportValueCreator.class.getSuperclass() == ResourceImportValueCreator.class);
  }
}

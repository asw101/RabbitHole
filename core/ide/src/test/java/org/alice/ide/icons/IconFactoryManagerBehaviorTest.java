package org.alice.ide.icons;

import org.junit.Test;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.JavaType;
import org.lgna.story.resources.BipedResource;

import java.awt.Color;

import static org.junit.Assert.*;

public class IconFactoryManagerBehaviorTest {
  private static final class DummyRegisteredType {
  }

  @Test
  public void registeredFactoryIsReturnedForTypeAndField() {
    JavaType dummyType = JavaType.getInstance(DummyRegisteredType.class);
    IconFactory factory = new ColorIconFactory(Color.ORANGE);
    IconFactoryManager.registerIconFactory(DummyRegisteredType.class, factory);

    assertSame(factory, IconFactoryManager.getRegisteredIconFactory(dummyType));
    assertSame(factory, IconFactoryManager.getIconFactoryForType(dummyType));
    assertSame(factory, IconFactoryManager.getIconFactoryForField(IconTestSupport.createField("label", dummyType)));
  }

  @Test
  public void nonVisualDynamicFieldFallsBackToProvidedFactory() {
    IconFactory fallback = CheckIconFactory.getInstance();
    assertSame(fallback, IconFactoryManager.getDynamicIconFactoryForField(IconTestSupport.createField("label", JavaType.STRING_TYPE), fallback));
  }

  @Test
  public void classesWithIconsContainsBipedResource() {
    assertTrue(IconFactoryManager.getSetOfClassesWithIcons().contains(BipedResource.class));
  }
}

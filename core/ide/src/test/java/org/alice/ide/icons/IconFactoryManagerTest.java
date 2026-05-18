package org.alice.ide.icons;

import org.junit.Test;
import org.lgna.croquet.icon.EmptyIconFactory;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.FishResource;
import org.lgna.story.resources.FlyerResource;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resources.QuadrupedResource;

import java.util.Set;

import static org.junit.Assert.*;

public class IconFactoryManagerTest {
  @Test
  public void getSetOfClassesWithIcons_notNull() {
    Set<Class<? extends JointedModelResource>> set = IconFactoryManager.getSetOfClassesWithIcons();
    assertNotNull(set);
  }

  @Test
  public void getSetOfClassesWithIcons_containsBipedResource() {
    assertTrue(IconFactoryManager.getSetOfClassesWithIcons().contains(BipedResource.class));
  }

  @Test
  public void getSetOfClassesWithIcons_containsFishResource() {
    assertTrue(IconFactoryManager.getSetOfClassesWithIcons().contains(FishResource.class));
  }

  @Test
  public void getSetOfClassesWithIcons_containsFlyerResource() {
    assertTrue(IconFactoryManager.getSetOfClassesWithIcons().contains(FlyerResource.class));
  }

  @Test
  public void getSetOfClassesWithIcons_containsQuadrupedResource() {
    assertTrue(IconFactoryManager.getSetOfClassesWithIcons().contains(QuadrupedResource.class));
  }

  @Test
  public void getRegisteredIconFactory_unknownType_returnsNull() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    IconFactory factory = IconFactoryManager.getRegisteredIconFactory(type);
    assertNull(factory);
  }

  @Test
  public void getRegisteredIconFactory_nullType_returnsNull() {
    IconFactory factory = IconFactoryManager.getRegisteredIconFactory(null);
    assertNull(factory);
  }

  @Test
  public void getIconFactoryForType_nullType_returnsEmptyFactory() {
    IconFactory factory = IconFactoryManager.getIconFactoryForType(null);
    assertSame(EmptyIconFactory.getInstance(), factory);
  }

  @Test
  public void getIconFactoryForField_nullField_returnsEmptyFactory() {
    IconFactory factory = IconFactoryManager.getIconFactoryForField(null);
    assertSame(EmptyIconFactory.getInstance(), factory);
  }

  @Test
  public void registerIconFactory_thenRetrieve() {
    JavaType testType = JavaType.getInstance(StringBuilder.class);
    IconFactory testFactory = EmptyIconFactory.getInstance();
    IconFactoryManager.registerIconFactory(testType, testFactory);
    IconFactory retrieved = IconFactoryManager.getRegisteredIconFactory(testType);
    assertSame(testFactory, retrieved);
  }

  @Test
  public void registerIconFactory_byClass() {
    IconFactory testFactory = EmptyIconFactory.getInstance();
    IconFactoryManager.registerIconFactory(java.util.ArrayList.class, testFactory);
    IconFactory retrieved = IconFactoryManager.getRegisteredIconFactory(JavaType.getInstance(java.util.ArrayList.class));
    assertSame(testFactory, retrieved);
  }
}

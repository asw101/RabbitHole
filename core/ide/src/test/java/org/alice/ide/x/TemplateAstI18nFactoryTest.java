package org.alice.ide.x;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class TemplateAstI18nFactoryTest {

  @Test
  public void getInstance_returnsSingleton() {
    assertSame(TemplateAstI18nFactory.getInstance(), TemplateAstI18nFactory.getInstance());
  }

  @Test
  public void templateFactory_extendsIdeAstI18nFactory() {
    assertEquals(IdeAstI18nFactory.class, TemplateAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void templateFactory_hasSinglePrivateConstructor() {
    Constructor<?>[] constructors = TemplateAstI18nFactory.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
  }

  @Test
  public void getInstance_returnsTemplateFactoryType() {
    assertEquals(TemplateAstI18nFactory.class, TemplateAstI18nFactory.getInstance().getClass());
  }
}

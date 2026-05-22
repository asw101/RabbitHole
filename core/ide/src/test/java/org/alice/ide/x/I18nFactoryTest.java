package org.alice.ide.x;

import org.junit.Test;

import static org.junit.Assert.*;

public class I18nFactoryTest {

  @Test
  public void classIsAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(I18nFactory.class.getModifiers()));
  }

  @Test
  public void classHasPublicCreateComponentMethod() throws NoSuchMethodException {
    // Verify createComponent(Page, InstancePropertyOwner) is public
    assertNotNull(I18nFactory.class.getMethod("createComponent",
        org.alice.ide.i18n.Page.class,
        edu.cmu.cs.dennisc.property.InstancePropertyOwner.class));
  }

  @Test
  public void classHasCreateComponentForOwner() throws NoSuchMethodException {
    assertNotNull(I18nFactory.class.getMethod("createComponent",
        edu.cmu.cs.dennisc.property.InstancePropertyOwner.class));
  }
}

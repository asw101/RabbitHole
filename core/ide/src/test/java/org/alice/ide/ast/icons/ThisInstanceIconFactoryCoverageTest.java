package org.alice.ide.ast.icons;

import org.junit.Test;
import org.lgna.croquet.icon.ResolutionIndependentIconFactory;

import javax.swing.Icon;
import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ThisInstanceIconFactoryCoverageTest {
  @Test
  public void getInstance_returnsSingleton() {
    assertSame(ThisInstanceIconFactory.getInstance(), ThisInstanceIconFactory.getInstance());
  }

  @Test
  public void extendsResolutionIndependentIconFactory() {
    assertTrue(ResolutionIndependentIconFactory.class.isAssignableFrom(ThisInstanceIconFactory.class));
  }

  @Test
  public void constructorIsPrivate() throws Exception {
    Constructor<?> constructor = ThisInstanceIconFactory.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void createIcon_returnsSizedIcon() throws Exception {
    Method method = ThisInstanceIconFactory.class.getDeclaredMethod("createIcon", Dimension.class);
    method.setAccessible(true);
    Icon icon = (Icon) method.invoke(ThisInstanceIconFactory.getInstance(), new Dimension(24, 16));
    assertEquals(24, icon.getIconWidth());
    assertEquals(16, icon.getIconHeight());
  }
}

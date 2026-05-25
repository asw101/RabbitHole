package org.alice.ide.croquet.models.help.views;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class HelpViewsPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.help.views", AbstractIssueView.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.help.views", ShowPathPropertyView.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(AbstractIssueView.class.getModifiers()));
    assertFalse(Modifier.isInterface(ShowPathPropertyView.class.getModifiers()));
  }
}

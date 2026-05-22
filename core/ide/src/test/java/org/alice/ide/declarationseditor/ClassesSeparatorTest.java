package org.alice.ide.declarationseditor;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ClassesSeparatorTest {

  @Test
  public void getInstance_returnsSingleton() {
    assertSame(ClassesSeparator.getInstance(), ClassesSeparator.getInstance());
  }

  @Test
  public void classesSeparator_extendsLabelMenuSeparatorModel() {
    assertEquals(org.lgna.croquet.LabelMenuSeparatorModel.class, ClassesSeparator.class.getSuperclass());
  }

  @Test
  public void classesSeparator_isNotFinal() {
    assertFalse(Modifier.isFinal(ClassesSeparator.class.getModifiers()));
  }

  @Test
  public void classesSeparator_getNameReturnsNull() {
    assertNull(ClassesSeparator.getInstance().getName());
  }
}

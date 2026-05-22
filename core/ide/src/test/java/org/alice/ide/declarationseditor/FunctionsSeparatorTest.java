package org.alice.ide.declarationseditor;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class FunctionsSeparatorTest {

  @Test
  public void getInstance_returnsSingleton() {
    assertSame(FunctionsSeparator.getInstance(), FunctionsSeparator.getInstance());
  }

  @Test
  public void functionsSeparator_extendsLabelMenuSeparatorModel() {
    assertEquals(org.lgna.croquet.LabelMenuSeparatorModel.class, FunctionsSeparator.class.getSuperclass());
  }

  @Test
  public void functionsSeparator_isNotFinal() {
    assertFalse(Modifier.isFinal(FunctionsSeparator.class.getModifiers()));
  }

  @Test
  public void functionsSeparator_hasAccessibleSingletonFactory() throws Exception {
    assertNotNull(FunctionsSeparator.class.getMethod("getInstance"));
  }
}

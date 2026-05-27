package org.lgna.story.resourceutilities;

import org.junit.Test;

import static org.junit.Assert.assertSame;

public class BaseModelClassDataTest {
  @Test
  public void constructorStoresAbstractionAndImplementationClasses() {
    BaseModelClassData data = new BaseModelClassData(Number.class, Integer.class);

    assertSame(Number.class, data.abstractionClass);
    assertSame(Integer.class, data.implementationClass);
  }

  @Test
  public void copyConstructorPreservesStoredClasses() {
    BaseModelClassData original = new BaseModelClassData(CharSequence.class, String.class);
    BaseModelClassData copy = new BaseModelClassData(original);

    assertSame(CharSequence.class, copy.abstractionClass);
    assertSame(String.class, copy.implementationClass);
  }
}

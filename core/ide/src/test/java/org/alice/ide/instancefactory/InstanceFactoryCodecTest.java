package org.alice.ide.instancefactory;

import org.alice.ide.instancefactory.croquet.codecs.InstanceFactoryCodec;
import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceFactoryCodecTest {
  @Test
  public void singleton_isNotNull() {
    assertNotNull(InstanceFactoryCodec.SINGLETON);
  }

  @Test
  public void getValueClass_returnsInstanceFactory() {
    assertEquals(InstanceFactory.class, InstanceFactoryCodec.SINGLETON.getValueClass());
  }

  @Test
  public void appendRepresentation_doesNotThrow() {
    StringBuilder sb = new StringBuilder();
    InstanceFactoryCodec.SINGLETON.appendRepresentation(sb, null);
  }
}

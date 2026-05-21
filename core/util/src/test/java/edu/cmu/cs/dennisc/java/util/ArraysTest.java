package edu.cmu.cs.dennisc.java.util;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ArraysTest {
  @Test
  public void constructorThrowsAssertionError() throws Exception {
    Constructor<Arrays> constructor = Arrays.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      Assert.fail("Expected constructor to throw");
    } catch (InvocationTargetException ite) {
      Assert.assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void indexOfUsesIdentity() {
    String first = new String("same");
    String second = new String("same");
    Object[] values = {first, second};

    Assert.assertEquals(0, Arrays.indexOf(values, first));
    Assert.assertEquals(1, Arrays.indexOf(values, second));
    Assert.assertEquals(-1, Arrays.indexOf(values, new String("same")));
  }

  @Test
  public void containsDelegatesToIndexOf() {
    Object marker = new Object();
    Object[] values = {"a", marker, "b"};

    Assert.assertTrue(Arrays.contains(values, marker));
    Assert.assertFalse(Arrays.contains(values, new Object()));
  }
}

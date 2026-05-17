package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class EnumUtilitiesTest {

  @Test
  public void getFld_returnsCorrectField() {
    Field field = EnumUtilities.getFld(Thread.State.NEW);
    assertNotNull(field);
    assertEquals("NEW", field.getName());
  }

  @Test
  public void getFld_null_returnsNull() {
    assertNull(EnumUtilities.getFld(null));
  }

  @Test
  public void getFld_anotherEnumValue() {
    Field field = EnumUtilities.getFld(Thread.State.RUNNABLE);
    assertNotNull(field);
    assertEquals("RUNNABLE", field.getName());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_nullCriterion_returnsAll() {
    Class<? extends Thread.State>[] classes = new Class[] {Thread.State.class};
    List<Thread.State> result = EnumUtilities.getEnumConstants(classes, null);
    assertEquals(Thread.State.values().length, result.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getEnumConstants_withCriterion_filters() {
    Class<? extends Thread.State>[] classes = new Class[] {Thread.State.class};
    List<Thread.State> result = EnumUtilities.getEnumConstants(classes, e -> e == Thread.State.NEW);
    assertEquals(1, result.size());
    assertEquals(Thread.State.NEW, result.get(0));
  }
}

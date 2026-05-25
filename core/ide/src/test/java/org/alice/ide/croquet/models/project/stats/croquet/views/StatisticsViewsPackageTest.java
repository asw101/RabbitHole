package org.alice.ide.croquet.models.project.stats.croquet.views;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class StatisticsViewsPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.project.stats.croquet.views", StatisticsFrameView.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.project.stats.croquet.views", StatisticsMethodFrequencyView.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(StatisticsFrameView.class.getModifiers()));
    assertFalse(Modifier.isInterface(StatisticsMethodFrequencyView.class.getModifiers()));
  }
}

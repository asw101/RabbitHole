package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class BackspaceOperationTest {
  private static void assumeNotHeadless() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void sameModelReusesInstance() {
    assumeNotHeadless();
    assertSame(BackspaceOperation.getInstance(IntegerModel.getInstance()),
        BackspaceOperation.getInstance(IntegerModel.getInstance()));
  }

  @Test
  public void differentModelsUseDifferentInstances() {
    assumeNotHeadless();
    assertNotSame(BackspaceOperation.getInstance(IntegerModel.getInstance()),
        BackspaceOperation.getInstance(DoubleModel.getInstance()));
  }

  @Test
  public void localizedNameUsesBackArrow() {
    assumeNotHeadless();
    assertEquals("←", BackspaceOperation.getInstance(IntegerModel.getInstance()).getImp().getName());
  }
}

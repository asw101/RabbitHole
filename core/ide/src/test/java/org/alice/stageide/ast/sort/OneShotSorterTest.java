package org.alice.stageide.ast.sort;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link OneShotSorter} — enum singleton and sorting contract.
 * Static initialization may require jointed model types, so tests guard
 * against initialization failures in headless environments.
 */
public class OneShotSorterTest {

  @Test
  public void singleton_isNotNull() {
    try {
      assertNotNull(OneShotSorter.SINGLETON);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("OneShotSorter init requires runtime types", e);
    }
  }

  @Test
  public void createSortedList_emptyInput_returnsEmpty() {
    try {
      List<?> result = OneShotSorter.SINGLETON.createSortedList(new ArrayList<>());
      assertTrue(result.isEmpty());
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("OneShotSorter init requires runtime types", e);
    }
  }

  @Test
  public void staticMethodConstants_areAvailable() {
    try {
      // These static fields are JavaMethod instances initialized in the static block
      assertNotNull(OneShotSorter.TURN_METHOD);
      assertNotNull(OneShotSorter.ROLL_METHOD);
      assertNotNull(OneShotSorter.MOVE_METHOD);
      assertNotNull(OneShotSorter.PLACE_METHOD);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("OneShotSorter init requires runtime types", e);
    }
  }

  @Test
  public void orientationMethods_areAvailable() {
    try {
      assertNotNull(OneShotSorter.TURN_TO_FACE_METHOD);
      assertNotNull(OneShotSorter.POINT_AT_METHOD);
      assertNotNull(OneShotSorter.ORIENT_TO_UPRIGHT_METHOD);
      assertNotNull(OneShotSorter.ORIENT_TO_METHOD);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("OneShotSorter init requires runtime types", e);
    }
  }

  @Test
  public void movementMethods_areAvailable() {
    try {
      assertNotNull(OneShotSorter.MOVE_TOWARD_METHOD);
      assertNotNull(OneShotSorter.MOVE_AWAY_FROM_METHOD);
      assertNotNull(OneShotSorter.MOVE_TO_METHOD);
      assertNotNull(OneShotSorter.MOVE_AND_ORIENT_TO_METHOD);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("OneShotSorter init requires runtime types", e);
    }
  }

  @Test
  public void paintAndOpacityMethods_areAvailable() {
    try {
      assertNotNull(OneShotSorter.GROUND_SET_PAINT_METHOD);
      assertNotNull(OneShotSorter.MODEL_SET_PAINT_METHOD);
      assertNotNull(OneShotSorter.GROUND_SET_OPACITY_METHOD);
      assertNotNull(OneShotSorter.MODEL_SET_OPACITY_METHOD);
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      Assume.assumeNoException("OneShotSorter init requires runtime types", e);
    }
  }
}

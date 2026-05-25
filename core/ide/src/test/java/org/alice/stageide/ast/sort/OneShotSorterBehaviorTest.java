package org.alice.stageide.ast.sort;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.AbstractMember;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OneShotSorterBehaviorTest {
  private static void assumeSorterAvailable() {
    try {
      assertNotNull(OneShotSorter.SINGLETON);
    } catch (ExceptionInInitializerError | NoClassDefFoundError error) {
      Assume.assumeNoException(error);
    }
  }

  @Test
  public void createSortedList_ordersKnownMethodsByConfiguredPriorityBuckets() {
    assumeSorterAvailable();

    List<AbstractMember> sorted = OneShotSorter.SINGLETON.createSortedList(Arrays.asList(
        OneShotSorter.ORIENT_TO_METHOD,
        OneShotSorter.STRAIGHTEN_OUT_JOINTS_METHOD,
        OneShotSorter.TURN_METHOD,
        OneShotSorter.MOVE_METHOD
    ));

    assertEquals(Arrays.asList(
        OneShotSorter.MOVE_METHOD,
        OneShotSorter.TURN_METHOD,
        OneShotSorter.ORIENT_TO_METHOD,
        OneShotSorter.STRAIGHTEN_OUT_JOINTS_METHOD
    ), sorted);
  }

  @Test
  public void createSortedList_preservesRelativeOrderForMembersWithEqualSortValues() {
    assumeSorterAvailable();

    List<AbstractMember> sorted = OneShotSorter.SINGLETON.createSortedList(Arrays.asList(
        OneShotSorter.MODEL_SET_PAINT_METHOD,
        OneShotSorter.GROUND_SET_PAINT_METHOD
    ));

    assertEquals(Arrays.asList(
        OneShotSorter.MODEL_SET_PAINT_METHOD,
        OneShotSorter.GROUND_SET_PAINT_METHOD
    ), sorted);
  }

  @Test
  public void createSortedList_doesNotMutateSourceList() {
    assumeSorterAvailable();

    List<AbstractMember> source = new ArrayList<>(Arrays.asList(
        OneShotSorter.ORIENT_TO_METHOD,
        OneShotSorter.MOVE_METHOD,
        OneShotSorter.TURN_METHOD
    ));
    List<AbstractMember> originalOrder = new ArrayList<>(source);

    OneShotSorter.SINGLETON.createSortedList(source);

    assertEquals(originalOrder, source);
  }
}

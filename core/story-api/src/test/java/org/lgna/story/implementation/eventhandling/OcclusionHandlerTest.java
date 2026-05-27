package org.lgna.story.implementation.eventhandling;

import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SModel;
import org.lgna.story.event.OcclusionEndListener;
import org.lgna.story.event.OcclusionEvent;
import org.lgna.story.event.OcclusionStartListener;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class OcclusionHandlerTest {
  @Test
  public void addOcclusionListenerRegistersSymmetricTrackingWithoutCameraSetup() {
    EventHandlerTestSupport.TestModel background = new EventHandlerTestSupport.TestModel("background", 8.0);
    EventHandlerTestSupport.TestModel foreground = new EventHandlerTestSupport.TestModel("foreground", 4.0);
    EventHandlerTestSupport.SyncOcclusionHandler handler = new EventHandlerTestSupport.SyncOcclusionHandler();
    OcclusionStartListener listener = event -> {
    };

    handler.addOcclusionEventListener(listener, List.of(background), List.of(foreground), MultipleEventPolicy.COMBINE);

    assertEquals(List.of(background, foreground), handler.getModelList());
    assertTrue(handler.interactionListeners.get(background).get(foreground).contains(listener));
    assertTrue(handler.interactionListeners.get(foreground).get(background).contains(listener));
  }

  @Test
  public void occlusionLifecycleFiresStartAndEndWithForegroundOrdering() {
    EventHandlerTestSupport.TestModel background = new EventHandlerTestSupport.TestModel("background", 8.0);
    EventHandlerTestSupport.TestModel foreground = new EventHandlerTestSupport.TestModel("foreground", 4.0);
    EventHandlerTestSupport.SyncOcclusionHandler handler = new EventHandlerTestSupport.SyncOcclusionHandler();
    List<OcclusionEvent> starts = new ArrayList<>();
    List<OcclusionEvent> ends = new ArrayList<>();

    handler.addOcclusionEventListener((OcclusionStartListener) starts::add, List.of(background), List.of(foreground), MultipleEventPolicy.IGNORE);
    handler.addOcclusionEventListener((OcclusionEndListener) ends::add, List.of(background), List.of(foreground), MultipleEventPolicy.IGNORE);

    handler.setOccluded(background, foreground, false);
    handler.trigger(background);
    assertTrue(starts.isEmpty());

    handler.setOccluded(background, foreground, true);
    handler.trigger(background);
    assertEquals(1, starts.size());
    assertSame(foreground, starts.get(0).getForegroundModel());
    assertSame(background, starts.get(0).getBackgroundModel());

    handler.setOccluded(background, foreground, false);
    handler.trigger(background);
    assertEquals(1, ends.size());
    assertSame(foreground, ends.get(0).getForegroundModel());
    assertSame(background, ends.get(0).getBackgroundModel());
  }

  @Test
  public void occlusionHandlerMaintainsPairStatePerModel() {
    EventHandlerTestSupport.TestModel background = new EventHandlerTestSupport.TestModel("background", 9.0);
    EventHandlerTestSupport.TestModel foreground = new EventHandlerTestSupport.TestModel("foreground", 3.0);
    EventHandlerTestSupport.SyncOcclusionHandler handler = new EventHandlerTestSupport.SyncOcclusionHandler();

    handler.addOcclusionEventListener((OcclusionStartListener) event -> {
    }, List.of(background), List.of(foreground), MultipleEventPolicy.IGNORE);
    handler.setOccluded(background, foreground, true);
    handler.trigger(background);
    handler.trigger(background);

    java.util.Map<SModel, java.util.Map<SModel, Boolean>> wereOccluded = handler.wereOccluded();
    assertTrue(wereOccluded.get(background).get(foreground));
    assertTrue(wereOccluded.get(foreground).get(background));
  }
}

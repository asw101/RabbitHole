package org.lgna.story.implementation.eventhandling;

import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SModel;
import org.lgna.story.STextModel;
import org.lgna.story.event.EnterViewEvent;
import org.lgna.story.event.ExitViewEvent;
import org.lgna.story.event.SceneActivationEvent;
import org.lgna.story.event.SceneActivationListener;
import org.lgna.story.event.ViewEnterListener;
import org.lgna.story.event.ViewEvent;
import org.lgna.story.event.ViewExitListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SceneActivationAndViewEventHandlerTest {
  @Test
  public void sceneActivationHandlerPreservesRegistrationOrder() {
    SyncSceneActivationHandler handler = new SyncSceneActivationHandler();
    List<String> calls = new ArrayList<>();

    handler.addListener(event -> calls.add("first"));
    handler.addListener(event -> calls.add("second"));
    handler.addListener(event -> calls.add("third"));

    handler.handleEventFire(new SceneActivationEvent());

    assertEquals(List.of("first", "second", "third"), calls);
  }

  @Test
  public void sceneActivationHandlerDuplicateRegistrationFiresTwiceAndSingleRemoveLeavesOne() {
    SyncSceneActivationHandler handler = new SyncSceneActivationHandler();
    List<String> calls = new ArrayList<>();
    SceneActivationListener listener = event -> calls.add("dup");

    handler.addListener(listener);
    handler.addListener(listener);
    handler.handleEventFire(new SceneActivationEvent());
    assertEquals(List.of("dup", "dup"), calls);

    calls.clear();
    handler.removeListener(listener);
    handler.handleEventFire(new SceneActivationEvent());
    assertEquals(List.of("dup"), calls);
  }

  @Test
  public void viewEventHandlerStoresListenersByModelAndDispatchesMatchingEventTypes() {
    ExposedViewEventHandler handler = new ExposedViewEventHandler();
    STextModel model = new STextModel();
    List<String> calls = new ArrayList<>();

    ViewEnterListener enterListener = event -> {
      calls.add("enter");
      assertSame(model, event.getModel());
    };
    ViewExitListener exitListener = event -> {
      calls.add("exit");
      assertSame(model, event.getModel());
    };

    handler.registerListener(enterListener, model, MultipleEventPolicy.IGNORE);
    handler.registerListener(exitListener, model, MultipleEventPolicy.IGNORE);

    assertTrue(handler.listenersFor(model).contains(enterListener));
    assertTrue(handler.listenersFor(model).contains(exitListener));

    handler.dispatch(enterListener, new EnterViewEvent(model));
    handler.dispatch(exitListener, new ExitViewEvent(model));

    assertEquals(List.of("enter", "exit"), calls);
  }

  @Test
  public void viewEventHandlerRetainsDuplicateRegistrationsInInsertionOrder() {
    ExposedViewEventHandler handler = new ExposedViewEventHandler();
    STextModel model = new STextModel();
    ViewEnterListener listener = event -> {
    };

    handler.registerListener(listener, model, MultipleEventPolicy.IGNORE);
    handler.registerListener(listener, model, MultipleEventPolicy.IGNORE);

    List<Object> listeners = handler.listenersFor(model);
    assertEquals(2, listeners.size());
    assertSame(listener, listeners.get(0));
    assertSame(listener, listeners.get(1));
  }

  private static final class SyncSceneActivationHandler extends SceneActivationHandler {
    @Override
    protected void fireEvent(SceneActivationListener listener, SceneActivationEvent event) {
      fire(listener, event);
    }

    @Override
    protected void fireEvent(SceneActivationListener listener, SceneActivationEvent event, Object multiEventLock) {
      fire(listener, event);
    }
  }

  private static final class ExposedViewEventHandler extends ViewEventHandler {
    void registerListener(Object listener, SModel model, MultipleEventPolicy policy) {
      registerIsFiringMap(listener);
      registerPolicyMap(listener, policy);
      listenersByModel().computeIfAbsent(model, ignored -> new CopyOnWriteArrayList<>()).add(listener);
    }

    List<Object> listenersFor(SModel model) {
      return listenersByModel().getOrDefault(model, List.of());
    }

    void dispatch(Object listener, ViewEvent event) {
      fireEvent(listener, event);
    }

    @SuppressWarnings("unchecked")
    private Map<SModel, List<Object>> listenersByModel() {
      try {
        Field field = ViewEventHandler.class.getDeclaredField("map");
        field.setAccessible(true);
        return (Map<SModel, List<Object>>) field.get(this);
      } catch (ReflectiveOperationException e) {
        throw new AssertionError(e);
      }
    }

    @Override
    protected void fireEvent(Object listener, ViewEvent event) {
      fire(listener, event);
    }

    @Override
    protected void fireEvent(Object listener, ViewEvent event, Object multiEventLock) {
      fire(listener, event);
    }
  }
}

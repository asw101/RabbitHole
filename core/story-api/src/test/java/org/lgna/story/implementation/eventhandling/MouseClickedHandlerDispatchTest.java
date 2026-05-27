package org.lgna.story.implementation.eventhandling;

import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SModel;
import org.lgna.story.STextModel;
import org.lgna.story.Visual;
import org.lgna.story.event.MouseClickEvent;
import org.lgna.story.event.MouseClickEventImp;
import org.lgna.story.event.MouseClickOnObjectListener;
import org.lgna.story.event.MouseClickOnScreenListener;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

public class MouseClickedHandlerDispatchTest {
  @Test
  public void fireAllTargetedDispatchesScreenThenAllVisualThenTargetedObjectListeners() {
    SyncMouseClickedHandler handler = new SyncMouseClickedHandler();
    STextModel target = new STextModel();
    List<String> calls = new ArrayList<>();

    handler.addListener((MouseClickOnScreenListener) event -> calls.add("screen"), MultipleEventPolicy.IGNORE, null);
    handler.addListener((MouseClickOnObjectListener) event -> {
      calls.add("all-visuals");
      assertSame(target, event.getModelAtMouseLocation());
    }, MultipleEventPolicy.IGNORE, new Visual[0]);
    handler.addListener((MouseClickOnObjectListener) event -> {
      calls.add("targeted");
      assertSame(target, event.getModelAtMouseLocation());
    }, MultipleEventPolicy.IGNORE, new Visual[] {target});

    handler.fireAllTargeted(clickEvent(target));

    assertEquals(List.of("screen", "all-visuals", "targeted"), calls);
  }

  @Test
  public void nullPickOnlyDispatchesScreenListeners() {
    SyncMouseClickedHandler handler = new SyncMouseClickedHandler();
    List<String> calls = new ArrayList<>();

    handler.addListener((MouseClickOnScreenListener) event -> calls.add("screen"), MultipleEventPolicy.IGNORE, null);
    handler.addListener((MouseClickOnObjectListener) event -> calls.add("object"), MultipleEventPolicy.IGNORE, new Visual[0]);

    handler.fireAllTargeted(clickEvent(null));

    assertEquals(List.of("screen"), calls);
    assertFalse(calls.contains("object"));
  }

  @Test
  public void duplicateScreenListenerRegistrationsFireTwice() {
    SyncMouseClickedHandler handler = new SyncMouseClickedHandler();
    List<String> calls = new ArrayList<>();
    MouseClickOnScreenListener listener = event -> calls.add("screen");

    handler.addListener(listener, MultipleEventPolicy.IGNORE, null);
    handler.addListener(listener, MultipleEventPolicy.IGNORE, null);

    handler.fireAllTargeted(clickEvent(null));

    assertEquals(List.of("screen", "screen"), calls);
  }

  private static MouseClickEventImp clickEvent(SModel modelAtMouseLocation) {
    MouseClickEventImp event = new MouseClickEventImp(
        new MouseEvent(new JPanel(), MouseEvent.MOUSE_RELEASED, 100, 0, 10, 10, 1, false, MouseEvent.BUTTON1),
        null);
    if (modelAtMouseLocation != null) {
      setField(event, "modelAtMouseLocation", modelAtMouseLocation);
      setField(event, "isPickPerformed", true);
    }
    return event;
  }

  private static void setField(Object instance, String name, Object value) {
    try {
      Field field = instance.getClass().getDeclaredField(name);
      field.setAccessible(true);
      field.set(instance, value);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static final class SyncMouseClickedHandler extends MouseClickedHandler {
    @Override
    protected void fireEvent(Object listener, MouseClickEvent event) {
      fire(listener, event);
    }

    @Override
    protected void fireEvent(Object listener, MouseClickEvent event, Object multiEventLock) {
      fire(listener, event);
    }
  }
}

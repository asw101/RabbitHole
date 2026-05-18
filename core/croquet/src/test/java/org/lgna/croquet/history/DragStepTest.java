package org.lgna.croquet.history;

import org.junit.Test;

import javax.swing.*;
import java.awt.event.MouseEvent;

import static org.junit.Assert.*;

public class DragStepTest {

  @Test
  public void constructorStoresModel() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestDragModel model = new HistoryTestSupport.TestDragModel();
    HistoryTestSupport.TestDragComponent component = new HistoryTestSupport.TestDragComponent(model);
    MouseEvent event = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_DRAGGED,
        System.currentTimeMillis(), 0, 2, 3, 1, false);

    DragStep step = new DragStep(activity, model,
        org.lgna.croquet.triggers.DragTrigger.createUserInstance(component, event));

    assertSame(model, step.getModel());
  }

  @Test
  public void getUserActivityReturnsOwner() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestDragModel model = new HistoryTestSupport.TestDragModel();
    HistoryTestSupport.TestDragComponent component = new HistoryTestSupport.TestDragComponent(model);
    MouseEvent event = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_DRAGGED,
        System.currentTimeMillis(), 0, 2, 3, 1, false);

    DragStep step = new DragStep(activity, model,
        org.lgna.croquet.triggers.DragTrigger.createUserInstance(component, event));

    assertSame(activity, step.getUserActivity());
  }

  @Test
  public void getDragSourceReturnsTriggerView() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestDragModel model = new HistoryTestSupport.TestDragModel();
    HistoryTestSupport.TestDragComponent component = new HistoryTestSupport.TestDragComponent(model);
    MouseEvent event = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_DRAGGED,
        System.currentTimeMillis(), 0, 2, 3, 1, false);
    DragStep step = new DragStep(activity, model,
        org.lgna.croquet.triggers.DragTrigger.createUserInstance(component, event));

    assertSame(component, step.getDragSource());
  }

  @Test
  public void latestMouseEventRoundTrips() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestDragModel model = new HistoryTestSupport.TestDragModel();
    HistoryTestSupport.TestDragComponent component = new HistoryTestSupport.TestDragComponent(model);
    MouseEvent initial = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_DRAGGED,
        System.currentTimeMillis(), 0, 2, 3, 1, false);
    DragStep step = new DragStep(activity, model,
        org.lgna.croquet.triggers.DragTrigger.createUserInstance(component, initial));
    MouseEvent updated = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_DRAGGED,
        System.currentTimeMillis(), 0, 4, 5, 1, false);

    step.setLatestMouseEvent(updated);

    assertSame(updated, step.getLatestMouseEvent());
  }

  @Test
  public void fireDragStartedNotifiesPotentialReceptors() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestDragModel model = new HistoryTestSupport.TestDragModel();
    HistoryTestSupport.TestDragComponent component = new HistoryTestSupport.TestDragComponent(model);
    HistoryTestSupport.TestDropReceptor receptor = new HistoryTestSupport.TestDropReceptor();
    JPanel container = new JPanel(null);
    container.add(component.getAwtComponent());
    component.getAwtComponent().setBounds(0, 0, 100, 100);
    container.add(receptor.view.getAwtComponent());
    receptor.view.getAwtComponent().setBounds(10, 10, 20, 20);
    model.receptors.add(receptor);
    MouseEvent event = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_DRAGGED,
        System.currentTimeMillis(), 0, 15, 15, 1, false);
    DragStep step = new DragStep(activity, model,
        org.lgna.croquet.triggers.DragTrigger.createUserInstance(component, event));

    step.fireDragStarted();

    assertEquals(1, receptor.dragStartedCount);
  }

  @Test
  public void handleMouseDraggedUpdatesCurrentPotentialDropSite() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestDragModel model = new HistoryTestSupport.TestDragModel();
    HistoryTestSupport.TestDragComponent component = new HistoryTestSupport.TestDragComponent(model);
    HistoryTestSupport.TestDropReceptor receptor = new HistoryTestSupport.TestDropReceptor();
    JPanel container = new JPanel(null);
    container.add(component.getAwtComponent());
    component.getAwtComponent().setBounds(0, 0, 100, 100);
    container.add(receptor.view.getAwtComponent());
    receptor.view.getAwtComponent().setBounds(10, 10, 20, 20);
    model.receptors.add(receptor);
    MouseEvent event = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_DRAGGED,
        System.currentTimeMillis(), 0, 15, 15, 1, false);
    DragStep step = new DragStep(activity, model,
        org.lgna.croquet.triggers.DragTrigger.createUserInstance(component, event));

    step.handleMouseDragged(event);

    assertSame(receptor.dropSite, step.getCurrentPotentialDropSite());
    assertEquals(1, receptor.dragEnteredCount);
    assertEquals(1, model.enteredCount);
  }

  @Test
  public void handleMouseReleasedWithoutDropReceptorCancelsOwner() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestDragModel model = new HistoryTestSupport.TestDragModel();
    HistoryTestSupport.TestDragComponent component = new HistoryTestSupport.TestDragComponent(model);
    MouseEvent event = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_RELEASED,
        System.currentTimeMillis(), 0, 5, 5, 1, false);
    DragStep step = new DragStep(activity, model,
        org.lgna.croquet.triggers.DragTrigger.createUserInstance(component, event));

    step.handleMouseReleased(event);

    assertTrue(activity.isCanceled());
    assertEquals(1, model.stoppedCount);
  }

  @Test
  public void handleCancelCancelsOwnerAndStopsReceptors() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestDragModel model = new HistoryTestSupport.TestDragModel();
    HistoryTestSupport.TestDragComponent component = new HistoryTestSupport.TestDragComponent(model);
    HistoryTestSupport.TestDropReceptor receptor = new HistoryTestSupport.TestDropReceptor();
    JPanel container = new JPanel(null);
    container.add(component.getAwtComponent());
    component.getAwtComponent().setBounds(0, 0, 100, 100);
    container.add(receptor.view.getAwtComponent());
    receptor.view.getAwtComponent().setBounds(10, 10, 20, 20);
    model.receptors.add(receptor);
    MouseEvent event = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_DRAGGED,
        System.currentTimeMillis(), 0, 15, 15, 1, false);
    DragStep step = new DragStep(activity, model,
        org.lgna.croquet.triggers.DragTrigger.createUserInstance(component, event));

    step.handleCancel(event);

    assertTrue(activity.isCanceled());
    assertEquals(1, receptor.dragStoppedCount);
    assertEquals(1, model.stoppedCount);
  }

  @Test
  public void handleMouseReleasedReturnsEarlyForCanceledOwner() {
    UserActivity activity = new UserActivity();
    activity.cancel();
    HistoryTestSupport.TestDragModel model = new HistoryTestSupport.TestDragModel();
    HistoryTestSupport.TestDragComponent component = new HistoryTestSupport.TestDragComponent(model);
    MouseEvent event = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_RELEASED,
        System.currentTimeMillis(), 0, 5, 5, 1, false);
    DragStep step = new DragStep(activity, model,
        org.lgna.croquet.triggers.DragTrigger.createUserInstance(component, event));

    step.handleMouseReleased(event);

    assertNull(step.getLatestMouseEvent());
    assertEquals(0, model.stoppedCount);
  }
}

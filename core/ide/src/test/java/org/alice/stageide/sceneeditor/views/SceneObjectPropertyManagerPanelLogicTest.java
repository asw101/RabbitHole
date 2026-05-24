package org.alice.stageide.sceneeditor.views;

import org.junit.Test;

import static org.junit.Assert.*;

public class SceneObjectPropertyManagerPanelLogicTest {
  @Test
  public void chooseAdapterSelectsSceneColorLabels() {
    SceneObjectPropertyManagerPanelLogic.AdapterChoice choice = SceneObjectPropertyManagerPanelLogic.chooseAdapter(
        "setFromBelowLightColor", SceneObjectPropertyManagerPanelLogic.EntityKind.SCENE, false, false);

    assertEquals(SceneObjectPropertyManagerPanelLogic.AdapterKind.COLOR, choice.kind);
    assertEquals("Below Light Color", choice.label);
  }

  @Test
  public void chooseAdapterSelectsDoubleAdaptersForShapeMeasurements() {
    assertEquals(SceneObjectPropertyManagerPanelLogic.AdapterKind.DOUBLE,
        SceneObjectPropertyManagerPanelLogic.chooseAdapter("setRadius", SceneObjectPropertyManagerPanelLogic.EntityKind.SPHERE, false, false).kind);
    assertEquals("OuterRadius",
        SceneObjectPropertyManagerPanelLogic.chooseAdapter("setOuterRadius", SceneObjectPropertyManagerPanelLogic.EntityKind.TORUS, false, false).label);
    assertEquals("Length",
        SceneObjectPropertyManagerPanelLogic.chooseAdapter("setLength", SceneObjectPropertyManagerPanelLogic.EntityKind.CYLINDER, false, false).label);
  }

  @Test
  public void chooseAdapterFallsBackToNonfreeWhenProvided() {
    SceneObjectPropertyManagerPanelLogic.AdapterChoice choice = SceneObjectPropertyManagerPanelLogic.chooseAdapter(
        "setSparkles", SceneObjectPropertyManagerPanelLogic.EntityKind.UNKNOWN, true, false);

    assertEquals(SceneObjectPropertyManagerPanelLogic.AdapterKind.NONFREE, choice.kind);
  }

  @Test
  public void chooseAdapterUsesMutableRiderVehicleAdapterOnlyForRiders() {
    assertEquals(SceneObjectPropertyManagerPanelLogic.AdapterKind.MUTABLE_RIDER_VEHICLE,
        SceneObjectPropertyManagerPanelLogic.chooseAdapter("setVehicle", SceneObjectPropertyManagerPanelLogic.EntityKind.MODEL, false, true).kind);
    assertEquals(SceneObjectPropertyManagerPanelLogic.AdapterKind.NONE,
        SceneObjectPropertyManagerPanelLogic.chooseAdapter("setVehicle", SceneObjectPropertyManagerPanelLogic.EntityKind.MODEL, false, false).kind);
  }
}

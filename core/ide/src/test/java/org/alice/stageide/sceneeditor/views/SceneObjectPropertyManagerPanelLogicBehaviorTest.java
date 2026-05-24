package org.alice.stageide.sceneeditor.views;

import org.junit.Test;
import org.lgna.story.SBillboard;
import org.lgna.story.SCone;
import org.lgna.story.SCylinder;
import org.lgna.story.SDisc;
import org.lgna.story.SGround;
import org.lgna.story.SScene;
import org.lgna.story.SSphere;
import org.lgna.story.STextModel;
import org.lgna.story.STorus;
import org.lgna.story.SVRUser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SceneObjectPropertyManagerPanelLogicBehaviorTest {
  private static final class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }

  private static void assertConstructorThrowsAssertionError(Class<?> type) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected constructor to reject instantiation");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  private static void assertChoice(String setterName, SceneObjectPropertyManagerPanelLogic.EntityKind entityKind,
                                   boolean hasNonfreeAdapter, boolean isMutableRider,
                                   SceneObjectPropertyManagerPanelLogic.AdapterKind expectedKind, String expectedLabel) {
    SceneObjectPropertyManagerPanelLogic.AdapterChoice choice =
        SceneObjectPropertyManagerPanelLogic.chooseAdapter(setterName, entityKind, hasNonfreeAdapter, isMutableRider);
    assertEquals(expectedKind, choice.kind);
    assertEquals(expectedLabel, choice.label);
  }

  @Test
  public void utilityConstructorRejectsInstantiation() throws Exception {
    assertConstructorThrowsAssertionError(SceneObjectPropertyManagerPanelLogic.class);
  }

  @Test
  public void classifyEntityImpRecognizesStoryImplementationsAndUnknowns() {
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.GROUND,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(new SGround().getImplementation()));
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.SCENE,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(new TestScene().getImplementation()));
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.BILLBOARD,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(new SBillboard().getImplementation()));
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.TEXT_MODEL,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(new STextModel().getImplementation()));
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.CYLINDER,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(new SCylinder().getImplementation()));
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.SPHERE,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(new SSphere().getImplementation()));
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.DISC,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(new SDisc().getImplementation()));
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.CONE,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(new SCone().getImplementation()));
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.TORUS,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(new STorus().getImplementation()));
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.VR_USER,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(new SVRUser().getImplementation()));
    assertEquals(SceneObjectPropertyManagerPanelLogic.EntityKind.UNKNOWN,
        SceneObjectPropertyManagerPanelLogic.classifyEntityImp(null));
  }

  @Test
  public void chooseAdapterCoversRemainingSetterCases() {
    assertChoice("setOpacity", SceneObjectPropertyManagerPanelLogic.EntityKind.GROUND, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.GROUND_OPACITY, null);
    assertChoice("setOpacity", SceneObjectPropertyManagerPanelLogic.EntityKind.BILLBOARD, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.MODEL_OPACITY, null);
    assertChoice("setFogDensity", SceneObjectPropertyManagerPanelLogic.EntityKind.SCENE, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.SCENE_FOG_DENSITY, null);
    assertChoice("setFogDensity", SceneObjectPropertyManagerPanelLogic.EntityKind.MODEL, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.NONE, null);
    assertChoice("setResource", SceneObjectPropertyManagerPanelLogic.EntityKind.JOINTED_MODEL, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.RESOURCE, null);
    assertChoice("setPaint", SceneObjectPropertyManagerPanelLogic.EntityKind.GROUND, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.GROUND_PAINT, "Paint");
    assertChoice("setPaint", SceneObjectPropertyManagerPanelLogic.EntityKind.BILLBOARD, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.BILLBOARD_FRONT_PAINT, null);
    assertChoice("setPaint", SceneObjectPropertyManagerPanelLogic.EntityKind.MODEL, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.MODEL_PAINT, "Paint");
    assertChoice("setFromAboveLightColor", SceneObjectPropertyManagerPanelLogic.EntityKind.SCENE, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.COLOR, "Above Light Color");
    assertChoice("setAtmosphereColor", SceneObjectPropertyManagerPanelLogic.EntityKind.SCENE, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.COLOR, "Atmosphere Color");
    assertChoice("setAmbientLightColor", SceneObjectPropertyManagerPanelLogic.EntityKind.SCENE, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.COLOR, "Light Color");
    assertChoice("setBackPaint", SceneObjectPropertyManagerPanelLogic.EntityKind.BILLBOARD, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.BILLBOARD_BACK_PAINT, null);
    assertChoice("setFrontPaint", SceneObjectPropertyManagerPanelLogic.EntityKind.BILLBOARD, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.BILLBOARD_FRONT_PAINT, null);
    assertChoice("setFont", SceneObjectPropertyManagerPanelLogic.EntityKind.TEXT_MODEL, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.TEXT_FONT, null);
    assertChoice("setValue", SceneObjectPropertyManagerPanelLogic.EntityKind.TEXT_MODEL, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.TEXT_VALUE, null);
    assertChoice("setBaseRadius", SceneObjectPropertyManagerPanelLogic.EntityKind.CONE, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.DOUBLE, "Radius");
    assertChoice("setInnerRadius", SceneObjectPropertyManagerPanelLogic.EntityKind.TORUS, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.DOUBLE, "InnerRadius");
    assertChoice("setScale", SceneObjectPropertyManagerPanelLogic.EntityKind.VR_USER, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.DOUBLE, "Scale");
    assertChoice("setMystery", SceneObjectPropertyManagerPanelLogic.EntityKind.UNKNOWN, false, false,
        SceneObjectPropertyManagerPanelLogic.AdapterKind.NONE, null);
  }
}

package org.alice.stageide.sceneeditor.views;

import org.lgna.story.implementation.*;

final class SceneObjectPropertyManagerPanelLogic {
  enum EntityKind {
    MODEL,
    GROUND,
    SCENE,
    JOINTED_MODEL,
    BILLBOARD,
    TEXT_MODEL,
    CYLINDER,
    SPHERE,
    DISC,
    CONE,
    TORUS,
    VR_USER,
    UNKNOWN
  }

  enum AdapterKind {
    NONE,
    MODEL_OPACITY,
    GROUND_OPACITY,
    SCENE_FOG_DENSITY,
    RESOURCE,
    GROUND_PAINT,
    BILLBOARD_FRONT_PAINT,
    MODEL_PAINT,
    MUTABLE_RIDER_VEHICLE,
    COLOR,
    BILLBOARD_BACK_PAINT,
    NONFREE,
    TEXT_FONT,
    TEXT_VALUE,
    DOUBLE
  }

  static final class AdapterChoice {
    final AdapterKind kind;
    final String label;

    private AdapterChoice(AdapterKind kind, String label) {
      this.kind = kind;
      this.label = label;
    }
  }

  private SceneObjectPropertyManagerPanelLogic() {
    throw new AssertionError();
  }

  static EntityKind classifyEntityImp(EntityImp entityImp) {
    if (entityImp instanceof JointedModelImp<?, ?>) {
      return EntityKind.JOINTED_MODEL;
    }
    if (entityImp instanceof GroundImp) {
      return EntityKind.GROUND;
    }
    if (entityImp instanceof SceneImp) {
      return EntityKind.SCENE;
    }
    if (entityImp instanceof BillboardImp) {
      return EntityKind.BILLBOARD;
    }
    if (entityImp instanceof TextModelImp) {
      return EntityKind.TEXT_MODEL;
    }
    if (entityImp instanceof CylinderImp) {
      return EntityKind.CYLINDER;
    }
    if (entityImp instanceof SphereImp) {
      return EntityKind.SPHERE;
    }
    if (entityImp instanceof DiscImp) {
      return EntityKind.DISC;
    }
    if (entityImp instanceof ConeImp) {
      return EntityKind.CONE;
    }
    if (entityImp instanceof TorusImp) {
      return EntityKind.TORUS;
    }
    if (entityImp instanceof VrUserImp) {
      return EntityKind.VR_USER;
    }
    if (entityImp instanceof ModelImp) {
      return EntityKind.MODEL;
    }
    return EntityKind.UNKNOWN;
  }

  static AdapterChoice chooseAdapter(String setterName, EntityKind entityKind, boolean hasNonfreeAdapter, boolean isMutableRider) {
    return switch (setterName) {
      case "setOpacity" -> switch (entityKind) {
        case MODEL, JOINTED_MODEL, BILLBOARD -> new AdapterChoice(AdapterKind.MODEL_OPACITY, null);
        case GROUND -> new AdapterChoice(AdapterKind.GROUND_OPACITY, null);
        default -> new AdapterChoice(AdapterKind.NONE, null);
      };
      case "setFogDensity" -> entityKind == EntityKind.SCENE ? new AdapterChoice(AdapterKind.SCENE_FOG_DENSITY, null) : new AdapterChoice(AdapterKind.NONE, null);
      case "setResource" -> entityKind == EntityKind.JOINTED_MODEL ? new AdapterChoice(AdapterKind.RESOURCE, null) : new AdapterChoice(AdapterKind.NONE, null);
      case "setPaint" -> switch (entityKind) {
        case GROUND -> new AdapterChoice(AdapterKind.GROUND_PAINT, "Paint");
        case BILLBOARD -> new AdapterChoice(AdapterKind.BILLBOARD_FRONT_PAINT, null);
        case MODEL, JOINTED_MODEL -> new AdapterChoice(AdapterKind.MODEL_PAINT, "Paint");
        default -> new AdapterChoice(AdapterKind.NONE, null);
      };
      case "setVehicle" -> isMutableRider ? new AdapterChoice(AdapterKind.MUTABLE_RIDER_VEHICLE, null) : new AdapterChoice(AdapterKind.NONE, null);
      case "setFromAboveLightColor" -> entityKind == EntityKind.SCENE ? new AdapterChoice(AdapterKind.COLOR, "Above Light Color") : new AdapterChoice(AdapterKind.NONE, null);
      case "setFromBelowLightColor" -> entityKind == EntityKind.SCENE ? new AdapterChoice(AdapterKind.COLOR, "Below Light Color") : new AdapterChoice(AdapterKind.NONE, null);
      case "setAtmosphereColor" -> entityKind == EntityKind.SCENE ? new AdapterChoice(AdapterKind.COLOR, "Atmosphere Color") : new AdapterChoice(AdapterKind.NONE, null);
      case "setAmbientLightColor" -> entityKind == EntityKind.SCENE ? new AdapterChoice(AdapterKind.COLOR, "Light Color") : new AdapterChoice(AdapterKind.NONE, null);
      case "setBackPaint" -> entityKind == EntityKind.BILLBOARD ? new AdapterChoice(AdapterKind.BILLBOARD_BACK_PAINT, null) : new AdapterChoice(AdapterKind.NONE, null);
      case "setFrontPaint" -> entityKind == EntityKind.BILLBOARD ? new AdapterChoice(AdapterKind.BILLBOARD_FRONT_PAINT, null) : new AdapterChoice(AdapterKind.NONE, null);
      case "setFont" -> entityKind == EntityKind.TEXT_MODEL ? new AdapterChoice(AdapterKind.TEXT_FONT, null) : new AdapterChoice(AdapterKind.NONE, null);
      case "setValue" -> entityKind == EntityKind.TEXT_MODEL ? new AdapterChoice(AdapterKind.TEXT_VALUE, null) : new AdapterChoice(AdapterKind.NONE, null);
      case "setRadius" -> switch (entityKind) {
        case CYLINDER, SPHERE, DISC -> new AdapterChoice(AdapterKind.DOUBLE, "Radius");
        default -> new AdapterChoice(AdapterKind.NONE, null);
      };
      case "setBaseRadius" -> entityKind == EntityKind.CONE ? new AdapterChoice(AdapterKind.DOUBLE, "Radius") : new AdapterChoice(AdapterKind.NONE, null);
      case "setInnerRadius" -> entityKind == EntityKind.TORUS ? new AdapterChoice(AdapterKind.DOUBLE, "InnerRadius") : new AdapterChoice(AdapterKind.NONE, null);
      case "setOuterRadius" -> entityKind == EntityKind.TORUS ? new AdapterChoice(AdapterKind.DOUBLE, "OuterRadius") : new AdapterChoice(AdapterKind.NONE, null);
      case "setLength" -> switch (entityKind) {
        case CYLINDER, CONE -> new AdapterChoice(AdapterKind.DOUBLE, "Length");
        default -> new AdapterChoice(AdapterKind.NONE, null);
      };
      case "setScale" -> entityKind == EntityKind.VR_USER ? new AdapterChoice(AdapterKind.DOUBLE, "Scale") : new AdapterChoice(AdapterKind.NONE, null);
      default -> hasNonfreeAdapter ? new AdapterChoice(AdapterKind.NONFREE, null) : new AdapterChoice(AdapterKind.NONE, null);
    };
  }
}

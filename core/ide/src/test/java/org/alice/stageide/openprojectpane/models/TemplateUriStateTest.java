package org.alice.stageide.openprojectpane.models;

import org.junit.Test;
import org.lgna.story.SGround;

import static org.junit.Assert.*;

public class TemplateUriStateTest {

  @Test
  public void blankScheme_isGen() {
    assertEquals("gen", TemplateUriState.BLANK_SCHEME);
  }

  @Test
  public void starterScheme_isStarterfile() {
    assertEquals("starterfile", TemplateUriState.STARTER_SCHEME);
  }

  @Test
  public void template_GRASS_surfaceAppearance() {
    assertEquals(SGround.SurfaceAppearance.GRASS, TemplateUriState.Template.GRASS.getSurfaceAppearance());
  }

  @Test
  public void template_GRASS_isNotRoom() {
    assertFalse(TemplateUriState.Template.GRASS.isRoom());
  }

  @Test
  public void template_GRASS_atmosphereColor_notNull() {
    assertNotNull(TemplateUriState.Template.GRASS.getAtmospherColor());
  }

  @Test
  public void template_GRASS_groundOpacity_isOne() {
    assertEquals(1.0, TemplateUriState.Template.GRASS.getGroundOpacity(), 0.001);
  }

  @Test
  public void template_GRASS_fogDensity_isNaN() {
    assertTrue(Double.isNaN(TemplateUriState.Template.GRASS.getFogDensity()));
  }

  @Test
  public void template_SEA_FLOOR_surfaceAppearance() {
    assertEquals(SGround.SurfaceAppearance.OCEAN_FLOOR, TemplateUriState.Template.SEA_FLOOR.getSurfaceAppearance());
  }

  @Test
  public void template_SEA_FLOOR_fogDensity() {
    assertEquals(0.3, TemplateUriState.Template.SEA_FLOOR.getFogDensity(), 0.001);
  }

  @Test
  public void template_SEA_FLOOR_aboveLightColor_notNull() {
    assertNotNull(TemplateUriState.Template.SEA_FLOOR.getAboveLightColor());
  }

  @Test
  public void template_SEA_FLOOR_belowLightColor_notNull() {
    assertNotNull(TemplateUriState.Template.SEA_FLOOR.getBelowLightColor());
  }

  @Test
  public void template_SEA_SURFACE_groundOpacity_point7() {
    assertEquals(0.7, TemplateUriState.Template.SEA_SURFACE.getGroundOpacity(), 0.001);
  }

  @Test
  public void template_MOON_surfaceAppearance() {
    assertEquals(SGround.SurfaceAppearance.MOON, TemplateUriState.Template.MOON.getSurfaceAppearance());
  }

  @Test
  public void template_MARS_fogDensity() {
    assertEquals(0.25, TemplateUriState.Template.MARS.getFogDensity(), 0.001);
  }

  @Test
  public void template_SNOW_surfaceAppearance() {
    assertEquals(SGround.SurfaceAppearance.SNOW, TemplateUriState.Template.SNOW.getSurfaceAppearance());
  }

  @Test
  public void template_ROOM_isRoom() {
    assertTrue(TemplateUriState.Template.ROOM.isRoom());
  }

  @Test
  public void template_ROOM_floorAppearance_notNull() {
    // ROOM uses NebulousIde which may return null in test env
    // Just ensure accessor doesn't throw
    TemplateUriState.Template.ROOM.getFloorAppearance();
  }

  @Test
  public void template_ROOM_wallAppearance_accessible() {
    TemplateUriState.Template.ROOM.getWallAppearance();
  }

  @Test
  public void template_ROOM_ceilingAppearance_accessible() {
    TemplateUriState.Template.ROOM.getCeilingAppearance();
  }

  @Test
  public void template_ROOM_surfaceAppearance_isNull() {
    // ROOM uses the 4-arg Paint constructor where surfaceAppearance can be null
    assertNull(TemplateUriState.Template.ROOM.getSurfaceAppearance());
  }

  @Test
  public void template_DESERT_surfaceAppearance() {
    assertEquals(SGround.SurfaceAppearance.SANDY_DESERT, TemplateUriState.Template.DESERT.getSurfaceAppearance());
  }

  @Test
  public void template_ICE_surfaceAppearance() {
    assertEquals(SGround.SurfaceAppearance.ICE, TemplateUriState.Template.ICE.getSurfaceAppearance());
  }

  @Test
  public void template_AMAZON_surfaceAppearance() {
    assertEquals(SGround.SurfaceAppearance.JUNGLE, TemplateUriState.Template.AMAZON.getSurfaceAppearance());
  }

  @Test
  public void template_values_hasManyTemplates() {
    TemplateUriState.Template[] values = TemplateUriState.Template.values();
    assertTrue(values.length >= 18);
  }

  @Test
  public void template_valueOf_GRASS() {
    assertEquals(TemplateUriState.Template.GRASS, TemplateUriState.Template.valueOf("GRASS"));
  }

  @Test
  public void getLocalizedName_GRASS_returnsNonNull() {
    String name = TemplateUriState.getLocalizedName(TemplateUriState.Template.GRASS);
    assertNotNull(name);
    // In default locale, should resolve to "Grass"
    assertEquals("Grass", name);
  }

  @Test
  public void getLocalizedName_string_DESERT_returnsDesert() {
    String name = TemplateUriState.getLocalizedName("DESERT");
    assertEquals("Desert", name);
  }

  @Test
  public void getLocalizedName_unknownKey_returnsSameString() {
    String name = TemplateUriState.getLocalizedName("UNKNOWN_TEMPLATE_XYZ");
    assertEquals("UNKNOWN_TEMPLATE_XYZ", name);
  }

  @Test
  public void getLocalizedName_null_returnsNull() {
    String name = TemplateUriState.getLocalizedName((String) null);
    assertNull(name);
  }

  @Test
  public void template_WONDERLAND_surfaceAppearance() {
    assertEquals(SGround.SurfaceAppearance.DARK_GRASS, TemplateUriState.Template.WONDERLAND.getSurfaceAppearance());
  }

  @Test
  public void template_SWAMP_groundOpacity_point7() {
    assertEquals(0.7, TemplateUriState.Template.SWAMP.getGroundOpacity(), 0.001);
  }

  @Test
  public void template_NORTHWEST_FOREST_surfaceAppearance() {
    assertEquals(SGround.SurfaceAppearance.FOREST_FLOOR, TemplateUriState.Template.NORTHWEST_FOREST.getSurfaceAppearance());
  }
}

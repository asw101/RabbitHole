package org.alice.stageide;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class LaunchConfigurationEdgeCaseTest {

  @Test
  public void defaultWidthConstantValue() {
    assertEquals(1000, LaunchConfiguration.DEFAULT_WIDTH);
  }

  @Test
  public void defaultHeightConstantValue() {
    assertEquals(740, LaunchConfiguration.DEFAULT_HEIGHT);
  }

  @Test
  public void localeWithProjectAndPosition() {
    LaunchConfiguration config = LaunchConfiguration.parse(
        new String[]{"-l", "ja", "project.a3p", "100", "200"});
    assertEquals("ja", config.getLocaleString());
    assertEquals("project.a3p", config.getProjectFile().getPath());
    assertEquals(100, config.getXLocation());
    assertEquals(200, config.getYLocation());
    assertFalse(config.isMaximizationDesired());
  }

  @Test
  public void localeWithProjectPositionAndSize() {
    LaunchConfiguration config = LaunchConfiguration.parse(
        new String[]{"-l", "zh", "project.a3p", "50", "60", "800", "600"});
    assertEquals("zh", config.getLocaleString());
    assertEquals("project.a3p", config.getProjectFile().getPath());
    assertEquals(50, config.getXLocation());
    assertEquals(60, config.getYLocation());
    assertEquals(800, config.getWidth());
    assertEquals(600, config.getHeight());
    assertFalse(config.isMaximizationDesired());
  }

  @Test
  public void invalidPositionNumbersFallBackToDefaults() {
    LaunchConfiguration config = LaunchConfiguration.parse(
        new String[]{"project.a3p", "notanumber", "22"});
    assertEquals(0, config.getXLocation());
    assertEquals(0, config.getYLocation());
    assertEquals(LaunchConfiguration.DEFAULT_WIDTH, config.getWidth());
    assertEquals(LaunchConfiguration.DEFAULT_HEIGHT, config.getHeight());
    assertTrue(config.isMaximizationDesired());
  }

  @Test
  public void onlyProjectFileNoPositionKeepsMaximization() {
    LaunchConfiguration config = LaunchConfiguration.parse(new String[]{"myworld.a3p"});
    assertEquals("myworld.a3p", config.getProjectFile().getPath());
    assertTrue(config.isMaximizationDesired());
    assertEquals(0, config.getXLocation());
    assertEquals(0, config.getYLocation());
  }

  @Test
  public void positionWithoutSizeKeepsDefaultSize() {
    LaunchConfiguration config = LaunchConfiguration.parse(
        new String[]{"project.a3p", "10", "20"});
    assertEquals(LaunchConfiguration.DEFAULT_WIDTH, config.getWidth());
    assertEquals(LaunchConfiguration.DEFAULT_HEIGHT, config.getHeight());
    assertFalse(config.isMaximizationDesired());
  }

  @Test
  public void positionWithPartialSizeKeepsDefaultSize() {
    LaunchConfiguration config = LaunchConfiguration.parse(
        new String[]{"project.a3p", "10", "20", "400"});
    assertEquals(LaunchConfiguration.DEFAULT_WIDTH, config.getWidth());
    assertEquals(LaunchConfiguration.DEFAULT_HEIGHT, config.getHeight());
    assertFalse(config.isMaximizationDesired());
  }

  @Test
  public void zeroPositionWithSizeDisablesMaximization() {
    LaunchConfiguration config = LaunchConfiguration.parse(
        new String[]{"project.a3p", "0", "0", "1920", "1080"});
    assertEquals(0, config.getXLocation());
    assertEquals(0, config.getYLocation());
    assertEquals(1920, config.getWidth());
    assertEquals(1080, config.getHeight());
    assertFalse(config.isMaximizationDesired());
  }

  @Test
  public void localeFlagAloneWithNoArgumentsSetsNoLocaleOrProject() {
    LaunchConfiguration config = LaunchConfiguration.parse(new String[]{"-l"});
    assertNull(config.getLocaleString());
    assertNull(config.getProjectFile());
  }

  @Test
  public void localeFlagWithOnlyLocaleNoProject() {
    LaunchConfiguration config = LaunchConfiguration.parse(new String[]{"-l", "ko"});
    assertEquals("ko", config.getLocaleString());
    assertNull(config.getProjectFile());
  }

  @Test
  public void negativePositionValuesAreAccepted() {
    LaunchConfiguration config = LaunchConfiguration.parse(
        new String[]{"project.a3p", "-100", "-200"});
    assertEquals(-100, config.getXLocation());
    assertEquals(-200, config.getYLocation());
    assertFalse(config.isMaximizationDesired());
  }

  @Test
  public void nonLocaleFlagIsProjectFile() {
    LaunchConfiguration config = LaunchConfiguration.parse(new String[]{"-x"});
    assertEquals("-x", config.getProjectFile().getPath());
    assertNull(config.getLocaleString());
  }

  @Test
  public void largeNumberOfArgsOnlyUsesRelevantOnes() {
    LaunchConfiguration config = LaunchConfiguration.parse(
        new String[]{"project.a3p", "10", "20", "800", "600", "extra1", "extra2"});
    assertEquals(10, config.getXLocation());
    assertEquals(20, config.getYLocation());
    assertEquals(800, config.getWidth());
    assertEquals(600, config.getHeight());
  }
}

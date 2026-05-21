package edu.cmu.cs.dennisc.javax.swing;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import static org.junit.Assert.*;

public class UIManagerUtilitiesTest {
  private Object originalDefaultFont;
  private String originalFontScale;

  @Before
  public void setUp() {
    originalDefaultFont = UIManager.getDefaults().get("defaultFont");
    originalFontScale = System.getProperty("uimanager.fontScale");
  }

  @After
  public void tearDown() {
    if (originalFontScale != null) {
      System.setProperty("uimanager.fontScale", originalFontScale);
    } else {
      System.clearProperty("uimanager.fontScale");
    }
    UIManager.getDefaults().put("defaultFont", originalDefaultFont);
    UIManager.getLookAndFeelDefaults().remove("copilot.scale.font");
    UIManager.getLookAndFeelDefaults().remove("copilot.active.font");
  }

  @Test
  public void getFontScaleDefaultsToOneAndReadsPropertyOverride() {
    System.clearProperty("uimanager.fontScale");
    assertEquals(1.0, UIManagerUtilities.getFontScale(), 0.0);

    System.setProperty("uimanager.fontScale", "1.5");
    assertEquals(1.5, UIManagerUtilities.getFontScale(), 0.0);
  }

  @Test
  public void getDefaultFontSizeFallsBackToTwelveForNonFontDefaults() {
    UIManager.getDefaults().put("defaultFont", "not-a-font");

    assertEquals(12, UIManagerUtilities.getDefaultFontSize());
  }

  @Test
  public void scaleFontUpdatesDirectAndActiveFonts() {
    UIDefaults defaults = UIManager.getLookAndFeelDefaults();
    defaults.put("copilot.scale.font", new FontUIResource("Dialog", FontUIResource.PLAIN, 10));
    defaults.put("copilot.active.font", (UIDefaults.ActiveValue) table -> new FontUIResource("Dialog", FontUIResource.BOLD, 8));

    UIManagerUtilities.scaleFont(2.0);

    assertEquals(20, ((FontUIResource) defaults.get("copilot.scale.font")).getSize());
    assertEquals(16, ((FontUIResource) defaults.get("copilot.active.font")).getSize());
  }

  @Test
  public void scaleFontAppropriateLeavesFontsAloneAtUnityScale() {
    UIDefaults defaults = UIManager.getLookAndFeelDefaults();
    defaults.put("copilot.scale.font", new FontUIResource("Dialog", FontUIResource.PLAIN, 11));
    System.setProperty("uimanager.fontScale", "1.0");

    UIManagerUtilities.scaleFontIAppropriate();

    assertEquals(11, ((FontUIResource) defaults.get("copilot.scale.font")).getSize());
  }

  @Test
  public void getDefaultFontSizeReturnsConfiguredFontSize() {
    UIManager.getDefaults().put("defaultFont", new FontUIResource("Dialog", FontUIResource.PLAIN, 17));

    assertEquals(17, UIManagerUtilities.getDefaultFontSize());
  }
}

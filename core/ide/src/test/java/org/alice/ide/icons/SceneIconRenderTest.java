package org.alice.ide.icons;

import org.junit.Test;

import javax.swing.Icon;
import java.awt.Color;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SceneIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    SceneIcon icon = new SceneIcon(getSize());
    SceneIconTestSupport.seedCachedImage(icon);
    return icon;
  }

  @Test
  public void markDirtyTriggersAnotherRenderablePass() {
    SceneIcon icon = (SceneIcon) createIcon();
    assertTrue(countOpaquePixels(render(icon)) > 0);

    icon.markDirty();
    assertNull(SceneIconTestSupport.readField(icon, "image"));
    assertTrue((Boolean) SceneIconTestSupport.readField(icon, "isDirty"));

    SceneIconTestSupport.seedCachedImage(icon, new Color(0x33aa55));
    assertTrue(countOpaquePixels(render(icon)) > 0);
  }
}

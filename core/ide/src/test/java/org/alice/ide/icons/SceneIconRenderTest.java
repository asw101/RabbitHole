package org.alice.ide.icons;

import org.junit.Test;

import javax.swing.Icon;

import static org.junit.Assert.assertTrue;

public class SceneIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new SceneIcon(getSize());
  }

  @Test
  public void markDirtyTriggersAnotherRenderablePass() {
    SceneIcon icon = (SceneIcon) createIcon();
    assertTrue(countOpaquePixels(render(icon)) > 0);
    icon.markDirty();
    assertTrue(countOpaquePixels(render(icon)) > 0);
  }
}

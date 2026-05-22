package org.alice.ide.icons;

import javax.swing.Icon;

public class TabIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new TabIcon(getSize(), java.awt.Color.ORANGE);
  }
}

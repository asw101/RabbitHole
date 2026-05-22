package org.alice.ide.icons;

import javax.swing.Icon;

public class ConeIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new ConeIcon(getSize());
  }
}

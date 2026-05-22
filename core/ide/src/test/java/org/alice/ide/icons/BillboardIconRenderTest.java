package org.alice.ide.icons;

import javax.swing.Icon;

public class BillboardIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new BillboardIcon(getSize());
  }
}

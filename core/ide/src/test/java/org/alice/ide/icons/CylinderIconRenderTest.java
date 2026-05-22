package org.alice.ide.icons;

import javax.swing.Icon;

public class CylinderIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new CylinderIcon(getSize());
  }
}

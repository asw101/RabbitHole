package org.alice.ide.icons;

import javax.swing.Icon;

public class ClosedTrashIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new ClosedTrashIcon(getSize().width, getSize().height, java.awt.Color.GRAY);
  }
}

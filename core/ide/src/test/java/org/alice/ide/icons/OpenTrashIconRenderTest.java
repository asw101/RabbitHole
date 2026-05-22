package org.alice.ide.icons;

import javax.swing.Icon;

public class OpenTrashIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new OpenTrashIcon(getSize().width, getSize().height, java.awt.Color.GRAY);
  }
}

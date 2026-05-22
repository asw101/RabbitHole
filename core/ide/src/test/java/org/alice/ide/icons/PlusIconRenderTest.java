package org.alice.ide.icons;

import javax.swing.Icon;

public class PlusIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new PlusIcon(getSize());
  }
}

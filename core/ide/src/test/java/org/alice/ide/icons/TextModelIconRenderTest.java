package org.alice.ide.icons;

import javax.swing.Icon;

public class TextModelIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new TextModelIcon(getSize());
  }
}

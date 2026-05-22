package org.alice.ide.icons;

import javax.swing.Icon;

public class JointIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new JointIcon(getSize());
  }
}

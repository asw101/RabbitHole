package org.alice.ide.icons;

import javax.swing.Icon;

public class ThemeIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new ThemeIcon(getSize(), IconTestSupport.createResourceKey("missing-theme-icon"));
  }
}

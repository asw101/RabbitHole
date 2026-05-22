package org.alice.ide.icons;

import org.lgna.croquet.icon.IconFactory;

public class ThemeIconFactoryBehaviorTest extends AbstractIconFactoryBehaviorTest {
  @Override
  protected IconFactory createIconFactory() {
    return new ThemeIconFactory(IconTestSupport.createResourceKey("missing-theme-icon"));
  }
}

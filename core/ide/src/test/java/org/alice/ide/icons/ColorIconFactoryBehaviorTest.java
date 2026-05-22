package org.alice.ide.icons;

import org.lgna.croquet.icon.IconFactory;

public class ColorIconFactoryBehaviorTest extends AbstractIconFactoryBehaviorTest {
  @Override
  protected IconFactory createIconFactory() {
    return new ColorIconFactory(java.awt.Color.MAGENTA);
  }
}

package org.alice.ide.icons;

import org.lgna.croquet.icon.IconFactory;

public class ShapeIconFactoryBehaviorTest extends AbstractIconFactoryBehaviorTest {
  @Override
  protected IconFactory createIconFactory() {
    return new ShapeIconFactory(BoxIcon::new);
  }
}

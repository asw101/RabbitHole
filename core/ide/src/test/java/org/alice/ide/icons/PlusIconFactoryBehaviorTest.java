package org.alice.ide.icons;

import org.lgna.croquet.icon.IconFactory;

public class PlusIconFactoryBehaviorTest extends AbstractIconFactoryBehaviorTest {
  @Override
  protected IconFactory createIconFactory() {
    return PlusIconFactory.getInstance();
  }
}

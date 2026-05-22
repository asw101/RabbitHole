package org.alice.ide.icons;

import org.lgna.croquet.icon.IconFactory;

public class CheckIconFactoryBehaviorTest extends AbstractIconFactoryBehaviorTest {
  @Override
  protected IconFactory createIconFactory() {
    return CheckIconFactory.getInstance();
  }
}

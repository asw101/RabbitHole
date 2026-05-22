package org.alice.ide.icons;

import org.lgna.croquet.icon.IconFactory;

public class SceneIconFactoryBehaviorTest extends AbstractIconFactoryBehaviorTest {
  @Override
  protected IconFactory createIconFactory() {
    return SceneIconFactory.getInstance();
  }
}

package org.alice.ide.icons;

import org.lgna.croquet.icon.IconFactory;

import javax.swing.Icon;

public abstract class AbstractIconFactoryBehaviorTest extends AbstractRenderableIconTest {
  protected abstract IconFactory createIconFactory();

  @Override
  protected final Icon createIcon() {
    Icon icon = createIconFactory().getIconToFit(getSize());
    if (icon instanceof SceneIcon) {
      SceneIconTestSupport.seedCachedImage((SceneIcon) icon);
    }
    return icon;
  }
}

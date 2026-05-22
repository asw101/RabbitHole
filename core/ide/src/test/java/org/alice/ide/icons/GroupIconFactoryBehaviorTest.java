package org.alice.ide.icons;

import org.lgna.croquet.icon.IconFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;

public class GroupIconFactoryBehaviorTest extends AbstractIconFactoryBehaviorTest {
  @Override
  protected Dimension getSize() {
    return new Dimension(96, 96);
  }

  @Override
  protected IconFactory createIconFactory() {
    return new GroupIconFactory(Arrays.asList(
        IconTestSupport.createSingleSourceFactory(Color.RED),
        IconTestSupport.createSingleSourceFactory(Color.GREEN)));
  }
}

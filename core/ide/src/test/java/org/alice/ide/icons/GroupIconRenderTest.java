package org.alice.ide.icons;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;

public class GroupIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Dimension getSize() {
    return new Dimension(96, 96);
  }

  @Override
  protected Icon createIcon() {
    return new GroupIcon(getSize(), Arrays.asList(
        IconTestSupport.createSingleSourceFactory(Color.RED),
        IconTestSupport.createSingleSourceFactory(Color.GREEN),
        IconTestSupport.createSingleSourceFactory(Color.BLUE)));
  }
}

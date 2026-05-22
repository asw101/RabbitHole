package org.alice.ide.icons;

import org.junit.Test;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.assertNotNull;

public class FieldIconFactoryBehaviorTest extends AbstractIconFactoryBehaviorTest {
  @Override
  protected IconFactory createIconFactory() {
    return new FieldIconFactory(IconTestSupport.createField("camera", JavaType.STRING_TYPE), CheckIconFactory.getInstance());
  }

  @Test
  public void markAllIconsDirtyLeavesFactoryUsable() {
    FieldIconFactory factory = (FieldIconFactory) createIconFactory();
    assertNotNull(factory.getIconToFit(getSize()));
    factory.markAllIconsDirty();
    assertNotNull(factory.getIconToFit(getSize()));
  }
}

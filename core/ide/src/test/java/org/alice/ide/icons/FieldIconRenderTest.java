package org.alice.ide.icons;

import org.junit.Test;
import org.lgna.project.ast.JavaType;

import javax.swing.Icon;

import static org.junit.Assert.assertTrue;

public class FieldIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new FieldIcon(IconTestSupport.createField("hero", JavaType.STRING_TYPE), new CheckIcon(getSize()));
  }

  @Test
  public void markDirtyAllowsRepeatedRendering() {
    FieldIcon icon = (FieldIcon) createIcon();
    assertTrue(countOpaquePixels(render(icon)) > 0);
    icon.markDirty();
    assertTrue(countOpaquePixels(render(icon)) > 0);
  }
}

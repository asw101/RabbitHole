package org.alice.ide.icons;

import org.alice.stageide.modelresource.ResourceKey;
import org.alice.stageide.modelresource.ResourceNode;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.SingleSelectTreeState;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.croquet.icon.AbstractSingleSourceImageIconFactory;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Set;

final class IconTestSupport {
  private IconTestSupport() {
    throw new AssertionError();
  }

  static UserField createField(String name, AbstractType<?, ?, ?> valueType) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(valueType);
    return field;
  }

  static AbstractSingleSourceImageIconFactory createSingleSourceFactory(Color color) {
    return new SolidSourceFactory(color);
  }

  static ResourceKey createResourceKey(String internalName) {
    return new DummyResourceKey(internalName);
  }

  private static final class SolidSourceFactory extends AbstractSingleSourceImageIconFactory {
    private SolidSourceFactory(Color color) {
      super(createIcon(color));
    }

    private static Icon createIcon(Color color) {
      BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = image.createGraphics();
      try {
        g2.setColor(color);
        g2.fillRect(0, 0, image.getWidth(), image.getHeight());
      } finally {
        g2.dispose();
      }
      return new ImageIcon(image);
    }

    @Override
    protected Icon createIcon(Dimension size) {
      return getSourceImageIcon();
    }
  }

  private static final class DummyResourceKey extends ResourceKey {
    private final String internalName;

    private DummyResourceKey(String internalName) {
      this.internalName = internalName;
    }

    @Override
    public String getSearchText() {
      return this.internalName;
    }

    @Override
    public String getInternalName() {
      return this.internalName;
    }

    @Override
    public String getLocalizedName() {
      return this.internalName;
    }

    @Override
    public String getLocalizedCreationText() {
      return this.internalName;
    }

    @Override
    public IconFactory getIconFactory() {
      return null;
    }

    @Override
    public boolean isLeaf() {
      return true;
    }

    @Override
    protected void appendRep(StringBuilder sb) {
      sb.append(this.internalName);
    }

    @Override
    public InstanceCreation createInstanceCreation(Set<NamedUserType> typeCache) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String[] getTags() {
      return null;
    }

    @Override
    public String[] getGroupTags() {
      return null;
    }

    @Override
    public String[] getThemeTags() {
      return null;
    }

    @Override
    public boolean isInstanceCreator() {
      return false;
    }

    @Override
    public Triggerable getLeftClickOperation(ResourceNode node, SingleSelectTreeState<ResourceNode> controller) {
      return null;
    }

    @Override
    public Triggerable getDropOperation(ResourceNode node, DragStep step, DropSite dropSite) {
      return null;
    }
  }
}

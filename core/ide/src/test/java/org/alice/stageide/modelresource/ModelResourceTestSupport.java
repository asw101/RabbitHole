package org.alice.stageide.modelresource;

import edu.cmu.cs.dennisc.java.util.InitializingIfAbsentListHashMap;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.SingleSelectTreeState;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.croquet.icon.AbstractSingleSourceImageIconFactory;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.NamedUserType;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ModelResourceTestSupport {
  private ModelResourceTestSupport() {
  }

  static final class FakeResourceKey extends ResourceKey {
    private final String internalName;
    private final String localizedName;
    private final boolean leaf;
    private final String[] groupTags;
    private final String[] themeTags;
    private final IconFactory iconFactory;

    FakeResourceKey(String internalName, String localizedName, boolean leaf, String[] groupTags, String[] themeTags, IconFactory iconFactory) {
      this.internalName = internalName;
      this.localizedName = localizedName;
      this.leaf = leaf;
      this.groupTags = groupTags;
      this.themeTags = themeTags;
      this.iconFactory = iconFactory;
    }

    @Override
    public String getSearchText() {
      return internalName;
    }

    @Override
    public String getInternalName() {
      return internalName;
    }

    @Override
    public String getLocalizedName() {
      return localizedName;
    }

    @Override
    public String getLocalizedCreationText() {
      return localizedName;
    }

    @Override
    public IconFactory getIconFactory() {
      return iconFactory;
    }

    @Override
    public boolean isLeaf() {
      return leaf;
    }

    @Override
    protected void appendRep(StringBuilder sb) {
      sb.append(localizedName);
    }

    @Override
    public InstanceCreation createInstanceCreation(Set<NamedUserType> typeCache) {
      return null;
    }

    @Override
    public String[] getTags() {
      return null;
    }

    @Override
    public String[] getGroupTags() {
      return groupTags;
    }

    @Override
    public String[] getThemeTags() {
      return themeTags;
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

  static final class FakeSingleSourceFactory extends AbstractSingleSourceImageIconFactory {
    FakeSingleSourceFactory() {
      super(new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)));
    }

    @Override
    protected Icon createIcon(Dimension size) {
      return getSourceImageIcon();
    }
  }

  static ResourceNode node(String internalName, String localizedName, String[] groupTags, String[] themeTags, IconFactory iconFactory, ResourceNode... children) {
    return new ResourceNode(new FakeResourceKey(internalName, localizedName, children.length == 0, groupTags, themeTags, iconFactory), Arrays.asList(children));
  }

  @SuppressWarnings("unchecked")
  static <T> T invokeTreeUtilities(String name, Class<?>[] parameterTypes, Object... args) throws Exception {
    Method method = TreeUtilities.class.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return (T) method.invoke(null, args);
  }

  static InitializingIfAbsentListHashMap<String, ResourceNode> map() {
    return new InitializingIfAbsentListHashMap<>();
  }

  static List<ResourceNode> singleton(ResourceNode node) {
    return Collections.singletonList(node);
  }

  static Map<String, ResourceNode> emptyTagNodeMap() {
    return java.util.Collections.emptyMap();
  }
}

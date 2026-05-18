package org.alice.stageide.modelresource;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.SingleSelectTreeState;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.croquet.icon.AbstractSingleSourceImageIconFactory;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.resources.DynamicPropResource;
import org.lgna.story.resources.ModelResource;
import org.lgna.story.resources.PropResource;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ResourceNodeTest {
  private static class SimpleIconFactory extends AbstractSingleSourceImageIconFactory {
    public SimpleIconFactory() {
      super(new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)));
    }

    @Override
    protected Icon createIcon(Dimension size) {
      return getSourceImageIcon();
    }
  }

  private static class TestResourceKey extends ResourceKey {
    private final String localizedName;
    private final String localizedCreationText;
    private final boolean leaf;
    private final IconFactory iconFactory;
    private final Triggerable leftClickOperation;
    private final Triggerable dropOperation;

    private TestResourceKey(String localizedName, String localizedCreationText, boolean leaf, IconFactory iconFactory, Triggerable leftClickOperation, Triggerable dropOperation) {
      this.localizedName = localizedName;
      this.localizedCreationText = localizedCreationText;
      this.leaf = leaf;
      this.iconFactory = iconFactory;
      this.leftClickOperation = leftClickOperation;
      this.dropOperation = dropOperation;
    }

    @Override
    public String getSearchText() {
      return this.localizedName;
    }

    @Override
    public String getInternalName() {
      return this.localizedName;
    }

    @Override
    public String getLocalizedName() {
      return this.localizedName;
    }

    @Override
    public String getLocalizedCreationText() {
      return this.localizedCreationText;
    }

    @Override
    public IconFactory getIconFactory() {
      return this.iconFactory;
    }

    @Override
    public boolean isLeaf() {
      return this.leaf;
    }

    @Override
    protected void appendRep(StringBuilder sb) {
      sb.append(this.localizedName);
    }

    @Override
    public InstanceCreation createInstanceCreation(java.util.Set<NamedUserType> typeCache) {
      return null;
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
      return this.leftClickOperation;
    }

    @Override
    public Triggerable getDropOperation(ResourceNode node, DragStep step, DropSite dropSite) {
      return this.dropOperation;
    }
  }

  private static class TestInstanceCreatorKey extends InstanceCreatorKey {
    private final AxisAlignedBox boundingBox;
    private final boolean placeOnGround;

    private TestInstanceCreatorKey(AxisAlignedBox boundingBox, boolean placeOnGround) {
      this.boundingBox = boundingBox;
      this.placeOnGround = placeOnGround;
    }

    @Override
    public Class<? extends ModelResource> getModelResourceCls() {
      return PropResource.class;
    }

    @Override
    public String getSearchText() {
      return "instance creator";
    }

    @Override
    public String getInternalName() {
      return "instance creator";
    }

    @Override
    public String getLocalizedName() {
      return "Instance Creator";
    }

    @Override
    public String getLocalizedCreationText() {
      return "Create Instance Creator";
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
      sb.append("instance creator");
    }

    @Override
    public InstanceCreation createInstanceCreation(java.util.Set<NamedUserType> typeCache) {
      return null;
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
    public Triggerable getLeftClickOperation(ResourceNode node, SingleSelectTreeState<ResourceNode> controller) {
      return null;
    }

    @Override
    public Triggerable getDropOperation(ResourceNode node, DragStep step, DropSite dropSite) {
      return null;
    }

    @Override
    public AxisAlignedBox getBoundingBox() {
      return this.boundingBox;
    }

    @Override
    public boolean getPlaceOnGround() {
      return this.placeOnGround;
    }
  }

  private TestResourceKey createKey(String localizedName) {
    return new TestResourceKey(localizedName, "Create " + localizedName, false, null, null, null);
  }

  private DynamicPropResource createDynamicResource(String modelName) {
    ModelManifest manifest = new ModelManifest();
    manifest.parentClass = "Prop";
    manifest.description.name = modelName;
    manifest.description.tags.add("tag");
    manifest.description.groupTags.add("group");
    manifest.description.themeTags.add("theme");
    manifest.placeOnGround = true;
    ModelManifest.BoundingBox boundingBox = new ModelManifest.BoundingBox();
    boundingBox.min = List.of(0.0f, 1.0f, 2.0f);
    boundingBox.max = List.of(3.0f, 4.0f, 5.0f);
    manifest.boundingBox = boundingBox;
    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.name = "DEFAULT";
    return new DynamicPropResource(manifest, variant);
  }

  @Test
  public void constructor_setsParentOnChildren() {
    ResourceNode child = new ResourceNode(createKey("Child"), new ArrayList<>());
    ResourceNode parent = new ResourceNode(createKey("Parent"), new ArrayList<>(List.of(child)));
    assertNull(parent.getParent());
    assertSame(parent, child.getParent());
  }

  @Test
  public void getResourceKey_and_children_returnProvidedValues() {
    TestResourceKey key = createKey("Parent");
    ResourceNode child = new ResourceNode(createKey("Child"), new ArrayList<>());
    ResourceNode parent = new ResourceNode(key, new ArrayList<>(List.of(child)));
    assertSame(key, parent.getResourceKey());
    assertEquals(1, parent.getNodeChildren().size());
    assertSame(child, parent.getNodeChildren().getFirst());
  }

  @Test
  public void isUserDefinedModel_trueForDynamicResourceKey() {
    ResourceNode node = new ResourceNode(new DynamicResourceKey(createDynamicResource("Custom Model")), new ArrayList<>());
    assertTrue(node.isUserDefinedModel());
  }

  @Test
  public void getText_and_getSimpleClassName_delegateToKey() {
    ResourceNode node = new ResourceNode(createKey("Display Name"), new ArrayList<>());
    assertEquals("Create Display Name", node.getText());
    assertEquals("Display Name", node.getSimpleClassName());
  }

  @Test
  public void getIconFactory_delegatesToKey() {
    IconFactory iconFactory = new SimpleIconFactory();
    ResourceNode node = new ResourceNode(new TestResourceKey("Icon", "Create Icon", false, iconFactory, null, null), new ArrayList<>());
    assertSame(iconFactory, node.getIconFactory());
  }

  @Test
  public void getAddFieldBlankChild_nonLeafNode_returnsBlankChild() {
    ResourceNode node = new ResourceNode(createKey("Parent"), new ArrayList<>());
    assertNotNull(node.getAddFieldBlankChild());
  }

  @Test
  public void getDropOperation_and_getLeftButtonClickOperation_delegateToKey() {
    Triggerable leftClick = activity -> {
    };
    Triggerable drop = activity -> {
    };
    ResourceNode node = new ResourceNode(new TestResourceKey("Ops", "Create Ops", false, null, leftClick, drop), new ArrayList<>());
    assertSame(leftClick, node.getLeftButtonClickOperation(null));
    assertSame(drop, node.getDropOperation(null, null));
  }

  @Test
  public void leftClickHack_forcesNull() {
    Triggerable leftClick = activity -> {
    };
    ResourceNode node = new ResourceNode(new TestResourceKey("Ops", "Create Ops", false, null, leftClick, null), new ArrayList<>());
    ResourceNode.ACCEPTABLE_HACK_FOR_GALLERY_QA_setLeftClickModelAlwaysNull(true);
    try {
      assertNull(node.getLeftButtonClickOperation(null));
    } finally {
      ResourceNode.ACCEPTABLE_HACK_FOR_GALLERY_QA_setLeftClickModelAlwaysNull(false);
    }
  }

  @Test
  public void breadcrumbFlag_and_compareTo_work() {
    ResourceNode beta = new ResourceNode(createKey("Beta"), new ArrayList<>(), true);
    ResourceNode alpha = new ResourceNode(createKey("alpha"), new ArrayList<>());
    assertTrue(beta.isBreadcrumbButtonIconDesired());
    assertTrue(beta.compareTo(alpha) > 0);
  }

  @Test
  public void instanceCreatorProperties_delegateToKey() {
    AxisAlignedBox boundingBox = new AxisAlignedBox(new Point3(1, 2, 3), new Point3(4, 5, 6));
    ResourceNode node = new ResourceNode(new TestInstanceCreatorKey(boundingBox, true), new ArrayList<>());
    assertTrue(node.isInstanceCreator());
    assertEquals(boundingBox, node.getBoundingBox());
    assertTrue(node.placeOnGround());
  }
}

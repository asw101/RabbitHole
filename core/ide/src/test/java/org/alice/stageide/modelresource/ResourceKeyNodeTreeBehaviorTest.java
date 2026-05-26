package org.alice.stageide.modelresource;

import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.SingleSelectTreeState;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.NamedUserType;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class ResourceKeyNodeTreeBehaviorTest {
  @Before
  public void setUpIde() {
    TestIdeBootstrap.ensureInstalled();
  }

  @After
  public void tearDownIde() {
    TestIdeBootstrap.reset();
  }

  private static final class RecordingTriggerable implements Triggerable {
    private int fireCount;

    @Override
    public void fire(org.lgna.croquet.history.UserActivity activity) {
      this.fireCount++;
    }
  }

  private static final class RecordingKey extends ResourceKey {
    private final String internalName;
    private final String localizedName;
    private final boolean leaf;
    private final Triggerable leftClickOperation;
    private int createInstanceCreationCalls;
    private int lastTypeCacheSize = -1;

    private RecordingKey(String internalName, String localizedName, boolean leaf, Triggerable leftClickOperation) {
      this.internalName = internalName;
      this.localizedName = localizedName;
      this.leaf = leaf;
      this.leftClickOperation = leftClickOperation;
    }

    @Override
    public String getSearchText() {
      return internalName + " " + localizedName;
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
      return "new " + localizedName;
    }

    @Override
    public org.lgna.croquet.icon.IconFactory getIconFactory() {
      return null;
    }

    @Override
    public boolean isLeaf() {
      return leaf;
    }

    @Override
    protected void appendRep(StringBuilder sb) {
      sb.append(internalName).append("->").append(localizedName);
    }

    @Override
    public InstanceCreation createInstanceCreation(Set<NamedUserType> typeCache) {
      this.createInstanceCreationCalls++;
      this.lastTypeCacheSize = typeCache.size();
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
      return leftClickOperation;
    }

    @Override
    public Triggerable getDropOperation(ResourceNode node, DragStep step, DropSite dropSite) {
      return null;
    }
  }

  private static final class ExposedResourceNodeTreeState extends ResourceNodeTreeState {
    private ExposedResourceNodeTreeState(ResourceNode root) {
      super(root);
    }

    private void select(ResourceNode node) {
      super.setCurrentTruthAndBeautyValue(node);
    }

    private int childCount(ResourceNode parent) {
      return super.getChildCount(parent);
    }

    private ResourceNode child(ResourceNode parent, int index) {
      return super.getChild(parent, index);
    }

    private int indexOf(ResourceNode parent, ResourceNode child) {
      return super.getIndexOfChild(parent, child);
    }

    private String textFor(ResourceNode node) {
      return super.getTextForNode(node);
    }
  }

  @Test
  public void resourceKeyToStringUsesAppendRepAndCreateInstanceCreationUsesEmptyCacheWithoutProject() {
    RecordingKey key = new RecordingKey("penguin", "Penguin", true, null);

    key.createInstanceCreation();

    assertEquals("RecordingKey[penguin->Penguin]", key.toString());
    assertEquals(1, key.createInstanceCreationCalls);
    assertEquals(0, key.lastTypeCacheSize);
  }

  @Test
  public void addNodeChildAssignsParentAndMaintainsCaseInsensitiveOrdering() {
    ResourceNode root = new ResourceNode(new RootResourceKey("root", "Root"), new ArrayList<>());
    ResourceNode zebra = new ResourceNode(UUID.randomUUID(), new RecordingKey("zebra", "Zebra", true, null));
    ResourceNode apple = new ResourceNode(UUID.randomUUID(), new RecordingKey("apple", "apple", true, null));
    ResourceNode bear = new ResourceNode(UUID.randomUUID(), new RecordingKey("bear", "Bear", true, null));

    root.addNodeChild(zebra);
    root.addNodeChild(apple);
    root.addNodeChild(bear);

    assertSame(root, zebra.getParent());
    assertSame(root, apple.getParent());
    assertSame(root, bear.getParent());
    assertEquals(
        java.util.List.of("new apple", "new Bear", "new Zebra"),
        root.getNodeChildren().stream().map(ResourceNode::getText).toList());
  }

  @Test
  public void resourceNodeTreeStateNavigatesChildrenAndUsesCreationText() {
    ResourceNode first = new ResourceNode(UUID.randomUUID(), new RecordingKey("alpha", "Alpha", true, null));
    ResourceNode second = new ResourceNode(UUID.randomUUID(), new RecordingKey("beta", "Beta", true, null));
    ResourceNode root = new ResourceNode(new RootResourceKey("root", "Root"), new ArrayList<>(java.util.List.of(first, second)));
    ExposedResourceNodeTreeState state = new ExposedResourceNodeTreeState(root);

    assertEquals(2, state.childCount(root));
    assertSame(first, state.child(root, 0));
    assertEquals(1, state.indexOf(root, second));
    assertEquals("new Alpha", state.textFor(first));
  }

  @Test
  public void selectingLeafNodeFiresLeftClickOperationButBranchSelectionDoesNot() {
    RecordingTriggerable triggerable = new RecordingTriggerable();
    ResourceNode leaf = new ResourceNode(UUID.randomUUID(), new RecordingKey("leaf", "Leaf", true, triggerable));
    ResourceNode branch = new ResourceNode(new RecordingKey("branch", "Branch", false, triggerable), new ArrayList<>(java.util.List.of(leaf)));
    ExposedResourceNodeTreeState state = new ExposedResourceNodeTreeState(branch);

    state.select(branch);
    state.select(leaf);

    assertEquals(1, triggerable.fireCount);
    assertNotNull(leaf.getAddFieldBlankChild());
  }
}

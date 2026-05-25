package org.alice.stageide.gallerybrowser.views;

import org.alice.stageide.modelresource.ResourceKey;
import org.alice.stageide.modelresource.ResourceNode;
import org.junit.Test;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.SingleSelectTreeState;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.NamedUserType;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class GallerySelectionScrollStateTest {
  private static final class FakeResourceKey extends ResourceKey {
    private final String name;

    private FakeResourceKey(String name) {
      this.name = name;
    }

    @Override
    public String getSearchText() {
      return name;
    }

    @Override
    public String getInternalName() {
      return name;
    }

    @Override
    public String getLocalizedName() {
      return name;
    }

    @Override
    public String getLocalizedCreationText() {
      return name;
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
      sb.append(name);
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

  private static ResourceNode node(String name) {
    return new ResourceNode(new FakeResourceKey(name), Collections.emptyList());
  }

  @Test
  public void rememberAndGetNextPositionRestoresSavedSelections() {
    GallerySelectionScrollState state = new GallerySelectionScrollState();
    ResourceNode cat = node("cat");
    ResourceNode dog = node("dog");
    ResourceNode bunny = node("bunny");

    assertEquals(0, state.rememberAndGetNextPosition(null, cat, 12));
    assertEquals(0, state.rememberAndGetNextPosition(cat, dog, 24));
    assertEquals(24, state.rememberAndGetNextPosition(dog, cat, 30));
    assertEquals(30, state.rememberAndGetNextPosition(cat, dog, 6));
    assertEquals(0, state.rememberAndGetNextPosition(dog, bunny, 18));
  }
}

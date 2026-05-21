package org.alice.stageide.modelresource;

import org.junit.Test;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.SingleSelectTreeState;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.fish.ArapaimaResource;
import org.lgna.story.resources.prop.ArrowResource;

import java.lang.reflect.Modifier;
import java.util.Set;

import static org.junit.Assert.*;

public class ResourceKeyStructureTest {

  private static final class TestResourceKey extends ResourceKey {
    @Override
    public String getSearchText() {
      return "search-text";
    }

    @Override
    public String getInternalName() {
      return "internal-name";
    }

    @Override
    public String getLocalizedName() {
      return "localized-name";
    }

    @Override
    public String getLocalizedCreationText() {
      return "localized-creation-text";
    }

    @Override
    public org.lgna.croquet.icon.IconFactory getIconFactory() {
      return null;
    }

    @Override
    public boolean isLeaf() {
      return false;
    }

    @Override
    protected void appendRep(StringBuilder sb) {
      sb.append("test-representation");
    }

    @Override
    public InstanceCreation createInstanceCreation(Set<NamedUserType> typeCache) {
      return null;
    }

    @Override
    public String[] getTags() {
      return new String[] {"one"};
    }

    @Override
    public String[] getGroupTags() {
      return new String[] {"group"};
    }

    @Override
    public String[] getThemeTags() {
      return new String[] {"theme"};
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

  @Test
  public void resourceKey_classIsAbstract() {
    assertTrue(Modifier.isAbstract(ResourceKey.class.getModifiers()));
  }

  @Test
  public void classResourceKey_classIsFinal() {
    assertTrue(Modifier.isFinal(ClassResourceKey.class.getModifiers()));
  }

  @Test
  public void enumConstantResourceKey_classIsFinal() {
    assertTrue(Modifier.isFinal(EnumConstantResourceKey.class.getModifiers()));
  }

  @Test
  public void classResourceKey_extendsInstanceCreatorKey() {
    assertEquals(InstanceCreatorKey.class, ClassResourceKey.class.getSuperclass());
  }

  @Test
  public void enumConstantResourceKey_extendsInstanceCreatorKey() {
    assertEquals(InstanceCreatorKey.class, EnumConstantResourceKey.class.getSuperclass());
  }

  @Test
  public void toString_testSubclass_usesSimpleNameAndAppendRep() {
    assertEquals("TestResourceKey[test-representation]", new TestResourceKey().toString());
  }

  @Test
  public void classResourceKey_getModelResourceCls_returnsProvidedClass() {
    ClassResourceKey key = new ClassResourceKey(ArapaimaResource.class);
    assertEquals(ArapaimaResource.class, key.getModelResourceCls());
  }

  @Test
  public void classResourceKey_getType_singleConstantEnum_returnsJavaType() {
    ClassResourceKey key = new ClassResourceKey(ArapaimaResource.class);
    assertNotNull(key.getType());
  }

  @Test
  public void classResourceKey_isLeaf_singleConstantEnum_returnsTrue() {
    ClassResourceKey key = new ClassResourceKey(ArapaimaResource.class);
    assertTrue(key.isLeaf());
  }

  @Test
  public void classResourceKey_isInterface_storyInterface_returnsTrue() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertTrue(key.isInterface());
  }

  @Test
  public void classResourceKey_equals_sameResourceClass_returnsTrue() {
    assertEquals(new ClassResourceKey(ArapaimaResource.class), new ClassResourceKey(ArapaimaResource.class));
  }

  @Test
  public void enumConstantResourceKey_getEnumConstant_returnsProvidedConstant() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertEquals(ArapaimaResource.DEFAULT, key.getEnumConstant());
  }

  @Test
  public void enumConstantResourceKey_getModelResourceCls_returnsDeclaringClass() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertEquals(ArapaimaResource.class, key.getModelResourceCls());
  }

  @Test
  public void enumConstantResourceKey_getField_returnsMatchingField() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertEquals("DEFAULT", key.getField().getName());
  }

  @Test
  public void enumConstantResourceKey_equals_sameEnumConstant_returnsTrue() {
    assertEquals(new EnumConstantResourceKey(ArapaimaResource.DEFAULT), new EnumConstantResourceKey(ArapaimaResource.DEFAULT));
  }

  @Test
  public void enumConstantResourceKey_equals_differentEnumConstant_returnsFalse() {
    assertNotEquals(new EnumConstantResourceKey(ArapaimaResource.DEFAULT), new EnumConstantResourceKey(ArrowResource.DEFAULT));
  }

}

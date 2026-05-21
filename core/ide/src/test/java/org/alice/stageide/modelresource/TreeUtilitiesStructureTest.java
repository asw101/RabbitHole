package org.alice.stageide.modelresource;

import org.junit.Test;
import org.lgna.croquet.ItemCodec;
import org.lgna.croquet.SingleSelectListState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TreeUtilitiesStructureTest {

  private Field getDeclaredField(String name) throws Exception {
    return TreeUtilities.class.getDeclaredField(name);
  }

  private Method getDeclaredMethod(String name, Class<?>... parameterTypes) throws Exception {
    return TreeUtilities.class.getDeclaredMethod(name, parameterTypes);
  }

  @Test
  public void class_isLoadable() {
    assertNotNull(TreeUtilities.class);
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(TreeUtilities.class.getModifiers()));
  }

  @Test
  public void constructor_privateNoArgs_exists() throws Exception {
    Constructor<TreeUtilities> constructor = TreeUtilities.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    assertEquals(0, constructor.getParameterTypes().length);
  }

  @Test
  public void constructors_onlySingleUtilityConstructor_exists() {
    assertEquals(1, TreeUtilities.class.getDeclaredConstructors().length);
  }

  @Test
  public void getClassTreeState_publicStatic_returnsResourceNodeTreeState() throws Exception {
    Method method = TreeUtilities.class.getMethod("getClassTreeState");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(ResourceNodeTreeState.class, method.getReturnType());
  }

  @Test
  public void getThemeTreeState_publicStatic_returnsResourceNodeTreeState() throws Exception {
    Method method = TreeUtilities.class.getMethod("getThemeTreeState");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(ResourceNodeTreeState.class, method.getReturnType());
  }

  @Test
  public void getGroupTreeState_publicStatic_returnsResourceNodeTreeState() throws Exception {
    Method method = TreeUtilities.class.getMethod("getGroupTreeState");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(ResourceNodeTreeState.class, method.getReturnType());
  }

  @Test
  public void getUserTreeState_publicStatic_returnsResourceNodeTreeState() throws Exception {
    Method method = TreeUtilities.class.getMethod("getUserTreeState");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(ResourceNodeTreeState.class, method.getReturnType());
  }

  @Test
  public void getSClassListState_publicStatic_returnsSingleSelectListState() throws Exception {
    Method method = TreeUtilities.class.getMethod("getSClassListState");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(SingleSelectListState.class, method.getReturnType());
  }

  @Test
  public void nestedSClassCodec_existsAsDeclaredClass() {
    List<String> names = Arrays.stream(TreeUtilities.class.getDeclaredClasses())
        .map(Class::getSimpleName)
        .collect(Collectors.toList());
    assertTrue(names.contains("SClassCodec"));
  }

  @Test
  public void nestedSClassCodec_isStaticAndImplementsItemCodec() {
    Class<?> codecClass = Arrays.stream(TreeUtilities.class.getDeclaredClasses())
        .filter(cls -> cls.getSimpleName().equals("SClassCodec"))
        .findFirst()
        .orElseThrow();
    assertTrue(Modifier.isStatic(codecClass.getModifiers()));
    assertTrue(ItemCodec.class.isAssignableFrom(codecClass));
    assertEquals(TreeUtilities.class, codecClass.getEnclosingClass());
  }

  @Test
  public void cacheFields_privateStatic_existForAllKnownTrees() throws Exception {
    assertTrue(Modifier.isPrivate(getDeclaredField("classTreeState").getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredField("classTreeState").getModifiers()));
    assertTrue(Modifier.isPrivate(getDeclaredField("themeTreeState").getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredField("themeTreeState").getModifiers()));
    assertTrue(Modifier.isPrivate(getDeclaredField("groupTreeState").getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredField("groupTreeState").getModifiers()));
    assertTrue(Modifier.isPrivate(getDeclaredField("userTreeState").getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredField("userTreeState").getModifiers()));
  }

  @Test
  public void backingRootFields_privateStatic_existForDerivedTrees() throws Exception {
    assertTrue(Modifier.isPrivate(getDeclaredField("treeBasedOnClassHierarchy").getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredField("treeBasedOnClassHierarchy").getModifiers()));
    assertTrue(Modifier.isPrivate(getDeclaredField("treeBasedOnTheme").getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredField("treeBasedOnTheme").getModifiers()));
    assertTrue(Modifier.isPrivate(getDeclaredField("treeBasedOnGroup").getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredField("treeBasedOnGroup").getModifiers()));
  }

  @Test
  public void helperMethods_privateStatic_existWithExpectedNames() throws Exception {
    assertTrue(Modifier.isPrivate(getDeclaredMethod("createNode", org.lgna.story.resourceutilities.GalleryResourceTreeNode.class, ResourceKey.class).getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredMethod("createNode", org.lgna.story.resourceutilities.GalleryResourceTreeNode.class, ResourceKey.class).getModifiers()));
    assertTrue(Modifier.isPrivate(getDeclaredMethod("findByKey", ResourceNode.class, ResourceKey.class).getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredMethod("findByKey", ResourceNode.class, ResourceKey.class).getModifiers()));
    assertTrue(Modifier.isPrivate(getDeclaredMethod("selectResourceNodes", ResourceNode.class, java.util.List.class, java.util.function.Predicate.class).getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredMethod("selectResourceNodes", ResourceNode.class, java.util.List.class, java.util.function.Predicate.class).getModifiers()));
  }

  @Test
  public void treeBuildingMethods_privateStatic_exist() throws Exception {
    assertTrue(Modifier.isPrivate(getDeclaredMethod("createTreesBasedOnThemeAndGroup").getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredMethod("createTreesBasedOnThemeAndGroup").getModifiers()));
    assertTrue(Modifier.isPrivate(getDeclaredMethod("getTreeBasedOnTheme").getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredMethod("getTreeBasedOnTheme").getModifiers()));
    assertTrue(Modifier.isPrivate(getDeclaredMethod("getTreeBasedOnGroup").getModifiers()));
    assertTrue(Modifier.isStatic(getDeclaredMethod("getTreeBasedOnGroup").getModifiers()));
  }
}

package org.alice.stageide.modelresource;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import static org.junit.Assert.*;

public class ResourceKeyHierarchyTest {

  private static Method declaredMethod(Class<?> cls, String name, Class<?>... parameterTypes) throws Exception {
    Method method = cls.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return method;
  }

  @Test
  public void resourceKeyIsPublicAbstract() {
    int modifiers = ResourceKey.class.getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertTrue(Modifier.isAbstract(modifiers));
  }

  @Test
  public void instanceCreatorKeyExtendsResourceKey() {
    assertEquals(ResourceKey.class, InstanceCreatorKey.class.getSuperclass());
  }

  @Test
  public void classResourceKeyExtendsInstanceCreatorKey() {
    assertEquals(InstanceCreatorKey.class, ClassResourceKey.class.getSuperclass());
  }

  @Test
  public void enumConstantResourceKeyExtendsInstanceCreatorKey() {
    assertEquals(InstanceCreatorKey.class, EnumConstantResourceKey.class.getSuperclass());
  }

  @Test
  public void dynamicResourceKeyExtendsInstanceCreatorKey() {
    assertEquals(InstanceCreatorKey.class, DynamicResourceKey.class.getSuperclass());
  }

  @Test
  public void classResourceKeyIsFinal() {
    assertTrue(Modifier.isFinal(ClassResourceKey.class.getModifiers()));
  }

  @Test
  public void enumConstantResourceKeyIsFinal() {
    assertTrue(Modifier.isFinal(EnumConstantResourceKey.class.getModifiers()));
  }

  @Test
  public void dynamicResourceKeyIsConcrete() {
    assertFalse(Modifier.isAbstract(DynamicResourceKey.class.getModifiers()));
  }

  @Test
  public void resourceKeyDeclaresSearchTextMethod() throws Exception {
    assertEquals(String.class, declaredMethod(ResourceKey.class, "getSearchText").getReturnType());
  }

  @Test
  public void resourceKeyDeclaresInternalNameMethod() throws Exception {
    assertEquals(String.class, declaredMethod(ResourceKey.class, "getInternalName").getReturnType());
  }

  @Test
  public void resourceKeyDeclaresLocalizedNameMethod() throws Exception {
    assertEquals(String.class, declaredMethod(ResourceKey.class, "getLocalizedName").getReturnType());
  }

  @Test
  public void resourceKeyDeclaresLocalizedCreationTextMethod() throws Exception {
    assertEquals(String.class, declaredMethod(ResourceKey.class, "getLocalizedCreationText").getReturnType());
  }

  @Test
  public void resourceKeyDeclaresCreateInstanceCreationWithTypeCache() throws Exception {
    assertEquals("createInstanceCreation", declaredMethod(ResourceKey.class, "createInstanceCreation", Set.class).getName());
  }

  @Test
  public void resourceKeyDeclaresLeftClickOperationMethod() throws Exception {
    assertEquals("getLeftClickOperation", declaredMethod(ResourceKey.class, "getLeftClickOperation", ResourceNode.class, org.lgna.croquet.SingleSelectTreeState.class).getName());
  }

  @Test
  public void resourceKeyDeclaresDropOperationMethod() throws Exception {
    assertEquals("getDropOperation", declaredMethod(ResourceKey.class, "getDropOperation", ResourceNode.class, org.lgna.croquet.history.DragStep.class, org.lgna.croquet.DropSite.class).getName());
  }

  @Test
  public void resourceKeyDeclaresProtectedAppendRep() throws Exception {
    Method method = declaredMethod(ResourceKey.class, "appendRep", StringBuilder.class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void resourceKeyProvidesConcreteDefaultIsInterfaceMethod() throws Exception {
    Method method = declaredMethod(ResourceKey.class, "isInterface");
    assertFalse(Modifier.isAbstract(method.getModifiers()));
    assertEquals(boolean.class, method.getReturnType());
  }

  @Test
  public void resourceKeyDeclaresTagAccessors() throws Exception {
    assertEquals(String[].class, declaredMethod(ResourceKey.class, "getTags").getReturnType());
    assertEquals(String[].class, declaredMethod(ResourceKey.class, "getGroupTags").getReturnType());
    assertEquals(String[].class, declaredMethod(ResourceKey.class, "getThemeTags").getReturnType());
  }

  @Test
  public void resourceKeyDeclaresPublicToStringOverride() throws Exception {
    Method method = ResourceKey.class.getMethod("toString");
    assertEquals(String.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void instanceCreatorKeyDeclaresModelResourceClassAccessor() throws Exception {
    assertEquals(Class.class, declaredMethod(InstanceCreatorKey.class, "getModelResourceCls").getReturnType());
  }

  @Test
  public void instanceCreatorKeyDeclaresBoundingBoxAccessor() throws Exception {
    assertEquals(org.alice.math.immutable.AxisAlignedBox.class, declaredMethod(InstanceCreatorKey.class, "getBoundingBox").getReturnType());
  }

  @Test
  public void instanceCreatorKeyDeclaresPlaceOnGroundAccessor() throws Exception {
    assertEquals(boolean.class, declaredMethod(InstanceCreatorKey.class, "getPlaceOnGround").getReturnType());
  }

  @Test
  public void instanceCreatorKeyOverridesIsInstanceCreator() throws Exception {
    Method method = declaredMethod(InstanceCreatorKey.class, "isInstanceCreator");
    assertFalse(Modifier.isAbstract(method.getModifiers()));
    assertEquals(boolean.class, method.getReturnType());
  }

  @Test
  public void concreteResourceKeyTypesOverrideAppendRep() throws Exception {
    assertEquals(ClassResourceKey.class, declaredMethod(ClassResourceKey.class, "appendRep", StringBuilder.class).getDeclaringClass());
    assertEquals(EnumConstantResourceKey.class, declaredMethod(EnumConstantResourceKey.class, "appendRep", StringBuilder.class).getDeclaringClass());
    assertEquals(DynamicResourceKey.class, declaredMethod(DynamicResourceKey.class, "appendRep", StringBuilder.class).getDeclaringClass());
  }
}

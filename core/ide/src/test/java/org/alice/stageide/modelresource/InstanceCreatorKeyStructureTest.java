package org.alice.stageide.modelresource;

import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.story.resources.DynamicPropResource;
import org.lgna.story.resources.ModelResource;
import org.lgna.story.resources.fish.ArapaimaResource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

public class InstanceCreatorKeyStructureTest {

  private DynamicPropResource createResource() {
    ModelManifest manifest = new ModelManifest();
    manifest.parentClass = "Prop";
    manifest.description.name = "DynamicThing";
    manifest.description.tags = Arrays.asList("one");
    manifest.description.groupTags = Arrays.asList("group");
    manifest.description.themeTags = Arrays.asList("theme");
    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.name = "DEFAULT";
    manifest.models.add(variant);
    return new DynamicPropResource(manifest, variant);
  }

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(InstanceCreatorKey.class.getModifiers()));
  }

  @Test
  public void class_extendsResourceKey() {
    assertEquals(ResourceKey.class, InstanceCreatorKey.class.getSuperclass());
  }

  @Test
  public void getAbstractionTypeForResourceType_methodExists() throws Exception {
    Method method = InstanceCreatorKey.class.getDeclaredMethod("getAbstractionTypeForResourceType", AbstractType.class);
    assertNotNull(method);
  }

  @Test
  public void getAbstractionTypeForResourceType_methodIsStaticAndPackageVisible() throws Exception {
    Method method = InstanceCreatorKey.class.getDeclaredMethod("getAbstractionTypeForResourceType", AbstractType.class);
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertFalse(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void getAbstractionTypeForResourceType_methodReturnsJavaType() throws Exception {
    Method method = InstanceCreatorKey.class.getDeclaredMethod("getAbstractionTypeForResourceType", AbstractType.class);
    assertEquals(JavaType.class, method.getReturnType());
  }

  @Test
  public void getModelResourceCls_methodIsAbstract() throws Exception {
    Method method = InstanceCreatorKey.class.getDeclaredMethod("getModelResourceCls");
    assertTrue(Modifier.isAbstract(method.getModifiers()));
    assertEquals(Class.class, method.getReturnType());
  }

  @Test
  public void getBoundingBox_methodIsAbstract() throws Exception {
    Method method = InstanceCreatorKey.class.getDeclaredMethod("getBoundingBox");
    assertTrue(Modifier.isAbstract(method.getModifiers()));
    assertEquals(org.alice.math.immutable.AxisAlignedBox.class, method.getReturnType());
  }

  @Test
  public void getPlaceOnGround_methodIsAbstract() throws Exception {
    Method method = InstanceCreatorKey.class.getDeclaredMethod("getPlaceOnGround");
    assertTrue(Modifier.isAbstract(method.getModifiers()));
    assertEquals(boolean.class, method.getReturnType());
  }

  @Test
  public void mappingField_privateStatic_exists() throws Exception {
    Field field = InstanceCreatorKey.class.getDeclaredField("mapResourceTypeToAbstractionType");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
  }

  @Test
  public void classResourceKey_inheritedImplementation_reportsInstanceCreator() {
    assertTrue(new ClassResourceKey(ArapaimaResource.class).isInstanceCreator());
  }

  @Test
  public void enumConstantResourceKey_inheritedImplementation_reportsInstanceCreator() {
    assertTrue(new EnumConstantResourceKey(ArapaimaResource.DEFAULT).isInstanceCreator());
  }

  @Test
  public void dynamicResourceKey_inheritedImplementation_reportsInstanceCreator() {
    assertTrue(new DynamicResourceKey(createResource()).isInstanceCreator());
  }

  @Test
  public void subclasses_allExtendInstanceCreatorKey() {
    assertTrue(InstanceCreatorKey.class.isAssignableFrom(ClassResourceKey.class));
    assertTrue(InstanceCreatorKey.class.isAssignableFrom(EnumConstantResourceKey.class));
    assertTrue(InstanceCreatorKey.class.isAssignableFrom(DynamicResourceKey.class));
  }

  @Test
  public void dynamicResourceKey_getModelResourceCls_returnsConcreteDynamicType() {
    DynamicResourceKey key = new DynamicResourceKey(createResource());
    assertEquals(DynamicPropResource.class, key.getModelResourceCls());
  }

  @Test
  public void getModelResourceCls_genericReturnType_mentionsModelResourceBound() throws Exception {
    Method method = InstanceCreatorKey.class.getDeclaredMethod("getModelResourceCls");
    assertTrue(method.getGenericReturnType().getTypeName().contains(ModelResource.class.getSimpleName()));
  }

  @Test
  public void className_structure_matchesExpectedName() {
    assertEquals("InstanceCreatorKey", InstanceCreatorKey.class.getSimpleName());
  }
}

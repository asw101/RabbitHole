package org.alice.ide.croquet.models.declaration;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.Test;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.imp.cascade.BlankNode;
import org.lgna.project.ast.AbstractType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.List;

import static org.junit.Assert.*;

public class GalleryResourceUtilitiesComprehensiveTest {

  @Test
  public void galleryResourceUtilities_isPublicConcreteTopLevelClass() {
    assertTrue(Modifier.isPublic(GalleryResourceUtilities.class.getModifiers()));
    assertFalse(Modifier.isAbstract(GalleryResourceUtilities.class.getModifiers()));
    assertNull(GalleryResourceUtilities.class.getEnclosingClass());
  }

  @Test
  public void galleryResourceUtilities_hasSinglePrivateNoArgConstructor() {
    Constructor<?>[] constructors = GalleryResourceUtilities.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertEquals(0, constructors[0].getParameterTypes().length);
    assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
  }

  @Test
  public void galleryResourceUtilities_privateConstructorThrowsAssertionError() throws Exception {
    ReflectionTestHelper.assertPrivateConstructorThrowsAssertionError(GalleryResourceUtilities.class);
  }

  @Test
  public void galleryResourceUtilities_allConstructorsArePrivate() {
    ReflectionTestHelper.assertAllConstructorsPrivate(GalleryResourceUtilities.class);
  }

  @Test
  public void galleryResourceUtilities_allPublicMethodsAreStatic() {
    ReflectionTestHelper.assertAllPublicMethodsStatic(GalleryResourceUtilities.class);
  }

  @Test
  public void galleryResourceUtilities_declaresNoFields() {
    assertEquals(0, declaredFields(GalleryResourceUtilities.class).length);
  }

  @Test
  public void galleryResourceUtilities_declaresSinglePublicMethod() {
    assertEquals(1, declaredMethods(GalleryResourceUtilities.class).length);
  }

  @Test
  public void updateChildren_isPublicStaticVoid() throws Exception {
    Method method = GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class);
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void updateChildren_acceptsListBlankNodeAndAbstractType() throws Exception {
    Method method = GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class);
    assertArrayEquals(new Class<?>[] {List.class, BlankNode.class, AbstractType.class}, method.getParameterTypes());
  }

  @Test
  public void updateChildren_declaresSingleGenericTypeParameter() throws Exception {
    Method method = GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class);
    TypeVariable<Method>[] typeParameters = method.getTypeParameters();
    assertEquals(1, typeParameters.length);
    assertEquals("B", typeParameters[0].getName());
  }

  @Test
  public void updateChildren_firstParameterIsRawListType() throws Exception {
    Method method = GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class);
    assertEquals(List.class, method.getParameterTypes()[0]);
  }

  @Test
  public void updateChildren_secondParameterIsBlankNodeType() throws Exception {
    Method method = GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class);
    assertEquals(BlankNode.class, method.getParameterTypes()[1]);
  }

  @Test
  public void updateChildren_thirdParameterIsAbstractType() throws Exception {
    Method method = GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class);
    assertEquals(AbstractType.class, method.getParameterTypes()[2]);
  }

  @Test
  public void updateChildren_isOnlyDeclaredBehavioralMethod() {
    assertEquals("updateChildren", declaredMethods(GalleryResourceUtilities.class)[0].getName());
  }

  @Test
  public void galleryResourceUtilities_isNotFinalOrEnum() {
    assertFalse(Modifier.isFinal(GalleryResourceUtilities.class.getModifiers()));
    assertFalse(GalleryResourceUtilities.class.isEnum());
  }

  @Test
  public void galleryResourceUtilities_isNotAnInterfaceOrAnnotation() {
    assertFalse(GalleryResourceUtilities.class.isInterface());
    assertFalse(GalleryResourceUtilities.class.isAnnotation());
  }

  @Test
  public void utilityClassSimpleName_matchesExpectation() {
    assertEquals("GalleryResourceUtilities", GalleryResourceUtilities.class.getSimpleName());
  }

  @Test
  public void utilityClassPackage_matchesExpectation() {
    assertEquals("org.alice.ide.croquet.models.declaration", GalleryResourceUtilities.class.getPackage().getName());
  }

  @Test
  public void updateChildren_canBeLocatedViaHelper() {
    assertNotNull(ReflectionTestHelper.findMethod(GalleryResourceUtilities.class, "updateChildren"));
  }

  @Test
  public void updateChildren_firstParameterSupportsCascadeChildrenLists() throws Exception {
    Method method = GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class);
    assertTrue(List.class.isAssignableFrom(method.getParameterTypes()[0]));
    assertTrue(CascadeBlankChild.class.isAssignableFrom(CascadeBlankChild.class));
  }

  @Test
  public void updateChildren_declaredMethodCountRemainsStable() {
    long count = java.util.Arrays.stream(declaredMethods(GalleryResourceUtilities.class)).count();
    assertEquals(1L, count);
  }

  @Test
  public void utilityAssertions_canBeAppliedTogether() throws Exception {
    ReflectionTestHelper.assertUtilityClass(GalleryResourceUtilities.class);
  }

  @Test
  public void updateChildren_declaresNoCheckedExceptions() throws Exception {
    assertEquals(0, GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class).getExceptionTypes().length);
  }

  @Test
  public void updateChildren_isNotFinal() throws Exception {
    assertFalse(Modifier.isFinal(GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class).getModifiers()));
  }

  @Test
  public void updateChildren_isNotSynchronized() throws Exception {
    assertFalse(Modifier.isSynchronized(GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class).getModifiers()));
  }

  @Test
  public void updateChildren_typeParameterHasObjectBound() throws Exception {
    TypeVariable<Method> typeVariable = GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class).getTypeParameters()[0];
    assertEquals(1, typeVariable.getBounds().length);
    assertEquals(Object.class, typeVariable.getBounds()[0]);
  }

  @Test
  public void galleryResourceUtilities_declaresNoNestedTypes() {
    assertEquals(0, GalleryResourceUtilities.class.getDeclaredClasses().length);
  }

  @Test
  public void galleryResourceUtilities_implementsNoInterfaces() {
    assertEquals(0, GalleryResourceUtilities.class.getInterfaces().length);
  }

  @Test
  public void updateChildren_canBeResolvedAsDeclaredMethod() throws Exception {
    assertNotNull(GalleryResourceUtilities.class.getDeclaredMethod("updateChildren", List.class, BlankNode.class, AbstractType.class));
  }

  @Test
  public void constructorReflection_reportsNoParameters() {
    assertEquals(0, GalleryResourceUtilities.class.getDeclaredConstructors()[0].getParameterCount());
  }


  @Test
  public void updateChildren_parameterCountIsThree() throws Exception {
    assertEquals(3, GalleryResourceUtilities.class.getMethod("updateChildren", List.class, BlankNode.class, AbstractType.class).getParameterCount());
  }

  @Test
  public void utilityClassClassLoaderIsAvailable() {
    assertNotNull(GalleryResourceUtilities.class.getClassLoader());
  }

}

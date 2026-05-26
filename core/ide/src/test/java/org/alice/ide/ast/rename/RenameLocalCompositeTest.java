package org.alice.ide.ast.rename;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserLocal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.Assert.*;

public class RenameLocalCompositeTest {

  private UserLocal createDeclaration(String name) {
    return new UserLocal(name, JavaType.STRING_TYPE, false);
  }

  @SuppressWarnings("unchecked")
  private Map<Object, Object> getBackingMap() throws Exception {
    Field field = RenameLocalComposite.class.getDeclaredField("map");
    field.setAccessible(true);
    return (Map<Object, Object>) field.get(null);
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(RenameLocalComposite.class.getModifiers()));
  }

  @Test
  public void classIsConcrete() {
    assertFalse(Modifier.isAbstract(RenameLocalComposite.class.getModifiers()));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(RenameLocalComposite.class.getModifiers()));
  }

  @Test
  public void classIsNotInterfaceOrEnum() {
    assertFalse(RenameLocalComposite.class.isInterface());
    assertFalse(RenameLocalComposite.class.isEnum());
  }

  @Test
  public void extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameLocalComposite.class));
  }

  @Test
  public void extendsRenameComposite() {
    assertTrue(RenameComposite.class.isAssignableFrom(RenameLocalComposite.class));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("RenameLocalComposite", RenameLocalComposite.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.rename", RenameLocalComposite.class.getPackage().getName());
  }

  @Test
  public void mapFieldExists() throws Exception {
    assertNotNull(RenameLocalComposite.class.getDeclaredField("map"));
  }

  @Test
  public void mapFieldIsPrivateStatic() throws Exception {
    Field field = RenameLocalComposite.class.getDeclaredField("map");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
  }

  @Test
  public void mapFieldImplementsMap() throws Exception {
    Field field = RenameLocalComposite.class.getDeclaredField("map");
    assertTrue(Map.class.isAssignableFrom(field.getType()));
  }

  @Test
  public void hasSinglePrivateConstructor() {
    Constructor<?>[] constructors = RenameLocalComposite.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
  }

  @Test
  public void constructorAcceptsDeclarationType() throws Exception {
    assertNotNull(RenameLocalComposite.class.getDeclaredConstructor(UserLocal.class));
  }

  @Test
  public void constructorHasSingleParameter() throws Exception {
    assertEquals(1, RenameLocalComposite.class.getDeclaredConstructor(UserLocal.class).getParameterCount());
  }

  @Test
  public void getInstanceMethodExists() throws Exception {
    assertNotNull(RenameLocalComposite.class.getDeclaredMethod("getInstance", UserLocal.class));
  }

  @Test
  public void getInstanceMethodIsPublicStaticSynchronized() throws Exception {
    Method method = RenameLocalComposite.class.getDeclaredMethod("getInstance", UserLocal.class);
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertTrue(Modifier.isSynchronized(method.getModifiers()));
  }

  @Test
  public void getInstanceMethodReturnsConcreteType() throws Exception {
    Method method = RenameLocalComposite.class.getDeclaredMethod("getInstance", UserLocal.class);
    assertEquals(RenameLocalComposite.class, method.getReturnType());
  }

  @Test
  public void getInstanceMethodHasSingleParameter() throws Exception {
    Method method = RenameLocalComposite.class.getDeclaredMethod("getInstance", UserLocal.class);
    assertEquals(1, method.getParameterCount());
  }

  @Test
  public void getInstanceReturnsConcreteComposite() {
    Object composite = RenameLocalComposite.getInstance(createDeclaration("alpha"));
    assertNotNull(composite);
    assertSame(RenameLocalComposite.class, composite.getClass());
  }

  @Test
  public void getInstanceCachesSameDeclaration() {
    UserLocal declaration = createDeclaration("beta");
    assertSame(RenameLocalComposite.getInstance(declaration), RenameLocalComposite.getInstance(declaration));
  }

  @Test
  public void getInstanceUsesDifferentInstancesForDifferentDeclarations() {
    assertNotSame(RenameLocalComposite.getInstance(createDeclaration("one")), RenameLocalComposite.getInstance(createDeclaration("two")));
  }

  @Test
  public void cachedMapContainsDeclarationKey() throws Exception {
    UserLocal declaration = createDeclaration("gamma");
    RenameLocalComposite.getInstance(declaration);
    assertTrue(getBackingMap().containsKey(declaration));
  }

  @Test
  public void cachedMapStoresReturnedCompositeValue() throws Exception {
    UserLocal declaration = createDeclaration("delta");
    RenameLocalComposite composite = RenameLocalComposite.getInstance(declaration);
    assertSame(composite, getBackingMap().get(declaration));
  }

  @Test
  public void inheritedNameStateIsAvailable() {
    assertNotNull(RenameLocalComposite.getInstance(createDeclaration("epsilon")).getNameState());
  }

  @Test
  public void inheritedNameStateStartsEmptyBeforeDialog() {
    assertEquals("", RenameLocalComposite.getInstance(createDeclaration("zeta")).getNameState().getValue());
  }

  @Test
  public void lookupAfterDeclarationRenameReturnsSameCachedComposite() {
    UserLocal declaration = createDeclaration("eta");
    RenameLocalComposite first = RenameLocalComposite.getInstance(declaration);
    declaration.name.setValue("etaRenamed");
    RenameLocalComposite second = RenameLocalComposite.getInstance(declaration);
    assertSame(first, second);
  }

  @Test
  public void declaredFieldsIncludeMapField() {
    assertTrue(declaredFields(RenameLocalComposite.class).length >= 1);
  }

  @Test
  public void declaredMethodCountIsOne() {
    assertEquals(1, declaredMethods(RenameLocalComposite.class).length);
  }

  @Test
  public void declaredConstructorCountIsOne() {
    assertEquals(1, RenameLocalComposite.class.getDeclaredConstructors().length);
  }


  @Test
  public void backingMapIsAccessibleViaReflection() throws Exception {
    assertNotNull(getBackingMap());
  }

  @Test
  public void cachedCompositeRemainsMappedAfterRepeatedLookup() throws Exception {
    Object declaration = createDeclaration("theta");
    Object composite = RenameLocalComposite.getInstance((UserLocal) declaration);
    RenameLocalComposite.getInstance((UserLocal) declaration);
    assertSame(composite, getBackingMap().get(declaration));
  }

}

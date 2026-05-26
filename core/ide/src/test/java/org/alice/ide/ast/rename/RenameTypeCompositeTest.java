package org.alice.ide.ast.rename;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.Assert.*;

public class RenameTypeCompositeTest {

  private NamedUserType createDeclaration(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.OBJECT_TYPE);
    return type;
  }

  @SuppressWarnings("unchecked")
  private Map<Object, Object> getBackingMap() throws Exception {
    Field field = RenameTypeComposite.class.getDeclaredField("map");
    field.setAccessible(true);
    return (Map<Object, Object>) field.get(null);
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(RenameTypeComposite.class.getModifiers()));
  }

  @Test
  public void classIsConcrete() {
    assertFalse(Modifier.isAbstract(RenameTypeComposite.class.getModifiers()));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(RenameTypeComposite.class.getModifiers()));
  }

  @Test
  public void classIsNotInterfaceOrEnum() {
    assertFalse(RenameTypeComposite.class.isInterface());
    assertFalse(RenameTypeComposite.class.isEnum());
  }

  @Test
  public void extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameTypeComposite.class));
  }

  @Test
  public void extendsRenameComposite() {
    assertTrue(RenameComposite.class.isAssignableFrom(RenameTypeComposite.class));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("RenameTypeComposite", RenameTypeComposite.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.rename", RenameTypeComposite.class.getPackage().getName());
  }

  @Test
  public void mapFieldExists() throws Exception {
    assertNotNull(RenameTypeComposite.class.getDeclaredField("map"));
  }

  @Test
  public void mapFieldIsPrivateStatic() throws Exception {
    Field field = RenameTypeComposite.class.getDeclaredField("map");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
  }

  @Test
  public void mapFieldImplementsMap() throws Exception {
    Field field = RenameTypeComposite.class.getDeclaredField("map");
    assertTrue(Map.class.isAssignableFrom(field.getType()));
  }

  @Test
  public void hasSinglePrivateConstructor() {
    Constructor<?>[] constructors = RenameTypeComposite.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
  }

  @Test
  public void constructorAcceptsDeclarationType() throws Exception {
    assertNotNull(RenameTypeComposite.class.getDeclaredConstructor(NamedUserType.class));
  }

  @Test
  public void constructorHasSingleParameter() throws Exception {
    assertEquals(1, RenameTypeComposite.class.getDeclaredConstructor(NamedUserType.class).getParameterCount());
  }

  @Test
  public void getInstanceMethodExists() throws Exception {
    assertNotNull(RenameTypeComposite.class.getDeclaredMethod("getInstance", NamedUserType.class));
  }

  @Test
  public void getInstanceMethodIsPublicStaticSynchronized() throws Exception {
    Method method = RenameTypeComposite.class.getDeclaredMethod("getInstance", NamedUserType.class);
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertTrue(Modifier.isSynchronized(method.getModifiers()));
  }

  @Test
  public void getInstanceMethodReturnsConcreteType() throws Exception {
    Method method = RenameTypeComposite.class.getDeclaredMethod("getInstance", NamedUserType.class);
    assertEquals(RenameTypeComposite.class, method.getReturnType());
  }

  @Test
  public void getInstanceMethodHasSingleParameter() throws Exception {
    Method method = RenameTypeComposite.class.getDeclaredMethod("getInstance", NamedUserType.class);
    assertEquals(1, method.getParameterCount());
  }

  @Test
  public void getInstanceReturnsConcreteComposite() {
    Object composite = RenameTypeComposite.getInstance(createDeclaration("alpha"));
    assertNotNull(composite);
    assertSame(RenameTypeComposite.class, composite.getClass());
  }

  @Test
  public void getInstanceCachesSameDeclaration() {
    NamedUserType declaration = createDeclaration("beta");
    assertSame(RenameTypeComposite.getInstance(declaration), RenameTypeComposite.getInstance(declaration));
  }

  @Test
  public void getInstanceUsesDifferentInstancesForDifferentDeclarations() {
    assertNotSame(RenameTypeComposite.getInstance(createDeclaration("one")), RenameTypeComposite.getInstance(createDeclaration("two")));
  }

  @Test
  public void cachedMapContainsDeclarationKey() throws Exception {
    NamedUserType declaration = createDeclaration("gamma");
    RenameTypeComposite.getInstance(declaration);
    assertTrue(getBackingMap().containsKey(declaration));
  }

  @Test
  public void cachedMapStoresReturnedCompositeValue() throws Exception {
    NamedUserType declaration = createDeclaration("delta");
    RenameTypeComposite composite = RenameTypeComposite.getInstance(declaration);
    assertSame(composite, getBackingMap().get(declaration));
  }

  @Test
  public void inheritedNameStateIsAvailable() {
    assertNotNull(RenameTypeComposite.getInstance(createDeclaration("epsilon")).getNameState());
  }

  @Test
  public void inheritedNameStateStartsEmptyBeforeDialog() {
    assertEquals("", RenameTypeComposite.getInstance(createDeclaration("zeta")).getNameState().getValue());
  }

  @Test
  public void lookupAfterDeclarationRenameReturnsSameCachedComposite() {
    NamedUserType declaration = createDeclaration("eta");
    RenameTypeComposite first = RenameTypeComposite.getInstance(declaration);
    declaration.name.setValue("etaRenamed");
    RenameTypeComposite second = RenameTypeComposite.getInstance(declaration);
    assertSame(first, second);
  }

  @Test
  public void declaredFieldsIncludeMapField() {
    assertTrue(declaredFields(RenameTypeComposite.class).length >= 1);
  }

  @Test
  public void declaredMethodCountIsOne() {
    assertEquals(1, declaredMethods(RenameTypeComposite.class).length);
  }

  @Test
  public void declaredConstructorCountIsOne() {
    assertEquals(1, RenameTypeComposite.class.getDeclaredConstructors().length);
  }


  @Test
  public void backingMapIsAccessibleViaReflection() throws Exception {
    assertNotNull(getBackingMap());
  }

  @Test
  public void cachedCompositeRemainsMappedAfterRepeatedLookup() throws Exception {
    Object declaration = createDeclaration("theta");
    Object composite = RenameTypeComposite.getInstance((NamedUserType) declaration);
    RenameTypeComposite.getInstance((NamedUserType) declaration);
    assertSame(composite, getBackingMap().get(declaration));
  }

}

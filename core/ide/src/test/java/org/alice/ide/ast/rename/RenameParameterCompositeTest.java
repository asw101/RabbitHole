package org.alice.ide.ast.rename;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.junit.Assert.*;

public class RenameParameterCompositeTest {

  private UserParameter createDeclaration(String name) {
    return new UserParameter(name, JavaType.STRING_TYPE);
  }

  @SuppressWarnings("unchecked")
  private Map<Object, Object> getBackingMap() throws Exception {
    Field field = RenameParameterComposite.class.getDeclaredField("map");
    field.setAccessible(true);
    return (Map<Object, Object>) field.get(null);
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(RenameParameterComposite.class.getModifiers()));
  }

  @Test
  public void classIsConcrete() {
    assertFalse(Modifier.isAbstract(RenameParameterComposite.class.getModifiers()));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(RenameParameterComposite.class.getModifiers()));
  }

  @Test
  public void classIsNotInterfaceOrEnum() {
    assertFalse(RenameParameterComposite.class.isInterface());
    assertFalse(RenameParameterComposite.class.isEnum());
  }

  @Test
  public void extendsRenameDeclarationComposite() {
    assertTrue(RenameDeclarationComposite.class.isAssignableFrom(RenameParameterComposite.class));
  }

  @Test
  public void extendsRenameComposite() {
    assertTrue(RenameComposite.class.isAssignableFrom(RenameParameterComposite.class));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("RenameParameterComposite", RenameParameterComposite.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.rename", RenameParameterComposite.class.getPackage().getName());
  }

  @Test
  public void mapFieldExists() throws Exception {
    assertNotNull(RenameParameterComposite.class.getDeclaredField("map"));
  }

  @Test
  public void mapFieldIsPrivateStatic() throws Exception {
    Field field = RenameParameterComposite.class.getDeclaredField("map");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
  }

  @Test
  public void mapFieldImplementsMap() throws Exception {
    Field field = RenameParameterComposite.class.getDeclaredField("map");
    assertTrue(Map.class.isAssignableFrom(field.getType()));
  }

  @Test
  public void hasSinglePrivateConstructor() {
    Constructor<?>[] constructors = RenameParameterComposite.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
  }

  @Test
  public void constructorAcceptsDeclarationType() throws Exception {
    assertNotNull(RenameParameterComposite.class.getDeclaredConstructor(UserParameter.class));
  }

  @Test
  public void constructorHasSingleParameter() throws Exception {
    assertEquals(1, RenameParameterComposite.class.getDeclaredConstructor(UserParameter.class).getParameterCount());
  }

  @Test
  public void getInstanceMethodExists() throws Exception {
    assertNotNull(RenameParameterComposite.class.getDeclaredMethod("getInstance", UserParameter.class));
  }

  @Test
  public void getInstanceMethodIsPublicStaticSynchronized() throws Exception {
    Method method = RenameParameterComposite.class.getDeclaredMethod("getInstance", UserParameter.class);
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertTrue(Modifier.isSynchronized(method.getModifiers()));
  }

  @Test
  public void getInstanceMethodReturnsConcreteType() throws Exception {
    Method method = RenameParameterComposite.class.getDeclaredMethod("getInstance", UserParameter.class);
    assertEquals(RenameParameterComposite.class, method.getReturnType());
  }

  @Test
  public void getInstanceMethodHasSingleParameter() throws Exception {
    Method method = RenameParameterComposite.class.getDeclaredMethod("getInstance", UserParameter.class);
    assertEquals(1, method.getParameterCount());
  }

  @Test
  public void getInstanceReturnsConcreteComposite() {
    Object composite = RenameParameterComposite.getInstance(createDeclaration("alpha"));
    assertNotNull(composite);
    assertSame(RenameParameterComposite.class, composite.getClass());
  }

  @Test
  public void getInstanceCachesSameDeclaration() {
    UserParameter declaration = createDeclaration("beta");
    assertSame(RenameParameterComposite.getInstance(declaration), RenameParameterComposite.getInstance(declaration));
  }

  @Test
  public void getInstanceUsesDifferentInstancesForDifferentDeclarations() {
    assertNotSame(RenameParameterComposite.getInstance(createDeclaration("one")), RenameParameterComposite.getInstance(createDeclaration("two")));
  }

  @Test
  public void cachedMapContainsDeclarationKey() throws Exception {
    UserParameter declaration = createDeclaration("gamma");
    RenameParameterComposite.getInstance(declaration);
    assertTrue(getBackingMap().containsKey(declaration));
  }

  @Test
  public void cachedMapStoresReturnedCompositeValue() throws Exception {
    UserParameter declaration = createDeclaration("delta");
    RenameParameterComposite composite = RenameParameterComposite.getInstance(declaration);
    assertSame(composite, getBackingMap().get(declaration));
  }

  @Test
  public void inheritedNameStateIsAvailable() {
    assertNotNull(RenameParameterComposite.getInstance(createDeclaration("epsilon")).getNameState());
  }

  @Test
  public void inheritedNameStateStartsEmptyBeforeDialog() {
    assertEquals("", RenameParameterComposite.getInstance(createDeclaration("zeta")).getNameState().getValue());
  }

  @Test
  public void lookupAfterDeclarationRenameReturnsSameCachedComposite() {
    UserParameter declaration = createDeclaration("eta");
    RenameParameterComposite first = RenameParameterComposite.getInstance(declaration);
    declaration.name.setValue("etaRenamed");
    RenameParameterComposite second = RenameParameterComposite.getInstance(declaration);
    assertSame(first, second);
  }

  @Test
  public void declaredFieldsIncludeMapField() {
    assertTrue(RenameParameterComposite.class.getDeclaredFields().length >= 1);
  }

  @Test
  public void declaredMethodCountIsOne() {
    assertEquals(1, RenameParameterComposite.class.getDeclaredMethods().length);
  }

  @Test
  public void declaredConstructorCountIsOne() {
    assertEquals(1, RenameParameterComposite.class.getDeclaredConstructors().length);
  }


  @Test
  public void backingMapIsAccessibleViaReflection() throws Exception {
    assertNotNull(getBackingMap());
  }

  @Test
  public void cachedCompositeRemainsMappedAfterRepeatedLookup() throws Exception {
    Object declaration = createDeclaration("theta");
    Object composite = RenameParameterComposite.getInstance((UserParameter) declaration);
    RenameParameterComposite.getInstance((UserParameter) declaration);
    assertSame(composite, getBackingMap().get(declaration));
  }

}

package org.alice.ide.ast.declaration;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import edu.cmu.cs.dennisc.java.util.InitializingIfAbsentMap;
import org.junit.Test;
import org.lgna.croquet.StringState;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class DeclarationNameStateTest {

  private UserField createField(String name) {
    return new UserField(name, JavaType.STRING_TYPE, new NullLiteral());
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(DeclarationNameState.class.getModifiers()));
  }

  @Test
  public void classIsConcrete() {
    assertFalse(Modifier.isAbstract(DeclarationNameState.class.getModifiers()));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(DeclarationNameState.class.getModifiers()));
  }

  @Test
  public void classIsNotInterfaceEnumOrAnnotation() {
    assertFalse(DeclarationNameState.class.isInterface());
    assertFalse(DeclarationNameState.class.isEnum());
    assertFalse(DeclarationNameState.class.isAnnotation());
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("DeclarationNameState", DeclarationNameState.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.declaration", DeclarationNameState.class.getPackage().getName());
  }

  @Test
  public void extendsStringState() {
    assertTrue(StringState.class.isAssignableFrom(DeclarationNameState.class));
  }

  @Test
  public void mapFieldExists() throws Exception {
    assertNotNull(DeclarationNameState.class.getDeclaredField("map"));
  }

  @Test
  public void mapFieldIsPrivateStatic() throws Exception {
    Field field = DeclarationNameState.class.getDeclaredField("map");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
  }

  @Test
  public void mapFieldIsNotFinal() throws Exception {
    Field field = DeclarationNameState.class.getDeclaredField("map");
    assertFalse(Modifier.isFinal(field.getModifiers()));
  }

  @Test
  public void mapFieldUsesInitializingIfAbsentMap() throws Exception {
    Field field = DeclarationNameState.class.getDeclaredField("map");
    assertEquals(InitializingIfAbsentMap.class, field.getType());
  }

  @Test
  public void hasSinglePrivateConstructor() {
    Constructor<?>[] constructors = DeclarationNameState.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
  }

  @Test
  public void constructorAcceptsAbstractDeclaration() throws Exception {
    assertNotNull(DeclarationNameState.class.getDeclaredConstructor(AbstractDeclaration.class));
  }

  @Test
  public void constructorHasSingleParameter() throws Exception {
    assertEquals(1, DeclarationNameState.class.getDeclaredConstructor(AbstractDeclaration.class).getParameterCount());
  }

  @Test
  public void getInstanceMethodExists() throws Exception {
    assertNotNull(DeclarationNameState.class.getDeclaredMethod("getInstance", AbstractDeclaration.class));
  }

  @Test
  public void getInstanceMethodIsPublicStatic() throws Exception {
    Method method = DeclarationNameState.class.getDeclaredMethod("getInstance", AbstractDeclaration.class);
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
  }

  @Test
  public void getInstanceMethodReturnsDeclarationNameState() throws Exception {
    Method method = DeclarationNameState.class.getDeclaredMethod("getInstance", AbstractDeclaration.class);
    assertEquals(DeclarationNameState.class, method.getReturnType());
  }

  @Test
  public void getInstanceMethodHasSingleParameter() throws Exception {
    Method method = DeclarationNameState.class.getDeclaredMethod("getInstance", AbstractDeclaration.class);
    assertEquals(1, method.getParameterCount());
  }

  @Test
  public void getInstanceReturnsNonNullForFieldDeclaration() {
    assertNotNull(DeclarationNameState.getInstance(createField("speed")));
  }

  @Test
  public void getInstanceReturnsExactClass() {
    assertSame(DeclarationNameState.class, DeclarationNameState.getInstance(createField("power")).getClass());
  }

  @Test
  public void getInstanceCachesSameField() {
    UserField field = createField("distance");
    assertSame(DeclarationNameState.getInstance(field), DeclarationNameState.getInstance(field));
  }

  @Test
  public void getInstanceReturnsDifferentObjectsForDifferentFields() {
    assertNotSame(DeclarationNameState.getInstance(createField("left")), DeclarationNameState.getInstance(createField("right")));
  }

  @Test
  public void cachedStateRetainsInitialDeclarationName() {
    UserField field = createField("initialName");
    DeclarationNameState state = DeclarationNameState.getInstance(field);
    assertEquals("initialName", state.getValue());
  }

  @Test
  public void inheritedGetValueMethodIsAvailable() throws Exception {
    assertNotNull(StringState.class.getMethod("getValue"));
  }

  @Test
  public void inheritedSetValueTransactionlesslyMethodExistsSomewhereInHierarchy() {
    boolean found = false;
    for (Method method : DeclarationNameState.class.getMethods()) {
      if ("setValueTransactionlessly".equals(method.getName())) {
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test
  public void lookupAfterDeclarationRenameReturnsSameCachedInstance() {
    UserField field = createField("alpha");
    DeclarationNameState first = DeclarationNameState.getInstance(field);
    field.name.setValue("beta");
    assertSame(first, DeclarationNameState.getInstance(field));
  }

  @Test
  public void freshFieldUsesCurrentDeclarationName() {
    UserField field = createField("createdLater");
    assertEquals("createdLater", DeclarationNameState.getInstance(field).getValue());
  }

  @Test
  public void declaredMethodCountIsOne() {
    assertEquals(1, declaredMethods(DeclarationNameState.class).length);
  }

  @Test
  public void declaredFieldCountIsOne() {
    assertEquals(1, declaredFields(DeclarationNameState.class).length);
  }


  @Test
  public void getInstanceProducesStringStateSubclass() {
    assertTrue(DeclarationNameState.getInstance(createField("subclassCheck")) instanceof StringState);
  }

  @Test
  public void stateCanBeUpdatedTransactionlessly() {
    DeclarationNameState state = DeclarationNameState.getInstance(createField("beforeUpdate"));
    state.setValueTransactionlessly("afterUpdate");
    assertEquals("afterUpdate", state.getValue());
  }

  @Test
  public void constructorParameterTypeNameMatchesAbstractDeclaration() throws Exception {
    assertEquals("org.lgna.project.ast.AbstractDeclaration", DeclarationNameState.class.getDeclaredConstructor(AbstractDeclaration.class).getParameterTypes()[0].getName());
  }

}

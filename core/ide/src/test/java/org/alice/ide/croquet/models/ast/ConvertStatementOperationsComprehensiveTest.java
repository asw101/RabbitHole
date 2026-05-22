package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import org.lgna.croquet.ActionOperation;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.AbstractStatementWithBody;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.DoTogether;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class ConvertStatementOperationsComprehensiveTest {

  @Test
  public void convertStatementWithBodyOperation_isPublic() {
    assertTrue(Modifier.isPublic(ConvertStatementWithBodyOperation.class.getModifiers()));
  }

  @Test
  public void convertStatementWithBodyOperation_isAbstract() {
    assertTrue(Modifier.isAbstract(ConvertStatementWithBodyOperation.class.getModifiers()));
  }

  @Test
  public void convertStatementWithBodyOperation_extendsActionOperation() {
    assertTrue(ActionOperation.class.isAssignableFrom(ConvertStatementWithBodyOperation.class));
  }

  @Test
  public void convertStatementWithBodyOperation_declaresOriginalField() throws Exception {
    Field field = ConvertStatementWithBodyOperation.class.getDeclaredField("original");
    assertEquals(AbstractStatementWithBody.class, field.getType());
    assertTrue(Modifier.isPrivate(field.getModifiers()));
  }

  @Test
  public void convertStatementWithBodyOperation_constructorIsPublic() {
    Constructor<?> constructor = ConvertStatementWithBodyOperation.class.getConstructors()[0];
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void convertStatementWithBodyOperation_constructorParametersMatchSource() throws Exception {
    Constructor<ConvertStatementWithBodyOperation> constructor =
        ConvertStatementWithBodyOperation.class.getConstructor(UUID.class, AbstractStatementWithBody.class);
    assertArrayEquals(new Class<?>[] {UUID.class, AbstractStatementWithBody.class}, constructor.getParameterTypes());
  }

  @Test
  public void convertStatementWithBodyOperation_hasPublicGetOriginal() throws Exception {
    Method method = ConvertStatementWithBodyOperation.class.getMethod("getOriginal");
    assertEquals(AbstractStatementWithBody.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void convertStatementWithBodyOperation_hasProtectedAbstractCreateReplacement() throws Exception {
    Method method = ConvertStatementWithBodyOperation.class.getDeclaredMethod("createReplacement");
    assertEquals(AbstractStatementWithBody.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void convertStatementWithBodyOperation_hasProtectedPerformMethod() throws Exception {
    Method method = ConvertStatementWithBodyOperation.class.getDeclaredMethod("perform", UserActivity.class);
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void convertDoInOrderToDoTogether_extendsBaseType() {
    assertTrue(ConvertStatementWithBodyOperation.class.isAssignableFrom(ConvertDoInOrderToDoTogetherOperation.class));
  }

  @Test
  public void convertDoTogetherToDoInOrder_extendsBaseType() {
    assertTrue(ConvertStatementWithBodyOperation.class.isAssignableFrom(ConvertDoTogetherToDoInOrderOperation.class));
  }

  @Test
  public void concreteConvertTypes_arePublicAndConcrete() {
    assertTrue(Modifier.isPublic(ConvertDoInOrderToDoTogetherOperation.class.getModifiers()));
    assertFalse(Modifier.isAbstract(ConvertDoInOrderToDoTogetherOperation.class.getModifiers()));
    assertTrue(Modifier.isPublic(ConvertDoTogetherToDoInOrderOperation.class.getModifiers()));
    assertFalse(Modifier.isAbstract(ConvertDoTogetherToDoInOrderOperation.class.getModifiers()));
  }

  @Test
  public void convertDoInOrderToDoTogether_hasPublicStaticSynchronizedGetInstance() throws Exception {
    Method method = ConvertDoInOrderToDoTogetherOperation.class.getMethod("getInstance", DoInOrder.class);
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertTrue(Modifier.isSynchronized(method.getModifiers()));
  }

  @Test
  public void convertDoTogetherToDoInOrder_hasPublicStaticSynchronizedGetInstance() throws Exception {
    Method method = ConvertDoTogetherToDoInOrderOperation.class.getMethod("getInstance", DoTogether.class);
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertTrue(Modifier.isSynchronized(method.getModifiers()));
  }

  @Test
  public void getInstanceCachesDoInOrderOperationsPerOriginal() {
    DoInOrder original = new DoInOrder();
    assertSame(
        ConvertDoInOrderToDoTogetherOperation.getInstance(original),
        ConvertDoInOrderToDoTogetherOperation.getInstance(original));
  }

  @Test
  public void getInstanceCachesDoTogetherOperationsPerOriginal() {
    DoTogether original = new DoTogether();
    assertSame(
        ConvertDoTogetherToDoInOrderOperation.getInstance(original),
        ConvertDoTogetherToDoInOrderOperation.getInstance(original));
  }

  @Test
  public void getInstanceReturnsDifferentOperationForDifferentDoInOrders() {
    assertNotSame(
        ConvertDoInOrderToDoTogetherOperation.getInstance(new DoInOrder()),
        ConvertDoInOrderToDoTogetherOperation.getInstance(new DoInOrder()));
  }

  @Test
  public void getInstanceReturnsDifferentOperationForDifferentDoTogethers() {
    assertNotSame(
        ConvertDoTogetherToDoInOrderOperation.getInstance(new DoTogether()),
        ConvertDoTogetherToDoInOrderOperation.getInstance(new DoTogether()));
  }

  @Test
  public void getOriginalReturnsSuppliedDoInOrder() {
    DoInOrder original = new DoInOrder();
    ConvertDoInOrderToDoTogetherOperation operation = ConvertDoInOrderToDoTogetherOperation.getInstance(original);
    assertSame(original, operation.getOriginal());
  }

  @Test
  public void getOriginalReturnsSuppliedDoTogether() {
    DoTogether original = new DoTogether();
    ConvertDoTogetherToDoInOrderOperation operation = ConvertDoTogetherToDoInOrderOperation.getInstance(original);
    assertSame(original, operation.getOriginal());
  }

  @Test
  public void createReplacementForDoInOrderProducesDoTogether() {
    ConvertDoInOrderToDoTogetherOperation operation =
        ConvertDoInOrderToDoTogetherOperation.getInstance(new DoInOrder());
    AbstractStatementWithBody replacement = operation.createReplacement();
    assertTrue(replacement instanceof DoTogether);
  }

  @Test
  public void createReplacementForDoTogetherProducesDoInOrder() {
    ConvertDoTogetherToDoInOrderOperation operation =
        ConvertDoTogetherToDoInOrderOperation.getInstance(new DoTogether());
    AbstractStatementWithBody replacement = operation.createReplacement();
    assertTrue(replacement instanceof DoInOrder);
  }

  @Test
  public void createReplacementCreatesNewInstanceEachTimeForDoInOrder() {
    ConvertDoInOrderToDoTogetherOperation operation =
        ConvertDoInOrderToDoTogetherOperation.getInstance(new DoInOrder());
    assertNotSame(operation.createReplacement(), operation.createReplacement());
  }

  @Test
  public void createReplacementCreatesNewInstanceEachTimeForDoTogether() {
    ConvertDoTogetherToDoInOrderOperation operation =
        ConvertDoTogetherToDoInOrderOperation.getInstance(new DoTogether());
    assertNotSame(operation.createReplacement(), operation.createReplacement());
  }

  @Test
  public void concreteConstructorsArePrivate() {
    assertEquals(1, ConvertDoInOrderToDoTogetherOperation.class.getDeclaredConstructors().length);
    assertEquals(1, ConvertDoTogetherToDoInOrderOperation.class.getDeclaredConstructors().length);
    assertTrue(Modifier.isPrivate(ConvertDoInOrderToDoTogetherOperation.class.getDeclaredConstructors()[0].getModifiers()));
    assertTrue(Modifier.isPrivate(ConvertDoTogetherToDoInOrderOperation.class.getDeclaredConstructors()[0].getModifiers()));
  }

  @Test
  public void concreteTypesDeclarePrivateStaticMapFields() throws Exception {
    Field first = ConvertDoInOrderToDoTogetherOperation.class.getDeclaredField("map");
    Field second = ConvertDoTogetherToDoInOrderOperation.class.getDeclaredField("map");
    assertTrue(Modifier.isPrivate(first.getModifiers()));
    assertTrue(Modifier.isStatic(first.getModifiers()));
    assertTrue(Modifier.isPrivate(second.getModifiers()));
    assertTrue(Modifier.isStatic(second.getModifiers()));
  }

  @Test
  public void createReplacementReturnTypesRemainAbstractStatementWithBody() throws Exception {
    assertEquals(AbstractStatementWithBody.class,
        ConvertDoInOrderToDoTogetherOperation.class.getDeclaredMethod("createReplacement").getReturnType());
    assertEquals(AbstractStatementWithBody.class,
        ConvertDoTogetherToDoInOrderOperation.class.getDeclaredMethod("createReplacement").getReturnType());
  }
}

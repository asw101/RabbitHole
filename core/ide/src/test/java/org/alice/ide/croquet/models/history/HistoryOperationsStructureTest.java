package org.alice.ide.croquet.models.history;

import org.junit.Test;
import org.lgna.croquet.ActionOperation;
import org.lgna.croquet.DocumentFrame;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.undo.UndoHistory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class HistoryOperationsStructureTest {

  private static void assertSingletonShape(Class<?> type) throws Exception {
    Class<?> holder = Class.forName(type.getName() + "$SingletonHolder", false, type.getClassLoader());
    assertTrue("SingletonHolder must be private", Modifier.isPrivate(holder.getModifiers()));

    Method getInstance = type.getMethod("getInstance");
    assertTrue("getInstance must be public", Modifier.isPublic(getInstance.getModifiers()));
    assertTrue("getInstance must be static", Modifier.isStatic(getInstance.getModifiers()));
    assertEquals("getInstance must return declaring type", type, getInstance.getReturnType());

    Constructor<?> constructor = type.getDeclaredConstructor();
    assertTrue("singleton constructor must be private", Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void historyOperation_whenReflected_isPublicAbstract() {
    assertTrue(Modifier.isPublic(HistoryOperation.class.getModifiers()));
    assertTrue(Modifier.isAbstract(HistoryOperation.class.getModifiers()));
  }

  @Test
  public void historyOperation_whenReflected_extendsActionOperation() {
    assertEquals(ActionOperation.class, HistoryOperation.class.getSuperclass());
  }

  @Test
  public void historyOperation_constructor_whenReflected_acceptsUuidAndDocumentFrame() throws Exception {
    Constructor<HistoryOperation> constructor = HistoryOperation.class.getDeclaredConstructor(UUID.class, DocumentFrame.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(2, constructor.getParameterCount());
  }

  @Test
  public void historyOperation_performInternal_whenReflected_isProtectedAbstract() throws Exception {
    Method method = HistoryOperation.class.getDeclaredMethod("performInternal", UndoHistory.class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void historyOperation_perform_whenReflected_acceptsUserActivity() throws Exception {
    Method method = HistoryOperation.class.getDeclaredMethod("perform", UserActivity.class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void historyOperation_documentFrameField_whenReflected_isPrivateFinal() throws Exception {
    Field field = HistoryOperation.class.getDeclaredField("documentFrame");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
    assertEquals(DocumentFrame.class, field.getType());
  }

  @Test
  public void undoOperation_whenReflected_isPublicConcreteHistoryOperation() {
    assertTrue(Modifier.isPublic(UndoOperation.class.getModifiers()));
    assertFalse(Modifier.isAbstract(UndoOperation.class.getModifiers()));
    assertEquals(HistoryOperation.class, UndoOperation.class.getSuperclass());
  }

  @Test
  public void undoOperation_constructor_whenReflected_acceptsDocumentFrame() throws Exception {
    Constructor<UndoOperation> constructor = UndoOperation.class.getDeclaredConstructor(DocumentFrame.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(1, constructor.getParameterCount());
  }

  @Test
  public void undoOperation_performInternal_whenReflected_acceptsUndoHistory() throws Exception {
    Method method = UndoOperation.class.getDeclaredMethod("performInternal", UndoHistory.class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
    assertEquals(UndoOperation.class, method.getDeclaringClass());
  }

  @Test
  public void redoOperation_whenReflected_isPublicConcreteHistoryOperation() {
    assertTrue(Modifier.isPublic(RedoOperation.class.getModifiers()));
    assertFalse(Modifier.isAbstract(RedoOperation.class.getModifiers()));
    assertEquals(HistoryOperation.class, RedoOperation.class.getSuperclass());
  }

  @Test
  public void redoOperation_constructor_whenReflected_acceptsDocumentFrame() throws Exception {
    Constructor<RedoOperation> constructor = RedoOperation.class.getDeclaredConstructor(DocumentFrame.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(1, constructor.getParameterCount());
  }

  @Test
  public void redoOperation_performInternal_whenReflected_acceptsUndoHistory() throws Exception {
    Method method = RedoOperation.class.getDeclaredMethod("performInternal", UndoHistory.class);
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
    assertEquals(RedoOperation.class, method.getDeclaringClass());
  }

  @Test
  public void redoOperation_isToolBarTextClobbered_whenReflected_returnsBoolean() throws Exception {
    Method method = RedoOperation.class.getDeclaredMethod("isToolBarTextClobbered");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertEquals(boolean.class, method.getReturnType());
  }

  @Test
  public void projectHistoryComposite_whenReflected_isPublicConcreteHistoryComposite() {
    assertTrue(Modifier.isPublic(ProjectHistoryComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(ProjectHistoryComposite.class.getModifiers()));
    assertEquals(HistoryComposite.class, ProjectHistoryComposite.class.getSuperclass());
  }

  @Test
  public void projectHistoryComposite_singletonPattern_whenReflected_isPresent() throws Exception {
    assertSingletonShape(ProjectHistoryComposite.class);
  }

  @Test
  public void uiHistoryComposite_whenReflected_isPublicConcreteHistoryComposite() {
    assertTrue(Modifier.isPublic(UiHistoryComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(UiHistoryComposite.class.getModifiers()));
    assertEquals(HistoryComposite.class, UiHistoryComposite.class.getSuperclass());
  }

  @Test
  public void uiHistoryComposite_singletonPattern_whenReflected_isPresent() throws Exception {
    assertSingletonShape(UiHistoryComposite.class);
  }

  @Test
  public void uiHistoryComposite_localize_whenReflected_isProtectedOverride() throws Exception {
    Method method = UiHistoryComposite.class.getDeclaredMethod("localize");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }
}

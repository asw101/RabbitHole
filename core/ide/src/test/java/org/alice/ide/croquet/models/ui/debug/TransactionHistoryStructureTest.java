package org.alice.ide.croquet.models.ui.debug;

import edu.cmu.cs.dennisc.javax.swing.models.AbstractMutableTreeModel;
import org.alice.ide.croquet.models.ui.debug.components.TransactionHistoryTreeModel;
import org.alice.ide.croquet.models.ui.debug.components.TransactionHistoryView;
import org.junit.Test;
import org.lgna.croquet.FrameCompositeWithInternalIsShowingState;
import org.lgna.croquet.Group;
import org.lgna.croquet.history.ActivityNode;
import org.lgna.croquet.history.UserActivity;

import javax.swing.tree.TreePath;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class TransactionHistoryStructureTest {

  private static void assertSingletonShape(Class<?> type) throws Exception {
    Class<?> holder = Class.forName(type.getName() + "$SingletonHolder", false, type.getClassLoader());
    assertTrue(Modifier.isPrivate(holder.getModifiers()));

    Method getInstance = type.getMethod("getInstance");
    assertTrue(Modifier.isPublic(getInstance.getModifiers()));
    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertEquals(type, getInstance.getReturnType());

    Constructor<?> constructor = type.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void transactionHistoryComposite_whenReflected_isPublicAbstract() {
    assertTrue(Modifier.isPublic(TransactionHistoryComposite.class.getModifiers()));
    assertTrue(Modifier.isAbstract(TransactionHistoryComposite.class.getModifiers()));
  }

  @Test
  public void transactionHistoryComposite_whenReflected_extendsFrameComposite() {
    assertEquals(FrameCompositeWithInternalIsShowingState.class, TransactionHistoryComposite.class.getSuperclass());
  }

  @Test
  public void transactionHistoryComposite_constructor_whenReflected_acceptsUuidAndGroup() throws Exception {
    Constructor<TransactionHistoryComposite> constructor = TransactionHistoryComposite.class.getDeclaredConstructor(UUID.class, Group.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(2, constructor.getParameterCount());
  }

  @Test
  public void transactionHistoryComposite_createView_whenReflected_returnsTransactionHistoryView() throws Exception {
    Method method = TransactionHistoryComposite.class.getDeclaredMethod("createView");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(TransactionHistoryView.class, method.getReturnType());
  }

  @Test
  public void activeTransactionHistoryComposite_whenReflected_isPublicConcreteSubclass() {
    assertTrue(Modifier.isPublic(ActiveTransactionHistoryComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(ActiveTransactionHistoryComposite.class.getModifiers()));
    assertEquals(TransactionHistoryComposite.class, ActiveTransactionHistoryComposite.class.getSuperclass());
  }

  @Test
  public void activeTransactionHistoryComposite_singletonPattern_whenReflected_isPresent() throws Exception {
    assertSingletonShape(ActiveTransactionHistoryComposite.class);
  }

  @Test
  public void activeTransactionHistoryComposite_localize_whenReflected_isProtected() throws Exception {
    Method method = ActiveTransactionHistoryComposite.class.getDeclaredMethod("localize");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void activeTransactionHistoryComposite_createView_whenReflected_returnsTransactionHistoryView() throws Exception {
    Method method = ActiveTransactionHistoryComposite.class.getDeclaredMethod("createView");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(TransactionHistoryView.class, method.getReturnType());
  }

  @Test
  public void transactionHistoryTreeModel_whenReflected_isPublicConcreteTreeModel() {
    assertTrue(Modifier.isPublic(TransactionHistoryTreeModel.class.getModifiers()));
    assertFalse(Modifier.isAbstract(TransactionHistoryTreeModel.class.getModifiers()));
    assertEquals(AbstractMutableTreeModel.class, TransactionHistoryTreeModel.class.getSuperclass());
  }

  @Test
  public void transactionHistoryTreeModel_constructor_whenReflected_acceptsUserActivity() throws Exception {
    Constructor<TransactionHistoryTreeModel> constructor = TransactionHistoryTreeModel.class.getDeclaredConstructor(UserActivity.class);
    assertFalse(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(1, constructor.getParameterCount());
  }

  @Test
  public void transactionHistoryTreeModel_rootField_whenReflected_isPrivateUserActivity() throws Exception {
    Field field = TransactionHistoryTreeModel.class.getDeclaredField("root");
    assertTrue(Modifier.isPrivate(field.getModifiers()));
    assertEquals(UserActivity.class, field.getType());
  }

  @Test
  public void transactionHistoryTreeModel_coreMethods_whenReflected_matchExpectedShapes() throws Exception {
    assertEquals(UserActivity.class, TransactionHistoryTreeModel.class.getDeclaredMethod("getRoot").getReturnType());
    assertEquals(boolean.class, TransactionHistoryTreeModel.class.getDeclaredMethod("isLeaf", Object.class).getReturnType());
    assertEquals(int.class, TransactionHistoryTreeModel.class.getDeclaredMethod("getChildCount", Object.class).getReturnType());
    assertEquals(Object.class, TransactionHistoryTreeModel.class.getDeclaredMethod("getChild", Object.class, int.class).getReturnType());
    assertEquals(int.class, TransactionHistoryTreeModel.class.getDeclaredMethod("getIndexOfChild", Object.class, Object.class).getReturnType());
    assertEquals(TreePath.class, TransactionHistoryTreeModel.class.getDeclaredMethod("getTreePath", Object.class).getReturnType());
  }

  @Test
  public void transactionHistoryTreeModel_updatePath_whenReflected_isPrivateHelper() throws Exception {
    Method method = TransactionHistoryTreeModel.class.getDeclaredMethod("updatePath", List.class, ActivityNode.class);
    assertTrue(Modifier.isPrivate(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void transactionHistoryTreeModel_declaredMethodCount_whenReflected_coversCoreApi() {
    assertTrue(TransactionHistoryTreeModel.class.getDeclaredMethods().length >= 7);
  }
}

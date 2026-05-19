package org.alice.ide.sceneeditor;

import org.alice.ide.preferences.IsToolBarShowing;
import org.lgna.project.ast.AbstractField;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SceneEditorContractTest {

  // ===== FieldAndInstanceMapper interface ===

  @Test
  public void mapper_interfaceHasFourMethods() {
    Method[] methods = FieldAndInstanceMapper.class.getDeclaredMethods();
    assertEquals(4, methods.length);
  }

  @Test
  public void mapper_allMethodsArePublic() {
    for (Method m : FieldAndInstanceMapper.class.getDeclaredMethods()) {
      assertTrue(m.getName() + " should be public",
          Modifier.isPublic(m.getModifiers()));
    }
  }

  @Test
  public void mapper_isAnInterface() {
    assertTrue(FieldAndInstanceMapper.class.isInterface());
  }

  @Test
  public void mapper_stubReturnsNullForUnknownInstance() {
    FieldAndInstanceMapper stub = createMapStub();
    assertNull(stub.getFieldForInstanceInUserVM(new Object()));
    assertNull(stub.getFieldForInstanceInJavaVM(new Object()));
  }

  @Test
  public void mapper_stubReturnsNullForNullField() {
    FieldAndInstanceMapper stub = createMapStub();
    assertNull(stub.getInstanceInUserVMForField(null));
    assertNull(stub.getInstanceInJavaVMForField(null));
  }

  private FieldAndInstanceMapper createMapStub() {
    final Map<Object, AbstractField> instanceToField = new HashMap<>();
    final Map<AbstractField, Object> fieldToInstance = new HashMap<>();
    return new FieldAndInstanceMapper() {
      @Override
      public AbstractField getFieldForInstanceInUserVM(Object instance) {
        return instanceToField.get(instance);
      }

      @Override
      public Object getInstanceInUserVMForField(AbstractField field) {
        return fieldToInstance.get(field);
      }

      @Override
      public AbstractField getFieldForInstanceInJavaVM(Object instance) {
        return instanceToField.get(instance);
      }

      @Override
      public Object getInstanceInJavaVMForField(AbstractField field) {
        return fieldToInstance.get(field);
      }
    };
  }

  // ===== IsToolBarShowing =====

  @Test
  public void isToolBarShowing_returnsFalse() {
    assertFalse(IsToolBarShowing.getValue());
  }

  @Test
  public void isToolBarShowing_getValue_isStatic() throws NoSuchMethodException {
    Method m = IsToolBarShowing.class.getMethod("getValue");
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void isToolBarShowing_constructorIsPrivate() throws Exception {
    Constructor<?> ctor = IsToolBarShowing.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(ctor.getModifiers()));
  }
}

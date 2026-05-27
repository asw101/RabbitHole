package org.lgna.story.implementation;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final class UserDialogDelegateTestSupport {
  private UserDialogDelegateTestSupport() {
  }

  static Object newDoubleNumberModel(String message) throws Exception {
    return instantiate("DoubleNumberModel", message);
  }

  static Object newIntegerNumberModel(String message) throws Exception {
    return instantiate("IntegerNumberModel", message);
  }

  static void appendDigit(Object model, int digit) throws Exception {
    invoke(model, "appendDigit", new Class<?>[] {short.class}, (short) digit);
  }

  static void appendDecimalPoint(Object model) throws Exception {
    invoke(model, "appendDecimalPoint", new Class<?>[0]);
  }

  static void negate(Object model) throws Exception {
    invoke(model, "negate", new Class<?>[0]);
  }

  static void deleteLastCharacter(Object model) throws Exception {
    invoke(model, "deleteLastCharacter", new Class<?>[0]);
  }

  static Number getValue(Object model) throws Exception {
    return (Number) invoke(model, "getValue", new Class<?>[0]);
  }

  static Action getDecimalPointAction(Object model) throws Exception {
    return (Action) invoke(model, "getDecimalPointAction", new Class<?>[0]);
  }

  static Action getBackspaceAction(Object model) throws Exception {
    return (Action) getFieldValue(model, "backspaceAction");
  }

  static Action getNegateAction(Object model) throws Exception {
    return (Action) getFieldValue(model, "negateAction");
  }

  static Action getNumeralAction(Object model, int digit) throws Exception {
    Object actions = getFieldValue(model, "numeralActions");
    return (Action) Array.get(actions, digit);
  }

  static JPanel createComponent(Object model) throws Exception {
    return (JPanel) invoke(model, "createComponent", new Class<?>[0]);
  }

  static void addListeners(Object numPad) throws Exception {
    invoke(numPad, "addListeners", new Class<?>[0]);
  }

  static void removeListeners(Object numPad) throws Exception {
    invoke(numPad, "removeListeners", new Class<?>[0]);
  }

  static JTextField getTextField(Object numPad) throws Exception {
    return (JTextField) getFieldValue(numPad, "textField");
  }

  static String getDocumentText(Object model) throws Exception {
    Document document = getDocument(model);
    try {
      return document.getText(0, document.getLength());
    } catch (BadLocationException ble) {
      throw new RuntimeException(ble);
    }
  }

  static void setDocumentText(Object model, String text) throws Exception {
    Document document = getDocument(model);
    try {
      document.remove(0, document.getLength());
      document.insertString(0, text, null);
    } catch (BadLocationException ble) {
      throw new RuntimeException(ble);
    }
  }

  static List<JButton> findButtons(Container container) {
    List<JButton> buttons = new ArrayList<>();
    collectButtons(container, buttons);
    return buttons;
  }

  private static void collectButtons(Container container, List<JButton> buttons) {
    for (Component component : container.getComponents()) {
      if (component instanceof JButton button) {
        buttons.add(button);
      }
      if (component instanceof Container child) {
        collectButtons(child, buttons);
      }
    }
  }

  private static Document getDocument(Object model) throws Exception {
    return (Document) getFieldValue(model, "document");
  }

  private static Object instantiate(String simpleName, String message) throws Exception {
    Class<?> nestedClass = findNestedClass(simpleName);
    Constructor<?> constructor = nestedClass.getDeclaredConstructor(String.class);
    constructor.setAccessible(true);
    return constructor.newInstance(message);
  }

  private static Object getFieldValue(Object target, String name) throws Exception {
    Field field = findField(target.getClass(), name);
    field.setAccessible(true);
    return field.get(target);
  }

  private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
    Class<?> current = type;
    while (current != null) {
      try {
        return current.getDeclaredField(name);
      } catch (NoSuchFieldException ignored) {
        current = current.getSuperclass();
      }
    }
    throw new NoSuchFieldException(name);
  }

  private static Object invoke(Object target, String name, Class<?>[] parameterTypes, Object... args) throws Exception {
    Method method = findMethod(target.getClass(), name, parameterTypes);
    method.setAccessible(true);
    return method.invoke(target, args);
  }

  private static Method findMethod(Class<?> type, String name, Class<?>[] parameterTypes) throws NoSuchMethodException {
    Class<?> current = type;
    while (current != null) {
      try {
        return current.getDeclaredMethod(name, parameterTypes);
      } catch (NoSuchMethodException ignored) {
        current = current.getSuperclass();
      }
    }
    throw new NoSuchMethodException(name);
  }

  private static Class<?> findNestedClass(String simpleName) {
    for (Class<?> nestedClass : UserDialogDelegate.class.getDeclaredClasses()) {
      if (nestedClass.getSimpleName().equals(simpleName)) {
        return nestedClass;
      }
    }
    throw new IllegalArgumentException("No nested class named " + simpleName);
  }
}

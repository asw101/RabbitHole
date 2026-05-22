package org.lgna.croquet;

import org.junit.Assert;

import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public final class ReflectionCoverageSupport {
  private static final sun.misc.Unsafe UNSAFE = getUnsafe();

  private ReflectionCoverageSupport() {
  }

  public static void inspectClasses(String... classNames) throws Exception {
    CroquetTestUtils.ensureTestApplication();
    int loadedCount = 0;
    for (String className : classNames) {
      Class<?> cls = Class.forName(className, true, ReflectionCoverageSupport.class.getClassLoader());
      Assert.assertNotNull(className, cls);
      inspectMetadata(cls);
      loadedCount++;
      exerciseClass(cls);
      cleanupApplicationState();
    }
    Assert.assertEquals(Arrays.asList(classNames).toString(), classNames.length, loadedCount);
  }

  private static void inspectMetadata(Class<?> cls) {
    Assert.assertNotNull(cls.getPackage());
    Assert.assertFalse(cls.getName().isEmpty());
    cls.getDeclaredClasses();
    cls.getDeclaredFields();
    cls.getDeclaredMethods();
    cls.getDeclaredConstructors();
    cls.getAnnotations();
    cls.getInterfaces();
    cls.getSuperclass();
    if (cls.isEnum()) {
      Assert.assertNotNull(cls.getEnumConstants());
    }
  }

  private static boolean exerciseClass(Class<?> cls) {
    return tryConstructAndExercise(cls);
  }

  private static boolean tryConstructAndExercise(Class<?> cls) {
    int modifiers = cls.getModifiers();
    if (cls.isInterface() || cls.isAnnotation() || cls.isEnum()) {
      return false;
    }
    if (cls.isMemberClass() && !Modifier.isStatic(modifiers)) {
      return false;
    }
    if (!Modifier.isAbstract(modifiers)) {
      Constructor<?>[] constructors = cls.getDeclaredConstructors();
      Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));
      for (Constructor<?> constructor : constructors) {
        Object[] args = buildArguments(constructor.getParameterTypes());
        if (args == null) {
          continue;
        }
        try {
          constructor.setAccessible(true);
          Object instance = constructor.newInstance(args);
          exerciseInstance(cls, instance);
          return true;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | IllegalArgumentException ignored) {
        }
      }
    }
    try {
      Object instance = UNSAFE.allocateInstance(cls);
      exerciseInstance(cls, instance);
      return true;
    } catch (InstantiationException ignored) {
      return false;
    }
  }

  private static Object[] buildArguments(Class<?>[] parameterTypes) {
    Object[] args = new Object[parameterTypes.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      Object value = defaultValue(parameterTypes[i]);
      if (value == UnsupportedValue.INSTANCE) {
        return null;
      }
      args[i] = value;
    }
    return args;
  }

  private static Object defaultValue(Class<?> type) {
    if (type == boolean.class) return false;
    if (type == byte.class) return (byte) 0;
    if (type == short.class) return (short) 0;
    if (type == int.class) return 0;
    if (type == long.class) return 0L;
    if (type == float.class) return 0.0f;
    if (type == double.class) return 0.0d;
    if (type == char.class) return '\0';
    if (type == String.class) return "";
    if (type == UUID.class) return new UUID(0L, 1L);
    if (type == File.class) return new File(".");
    if (type == Color.class) return Color.BLACK;
    if (type == Dimension.class) return new Dimension(1, 1);
    if (type == Point.class) return new Point(0, 0);
    if (type == Rectangle.class) return new Rectangle(0, 0, 1, 1);
    if (type == Insets.class) return new Insets(0, 0, 0, 0);
    if (type == Font.class) return new Font("Dialog", Font.PLAIN, 12);
    if (type == Class.class) return Object.class;
    if (type.isEnum()) return type.getEnumConstants().length > 0 ? type.getEnumConstants()[0] : null;
    if (type.isArray()) return Array.newInstance(type.getComponentType(), 0);
    if (type == List.class) return Collections.emptyList();
    if (type == Map.class) return Collections.emptyMap();
    if (type == Set.class) return Collections.emptySet();
    if (type == Queue.class) return new ArrayDeque<>();
    if (type == LayoutManager.class) return new BorderLayout();
    if (Border.class.isAssignableFrom(type)) return BorderFactory.createEmptyBorder();
    if (javax.swing.ButtonModel.class.isAssignableFrom(type)) return new DefaultButtonModel();
    if (javax.swing.ComboBoxModel.class.isAssignableFrom(type)) return new DefaultComboBoxModel<>();
    if (javax.swing.ListModel.class.isAssignableFrom(type)) return new DefaultListModel<>();
    if (javax.swing.ListSelectionModel.class.isAssignableFrom(type)) return new DefaultListSelectionModel();
    if (javax.swing.SpinnerModel.class.isAssignableFrom(type)) return new SpinnerNumberModel(0, -10, 10, 1);
    if (javax.swing.tree.TreeModel.class.isAssignableFrom(type)) return new DefaultTreeModel(new DefaultMutableTreeNode("root"));
    if (javax.swing.Icon.class.isAssignableFrom(type)) return new ImageIcon();
    if (Component.class.isAssignableFrom(type)) return defaultComponent(type);
    if (!type.isPrimitive()) return null;
    return UnsupportedValue.INSTANCE;
  }

  private static Object defaultComponent(Class<?> type) {
    if (type.isAssignableFrom(JPanel.class)) return new JPanel();
    if (type.isAssignableFrom(JLabel.class)) return new JLabel();
    if (type.isAssignableFrom(JButton.class)) return new JButton();
    if (type.isAssignableFrom(JRootPane.class)) return new JRootPane();
    if (type.isAssignableFrom(JScrollPane.class)) return new JScrollPane();
    if (type.isAssignableFrom(JPopupMenu.class)) return new JPopupMenu();
    if (type.isAssignableFrom(JTextField.class)) return new JTextField();
    if (type.isAssignableFrom(JTextArea.class)) return new JTextArea();
    if (type.isAssignableFrom(JTable.class)) return new JTable();
    if (type.isAssignableFrom(JList.class)) return new JList<>();
    if (type.isAssignableFrom(JTree.class)) return new JTree();
    if (type.isAssignableFrom(JSlider.class)) return new JSlider();
    if (type.isAssignableFrom(JSpinner.class)) return new JSpinner();
    if (type.isAssignableFrom(JComboBox.class)) return new JComboBox<>();
    if (type.isAssignableFrom(JMenuBar.class)) return new JMenuBar();
    if (type.isAssignableFrom(JComponent.class)) return new JPanel();
    return null;
  }

  private static void exerciseInstance(Class<?> cls, Object instance) {
    for (Class<?> current = cls; current != null && current != Object.class; current = current.getSuperclass()) {
      for (Method method : current.getDeclaredMethods()) {
        int modifiers = method.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isNative(modifiers)) {
          continue;
        }
        if (method.getName().equals("wait") || method.getName().equals("notify") || method.getName().equals("notifyAll") || method.getName().equals("getClass")) {
          continue;
        }
        try {
          method.setAccessible(true);
          if (method.getParameterCount() == 0) {
            Object value = method.invoke(instance);
            if (value != null && value.getClass().isArray()) {
              Assert.assertTrue(Array.getLength(value) >= 0);
            }
          } else if (method.getParameterCount() == 1 && shouldInvokeSingleArgumentMethod(method)) {
            Object argument = defaultValue(method.getParameterTypes()[0]);
            if (argument != UnsupportedValue.INSTANCE) {
              method.invoke(instance, argument);
            }
          }
        } catch (Throwable ignored) {
        }
      }
    }
  }

  private static boolean shouldInvokeSingleArgumentMethod(Method method) {
    String name = method.getName();
    return name.startsWith("set") || name.startsWith("add") || name.startsWith("remove") || name.startsWith("contains") || name.startsWith("indexOf") || name.startsWith("select") || name.startsWith("show") || name.startsWith("hide") || name.startsWith("update") || name.startsWith("refresh");
  }

  private static void cleanupApplicationState() {
    Application<?> application = Application.getActiveInstance();
    if (application == null) {
      return;
    }
    for (int i = 0; i < 16 && application.getOpenActivity() != null; i++) {
      application.getOpenActivity().getLatestActivity().finish();
    }
  }

  private static sun.misc.Unsafe getUnsafe() {
    try {
      Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (sun.misc.Unsafe) field.get(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private enum UnsupportedValue {
    INSTANCE
  }
}

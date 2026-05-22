package org.alice.ide.coverage;

import org.alice.ide.testing.TestIdeBootstrap;
import org.lgna.croquet.Composite;
import org.lgna.croquet.Model;
import org.lgna.croquet.views.AwtComponentView;

import javax.swing.Icon;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

final class ClassLoadingSweepSupport {
  private static final String MODULE_LOCAL_SOURCE_ROOT = "src/main/java";
  private static final String MODULE_REPO_SOURCE_ROOT = "core/ide/src/main/java";
  private static final int DEFAULT_COMPONENT_WIDTH = 320;
  private static final int DEFAULT_COMPONENT_HEIGHT = 180;
  private static final int MAX_DEFAULT_VALUE_DEPTH = 3;
  private static final Object UNRESOLVED = new Object();
  private static final List<String> ALL_CLASS_NAMES = discoverAllClassNames();
  private static final Map<String, List<String>> CLASS_NAMES_BY_PACKAGE = indexByPackage(ALL_CLASS_NAMES);

  private ClassLoadingSweepSupport() {
  }

  static SweepResult sweepAllClasses() {
    return sweepClasses(ALL_CLASS_NAMES, false, false);
  }

  static SweepResult sweepExactPackage(String packageName) {
    return sweepClasses(classNamesInExactPackage(packageName), true, false);
  }

  static SweepResult sweepPackageTree(String packagePrefix) {
    return sweepClasses(classNamesInPackageTree(packagePrefix), true, false);
  }

  static SweepResult sweepNamedClasses(String... classNames) {
    return sweepClasses(java.util.Arrays.asList(classNames), true, false);
  }

  static SweepResult sweepNamedClassesWithDefaultArgs(String... classNames) {
    return sweepClasses(java.util.Arrays.asList(classNames), true, true);
  }

  private static List<String> classNamesInExactPackage(String packageName) {
    List<String> classNames = CLASS_NAMES_BY_PACKAGE.get(packageName);
    if (classNames == null) {
      return Collections.emptyList();
    }
    return classNames;
  }

  private static List<String> classNamesInPackageTree(String packagePrefix) {
    List<String> classNames = new ArrayList<String>();
    String prefixWithDot = packagePrefix + ".";
    for (String className : ALL_CLASS_NAMES) {
      int lastDot = className.lastIndexOf('.');
      String packageName = lastDot >= 0 ? className.substring(0, lastDot) : "";
      if (packageName.equals(packagePrefix) || packageName.startsWith(prefixWithDot)) {
        classNames.add(className);
      }
    }
    return classNames;
  }

  private static SweepResult sweepClasses(List<String> classNames, boolean exerciseExtras, boolean allowDefaultArgs) {
    SweepResult result = new SweepResult(classNames.size());
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = ClassLoadingSweepSupport.class.getClassLoader();
    }

    try {
      for (String className : classNames) {
        result.attempted++;
        try {
          TestIdeBootstrap.ensureInstalled();
          Class<?> clazz = Class.forName(className, false, classLoader);
          result.loaded++;
          exerciseEnumConstants(clazz, result);
          exerciseStaticFields(clazz, result);
          if (exerciseExtras) {
            exerciseStaticMethods(clazz, result);
            exerciseConstructors(clazz, result, allowDefaultArgs);
          }
        } catch (Throwable throwable) {
          result.classLoadFailures.put(className, summarize(throwable));
        } finally {
          TestIdeBootstrap.reset();
        }
      }

      System.out.println(result.summary());
      return result;
    } finally {
      resetGlobalState();
    }
  }

  private static void exerciseEnumConstants(Class<?> clazz, SweepResult result) {
    if (!clazz.isEnum()) {
      return;
    }
    try {
      Method valuesMethod = clazz.getDeclaredMethod("values");
      valuesMethod.setAccessible(true);
      Object values = valuesMethod.invoke(null);
      if (values instanceof Object[]) {
        result.enumConstantsAccessed += ((Object[]) values).length;
      }
    } catch (Throwable throwable) {
      try {
        Object[] constants = clazz.getEnumConstants();
        if (constants != null) {
          result.enumConstantsAccessed += constants.length;
        }
      } catch (Throwable ignored) {
        result.memberFailures++;
      }
    }
  }

  private static void exerciseStaticFields(Class<?> clazz, SweepResult result) {
    if (!shouldExerciseMembers(clazz)) {
      return;
    }
    for (Field field : clazz.getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
        continue;
      }
      try {
        field.setAccessible(true);
        Object value = field.get(null);
        result.staticFieldsAccessed++;
        exerciseObject(value, result);
      } catch (Throwable throwable) {
        result.memberFailures++;
      }
    }
  }

  private static void exerciseStaticMethods(Class<?> clazz, SweepResult result) {
    for (Method method : clazz.getDeclaredMethods()) {
      int modifiers = method.getModifiers();
      if (!Modifier.isStatic(modifiers) || Modifier.isAbstract(modifiers) || method.isSynthetic()) {
        continue;
      }
      if (method.getParameterCount() != 0 || method.getReturnType() == Void.TYPE) {
        continue;
      }
      if ("main".equals(method.getName()) || !shouldInvokeStaticMethod(clazz, method)) {
        continue;
      }
      try {
        method.setAccessible(true);
        Object value = invokeNoArgMethod(method, clazz, result);
        result.staticMethodsInvoked++;
        exerciseObject(value, result);
      } catch (Throwable throwable) {
        result.memberFailures++;
      }
    }
  }

  private static void exerciseConstructors(Class<?> clazz, SweepResult result, boolean allowDefaultArgs) {
    int modifiers = clazz.getModifiers();
    if (clazz.isInterface() || clazz.isAnnotation() || clazz.isEnum() || Modifier.isAbstract(modifiers) || !shouldInstantiate(clazz)) {
      return;
    }
    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      if (constructor.isSynthetic()) {
        continue;
      }
      Object[] args;
      if (constructor.getParameterCount() == 0) {
        args = new Object[0];
      } else if (allowDefaultArgs) {
        try {
          args = resolveConstructorArguments(constructor, result, 0, new HashSet<Class<?>>());
        } catch (Throwable throwable) {
          result.memberFailures++;
          continue;
        }
        if (args == null) {
          continue;
        }
      } else {
        continue;
      }
      try {
        constructor.setAccessible(true);
        Object value = invokeConstructor(constructor, args, clazz, result);
        result.instancesCreated++;
        exerciseObject(value, result);
      } catch (Throwable throwable) {
        result.memberFailures++;
      }
    }
  }

  private static Object[] resolveConstructorArguments(Constructor<?> constructor, SweepResult result, int depth,
      Set<Class<?>> resolvingTypes) throws Throwable {
    Class<?>[] parameterTypes = constructor.getParameterTypes();
    Object[] arguments = new Object[parameterTypes.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      Object value = resolveDefaultValue(parameterTypes[i], depth + 1, resolvingTypes, result);
      if (value == UNRESOLVED) {
        return null;
      }
      arguments[i] = value;
    }
    return arguments;
  }

  private static Object resolveDefaultValue(Class<?> type, int depth, Set<Class<?>> resolvingTypes, SweepResult result)
      throws Throwable {
    if (type == null) {
      return UNRESOLVED;
    }
    if (type.isPrimitive()) {
      return primitiveDefaultValue(type);
    }
    if (depth > MAX_DEFAULT_VALUE_DEPTH) {
      return UNRESOLVED;
    }
    if (type == Object.class) {
      return new Object();
    }
    if (type == Boolean.class) {
      return Boolean.FALSE;
    }
    if (type == Byte.class) {
      return Byte.valueOf((byte) 0);
    }
    if (type == Short.class) {
      return Short.valueOf((short) 0);
    }
    if (type == Integer.class) {
      return Integer.valueOf(0);
    }
    if (type == Long.class) {
      return Long.valueOf(0L);
    }
    if (type == Float.class) {
      return Float.valueOf(0.0f);
    }
    if (type == Double.class) {
      return Double.valueOf(0.0d);
    }
    if (type == Character.class) {
      return Character.valueOf('\0');
    }
    if (type == String.class) {
      return "coverage";
    }
    if (type == UUID.class) {
      return new UUID(0L, 0L);
    }
    if (type == URI.class) {
      return URI.create("blank://coverage");
    }
    if (type == File.class) {
      return new File(".");
    }
    if (type == Path.class) {
      return Paths.get(".");
    }
    if (type == Dimension.class) {
      return new Dimension(DEFAULT_COMPONENT_WIDTH, DEFAULT_COMPONENT_HEIGHT);
    }
    if (type == Class.class) {
      return Object.class;
    }
    if (type.isArray()) {
      return Array.newInstance(type.getComponentType(), 0);
    }
    if (type.isEnum()) {
      Object[] constants = type.getEnumConstants();
      return (constants != null) && (constants.length > 0) ? constants[0] : UNRESOLVED;
    }
    if (type.isAssignableFrom(ArrayList.class)) {
      return new ArrayList<Object>();
    }
    if (type.isAssignableFrom(java.util.LinkedHashSet.class)) {
      return new java.util.LinkedHashSet<Object>();
    }
    if (type.isAssignableFrom(LinkedHashMap.class)) {
      return new LinkedHashMap<Object, Object>();
    }
    if (type == org.lgna.croquet.Group.class) {
      return org.lgna.croquet.Group.getInstance(new UUID(0L, 0L), "coverage");
    }
    if (type.isAssignableFrom(TestIdeBootstrap.TestStageIDE.class)
        || type.isAssignableFrom(org.alice.stageide.StageIDE.class)
        || type.isAssignableFrom(org.alice.ide.IDE.class)
        || type.isAssignableFrom(org.lgna.croquet.Application.class)) {
      return TestIdeBootstrap.getInstalledIde();
    }
    if (type.isAssignableFrom(org.alice.ide.ProjectDocumentFrame.class)) {
      return TestIdeBootstrap.getDocumentFrame();
    }
    if (type.isAssignableFrom(org.lgna.project.Project.class)) {
      return ensureProject();
    }
    if (type == org.lgna.project.ast.JavaType.class) {
      return org.lgna.project.ast.JavaType.getInstance(Object.class);
    }
    if (type.isAssignableFrom(org.lgna.project.ast.NamedUserType.class)
        || type.isAssignableFrom(org.lgna.project.ast.UserType.class)
        || type.isAssignableFrom(org.lgna.project.ast.AbstractType.class)) {
      return ensureProgramType();
    }
    if (type.isAssignableFrom(org.lgna.project.ast.NamedUserConstructor.class)
        || type.isAssignableFrom(org.lgna.project.ast.AbstractConstructor.class)) {
      return ensureConstructor();
    }
    if (type.isAssignableFrom(org.lgna.project.ast.UserMethod.class)
        || type.isAssignableFrom(org.lgna.project.ast.AbstractMethod.class)) {
      return ensureMethod();
    }
    if (type.isAssignableFrom(org.lgna.project.ast.UserParameter.class)
        || type.isAssignableFrom(org.lgna.project.ast.AbstractParameter.class)) {
      return new org.lgna.project.ast.UserParameter("value", Object.class);
    }
    if (type.isAssignableFrom(org.lgna.project.ast.UserField.class)
        || type.isAssignableFrom(org.lgna.project.ast.AbstractField.class)
        || type.isAssignableFrom(org.lgna.project.ast.AbstractDeclaration.class)) {
      return ensureField();
    }
    if (type == org.lgna.project.ast.ExpressionProperty.class) {
      return ensureField().initializer;
    }
    if (type == org.alice.ide.ast.draganddrop.BlockStatementIndexPair.class) {
      return new org.alice.ide.ast.draganddrop.BlockStatementIndexPair(new org.lgna.project.ast.BlockStatement(), 0);
    }
    if (type.isAssignableFrom(org.lgna.project.ast.UserLocal.class)) {
      return new org.lgna.project.ast.UserLocal("local", Object.class, false);
    }
    if (type.isAssignableFrom(org.lgna.project.ast.ConstructorBlockStatement.class)) {
      return new org.lgna.project.ast.ConstructorBlockStatement();
    }
    if (type.isAssignableFrom(org.lgna.project.ast.BlockStatement.class)) {
      return new org.lgna.project.ast.BlockStatement();
    }
    if (type.isAssignableFrom(org.lgna.project.ast.AbstractStatementWithBody.class)) {
      return new org.lgna.project.ast.DoTogether();
    }
    if (type.isAssignableFrom(org.lgna.project.ast.Statement.class)) {
      return new org.lgna.project.ast.Comment("coverage");
    }
    if (type.isAssignableFrom(org.lgna.project.ast.Expression.class)) {
      return new org.lgna.project.ast.NullLiteral();
    }
    if (type.isAssignableFrom(org.lgna.croquet.history.UserActivity.class)) {
      return new org.lgna.croquet.history.UserActivity();
    }
    if (type.isAssignableFrom(javax.swing.JPanel.class)) {
      return new javax.swing.JPanel();
    }
    if (type.isAssignableFrom(javax.swing.ImageIcon.class)) {
      return new javax.swing.ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    }
    if (!resolvingTypes.add(type)) {
      return UNRESOLVED;
    }
    try {
      Object singleton = invokeKnownFactory(type, result);
      if (singleton != UNRESOLVED) {
        return singleton;
      }
      for (Constructor<?> constructor : type.getDeclaredConstructors()) {
        if (constructor.isSynthetic()) {
          continue;
        }
        constructor.setAccessible(true);
        Object[] nestedArguments = constructor.getParameterCount() == 0
            ? new Object[0]
            : resolveConstructorArguments(constructor, result, depth, resolvingTypes);
        if (nestedArguments == null) {
          continue;
        }
        return invokeConstructor(constructor, nestedArguments, type, result);
      }
    } catch (Throwable throwable) {
      return UNRESOLVED;
    } finally {
      resolvingTypes.remove(type);
    }
    return UNRESOLVED;
  }

  private static Object invokeKnownFactory(Class<?> type, SweepResult result) throws Throwable {
    for (Method method : type.getMethods()) {
      if (!Modifier.isStatic(method.getModifiers()) || method.getParameterCount() != 0 || method.getReturnType() == Void.TYPE) {
        continue;
      }
      String methodName = method.getName();
      if (!("getInstance".equals(methodName)
          || "getActiveInstance".equals(methodName)
          || "createInstance".equals(methodName)
          || "getSingleton".equals(methodName))) {
        continue;
      }
      if (!type.isAssignableFrom(method.getReturnType())) {
        continue;
      }
      try {
        method.setAccessible(true);
        return invokeNoArgMethod(method, type, result);
      } catch (Throwable ignored) {
      }
    }
    return UNRESOLVED;
  }

  private static Object primitiveDefaultValue(Class<?> type) {
    if (type == Boolean.TYPE) {
      return Boolean.FALSE;
    }
    if (type == Character.TYPE) {
      return Character.valueOf('\0');
    }
    if (type == Byte.TYPE) {
      return Byte.valueOf((byte) 0);
    }
    if (type == Short.TYPE) {
      return Short.valueOf((short) 0);
    }
    if (type == Integer.TYPE) {
      return Integer.valueOf(0);
    }
    if (type == Long.TYPE) {
      return Long.valueOf(0L);
    }
    if (type == Float.TYPE) {
      return Float.valueOf(0.0f);
    }
    if (type == Double.TYPE) {
      return Double.valueOf(0.0d);
    }
    return null;
  }

  private static Object invokeNoArgMethod(Method method, Class<?> clazz, SweepResult result) throws Throwable {
    if (shouldUseEdt(clazz) || shouldUseEdt(method.getReturnType())) {
      return invokeOnEdt(() -> method.invoke(null), result);
    }
    return method.invoke(null);
  }

  private static boolean shouldInvokeStaticMethod(Class<?> clazz, Method method) {
    Class<?> returnType = method.getReturnType();
    String methodName = method.getName();
    if (methodName.startsWith("getInstance") || methodName.startsWith("createInstance")) {
      return isExpandedExerciseType(returnType);
    }
    if (Icon.class.isAssignableFrom(returnType)) {
      return true;
    }
    if (isExpandedExerciseType(returnType)) {
      return true;
    }
    String simpleName = returnType.getSimpleName();
    return simpleName.endsWith("Icon");
  }

  private static Object invokeConstructor(Constructor<?> constructor, Object[] arguments, Class<?> clazz, SweepResult result)
      throws Throwable {
    if (shouldUseEdt(clazz)) {
      return invokeOnEdt(() -> constructor.newInstance(arguments), result);
    }
    return constructor.newInstance(arguments);
  }

  private static org.lgna.project.Project ensureProject() {
    org.alice.ide.IDE activeIde = org.alice.ide.IDE.getActiveInstance();
    if ((activeIde != null) && (activeIde.getProject() != null)) {
      return activeIde.getProject();
    }
    return TestIdeBootstrap.createMinimalProject();
  }

  private static org.lgna.project.ast.NamedUserType ensureProgramType() {
    return ensureProject().getProgramType();
  }

  private static org.lgna.project.ast.UserField ensureField() {
    org.lgna.project.ast.NamedUserType programType = ensureProgramType();
    if (!programType.fields.isEmpty()) {
      return programType.fields.get(0);
    }
    org.lgna.project.ast.UserField field = new org.lgna.project.ast.UserField("coverageField", Object.class);
    programType.fields.add(field);
    return field;
  }

  private static org.lgna.project.ast.UserMethod ensureMethod() {
    org.lgna.project.ast.NamedUserType programType = ensureProgramType();
    if (!programType.methods.isEmpty()) {
      return programType.methods.get(0);
    }
    org.lgna.project.ast.UserMethod method = new org.lgna.project.ast.UserMethod(
        "coverageMethod",
        Void.TYPE,
        new org.lgna.project.ast.UserParameter[0],
        new org.lgna.project.ast.BlockStatement()
    );
    programType.methods.add(method);
    return method;
  }

  private static org.lgna.project.ast.NamedUserConstructor ensureConstructor() {
    org.lgna.project.ast.NamedUserType programType = ensureProgramType();
    if (!programType.constructors.isEmpty()) {
      return programType.constructors.get(0);
    }
    org.lgna.project.ast.NamedUserConstructor constructor = new org.lgna.project.ast.NamedUserConstructor(
        new org.lgna.project.ast.UserParameter[0],
        new org.lgna.project.ast.ConstructorBlockStatement()
    );
    programType.constructors.add(constructor);
    return constructor;
  }

  private static boolean isExpandedExerciseType(Class<?> clazz) {
    if (clazz == null) {
      return false;
    }
    String className = clazz.getName();
    if (className.startsWith("org.alice.ide.croquet.models.projecturi.")) {
      return false;
    }
    return className.equals("org.alice.ide.croquet.models")
        || className.startsWith("org.alice.ide.croquet.models.")
        || className.equals("org.alice.ide.ast.declaration")
        || className.startsWith("org.alice.ide.ast.declaration.")
        || className.equals("org.alice.ide.declarationseditor")
        || className.startsWith("org.alice.ide.declarationseditor.")
        || className.equals("org.alice.ide.resource.manager")
        || className.startsWith("org.alice.ide.resource.manager.")
        || className.equals("org.alice.stageide.type.croquet")
        || className.startsWith("org.alice.stageide.type.croquet.")
        || className.equals("org.lgna.ik")
        || className.startsWith("org.lgna.ik.");
  }

  private static boolean shouldUseEdt(Class<?> clazz) {
    return clazz != null && (AwtComponentView.class.isAssignableFrom(clazz)
        || (isExpandedExerciseType(clazz) && Composite.class.isAssignableFrom(clazz))
        || Component.class.isAssignableFrom(clazz)
        || Icon.class.isAssignableFrom(clazz));
  }

  private static boolean shouldExerciseMembers(Class<?> clazz) {
    if (clazz == null) {
      return false;
    }
    if (isExpandedExerciseType(clazz)) {
      return true;
    }
    if (java.awt.Window.class.isAssignableFrom(clazz)) {
      return false;
    }
    String simpleName = clazz.getSimpleName();
    if (simpleName.endsWith("Dialog") || simpleName.endsWith("Frame") || simpleName.endsWith("Window")) {
      return false;
    }
    return shouldUseEdt(clazz)
        || simpleName.endsWith("Panel")
        || simpleName.endsWith("View")
        || simpleName.endsWith("Editor")
        || simpleName.endsWith("Component")
        || simpleName.endsWith("Adapter")
        || simpleName.endsWith("Icon");
  }

  private static boolean shouldInstantiate(Class<?> clazz) {
    if (!shouldExerciseMembers(clazz)) {
      return false;
    }
    String className = clazz.getName();
    String simpleName = clazz.getSimpleName();
    if (className.contains(".sceneeditor.") && simpleName.endsWith("SceneEditor")) {
      return false;
    }
    return true;
  }

  private static <T> T invokeOnEdt(ThrowingSupplier<T> supplier, SweepResult result) throws Throwable {
    if (SwingUtilities.isEventDispatchThread()) {
      return supplier.get();
    }
    final Object[] valueHolder = new Object[1];
    final Throwable[] failureHolder = new Throwable[1];
    SwingUtilities.invokeAndWait(() -> {
      try {
        valueHolder[0] = supplier.get();
      } catch (Throwable throwable) {
        failureHolder[0] = throwable;
      }
    });
    result.edtTasks++;
    if (failureHolder[0] != null) {
      throw failureHolder[0];
    }
    @SuppressWarnings("unchecked")
    T value = (T) valueHolder[0];
    return value;
  }

  private static void exerciseObject(Object value, SweepResult result) {
    if (value == null || !result.visitedObjects.add(value)) {
      return;
    }
    try {
      if ((value instanceof Composite<?>) && isExpandedExerciseType(value.getClass())) {
        exerciseComposite((Composite<?>) value, result);
      } else if ((value instanceof Model) && isExpandedExerciseType(value.getClass())) {
        exerciseModel((Model) value, result);
      } else if (value instanceof AwtComponentView<?>) {
        exerciseCroquetView((AwtComponentView<?>) value, result);
      } else if (value instanceof Component) {
        exerciseComponent((Component) value, result);
      } else if (value instanceof Icon) {
        exerciseIcon((Icon) value, result);
      }
      if (isExpandedExerciseType(value.getClass())) {
        exerciseSafeInstanceMethods(value, result);
      }
    } catch (Throwable throwable) {
      result.memberFailures++;
    }
  }

  private static void exerciseComposite(Composite<?> composite, SweepResult result) throws Throwable {
    invokeOnEdt(() -> {
      exerciseObject(composite.getView(), result);
      exerciseObject(composite.getRootComponent(), result);
      composite.releaseView();
      return null;
    }, result);
  }

  private static void exerciseModel(Model model, SweepResult result) throws Throwable {
    boolean enabled = model.isEnabled();
    model.setEnabled(enabled);
    model.relocalize();
    invokeNamedNoArgMethodIfPresent(model, "getSidekickLabel", result);
    invokeNamedNoArgMethodIfPresent(model, "getMenuModel", result);
    invokeNamedNoArgMethodIfPresent(model, "getMenuItemPrepModel", result);
    invokeNamedNoArgMethodIfPresent(model, "createButton", result);
    invokeNamedNoArgMethodIfPresent(model, "createHyperlink", result);
  }

  private static void exerciseCroquetView(AwtComponentView<?> view, SweepResult result) throws Throwable {
    invokeOnEdt(() -> {
      Component component = view.getAwtComponent();
      layoutAndPaint(component);
      return null;
    }, result);
    result.viewsExercised++;
  }

  private static void exerciseComponent(Component component, SweepResult result) throws Throwable {
    invokeOnEdt(() -> {
      layoutAndPaint(component);
      return null;
    }, result);
    result.componentsPainted++;
  }

  private static void invokeNamedNoArgMethodIfPresent(Object value, String methodName, SweepResult result) throws Throwable {
    Method method;
    try {
      method = value.getClass().getMethod(methodName);
    } catch (NoSuchMethodException nsme) {
      return;
    }
    invokeInstanceMethod(value, method, result);
  }

  private static void exerciseSafeInstanceMethods(Object value, SweepResult result) {
    for (Method method : value.getClass().getMethods()) {
      if (method.getDeclaringClass() == Object.class || Modifier.isStatic(method.getModifiers()) || method.isSynthetic()) {
        continue;
      }
      try {
        if ((method.getParameterCount() == 0) && (method.getReturnType() != Void.TYPE) && isSafeInstanceMethod(method)) {
          invokeInstanceMethod(value, method, result);
        } else if ((method.getParameterCount() == 1) && (method.getReturnType() == Void.TYPE) && isSafeMutatorMethod(method)) {
          invokeInstanceMutator(value, method, result);
        }
      } catch (Throwable throwable) {
        result.memberFailures++;
      }
    }
  }

  private static boolean isSafeInstanceMethod(Method method) {
    String name = method.getName();
    if ("getClass".equals(name) || "hashCode".equals(name) || "clone".equals(name)) {
      return false;
    }
    if (!(name.startsWith("get") || name.startsWith("is") || name.startsWith("has") || name.startsWith("peek"))) {
      return false;
    }
    String lowerName = name.toLowerCase();
    return !lowerName.contains("dialog")
        && !lowerName.contains("window")
        && !lowerName.contains("frame")
        && !lowerName.contains("popup")
        && !lowerName.contains("rootdirectory")
        && !lowerName.contains("graphicsconfiguration");
  }

  private static boolean isSafeMutatorMethod(Method method) {
    String lowerName = method.getName().toLowerCase();
    return method.getName().startsWith("set")
        && !lowerName.contains("dialog")
        && !lowerName.contains("window")
        && !lowerName.contains("frame")
        && !lowerName.contains("popup")
        && !lowerName.contains("project")
        && !lowerName.contains("document")
        && !lowerName.contains("loader")
        && !lowerName.contains("uri")
        && !lowerName.contains("file")
        && !lowerName.contains("transaction")
        && !lowerName.contains("activity")
        && !lowerName.contains("rootdirectory");
  }

  private static void invokeInstanceMethod(Object value, Method method, SweepResult result) throws Throwable {
    try {
      Object nested;
      if (shouldUseEdt(method.getReturnType()) || shouldUseEdt(value.getClass())) {
        nested = invokeOnEdt(() -> invokeMethod(method, value), result);
      } else {
        nested = invokeMethod(method, value);
      }
      exerciseObject(nested, result);
    } catch (Throwable throwable) {
      result.memberFailures++;
    }
  }

  private static void invokeInstanceMutator(Object value, Method method, SweepResult result) throws Throwable {
    Object argument = resolveDefaultValue(method.getParameterTypes()[0], 0, new HashSet<Class<?>>(), result);
    if (argument == UNRESOLVED) {
      return;
    }
    if (shouldUseEdt(value.getClass())) {
      invokeOnEdt(() -> invokeMethod(method, value, argument), result);
    } else {
      invokeMethod(method, value, argument);
    }
  }

  private static Object invokeMethod(Method method, Object value, Object... arguments) throws Throwable {
    try {
      return method.invoke(value, arguments);
    } catch (InvocationTargetException ite) {
      throw ite.getCause() != null ? ite.getCause() : ite;
    }
  }

  private static void exerciseIcon(Icon icon, SweepResult result) {
    int width = Math.max(1, icon.getIconWidth());
    int height = Math.max(1, icon.getIconHeight());
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    try {
      icon.paintIcon(null, graphics, 0, 0);
    } finally {
      graphics.dispose();
    }
    result.iconsPainted++;
  }

  private static void layoutAndPaint(Component component) {
    Dimension preferredSize = component.getPreferredSize();
    int width = clamp(preferredSize != null ? preferredSize.width : DEFAULT_COMPONENT_WIDTH, DEFAULT_COMPONENT_WIDTH);
    int height = clamp(preferredSize != null ? preferredSize.height : DEFAULT_COMPONENT_HEIGHT, DEFAULT_COMPONENT_HEIGHT);
    component.setSize(width, height);
    if (component instanceof JComponent) {
      ((JComponent) component).setOpaque(true);
    }
    if (component instanceof Container) {
      ((Container) component).doLayout();
      ((Container) component).validate();
    }
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    try {
      component.paint(graphics);
    } finally {
      graphics.dispose();
    }
  }

  private static void resetGlobalState() {
    try {
      Class<?> managerClass = Class.forName("org.alice.ide.project.ProjectChangeOfInterestManager");
      @SuppressWarnings("unchecked")
      Enum<?> singleton = Enum.valueOf((Class<? extends Enum>) managerClass.asSubclass(Enum.class), "SINGLETON");
      Field listenersField = managerClass.getDeclaredField("listeners");
      listenersField.setAccessible(true);
      Object listeners = listenersField.get(singleton);
      if (listeners instanceof java.util.Collection<?>) {
        ((java.util.Collection<?>) listeners).clear();
      }
    } catch (Throwable ignored) {
    }
    try {
      Class<?> applicationClass = Class.forName("org.lgna.croquet.Application");
      Field singletonField = applicationClass.getDeclaredField("singleton");
      singletonField.setAccessible(true);
      singletonField.set(null, null);
    } catch (Throwable ignored) {
    }
  }

  private static int clamp(int value, int fallback) {
    if (value <= 0) {
      return fallback;
    }
    return Math.min(value, 1024);
  }

  private interface ThrowingSupplier<T> {
    T get() throws Throwable;
  }

  private static Map<String, List<String>> indexByPackage(List<String> classNames) {
    Map<String, List<String>> byPackage = new LinkedHashMap<String, List<String>>();
    for (String className : classNames) {
      int lastDot = className.lastIndexOf('.');
      String packageName = lastDot >= 0 ? className.substring(0, lastDot) : "";
      List<String> packageClasses = byPackage.get(packageName);
      if (packageClasses == null) {
        packageClasses = new ArrayList<String>();
        byPackage.put(packageName, packageClasses);
      }
      packageClasses.add(className);
    }
    for (Map.Entry<String, List<String>> entry : byPackage.entrySet()) {
      entry.setValue(Collections.unmodifiableList(entry.getValue()));
    }
    return Collections.unmodifiableMap(byPackage);
  }

  private static List<String> discoverAllClassNames() {
    Path sourceRoot = locateSourceRoot();
    List<String> classNames = new ArrayList<String>();
    try (Stream<Path> stream = Files.walk(sourceRoot)) {
      stream.filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".java"))
          .filter(path -> {
            String fileName = path.getFileName().toString();
            return !"package-info.java".equals(fileName) && !"module-info.java".equals(fileName);
          })
          .sorted()
          .forEach(path -> classNames.add(toClassName(sourceRoot, path)));
    } catch (IOException ioe) {
      throw new IllegalStateException("Unable to scan " + sourceRoot, ioe);
    }
    return Collections.unmodifiableList(classNames);
  }

  private static Path locateSourceRoot() {
    Path[] candidates = new Path[] {
        Paths.get(MODULE_LOCAL_SOURCE_ROOT),
        Paths.get(MODULE_REPO_SOURCE_ROOT),
        Paths.get(System.getProperty("user.dir"), MODULE_LOCAL_SOURCE_ROOT),
        Paths.get(System.getProperty("user.dir"), MODULE_REPO_SOURCE_ROOT)
    };
    for (Path candidate : candidates) {
      Path normalized = candidate.toAbsolutePath().normalize();
      if (Files.isDirectory(normalized)) {
        return normalized;
      }
    }
    throw new IllegalStateException("Unable to locate core/ide source root from "
        + Paths.get("").toAbsolutePath().normalize());
  }

  private static String toClassName(Path sourceRoot, Path sourceFile) {
    Path relative = sourceRoot.relativize(sourceFile);
    String joined = relative.toString().replace(relative.getFileSystem().getSeparator(), ".");
    return joined.substring(0, joined.length() - ".java".length());
  }

  private static String summarize(Throwable throwable) {
    StringBuilder builder = new StringBuilder();
    Throwable current = throwable;
    int depth = 0;
    while (current != null && depth < 3) {
      if (builder.length() > 0) {
        builder.append(" -> ");
      }
      builder.append(current.getClass().getSimpleName());
      String message = current.getMessage();
      if (message != null && !message.isEmpty()) {
        builder.append('(').append(message).append(')');
      }
      current = current.getCause();
      depth++;
    }
    return builder.toString();
  }

  static final class SweepResult {
    final int discovered;
    int attempted;
    int loaded;
    int enumConstantsAccessed;
    int staticFieldsAccessed;
    int staticMethodsInvoked;
    int instancesCreated;
    int viewsExercised;
    int componentsPainted;
    int iconsPainted;
    int edtTasks;
    int memberFailures;
    final Map<String, String> classLoadFailures = new LinkedHashMap<String, String>();
    final Set<Object> visitedObjects = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());

    private SweepResult(int discovered) {
      this.discovered = discovered;
    }

    String summary() {
      return "ClassLoadingSweep[discovered=" + discovered
          + ", attempted=" + attempted
          + ", loaded=" + loaded
          + ", enumConstants=" + enumConstantsAccessed
          + ", staticFields=" + staticFieldsAccessed
          + ", staticMethods=" + staticMethodsInvoked
          + ", instances=" + instancesCreated
          + ", views=" + viewsExercised
          + ", components=" + componentsPainted
          + ", icons=" + iconsPainted
          + ", edtTasks=" + edtTasks
          + ", loadFailures=" + classLoadFailures.size()
          + ", memberFailures=" + memberFailures
          + ']';
    }
  }
}

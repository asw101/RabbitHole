package org.alice.ide.x;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Reflection-based structural tests for all 18 component view classes
 * in org.alice.ide.x.components. Tests verify class loadability, superclass
 * chains, constructor parameters, declared methods/fields, generic type
 * bounds, and inner class structure.
 *
 * All tests are headless-safe (pure reflection, no GUI instantiation).
 */
public class AstI18nFactoryComponentsTest {

  private static final String PKG = "org.alice.ide.x.components.";
  private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();

  private static final String[] ALL_COMPONENT_NAMES = {
      "AbstractExpressionView", "ExpressionView", "ExpressionPropertyView",
      "ArgumentView", "ArgumentListPropertyView", "ArgumentListPropertyPane",
      "KeyedArgumentView", "KeyedArgumentListPropertyView",
      "NodePropertyView", "FieldAccessView", "InfixExpressionView",
      "InstanceCreationView", "InstancePropertyLabelView", "ListPropertyLabelsView",
      "ResourcePropertyView", "StatementListPropertyView",
      "ExpressionListPropertyPane", "ThisExpressionLikeView"
  };

  // ========================================================================
  // Helper methods
  // ========================================================================

  private Class<?> loadComponent(String simpleName) throws ClassNotFoundException {
    Class<?> cached = CLASS_CACHE.get(simpleName);
    if (cached != null) {
      return cached;
    }
    Class<?> cls = Class.forName(PKG + simpleName);
    CLASS_CACHE.put(simpleName, cls);
    return cls;
  }

  // ========================================================================
  // AbstractExpressionView
  // ========================================================================

  @Test
  public void abstractExpressionView_isLoadable() throws ClassNotFoundException {
    loadComponent("AbstractExpressionView");
  }

  @Test
  public void abstractExpressionView_isConcrete() throws ClassNotFoundException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    assertFalse(Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void abstractExpressionView_isPublic() throws ClassNotFoundException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    assertTrue(Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void abstractExpressionView_extendsExpressionLikeSubstance() throws ClassNotFoundException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    assertEquals("ExpressionLikeSubstance", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void abstractExpressionView_hasGenericTypeParam() throws ClassNotFoundException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    TypeVariable<?>[] typeParams = cls.getTypeParameters();
    assertEquals(1, typeParams.length);
    assertEquals("E", typeParams[0].getName());
    // E extends Expression
    Type[] bounds = typeParams[0].getBounds();
    assertTrue(bounds.length > 0);
    assertTrue(bounds[0].toString().contains("Expression"));
  }

  @Test
  public void abstractExpressionView_hasConstructorWithFactoryAndExpression() throws ClassNotFoundException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    boolean found = false;
    for (Constructor<?> c : ctors) {
      if (c.getParameterCount() == 2) {
        Class<?>[] params = c.getParameterTypes();
        if (params[0].getSimpleName().equals("AstI18nFactory")) {
          found = true;
          assertTrue(Modifier.isPublic(c.getModifiers()));
        }
      }
    }
    assertTrue("Constructor(AstI18nFactory, Expression) not found", found);
  }

  @Test
  public void abstractExpressionView_hasFactoryField() throws ClassNotFoundException, NoSuchFieldException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    Field f = cls.getDeclaredField("factory");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void abstractExpressionView_hasExpressionField() throws ClassNotFoundException, NoSuchFieldException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    Field f = cls.getDeclaredField("expression");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void abstractExpressionView_hasGetExpression() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    Method m = cls.getMethod("getExpression");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void abstractExpressionView_hasGetBackgroundColor() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    Method m = cls.getMethod("getBackgroundColor");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(java.awt.Color.class, m.getReturnType());
  }

  @Test
  public void abstractExpressionView_hasGetExpressionType() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    Method m = cls.getMethod("getExpressionType");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void abstractExpressionView_hasIsExpressionTypeFeedbackDesired() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    Method m = cls.getDeclaredMethod("isExpressionTypeFeedbackDesired");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void abstractExpressionView_hasGetInsetTop() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    Method m = cls.getDeclaredMethod("getInsetTop");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void abstractExpressionView_hasGetInsetBottom() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = loadComponent("AbstractExpressionView");
    Method m = cls.getDeclaredMethod("getInsetBottom");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  // ========================================================================
  // ExpressionView — extends AbstractExpressionView<Expression>
  // ========================================================================

  @Test
  public void expressionView_isLoadable() throws ClassNotFoundException {
    loadComponent("ExpressionView");
  }

  @Test
  public void expressionView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("ExpressionView").getModifiers()));
  }

  @Test
  public void expressionView_extendsAbstractExpressionView() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ExpressionView");
    assertEquals("AbstractExpressionView", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void expressionView_genericSuperclassUsesExpression() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ExpressionView");
    Type genericSuper = cls.getGenericSuperclass();
    assertTrue(genericSuper instanceof ParameterizedType);
    ParameterizedType pt = (ParameterizedType) genericSuper;
    Type[] args = pt.getActualTypeArguments();
    assertEquals(1, args.length);
    assertTrue(args[0].toString().contains("Expression"));
  }

  @Test
  public void expressionView_hasConstructor() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ExpressionView");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertEquals(2, ctors[0].getParameterCount());
    assertTrue(Modifier.isPublic(ctors[0].getModifiers()));
  }

  @Test
  public void expressionView_noDeclaredMethods() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ExpressionView");
    Method[] methods = cls.getDeclaredMethods();
    assertEquals(0, java.util.Arrays.stream(methods).filter(method -> !method.isSynthetic()).count());
  }

  @Test
  public void expressionView_noDeclaredFields() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ExpressionView");
    Field[] fields = cls.getDeclaredFields();
    assertEquals(0, fields.length);
  }

  // ========================================================================
  // ExpressionPropertyView — extends NodePropertyView<ExpressionProperty, Expression>
  // ========================================================================

  @Test
  public void expressionPropertyView_isLoadable() throws ClassNotFoundException {
    loadComponent("ExpressionPropertyView");
  }

  @Test
  public void expressionPropertyView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("ExpressionPropertyView").getModifiers()));
  }

  @Test
  public void expressionPropertyView_extendsNodePropertyView() throws ClassNotFoundException {
    assertEquals("NodePropertyView",
        loadComponent("ExpressionPropertyView").getSuperclass().getSimpleName());
  }

  @Test
  public void expressionPropertyView_genericSuperclassParams() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ExpressionPropertyView");
    Type genericSuper = cls.getGenericSuperclass();
    assertTrue(genericSuper instanceof ParameterizedType);
    ParameterizedType pt = (ParameterizedType) genericSuper;
    Type[] args = pt.getActualTypeArguments();
    assertEquals(2, args.length);
    assertTrue(args[0].toString().contains("ExpressionProperty"));
    assertTrue(args[1].toString().contains("Expression"));
  }

  @Test
  public void expressionPropertyView_hasCreateComponentMethod() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = loadComponent("ExpressionPropertyView");
    Method m = cls.getDeclaredMethod("createComponent",
        org.lgna.project.ast.Expression.class);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void expressionPropertyView_hasConstructor() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ExpressionPropertyView");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertEquals(2, ctors[0].getParameterCount());
  }

  // ========================================================================
  // ArgumentView — abstract, extends LineAxisPanel
  // ========================================================================

  @Test
  public void argumentView_isLoadable() throws ClassNotFoundException {
    loadComponent("ArgumentView");
  }

  @Test
  public void argumentView_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(loadComponent("ArgumentView").getModifiers()));
  }

  @Test
  public void argumentView_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(loadComponent("ArgumentView").getModifiers()));
  }

  @Test
  public void argumentView_extendsLineAxisPanel() throws ClassNotFoundException {
    assertEquals("LineAxisPanel",
        loadComponent("ArgumentView").getSuperclass().getSimpleName());
  }

  @Test
  public void argumentView_hasTypeParameterN() throws ClassNotFoundException {
    TypeVariable<?>[] params = loadComponent("ArgumentView").getTypeParameters();
    assertEquals(1, params.length);
    assertEquals("N", params[0].getName());
    // N extends AbstractArgument
    assertTrue(params[0].getBounds()[0].toString().contains("AbstractArgument"));
  }

  @Test
  public void argumentView_hasFactoryField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("ArgumentView").getDeclaredField("factory");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void argumentView_hasArgumentField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("ArgumentView").getDeclaredField("argument");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void argumentView_hasAbstractGetName() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("ArgumentView").getDeclaredMethod("getName");
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(String.class, m.getReturnType());
  }

  @Test
  public void argumentView_hasGetArgument() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("ArgumentView").getMethod("getArgument");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void argumentView_hasInternalRefresh() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("ArgumentView").getDeclaredMethod("internalRefresh");
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // ========================================================================
  // ArgumentListPropertyView — abstract, extends LineAxisPanel
  // ========================================================================

  @Test
  public void argumentListPropertyView_isLoadable() throws ClassNotFoundException {
    loadComponent("ArgumentListPropertyView");
  }

  @Test
  public void argumentListPropertyView_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(loadComponent("ArgumentListPropertyView").getModifiers()));
  }

  @Test
  public void argumentListPropertyView_extendsLineAxisPanel() throws ClassNotFoundException {
    assertEquals("LineAxisPanel",
        loadComponent("ArgumentListPropertyView").getSuperclass().getSimpleName());
  }

  @Test
  public void argumentListPropertyView_hasTypeParameterN() throws ClassNotFoundException {
    TypeVariable<?>[] params = loadComponent("ArgumentListPropertyView").getTypeParameters();
    assertEquals(1, params.length);
    assertEquals("N", params[0].getName());
  }

  @Test
  public void argumentListPropertyView_hasSeparatorField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("ArgumentListPropertyView").getDeclaredField("SEPARATOR");
    assertTrue(Modifier.isProtected(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(String.class, f.getType());
  }

  @Test
  public void argumentListPropertyView_hasFactoryField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("ArgumentListPropertyView").getDeclaredField("factory");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void argumentListPropertyView_hasArgumentListPropertyField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("ArgumentListPropertyView").getDeclaredField("argumentListProperty");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void argumentListPropertyView_hasListPropertyAdapterField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("ArgumentListPropertyView").getDeclaredField("listPropertyAdapter");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void argumentListPropertyView_hasGetFactory() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("ArgumentListPropertyView").getMethod("getFactory");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void argumentListPropertyView_hasGetArgumentListProperty() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("ArgumentListPropertyView").getMethod("getArgumentListProperty");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void argumentListPropertyView_hasAbstractGetInitialPrefix() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("ArgumentListPropertyView").getDeclaredMethod("getInitialPrefix");
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(String.class, m.getReturnType());
  }

  @Test
  public void argumentListPropertyView_hasGetBoxLayoutPad() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("ArgumentListPropertyView").getDeclaredMethod("getBoxLayoutPad");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  // ========================================================================
  // ArgumentListPropertyPane — extends AbstractArgumentListPropertyPane
  // ========================================================================

  @Test
  public void argumentListPropertyPane_isLoadable() throws ClassNotFoundException {
    loadComponent("ArgumentListPropertyPane");
  }

  @Test
  public void argumentListPropertyPane_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("ArgumentListPropertyPane").getModifiers()));
  }

  @Test
  public void argumentListPropertyPane_extendsAbstractArgumentListPropertyPane() throws ClassNotFoundException {
    assertEquals("AbstractArgumentListPropertyPane",
        loadComponent("ArgumentListPropertyPane").getSuperclass().getSimpleName());
  }

  @Test
  public void argumentListPropertyPane_constructorTakesImmutableFactory() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ArgumentListPropertyPane");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    Class<?>[] params = ctors[0].getParameterTypes();
    assertEquals(2, params.length);
    assertEquals("ImmutableAstI18nFactory", params[0].getSimpleName());
    assertEquals("SimpleArgumentListProperty", params[1].getSimpleName());
  }

  @Test
  public void argumentListPropertyPane_hasCreateComponent() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = loadComponent("ArgumentListPropertyPane");
    Method m = cls.getDeclaredMethod("createComponent",
        org.lgna.project.ast.SimpleArgument.class);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void argumentListPropertyPane_hasIsComponentDesiredFor() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ArgumentListPropertyPane");
    for (Method m : cls.getDeclaredMethods()) {
      if (m.getName().equals("isComponentDesiredFor")) {
        assertTrue(Modifier.isProtected(m.getModifiers()));
        assertEquals(boolean.class, m.getReturnType());
        return;
      }
    }
    fail("isComponentDesiredFor method not found");
  }

  // ========================================================================
  // KeyedArgumentView — extends ArgumentView<JavaKeyedArgument>
  // ========================================================================

  @Test
  public void keyedArgumentView_isLoadable() throws ClassNotFoundException {
    loadComponent("KeyedArgumentView");
  }

  @Test
  public void keyedArgumentView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("KeyedArgumentView").getModifiers()));
  }

  @Test
  public void keyedArgumentView_extendsArgumentView() throws ClassNotFoundException {
    assertEquals("ArgumentView",
        loadComponent("KeyedArgumentView").getSuperclass().getSimpleName());
  }

  @Test
  public void keyedArgumentView_genericSuperUsesJavaKeyedArgument() throws ClassNotFoundException {
    Class<?> cls = loadComponent("KeyedArgumentView");
    Type genericSuper = cls.getGenericSuperclass();
    assertTrue(genericSuper instanceof ParameterizedType);
    ParameterizedType pt = (ParameterizedType) genericSuper;
    assertTrue(pt.getActualTypeArguments()[0].toString().contains("JavaKeyedArgument"));
  }

  @Test
  public void keyedArgumentView_hasConstructor() throws ClassNotFoundException {
    Class<?> cls = loadComponent("KeyedArgumentView");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertEquals(2, ctors[0].getParameterCount());
  }

  @Test
  public void keyedArgumentView_implementsGetName() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("KeyedArgumentView").getDeclaredMethod("getName");
    assertFalse(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(String.class, m.getReturnType());
  }

  @Test
  public void keyedArgumentView_overridesInternalRefresh() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("KeyedArgumentView").getDeclaredMethod("internalRefresh");
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  // ========================================================================
  // KeyedArgumentListPropertyView — extends ArgumentListPropertyView<JavaKeyedArgument>
  // ========================================================================

  @Test
  public void keyedArgumentListPropertyView_isLoadable() throws ClassNotFoundException {
    loadComponent("KeyedArgumentListPropertyView");
  }

  @Test
  public void keyedArgumentListPropertyView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("KeyedArgumentListPropertyView").getModifiers()));
  }

  @Test
  public void keyedArgumentListPropertyView_extendsArgumentListPropertyView() throws ClassNotFoundException {
    assertEquals("ArgumentListPropertyView",
        loadComponent("KeyedArgumentListPropertyView").getSuperclass().getSimpleName());
  }

  @Test
  public void keyedArgumentListPropertyView_genericSuperUsesJavaKeyedArgument() throws ClassNotFoundException {
    Class<?> cls = loadComponent("KeyedArgumentListPropertyView");
    Type genericSuper = cls.getGenericSuperclass();
    assertTrue(genericSuper instanceof ParameterizedType);
    ParameterizedType pt = (ParameterizedType) genericSuper;
    assertTrue(pt.getActualTypeArguments()[0].toString().contains("JavaKeyedArgument"));
  }

  @Test
  public void keyedArgumentListPropertyView_implementsGetInitialPrefix() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("KeyedArgumentListPropertyView").getDeclaredMethod("getInitialPrefix");
    assertFalse(Modifier.isAbstract(m.getModifiers()));
    assertEquals(String.class, m.getReturnType());
  }

  @Test
  public void keyedArgumentListPropertyView_overridesInternalRefresh() throws ClassNotFoundException, NoSuchMethodException {
    loadComponent("KeyedArgumentListPropertyView").getDeclaredMethod("internalRefresh");
  }

  // ========================================================================
  // NodePropertyView — extends AbstractPropertyPane<P, N>
  // ========================================================================

  @Test
  public void nodePropertyView_isLoadable() throws ClassNotFoundException {
    loadComponent("NodePropertyView");
  }

  @Test
  public void nodePropertyView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("NodePropertyView").getModifiers()));
  }

  @Test
  public void nodePropertyView_extendsAbstractPropertyPane() throws ClassNotFoundException {
    assertEquals("AbstractPropertyPane",
        loadComponent("NodePropertyView").getSuperclass().getSimpleName());
  }

  @Test
  public void nodePropertyView_hasTwoTypeParameters() throws ClassNotFoundException {
    TypeVariable<?>[] params = loadComponent("NodePropertyView").getTypeParameters();
    assertEquals(2, params.length);
    assertEquals("P", params[0].getName());
    assertEquals("N", params[1].getName());
  }

  @Test
  public void nodePropertyView_hasCreateComponent() throws ClassNotFoundException {
    Class<?> cls = loadComponent("NodePropertyView");
    for (Method m : cls.getDeclaredMethods()) {
      if (m.getName().equals("createComponent") && m.getParameterCount() == 1) {
        assertTrue(Modifier.isProtected(m.getModifiers()));
        return;
      }
    }
    fail("createComponent method not found");
  }

  @Test
  public void nodePropertyView_hasFinalInternalRefresh() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("NodePropertyView").getDeclaredMethod("internalRefresh");
    assertTrue(Modifier.isFinal(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void nodePropertyView_noDeclaredFields() throws ClassNotFoundException {
    assertEquals(0, loadComponent("NodePropertyView").getDeclaredFields().length);
  }

  // ========================================================================
  // FieldAccessView — extends AbstractExpressionView<FieldAccess>
  // ========================================================================

  @Test
  public void fieldAccessView_isLoadable() throws ClassNotFoundException {
    loadComponent("FieldAccessView");
  }

  @Test
  public void fieldAccessView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("FieldAccessView").getModifiers()));
  }

  @Test
  public void fieldAccessView_extendsAbstractExpressionView() throws ClassNotFoundException {
    assertEquals("AbstractExpressionView",
        loadComponent("FieldAccessView").getSuperclass().getSimpleName());
  }

  @Test
  public void fieldAccessView_genericSuperUsesFieldAccess() throws ClassNotFoundException {
    Type genericSuper = loadComponent("FieldAccessView").getGenericSuperclass();
    assertTrue(genericSuper instanceof ParameterizedType);
    assertTrue(((ParameterizedType) genericSuper)
        .getActualTypeArguments()[0].toString().contains("FieldAccess"));
  }

  @Test
  public void fieldAccessView_hasReplacementField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("FieldAccessView").getDeclaredField("replacement");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void fieldAccessView_overridesIsExpressionTypeFeedbackDesired() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("FieldAccessView").getDeclaredMethod("isExpressionTypeFeedbackDesired");
    assertFalse(Modifier.isAbstract(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void fieldAccessView_overridesGetExpressionType() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("FieldAccessView").getDeclaredMethod("getExpressionType");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ========================================================================
  // InfixExpressionView — extends AbstractExpressionView<InfixExpression>
  // ========================================================================

  @Test
  public void infixExpressionView_isLoadable() throws ClassNotFoundException {
    loadComponent("InfixExpressionView");
  }

  @Test
  public void infixExpressionView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("InfixExpressionView").getModifiers()));
  }

  @Test
  public void infixExpressionView_extendsAbstractExpressionView() throws ClassNotFoundException {
    assertEquals("AbstractExpressionView",
        loadComponent("InfixExpressionView").getSuperclass().getSimpleName());
  }

  @Test
  public void infixExpressionView_genericSuperUsesInfixExpression() throws ClassNotFoundException {
    Type genericSuper = loadComponent("InfixExpressionView").getGenericSuperclass();
    assertTrue(genericSuper instanceof ParameterizedType);
    assertTrue(((ParameterizedType) genericSuper)
        .getActualTypeArguments()[0].toString().contains("InfixExpression"));
  }

  @Test
  public void infixExpressionView_hasScaledExpressionsField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("InfixExpressionView").getDeclaredField("scaledExpressions");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void infixExpressionView_noDeclaredMethods() throws ClassNotFoundException {
    assertEquals(0, java.util.Arrays.stream(loadComponent("InfixExpressionView").getDeclaredMethods())
        .filter(method -> !method.isSynthetic())
        .count());
  }

  // ========================================================================
  // InstanceCreationView — extends AbstractExpressionView<InstanceCreation>
  // ========================================================================

  @Test
  public void instanceCreationView_isLoadable() throws ClassNotFoundException {
    loadComponent("InstanceCreationView");
  }

  @Test
  public void instanceCreationView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("InstanceCreationView").getModifiers()));
  }

  @Test
  public void instanceCreationView_extendsAbstractExpressionView() throws ClassNotFoundException {
    assertEquals("AbstractExpressionView",
        loadComponent("InstanceCreationView").getSuperclass().getSimpleName());
  }

  @Test
  public void instanceCreationView_genericSuperUsesInstanceCreation() throws ClassNotFoundException {
    Type genericSuper = loadComponent("InstanceCreationView").getGenericSuperclass();
    assertTrue(genericSuper instanceof ParameterizedType);
    assertTrue(((ParameterizedType) genericSuper)
        .getActualTypeArguments()[0].toString().contains("InstanceCreation"));
  }

  @Test
  public void instanceCreationView_noDeclaredMethods() throws ClassNotFoundException {
    assertEquals(0, java.util.Arrays.stream(loadComponent("InstanceCreationView").getDeclaredMethods())
        .filter(method -> !method.isSynthetic())
        .count());
  }

  @Test
  public void instanceCreationView_noDeclaredFields() throws ClassNotFoundException {
    assertEquals(0, loadComponent("InstanceCreationView").getDeclaredFields().length);
  }

  @Test
  public void instanceCreationView_hasConstructor() throws ClassNotFoundException {
    Constructor<?>[] ctors = loadComponent("InstanceCreationView").getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertEquals(2, ctors[0].getParameterCount());
    assertTrue(Modifier.isPublic(ctors[0].getModifiers()));
  }

  // ========================================================================
  // InstancePropertyLabelView — extends AbstractPropertyPane
  // ========================================================================

  @Test
  public void instancePropertyLabelView_isLoadable() throws ClassNotFoundException {
    loadComponent("InstancePropertyLabelView");
  }

  @Test
  public void instancePropertyLabelView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("InstancePropertyLabelView").getModifiers()));
  }

  @Test
  public void instancePropertyLabelView_extendsAbstractPropertyPane() throws ClassNotFoundException {
    assertEquals("AbstractPropertyPane",
        loadComponent("InstancePropertyLabelView").getSuperclass().getSimpleName());
  }

  @Test
  public void instancePropertyLabelView_hasLabelField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("InstancePropertyLabelView").getDeclaredField("label");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void instancePropertyLabelView_hasInternalRefresh() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("InstancePropertyLabelView").getDeclaredMethod("internalRefresh");
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // ========================================================================
  // ListPropertyLabelsView — extends AbstractListPropertyPane
  // ========================================================================

  @Test
  public void listPropertyLabelsView_isLoadable() throws ClassNotFoundException {
    loadComponent("ListPropertyLabelsView");
  }

  @Test
  public void listPropertyLabelsView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("ListPropertyLabelsView").getModifiers()));
  }

  @Test
  public void listPropertyLabelsView_extendsAbstractListPropertyPane() throws ClassNotFoundException {
    assertEquals("AbstractListPropertyPane",
        loadComponent("ListPropertyLabelsView").getSuperclass().getSimpleName());
  }

  @Test
  public void listPropertyLabelsView_hasCreateComponent() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ListPropertyLabelsView");
    for (Method m : cls.getDeclaredMethods()) {
      if (m.getName().equals("createComponent")) {
        assertTrue(Modifier.isProtected(m.getModifiers()));
        return;
      }
    }
    fail("createComponent not found");
  }

  @Test
  public void listPropertyLabelsView_noDeclaredFields() throws ClassNotFoundException {
    assertEquals(0, loadComponent("ListPropertyLabelsView").getDeclaredFields().length);
  }

  // ========================================================================
  // ResourcePropertyView — extends AbstractPropertyPane
  // ========================================================================

  @Test
  public void resourcePropertyView_isLoadable() throws ClassNotFoundException {
    loadComponent("ResourcePropertyView");
  }

  @Test
  public void resourcePropertyView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("ResourcePropertyView").getModifiers()));
  }

  @Test
  public void resourcePropertyView_extendsAbstractPropertyPane() throws ClassNotFoundException {
    assertEquals("AbstractPropertyPane",
        loadComponent("ResourcePropertyView").getSuperclass().getSimpleName());
  }

  @Test
  public void resourcePropertyView_hasDurationFormatField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("ResourcePropertyView").getDeclaredField("DURATION_FORMAT");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void resourcePropertyView_hasLabelField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("ResourcePropertyView").getDeclaredField("label");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void resourcePropertyView_hasPrevResourceField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("ResourcePropertyView").getDeclaredField("prevResource");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void resourcePropertyView_hasNameListenerField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("ResourcePropertyView").getDeclaredField("nameListener");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void resourcePropertyView_hasInternalRefresh() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("ResourcePropertyView").getDeclaredMethod("internalRefresh");
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void resourcePropertyView_hasHandleDisplayable() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("ResourcePropertyView").getDeclaredMethod("handleDisplayable");
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void resourcePropertyView_hasHandleUndisplayable() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("ResourcePropertyView").getDeclaredMethod("handleUndisplayable");
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // ========================================================================
  // StatementListPropertyView — extends AbstractListPropertyPane
  // ========================================================================

  @Test
  public void statementListPropertyView_isLoadable() throws ClassNotFoundException {
    loadComponent("StatementListPropertyView");
  }

  @Test
  public void statementListPropertyView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("StatementListPropertyView").getModifiers()));
  }

  @Test
  public void statementListPropertyView_extendsAbstractListPropertyPane() throws ClassNotFoundException {
    assertEquals("AbstractListPropertyPane",
        loadComponent("StatementListPropertyView").getSuperclass().getSimpleName());
  }

  @Test
  public void statementListPropertyView_hasIndentField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("StatementListPropertyView").getDeclaredField("INDENT");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(int.class, f.getType());
  }

  @Test
  public void statementListPropertyView_hasIntrasticialMiddleField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("StatementListPropertyView").getDeclaredField("INTRASTICIAL_MIDDLE");
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void statementListPropertyView_hasPublicIntrasticialPadField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("StatementListPropertyView").getDeclaredField("INTRASTICIAL_PAD");
    assertTrue(Modifier.isPublic(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(int.class, f.getType());
  }

  @Test
  public void statementListPropertyView_hasStatementListBorderField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("StatementListPropertyView").getDeclaredField("statementListBorder");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void statementListPropertyView_hasFeedbackColorField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("StatementListPropertyView").getDeclaredField("FEEDBACK_COLOR");
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(java.awt.Color.class, f.getType());
  }

  @Test
  public void statementListPropertyView_hasEpicHackField() throws ClassNotFoundException, NoSuchFieldException {
    Field f = loadComponent("StatementListPropertyView").getDeclaredField("EPIC_HACK_ignoreDrawingDesired");
    assertTrue(Modifier.isPublic(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void statementListPropertyView_hasTwoConstructors() throws ClassNotFoundException {
    Constructor<?>[] ctors = loadComponent("StatementListPropertyView").getDeclaredConstructors();
    assertEquals(2, ctors.length);
  }

  @Test
  public void statementListPropertyView_hasThreeArgConstructor() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    boolean found = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (c.getParameterCount() == 3) {
        found = true;
        Class<?>[] params = c.getParameterTypes();
        assertEquals("AstI18nFactory", params[0].getSimpleName());
        assertEquals("StatementListProperty", params[1].getSimpleName());
        assertEquals(int.class, params[2]);
      }
    }
    assertTrue("3-arg constructor not found", found);
  }

  @Test
  public void statementListPropertyView_hasGetStatementListBorder() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("StatementListPropertyView").getMethod("getStatementListBorder");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void statementListPropertyView_hasIsAcceptingOfAddEventListener() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("StatementListPropertyView").getMethod(
        "isAcceptingOfAddEventListenerMethodInvocationStatements");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void statementListPropertyView_hasSetIsCurrentUnder() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("StatementListPropertyView").getMethod("setIsCurrentUnder", boolean.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void statementListPropertyView_hasGetCurrentPotentialDropIndex() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("StatementListPropertyView").getMethod("getCurrentPotentialDropIndex");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void statementListPropertyView_hasCalculateIndex() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("StatementListPropertyView").getMethod("calculateIndex",
        java.awt.Point.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void statementListPropertyView_hasCalculateYBounds() throws ClassNotFoundException, NoSuchMethodException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Method m = cls.getMethod("calculateYBounds", int.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void statementListPropertyView_hasIsEmpty() throws ClassNotFoundException, NoSuchMethodException {
    Method m = loadComponent("StatementListPropertyView").getMethod("isEmpty");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void statementListPropertyView_hasFeedbackJPanelInnerClass() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    for (Class<?> inner : cls.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("FeedbackJPanel")) {
        assertTrue(Modifier.isPublic(inner.getModifiers()));
        assertFalse(Modifier.isStatic(inner.getModifiers()));
        return;
      }
    }
    fail("FeedbackJPanel inner class not found");
  }

  @Test
  public void statementListPropertyView_hasBoundInformationInnerClass() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    for (Class<?> inner : cls.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("BoundInformation")) {
        assertTrue(Modifier.isPublic(inner.getModifiers()));
        assertTrue(Modifier.isStatic(inner.getModifiers()));
        return;
      }
    }
    fail("BoundInformation inner class not found");
  }

  @Test
  public void statementListPropertyView_feedbackJPanelExtendsDefaultJPanel() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    for (Class<?> inner : cls.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("FeedbackJPanel")) {
        assertEquals("DefaultJPanel", inner.getSuperclass().getSimpleName());
      }
    }
  }

  // ========================================================================
  // ExpressionListPropertyPane — extends AbstractListPropertyPane
  // ========================================================================

  @Test
  public void expressionListPropertyPane_isLoadable() throws ClassNotFoundException {
    loadComponent("ExpressionListPropertyPane");
  }

  @Test
  public void expressionListPropertyPane_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("ExpressionListPropertyPane").getModifiers()));
  }

  @Test
  public void expressionListPropertyPane_extendsAbstractListPropertyPane() throws ClassNotFoundException {
    assertEquals("AbstractListPropertyPane",
        loadComponent("ExpressionListPropertyPane").getSuperclass().getSimpleName());
  }

  @Test
  public void expressionListPropertyPane_hasCreateInterstitial() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ExpressionListPropertyPane");
    for (Method m : cls.getDeclaredMethods()) {
      if (m.getName().equals("createInterstitial")) {
        assertTrue(Modifier.isProtected(m.getModifiers()));
        return;
      }
    }
    fail("createInterstitial not found");
  }

  @Test
  public void expressionListPropertyPane_hasCreateComponent() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ExpressionListPropertyPane");
    for (Method m : cls.getDeclaredMethods()) {
      if (m.getName().equals("createComponent")) {
        assertTrue(Modifier.isProtected(m.getModifiers()));
        return;
      }
    }
    fail("createComponent not found");
  }

  @Test
  public void expressionListPropertyPane_noDeclaredFields() throws ClassNotFoundException {
    assertEquals(0, loadComponent("ExpressionListPropertyPane").getDeclaredFields().length);
  }

  @Test
  public void expressionListPropertyPane_hasConstructor() throws ClassNotFoundException {
    Constructor<?>[] ctors = loadComponent("ExpressionListPropertyPane").getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertEquals(2, ctors[0].getParameterCount());
  }

  // ========================================================================
  // ThisExpressionLikeView — extends AbstractExpressionView<Expression>
  // ========================================================================

  @Test
  public void thisExpressionLikeView_isLoadable() throws ClassNotFoundException {
    loadComponent("ThisExpressionLikeView");
  }

  @Test
  public void thisExpressionLikeView_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadComponent("ThisExpressionLikeView").getModifiers()));
  }

  @Test
  public void thisExpressionLikeView_extendsAbstractExpressionView() throws ClassNotFoundException {
    assertEquals("AbstractExpressionView",
        loadComponent("ThisExpressionLikeView").getSuperclass().getSimpleName());
  }

  @Test
  public void thisExpressionLikeView_genericSuperUsesExpression() throws ClassNotFoundException {
    Type genericSuper = loadComponent("ThisExpressionLikeView").getGenericSuperclass();
    assertTrue(genericSuper instanceof ParameterizedType);
    assertTrue(((ParameterizedType) genericSuper)
        .getActualTypeArguments()[0].toString().contains("Expression"));
  }

  @Test
  public void thisExpressionLikeView_hasThreeConstructors() throws ClassNotFoundException {
    Constructor<?>[] ctors = loadComponent("ThisExpressionLikeView").getDeclaredConstructors();
    assertEquals(3, ctors.length);
  }

  @Test
  public void thisExpressionLikeView_hasPrivateExpressionConstructor() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ThisExpressionLikeView");
    boolean foundPrivate = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (Modifier.isPrivate(c.getModifiers())) {
        foundPrivate = true;
        assertEquals(2, c.getParameterCount());
      }
    }
    assertTrue("Private constructor not found", foundPrivate);
  }

  @Test
  public void thisExpressionLikeView_hasPublicThisExpressionConstructor() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ThisExpressionLikeView");
    boolean found = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (Modifier.isPublic(c.getModifiers())) {
        Class<?>[] params = c.getParameterTypes();
        if (params.length == 2 && params[1].getSimpleName().equals("ThisExpression")) {
          found = true;
        }
      }
    }
    assertTrue("Public ThisExpression constructor not found", found);
  }

  @Test
  public void thisExpressionLikeView_hasPublicCurrentThisExpressionConstructor() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ThisExpressionLikeView");
    boolean found = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (Modifier.isPublic(c.getModifiers())) {
        Class<?>[] params = c.getParameterTypes();
        if (params.length == 2 && params[1].getSimpleName().equals("CurrentThisExpression")) {
          found = true;
        }
      }
    }
    assertTrue("Public CurrentThisExpression constructor not found", found);
  }

  @Test
  public void thisExpressionLikeView_noDeclaredMethods() throws ClassNotFoundException {
    assertEquals(0, java.util.Arrays.stream(loadComponent("ThisExpressionLikeView").getDeclaredMethods())
        .filter(method -> !method.isSynthetic())
        .count());
  }

  @Test
  public void thisExpressionLikeView_noDeclaredFields() throws ClassNotFoundException {
    assertEquals(0, loadComponent("ThisExpressionLikeView").getDeclaredFields().length);
  }

  // ========================================================================
  // Cross-cutting: all 18 classes are in correct package
  // ========================================================================

  @Test
  public void allComponentClasses_inCorrectPackage() throws ClassNotFoundException {
    for (String name : ALL_COMPONENT_NAMES) {
      Class<?> cls = loadComponent(name);
      assertEquals("org.alice.ide.x.components", cls.getPackage().getName());
    }
  }

  @Test
  public void allComponentClasses_arePublic() throws ClassNotFoundException {
    for (String name : ALL_COMPONENT_NAMES) {
      Class<?> cls = loadComponent(name);
      assertTrue(name + " should be public", Modifier.isPublic(cls.getModifiers()));
    }
  }

  @Test
  public void allComponentClasses_firstConstructorParamIsFactory() throws ClassNotFoundException {
    // ArgumentView and ThisExpressionLikeView have different constructor patterns
    String[] factoryCtorNames = {
        "AbstractExpressionView", "ExpressionView", "ExpressionPropertyView",
        "ArgumentListPropertyView", "ArgumentListPropertyPane",
        "KeyedArgumentView", "KeyedArgumentListPropertyView",
        "NodePropertyView", "FieldAccessView", "InfixExpressionView",
        "InstanceCreationView", "InstancePropertyLabelView", "ListPropertyLabelsView",
        "ResourcePropertyView", "StatementListPropertyView",
        "ExpressionListPropertyPane"
    };
    for (String name : factoryCtorNames) {
      Class<?> cls = loadComponent(name);
      Constructor<?>[] ctors = cls.getDeclaredConstructors();
      assertTrue(name + " should have at least one constructor", ctors.length > 0);
      boolean hasFactoryParam = false;
      for (Constructor<?> c : ctors) {
        if (c.getParameterCount() > 0) {
          String paramName = c.getParameterTypes()[0].getSimpleName();
          if (paramName.contains("AstI18nFactory") || paramName.contains("I18nFactory")) {
            hasFactoryParam = true;
            break;
          }
        }
      }
      assertTrue(name + " first ctor param should be a factory type", hasFactoryParam);
    }
  }

  // ArgumentView has factory param too
  @Test
  public void argumentView_firstConstructorParamIsFactory() throws ClassNotFoundException {
    Class<?> cls = loadComponent("ArgumentView");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
    assertEquals("AstI18nFactory", ctors[0].getParameterTypes()[0].getSimpleName());
  }
}

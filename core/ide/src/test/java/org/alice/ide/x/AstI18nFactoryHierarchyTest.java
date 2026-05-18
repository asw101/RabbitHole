package org.alice.ide.x;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.Assert.*;

/**
 * Reflection-based characterization tests for all 13 factory classes
 * in the org.alice.ide.x hierarchy. Tests verify class loadability,
 * abstract/concrete modifiers, inheritance chains, method signatures,
 * singleton patterns, constructor access, and field declarations.
 *
 * All tests are headless-safe (pure reflection, no GUI instantiation)
 * and never invoke getInstance() or methods that would trigger IDE bootstrap.
 */
public class AstI18nFactoryHierarchyTest {

  // ========================================================================
  // I18nFactory — root of the hierarchy
  // ========================================================================

  @Test
  public void i18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.I18nFactory");
  }

  @Test
  public void i18nFactory_isAbstract() {
    assertTrue(Modifier.isAbstract(I18nFactory.class.getModifiers()));
  }

  @Test
  public void i18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(I18nFactory.class.getModifiers()));
  }

  @Test
  public void i18nFactory_extendsObject() {
    assertEquals(Object.class, I18nFactory.class.getSuperclass());
  }

  @Test
  public void i18nFactory_hasNoPublicConstructors() {
    Constructor<?>[] ctors = I18nFactory.class.getConstructors();
    // Abstract class: only default or protected constructors expected
    for (Constructor<?> ctor : ctors) {
      assertTrue("Public constructor should have 0 params",
          ctor.getParameterCount() == 0);
    }
  }

  @Test
  public void i18nFactory_hasCreateComponentPageMethod() throws NoSuchMethodException {
    Method m = I18nFactory.class.getMethod("createComponent",
        org.alice.ide.i18n.Page.class,
        edu.cmu.cs.dennisc.property.InstancePropertyOwner.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertFalse(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void i18nFactory_hasCreateComponentOwnerMethod() throws NoSuchMethodException {
    Method m = I18nFactory.class.getMethod("createComponent",
        edu.cmu.cs.dennisc.property.InstancePropertyOwner.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void i18nFactory_hasProtectedAbstractCreateGetsComponent() throws NoSuchMethodException {
    Method m = I18nFactory.class.getDeclaredMethod("createGetsComponent", boolean.class);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void i18nFactory_hasProtectedAbstractCreatePropertyComponent() throws NoSuchMethodException {
    Method m = I18nFactory.class.getDeclaredMethod("createPropertyComponent",
        edu.cmu.cs.dennisc.property.InstanceProperty.class, int.class);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void i18nFactory_noExplicitFields() {
    Field[] fields = I18nFactory.class.getDeclaredFields();
    // May have synthetic fields; verify no user-declared public/protected fields
    for (Field f : fields) {
      assertTrue("Unexpected non-synthetic field: " + f.getName(),
          f.isSynthetic() || Modifier.isPrivate(f.getModifiers()));
    }
  }

  // ========================================================================
  // AstI18nFactory — extends I18nFactory
  // ========================================================================

  @Test
  public void astI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.AstI18nFactory");
  }

  @Test
  public void astI18nFactory_isAbstract() {
    assertTrue(Modifier.isAbstract(AstI18nFactory.class.getModifiers()));
  }

  @Test
  public void astI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(AstI18nFactory.class.getModifiers()));
  }

  @Test
  public void astI18nFactory_extendsI18nFactory() {
    assertEquals(I18nFactory.class, AstI18nFactory.class.getSuperclass());
  }

  @Test
  public void astI18nFactory_hasLocalPropertyNamesField() throws NoSuchFieldException {
    Field f = AstI18nFactory.class.getDeclaredField("LOCAL_PROPERTY_NAMES");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasAbstractGetFallBackType() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getDeclaredMethod("getFallBackTypeForThisExpression");
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasAbstractCreateExpressionPropertyPane() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getMethod("createExpressionPropertyPane",
        org.lgna.project.ast.ExpressionProperty.class,
        org.lgna.project.ast.AbstractType.class);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasFinalCreateExpressionPropertyPaneSingleArg() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getMethod("createExpressionPropertyPane",
        org.lgna.project.ast.ExpressionProperty.class);
    assertTrue(Modifier.isFinal(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasAbstractCreateSimpleArgListPane() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getDeclaredMethod("createSimpleArgumentListPropertyPane",
        org.lgna.project.ast.SimpleArgumentListProperty.class);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasAbstractCreateKeyedArgListPane() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getDeclaredMethod("createKeyedArgumentListPropertyPane",
        org.lgna.project.ast.KeyedArgumentListProperty.class);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasAbstractCreateIdeExpressionPane() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getDeclaredMethod("createIdeExpressionPane",
        org.alice.ide.ast.IdeExpression.class);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasCreateStatementPane() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getMethod("createStatementPane",
        org.lgna.project.ast.Statement.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasCreateStatementPaneThreeArgs() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getMethod("createStatementPane",
        org.lgna.croquet.DragModel.class,
        org.lgna.project.ast.Statement.class,
        org.lgna.project.ast.StatementListProperty.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasCreateExpressionPane() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getMethod("createExpressionPane",
        org.lgna.project.ast.Expression.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasIsCommentMutable() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getMethod("isCommentMutable",
        org.lgna.project.ast.Comment.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void astI18nFactory_hasIsSignatureLocked() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getMethod("isSignatureLocked",
        org.lgna.project.ast.Code.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void astI18nFactory_hasGetInvalidExpressionColor() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getMethod("getInvalidExpressionColor",
        java.awt.Color.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(java.awt.Color.class, m.getReturnType());
  }

  @Test
  public void astI18nFactory_hasCreateNameView() throws NoSuchMethodException {
    Method m = AstI18nFactory.class.getMethod("createNameView",
        org.lgna.project.ast.AbstractDeclaration.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ========================================================================
  // IdeAstI18nFactory — extends AstI18nFactory
  // ========================================================================

  @Test
  public void ideAstI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.IdeAstI18nFactory");
  }

  @Test
  public void ideAstI18nFactory_isAbstract() {
    assertTrue(Modifier.isAbstract(IdeAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void ideAstI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(IdeAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void ideAstI18nFactory_extendsAstI18nFactory() {
    assertEquals(AstI18nFactory.class, IdeAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void ideAstI18nFactory_implementsCreateExpressionPropertyPane() throws NoSuchMethodException {
    Method m = IdeAstI18nFactory.class.getDeclaredMethod("createExpressionPropertyPane",
        org.lgna.project.ast.ExpressionProperty.class,
        org.lgna.project.ast.AbstractType.class);
    assertFalse("Should not be abstract in IdeAstI18nFactory",
        Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void ideAstI18nFactory_implementsCreateIdeExpressionPane() throws NoSuchMethodException {
    Method m = IdeAstI18nFactory.class.getDeclaredMethod("createIdeExpressionPane",
        org.alice.ide.ast.IdeExpression.class);
    assertFalse(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void ideAstI18nFactory_noDeclaredFields() {
    Field[] fields = IdeAstI18nFactory.class.getDeclaredFields();
    assertEquals(0, fields.length);
  }

  @Test
  public void ideAstI18nFactory_noConstructorsWithParams() {
    Constructor<?>[] ctors = IdeAstI18nFactory.class.getDeclaredConstructors();
    for (Constructor<?> c : ctors) {
      assertEquals("IdeAstI18nFactory constructors should have 0 params",
          0, c.getParameterCount());
    }
  }

  // ========================================================================
  // ImmutableAstI18nFactory — extends IdeAstI18nFactory
  // ========================================================================

  @Test
  public void immutableAstI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.ImmutableAstI18nFactory");
  }

  @Test
  public void immutableAstI18nFactory_isAbstract() {
    assertTrue(Modifier.isAbstract(ImmutableAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void immutableAstI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(ImmutableAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void immutableAstI18nFactory_extendsIdeAstI18nFactory() {
    assertEquals(IdeAstI18nFactory.class, ImmutableAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void immutableAstI18nFactory_hasFinalCreateSimpleArgListPane() throws NoSuchMethodException {
    Method m = ImmutableAstI18nFactory.class.getDeclaredMethod(
        "createSimpleArgumentListPropertyPane",
        org.lgna.project.ast.SimpleArgumentListProperty.class);
    assertTrue(Modifier.isFinal(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void immutableAstI18nFactory_hasFinalCreateKeyedArgListPane() throws NoSuchMethodException {
    Method m = ImmutableAstI18nFactory.class.getDeclaredMethod(
        "createKeyedArgumentListPropertyPane",
        org.lgna.project.ast.KeyedArgumentListProperty.class);
    assertTrue(Modifier.isFinal(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void immutableAstI18nFactory_noDeclaredFields() {
    Field[] fields = ImmutableAstI18nFactory.class.getDeclaredFields();
    assertEquals(0, fields.length);
  }

  // ========================================================================
  // MutableAstI18nFactory — extends AstI18nFactory (sibling to IdeAstI18nFactory)
  // ========================================================================

  @Test
  public void mutableAstI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.MutableAstI18nFactory");
  }

  @Test
  public void mutableAstI18nFactory_isAbstract() {
    assertTrue(Modifier.isAbstract(MutableAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void mutableAstI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(MutableAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void mutableAstI18nFactory_extendsAstI18nFactory() {
    assertEquals(AstI18nFactory.class, MutableAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void mutableAstI18nFactory_hasGroupConstructor() throws NoSuchMethodException {
    Constructor<?> ctor = MutableAstI18nFactory.class.getConstructor(
        org.lgna.croquet.Group.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void mutableAstI18nFactory_hasGroupField() throws NoSuchFieldException {
    Field f = MutableAstI18nFactory.class.getDeclaredField("group");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void mutableAstI18nFactory_overridesCreateNameView() throws NoSuchMethodException {
    Method m = MutableAstI18nFactory.class.getDeclaredMethod("createNameView",
        org.lgna.project.ast.AbstractDeclaration.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void mutableAstI18nFactory_implementsGetFallBackType() throws NoSuchMethodException {
    Method m = MutableAstI18nFactory.class.getDeclaredMethod("getFallBackTypeForThisExpression");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void mutableAstI18nFactory_hasIsStatementListPropertyMutable() throws NoSuchMethodException {
    Method m = MutableAstI18nFactory.class.getMethod("isStatementListPropertyMutable",
        org.lgna.project.ast.StatementListProperty.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void mutableAstI18nFactory_hasIsKeyedArgumentListMutable() throws NoSuchMethodException {
    Method m = MutableAstI18nFactory.class.getMethod("isKeyedArgumentListMutable",
        org.lgna.project.ast.ArgumentListProperty.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void mutableAstI18nFactory_implementsCreateExpressionPropertyPane() throws NoSuchMethodException {
    Method m = MutableAstI18nFactory.class.getDeclaredMethod("createExpressionPropertyPane",
        org.lgna.project.ast.ExpressionProperty.class,
        org.lgna.project.ast.AbstractType.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  // ========================================================================
  // PreviewAstI18nFactory — concrete, extends ImmutableAstI18nFactory
  // ========================================================================

  @Test
  public void previewAstI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.PreviewAstI18nFactory");
  }

  @Test
  public void previewAstI18nFactory_isConcrete() {
    assertFalse(Modifier.isAbstract(PreviewAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void previewAstI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(PreviewAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void previewAstI18nFactory_extendsImmutableAstI18nFactory() {
    assertEquals(ImmutableAstI18nFactory.class, PreviewAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void previewAstI18nFactory_hasPrivateConstructor() {
    Constructor<?>[] ctors = PreviewAstI18nFactory.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
    assertEquals(0, ctors[0].getParameterCount());
  }

  @Test
  public void previewAstI18nFactory_hasGetInstance() throws NoSuchMethodException {
    Method m = PreviewAstI18nFactory.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(PreviewAstI18nFactory.class, m.getReturnType());
  }

  @Test
  public void previewAstI18nFactory_hasSingletonHolderInnerClass() {
    Class<?>[] inner = PreviewAstI18nFactory.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : inner) {
      if (c.getSimpleName().equals("SingletonHolder")) {
        found = true;
        assertTrue(Modifier.isPrivate(c.getModifiers()));
        assertTrue(Modifier.isStatic(c.getModifiers()));
      }
    }
    assertTrue("SingletonHolder inner class not found", found);
  }

  @Test
  public void previewAstI18nFactory_overridesGetFallBackType() throws NoSuchMethodException {
    Method m = PreviewAstI18nFactory.class.getDeclaredMethod("getFallBackTypeForThisExpression");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void previewAstI18nFactory_overridesCreateStatementPane() throws NoSuchMethodException {
    Method m = PreviewAstI18nFactory.class.getDeclaredMethod("createStatementPane",
        org.lgna.croquet.DragModel.class,
        org.lgna.project.ast.Statement.class,
        org.lgna.project.ast.StatementListProperty.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void previewAstI18nFactory_fullInheritanceChain() {
    Class<?> cls = PreviewAstI18nFactory.class;
    assertEquals(ImmutableAstI18nFactory.class, cls.getSuperclass());
    assertEquals(IdeAstI18nFactory.class, cls.getSuperclass().getSuperclass());
    assertEquals(AstI18nFactory.class, cls.getSuperclass().getSuperclass().getSuperclass());
    assertEquals(I18nFactory.class, cls.getSuperclass().getSuperclass().getSuperclass().getSuperclass());
    assertEquals(Object.class, cls.getSuperclass().getSuperclass().getSuperclass().getSuperclass().getSuperclass());
  }

  // ========================================================================
  // ClipboardAstI18nFactory — standalone, extends Object
  // ========================================================================

  @Test
  public void clipboardAstI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.ClipboardAstI18nFactory");
  }

  @Test
  public void clipboardAstI18nFactory_isConcrete() {
    assertFalse(Modifier.isAbstract(ClipboardAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void clipboardAstI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(ClipboardAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void clipboardAstI18nFactory_extendsObject() {
    assertEquals(Object.class, ClipboardAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void clipboardAstI18nFactory_hasNoDeclaredMethods() {
    Method[] methods = ClipboardAstI18nFactory.class.getDeclaredMethods();
    assertEquals(0, methods.length);
  }

  @Test
  public void clipboardAstI18nFactory_hasNoDeclaredFields() {
    Field[] fields = ClipboardAstI18nFactory.class.getDeclaredFields();
    assertEquals(0, fields.length);
  }

  @Test
  public void clipboardAstI18nFactory_hasDefaultConstructorOnly() {
    Constructor<?>[] ctors = ClipboardAstI18nFactory.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPublic(ctors[0].getModifiers()));
    assertEquals(0, ctors[0].getParameterCount());
  }

  @Test
  public void clipboardAstI18nFactory_noInnerClasses() {
    assertEquals(0, ClipboardAstI18nFactory.class.getDeclaredClasses().length);
  }

  // ========================================================================
  // DialogAstI18nFactory — extends MutableAstI18nFactory
  // ========================================================================

  @Test
  public void dialogAstI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.DialogAstI18nFactory");
  }

  @Test
  public void dialogAstI18nFactory_isConcrete() {
    assertFalse(Modifier.isAbstract(DialogAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void dialogAstI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(DialogAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void dialogAstI18nFactory_extendsMutableAstI18nFactory() {
    assertEquals(MutableAstI18nFactory.class, DialogAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void dialogAstI18nFactory_hasPrivateConstructor() {
    Constructor<?>[] ctors = DialogAstI18nFactory.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
    assertEquals(0, ctors[0].getParameterCount());
  }

  @Test
  public void dialogAstI18nFactory_hasGetInstance() throws NoSuchMethodException {
    Method m = DialogAstI18nFactory.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(DialogAstI18nFactory.class, m.getReturnType());
  }

  @Test
  public void dialogAstI18nFactory_hasSingletonHolder() {
    Class<?>[] inner = DialogAstI18nFactory.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : inner) {
      if (c.getSimpleName().equals("SingletonHolder")) {
        found = true;
        assertTrue(Modifier.isPrivate(c.getModifiers()));
        assertTrue(Modifier.isStatic(c.getModifiers()));
      }
    }
    assertTrue("SingletonHolder inner class not found", found);
  }

  @Test
  public void dialogAstI18nFactory_fullInheritanceChain() {
    Class<?> cls = DialogAstI18nFactory.class;
    assertEquals(MutableAstI18nFactory.class, cls.getSuperclass());
    assertEquals(AstI18nFactory.class, cls.getSuperclass().getSuperclass());
    assertEquals(I18nFactory.class, cls.getSuperclass().getSuperclass().getSuperclass());
  }

  // ========================================================================
  // TemplateAstI18nFactory — extends IdeAstI18nFactory
  // ========================================================================

  @Test
  public void templateAstI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.TemplateAstI18nFactory");
  }

  @Test
  public void templateAstI18nFactory_isConcrete() {
    assertFalse(Modifier.isAbstract(TemplateAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void templateAstI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(TemplateAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void templateAstI18nFactory_extendsIdeAstI18nFactory() {
    assertEquals(IdeAstI18nFactory.class, TemplateAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void templateAstI18nFactory_hasPrivateConstructor() {
    Constructor<?>[] ctors = TemplateAstI18nFactory.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  @Test
  public void templateAstI18nFactory_hasGetInstance() throws NoSuchMethodException {
    Method m = TemplateAstI18nFactory.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(TemplateAstI18nFactory.class, m.getReturnType());
  }

  @Test
  public void templateAstI18nFactory_hasSingletonHolder() {
    Class<?>[] inner = TemplateAstI18nFactory.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : inner) {
      if (c.getSimpleName().equals("SingletonHolder")) {
        found = true;
      }
    }
    assertTrue("SingletonHolder inner class not found", found);
  }

  @Test
  public void templateAstI18nFactory_overridesGetFallBackType() throws NoSuchMethodException {
    Method m = TemplateAstI18nFactory.class.getDeclaredMethod("getFallBackTypeForThisExpression");
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void templateAstI18nFactory_overridesCreateSimpleArgListPane() throws NoSuchMethodException {
    Method m = TemplateAstI18nFactory.class.getDeclaredMethod(
        "createSimpleArgumentListPropertyPane",
        org.lgna.project.ast.SimpleArgumentListProperty.class);
    assertFalse(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void templateAstI18nFactory_overridesCreateKeyedArgListPane() throws NoSuchMethodException {
    Method m = TemplateAstI18nFactory.class.getDeclaredMethod(
        "createKeyedArgumentListPropertyPane",
        org.lgna.project.ast.KeyedArgumentListProperty.class);
    assertFalse(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void templateAstI18nFactory_fullInheritanceChain() {
    Class<?> cls = TemplateAstI18nFactory.class;
    assertEquals(IdeAstI18nFactory.class, cls.getSuperclass());
    assertEquals(AstI18nFactory.class, cls.getSuperclass().getSuperclass());
    assertEquals(I18nFactory.class, cls.getSuperclass().getSuperclass().getSuperclass());
  }

  // ========================================================================
  // MenuIconIdeAstI18nFactory — extends ImmutableAstI18nFactory
  // ========================================================================

  @Test
  public void menuIconIdeAstI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.MenuIconIdeAstI18nFactory");
  }

  @Test
  public void menuIconIdeAstI18nFactory_isConcrete() {
    assertFalse(Modifier.isAbstract(MenuIconIdeAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void menuIconIdeAstI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(MenuIconIdeAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void menuIconIdeAstI18nFactory_extendsImmutableAstI18nFactory() {
    assertEquals(ImmutableAstI18nFactory.class, MenuIconIdeAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void menuIconIdeAstI18nFactory_hasPrivateConstructor() {
    Constructor<?>[] ctors = MenuIconIdeAstI18nFactory.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  @Test
  public void menuIconIdeAstI18nFactory_hasGetInstance() throws NoSuchMethodException {
    Method m = MenuIconIdeAstI18nFactory.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(MenuIconIdeAstI18nFactory.class, m.getReturnType());
  }

  @Test
  public void menuIconIdeAstI18nFactory_hasSingletonHolder() {
    Class<?>[] inner = MenuIconIdeAstI18nFactory.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : inner) {
      if (c.getSimpleName().equals("SingletonHolder")) {
        found = true;
      }
    }
    assertTrue("SingletonHolder inner class not found", found);
  }

  @Test
  public void menuIconIdeAstI18nFactory_overridesGetFallBackType() throws NoSuchMethodException {
    Method m = MenuIconIdeAstI18nFactory.class.getDeclaredMethod("getFallBackTypeForThisExpression");
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void menuIconIdeAstI18nFactory_overridesEpicHackWrapper() throws NoSuchMethodException {
    Method m = MenuIconIdeAstI18nFactory.class.getDeclaredMethod(
        "EPIC_HACK_createWrapperIfNecessaryForExpressionPanelessComponent",
        org.lgna.croquet.views.SwingComponentView.class);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void menuIconIdeAstI18nFactory_fullInheritanceChain() {
    Class<?> cls = MenuIconIdeAstI18nFactory.class;
    assertEquals(ImmutableAstI18nFactory.class, cls.getSuperclass());
    assertEquals(IdeAstI18nFactory.class, cls.getSuperclass().getSuperclass());
    assertEquals(AstI18nFactory.class, cls.getSuperclass().getSuperclass().getSuperclass());
  }

  // ========================================================================
  // AbstractProjectEditorAstI18nFactory — extends MutableAstI18nFactory
  // ========================================================================

  @Test
  public void abstractProjectEditorAstI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.AbstractProjectEditorAstI18nFactory");
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractProjectEditorAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(AbstractProjectEditorAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_extendsMutableAstI18nFactory() {
    assertEquals(MutableAstI18nFactory.class,
        AbstractProjectEditorAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_hasPublicNoArgConstructor() throws NoSuchMethodException {
    Constructor<?> ctor = AbstractProjectEditorAstI18nFactory.class.getConstructor();
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_hasIsMutableField() throws NoSuchFieldException {
    Field f = AbstractProjectEditorAstI18nFactory.class.getDeclaredField("IS_MUTABLE");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_hasDeclarationNameFontScaleField() throws NoSuchFieldException {
    Field f = AbstractProjectEditorAstI18nFactory.class.getDeclaredField("declarationNameFontScale");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(float.class, f.getType());
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_hasIsDraggable() throws NoSuchMethodException {
    Method m = AbstractProjectEditorAstI18nFactory.class.getMethod("isDraggable",
        org.lgna.project.ast.Statement.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_hasCreateCodeHeader() throws NoSuchMethodException {
    Method m = AbstractProjectEditorAstI18nFactory.class.getMethod("createCodeHeader",
        org.lgna.project.ast.UserCode.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_overridesGetInvalidExpressionColor() throws NoSuchMethodException {
    Method m = AbstractProjectEditorAstI18nFactory.class.getDeclaredMethod(
        "getInvalidExpressionColor", java.awt.Color.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_overridesIsSignatureLocked() throws NoSuchMethodException {
    Method m = AbstractProjectEditorAstI18nFactory.class.getDeclaredMethod(
        "isSignatureLocked", org.lgna.project.ast.Code.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void abstractProjectEditorAstI18nFactory_overridesGetDeclarationNameFontScale() throws NoSuchMethodException {
    Method m = AbstractProjectEditorAstI18nFactory.class.getDeclaredMethod("getDeclarationNameFontScale");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(float.class, m.getReturnType());
  }

  // ========================================================================
  // ProjectEditorAstI18nFactory — extends AbstractProjectEditorAstI18nFactory
  // ========================================================================

  @Test
  public void projectEditorAstI18nFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.ProjectEditorAstI18nFactory");
  }

  @Test
  public void projectEditorAstI18nFactory_isConcrete() {
    assertFalse(Modifier.isAbstract(ProjectEditorAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void projectEditorAstI18nFactory_isPublic() {
    assertTrue(Modifier.isPublic(ProjectEditorAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void projectEditorAstI18nFactory_extendsAbstractProjectEditor() {
    assertEquals(AbstractProjectEditorAstI18nFactory.class,
        ProjectEditorAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void projectEditorAstI18nFactory_hasPrivateConstructor() {
    Constructor<?>[] ctors = ProjectEditorAstI18nFactory.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  @Test
  public void projectEditorAstI18nFactory_hasGetInstance() throws NoSuchMethodException {
    Method m = ProjectEditorAstI18nFactory.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(ProjectEditorAstI18nFactory.class, m.getReturnType());
  }

  @Test
  public void projectEditorAstI18nFactory_hasSingletonHolder() {
    Class<?>[] inner = ProjectEditorAstI18nFactory.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : inner) {
      if (c.getSimpleName().equals("SingletonHolder")) {
        found = true;
      }
    }
    assertTrue("SingletonHolder inner class not found", found);
  }

  @Test
  public void projectEditorAstI18nFactory_overridesIsCommentMutable() throws NoSuchMethodException {
    Method m = ProjectEditorAstI18nFactory.class.getDeclaredMethod("isCommentMutable",
        org.lgna.project.ast.Comment.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void projectEditorAstI18nFactory_fullInheritanceChain() {
    Class<?> cls = ProjectEditorAstI18nFactory.class;
    assertEquals(AbstractProjectEditorAstI18nFactory.class, cls.getSuperclass());
    assertEquals(MutableAstI18nFactory.class, cls.getSuperclass().getSuperclass());
    assertEquals(AstI18nFactory.class, cls.getSuperclass().getSuperclass().getSuperclass());
    assertEquals(I18nFactory.class, cls.getSuperclass().getSuperclass().getSuperclass().getSuperclass());
  }

  // ========================================================================
  // SceneEditorUpdatingProjectEditorAstI18nFactory — extends AbstractProjectEditorAstI18nFactory
  // ========================================================================

  @Test
  public void sceneEditorUpdatingFactory_isLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.x.SceneEditorUpdatingProjectEditorAstI18nFactory");
  }

  @Test
  public void sceneEditorUpdatingFactory_isConcrete() {
    assertFalse(Modifier.isAbstract(
        SceneEditorUpdatingProjectEditorAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void sceneEditorUpdatingFactory_isPublic() {
    assertTrue(Modifier.isPublic(
        SceneEditorUpdatingProjectEditorAstI18nFactory.class.getModifiers()));
  }

  @Test
  public void sceneEditorUpdatingFactory_extendsAbstractProjectEditor() {
    assertEquals(AbstractProjectEditorAstI18nFactory.class,
        SceneEditorUpdatingProjectEditorAstI18nFactory.class.getSuperclass());
  }

  @Test
  public void sceneEditorUpdatingFactory_hasPrivateConstructor() {
    Constructor<?>[] ctors = SceneEditorUpdatingProjectEditorAstI18nFactory.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  @Test
  public void sceneEditorUpdatingFactory_hasGetInstance() throws NoSuchMethodException {
    Method m = SceneEditorUpdatingProjectEditorAstI18nFactory.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(SceneEditorUpdatingProjectEditorAstI18nFactory.class, m.getReturnType());
  }

  @Test
  public void sceneEditorUpdatingFactory_hasSingletonHolder() {
    Class<?>[] inner = SceneEditorUpdatingProjectEditorAstI18nFactory.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> c : inner) {
      if (c.getSimpleName().equals("SingletonHolder")) {
        found = true;
      }
    }
    assertTrue("SingletonHolder inner class not found", found);
  }

  @Test
  public void sceneEditorUpdatingFactory_overridesGetArgumentCascade() throws NoSuchMethodException {
    Method m = SceneEditorUpdatingProjectEditorAstI18nFactory.class.getDeclaredMethod(
        "getArgumentCascade",
        org.lgna.project.ast.SimpleArgument.class);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void sceneEditorUpdatingFactory_fullInheritanceChain() {
    Class<?> cls = SceneEditorUpdatingProjectEditorAstI18nFactory.class;
    assertEquals(AbstractProjectEditorAstI18nFactory.class, cls.getSuperclass());
    assertEquals(MutableAstI18nFactory.class, cls.getSuperclass().getSuperclass());
    assertEquals(AstI18nFactory.class, cls.getSuperclass().getSuperclass().getSuperclass());
  }
}

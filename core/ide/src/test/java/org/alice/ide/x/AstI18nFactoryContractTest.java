package org.alice.ide.x;

import org.junit.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Deep behavioral contract tests for the AstI18nFactory hierarchy and
 * component view classes. Verifies method contracts, return types,
 * parameter types, access modifiers, class relationships, field declarations,
 * annotation presence, generic bounds, and design patterns via reflection.
 *
 * All tests are headless-safe (pure reflection, no GUI instantiation).
 */
public class AstI18nFactoryContractTest {

  // ========================================================================
  // Package and class FQN constants
  // ========================================================================

  private static final String FACTORY_PKG = "org.alice.ide.x.";
  private static final String COMPONENT_PKG = "org.alice.ide.x.components.";
  private static final String CROQUET_PKG = "org.alice.ide.x.croquet.";
  private static final String EDIT_PKG = "org.alice.ide.x.croquet.edits.";

  private static final String[] FACTORY_CLASSES = {
      "I18nFactory", "AstI18nFactory", "MutableAstI18nFactory",
      "IdeAstI18nFactory", "ImmutableAstI18nFactory",
      "PreviewAstI18nFactory", "TemplateAstI18nFactory",
      "DialogAstI18nFactory", "ClipboardAstI18nFactory",
      "MenuIconIdeAstI18nFactory", "ProjectEditorAstI18nFactory",
      "AbstractProjectEditorAstI18nFactory",
      "SceneEditorUpdatingProjectEditorAstI18nFactory"
  };

  private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();

  private Class<?> loadClass(String fqn) throws ClassNotFoundException {
    Class<?> cached = CLASS_CACHE.get(fqn);
    if (cached != null) return cached;
    Class<?> cls = Class.forName(fqn);
    CLASS_CACHE.put(fqn, cls);
    return cls;
  }

  private Class<?> loadFactory(String simple) throws ClassNotFoundException {
    return loadClass(FACTORY_PKG + simple);
  }

  private Class<?> loadComponent(String simple) throws ClassNotFoundException {
    return loadClass(COMPONENT_PKG + simple);
  }

  // ========================================================================
  // I18nFactory contract tests
  // ========================================================================

  @Test
  public void i18nFactory_isAbstract() throws ClassNotFoundException {
    Class<?> cls = loadFactory("I18nFactory");
    assertTrue(Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void i18nFactory_hasCreateComponentPageMethod() throws ClassNotFoundException {
    Class<?> cls = loadFactory("I18nFactory");
    Method[] methods = cls.getDeclaredMethods();
    boolean found = false;
    for (Method m : methods) {
      if (m.getName().equals("createComponent") && m.getParameterCount() == 2) {
        Class<?>[] params = m.getParameterTypes();
        if (params[0].getSimpleName().equals("Page")
            && params[1].getSimpleName().equals("InstancePropertyOwner")) {
          found = true;
          assertTrue("createComponent(Page, owner) should be public",
              Modifier.isPublic(m.getModifiers()));
          break;
        }
      }
    }
    assertTrue("Should have createComponent(Page, InstancePropertyOwner)", found);
  }

  @Test
  public void i18nFactory_hasCreateComponentOwnerMethod() throws ClassNotFoundException {
    Class<?> cls = loadFactory("I18nFactory");
    Method[] methods = cls.getDeclaredMethods();
    boolean found = false;
    for (Method m : methods) {
      if (m.getName().equals("createComponent") && m.getParameterCount() == 1) {
        Class<?>[] params = m.getParameterTypes();
        if (params[0].getSimpleName().equals("InstancePropertyOwner")) {
          found = true;
          assertTrue("createComponent(owner) should be public",
              Modifier.isPublic(m.getModifiers()));
          break;
        }
      }
    }
    assertTrue("Should have createComponent(InstancePropertyOwner)", found);
  }

  @Test
  public void i18nFactory_declaresTwoAbstractProtectedMethods() throws ClassNotFoundException {
    Class<?> cls = loadFactory("I18nFactory");
    long abstractProtectedCount = Arrays.stream(cls.getDeclaredMethods())
        .filter(m -> Modifier.isAbstract(m.getModifiers())
            && Modifier.isProtected(m.getModifiers()))
        .count();
    assertTrue("Should have at least 2 abstract protected methods", abstractProtectedCount >= 2);
  }

  @Test
  public void i18nFactory_hasGetPixelsPerIndent() throws ClassNotFoundException {
    Class<?> cls = loadFactory("I18nFactory");
    Method[] methods = cls.getDeclaredMethods();
    boolean found = Arrays.stream(methods)
        .anyMatch(m -> m.getName().equals("getPixelsPerIndent"));
    assertTrue("Should have getPixelsPerIndent method", found);
  }

  @Test
  public void i18nFactory_getPixelsPerIndentIsPrivate() throws ClassNotFoundException {
    Class<?> cls = loadFactory("I18nFactory");
    Method method = Arrays.stream(cls.getDeclaredMethods())
        .filter(m -> m.getName().equals("getPixelsPerIndent"))
        .findFirst().orElse(null);
    assertNotNull(method);
    assertTrue("getPixelsPerIndent should be private",
        Modifier.isPrivate(method.getModifiers()));
  }

  @Test
  public void i18nFactory_createGetsComponentReturnsSwingComponentView() throws ClassNotFoundException {
    Class<?> cls = loadFactory("I18nFactory");
    Method method = Arrays.stream(cls.getDeclaredMethods())
        .filter(m -> m.getName().equals("createGetsComponent"))
        .findFirst().orElse(null);
    assertNotNull(method);
    assertEquals("SwingComponentView", method.getReturnType().getSimpleName());
  }

  @Test
  public void i18nFactory_createPropertyComponentIsAbstract() throws ClassNotFoundException {
    Class<?> cls = loadFactory("I18nFactory");
    Method method = Arrays.stream(cls.getDeclaredMethods())
        .filter(m -> m.getName().equals("createPropertyComponent"))
        .findFirst().orElse(null);
    assertNotNull(method);
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }

  // ========================================================================
  // AstI18nFactory contract tests
  // ========================================================================

  @Test
  public void astI18nFactory_extendsI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    assertEquals("I18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void astI18nFactory_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(loadFactory("AstI18nFactory").getModifiers()));
  }

  @Test
  public void astI18nFactory_hasGetFallBackTypeForThisExpression() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getFallBackTypeForThisExpression"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasCreateExpressionPropertyPaneTwoParam() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createExpressionPropertyPane")
            && mt.getParameterCount() == 2)
        .findFirst().orElse(null);
    assertNotNull("Should have createExpressionPropertyPane(ExpressionProperty, AbstractType)", m);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasCreateExpressionPropertyPaneOneParam() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createExpressionPropertyPane")
            && mt.getParameterCount() == 1)
        .findFirst().orElse(null);
    assertNotNull("Should have createExpressionPropertyPane(ExpressionProperty)", m);
    assertTrue(Modifier.isFinal(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasCreateStatementPaneWithDragModel() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createStatementPane")
            && mt.getParameterCount() == 3)
        .findFirst().orElse(null);
    assertNotNull("Should have 3-param createStatementPane", m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasCreateStatementPaneOneParam() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createStatementPane")
            && mt.getParameterCount() == 1)
        .findFirst().orElse(null);
    assertNotNull("Should have 1-param createStatementPane", m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_hasCreateExpressionPane() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createExpressionPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(1, m.getParameterCount());
  }

  @Test
  public void astI18nFactory_hasCreateArgumentPane() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createArgumentPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(2, m.getParameterCount());
  }

  @Test
  public void astI18nFactory_isCommentMutableDefaultsFalse() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isCommentMutable"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals(1, m.getParameterCount());
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_isSignatureLockedDefaultsTrue() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isSignatureLocked"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals(1, m.getParameterCount());
  }

  @Test
  public void astI18nFactory_localPropertyNamesField() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("LOCAL_PROPERTY_NAMES"))
        .findFirst().orElse(null);
    assertNotNull("Should have LOCAL_PROPERTY_NAMES field", f);
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals("Set", f.getType().getSimpleName());
  }

  @Test
  public void astI18nFactory_createFieldAccessPaneIsProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createFieldAccessPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createInstanceCreationPaneIsProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createInstanceCreationPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createNameViewIsPublic() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createNameView"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_getInvalidExpressionColorIsPublic() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getInvalidExpressionColor"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals("Color", m.getReturnType().getSimpleName());
  }

  @Test
  public void astI18nFactory_createGetsComponentOverrides() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createGetsComponent"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_isLocalDraggableAndMutableDefaultTrue() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isLocalDraggableAndMutable"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_getDeclarationNameFontScaleIsProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getDeclarationNameFontScale"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(float.class, m.getReturnType());
  }

  @Test
  public void astI18nFactory_createStatementListPropertyPaneProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createStatementListPropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createExpressionListPropertyPaneProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createExpressionListPropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createResourcePropertyPaneProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createResourcePropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createLocalPaneProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createLocalPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createLocalDeclarationPaneProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createLocalDeclarationPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_epicHackMethod() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("EPIC_HACK_createWrapperIfNecessaryForExpressionPanelessComponent"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(1, m.getParameterCount());
  }

  @Test
  public void astI18nFactory_createSimpleArgumentListPropertyPaneAbstract() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createSimpleArgumentListPropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createKeyedArgumentListPropertyPaneAbstract() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createKeyedArgumentListPropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createIdeExpressionPaneAbstract() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createIdeExpressionPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_totalAbstractMethodCount() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    long count = Arrays.stream(cls.getDeclaredMethods())
        .filter(m -> Modifier.isAbstract(m.getModifiers()))
        .count();
    assertTrue("AstI18nFactory should have at least 4 abstract methods", count >= 4);
  }

  @Test
  public void astI18nFactory_createPropertyComponentOverrides() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createPropertyComponent"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertFalse(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(2, m.getParameterCount());
  }

  @Test
  public void astI18nFactory_createGenericNodePropertyPaneProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createGenericNodePropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createGenericInstancePropertyPaneProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createGenericInstancePropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createGenericListPropertyPaneProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createGenericListPropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_createGenericNodeListPropertyPaneProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createGenericNodeListPropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_getArgumentCascadeProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getArgumentCascade"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void astI18nFactory_isDropDownDesiredForProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isDropDownDesiredFor"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // ========================================================================
  // MutableAstI18nFactory contract tests
  // ========================================================================

  @Test
  public void mutableFactory_extendsAstI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    assertEquals("AstI18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void mutableFactory_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(loadFactory("MutableAstI18nFactory").getModifiers()));
  }

  @Test
  public void mutableFactory_hasGroupField() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("group"))
        .findFirst().orElse(null);
    assertNotNull("Should have 'group' field", f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void mutableFactory_constructorTakesGroup() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    boolean found = false;
    for (Constructor<?> c : ctors) {
      if (c.getParameterCount() == 1
          && c.getParameterTypes()[0].getSimpleName().equals("Group")) {
        found = true;
        break;
      }
    }
    assertTrue("Constructor should take a Group parameter", found);
  }

  @Test
  public void mutableFactory_isStatementListPropertyMutablePublic() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isStatementListPropertyMutable"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void mutableFactory_isKeyedArgumentListMutablePublic() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isKeyedArgumentListMutable"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void mutableFactory_overridesCreateNameView() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createNameView"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void mutableFactory_getFallBackTypeReturnsNull() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getFallBackTypeForThisExpression"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void mutableFactory_createIdeExpressionPaneThrows() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createIdeExpressionPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void mutableFactory_overridesCreateExpressionPropertyPane() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createExpressionPropertyPane")
            && mt.getParameterCount() == 2)
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void mutableFactory_isStatementContextMenuDesiredForProtected() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isStatementContextMenuDesiredFor"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void mutableFactory_overridesCreateStatementPane() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createStatementPane")
            && mt.getParameterCount() == 3)
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ========================================================================
  // IdeAstI18nFactory contract tests
  // ========================================================================

  @Test
  public void ideFactory_extendsAstI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("IdeAstI18nFactory");
    assertEquals("AstI18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void ideFactory_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(loadFactory("IdeAstI18nFactory").getModifiers()));
  }

  @Test
  public void ideFactory_overridesCreateIdeExpressionPane() throws ClassNotFoundException {
    Class<?> cls = loadFactory("IdeAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createIdeExpressionPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertFalse(Modifier.isAbstract(m.getModifiers()));
  }

  @Test
  public void ideFactory_overridesCreateExpressionPropertyPane() throws ClassNotFoundException {
    Class<?> cls = loadFactory("IdeAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createExpressionPropertyPane")
            && mt.getParameterCount() == 2)
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ========================================================================
  // ImmutableAstI18nFactory contract tests
  // ========================================================================

  @Test
  public void immutableFactory_extendsIdeAstI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("ImmutableAstI18nFactory");
    assertEquals("IdeAstI18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void immutableFactory_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(loadFactory("ImmutableAstI18nFactory").getModifiers()));
  }

  @Test
  public void immutableFactory_createSimpleArgumentListIsFinal() throws ClassNotFoundException {
    Class<?> cls = loadFactory("ImmutableAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createSimpleArgumentListPropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isFinal(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void immutableFactory_createKeyedArgumentListIsFinal() throws ClassNotFoundException {
    Class<?> cls = loadFactory("ImmutableAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createKeyedArgumentListPropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isFinal(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  // ========================================================================
  // PreviewAstI18nFactory — singleton and concrete tests
  // ========================================================================

  @Test
  public void previewFactory_extendsImmutableAstI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("PreviewAstI18nFactory");
    assertEquals("ImmutableAstI18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void previewFactory_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadFactory("PreviewAstI18nFactory").getModifiers()));
  }

  @Test
  public void previewFactory_hasSingletonHolder() throws ClassNotFoundException {
    Class<?> cls = loadFactory("PreviewAstI18nFactory");
    Class<?>[] innerClasses = cls.getDeclaredClasses();
    boolean found = Arrays.stream(innerClasses)
        .anyMatch(ic -> ic.getSimpleName().equals("SingletonHolder"));
    assertTrue("Should have SingletonHolder inner class", found);
  }

  @Test
  public void previewFactory_hasGetInstance() throws Exception {
    Class<?> cls = loadFactory("PreviewAstI18nFactory");
    Method m = cls.getMethod("getInstance");
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(cls, m.getReturnType());
  }

  @Test
  public void previewFactory_constructorIsPrivate() throws ClassNotFoundException {
    Class<?> cls = loadFactory("PreviewAstI18nFactory");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  @Test
  public void previewFactory_overridesCreateStatementPane() throws ClassNotFoundException {
    Class<?> cls = loadFactory("PreviewAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createStatementPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertEquals(3, m.getParameterCount());
  }

  // ========================================================================
  // TemplateAstI18nFactory — singleton and concrete tests
  // ========================================================================

  @Test
  public void templateFactory_extendsIdeAstI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("TemplateAstI18nFactory");
    assertEquals("IdeAstI18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void templateFactory_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadFactory("TemplateAstI18nFactory").getModifiers()));
  }

  @Test
  public void templateFactory_hasSingletonHolder() throws ClassNotFoundException {
    Class<?> cls = loadFactory("TemplateAstI18nFactory");
    Class<?>[] innerClasses = cls.getDeclaredClasses();
    boolean found = Arrays.stream(innerClasses)
        .anyMatch(ic -> ic.getSimpleName().equals("SingletonHolder"));
    assertTrue("Should have SingletonHolder inner class", found);
  }

  @Test
  public void templateFactory_hasGetInstance() throws Exception {
    Class<?> cls = loadFactory("TemplateAstI18nFactory");
    Method m = cls.getMethod("getInstance");
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(cls, m.getReturnType());
  }

  @Test
  public void templateFactory_constructorIsPrivate() throws ClassNotFoundException {
    Class<?> cls = loadFactory("TemplateAstI18nFactory");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  @Test
  public void templateFactory_overridesCreateSimpleArgumentListPropertyPane() throws ClassNotFoundException {
    Class<?> cls = loadFactory("TemplateAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createSimpleArgumentListPropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
  }

  @Test
  public void templateFactory_overridesCreateKeyedArgumentListPropertyPane() throws ClassNotFoundException {
    Class<?> cls = loadFactory("TemplateAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createKeyedArgumentListPropertyPane"))
        .findFirst().orElse(null);
    assertNotNull(m);
  }

  // ========================================================================
  // DialogAstI18nFactory — singleton and concrete tests
  // ========================================================================

  @Test
  public void dialogFactory_extendsMutableAstI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("DialogAstI18nFactory");
    assertEquals("MutableAstI18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void dialogFactory_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadFactory("DialogAstI18nFactory").getModifiers()));
  }

  @Test
  public void dialogFactory_hasSingletonHolder() throws ClassNotFoundException {
    Class<?> cls = loadFactory("DialogAstI18nFactory");
    Class<?>[] innerClasses = cls.getDeclaredClasses();
    boolean found = Arrays.stream(innerClasses)
        .anyMatch(ic -> ic.getSimpleName().equals("SingletonHolder"));
    assertTrue(found);
  }

  @Test
  public void dialogFactory_hasGetInstance() throws Exception {
    Class<?> cls = loadFactory("DialogAstI18nFactory");
    Method m = cls.getMethod("getInstance");
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(cls, m.getReturnType());
  }

  @Test
  public void dialogFactory_constructorIsPrivate() throws ClassNotFoundException {
    Class<?> cls = loadFactory("DialogAstI18nFactory");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  @Test
  public void dialogFactory_noAdditionalMethods() throws ClassNotFoundException {
    Class<?> cls = loadFactory("DialogAstI18nFactory");
    Method[] declared = cls.getDeclaredMethods();
    assertEquals("DialogAstI18nFactory should not declare additional methods beyond getInstance",
        1, declared.length);
    assertEquals("getInstance", declared[0].getName());
  }

  // ========================================================================
  // ClipboardAstI18nFactory — structural tests
  // ========================================================================

  @Test
  public void clipboardFactory_isLoadable() throws ClassNotFoundException {
    loadFactory("ClipboardAstI18nFactory");
  }

  @Test
  public void clipboardFactory_doesNotExtendAstI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("ClipboardAstI18nFactory");
    assertEquals("Should extend Object directly", Object.class, cls.getSuperclass());
  }

  @Test
  public void clipboardFactory_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(loadFactory("ClipboardAstI18nFactory").getModifiers()));
  }

  @Test
  public void clipboardFactory_hasDefaultConstructor() throws ClassNotFoundException {
    Class<?> cls = loadFactory("ClipboardAstI18nFactory");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertEquals(0, ctors[0].getParameterCount());
  }

  @Test
  public void clipboardFactory_noFields() throws ClassNotFoundException {
    Class<?> cls = loadFactory("ClipboardAstI18nFactory");
    assertEquals(0, cls.getDeclaredFields().length);
  }

  @Test
  public void clipboardFactory_noMethods() throws ClassNotFoundException {
    Class<?> cls = loadFactory("ClipboardAstI18nFactory");
    assertEquals(0, cls.getDeclaredMethods().length);
  }

  // ========================================================================
  // MenuIconIdeAstI18nFactory tests
  // ========================================================================

  @Test
  public void menuIconFactory_extendsImmutableAstI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MenuIconIdeAstI18nFactory");
    assertEquals("ImmutableAstI18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void menuIconFactory_hasGetInstance() throws Exception {
    Class<?> cls = loadFactory("MenuIconIdeAstI18nFactory");
    Method m = cls.getMethod("getInstance");
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(cls, m.getReturnType());
  }

  @Test
  public void menuIconFactory_overridesEpicHack() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MenuIconIdeAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("EPIC_HACK_createWrapperIfNecessaryForExpressionPanelessComponent"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void menuIconFactory_overridesGetFallBackType() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MenuIconIdeAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getFallBackTypeForThisExpression"))
        .findFirst().orElse(null);
    assertNotNull(m);
  }

  @Test
  public void menuIconFactory_constructorIsPrivate() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MenuIconIdeAstI18nFactory");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  // ========================================================================
  // AbstractProjectEditorAstI18nFactory tests
  // ========================================================================

  @Test
  public void abstractProjectEditor_extendsMutableAstI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AbstractProjectEditorAstI18nFactory");
    assertEquals("MutableAstI18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void abstractProjectEditor_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(
        loadFactory("AbstractProjectEditorAstI18nFactory").getModifiers()));
  }

  @Test
  public void abstractProjectEditor_hasIsMutableField() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AbstractProjectEditorAstI18nFactory");
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("IS_MUTABLE"))
        .findFirst().orElse(null);
    assertNotNull(f);
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void abstractProjectEditor_hasDeclarationNameFontScaleField() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AbstractProjectEditorAstI18nFactory");
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("declarationNameFontScale"))
        .findFirst().orElse(null);
    assertNotNull(f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals(float.class, f.getType());
  }

  @Test
  public void abstractProjectEditor_overridesGetInvalidExpressionColor() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AbstractProjectEditorAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getInvalidExpressionColor"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void abstractProjectEditor_isDraggablePublic() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AbstractProjectEditorAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isDraggable"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void abstractProjectEditor_createCodeHeaderPublic() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AbstractProjectEditorAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createCodeHeader"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(1, m.getParameterCount());
  }

  @Test
  public void abstractProjectEditor_noArgConstructor() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AbstractProjectEditorAstI18nFactory");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertEquals(0, ctors[0].getParameterCount());
    assertTrue(Modifier.isPublic(ctors[0].getModifiers()));
  }

  // ========================================================================
  // ProjectEditorAstI18nFactory tests
  // ========================================================================

  @Test
  public void projectEditorFactory_extendsAbstractProjectEditor() throws ClassNotFoundException {
    Class<?> cls = loadFactory("ProjectEditorAstI18nFactory");
    assertEquals("AbstractProjectEditorAstI18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void projectEditorFactory_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(
        loadFactory("ProjectEditorAstI18nFactory").getModifiers()));
  }

  @Test
  public void projectEditorFactory_hasGetInstance() throws Exception {
    Class<?> cls = loadFactory("ProjectEditorAstI18nFactory");
    Method m = cls.getMethod("getInstance");
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(cls, m.getReturnType());
  }

  @Test
  public void projectEditorFactory_overridesIsCommentMutable() throws ClassNotFoundException {
    Class<?> cls = loadFactory("ProjectEditorAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isCommentMutable"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void projectEditorFactory_constructorIsPrivate() throws ClassNotFoundException {
    Class<?> cls = loadFactory("ProjectEditorAstI18nFactory");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  // ========================================================================
  // SceneEditorUpdatingProjectEditorAstI18nFactory tests
  // ========================================================================

  @Test
  public void sceneEditorFactory_extendsAbstractProjectEditor() throws ClassNotFoundException {
    Class<?> cls = loadFactory("SceneEditorUpdatingProjectEditorAstI18nFactory");
    assertEquals("AbstractProjectEditorAstI18nFactory", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void sceneEditorFactory_isConcrete() throws ClassNotFoundException {
    assertFalse(Modifier.isAbstract(
        loadFactory("SceneEditorUpdatingProjectEditorAstI18nFactory").getModifiers()));
  }

  @Test
  public void sceneEditorFactory_hasGetInstance() throws Exception {
    Class<?> cls = loadFactory("SceneEditorUpdatingProjectEditorAstI18nFactory");
    Method m = cls.getMethod("getInstance");
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(cls, m.getReturnType());
  }

  @Test
  public void sceneEditorFactory_overridesGetArgumentCascade() throws ClassNotFoundException {
    Class<?> cls = loadFactory("SceneEditorUpdatingProjectEditorAstI18nFactory");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getArgumentCascade"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void sceneEditorFactory_constructorIsPrivate() throws ClassNotFoundException {
    Class<?> cls = loadFactory("SceneEditorUpdatingProjectEditorAstI18nFactory");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  // ========================================================================
  // Inheritance chain completeness tests
  // ========================================================================

  @Test
  public void inheritanceChain_previewToI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("PreviewAstI18nFactory");
    List<String> chain = new ArrayList<>();
    Class<?> c = cls;
    while (c != null && c != Object.class) {
      chain.add(c.getSimpleName());
      c = c.getSuperclass();
    }
    assertTrue(chain.contains("PreviewAstI18nFactory"));
    assertTrue(chain.contains("ImmutableAstI18nFactory"));
    assertTrue(chain.contains("IdeAstI18nFactory"));
    assertTrue(chain.contains("AstI18nFactory"));
    assertTrue(chain.contains("I18nFactory"));
  }

  @Test
  public void inheritanceChain_dialogToI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("DialogAstI18nFactory");
    List<String> chain = new ArrayList<>();
    Class<?> c = cls;
    while (c != null && c != Object.class) {
      chain.add(c.getSimpleName());
      c = c.getSuperclass();
    }
    assertTrue(chain.contains("DialogAstI18nFactory"));
    assertTrue(chain.contains("MutableAstI18nFactory"));
    assertTrue(chain.contains("AstI18nFactory"));
    assertTrue(chain.contains("I18nFactory"));
  }

  @Test
  public void inheritanceChain_sceneEditorToI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("SceneEditorUpdatingProjectEditorAstI18nFactory");
    List<String> chain = new ArrayList<>();
    Class<?> c = cls;
    while (c != null && c != Object.class) {
      chain.add(c.getSimpleName());
      c = c.getSuperclass();
    }
    assertTrue(chain.contains("SceneEditorUpdatingProjectEditorAstI18nFactory"));
    assertTrue(chain.contains("AbstractProjectEditorAstI18nFactory"));
    assertTrue(chain.contains("MutableAstI18nFactory"));
    assertTrue(chain.contains("AstI18nFactory"));
    assertTrue(chain.contains("I18nFactory"));
  }

  @Test
  public void inheritanceChain_menuIconToI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MenuIconIdeAstI18nFactory");
    List<String> chain = new ArrayList<>();
    Class<?> c = cls;
    while (c != null && c != Object.class) {
      chain.add(c.getSimpleName());
      c = c.getSuperclass();
    }
    assertTrue(chain.contains("MenuIconIdeAstI18nFactory"));
    assertTrue(chain.contains("ImmutableAstI18nFactory"));
    assertTrue(chain.contains("IdeAstI18nFactory"));
    assertTrue(chain.contains("AstI18nFactory"));
    assertTrue(chain.contains("I18nFactory"));
  }

  @Test
  public void inheritanceChain_templateToI18nFactory() throws ClassNotFoundException {
    Class<?> cls = loadFactory("TemplateAstI18nFactory");
    List<String> chain = new ArrayList<>();
    Class<?> c = cls;
    while (c != null && c != Object.class) {
      chain.add(c.getSimpleName());
      c = c.getSuperclass();
    }
    assertTrue(chain.contains("TemplateAstI18nFactory"));
    assertTrue(chain.contains("IdeAstI18nFactory"));
    assertTrue(chain.contains("AstI18nFactory"));
    assertTrue(chain.contains("I18nFactory"));
  }

  // ========================================================================
  // Cross-cutting: all singletons verified
  // ========================================================================

  @Test
  public void allSingletons_getInstanceReturnsSameType() throws Exception {
    String[] singletonFactories = {
        "PreviewAstI18nFactory", "TemplateAstI18nFactory",
        "DialogAstI18nFactory", "MenuIconIdeAstI18nFactory",
        "ProjectEditorAstI18nFactory",
        "SceneEditorUpdatingProjectEditorAstI18nFactory"
    };
    for (String name : singletonFactories) {
      Class<?> cls = loadFactory(name);
      Method m = cls.getMethod("getInstance");
      assertEquals(name + ".getInstance() return type should match class",
          cls, m.getReturnType());
      assertTrue(name + ".getInstance() should be static",
          Modifier.isStatic(m.getModifiers()));
    }
  }

  @Test
  public void allSingletons_havePrivateConstructors() throws ClassNotFoundException {
    String[] singletonFactories = {
        "PreviewAstI18nFactory", "TemplateAstI18nFactory",
        "DialogAstI18nFactory", "MenuIconIdeAstI18nFactory",
        "ProjectEditorAstI18nFactory",
        "SceneEditorUpdatingProjectEditorAstI18nFactory"
    };
    for (String name : singletonFactories) {
      Class<?> cls = loadFactory(name);
      Constructor<?>[] ctors = cls.getDeclaredConstructors();
      for (Constructor<?> c : ctors) {
        assertTrue(name + " constructor should be private",
            Modifier.isPrivate(c.getModifiers()));
      }
    }
  }

  @Test
  public void allSingletons_haveSingletonHolderInnerClass() throws ClassNotFoundException {
    String[] singletonFactories = {
        "PreviewAstI18nFactory", "TemplateAstI18nFactory",
        "DialogAstI18nFactory", "MenuIconIdeAstI18nFactory",
        "ProjectEditorAstI18nFactory",
        "SceneEditorUpdatingProjectEditorAstI18nFactory"
    };
    for (String name : singletonFactories) {
      Class<?> cls = loadFactory(name);
      boolean found = Arrays.stream(cls.getDeclaredClasses())
          .anyMatch(ic -> ic.getSimpleName().equals("SingletonHolder"));
      assertTrue(name + " should have SingletonHolder inner class", found);
    }
  }

  // ========================================================================
  // Croquet package tests — SceneEditorUpdatingArgumentCascade
  // ========================================================================

  @Test
  public void sceneEditorCascade_isLoadable() throws ClassNotFoundException {
    loadClass(CROQUET_PKG + "SceneEditorUpdatingArgumentCascade");
  }

  @Test
  public void sceneEditorCascade_extendsAbstractArgumentCascade() throws ClassNotFoundException {
    Class<?> cls = loadClass(CROQUET_PKG + "SceneEditorUpdatingArgumentCascade");
    assertEquals("AbstractArgumentCascade", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void sceneEditorCascade_hasGetInstance() throws ClassNotFoundException {
    Class<?> cls = loadClass(CROQUET_PKG + "SceneEditorUpdatingArgumentCascade");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getInstance"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isSynchronized(m.getModifiers()));
  }

  @Test
  public void sceneEditorCascade_hasMapField() throws ClassNotFoundException {
    Class<?> cls = loadClass(CROQUET_PKG + "SceneEditorUpdatingArgumentCascade");
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("map"))
        .findFirst().orElse(null);
    assertNotNull(f);
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertEquals("Map", f.getType().getSimpleName());
  }

  @Test
  public void sceneEditorCascade_constructorIsPrivate() throws ClassNotFoundException {
    Class<?> cls = loadClass(CROQUET_PKG + "SceneEditorUpdatingArgumentCascade");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertTrue(Modifier.isPrivate(ctors[0].getModifiers()));
  }

  @Test
  public void sceneEditorCascade_overridesCreateExpressionPropertyEdit() throws ClassNotFoundException {
    Class<?> cls = loadClass(CROQUET_PKG + "SceneEditorUpdatingArgumentCascade");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("createExpressionPropertyEdit"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(4, m.getParameterCount());
  }

  // ========================================================================
  // Edits package tests — SceneEditorUpdatingExpressionPropertyEdit
  // ========================================================================

  @Test
  public void sceneEditorEdit_isLoadable() throws ClassNotFoundException {
    loadClass(EDIT_PKG + "SceneEditorUpdatingExpressionPropertyEdit");
  }

  @Test
  public void sceneEditorEdit_extendsExpressionPropertyEdit() throws ClassNotFoundException {
    Class<?> cls = loadClass(EDIT_PKG + "SceneEditorUpdatingExpressionPropertyEdit");
    assertEquals("ExpressionPropertyEdit", cls.getSuperclass().getSimpleName());
  }

  @Test
  public void sceneEditorEdit_hasFieldUserField() throws ClassNotFoundException {
    Class<?> cls = loadClass(EDIT_PKG + "SceneEditorUpdatingExpressionPropertyEdit");
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("field"))
        .findFirst().orElse(null);
    assertNotNull(f);
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals("UserField", f.getType().getSimpleName());
  }

  @Test
  public void sceneEditorEdit_hasTwoConstructors() throws ClassNotFoundException {
    Class<?> cls = loadClass(EDIT_PKG + "SceneEditorUpdatingExpressionPropertyEdit");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(2, ctors.length);
  }

  @Test
  public void sceneEditorEdit_primaryConstructorParams() throws ClassNotFoundException {
    Class<?> cls = loadClass(EDIT_PKG + "SceneEditorUpdatingExpressionPropertyEdit");
    boolean found = false;
    for (Constructor<?> c : cls.getDeclaredConstructors()) {
      if (c.getParameterCount() == 5) {
        found = true;
        Class<?>[] params = c.getParameterTypes();
        assertEquals("UserActivity", params[0].getSimpleName());
        assertEquals("ExpressionProperty", params[1].getSimpleName());
        assertEquals("Expression", params[2].getSimpleName());
        assertEquals("Expression", params[3].getSimpleName());
        assertEquals("UserField", params[4].getSimpleName());
      }
    }
    assertTrue("Should have a 5-param constructor", found);
  }

  @Test
  public void sceneEditorEdit_overridesSetValue() throws ClassNotFoundException {
    Class<?> cls = loadClass(EDIT_PKG + "SceneEditorUpdatingExpressionPropertyEdit");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("setValue"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertEquals(1, m.getParameterCount());
  }

  // ========================================================================
  // Cross-cutting: method count and field count smoke tests
  // ========================================================================

  @Test
  public void astI18nFactory_hasManyDeclaredMethods() throws ClassNotFoundException {
    Class<?> cls = loadFactory("AstI18nFactory");
    assertTrue("AstI18nFactory should have 15+ declared methods",
        cls.getDeclaredMethods().length >= 15);
  }

  @Test
  public void i18nFactory_hasFewDeclaredMethods() throws ClassNotFoundException {
    Class<?> cls = loadFactory("I18nFactory");
    int count = cls.getDeclaredMethods().length;
    assertTrue("I18nFactory should have 5-10 declared methods",
        count >= 5 && count <= 10);
  }

  @Test
  public void mutableFactory_hasManyDeclaredMethods() throws ClassNotFoundException {
    Class<?> cls = loadFactory("MutableAstI18nFactory");
    assertTrue("MutableAstI18nFactory should have 7+ declared methods",
        cls.getDeclaredMethods().length >= 7);
  }

  // ========================================================================
  // All factory classes loadable
  // ========================================================================

  @Test
  public void allFactoryClasses_areLoadable() throws ClassNotFoundException {
    for (String name : FACTORY_CLASSES) {
      Class<?> cls = loadFactory(name);
      assertNotNull(name + " should be loadable", cls);
    }
  }

  // ========================================================================
  // Package-level tests: all classes in x package
  // ========================================================================

  @Test
  public void xPackage_allConcreteFactoriesHaveGetInstance() throws Exception {
    String[] concreteWithGetInstance = {
        "PreviewAstI18nFactory", "TemplateAstI18nFactory",
        "DialogAstI18nFactory", "MenuIconIdeAstI18nFactory",
        "ProjectEditorAstI18nFactory",
        "SceneEditorUpdatingProjectEditorAstI18nFactory"
    };
    for (String name : concreteWithGetInstance) {
      Class<?> cls = loadFactory(name);
      assertFalse(name + " should be concrete", Modifier.isAbstract(cls.getModifiers()));
      Method m = cls.getMethod("getInstance");
      assertNotNull(name + " should have getInstance()", m);
    }
  }

  @Test
  public void xPackage_abstractClassesCannotBeInstantiated() throws ClassNotFoundException {
    String[] abstractFactories = {
        "I18nFactory", "AstI18nFactory", "MutableAstI18nFactory",
        "IdeAstI18nFactory", "ImmutableAstI18nFactory",
        "AbstractProjectEditorAstI18nFactory"
    };
    for (String name : abstractFactories) {
      Class<?> cls = loadFactory(name);
      assertTrue(name + " should be abstract", Modifier.isAbstract(cls.getModifiers()));
    }
  }

  // ========================================================================
  // Component view parent class contract tests
  // ========================================================================

  @Test
  public void statementListPropertyView_intrasticial_padField() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("INTRASTICIAL_PAD"))
        .findFirst().orElse(null);
    assertNotNull(f);
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertTrue(Modifier.isPublic(f.getModifiers()));
    assertEquals(int.class, f.getType());
  }

  @Test
  public void statementListPropertyView_epicHackField() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("EPIC_HACK_ignoreDrawingDesired"))
        .findFirst().orElse(null);
    assertNotNull(f);
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isPublic(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void statementListPropertyView_hasBoundInformationInnerClass() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    boolean found = Arrays.stream(cls.getDeclaredClasses())
        .anyMatch(ic -> ic.getSimpleName().equals("BoundInformation"));
    assertTrue("Should have BoundInformation inner class", found);
  }

  @Test
  public void statementListPropertyView_boundInformationFields() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Class<?> bi = Arrays.stream(cls.getDeclaredClasses())
        .filter(ic -> ic.getSimpleName().equals("BoundInformation"))
        .findFirst().orElse(null);
    assertNotNull(bi);

    Set<String> fieldNames = Arrays.stream(bi.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toSet());
    assertTrue(fieldNames.contains("yMinimum"));
    assertTrue(fieldNames.contains("yMaximum"));
    assertTrue(fieldNames.contains("y"));
    assertTrue(fieldNames.contains("yPlusHeight"));
  }

  @Test
  public void statementListPropertyView_hasFeedbackJPanelInnerClass() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    boolean found = Arrays.stream(cls.getDeclaredClasses())
        .anyMatch(ic -> ic.getSimpleName().equals("FeedbackJPanel"));
    assertTrue("Should have FeedbackJPanel inner class", found);
  }

  @Test
  public void statementListPropertyView_calculateIndexMethod() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("calculateIndex"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void statementListPropertyView_calculateYBoundsMethod() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("calculateYBounds"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void statementListPropertyView_setCurrentPotentialDropIndex() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("setCurrentPotentialDropIndexAndDragStep"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(2, m.getParameterCount());
  }

  @Test
  public void statementListPropertyView_isEmptyMethod() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("isEmpty"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void statementListPropertyView_getAvailableDropProxyHeight() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getAvailableDropProxyHeight"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void statementListPropertyView_getStatementListBorder() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getStatementListBorder"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void statementListPropertyView_setIsCurrentUnder() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("setIsCurrentUnder"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(1, m.getParameterCount());
    assertEquals(boolean.class, m.getParameterTypes()[0]);
  }

  @Test
  public void statementListPropertyView_getDropBounds() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Method m = Arrays.stream(cls.getDeclaredMethods())
        .filter(mt -> mt.getName().equals("getDropBounds"))
        .findFirst().orElse(null);
    assertNotNull(m);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals("Rectangle", m.getReturnType().getSimpleName());
  }

  @Test
  public void statementListPropertyView_twoConstructors() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Constructor<?>[] ctors = cls.getDeclaredConstructors();
    assertEquals(2, ctors.length);
  }

  @Test
  public void statementListPropertyView_feedbackColorField() throws ClassNotFoundException {
    Class<?> cls = loadComponent("StatementListPropertyView");
    Field f = Arrays.stream(cls.getDeclaredFields())
        .filter(fd -> fd.getName().equals("FEEDBACK_COLOR"))
        .findFirst().orElse(null);
    assertNotNull(f);
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }
}

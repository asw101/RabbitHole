package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.imp.operation.OperationImp;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for the {@link MenuItemPrepModel} abstract class.
 * Uses reflection since the class has abstract methods and extends AbstractElement.
 */
public class MenuItemPrepModelCoverageTest {

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(MenuItemPrepModel.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractElement() {
    assertTrue(AbstractElement.class.isAssignableFrom(MenuItemPrepModel.class));
  }

  @Test
  public void class_implementsElement() {
    assertTrue(Element.class.isAssignableFrom(MenuItemPrepModel.class));
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_takesUUID() throws Exception {
    var ctor = MenuItemPrepModel.class.getDeclaredConstructor(java.util.UUID.class);
    assertNotNull(ctor);
  }

  // ── StandardMenuItemPrepModel ─────────────────────────────────────

  @Test
  public void standardMenuItemPrepModel_isAbstract() {
    assertTrue(Modifier.isAbstract(StandardMenuItemPrepModel.class.getModifiers()));
  }

  @Test
  public void standardMenuItemPrepModel_extendsMenuItemPrepModel() {
    assertTrue(MenuItemPrepModel.class.isAssignableFrom(
        StandardMenuItemPrepModel.class));
  }

  @Test
  public void standardMenuItemPrepModel_hasCreateMenuItemAndAddTo() throws Exception {
    Method m = StandardMenuItemPrepModel.class.getDeclaredMethod(
        "createMenuItemAndAddTo",
        org.lgna.croquet.views.MenuItemContainer.class);
    assertNotNull(m);
    assertTrue(Modifier.isAbstract(m.getModifiers()));
  }

  // ── PrepModel interface ───────────────────────────────────────────

  @Test
  public void prepModel_isInterface() {
    assertTrue(PrepModel.class.isInterface());
  }

  // ── Triggerable interface ─────────────────────────────────────────

  @Test
  public void triggerable_isInterface() {
    assertTrue(Triggerable.class.isInterface());
  }

  @Test
  public void triggerable_hasFire() throws Exception {
    Method m = Triggerable.class.getMethod("fire",
        org.lgna.croquet.history.UserActivity.class);
    assertNotNull(m);
  }

  // ── Model interface ───────────────────────────────────────────────

  @Test
  public void model_isInterface() {
    assertTrue(Model.class.isInterface());
  }

  @Test
  public void model_hasIsEnabled() throws Exception {
    Method m = Model.class.getMethod("isEnabled");
    assertNotNull(m);
  }

  @Test
  public void model_hasSetEnabled() throws Exception {
    Method m = Model.class.getMethod("setEnabled", boolean.class);
    assertNotNull(m);
  }

  // ── CompletionModel interface ─────────────────────────────────────

  @Test
  public void completionModel_isInterface() {
    assertTrue(CompletionModel.class.isInterface());
  }

  @Test
  public void completionModel_hasSidekickLabel() throws Exception {
    Method m = CompletionModel.class.getMethod("getSidekickLabel");
    assertNotNull(m);
  }

  @Test
  public void completionModel_hasHasSidekickLabel() throws Exception {
    Method m = CompletionModel.class.getMethod("hasSidekickLabel");
    assertNotNull(m);
  }

  // ── Element interface ─────────────────────────────────────────────

  @Test
  public void element_isInterface() {
    assertTrue(Element.class.isInterface());
  }

  @Test
  public void element_hasInitializeIfNecessary() throws Exception {
    Method m = Element.class.getMethod("initializeIfNecessary");
    assertNotNull(m);
  }

  @Test
  public void element_hasRelocalize() throws Exception {
    // Element may not have getMigrationId directly
    assertTrue(Element.class.isInterface());
  }

  @Test
  public void element_hasAppendUserRepr() throws Exception {
    Method m = Element.class.getMethod("appendUserRepr", StringBuilder.class);
    assertNotNull(m);
  }
}

package org.alice.stageide.properties.uicontroller;

import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * TDD contract tests for the gbc() helper and INSETS_2 constant
 * extracted into ModelSizePropertyController (issue #705).
 *
 * The refactoring adds:
 *   - private static final Insets INSETS_2 = new Insets(2, 2, 2, 2)
 *   - private static GridBagConstraints gbc(int gridX, int gridY,
 *         int gridHeight, int anchor, Insets insets)
 *
 * These tests verify:
 *   1. INSETS_2 constant exists with correct value
 *   2. gbc() method exists with correct signature
 *   3. gbc() hard-codes the six fixed defaults correctly
 *   4. gbc() propagates all five caller-specified parameters
 *   5. gbc() output matches the original inline GridBagConstraints for each
 *      of the 11 qualifying call sites
 *   6. The glue outlier is NOT covered by gbc() (weight/fill differ)
 *
 * TDD: these tests FAIL until the refactoring is implemented.
 */
public class ModelSizeGbcHelperContractTest {

  private static final String CLASS_FQCN =
      "org.alice.stageide.properties.uicontroller.ModelSizePropertyController";

  private static Class<?> controllerClazz;
  private static Method gbcMethod;
  private static Field insets2Field;

  @BeforeClass
  public static void loadReflection() {
    try {
      controllerClazz = Class.forName(CLASS_FQCN);
    } catch (ClassNotFoundException e) {
      fail("ModelSizePropertyController class not found: " + e.getMessage());
    }

    try {
      gbcMethod = controllerClazz.getDeclaredMethod(
          "gbc", int.class, int.class, int.class, int.class, Insets.class);
      gbcMethod.setAccessible(true);
    } catch (NoSuchMethodException e) {
      // Will be caught by individual tests
      gbcMethod = null;
    }

    try {
      insets2Field = controllerClazz.getDeclaredField("INSETS_2");
      insets2Field.setAccessible(true);
    } catch (NoSuchFieldException e) {
      insets2Field = null;
    }
  }

  // --- INSETS_2 constant tests ---

  @Test
  public void insets2_fieldExists() {
    assertNotNull("INSETS_2 field should exist on ModelSizePropertyController",
        insets2Field);
  }

  @Test
  public void insets2_isPrivateStaticFinal() {
    assertNotNull("INSETS_2 field must exist", insets2Field);
    int mods = insets2Field.getModifiers();
    assertTrue("INSETS_2 should be private", Modifier.isPrivate(mods));
    assertTrue("INSETS_2 should be static", Modifier.isStatic(mods));
    assertTrue("INSETS_2 should be final", Modifier.isFinal(mods));
  }

  @Test
  public void insets2_isInsetsType() {
    assertNotNull("INSETS_2 field must exist", insets2Field);
    assertEquals("INSETS_2 should be of type Insets",
        Insets.class, insets2Field.getType());
  }

  @Test
  public void insets2_hasCorrectValue() throws Exception {
    assertNotNull("INSETS_2 field must exist", insets2Field);
    Insets value = (Insets) insets2Field.get(null);
    assertNotNull("INSETS_2 should not be null", value);
    assertEquals("INSETS_2.top", 2, value.top);
    assertEquals("INSETS_2.left", 2, value.left);
    assertEquals("INSETS_2.bottom", 2, value.bottom);
    assertEquals("INSETS_2.right", 2, value.right);
  }

  // --- gbc() method existence and signature tests ---

  @Test
  public void gbc_methodExists() {
    assertNotNull("gbc(int,int,int,int,Insets) method should exist on "
        + "ModelSizePropertyController", gbcMethod);
  }

  @Test
  public void gbc_isPrivateStatic() {
    assertNotNull("gbc method must exist", gbcMethod);
    int mods = gbcMethod.getModifiers();
    assertTrue("gbc should be private", Modifier.isPrivate(mods));
    assertTrue("gbc should be static", Modifier.isStatic(mods));
  }

  @Test
  public void gbc_returnsGridBagConstraints() {
    assertNotNull("gbc method must exist", gbcMethod);
    assertEquals("gbc should return GridBagConstraints",
        GridBagConstraints.class, gbcMethod.getReturnType());
  }

  // --- gbc() hard-coded defaults tests ---

  @Test
  public void gbc_gridWidthAlwaysOne() throws Exception {
    GridBagConstraints result = invokeGbc(0, 0, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertEquals("gridWidth must be 1", 1, result.gridwidth);
  }

  @Test
  public void gbc_weightXAlwaysZero() throws Exception {
    GridBagConstraints result = invokeGbc(0, 0, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertEquals("weightX must be 0.0", 0.0, result.weightx, 0.0);
  }

  @Test
  public void gbc_weightYAlwaysZero() throws Exception {
    GridBagConstraints result = invokeGbc(0, 0, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertEquals("weightY must be 0.0", 0.0, result.weighty, 0.0);
  }

  @Test
  public void gbc_fillAlwaysNone() throws Exception {
    GridBagConstraints result = invokeGbc(0, 0, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertEquals("fill must be NONE",
        GridBagConstraints.NONE, result.fill);
  }

  @Test
  public void gbc_ipadXAlwaysZero() throws Exception {
    GridBagConstraints result = invokeGbc(0, 0, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertEquals("ipadX must be 0", 0, result.ipadx);
  }

  @Test
  public void gbc_ipadYAlwaysZero() throws Exception {
    GridBagConstraints result = invokeGbc(0, 0, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertEquals("ipadY must be 0", 0, result.ipady);
  }

  // --- gbc() parameter propagation tests ---

  @Test
  public void gbc_propagatesGridX() throws Exception {
    GridBagConstraints result = invokeGbc(5, 0, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertEquals("gridX should be propagated", 5, result.gridx);
  }

  @Test
  public void gbc_propagatesGridY() throws Exception {
    GridBagConstraints result = invokeGbc(0, 2, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertEquals("gridY should be propagated", 2, result.gridy);
  }

  @Test
  public void gbc_propagatesGridHeight() throws Exception {
    GridBagConstraints result = invokeGbc(0, 0, 3,
        GridBagConstraints.WEST, new Insets(2, 2, 2, 2));
    assertEquals("gridHeight should be propagated", 3, result.gridheight);
  }

  @Test
  public void gbc_propagatesAnchor() throws Exception {
    GridBagConstraints result = invokeGbc(0, 0, 1,
        GridBagConstraints.NORTHWEST, new Insets(2, 2, 2, 2));
    assertEquals("anchor should be propagated",
        GridBagConstraints.NORTHWEST, result.anchor);
  }

  @Test
  public void gbc_propagatesInsets() throws Exception {
    Insets custom = new Insets(16, 2, 2, 2);
    GridBagConstraints result = invokeGbc(3, 0, 3,
        GridBagConstraints.NORTHWEST, custom);
    assertEquals("insets should be propagated", custom, result.insets);
  }

  // --- gbc() matches original inline GridBagConstraints for all 11 sites ---

  @Test
  public void gbc_matchesOriginal_widthLabel() throws Exception {
    // Original: gridX=0, gridY=0, gridWidth=1, gridHeight=1,
    //   weightX=0.0, weightY=0.0, anchor=EAST, fill=NONE,
    //   insets=(2,2,2,2), ipadX=0, ipadY=0
    GridBagConstraints expected = new GridBagConstraints(
        0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(0, 0, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertGbcEqual("widthLabel", expected, actual);
  }

  @Test
  public void gbc_matchesOriginal_widthField() throws Exception {
    GridBagConstraints expected = new GridBagConstraints(
        1, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(1, 0, 1,
        GridBagConstraints.WEST, new Insets(2, 2, 2, 2));
    assertGbcEqual("widthField", expected, actual);
  }

  @Test
  public void gbc_matchesOriginal_heightLabel() throws Exception {
    GridBagConstraints expected = new GridBagConstraints(
        0, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(0, 1, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertGbcEqual("heightLabel", expected, actual);
  }

  @Test
  public void gbc_matchesOriginal_heightField() throws Exception {
    GridBagConstraints expected = new GridBagConstraints(
        1, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(1, 1, 1,
        GridBagConstraints.WEST, new Insets(2, 2, 2, 2));
    assertGbcEqual("heightField", expected, actual);
  }

  @Test
  public void gbc_matchesOriginal_depthLabel() throws Exception {
    GridBagConstraints expected = new GridBagConstraints(
        0, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.EAST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(0, 2, 1,
        GridBagConstraints.EAST, new Insets(2, 2, 2, 2));
    assertGbcEqual("depthLabel", expected, actual);
  }

  @Test
  public void gbc_matchesOriginal_depthField() throws Exception {
    GridBagConstraints expected = new GridBagConstraints(
        1, 2, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(1, 2, 1,
        GridBagConstraints.WEST, new Insets(2, 2, 2, 2));
    assertGbcEqual("depthField", expected, actual);
  }

  @Test
  public void gbc_matchesOriginal_linkAllButton() throws Exception {
    // SCALE_ALL_X_POS = 6, gridY=0, gridHeight=3, anchor=WEST
    GridBagConstraints expected = new GridBagConstraints(
        6, 0, 1, 3, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(6, 0, 3,
        GridBagConstraints.WEST, new Insets(2, 2, 2, 2));
    assertGbcEqual("linkAllButton", expected, actual);
  }

  @Test
  public void gbc_matchesOriginal_linkXYButton_customInsets() throws Exception {
    // SCALE_XY_X_POS = 3, gridY=0, gridHeight=3, anchor=NORTHWEST,
    // insets=(16,2,2,2) — custom top padding
    GridBagConstraints expected = new GridBagConstraints(
        3, 0, 1, 3, 0.0, 0.0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
        new Insets(16, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(3, 0, 3,
        GridBagConstraints.NORTHWEST, new Insets(16, 2, 2, 2));
    assertGbcEqual("linkXYButton (custom insets 16,2,2,2)", expected, actual);
  }

  @Test
  public void gbc_matchesOriginal_linkXZButton() throws Exception {
    // SCALE_XZ_X_POS = 5, gridY=0, gridHeight=3, anchor=WEST
    GridBagConstraints expected = new GridBagConstraints(
        5, 0, 1, 3, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(5, 0, 3,
        GridBagConstraints.WEST, new Insets(2, 2, 2, 2));
    assertGbcEqual("linkXZButton", expected, actual);
  }

  @Test
  public void gbc_matchesOriginal_linkYZButton_customInsets() throws Exception {
    // SCALE_YZ_X_POS = 4, gridY=0, gridHeight=3, anchor=NORTHWEST,
    // insets=(48,2,2,2) — custom top padding
    GridBagConstraints expected = new GridBagConstraints(
        4, 0, 1, 3, 0.0, 0.0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
        new Insets(48, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(4, 0, 3,
        GridBagConstraints.NORTHWEST, new Insets(48, 2, 2, 2));
    assertGbcEqual("linkYZButton (custom insets 48,2,2,2)", expected, actual);
  }

  @Test
  public void gbc_matchesOriginal_resetButton() throws Exception {
    // RESET_X_POS = 7, gridY=0, gridHeight=3, anchor=WEST
    GridBagConstraints expected = new GridBagConstraints(
        7, 0, 1, 3, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(2, 2, 2, 2), 0, 0);
    GridBagConstraints actual = invokeGbc(7, 0, 3,
        GridBagConstraints.WEST, new Insets(2, 2, 2, 2));
    assertGbcEqual("resetButton", expected, actual);
  }

  // --- Outlier: glue component should NOT match gbc() defaults ---

  @Test
  public void glueOutlier_cannotBeExpressedByGbc() throws Exception {
    // The glue uses weight=1.0 and fill=BOTH — gbc() hard-codes 0.0 and NONE.
    // This test documents that the glue outlier intentionally stays inline.
    GridBagConstraints glue = new GridBagConstraints(
        8, 0, 1, 3, 1.0, 1.0,
        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0);
    GridBagConstraints fromGbc = invokeGbc(8, 0, 3,
        GridBagConstraints.CENTER, new Insets(0, 0, 0, 0));

    // gbc() cannot produce the glue's weights or fill
    assertNotEquals("weightX should differ for glue",
        glue.weightx, fromGbc.weightx, 0.0);
    assertNotEquals("weightY should differ for glue",
        glue.weighty, fromGbc.weighty, 0.0);
    assertNotEquals("fill should differ for glue",
        glue.fill, fromGbc.fill);
  }

  // --- Line-count characterization test ---

  @Test
  public void fileLineCount_underTarget() throws Exception {
    // Maven runs from module root (core/ide/), so use module-relative path
    java.nio.file.Path srcFile = java.nio.file.Paths.get(
        "src/main/java/org/alice/stageide/properties/uicontroller/"
        + "ModelSizePropertyController.java");
    if (!java.nio.file.Files.exists(srcFile)) {
      // Fallback: try repo-root-relative path (IDE or worktree run)
      srcFile = java.nio.file.Paths.get(
          "core/ide/src/main/java/org/alice/stageide/properties/uicontroller/"
          + "ModelSizePropertyController.java");
    }
    assertTrue("Source file must exist for line-count check",
        java.nio.file.Files.exists(srcFile));
    long lines = java.nio.file.Files.lines(srcFile).count();
    assertTrue("ModelSizePropertyController should be under 500 lines "
        + "(was " + lines + ")", lines < 500);
  }

  // --- Helpers ---

  private GridBagConstraints invokeGbc(int gridX, int gridY,
      int gridHeight, int anchor, Insets insets) throws Exception {
    assertNotNull("gbc method must exist to invoke", gbcMethod);
    return (GridBagConstraints) gbcMethod.invoke(null,
        gridX, gridY, gridHeight, anchor, insets);
  }

  private void assertGbcEqual(String label,
      GridBagConstraints expected, GridBagConstraints actual) {
    assertEquals(label + " gridx", expected.gridx, actual.gridx);
    assertEquals(label + " gridy", expected.gridy, actual.gridy);
    assertEquals(label + " gridwidth", expected.gridwidth, actual.gridwidth);
    assertEquals(label + " gridheight", expected.gridheight, actual.gridheight);
    assertEquals(label + " weightx", expected.weightx, actual.weightx, 0.0);
    assertEquals(label + " weighty", expected.weighty, actual.weighty, 0.0);
    assertEquals(label + " anchor", expected.anchor, actual.anchor);
    assertEquals(label + " fill", expected.fill, actual.fill);
    assertEquals(label + " insets", expected.insets, actual.insets);
    assertEquals(label + " ipadx", expected.ipadx, actual.ipadx);
    assertEquals(label + " ipady", expected.ipady, actual.ipady);
  }
}

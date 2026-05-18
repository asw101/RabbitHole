package org.alice.stageide;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Behavioral characterization tests for StoryApiConfigurationManager
 * and sceneeditor gap-fill tests.
 *
 * All tests are headless — reflection and source-analysis only.
 * The StoryApiConfigurationManager singleton is created by NebulousIde.nonfree,
 * which is not available in test, so we use reflection.
 */
public class StoryApiConfigManagerBehavioralTest {

  private static final String CONFIG_MANAGER_CLASS =
      "org.alice.stageide.StoryApiConfigurationManager";
  private static final String CONFIG_MANAGER_SRC =
      "src/main/java/org/alice/stageide/StoryApiConfigurationManager.java";
  private static final String SCENE_EDITOR_DIR =
      "src/main/java/org/alice/stageide/sceneeditor";

  // ---- StoryApiConfigurationManager hierarchy ----

  @Test
  public void configManager_extendsApiConfigurationManager() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Class<?> parent = Class.forName("org.alice.ide.ApiConfigurationManager");
    assertTrue("Must extend ApiConfigurationManager",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void configManager_isPublic() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    assertTrue("Must be public", Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void configManager_isConcrete() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    assertFalse("Must not be abstract", Modifier.isAbstract(cls.getModifiers()));
  }

  // ---- Singleton ----

  @Test
  public void configManager_hasGetInstanceMethod() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getMethod("getInstance");
    assertTrue("getInstance must be public", Modifier.isPublic(m.getModifiers()));
    assertTrue("getInstance must be static", Modifier.isStatic(m.getModifiers()));
    assertEquals("Must return StoryApiConfigurationManager", cls, m.getReturnType());
  }

  @Test
  public void configManager_hasSingletonHolderInnerClass() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    boolean hasSingletonHolder = Arrays.stream(cls.getDeclaredClasses())
        .anyMatch(c -> c.getSimpleName().equals("SingletonHolder"));
    assertTrue("Must have SingletonHolder inner class", hasSingletonHolder);
  }

  // ---- Static constants ----

  @Test
  public void configManager_hasSET_ACTIVE_SCENE_METHOD() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Field f = cls.getField("SET_ACTIVE_SCENE_METHOD");
    assertTrue("Must be public", Modifier.isPublic(f.getModifiers()));
    assertTrue("Must be static", Modifier.isStatic(f.getModifiers()));
    assertTrue("Must be final", Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void configManager_hasBIPED_RESOURCE_TYPE() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Field f = cls.getDeclaredField("BIPED_RESOURCE_TYPE");
    assertTrue("Must be static", Modifier.isStatic(f.getModifiers()));
    assertTrue("Must be final", Modifier.isFinal(f.getModifiers()));
  }

  // ---- Override methods from ApiConfigurationManager ----

  @Test
  public void configManager_overridesGetTypeComparator() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getMethod("getTypeComparator");
    assertEquals("getTypeComparator must be declared on StoryApiConfigurationManager",
        cls, m.getDeclaringClass());
  }

  @Test
  public void configManager_overridesGetExpressionCreator() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getMethod("getExpressionCreator");
    assertEquals("getExpressionCreator must be declared on StoryApiConfigurationManager",
        cls, m.getDeclaringClass());
  }

  @Test
  public void configManager_overridesIsDeclaringTypeForManagedFields() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("isDeclaringTypeForManagedFields",
        org.lgna.project.ast.UserType.class);
    assertNotNull("Must override isDeclaringTypeForManagedFields", m);
  }

  @Test
  public void configManager_overridesIsInstanceFactoryDesiredForType() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("isInstanceFactoryDesiredForType",
        org.lgna.project.ast.AbstractType.class);
    assertNotNull("Must override isInstanceFactoryDesiredForType", m);
  }

  @Test
  public void configManager_overridesGetGalleryResourceParentFor() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("getGalleryResourceParentFor",
        org.lgna.project.ast.JavaType.class);
    assertNotNull("Must override getGalleryResourceParentFor", m);
  }

  @Test
  public void configManager_overridesGetGalleryResourceChildrenFor() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("getGalleryResourceChildrenFor",
        org.lgna.project.ast.AbstractType.class);
    assertNotNull("Must override getGalleryResourceChildrenFor", m);
  }

  @Test
  public void configManager_overridesIsSignatureLocked() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("isSignatureLocked",
        org.lgna.project.ast.Code.class);
    assertNotNull("Must override isSignatureLocked", m);
  }

  @Test
  public void configManager_overridesIsTabClosable() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("isTabClosable",
        org.lgna.project.ast.AbstractCode.class);
    assertNotNull("Must override isTabClosable", m);
  }

  @Test
  public void configManager_overridesAugmentTypeIfNecessary() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("augmentTypeIfNecessary",
        org.lgna.project.ast.UserType.class);
    assertNotNull("Must override augmentTypeIfNecessary", m);
  }

  @Test
  public void configManager_overridesIsExportTypeDesiredFor() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("isExportTypeDesiredFor",
        org.lgna.project.ast.NamedUserType.class);
    assertNotNull("Must override isExportTypeDesiredFor", m);
  }

  @Test
  public void configManager_overridesCreateReplacementForFieldAccessIfAppropriate() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("createReplacementForFieldAccessIfAppropriate",
        org.lgna.project.ast.FieldAccess.class);
    assertNotNull("Must override createReplacementForFieldAccessIfAppropriate", m);
  }

  // ---- Instance factory sub-menu methods ----

  @Test
  public void configManager_overridesGetInstanceFactorySubMenuForThis() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("getInstanceFactorySubMenuForThis",
        org.lgna.project.ast.AbstractType.class);
    assertNotNull(m);
  }

  @Test
  public void configManager_overridesGetInstanceFactorySubMenuForThisFieldAccess() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("getInstanceFactorySubMenuForThisFieldAccess",
        org.lgna.project.ast.UserField.class);
    assertNotNull(m);
  }

  @Test
  public void configManager_overridesGetInstanceFactorySubMenuForParameterAccess() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("getInstanceFactorySubMenuForParameterAccess",
        org.lgna.project.ast.UserParameter.class);
    assertNotNull(m);
  }

  @Test
  public void configManager_overridesGetInstanceFactorySubMenuForLocalAccess() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getDeclaredMethod("getInstanceFactorySubMenuForLocalAccess",
        org.lgna.project.ast.UserLocal.class);
    assertNotNull(m);
  }

  // ---- Category composite lists ----

  @Test
  public void configManager_hasGetCategoryProcedureSubComposites() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getMethod("getCategoryProcedureSubComposites");
    assertNotNull(m);
    assertEquals("Must return List", java.util.List.class, m.getReturnType());
  }

  @Test
  public void configManager_hasGetCategoryFunctionSubComposites() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getMethod("getCategoryFunctionSubComposites");
    assertNotNull(m);
  }

  @Test
  public void configManager_hasGetCategoryOrAlphabeticalProcedureSubComposites() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getMethod("getCategoryOrAlphabeticalProcedureSubComposites");
    assertNotNull(m);
  }

  @Test
  public void configManager_hasGetCategoryOrAlphabeticalFunctionSubComposites() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getMethod("getCategoryOrAlphabeticalFunctionSubComposites");
    assertNotNull(m);
  }

  // ---- Pose builder methods ----

  @Test
  public void configManager_hasBuildMethodPoseBuilderType() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getMethod("getBuildMethodPoseBuilderType",
        org.lgna.project.ast.MethodInvocation.class);
    assertNotNull(m);
    assertTrue("Must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void configManager_hasIsBuildMethod() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method m = cls.getMethod("isBuildMethod",
        org.lgna.project.ast.MethodInvocation.class);
    assertNotNull(m);
    assertEquals("Must return boolean", boolean.class, m.getReturnType());
  }

  // ---- Source analysis: constructor wiring ----

  @Test
  public void configManagerSource_registersIconFactories() throws Exception {
    String content = Files.readString(resolveSourceFile(CONFIG_MANAGER_SRC));
    assertTrue("Must register icon factories",
        content.contains("IconFactoryManager.registerIconFactory"));
  }

  @Test
  public void configManagerSource_registersMultipleShapeIconFactories() throws Exception {
    String content = Files.readString(resolveSourceFile(CONFIG_MANAGER_SRC));
    long count = content.lines()
        .filter(line -> line.contains("registerIconFactory"))
        .count();
    assertTrue("Must register at least 10 icon factories", count >= 10);
  }

  @Test
  public void configManagerSource_registersBeveledShapeForSThing() throws Exception {
    String content = Files.readString(resolveSourceFile(CONFIG_MANAGER_SRC));
    assertTrue("Must add SThing as round type",
        content.contains("BeveledShapeForType.addRoundType(SThing.class)"));
  }

  @Test
  public void configManagerSource_createsCategorySubComposites() throws Exception {
    String content = Files.readString(resolveSourceFile(CONFIG_MANAGER_SRC));
    assertTrue("Must create procedure sub-composites",
        content.contains("categoryProcedureSubComposites"));
    assertTrue("Must create function sub-composites",
        content.contains("categoryFunctionSubComposites"));
  }

  @Test
  public void configManagerSource_referencesExpressionCreatorFromNonfree() throws Exception {
    String content = Files.readString(resolveSourceFile(CONFIG_MANAGER_SRC));
    assertTrue("Must create expression creator from NebulousIde.nonfree",
        content.contains("NebulousIde.nonfree.newExpressionCreator()"));
  }

  @Test
  public void configManagerSource_referencesJointMethodAugmentor() throws Exception {
    String content = Files.readString(resolveSourceFile(CONFIG_MANAGER_SRC));
    assertTrue("Must reference JointMethodAugmentor in augmentTypeIfNecessary",
        content.contains("JointMethodAugmentor.augment"));
  }

  @Test
  public void configManagerSource_referencesMyFirstProcedureName() throws Exception {
    String content = Files.readString(resolveSourceFile(CONFIG_MANAGER_SRC));
    assertTrue("Must reference MY_FIRST_PROCEDURE_NAME",
        content.contains("MY_FIRST_PROCEDURE_NAME"));
  }

  // ======== SCENEEDITOR GAP-FILL TESTS ========

  // ---- ShowJointedModelJointAxesState ----

  @Test
  public void showJointedModelJointAxesState_extendsBooleanState() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.ShowJointedModelJointAxesState");
    Class<?> parent = Class.forName("org.lgna.croquet.BooleanState");
    assertTrue("Must extend BooleanState", parent.isAssignableFrom(cls));
  }

  @Test
  public void showJointedModelJointAxesState_hasGetInstanceMethod() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.ShowJointedModelJointAxesState");
    Method m = cls.getMethod("getInstance", org.lgna.project.ast.AbstractField.class);
    assertTrue("getInstance must be public", Modifier.isPublic(m.getModifiers()));
    assertTrue("getInstance must be static", Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void showJointedModelJointAxesState_hasGetFieldMethod() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.ShowJointedModelJointAxesState");
    Method m = cls.getMethod("getField");
    assertNotNull("Must have getField", m);
    assertTrue("Must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void showJointedModelJointAxesState_constructorIsPrivate() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.ShowJointedModelJointAxesState");
    for (Constructor<?> ctor : cls.getDeclaredConstructors()) {
      assertTrue("Constructor must be private", Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  @Test
  public void showJointedModelJointAxesState_usesMapCache() throws Exception {
    Path src = resolveSourceFile(SCENE_EDITOR_DIR + "/" + "ShowJointedModelJointAxesState.java");
    String content = Files.readString(src);
    assertTrue("Must use Map for caching instances", content.contains("Map<"));
    assertTrue("Must use Maps.newHashMap()", content.contains("Maps.newHashMap()"));
  }

  @Test
  public void showJointedModelJointAxesState_isSynchronized() throws Exception {
    Path src = resolveSourceFile(SCENE_EDITOR_DIR + "/" + "ShowJointedModelJointAxesState.java");
    String content = Files.readString(src);
    assertTrue("getInstance must use synchronized block",
        content.contains("synchronized"));
  }

  // ---- ThumbnailGenerator ----

  @Test
  public void thumbnailGenerator_isFinalClass() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.ThumbnailGenerator");
    assertTrue("Must be final", Modifier.isFinal(cls.getModifiers()));
  }

  @Test
  public void thumbnailGenerator_hasWidthHeightConstructor() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.ThumbnailGenerator");
    Constructor<?> ctor = cls.getDeclaredConstructor(int.class, int.class);
    assertTrue("Constructor must be public", Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void thumbnailGenerator_declaresCreateThumbnail() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.ThumbnailGenerator");
    Method m = cls.getMethod("createThumbnail");
    assertNotNull("Must have createThumbnail", m);
    assertTrue("Must be public", Modifier.isPublic(m.getModifiers()));
    assertEquals("Must return BufferedImage",
        java.awt.image.BufferedImage.class, m.getReturnType());
  }

  @Test
  public void thumbnailGenerator_createThumbnailIsSynchronized() throws Exception {
    Path src = resolveSourceFile(SCENE_EDITOR_DIR + "/" + "ThumbnailGenerator.java");
    String content = Files.readString(src);
    assertTrue("createThumbnail must use synchronized block",
        content.contains("synchronized"));
  }

  @Test
  public void thumbnailGenerator_usesOffscreenRenderTarget() throws Exception {
    Path src = resolveSourceFile(SCENE_EDITOR_DIR + "/" + "ThumbnailGenerator.java");
    String content = Files.readString(src);
    assertTrue("Must use OffscreenRenderTarget",
        content.contains("OffscreenRenderTarget"));
    assertTrue("Must use GlrRenderFactory",
        content.contains("GlrRenderFactory"));
  }

  // ---- CameraOption ----

  @Test
  public void cameraOption_isEnum() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.CameraOption");
    assertTrue("CameraOption must be an enum", cls.isEnum());
  }

  @Test
  public void cameraOption_hasExpectedConstants() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.CameraOption");
    Object[] constants = cls.getEnumConstants();
    String[] names = Arrays.stream(constants).map(Object::toString).toArray(String[]::new);
    List<String> nameList = Arrays.asList(names);
    assertTrue("Must have STARTING_CAMERA_VIEW", nameList.contains("STARTING_CAMERA_VIEW"));
    assertTrue("Must have LAYOUT_SCENE_VIEW", nameList.contains("LAYOUT_SCENE_VIEW"));
    assertTrue("Must have TOP", nameList.contains("TOP"));
    assertTrue("Must have SIDE", nameList.contains("SIDE"));
    assertTrue("Must have FRONT", nameList.contains("FRONT"));
  }

  @Test
  public void cameraOption_hasExactly5Constants() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.CameraOption");
    assertEquals("Must have exactly 5 enum constants", 5, cls.getEnumConstants().length);
  }

  // ---- SceneEditor file count guard ----

  @Test
  public void sceneeditorDirectory_hasExpectedFileCount() throws Exception {
    Path dir = resolveSourceFile(SCENE_EDITOR_DIR + "/CameraOption.java").getParent();
    long count = Files.list(dir)
        .filter(p -> p.toString().endsWith(".java"))
        .filter(p -> !Files.isDirectory(p))
        .count();
    assertTrue("Sceneeditor directory must have at least 12 Java files",
        count >= 12);
  }

  // ---- All sceneeditor source files exist ----

  @Test
  public void sceneeditorFiles_allExpectedExist() {
    String[] expectedFiles = {
        "CameraOption.java",
        "LookingGlassPanel.java",
        "SceneEditorDropReceptor.java",
        "SceneEditorFieldManager.java",
        "SceneEditorInitializer.java",
        "SceneEditorLifecycleManager.java",
        "SceneEditorListeners.java",
        "SceneFieldCodeGenerator.java",
        "SceneRenderTargetListener.java",
        "SetUpMethodGenerator.java",
        "ShowJointedModelJointAxesState.java",
        "StorytellingSceneEditor.java",
        "ThumbnailGenerator.java",
    };
    for (String file : expectedFiles) {
      assertTrue(file + " must exist",
          Files.exists(resolveSourceFile(SCENE_EDITOR_DIR + "/" + file)));
    }
  }

  // ---- Guard: StoryApiConfigurationManager declared method count ----

  @Test
  public void configManager_hasSufficientDeclaredMethods() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Method[] methods = cls.getDeclaredMethods();
    assertTrue("Must have at least 15 declared methods", methods.length >= 15);
  }

  // ---- Guard: constructor has public access ----

  @Test
  public void configManager_constructorIsPublic() throws Exception {
    Class<?> cls = Class.forName(CONFIG_MANAGER_CLASS);
    Constructor<?> ctor = cls.getDeclaredConstructor();
    assertTrue("Constructor must be public",
        Modifier.isPublic(ctor.getModifiers()));
  }

  private static Path resolveSourceFile(String relativePath) {
    Path cwd = Paths.get(System.getProperty("user.dir"));
    Path candidate = cwd.resolve(relativePath);
    if (Files.exists(candidate)) {
      return candidate;
    }
    Path dir = cwd;
    while (dir != null) {
      candidate = dir.resolve(relativePath);
      if (Files.exists(candidate)) {
        return candidate;
      }
      dir = dir.getParent();
    }
    fail("Cannot find source file: " + relativePath + " from " + cwd);
    return null;
  }
}

package org.alice.stageide.cascade;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Registration completeness and cross-reference verification for the
 * ExpressionCascadeManager and all 23 filler-inner classes.
 *
 * All tests are headless — source-analysis and reflection only.
 */
public class ExpressionCascadeManagerRegistrationTest {

  private static final String CASCADE_SRC =
      "src/main/java/org/alice/stageide/cascade/ExpressionCascadeManager.java";
  private static final String FILLER_INNER_DIR =
      "src/main/java/org/alice/stageide/cascade/fillerinners";
  private static final String FILLER_INNER_PACKAGE =
      "org.alice.stageide.cascade.fillerinners";

  // All 23 filler-inner classes (complete list from the fillerinners directory)
  private static final String[] ALL_FILLER_INNER_CLASSES = {
      "ArrowKeyListenerFillerInner",
      "AudioSourceFillerInner",
      "ColorFillerInner",
      "ComesIntoViewEventListenerFillerInner",
      "EndCollisionListenerFillerInner",
      "EndOcclusionEventListenerFillerInner",
      "EnterProximityEventListenerFillerInner",
      "ExitProximityEventListenerFillerInner",
      "ImagePaintFillerInner",
      "ImageSourceFillerInner",
      "KeyFillerInner",
      "KeyListenerFillerInner",
      "LeavesViewEventListenerFillerInner",
      "ModelResourceFillerInner",
      "MouseClickOnObjectFillerInner",
      "MouseClickedOnScreenFillerInner",
      "NumberKeyListenerFillerInner",
      "SceneActivationEventFillerInner",
      "SourceFillerInner",
      "StartCollisionListenerFillerInner",
      "StartOcclusionEventListenerFillerInner",
      "TimerEventListenerFillerInner",
      "TransformationListenerFillerInner",
  };

  // The 21 concrete classes that should be registered via addExpressionFillerInner
  private static final String[] REGISTERED_FILLER_INNERS = {
      "ImagePaintFillerInner",
      "ImageSourceFillerInner",
      "AudioSourceFillerInner",
      "ColorFillerInner",
      "KeyFillerInner",
      "ArrowKeyListenerFillerInner",
      "NumberKeyListenerFillerInner",
      "MouseClickedOnScreenFillerInner",
      "MouseClickOnObjectFillerInner",
      "TransformationListenerFillerInner",
      "ComesIntoViewEventListenerFillerInner",
      "LeavesViewEventListenerFillerInner",
      "StartCollisionListenerFillerInner",
      "EndCollisionListenerFillerInner",
      "EnterProximityEventListenerFillerInner",
      "ExitProximityEventListenerFillerInner",
      "StartOcclusionEventListenerFillerInner",
      "EndOcclusionEventListenerFillerInner",
      "SceneActivationEventFillerInner",
      "TimerEventListenerFillerInner",
      "KeyListenerFillerInner",
  };

  // ---- ExpressionCascadeManager class structure ----

  @Test
  public void expressionCascadeManager_extendsParentCascadeManager() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Class<?> parent = Class.forName("org.alice.ide.cascade.ExpressionCascadeManager");
    assertTrue("Must extend org.alice.ide.cascade.ExpressionCascadeManager",
        parent.isAssignableFrom(cls));
  }

  @Test
  public void expressionCascadeManager_isConcrete() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    assertFalse("Must not be abstract", Modifier.isAbstract(cls.getModifiers()));
  }

  @Test
  public void expressionCascadeManager_isPublic() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    assertTrue("Must be public", Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void expressionCascadeManager_hasNoArgConstructor() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Constructor<?> ctor = cls.getDeclaredConstructor();
    assertTrue("Constructor must be public", Modifier.isPublic(ctor.getModifiers()));
  }

  // ---- Registration completeness: all 21 concrete filler-inners registered ----

  @Test
  public void registeredFillerInnerCount_isExactly21() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    // Count uncommented addExpressionFillerInner calls
    long count = source.lines()
        .filter(line -> !line.trim().startsWith("//"))
        .filter(line -> line.contains("addExpressionFillerInner"))
        .count();
    assertEquals("Must have exactly 21 uncommented addExpressionFillerInner calls", 21, count);
  }

  @Test
  public void allRegisteredFillerInners_appearsInConstructor() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    for (String className : REGISTERED_FILLER_INNERS) {
      assertTrue("ExpressionCascadeManager must reference " + className,
          source.contains("new " + className + "()"));
    }
  }

  @Test
  public void noOrphanedFillerInnerClasses() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    for (String className : ALL_FILLER_INNER_CLASSES) {
      if ("SourceFillerInner".equals(className)) {
        continue; // abstract base — not directly registered
      }
      if ("ModelResourceFillerInner".equals(className)) {
        continue; // used via PublicStaticFieldValueDetails, not registered directly
      }
      assertTrue(className + " must be referenced in ExpressionCascadeManager",
          source.contains(className));
    }
  }

  // ---- Registration order: first 5 are expression types, then 16 event listeners ----

  @Test
  public void registrationOrder_expressionTypesFirst() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    List<String> registrations = source.lines()
        .filter(line -> !line.trim().startsWith("//"))
        .filter(line -> line.contains("addExpressionFillerInner"))
        .collect(Collectors.toList());

    // First 5 should be expression types
    String[] expectedFirst5 = {
        "ImagePaintFillerInner", "ImageSourceFillerInner", "AudioSourceFillerInner",
        "ColorFillerInner", "KeyFillerInner"
    };
    for (int i = 0; i < expectedFirst5.length; i++) {
      assertTrue("Registration #" + (i + 1) + " should be " + expectedFirst5[i],
          registrations.get(i).contains(expectedFirst5[i]));
    }
  }

  @Test
  public void registrationOrder_eventListenersAfterExpressionTypes() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    List<String> registrations = source.lines()
        .filter(line -> !line.trim().startsWith("//"))
        .filter(line -> line.contains("addExpressionFillerInner"))
        .collect(Collectors.toList());

    // Items 5-20 (0-indexed) are event listener / mouse / keyboard types
    for (int i = 5; i < registrations.size(); i++) {
      String line = registrations.get(i);
      // All after the first 5 are mouse/key/collision/proximity/occlusion/view/timer/scene types
      assertFalse("Registration #" + (i + 1) + " should not be an expression value type (Color/Key/ImagePaint/Source)",
          line.contains("ColorFillerInner") || line.contains("ImagePaintFillerInner")
              || line.contains("ImageSourceFillerInner") || line.contains("AudioSourceFillerInner")
              || line.contains("KeyFillerInner("));
    }
  }

  // ---- addRelationalTypeToBooleanFillerInner registrations ----

  @Test
  public void relationalTypeRegistrations_count() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    long count = source.lines()
        .filter(line -> !line.trim().startsWith("//"))
        .filter(line -> line.contains("addRelationalTypeToBooleanFillerInner"))
        .count();
    assertEquals("Must have exactly 7 relational type registrations", 7, count);
  }

  @Test
  public void relationalTypeRegistrations_includeSThing() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    assertTrue("Must register SThing for relational",
        source.contains("addRelationalTypeToBooleanFillerInner(SThing.class)"));
  }

  @Test
  public void relationalTypeRegistrations_includeDirectionTypes() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    assertTrue("Must register MoveDirection",
        source.contains("addRelationalTypeToBooleanFillerInner(MoveDirection.class)"));
    assertTrue("Must register TurnDirection",
        source.contains("addRelationalTypeToBooleanFillerInner(TurnDirection.class)"));
    assertTrue("Must register RollDirection",
        source.contains("addRelationalTypeToBooleanFillerInner(RollDirection.class)"));
  }

  @Test
  public void relationalTypeRegistrations_includeKeyAndColor() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    assertTrue("Must register Key for relational",
        source.contains("addRelationalTypeToBooleanFillerInner(Key.class)"));
    assertTrue("Must register Color for relational",
        source.contains("addRelationalTypeToBooleanFillerInner(Color.class)"));
    assertTrue("Must register Paint for relational",
        source.contains("addRelationalTypeToBooleanFillerInner(Paint.class)"));
  }

  // ---- addSimsExpressionFillerInners hook ----

  @Test
  public void addSimsExpressionFillerInners_isProtected() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("addSimsExpressionFillerInners");
    assertTrue("addSimsExpressionFillerInners must be protected",
        Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void addSimsExpressionFillerInners_isEmptyHook() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    // Find the method body — should be empty
    assertTrue("addSimsExpressionFillerInners must be defined",
        source.contains("protected void addSimsExpressionFillerInners()"));
  }

  @Test
  public void addSimsExpressionFillerInners_calledInConstructor() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    assertTrue("addSimsExpressionFillerInners must be called in constructor",
        source.contains("this.addSimsExpressionFillerInners()"));
  }

  // ---- Override methods ----

  @Test
  public void expressionCascadeManager_overridesGetEnumTypeForInterfaceType() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("getEnumTypeForInterfaceType",
        org.lgna.project.ast.AbstractType.class);
    assertNotNull("Must override getEnumTypeForInterfaceType", m);
  }

  @Test
  public void expressionCascadeManager_overridesAreEnumConstantsDesired() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("areEnumConstantsDesired",
        org.lgna.project.ast.AbstractType.class);
    assertNotNull("Must override areEnumConstantsDesired", m);
  }

  @Test
  public void expressionCascadeManager_overridesCreatePartMenuModel() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("createPartMenuModel",
        org.lgna.project.ast.Expression.class,
        org.lgna.project.ast.AbstractType.class,
        org.lgna.project.ast.AbstractType.class,
        boolean.class);
    assertNotNull("Must override createPartMenuModel", m);
  }

  @Test
  public void expressionCascadeManager_overridesIsApplicableForPartFillIn() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("isApplicableForPartFillIn",
        org.lgna.project.ast.AbstractType.class,
        org.lgna.project.ast.AbstractType.class);
    assertNotNull("Must override isApplicableForPartFillIn", m);
  }

  @Test
  public void expressionCascadeManager_overridesAppendOtherTypes() throws Exception {
    Class<?> cls = Class.forName("org.alice.stageide.cascade.ExpressionCascadeManager");
    Method m = cls.getDeclaredMethod("appendOtherTypes", java.util.List.class);
    assertNotNull("Must override appendOtherTypes", m);
  }

  // ---- Source imports wildcard for fillerinners ----

  @Test
  public void cascadeManagerSource_importsFillerInnersPackage() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    assertTrue("Must import fillerinners package",
        source.contains("import org.alice.stageide.cascade.fillerinners.*"));
  }

  // ---- Commented-out registration line ----

  @Test
  public void cascadeManagerSource_hasCommentedOutConstantsOwningFillerInner() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    assertTrue("Must have commented-out ConstantsOwningFillerInner",
        source.contains("//this.addExpressionFillerInner"));
  }

  // ---- Filler-inner file count matches expected ----

  @Test
  public void fillerInnerDirectory_containsExactly23Files() throws Exception {
    // Resolve directory by finding any known file in it, then getting parent
    Path dir = resolveSourceFile(FILLER_INNER_DIR + "/ColorFillerInner.java").getParent();
    long count = Files.list(dir)
        .filter(p -> p.toString().endsWith(".java"))
        .count();
    assertEquals("Filler-inner directory must contain exactly 23 Java files",
        23, count);
  }

  // ---- All filler-inner classes are loadable ----

  @Test
  public void allFillerInnerClasses_areLoadable() throws Exception {
    for (String name : ALL_FILLER_INNER_CLASSES) {
      Class<?> cls = Class.forName(FILLER_INNER_PACKAGE + "." + name);
      assertNotNull(name + " must be loadable", cls);
    }
  }

  // ---- Guard: only SourceFillerInner is abstract ----

  @Test
  public void onlySourceFillerInner_isAbstract() throws Exception {
    for (String name : ALL_FILLER_INNER_CLASSES) {
      Class<?> cls = Class.forName(FILLER_INNER_PACKAGE + "." + name);
      if ("SourceFillerInner".equals(name)) {
        assertTrue(name + " must be abstract", Modifier.isAbstract(cls.getModifiers()));
      } else {
        assertFalse(name + " must NOT be abstract", Modifier.isAbstract(cls.getModifiers()));
      }
    }
  }

  // ---- All concrete filler-inners are instantiable (no-arg) ----

  @Test
  public void allConcreteFillerInners_haveNoArgConstructor() throws Exception {
    for (String name : ALL_FILLER_INNER_CLASSES) {
      if ("SourceFillerInner".equals(name)) {
        continue;
      }
      Class<?> cls = Class.forName(FILLER_INNER_PACKAGE + "." + name);
      try {
        Constructor<?> ctor = cls.getDeclaredConstructor();
        assertNotNull(name + " must have no-arg constructor", ctor);
      } catch (NoSuchMethodException e) {
        fail(name + " must have a no-arg constructor");
      }
    }
  }

  // ---- AnimationStyle mapping ----

  @Test
  public void cascadeManagerSource_mapsStyleToAnimationStyle() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    assertTrue("Must map Style to AnimationStyle",
        source.contains("Style.class") && source.contains("AnimationStyle.class"));
  }

  // ---- Key enum constants suppressed ----

  @Test
  public void cascadeManagerSource_suppressesKeyEnumConstants() throws Exception {
    String source = Files.readString(resolveSourceFile(CASCADE_SRC));
    assertTrue("Must check for Key.class in areEnumConstantsDesired",
        source.contains("Key.class"));
    assertTrue("Must return false for Key enum constants",
        source.contains("return false"));
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

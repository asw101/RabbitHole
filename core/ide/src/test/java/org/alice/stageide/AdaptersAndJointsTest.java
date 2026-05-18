package org.alice.stageide;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Tests for apis/story/event adapters, joint package classes,
 * modelresource keys, and run package classes.
 */
public class AdaptersAndJointsTest {

  // ── AbstractAdapter contract ─────────────────────────────────────

  @Test
  public void abstractAdapter_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(Class.forName("org.alice.stageide.apis.story.event.AbstractAdapter").getModifiers()));
  }

  @Test
  public void abstractAdapter_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName("org.alice.stageide.apis.story.event.AbstractAdapter").getModifiers()));
  }

  @Test
  public void abstractAdapter_invokeEntryPoint_protected() throws ClassNotFoundException {
    Class<?> c = Class.forName("org.alice.stageide.apis.story.event.AbstractAdapter");
    assertTrue(Arrays.stream(c.getDeclaredMethods())
        .anyMatch(m -> "invokeEntryPoint".equals(m.getName()) && Modifier.isProtected(m.getModifiers())));
  }

  @Test
  public void abstractAdapter_contextField() throws Exception {
    Class<?> c = Class.forName("org.alice.stageide.apis.story.event.AbstractAdapter");
    Field f = c.getDeclaredField("context");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void abstractAdapter_userInstanceField() throws Exception {
    Class<?> c = Class.forName("org.alice.stageide.apis.story.event.AbstractAdapter");
    Field f = c.getDeclaredField("userInstance");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void abstractAdapter_lambdaField() throws Exception {
    Class<?> c = Class.forName("org.alice.stageide.apis.story.event.AbstractAdapter");
    Field f = c.getDeclaredField("lambda");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void abstractAdapter_singleAbstractMethodField() throws Exception {
    Class<?> c = Class.forName("org.alice.stageide.apis.story.event.AbstractAdapter");
    Field f = c.getDeclaredField("singleAbstractMethod");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  // ── All 16 concrete adapters ─────────────────────────────────────

  private static final String[] ADAPTER_NAMES = {
      "SceneActivationAdapter", "MouseClickOnScreenAdapter",
      "MouseClickOnObjectAdapter", "KeyAdapter",
      "ArrowKeyAdapter", "NumberKeyAdapter",
      "TransformationEventAdapter", "ComesIntoViewEventAdapter",
      "ComesOutOfViewEventAdapter", "StartCollisionAdapter",
      "EndCollisionAdapter", "EnterProximityAdapter",
      "ExitProximityAdapter", "StartOcclusionEventAdapter",
      "EndOcclusionEventAdapter", "TimerEventAdapter"
  };

  @Test
  public void allAdapters_loadSuccessfully() throws ClassNotFoundException {
    for (String name : ADAPTER_NAMES) {
      assertNotNull(name, Class.forName("org.alice.stageide.apis.story.event." + name));
    }
  }

  @Test
  public void allAdapters_extendAbstractAdapter() throws ClassNotFoundException {
    Class<?> base = Class.forName("org.alice.stageide.apis.story.event.AbstractAdapter");
    for (String name : ADAPTER_NAMES) {
      assertTrue(name, base.isAssignableFrom(Class.forName("org.alice.stageide.apis.story.event." + name)));
    }
  }

  @Test
  public void allAdapters_areConcrete() throws ClassNotFoundException {
    for (String name : ADAPTER_NAMES) {
      assertFalse(name, Modifier.isAbstract(Class.forName("org.alice.stageide.apis.story.event." + name).getModifiers()));
    }
  }

  @Test
  public void allAdapters_arePublic() throws ClassNotFoundException {
    for (String name : ADAPTER_NAMES) {
      assertTrue(name, Modifier.isPublic(Class.forName("org.alice.stageide.apis.story.event." + name).getModifiers()));
    }
  }

  @Test
  public void allAdapters_implementAtLeastOneInterface() throws ClassNotFoundException {
    for (String name : ADAPTER_NAMES) {
      assertTrue(name, Class.forName("org.alice.stageide.apis.story.event." + name).getInterfaces().length >= 1);
    }
  }

  // ── SceneAdapter ─────────────────────────────────────────────────

  @Test
  public void sceneAdapter_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.ast.SceneAdapter");
  }

  @Test
  public void sceneAdapter_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName("org.alice.stageide.ast.SceneAdapter").getModifiers()));
  }

  // ── KeyCodec ─────────────────────────────────────────────────────

  @Test
  public void keyCodec_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.apis.org.lgna.story.codecs.KeyCodec");
  }

  @Test
  public void keyCodec_hasSingletonField() throws Exception {
    Class<?> c = Class.forName("org.alice.stageide.apis.org.lgna.story.codecs.KeyCodec");
    Field f = c.getField("SINGLETON");
    assertTrue(Modifier.isPublic(f.getModifiers()));
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  // ── Joint package ────────────────────────────────────────────────

  @Test
  public void jointsSubMenu_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.joint.JointsSubMenu");
  }

  @Test
  public void jointsSubMenu_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName("org.alice.stageide.joint.JointsSubMenu").getModifiers()));
  }

  @Test
  public void jointsSubMenuManager_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.joint.JointsSubMenuManager");
  }

  @Test
  public void otherSBipedJointsSubMenu_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.joint.OtherSBipedJointsSubMenu");
  }

  @Test
  public void otherSFlyerJointsSubMenu_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.joint.OtherSFlyerJointsSubMenu");
  }

  @Test
  public void otherSQuadrupedJointsSubMenu_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.joint.OtherSQuadrupedJointsSubMenu");
  }

  @Test
  public void allOtherJointSubMenus_extendJointsSubMenu() throws ClassNotFoundException {
    Class<?> parent = Class.forName("org.alice.stageide.joint.JointsSubMenu");
    for (String name : new String[]{"OtherSBipedJointsSubMenu", "OtherSFlyerJointsSubMenu", "OtherSQuadrupedJointsSubMenu"}) {
      assertTrue(name, parent.isAssignableFrom(Class.forName("org.alice.stageide.joint." + name)));
    }
  }

  // ── ModelResource keys ───────────────────────────────────────────

  @Test
  public void resourceKey_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.ResourceKey");
  }

  @Test
  public void rootResourceKey_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.RootResourceKey");
  }

  @Test
  public void classResourceKey_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.ClassResourceKey");
  }

  @Test
  public void enumConstantResourceKey_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.EnumConstantResourceKey");
  }

  @Test
  public void dynamicResourceKey_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.DynamicResourceKey");
  }

  @Test
  public void instanceCreatorKey_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.InstanceCreatorKey");
  }

  @Test
  public void tagKey_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.TagKey");
  }

  @Test
  public void groupTagKey_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.GroupTagKey");
  }

  @Test
  public void themeTagKey_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.ThemeTagKey");
  }

  @Test
  public void resourceNode_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.ResourceNode");
  }

  @Test
  public void resourceNodeCodec_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.modelresource.ResourceNodeCodec");
  }

  // ── Run package ──────────────────────────────────────────────────

  @Test
  public void runComposite_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.run.RunComposite");
  }

  @Test
  public void runComposite_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName("org.alice.stageide.run.RunComposite").getModifiers()));
  }

  @Test
  public void fastForwardToStatementOperation_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.run.FastForwardToStatementOperation");
  }

  // ── AST source import value creators ─────────────────────────────

  @Test
  public void sourceImportValueCreator_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.ast.source.SourceImportValueCreator");
  }

  @Test
  public void sourceImportValueCreator_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(Class.forName("org.alice.stageide.ast.source.SourceImportValueCreator").getModifiers()));
  }

  @Test
  public void imageSourceImportValueCreator_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.ast.source.ImageSourceImportValueCreator");
  }

  @Test
  public void imageSourceImportValueCreator_extendsBase() throws ClassNotFoundException {
    Class<?> parent = Class.forName("org.alice.stageide.ast.source.SourceImportValueCreator");
    assertTrue(parent.isAssignableFrom(Class.forName("org.alice.stageide.ast.source.ImageSourceImportValueCreator")));
  }

  @Test
  public void audioSourceImportValueCreator_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.ast.source.AudioSourceImportValueCreator");
  }

  @Test
  public void audioSourceImportValueCreator_extendsBase() throws ClassNotFoundException {
    Class<?> parent = Class.forName("org.alice.stageide.ast.source.SourceImportValueCreator");
    assertTrue(parent.isAssignableFrom(Class.forName("org.alice.stageide.ast.source.AudioSourceImportValueCreator")));
  }

  // ── Perspective state ────────────────────────────────────────────

  @Test
  public void perspectiveState_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.perspectives.PerspectiveState");
  }

  @Test
  public void codePerspective_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.perspectives.CodePerspective");
  }

  @Test
  public void setupScenePerspective_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.perspectives.SetupScenePerspective");
  }

  // ── SceneTypeMetaState ───────────────────────────────────────────

  @Test
  public void sceneTypeMetaState_exists() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.project.SceneTypeMetaState");
  }

  @Test
  public void sceneTypeMetaState_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName("org.alice.stageide.project.SceneTypeMetaState").getModifiers()));
  }
}

package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserPackage;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.SCamera;
import org.lgna.story.SScene;
import org.lgna.story.SVRUser;

import java.awt.Frame;

import static org.junit.Assert.*;

public class StageIdeConfigurationTest {
  private final StageIdeConfiguration configuration = new StageIdeConfiguration();

  @Test
  public void automaticDisplayDeltaTracksIconifyTransitions() {
    assertEquals(1, configuration.getAutomaticDisplayDelta(Frame.ICONIFIED, Frame.NORMAL));
    assertEquals(-1, configuration.getAutomaticDisplayDelta(Frame.NORMAL, Frame.ICONIFIED));
    assertEquals(0, configuration.getAutomaticDisplayDelta(Frame.NORMAL, Frame.NORMAL));
  }

  @Test
  public void declarationFilterExcludesGeneratedSetupMethodOnly() {
    UserMethod generatedSetup = new UserMethod(StageIDE.PERFORM_GENERATED_SET_UP_METHOD_NAME, void.class, new UserParameter[0], new BlockStatement());
    UserMethod userMethod = new UserMethod("myMethod", void.class, new UserParameter[0], new BlockStatement());

    assertFalse(configuration.isDeclarationIncluded(generatedSetup));
    assertTrue(configuration.isDeclarationIncluded(userMethod));
  }

  @Test
  public void instanceCreationAllowedRejectsSceneCameraAndVrTypes() {
    assertFalse(configuration.isInstanceCreationAllowed(SScene.class));
    assertFalse(configuration.isInstanceCreationAllowed(SCamera.class));
    assertFalse(configuration.isInstanceCreationAllowed(SVRUser.class));
    assertTrue(configuration.isInstanceCreationAllowed(String.class));
  }

  @Test
  public void sceneScopedRequiresExactSelectedSceneType() {
    NamedUserType sceneType = new NamedUserType("Scene", new UserPackage("test"), Object.class,
        new NamedUserConstructor[0], new UserMethod[0], new UserField[0]);
    NamedUserType otherType = new NamedUserType("Other", new UserPackage("test"), Object.class,
        new NamedUserConstructor[0], new UserMethod[0], new UserField[0]);

    assertTrue(configuration.isSceneScoped(sceneType, sceneType));
    assertFalse(configuration.isSceneScoped(sceneType, otherType));
    assertFalse(configuration.isSceneScoped(null, sceneType));
  }
}

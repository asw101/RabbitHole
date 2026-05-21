package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.SBiped;
import org.lgna.story.SCamera;
import org.lgna.story.SVRUser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class StoryApiConfigurationManagerMenuModelTest {
  @Test
  public void instanceFactoryMenus_coverCameraVrAndJointedBranches() {
    StoryApiConfigurationManager manager = StoryApiConfigurationManager.getInstance();

    UserField cameraField = new UserField();
    cameraField.valueType.setValue(JavaType.getInstance(SCamera.class));
    assertNotNull(manager.getInstanceFactorySubMenuForThisFieldAccess(cameraField));
    assertSame(manager.getInstanceFactorySubMenuForThisFieldAccess(cameraField), manager.getInstanceFactorySubMenuForThisFieldAccess(cameraField));

    UserField vrField = new UserField();
    vrField.valueType.setValue(JavaType.getInstance(SVRUser.class));
    assertNotNull(manager.getInstanceFactorySubMenuForThisFieldAccess(vrField));

    UserParameter parameter = new UserParameter("jointed", JavaType.getInstance(SBiped.class));
    UserLocal local = new UserLocal("jointedLocal", JavaType.getInstance(SBiped.class), false);
    assertNotNull(manager.getInstanceFactorySubMenuForParameterAccess(parameter));
    assertNotNull(manager.getInstanceFactorySubMenuForLocalAccess(local));
    assertNull(manager.getInstanceFactorySubMenuForThis(JavaType.getInstance(String.class)));
  }
}

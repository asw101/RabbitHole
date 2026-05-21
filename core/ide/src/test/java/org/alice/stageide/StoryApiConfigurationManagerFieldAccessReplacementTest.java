package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.story.SScene;
import org.lgna.story.SThing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class StoryApiConfigurationManagerFieldAccessReplacementTest {
  @Test
  public void createReplacementForFieldAccessIfAppropriate_onlyWrapsSceneThingFields() {
    StoryApiConfigurationManager manager = StoryApiConfigurationManager.getInstance();
    NamedUserType sceneType = new NamedUserType();
    sceneType.name.setValue("SceneType");
    sceneType.superType.setValue(JavaType.getInstance(SScene.class));
    UserField thingField = new UserField();
    thingField.name.setValue("thingField");
    thingField.valueType.setValue(JavaType.getInstance(SThing.class));
    sceneType.fields.add(thingField);

    UserField textField = new UserField();
    textField.name.setValue("textField");
    textField.valueType.setValue(JavaType.getInstance(String.class));
    sceneType.fields.add(textField);

    assertNotNull(manager.createReplacementForFieldAccessIfAppropriate(new FieldAccess(new ThisExpression(), thingField)));
    assertNull(manager.createReplacementForFieldAccessIfAppropriate(new FieldAccess(new ThisExpression(), textField)));
  }
}

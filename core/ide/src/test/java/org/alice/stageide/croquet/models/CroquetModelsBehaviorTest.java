package org.alice.stageide.croquet.models;

import org.alice.ide.croquet.models.cascade.StaticFieldAccessFillIn;
import org.alice.stageide.croquet.models.cascade.keymenus.AbstractKeyCascadeMenu;
import org.alice.stageide.croquet.models.cascade.keymenus.ArrowsKeyCascadeMenu;
import org.alice.stageide.croquet.models.cascade.keymenus.DigitsKeyCascadeMenu;
import org.alice.stageide.croquet.models.cascade.keymenus.LettersKeyCascadeMenu;
import org.alice.stageide.croquet.models.cascade.source.AudioSourceFillIn;
import org.alice.stageide.croquet.models.cascade.source.ImageSourceFillIn;
import org.alice.stageide.croquet.models.cascade.source.SourceFillIn;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.common.resources.AudioResource;
import org.lgna.common.resources.ImageResource;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.imp.cascade.BlankNode;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.story.AudioSource;
import org.lgna.story.ImageSource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CroquetModelsBehaviorTest {
  private static Method updateBlankChildrenMethod;

  @BeforeClass
  public static void setUpReflection() throws Exception {
    updateBlankChildrenMethod = AbstractKeyCascadeMenu.class.getDeclaredMethod(
        "updateBlankChildren",
        List.class,
        BlankNode.class);
    updateBlankChildrenMethod.setAccessible(true);
  }

  private void assertMenuFields(AbstractKeyCascadeMenu menu, String... expectedFieldNames) throws Exception {
    List<CascadeBlankChild> blankChildren = new ArrayList<>();
    updateBlankChildrenMethod.invoke(menu, blankChildren, null);

    assertEquals(expectedFieldNames.length, blankChildren.size());
    for (int i = 0; i < expectedFieldNames.length; i++) {
      StaticFieldAccessFillIn fillIn = (StaticFieldAccessFillIn) blankChildren.get(i);
      FieldAccess transientValue = fillIn.getTransientValue(null);
      assertEquals(expectedFieldNames[i], transientValue.field.getValue().getName());
    }
  }

  private <T extends Resource> void assertSourceFillIn(
      SourceFillIn<T> fillIn,
      Class<?> expectedSourceClass,
      Class<T> expectedResourceClass,
      T resource) {
    InstanceCreation transientValue = fillIn.getTransientValue(null);
    InstanceCreation secondTransientValue = fillIn.getTransientValue(null);
    InstanceCreation createdValue = fillIn.createValue(null);

    assertSame(transientValue, secondTransientValue);
    assertNotSame(transientValue, createdValue);
    assertEquals(JavaType.getInstance(expectedSourceClass), transientValue.getType());
    assertEquals(JavaType.getInstance(expectedSourceClass), createdValue.getType());
    assertEquals(transientValue.constructor.getValue(), createdValue.constructor.getValue());

    ResourceExpression transientExpression = (ResourceExpression) transientValue.requiredArguments.getValue().getFirst().expression.getValue();
    ResourceExpression createdExpression = (ResourceExpression) createdValue.requiredArguments.getValue().getFirst().expression.getValue();

    assertEquals(JavaType.getInstance(expectedResourceClass), transientExpression.getType());
    assertEquals(JavaType.getInstance(expectedResourceClass), createdExpression.getType());
    assertSame(resource, transientExpression.resource.getValue());
    assertSame(resource, createdExpression.resource.getValue());
  }

  @Test
  public void keyMenuSingletonsRemainStable() {
    assertSame(ArrowsKeyCascadeMenu.getInstance(), ArrowsKeyCascadeMenu.getInstance());
    assertSame(DigitsKeyCascadeMenu.getInstance(), DigitsKeyCascadeMenu.getInstance());
    assertSame(LettersKeyCascadeMenu.getInstance(), LettersKeyCascadeMenu.getInstance());
  }

  @Test
  public void keyMenusExposeExpectedFieldAccessOrder() throws Exception {
    assertMenuFields(ArrowsKeyCascadeMenu.getInstance(), "LEFT", "UP", "RIGHT", "DOWN");
    assertMenuFields(DigitsKeyCascadeMenu.getInstance(),
        "DIGIT_0", "DIGIT_1", "DIGIT_2", "DIGIT_3", "DIGIT_4",
        "DIGIT_5", "DIGIT_6", "DIGIT_7", "DIGIT_8", "DIGIT_9");
    assertMenuFields(LettersKeyCascadeMenu.getInstance(),
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
  }

  @Test
  public void sourceFillInsCreateStableTransientValuesAndFreshInstances() {
    AudioResource audioResource = new AudioResource(UUID.randomUUID());
    ImageResource imageResource = new ImageResource(UUID.randomUUID());

    assertSourceFillIn(new AudioSourceFillIn(audioResource), AudioSource.class, AudioResource.class, audioResource);
    assertSourceFillIn(new ImageSourceFillIn(imageResource), ImageSource.class, ImageResource.class, imageResource);
  }
}

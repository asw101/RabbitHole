package org.alice.stageide.cascade;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.CascadeMenuModel;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaType;
import org.lgna.story.SBiped;
import org.lgna.story.SJoint;
import org.lgna.story.SThing;
import org.lgna.story.SVRHand;
import org.lgna.story.SVRHeadset;
import org.lgna.story.SVRUser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ExpressionCascadeManagerBehaviorTest {
  private static ExpressionCascadeManager manager;
  private static Method createPartMenuModelMethod;
  private static Method isApplicableForPartFillInMethod;
  private static Method appendOtherTypesMethod;

  @BeforeClass
  public static void setUpReflection() throws Exception {
    manager = new ExpressionCascadeManager();

    createPartMenuModelMethod = ExpressionCascadeManager.class.getDeclaredMethod(
        "createPartMenuModel",
        Expression.class,
        AbstractType.class,
        AbstractType.class,
        boolean.class);
    createPartMenuModelMethod.setAccessible(true);

    isApplicableForPartFillInMethod = ExpressionCascadeManager.class.getDeclaredMethod(
        "isApplicableForPartFillIn",
        AbstractType.class,
        AbstractType.class);
    isApplicableForPartFillInMethod.setAccessible(true);

    appendOtherTypesMethod = ExpressionCascadeManager.class.getDeclaredMethod("appendOtherTypes", List.class);
    appendOtherTypesMethod.setAccessible(true);
  }

  @SuppressWarnings("unchecked")
  private CascadeMenuModel<Expression> createPartMenuModel(
      AbstractType<?, ?, ?> desiredType,
      AbstractType<?, ?, ?> expressionType,
      boolean isOwnedByCascadeItemMenuCombo) throws Exception {
    return (CascadeMenuModel<Expression>) createPartMenuModelMethod.invoke(
        manager,
        null,
        desiredType,
        expressionType,
        isOwnedByCascadeItemMenuCombo);
  }

  private boolean isApplicableForPartFillIn(
      AbstractType<?, ?, ?> desiredType,
      AbstractType<?, ?, ?> expressionType) throws Exception {
    return (Boolean) isApplicableForPartFillInMethod.invoke(manager, desiredType, expressionType);
  }

  @Test
  public void createPartMenuModel_returnsJointMenuForJointedModels() throws Exception {
    CascadeMenuModel<Expression> menuModel = createPartMenuModel(
        JavaType.getInstance(SJoint.class),
        JavaType.getInstance(SBiped.class),
        false);

    assertNotNull(menuModel);
    assertEquals(JointExpressionMenuModel.class, menuModel.getClass());
  }

  @Test
  public void createPartMenuModel_returnsVrMenusForUserHandsAndHeadsets() throws Exception {
    CascadeMenuModel<Expression> handMenuModel = createPartMenuModel(
        JavaType.getInstance(SVRHand.class),
        JavaType.getInstance(SVRUser.class),
        false);
    CascadeMenuModel<Expression> headsetMenuModel = createPartMenuModel(
        JavaType.getInstance(SVRHeadset.class),
        JavaType.getInstance(SVRUser.class),
        false);

    assertNotNull(handMenuModel);
    assertNotNull(headsetMenuModel);
    assertFalse(handMenuModel.getClass().equals(JointExpressionMenuModel.class));
    assertFalse(headsetMenuModel.getClass().equals(JointExpressionMenuModel.class));
  }

  @Test
  public void createPartMenuModel_returnsNullForUnsupportedTypes() throws Exception {
    CascadeMenuModel<Expression> menuModel = createPartMenuModel(
        JavaType.getInstance(String.class),
        JavaType.getInstance(String.class),
        false);

    assertNull(menuModel);
  }

  @Test
  public void isApplicableForPartFillIn_matchesJointAndVrBranches() throws Exception {
    assertTrue(isApplicableForPartFillIn(JavaType.getInstance(SJoint.class), JavaType.getInstance(SBiped.class)));
    assertTrue(isApplicableForPartFillIn(JavaType.getInstance(SVRHand.class), JavaType.getInstance(SVRUser.class)));
    assertTrue(isApplicableForPartFillIn(JavaType.getInstance(SVRHeadset.class), JavaType.getInstance(SVRUser.class)));
    assertFalse(isApplicableForPartFillIn(JavaType.getInstance(String.class), JavaType.getInstance(String.class)));
  }

  @Test
  public void appendOtherTypes_addsStoryThingExactlyOnce() throws Exception {
    List<AbstractType<?, ?, ?>> otherTypes = new ArrayList<>();

    appendOtherTypesMethod.invoke(manager, otherTypes);

    AbstractType<?, ?, ?> sThingType = JavaType.getInstance(SThing.class);
    assertTrue(otherTypes.contains(sThingType));
    assertEquals(1, Collections.frequency(otherTypes, sThingType));
  }
}

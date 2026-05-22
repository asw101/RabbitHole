package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.story.SCameraMarker;
import org.lgna.story.SThing;
import org.lgna.story.SThingMarker;

import static org.junit.Assert.*;

public class FieldRegistryTest {
  private final FieldRegistry registry = new FieldRegistry();

  @Test
  public void createSelectionPlan_fieldAccessSelectsUserField() {
    UserField field = new UserField("actor", JavaType.getInstance(SThing.class));

    FieldRegistry.SelectionPlan plan = registry.createSelectionPlan(new FieldAccess(field), null);

    assertTrue(plan.isFieldSelection());
    assertSame(field, plan.getField());
    assertFalse(plan.isExpressionSelection());
  }

  @Test
  public void createSelectionPlan_methodInvocationSelectsExpression() {
    MethodInvocation methodInvocation = new MethodInvocation();

    FieldRegistry.SelectionPlan plan = registry.createSelectionPlan(methodInvocation, null);

    assertTrue(plan.isExpressionSelection());
    assertSame(methodInvocation, plan.getExpression());
  }

  @Test
  public void createSelectionPlan_arrayAccessSelectsExpression() {
    ArrayAccess arrayAccess = new ArrayAccess(JavaType.getInstance(String[].class), new NullLiteral(), new NullLiteral());

    FieldRegistry.SelectionPlan plan = registry.createSelectionPlan(arrayAccess, null);

    assertTrue(plan.isExpressionSelection());
    assertSame(arrayAccess, plan.getExpression());
  }

  @Test
  public void createSelectionPlan_thisExpressionFallsBackToActiveSceneField() {
    UserField activeSceneField = new UserField("scene", JavaType.getInstance(SThing.class));

    FieldRegistry.SelectionPlan plan = registry.createSelectionPlan(new ThisExpression(), activeSceneField);

    assertTrue(plan.isFieldSelection());
    assertSame(activeSceneField, plan.getField());
  }

  @Test
  public void createSelectionPlan_thisExpressionWithoutActiveSceneFieldReturnsNone() {
    FieldRegistry.SelectionPlan plan = registry.createSelectionPlan(new ThisExpression(), null);

    assertFalse(plan.isFieldSelection());
    assertFalse(plan.isExpressionSelection());
  }

  @Test
  public void classifyFieldRecognizesMarkerKinds() {
    UserField cameraMarkerField = new UserField("cameraMarker", JavaType.getInstance(SCameraMarker.class));
    UserField objectMarkerField = new UserField("objectMarker", JavaType.getInstance(SThingMarker.class));
    UserField regularField = new UserField("regular", JavaType.STRING_TYPE);

    assertSame(FieldRegistry.MarkerKind.CAMERA, registry.classifyField(cameraMarkerField));
    assertSame(FieldRegistry.MarkerKind.OBJECT, registry.classifyField(objectMarkerField));
    assertSame(FieldRegistry.MarkerKind.REGULAR, registry.classifyField(regularField));
    assertSame(FieldRegistry.MarkerKind.NONE, registry.classifyField(null));
  }
}

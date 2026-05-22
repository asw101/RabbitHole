package org.alice.ide.croquet.models;

import org.alice.ide.croquet.components.ExpressionDropDown;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.DefaultCustomItemState;
import org.lgna.croquet.Group;
import org.lgna.croquet.imp.cascade.BlankNode;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class ExpressionStateComprehensiveTest {

  @Test
  public void expressionState_isPublicAbstractClass() {
    assertTrue(Modifier.isPublic(ExpressionState.class.getModifiers()));
    assertTrue(Modifier.isAbstract(ExpressionState.class.getModifiers()));
  }

  @Test
  public void expressionState_extendsDefaultCustomItemState() {
    assertTrue(DefaultCustomItemState.class.isAssignableFrom(ExpressionState.class));
  }

  @Test
  public void expressionState_hasSinglePublicConstructor() {
    Constructor<?>[] constructors = ExpressionState.class.getConstructors();
    assertEquals(1, constructors.length);
    assertTrue(Modifier.isPublic(constructors[0].getModifiers()));
  }

  @Test
  public void expressionState_constructorParametersMatchSource() throws Exception {
    Constructor<ExpressionState> constructor = ExpressionState.class.getConstructor(Group.class, UUID.class, Expression.class);
    assertArrayEquals(new Class<?>[] {Group.class, UUID.class, Expression.class}, constructor.getParameterTypes());
  }

  @Test
  public void expressionState_declaresNoFields() {
    assertEquals(0, ExpressionState.class.getDeclaredFields().length);
  }

  @Test
  public void expressionState_declaresExactlyTwoMethods() {
    assertEquals(2, ExpressionState.class.getDeclaredMethods().length);
  }

  @Test
  public void createEditor_signatureMatchesSource() throws Exception {
    Method method = ExpressionState.class.getMethod("createEditor", org.alice.ide.x.AstI18nFactory.class);
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void createView_signatureMatchesSource() throws Exception {
    Method method = ExpressionState.class.getMethod("createView", org.alice.ide.x.AstI18nFactory.class);
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  @Test
  public void testSubclass_canBeInstantiated() {
    TestExpressionState state = new TestExpressionState(new NullLiteral());
    assertNotNull(state);
  }

  @Test
  public void testSubclass_preservesInitialExpressionValue() {
    StringLiteral literal = new StringLiteral("sample");
    TestExpressionState state = new TestExpressionState(literal);
    assertSame(literal, state.getValue());
  }

  @Test
  public void testSubclass_acceptsNullInitialValue() {
    TestExpressionState state = new TestExpressionState(null);
    assertNull(state.getValue());
  }

  @Test
  public void createEditor_returnsExpressionDropDownEvenWithNullFactory() {
    TestExpressionState state = new TestExpressionState(new NullLiteral());
    assertTrue(state.createEditor(null) instanceof ExpressionDropDown);
  }

  @Test
  public void createEditor_returnsFreshViewEachCall() {
    TestExpressionState state = new TestExpressionState(new NullLiteral());
    assertNotSame(state.createEditor(null), state.createEditor(null));
  }

  @Test
  public void migrationId_isPreservedFromConstructor() {
    UUID id = UUID.randomUUID();
    TestExpressionState state = new TestExpressionState(id, new NullLiteral());
    assertEquals(id, state.getMigrationId());
  }

  @Test
  public void stateUsesInheritGroupByDefaultInTestSubclass() {
    TestExpressionState state = new TestExpressionState(new NullLiteral());
    assertSame(Application.INHERIT_GROUP, state.getGroup());
  }

  @Test
  public void stateStartsEnabled() {
    TestExpressionState state = new TestExpressionState(new NullLiteral());
    assertTrue(state.isEnabled());
  }

  @Test
  public void stateCanBeDisabledAndReenabled() {
    TestExpressionState state = new TestExpressionState(new NullLiteral());
    state.setEnabled(false);
    assertFalse(state.isEnabled());
    state.setEnabled(true);
    assertTrue(state.isEnabled());
  }

  @Test
  public void expressionState_isTopLevelType() {
    assertNull(ExpressionState.class.getEnclosingClass());
  }

  @Test
  public void expressionState_isNotFinalOrEnum() {
    assertFalse(Modifier.isFinal(ExpressionState.class.getModifiers()));
    assertFalse(ExpressionState.class.isEnum());
  }

  @Test
  public void expressionState_packageNameMatchesSource() {
    assertEquals("org.alice.ide.croquet.models", ExpressionState.class.getPackage().getName());
  }

  @Test
  public void expressionState_simpleNameMatchesSource() {
    assertEquals("ExpressionState", ExpressionState.class.getSimpleName());
  }

  @Test
  public void updateBlankChildren_overrideInTestSubclassDoesNotAffectStoredValue() {
    StringLiteral literal = new StringLiteral("unchanged");
    TestExpressionState state = new TestExpressionState(literal);
    state.updateBlankChildren(List.of(), null);
    assertSame(literal, state.getValue());
  }

  private static final class TestExpressionState extends ExpressionState {
    private TestExpressionState(Expression initialValue) {
      this(UUID.randomUUID(), initialValue);
    }

    private TestExpressionState(UUID id, Expression initialValue) {
      super(Application.INHERIT_GROUP, id, initialValue);
    }

    @Override
    protected void updateBlankChildren(List<CascadeBlankChild> blankChildren, BlankNode<Expression> blankNode) {
    }
  }

  @Test
  public void createEditor_returnsSwingComponentViewSubtype() {
    TestExpressionState state = new TestExpressionState(new NullLiteral());
    assertTrue(org.lgna.croquet.views.SwingComponentView.class.isInstance(state.createEditor(null)));
  }

  @Test
  public void createView_reflectionReturnTypeIsSwingComponentView() throws Exception {
    Method method = ExpressionState.class.getMethod("createView", org.alice.ide.x.AstI18nFactory.class);
    assertEquals(org.lgna.croquet.views.SwingComponentView.class, method.getReturnType());
  }

  @Test
  public void createEditor_reflectionReturnTypeIsSwingComponentView() throws Exception {
    Method method = ExpressionState.class.getMethod("createEditor", org.alice.ide.x.AstI18nFactory.class);
    assertEquals(org.lgna.croquet.views.SwingComponentView.class, method.getReturnType());
  }

  @Test
  public void differentTestStatesRemainDistinctInstances() {
    assertNotSame(new TestExpressionState(new NullLiteral()), new TestExpressionState(new NullLiteral()));
  }


  @Test
  public void expressionState_declaresNoTypeParameters() {
    assertEquals(0, ExpressionState.class.getTypeParameters().length);
  }

}

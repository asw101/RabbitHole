package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class ParameterEditTest {
  private static class TestParameterEdit extends ParameterEdit {
    private int doCallCount;
    private int undoCallCount;
    private Boolean lastIsDo;

    TestParameterEdit(UserActivity userActivity, UserCode code, UserParameter parameter) {
      super(userActivity, code, parameter);
    }

    @Override
    protected void doOrRedoInternal(boolean isDo) {
      this.doCallCount++;
      this.lastIsDo = isDo;
    }

    @Override
    protected void undoInternal() {
      this.undoCallCount++;
    }

    @Override
    protected void appendDescription(StringBuilder rv, DescriptionStyle descriptionStyle) {
      rv.append("TestParameterEdit");
    }

    NodeListProperty<UserParameter> exposeParametersProperty() {
      return this.getParametersProperty();
    }

    int getDoCallCount() {
      return this.doCallCount;
    }

    int getUndoCallCount() {
      return this.undoCallCount;
    }

    Boolean getLastIsDo() {
      return this.lastIsDo;
    }
  }

  private static UserMethod createMethod(UserParameter... parameters) {
    return new UserMethod("sample", Object.class, parameters, new BlockStatement());
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), new UserParameter("value", String.class));
    assertNotNull(edit);
  }

  @Test
  public void getCode_returnsConstructionValue() {
    UserMethod method = createMethod();
    TestParameterEdit edit = new TestParameterEdit(null, method, new UserParameter("value", String.class));

    assertSame(method, edit.getCode());
  }

  @Test
  public void getParameter_returnsConstructionValue() {
    UserParameter parameter = new UserParameter("value", String.class);
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), parameter);

    assertSame(parameter, edit.getParameter());
  }

  @Test
  public void construct_withNullCode_preservesNull() {
    TestParameterEdit edit = new TestParameterEdit(null, null, new UserParameter("value", String.class));

    assertNull(edit.getCode());
  }

  @Test
  public void construct_withNullParameter_preservesNull() {
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), null);

    assertNull(edit.getParameter());
  }

  @Test
  public void getParametersProperty_returnsCodesRequiredParametersProperty() {
    UserMethod method = createMethod(new UserParameter("first", Integer.class));
    TestParameterEdit edit = new TestParameterEdit(null, method, new UserParameter("second", Double.class));

    assertSame(method.getRequiredParamtersProperty(), edit.exposeParametersProperty());
  }

  @Test
  public void getParametersProperty_reflectsExistingParameters() {
    UserParameter parameter = new UserParameter("first", Integer.class);
    UserMethod method = createMethod(parameter);
    TestParameterEdit edit = new TestParameterEdit(null, method, new UserParameter("second", Double.class));

    assertEquals(1, edit.exposeParametersProperty().size());
    assertSame(parameter, edit.exposeParametersProperty().get(0));
  }

  @Test
  public void construct_extendsAbstractEdit() {
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), new UserParameter("value", String.class));

    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void canUndo_always_returnsTrue() {
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), new UserParameter("value", String.class));

    assertTrue(edit.canUndo());
  }

  @Test
  public void canRedo_always_returnsTrue() {
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), new UserParameter("value", String.class));

    assertTrue(edit.canRedo());
  }

  @Test
  public void doOrRedoInternal_whenDo_recordsTrueFlag() {
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), new UserParameter("value", String.class));

    edit.doOrRedoInternal(true);

    assertEquals(1, edit.getDoCallCount());
    assertEquals(Boolean.TRUE, edit.getLastIsDo());
  }

  @Test
  public void doOrRedoInternal_whenRedo_recordsFalseFlag() {
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), new UserParameter("value", String.class));

    edit.doOrRedoInternal(false);

    assertEquals(1, edit.getDoCallCount());
    assertEquals(Boolean.FALSE, edit.getLastIsDo());
  }

  @Test
  public void undoInternal_afterDo_incrementsUndoCount() {
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), new UserParameter("value", String.class));

    edit.doOrRedoInternal(true);
    edit.undoInternal();

    assertEquals(1, edit.getDoCallCount());
    assertEquals(1, edit.getUndoCallCount());
  }

  @Test
  public void appendDescription_viaTerseDescription_returnsSubclassText() {
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), new UserParameter("value", String.class));

    assertEquals("TestParameterEdit", edit.getTerseDescription());
  }

  @Test
  public void getDetailedDescription_containsClassNameAndDescription() {
    TestParameterEdit edit = new TestParameterEdit(null, createMethod(), new UserParameter("value", String.class));
    String description = edit.getDetailedDescription();

    assertTrue(description.contains(TestParameterEdit.class.getName()));
    assertTrue(description.contains("TestParameterEdit"));
  }
}

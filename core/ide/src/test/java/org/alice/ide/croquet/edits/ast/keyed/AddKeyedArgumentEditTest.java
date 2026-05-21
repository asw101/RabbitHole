package org.alice.ide.croquet.edits.ast.keyed;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class AddKeyedArgumentEditTest {

  private static final class TestableAddKeyedArgumentEdit extends AddKeyedArgumentEdit {
    private TestableAddKeyedArgumentEdit(UserActivity userActivity, ArgumentOwner argumentOwner, JavaKeyedArgument keyedArgument) {
      super(userActivity, argumentOwner, keyedArgument);
    }

    private void performDoOrRedo(boolean isDo) {
      this.doOrRedoInternal(isDo);
    }

    private void performUndo() {
      this.undoInternal();
    }

    private String describeForTest() {
      StringBuilder sb = new StringBuilder();
      this.appendDescription(sb, DescriptionStyle.TERSE);
      return sb.toString();
    }
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    TestableAddKeyedArgumentEdit edit = new TestableAddKeyedArgumentEdit(null, createArgumentOwner(), createKeyedArgument("detail"));
    assertNotNull(edit);
  }

  @Test
  public void getArgumentOwner_withMethodInvocation_returnsConstructionValue() {
    ArgumentOwner argumentOwner = createArgumentOwner();
    TestableAddKeyedArgumentEdit edit = new TestableAddKeyedArgumentEdit(null, argumentOwner, createKeyedArgument("detail"));
    assertSame(argumentOwner, edit.getArgumentOwner());
  }

  @Test
  public void getKeyedArgument_withJavaKeyedArgument_returnsConstructionValue() {
    JavaKeyedArgument keyedArgument = createKeyedArgument("detail");
    TestableAddKeyedArgumentEdit edit = new TestableAddKeyedArgumentEdit(null, createArgumentOwner(), keyedArgument);
    assertSame(keyedArgument, edit.getKeyedArgument());
  }

  @Test
  public void doOrRedoInternal_onEmptyList_addsKeyedArgument() {
    MethodInvocation argumentOwner = createArgumentOwner();
    JavaKeyedArgument keyedArgument = createKeyedArgument("new detail");
    TestableAddKeyedArgumentEdit edit = new TestableAddKeyedArgumentEdit(null, argumentOwner, keyedArgument);

    edit.performDoOrRedo(true);

    assertEquals(1, argumentOwner.getKeyedArgumentsProperty().size());
    assertSame(keyedArgument, argumentOwner.getKeyedArgumentsProperty().get(0));
  }

  @Test
  public void doOrRedoInternal_withExistingKeyedArgument_appendsNewArgument() {
    MethodInvocation argumentOwner = createArgumentOwner();
    JavaKeyedArgument existing = createKeyedArgument("existing");
    JavaKeyedArgument added = createKeyedArgument("added");
    argumentOwner.getKeyedArgumentsProperty().add(existing);

    TestableAddKeyedArgumentEdit edit = new TestableAddKeyedArgumentEdit(null, argumentOwner, added);
    edit.performDoOrRedo(true);

    assertEquals(2, argumentOwner.getKeyedArgumentsProperty().size());
    assertSame(existing, argumentOwner.getKeyedArgumentsProperty().get(0));
    assertSame(added, argumentOwner.getKeyedArgumentsProperty().get(1));
  }

  @Test
  public void undoInternal_afterDo_removesAddedKeyedArgument() {
    MethodInvocation argumentOwner = createArgumentOwner();
    JavaKeyedArgument keyedArgument = createKeyedArgument("detail");
    TestableAddKeyedArgumentEdit edit = new TestableAddKeyedArgumentEdit(null, argumentOwner, keyedArgument);

    edit.performDoOrRedo(true);
    assertTrue(argumentOwner.getKeyedArgumentsProperty().contains(keyedArgument));

    edit.performUndo();

    assertFalse(argumentOwner.getKeyedArgumentsProperty().contains(keyedArgument));
    assertEquals(0, argumentOwner.getKeyedArgumentsProperty().size());
  }

  @Test
  public void doUndoRedo_roundTrip_reappliesKeyedArgument() {
    MethodInvocation argumentOwner = createArgumentOwner();
    JavaKeyedArgument keyedArgument = createKeyedArgument("again");
    TestableAddKeyedArgumentEdit edit = new TestableAddKeyedArgumentEdit(null, argumentOwner, keyedArgument);

    edit.performDoOrRedo(true);
    edit.performUndo();
    edit.performDoOrRedo(false);

    assertEquals(1, argumentOwner.getKeyedArgumentsProperty().size());
    assertSame(keyedArgument, argumentOwner.getKeyedArgumentsProperty().get(0));
  }

  @Test
  public void appendDescription_withArgument_containsPrefix() {
    TestableAddKeyedArgumentEdit edit = new TestableAddKeyedArgumentEdit(null, createArgumentOwner(), createKeyedArgument("spoken"));
    String description = edit.describeForTest();
    assertTrue(description.startsWith("add detail "));
    assertTrue(description.contains("spoken"));
  }

  @Test
  public void extendsAbstractEdit_afterConstruction_returnsTrue() {
    TestableAddKeyedArgumentEdit edit = new TestableAddKeyedArgumentEdit(null, createArgumentOwner(), createKeyedArgument("detail"));
    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void multipleEdits_withDifferentOwners_modifyIndependentLists() {
    MethodInvocation firstOwner = createArgumentOwner();
    MethodInvocation secondOwner = createArgumentOwner();
    JavaKeyedArgument firstArgument = createKeyedArgument("first");
    JavaKeyedArgument secondArgument = createKeyedArgument("second");

    new TestableAddKeyedArgumentEdit(null, firstOwner, firstArgument).performDoOrRedo(true);
    new TestableAddKeyedArgumentEdit(null, secondOwner, secondArgument).performDoOrRedo(true);

    assertEquals(1, firstOwner.getKeyedArgumentsProperty().size());
    assertSame(firstArgument, firstOwner.getKeyedArgumentsProperty().get(0));
    assertEquals(1, secondOwner.getKeyedArgumentsProperty().size());
    assertSame(secondArgument, secondOwner.getKeyedArgumentsProperty().get(0));
  }

  private MethodInvocation createArgumentOwner() {
    return new MethodInvocation(new StringLiteral("hello"), JavaMethod.getInstance(String.class, "trim"));
  }

  private JavaKeyedArgument createKeyedArgument(String text) {
    JavaKeyedArgument keyedArgument = new JavaKeyedArgument();
    keyedArgument.expression.setValue(new StringLiteral(text));
    return keyedArgument;
  }
}

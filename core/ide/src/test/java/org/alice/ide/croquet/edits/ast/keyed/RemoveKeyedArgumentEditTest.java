package org.alice.ide.croquet.edits.ast.keyed;

import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.alice.ide.croquet.models.ast.keyed.RemoveKeyedArgumentOperation;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaKeyedArgument;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class RemoveKeyedArgumentEditTest {
  private MethodInvocation owner;
  private JavaKeyedArgument first;
  private JavaKeyedArgument target;
  private UserActivity activity;

  private static JavaKeyedArgument createKeyedArgument(int value) {
    JavaMethod parameterOwner = JavaMethod.getInstance(String.class, "substring", int.class);
    JavaMethod keyMethod = JavaMethod.getInstance(String.class, "valueOf", int.class);
    return new JavaKeyedArgument(parameterOwner.getRequiredParameters().get(0), keyMethod, new IntegerLiteral(value));
  }

  @Before
  public void setUp() {
    owner = new MethodInvocation();
    first = createKeyedArgument(1);
    target = createKeyedArgument(2);
    owner.getKeyedArgumentsProperty().add(first);
    owner.getKeyedArgumentsProperty().add(target);
    activity = new UserActivity();
    activity.setCompletionModel(RemoveKeyedArgumentOperation.getInstance(owner.getKeyedArgumentsProperty(), target));
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(null);
    assertNotNull(edit);
  }

  @Test
  public void extendsAbstractEdit() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void doOrRedoInternal_removesTargetArgument() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    edit.doOrRedoInternal(true);
    assertFalse(owner.getKeyedArgumentsProperty().contains(target));
  }

  @Test
  public void doOrRedoInternal_preservesOtherArguments() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    edit.doOrRedoInternal(true);
    assertSame(first, owner.getKeyedArgumentsProperty().get(0));
  }

  @Test
  public void undoInternal_readdsTargetArgument() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertTrue(owner.getKeyedArgumentsProperty().contains(target));
  }

  @Test
  public void redoAfterUndo_removesTargetAgain() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    edit.doOrRedoInternal(false);
    assertFalse(owner.getKeyedArgumentsProperty().contains(target));
  }

  @Test
  public void undoInternal_appendsTargetAtEnd() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertSame(target, owner.getKeyedArgumentsProperty().get(owner.getKeyedArgumentsProperty().size() - 1));
  }

  @Test
  public void roundTrip_restoresArgumentCount() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertEquals(2, owner.getKeyedArgumentsProperty().size());
  }

  @Test
  public void getTerseDescription_containsAddPrefixFromSource() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    assertTrue(edit.getTerseDescription().startsWith("add:"));
  }

  @Test
  public void getDetailedDescription_containsClassName() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    assertTrue(edit.getDetailedDescription().contains(RemoveKeyedArgumentEdit.class.getName()));
  }

  @Test
  public void getRedoPresentation_containsRedoPrefix() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void getUndoPresentation_containsUndoPrefix() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  @Test
  public void encodeMethodExistsViaInheritance() throws Exception {
    Method method = RemoveKeyedArgumentEdit.class.getMethod("encode", BinaryEncoder.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void userActivityConstructorSignature_matchesSource() throws Exception {
    Constructor<RemoveKeyedArgumentEdit> constructor = RemoveKeyedArgumentEdit.class.getConstructor(UserActivity.class);
    assertNotNull(constructor);
  }

  @Test
  public void binaryDecoderConstructor_exists() throws Exception {
    Constructor<RemoveKeyedArgumentEdit> constructor = RemoveKeyedArgumentEdit.class.getConstructor(
        edu.cmu.cs.dennisc.codec.BinaryDecoder.class,
        Object.class);
    assertNotNull(constructor);
  }

  @Test
  public void directSuperclassIsAbstractEdit() {
    assertSame(AbstractEdit.class, RemoveKeyedArgumentEdit.class.getSuperclass());
  }

  @Test
  public void modelTargetsRequestedArgument() {
    RemoveKeyedArgumentEdit edit = new RemoveKeyedArgumentEdit(activity);
    assertSame(target, edit.getModel().getArgument());
  }
}

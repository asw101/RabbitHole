package org.alice.ide.ast.code;

import org.alice.ide.ast.code.edits.EnvelopStatementsEdit;
import org.alice.ide.ast.code.edits.MoveStatementEdit;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.ast.draganddrop.statement.DoInOrderTemplateDragModel;
import org.alice.ide.ast.draganddrop.statement.StatementDragModel;
import org.alice.ide.project.ProjectChangeOfInterestManager;
import org.alice.ide.project.events.ProjectChangeOfInterestListener;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class CodeSmallGapBehaviorTest {
  private static Comment comment(String text) {
    return new Comment(text);
  }

  private static BlockStatementIndexPair pair(BlockStatement block, int index) {
    return new BlockStatementIndexPair(block, index);
  }

  private static List<String> texts(BlockStatement block) {
    List<String> values = new ArrayList<>();
    for (int i = 0; i < block.statements.size(); i++) {
      values.add(((Comment) block.statements.get(i)).text.getValue());
    }
    return values;
  }

  private static List<String> parameterNames(UserMethod method) {
    List<String> values = new ArrayList<>();
    for (int i = 0; i < method.requiredParameters.size(); i++) {
      values.add(method.requiredParameters.get(i).getName());
    }
    return values;
  }

  private static UserMethod methodWithParameters(String... names) {
    UserMethod method = new UserMethod();
    method.name.setValue("swapTarget");
    for (String name : names) {
      UserParameter parameter = new UserParameter();
      parameter.name.setValue(name);
      method.requiredParameters.add(parameter);
    }
    return method;
  }

  @Test
  public void shiftMoveCount_returnsTailSizeWhenMovingBackwardWithinSameBlock() {
    BlockStatement block = new BlockStatement();
    block.statements.add(comment("a"));
    block.statements.add(comment("b"));
    block.statements.add(comment("c"));
    block.statements.add(comment("d"));

    assertEquals(2, ShiftDragStatementUtilities.calculateShiftMoveCount(pair(block, 2), pair(block, 1)));
  }

  @Test
  public void shiftMoveCount_returnsZeroWhenMovingForwardWithinSameBlock() {
    BlockStatement block = new BlockStatement();
    block.statements.add(comment("a"));
    block.statements.add(comment("b"));
    block.statements.add(comment("c"));

    assertEquals(0, ShiftDragStatementUtilities.calculateShiftMoveCount(pair(block, 0), pair(block, 2)));
  }

  @Test
  public void shiftMoveCount_stopsBeforeEnteringAncestorStatementBody() {
    BlockStatement root = new BlockStatement();
    root.statements.add(comment("before"));
    DoInOrder container = new DoInOrder();
    container.body.setValue(new BlockStatement());
    container.body.getValue().statements.add(comment("inner"));
    root.statements.add(container);
    root.statements.add(comment("after"));

    assertEquals(1, ShiftDragStatementUtilities.calculateShiftMoveCount(pair(root, 0), pair(container.body.getValue(), 0)));
    assertEquals(0, ShiftDragStatementUtilities.calculateShiftMoveCount(pair(root, 1), pair(container.body.getValue(), 0)));
  }

  @Test
  public void candidateForEnvelop_distinguishesTemplateAndConcreteStatementDragModels() {
    assertTrue(ShiftDragStatementUtilities.isCandidateForEnvelop(DoInOrderTemplateDragModel.getInstance()));
    assertFalse(ShiftDragStatementUtilities.isCandidateForEnvelop(StatementDragModel.getInstance(new DoInOrder())));
  }

  @Test
  public void moveStatementEdit_movesAndUndoesAcrossBlocks() {
    BlockStatement fromBlock = new BlockStatement();
    fromBlock.statements.add(comment("a"));
    fromBlock.statements.add(comment("b"));
    BlockStatement toBlock = new BlockStatement();
    toBlock.statements.add(comment("x"));

    MoveStatementEdit edit = new MoveStatementEdit(new UserActivity(), pair(fromBlock, 1), fromBlock.statements.get(1), pair(toBlock, 1), false);

    edit.doOrRedoInternal(true);
    assertEquals(List.of("a"), texts(fromBlock));
    assertEquals(List.of("x", "b"), texts(toBlock));

    edit.undoInternal();
    assertEquals(List.of("a", "b"), texts(fromBlock));
    assertEquals(List.of("x"), texts(toBlock));
  }

  @Test
  public void moveStatementEdit_movesTrailingStatementsAsOneSliceWithinSameBlock() {
    BlockStatement block = new BlockStatement();
    block.statements.add(comment("a"));
    block.statements.add(comment("b"));
    block.statements.add(comment("c"));
    block.statements.add(comment("d"));

    MoveStatementEdit edit = new MoveStatementEdit(new UserActivity(), pair(block, 2), block.statements.get(2), pair(block, 1), true);

    edit.doOrRedoInternal(true);
    assertEquals(List.of("a", "c", "d", "b"), texts(block));

    edit.undoInternal();
    assertEquals(List.of("a", "b", "c", "d"), texts(block));
  }

  @Test
  public void envelopStatementsEdit_notifiesProjectChangeListenersOnDoAndUndo() {
    BlockStatement block = new BlockStatement();
    block.statements.add(comment("wrapped"));
    EnvelopStatementsEdit edit = new EnvelopStatementsEdit(new UserActivity(), pair(block, 0), pair(block, 0));
    AtomicInteger notifications = new AtomicInteger();
    ProjectChangeOfInterestListener listener = notifications::incrementAndGet;
    ProjectChangeOfInterestManager.SINGLETON.addProjectChangeOfInterestListener(listener);
    try {
      edit.doOrRedoInternal(true);
      edit.undoInternal();
    } finally {
      ProjectChangeOfInterestManager.SINGLETON.removeProjectChangeOfInterestListener(listener);
    }

    assertEquals(2, notifications.get());
  }

  @Test
  public void envelopStatementsOperation_exposesCachedEndpoints() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair from = pair(block, 0);
    BlockStatementIndexPair to = pair(block, 1);

    EnvelopStatementsOperation operation = EnvelopStatementsOperation.getInstance(from, to);

    assertSame(from, operation.getFromLocation());
    assertSame(to, operation.getToLocation());
    assertSame(operation, EnvelopStatementsOperation.getInstance(from, to));
  }

  @Test
  public void shiftParameterOperations_reportBoundaryAppropriatenessAndSwapIndices() {
    UserMethod method = methodWithParameters("first", "middle", "last");

    ForwardShiftParameterOperation forwardMiddle = new ForwardShiftParameterOperation(method.requiredParameters, method.requiredParameters.get(1));
    ForwardShiftParameterOperation forwardFirst = new ForwardShiftParameterOperation(method.requiredParameters, method.requiredParameters.get(0));
    BackwardShiftParameterOperation backwardMiddle = new BackwardShiftParameterOperation(method.requiredParameters, method.requiredParameters.get(1));
    BackwardShiftParameterOperation backwardLast = new BackwardShiftParameterOperation(method.requiredParameters, method.requiredParameters.get(2));

    assertTrue(forwardMiddle.isIndexAppropriate());
    assertEquals(0, forwardMiddle.getIndexA());
    assertFalse(forwardFirst.isIndexAppropriate());

    assertTrue(backwardMiddle.isIndexAppropriate());
    assertEquals(1, backwardMiddle.getIndexA());
    assertFalse(backwardLast.isIndexAppropriate());
    assertEquals(Arrays.asList("first", "middle", "last"), parameterNames(method));
  }
}

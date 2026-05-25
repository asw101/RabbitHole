package org.alice.ide.ast.draganddrop;

import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.ide.ProjectStack;
import org.alice.ide.ast.draganddrop.expression.AbstractExpressionDragModel;
import org.alice.ide.ast.draganddrop.statement.StatementDragModel;
import org.alice.ide.ast.draganddrop.statement.StatementTemplateDragModel;
import org.junit.Test;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.project.Project;
import org.lgna.project.ast.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.junit.Assert.*;

public class DragAndDropDataTransferTest {
  @FunctionalInterface
  private interface BinaryEncodable {
    void encode(BinaryEncoder encoder);
  }

  private static Project createProject(BlockStatement blockStatement) {
    UserMethod method = new UserMethod("myFirstMethod", JavaType.VOID_TYPE, new UserParameter[0], blockStatement);
    NamedUserType programType = new NamedUserType(
        "Program",
        null,
        Object.class,
        new NamedUserConstructor[0],
        new UserMethod[] {method},
        new UserField[0]);
    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }

  private static InputStreamBinaryDecoder decoderFor(BinaryEncodable encodable) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(outputStream);
    encodable.encode(encoder);
    encoder.flush();
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(outputStream.toByteArray()));
  }

  @Test
  public void createInstanceFromChildStatementUsesParentBlockAndIndex() {
    ExpressionStatement first = new ExpressionStatement(new BooleanLiteral(true));
    Comment second = new Comment("done");
    BlockStatement block = new BlockStatement(first, second);

    BlockStatementIndexPair pair = BlockStatementIndexPair.createInstanceFromChildStatement(second);

    assertSame(block, pair.getBlockStatement());
    assertEquals(1, pair.getIndex());
  }

  @Test
  public void blockStatementIndexPairRoundTripsThroughBinaryEncoding() {
    ExpressionStatement statement = new ExpressionStatement(new BooleanLiteral(true));
    BlockStatement block = new BlockStatement(statement);
    Project project = createProject(block);
    BlockStatementIndexPair original = new BlockStatementIndexPair(block, 0);

    ProjectStack.pushProject(project);
    try {
      BlockStatementIndexPair decoded = new BlockStatementIndexPair(decoderFor(original::encode));
      assertEquals(original, decoded);
      assertEquals(original.hashCode(), decoded.hashCode());
      assertTrue(decoded.toString().contains("index=0"));
    } finally {
      ProjectStack.popAndCheckProject(project);
    }
  }

  @Test
  public void expressionPropertyDropSiteRoundTripsThroughBinaryEncoding() {
    ExpressionStatement statement = new ExpressionStatement(new BooleanLiteral(true));
    Project project = createProject(new BlockStatement(statement));
    ExpressionPropertyDropSite original = new ExpressionPropertyDropSite(statement.expression);

    ProjectStack.pushProject(project);
    try {
      ExpressionPropertyDropSite decoded = new ExpressionPropertyDropSite(decoderFor(original::encode));
      assertEquals(original, decoded);
      assertEquals(original.hashCode(), decoded.hashCode());
      assertSame(statement.expression, decoded.getExpressionProperty());
      assertTrue(decoded.toString().contains("expressionProperty="));
    } finally {
      ProjectStack.popAndCheckProject(project);
    }
  }

  @Test
  public void statementDragModelCachesInstancesAndRejectsConstructorInvocations() {
    ExpressionStatement statement = new ExpressionStatement(new BooleanLiteral(true));

    assertSame(StatementDragModel.getInstance(statement), StatementDragModel.getInstance(statement));
    assertNull(StatementDragModel.getInstance(new SuperConstructorInvocationStatement()));
  }

  @Test
  public void abstractExpressionDragModelRoutesExpressionDropSites() {
    RecordingExpressionDragModel dragModel = new RecordingExpressionDragModel();
    ExpressionStatement statement = new ExpressionStatement(new BooleanLiteral(true));
    ExpressionPropertyDropSite dropSite = new ExpressionPropertyDropSite(statement.expression);

    Triggerable operation = dragModel.getDropOperation(null, dropSite);

    assertSame(dragModel.operation, operation);
    assertSame(statement.expression, dragModel.capturedProperty);
  }

  @Test(expected = AssertionError.class)
  public void abstractExpressionDragModelRejectsStatementDropSites() {
    RecordingExpressionDragModel dragModel = new RecordingExpressionDragModel();
    dragModel.getDropOperation(null, new BlockStatementIndexPair(new BlockStatement(new Comment("todo")), 0));
  }

  @Test
  public void statementTemplateDragModelRoutesBlockDropSites() {
    RecordingStatementTemplateDragModel dragModel = new RecordingStatementTemplateDragModel();
    BlockStatementIndexPair dropSite = new BlockStatementIndexPair(new BlockStatement(new Comment("todo")), 0);

    Triggerable operation = dragModel.getDropOperation(null, dropSite);

    assertSame(dragModel.operation, operation);
    assertSame(dropSite, dragModel.capturedDropSite);
    assertEquals(Comment.class, dragModel.getStatementCls());
    assertTrue(dragModel.getPossiblyIncompleteStatement() instanceof Comment);
  }

  private static final class RecordingExpressionDragModel extends AbstractExpressionDragModel {
    private final Triggerable operation = activity -> {};
    private ExpressionProperty capturedProperty;

    private RecordingExpressionDragModel() {
      super(UUID.fromString("0be29091-d3c1-4c8d-b336-6111c3a8a1ee"));
    }

    @Override
    public AbstractType<?, ?, ?> getType() {
      return JavaType.getInstance(String.class);
    }

    @Override
    public boolean isPotentialStatementCreator() {
      return false;
    }

    @Override
    protected Triggerable getDropOperation(ExpressionProperty expressionProperty) {
      this.capturedProperty = expressionProperty;
      return this.operation;
    }
  }

  private static final class RecordingStatementTemplateDragModel extends StatementTemplateDragModel {
    private final Triggerable operation = activity -> {};
    private BlockStatementIndexPair capturedDropSite;

    private RecordingStatementTemplateDragModel() {
      super(
          UUID.fromString("a382e34c-6378-4b17-b676-f4872ef4b34d"),
          Comment.class,
          new Comment("template"));
    }

    @Override
    protected Triggerable getDropOperation(DragStep step, BlockStatementIndexPair dropSite) {
      this.capturedDropSite = dropSite;
      return this.operation;
    }
  }
}

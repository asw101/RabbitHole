package org.alice.ide.controlflow;

import org.alice.ide.ast.draganddrop.statement.*;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ControlFlowCompositeStructureTest {
  private static UserMethod createVoidMethod(String name) {
    return new UserMethod(name, void.class, new UserParameter[0], new BlockStatement());
  }

  private static UserMethod createNonVoidMethod(String name) {
    return new UserMethod(name, String.class, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void getInstanceCachesCompositesPerCode() {
    UserMethod method = createVoidMethod("demo");

    assertSame(ControlFlowComposite.getInstance(method), ControlFlowComposite.getInstance(method));
  }

  @Test
  public void initializePopulatesVoidMethodModelsInInsertionOrder() {
    UserMethod method = createVoidMethod("voidMethod");
    ControlFlowComposite composite = ControlFlowComposite.getInstance(method);

    composite.initialize();

    List<StatementTemplateDragModel> models = composite.getModels();
    assertEquals(15, models.size());
    assertEquals(Arrays.asList(
        DoInOrderTemplateDragModel.getInstance(),
        null,
        CountLoopTemplateDragModel.getInstance(),
        WhileLoopTemplateDragModel.getInstance(),
        ForEachInArrayLoopTemplateDragModel.getInstance(),
        null,
        ConditionalStatementTemplateDragModel.getInstance(),
        null,
        DoTogetherTemplateDragModel.getInstance(),
        EachInArrayTogetherTemplateDragModel.getInstance(),
        null,
        DeclareLocalDragModel.getInstance(),
        AssignmentTemplateDragModel.getInstance(),
        null,
        CommentTemplateDragModel.getInstance()
    ), models);
  }

  @Test
  public void initializeAppendsReturnTemplateForNonVoidMethods() {
    UserMethod method = createNonVoidMethod("answer");
    ControlFlowComposite composite = ControlFlowComposite.getInstance(method);

    composite.initialize();

    List<StatementTemplateDragModel> models = composite.getModels();
    assertEquals(17, models.size());
    assertNull(models.get(15));
    assertSame(ReturnStatementTemplateDragModel.getInstance(method), models.get(16));
  }

  @Test
  public void containsRecognizesInjectedStatementTemplates() {
    UserMethod method = createVoidMethod("containsMethod");
    ControlFlowComposite composite = ControlFlowComposite.getInstance(method);

    composite.initialize();

    assertTrue(composite.contains(DoTogetherTemplateDragModel.getInstance()));
    assertFalse(composite.contains(ReturnStatementTemplateDragModel.getInstance(createNonVoidMethod("other"))));
  }
}

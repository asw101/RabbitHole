package org.alice.ide;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.undo.UndoHistory;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;

import static org.junit.Assert.*;

public class ProjectDocumentTest {
  private static Project createProject() {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    sceneType.methods.add(new UserMethod(
        "markerProcedure",
        JavaType.VOID_TYPE,
        new UserParameter[0],
        new BlockStatement(new Comment("ProjectDocument test marker"))));
    NamedUserType programType = AstUtilities.createType("Program", JavaType.getInstance(SProgram.class));
    programType.fields.add(new UserField("myScene", sceneType));
    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }

  @Test
  public void constructorStoresProject() {
    Project project = createProject();
    ProjectDocument document = new ProjectDocument(project, new UserActivity());

    assertSame(project, document.getProject());
  }

  @Test
  public void constructorStoresUserActivity() {
    UserActivity userActivity = new UserActivity();
    ProjectDocument document = new ProjectDocument(createProject(), userActivity);

    assertSame(userActivity, document.getUserActivity());
  }

  @Test
  public void constructorCreatesTypeCache() {
    ProjectDocument document = new ProjectDocument(createProject(), new UserActivity());

    assertNotNull(document.getTypeCache());
  }

  @Test
  public void getUndoHistoryReturnsSameInstanceForSameGroup() {
    ProjectDocument document = new ProjectDocument(createProject(), new UserActivity());

    UndoHistory first = document.getUndoHistory(Application.DOCUMENT_UI_GROUP);
    UndoHistory second = document.getUndoHistory(Application.DOCUMENT_UI_GROUP);

    assertSame(first, second);
  }
}

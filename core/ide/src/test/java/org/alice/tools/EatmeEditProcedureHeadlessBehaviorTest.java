package org.alice.tools;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EatmeEditProcedureHeadlessBehaviorTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void helperMethodsFindSceneMethodsAndMarkersWithoutUi() throws Exception {
    Project project = projectWithSceneMethods("targetMethod", "otherMethod");
    NamedUserType sceneType = sceneType(project);
    UserMethod targetMethod = findMethod(sceneType, "targetMethod");
    UserMethod otherMethod = findMethod(sceneType, "otherMethod");
    targetMethod.body.getValue().statements.add(new Comment("marker"));
    otherMethod.body.getValue().statements.add(new Comment("marker"));

    NamedUserType resolvedSceneType = (NamedUserType) invokePrivate(
        "findSceneType",
        new Class<?>[] {Project.class},
        project);
    assertSame(sceneType, resolvedSceneType);

    @SuppressWarnings("unchecked")
    List<String> methodNames = (List<String>) invokePrivate(
        "methodNames",
        new Class<?>[] {NamedUserType.class},
        sceneType);
    assertEquals(List.of("targetMethod", "otherMethod"), methodNames);

    UserMethod resolvedMethod = (UserMethod) invokePrivate(
        "findMethod",
        new Class<?>[] {NamedUserType.class, String.class},
        sceneType,
        "targetMethod");
    assertSame(targetMethod, resolvedMethod);

    EatmeEditProcedure.MarkerCounts counts = (EatmeEditProcedure.MarkerCounts) invokePrivate(
        "countCommentMarkers",
        new Class<?>[] {NamedUserType.class, UserMethod.class, String.class},
        sceneType,
        targetMethod,
        "marker");
    assertEquals(1, counts.target());
    assertEquals(1, counts.outsideTarget());
    assertEquals(2, counts.total());
  }

  @Test
  public void helperMethodsNormalizeArtifactsAndRequireBodies() throws Exception {
    UserMethod method = new UserMethod("targetMethod", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    method.body.setValue(null);

    invokePrivate("ensureProcedureBody", new Class<?>[] {UserMethod.class}, method);
    assertNotNull(method.body.getValue());
    assertTrue(method.body.getValue().statements.isEmpty());

    Path evidenceDir = temporaryFolder.newFolder("evidence").toPath();
    Path artifact = (Path) invokePrivate(
        "artifactPath",
        new Class<?>[] {Path.class, String.class},
        evidenceDir,
        "proof.json");
    assertEquals(evidenceDir.resolve("proof.json"), artifact);

    assertThrowsWithMessage(IllegalArgumentException.class, "single relative file name", () ->
        invokePrivate("artifactPath", new Class<?>[] {Path.class, String.class}, evidenceDir, "nested/proof.json"));
    assertThrowsWithMessage(IllegalArgumentException.class, "single relative file name", () ->
        invokePrivate("artifactPath", new Class<?>[] {Path.class, String.class}, evidenceDir, "../proof.json"));
    assertThrowsWithMessage(IllegalArgumentException.class, "single relative file name", () ->
        invokePrivate("artifactPath", new Class<?>[] {Path.class, String.class}, evidenceDir, evidenceDir.resolve("proof.json").toString()));

    Path emptyFile = evidenceDir.resolve("empty.json");
    Files.writeString(emptyFile, "", StandardCharsets.UTF_8);
    assertThrowsWithMessage(IOException.class, "was not written", () ->
        invokePrivate("requireNonEmptyArtifact", new Class<?>[] {Path.class, String.class}, emptyFile, "empty artifact"));

    Path nonEmptyFile = evidenceDir.resolve("proof.json");
    Files.writeString(nonEmptyFile, "{}", StandardCharsets.UTF_8);
    invokePrivate("requireNonEmptyArtifact", new Class<?>[] {Path.class, String.class}, nonEmptyFile, "proof artifact");
  }

  @Test
  public void jsonRenderersEscapeValuesInHeadlessMode() throws Exception {
    EatmeEditProcedure.ProcedureTabSelectionEvidence tabSelection = new EatmeEditProcedure.ProcedureTabSelectionEvidence(
        "scene\"Method",
        "code\\Composite",
        "code\nEditor",
        "code\tValue");
    ProcedureEditCommand.Result commandResult = new ProcedureEditCommand.Result(
        "scene.targetMethod",
        "append-comment",
        "targetMethod",
        "targetMethod",
        1,
        2,
        true);
    EatmeEditProcedure.ProcedureEdit edit = new EatmeEditProcedure.ProcedureEdit(
        "scene.targetMethod",
        "append-comment:line\n\"two\"",
        "input\\project.a3p",
        "Scene",
        "targetMethod",
        1,
        2,
        "edited-project.a3p",
        tabSelection,
        commandResult,
        List.of("beforeOne", "before\"Two"),
        List.of("afterOne", "after\\Two"),
        "marker\nvalue",
        1,
        0);

    String resultJson = (String) invokePrivate(
        "resultJson",
        new Class<?>[] {EatmeEditProcedure.ProcedureEdit.class},
        edit);
    assertTrue(resultJson.contains("\"schema_version\":\"eatme.alice-first-lesson-code-editor-action-proof-result/v1\""));
    assertTrue(resultJson.contains("\"procedure_selector\":\"scene.targetMethod\""));
    assertTrue(resultJson.contains("visible rendering correctness"));

    String actionProofJson = (String) invokePrivate(
        "actionProofArtifactJson",
        new Class<?>[] {EatmeEditProcedure.ProcedureEdit.class},
        edit);
    assertTrue(actionProofJson.contains("append-comment:line\\n\\\"two\\\""));
    assertTrue(actionProofJson.contains("input\\\\project.a3p"));
    assertTrue(actionProofJson.contains("scene\\\"Method"));
    assertTrue(actionProofJson.contains("code\\\\Composite"));
    assertTrue(actionProofJson.contains("code\\nEditor"));
    assertTrue(actionProofJson.contains("code\\tValue"));
    assertTrue(actionProofJson.contains("\"before_methods\": [\"beforeOne\", \"before\\\"Two\"]"));
    assertTrue(actionProofJson.contains("\"after_methods\": [\"afterOne\", \"after\\\\Two\"]"));
    assertTrue(actionProofJson.contains("Save-menu completion"));
  }

  private static Project projectWithSceneMethods(String... methodNames) {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    for (String methodName : methodNames) {
      sceneType.methods.add(new UserMethod(
          methodName,
          JavaType.VOID_TYPE,
          new UserParameter[0],
          new BlockStatement(new Comment("existing " + methodName))));
    }
    NamedUserType programType = AstUtilities.createType("Program", JavaType.getInstance(SProgram.class));
    programType.fields.add(new UserField("myScene", sceneType));
    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }

  private static NamedUserType sceneType(Project project) {
    return (NamedUserType) project.getProgramType().getDeclaredFields().get(0).getValueType();
  }

  private static UserMethod findMethod(NamedUserType sceneType, String methodName) {
    for (UserMethod method : sceneType.getDeclaredMethods()) {
      if (methodName.equals(method.getName())) {
        return method;
      }
    }
    throw new AssertionError("method not found: " + methodName);
  }

  private static Object invokePrivate(String name, Class<?>[] parameterTypes, Object... args) throws Exception {
    Method method = EatmeEditProcedure.class.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    try {
      return method.invoke(null, args);
    } catch (InvocationTargetException ex) {
      Throwable cause = ex.getCause();
      if (cause instanceof Exception exception) {
        throw exception;
      }
      if (cause instanceof Error error) {
        throw error;
      }
      throw ex;
    }
  }

  private static void assertThrowsWithMessage(Class<? extends Throwable> type, String fragment, ThrowingRunnable runnable) throws Exception {
    try {
      runnable.run();
      fail("expected exception: " + type.getName());
    } catch (Throwable throwable) {
      if (!type.isInstance(throwable)) {
        throw throwable;
      }
      assertTrue(String.valueOf(throwable.getMessage()), String.valueOf(throwable.getMessage()).contains(fragment));
    }
  }

  @FunctionalInterface
  private interface ThrowingRunnable {
    void run() throws Exception;
  }
}

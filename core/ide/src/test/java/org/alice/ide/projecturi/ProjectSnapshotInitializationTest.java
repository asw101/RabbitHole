package org.alice.ide.projecturi;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;

import javax.swing.Icon;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ProjectSnapshotInitializationTest {
  private static Icon projectSnapshotIcon(String fieldName) throws Exception {
    Field field = ProjectSnapshot.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return (Icon) field.get(null);
  }

  private static Path newTestDir(String name) throws Exception {
    return Files.createDirectories(Path.of(
        "target",
        "project-snapshot-initialization-test",
        name,
        UUID.randomUUID().toString()));
  }

  private static Project project(Project.SceneCameraType sceneCameraType) {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    NamedUserType programType = AstUtilities.createType("Program", JavaType.getInstance(SProgram.class));
    programType.fields.add(new UserField("myScene", sceneType));
    sceneType.methods.add(new UserMethod(
        "snapshotMethod",
        JavaType.VOID_TYPE,
        new UserParameter[0],
        new BlockStatement(new Comment("project snapshot manifest coverage"))));
    return new Project(programType, sceneCameraType);
  }

  @Test
  public void nullUriConstructorLeavesTextNullAndUsesMissingFileIcon() throws Exception {
    ProjectSnapshot snapshot = new ProjectSnapshot((URI) null);

    assertNull(snapshot.getText());
    assertSame(projectSnapshotIcon("FILE_DOES_NOT_EXIST_ICON"), snapshot.getIcon());
    assertNull(snapshot.getThumbnail());
    assertFalse(snapshot.hasUri());
  }

  @Test
  public void relativeUriUsesLiteralTextWithoutLoadingAFile() throws Exception {
    ProjectSnapshot snapshot = new ProjectSnapshot(URI.create("relative/worlds/demo.a3p"));

    assertEquals("relative/worlds/demo.a3p", snapshot.getText());
    assertSame(projectSnapshotIcon("FILE_DOES_NOT_EXIST_ICON"), snapshot.getIcon());
    assertNull(snapshot.getThumbnail());
  }

  @Test
  public void missingAbsoluteFileKeepsDoesNotExistIconAndUsesFileName() throws Exception {
    Path missingFile = newTestDir("missing-file").resolve("missing-world.a3p");

    ProjectSnapshot snapshot = new ProjectSnapshot(missingFile.toUri());

    assertEquals("missing-world.a3p", snapshot.getText());
    assertSame(projectSnapshotIcon("FILE_DOES_NOT_EXIST_ICON"), snapshot.getIcon());
    assertNull(snapshot.getThumbnail());
  }

  @Test
  public void unreadableExistingFileFallsBackToSnapshotNotAvailableIcon() throws Exception {
    Path invalidFile = Files.writeString(
        newTestDir("broken-project").resolve("broken-world.a3p"),
        "not-a-valid-project-archive");

    ProjectSnapshot snapshot = new ProjectSnapshot(invalidFile.toUri());

    assertEquals("broken-world.a3p", snapshot.getText());
    assertSame(projectSnapshotIcon("SNAPSHOT_NOT_AVAILABLE_ICON"), snapshot.getIcon());
    assertNull(snapshot.getThumbnail());
  }

  @Test
  public void savedVrProjectReadsManifestAndAppendsVrMarkerToText() throws Exception {
    Path projectFile = newTestDir("vr-project").resolve("vr-world.a3p");
    IoUtilities.writeProject(projectFile.toFile(), project(Project.SceneCameraType.VRHeadset));

    ProjectSnapshot snapshot = new ProjectSnapshot(projectFile.toUri());

    assertTrue(snapshot.isVrProject());
    assertEquals("vr-world.a3p (VR)", snapshot.getText());
    assertNotNull(snapshot.getIcon());
  }

  @Test
  public void savedWindowProjectKeepsOriginalFileNameWithoutVrMarker() throws Exception {
    Path projectFile = newTestDir("window-project").resolve("window-world.a3p");
    IoUtilities.writeProject(projectFile.toFile(), project(Project.SceneCameraType.WindowCamera));

    ProjectSnapshot snapshot = new ProjectSnapshot(projectFile.toUri());

    assertFalse(snapshot.isVrProject());
    assertEquals("window-world.a3p", snapshot.getText());
    assertNotNull(snapshot.getIcon());
  }
}

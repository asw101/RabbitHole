package org.alice.tools;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EatmeTransformObjectTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void transformsPlacedBunnyAndWritesEatmeProofArtifacts() throws Exception {
    File starterProject = placedBunnyProject();
    Path evidenceDir = temporaryFolder.newFolder("evidence").toPath();
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();

    int status = EatmeTransformObject.run(
        new String[] {
            "--project", starterProject.getAbsolutePath(),
            "--object-identifier", "alice-gallery://animals/bunny",
            "--target-position", "1.5,0.0,-2.0",
            "--scale", "1.25",
            "--evidence-dir", evidenceDir.toString(),
            "--json"
        },
        new PrintStream(stdout),
        new PrintStream(stderr));

    assertEquals(stderr.toString(StandardCharsets.UTF_8), 0, status);
    String result = stdout.toString(StandardCharsets.UTF_8);
    assertTrue(result, result.contains("\"schema_version\":\"eatme.alice-object-transform-result/v1\""));
    assertTrue(result, result.contains("\"status\":\"transformed\""));
    assertTrue(result, result.contains("\"object_id\":\"alice-gallery://animals/bunny\""));
    assertTrue(result, result.contains("\"transform_artifact\":\"object-transform.json\""));
    assertTrue(result, result.contains("\"transformed_project_artifact\":\"transformed-project.a3p\""));
    assertTrue(result, result.contains("\"target_position\":{\"x\":1.5,\"y\":0.0,\"z\":-2.0}"));
    assertTrue(result, result.contains("\"scale\":1.25"));
    assertTrue(result, result.contains("\"persistence\":\"scene_method_marker\""));

    assertTrue(Files.size(evidenceDir.resolve("object-transform.json")) > 0);
    assertTrue(Files.size(evidenceDir.resolve("transformed-project.a3p")) > 0);
    String artifact = Files.readString(evidenceDir.resolve("object-transform.json"));
    assertTrue(artifact, artifact.contains("\"schema_version\": \"eatme.alice-object-transform-artifact/v1\""));
    assertTrue(artifact, artifact.contains("\"field_name\": \"bunny\""));
  }

  @Test
  public void rejectsProjectWithoutPlacedBunnyWithoutProofArtifacts() throws Exception {
    File starterProject = temporaryFolder.newFile("starter.a3p");
    IoUtilities.writeProject(starterProject, projectWithScene());
    Path evidenceDir = temporaryFolder.newFolder("evidence").toPath();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();

    int status = EatmeTransformObject.run(
        new String[] {
            "--project", starterProject.getAbsolutePath(),
            "--object-identifier", "alice-gallery://animals/bunny",
            "--evidence-dir", evidenceDir.toString(),
            "--json"
        },
        new PrintStream(new ByteArrayOutputStream()),
        new PrintStream(stderr));

    assertEquals(2, status);
    assertTrue(stderr.toString(StandardCharsets.UTF_8).contains("project does not contain a placed bunny object"));
    assertTrue(Files.notExists(evidenceDir.resolve("object-transform.json")));
    assertTrue(Files.notExists(evidenceDir.resolve("transformed-project.a3p")));
  }

  @Test
  public void rejectsOtherBipedWithoutBunnyPlacementWithoutProofArtifacts() throws Exception {
    File projectFile = placedBunnyProject();
    Project project = IoUtilities.readProject(projectFile);
    NamedUserType sceneType = (NamedUserType) project.getProgramType()
        .getDeclaredFields()
        .get(0)
        .getValueType();
    sceneType.getDeclaredFields().get(0).name.setValue("astronaut");
    IoUtilities.writeProject(projectFile, project);
    Path evidenceDir = temporaryFolder.newFolder("evidence").toPath();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();

    int status = EatmeTransformObject.run(
        new String[] {
            "--project", projectFile.getAbsolutePath(),
            "--object-identifier", "alice-gallery://animals/bunny",
            "--evidence-dir", evidenceDir.toString(),
            "--json"
        },
        new PrintStream(new ByteArrayOutputStream()),
        new PrintStream(stderr));

    assertEquals(2, status);
    assertTrue(stderr.toString(StandardCharsets.UTF_8).contains("project does not contain a placed bunny object"));
    assertTrue(Files.notExists(evidenceDir.resolve("object-transform.json")));
  }

  @Test
  public void rejectsUnsupportedObjectIdentifierWithoutProofArtifacts() throws Exception {
    File starterProject = placedBunnyProject();
    Path evidenceDir = temporaryFolder.newFolder("evidence").toPath();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();

    int status = EatmeTransformObject.run(
        new String[] {
            "--project", starterProject.getAbsolutePath(),
            "--object-identifier", "alice-gallery://animals/dragon",
            "--evidence-dir", evidenceDir.toString(),
            "--json"
        },
        new PrintStream(new ByteArrayOutputStream()),
        new PrintStream(stderr));

    assertEquals(2, status);
    assertTrue(stderr.toString(StandardCharsets.UTF_8).contains("unsupported object identifier"));
    assertTrue(Files.notExists(evidenceDir.resolve("object-transform.json")));
  }

  private File placedBunnyProject() throws Exception {
    File starterProject = temporaryFolder.newFile("starter.a3p");
    IoUtilities.writeProject(starterProject, projectWithScene());
    Path placementDir = temporaryFolder.newFolder("placement").toPath();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    int status = EatmePlaceObject.run(
        new String[] {
            "--project", starterProject.getAbsolutePath(),
            "--object", "alice-gallery://animals/bunny",
            "--evidence-dir", placementDir.toString(),
            "--json"
        },
        new PrintStream(new ByteArrayOutputStream()),
        new PrintStream(stderr));
    assertEquals(stderr.toString(StandardCharsets.UTF_8), 0, status);
    return placementDir.resolve("placed-project.a3p").toFile();
  }

  private static Project projectWithScene() {
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    NamedUserType programType = AstUtilities.createType("Program", JavaType.getInstance(SProgram.class));
    programType.fields.add(new UserField("myScene", sceneType));
    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }
}

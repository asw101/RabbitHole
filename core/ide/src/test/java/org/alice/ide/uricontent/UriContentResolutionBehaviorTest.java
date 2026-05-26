package org.alice.ide.uricontent;

import org.alice.stageide.openprojectpane.models.TemplateUriState;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SProgram;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.*;

public class UriContentResolutionBehaviorTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void starterProjectUtilitiesRoundTripStarterUrisWithEncodedCharacters() throws IOException {
    File directory = temporaryFolder.newFolder("starter worlds");
    File projectFile = new File(directory, "über starter world.a3p");
    assertTrue(projectFile.createNewFile());

    URI starterUri = StarterProjectUtilities.toUri(projectFile);

    assertEquals(TemplateUriState.STARTER_SCHEME, starterUri.getScheme());
    assertEquals(projectFile.toURI(), StarterProjectUtilities.toFileUriFromStarterUri(starterUri));
    assertEquals(projectFile.getCanonicalFile(), StarterProjectUtilities.toFile(starterUri).getCanonicalFile());
  }

  @Test
  public void starterProjectLoaderResolvesStarterUriBackToLoadableProject() throws IOException {
    File projectFile = temporaryFolder.newFile("starter-generated-world.a3p");
    Project project = new Project(programType("StarterProgram"), Project.SceneCameraType.WindowCamera);
    IoUtilities.writeProject(projectFile, project);
    URI starterUri = StarterProjectUtilities.toUri(projectFile);

    StarterProjectFileLoader loader = new StarterProjectFileLoader(starterUri, false);
    Project loadedProject = loader.load();

    assertNotNull(loadedProject);
    assertEquals("StarterProgram", loadedProject.getProgramType().getName());
    assertEquals(starterUri, loader.getUri());
    assertTrue(loader.isNewProject());
    assertNull(loader.getMainProjectFile());
  }

  @Test
  public void blankSlateLoaderUsesRequestedCameraTypeForHeadlessProjectCreation() {
    BlankSlateProjectLoader windowLoader = new BlankSlateProjectLoader(TemplateUriState.Template.GRASS, false);
    BlankSlateProjectLoader vrLoader = new BlankSlateProjectLoader(TemplateUriState.Template.GRASS, true);

    Project windowProject = windowLoader.load();
    Project vrProject = vrLoader.load();

    assertEquals(Project.SceneCameraType.WindowCamera, windowProject.createSaveManifest().projectStructure.sceneCameraType);
    assertEquals(Project.SceneCameraType.VRHeadset, vrProject.createSaveManifest().projectStructure.sceneCameraType);
    assertTrue(windowLoader.isNewProject());
    assertTrue(vrLoader.isNewProject());
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    return type;
  }
}

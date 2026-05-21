package org.lgna.project;

import org.alice.tweedle.file.Manifest;
import org.alice.tweedle.file.ProjectManifest;
import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.event.ResourceEvent;
import org.lgna.project.event.ResourceListener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class ProjectTest {
  @Test
  public void saveManifestUsesWorldMetadata() {
    NamedUserType programType = createProgramType("Program");
    Project project = new Project(programType, Project.SceneCameraType.WindowCamera);

    ProjectManifest manifest = project.createSaveManifest();

    assertEquals("a3p", manifest.metadata.fileType);
    assertEquals(programType.getName(), manifest.description.name);
    assertEquals(programType.getId().toString(), manifest.metadata.identifier.name);
    assertEquals(Manifest.ProjectType.World, manifest.metadata.identifier.type);
    assertEquals(Project.SceneCameraType.WindowCamera, manifest.projectStructure.sceneCameraType);
    assertNotNull(project.getLock());
    assertSame(programType, project.getProgramType());
  }

  @Test
  public void exportManifestAddsStandardLibraryPrerequisite() {
    NamedUserType programType = createProgramType("Program");
    Project project = new Project(programType, Project.SceneCameraType.VRHeadset);

    ProjectManifest manifest = project.createExportManifest();

    assertEquals("a3w", manifest.metadata.fileType);
    assertEquals(Project.SceneCameraType.VRHeadset, manifest.projectStructure.sceneCameraType);
    assertEquals(1, manifest.prerequisites.size());
    assertEquals(Manifest.ProjectType.Library, manifest.prerequisites.get(0).type);
    assertEquals("SceneGraphLibrary", manifest.prerequisites.get(0).name);
    assertEquals("0.16", manifest.prerequisites.get(0).version);
  }

  @Test
  public void addAndRemoveResourceNotifyListenersAndIgnoreDuplicates() {
    NamedUserType programType = createProgramType("Program");
    Project project = new Project(programType, new HashSet<>(Collections.singleton(programType)), new HashSet<>(), Project.SceneCameraType.WindowCamera);
    RecordingResourceListener listener = new RecordingResourceListener();
    project.addResourceListener(listener);

    TestResource resource = new TestResource("sound.wav");
    project.addResource(resource);
    project.addResource(resource);
    project.removeResource(resource);

    assertEquals(1, listener.addedCount);
    assertEquals(1, listener.removedCount);
    assertSame(resource, listener.lastEvent.getResource());
    assertFalse(project.getResources().contains(resource));
  }

  @Test
  public void removingListenerStopsFurtherNotifications() {
    NamedUserType programType = createProgramType("Program");
    Project project = new Project(programType, new HashSet<>(Collections.singleton(programType)), new HashSet<>(), Project.SceneCameraType.WindowCamera);
    RecordingResourceListener listener = new RecordingResourceListener();
    project.addResourceListener(listener);
    project.removeResourceListener(listener);

    project.addResource(new TestResource("image.png"));

    assertEquals(0, listener.addedCount);
    assertEquals(0, listener.removedCount);
  }

  @Test
  public void referencedResourcesAreCollectedAndMissingEntriesAreAddedBackToProject() {
    NamedUserType programType = createProgramType("Program");
    TestResource referenced = new TestResource("texture.png");
    programType.fields.add(new UserField("resourceField", JavaType.getInstance(TestResource.class), new ResourceExpression(TestResource.class, referenced)));

    Project project = new Project(programType, new HashSet<>(Collections.singleton(programType)), new HashSet<>(), Project.SceneCameraType.WindowCamera);

    Set<Resource> referencedResources = project.getReferencedResources();

    assertTrue(referencedResources.contains(referenced));
    assertTrue(project.getResources().contains(referenced));
  }

  @Test
  public void getNamedUserTypesIncludesProgramTypeAndSeedTypes() {
    NamedUserType programType = createProgramType("Program");
    NamedUserType helper = createProgramType("Helper");
    Project project = new Project(programType, new HashSet<>(Set.of(programType, helper)), new HashSet<>(), Project.SceneCameraType.WindowCamera);

    Set<NamedUserType> types = project.getNamedUserTypes();

    assertTrue(types.contains(programType));
    assertTrue(types.contains(helper));
  }

  private static NamedUserType createProgramType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.OBJECT_TYPE);
    type.methods.add(new UserMethod("marker", Void.TYPE, new UserParameter[0], new BlockStatement()));
    return type;
  }

  private static final class RecordingResourceListener implements ResourceListener {
    private int addedCount;
    private int removedCount;
    private ResourceEvent lastEvent;

    @Override
    public void resourceAdded(ResourceEvent e) {
      addedCount++;
      lastEvent = e;
    }

    @Override
    public void resourceRemoved(ResourceEvent e) {
      removedCount++;
      lastEvent = e;
    }
  }

  private static final class TestResource extends Resource {
    private TestResource(String name) {
      super(UUID.randomUUID());
      setOriginalFileName(name);
      setName(name);
      setContent("application/octet-stream", new byte[] {2, 4});
    }
  }
}

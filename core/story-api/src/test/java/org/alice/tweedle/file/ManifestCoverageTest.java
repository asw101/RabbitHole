package org.alice.tweedle.file;

import org.alice.math.immutable.AxisAlignedBox;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.annotations.Visibility;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class ManifestCoverageTest {
  @Test
  public void manifestAccessorsAndUriUseMetadataAndRootFile() {
    Manifest manifest = new Manifest();
    manifest.description.name = "ExampleModel";
    manifest.metadata.identifier.name = "example-id";
    manifest.metadata.identifier.type = Manifest.ProjectType.World;
    manifest.setRootFile(new File("core/story-api"));

    assertEquals("example-id", manifest.getId());
    assertEquals("ExampleModel", manifest.getName());
    assertTrue(manifest.getUri("fixture.txt").toString().contains("core/story-api"));
    assertNull(manifest.getUri(null));
  }

  @Test
  public void manifestCopyForExportKeepsOnlyStructureResourcesAndResetsMetadata() {
    Manifest manifest = new Manifest();
    manifest.description.name = "GalleryThing";
    manifest.provenance.creator = "Tester";
    manifest.metadata.identifier.name = "ignored";
    manifest.metadata.identifier.type = Manifest.ProjectType.Library;

    StructureReference structure = new StructureReference();
    structure.name = "mesh";
    structure.file = "mesh.a3r";
    AliceTextureReference texture = new AliceTextureReference();
    texture.name = "texture";
    texture.file = "texture.a3t";
    manifest.resources.add(structure);
    manifest.resources.add(texture);

    ExportableManifest copy = new ExportableManifest();
    manifest.copyForExport(copy);

    assertSame(manifest.description, copy.description);
    assertSame(manifest.provenance, copy.provenance);
    assertEquals("GalleryThing", copy.metadata.identifier.name);
    assertEquals(Manifest.ProjectType.Model, copy.metadata.identifier.type);
    assertEquals(1, copy.resources.size());
    assertTrue(copy.resources.get(0) instanceof StructureReference);
    assertNotSame(structure, copy.resources.get(0));
  }

  @Test
  public void modelManifestLookupAndCopyForExportCoverNestedTypes() {
    ModelManifest manifest = new ModelManifest();
    manifest.description.name = "OriginalName";
    manifest.parentClass = "Prop";
    manifest.rootJoints.add("ROOT");
    manifest.placeOnGround = Boolean.FALSE;

    StructureReference structure = new StructureReference();
    structure.name = "structureA";
    structure.file = "mesh.a3r";
    AliceTextureReference texture = new AliceTextureReference();
    texture.name = "textureA";
    texture.file = "texture.a3t";
    manifest.resources.add(structure);
    manifest.resources.add(texture);

    ModelManifest.TextureSet textureSet = new ModelManifest.TextureSet();
    textureSet.name = "setA";
    textureSet.idToResourceMap.put(1, "textureA");
    manifest.textureSets.add(textureSet);

    ModelManifest.ModelVariant defaultVariant = new ModelManifest.ModelVariant();
    defaultVariant.name = "OriginalName";
    defaultVariant.structure = "structureA";
    defaultVariant.textureSet = "setA";
    defaultVariant.icon = "thumb.png";
    manifest.models.add(defaultVariant);

    ModelManifest.ModelVariant altVariant = new ModelManifest.ModelVariant();
    altVariant.name = "ALT";
    altVariant.structure = "structureA";
    altVariant.textureSet = "setA";
    manifest.models.add(altVariant);

    ModelManifest.Joint joint = new ModelManifest.Joint();
    joint.name = "Jaw";
    joint.parent = "Head";
    joint.visibility = Visibility.PRIME_TIME;
    manifest.additionalJoints.add(joint);

    ModelManifest.JointArray jointArray = new ModelManifest.JointArray();
    jointArray.name = "Tail";
    jointArray.visibility = Visibility.COMPLETELY_HIDDEN;
    jointArray.jointIds.addAll(List.of("Tail0", "Tail1"));
    manifest.additionalJointArrays.add(jointArray);

    ModelManifest.JointArrayId jointArrayId = new ModelManifest.JointArrayId();
    jointArrayId.name = "TAIL";
    jointArrayId.patternId = "Tail";
    jointArrayId.rootJoint = "ROOT";
    jointArrayId.visibility = Visibility.PRIME_TIME;
    manifest.additionalJointArrayIds.add(jointArrayId);

    ModelManifest.Pose pose = new ModelManifest.Pose();
    pose.name = "Wave";
    ModelManifest.JointTransform transform = new ModelManifest.JointTransform();
    transform.jointName = "Jaw";
    transform.orientation = List.of(0.0f, 0.0f, 0.0f, 1.0f);
    transform.position = List.of(1.0f, 2.0f, 3.0f);
    pose.transforms.add(transform);
    manifest.poses.add(pose);

    ModelManifest.BoundingBox bbox = new ModelManifest.BoundingBox();
    bbox.min = List.of(-1.0f, 0.0f, -2.0f);
    bbox.max = List.of(2.0f, 3.0f, 4.0f);
    manifest.boundingBox = bbox;

    manifest.addBoundsForJoint("Jaw", AxisAlignedBox.createAxisAlignedBox(-1, 0, -2, 2, 3, 4));
    manifest.addBoundsForJoint("Ignored", AxisAlignedBox.NaN);

    assertSame(defaultVariant, manifest.getModelVariant("OriginalName"));
    assertNull(manifest.getModelVariant("missing"));
    assertSame(texture, manifest.getAliceTextureReference("textureA"));
    assertNull(manifest.getAliceTextureReference("structureA"));
    assertSame(structure, manifest.getStructure("structureA"));
    assertNull(manifest.getStructure("textureA"));
    assertSame(textureSet, manifest.getTextureSet("setA"));
    assertNull(manifest.getTextureSet("missing"));
    assertEquals(1, manifest.jointBounds.size());

    ModelManifest copy = manifest.copyForExport();
    assertEquals("Prop", copy.parentClass);
    assertEquals(List.of("ROOT"), copy.rootJoints);
    assertFalse(copy.placeOnGround);
    assertEquals(2, copy.models.size());
    assertEquals("DEFAULT", copy.models.get(0).name);
    assertEquals("ALT", copy.models.get(1).name);
    assertEquals("setA", copy.models.get(0).textureSet);
    assertEquals("textureA", copy.textureSets.get(0).idToResourceMap.get(1));
    assertEquals(1, copy.resources.size());
    assertTrue(copy.resources.get(0) instanceof StructureReference);
    assertEquals("Jaw (->Head)", joint.toString());
    assertEquals(joint, joint);
    assertEquals(joint.hashCode(), newJoint("Jaw", "Head").hashCode());
    assertNotEquals(joint, newJoint("Jaw", "Neck"));
  }

  @Test
  public void projectManifestStartsWithProjectStructureAndAllowsSceneCameraType() {
    ProjectManifest manifest = new ProjectManifest();
    assertNotNull(manifest.projectStructure);
    assertNull(manifest.projectStructure.sceneCameraType);

    manifest.projectStructure.sceneCameraType = Project.SceneCameraType.WindowCamera;
    assertEquals(Project.SceneCameraType.WindowCamera, manifest.projectStructure.sceneCameraType);
  }

  private static ModelManifest.Joint newJoint(String name, String parent) {
    ModelManifest.Joint joint = new ModelManifest.Joint();
    joint.name = name;
    joint.parent = parent;
    return joint;
  }

  private static final class ExportableManifest extends Manifest {
  }
}

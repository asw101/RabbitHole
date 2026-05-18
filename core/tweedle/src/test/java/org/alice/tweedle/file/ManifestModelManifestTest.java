package org.alice.tweedle.file;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;
import org.lgna.project.annotations.Visibility;

import java.io.File;
import java.time.Year;
import java.util.List;

import static org.junit.Assert.*;

public class ManifestModelManifestTest {

  @Test
  public void manifestExposesDefaultsAndResourceLookups() {
    Manifest manifest = new Manifest();
    StructureReference structure = new StructureReference();
    structure.name = "body";
    structure.file = "body.skel";
    structure.format = "model/x-skeleton";
    manifest.description.name = "Alien";
    manifest.metadata.identifier.name = "alien-id";
    manifest.resources.add(structure);

    assertEquals("Alien", manifest.getName());
    assertEquals("alien-id", manifest.getId());
    assertEquals("thumbnail.png", manifest.description.icon);
    assertEquals("Anonymous", manifest.provenance.creator);
    assertEquals("0.1", manifest.metadata.formatVersion);
    assertEquals("1.0", manifest.metadata.identifier.version);
    assertSame(structure, manifest.getResource("body"));
    assertNull(manifest.getResource("missing"));
    assertNull(manifest.getUri(null));
    assertTrue(manifest.getUri("body.skel").toString().endsWith("body.skel"));

    manifest.setRootFile(new File("core/tweedle"));
    assertTrue(manifest.getUri("nested/file.png").toString().contains("core/tweedle/nested/file.png"));
  }

  @Test
  public void manifestCopyForExportClonesOnlyStructureResources() {
    Manifest original = new Manifest();
    original.description.name = "Robot";
    original.provenance.creator = "Tester";
    original.provenance.created = Year.of(2024);

    Manifest.ProjectIdentifier prerequisite = new Manifest.ProjectIdentifier();
    prerequisite.name = "Base";
    prerequisite.type = Manifest.ProjectType.Library;
    original.prerequisites.add(prerequisite);

    StructureReference structure = new StructureReference();
    structure.name = "robotBody";
    structure.file = "robot.skel";
    structure.format = "model/x-skeleton";

    ImageReference image = new ImageReference("icon", "icon.png", "image/png", 64, 32);
    original.resources.add(structure);
    original.resources.add(image);

    Manifest copy = new Manifest();
    original.copyForExport(copy);

    assertSame(original.description, copy.description);
    assertSame(original.provenance, copy.provenance);
    assertNotSame(original.metadata, copy.metadata);
    assertEquals("Robot", copy.metadata.identifier.name);
    assertEquals(Manifest.ProjectType.Model, copy.metadata.identifier.type);
    assertEquals(1, copy.prerequisites.size());
    assertNotSame(original.prerequisites, copy.prerequisites);
    assertEquals(1, copy.resources.size());
    assertTrue(copy.resources.getFirst() instanceof StructureReference);
    assertNotSame(structure, copy.resources.getFirst());
  }

  @Test
  public void modelManifestCopyForExportCopiesVariantsTextureSetsAndBounds() {
    ModelManifest manifest = new ModelManifest();
    manifest.description.name = "Alien";
    manifest.parentClass = "SModel";
    manifest.rootJoints.add("ROOT");
    manifest.additionalJointArrays.add(new ModelManifest.JointArray());
    manifest.additionalJointArrayIds.add(new ModelManifest.JointArrayId());
    manifest.poses.add(new ModelManifest.Pose());
    manifest.placeOnGround = Boolean.FALSE;
    manifest.boundingBox = new ModelManifest.BoundingBox();
    manifest.boundingBox.min = List.of(-1.0f, -2.0f, -3.0f);
    manifest.boundingBox.max = List.of(1.0f, 2.0f, 3.0f);

    ModelManifest.TextureSet textureSet = new ModelManifest.TextureSet();
    textureSet.name = "default";
    textureSet.idToResourceMap.put(1, "texture-1");
    manifest.textureSets.add(textureSet);

    ModelManifest.ModelVariant defaultVariant = new ModelManifest.ModelVariant();
    defaultVariant.name = "Alien";
    defaultVariant.structure = "body";
    defaultVariant.textureSet = "default";
    defaultVariant.icon = "alien.png";
    manifest.models.add(defaultVariant);

    ModelManifest.ModelVariant altVariant = new ModelManifest.ModelVariant();
    altVariant.name = "VariantA";
    altVariant.structure = "body";
    altVariant.textureSet = "default";
    altVariant.icon = "variant.png";
    manifest.models.add(altVariant);

    AliceTextureReference aliceTexture = new AliceTextureReference();
    aliceTexture.name = "texture-1";
    StructureReference structure = new StructureReference();
    structure.name = "body";
    manifest.resources.add(aliceTexture);
    manifest.resources.add(structure);

    AxisAlignedBox bounds = AxisAlignedBox.createAxisAlignedBox(-1, -2, -3, 1, 2, 3);
    manifest.addBoundsForJoint("ROOT", bounds);
    manifest.addBoundsForJoint("IGNORED", AxisAlignedBox.NaN);

    ModelManifest copy = manifest.copyForExport();

    assertEquals("SModel", copy.parentClass);
    assertEquals(List.of("ROOT"), copy.rootJoints);
    assertNotSame(manifest.textureSets, copy.textureSets);
    assertNotSame(textureSet, copy.textureSets.getFirst());
    assertEquals("texture-1", copy.textureSets.getFirst().idToResourceMap.get(1));
    assertEquals("DEFAULT", copy.models.get(0).name);
    assertEquals("VariantA", copy.models.get(1).name);
    assertEquals(1, manifest.jointBounds.size());
    assertEquals("ROOT", manifest.jointBounds.getFirst().name);
    assertEquals(List.of(-1.0f, -2.0f, -3.0f), manifest.jointBounds.getFirst().bounds.min);
    assertEquals(List.of(1.0f, 2.0f, 3.0f), manifest.jointBounds.getFirst().bounds.max);
    assertEquals(1, copy.resources.size());
    assertTrue(copy.resources.getFirst() instanceof StructureReference);
    assertEquals(Boolean.FALSE, copy.placeOnGround);
  }

  @Test
  public void modelManifestFindersAndInnerTypesBehaveAsExpected() {
    ModelManifest manifest = new ModelManifest();

    AliceTextureReference aliceTexture = new AliceTextureReference();
    aliceTexture.name = "texture";
    StructureReference structure = new StructureReference();
    structure.name = "skeleton";
    manifest.resources.add(aliceTexture);
    manifest.resources.add(structure);

    ModelManifest.TextureSet textureSet = new ModelManifest.TextureSet();
    textureSet.name = "default";
    textureSet.idToResourceMap.put(2, "texture");
    manifest.textureSets.add(textureSet);

    ModelManifest.ModelVariant variant = new ModelManifest.ModelVariant();
    variant.name = "default";
    manifest.models.add(variant);

    assertSame(variant, manifest.getModelVariant("default"));
    assertNull(manifest.getModelVariant("other"));
    assertSame(aliceTexture, manifest.getAliceTextureReference("texture"));
    assertNull(manifest.getAliceTextureReference("skeleton"));
    assertSame(structure, manifest.getStructure("skeleton"));
    assertNull(manifest.getStructure("texture"));
    assertSame(textureSet, manifest.getTextureSet("default"));
    assertNull(manifest.getTextureSet("other"));

    ModelManifest.Joint root = new ModelManifest.Joint();
    root.name = "ROOT";
    root.parent = null;
    root.visibility = Visibility.PRIME_TIME;

    ModelManifest.Joint child = new ModelManifest.Joint();
    child.name = "ROOT";
    child.parent = null;

    ModelManifest.Joint childWithParent = new ModelManifest.Joint();
    childWithParent.name = "HAND";
    childWithParent.parent = "ARM";

    assertEquals(root, child);
    assertEquals(root.hashCode(), child.hashCode());
    assertNotEquals(root, childWithParent);
    assertEquals("ROOT", root.toString());
    assertEquals("HAND (->ARM)", childWithParent.toString());

    ModelManifest.JointBounds constructed = new ModelManifest.JointBounds("HAND", new AxisAlignedBox(new Point3(1, 2, 3), new Point3(4, 5, 6)));
    ModelManifest.JointBounds empty = new ModelManifest.JointBounds();
    assertEquals("HAND", constructed.name);
    assertEquals(List.of(1.0f, 2.0f, 3.0f), constructed.bounds.min);
    assertEquals(List.of(4.0f, 5.0f, 6.0f), constructed.bounds.max);
    assertEquals("", empty.name);
  }

  @Test
  public void trivialManifestSubtypesRemainPlainManifests() {
    assertTrue(new LibraryManifest() instanceof Manifest);
    assertTrue(new TypeManifest() instanceof Manifest);
  }
}

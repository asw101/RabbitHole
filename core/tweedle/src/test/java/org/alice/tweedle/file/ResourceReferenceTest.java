package org.alice.tweedle.file;

import org.junit.Test;
import org.lgna.common.resources.AudioResource;
import org.lgna.common.resources.ImageResource;

import java.util.UUID;

import static org.junit.Assert.*;

public class ResourceReferenceTest {

  @Test
  public void resourceReferenceSubtypesReportTheirContentTypes() {
    TypeReference typeReference = new TypeReference();
    ModelReference modelReference = new ModelReference();
    AliceTextureReference aliceTextureReference = new AliceTextureReference();
    StructureReference structureReference = new StructureReference();

    assertEquals("Class", typeReference.getContentType());
    assertEquals("Class", typeReference.type);
    assertEquals("model", modelReference.getContentType());
    assertEquals("aliceTexture", aliceTextureReference.getContentType());
    assertEquals("skeletonMesh", structureReference.getContentType());
  }

  @Test
  public void resourceReferencesCanBeClonedAndConstructedWithBasicFields() throws CloneNotSupportedException {
    TypeReference typeReference = new TypeReference("Scene", "Scene.cls", "application/json");
    typeReference.provenance = new Manifest.Provenance();
    ResourceReference cloned = typeReference.clone();

    assertNotSame(typeReference, cloned);
    assertEquals("Scene", cloned.name);
    assertEquals("Scene.cls", cloned.file);
    assertEquals("application/json", cloned.format);
    assertEquals("Class", cloned.type);
    assertSame(typeReference.provenance, cloned.provenance);
  }

  @Test
  public void imageReferenceCapturesDirectConstructionAndImageResources() {
    ImageReference direct = new ImageReference("icon", "icon.png", "image/png", 64, 32);
    assertEquals("icon", direct.name);
    assertEquals("icon.png", direct.file);
    assertEquals("image/png", direct.format);
    assertEquals(64.0f, direct.width, 0.0f);
    assertEquals(32.0f, direct.height, 0.0f);
    assertNotNull(direct.uuid);
    assertEquals("image", direct.getContentType());

    UUID imageId = UUID.randomUUID();
    ImageResource resource = new ImageResource(imageId);
    resource.setName("texture");
    resource.setOriginalFileName("texture.png");
    resource.setContent("image/png", new byte[]{1, 2, 3});
    resource.setWidth(128);
    resource.setHeight(256);

    ImageReference fromResource = new ImageReference(resource);
    assertEquals(imageId, fromResource.uuid);
    assertEquals("texture", fromResource.name);
    assertEquals("texture.png", fromResource.file);
    assertEquals("image/png", fromResource.format);
    assertEquals(128.0f, fromResource.width, 0.0f);
    assertEquals(256.0f, fromResource.height, 0.0f);
  }

  @Test
  public void audioReferencePreservesDurationAndNormalizesNaN() {
    UUID audioId = UUID.randomUUID();
    AudioResource resource = new AudioResource(audioId);
    resource.setName("beep");
    resource.setOriginalFileName("beep.wav");
    resource.setContent("audio.x_wav", new byte[]{4, 5, 6});
    resource.setDuration(4.25);

    AudioReference fromResource = new AudioReference(resource);
    assertEquals(audioId, fromResource.uuid);
    assertEquals("beep", fromResource.name);
    assertEquals("beep.wav", fromResource.file);
    assertEquals("audio.x_wav", fromResource.format);
    assertEquals(4.25, fromResource.duration, 0.0);
    assertEquals("audio", fromResource.getContentType());

    AudioResource nanResource = new AudioResource(UUID.randomUUID());
    nanResource.setName("silent");
    nanResource.setOriginalFileName("silent.wav");
    nanResource.setContent("audio.x_wav", new byte[]{7});
    nanResource.setDuration(Double.NaN);

    AudioReference normalized = new AudioReference(nanResource);
    assertEquals(0.0, normalized.duration, 0.0);
  }
}

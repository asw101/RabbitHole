package org.lgna.common.resources;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ImageResourceDeepTest {

  @Test
  public void getContentType_png() {
    assertEquals("image/png", ImageResource.getContentType("sprite.png"));
  }

  @Test
  public void getContentType_jpg() {
    assertEquals("image/jpeg", ImageResource.getContentType("photo.jpg"));
  }

  @Test
  public void getContentType_unknownExtension() {
    assertNull(ImageResource.getContentType("notes.txt"));
  }

  @Test
  public void uuidConstructor_storesUuid() {
    UUID uuid = UUID.randomUUID();

    assertEquals(uuid, new ImageResource(uuid).getId());
  }

  @Test
  public void width_defaultIsNegativeOne() {
    assertEquals(-1, new ImageResource(UUID.randomUUID()).getWidth());
  }

  @Test
  public void height_defaultIsNegativeOne() {
    assertEquals(-1, new ImageResource(UUID.randomUUID()).getHeight());
  }

  @Test
  public void setWidth_getWidth_roundTrip() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    resource.setWidth(640);

    assertEquals(640, resource.getWidth());
  }

  @Test
  public void setHeight_getHeight_roundTrip() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    resource.setHeight(480);

    assertEquals(480, resource.getHeight());
  }

  @Test
  public void setWidth_zero() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    resource.setWidth(0);

    assertEquals(0, resource.getWidth());
  }

  @Test
  public void setHeight_zero() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    resource.setHeight(0);

    assertEquals(0, resource.getHeight());
  }

  @Test
  public void valueOf_returnsSameInstanceForSameUuid() {
    UUID uuid = UUID.randomUUID();

    assertSame(ImageResource.valueOf(uuid.toString()), ImageResource.valueOf(uuid.toString()));
  }

  @Test
  public void valueOf_createsResourceWithMatchingUuid() {
    UUID uuid = UUID.randomUUID();

    assertEquals(uuid, ImageResource.valueOf(uuid.toString()).getId());
  }

  @Test
  public void valueOf_differentUuidsReturnDifferentInstances() {
    UUID first = UUID.randomUUID();
    UUID second = UUID.randomUUID();

    assertNotSame(ImageResource.valueOf(first.toString()), ImageResource.valueOf(second.toString()));
  }

  @Test
  public void createFilenameFilter_notNull() {
    assertNotNull(ImageResource.createFilenameFilter(false));
  }

  @Test
  public void createFilenameFilter_acceptsDirectories_true() {
    assertNotNull(ImageResource.createFilenameFilter(true));
  }

  @Test
  public void setName_getName_roundTrip() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    resource.setName("photo.png");

    assertEquals("photo.png", resource.getName());
  }

  @Test
  public void setOriginalFileName_getOriginalFileName() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    resource.setOriginalFileName("original.jpg");

    assertEquals("original.jpg", resource.getOriginalFileName());
  }

  @Test
  public void setContent_setsContentTypeAndData() {
    ImageResource resource = new ImageResource(UUID.randomUUID());
    byte[] data = { (byte) 0x89, 0x50, 0x4E, 0x47 };

    resource.setContent("image/png", data);

    assertEquals("image/png", resource.getContentType());
    assertArrayEquals(data, resource.getData());
  }

  @Test
  public void setWidthAndHeight() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    resource.setWidth(1920);
    resource.setHeight(1080);

    assertEquals(1920, resource.getWidth());
    assertEquals(1080, resource.getHeight());
  }

  @Test
  public void toString_containsResource() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    resource.setName("test.png");

    assertTrue(resource.toString().contains("Resource"));
  }

  @Test
  public void multipleSetWidth_lastWins() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    resource.setWidth(100);
    resource.setWidth(200);

    assertEquals(200, resource.getWidth());
  }

  @Test
  public void multipleSetHeight_lastWins() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    resource.setHeight(100);
    resource.setHeight(200);

    assertEquals(200, resource.getHeight());
  }

  @Test
  public void getName_initiallyNull() {
    assertNull(new ImageResource(UUID.randomUUID()).getName());
  }

  @Test
  public void nameListeners_emptyByDefault() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    assertNotNull(resource.getNameListeners());
    assertTrue(resource.getNameListeners().isEmpty());
  }

  @Test
  public void contentListeners_emptyByDefault() {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    assertNotNull(resource.getContentListeners());
    assertTrue(resource.getContentListeners().isEmpty());
  }
}

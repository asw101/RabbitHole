package org.lgna.common.resources;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ImageResourceTest {

  @Test
  public void getContentType_png() {
    assertEquals("image/png", ImageResource.getContentType("test.png"));
  }

  @Test
  public void getContentType_jpg() {
    assertEquals("image/jpeg", ImageResource.getContentType("test.jpg"));
  }

  @Test
  public void getContentType_gif() {
    assertEquals("image/gif", ImageResource.getContentType("test.gif"));
  }

  @Test
  public void getContentType_bmp() {
    assertEquals("image/bmp", ImageResource.getContentType("test.bmp"));
  }

  @Test
  public void getContentType_tga() {
    assertEquals("image/tga", ImageResource.getContentType("test.tga"));
  }

  @Test
  public void getContentType_unknown() {
    assertNull(ImageResource.getContentType("test.xyz"));
  }

  @Test
  public void getContentType_caseInsensitive() {
    assertEquals("image/png", ImageResource.getContentType("TEST.PNG"));
  }

  @Test
  public void createFilenameFilter_isNotNull() {
    assertNotNull(ImageResource.createFilenameFilter(false));
    assertNotNull(ImageResource.createFilenameFilter(true));
  }

  @Test
  public void valueOf_createsResource() {
    UUID id = UUID.randomUUID();
    ImageResource r = ImageResource.valueOf(id.toString());
    assertNotNull(r);
    assertEquals(id, r.getId());
  }

  @Test
  public void constructor_uuid() {
    UUID id = UUID.randomUUID();
    ImageResource r = new ImageResource(id);
    assertEquals(id, r.getId());
  }

  @Test
  public void widthAndHeight_setAndGet() {
    ImageResource r = new ImageResource(UUID.randomUUID());
    r.setWidth(800);
    r.setHeight(600);
    assertEquals(800, r.getWidth());
    assertEquals(600, r.getHeight());
  }
}

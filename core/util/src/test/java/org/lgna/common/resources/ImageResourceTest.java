package org.lgna.common.resources;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
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

  @Test
  public void widthAndHeight_defaultMinusOne() {
    ImageResource r = new ImageResource(UUID.randomUUID());
    assertEquals(-1, r.getWidth());
    assertEquals(-1, r.getHeight());
  }

  @Test
  public void encodeAttributes_includesWidthHeight() throws Exception {
    ImageResource r = new ImageResource(UUID.randomUUID());
    r.setName("pic.png");
    r.setOriginalFileName("pic.png");
    r.setContent("image/png", new byte[]{1, 2, 3});
    r.setWidth(1024);
    r.setHeight(768);

    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element elem = doc.createElement("image");
    r.encodeAttributes(elem);

    assertEquals("1024", elem.getAttribute("width"));
    assertEquals("768", elem.getAttribute("height"));
    assertEquals("pic.png", elem.getAttribute("name"));
    assertEquals("image/png", elem.getAttribute("contentType"));
  }

  @Test
  public void decodeAttributes_restoresWidthHeight() throws Exception {
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element elem = doc.createElement("image");
    elem.setAttribute("name", "photo.jpg");
    elem.setAttribute("originalFileName", "photo.jpg");
    elem.setAttribute("contentType", "image/jpeg");
    elem.setAttribute("width", "640");
    elem.setAttribute("height", "480");

    ImageResource r = new ImageResource(UUID.randomUUID());
    r.decodeAttributes(elem, new byte[]{10});
    assertEquals("photo.jpg", r.getName());
    assertEquals("image/jpeg", r.getContentType());
    assertEquals(640, r.getWidth());
    assertEquals(480, r.getHeight());
  }

  @Test
  public void valueOf_sameUuid_returnsCached() {
    UUID id = UUID.randomUUID();
    ImageResource r1 = ImageResource.valueOf(id.toString());
    ImageResource r2 = ImageResource.valueOf(id.toString());
    assertSame(r1, r2);
  }
}

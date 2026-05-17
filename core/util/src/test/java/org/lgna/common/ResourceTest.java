package org.lgna.common;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ResourceTest {

  @Test
  public void getId_returnsUUID() {
    UUID id = UUID.randomUUID();
    TestResource r = new TestResource(id);
    assertEquals(id, r.getId());
  }

  @Test
  public void name_setAndGet() {
    TestResource r = new TestResource(UUID.randomUUID());
    r.setName("myResource");
    assertEquals("myResource", r.getName());
  }

  @Test
  public void contentType_returnsSet() {
    TestResource r = new TestResource("test.txt", "text/plain", new byte[]{1, 2, 3});
    assertEquals("text/plain", r.getContentType());
  }

  @Test
  public void data_returnsSetData() {
    byte[] data = {10, 20, 30};
    TestResource r = new TestResource("test.dat", "application/octet-stream", data);
    assertArrayEquals(data, r.getData());
  }

  @Test
  public void setContent_updatesTypeAndData() {
    TestResource r = new TestResource(UUID.randomUUID());
    byte[] newData = {1, 2, 3, 4, 5};
    r.setContent("text/html", newData);
    assertEquals("text/html", r.getContentType());
    assertArrayEquals(newData, r.getData());
  }

  @Test
  public void originalFileName_setAndGet() {
    TestResource r = new TestResource(UUID.randomUUID());
    r.setOriginalFileName("original.txt");
    assertEquals("original.txt", r.getOriginalFileName());
  }

  @Test
  public void toString_containsClassName() {
    TestResource r = new TestResource(UUID.randomUUID());
    r.setName("hello");
    String s = r.toString();
    assertNotNull(s);
    assertTrue(s.contains("hello"));
  }

  // Concrete subclass for testing the abstract Resource
  static class TestResource extends Resource {
    public TestResource(UUID uuid) {
      super(uuid);
    }

    public TestResource(String fileName, String contentType, byte[] data) {
      super(fileName, contentType, data);
    }
  }
}

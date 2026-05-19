package org.lgna.common.event;

import org.lgna.common.Resource;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class ResourceContentEventTest {

  @Test
  public void contentType_isReturned() {
    Resource resource = new TestResource();
    ResourceContentEvent event = new ResourceContentEvent(resource, "image/png", new byte[]{1, 2, 3});
    assertEquals("image/png", event.getContentType());
  }

  @Test
  public void data_isReturned() {
    Resource resource = new TestResource();
    byte[] data = {10, 20, 30, 40};
    ResourceContentEvent event = new ResourceContentEvent(resource, "audio/wav", data);
    assertSame(data, event.getData());
    assertEquals(4, event.getData().length);
  }

  @Test
  public void source_isResource() {
    Resource resource = new TestResource();
    ResourceContentEvent event = new ResourceContentEvent(resource, "text/plain", new byte[0]);
    assertSame(resource, event.getSource());
    assertSame(resource, event.getTypedSource());
  }

  @Test
  public void nullContentType() {
    Resource resource = new TestResource();
    ResourceContentEvent event = new ResourceContentEvent(resource, null, null);
    assertNull(event.getContentType());
    assertNull(event.getData());
  }

  @Test
  public void emptyData() {
    Resource resource = new TestResource();
    ResourceContentEvent event = new ResourceContentEvent(resource, "application/octet-stream", new byte[0]);
    assertEquals(0, event.getData().length);
  }

  @Test
  public void isReservedForReuse_defaultFalse() {
    Resource resource = new TestResource();
    ResourceContentEvent event = new ResourceContentEvent(resource, "text/html", new byte[]{1});
    assertFalse(event.isReservedForReuse());
  }

  private static class TestResource extends Resource {
    TestResource() {
      super(UUID.randomUUID());
    }
  }
}

package org.lgna.common;

import edu.cmu.cs.dennisc.pattern.event.NameEvent;
import edu.cmu.cs.dennisc.pattern.event.NameListener;
import org.junit.Test;
import org.lgna.common.event.ResourceContentEvent;
import org.lgna.common.event.ResourceContentListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

  @Test
  public void getName_beforeSet_isNull() {
    TestResource r = new TestResource(UUID.randomUUID());
    assertNull(r.getName());
  }

  @Test
  public void setName_sameName_noEvent() {
    TestResource r = new TestResource(UUID.randomUUID());
    r.setName("same");
    List<String> changes = new ArrayList<>();
    r.addNameListener(new NameListener() {
      @Override public void nameChanging(NameEvent e) { changes.add("changing"); }
      @Override public void nameChanged(NameEvent e) { changes.add("changed"); }
    });
    r.setName("same");
    assertTrue("No events should fire for same name", changes.isEmpty());
  }

  @Test
  public void setName_differentName_firesEvents() {
    TestResource r = new TestResource(UUID.randomUUID());
    r.setName("old");
    List<String> events = new ArrayList<>();
    r.addNameListener(new NameListener() {
      @Override public void nameChanging(NameEvent e) {
        events.add("changing:" + e.getPreviousName() + "->" + e.getNextName());
      }
      @Override public void nameChanged(NameEvent e) {
        events.add("changed:" + e.getPreviousName() + "->" + e.getNextName());
      }
    });
    r.setName("new");
    assertEquals(2, events.size());
    assertEquals("changing:old->new", events.get(0));
    assertEquals("changed:old->new", events.get(1));
  }

  @Test
  public void removeNameListener_stopsEvents() {
    TestResource r = new TestResource(UUID.randomUUID());
    List<String> events = new ArrayList<>();
    NameListener listener = new NameListener() {
      @Override public void nameChanging(NameEvent e) { events.add("changing"); }
      @Override public void nameChanged(NameEvent e) { events.add("changed"); }
    };
    r.addNameListener(listener);
    r.setName("first");
    assertEquals(2, events.size());
    r.removeNameListener(listener);
    r.setName("second");
    assertEquals(2, events.size());
  }

  @Test
  public void getNameListeners_returnsListeners() {
    TestResource r = new TestResource(UUID.randomUUID());
    NameListener listener = new NameListener() {
      @Override public void nameChanging(NameEvent e) {}
      @Override public void nameChanged(NameEvent e) {}
    };
    r.addNameListener(listener);
    Collection<NameListener> listeners = r.getNameListeners();
    assertEquals(1, listeners.size());
    assertTrue(listeners.contains(listener));
  }

  @Test
  public void setContent_firesContentEvents() {
    TestResource r = new TestResource(UUID.randomUUID());
    List<String> events = new ArrayList<>();
    r.addContentListener(new ResourceContentListener() {
      @Override public void contentChanging(ResourceContentEvent e) { events.add("changing"); }
      @Override public void contentChanged(ResourceContentEvent e) { events.add("changed"); }
    });
    r.setContent("text/plain", new byte[]{1});
    assertEquals(2, events.size());
    assertEquals("changing", events.get(0));
    assertEquals("changed", events.get(1));
  }

  @Test
  public void removeContentListener_stopsEvents() {
    TestResource r = new TestResource(UUID.randomUUID());
    List<String> events = new ArrayList<>();
    ResourceContentListener listener = new ResourceContentListener() {
      @Override public void contentChanging(ResourceContentEvent e) { events.add("x"); }
      @Override public void contentChanged(ResourceContentEvent e) { events.add("x"); }
    };
    r.addContentListener(listener);
    r.setContent("a", new byte[]{1});
    assertEquals(2, events.size());
    r.removeContentListener(listener);
    r.setContent("b", new byte[]{2});
    assertEquals(2, events.size());
  }

  @Test
  public void getContentListeners_returnsListeners() {
    TestResource r = new TestResource(UUID.randomUUID());
    ResourceContentListener listener = new ResourceContentListener() {
      @Override public void contentChanging(ResourceContentEvent e) {}
      @Override public void contentChanged(ResourceContentEvent e) {}
    };
    r.addContentListener(listener);
    Collection<ResourceContentListener> listeners = r.getContentListeners();
    assertEquals(1, listeners.size());
  }

  @Test
  public void encodeDecodeAttributes_roundTrip() throws Exception {
    TestResource r = new TestResource("file.txt", "text/plain", new byte[]{10, 20});
    r.setOriginalFileName("orig.txt");

    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element elem = doc.createElement("resource");
    r.encodeAttributes(elem);

    assertEquals("file.txt", elem.getAttribute("name"));
    assertEquals("orig.txt", elem.getAttribute("originalFileName"));
    assertEquals("text/plain", elem.getAttribute("contentType"));

    TestResource r2 = new TestResource(UUID.randomUUID());
    byte[] newData = {30, 40};
    r2.decodeAttributes(elem, newData);
    assertEquals("file.txt", r2.getName());
    assertEquals("orig.txt", r2.getOriginalFileName());
    assertEquals("text/plain", r2.getContentType());
    assertArrayEquals(newData, r2.getData());
  }

  @Test
  public void toString_containsUUIDAndContentType() {
    TestResource r = new TestResource("f.dat", "application/octet-stream", new byte[]{1});
    String s = r.toString();
    assertTrue(s.contains("application/octet-stream"));
    assertTrue(s.contains(r.getId().toString()));
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

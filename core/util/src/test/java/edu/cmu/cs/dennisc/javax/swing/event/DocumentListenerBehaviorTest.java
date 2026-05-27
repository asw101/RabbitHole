package edu.cmu.cs.dennisc.javax.swing.event;

import org.junit.jupiter.api.Test;

import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentListenerBehaviorTest {
  private record StubDocumentEvent(Document document, EventType type, int offset, int length) implements DocumentEvent {
    @Override
    public Document getDocument() {
      return document;
    }

    @Override
    public int getOffset() {
      return offset;
    }

    @Override
    public int getLength() {
      return length;
    }

    @Override
    public EventType getType() {
      return type;
    }

    @Override
    public ElementChange getChange(Element elem) {
      return null;
    }
  }

  @Test
  void unifiedDocumentListenerRoutesAllDocumentEvents() throws Exception {
    PlainDocument document = new PlainDocument();
    List<String> events = new ArrayList<>();
    UnifiedDocumentListener listener = new UnifiedDocumentListener((event) ->
        events.add(event.getType() + ":" + event.getOffset() + ":" + event.getLength()));
    document.addDocumentListener(listener);

    document.insertString(0, "abc", null);
    document.remove(1, 1);
    listener.changedUpdate(new StubDocumentEvent(document, DocumentEvent.EventType.CHANGE, 0, 2));

    assertEquals(List.of("INSERT:0:3", "REMOVE:1:1", "CHANGE:0:2"), events);
  }

  @Test
  void unifiedDocumentListenerRunnableConstructorRunsForAllCallbacks() {
    PlainDocument document = new PlainDocument();
    AtomicInteger updates = new AtomicInteger();
    UnifiedDocumentListener listener = new UnifiedDocumentListener(updates::incrementAndGet);
    document.addDocumentListener(listener);

    listener.insertUpdate(new StubDocumentEvent(document, DocumentEvent.EventType.INSERT, 0, 1));
    listener.removeUpdate(new StubDocumentEvent(document, DocumentEvent.EventType.REMOVE, 0, 1));
    listener.changedUpdate(new StubDocumentEvent(document, DocumentEvent.EventType.CHANGE, 0, 1));

    assertEquals(3, updates.get());
  }

  @Test
  void pausableDocumentListenerSuppressesEventsUntilNestedPausesResume() throws Exception {
    PlainDocument document = new PlainDocument();
    List<String> events = new ArrayList<>();
    PausableDocumentListener listener = new PausableDocumentListener((event) ->
        events.add(event.getType() + ":" + event.getOffset() + ":" + event.getLength()));
    document.addDocumentListener(listener);

    document.insertString(0, "A", null);
    listener.pause();
    document.insertString(1, "B", null);
    listener.pause();
    document.insertString(2, "C", null);
    listener.resume();
    document.insertString(3, "D", null);
    listener.resume();
    document.insertString(4, "E", null);

    assertEquals(List.of("INSERT:0:1", "INSERT:4:1"), events);
  }
}

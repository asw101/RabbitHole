package org.lgna.croquet.triggers;

import org.junit.Test;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class DocumentEventTriggerBehaviorTest extends TriggerTestSupport {
  @Test
  public void createUserInstance_setsTriggerOnActivity_andShowPopupMenuThrowsTodo() throws BadLocationException {
    AtomicReference<DocumentEvent> ref = new AtomicReference<>();
    PlainDocument document = new PlainDocument();
    document.addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        ref.set(e);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
      }
    });
    document.insertString(0, "x", null);

    DocumentEventTrigger trigger = DocumentEventTrigger.createUserInstance(ref.get());

    assertSame(trigger, trigger.getUserActivity().getTrigger());
    RuntimeException exception = assertThrows(RuntimeException.class, () -> trigger.showPopupMenu(null));
    assertEquals("todo", exception.getMessage());
  }
}

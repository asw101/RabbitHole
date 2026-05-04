package org.alice.netbeans.palette.items;

import org.junit.Test;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AliceComponentPaletteUtilitiesTest {

  @Test
  public void insertFormattedPropagatesBadLocationException() throws Exception {
    JTextPane target = new JTextPane();
    Document document = new RemoveFailsDocument();
    target.setDocument((DefaultStyledDocument) document);
    document.insertString(0, "class Scene {}", null);
    target.setCaretPosition(6);
    Method method = AliceComponentPaletteUtilities.class.getDeclaredMethod(
        "insertFormated",
        String.class,
        JTextComponent.class,
        Document.class);
    method.setAccessible(true);

    try {
      method.invoke(null, "Inserted", target, document);
      fail("Expected BadLocationException");
    } catch (InvocationTargetException expected) {
      assertTrue(expected.getCause() instanceof BadLocationException);
      assertTrue(expected.getCause().getMessage().contains("remove failed"));
    }
  }

  private static final class RemoveFailsDocument extends DefaultStyledDocument {
    @Override
    public void remove(int offs, int len) throws BadLocationException {
      throw new BadLocationException("remove failed", offs);
    }
  }
}

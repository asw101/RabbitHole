package org.alice.netbeans.completion;

import org.junit.Assert;
import org.junit.Test;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;

import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Alice3CompletionBehaviorTest {
  @Test
  public void completionItemRendersMetadataAndReplacesTypedPrefix() throws Exception {
    Alice3CompletionItem item = new Alice3CompletionItem("doTogether", "doTogether();", 0, 3, 7);
    JTextPane component = new JTextPane();
    StyledDocument document = new DefaultStyledDocument();
    component.setDocument(document);
    document.insertString(0, "doT", null);

    BufferedImage canvas = new BufferedImage(160, 30, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = canvas.createGraphics();
    Font font = component.getFont();
    Assert.assertTrue(item.getPreferredWidth(graphics, font) > 0);
    item.render(graphics, font, Color.BLACK, Color.WHITE, 160, 30, false);
    graphics.dispose();

    Assert.assertEquals("doTogether", item.getSortText());
    Assert.assertEquals("doTogether", item.getInsertPrefix());
    Assert.assertEquals(7, item.getSortPriority());
    Assert.assertNull(item.createDocumentationTask());
    Assert.assertNull(item.createToolTipTask());
    Assert.assertFalse(item.instantSubstitution(component));
    item.processKeyEvent(new KeyEvent(component, KeyEvent.KEY_TYPED, 1L, 0, KeyEvent.VK_UNDEFINED, 'd'));

    item.defaultAction(component);
    Assert.assertEquals("doTogether();", document.getText(0, document.getLength()));
  }

  @Test
  public void completionProviderComputesIndentationAndCreatesCompletionTasks() throws Exception {
    Alice3CompletionProvider provider = new Alice3CompletionProvider();
    DefaultStyledDocument document = new DefaultStyledDocument();
    document.insertString(0, "    doT", null);

    int firstNonWhite = (Integer) invokeStatic(Alice3CompletionProvider.class, "getRowFirstNonWhite", new Class<?>[] {StyledDocument.class, int.class}, document, document.getLength());
    int whiteIndex = (Integer) invokeStatic(Alice3CompletionProvider.class, "indexOfWhite", new Class<?>[] {char[].class}, new char[] {'d', 'o', ' ', 'T'});

    Assert.assertEquals(4, firstNonWhite);
    Assert.assertEquals(2, whiteIndex);
    Assert.assertNull(provider.createTask(0, new JTextPane()));
    CompletionTask task = provider.createTask(CompletionProvider.COMPLETION_QUERY_TYPE, new JTextPane());
    Assert.assertNotNull(task);
    Assert.assertEquals(0, provider.getAutoQueryTypes(new JTextPane(), "d"));
  }

  private static Object invokeStatic(Class<?> type, String name, Class<?>[] parameterTypes, Object... args) {
    try {
      var method = type.getDeclaredMethod(name, parameterTypes);
      method.setAccessible(true);
      return method.invoke(null, args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

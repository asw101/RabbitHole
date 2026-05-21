package org.alice.netbeans.palette.items;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

public class AliceComponentPaletteUtilitiesBehaviorTest {
  @Test
  public void redundantImportDetectionHandlesExactAndWildcardMatches() {
    Assert.assertTrue(AliceComponentPaletteUtilities.isImportRedundant("java.util.List", "java.util.List"));
    Assert.assertTrue(AliceComponentPaletteUtilities.isImportRedundant("java.util.*", "java.util.List"));
    Assert.assertFalse(AliceComponentPaletteUtilities.isImportRedundant("java.io.*", "java.util.List"));
    Assert.assertFalse(AliceComponentPaletteUtilities.isImportRedundant("java.util.Map", "java.util.List"));
  }

  @Test
  public void insertRejectsDocumentsWithoutJavaSourceAssociation() {
    JTextPane target = new JTextPane();

    BadLocationException error = org.junit.Assert.assertThrows(
        BadLocationException.class,
        () -> AliceComponentPaletteUtilities.insert("System.out.println();", null, target));

    Assert.assertTrue(error.getMessage().contains("No Java source"));
    Assert.assertEquals(target.getCaretPosition(), error.offsetRequested());
  }
}

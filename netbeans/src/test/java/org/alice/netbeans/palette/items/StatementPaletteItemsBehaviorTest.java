package org.alice.netbeans.palette.items;

import org.junit.Assert;
import org.junit.Test;

public class StatementPaletteItemsBehaviorTest {
  @Test
  public void doInOrderUsesLocalizedTemplateBody() {
    String body = createBody(new DoInOrder());

    Assert.assertTrue(body.startsWith("\n/*DoInOrder*/ {"));
    Assert.assertTrue(body.contains("\t//TODO: Code goes here."));
    Assert.assertTrue(body.endsWith("}\n"));
    Assert.assertEquals(1, countOccurrences(body, "//TODO: Code goes here."));
  }

  @Test
  public void ifStatementUsesIfElseTemplate() {
    String body = createBody(new IfStatement());

    Assert.assertTrue(body.contains("if (replace_with_BOOLEAN_EXPRESSION) {"));
    Assert.assertTrue(body.contains("} else {"));
    Assert.assertEquals(2, countOccurrences(body, "//TODO: Code goes here."));
  }

  @Test
  public void whileLoopUsesLoopTemplate() {
    String body = createBody(new WhileLoop());

    Assert.assertTrue(body.contains("while (replace_with_BOOLEAN_EXPRESSION) {"));
    Assert.assertEquals(1, countOccurrences(body, "//TODO: Code goes here."));
    Assert.assertTrue(body.endsWith("}\n"));
  }

  @Test
  public void forAllTogetherAddsStaticImportAndParallelTemplate() {
    ForAllTogether drop = new ForAllTogether();
    String body = createBody(drop);

    Assert.assertArrayEquals(
        new String[] {"static org.lgna.common.ThreadUtilities.eachInTogether"},
        drop.getImports());
    Assert.assertTrue(body.contains("eachInTogether( ( replace_with_CLASS_NAME item ) -> {"));
    Assert.assertTrue(body.contains("replace_with_ARRAY_OF_ITEMS"));
    Assert.assertEquals(1, countOccurrences(body, "//TODO: Code goes here"));
  }

  private static String createBody(AbstractActiveEditorDrop drop) {
    try {
      java.lang.reflect.Method method = AbstractActiveEditorDrop.class.getDeclaredMethod("createBody");
      method.setAccessible(true);
      return (String) method.invoke(drop);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static int countOccurrences(String text, String token) {
    int count = 0;
    int index = 0;
    while ((index = text.indexOf(token, index)) >= 0) {
      count++;
      index += token.length();
    }
    return count;
  }
}

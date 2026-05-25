package org.alice.netbeans.palette.items;

import org.junit.Assert;
import org.junit.Test;

public class DoTogetherBehaviorTest {
  @Test
  public void createBodyMatchesRunnableCountAndKeepsTemplateComments() throws Exception {
    DoTogether doTogether = new DoTogether();
    Assert.assertEquals(2, doTogether.getRunnableCount());

    doTogether.setRunnableCount(3);
    Assert.assertEquals(3, doTogether.getRunnableCount());

    String body = (String) invoke(doTogether, "createBody");
    Assert.assertTrue(body.contains("//start a Thread for each Runnable and wait until they complete"));
    Assert.assertTrue(body.contains("doTogether( ()-> {"));
    Assert.assertEquals(3, countOccurrences(body, "//TODO: Code goes here"));
    Assert.assertEquals(2, countOccurrences(body, "}, ()-> {"));
  }

  private static Object invoke(Object target, String name) {
    try {
      var method = DoTogether.class.getDeclaredMethod(name);
      method.setAccessible(true);
      return method.invoke(target);
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

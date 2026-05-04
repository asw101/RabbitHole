package org.alice.netbeans.completion;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class Alice3CompletionItemTest {

  @Test
  public void completionIconResourceIsPackagedOnClasspath() {
    assertNotNull(
        Alice3CompletionItem.FIELD_ICON_RESOURCE,
        Thread.currentThread().getContextClassLoader().getResource(Alice3CompletionItem.FIELD_ICON_RESOURCE));
  }
}

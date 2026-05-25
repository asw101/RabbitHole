package org.alice.netbeans.palette.items;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.JTextPane;

public class AbstractActiveEditorDropBehaviorTest {
  @Test
  public void handleTransferReturnsFalseWhenPrologueRejectsDrop() {
    Assert.assertFalse(new RejectingDrop().handleTransfer(new JTextPane()));
  }

  @Test
  public void handleTransferBuildsBodyAndReportsInsertFailures() throws Exception {
    CountLoop drop = new CountLoop();
    String body = (String) invoke(AbstractActiveEditorDrop.class, drop, "createBody");

    Assert.assertTrue(body.contains("for(Integer i=0; i<replace_with_COUNT; i++)"));
    Assert.assertFalse(drop.handleTransfer(new JTextPane()));
  }

  private static Object invoke(Class<?> type, Object target, String name) {
    try {
      var method = type.getDeclaredMethod(name);
      method.setAccessible(true);
      return method.invoke(target);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static final class RejectingDrop extends AbstractActiveEditorDrop {
    @Override
    protected boolean prologue(javax.swing.text.JTextComponent targetComponent) {
      return false;
    }
  }

  private static final class CountLoop extends AbstractActiveEditorDrop {
    @Override
    protected boolean prologue(javax.swing.text.JTextComponent targetComponent) {
      return true;
    }
  }
}

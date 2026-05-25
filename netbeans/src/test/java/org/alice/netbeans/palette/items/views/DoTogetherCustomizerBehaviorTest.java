package org.alice.netbeans.palette.items.views;

import org.alice.netbeans.palette.items.DoTogether;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;

public class DoTogetherCustomizerBehaviorTest {
  @Test
  public void evaluateInputWritesSpinnerValueBackToDoTogetherDrop() throws Exception {
    DoTogether doTogether = new DoTogether();
    DoTogetherCustomizer customizer = new DoTogetherCustomizer(doTogether, new JTextPane());
    JSpinner spinner = (JSpinner) readField(customizer, "jSpinner1");
    SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();

    Assert.assertEquals(2, model.getNumber().intValue());
    Assert.assertEquals(2, ((Number) model.getMinimum()).intValue());
    Assert.assertEquals(100, ((Number) model.getMaximum()).intValue());

    spinner.setValue(4);
    invoke(customizer, "evaluateInput");
    Assert.assertEquals(4, doTogether.getRunnableCount());
  }

  private static Object readField(Object target, String name) {
    try {
      var field = DoTogetherCustomizer.class.getDeclaredField(name);
      field.setAccessible(true);
      return field.get(target);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Object invoke(Object target, String name) {
    try {
      var method = DoTogetherCustomizer.class.getDeclaredMethod(name);
      method.setAccessible(true);
      return method.invoke(target);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

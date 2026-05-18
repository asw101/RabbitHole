package org.alice.ide.croquet.models.ui.formatter;

import org.alice.ide.formatter.AliceFormatter;
import org.junit.Test;

import static org.junit.Assert.*;

public class FormatterStateTest {
  @Test
  public void getInstanceReturnsSingleton() {
    assertSame(FormatterState.getInstance(), FormatterState.getInstance());
  }

  @Test
  public void initialValueIsAliceFormatter() {
    assertSame(AliceFormatter.getInstance(), FormatterState.getInstance().getValue());
  }

  @Test
  public void isJavaIsFalseByDefault() {
    assertFalse(FormatterState.isJava());
  }
}

package org.alice.ide.common;

import org.alice.ide.croquet.models.ui.formatter.FormatterState;
import org.alice.ide.formatter.AliceFormatter;
import org.alice.ide.formatter.Formatter;
import org.alice.ide.formatter.JavaFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TypeNameComputationTest {
  private static final JavaType STRING_TYPE = JavaType.getInstance(String.class);

  private Formatter previousFormatter;

  @Before
  public void rememberFormatter() {
    previousFormatter = FormatterState.getInstance().getValue();
  }

  @After
  public void restoreFormatter() throws Exception {
    setFormatterWithoutSwing(previousFormatter);
  }

  @Test
  public void typeIconComputesTypeTextFromCurrentFormatterState() throws Exception {
    TypeIcon icon = new TypeIcon(STRING_TYPE);

    setFormatterWithoutSwing(AliceFormatter.getInstance());
    String aliceText = invokeTypeText(icon);

    setFormatterWithoutSwing(JavaFormatter.getInstance());
    String javaText = invokeTypeText(icon);

    assertEquals(AliceFormatter.getInstance().getTextForType(STRING_TYPE), aliceText);
    assertEquals(JavaFormatter.getInstance().getTextForType(STRING_TYPE), javaText);
    assertNotEquals(aliceText, javaText);
  }

  @Test
  public void typeIconComputesNullTextFromFormatterState() throws Exception {
    TypeIcon icon = new TypeIcon(null);

    setFormatterWithoutSwing(AliceFormatter.getInstance());
    assertEquals(AliceFormatter.getInstance().getTextForNull(), invokeTypeText(icon));

    setFormatterWithoutSwing(JavaFormatter.getInstance());
    assertEquals(JavaFormatter.getInstance().getTextForNull(), invokeTypeText(icon));
  }

  private static void setFormatterWithoutSwing(Formatter formatter) throws Exception {
    FormatterState state = FormatterState.getInstance();
    Method setCurrentTruthAndBeautyValue = Class.forName("org.lgna.croquet.SingleSelectListState")
        .getDeclaredMethod("setCurrentTruthAndBeautyValue", Object.class);
    setCurrentTruthAndBeautyValue.setAccessible(true);
    setCurrentTruthAndBeautyValue.invoke(state, formatter);

    Field previousValue = Class.forName("org.lgna.croquet.State").getDeclaredField("previousValue");
    previousValue.setAccessible(true);
    previousValue.set(state, formatter);
  }

  private static String invokeTypeText(TypeIcon icon) throws Exception {
    Method method = TypeIcon.class.getDeclaredMethod("getTypeText");
    method.setAccessible(true);
    return (String) method.invoke(icon);
  }
}

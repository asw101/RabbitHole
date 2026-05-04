package org.alice.netbeans.palette.items;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;

public class CountLoopTest {

  @Test
  public void epilogueTreatsJavaIdentifierAsLiteralReplacement() throws Exception {
    CountLoop countLoop = new CountLoop();
    Field counterVariableName = CountLoop.class.getDeclaredField("counterVariableName");
    counterVariableName.setAccessible(true);
    counterVariableName.set(countLoop, "$index");

    String result = countLoop.epilogue("for(Integer i=0; i<replace_with_COUNT; i++) {");

    assertTrue(result, result.contains("Integer $index=0"));
  }
}

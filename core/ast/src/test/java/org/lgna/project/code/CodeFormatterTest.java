package org.lgna.project.code;

import org.junit.Test;

import static org.junit.Assert.*;

public class CodeFormatterTest {
  @Test
  public void formatsSimpleAssignment() {
    assertEquals("value = 1;\n", CodeFormatter.format("value=1;"));
  }

  @Test
  public void formatWithExplicitZeroIndentMatchesDefaultOverload() {
    assertEquals(CodeFormatter.format("value=1;"), CodeFormatter.format("value=1;", 0));
  }

  @Test
  public void appliesRequestedIndentLevelToFirstLine() {
    assertEquals("\t\tvalue = 1;\n", CodeFormatter.format("value=1;", 2));
  }

  @Test
  public void insertsNewlinesAroundBlocks() {
    String formatted = CodeFormatter.format("if(a){b();}");

    assertTrue(formatted.contains("if( a ) {"));
    assertTrue(formatted.contains("\tb();"));
    assertTrue(formatted.contains("\n }\n") || formatted.endsWith(" }\n"));
  }

  @Test
  public void removesWhitespaceBeforeElse() {
    String formatted = CodeFormatter.format("if(a){b();}else{c();}");

    assertTrue(formatted.contains("} else {") || formatted.contains(" } else {"));
  }

  @Test
  public void keepsEqualityAndRelationalOperatorsIntact() {
    String formatted = CodeFormatter.format("if(a==b){c();}if(a<=b){c();}if(a>=b){c();}");

    assertTrue(formatted.contains("a == b"));
    assertTrue(formatted.contains("a <= b"));
    assertTrue(formatted.contains("a >= b"));
  }

  @Test
  public void formatsLambdaArrowWithoutBreakingToken() {
    String formatted = CodeFormatter.format("callback=(x)->x+1;");

    assertTrue(formatted.contains("callback = ( x ) ->x+1;") || formatted.contains("callback = ( x ) -> x+1;"));
    assertFalse(formatted.contains("- >"));
  }

  @Test
  public void formatsBooleanOperatorsWithSpaces() {
    String formatted = CodeFormatter.format("if(a&&b||c){go();}");

    assertTrue(formatted.contains("a && b || c"));
  }

  @Test
  public void formatsCommasAndColons() {
    String formatted = CodeFormatter.format("call(a,b);case 1:go();");

    assertTrue(formatted.contains("call( a, b )"));
    assertTrue(formatted.contains("case 1 : go()"));
  }

  @Test
  public void removesExtraSpacesAndKeepsEmptyParensTight() {
    String formatted = CodeFormatter.format("  call(   );  ");

    assertEquals("call();\n", formatted);
  }

  @Test
  public void collapsesRepeatedWhitespaceAtLineStarts() {
    String formatted = CodeFormatter.format("if(a){   b();   }");

    assertFalse(formatted.contains("  "));
  }

  @Test
  public void keepsForLoopHeaderOnOneLine() {
    String formatted = CodeFormatter.format("for(int i=0;i<3;i++){go();}");

    assertTrue(formatted.contains("for( int i = 0; i < 3; i++ )"));
    assertFalse(formatted.contains("for( int i = 0;\n"));
  }

  @Test
  public void appendsNewlineAfterStatements() {
    assertTrue(CodeFormatter.format("first();second();").endsWith("\n"));
  }

  @Test
  public void formatsOverrideAnnotationOntoItsOwnLine() {
    String formatted = CodeFormatter.format("@Override public void run(){}", 1);

    assertTrue(formatted.contains("@Override\n\tpublic void run()"));
  }

  @Test
  public void emptyInputFormatsToEmptyString() {
    assertEquals("", CodeFormatter.format("   "));
  }
}

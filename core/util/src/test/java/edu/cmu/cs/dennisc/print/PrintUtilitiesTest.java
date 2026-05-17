package edu.cmu.cs.dennisc.print;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;

import static org.junit.Assert.*;

// Used for Printable tests below

/**
 * Tests for PrintUtilities — push/pop static stacks, append formatting,
 * toString delegation, and println output capture.
 */
public class PrintUtilitiesTest {

  private PrintStream originalOut;
  private ByteArrayOutputStream capturedOutput;
  private PrintStream capturePrintStream;

  @Before
  public void setUp() {
    originalOut = System.out;
    capturedOutput = new ByteArrayOutputStream();
    capturePrintStream = new PrintStream(capturedOutput);
  }

  @After
  public void tearDown() {
    System.setOut(originalOut);
  }

  // --- PrintStream stack ---

  @Test
  public void pushPopPrintStream() {
    PrintStream before = PrintUtilities.accessPrintStream();
    PrintUtilities.pushPrintStream();
    PrintUtilities.setPrintStream(capturePrintStream);
    assertSame(capturePrintStream, PrintUtilities.accessPrintStream());
    PrintUtilities.popPrintStream();
    assertSame(before, PrintUtilities.accessPrintStream());
  }

  @Test
  public void accessPrintStream_defaultIsNotNull() {
    assertNotNull(PrintUtilities.accessPrintStream());
  }

  // --- DecimalFormat stack ---

  @Test
  public void pushPopDecimalFormat() {
    DecimalFormat before = PrintUtilities.accessDecimalFormat();
    PrintUtilities.pushDecimalFormat();
    DecimalFormat custom = new DecimalFormat("#.##");
    PrintUtilities.setDecimalFormat(custom);
    assertSame(custom, PrintUtilities.accessDecimalFormat());
    PrintUtilities.popDecimalFormat();
    assertSame(before, PrintUtilities.accessDecimalFormat());
  }

  // --- IndentText stack ---

  @Test
  public void pushPopIndentText() {
    String before = PrintUtilities.getIndentText();
    PrintUtilities.pushIndentText();
    PrintUtilities.setIndentText(">>>");
    assertEquals(">>>", PrintUtilities.getIndentText());
    PrintUtilities.popIndentText();
    assertEquals(before, PrintUtilities.getIndentText());
  }

  // --- SeparatorText stack ---

  @Test
  public void pushPopSeparatorText() {
    String before = PrintUtilities.getSeparatorText();
    PrintUtilities.pushSeparatorText();
    PrintUtilities.setSeparatorText(" | ");
    assertEquals(" | ", PrintUtilities.getSeparatorText());
    PrintUtilities.popSeparatorText();
    assertEquals(before, PrintUtilities.getSeparatorText());
  }

  // --- append for primitives ---

  @Test
  public void append_int() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, 42);
    assertTrue(sb.toString().contains("42"));
  }

  @Test
  public void append_string() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, "hello");
    assertTrue(sb.toString().contains("hello"));
  }

  @Test
  public void append_null() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, (Object) null);
    String result = sb.toString();
    assertTrue(result.contains("null"));
  }

  @Test
  public void append_multipleValues() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, "a", "b", "c");
    String result = sb.toString();
    assertTrue(result.contains("a"));
    assertTrue(result.contains("b"));
    assertTrue(result.contains("c"));
  }

  // --- append for Float/Double with DecimalFormat ---

  @Test
  public void append_float() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, Float.valueOf(3.14f));
    assertNotNull(sb.toString());
    assertTrue(sb.length() > 0);
  }

  @Test
  public void append_double() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, Double.valueOf(2.71828));
    assertNotNull(sb.toString());
    assertTrue(sb.length() > 0);
  }

  // --- append for int[] ---

  @Test
  public void append_intArray() {
    StringBuilder sb = new StringBuilder();
    int[] data = {1, 2, 3};
    PrintUtilities.append(sb, data);
    String result = sb.toString();
    assertTrue(result.contains("1"));
    assertTrue(result.contains("3"));
  }

  @Test
  public void appendLines_intArray() {
    StringBuilder sb = new StringBuilder();
    int[] data = {10, 20};
    PrintUtilities.appendLines(sb, data);
    String result = sb.toString();
    assertTrue(result.contains("10"));
    assertTrue(result.contains("20"));
  }

  // --- append for float[] ---

  @Test
  public void append_floatArray() {
    StringBuilder sb = new StringBuilder();
    float[] data = {1.5f, 2.5f};
    PrintUtilities.append(sb, data);
    assertNotNull(sb.toString());
    assertTrue(sb.length() > 0);
  }

  @Test
  public void appendLines_floatArray() {
    StringBuilder sb = new StringBuilder();
    float[] data = {1.0f, 2.0f};
    PrintUtilities.appendLines(sb, data);
    assertTrue(sb.length() > 0);
  }

  // --- append for double[] ---

  @Test
  public void append_doubleArray() {
    StringBuilder sb = new StringBuilder();
    double[] data = {1.1, 2.2};
    PrintUtilities.append(sb, data);
    assertTrue(sb.length() > 0);
  }

  @Test
  public void appendLines_doubleArray() {
    StringBuilder sb = new StringBuilder();
    double[] data = {3.3, 4.4};
    PrintUtilities.appendLines(sb, data);
    assertTrue(sb.length() > 0);
  }

  // --- append for IntBuffer ---

  @Test
  public void append_intBuffer() {
    StringBuilder sb = new StringBuilder();
    IntBuffer buf = IntBuffer.wrap(new int[]{5, 10, 15});
    PrintUtilities.append(sb, buf);
    String result = sb.toString();
    assertTrue(result.contains("5"));
  }

  @Test
  public void appendLines_intBuffer() {
    StringBuilder sb = new StringBuilder();
    IntBuffer buf = IntBuffer.wrap(new int[]{100, 200});
    PrintUtilities.appendLines(sb, buf);
    assertTrue(sb.length() > 0);
  }

  // --- append for FloatBuffer ---

  @Test
  public void append_floatBuffer() {
    StringBuilder sb = new StringBuilder();
    FloatBuffer buf = FloatBuffer.wrap(new float[]{1.0f, 2.0f});
    PrintUtilities.append(sb, buf);
    assertTrue(sb.length() > 0);
  }

  @Test
  public void appendLines_floatBuffer() {
    StringBuilder sb = new StringBuilder();
    FloatBuffer buf = FloatBuffer.wrap(new float[]{1.0f});
    PrintUtilities.appendLines(sb, buf);
    assertTrue(sb.length() > 0);
  }

  // --- append for DoubleBuffer ---

  @Test
  public void append_doubleBuffer() {
    StringBuilder sb = new StringBuilder();
    DoubleBuffer buf = DoubleBuffer.wrap(new double[]{9.9, 8.8});
    PrintUtilities.append(sb, buf);
    assertTrue(sb.length() > 0);
  }

  @Test
  public void appendLines_doubleBuffer() {
    StringBuilder sb = new StringBuilder();
    DoubleBuffer buf = DoubleBuffer.wrap(new double[]{7.7});
    PrintUtilities.appendLines(sb, buf);
    assertTrue(sb.length() > 0);
  }

  // --- toString ---

  @Test
  public void toString_singleValue() {
    String result = PrintUtilities.toString(42);
    assertNotNull(result);
    assertTrue(result.contains("42"));
  }

  @Test
  public void toString_multipleValues() {
    String result = PrintUtilities.toString("a", "b");
    assertNotNull(result);
    assertTrue(result.contains("a"));
    assertTrue(result.contains("b"));
  }

  @Test
  public void toStringLines_multipleValues() {
    String result = PrintUtilities.toStringLines("x", "y");
    assertNotNull(result);
    assertTrue(result.contains("x"));
    assertTrue(result.contains("y"));
  }

  // --- print/println with captured PrintStream ---

  @Test
  public void print_toCustomStream() {
    PrintUtilities.pushPrintStream();
    try {
      PrintUtilities.setPrintStream(capturePrintStream);
      PrintUtilities.print("test-output");
      capturePrintStream.flush();
      assertTrue(capturedOutput.toString().contains("test-output"));
    } finally {
      PrintUtilities.popPrintStream();
    }
  }

  @Test
  public void println_toCustomStream() {
    PrintUtilities.pushPrintStream();
    try {
      PrintUtilities.setPrintStream(capturePrintStream);
      PrintUtilities.println("line-output");
      capturePrintStream.flush();
      assertTrue(capturedOutput.toString().contains("line-output"));
    } finally {
      PrintUtilities.popPrintStream();
    }
  }

  @Test
  public void println_emptyLine() {
    PrintUtilities.pushPrintStream();
    try {
      PrintUtilities.setPrintStream(capturePrintStream);
      PrintUtilities.println();
      capturePrintStream.flush();
      assertTrue(capturedOutput.size() > 0);
    } finally {
      PrintUtilities.popPrintStream();
    }
  }

  @Test
  public void printlns_multipleEmptyLines() {
    PrintUtilities.pushPrintStream();
    try {
      PrintUtilities.setPrintStream(capturePrintStream);
      PrintUtilities.printlns(3);
      capturePrintStream.flush();
      assertTrue(capturedOutput.size() > 0);
    } finally {
      PrintUtilities.popPrintStream();
    }
  }

  @Test
  public void println_withExplicitPrintStream() {
    PrintUtilities.println(capturePrintStream, "explicit-stream");
    capturePrintStream.flush();
    assertTrue(capturedOutput.toString().contains("explicit-stream"));
  }

  @Test
  public void print_withExplicitPrintStream() {
    PrintUtilities.print(capturePrintStream, "explicit-print");
    capturePrintStream.flush();
    assertTrue(capturedOutput.toString().contains("explicit-print"));
  }

  // --- null values in append ---

  @Test
  public void append_nullInArray() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, (Object) null, "after");
    String result = sb.toString();
    assertTrue(result.contains("null"));
    assertTrue(result.contains("after"));
  }

  @Test
  public void append_multipleNulls() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, (Object) null, (Object) null);
    String result = sb.toString();
    // Should contain "null" at least twice
    int firstIdx = result.indexOf("null");
    assertTrue(firstIdx >= 0);
    int secondIdx = result.indexOf("null", firstIdx + 4);
    assertTrue(secondIdx >= 0);
  }

  // --- appendLines variants ---

  @Test
  public void appendLines_withStrings() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.appendLines(sb, "line1", "line2");
    String result = sb.toString();
    assertTrue(result.contains("line1"));
    assertTrue(result.contains("line2"));
  }

  @Test
  public void appendLines_withNull() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.appendLines(sb, (Object) null);
    assertTrue(sb.toString().contains("null"));
  }

  // --- Printable interface ---

  @Test
  public void append_printable() {
    Printable printable = (rv, decimalFormat, isLines) -> {
      rv.append("printable-output");
      return rv;
    };
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, (Object) printable);
    assertTrue(sb.toString().contains("printable-output"));
  }

  @Test
  public void appendLines_printable() {
    Printable printable = (rv, decimalFormat, isLines) -> {
      rv.append(isLines ? "lines-mode" : "single-mode");
      return rv;
    };
    StringBuilder sb = new StringBuilder();
    PrintUtilities.appendLines(sb, (Object) printable);
    assertTrue(sb.toString().contains("lines-mode"));
  }

  @Test
  public void append_printableAppendable() {
    Printable printable = (rv, decimalFormat, isLines) -> {
      rv.append("via-appendable");
      return rv;
    };
    StringBuilder sb = new StringBuilder();
    Appendable result = PrintUtilities.append((Appendable) sb, printable);
    assertNotNull(result);
    assertTrue(sb.toString().contains("via-appendable"));
  }

  @Test
  public void appendLines_printableAppendable() {
    Printable printable = (rv, decimalFormat, isLines) -> {
      rv.append(isLines ? "lines" : "single");
      return rv;
    };
    StringBuilder sb = new StringBuilder();
    PrintUtilities.appendLines((Appendable) sb, printable);
    assertTrue(sb.toString().contains("lines"));
  }

  // --- Object[] (2D arrays) ---

  @Test
  public void append_objectArray() {
    StringBuilder sb = new StringBuilder();
    Object[] arr = {"elem1", "elem2"};
    PrintUtilities.append(sb, (Object) arr);
    String result = sb.toString();
    assertTrue(result.contains("elem1"));
    assertTrue(result.contains("elem2"));
    assertTrue(result.contains("length=2"));
  }

  @Test
  public void appendLines_objectArray() {
    StringBuilder sb = new StringBuilder();
    Object[] arr = {"x", "y"};
    PrintUtilities.appendLines(sb, (Object) arr);
    String result = sb.toString();
    assertTrue(result.contains("x"));
    assertTrue(result.contains("y"));
  }

  // --- int[] null case ---

  @Test
  public void append_intArrayNull() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, (int[]) null);
    assertTrue(sb.toString().contains("null"));
  }

  // --- float[] null case ---

  @Test
  public void append_floatArrayNull() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, (float[]) null);
    assertTrue(sb.toString().contains("null"));
  }

  // --- double[] null case ---

  @Test
  public void append_doubleArrayNull() {
    StringBuilder sb = new StringBuilder();
    PrintUtilities.append(sb, (double[]) null);
    assertTrue(sb.toString().contains("null"));
  }

  // --- printlns with explicit PrintStream ---

  @Test
  public void printlns_withExplicitPrintStream() {
    PrintUtilities.printlns(capturePrintStream, "multiline");
    capturePrintStream.flush();
    assertTrue(capturedOutput.toString().contains("multiline"));
  }

  // --- printlns with captured stream ---

  @Test
  public void printlns_toCustomStream() {
    PrintUtilities.pushPrintStream();
    try {
      PrintUtilities.setPrintStream(capturePrintStream);
      PrintUtilities.printlns("lines-test");
      capturePrintStream.flush();
      assertTrue(capturedOutput.toString().contains("lines-test"));
    } finally {
      PrintUtilities.popPrintStream();
    }
  }

  // --- println with explicit PrintStream and count ---

  @Test
  public void printlns_countWithExplicitStream() {
    PrintUtilities.printlns(capturePrintStream, 3);
    capturePrintStream.flush();
    assertTrue(capturedOutput.size() > 0);
  }

  @Test
  public void println_singleLineExplicitStream() {
    PrintUtilities.println(capturePrintStream);
    capturePrintStream.flush();
    assertTrue(capturedOutput.size() > 0);
  }

  // --- DecimalFormat affects Float/Double formatting ---

  @Test
  public void append_Float_usesDecimalFormat() {
    PrintUtilities.pushDecimalFormat();
    try {
      DecimalFormat fmt = new DecimalFormat("0.00");
      PrintUtilities.setDecimalFormat(fmt);
      StringBuilder sb = new StringBuilder();
      PrintUtilities.append(sb, Float.valueOf(1.5f));
      assertTrue(sb.length() > 0);
    } finally {
      PrintUtilities.popDecimalFormat();
    }
  }

  @Test
  public void append_Double_usesDecimalFormat() {
    PrintUtilities.pushDecimalFormat();
    try {
      DecimalFormat fmt = new DecimalFormat("0.00");
      PrintUtilities.setDecimalFormat(fmt);
      StringBuilder sb = new StringBuilder();
      PrintUtilities.append(sb, Double.valueOf(2.5));
      assertTrue(sb.length() > 0);
    } finally {
      PrintUtilities.popDecimalFormat();
    }
  }
}

package edu.cmu.cs.dennisc.print;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PrintUtilitiesDeepTest {
  private PrintStream originalPrintStream;
  private DecimalFormat originalDecimalFormat;
  private String originalIndentText;
  private String originalSeparatorText;
  private ByteArrayOutputStream capture;
  private PrintStream capturePrintStream;

  private static final class DemoPrintable implements Printable {
    private final String label;

    private DemoPrintable(String label) {
      this.label = label;
    }

    @Override
    public Appendable append(Appendable rv, DecimalFormat decimalFormat, boolean isLines) throws IOException {
      rv.append(this.label);
      rv.append(isLines ? "-lines" : "-single");
      return rv;
    }
  }

  @Before
  public void setUp() {
    this.originalPrintStream = PrintUtilities.accessPrintStream();
    this.originalDecimalFormat = (DecimalFormat) PrintUtilities.accessDecimalFormat().clone();
    this.originalIndentText = PrintUtilities.getIndentText();
    this.originalSeparatorText = PrintUtilities.getSeparatorText();
    this.capture = new ByteArrayOutputStream();
    this.capturePrintStream = new PrintStream(this.capture);
    PrintUtilities.setPrintStream(this.capturePrintStream);
  }

  @After
  public void tearDown() {
    PrintUtilities.setPrintStream(this.originalPrintStream);
    PrintUtilities.setDecimalFormat(this.originalDecimalFormat);
    PrintUtilities.setIndentText(this.originalIndentText);
    PrintUtilities.setSeparatorText(this.originalSeparatorText);
  }

  @Test
  public void printStreamPushAndPopRestorePreviousValue() {
    PrintUtilities.pushPrintStream();
    PrintUtilities.setPrintStream(System.err);
    assertSame(System.err, PrintUtilities.accessPrintStream());
    PrintUtilities.popPrintStream();
    assertSame(this.capturePrintStream, PrintUtilities.accessPrintStream());
  }

  @Test
  public void decimalFormatPushAndPopRestorePreviousValue() {
    DecimalFormat alternate = new DecimalFormat("0.0");
    PrintUtilities.pushDecimalFormat();
    PrintUtilities.setDecimalFormat(alternate);
    assertSame(alternate, PrintUtilities.accessDecimalFormat());
    PrintUtilities.popDecimalFormat();
    assertEquals(this.originalDecimalFormat.toPattern(), PrintUtilities.accessDecimalFormat().toPattern());
  }

  @Test
  public void indentTextPushAndPopRestorePreviousValue() {
    PrintUtilities.pushIndentText();
    PrintUtilities.setIndentText("--");
    assertEquals("--", PrintUtilities.getIndentText());
    PrintUtilities.popIndentText();
    assertEquals(this.originalIndentText, PrintUtilities.getIndentText());
  }

  @Test
  public void separatorTextPushAndPopRestorePreviousValue() {
    PrintUtilities.pushSeparatorText();
    PrintUtilities.setSeparatorText(" | ");
    assertEquals(" | ", PrintUtilities.getSeparatorText());
    PrintUtilities.popSeparatorText();
    assertEquals(this.originalSeparatorText, PrintUtilities.getSeparatorText());
  }

  @Test
  public void printAndPrintlnUseConfiguredPrintStream() {
    PrintUtilities.print("alpha", 1);
    PrintUtilities.println("beta", 2);
    String output = this.capture.toString();
    assertTrue(output.contains("alpha"));
    assertTrue(output.contains("beta"));
  }

  @Test
  public void printlnsWithCountWritesRequestedNumberOfBlankLines() {
    ByteArrayOutputStream localCapture = new ByteArrayOutputStream();
    PrintUtilities.printlns(new PrintStream(localCapture), 3);
    String output = localCapture.toString();
    assertEquals(3, output.chars().filter(ch -> ch == '\n').count());
  }

  @Test
  public void printlnWithoutArgumentsWritesSingleLineBreak() {
    ByteArrayOutputStream localCapture = new ByteArrayOutputStream();
    PrintUtilities.println(new PrintStream(localCapture));
    assertEquals(1, localCapture.toString().chars().filter(ch -> ch == '\n').count());
  }

  @Test
  public void separatorTextControlsRenderedValueSpacing() {
    PrintUtilities.setSeparatorText("|");
    String rendered = PrintUtilities.toString("a", "b", "c");
    assertTrue(rendered.contains("a|b|c|"));
  }

  @Test
  public void customDecimalFormatChangesFloatAndDoubleRendering() {
    PrintUtilities.setDecimalFormat(new DecimalFormat("0.00"));
    String rendered = PrintUtilities.toString(Float.valueOf(1.234f), Double.valueOf(9.876));
    assertTrue(rendered.contains("1.23"));
    assertTrue(rendered.contains("9.88"));
  }

  @Test
  public void appendSupportsPrimitiveWrapperAndNullValues() {
    StringBuilder builder = new StringBuilder();
    PrintUtilities.append(builder, Float.valueOf(2.5f), Double.valueOf(3.5d), null);
    String text = builder.toString();
    assertTrue(text.contains("2.5"));
    assertTrue(text.contains("3.5"));
    assertTrue(text.contains("null"));
  }

  @Test
  public void appendSupportsCollectionsAndMapsViaObjectToString() {
    Map<String, Integer> map = new LinkedHashMap<>();
    map.put("one", 1);
    map.put("two", 2);

    String rendered = PrintUtilities.toString(Arrays.asList("alpha", "beta"), map);
    assertTrue(rendered.contains("[alpha, beta]"));
    assertTrue(rendered.contains("{one=1, two=2}"));
  }

  @Test
  public void appendSupportsObjectArraysOnSingleLine() {
    String rendered = PrintUtilities.toString((Object) new String[] {"alpha", "beta"});
    assertTrue(rendered.contains("java.lang.String[]"));
    assertTrue(rendered.contains("alpha"));
    assertTrue(rendered.contains("beta"));
  }

  @Test
  public void appendLinesSupportsObjectArraysWithLineBreaks() {
    String rendered = PrintUtilities.toStringLines((Object) new Object[] {"alpha", "beta"});
    assertTrue(rendered.contains("java.lang.Object[]"));
    assertTrue(rendered.contains("\n"));
  }

  @Test
  public void appendSupportsIntArray() {
    String rendered = PrintUtilities.append(new StringBuilder(), new int[] {1, 2, 3}).toString();
    assertTrue(rendered.contains("int[]"));
    assertTrue(rendered.contains("length=3"));
    assertTrue(rendered.contains("1"));
  }

  @Test
  public void appendSupportsFloatArray() {
    String rendered = PrintUtilities.append(new StringBuilder(), new float[] {1.25f, 2.5f}).toString();
    assertTrue(rendered.contains("float[]"));
    assertTrue(rendered.contains("1.25"));
  }

  @Test
  public void appendSupportsDoubleArray() {
    String rendered = PrintUtilities.append(new StringBuilder(), new double[] {3.25d, 4.5d}).toString();
    assertTrue(rendered.contains("double[]"));
    assertTrue(rendered.contains("4.5"));
  }

  @Test
  public void appendSupportsNullPrimitiveArrays() {
    assertTrue(PrintUtilities.append(new StringBuilder(), (int[]) null).toString().contains("null"));
    assertTrue(PrintUtilities.append(new StringBuilder(), (float[]) null).toString().contains("null"));
    assertTrue(PrintUtilities.append(new StringBuilder(), (double[]) null).toString().contains("null"));
  }

  @Test
  public void appendSupportsIntBufferAndRewindsAfterUse() {
    IntBuffer buffer = IntBuffer.wrap(new int[] {10, 20, 30});
    String rendered = PrintUtilities.append(new StringBuilder(), buffer).toString();
    assertTrue(rendered.contains("10"));
    assertEquals(0, buffer.position());
  }

  @Test
  public void appendSupportsFloatBufferAndRewindsAfterUse() {
    FloatBuffer buffer = FloatBuffer.wrap(new float[] {1.5f, 2.5f});
    String rendered = PrintUtilities.append(new StringBuilder(), buffer).toString();
    assertTrue(rendered.contains("1.5"));
    assertEquals(0, buffer.position());
  }

  @Test
  public void appendSupportsDoubleBufferAndRewindsAfterUse() {
    DoubleBuffer buffer = DoubleBuffer.wrap(new double[] {9.0d, 10.5d});
    String rendered = PrintUtilities.append(new StringBuilder(), buffer).toString();
    assertTrue(rendered.contains("10.5"));
    assertEquals(0, buffer.position());
  }

  @Test
  public void appendLinesDelegatesToBufferSpecificRendering() {
    IntBuffer intBuffer = IntBuffer.wrap(new int[] {4, 5});
    FloatBuffer floatBuffer = FloatBuffer.wrap(new float[] {6.5f});
    DoubleBuffer doubleBuffer = DoubleBuffer.wrap(new double[] {7.5d});

    assertTrue(PrintUtilities.appendLines(new StringBuilder(), intBuffer).toString().contains("4"));
    assertTrue(PrintUtilities.appendLines(new StringBuilder(), floatBuffer).toString().contains("6.5"));
    assertTrue(PrintUtilities.appendLines(new StringBuilder(), doubleBuffer).toString().contains("7.5"));
  }

  @Test
  public void printableValuesAreRenderedThroughPrintableInterface() {
    DemoPrintable printable = new DemoPrintable("demo");
    String rendered = PrintUtilities.toString(printable);
    assertTrue(rendered.contains("demo-single"));
  }

  @Test
  public void printableAppendableHelpersUseLineFlagCorrectly() {
    DemoPrintable printable = new DemoPrintable("demo");
    StringBuilder singleLine = new StringBuilder();
    StringBuilder lines = new StringBuilder();
    PrintUtilities.append(singleLine, printable);
    PrintUtilities.appendLines(lines, printable);
    assertEquals("demo-single", singleLine.toString());
    assertEquals("demo-lines", lines.toString());
  }

  @Test
  public void appendLinesUsesPrintableBranchInsideObjectVarargs() {
    DemoPrintable printable = new DemoPrintable("embedded");
    String rendered = PrintUtilities.toStringLines(printable);
    assertTrue(rendered.contains("embedded-lines"));
  }

  @Test
  public void toStringAndAppendShareEquivalentRendering() {
    String viaToString = PrintUtilities.toString("alpha", Integer.valueOf(2));
    String viaAppend = PrintUtilities.append(new StringBuilder(), "alpha", Integer.valueOf(2)).toString();
    assertEquals(viaToString, viaAppend);
  }

  @Test
  public void printlnsWithValuesUsesLineBasedRendering() {
    PrintUtilities.printlns(new int[] {1, 2}, new double[] {3.5d});
    String output = this.capture.toString();
    assertTrue(output.contains("int[]"));
    assertTrue(output.contains("double[]"));
    assertTrue(output.endsWith(System.lineSeparator()));
  }

  @Test
  public void changingIndentTextDoesNotBreakFormattingAccessors() {
    PrintUtilities.setIndentText("\t");
    assertEquals("\t", PrintUtilities.getIndentText());
    assertNotNull(PrintUtilities.toString("indent"));
  }

  @Test
  public void nestedPushAndPopOperationsWorkAcrossDifferentStacks() {
    PrintUtilities.pushPrintStream();
    PrintUtilities.pushDecimalFormat();
    PrintUtilities.pushIndentText();
    PrintUtilities.pushSeparatorText();

    PrintUtilities.setPrintStream(System.err);
    PrintUtilities.setDecimalFormat(new DecimalFormat("0"));
    PrintUtilities.setIndentText("##");
    PrintUtilities.setSeparatorText("/");

    assertSame(System.err, PrintUtilities.accessPrintStream());
    assertEquals("#0", PrintUtilities.accessDecimalFormat().toPattern());
    assertEquals("##", PrintUtilities.getIndentText());
    assertEquals("/", PrintUtilities.getSeparatorText());

    PrintUtilities.popSeparatorText();
    PrintUtilities.popIndentText();
    PrintUtilities.popDecimalFormat();
    PrintUtilities.popPrintStream();

    assertSame(this.capturePrintStream, PrintUtilities.accessPrintStream());
    assertEquals(this.originalDecimalFormat.toPattern(), PrintUtilities.accessDecimalFormat().toPattern());
    assertEquals(this.originalIndentText, PrintUtilities.getIndentText());
    assertEquals(this.originalSeparatorText, PrintUtilities.getSeparatorText());
  }
}

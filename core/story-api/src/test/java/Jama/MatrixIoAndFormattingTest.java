package Jama;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static Jama.MatrixTestSupport.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MatrixIoAndFormattingTest {
  @Test
  public void printOverloadsAndReadRoundTripMatrixContent() throws Exception {
    Matrix matrix = new Matrix(new double[][]{{1.25, 2.5}, {3.75, 4.0}});

    StringWriter writer = new StringWriter();
    matrix.print(new PrintWriter(writer, true), 4, 2);
    String decimalText = writer.toString();
    assertTrue(decimalText.contains("1.25"));
    assertTrue(decimalText.contains("4.00"));
    assertMatrixEquals(matrix, Matrix.read(new BufferedReader(new StringReader(decimalText))));

    DecimalFormat format = new DecimalFormat("0.000");
    format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    StringWriter numberWriter = new StringWriter();
    matrix.print(new PrintWriter(numberWriter, true), format, 8);
    String formatted = numberWriter.toString();
    assertTrue(formatted.contains("   1.250"));
    assertTrue(formatted.endsWith("\n\n"));

    PrintStream originalOut = System.out;
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    try {
      System.setOut(new PrintStream(stdout, true, StandardCharsets.UTF_8.name()));
      matrix.print(4, 1);
      matrix.print(format, 7);
    } finally {
      System.setOut(originalOut);
    }

    String stdoutText = stdout.toString(StandardCharsets.UTF_8.name());
    assertTrue(stdoutText.contains("1."));
    assertTrue(stdoutText.contains("3.750"));
  }

  @Test
  public void readRejectsMalformedInput() throws Exception {
    expectIOException("", "Unexpected EOF on matrix read.");
    expectIOException("1 2\n3\n\n", "Row 2 is too short.");
    expectIOException("1 2\n3 4 5\n\n", "Row 2 is too long.");
  }

  private static void expectIOException(String text, String message) throws IOException {
    try {
      Matrix.read(new BufferedReader(new StringReader(text)));
      fail("expected IOException");
    } catch (IOException expected) {
      assertEquals(message, expected.getMessage());
    }
  }
}

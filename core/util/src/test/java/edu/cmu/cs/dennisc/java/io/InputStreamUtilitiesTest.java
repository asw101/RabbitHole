package edu.cmu.cs.dennisc.java.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import static org.junit.Assert.*;

public class InputStreamUtilitiesTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private static class ChunkedInputStream extends InputStream {
    private final byte[] data;
    private final int chunkSize;
    private int index;

    private ChunkedInputStream(byte[] data, int chunkSize) {
      this.data = data;
      this.chunkSize = chunkSize;
      this.index = 0;
    }

    @Override
    public int available() {
      return Math.min(this.chunkSize, this.data.length - this.index);
    }

    @Override
    public int read() {
      if (this.index >= this.data.length) {
        return -1;
      }
      return this.data[this.index++] & 0xff;
    }

    @Override
    public int read(byte[] b, int off, int len) {
      if (this.index >= this.data.length) {
        return -1;
      }
      int amount = Math.min(len, available());
      System.arraycopy(this.data, this.index, b, off, amount);
      this.index += amount;
      return amount;
    }
  }

  private static class AvailableThenEofInputStream extends InputStream {
    @Override
    public int available() {
      return 1;
    }

    @Override
    public int read() {
      return -1;
    }
  }

  private byte[] createBytes(int size) {
    byte[] data = new byte[size];
    for (int i = 0; i < size; i++) {
      data[i] = (byte) ((i * 37) % 251);
    }
    return data;
  }

  @Test
  public void drainEmptyStreamReturnsTrue() throws IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
    boolean drained = InputStreamUtilities.drain(inputStream);
    assertTrue(drained);
    assertEquals(0, inputStream.available());
  }

  @Test
  public void drainSingleByteStreamReturnsTrue() throws IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[] {42});
    boolean drained = InputStreamUtilities.drain(inputStream);
    assertTrue(drained);
    assertEquals(0, inputStream.available());
  }

  @Test
  public void drainLargeStreamReturnsTrue() throws IOException {
    byte[] data = createBytes(10000);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
    boolean drained = InputStreamUtilities.drain(inputStream);
    assertTrue(drained);
    assertEquals(0, inputStream.available());
  }

  @Test
  public void drainBinaryStreamPreservesBytesInOutputStream() throws IOException {
    byte[] data = new byte[] {0, 1, 2, -1, 127, -128};
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    boolean drained = InputStreamUtilities.drain(new ByteArrayInputStream(data), outputStream);
    assertTrue(drained);
    assertArrayEquals(data, outputStream.toByteArray());
  }

  @Test
  public void drainWithNullOutputStreamSucceeds() throws IOException {
    byte[] data = createBytes(32);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
    boolean drained = InputStreamUtilities.drain(inputStream, null);
    assertTrue(drained);
    assertEquals(0, inputStream.available());
  }

  @Test
  public void drainOutputStreamReceivesAllBytes() throws IOException {
    byte[] data = createBytes(257);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    boolean drained = InputStreamUtilities.drain(new ChunkedInputStream(data, 17), outputStream);
    assertTrue(drained);
    assertArrayEquals(data, outputStream.toByteArray());
  }

  @Test
  public void drainIsIdempotentForExhaustedStream() throws IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(createBytes(16));
    assertTrue(InputStreamUtilities.drain(inputStream));
    assertTrue(InputStreamUtilities.drain(inputStream));
    assertEquals(0, inputStream.available());
  }

  @Test
  public void drainReturnsFalseWhenAvailableReportsBytesButReadReturnsEof() throws IOException {
    AvailableThenEofInputStream inputStream = new AvailableThenEofInputStream();
    boolean drained = InputStreamUtilities.drain(inputStream);
    assertFalse(drained);
  }

  @Test
  public void getBytesFromInputStreamWithSize1() throws IOException {
    byte[] expected = createBytes(1);
    InputStream inputStream = new ChunkedInputStream(expected, 1);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(1, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize2() throws IOException {
    byte[] expected = createBytes(2);
    InputStream inputStream = new ChunkedInputStream(expected, 1);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(2, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize3() throws IOException {
    byte[] expected = createBytes(3);
    InputStream inputStream = new ChunkedInputStream(expected, 1);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(3, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize7() throws IOException {
    byte[] expected = createBytes(7);
    InputStream inputStream = new ChunkedInputStream(expected, 7);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(7, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize16() throws IOException {
    byte[] expected = createBytes(16);
    InputStream inputStream = new ChunkedInputStream(expected, 7);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(16, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize128() throws IOException {
    byte[] expected = createBytes(128);
    InputStream inputStream = new ChunkedInputStream(expected, 63);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(128, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize255() throws IOException {
    byte[] expected = createBytes(255);
    InputStream inputStream = new ChunkedInputStream(expected, 63);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(255, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize257() throws IOException {
    byte[] expected = createBytes(257);
    InputStream inputStream = new ChunkedInputStream(expected, 63);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(257, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize512() throws IOException {
    byte[] expected = createBytes(512);
    InputStream inputStream = new ChunkedInputStream(expected, 63);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(512, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize1024() throws IOException {
    byte[] expected = createBytes(1024);
    InputStream inputStream = new ChunkedInputStream(expected, 63);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(1024, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize4096() throws IOException {
    byte[] expected = createBytes(4096);
    InputStream inputStream = new ChunkedInputStream(expected, 63);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(4096, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize8192() throws IOException {
    byte[] expected = createBytes(8192);
    InputStream inputStream = new ChunkedInputStream(expected, 63);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(8192, actual.length);
  }

  @Test
  public void getBytesFromInputStreamWithSize10000() throws IOException {
    byte[] expected = createBytes(10000);
    InputStream inputStream = new ChunkedInputStream(expected, 63);
    byte[] actual = InputStreamUtilities.getBytes(inputStream);
    assertArrayEquals(expected, actual);
    assertEquals(10000, actual.length);
  }

  @Test
  public void getBytesSupportsPowerOfTwoSize1() throws IOException {
    byte[] expected = createBytes(1);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 16));
    assertArrayEquals(expected, actual);
    assertEquals(1, actual.length);
  }

  @Test
  public void getBytesSupportsPowerOfTwoSize2() throws IOException {
    byte[] expected = createBytes(2);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 16));
    assertArrayEquals(expected, actual);
    assertEquals(2, actual.length);
  }

  @Test
  public void getBytesSupportsPowerOfTwoSize4() throws IOException {
    byte[] expected = createBytes(4);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 16));
    assertArrayEquals(expected, actual);
    assertEquals(4, actual.length);
  }

  @Test
  public void getBytesSupportsPowerOfTwoSize8() throws IOException {
    byte[] expected = createBytes(8);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 16));
    assertArrayEquals(expected, actual);
    assertEquals(8, actual.length);
  }

  @Test
  public void getBytesSupportsPowerOfTwoSize16() throws IOException {
    byte[] expected = createBytes(16);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 16));
    assertArrayEquals(expected, actual);
    assertEquals(16, actual.length);
  }

  @Test
  public void getBytesSupportsPowerOfTwoSize32() throws IOException {
    byte[] expected = createBytes(32);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 16));
    assertArrayEquals(expected, actual);
    assertEquals(32, actual.length);
  }

  @Test
  public void getBytesSupportsPowerOfTwoSize64() throws IOException {
    byte[] expected = createBytes(64);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 16));
    assertArrayEquals(expected, actual);
    assertEquals(64, actual.length);
  }

  @Test
  public void getBytesSupportsPowerOfTwoSize128() throws IOException {
    byte[] expected = createBytes(128);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 16));
    assertArrayEquals(expected, actual);
    assertEquals(128, actual.length);
  }

  @Test
  public void getBytesSupportsPowerOfTwoSize256() throws IOException {
    byte[] expected = createBytes(256);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 16));
    assertArrayEquals(expected, actual);
    assertEquals(256, actual.length);
  }

  @Test
  public void getBytesSupportsPowerOfTwoSize512() throws IOException {
    byte[] expected = createBytes(512);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 16));
    assertArrayEquals(expected, actual);
    assertEquals(512, actual.length);
  }

  @Test
  public void getBytesFromFileMatchesWrittenBytes() throws IOException {
    File file = temporaryFolder.newFile("bytes.bin");
    byte[] expected = createBytes(257);
    Files.write(file.toPath(), expected);
    byte[] actual = InputStreamUtilities.getBytes(file);
    assertArrayEquals(expected, actual);
  }

  @Test
  public void getBytesFromResourceLoadsExistingTestResource() throws IOException {
    byte[] actual = InputStreamUtilities.getBytes(InputStreamUtilitiesTest.class, "/test-resource.txt");
    String text = new String(actual, StandardCharsets.UTF_8);
    assertTrue(text.contains("test resource content"));
    assertTrue(actual.length > 0);
  }

  @Test
  public void getBytesFromTextInputStreamReturnsUtf8Bytes() throws IOException {
    String text = "héllo 世界 🙂";
    byte[] expected = text.getBytes(StandardCharsets.UTF_8);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 5));
    assertArrayEquals(expected, actual);
    assertEquals(text, new String(actual, StandardCharsets.UTF_8));
  }

  @Test
  public void drainWithOutputStreamLeavesOutputUsableAfterDrain() throws IOException {
    byte[] data = createBytes(64);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    assertTrue(InputStreamUtilities.drain(new ByteArrayInputStream(data), outputStream));
    outputStream.write(7);
    assertEquals(data.length + 1, outputStream.toByteArray().length);
  }

  @Test
  public void getBytesFromChunkedStreamExercisesMultipleBufferPath() throws IOException {
    byte[] expected = createBytes(300);
    byte[] actual = InputStreamUtilities.getBytes(new ChunkedInputStream(expected, 64));
    assertArrayEquals(expected, actual);
    assertEquals(300, actual.length);
  }

  @Test
  public void drainChunkedInputStreamPreservesExactOrdering() throws IOException {
    byte[] expected = createBytes(300);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    assertTrue(InputStreamUtilities.drain(new ChunkedInputStream(expected, 9), outputStream));
    assertTrue(Arrays.equals(expected, outputStream.toByteArray()));
  }

  @Test
  public void getBytesFileRoundTripForTextContent() throws IOException {
    File file = temporaryFolder.newFile("text.txt");
    byte[] expected = "alpha\nbeta\ngamma".getBytes(StandardCharsets.UTF_8);
    Files.write(file.toPath(), expected);
    byte[] actual = InputStreamUtilities.getBytes(file);
    assertEquals("alpha\nbeta\ngamma", new String(actual, StandardCharsets.UTF_8));
  }

  @Test(expected = NullPointerException.class)
  public void getBytesFromEmptyByteArrayInputStreamThrowsNullPointerException() throws IOException {
    InputStreamUtilities.getBytes(new ByteArrayInputStream(new byte[0]));
  }

  @Test(expected = NullPointerException.class)
  public void getBytesFromExhaustedStreamThrowsNullPointerException() throws IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(createBytes(8));
    assertEquals(8, inputStream.available());
    assertTrue(InputStreamUtilities.drain(inputStream));
    InputStreamUtilities.getBytes(inputStream);
  }

}

package edu.cmu.cs.dennisc.java.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

import static org.junit.Assert.*;

public class InputStreamUtilitiesTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void drain_emptyStream() throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
    assertTrue(InputStreamUtilities.drain(bais));
  }

  @Test
  public void drain_withData() throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(new byte[]{1, 2, 3});
    assertTrue(InputStreamUtilities.drain(bais));
  }

  @Test
  public void drain_withOutputStream() throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(new byte[]{10, 20, 30});
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    assertTrue(InputStreamUtilities.drain(bais, baos));
    byte[] result = baos.toByteArray();
    assertEquals(3, result.length);
    assertEquals(10, result[0]);
    assertEquals(20, result[1]);
    assertEquals(30, result[2]);
  }

  @Test
  public void getBytes_fromStream() throws IOException {
    byte[] original = {1, 2, 3, 4, 5};
    ByteArrayInputStream bais = new ByteArrayInputStream(original);
    byte[] result = InputStreamUtilities.getBytes(bais);
    assertArrayEquals(original, result);
  }

  @Test
  public void getBytes_fromFile() throws IOException {
    File f = tempFolder.newFile("test.bin");
    byte[] data = {10, 20, 30, 40};
    try (FileOutputStream fos = new FileOutputStream(f)) {
      fos.write(data);
    }
    byte[] result = InputStreamUtilities.getBytes(f);
    assertArrayEquals(data, result);
  }

  @Test
  public void getBytes_largeData() throws IOException {
    byte[] data = new byte[10000];
    for (int i = 0; i < data.length; i++) {
      data[i] = (byte) (i % 256);
    }
    ByteArrayInputStream bais = new ByteArrayInputStream(data);
    byte[] result = InputStreamUtilities.getBytes(bais);
    assertArrayEquals(data, result);
  }
}

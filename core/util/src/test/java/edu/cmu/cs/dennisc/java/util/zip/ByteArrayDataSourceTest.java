package edu.cmu.cs.dennisc.java.util.zip;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ByteArrayDataSourceTest {
  @Test
  public void writesProvidedBytesAndKeepsName() throws Exception {
    ByteArrayDataSource dataSource = new ByteArrayDataSource("model.bin", new byte[]{1, 2, 3});
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    dataSource.write(output);

    assertEquals("model.bin", dataSource.getName());
    assertArrayEquals(new byte[]{1, 2, 3}, output.toByteArray());
  }

  @Test
  public void stringConstructorAndEqualityFollowDataSourceContract() throws Exception {
    ByteArrayDataSource ascii = new ByteArrayDataSource("shared.txt", "hello");
    ByteArrayDataSource sameNameDifferentContent = new ByteArrayDataSource("shared.txt", new byte[]{9, 9});
    ByteArrayDataSource differentName = new ByteArrayDataSource("other.txt", "hello");
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    ascii.write(output);

    assertArrayEquals("hello".getBytes(), output.toByteArray());
    assertEquals(ascii, sameNameDifferentContent);
    assertEquals(ascii.hashCode(), sameNameDifferentContent.hashCode());
    org.junit.Assert.assertNotEquals(ascii, differentName);
  }
}

package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.java.util.zip.DataSource;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ModelExportDataSourcesTest {
  @Test
  public void createReturnsNamedDataSourceThatWritesProvidedBytes() throws Exception {
    DataSource dataSource = ModelExportDataSources.create(
        "gallery/Hero/model.xml",
        outputStream -> outputStream.write(new byte[] {1, 2, 3}));

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    dataSource.write(outputStream);

    assertEquals("gallery/Hero/model.xml", dataSource.getName());
    assertArrayEquals(new byte[] {1, 2, 3}, outputStream.toByteArray());
  }

  @Test
  public void structureFileNameRelativeToStripsResourcePrefix() {
    DataSource dataSource = ModelExportDataSources.create(
        "gallery/Hero/model.xml",
        outputStream -> outputStream.write(new byte[0]));

    assertEquals(
        "model.xml",
        ModelExportDataSources.structureFileNameRelativeTo("gallery/Hero", dataSource));
  }

  @Test
  public void createPropagatesWriterIoExceptions() {
    DataSource dataSource = ModelExportDataSources.create(
        "gallery/Hero/model.xml",
        outputStream -> {
          throw new IOException("boom");
        });

    IOException exception = Assert.assertThrows(
        IOException.class,
        () -> dataSource.write(new ByteArrayOutputStream()));

    assertEquals("boom", exception.getMessage());
  }
}

package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.java.util.zip.DataSource;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JointedModelGltfExporterModelTest {
  @Test
  public void createStructureDataSourceWritesBinaryGlbWithEmbeddedJsonChunks() throws Exception {
    ModelManifest.ModelVariant variant = ExporterTestFixtures.createVariant("robotStructure", "RobotTexture");
    JointedModelGltfExporter exporter = new JointedModelGltfExporter(
        ExporterTestFixtures.createVisual(true),
        variant,
        "robot",
        "resource/path",
        Map.of("ROOT", "ROOT_ALIAS"));

    DataSource dataSource = exporter.createStructureDataSource();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    dataSource.write(outputStream);
    byte[] bytes = outputStream.toByteArray();
    String payload = new String(bytes, StandardCharsets.ISO_8859_1);

    assertEquals("resource/path/RobotTexture.glb", dataSource.getName());
    assertArrayEquals(new byte[]{0x67, 0x6c, 0x54, 0x46}, Arrays.copyOf(bytes, 4));
    assertTrue(bytes.length > 32);
    assertTrue(payload.contains("RobotTexture"));
    assertTrue(payload.contains("ROOT_ALIAS"));
  }
}

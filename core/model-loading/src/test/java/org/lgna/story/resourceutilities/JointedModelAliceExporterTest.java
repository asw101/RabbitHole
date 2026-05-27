package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.java.util.zip.DataSource;
import org.alice.tweedle.file.AliceTextureReference;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JointedModelAliceExporterTest {
  @Test
  public void createStructureDataSourceSerializesVisualAndClearsTextureArray() throws Exception {
    ModelManifest.ModelVariant variant = ExporterTestFixtures.createVariant("robotStructure", "RobotTexture");
    edu.cmu.cs.dennisc.scenegraph.SkeletonVisual visual = ExporterTestFixtures.createVisual(true);
    JointedModelAliceExporter exporter = new JointedModelAliceExporter(visual, variant, "resource/path");

    DataSource structure = exporter.createStructureDataSource();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    structure.write(outputStream);

    assertEquals(0, visual.textures.getValue().length);
    assertTrue(outputStream.size() > 0);
    assertTrue(structure.getName().endsWith(".a3r"));
    assertEquals(structure.getName().substring(structure.getName().lastIndexOf('/') + 1), exporter.getStructureFileName(structure));
    assertEquals("a3r", exporter.getStructureExtension());
  }

  @Test
  public void addImageDataSourcesAddsTextureManifestReferenceAndResourceMap() throws Exception {
    ModelManifest.ModelVariant variant = ExporterTestFixtures.createVariant("robotStructure", "RobotTexture");
    JointedModelAliceExporter exporter = new JointedModelAliceExporter(ExporterTestFixtures.createVisual(true), variant, "resource/path");
    List<DataSource> dataSources = new ArrayList<>();
    ModelManifest manifest = new ModelManifest();
    Map<Integer, String> resourceMap = new HashMap<>();

    exporter.addImageDataSources(dataSources, manifest, resourceMap);

    assertEquals(1, dataSources.size());
    assertEquals(1, manifest.resources.size());
    AliceTextureReference reference = (AliceTextureReference) manifest.resources.get(0);
    assertEquals(reference.name, resourceMap.get(0));
    assertTrue(reference.file.endsWith(".a3t"));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    dataSources.get(0).write(outputStream);
    assertFalse(outputStream.size() == 0);
  }
}

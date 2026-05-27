package org.lgna.story.resourceutilities;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModelResourceExporterXmlAndMetadataTest {
  @Test
  public void createXmlStringIncludesInheritedAndSubresourceMetadata() {
    ModelResourceExporter exporter = new ModelResourceExporter("testProp", ModelClassData.PROP_CLASS_DATA);
    exporter.addAttribution("Alice Team", "2024");
    exporter.setIsDeprecated(true);
    exporter.setPlaceOnGround(true);
    exporter.addTags("shared-tag");
    exporter.addGroupTags("shared-group");
    exporter.addThemeTags("shared-theme");
    exporter.addResource("VariantModel", "Default", "ALICE", "Guest Artist", "2025");
    exporter.addSubResourceTags("VariantModel", "Default", "shared-tag", "variant-tag");
    exporter.addSubResourceGroupTags("VariantModel", "Default", "shared-group", "variant-group");
    exporter.addSubResourceThemeTags("VariantModel", "Default", "shared-theme", "variant-theme");
    exporter.setBoundingBox("VariantModel", new AxisAlignedBox(new Point3(-1, 0, -2), new Point3(4, 5, 6)));

    String xml = exporter.createXMLString();

    assertTrue(xml.contains("<AliceModel creationYear=\"2024\" creator=\"Alice Team\" deprecated=\"TRUE\" name=\"TestProp\" placeOnGround=\"TRUE\">"));
    assertTrue(xml.contains("resourceName=\"" + exporter.createResourceEnumName("VariantModel", "Default") + "\""));
    assertTrue(xml.contains("creator=\"Guest Artist\""));
    assertTrue(xml.contains("creationYear=\"2025\""));
    assertTrue(xml.contains("variant-tag"));
    assertTrue(xml.contains("variant-group"));
    assertTrue(xml.contains("variant-theme"));
    assertEquals(1, countOccurrences(xml, "shared-tag"));
    assertEquals(1, countOccurrences(xml, "shared-group"));
    assertEquals(1, countOccurrences(xml, "shared-theme"));
    assertTrue(xml.contains("<Min x=\"-1.0\" y=\"0.0\" z=\"-2.0\"/>"));
    assertTrue(xml.contains("<Max x=\"4.0\" y=\"5.0\" z=\"6.0\"/>"));
  }

  @Test
  public void createXmlFileCopiesExistingXmlWhenRebuildIsNotForced() throws Exception {
    ModelResourceExporter exporter = new ModelResourceExporter("testProp", ModelClassData.PROP_CLASS_DATA);
    exporter.addResource("TestProp", "Default", "ALICE", null, null);

    Path root = ExporterTestFixtures.workDir("model-resource-xml-copy");
    Path existing = root.resolve("existing.xml");
    Files.writeString(existing, "sentinel-xml", StandardCharsets.UTF_8);
    exporter.setXMLFile(existing.toFile());

    File written = exporter.createXMLFile(root.toString(), false);

    assertTrue(written.isFile());
    assertEquals("sentinel-xml", Files.readString(written.toPath(), StandardCharsets.UTF_8));
  }

  private static int countOccurrences(String source, String token) {
    int count = 0;
    int index = 0;
    while ((index = source.indexOf(token, index)) >= 0) {
      count++;
      index += token.length();
    }
    return count;
  }
}

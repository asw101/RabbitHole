package org.lgna.story.resourceutilities;

import org.alice.math.immutable.AxisAlignedBox;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ModelSubResourceExporterTest {
  @Test
  public void accessorMethodsReturnConstructorValues() {
    ModelSubResourceExporter exporter = new ModelSubResourceExporter("Rabbit", "Default", "ALICE", "Artist", "2025");

    assertEquals("Rabbit", exporter.getModelName());
    assertEquals("Default", exporter.getTextureName());
    assertEquals("ALICE", exporter.getTypeString());
    assertEquals("Artist", exporter.getAttributionName());
    assertEquals("2025", exporter.getAttributionYear());
  }

  @Test
  public void tagsAndBoundingBoxesCanBeAccumulated() {
    ModelSubResourceExporter exporter = new ModelSubResourceExporter("Rabbit", "BlueStripe", "SIMS2", null, null);
    AxisAlignedBox box = AxisAlignedBox.createAxisAlignedBox(-1.0, 0.0, -2.0, 1.0, 3.0, 2.0);

    exporter.addTags("cute", "pet");
    exporter.addGroupTags("mammal");
    exporter.addThemeTags("farm");
    exporter.setBbox(box);

    assertEquals(2, exporter.getTags().size());
    assertEquals("cute", exporter.getTags().get(0));
    assertEquals("pet", exporter.getTags().get(1));
    assertEquals("mammal", exporter.getGroupTags().get(0));
    assertEquals("farm", exporter.getThemeTags().get(0));
    assertSame(box, exporter.getBbox());
  }
}

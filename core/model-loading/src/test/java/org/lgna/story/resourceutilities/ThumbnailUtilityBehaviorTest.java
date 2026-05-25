package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.image.ImageUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.lgna.story.implementation.alice.AliceResourceUtilities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ThumbnailUtilityBehaviorTest {
  @Test
  public void abstractThumbnailMakerComputesBordersAndFramingFromTransparentPixels() {
    BufferedImage image = new BufferedImage(6, 5, BufferedImage.TYPE_INT_ARGB);
    paintOpaquePixel(image, 2, 1);
    paintOpaquePixel(image, 3, 3);

    Assert.assertTrue(AbstractThumbnailMaker.isTransparent(0x00000000));
    Assert.assertFalse(AbstractThumbnailMaker.isTransparent(0xff000000));
    Assert.assertEquals(2, AbstractThumbnailMaker.getLeftBorder(image));
    Assert.assertEquals(3, AbstractThumbnailMaker.getRightBorder(image));
    Assert.assertEquals(1, AbstractThumbnailMaker.getTopBorder(image));
    Assert.assertEquals(2, AbstractThumbnailMaker.getBottomBorder(image));
    Assert.assertTrue(AbstractThumbnailMaker.isFullyFramed(image));

    paintOpaquePixel(image, 0, 0);
    Assert.assertFalse(AbstractThumbnailMaker.isFullyFramed(image));
  }

  @Test
  public void abstractThumbnailMakerReturnsZeroBordersForFullyTransparentImages() {
    BufferedImage image = new BufferedImage(4, 3, BufferedImage.TYPE_INT_ARGB);

    Assert.assertEquals(0, AbstractThumbnailMaker.getLeftBorder(image));
    Assert.assertEquals(0, AbstractThumbnailMaker.getRightBorder(image));
    Assert.assertEquals(0, AbstractThumbnailMaker.getTopBorder(image));
    Assert.assertEquals(0, AbstractThumbnailMaker.getBottomBorder(image));
    Assert.assertTrue(AbstractThumbnailMaker.isFullyFramed(image));
  }

  @Test
  public void modelResourceThumbnailWriterCreatesSubresourceAndClassThumbs() throws Exception {
    Path root = newWorkDir("writer-success");
    ModelSubResourceExporter subResource = new ModelSubResourceExporter("Hero", "Default", "type", null, null);
    BufferedImage thumbnail = solidImage(4, 4, Color.BLUE);

    List<java.io.File> files = ModelResourceThumbnailWriter.saveThumbnailsToDir(
        root.toString(),
        "org.example.gallery",
        "HeroResource",
        "hero-resource",
        null,
        Map.of(subResource, thumbnail),
        List.of(subResource));

    String firstThumbName = AliceResourceUtilities.getThumbnailResourceFileName("Hero", "Default");
    String classThumbName = AliceResourceUtilities.getThumbnailResourceFileName("HeroResource", null);
    java.io.File firstThumb = files.stream().filter(file -> file.getName().equals(firstThumbName)).findFirst().orElse(null);
    java.io.File classThumb = files.stream().filter(file -> file.getName().equals(classThumbName)).findFirst().orElse(null);

    Assert.assertEquals(2, files.size());
    Assert.assertNotNull(firstThumb);
    Assert.assertNotNull(classThumb);
    Assert.assertTrue(firstThumb.isFile());
    Assert.assertTrue(classThumb.isFile());
    Assert.assertNotNull(ImageUtilities.read(classThumb));
  }

  @Test
  public void modelResourceThumbnailWriterRejectsMissingSubresourcesAndNullImages() throws Exception {
    Path root = newWorkDir("writer-errors");
    ModelSubResourceExporter subResource = new ModelSubResourceExporter("Hero", "Default", "type", null, null);

    IOException noSubresources = Assert.assertThrows(
        IOException.class,
        () -> ModelResourceThumbnailWriter.saveThumbnailsToDir(
            root.toString(),
            "org.example.gallery",
            "HeroResource",
            "hero-resource",
            null,
            Map.of(subResource, solidImage(2, 2, Color.RED)),
            List.of()));
    Assert.assertTrue(noSubresources.getMessage().contains("no sub resources"));

    IOException nullImage = Assert.assertThrows(
        IOException.class,
        () -> ModelResourceThumbnailWriter.saveThumbnailsToDir(
            root.toString(),
            "org.example.gallery",
            "HeroResource",
            "hero-resource",
            null,
            java.util.Collections.singletonMap(subResource, null),
            List.of(subResource)));
    Assert.assertTrue(nullImage.getMessage().contains("image is null"));
  }

  private static BufferedImage solidImage(int width, int height, Color color) {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D graphics = image.createGraphics();
    try {
      graphics.setColor(color);
      graphics.fillRect(0, 0, width, height);
    } finally {
      graphics.dispose();
    }
    return image;
  }

  private static void paintOpaquePixel(BufferedImage image, int x, int y) {
    image.setRGB(x, y, 0xff00ff00);
  }

  private static Path newWorkDir(String name) throws IOException {
    Path dir = Path.of("target", "test-work", ThumbnailUtilityBehaviorTest.class.getSimpleName(), name).toAbsolutePath();
    deleteRecursively(dir);
    Files.createDirectories(dir);
    return dir;
  }

  private static void deleteRecursively(Path path) throws IOException {
    if (Files.notExists(path)) {
      return;
    }
    try (java.util.stream.Stream<Path> stream = Files.walk(path)) {
      stream.sorted(java.util.Comparator.reverseOrder()).forEach(current -> {
        try {
          Files.deleteIfExists(current);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }
}

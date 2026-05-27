package org.lgna.story.resourceutilities;

import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.lgna.story.resourceutilities.exporterutils.collada.COLLADA;
import org.lgna.story.resourceutilities.exporterutils.collada.Controller;
import org.lgna.story.resourceutilities.exporterutils.collada.LibraryControllers;
import org.lgna.story.resourceutilities.exporterutils.collada.LibraryGeometries;
import org.lgna.story.resourceutilities.exporterutils.collada.LibraryVisualScenes;
import org.lgna.story.resourceutilities.exporterutils.collada.VisualScene;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JointedModelColladaExporterXmlTest {
  @Test
  public void createColladaBuildsSceneGeometryAndControllers() throws Exception {
    ModelManifest.ModelVariant variant = ExporterTestFixtures.createVariant("robotStructure", "RobotTexture");
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        ExporterTestFixtures.createVisual(true),
        variant,
        "robot",
        "resource/path",
        Map.of("ROOT", "ROOT_ALIAS"));

    COLLADA collada = invokeCreateCollada(exporter);
    List<Object> libraries = collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras();
    LibraryVisualScenes visualScenes = libraries.stream()
        .filter(LibraryVisualScenes.class::isInstance)
        .map(LibraryVisualScenes.class::cast)
        .findFirst()
        .orElseThrow();
    LibraryGeometries geometries = libraries.stream()
        .filter(LibraryGeometries.class::isInstance)
        .map(LibraryGeometries.class::cast)
        .findFirst()
        .orElseThrow();
    LibraryControllers controllers = libraries.stream()
        .filter(LibraryControllers.class::isInstance)
        .map(LibraryControllers.class::cast)
        .findFirst()
        .orElseThrow();

    VisualScene scene = visualScenes.getVisualScene().get(0);
    assertEquals("1.4.1", collada.getVersion());
    assertEquals("#RobotTexture", collada.getScene().getInstanceVisualScene().getUrl());
    assertEquals("RobotTexture", scene.getId());
    assertEquals("ROOT_ALIAS", scene.getNode().get(0).getId());
    assertFalse(geometries.getGeometry().isEmpty());
    assertFalse(controllers.getController().isEmpty());
    Controller controller = controllers.getController().get(0);
    assertNotNull(controller.getSkin());
  }

  @Test
  public void writeColladaIncludesRenamedJointAndTextureUris() throws Exception {
    ModelManifest.ModelVariant variant = ExporterTestFixtures.createVariant("robotStructure", "RobotTexture");
    JointedModelColladaExporter exporter = new JointedModelColladaExporter(
        ExporterTestFixtures.createVisual(true),
        variant,
        "robot",
        "resource/path",
        Collections.singletonMap("ROOT", "ROOT_ALIAS"));

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    exporter.writeCollada(outputStream);
    String xml = outputStream.toString(StandardCharsets.UTF_8);

    assertTrue(xml.contains("<COLLADA"));
    assertTrue(xml.contains("ROOT_ALIAS"));
    assertTrue(xml.contains("RobotTexture_material_0_diffuseMap.png"));
    assertTrue(xml.contains("material_0_shader"));
  }

  private static COLLADA invokeCreateCollada(JointedModelColladaExporter exporter) throws Exception {
    Method method = JointedModelColladaExporter.class.getDeclaredMethod("createCollada");
    method.setAccessible(true);
    return (COLLADA) method.invoke(exporter);
  }
}

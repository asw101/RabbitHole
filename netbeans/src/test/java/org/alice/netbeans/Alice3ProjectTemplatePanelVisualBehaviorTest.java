package org.alice.netbeans;

import org.junit.Assert;
import org.junit.Test;
import org.openide.WizardDescriptor;

import javax.swing.JTextField;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Alice3ProjectTemplatePanelVisualBehaviorTest {
  @Test
  public void readUpdateStoreAndValidateRoundTripProjectSelections() throws Exception {
    Path root = createTestDirectory("panel-visual-round-trip");
    File projectLocation = Files.createDirectories(root.resolve("projects")).toFile();
    File aliceProject = Files.write(root.resolve("world.a3p"), new byte[] {1, 2, 3}).toFile();
    Alice3ProjectTemplatePanelVisual visual = new Alice3ProjectTemplatePanelVisual(new Alice3ProjectTemplateWizardPanel());
    WizardDescriptor settings = new WizardDescriptor(new WizardDescriptor.Panel[0]);
    settings.putProperty("projdir", new File(projectLocation, "ImportedWorld"));
    settings.putProperty("name", "ImportedWorld");

    visual.read(settings);
    Assert.assertEquals(projectLocation.getAbsolutePath(), textField(visual, "projectLocationTextField").getText());
    Assert.assertEquals("ImportedWorld", textField(visual, "projectNameTextField").getText());

    List<PropertyChangeEvent> events = new ArrayList<>();
    visual.addPropertyChangeListener(events::add);
    textField(visual, "aliceWorldLocationTextField").setText(aliceProject.getAbsolutePath());
    textField(visual, "projectLocationTextField").setText(projectLocation.getAbsolutePath());
    textField(visual, "projectNameTextField").setText("ImportedWorld");

    Assert.assertEquals(new File(projectLocation, "ImportedWorld").getAbsolutePath(), textField(visual, "createdFolderTextField").getText());
    Assert.assertTrue(visual.valid(settings));
    Assert.assertEquals("", settings.getProperty("WizardPanel_errorMessage"));

    visual.store(settings);
    Assert.assertEquals(aliceProject.getCanonicalFile(), ((File) settings.getProperty("aliceProjectFile")).getCanonicalFile());
    Assert.assertEquals(new File(projectLocation, "ImportedWorld").getCanonicalFile(), ((File) settings.getProperty("projdir")).getCanonicalFile());
    Assert.assertEquals("ImportedWorld", settings.getProperty("name"));
    Assert.assertTrue(events.stream().anyMatch(event -> Alice3ProjectTemplatePanelVisual.PROP_PROJECT_NAME.equals(event.getPropertyName())));
  }

  @Test
  public void validationRejectsExistingNonEmptyDestinationFolder() throws Exception {
    Path root = createTestDirectory("panel-visual-non-empty");
    File projectLocation = Files.createDirectories(root.resolve("projects")).toFile();
    File aliceProject = Files.write(root.resolve("world.a3p"), new byte[] {1}).toFile();
    File destination = new File(projectLocation, "ExistingWorld");
    Assert.assertTrue(destination.mkdirs());
    Files.write(destination.toPath().resolve("Program.java"), new byte[] {1, 2, 3});

    Alice3ProjectTemplatePanelVisual visual = new Alice3ProjectTemplatePanelVisual(new Alice3ProjectTemplateWizardPanel());
    textField(visual, "aliceWorldLocationTextField").setText(aliceProject.getAbsolutePath());
    textField(visual, "projectLocationTextField").setText(projectLocation.getAbsolutePath());
    textField(visual, "projectNameTextField").setText("ExistingWorld");
    WizardDescriptor settings = new WizardDescriptor(new WizardDescriptor.Panel[0]);

    Assert.assertFalse(visual.valid(settings));
    Assert.assertEquals("Project Folder already exists and is not empty.", settings.getProperty("WizardPanel_errorMessage"));
  }

  private static JTextField textField(Alice3ProjectTemplatePanelVisual visual, String name) throws Exception {
    Field field = Alice3ProjectTemplatePanelVisual.class.getDeclaredField(name);
    field.setAccessible(true);
    return (JTextField) field.get(visual);
  }

  private static Path createTestDirectory(String name) throws Exception {
    Path directory = Path.of("target", "test-data", name);
    deleteRecursively(directory);
    Files.createDirectories(directory);
    return directory;
  }

  private static void deleteRecursively(Path path) throws Exception {
    if (!Files.exists(path)) {
      return;
    }
    try (var walk = Files.walk(path)) {
      walk.sorted(Comparator.reverseOrder()).forEach(current -> {
        try {
          Files.deleteIfExists(current);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
    }
  }
}

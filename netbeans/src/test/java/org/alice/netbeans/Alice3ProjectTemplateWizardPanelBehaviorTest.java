package org.alice.netbeans;

import org.junit.Assert;
import org.junit.Test;
import org.openide.WizardDescriptor;

import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

public class Alice3ProjectTemplateWizardPanelBehaviorTest {
  @Test
  public void panelCreatesVisualFiresChangesAndStoresValidatedSettings() throws Exception {
    Path root = createTestDirectory("wizard-panel-behavior");
    File projectLocation = Files.createDirectories(root.resolve("projects")).toFile();
    File aliceProject = Files.write(root.resolve("world.a3p"), new byte[] {1, 2}).toFile();

    Alice3ProjectTemplateWizardPanel panel = new Alice3ProjectTemplateWizardPanel();
    panel.getComponent();
    WizardDescriptor settings = new WizardDescriptor(new WizardDescriptor.Panel[0]);
    settings.putProperty("projdir", new File(projectLocation, "ImportedWorld"));
    settings.putProperty("name", "ImportedWorld");

    panel.readSettings(settings);
    Alice3ProjectTemplatePanelVisual visual = (Alice3ProjectTemplatePanelVisual) panel.getComponent();
    Assert.assertSame(visual, panel.getComponent());
    Assert.assertNotNull(panel.getHelp());
    Assert.assertTrue(panel.isFinishPanel());

    textField(visual, "aliceWorldLocationTextField").setText(aliceProject.getAbsolutePath());
    textField(visual, "projectLocationTextField").setText(projectLocation.getAbsolutePath());
    textField(visual, "projectNameTextField").setText("ImportedWorld");

    AtomicReference<ChangeEvent> changeEvent = new AtomicReference<>();
    javax.swing.event.ChangeListener listener = changeEvent::set;
    panel.addChangeListener(listener);
    fireChangeEvent(panel);

    Assert.assertNotNull(changeEvent.get());
    Assert.assertTrue(panel.isValid());
    panel.validate();
    panel.storeSettings(settings);
    Assert.assertEquals(new File(projectLocation, "ImportedWorld").getCanonicalFile(), ((File) settings.getProperty("projdir")).getCanonicalFile());
    panel.removeChangeListener(listener);
  }

  private static void fireChangeEvent(Alice3ProjectTemplateWizardPanel panel) {
    try {
      var method = Alice3ProjectTemplateWizardPanel.class.getDeclaredMethod("fireChangeEvent");
      method.setAccessible(true);
      method.invoke(panel);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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

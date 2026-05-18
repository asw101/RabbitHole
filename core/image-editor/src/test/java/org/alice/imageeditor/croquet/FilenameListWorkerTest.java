package org.alice.imageeditor.croquet;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FilenameListWorkerTest {
  @Test
  public void backgroundThreadFindsOnlyPngFilesRecursively() throws Exception {
    File root = TestSupport.createEmptyDirectory("filename-list-worker-recursive");
    File nested = new File(root, "nested");
    Files.createDirectories(nested.toPath());
    File first = new File(root, "first.png");
    File ignored = new File(root, "ignored.txt");
    File second = new File(nested, "second.png");
    Files.write(first.toPath(), new byte[] {1});
    Files.write(ignored.toPath(), new byte[] {2});
    Files.write(second.toPath(), new byte[] {3});

    FilenameListWorker worker = new FilenameListWorker(new FilenameComboBoxModel(), root);
    File[] result = worker.do_onBackgroundThread();
    List<String> names = Arrays.stream(result).map(File::getName).sorted().collect(Collectors.toList());

    assertEquals(Arrays.asList("first.png", "second.png"), names);
    assertEquals(root.getAbsolutePath(), worker.getRootDirectory().getAbsolutePath());
  }

  @Test
  public void nonDirectoryRootsAndEventDispatchHandlersAreSupported() throws Exception {
    File root = TestSupport.createEmptyDirectory("filename-list-worker-handlers");
    File first = new File(root, "first.png");
    File second = new File(root, "second.png");
    Files.write(first.toPath(), new byte[] {1});
    Files.write(second.toPath(), new byte[] {2});

    FilenameComboBoxModel model = new FilenameComboBoxModel();
    model.prologue();

    FilenameListWorker worker = new FilenameListWorker(model, root);
    worker.handleProcess_onEventDispatchThread(Arrays.asList(first, second));
    assertEquals(3, model.getSize());
    assertEquals(first.getAbsolutePath(), model.getElementAt(0));
    assertEquals(second.getAbsolutePath(), model.getElementAt(1));

    worker.handleDone_onEventDispatchThread(new File[] {first, second});
    assertEquals(2, model.getSize());

    FilenameListWorker nonDirectoryWorker = new FilenameListWorker(model, new File(root, "missing.png"));
    assertEquals(0, nonDirectoryWorker.do_onBackgroundThread().length);
    assertTrue(nonDirectoryWorker.getRootDirectory().getName().endsWith("missing.png"));
  }
}

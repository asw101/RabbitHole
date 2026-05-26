package org.alice.ide.recentprojects;

import org.alice.ide.projecturi.ProjectSnapshot;
import org.alice.ide.projecturi.RecentProjectCountState;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RecentProjectsListDataBehaviorTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private final RecentProjectsListData listData = RecentProjectsListData.getInstance();
  private ProjectSnapshot[] previousSnapshots;

  @Before
  public void rememberPriorState() {
    this.previousSnapshots = this.listData.toArray(ProjectSnapshot.class);
  }

  @After
  public void restorePriorState() {
    restoreSnapshots(this.previousSnapshots);
  }

  @Test
  public void handleOpenMovesExistingProjectToFrontWithoutDuplicatingIt() throws IOException {
    restoreSnapshots(new ProjectSnapshot[0]);
    File first = this.temporaryFolder.newFile("first.a3p");
    File second = this.temporaryFolder.newFile("second.a3p");

    this.listData.handleOpen(first);
    this.listData.handleOpen(second);
    this.listData.handleOpen(first);

    ProjectSnapshot[] snapshots = this.listData.toArray(ProjectSnapshot.class);
    assertEquals(2, snapshots.length);
    assertEquals(first.toURI(), snapshots[0].getUri());
    assertEquals(second.toURI(), snapshots[1].getUri());
    assertTrue(this.listData.contains(new ProjectSnapshot(first.toURI())));
    assertEquals(0, this.listData.indexOf(new ProjectSnapshot(first.toURI())));
    assertEquals(1, this.listData.indexOf(new ProjectSnapshot(second.toURI())));
  }

  @Test
  public void handleSaveRespectsConfiguredRecentProjectLimit() throws IOException {
    restoreSnapshots(new ProjectSnapshot[0]);
    int capacity = RecentProjectCountState.getInstance().getValue();
    Assume.assumeTrue("recent projects capacity must be positive for this behavior test", capacity > 0);
    List<File> files = new ArrayList<>();
    for (int index = 0; index < capacity + 2; index++) {
      files.add(this.temporaryFolder.newFile("save-" + index + ".a3p"));
    }

    for (File file : files) {
      this.listData.handleSave(file);
    }

    assertEquals(capacity, this.listData.getItemCount());
    assertEquals(files.get(files.size() - 1).toURI(), this.listData.getItemAt(0).getUri());
    assertEquals(files.get(files.size() - capacity).toURI(), this.listData.getItemAt(capacity - 1).getUri());
  }

  @Test
  public void iteratorReturnsMostRecentProjectsFirst() throws IOException {
    restoreSnapshots(new ProjectSnapshot[0]);
    File first = this.temporaryFolder.newFile("iter-first.a3p");
    File second = this.temporaryFolder.newFile("iter-second.a3p");
    File third = this.temporaryFolder.newFile("iter-third.a3p");

    this.listData.handleSave(first);
    this.listData.handleSave(second);
    this.listData.handleSave(third);

    List<ProjectSnapshot> snapshots = new ArrayList<>();
    for (ProjectSnapshot snapshot : this.listData) {
      snapshots.add(snapshot);
    }

    assertEquals(third.toURI(), snapshots.get(0).getUri());
    assertEquals(second.toURI(), snapshots.get(1).getUri());
    assertEquals(first.toURI(), snapshots.get(2).getUri());
  }

  @Test
  public void handleOpenWithNullFileLeavesRecentProjectsUnchanged() throws IOException {
    restoreSnapshots(new ProjectSnapshot[0]);
    File first = this.temporaryFolder.newFile("keep.a3p");
    this.listData.handleOpen(first);

    ProjectSnapshot[] before = this.listData.toArray(ProjectSnapshot.class);
    this.listData.handleOpen(null);

    assertArrayEquals(before, this.listData.toArray(ProjectSnapshot.class));
    assertEquals(first.toURI(), this.listData.getItemAt(0).getUri());
  }

  @Test
  public void toArrayReturnsIndependentTypedCopy() throws IOException {
    restoreSnapshots(new ProjectSnapshot[0]);
    File file = this.temporaryFolder.newFile("copy.a3p");
    this.listData.handleSave(file);

    ProjectSnapshot[] snapshots = this.listData.toArray(ProjectSnapshot.class);
    snapshots[0] = null;

    assertNotNull(this.listData.getItemAt(0));
    assertEquals(file.toURI(), this.listData.getItemAt(0).getUri());
  }

  @Test
  public void internalMutationHooksRemainUnsupported() throws IOException {
    ProjectSnapshot snapshot = new ProjectSnapshot(this.temporaryFolder.newFile("unsupported.a3p").toURI());

    assertThrows(UnsupportedOperationException.class, () -> this.listData.internalAddItem(0, snapshot));
    assertThrows(UnsupportedOperationException.class, () -> this.listData.internalRemoveItem(snapshot));
    assertThrows(UnsupportedOperationException.class, () -> this.listData.internalSetAllItems(List.of(snapshot)));
  }

  @SuppressWarnings("unchecked")
  private void restoreSnapshots(ProjectSnapshot[] snapshots) {
    try {
      Field valuesField = RecentProjectsListData.class.getDeclaredField("values");
      valuesField.setAccessible(true);
      List<ProjectSnapshot> values = (List<ProjectSnapshot>) valuesField.get(this.listData);
      values.clear();
      values.addAll(List.of(snapshots));
      Method fireContentsChanged = org.lgna.croquet.data.AbstractMutableListData.class.getDeclaredMethod("fireContentsChanged");
      fireContentsChanged.setAccessible(true);
      fireContentsChanged.invoke(this.listData);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }
}

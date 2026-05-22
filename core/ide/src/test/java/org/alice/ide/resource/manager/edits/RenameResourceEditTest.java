package org.alice.ide.resource.manager.edits;

import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.common.resources.AudioResource;

import java.util.UUID;

import static org.junit.Assert.*;

public class RenameResourceEditTest {

  private Resource createTestResource(String name) {
    AudioResource r = new AudioResource(UUID.randomUUID());
    r.setName(name);
    return r;
  }

  @Test
  public void doOrRedo_setsNextValue() {
    Resource resource = createTestResource("oldName");
    RenameResourceEdit edit = new RenameResourceEdit(null, resource, "oldName", "newName");
    edit.doOrRedoInternal(true);
    assertEquals("newName", resource.getName());
  }

  @Test
  public void undo_restoresPrevValue() {
    Resource resource = createTestResource("oldName");
    RenameResourceEdit edit = new RenameResourceEdit(null, resource, "oldName", "newName");
    edit.doOrRedoInternal(true);
    assertEquals("newName", resource.getName());
    edit.undoInternal();
    assertEquals("oldName", resource.getName());
  }

  @Test
  public void appendDescription_containsRenameInfo() {
    Resource resource = createTestResource("alpha");
    RenameResourceEdit edit = new RenameResourceEdit(null, resource, "alpha", "beta");
    StringBuilder sb = new StringBuilder();
    edit.appendDescription(sb, null);
    String desc = sb.toString();
    assertTrue(desc.contains("rename"));
    assertTrue(desc.contains("alpha"));
    assertTrue(desc.contains("beta"));
  }

  @Test
  public void doOrRedo_thenRedo_roundTrips() {
    Resource resource = createTestResource("original");
    RenameResourceEdit edit = new RenameResourceEdit(null, resource, "original", "renamed");
    edit.doOrRedoInternal(true);
    assertEquals("renamed", resource.getName());
    edit.undoInternal();
    assertEquals("original", resource.getName());
    edit.doOrRedoInternal(false);
    assertEquals("renamed", resource.getName());
  }
}

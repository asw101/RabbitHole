package org.alice.imageeditor.croquet.edits;

import org.alice.imageeditor.croquet.TestSupport;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddShapeEditTest {
  @Test
  public void doRedoUndoAndDescriptionsReflectShapeChanges() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      Shape shape = new Rectangle2D.Double(1.5, 2.5, 30.0, 40.0);
      AddShapeEdit edit = new AddShapeEdit(new UserActivity(), shape, frame);

      assertEquals("add " + shape, edit.getTerseDescription());
      assertTrue(edit.getRedoPresentation().contains("Redo:add "));
      assertTrue(edit.getUndoPresentation().contains("Undo:add "));

      edit.doOrRedoInternal(true);
      assertEquals(1, frame.getShapes().size());
      assertEquals(shape, frame.getShapes().get(0));

      edit.undoInternal();
      assertTrue(frame.getShapes().isEmpty());
    });
  }
}

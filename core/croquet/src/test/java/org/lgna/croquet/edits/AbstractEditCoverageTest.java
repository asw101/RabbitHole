package org.lgna.croquet.edits;

import org.junit.Test;
import org.lgna.croquet.CompletionModel;
import org.lgna.croquet.Group;
import org.lgna.croquet.history.UserActivity;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link AbstractEdit} — DescriptionStyle enum, isPersistent,
 * canUndo/canRedo, description formatting, and encode behavior.
 */
public class AbstractEditCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0020-ffffffffffff"), "editCov");

  // ── DescriptionStyle enum ─────────────────────────────────────────

  @Test
  public void descriptionStyle_terseNotDetailed() {
    AbstractEdit.DescriptionStyle style = AbstractEdit.DescriptionStyle.TERSE;
    assertFalse(style.isDetailed());
    assertFalse(style.isLog());
  }

  @Test
  public void descriptionStyle_detailedIsDetailed() {
    AbstractEdit.DescriptionStyle style = AbstractEdit.DescriptionStyle.DETAILED;
    assertTrue(style.isDetailed());
    assertFalse(style.isLog());
  }

  @Test
  public void descriptionStyle_logIsDetailedAndLog() {
    AbstractEdit.DescriptionStyle style = AbstractEdit.DescriptionStyle.LOG;
    assertTrue(style.isDetailed());
    assertTrue(style.isLog());
  }

  @Test
  public void descriptionStyle_valuesContainsAll() {
    AbstractEdit.DescriptionStyle[] values = AbstractEdit.DescriptionStyle.values();
    assertEquals(3, values.length);
  }

  @Test
  public void descriptionStyle_valueOf_terse() {
    assertEquals(AbstractEdit.DescriptionStyle.TERSE,
        AbstractEdit.DescriptionStyle.valueOf("TERSE"));
  }

  @Test
  public void descriptionStyle_valueOf_detailed() {
    assertEquals(AbstractEdit.DescriptionStyle.DETAILED,
        AbstractEdit.DescriptionStyle.valueOf("DETAILED"));
  }

  @Test
  public void descriptionStyle_valueOf_log() {
    assertEquals(AbstractEdit.DescriptionStyle.LOG,
        AbstractEdit.DescriptionStyle.valueOf("LOG"));
  }

  // ── ConcreteEdit: canUndo/canRedo defaults ────────────────────────

  @Test
  public void defaultCanUndo_returnsTrue() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    assertTrue(edit.canUndo());
  }

  @Test
  public void defaultCanRedo_returnsTrue() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    assertTrue(edit.canRedo());
  }

  // ── getModel with null activity ───────────────────────────────────

  @Test
  public void getModel_nullActivity_returnsNull() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    assertNull(edit.getModel());
  }

  // ── getGroup with null model ──────────────────────────────────────

  @Test
  public void getGroup_nullModel_returnsNull() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    assertNull(edit.getGroup());
  }

  // ── Description formatting ────────────────────────────────────────

  @Test
  public void getTerseDescription_returnsNonEmpty() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    String desc = edit.getTerseDescription();
    assertNotNull(desc);
    assertFalse(desc.isEmpty());
  }

  @Test
  public void getDetailedDescription_containsClassName() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    String desc = edit.getDetailedDescription();
    assertTrue(desc.contains("ConcreteTestEdit"));
  }

  @Test
  public void getLogDescription_containsClassName() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    String desc = edit.getLogDescription();
    assertTrue(desc.contains("ConcreteTestEdit"));
  }

  @Test
  public void getRedoPresentation_startsWithRedo() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void getUndoPresentation_startsWithUndo() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_equalsDetailedDescription() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    assertEquals(edit.getDetailedDescription(), edit.toString());
  }

  // ── doOrRedo ──────────────────────────────────────────────────────

  @Test
  public void doOrRedo_isDo_callsInternal() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    edit.doOrRedo(true);
    assertTrue(edit.doOrRedoCalled);
  }

  @Test
  public void doOrRedo_isRedo_callsInternal() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    edit.doOrRedo(false);
    assertTrue(edit.doOrRedoCalled);
  }

  // ── undo ──────────────────────────────────────────────────────────

  @Test
  public void undo_callsInternal() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    edit.undo();
    assertTrue(edit.undoCalled);
  }

  // ── encode (no-op in base) ────────────────────────────────────────

  @Test
  public void encode_doesNotThrow() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null);
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    edit.encode(encoder);
  }

  // ── class structure ───────────────────────────────────────────────

  @Test
  public void abstractEdit_implementsEdit() {
    assertTrue(Edit.class.isAssignableFrom(AbstractEdit.class));
  }

  @Test
  public void abstractEdit_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractEdit.class.getModifiers()));
  }

  @Test
  public void abstractEdit_isPublic() {
    assertTrue(Modifier.isPublic(AbstractEdit.class.getModifiers()));
  }

  @Test
  public void abstractEdit_implementsBinaryEncodableAndDecodable() {
    assertTrue(edu.cmu.cs.dennisc.codec.BinaryEncodableAndDecodable.class
        .isAssignableFrom(AbstractEdit.class));
  }

  // ── createCopy static method exists ───────────────────────────────

  @Test
  public void createCopy_methodExists() throws NoSuchMethodException {
    Method m = AbstractEdit.class.getMethod("createCopy", AbstractEdit.class, UserActivity.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ── description with custom content ───────────────────────────────

  @Test
  public void customDescription_appearsInTerse() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null, "custom desc");
    assertTrue(edit.getTerseDescription().contains("custom desc"));
  }

  @Test
  public void customDescription_appearsInDetailed() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null, "custom desc");
    assertTrue(edit.getDetailedDescription().contains("custom desc"));
  }

  @Test
  public void customDescription_appearsInLog() {
    ConcreteTestEdit edit = new ConcreteTestEdit(null, "custom desc");
    assertTrue(edit.getLogDescription().contains("custom desc"));
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class ConcreteTestEdit extends AbstractEdit<CompletionModel> {
    boolean doOrRedoCalled = false;
    boolean undoCalled = false;
    private final String description;

    ConcreteTestEdit(UserActivity activity) {
      this(activity, "test");
    }

    ConcreteTestEdit(UserActivity activity, String description) {
      super(activity);
      this.description = description;
    }

    @Override
    protected void doOrRedoInternal(boolean isDo) {
      doOrRedoCalled = true;
    }

    @Override
    protected void undoInternal() {
      undoCalled = true;
    }

    @Override
    protected void appendDescription(StringBuilder rv, DescriptionStyle descriptionStyle) {
      rv.append(description);
    }
  }
}

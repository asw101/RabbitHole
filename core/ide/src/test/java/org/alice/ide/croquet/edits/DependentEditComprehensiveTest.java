package org.alice.ide.croquet.edits;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import org.alice.ide.croquet.models.ResponsibleModel;
import org.junit.Test;
import org.lgna.croquet.ActionOperation;
import org.lgna.croquet.Application;
import org.lgna.croquet.CompletionModel;
import org.lgna.croquet.Group;
import org.lgna.croquet.history.UserActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class DependentEditComprehensiveTest {

  @Test
  public void dependentEdit_isPublicFinalSubclassOfAbstractEdit() {
    assertTrue(Modifier.isPublic(DependentEdit.class.getModifiers()));
    assertTrue(Modifier.isFinal(DependentEdit.class.getModifiers()));
    assertTrue(org.lgna.croquet.edits.AbstractEdit.class.isAssignableFrom(DependentEdit.class));
  }

  @Test
  public void dependentEdit_declaresTwoConstructors() {
    assertEquals(2, DependentEdit.class.getDeclaredConstructors().length);
  }

  @Test
  public void dependentEdit_userActivityConstructorIsPublic() throws Exception {
    Constructor<DependentEdit> constructor = DependentEdit.class.getConstructor(UserActivity.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void dependentEdit_binaryDecoderConstructorIsPublic() throws Exception {
    Constructor<DependentEdit> constructor = DependentEdit.class.getConstructor(BinaryDecoder.class, Object.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void dependentEdit_constructedWithNullActivity_hasNullModelAndGroup() {
    DependentEdit<?> edit = new DependentEdit<>(null);
    assertNull(edit.getModel());
    assertNull(edit.getGroup());
  }

  @Test
  public void dependentEdit_constructedWithActivity_exposesCompletionModel() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    UserActivity activity = new UserActivity();
    activity.setCompletionModel(model);
    DependentEdit<TrackingCompletionModel> edit = new DependentEdit<>(activity);
    assertSame(model, edit.getModel());
  }

  @Test
  public void dependentEdit_groupDelegatesToCompletionModel() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    UserActivity activity = new UserActivity();
    activity.setCompletionModel(model);
    DependentEdit<TrackingCompletionModel> edit = new DependentEdit<>(activity);
    assertSame(model.getGroup(), edit.getGroup());
  }

  @Test
  public void doOrRedo_trueDelegatesToResponsibleModel() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    DependentEdit<TrackingCompletionModel> edit = createEdit(model);
    edit.doOrRedo(true);
    assertEquals(1, model.doCount);
    assertTrue(model.lastDoFlag);
  }

  @Test
  public void doOrRedo_falseDelegatesToResponsibleModel() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    DependentEdit<TrackingCompletionModel> edit = createEdit(model);
    edit.doOrRedo(false);
    assertEquals(1, model.redoCount);
    assertFalse(model.lastDoFlag);
  }

  @Test
  public void undo_delegatesToResponsibleModel() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    DependentEdit<TrackingCompletionModel> edit = createEdit(model);
    edit.undo();
    assertEquals(1, model.undoCount);
  }

  @Test
  public void terseDescriptionUsesResponsibleModelAppendDescriptionWithFalseFlag() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    DependentEdit<TrackingCompletionModel> edit = createEdit(model);
    assertEquals("tracked:false", edit.getTerseDescription());
  }

  @Test
  public void undoPresentationUsesResponsibleModelDescription() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    DependentEdit<TrackingCompletionModel> edit = createEdit(model);
    assertEquals("Undo:tracked:false", edit.getUndoPresentation());
  }

  @Test
  public void redoPresentationUsesResponsibleModelDescription() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    DependentEdit<TrackingCompletionModel> edit = createEdit(model);
    assertEquals("Redo:tracked:false", edit.getRedoPresentation());
  }

  @Test
  public void detailedDescriptionUsesResponsibleModelDetailedFlag() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    DependentEdit<TrackingCompletionModel> edit = createEdit(model);
    assertTrue(edit.getDetailedDescription().endsWith("tracked:true"));
  }

  @Test
  public void logDescriptionAlsoUsesDetailedFlag() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    DependentEdit<TrackingCompletionModel> edit = createEdit(model);
    assertTrue(edit.getLogDescription().endsWith("tracked:true"));
  }

  @Test
  public void toStringDelegatesToDetailedDescription() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    DependentEdit<TrackingCompletionModel> edit = createEdit(model);
    assertEquals(edit.getDetailedDescription(), edit.toString());
  }

  @Test
  public void dependentEdit_canUndoAndRedoByDefault() {
    DependentEdit<?> edit = new DependentEdit<>(null);
    assertTrue(edit.canUndo());
    assertTrue(edit.canRedo());
  }

  @Test(expected = RuntimeException.class)
  public void doOrRedo_throwsWhenCompletionModelIsNotResponsible() {
    DependentEdit<PlainCompletionModel> edit = createEdit(new PlainCompletionModel());
    edit.doOrRedo(true);
  }

  @Test(expected = RuntimeException.class)
  public void undo_throwsWhenCompletionModelIsNotResponsible() {
    DependentEdit<PlainCompletionModel> edit = createEdit(new PlainCompletionModel());
    edit.undo();
  }

  @Test
  public void protectedOverrideMethodsRemainDeclaredOnDependentEdit() throws Exception {
    Method doOrRedo = DependentEdit.class.getDeclaredMethod("doOrRedoInternal", boolean.class);
    Method undo = DependentEdit.class.getDeclaredMethod("undoInternal");
    Method append = DependentEdit.class.getDeclaredMethod("appendDescription", StringBuilder.class,
        Class.forName("org.lgna.croquet.edits.AbstractEdit$DescriptionStyle"));
    assertTrue(Modifier.isProtected(doOrRedo.getModifiers()));
    assertTrue(Modifier.isProtected(undo.getModifiers()));
    assertTrue(Modifier.isProtected(append.getModifiers()));
  }

  @Test
  public void multipleEditsAgainstSameModelRemainDistinctInstances() {
    TrackingCompletionModel model = new TrackingCompletionModel();
    assertNotSame(createEdit(model), createEdit(model));
  }

  private static <M extends CompletionModel> DependentEdit<M> createEdit(M model) {
    UserActivity activity = new UserActivity();
    activity.setCompletionModel(model);
    return new DependentEdit<>(activity);
  }

  private static final class TrackingCompletionModel extends ActionOperation implements ResponsibleModel {
    private int doCount;
    private int redoCount;
    private int undoCount;
    private boolean lastDoFlag;

    private TrackingCompletionModel() {
      super(Application.PROJECT_GROUP, UUID.randomUUID());
    }

    @Override
    protected void perform(UserActivity activity) {
    }

    @Override
    public void doOrRedoInternal(boolean isDo) {
      lastDoFlag = isDo;
      if (isDo) {
        doCount++;
      } else {
        redoCount++;
      }
    }

    @Override
    public void undoInternal() {
      undoCount++;
    }

    @Override
    public void appendDescription(StringBuilder rv, boolean isDetailed) {
      rv.append("tracked:").append(isDetailed);
    }
  }

  private static final class PlainCompletionModel extends ActionOperation {
    private PlainCompletionModel() {
      super(Application.PROJECT_GROUP, UUID.randomUUID());
    }

    @Override
    protected void perform(UserActivity activity) {
    }

    @Override
    public Group getGroup() {
      return super.getGroup();
    }
  }
}

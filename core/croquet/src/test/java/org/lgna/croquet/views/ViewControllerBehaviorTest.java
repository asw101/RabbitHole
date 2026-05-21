package org.lgna.croquet.views;

import org.junit.Test;
import org.lgna.croquet.PopupPrepModel;
import org.lgna.croquet.history.UserActivity;

import javax.swing.JPanel;
import java.util.UUID;

import static org.junit.Assert.*;

public class ViewControllerBehaviorTest {
  @Test
  public void constructor_initializesModelOnce() {
    TestPopupPrepModel model = new TestPopupPrepModel();

    TestViewController viewController = new TestViewController(model);

    assertSame(model, viewController.getModel());
    assertEquals(1, model.initializeCount);
  }

  @Test
  public void setPopupPrepModel_addsAndRemovesMouseListeners() {
    TestViewController viewController = new TestViewController(new TestPopupPrepModel());
    int baseMouseListeners = viewController.getAwtComponent().getMouseListeners().length;
    int baseMotionListeners = viewController.getAwtComponent().getMouseMotionListeners().length;
    TestPopupPrepModel popupModel = new TestPopupPrepModel();

    viewController.setPopupPrepModel(popupModel);
    assertSame(popupModel, viewController.getPopupPrepModel());
    assertEquals(baseMouseListeners + 1, viewController.getAwtComponent().getMouseListeners().length);
    assertEquals(baseMotionListeners + 1, viewController.getAwtComponent().getMouseMotionListeners().length);

    viewController.setPopupPrepModel(null);
    assertNull(viewController.getPopupPrepModel());
    assertEquals(baseMouseListeners, viewController.getAwtComponent().getMouseListeners().length);
    assertEquals(baseMotionListeners, viewController.getAwtComponent().getMouseMotionListeners().length);
  }

  private static final class TestViewController extends ViewController<JPanel, TestPopupPrepModel> {
    private TestViewController(TestPopupPrepModel model) {
      super(model);
    }

    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }
  }

  private static class TestPopupPrepModel extends PopupPrepModel {
    private int initializeCount;

    private TestPopupPrepModel() {
      super(UUID.fromString("00000000-0000-0000-0000-000000000103"));
    }

    @Override
    protected void initialize() {
      this.initializeCount++;
      super.initialize();
    }

    @Override
    protected void perform(UserActivity activity) {
    }
  }
}

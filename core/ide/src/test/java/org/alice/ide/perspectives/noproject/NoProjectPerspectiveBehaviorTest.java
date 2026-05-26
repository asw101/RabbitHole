package org.alice.ide.perspectives.noproject;

import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import org.alice.ide.IdeApp;
import org.alice.ide.ProjectDocumentFrame;
import org.alice.ide.croquet.models.projecturi.ExitOperation;
import org.alice.ide.recentprojects.RecentProjectsMenuModel;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.MenuModel;
import org.lgna.croquet.Operation;
import org.lgna.croquet.StandardMenuItemPrepModel;
import org.lgna.croquet.ToolBarComposite;
import org.lgna.croquet.history.UserActivity;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class NoProjectPerspectiveBehaviorTest {
  @Test
  public void noProjectPerspectiveBuildsFileAndHelpMenusAroundMainComposite() {
    FakeProjectDocumentFrame frame = createFrame();

    NoProjectPerspective perspective = new NoProjectPerspective(frame);

    assertNull(perspective.getToolBarComposite());
    assertTrue(perspective.getMainComposite() instanceof MainComposite);

    List<StandardMenuItemPrepModel> children = new ArrayList<>();
    for (StandardMenuItemPrepModel child : perspective.getMenuBarComposite().getChildren()) {
      children.add(child);
    }

    assertEquals(2, children.size());
    assertTrue(children.get(0) instanceof FileMenuModel);
    assertSame(IdeApp.INSTANCE.getHelpMenu(), children.get(1));
  }

  @Test
  public void fileMenuPrepModelsPreserveNewOpenRecentAndPlatformExitBehavior() throws Exception {
    FakeProjectDocumentFrame frame = createFrame();

    StandardMenuItemPrepModel[] models = invokeCreateMenuItemPrepModels(frame);

    assertSame(frame.newProjectOperation.getMenuItemPrepModel(), models[0]);
    assertSame(frame.openProjectOperation.getMenuItemPrepModel(), models[1]);
    assertSame(MenuModel.SEPARATOR, models[2]);
    assertSame(RecentProjectsMenuModel.getInstance(), models[3]);

    if (SystemUtilities.isMac()) {
      assertEquals(4, models.length);
    } else {
      assertEquals(6, models.length);
      assertSame(MenuModel.SEPARATOR, models[4]);
      assertSame(ExitOperation.getInstance().getMenuItemPrepModel(), models[5]);
    }
  }

  private static StandardMenuItemPrepModel[] invokeCreateMenuItemPrepModels(ProjectDocumentFrame frame) throws Exception {
    Method method = FileMenuModel.class.getDeclaredMethod("createMenuItemPrepModels", ProjectDocumentFrame.class);
    method.setAccessible(true);
    return (StandardMenuItemPrepModel[]) method.invoke(null, frame);
  }

  private static FakeProjectDocumentFrame createFrame() {
    FakeProjectDocumentFrame frame = allocate(FakeProjectDocumentFrame.class);
    frame.newProjectOperation = new TestOperation("new-project");
    frame.openProjectOperation = new TestOperation("open-project");
    return frame;
  }

  @SuppressWarnings("unchecked")
  private static <T> T allocate(Class<T> type) {
    try {
      Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      Unsafe unsafe = (Unsafe) unsafeField.get(null);
      return (T) unsafe.allocateInstance(type);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private static final class FakeProjectDocumentFrame extends ProjectDocumentFrame {
    private Operation newProjectOperation;
    private Operation openProjectOperation;

    private FakeProjectDocumentFrame() {
      super(null);
    }

    @Override
    public Operation getNewProjectOperation() {
      return this.newProjectOperation;
    }

    @Override
    public Operation getOpenProjectOperation() {
      return this.openProjectOperation;
    }
  }

  private static final class TestOperation extends Operation {
    private final String name;

    private TestOperation(String name) {
      super(Application.DOCUMENT_UI_GROUP, UUID.randomUUID());
      this.name = name;
      this.setName(name);
    }

    @Override
    protected void performInActivity(UserActivity userActivity) {
      userActivity.finish();
    }

    @Override
    protected void localize() {
      this.setName(this.name);
    }
  }
}

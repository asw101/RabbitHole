package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.views.Dialog;
import org.lgna.croquet.views.Panel;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class WizardDialogCoreCompositeBehaviorTest {
  private TestWizardDialog wizard;
  private TestWizardPage pageA;
  private TestWizardPage pageB;
  private TestWizardPage pageC;

  @Before
  public void setUp() {
    CroquetTestUtils.ensureTestApplication();
    wizard = new TestWizardDialog();
    pageA = new TestWizardPage(wizard, true, WizardPageComposite.IS_GOOD_TO_GO_STATUS);
    pageB = new TestWizardPage(wizard, false, WizardPageComposite.IS_GOOD_TO_GO_STATUS);
    pageC = new TestWizardPage(wizard, false, new AbstractSeverityStatusComposite.Status() {
      @Override public boolean isGoodToGo() { return false; }
      @Override public String getText() { return "blocked"; }
    });
    wizard.addPage(pageA);
    wizard.addPage(pageB);
    wizard.addPage(pageC);
  }

  @Test
  public void handlePreActivation_skipsAutoAdvancePagesAndResetsPages() {
    wizard.handlePreActivation();

    assertEquals(1, getIndex());
    assertEquals(1, pageA.resetCount);
    assertEquals(1, pageB.resetCount);
    assertEquals(1, pageC.resetCount);
    assertTrue(wizard.getPrevOperation().isEnabled());
    assertTrue(wizard.getNextOperation().isEnabled());
  }

  @Test
  public void nextPrevAddRemoveAndIteratorReflectWizardPages() throws Exception {
    wizard.handlePreActivation();
    invokePrivate("prev");
    assertEquals(0, getIndex());

    invokePrivate("next", false);
    assertEquals(1, getIndex());

    List<TestWizardPage> pages = new ArrayList<>();
    Iterator<WizardPageComposite<?, ?>> iterator = wizard.getWizardPageIterator();
    while (iterator.hasNext()) {
      pages.add((TestWizardPage) iterator.next());
    }
    assertEquals(3, pages.size());
    assertSame(pageA, pages.get(0));
    assertSame(pageC, pages.get(2));

    wizard.removePage(pageC);
    assertEquals(2, sizeOfIterator());
    wizard.addPage(pageC);
    assertEquals(3, sizeOfIterator());
  }

  @Test
  public void createViewAndCommitReadinessReflectWizardPages() {
    wizard.handlePreActivation();

    Panel view = wizard.createViewForTest();
    assertNotNull(view);
    assertNotNull(wizard.getPrevOperation());
    assertNotNull(wizard.getNextOperation());

    wizard.updateIsGoodToGo(true);
    assertFalse(wizard.getCommitOperation().isEnabled());

    pageC.status = WizardPageComposite.IS_GOOD_TO_GO_STATUS;
    wizard.updateIsGoodToGo(true);
    assertTrue(wizard.getNextOperation().isEnabled());
  }

  private int getIndex() {
    try {
      Field field = WizardDialogCoreComposite.class.getDeclaredField("index");
      field.setAccessible(true);
      return field.getInt(wizard);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private void invokePrivate(String name, Object... args) {
    try {
      Class<?>[] types = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) {
        types[i] = args[i].getClass() == Boolean.class ? boolean.class : args[i].getClass();
      }
      Method method = WizardDialogCoreComposite.class.getDeclaredMethod(name, types);
      method.setAccessible(true);
      method.invoke(wizard, args);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private int sizeOfIterator() {
    int count = 0;
    Iterator<WizardPageComposite<?, ?>> iterator = wizard.getWizardPageIterator();
    while (iterator.hasNext()) {
      iterator.next();
      count++;
    }
    return count;
  }

  private static final class TestWizardDialog extends WizardDialogCoreComposite {
    private TestWizardDialog() {
      super(CroquetTestUtils.nextTestUUID(), new WizardPageComposite[0]);
    }

    @Override
    protected String getDefaultTitleText() {
      return "wizard";
    }

    Panel createViewForTest() {
      return super.createView();
    }

    Status getStatusForTest() {
      return super.getStatusPreRejectorCheck();
    }

    void handlePreShowDialogForTest(Dialog dialog) {
      super.handlePreShowDialog(dialog);
    }

    void handlePostHideDialogForTest() {
      super.handlePostHideDialog();
    }
  }

  private static final class TestWizardPage extends WizardPageComposite<Panel, TestWizardDialog> {
    private final boolean optional;
    private Status status;
    private int resetCount;
    private int preShowCount;
    private int postHideCount;

    private TestWizardPage(TestWizardDialog owner, boolean optional, Status status) {
      super(CroquetTestUtils.nextTestUUID(), owner);
      this.optional = optional;
      this.status = status;
    }

    @Override
    public Status getPageStatus() {
      return this.status;
    }

    @Override
    protected boolean isOptional() {
      return this.optional;
    }

    @Override
    public void resetData() {
      this.resetCount++;
    }

    @Override
    public void handlePreShowDialog() {
      this.preShowCount++;
    }

    @Override
    public void handlePostHideDialog() {
      this.postHideCount++;
    }

    @Override
    protected Panel createView() {
      return new Panel(this) {
        @Override
        protected java.awt.LayoutManager createLayoutManager(JPanel jPanel) {
          return new FlowLayout();
        }
      };
    }

  }
}

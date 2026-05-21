package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.Panel;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class BooleanStateViewTest {
  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-1300-000000000001"), "booleanViews");

  private TestBooleanState state;

  @Before
  public void setUp() {
    state = new TestBooleanState(false);
    CroquetTestUtils.removeItemListeners(state);
  }

  @Test
  public void createVerticalRadioButtons_buildsTwoButtons() throws Exception {
    JComponent awtComponent = createPanelComponent(() -> state.createVerticalRadioButtons(true));

    assertEquals(2, awtComponent.getComponentCount());
    assertTrue(awtComponent.getLayout() instanceof BoxLayout);
  }

  @Test
  public void createHorizontalToggleButtons_buildsTwoButtons() throws Exception {
    JComponent awtComponent = createPanelComponent(() -> state.createHorizontalToggleButtons(false));

    assertEquals(2, awtComponent.getComponentCount());
    assertTrue(awtComponent.getLayout() instanceof BoxLayout);
  }

  @Test
  public void setToTrueOperation_fireUpdatesState() {
    UserActivity activity = new UserActivity();

    state.getSetToTrueOperation().fire(activity);

    assertTrue(state.getValue());
    assertSame(state.getSetToTrueOperation(), activity.getCompletionModel());
  }

  @Test
  public void setToFalseOperation_fireUpdatesState() {
    state.setValueTransactionlessly(true);
    UserActivity activity = new UserActivity();

    state.getSetToFalseOperation().fire(activity);

    assertFalse(state.getValue());
    assertSame(state.getSetToFalseOperation(), activity.getCompletionModel());
  }

  @Test
  public void menuArtifacts_areAvailable() {
    assertNotNull(state.getMenuModel());
    assertNotNull(state.getMenuItemPrepModel());
  }

  private interface PanelSupplier {
    Panel create();
  }

  private JComponent createPanelComponent(PanelSupplier supplier) throws Exception {
    AtomicReference<JComponent> ref = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> ref.set(supplier.create().getAwtComponent()));
    return ref.get();
  }

  private static final class TestBooleanState extends BooleanState {
    private TestBooleanState(boolean initialValue) {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID(), initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestBooleanState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "booleanViews";
    }
  }
}

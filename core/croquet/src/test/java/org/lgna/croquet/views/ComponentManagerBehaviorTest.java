package org.lgna.croquet.views;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Manager;
import org.lgna.croquet.Model;

import javax.swing.JPanel;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class ComponentManagerBehaviorTest {

  @Before
  public void setUp() throws Exception {
    CroquetTestUtils.ensureTestApplication();
    ComponentManager.map.clear();
    getManagerMap().clear();
  }

  @After
  public void tearDown() throws Exception {
    ComponentManager.map.clear();
    getManagerMap().clear();
  }

  @Test
  public void addAndRemoveComponentTracksManagerRegistration() throws Exception {
    TestModel model = new TestModel();
    TestView component = new TestView(false, true);

    ComponentManager.addComponent(model, component);

    assertEquals(1, ComponentManager.getComponents(model).size());
    assertTrue(getManagerMap().containsKey(model.getMigrationId()));
    assertTrue(getManagerMap().get(model.getMigrationId()).contains(model));

    ComponentManager.removeComponent(model, component);

    assertTrue(ComponentManager.getComponents(model).isEmpty());
    assertFalse(getManagerMap().containsKey(model.getMigrationId()));
  }

  @Test
  public void getFirstComponentPrefersShowingComponent() {
    TestModel model = new TestModel();
    TestView visibleOnly = new TestView(false, true);
    TestView showing = new TestView(true, true);
    ComponentManager.addComponent(model, visibleOnly);
    ComponentManager.addComponent(model, showing);

    TestView first = ComponentManager.getFirstComponent(model, TestView.class, true);

    assertSame(showing, first);
  }

  @Test
  public void getFirstComponentFallsBackToVisibleWhenAllowed() {
    TestModel model = new TestModel();
    TestView visibleOnly = new TestView(false, true);
    ComponentManager.addComponent(model, visibleOnly);

    assertNull(ComponentManager.getFirstComponent(model, TestView.class, false));
    assertSame(visibleOnly, ComponentManager.getFirstComponent(model, TestView.class, true));
  }

  @Test
  public void repaintAndRevalidateBroadcastToAllRegisteredComponents() {
    TestModel model = new TestModel();
    TestView first = new TestView(false, true);
    TestView second = new TestView(false, true);
    ComponentManager.addComponent(model, first);
    ComponentManager.addComponent(model, second);

    ComponentManager.repaintAllComponents(model);
    ComponentManager.revalidateAndRepaintAllComponents(model);

    assertEquals(2, first.repaintCount);
    assertEquals(2, second.repaintCount);
    assertEquals(1, first.revalidateAndRepaintCount);
    assertEquals(1, second.revalidateAndRepaintCount);
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, Set<Model>> getManagerMap() throws Exception {
    Field field = Manager.class.getDeclaredField("mapIdToModels");
    field.setAccessible(true);
    return (Map<UUID, Set<Model>>) field.get(null);
  }

  private static final class TestModel implements Model {
    private final UUID migrationId = CroquetTestUtils.nextTestUUID();
    private boolean enabled = true;

    @Override
    public UUID getMigrationId() {
      return migrationId;
    }

    @Override
    public void relocalize() {
    }

    @Override
    public boolean isEnabled() {
      return enabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
      this.enabled = isEnabled;
    }

    @Override
    public void initializeIfNecessary() {
    }

    @Override
    public void appendUserRepr(StringBuilder sb) {
      sb.append("test-model");
    }
  }

  private static final class TestPanel extends JPanel {
    private final boolean showing;
    private final boolean visible;

    private TestPanel(boolean showing, boolean visible) {
      this.showing = showing;
      this.visible = visible;
    }

    @Override
    public boolean isShowing() {
      return showing;
    }

    @Override
    public boolean isVisible() {
      return visible;
    }
  }

  private static final class TestView extends SwingComponentView<JPanel> {
    private final boolean showing;
    private final boolean visible;
    private int repaintCount;
    private int revalidateAndRepaintCount;

    private TestView(boolean showing, boolean visible) {
      this.showing = showing;
      this.visible = visible;
    }

    @Override
    protected JPanel createAwtComponent() {
      return new TestPanel(showing, visible);
    }

    @Override
    public void repaint() {
      repaintCount++;
    }

    @Override
    public void revalidateAndRepaint() {
      revalidateAndRepaintCount++;
      repaint();
    }
  }
}

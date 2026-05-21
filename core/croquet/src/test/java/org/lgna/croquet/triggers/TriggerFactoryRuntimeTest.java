package org.lgna.croquet.triggers;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Model;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.ViewController;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.tree.TreePath;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;

import static org.junit.Assert.*;

public class TriggerFactoryRuntimeTest {
  @BeforeClass
  public static void setUpClass() {
    CroquetTestUtils.ensureTestApplication();
  }

  @Test
  public void actionEventTrigger_factoryCreatesTrigger() {
    ActionEvent event = new ActionEvent("source", 1, "command");
    UserActivity activity = ActionEventTrigger.createUserActivity(event);

    assertTrue(activity.getTrigger() instanceof ActionEventTrigger);
    assertSame(event, ((ActionEventTrigger) activity.getTrigger()).getEvent());
    activity.finish();
  }

  @Test
  public void itemEventTrigger_factoryCreatesTrigger() {
    ItemEvent event = new ItemEvent(new StubItemSelectable(), ItemEvent.ITEM_STATE_CHANGED, "item", ItemEvent.SELECTED);
    UserActivity activity = ItemEventTrigger.createUserActivity(event);

    assertTrue(activity.getTrigger() instanceof ItemEventTrigger);
    assertSame(event, ((ItemEventTrigger) activity.getTrigger()).getEvent());
    activity.finish();
  }

  @Test
  public void keyEventTrigger_factoryCreatesTrigger() {
    TestViewController controller = createViewController();
    KeyEvent event = new KeyEvent(controller.getAwtComponent(), KeyEvent.KEY_PRESSED, 1L, 0, KeyEvent.VK_A, 'a');
    UserActivity activity = KeyEventTrigger.createUserActivity(controller, event);

    assertTrue(activity.getTrigger() instanceof KeyEventTrigger);
    assertSame(controller, activity.getTrigger().getViewController());
    assertSame(event, ((KeyEventTrigger) activity.getTrigger()).getEvent());
    activity.finish();
  }

  @Test
  public void popupMenuEventTrigger_factoryCreatesTrigger() {
    TestViewController controller = createViewController();
    PopupMenuEvent event = new PopupMenuEvent(new JPopupMenu());
    UserActivity activity = PopupMenuEventTrigger.createUserActivity(controller, event);

    assertTrue(activity.getTrigger() instanceof PopupMenuEventTrigger);
    assertSame(controller, activity.getTrigger().getViewController());
    assertSame(event, ((PopupMenuEventTrigger) activity.getTrigger()).getEvent());
    activity.finish();
  }

  @Test
  public void propertyChangeEventTrigger_factoryCreatesTrigger() {
    TestViewController controller = createViewController();
    PropertyChangeEvent event = new PropertyChangeEvent("source", "name", "old", "new");
    UserActivity activity = PropertyChangeEventTrigger.createUserActivity(controller, event);

    assertTrue(activity.getTrigger() instanceof PropertyChangeEventTrigger);
    assertSame(controller, activity.getTrigger().getViewController());
    assertSame(event, ((PropertyChangeEventTrigger) activity.getTrigger()).getEvent());
    activity.finish();
  }

  @Test
  public void treeSelectionEventTrigger_factoryCreatesTrigger() {
    TreeSelectionEvent event = new TreeSelectionEvent(new JTree(), new TreePath("node"), true, null, new TreePath("node"));
    UserActivity activity = TreeSelectionEventTrigger.createUserActivity(event);

    assertTrue(activity.getTrigger() instanceof TreeSelectionEventTrigger);
    assertSame(event, ((TreeSelectionEventTrigger) activity.getTrigger()).getEvent());
    activity.finish();
  }

  @Test
  public void nullTrigger_factoryCreatesActivityWithNullTrigger() {
    UserActivity activity = NullTrigger.createUserActivity();

    assertTrue(activity.getTrigger() instanceof NullTrigger);
    activity.finish();
  }

  @Test
  public void documentEventTrigger_factoryCreatesTrigger() {
    DocumentEventTrigger trigger = DocumentEventTrigger.createUserInstance(new StubDocumentEvent());

    assertSame(trigger, trigger.getUserActivity().getTrigger());
    trigger.getUserActivity().finish();
  }

  private static TestViewController createViewController() {
    final TestViewController[] ref = new TestViewController[1];
    try {
      SwingUtilities.invokeAndWait(() -> {
        ref[0] = new TestViewController();
        ref[0].getAwtComponent();
      });
    } catch (Exception e) {
      throw new AssertionError(e);
    }
    return ref[0];
  }

  private static final class TestViewController extends ViewController<JPanel, Model> {
    private TestViewController() {
      super(null);
    }

    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }
  }

  private static final class StubItemSelectable implements ItemSelectable {
    @Override
    public Object[] getSelectedObjects() {
      return new Object[]{"item"};
    }

    @Override
    public void addItemListener(ItemListener l) {
    }

    @Override
    public void removeItemListener(ItemListener l) {
    }
  }

  private static final class StubDocumentEvent implements DocumentEvent {
    private final Document document = new PlainDocument();

    @Override
    public int getOffset() {
      return 0;
    }

    @Override
    public int getLength() {
      return 0;
    }

    @Override
    public Document getDocument() {
      return this.document;
    }

    @Override
    public EventType getType() {
      return EventType.CHANGE;
    }

    @Override
    public ElementChange getChange(Element elem) {
      return null;
    }
  }
}

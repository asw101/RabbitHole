package org.lgna.croquet.triggers;

import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.junit.Before;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.DropReceptor;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.Model;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.PopupMenu;
import org.lgna.croquet.views.ViewController;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;

public abstract class TriggerTestSupport {
  @Before
  public void setUpApplication() {
    CroquetTestUtils.ensureTestApplication();
  }

  protected static final class TestViewController extends ViewController<JPanel, Model> {
    TestViewController() {
      super(null);
    }

    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }
  }

  protected static final class TestComboBoxView extends ViewController<JComboBox<String>, Model> {
    TestComboBoxView() {
      super(null);
    }

    @Override
    protected JComboBox<String> createAwtComponent() {
      return new JComboBox<>(new String[]{"alpha", "beta"});
    }
  }

  protected static final class TestDropSite implements DropSite {
    private final String name;

    TestDropSite(String name) {
      this.name = name;
    }

    @Override
    public DropReceptor getOwningDropReceptor() {
      return null;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
      binaryEncoder.encode(this.name);
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  protected static final class TestTrigger extends Trigger {
    private final ViewController<?, ?> viewController;
    private int popupCount;

    TestTrigger(UserActivity activity, ViewController<?, ?> viewController) {
      super(activity);
      this.viewController = viewController;
    }

    @Override
    public ViewController<?, ?> getViewController() {
      return this.viewController;
    }

    @Override
    public void showPopupMenu(PopupMenu popupMenu) {
      this.popupCount++;
    }

    int getPopupCount() {
      return this.popupCount;
    }
  }

  protected static MouseEvent createMouseEvent(java.awt.Component source, int id, int button, int x, int y) {
    int modifiers = switch (button) {
      case MouseEvent.BUTTON1 -> InputEvent.BUTTON1_DOWN_MASK;
      case MouseEvent.BUTTON2 -> InputEvent.BUTTON2_DOWN_MASK;
      case MouseEvent.BUTTON3 -> InputEvent.BUTTON3_DOWN_MASK;
      default -> 0;
    };
    return new MouseEvent(source, id, System.currentTimeMillis(), modifiers, x, y, 1, false, button);
  }

  protected static KeyEvent createKeyEvent(java.awt.Component source) {
    return new KeyEvent(source, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'a');
  }

  protected static Point invokePoint(Trigger trigger) {
    try {
      Class<?> cls = trigger.getClass();
      while (cls != null) {
        try {
          Method method = cls.getDeclaredMethod("getPoint");
          method.setAccessible(true);
          return (Point) method.invoke(trigger);
        } catch (NoSuchMethodException nsme) {
          cls = cls.getSuperclass();
        }
      }
      throw new AssertionError("getPoint not found for " + trigger.getClass());
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }
}

package org.lgna.croquet;

import edu.cmu.cs.dennisc.java.awt.event.InputEventUtilities;
import org.junit.Test;

import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static org.junit.Assert.*;

public class AbstractElementDeepTest {

  @Test
  public void getKeyStroke_characterKey_includesPlatformAcceleratorByDefault() {
    KeyStroke keyStroke = AbstractElement.getKeyStroke("VK_Q");

    assertNotNull(keyStroke);
    assertEquals(KeyEvent.VK_Q, keyStroke.getKeyCode());
    assertTrue((keyStroke.getModifiers() & InputEventUtilities.getAcceleratorMask()) != 0);
  }

  @Test
  public void getKeyStroke_functionKey_canAlsoCarryExplicitShiftModifier() {
    KeyStroke keyStroke = AbstractElement.getKeyStroke("VK_F2,SHIFT_DOWN_MASK");

    assertNotNull(keyStroke);
    assertEquals(KeyEvent.VK_F2, keyStroke.getKeyCode());
    assertTrue((keyStroke.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0);
  }

  @Test
  public void getKeyStroke_platformAndShift_retainsBothModifiers() {
    KeyStroke keyStroke = AbstractElement.getKeyStroke("VK_P,PLATFORM_ACCELERATOR_MASK|SHIFT_DOWN_MASK");

    assertNotNull(keyStroke);
    assertEquals(KeyEvent.VK_P, keyStroke.getKeyCode());
    assertTrue((keyStroke.getModifiers() & InputEventUtilities.getAcceleratorMask()) != 0);
    assertTrue((keyStroke.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0);
  }

  @Test
  public void getKeyStroke_multipleExplicitModifiers_areRetained() {
    KeyStroke keyStroke = AbstractElement.getKeyStroke("VK_N,CTRL_DOWN_MASK|ALT_DOWN_MASK");

    assertNotNull(keyStroke);
    assertEquals(KeyEvent.VK_N, keyStroke.getKeyCode());
    assertTrue((keyStroke.getModifiers() & InputEvent.CTRL_DOWN_MASK) != 0);
    assertTrue((keyStroke.getModifiers() & InputEvent.ALT_DOWN_MASK) != 0);
  }

  @Test
  public void initializeIfNecessary_reentrantLocalize_runsOnlyOnce() {
    ReentrantElement element = new ReentrantElement();

    element.initializeIfNecessary();

    assertEquals(1, element.localizeCount);
  }

  @Test
  public void toString_includesSubclassAppendedDetails() {
    DetailElement element = new DetailElement();

    assertEquals("DetailElement[detail=value]", element.toString());
  }

  @Test
  public void toString_isStableAcrossCalls() {
    DetailElement element = new DetailElement();

    assertEquals(element.toString(), element.toString());
  }

  @Test
  public void getClassUsedForLocalization_defaultsToRuntimeClass() {
    DefaultElement element = new DefaultElement();

    assertEquals(DefaultElement.class, element.exposedClassUsedForLocalization());
  }

  @Test
  public void getSubKeyForLocalization_defaultsToNull() {
    assertNull(new DefaultElement().exposedSubKeyForLocalization());
  }

  @Test
  public void findDefaultLocalizedText_withoutBundle_returnsNull() {
    assertNull(new DefaultElement().exposedFindDefaultLocalizedText());
  }

  private static class DefaultElement extends AbstractElement {
    private DefaultElement() {
      super(CroquetTestUtils.nextTestUUID());
    }

    @Override
    protected void localize() {
    }

    private Class<? extends Element> exposedClassUsedForLocalization() {
      return this.getClassUsedForLocalization();
    }

    private String exposedSubKeyForLocalization() {
      return this.getSubKeyForLocalization();
    }

    private String exposedFindDefaultLocalizedText() {
      return this.findDefaultLocalizedText();
    }
  }

  private static final class ReentrantElement extends DefaultElement {
    private int localizeCount;

    @Override
    protected void localize() {
      this.localizeCount++;
      this.initializeIfNecessary();
    }
  }

  private static final class DetailElement extends DefaultElement {
    @Override
    protected void appendRepr(StringBuilder sb) {
      sb.append("detail=value");
    }
  }
}

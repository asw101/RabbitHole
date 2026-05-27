package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.animation.Animation;
import edu.cmu.cs.dennisc.animation.AnimationObserver;
import edu.cmu.cs.dennisc.animation.Animator;
import edu.cmu.cs.dennisc.animation.FrameObserver;
import org.junit.Test;
import org.lgna.story.SProgram;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProgramControlPanelTest {
  @Test
  public void constructorAddsExpectedControlsIncludingRestartAndFullScreen() {
    TrackingProgramImp program = new TrackingProgramImp();
    program.setRestartAction(new AbstractAction("Restart") {
      @Override
      public void actionPerformed(ActionEvent e) {
      }
    });

    ProgramControlPanel panel = new ProgramControlPanel(program);

    assertEquals(5, panel.getComponentCount());
    assertTrue(panel.getComponent(0) instanceof JButton);
    assertTrue(panel.getComponent(1) instanceof JLabel);
    assertTrue(panel.getComponent(2) instanceof JSlider);
    assertTrue(panel.getComponent(3) instanceof JButton);
    assertTrue(panel.getComponent(4) instanceof JToggleButton);
    assertEquals("speed: 1x", ((JLabel) panel.getComponent(1)).getText());
    for (Component component : panel.getComponents()) {
      assertFalse(component.isFocusable());
    }
  }

  @Test
  public void playPauseButtonTogglesAnimatorSpeed() {
    TrackingProgramImp program = new TrackingProgramImp();
    ProgramControlPanel panel = new ProgramControlPanel(program);
    JButton playPauseButton = (JButton) panel.getComponent(0);

    assertTrue(playPauseButton.getModel().isSelected());
    assertEquals(1.0, program.animator.getSpeedFactor(), 1e-6);

    playPauseButton.doClick();
    assertFalse(playPauseButton.getModel().isSelected());
    assertEquals(0.0, program.animator.getSpeedFactor(), 1e-6);

    playPauseButton.doClick();
    assertTrue(playPauseButton.getModel().isSelected());
    assertEquals(1.0, program.animator.getSpeedFactor(), 1e-6);
  }

  @Test
  public void sliderChangesUpdateLabelAndDelegateSpeedChange() {
    TrackingProgramImp program = new TrackingProgramImp();
    ProgramControlPanel panel = new ProgramControlPanel(program);
    JLabel label = (JLabel) panel.getComponent(1);
    JSlider slider = (JSlider) panel.getComponent(2);

    slider.setValue(4);

    assertEquals(4.0, program.lastHandledSpeed, 1e-6);
    assertEquals(4.0, program.animator.getSpeedFactor(), 1e-6);
    assertEquals("speed: 4x", label.getText());
  }

  @Test
  public void sliderReleaseWithoutControlResetsSpeedToOne() {
    TrackingProgramImp program = new TrackingProgramImp();
    ProgramControlPanel panel = new ProgramControlPanel(program);
    JLabel label = (JLabel) panel.getComponent(1);
    JSlider slider = (JSlider) panel.getComponent(2);

    slider.setValue(6);
    MouseEvent release = new MouseEvent(slider, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 0, 0, 1, false);
    for (MouseListener listener : slider.getMouseListeners()) {
      listener.mouseReleased(release);
    }

    assertEquals(1, slider.getValue());
    assertEquals(1.0, program.lastHandledSpeed, 1e-6);
    assertEquals(1.0, program.animator.getSpeedFactor(), 1e-6);
    assertEquals("speed: 1x", label.getText());
  }

  private static final class TrackingProgramImp extends ProgramImp {
    private final TrackingAnimator animator = new TrackingAnimator();
    private double lastHandledSpeed = Double.NaN;

    private TrackingProgramImp() {
      super((SProgram) null, null);
    }

    @Override
    protected void handleSpeedChange(double speedFactor) {
      this.lastHandledSpeed = speedFactor;
      super.handleSpeedChange(speedFactor);
    }

    @Override
    public Animator getAnimator() {
      return this.animator;
    }
  }

  private static final class TrackingAnimator implements Animator {
    private double speedFactor = 1.0;

    @Override
    public double getCurrentTime() {
      return 0;
    }

    @Override
    public double getSpeedFactor() {
      return this.speedFactor;
    }

    @Override
    public void setSpeedFactor(double speedFactor) {
      this.speedFactor = speedFactor;
    }

    @Override
    public void update() {
    }

    @Override
    public void invokeLater(Animation animation, AnimationObserver animationObserver) {
    }

    @Override
    public void invokeAndWait(Animation animation, AnimationObserver animationObserver)
        throws InterruptedException, InvocationTargetException {
    }

    @Override
    public void invokeAndWait_ThrowRuntimeExceptionsIfNecessary(Animation animation, AnimationObserver animationObserver) {
    }

    @Override
    public void addFrameObserver(FrameObserver runnable) {
    }

    @Override
    public void removeFrameObserver(FrameObserver runnable) {
    }

    @Override
    public void completeAll() {
    }

    @Override
    public void cancelAnimation() {
    }
  }
}

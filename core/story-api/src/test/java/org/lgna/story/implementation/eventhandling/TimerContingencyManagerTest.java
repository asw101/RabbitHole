package org.lgna.story.implementation.eventhandling;

import org.junit.Before;
import org.junit.Test;
import org.lgna.story.AddCollisionEndListener;
import org.lgna.story.AddCollisionStartListener;
import org.lgna.story.AddOcclusionEndListener;
import org.lgna.story.AddOcclusionStartListener;
import org.lgna.story.AddProximityEnterListener;
import org.lgna.story.AddProximityExitListener;
import org.lgna.story.AddViewEnterListener;
import org.lgna.story.AddViewExitListener;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SModel;
import org.lgna.story.SScene;
import org.lgna.story.SThing;
import org.lgna.story.event.*;
import org.lgna.story.implementation.EntityImp;
import org.lgna.story.implementation.ModelImp;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TimerContingencyManagerTest {
  private FakeTimerEventHandler timer;
  private TimerContingencyManager manager;
  private TestScene scene;

  @Before
  public void setUp() {
    timer = new FakeTimerEventHandler();
    manager = new TimerContingencyManager(timer);
    scene = new TestScene();
    manager.setScene(scene.getImplementation());
  }

  @Test
  public void collisionRegistrationUsesTimerFrequencyAndSceneAdapters() {
    TestThing left = new TestThing("left");
    TestThing right = new TestThing("right");
    WhileCollisionListener listener = event -> {};

    manager.register(listener, List.of(left, right), List.of(right, left), 0.5, MultipleEventPolicy.ENQUEUE);

    assertSame(listener, timer.addedListener);
    assertEquals(0.5, timer.frequency, 0.0);
    assertEquals(MultipleEventPolicy.ENQUEUE, timer.policy);
    assertEquals(List.of(listener), timer.deactivated);
    assertArrayEquals(new SThing[] {left, right}, scene.collisionStartSetA);
    assertArrayEquals(new SThing[] {right, left}, scene.collisionStartSetB);
    assertNotNull(scene.collisionStartListener);
    assertNotNull(scene.collisionEndListener);

    scene.collisionStartListener.collisionStarted(null);
    scene.collisionEndListener.collisionEnded(null);

    assertEquals(List.of(listener), timer.activated);
    assertEquals(2, timer.deactivated.size());
  }

  @Test
  public void proximityRegistrationActivatesOnEnterAndDeactivatesOnExit() {
    TestThing left = new TestThing("left");
    TestThing right = new TestThing("right");
    WhileProximityListener listener = event -> {};

    manager.register(listener, List.of(left), List.of(right), 2.0, 0.25, MultipleEventPolicy.IGNORE);

    assertEquals(0.25, timer.frequency, 0.0);
    assertEquals(List.of(listener), timer.deactivated);
    assertEquals(2.0, scene.proximityDistance, 0.0);

    scene.proximityEnterListener.proximityEntered(null);
    scene.proximityExitListener.proximityExited(null);

    assertEquals(List.of(listener), timer.activated);
    assertEquals(2, timer.deactivated.size());
  }

  @Test
  public void occlusionRegistrationConvertsModelListsAndTogglesTimer() {
    TestModel foreground = new TestModel("foreground");
    TestModel background = new TestModel("background");
    WhileOcclusionListener listener = event -> {};

    manager.register(listener, List.of(foreground), List.of(background), 1.0, MultipleEventPolicy.COMBINE);

    assertEquals(1.0, timer.frequency, 0.0);
    assertEquals(MultipleEventPolicy.COMBINE, timer.policy);
    assertArrayEquals(new SModel[] {foreground}, scene.occlusionStartSetA);
    assertArrayEquals(new SModel[] {background}, scene.occlusionStartSetB);

    scene.occlusionStartListener.occlusionStarted(null);
    scene.occlusionEndListener.occlusionEnded(null);

    assertEquals(List.of(listener), timer.activated);
    assertEquals(2, timer.deactivated.size());
  }

  @Test
  public void inViewRegistrationTracksViewEnterAndExit() {
    TestModel model = new TestModel("model");
    WhileInViewListener listener = event -> {};

    manager.register(listener, List.of(model), 0.75, MultipleEventPolicy.IGNORE);

    assertArrayEquals(new SModel[] {model}, scene.viewEnterSet);
    assertArrayEquals(new SModel[] {model}, scene.viewExitSet);
    assertEquals(List.of(listener), timer.deactivated);

    scene.viewEnterListener.viewEntered(null);
    scene.viewExitListener.viewExited(null);

    assertEquals(List.of(listener), timer.activated);
    assertEquals(2, timer.deactivated.size());
  }

  private static final class FakeTimerEventHandler extends TimerEventHandler {
    private TimeListener addedListener;
    private double frequency;
    private MultipleEventPolicy policy;
    private final List<WhileContingencyListener> activated = new ArrayList<>();
    private final List<WhileContingencyListener> deactivated = new ArrayList<>();

    @Override
    public void addListener(TimeListener timerEventListener, Double frequency, MultipleEventPolicy policy) {
      this.addedListener = timerEventListener;
      this.frequency = frequency;
      this.policy = policy;
    }

    @Override
    public void activate(WhileContingencyListener listener) {
      activated.add(listener);
    }

    @Override
    public void deactivate(WhileContingencyListener listener) {
      deactivated.add(listener);
    }
  }

  private static final class TestScene extends SScene {
    private CollisionStartListener collisionStartListener;
    private CollisionEndListener collisionEndListener;
    private ProximityEnterListener proximityEnterListener;
    private ProximityExitListener proximityExitListener;
    private OcclusionStartListener occlusionStartListener;
    private OcclusionEndListener occlusionEndListener;
    private ViewEnterListener viewEnterListener;
    private ViewExitListener viewExitListener;
    private SThing[] collisionStartSetA;
    private SThing[] collisionStartSetB;
    private SThing[] proximityEnterSetA;
    private SThing[] proximityEnterSetB;
    private double proximityDistance;
    private SModel[] occlusionStartSetA;
    private SModel[] occlusionStartSetB;
    private SModel[] viewEnterSet;
    private SModel[] viewExitSet;

    @Override
    public void addCollisionStartListener(CollisionStartListener listener, SThing[] setA, SThing[] setB, AddCollisionStartListener.Detail... details) {
      this.collisionStartListener = listener;
      this.collisionStartSetA = setA;
      this.collisionStartSetB = setB;
    }

    @Override
    public void addCollisionEndListener(CollisionEndListener listener, SThing[] setA, SThing[] setB, AddCollisionEndListener.Detail... details) {
      this.collisionEndListener = listener;
    }

    @Override
    public void addProximityEnterListener(ProximityEnterListener listener, SThing[] setA, SThing[] setB, Number distance, AddProximityEnterListener.Detail... details) {
      this.proximityEnterListener = listener;
      this.proximityEnterSetA = setA;
      this.proximityEnterSetB = setB;
      this.proximityDistance = distance.doubleValue();
    }

    @Override
    public void addProximityExitListener(ProximityExitListener listener, SThing[] setA, SThing[] setB, Number distance, AddProximityExitListener.Detail... details) {
      this.proximityExitListener = listener;
    }

    @Override
    public void addOcclusionStartListener(OcclusionStartListener listener, SModel[] setA, SModel[] setB, AddOcclusionStartListener.Detail... details) {
      this.occlusionStartListener = listener;
      this.occlusionStartSetA = setA;
      this.occlusionStartSetB = setB;
    }

    @Override
    public void addOcclusionEndListener(OcclusionEndListener listener, SModel[] setA, SModel[] setB, AddOcclusionEndListener.Detail... details) {
      this.occlusionEndListener = listener;
    }

    @Override
    public void addViewEnterListener(ViewEnterListener listener, SModel[] set, AddViewEnterListener.Detail... details) {
      this.viewEnterListener = listener;
      this.viewEnterSet = set;
    }

    @Override
    public void addViewExitListener(ViewExitListener listener, SModel[] set, AddViewExitListener.Detail... details) {
      this.viewExitListener = listener;
      this.viewExitSet = set;
    }

    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }

  private static final class TestThing extends SThing {
    private final String name;

    private TestThing(String name) {
      this.name = name;
    }

    @Override
    public <T extends EntityImp> T getImplementation() {
      return null;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void setName(String name) {
    }
  }

  private static final class TestModel extends SModel {
    private final String name;

    private TestModel(String name) {
      this.name = name;
    }

    @Override
    public ModelImp getImplementation() {
      return null;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void setName(String name) {
    }
  }
}

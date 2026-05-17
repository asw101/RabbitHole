package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.pattern.Visitable;
import edu.cmu.cs.dennisc.pattern.Visitor;
import edu.cmu.cs.dennisc.pattern.event.ReleaseEvent;
import edu.cmu.cs.dennisc.pattern.event.ReleaseListener;
import edu.cmu.cs.dennisc.scenegraph.event.ComponentAddedEvent;
import edu.cmu.cs.dennisc.scenegraph.event.ComponentRemovedEvent;
import edu.cmu.cs.dennisc.scenegraph.event.ComponentsListener;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CompositeDeepHierarchyTest {

  @Test
  public void visitorTraversesEntireSubtreeInDepthFirstOrder() {
    Scene scene = new Scene();
    scene.setName("scene");
    Transformable parent = named("parent");
    Transformable child = named("child");
    Transformable grandchild = named("grandchild");

    scene.addComponent(parent);
    parent.addComponent(child);
    child.addComponent(grandchild);

    List<String> visited = new ArrayList<>();
    scene.accept(new Visitor() {
      @Override
      public void visit(Visitable v) {
        if (v instanceof Element e) {
          visited.add(e.getName());
        }
      }
    });

    assertEquals(4, visited.size());
    assertEquals("scene", visited.get(0));
    assertEquals("parent", visited.get(1));
    assertEquals("child", visited.get(2));
    assertEquals("grandchild", visited.get(3));
  }

  @Test
  public void newCopyProducesDeepCopyWithCorrectParentReferences() {
    Scene scene = new Scene();
    scene.setName("scene");
    Transformable childA = named("childA");
    Transformable childB = named("childB");
    Transformable grandA = named("grandA");
    Transformable grandB = named("grandB");

    scene.addComponent(childA);
    scene.addComponent(childB);
    childA.addComponent(grandA);
    childB.addComponent(grandB);

    Element copy = scene.newCopy();
    assertNotNull(copy);
    assertNotSame(scene, copy);
    assertTrue(copy instanceof Scene);

    Scene sceneCopy = (Scene) copy;
    assertEquals(2, sceneCopy.getComponentCount());

    Component copyChildA = sceneCopy.getComponentAt(0);
    Component copyChildB = sceneCopy.getComponentAt(1);
    assertNotSame(childA, copyChildA);
    assertNotSame(childB, copyChildB);
    assertSame(sceneCopy, copyChildA.getParent());
    assertSame(sceneCopy, copyChildB.getParent());

    assertEquals("childA", copyChildA.getName());
    assertEquals("childB", copyChildB.getName());

    // Verify grandchildren were deep-copied
    Composite compositeA = (Composite) copyChildA;
    assertEquals(1, compositeA.getComponentCount());
    Component copyGrandA = compositeA.getComponentAt(0);
    assertNotSame(grandA, copyGrandA);
    assertSame(compositeA, copyGrandA.getParent());
    assertEquals("grandA", copyGrandA.getName());
  }

  @Test
  public void releasePropagatesToAllDescendants() {
    Scene scene = new Scene();
    scene.setName("root");
    Transformable child = named("child");
    Transformable grandchild = named("grandchild");

    scene.addComponent(child);
    child.addComponent(grandchild);

    List<String> releasingCalls = new ArrayList<>();
    List<String> releasedCalls = new ArrayList<>();
    ReleaseListener listener = new ReleaseListener() {
      @Override
      public void releasing(ReleaseEvent e) {
        releasingCalls.add(((Element) e.getSource()).getName());
      }

      @Override
      public void released(ReleaseEvent e) {
        releasedCalls.add(((Element) e.getSource()).getName());
      }
    };

    scene.addReleaseListener(listener);
    child.addReleaseListener(listener);
    grandchild.addReleaseListener(listener);

    scene.release();

    assertEquals(3, releasingCalls.size());
    assertEquals(3, releasedCalls.size());
    assertTrue(releasingCalls.contains("root"));
    assertTrue(releasingCalls.contains("child"));
    assertTrue(releasingCalls.contains("grandchild"));
  }

  @Test(expected = RuntimeException.class)
  public void removeComponentThrowsWhenChildBelongsToOtherParent() {
    Scene parent1 = new Scene();
    Scene parent2 = new Scene();
    Transformable child = new Transformable();
    parent1.addComponent(child);

    parent2.removeComponent(child);
  }

  @Test
  public void childrenListenerReceivesEventsOnlyWhileRegistered() {
    Scene scene = new Scene();
    AtomicInteger addCount = new AtomicInteger();
    ComponentsListener listener = new ComponentsListener() {
      @Override
      public void componentAdded(ComponentAddedEvent event) {
        addCount.incrementAndGet();
      }

      @Override
      public void componentRemoved(ComponentRemovedEvent event) {
      }
    };

    scene.addChildrenListener(listener);
    scene.addComponent(new Transformable());
    assertEquals(1, addCount.get());

    scene.removeChildrenListener(listener);
    scene.addComponent(new Transformable());
    assertEquals(1, addCount.get());
  }

  @Test
  public void getComponentsReturnsLiveIterableOverChildren() {
    Scene scene = new Scene();
    Transformable first = named("first");
    Transformable second = named("second");

    scene.addComponent(first);
    scene.addComponent(second);

    List<String> names = new ArrayList<>();
    for (Component c : scene.getComponents()) {
      names.add(c.getName());
    }
    assertEquals(2, names.size());
    assertEquals("first", names.get(0));
    assertEquals("second", names.get(1));
    assertEquals(2, scene.getComponentCount());

    Transformable third = named("third");
    scene.addComponent(third);

    names.clear();
    for (Component c : scene.getComponents()) {
      names.add(c.getName());
    }
    assertEquals(3, names.size());
    assertEquals("third", names.get(2));
  }

  @Test
  public void absoluteTransformationPropagatesThroughDeepHierarchy() {
    Scene scene = new Scene();
    Transformable lvl1 = named("lvl1");
    Transformable lvl2 = named("lvl2");
    Transformable lvl3 = named("lvl3");

    scene.addComponent(lvl1);
    lvl1.addComponent(lvl2);
    lvl2.addComponent(lvl3);

    AtomicInteger sceneEvents = new AtomicInteger();
    AtomicInteger lvl1Events = new AtomicInteger();
    AtomicInteger lvl2Events = new AtomicInteger();
    AtomicInteger lvl3Events = new AtomicInteger();

    scene.addAbsoluteTransformationListener(e -> sceneEvents.incrementAndGet());
    lvl1.addAbsoluteTransformationListener(e -> lvl1Events.incrementAndGet());
    lvl2.addAbsoluteTransformationListener(e -> lvl2Events.incrementAndGet());
    lvl3.addAbsoluteTransformationListener(e -> lvl3Events.incrementAndGet());

    // notifyTransformationListeners triggers fireAbsoluteTransformationChange cascade
    lvl1.notifyTransformationListeners();

    assertTrue("lvl1 should have received event", lvl1Events.get() >= 1);
    assertTrue("lvl2 should have received event", lvl2Events.get() >= 1);
    assertTrue("lvl3 should have received event", lvl3Events.get() >= 1);
  }

  @Test
  public void hierarchyChangeFiresOnAllDescendantsWhenSubtreeReparented() {
    Scene scene = new Scene();
    Transformable parent = named("parent");
    Transformable child = named("child");
    Transformable grandchild = named("grandchild");

    parent.addComponent(child);
    child.addComponent(grandchild);
    scene.addComponent(parent);

    AtomicInteger childHierarchy = new AtomicInteger();
    AtomicInteger grandchildHierarchy = new AtomicInteger();

    child.addHierarchyListener(e -> childHierarchy.incrementAndGet());
    grandchild.addHierarchyListener(e -> grandchildHierarchy.incrementAndGet());

    // Detach parent from scene — fires hierarchy change on all descendants
    scene.removeComponent(parent);

    assertTrue("child should have received hierarchy event", childHierarchy.get() >= 1);
    assertTrue("grandchild should have received hierarchy event", grandchildHierarchy.get() >= 1);
  }

  @Test
  public void isDescendantOfReturnsFalseForUnrelatedNode() {
    Scene scene1 = new Scene();
    Scene scene2 = new Scene();
    Transformable child1 = new Transformable();
    Transformable child2 = new Transformable();

    scene1.addComponent(child1);
    scene2.addComponent(child2);

    assertFalse(child1.isDescendantOf(scene2));
    assertFalse(child2.isDescendantOf(scene1));
    assertTrue(child1.isDescendantOf(scene1));
    assertTrue(child2.isDescendantOf(scene2));
  }

  @Test
  public void getRootReturnsNullWhenDetached() {
    Transformable orphan = new Transformable();
    assertNull(orphan.getRoot());
  }

  @Test
  public void isAncestorOfReturnsTrueForDescendant() {
    Scene scene = new Scene();
    Transformable child = named("child");
    Transformable grandchild = named("grandchild");

    scene.addComponent(child);
    child.addComponent(grandchild);

    assertTrue(scene.isAncestorOf(grandchild));
    assertTrue(scene.isAncestorOf(child));
    assertTrue(child.isAncestorOf(grandchild));
    assertFalse(grandchild.isAncestorOf(scene));
  }

  @Test
  public void isAncestorOfReturnsFalseForNull() {
    Scene scene = new Scene();
    assertFalse(scene.isAncestorOf(null));
  }

  @Test
  public void getIndexOfComponentReturnsCorrectIndex() {
    Scene scene = new Scene();
    Transformable a = named("a");
    Transformable b = named("b");
    Transformable c = named("c");

    scene.addComponent(a);
    scene.addComponent(b);
    scene.addComponent(c);

    assertEquals(0, scene.getIndexOfComponent(a));
    assertEquals(1, scene.getIndexOfComponent(b));
    assertEquals(2, scene.getIndexOfComponent(c));
  }

  @Test
  public void getComponentsAsArrayReturnsCorrectArray() {
    Scene scene = new Scene();
    Transformable a = named("a");
    Transformable b = named("b");

    scene.addComponent(a);
    scene.addComponent(b);

    Component[] array = scene.getComponentsAsArray();
    assertEquals(2, array.length);
    assertSame(a, array[0]);
    assertSame(b, array[1]);
  }

  @Test
  public void getChildrenListenersReturnsRegisteredListeners() {
    Scene scene = new Scene();
    ComponentsListener listener = new ComponentsListener() {
      @Override
      public void componentAdded(ComponentAddedEvent event) {}
      @Override
      public void componentRemoved(ComponentRemovedEvent event) {}
    };

    scene.addChildrenListener(listener);
    boolean found = false;
    for (ComponentsListener l : scene.getChildrenListeners()) {
      if (l == listener) {
        found = true;
        break;
      }
    }
    assertTrue("Should find registered listener", found);
  }

  @Test
  public void componentRemovedEventFires() {
    Scene scene = new Scene();
    Transformable child = named("child");
    scene.addComponent(child);

    AtomicInteger removeCount = new AtomicInteger();
    scene.addChildrenListener(new ComponentsListener() {
      @Override
      public void componentAdded(ComponentAddedEvent event) {}
      @Override
      public void componentRemoved(ComponentRemovedEvent event) {
        removeCount.incrementAndGet();
      }
    });

    scene.removeComponent(child);
    assertEquals(1, removeCount.get());
  }

  @Test
  public void getRootReturnsSceneForAttachedNode() {
    Scene scene = new Scene();
    Transformable parent = named("parent");
    Transformable child = named("child");

    scene.addComponent(parent);
    parent.addComponent(child);

    assertSame(scene, child.getRoot());
    assertSame(scene, parent.getRoot());
  }

  @Test
  public void setParentDirectlyReparentsComponent() {
    Scene scene = new Scene();
    Transformable parent1 = named("p1");
    Transformable parent2 = named("p2");
    Transformable child = named("child");

    scene.addComponent(parent1);
    scene.addComponent(parent2);
    parent1.addComponent(child);

    assertSame(parent1, child.getParent());
    assertEquals(1, parent1.getComponentCount());
    assertEquals(0, parent2.getComponentCount());

    child.setParent(parent2);

    assertSame(parent2, child.getParent());
    assertEquals(0, parent1.getComponentCount());
    assertEquals(1, parent2.getComponentCount());
  }

  @Test
  public void setParentToSameParentIsNoOp() {
    Scene scene = new Scene();
    Transformable child = named("child");
    scene.addComponent(child);

    AtomicInteger events = new AtomicInteger();
    scene.addChildrenListener(new ComponentsListener() {
      @Override
      public void componentAdded(ComponentAddedEvent event) {
        events.incrementAndGet();
      }
      @Override
      public void componentRemoved(ComponentRemovedEvent event) {
        events.incrementAndGet();
      }
    });

    // Setting same parent should be no-op
    child.setParent(scene);
    assertEquals(0, events.get());
  }

  private static Transformable named(String name) {
    Transformable t = new Transformable();
    t.setName(name);
    return t;
  }
}

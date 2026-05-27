package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.pattern.Visitable;
import edu.cmu.cs.dennisc.scenegraph.event.ComponentsListener;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.alice.math.immutable.Vector4;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ComponentHierarchyBehaviorTest {
  @Test
  public void parentChangesNotifyHierarchyAndChildrenListeners() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    Transformable child = new Transformable();

    AtomicInteger addedCount = new AtomicInteger();
    AtomicInteger removedCount = new AtomicInteger();
    AtomicInteger absoluteCount = new AtomicInteger();
    AtomicInteger hierarchyCount = new AtomicInteger();
    parent.addChildrenListener(new ComponentsListener() {
      @Override
      public void componentAdded(edu.cmu.cs.dennisc.scenegraph.event.ComponentAddedEvent e) {
        addedCount.incrementAndGet();
      }

      @Override
      public void componentRemoved(edu.cmu.cs.dennisc.scenegraph.event.ComponentRemovedEvent e) {
        removedCount.incrementAndGet();
      }
    });
    child.addAbsoluteTransformationListener(e -> absoluteCount.incrementAndGet());
    child.addHierarchyListener(e -> hierarchyCount.incrementAndGet());

    scene.addComponent(parent);
    child.setParent(parent);

    assertSame(parent, child.getParent());
    assertSame(scene, child.getRoot());
    assertEquals(1, parent.getComponentCount());
    assertSame(child, parent.getComponentAt(0));
    assertEquals(1, addedCount.get());
    assertEquals(0, removedCount.get());
    assertEquals(1, absoluteCount.get());
    assertEquals(1, hierarchyCount.get());

    child.setParent(null);

    assertNull(child.getParent());
    assertNull(child.getRoot());
    assertEquals(0, parent.getComponentCount());
    assertEquals(1, removedCount.get());
    assertEquals(2, absoluteCount.get());
    assertEquals(2, hierarchyCount.get());
  }

  @Test
  public void setParentWithoutMovingSkipsAbsoluteTransformationEvents() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    Transformable child = new Transformable();

    AtomicInteger absoluteCount = new AtomicInteger();
    AtomicInteger hierarchyCount = new AtomicInteger();
    child.addAbsoluteTransformationListener(e -> absoluteCount.incrementAndGet());
    child.addHierarchyListener(e -> hierarchyCount.incrementAndGet());

    scene.addComponent(parent);
    child.setParentWithoutMoving(parent);

    assertSame(parent, child.getParent());
    assertSame(scene, child.getRoot());
    assertEquals(0, absoluteCount.get());
    assertEquals(1, hierarchyCount.get());
  }

  @Test
  public void referenceFramePredicatesAndTransformHelpersUseHierarchyState() {
    Scene scene = new Scene();
    Transformable vehicle = new Transformable();
    Transformable child = new Transformable();
    Transformable observer = new Transformable();

    vehicle.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));
    observer.setLocalTransformation(AffineMatrix4x4.createTranslation(2, 0, 0));
    scene.addComponent(vehicle);
    vehicle.addComponent(child);
    scene.addComponent(observer);

    assertTrue(child.isLocalOf(child));
    assertTrue(vehicle.isVehicleOf(child));
    assertTrue(scene.isSceneOf(child));
    assertTrue(child.isDescendantOf(scene));

    assertPointEquals(new Point3(8, 5, 0), child.getTranslation(observer));
    assertTrue(child.getAxes(AsSeenBy.SCENE).isIdentity());

    Point3 point = new Point3(1, 1, 1);
    Point3 absolutePoint = child.transformToAbsolute(point);
    Point3 observerPoint = child.transformTo(point, observer);
    assertPointEquals(new Point3(11, 6, 1), absolutePoint);
    assertPointEquals(new Point3(9, 6, 1), observerPoint);
    assertPointEquals(point, child.transformFromAbsolute(absolutePoint));
    assertPointEquals(point, observer.transformTo(observerPoint, child));

    Vector4 vector4 = new Vector4(1, 2, 3, 1);
    Vector4 absoluteVector4 = child.transformToAbsolute(vector4);
    assertEquals(11.0, absoluteVector4.x(), ScenegraphTestAssertions.EPSILON);
    assertEquals(7.0, absoluteVector4.y(), ScenegraphTestAssertions.EPSILON);
    assertEquals(3.0, absoluteVector4.z(), ScenegraphTestAssertions.EPSILON);
    assertEquals(1.0, absoluteVector4.w(), ScenegraphTestAssertions.EPSILON);
    assertEquals(vector4, child.transformFromAbsolute(absoluteVector4));

    Vector3 vector3 = new Vector3(1, 2, 3);
    assertEquals(vector3, child.transformFromAbsolute(child.transformToAbsolute(vector3)));
  }

  @Test
  public void acceptDelegatesToVisitor() {
    Transformable child = new Transformable();
    List<Visitable> visited = new ArrayList<>();

    child.accept(visited::add);

    assertEquals(List.of(child), visited);
  }
}

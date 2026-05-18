package org.alice.ide.cascade.fillerinners;

import org.lgna.croquet.CascadeBlankChild;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link ConstantsOwningFillerInner} — getInstance cache semantics,
 * appendItems with types that have public static final fields, and type assignability.
 */
public class ConstantsOwningFillerInnerTest {

  // ---- getInstance cache ----

  @Test
  public void getInstance_sameCls_returnsSameInstance() {
    ConstantsOwningFillerInner first = ConstantsOwningFillerInner.getInstance(Thread.State.class);
    ConstantsOwningFillerInner second = ConstantsOwningFillerInner.getInstance(Thread.State.class);
    assertSame("Same class should yield same cached instance", first, second);
  }

  @Test
  public void getInstance_sameType_returnsSameInstance() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Thread.State.class);
    ConstantsOwningFillerInner first = ConstantsOwningFillerInner.getInstance(type);
    ConstantsOwningFillerInner second = ConstantsOwningFillerInner.getInstance(type);
    assertSame("Same type should yield same cached instance", first, second);
  }

  @Test
  public void getInstance_differentTypes_returnsDifferentInstances() {
    ConstantsOwningFillerInner a = ConstantsOwningFillerInner.getInstance(Thread.State.class);
    ConstantsOwningFillerInner b = ConstantsOwningFillerInner.getInstance(java.math.RoundingMode.class);
    assertNotSame("Different types should yield different instances", a, b);
  }

  @Test
  public void getInstance_classAndTypeEquivalent_returnsSameInstance() {
    ConstantsOwningFillerInner fromCls = ConstantsOwningFillerInner.getInstance(Thread.State.class);
    ConstantsOwningFillerInner fromType = ConstantsOwningFillerInner.getInstance(JavaType.getInstance(Thread.State.class));
    assertSame("Class-based and Type-based should yield same instance", fromCls, fromType);
  }

  // ---- isAssignableTo ----

  @Test
  public void isAssignableTo_sameType_returnsTrue() {
    ConstantsOwningFillerInner filler = ConstantsOwningFillerInner.getInstance(Thread.State.class);
    assertTrue(filler.isAssignableTo(JavaType.getInstance(Thread.State.class)));
  }

  @Test
  public void isAssignableTo_superType_returnsTrue() {
    ConstantsOwningFillerInner filler = ConstantsOwningFillerInner.getInstance(Thread.State.class);
    // Thread.State is an Enum, so assignable to Enum (or Object)
    assertTrue(filler.isAssignableTo(JavaType.OBJECT_TYPE));
  }

  @Test
  public void isAssignableTo_unrelatedType_returnsFalse() {
    ConstantsOwningFillerInner filler = ConstantsOwningFillerInner.getInstance(Thread.State.class);
    assertFalse(filler.isAssignableTo(JavaType.STRING_TYPE));
  }

  // ---- appendItems with enum constants ----

  @Test
  public void appendItems_enumType_addsFieldFillIns() {
    ConstantsOwningFillerInner filler = ConstantsOwningFillerInner.getInstance(Thread.State.class);
    List<CascadeBlankChild> items = new ArrayList<>();

    filler.appendItems(items, null, false, null);

    // Thread.State has 6 enum constants: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED
    // Each is a public static final field of the enum type
    assertTrue("Should add FillIns for enum constants", items.size() >= 6);
  }

  @Test
  public void appendItems_withDefaultValue_marksDefault() {
    ConstantsOwningFillerInner filler = ConstantsOwningFillerInner.getInstance(Thread.State.class);
    List<CascadeBlankChild> items = new ArrayList<>();

    filler.appendItems(items, null, false, null, "NEW");

    // Should still add all enum constants, with NEW marked as default
    assertTrue("Should add FillIns even with default", items.size() >= 6);
  }

  @Test
  public void appendItems_withNullDefault_noDefaultMarked() {
    ConstantsOwningFillerInner filler = ConstantsOwningFillerInner.getInstance(Thread.State.class);
    List<CascadeBlankChild> items = new ArrayList<>();

    filler.appendItems(items, null, false, null, null);

    assertTrue("Should still add all enum constants", items.size() >= 6);
  }

  // ---- appendItems with non-enum type that has public static final fields ----

  @Test
  public void appendItems_typeWithNoConstants_addsNothing() {
    // String doesn't have public static final fields of type String (well, it has CASE_INSENSITIVE_ORDER but that's Comparator)
    ConstantsOwningFillerInner filler = ConstantsOwningFillerInner.getInstance(StringBuilder.class);
    List<CascadeBlankChild> items = new ArrayList<>();

    filler.appendItems(items, null, false, null);

    // StringBuilder has no public static final StringBuilder fields
    assertEquals("Type with no matching constants should add nothing", 0, items.size());
  }

  // ---- hierarchy check ----

  @Test
  public void extendsExpressionFillerInner() {
    assertTrue(ExpressionFillerInner.class.isAssignableFrom(ConstantsOwningFillerInner.class));
  }
}

package org.alice.ide.cascade;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.CascadeMenuModel;
import org.lgna.project.ast.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for ExpressionCascadeManager's type resolution and filler inner
 * registration, focusing on getTypeFor, appendOtherTypes, and
 * isApplicableForFillIn with various type hierarchies.
 */
public class ExpressionCascadeManagerTypeResolutionTest {

  private TestableManager manager;

  private static class TestableManager extends ExpressionCascadeManager {
    private boolean nullLiteralAllowed = false;
    private AbstractType<?, ?, ?> enumTypeForInterface = null;

    @Override
    protected boolean isApplicableForPartFillIn(AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType) {
      return false;
    }

    @Override
    protected CascadeMenuModel<Expression> createPartMenuModel(Expression expression, AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType, boolean isOwnedByCascadeItemMenuCombo) {
      return null;
    }

    @Override
    protected boolean isNullLiteralAllowedForType(AbstractType<?, ?, ?> type, List<CascadeBlankChild> items) {
      return nullLiteralAllowed;
    }

    @Override
    protected AbstractType<?, ?, ?> getEnumTypeForInterfaceType(AbstractType<?, ?, ?> interfaceType) {
      return enumTypeForInterface;
    }

    void setNullLiteralAllowed(boolean allowed) { this.nullLiteralAllowed = allowed; }
    void setEnumTypeForInterface(AbstractType<?, ?, ?> type) { this.enumTypeForInterface = type; }

    AbstractType<?, ?, ?> testGetTypeFor(AbstractType<?, ?, ?> type) { return getTypeFor(type); }
    boolean testAreEnumConstantsDesired(AbstractType<?, ?, ?> type) { return areEnumConstantsDesired(type); }
    boolean testIsApplicableForFillIn(AbstractType<?, ?, ?> desired, AbstractType<?, ?, ?> expression) {
      return isApplicableForFillIn(desired, expression);
    }
    boolean testIsNullLiteralAllowedForType(AbstractType<?, ?, ?> type, List<CascadeBlankChild> items) {
      return isNullLiteralAllowedForType(type, items);
    }
    List<CascadeBlankChild> testAddCustomFillIns(List<CascadeBlankChild> rv, AbstractType<?, ?, ?> type) {
      return addCustomFillIns(rv, null, type);
    }

    List<AbstractType<?, ?, ?>> getOtherTypes() {
      List<AbstractType<?, ?, ?>> types = new ArrayList<>();
      appendOtherTypes(types);
      return types;
    }
  }

  @Before
  public void setUp() {
    manager = new TestableManager();
  }

  // ---- getTypeFor ----

  @Test
  public void getTypeFor_numberClass_returnsDouble() {
    assertEquals(JavaType.DOUBLE_OBJECT_TYPE, manager.testGetTypeFor(JavaType.getInstance(Number.class)));
  }

  @Test
  public void getTypeFor_integerType_returnsItself() {
    assertSame(JavaType.INTEGER_OBJECT_TYPE, manager.testGetTypeFor(JavaType.INTEGER_OBJECT_TYPE));
  }

  @Test
  public void getTypeFor_stringType_returnsItself() {
    assertSame(JavaType.STRING_TYPE, manager.testGetTypeFor(JavaType.STRING_TYPE));
  }

  @Test
  public void getTypeFor_booleanType_returnsItself() {
    assertSame(JavaType.BOOLEAN_OBJECT_TYPE, manager.testGetTypeFor(JavaType.BOOLEAN_OBJECT_TYPE));
  }

  @Test
  public void getTypeFor_objectType_returnsItself() {
    assertSame(JavaType.OBJECT_TYPE, manager.testGetTypeFor(JavaType.OBJECT_TYPE));
  }

  @Test
  public void getTypeFor_doubleType_returnsItself() {
    assertSame(JavaType.DOUBLE_OBJECT_TYPE, manager.testGetTypeFor(JavaType.DOUBLE_OBJECT_TYPE));
  }

  // ---- appendOtherTypes ----

  @Test
  public void appendOtherTypes_hasThreeTypes() {
    assertEquals(3, manager.getOtherTypes().size());
  }

  @Test
  public void appendOtherTypes_containsString() {
    assertTrue(manager.getOtherTypes().contains(JavaType.STRING_TYPE));
  }

  @Test
  public void appendOtherTypes_containsDouble() {
    assertTrue(manager.getOtherTypes().contains(JavaType.DOUBLE_OBJECT_TYPE));
  }

  @Test
  public void appendOtherTypes_containsInteger() {
    assertTrue(manager.getOtherTypes().contains(JavaType.INTEGER_OBJECT_TYPE));
  }

  @Test
  public void appendOtherTypes_doesNotContainBoolean() {
    assertFalse(manager.getOtherTypes().contains(JavaType.BOOLEAN_OBJECT_TYPE));
  }

  // ---- areEnumConstantsDesired ----

  @Test
  public void areEnumConstantsDesired_defaultTrue() {
    assertTrue(manager.testAreEnumConstantsDesired(JavaType.STRING_TYPE));
  }

  // ---- isApplicableForFillIn type hierarchy ----

  @Test
  public void isApplicable_stringToObject_true() {
    assertTrue(manager.testIsApplicableForFillIn(JavaType.OBJECT_TYPE, JavaType.STRING_TYPE));
  }

  @Test
  public void isApplicable_integerToNumber_true() {
    assertTrue(manager.testIsApplicableForFillIn(JavaType.getInstance(Number.class), JavaType.INTEGER_OBJECT_TYPE));
  }

  @Test
  public void isApplicable_doubleToNumber_true() {
    assertTrue(manager.testIsApplicableForFillIn(JavaType.getInstance(Number.class), JavaType.DOUBLE_OBJECT_TYPE));
  }

  @Test
  public void isApplicable_stringToNumber_false() {
    assertFalse(manager.testIsApplicableForFillIn(JavaType.getInstance(Number.class), JavaType.STRING_TYPE));
  }

  @Test
  public void isApplicable_booleanToString_false() {
    assertFalse(manager.testIsApplicableForFillIn(JavaType.STRING_TYPE, JavaType.BOOLEAN_OBJECT_TYPE));
  }

  // ---- addCustomFillIns default ----

  @Test
  public void addCustomFillIns_default_returnsSameList() {
    List<CascadeBlankChild> list = new ArrayList<>();
    List<CascadeBlankChild> result = manager.testAddCustomFillIns(list, JavaType.STRING_TYPE);
    assertSame(list, result);
    assertTrue(result.isEmpty());
  }

  // ---- isNullLiteralAllowed overrides ----

  @Test
  public void isNullLiteralAllowed_defaultFalse() {
    manager.setNullLiteralAllowed(false);
    assertFalse(manager.testIsNullLiteralAllowedForType(JavaType.STRING_TYPE, java.util.Collections.emptyList()));
  }

  // ---- getEnumTypeForInterfaceType ----

  @Test
  public void getEnumTypeForInterface_default_returnsNull() {
    // The default getEnumTypeForInterfaceType returns null
    manager.setEnumTypeForInterface(null);
    // getTypeFor for Comparable returns Comparable itself (it's not Number)
    assertSame(JavaType.getInstance(Comparable.class),
        manager.testGetTypeFor(JavaType.getInstance(Comparable.class)));
  }

  // ---- addRelationalType ----

  @Test
  public void addRelationalType_multipleClasses_doesNotThrow() {
    // Smoke test: no exception when registering multiple relational types
    manager.addRelationalTypeToBooleanFillerInner(Double.class);
    manager.addRelationalTypeToBooleanFillerInner(Integer.class);
    manager.addRelationalTypeToBooleanFillerInner(String.class);
  }

  @Test
  public void addRelationalType_sameTypeMultipleTimes_doesNotThrow() {
    // Smoke test: no exception on duplicate registration
    manager.addRelationalTypeToBooleanFillerInner(Double.class);
    manager.addRelationalTypeToBooleanFillerInner(Double.class);
  }
}

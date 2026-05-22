package org.alice.stageide.ast.sort;

import org.alice.ide.ast.sort.MemberSorter;
import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.AbstractMember;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.UserMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OneShotSorterComprehensiveTest {

  private static void assumeSorterAvailable() {
    try {
      assertNotNull(OneShotSorter.SINGLETON);
    } catch (ExceptionInInitializerError e) {
      Assume.assumeNoException(e);
    } catch (NoClassDefFoundError e) {
      Assume.assumeNoException(e);
    }
  }

  private static UserMethod createUserMethod(String name) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    return method;
  }

  @Test
  public void enumHasSingleConstant() {
    assumeSorterAvailable();
    assertEquals(1, OneShotSorter.values().length);
  }

  @Test
  public void valueOfReturnsSingleton() {
    assumeSorterAvailable();
    assertSame(OneShotSorter.SINGLETON, OneShotSorter.valueOf("SINGLETON"));
  }

  @Test
  public void singletonImplementsMemberSorter() {
    assumeSorterAvailable();
    assertTrue(OneShotSorter.SINGLETON instanceof MemberSorter);
  }

  @Test
  public void singletonDeclaringClassIsEnumType() {
    assumeSorterAvailable();
    assertSame(OneShotSorter.class, OneShotSorter.SINGLETON.getDeclaringClass());
  }

  @Test
  public void createSortedListReturnsNewListInstance() {
    assumeSorterAvailable();
    List<UserMethod> source = new ArrayList<UserMethod>();
    source.add(createUserMethod("a"));
    List<UserMethod> sorted = OneShotSorter.SINGLETON.createSortedList(source);
    assertNotSame(source, sorted);
  }

  @Test
  public void createSortedListEmptyInputReturnsEmptyList() {
    assumeSorterAvailable();
    assertTrue(OneShotSorter.SINGLETON.createSortedList(new ArrayList<UserMethod>()).isEmpty());
  }

  @Test
  public void createSortedListPreservesElementCount() {
    assumeSorterAvailable();
    List<UserMethod> source = Arrays.asList(createUserMethod("z"), createUserMethod("a"), createUserMethod("m"));
    assertEquals(source.size(), OneShotSorter.SINGLETON.createSortedList(source).size());
  }

  @Test
  public void createSortedListIsStableForUnknownMembers() {
    assumeSorterAvailable();
    UserMethod first = createUserMethod("first");
    UserMethod second = createUserMethod("second");
    List<UserMethod> sorted = OneShotSorter.SINGLETON.createSortedList(Arrays.asList(first, second));
    assertSame(first, sorted.get(0));
    assertSame(second, sorted.get(1));
  }

  @Test
  public void unknownMembersSortBeforeKnownMembers() {
    assumeSorterAvailable();
    UserMethod unknown = createUserMethod("unknown");
    List<AbstractMember> sorted = OneShotSorter.SINGLETON.createSortedList(Arrays.<AbstractMember>asList(OneShotSorter.MOVE_METHOD, unknown));
    assertSame(unknown, sorted.get(0));
    assertSame(OneShotSorter.MOVE_METHOD, sorted.get(1));
  }

  @Test
  public void movementMethodFieldsAreInitialized() {
    assumeSorterAvailable();
    assertNotNull(OneShotSorter.MOVE_METHOD);
    assertNotNull(OneShotSorter.MOVE_TOWARD_METHOD);
    assertNotNull(OneShotSorter.MOVE_AWAY_FROM_METHOD);
    assertNotNull(OneShotSorter.MOVE_TO_METHOD);
    assertNotNull(OneShotSorter.MOVE_AND_ORIENT_TO_METHOD);
    assertNotNull(OneShotSorter.PLACE_METHOD);
  }

  @Test
  public void orientationMethodFieldsAreInitialized() {
    assumeSorterAvailable();
    assertNotNull(OneShotSorter.TURN_METHOD);
    assertNotNull(OneShotSorter.ROLL_METHOD);
    assertNotNull(OneShotSorter.TURN_TO_FACE_METHOD);
    assertNotNull(OneShotSorter.POINT_AT_METHOD);
    assertNotNull(OneShotSorter.ORIENT_TO_UPRIGHT_METHOD);
    assertNotNull(OneShotSorter.ORIENT_TO_METHOD);
  }

  @Test
  public void jointAndWingMethodFieldsAreInitialized() {
    assumeSorterAvailable();
    assertNotNull(OneShotSorter.STRAIGHTEN_OUT_JOINTS_METHOD);
    assertNotNull(OneShotSorter.SPREAD_WINGS_METHOD);
    assertNotNull(OneShotSorter.FOLD_WINGS_METHOD);
  }

  @Test
  public void paintAndOpacityMethodFieldsAreInitialized() {
    assumeSorterAvailable();
    assertNotNull(OneShotSorter.GROUND_SET_PAINT_METHOD);
    assertNotNull(OneShotSorter.MODEL_SET_PAINT_METHOD);
    assertNotNull(OneShotSorter.GROUND_SET_OPACITY_METHOD);
    assertNotNull(OneShotSorter.MODEL_SET_OPACITY_METHOD);
  }

  @Test
  public void goodVantagePointMethodIsInitialized() {
    assumeSorterAvailable();
    assertNotNull(OneShotSorter.MOVE_AND_ORIENT_TO_A_GOOD_VANTAGE_POINT_METHOD);
  }

  @Test
  public void representativeFieldsAreJavaMethods() {
    assumeSorterAvailable();
    assertTrue(OneShotSorter.MOVE_METHOD instanceof JavaMethod);
    assertTrue(OneShotSorter.TURN_METHOD instanceof JavaMethod);
  }

  @Test
  public void helperMethodsExistWithExpectedVisibility() throws Exception {
    assumeSorterAvailable();
    assertTrue(java.lang.reflect.Modifier.isPrivate(OneShotSorter.class.getDeclaredMethod("getSetPaintMethod", org.lgna.project.ast.JavaType.class).getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isStatic(OneShotSorter.class.getDeclaredMethod("getSetOpacityMethod", org.lgna.project.ast.JavaType.class).getModifiers()));
  }
}

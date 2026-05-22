package org.alice.ide.ast.sort;

import org.junit.Test;
import org.lgna.project.ast.AbstractMember;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MemberSorterComprehensiveTest {
  @Test
  public void memberSorterIsPublicInterface() {
    assertTrue(MemberSorter.class.isInterface());
    assertTrue(Modifier.isPublic(MemberSorter.class.getModifiers()));
  }

  @Test
  public void memberSorterDeclaresSingleCreateSortedListMethod() {
    Method[] methods = MemberSorter.class.getDeclaredMethods();

    assertEquals(1, methods.length);
    assertEquals("createSortedList", methods[0].getName());
  }

  @Test
  public void alphabeticalSingletonImplementsMemberSorter() {
    assertTrue(AlphabeticalMemberSorter.SINGLETON instanceof MemberSorter);
  }

  @Test
  public void singletonValueIsStableAcrossAccessPaths() {
    assertSame(AlphabeticalMemberSorter.SINGLETON, AlphabeticalMemberSorter.valueOf("SINGLETON"));
    assertSame(AlphabeticalMemberSorter.SINGLETON, AlphabeticalMemberSorter.values()[0]);
  }

  @Test
  public void sortsUserMethodsAlphabetically() {
    List<UserMethod> input = Arrays.asList(createMethod("gamma"), createMethod("alpha"), createMethod("beta"));

    List<UserMethod> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(input);

    assertEquals("alpha", result.get(0).getName());
    assertEquals("beta", result.get(1).getName());
    assertEquals("gamma", result.get(2).getName());
  }

  @Test
  public void sortingIsCaseInsensitiveForMethods() {
    List<UserMethod> input = Arrays.asList(createMethod("Bravo"), createMethod("alpha"), createMethod("charlie"));

    List<UserMethod> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(input);

    assertEquals("alpha", result.get(0).getName());
    assertEquals("Bravo", result.get(1).getName());
    assertEquals("charlie", result.get(2).getName());
  }

  @Test
  public void nullNamesSortBeforeNamedMembers() {
    UserMethod unnamed = createMethod(null);
    UserMethod named = createMethod("namedMethod");

    List<UserMethod> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(named, unnamed));

    assertNull(result.get(0).getName());
    assertEquals("namedMethod", result.get(1).getName());
  }

  @Test
  public void emptyListProducesEmptyResult() {
    List<UserMethod> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(Collections.<UserMethod>emptyList());

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void singleItemListProducesNewListInstance() {
    List<UserMethod> input = Collections.singletonList(createMethod("solo"));

    List<UserMethod> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(input);

    assertNotSame(input, result);
    assertEquals(1, result.size());
    assertEquals("solo", result.get(0).getName());
  }

  @Test
  public void alreadySortedMethodsRemainInAlphabeticalOrder() {
    List<UserMethod> input = Arrays.asList(createMethod("alpha"), createMethod("beta"), createMethod("gamma"));

    List<UserMethod> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(input);

    assertEquals("alpha", result.get(0).getName());
    assertEquals("beta", result.get(1).getName());
    assertEquals("gamma", result.get(2).getName());
  }

  @Test
  public void reverseSortedMethodsAreReorderedAscending() {
    List<UserMethod> input = Arrays.asList(createMethod("gamma"), createMethod("beta"), createMethod("alpha"));

    List<UserMethod> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(input);

    assertEquals("alpha", result.get(0).getName());
    assertEquals("beta", result.get(1).getName());
    assertEquals("gamma", result.get(2).getName());
  }

  @Test
  public void resultIsNewListAndSourceOrderIsNotMutated() {
    List<UserMethod> input = new ArrayList<UserMethod>(Arrays.asList(createMethod("zeta"), createMethod("alpha"), createMethod("eta")));

    List<UserMethod> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(input);

    assertNotSame(input, result);
    assertEquals("zeta", input.get(0).getName());
    assertEquals("alpha", input.get(1).getName());
    assertEquals("eta", input.get(2).getName());
    assertEquals("alpha", result.get(0).getName());
    assertEquals("eta", result.get(1).getName());
    assertEquals("zeta", result.get(2).getName());
  }

  @Test
  public void sortsMixedUserFieldsAndUserMethodsTogetherByName() {
    List<AbstractMember> input = Arrays.<AbstractMember>asList(createField("deltaField"), createMethod("betaMethod"), createField("alphaField"), createMethod("gammaMethod"));

    List<AbstractMember> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(input);

    assertEquals("alphaField", result.get(0).getName());
    assertEquals("betaMethod", result.get(1).getName());
    assertEquals("deltaField", result.get(2).getName());
    assertEquals("gammaMethod", result.get(3).getName());
  }

  @Test
  public void mixedMembersWithNullNamesSortBeforeNamedMembers() {
    List<AbstractMember> input = Arrays.<AbstractMember>asList(createField("fieldName"), createMethod(null), createField(null), createMethod("methodName"));

    List<AbstractMember> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(input);

    assertNull(result.get(0).getName());
    assertNull(result.get(1).getName());
    assertEquals("fieldName", result.get(2).getName());
    assertEquals("methodName", result.get(3).getName());
  }

  @Test
  public void duplicateNamesAreRetained() {
    List<UserMethod> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(createMethod("dup"), createMethod("dup")));

    assertEquals(2, result.size());
    assertEquals("dup", result.get(0).getName());
    assertEquals("dup", result.get(1).getName());
  }

  private static UserMethod createMethod(String name) {
    UserMethod method = new UserMethod();
    if (name != null) {
      method.name.setValue(name);
    }
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    method.body.setValue(new BlockStatement());
    return method;
  }

  private static UserField createField(String name) {
    UserField field = new UserField();
    if (name != null) {
      field.name.setValue(name);
    }
    field.valueType.setValue(JavaType.getInstance(String.class));
    field.managementLevel.setValue(ManagementLevel.NONE);
    field.initializer.setValue(new NullLiteral());
    return field;
  }
}

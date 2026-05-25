package org.alice.ide.ast.type.merge.core;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MergeUtilitiesMatchingBehaviorTest {
  @Test
  public void findMatchingTypeInExistingTypesReturnsTypeWithMatchingName() {
    NamedUserType source = MergeUtilitiesTestSupport.namedType("Hero");
    NamedUserType decoy = MergeUtilitiesTestSupport.namedType("Villain");
    NamedUserType match = MergeUtilitiesTestSupport.namedType("Hero");

    assertSame(match, MergeUtilities.findMatchingTypeInExistingTypes(source, MergeUtilitiesTestSupport.list(decoy, match)));
  }

  @Test
  public void findMatchingTypeInExistingTypesReturnsNullWhenNoNamesMatch() {
    NamedUserType source = MergeUtilitiesTestSupport.namedType("Hero");

    assertNull(MergeUtilities.findMatchingTypeInExistingTypes(source, MergeUtilitiesTestSupport.list(
        MergeUtilitiesTestSupport.namedType("Villain"),
        MergeUtilitiesTestSupport.namedType("Sidekick"))));
  }

  @Test
  public void findMethodWithMatchingNameReturnsMethodFromDestinationType() {
    NamedUserType sourceType = MergeUtilitiesTestSupport.namedType("Source");
    UserMethod sourceMethod = MergeUtilitiesTestSupport.addMethod(sourceType, "perform");
    NamedUserType destinationType = MergeUtilitiesTestSupport.namedType("Destination");
    UserMethod destinationMethod = MergeUtilitiesTestSupport.addMethod(destinationType, "perform");

    assertSame(destinationMethod, MergeUtilities.findMethodWithMatchingName(sourceMethod, destinationType));
  }

  @Test
  public void isHeaderEquivalentReturnsTrueForMatchingMethodSignatures() {
    UserMethod left = MergeUtilitiesTestSupport.addMethod(MergeUtilitiesTestSupport.namedType("Left"), "perform");
    UserMethod right = MergeUtilitiesTestSupport.addMethod(MergeUtilitiesTestSupport.namedType("Right"), "perform");

    assertTrue(MergeUtilities.isHeaderEquivalent(left, right));
  }

  @Test
  public void isHeaderEquivalentReturnsFalseWhenReturnTypesDiffer() {
    UserMethod left = MergeUtilitiesTestSupport.addMethod(MergeUtilitiesTestSupport.namedType("Left"), "perform");
    UserMethod right = MergeUtilitiesTestSupport.addMethod(MergeUtilitiesTestSupport.namedType("Right"), "perform");
    right.returnType.setValue(JavaType.getInstance(String.class));

    assertFalse(MergeUtilities.isHeaderEquivalent(left, right));
  }

  @Test
  public void isValueTypeEquivalentComparesFieldValueTypeNames() {
    NamedUserType owner = MergeUtilitiesTestSupport.namedType("Owner");
    UserField stringField = MergeUtilitiesTestSupport.addField(owner, "message", JavaType.getInstance(String.class));
    UserField otherStringField = MergeUtilitiesTestSupport.addField(owner, "title", JavaType.getInstance(String.class));
    UserField integerField = MergeUtilitiesTestSupport.addField(owner, "count", JavaType.getInstance(Integer.class));

    assertTrue(MergeUtilities.isValueTypeEquivalent(stringField, otherStringField));
    assertFalse(MergeUtilities.isValueTypeEquivalent(stringField, integerField));
  }
}

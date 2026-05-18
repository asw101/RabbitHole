package org.lgna.project.code;

import org.junit.Test;
import org.lgna.project.ast.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CodeOrganizerTest {
  @Test
  public void defaultDefinitionOrdersConstructorsMethodsAccessorsFieldsAndStaticMethods() {
    CodeOrganizer organizer = new CodeOrganizer(CodeOrganizer.defaultCodeOrganizer);
    NamedUserType type = createType("OrderedType");
    NamedUserConstructor constructor = type.constructors.get(0);
    UserMethod instanceMethod = new UserMethod("instanceMethod", Void.TYPE, new UserParameter[0], new BlockStatement());
    UserMethod staticMethod = new UserMethod("staticMethod", Void.TYPE, new UserParameter[0], new BlockStatement());
    staticMethod.isStatic.setValue(true);
    UserField field = new UserField("value", String.class, new StringLiteral("v"));

    type.methods.add(instanceMethod);
    type.methods.add(staticMethod);
    type.fields.add(field);

    organizer.addConstructor(constructor);
    organizer.addNonStaticMethod(instanceMethod);
    organizer.addGetters(field.getGetters());
    organizer.addSetters(field.getSetters());
    organizer.addField(field);
    organizer.addStaticMethod(staticMethod);

    LinkedHashMap<String, List<ProcessableNode>> ordered = organizer.getOrderedSections();

    assertEquals(
        Arrays.asList(
            "ConstructorSection",
            "MethodsAndFunctionsSection",
            "GettersAndSettersSection",
            "FieldsSection",
            "StaticMethodsSection"),
        new ArrayList<>(ordered.keySet()));
    assertEquals(Arrays.asList("OrderedType"), names(ordered.get("ConstructorSection")));
    assertEquals(Arrays.asList("instanceMethod"), names(ordered.get("MethodsAndFunctionsSection")));
    assertEquals(Arrays.asList("getValue", "setValue"), names(ordered.get("GettersAndSettersSection")));
    assertEquals(Arrays.asList("value"), names(ordered.get("FieldsSection")));
    assertEquals(Arrays.asList("staticMethod"), names(ordered.get("StaticMethodsSection")));
  }

  @Test
  public void customDefinitionRoutesExactNameBeforeFallbackGroup() {
    CodeOrganizer.CodeOrganizerDefinition definition = new CodeOrganizer.CodeOrganizerDefinition();
    definition.addSection("SpecialSection", "specialMethod");
    definition.addSection("MethodsSection", "NON_STATIC_METHODS");
    CodeOrganizer organizer = new CodeOrganizer(definition);

    UserMethod specialMethod = new UserMethod("specialMethod", Void.TYPE, new UserParameter[0], new BlockStatement());
    UserMethod regularMethod = new UserMethod("regularMethod", Void.TYPE, new UserParameter[0], new BlockStatement());

    organizer.addNonStaticMethod(specialMethod);
    organizer.addNonStaticMethod(regularMethod);

    LinkedHashMap<String, List<ProcessableNode>> ordered = organizer.getOrderedSections();

    assertEquals(Arrays.asList("specialMethod"), names(ordered.get("SpecialSection")));
    assertEquals(Arrays.asList("regularMethod"), names(ordered.get("MethodsSection")));
  }

  @Test
  public void unmatchedItemsFallBackToDefaultSection() {
    CodeOrganizer.CodeOrganizerDefinition definition = new CodeOrganizer.CodeOrganizerDefinition();
    definition.addSection("OnlySpecial", "specialMethod");
    CodeOrganizer organizer = new CodeOrganizer(definition);

    organizer.addNonStaticMethod(new UserMethod("other", Void.TYPE, new UserParameter[0], new BlockStatement()));

    LinkedHashMap<String, List<ProcessableNode>> ordered = organizer.getOrderedSections();

    assertTrue(ordered.containsKey("DEFAULT"));
    assertEquals(Arrays.asList("other"), names(ordered.get("DEFAULT")));
  }

  @Test
  public void nullGetterAndSetterListsAreIgnored() {
    CodeOrganizer.CodeOrganizerDefinition definition = new CodeOrganizer.CodeOrganizerDefinition();
    definition.addSection("Accessors", "GETTERS_AND_SETTERS");
    CodeOrganizer organizer = new CodeOrganizer(definition);

    organizer.addGetters(null);
    organizer.addSetters(null);

    assertTrue(organizer.getOrderedSections().get("Accessors").isEmpty());
  }

  @Test
  public void sceneClassDefinitionPlacesNamedMembersInExpectedSections() {
    CodeOrganizer organizer = new CodeOrganizer(CodeOrganizer.sceneClassCodeOrganizer);
    UserMethod listeners = new UserMethod("initializeEventListeners", Void.TYPE, new UserParameter[0], new BlockStatement());
    UserMethod generatedSetup = new UserMethod("performGeneratedSetUp", Void.TYPE, new UserParameter[0], new BlockStatement());
    UserMethod activeChanged = new UserMethod("handleActiveChanged", Void.TYPE, new UserParameter[0], new BlockStatement());
    UserField field = new UserField("sceneValue", String.class, new StringLiteral("scene"));

    organizer.addNonStaticMethod(listeners);
    organizer.addNonStaticMethod(generatedSetup);
    organizer.addNonStaticMethod(activeChanged);
    organizer.addField(field);
    organizer.addGetters(field.getGetters());
    organizer.addSetters(field.getSetters());

    LinkedHashMap<String, List<ProcessableNode>> ordered = organizer.getOrderedSections();

    assertEquals(Arrays.asList("initializeEventListeners"), names(ordered.get("EventListenersSection")));
    assertEquals(Arrays.asList("performGeneratedSetUp"), names(ordered.get("SceneSetupSection")));
    assertEquals(Arrays.asList("handleActiveChanged", "getSceneValue", "setSceneValue"), names(ordered.get("MultipleSceneSection")));
    assertEquals(Arrays.asList("sceneValue"), names(ordered.get("FieldsSection")));
    assertTrue(organizer.shouldCollapseSection("FieldsSection"));
    assertTrue(organizer.shouldCollapseSection("SceneSetupSection"));
  }

  @Test
  public void programClassDefinitionPlacesSceneMainAndAccessors() {
    CodeOrganizer organizer = new CodeOrganizer(CodeOrganizer.programClassCodeOrganizer);
    UserField myScene = new UserField("myScene", String.class, new StringLiteral("scene"));
    UserField otherField = new UserField("otherField", String.class, new StringLiteral("other"));
    UserMethod main = new UserMethod("main", Void.TYPE, new UserParameter[0], new BlockStatement());
    main.isStatic.setValue(true);

    organizer.addField(myScene);
    organizer.addField(otherField);
    organizer.addStaticMethod(main);
    organizer.addGetters(myScene.getGetters());
    organizer.addSetters(myScene.getSetters());

    LinkedHashMap<String, List<ProcessableNode>> ordered = organizer.getOrderedSections();

    assertEquals(Arrays.asList("myScene", "otherField"), names(ordered.get("FieldsSection")));
    assertEquals(Arrays.asList("main"), names(ordered.get("MainFunction")));
    assertEquals(Arrays.asList("getMyScene", "setMyScene"), names(ordered.get("GettersAndSettersSection")));
  }

  @Test
  public void fieldOrderingPreservesInsertionOrder() {
    CodeOrganizer.CodeOrganizerDefinition definition = new CodeOrganizer.CodeOrganizerDefinition();
    definition.addSection("FieldsSection", "FIELDS");
    CodeOrganizer organizer = new CodeOrganizer(definition);

    organizer.addField(new UserField("first", String.class, new StringLiteral("1")));
    organizer.addField(new UserField("second", String.class, new StringLiteral("2")));
    organizer.addField(new UserField("third", String.class, new StringLiteral("3")));

    assertEquals(Arrays.asList("first", "second", "third"), names(organizer.getOrderedSections().get("FieldsSection")));
  }

  @Test
  public void shouldCollapseSectionDefaultsToFalseForUnknownSection() {
    CodeOrganizer organizer = new CodeOrganizer(CodeOrganizer.defaultCodeOrganizer);

    assertFalse(organizer.shouldCollapseSection("MissingSection"));
  }

  private static NamedUserType createType(String name) {
    return new NamedUserType(
        name,
        null,
        Object.class,
        new NamedUserConstructor[] {new NamedUserConstructor(new UserParameter[0], new ConstructorBlockStatement())},
        new UserMethod[0],
        new UserField[0]);
  }

  private static List<String> names(List<ProcessableNode> nodes) {
    return nodes.stream().map(node -> {
      if (node instanceof AbstractDeclaration declaration) {
        return declaration.getName();
      }
      return node.getClass().getSimpleName();
    }).collect(Collectors.toList());
  }
}

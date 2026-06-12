package org.lgna.project.virtualmachine;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserField;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SceneEditorVmBehaviorCharacterizationTest {

  private ReleaseVirtualMachine vm;
  private NamedUserType type;

  @Before
  public void setUp() {
    vm = new ReleaseVirtualMachine();
    type = VmTestSupport.createTypeWithConstructor("SceneEditorVmBehaviorType");
  }

  @Test
  public void fieldLookupUsesManagedJavaInstancesOnly() {
    UserField managed = new UserField("managed", String.class, new StringLiteral("managed-value"));
    managed.managementLevel.setValue(ManagementLevel.MANAGED);
    UserField unmanaged = new UserField("unmanaged", String.class, new StringLiteral("unmanaged-value"));
    type.fields.add(managed);
    type.fields.add(unmanaged);

    UserInstance instance = vm.ENTRY_POINT_createInstance(type);
    instance.ensureInverseMapExists();

    assertEquals(managed, instance.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_getFieldForInstanceInJava("managed-value"));
    assertNull(instance.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_getFieldForInstanceInJava("unmanaged-value"));
  }

  @Test
  public void initializedManagedFieldIsAddedToExistingLookup() {
    UserInstance instance = vm.ENTRY_POINT_createInstance(type);
    instance.ensureInverseMapExists();

    UserField field = new UserField("added", String.class, new StringLiteral("added-value"));
    field.managementLevel.setValue(ManagementLevel.MANAGED);
    type.fields.add(field);

    vm.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_initializeField(instance, field);

    assertEquals(field, instance.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_getFieldForInstanceInJava("added-value"));
  }

  @Test
  public void reassigningManagedFieldPreservesPreviousLookupEntry() {
    UserField field = new UserField("managed", String.class, new StringLiteral("first-value"));
    field.managementLevel.setValue(ManagementLevel.MANAGED);
    type.fields.add(field);

    UserInstance instance = vm.ENTRY_POINT_createInstance(type);
    instance.ensureInverseMapExists();
    vm.set(field, instance, "second-value");

    assertEquals(field, instance.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_getFieldForInstanceInJava("first-value"));
    assertEquals(field, instance.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_getFieldForInstanceInJava("second-value"));
  }

  @Test
  public void sceneEditorStatementExecutionFiresEvents() {
    UserInstance instance = vm.ENTRY_POINT_createInstance(type);
    VmTestSupport.RecordingListener listener = new VmTestSupport.RecordingListener();
    vm.addVirtualMachineListener(listener);
    BlockStatement statement = new BlockStatement(new Comment("scene-editor statement"));

    vm.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_executeStatement(instance, statement);

    assertEquals(Arrays.asList(
        "executing:BlockStatement",
        "executing:Comment",
        "executed:Comment",
        "executed:BlockStatement"),
        listener.statementEvents);
  }

  @Test
  public void sceneEditorFieldInitializationPreservesInitializerValue() {
    UserInstance instance = vm.ENTRY_POINT_createInstance(type);
    UserField field = new UserField("score", Integer.class, new IntegerLiteral(7));
    type.fields.add(field);

    vm.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_initializeField(instance, field);

    assertEquals(7, vm.get(field, instance));
  }
}

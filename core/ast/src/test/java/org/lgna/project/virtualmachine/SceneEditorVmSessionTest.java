package org.lgna.project.virtualmachine;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserField;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SceneEditorVmSessionTest {

  private ReleaseVirtualMachine vm;
  private NamedUserType type;
  private UserInstance instance;

  @Before
  public void setUp() {
    vm = new ReleaseVirtualMachine();
    type = VmTestSupport.createTypeWithConstructor("SceneEditorVmSessionTestType");
    instance = vm.ENTRY_POINT_createInstance(type);
  }

  @Test
  public void prepareFieldLookupEnablesJavaInstanceToFieldLookup() {
    UserField field = new UserField("managed", String.class, new StringLiteral("managed-value"));
    field.managementLevel.setValue(ManagementLevel.MANAGED);
    type.fields.add(field);

    SceneEditorVmSession session = SceneEditorVmSession.forScene(instance);
    session.initializeField(field);
    session.prepareFieldLookup();

    assertEquals(field, session.getFieldForJavaInstance("managed-value"));
  }

  @Test
  public void initializeFieldDelegatesToSceneEditorVmBehavior() {
    UserField field = new UserField("score", Integer.class, new IntegerLiteral(11));
    type.fields.add(field);
    SceneEditorVmSession session = SceneEditorVmSession.forScene(instance);

    session.initializeField(field);

    assertEquals(11, vm.get(field, instance));
  }

  @Test
  public void executeStatementsDelegatesInOrder() {
    VmTestSupport.RecordingListener listener = new VmTestSupport.RecordingListener();
    vm.addVirtualMachineListener(listener);
    SceneEditorVmSession session = SceneEditorVmSession.forScene(instance);

    session.executeStatements(new Comment("first"), new Comment("second"));

    assertEquals(Arrays.asList(
        "executing:Comment",
        "executed:Comment",
        "executing:Comment",
        "executed:Comment"),
        listener.statementEvents);
  }
}

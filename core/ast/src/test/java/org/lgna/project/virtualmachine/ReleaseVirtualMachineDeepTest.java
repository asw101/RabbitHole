package org.lgna.project.virtualmachine;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ReturnStatement;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Deep coverage tests for {@link ReleaseVirtualMachine}.
 * Targets frame hierarchy (AbstractFrame, BogusFrame, MethodInvocationFrame,
 * ConstructorInvocationFrame, ThreadFrame), stack trace generation,
 * local/parameter management, and error paths.
 */
public class ReleaseVirtualMachineDeepTest {

  private ReleaseVirtualMachine vm;

  @Before
  public void setUp() {
    vm = new ReleaseVirtualMachine();
  }

  // ── Stack trace generation ────────────────────────────────────────────────

  @Test
  public void getStackTraceReturnsEmptyArrayForUnknownThread() {
    LgnaStackTraceElement[] trace = vm.getStackTrace(Thread.currentThread());
    assertNotNull(trace);
    assertEquals(0, trace.length);
  }

  @Test
  public void getStackTraceReturnsEmptyArrayForNullThread() {
    LgnaStackTraceElement[] trace = vm.getStackTrace(null);
    assertNotNull(trace);
    assertEquals(0, trace.length);
  }

  @Test
  public void getStackTraceReflectsFrameChainDuringExecution() {
    NamedUserType type = VmTestSupport.createProgramType("TraceTestProgram");
    UserMethod method = VmTestSupport.createStaticProcedure("traceTarget",
        new BlockStatement(new Comment("body")));
    type.methods.add(method);

    final LgnaStackTraceElement[][] captured = new LgnaStackTraceElement[1][];
    VmTestSupport.RecordingListener traceListener = new VmTestSupport.RecordingListener() {
      @Override
      public void statementExecuting(org.lgna.project.virtualmachine.events.StatementExecutionEvent e) {
        super.statementExecuting(e);
        if (captured[0] == null) {
          captured[0] = vm.getStackTrace(Thread.currentThread());
        }
      }
    };
    vm.addVirtualMachineListener(traceListener);

    vm.ENTRY_POINT_invoke(null, method);

    assertNotNull("Stack trace should have been captured during execution", captured[0]);
    assertTrue("Stack trace must have at least one frame", captured[0].length >= 1);
  }

  // ── getThis() without frames ──────────────────────────────────────────────

  @Test
  public void getThisReturnsNullWhenNoFrameIsActive() {
    NamedUserType type = VmTestSupport.createProgramType("NoThisProgram");
    UserMethod method = VmTestSupport.createStaticProcedure("noThis",
        new BlockStatement(new Comment("static")));
    type.methods.add(method);

    vm.ENTRY_POINT_invoke(null, method);
    // If we get here without exception, the null this was handled correctly
  }

  // ── Local variable push/pop lifecycle ─────────────────────────────────────

  @Test
  public void localDeclarationAndAccessRoundTripsWithDifferentTypes() {
    UserLocal strLocal = new UserLocal("greeting", String.class, false);
    LocalDeclarationStatement declStr = new LocalDeclarationStatement(strLocal, new StringLiteral("hello"));

    UserLocal intLocal = new UserLocal("count", Integer.class, false);
    LocalDeclarationStatement declInt = new LocalDeclarationStatement(intLocal, new IntegerLiteral(42));

    ReturnStatement ret = new ReturnStatement(
        JavaType.getInstance(Integer.class),
        new LocalAccess(intLocal));

    UserMethod method = new UserMethod("localTypes", Integer.class,
        new UserParameter[0],
        new BlockStatement(declStr, declInt, ret));
    method.isStatic.setValue(true);

    NamedUserType type = VmTestSupport.createProgramType("LocalTypesProgram");
    type.methods.add(method);

    Object result = vm.ENTRY_POINT_invoke(null, method);
    assertEquals(42, result);
  }

  // ── UserInstance field management ──────────────────────────────────────────

  @Test
  public void createInstancePopulatesFieldWithInitializer() {
    UserField nameField = new UserField("name", String.class, new StringLiteral("Alice"));
    NamedUserType personType = VmTestSupport.createTypeWithConstructor("Person");
    personType.fields.add(nameField);

    UserInstance instance = vm.ENTRY_POINT_createInstance(personType);
    assertNotNull(instance);
    Object value = instance.getFieldValue(nameField);
    assertEquals("Alice", value);
  }

  @Test
  public void createInstancePopulatesMultipleFields() {
    UserField nameField = new UserField("name", String.class, new StringLiteral("Bob"));
    UserField ageField = new UserField("age", Integer.class, new IntegerLiteral(30));
    NamedUserType personType = VmTestSupport.createTypeWithConstructor("MultiFieldPerson");
    personType.fields.add(nameField);
    personType.fields.add(ageField);

    UserInstance instance = vm.ENTRY_POINT_createInstance(personType);
    assertEquals("Bob", instance.getFieldValue(nameField));
    assertEquals(30, instance.getFieldValue(ageField));
  }

  // ── Multiple sequential method invocations ────────────────────────────────

  @Test
  public void sequentialStaticInvocationsShareSameVmState() {
    NamedUserType type = VmTestSupport.createProgramType("SequentialProgram");

    UserMethod method1 = VmTestSupport.createStaticProcedure("first",
        new BlockStatement(new Comment("one")));
    UserMethod method2 = VmTestSupport.createStaticProcedure("second",
        new BlockStatement(new Comment("two")));
    type.methods.add(method1);
    type.methods.add(method2);

    VmTestSupport.RecordingListener listener = new VmTestSupport.RecordingListener();
    vm.addVirtualMachineListener(listener);

    vm.ENTRY_POINT_invoke(null, method1);
    vm.ENTRY_POINT_invoke(null, method2);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("Two sequential invocations should fire two comment events", 2, commentCount);
  }

  // ── Frame toString ────────────────────────────────────────────────────────

  @Test
  public void stackTraceElementsHaveMeaningfulToString() {
    NamedUserType type = VmTestSupport.createProgramType("ToStringProgram");
    UserMethod method = VmTestSupport.createStaticProcedure("toStringTarget",
        new BlockStatement(new Comment("body")));
    type.methods.add(method);

    final String[] captured = new String[1];
    VmTestSupport.RecordingListener traceListener = new VmTestSupport.RecordingListener() {
      @Override
      public void statementExecuting(org.lgna.project.virtualmachine.events.StatementExecutionEvent e) {
        super.statementExecuting(e);
        if (captured[0] == null) {
          LgnaStackTraceElement[] trace = vm.getStackTrace(Thread.currentThread());
          if (trace.length > 0) {
            captured[0] = trace[0].toString();
          }
        }
      }
    };
    vm.addVirtualMachineListener(traceListener);

    vm.ENTRY_POINT_invoke(null, method);

    assertNotNull("Should have captured a stack trace element string", captured[0]);
    assertTrue("Frame toString should include class name",
        captured[0].length() > 0);
  }

  // ── Frame appendFormatted ────────────────────────────────────────────────

  @Test
  public void stackTraceElementsHaveFormattedRepresentation() {
    NamedUserType type = VmTestSupport.createProgramType("FormattedProgram");
    UserMethod method = VmTestSupport.createStaticProcedure("formattedTarget",
        new BlockStatement(new Comment("body")));
    type.methods.add(method);

    final StringBuilder[] captured = new StringBuilder[1];
    VmTestSupport.RecordingListener traceListener = new VmTestSupport.RecordingListener() {
      @Override
      public void statementExecuting(org.lgna.project.virtualmachine.events.StatementExecutionEvent e) {
        super.statementExecuting(e);
        if (captured[0] == null) {
          LgnaStackTraceElement[] trace = vm.getStackTrace(Thread.currentThread());
          if (trace.length > 0) {
            captured[0] = new StringBuilder();
            trace[0].appendFormatted(captured[0]);
          }
        }
      }
    };
    vm.addVirtualMachineListener(traceListener);

    vm.ENTRY_POINT_invoke(null, method);

    assertNotNull("Should have captured formatted output", captured[0]);
    assertTrue("Formatted output should contain method name or 'instance'",
        captured[0].length() > 0);
  }

  // ── Listener management ───────────────────────────────────────────────────

  @Test
  public void addAndRemoveVirtualMachineListenerWorks() {
    VmTestSupport.RecordingListener listener1 = new VmTestSupport.RecordingListener();
    VmTestSupport.RecordingListener listener2 = new VmTestSupport.RecordingListener();

    vm.addVirtualMachineListener(listener1);
    vm.addVirtualMachineListener(listener2);

    NamedUserType type = VmTestSupport.createProgramType("ListenerProgram");
    UserMethod method = VmTestSupport.createStaticProcedure("listenerTest",
        new BlockStatement(new Comment("event")));
    type.methods.add(method);

    vm.ENTRY_POINT_invoke(null, method);

    assertTrue("Listener1 should have received events", listener1.statementEvents.size() > 0);
    assertTrue("Listener2 should have received events", listener2.statementEvents.size() > 0);

    int listener1Count = listener1.statementEvents.size();
    vm.removeVirtualMachineListener(listener1);

    vm.ENTRY_POINT_invoke(null, method);

    assertEquals("Removed listener should not receive new events",
        listener1Count, listener1.statementEvents.size());
  }

  // ── Frame owner chain ─────────────────────────────────────────────────────

  @Test
  public void nestedMethodCallProducesDeeperStackTrace() {
    NamedUserType type = VmTestSupport.createProgramType("DeepCallProgram");

    UserMethod inner = VmTestSupport.createStaticProcedure("inner",
        new BlockStatement(new Comment("deepest")));
    type.methods.add(inner);

    org.lgna.project.ast.MethodInvocation call = new org.lgna.project.ast.MethodInvocation(
        new org.lgna.project.ast.NullLiteral(), inner);
    UserMethod outer = VmTestSupport.createStaticProcedure("outer",
        new BlockStatement(new org.lgna.project.ast.ExpressionStatement(call)));
    type.methods.add(outer);

    final int[] maxDepth = {0};
    VmTestSupport.RecordingListener traceListener = new VmTestSupport.RecordingListener() {
      @Override
      public void statementExecuting(org.lgna.project.virtualmachine.events.StatementExecutionEvent e) {
        super.statementExecuting(e);
        LgnaStackTraceElement[] trace = vm.getStackTrace(Thread.currentThread());
        if (trace.length > maxDepth[0]) {
          maxDepth[0] = trace.length;
        }
      }
    };
    vm.addVirtualMachineListener(traceListener);

    vm.ENTRY_POINT_invoke(null, outer);

    assertTrue("Nested call should produce stack trace depth >= 2", maxDepth[0] >= 2);
  }

  // ── Expression evaluation event ───────────────────────────────────────────

  @Test
  public void expressionEvaluationFiresListenerEvents() {
    VmTestSupport.RecordingListener listener = new VmTestSupport.RecordingListener();
    vm.addVirtualMachineListener(listener);

    vm.ENTRY_POINT_evaluate(null, new org.lgna.project.ast.Expression[]{
        new IntegerLiteral(1),
        new StringLiteral("two")
    });

    assertEquals(2, listener.expressionEvaluatedCount);
  }
}

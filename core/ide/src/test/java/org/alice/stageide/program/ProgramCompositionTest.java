package org.alice.stageide.program;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ProgramCompositionTest {

  private static final Class<?> PROGRAM_CONTEXT = ProgramContext.class;
  private static final Class<?> RUN_PROGRAM_CONTEXT = RunProgramContext.class;

  private static final String[] ADAPTER_CLASSES = {
      "org.alice.stageide.apis.story.event.SceneActivationAdapter",
      "org.alice.stageide.apis.story.event.MouseClickOnScreenAdapter",
      "org.alice.stageide.apis.story.event.MouseClickOnObjectAdapter",
      "org.alice.stageide.apis.story.event.KeyAdapter",
      "org.alice.stageide.apis.story.event.ArrowKeyAdapter",
      "org.alice.stageide.apis.story.event.NumberKeyAdapter",
      "org.alice.stageide.apis.story.event.TransformationEventAdapter",
      "org.alice.stageide.apis.story.event.ComesIntoViewEventAdapter",
      "org.alice.stageide.apis.story.event.ComesOutOfViewEventAdapter",
      "org.alice.stageide.apis.story.event.StartCollisionAdapter",
      "org.alice.stageide.apis.story.event.EndCollisionAdapter",
      "org.alice.stageide.apis.story.event.EnterProximityAdapter",
      "org.alice.stageide.apis.story.event.ExitProximityAdapter",
      "org.alice.stageide.apis.story.event.StartOcclusionEventAdapter",
      "org.alice.stageide.apis.story.event.EndOcclusionEventAdapter",
      "org.alice.stageide.apis.story.event.TimerEventAdapter"
  };

  private static Class<?> load(String name) throws Exception {
    return Class.forName(name, false, ProgramCompositionTest.class.getClassLoader());
  }

  private static void assertMethodSignature(Class<?> owner, String name, int expectedModifiers, Class<?> returnType, Class<?>... parameterTypes) throws Exception {
    Method method = owner.getDeclaredMethod(name, parameterTypes);
    assertNotNull(method);
    assertEquals(returnType, method.getReturnType());
    assertEquals(expectedModifiers, method.getModifiers() & expectedModifiers);
  }

  @Test
  public void programContext_isPublicAbstractClassExtendingObject() {
    assertTrue(Modifier.isPublic(PROGRAM_CONTEXT.getModifiers()));
    assertTrue(Modifier.isAbstract(PROGRAM_CONTEXT.getModifiers()));
    assertFalse(PROGRAM_CONTEXT.isInterface());
    assertSame(Object.class, PROGRAM_CONTEXT.getSuperclass());
  }

  @Test
  public void programContext_constructor_acceptsNamedUserType() throws Exception {
    Constructor<?> constructor = PROGRAM_CONTEXT.getConstructor(org.lgna.project.ast.NamedUserType.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void programContext_exposesSevenRequestedPublicMethods() throws Exception {
    assertEquals(org.lgna.project.virtualmachine.UserInstance.class,
        PROGRAM_CONTEXT.getMethod("getProgramInstance").getReturnType());
    assertEquals(org.lgna.story.SProgram.class,
        PROGRAM_CONTEXT.getMethod("getProgram").getReturnType());
    assertEquals(org.lgna.story.implementation.ProgramImp.class,
        PROGRAM_CONTEXT.getMethod("getProgramImp").getReturnType());
    assertEquals(org.lgna.project.virtualmachine.VirtualMachine.class,
        PROGRAM_CONTEXT.getMethod("getVirtualMachine").getReturnType());
    assertEquals(edu.cmu.cs.dennisc.render.OnscreenRenderTarget.class,
        PROGRAM_CONTEXT.getMethod("getOnscreenRenderTarget").getReturnType());
    assertEquals(void.class, PROGRAM_CONTEXT.getMethod("setActiveScene").getReturnType());
    assertEquals(void.class, PROGRAM_CONTEXT.getMethod("cleanUpProgram").getReturnType());
  }

  @Test
  public void programContext_templateMethods_haveProtectedVisibility() throws Exception {
    Method createProgramInstance = PROGRAM_CONTEXT.getDeclaredMethod("createProgramInstance", org.lgna.project.ast.NamedUserType.class);
    Method createVirtualMachine = PROGRAM_CONTEXT.getDeclaredMethod("createVirtualMachine");

    assertTrue(Modifier.isProtected(createProgramInstance.getModifiers()));
    assertTrue(Modifier.isProtected(createVirtualMachine.getModifiers()));
    assertEquals(org.lgna.project.virtualmachine.UserInstance.class, createProgramInstance.getReturnType());
    assertEquals(org.lgna.project.virtualmachine.VirtualMachine.class, createVirtualMachine.getReturnType());
  }

  @Test
  public void programContext_renderingHooks_haveExpectedVisibility() throws Exception {
    Method disableRendering = PROGRAM_CONTEXT.getDeclaredMethod("disableRendering");
    Method enableRendering = PROGRAM_CONTEXT.getDeclaredMethod("enableRendering");

    assertFalse(Modifier.isPublic(disableRendering.getModifiers()));
    assertFalse(Modifier.isProtected(disableRendering.getModifiers()));
    assertFalse(Modifier.isPrivate(disableRendering.getModifiers()));
    assertTrue(Modifier.isPrivate(enableRendering.getModifiers()));
  }

  @Test
  public void programContext_privateFinalFields_exist() throws Exception {
    Field programInstance = PROGRAM_CONTEXT.getDeclaredField("programInstance");
    Field vm = PROGRAM_CONTEXT.getDeclaredField("vm");

    assertTrue(Modifier.isPrivate(programInstance.getModifiers()));
    assertTrue(Modifier.isFinal(programInstance.getModifiers()));
    assertTrue(Modifier.isPrivate(vm.getModifiers()));
    assertTrue(Modifier.isFinal(vm.getModifiers()));
  }

  @Test
  public void runProgramContext_extendsProgramContextAndIsConcrete() {
    assertSame(PROGRAM_CONTEXT, RUN_PROGRAM_CONTEXT.getSuperclass());
    assertTrue(Modifier.isPublic(RUN_PROGRAM_CONTEXT.getModifiers()));
    assertFalse(Modifier.isAbstract(RUN_PROGRAM_CONTEXT.getModifiers()));
  }

  @Test
  public void runProgramContext_constructorAndInitializeInContainer_exist() throws Exception {
    Constructor<?> constructor = RUN_PROGRAM_CONTEXT.getConstructor(org.lgna.project.ast.NamedUserType.class);
    Method initializeInContainer = RUN_PROGRAM_CONTEXT.getMethod(
        "initializeInContainer",
        org.lgna.story.implementation.ProgramImp.AwtContainerInitializer.class);

    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertTrue(Modifier.isPublic(initializeInContainer.getModifiers()));
    assertEquals(void.class, initializeInContainer.getReturnType());
  }

  @Test
  public void runProgramContext_inheritsProgramContextApi() throws Exception {
    assertNotNull(RUN_PROGRAM_CONTEXT.getMethod("getProgramInstance"));
    assertNotNull(RUN_PROGRAM_CONTEXT.getMethod("getProgram"));
    assertNotNull(RUN_PROGRAM_CONTEXT.getMethod("getProgramImp"));
    assertNotNull(RUN_PROGRAM_CONTEXT.getMethod("getVirtualMachine"));
    assertNotNull(RUN_PROGRAM_CONTEXT.getMethod("getOnscreenRenderTarget"));
    assertNotNull(RUN_PROGRAM_CONTEXT.getMethod("setActiveScene"));
    assertNotNull(RUN_PROGRAM_CONTEXT.getMethod("cleanUpProgram"));
  }

  @Test
  public void allSixteenAdapters_extendAbstractAdapterAndAreConcrete() throws Exception {
    Class<?> abstractAdapter = load("org.alice.stageide.apis.story.event.AbstractAdapter");
    assertEquals(16, ADAPTER_CLASSES.length);

    for (String className : ADAPTER_CLASSES) {
      Class<?> adapterClass = load(className);
      assertTrue(className + " should extend AbstractAdapter", abstractAdapter.isAssignableFrom(adapterClass));
      assertFalse(className + " should be concrete", Modifier.isAbstract(adapterClass.getModifiers()));
    }
  }

  @Test
  public void abstractAdapter_isPublicAbstractAndHasProtectedInvokeEntryPoint() throws Exception {
    Class<?> abstractAdapter = load("org.alice.stageide.apis.story.event.AbstractAdapter");
    Method invokeEntryPoint = abstractAdapter.getDeclaredMethod("invokeEntryPoint", Object[].class);

    assertTrue(Modifier.isPublic(abstractAdapter.getModifiers()));
    assertTrue(Modifier.isAbstract(abstractAdapter.getModifiers()));
    assertTrue(Modifier.isProtected(invokeEntryPoint.getModifiers()));
    assertEquals(void.class, invokeEntryPoint.getReturnType());
  }

  @Test
  public void abstractAdapter_privateFinalFields_exist() throws Exception {
    Class<?> abstractAdapter = load("org.alice.stageide.apis.story.event.AbstractAdapter");
    String[] fieldNames = {"context", "userInstance", "lambda", "singleAbstractMethod"};

    for (String fieldName : fieldNames) {
      Field field = abstractAdapter.getDeclaredField(fieldName);
      assertTrue(fieldName + " should be private", Modifier.isPrivate(field.getModifiers()));
      assertTrue(fieldName + " should be final", Modifier.isFinal(field.getModifiers()));
    }
  }

  @Test
  public void sceneAdapter_classExists() throws Exception {
    Class<?> sceneAdapter = load("org.alice.stageide.ast.SceneAdapter");
    assertTrue(Modifier.isPublic(sceneAdapter.getModifiers()));
  }

  @Test
  public void setActiveSceneMethodField_isAJavaMethodNamedSetActiveScene() throws Exception {
    Class<?> managerClass = load("org.alice.stageide.StoryApiConfigurationManager");
    Field field = managerClass.getField("SET_ACTIVE_SCENE_METHOD");
    Object value = field.get(null);

    assertTrue(Modifier.isPublic(field.getModifiers()));
    assertTrue(Modifier.isStatic(field.getModifiers()));
    assertTrue(Modifier.isFinal(field.getModifiers()));
    assertEquals(org.lgna.project.ast.JavaMethod.class, field.getType());
    assertNotNull(value);
    assertEquals("setActiveScene", ((org.lgna.project.ast.JavaMethod) value).getName());
  }

  @Test
  public void programContext_declaresRequestedCoreMethods() throws Exception {
    assertMethodSignature(PROGRAM_CONTEXT, "createProgramInstance", Modifier.PROTECTED, org.lgna.project.virtualmachine.UserInstance.class, org.lgna.project.ast.NamedUserType.class);
    assertMethodSignature(PROGRAM_CONTEXT, "createVirtualMachine", Modifier.PROTECTED, org.lgna.project.virtualmachine.VirtualMachine.class);
    assertMethodSignature(PROGRAM_CONTEXT, "disableRendering", 0, void.class);
    assertMethodSignature(PROGRAM_CONTEXT, "enableRendering", Modifier.PRIVATE, void.class);
  }
}

package org.alice.stageide.program;

import edu.cmu.cs.dennisc.animation.Animation;
import edu.cmu.cs.dennisc.animation.AnimationObserver;
import edu.cmu.cs.dennisc.animation.Animator;
import edu.cmu.cs.dennisc.animation.FrameObserver;
import org.alice.ide.issue.UserProgramRunningStateUtilities;
import org.junit.After;
import org.junit.Test;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.SuperConstructorInvocationStatement;
import org.lgna.project.ast.UserField;
import org.lgna.project.virtualmachine.UserInstance;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;
import org.lgna.story.implementation.ProgramImp;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class RunProgramContextBehaviorTest {
  @After
  public void resetRunningState() {
    UserProgramRunningStateUtilities.setUserProgramRunning(false);
  }

  @Test
  public void constructorCreatesLifecycleContextAndMarksProgramRunning() {
    UserProgramRunningStateUtilities.setUserProgramRunning(false);
    RecordingRunProgramContext context = new RecordingRunProgramContext(programType());

    try {
      assertTrue(UserProgramRunningStateUtilities.isUserProgramRunning());
      assertNotNull(context.getVirtualMachine());
      assertNull(context.getProgramInstance());
      assertNotNull(context.getProgramImp());
      assertNull(context.getOnscreenRenderTarget());
    } finally {
      context.cleanUpProgram();
    }

    assertTrue(context.recordingProgramImp.shutDownCalled);
    assertFalse(UserProgramRunningStateUtilities.isUserProgramRunning());
  }

  @Test
  public void initializeInContainerDisablesRenderingAndDelegatesToProgramImp() {
    RecordingRunProgramContext context = new RecordingRunProgramContext(programType());
    ProgramImp.AwtContainerInitializer initializer = (onscreenRenderTarget, controlPanel) -> {
      // no-op: headless proof only
    };

    try {
      context.initializeInContainer(initializer);

      assertTrue(context.renderingDisabled);
      assertSame(initializer, context.recordingProgramImp.lastInitializer);
    } finally {
      context.cleanUpProgram();
    }

    assertTrue(context.recordingProgramImp.shutDownCalled);
    assertFalse(UserProgramRunningStateUtilities.isUserProgramRunning());
  }

  private static NamedUserType programType() {
    NamedUserType sceneType = createInstantiableType("Scene", JavaType.getInstance(SScene.class));
    NamedUserType programType = createInstantiableType("Program", JavaType.getInstance(SProgram.class));
    programType.fields.add(new UserField("myScene", sceneType));
    return programType;
  }

  private static NamedUserType createInstantiableType(String name, JavaType superType) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(superType);

    NamedUserConstructor constructor = new NamedUserConstructor();
    ConstructorBlockStatement constructorBody = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superConstructorInvocation = new SuperConstructorInvocationStatement();
    superConstructorInvocation.constructor.setValue(superType.getDeclaredConstructor());
    constructorBody.constructorInvocationStatement.setValue(superConstructorInvocation);
    constructor.body.setValue(constructorBody);
    type.constructors.add(constructor);
    return type;
  }

  private static final class RecordingRunProgramContext extends RunProgramContext {
    private final RecordingProgramImp recordingProgramImp = new RecordingProgramImp();
    private boolean renderingDisabled;

    private RecordingRunProgramContext(NamedUserType programType) {
      super(programType);
    }

    @Override
    protected UserInstance createProgramInstance(NamedUserType programType) {
      return null;
    }

    @Override
    void disableRendering() {
      renderingDisabled = true;
    }

    @Override
    public ProgramImp getProgramImp() {
      return recordingProgramImp;
    }
  }

  private static final class RecordingProgramImp extends ProgramImp {
    private ProgramImp.AwtContainerInitializer lastInitializer;
    private boolean shutDownCalled;

    private RecordingProgramImp() {
      super(null, null);
    }

    @Override
    public Animator getAnimator() {
      return SilentAnimator.INSTANCE;
    }

    @Override
    public void initializeInAwtContainer(AwtContainerInitializer awtContainerInitializer) {
      this.lastInitializer = awtContainerInitializer;
    }

    @Override
    public void shutDown() {
      this.shutDownCalled = true;
    }
  }

  private static final class SilentAnimator implements Animator {
    private static final SilentAnimator INSTANCE = new SilentAnimator();

    @Override
    public double getCurrentTime() {
      return 0;
    }

    @Override
    public double getSpeedFactor() {
      return 1.0;
    }

    @Override
    public void setSpeedFactor(double speedFactor) {
    }

    @Override
    public void update() {
    }

    @Override
    public void invokeLater(Animation animation, AnimationObserver animationObserver) {
    }

    @Override
    public void invokeAndWait(Animation animation, AnimationObserver animationObserver)
        throws InterruptedException, InvocationTargetException {
    }

    @Override
    public void invokeAndWait_ThrowRuntimeExceptionsIfNecessary(Animation animation, AnimationObserver animationObserver) {
    }

    @Override
    public void addFrameObserver(FrameObserver runnable) {
    }

    @Override
    public void removeFrameObserver(FrameObserver runnable) {
    }

    @Override
    public void completeAll() {
    }

    @Override
    public void cancelAnimation() {
    }
  }
}

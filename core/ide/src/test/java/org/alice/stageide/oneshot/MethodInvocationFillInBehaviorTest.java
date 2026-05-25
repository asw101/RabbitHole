package org.alice.stageide.oneshot;

import org.alice.ide.instancefactory.InstanceFactory;
import org.alice.ide.instancefactory.ThisInstanceFactory;
import org.alice.stageide.oneshot.edits.AllJointLocalTransformationsEdit;
import org.alice.stageide.oneshot.edits.LocalTransformationEdit;
import org.alice.stageide.oneshot.edits.SetOpacityEdit;
import org.alice.stageide.oneshot.edits.SetPaintEdit;
import org.alice.stageide.oneshot.edits.StrikePoseEdit;
import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.SimpleArgument;

import java.util.Collections;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MethodInvocationFillInBehaviorTest {
  private static final InstanceFactory INSTANCE_FACTORY = ThisInstanceFactory.getInstance();
  private static final JavaMethod NO_ARG_METHOD = JavaMethod.getInstance(Object.class, "toString");
  private static final JavaType OBJECT_TYPE = JavaType.getInstance(Object.class);

  @Test
  public void setPaintFillIn_cachesAcrossOverloads_andCreatesSetPaintEdit() {
    SetPaintMethodInvocationFillIn fillIn = SetPaintMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, NO_ARG_METHOD);

    assertSame(fillIn, SetPaintMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, OBJECT_TYPE, "toString"));
    assertSame(fillIn, SetPaintMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, Object.class, "toString"));
    assertNull(fillIn.getTransientValue(null));

    MethodInvocationEditFactory factory = fillIn.createValue(null);
    assertTrue(factory instanceof SetPaintMethodInvocationEditFactory);
    assertTrue(factory.createEdit(null) instanceof SetPaintEdit);
  }

  @Test
  public void setOpacityFillIn_cachesAcrossOverloads_andCreatesSetOpacityEdit() {
    SetOpacityMethodInvocationFillIn fillIn = SetOpacityMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, NO_ARG_METHOD);

    assertSame(fillIn, SetOpacityMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, OBJECT_TYPE, "toString"));
    assertSame(fillIn, SetOpacityMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, Object.class, "toString"));
    assertNull(fillIn.getTransientValue(null));

    MethodInvocationEditFactory factory = fillIn.createValue(null);
    assertTrue(factory instanceof SetOpacityMethodInvocationEditFactory);
    assertTrue(factory.createEdit(null) instanceof SetOpacityEdit);
  }

  @Test
  public void localTransformationFillIn_cachesAcrossOverloads_andCreatesLocalTransformationEdit() {
    LocalTransformationMethodInvocationFillIn fillIn = LocalTransformationMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, NO_ARG_METHOD);

    assertSame(fillIn, LocalTransformationMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, OBJECT_TYPE, "toString"));
    assertSame(fillIn, LocalTransformationMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, Object.class, "toString"));
    assertNull(fillIn.getTransientValue(null));

    MethodInvocationEditFactory factory = fillIn.createValue(null);
    assertTrue(factory instanceof LocalTransformationMethodInvocationEditFactory);
    assertTrue(factory.createEdit(null) instanceof LocalTransformationEdit);
  }

  @Test
  public void allJointTransformationsFillIn_cachesAcrossOverloads_andCreatesExpectedEdit() {
    AllJointLocalTransformationsMethodInvocationFillIn fillIn = AllJointLocalTransformationsMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, NO_ARG_METHOD);

    assertSame(fillIn, AllJointLocalTransformationsMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, OBJECT_TYPE, "toString"));
    assertSame(fillIn, AllJointLocalTransformationsMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, Object.class, "toString"));
    assertNull(fillIn.getTransientValue(null));

    MethodInvocationEditFactory factory = fillIn.createValue(null);
    assertTrue(factory instanceof AllJointLocalTransformationsMethodInvocationEditFactory);
    assertTrue(factory.createEdit(null) instanceof AllJointLocalTransformationsEdit);
  }

  @Test
  public void javaDefinedStrikePoseFillIn_cachesAcrossOverloads_andCreatesStrikePoseEdit() {
    JavaDefinedStrikePoseMethodInvocationFillIn fillIn = JavaDefinedStrikePoseMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, NO_ARG_METHOD);

    assertSame(fillIn, JavaDefinedStrikePoseMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, OBJECT_TYPE, "toString"));
    assertSame(fillIn, JavaDefinedStrikePoseMethodInvocationFillIn.getInstance(INSTANCE_FACTORY, Object.class, "toString"));
    assertNull(fillIn.getTransientValue(null));

    MethodInvocationEditFactory factory = fillIn.createValue(null);
    assertTrue(factory instanceof StrikePoseMethodInvocationEditFactory);
    assertTrue(factory.createEdit(null) instanceof StrikePoseEdit);
  }

  @Test
  public void strikePoseFillIn_cachesAndCreatesStrikePoseEditWithoutArguments() {
    StrikePoseMethodInvocationFillIn fillIn = StrikePoseMethodInvocationFillIn.getInstance(
        INSTANCE_FACTORY,
        NO_ARG_METHOD,
        Collections.<SimpleArgument>emptyList()
    );

    assertSame(fillIn, StrikePoseMethodInvocationFillIn.getInstance(
        INSTANCE_FACTORY,
        NO_ARG_METHOD,
        Collections.<SimpleArgument>emptyList()
    ));
    assertNull(fillIn.getTransientValue(null));

    MethodInvocationEditFactory factory = fillIn.createValue(null);
    assertTrue(factory instanceof StrikePoseMethodInvocationEditFactory);
    assertTrue(factory.createEdit(null) instanceof StrikePoseEdit);
  }
}

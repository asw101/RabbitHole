package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class InstanceFactoryHeadlessSweepTest {

  @Test
  public void exerciseInstanceFactoryClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.instancefactory.AbstractInstanceFactory",
        "org.alice.ide.instancefactory.InstanceFactory",
        "org.alice.ide.instancefactory.InstanceFactoryUtilities",
        "org.alice.ide.instancefactory.LocalAccessFactory",
        "org.alice.ide.instancefactory.LocalAccessMethodInvocationFactory",
        "org.alice.ide.instancefactory.MethodInvocationFactory",
        "org.alice.ide.instancefactory.ParameterAccessFactory",
        "org.alice.ide.instancefactory.ParameterAccessMethodInvocationFactory",
        "org.alice.ide.instancefactory.ParameterAccessMethodInvocationMethodInvocationFactory",
        "org.alice.ide.instancefactory.ThisFieldAccessArrayElementMethodInvocationFactory",
        "org.alice.ide.instancefactory.ThisFieldAccessFactory",
        "org.alice.ide.instancefactory.ThisFieldAccessMethodInvocationFactory",
        "org.alice.ide.instancefactory.ThisInstanceFactory",
        "org.alice.ide.instancefactory.ThisMethodInvocationFactory",
        "org.alice.ide.instancefactory.croquet.InstanceFactoryState",
        "org.alice.ide.instancefactory.croquet.InstanceFactoryStateLogic",
        "org.alice.ide.instancefactory.croquet.ParametersVariablesAndConstantsSeparator",
        "org.alice.ide.instancefactory.croquet.TypeCascadeMenuModel",
        "org.alice.ide.instancefactory.croquet.codecs.InstanceFactoryCodec"
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }
}

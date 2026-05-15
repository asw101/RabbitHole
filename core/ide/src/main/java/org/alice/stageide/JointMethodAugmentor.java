/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.alice.stageide;

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import org.alice.ide.identifier.IdentifierNameGenerator;
import org.lgna.project.annotations.FieldTemplate;
import org.lgna.project.annotations.Visibility;
import org.lgna.project.ast.*;
import org.lgna.story.*;
import org.lgna.story.resources.DynamicResource;
import org.lgna.story.resources.JointArrayId;
import org.lgna.story.resources.JointId;

import java.lang.reflect.Field;

/**
 * Generates joint-accessor methods on user types that extend SJointedModel.
 * Extracted from StoryApiConfigurationManager to reduce its size.
 */
final class JointMethodAugmentor {

  private static final JavaType JOINTED_MODEL_TYPE = JavaType.getInstance(SJointedModel.class);
  private static final JavaMethod GET_JOINT_METHOD = JOINTED_MODEL_TYPE.getDeclaredMethod("getJoint", JointId.class);
  private static final JavaMethod GET_JOINT_BY_NAME_METHOD = JOINTED_MODEL_TYPE.getDeclaredMethod("getJoint", String.class);
  private static final JavaMethod GET_JOINT_ARRAY_METHOD = JOINTED_MODEL_TYPE.getDeclaredMethod("getJointArray", JointId[].class);
  private static final JavaMethod GET_JOINT_ARRAY_ID_METHOD = JOINTED_MODEL_TYPE.getDeclaredMethod("getJointArray", JointArrayId.class);
  private static final JavaMethod STRIKE_POSE_METHOD = JOINTED_MODEL_TYPE.getDeclaredMethod("strikePose", Pose.class, StrikePose.Detail[].class);

  private JointMethodAugmentor() {
  }

  static UserType<?> augment(UserType<?> rv) {
    if (JOINTED_MODEL_TYPE.isAssignableFrom(rv)) {
      AbstractConstructor constructor0 = rv.getFirstDeclaredConstructor();
      //We have multiple different types of constructors to consider:
      // No parameters:
      // public Alien() { super(AlienResource.DEFAULT); }
      // public Alien2() { super(new DynamicBipedResource("Alien2", "Alien2")); }
      //
      // 1 parameter:
      // public Alice(AliceResource resource) { super(resource); }
      // public Biped(BipedResource resource) { super(resource); }
      Object firstArgument = constructor0.instantiateFirstArgumentPassedToSuperConstructor();
      AbstractType<?, ?, ?> constructorParameterType = constructor0.getFirstParameterType();
      AbstractType<?, ?, ?> inferredResourceType = constructorParameterType;
      if (inferredResourceType == null) {
        JavaField field = getArgumentField(constructor0);
        if (field != null) {
          inferredResourceType = field.getValueType();
        }
      }
      JavaType ancestorType = rv.getFirstEncounteredJavaType();
      if (constructorParameterType != ancestorType.getFirstParameterType()) {
        if (inferredResourceType != null) {
          addMethodsToType(rv, inferredResourceType);
        } else if (firstArgument instanceof DynamicResource resource) {
          addMethodsToType(rv, resource);
        } else {
          Logger.severe("Failed to augment type " + rv + ". Unable to find model resource type.");
        }
      }
    }
    return rv;
  }

  private static String getFieldMethodNameHint(AbstractField field) {
    if (field instanceof JavaField javaField) {
      Field fld = javaField.getFieldReflectionProxy().getReification();
      if (fld != null) {
        FieldTemplate annotation = fld.getAnnotation(FieldTemplate.class);
        if (annotation != null) {
          String methodNameHint = annotation.methodNameHint();
          if (!methodNameHint.isEmpty()) {
            return methodNameHint;
          }
        }
      }
    }
    return null;
  }

  private static void addMethodsToType(UserType<?> userType, DynamicResource dynamicResource) {
    for (JointId joint : dynamicResource.getModelSpecificJoints()) {
      if (joint.getVisibility() == Visibility.COMPLETELY_HIDDEN) {
        continue;
      }
      String methodName = IdentifierNameGenerator.SINGLETON.convertConstantNameToMethodName(joint.toString(), "get");
      UserMethod method = AstUtilities.createFunction(methodName, SJoint.class);
      method.managementLevel.setValue(ManagementLevel.GENERATED);
      BlockStatement body = method.body.getValue();
      Expression expression = AstUtilities.createMethodInvocation(new ThisExpression(), GET_JOINT_BY_NAME_METHOD, new StringLiteral(joint.toString()));
      body.statements.add(AstUtilities.createReturnStatement(SJoint.class, expression));
      userType.methods.add(method);
    }
  }

  private static void addMethodsToType(UserType<?> userType, AbstractType<?, ?, ?> resourceType) {
    for (AbstractField field : resourceType.getDeclaredFields()) {
      if (!field.isStatic() || field.getVisibility() == Visibility.COMPLETELY_HIDDEN) {
        continue;
      }
      AbstractType<?, ?, ?> valueType = field.getValueType();
      if (valueType.isAssignableTo(JointId.class)) {
        String methodName = getFieldMethodNameHint(field);
        if (methodName == null) {
          methodName = IdentifierNameGenerator.SINGLETON.convertConstantNameToMethodName(field.getName(), "get");
        }
        UserMethod method = AstUtilities.createFunction(methodName, SJoint.class);
        method.managementLevel.setValue(ManagementLevel.GENERATED);
        BlockStatement body = method.body.getValue();
        Expression expression = AstUtilities.createMethodInvocation(new ThisExpression(), GET_JOINT_METHOD, AstUtilities.createStaticFieldAccess(field));
        body.statements.add(AstUtilities.createReturnStatement(SJoint.class, expression));
        userType.methods.add(method);
      } else if (valueType.isAssignableTo(JointId[].class)) {
        String methodName = getFieldMethodNameHint(field);
        if (methodName == null) {
          methodName = IdentifierNameGenerator.SINGLETON.convertConstantNameToMethodName(field.getName(), "get");
        }
        UserMethod method = AstUtilities.createFunction(methodName, SJoint[].class);
        method.managementLevel.setValue(ManagementLevel.GENERATED);
        BlockStatement body = method.body.getValue();
        Expression expression = AstUtilities.createMethodInvocation(new ThisExpression(), GET_JOINT_ARRAY_METHOD, AstUtilities.createStaticFieldAccess(field));
        body.statements.add(AstUtilities.createReturnStatement(SJoint[].class, expression));
        userType.methods.add(method);
      } else if (valueType.isAssignableTo(JointArrayId.class)) {
        String methodName = getFieldMethodNameHint(field);
        if (methodName == null) {
          methodName = IdentifierNameGenerator.SINGLETON.convertConstantNameToMethodName(field.getName(), "get");
        }
        UserMethod method = AstUtilities.createFunction(methodName, SJoint[].class);
        method.managementLevel.setValue(ManagementLevel.GENERATED);
        BlockStatement body = method.body.getValue();
        Expression expression = AstUtilities.createMethodInvocation(new ThisExpression(), GET_JOINT_ARRAY_ID_METHOD, AstUtilities.createStaticFieldAccess(field));
        body.statements.add(AstUtilities.createReturnStatement(SJoint[].class, expression));
        userType.methods.add(method);
      } else if (valueType.isAssignableTo(Pose.class)) {
        String methodName = getFieldMethodNameHint(field);
        if (methodName == null) {
          methodName = IdentifierNameGenerator.SINGLETON.convertConstantNameToMethodName(field.getName());
        }
        UserMethod method = AstUtilities.createProcedure(methodName);
        method.managementLevel.setValue(ManagementLevel.GENERATED);
        BlockStatement body = method.body.getValue();
        MethodInvocation mi = AstUtilities.createMethodInvocation(new ThisExpression(), STRIKE_POSE_METHOD, AstUtilities.createStaticFieldAccess(field));
        body.statements.add(new ExpressionStatement(mi));
        userType.methods.add(method);
      }
    }
  }

  private static JavaField getArgumentField(AbstractConstructor constructor0) {
    if (!(constructor0 instanceof NamedUserConstructor namedUserConstructor)) {
      return null;
    }

    ConstructorInvocationStatement constructorInvocationStatement = namedUserConstructor.body.getValue().constructorInvocationStatement.getValue();
    SimpleArgumentListProperty args = constructorInvocationStatement.requiredArguments;
    return args.getJavaField();
  }
}

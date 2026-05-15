/*
 * Copyright (c) 2006-2011, Carnegie Mellon University. All rights reserved.
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
 */

package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.java.io.TextFileUtilities;
import edu.cmu.cs.dennisc.java.lang.reflect.ReflectionUtilities;
import edu.cmu.cs.dennisc.pattern.Tuple2;
import org.alice.math.immutable.AffineMatrix4x4;
import org.lgna.story.BipedPoseBuilder;
import org.lgna.story.FlyerPoseBuilder;
import org.lgna.story.JointedModelPoseBuilder;
import org.lgna.story.Pose;
import org.lgna.story.QuadrupedPoseBuilder;
import org.lgna.story.SlithererPoseBuilder;
import org.lgna.story.SwimmerPoseBuilder;
import org.lgna.story.implementation.alice.AliceResourceClassUtilities;
import org.lgna.story.implementation.alice.AliceResourceUtilities;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.FlyerResource;
import org.lgna.story.resources.JointArrayId;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resources.QuadrupedResource;
import org.lgna.story.resources.SlithererResource;
import org.lgna.story.resources.SwimmerResource;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.Map;

final class ModelResourceJavaGenerator {

  private static final boolean REMOVE_ROOT_JOINTS = false;
  static final String ROOT_IDS_FIELD_NAME = "JOINT_ID_ROOTS";
  static final String ROOT_IDS_METHOD_NAME = "getRootJointIds";

  private ModelResourceJavaGenerator() {
  }

  static String getJavaClassName(ModelResourceExporter exporter) {
    return exporter.getClassName() + AliceResourceClassUtilities.RESOURCE_SUFFIX;
  }

  static Field getJointRootsField(Class<?> cls) {
    if (cls == null) {
      return null;
    }
    Field[] rootFields = AliceResourceClassUtilities.getFieldsOfType(cls, JointId[].class);
    if (rootFields.length == 1) {
      return rootFields[0];
    } else {
      Class[] interfaces = cls.getInterfaces();
      for (Class i : interfaces) {
        Field rootField = getJointRootsField(i);
        if (rootField != null) {
          return rootField;
        }
      }
    }
    return null;
  }

  static boolean needsToDefineRootsMethod(Class<?> cls) {
    if (cls == null) {
      return false;
    }
    Method[] methods = cls.getMethods();
    for (Method m : methods) {
      if (JointId[].class.isAssignableFrom(m.getReturnType())) {
        return true;
      }
    }
    Class[] interfaces = cls.getInterfaces();
    for (Class i : interfaces) {
      boolean needToDefineMethod = needsToDefineRootsMethod(i);
      if (needToDefineMethod) {
        return needToDefineMethod;
      }
    }
    return false;
  }

  private static String createResourceEnumName(ModelResourceExporter parentExporter, String modelName, String textureName) {
    if (modelName.equalsIgnoreCase(parentExporter.getClassName())) {
      return AliceResourceUtilities.makeEnumName(textureName);
    }
    String modelEnumName = AliceResourceUtilities.makeEnumName(modelName);
    if (modelName.equalsIgnoreCase(textureName) || textureName.equalsIgnoreCase(AliceResourceUtilities.getDefaultTextureEnumName(modelName)) || textureName.equalsIgnoreCase(modelEnumName)) {
      return modelEnumName;
    } else {
      return modelEnumName + "_" + AliceResourceUtilities.makeEnumName(textureName);
    }
  }

  static String createResourceEnumName(ModelResourceExporter parentExporter, ModelSubResourceExporter resource) {
    return createResourceEnumName(parentExporter, resource.getModelName(), resource.getTextureName());
  }

  static String createResourceEnumNameForModelAndTexture(ModelResourceExporter parentExporter, String modelName, String textureName) {
    return createResourceEnumName(parentExporter, modelName, textureName);
  }

  static boolean isValidEnumName(ModelResourceExporter exporter, String modelName, String enumName) {
    Map<String, List<String>> forcedEnumNamesMap = exporter.getForcedEnumNamesMap();
    if (forcedEnumNamesMap.containsKey(modelName)) {
      List<String> validEnums = forcedEnumNamesMap.get(modelName);
      for (String e : validEnums) {
        String otherToCheck = modelName.toUpperCase() + "_" + e;
        if (e.equalsIgnoreCase(enumName) || otherToCheck.equalsIgnoreCase(enumName)) {
          return true;
        }
      }
      return false;
    }
    List<String> forcedOverridingEnumNames = exporter.getForcedOverridingEnumNames();
    if (forcedOverridingEnumNames.isEmpty()) {
      return true;
    }
    for (String e : forcedOverridingEnumNames) {
      if (e.equalsIgnoreCase(enumName)) {
        return true;
      }
    }
    return false;
  }

  static List<Tuple2<String, String>> makeCodeReadyTree(List<Tuple2<String, String>> sourceList) {
    return ModelResourceJointTreeUtilities.makeCodeReadyTree(sourceList, REMOVE_ROOT_JOINTS);
  }

  static String getJointAccessMethodNameForArrayJoint(String jointName) {
    String arrayName = ModelResourceArrayUtilities.getArrayNameForJoint(jointName, null, null);
    return getAccessorMethodName(arrayName);
  }

  static File createJavaFile(ModelResourceExporter exporter, String root) throws DataFormatException {
    String javaCode = buildJavaCodeBody(exporter);
    File javaFile = ModelResourceFileUtilities.getJavaFile(root, exporter.getPackageString(), getJavaClassName(exporter));
    TextFileUtilities.write(javaFile, javaCode);
    return javaFile;
  }

  static String getAccessorMethodName(String arrayName) {
    return "get" + AliceResourceUtilities.enumToCamelCase(arrayName);
  }

  static boolean needsAccessorMethodForFieldName(ModelClassData classData, String fieldName) {
    String fieldAccessorMethodName = getAccessorMethodName(fieldName);
    try {
      Method m = classData.superClass.getMethod(fieldAccessorMethodName);
      if ((m != null) && m.getDeclaringClass().isInterface()) {
        return true;
      }
    } catch (NoSuchMethodException me) {
    }
    return false;
  }

  static List<Method> getMandatoryMethods(Class<?> superClass, Class<?> returnType) {
    List<Method> methods = new ArrayList<>();
    for (Method method : superClass.getMethods()) {
      if (returnType.isAssignableFrom(method.getReturnType()) && method.getDeclaringClass().isInterface()) {
        methods.add(method);
      }
    }
    return methods;
  }

  static List<String> getMandatoryJointArrayNames(Class<?> superClass) {
    List<String> arrayNames = new ArrayList<>();
    for (Method method : getMandatoryMethods(superClass, JointId[].class)) {
      String methodName = method.getName();
      if (methodName.startsWith("get")) {
        if (!methodName.equals("getRootJointIds")) {
          String newName = methodName.substring(3);
          int arrayIndex = newName.indexOf("Array");
          if (arrayIndex != -1) {
            newName = newName.substring(0, arrayIndex);
          }
          arrayNames.add(AliceResourceUtilities.makeEnumName(newName));
        }
      } else {
        System.err.println("FROM " + superClass
            + ": UNABLE TO CONVERT " + methodName + " INTO AN ARRAY NAME.");
      }
    }
    return arrayNames;
  }

  static List<String> getMandatoryPoseNames(Class<?> superClass) {
    List<String> poseNames = new ArrayList<>();
    for (Method method : getMandatoryMethods(superClass, Pose.class)) {
      String methodName = method.getName();
      if (methodName.startsWith("get")) {
        String newName = methodName.substring(3);
        int poseIndex = newName.indexOf("Pose");
        if (poseIndex != -1) {
          newName = newName.substring(0, poseIndex);
        }
        poseNames.add(AliceResourceUtilities.makeEnumName(newName));
      } else {
        System.err.println("FROM " + superClass
            + ": UNABLE TO CONVERT " + methodName + " INTO POSE NAME.");
      }
    }
    return poseNames;
  }

  // Retained for test coverage — called by 7 tests in ModelResourceJavaGeneratorTest.
  static Class<?> getPoseBuilderTypeForSuperClass(Class<?> superClass) {
    if (FlyerResource.class.isAssignableFrom(superClass)) {
      return FlyerPoseBuilder.class;
    } else if (BipedResource.class.isAssignableFrom(superClass)) {
      return BipedPoseBuilder.class;
    } else if (QuadrupedResource.class.isAssignableFrom(superClass)) {
      return QuadrupedPoseBuilder.class;
    } else if (SwimmerResource.class.isAssignableFrom(superClass)) {
      return SwimmerPoseBuilder.class;
    } else if (SlithererResource.class.isAssignableFrom(superClass)) {
      return SlithererPoseBuilder.class;
    }
    return JointedModelPoseBuilder.class;
  }

  static List<String> getAlreadyDeclaredJointArrayNames(Class<?> superClass) {
    List<String> fieldNames = new ArrayList<>();
    for (Field field : ReflectionUtilities.getPublicStaticFinalFields(superClass, JointArrayId.class)) {
      fieldNames.add(field.getName());
    }
    return fieldNames;
  }

  static String buildJavaCodeBody(ModelResourceExporter exporter) throws java.util.zip.DataFormatException {
      StringBuilder sb = new StringBuilder(4096);
      String javaClassName = getJavaClassName(exporter);
      ModelClassData classData = exporter.getClassData();

      ResourceCodeTemplates.appendPreambleAndEnumConstants(sb, exporter, javaClassName);
      Set<String> existingIds = new HashSet<>(getExistingJointIds(classData.superClass));
      boolean addedRoots = false;
      List<Tuple2<String, String>> trimmedSkeleton = makeCodeReadyTree(exporter.getJointList());
      if (trimmedSkeleton != null) {
        Map<String, List<String>> arrayEntries;
        if (exporter.isEnableArraySupport()) {
          arrayEntries = ModelResourceArrayUtilities.getArrayEntriesFromJointList(trimmedSkeleton, exporter.getCustomArrayNameMap(), exporter.getJointIdsToSuppress(), exporter.getArrayNamesToSkip());
        } else {
          arrayEntries = new HashMap<>();
        }

        Map<String, String> jointToArrayName = new HashMap<>();
        for (Map.Entry<String, List<String>> ae : arrayEntries.entrySet()) {
          for (String joint : ae.getValue()) {
            jointToArrayName.put(joint, ae.getKey());
          }
        }
        Set<String> suppressJointIds = new HashSet<>(exporter.getJointIdsToSuppress());
        Set<String> hideElementArrays = new HashSet<>(exporter.getArraysToHideElementsOf());
        Set<String> exposeFirstArrays = new HashSet<>(exporter.getArraysToExposeFirstElementOf());

        Map<String, Map<String, AffineMatrix4x4>> poseEntries = new HashMap<>(exporter.getPoses());

        List<String> rootJoints = ResourceCodeTemplates.appendJointDeclarations(
            sb, trimmedSkeleton, existingIds, jointToArrayName,
            suppressJointIds, hideElementArrays, exposeFirstArrays, javaClassName);
        addedRoots = !rootJoints.isEmpty();

        if (addedRoots) {
          ResourceCodeTemplates.appendRootJointIds(sb, rootJoints);
        }

        List<String> mandatoryPoseNames = getMandatoryPoseNames(classData.superClass);
        ResourceCodeTemplates.appendPoseFields(sb, poseEntries, mandatoryPoseNames,
            classData, javaClassName);

        List<String> mandatoryArrayNames = getMandatoryJointArrayNames(classData.superClass);
        List<String> declaredArrays = getAlreadyDeclaredJointArrayNames(classData.superClass);
        ResourceCodeTemplates.appendArrayFields(sb, arrayEntries, mandatoryArrayNames,
            declaredArrays, trimmedSkeleton, hideElementArrays,
            classData, javaClassName);
      }

      ResourceCodeTemplates.appendConstructorsAndMethods(sb, addedRoots,
          classData, javaClassName);

      return sb.toString();
  }

  static List<String> getExistingJointIds(Class<?> resourceClass) {
    List<String> ids = new ArrayList<>();
    collectExistingJointIds(resourceClass, ids);
    return ids;
  }

  private static void collectExistingJointIds(Class<?> resourceClass, List<String> ids) {
    for (Field f : resourceClass.getDeclaredFields()) {
      if (JointId.class.isAssignableFrom(f.getType())) {
        ids.add(f.getName());
      }
    }
    for (Class<?> i : resourceClass.getInterfaces()) {
      collectExistingJointIds(i, ids);
    }
  }

  static String getAccessorMethodsForResourceClass(Class<? extends JointedModelResource> resourceClass) {
    StringBuilder sb = new StringBuilder();
    String className = resourceClass.getCanonicalName();
    for (String id : getExistingJointIds(resourceClass)) {
      sb.append("public Joint get").append(AliceResourceClassUtilities.getAliceMethodNameForEnum(id)).append("() {\n");
      sb.append("\t return org.lgna.story.Joint.getJoint( this, ").append(className).append(".").append(id).append(");\n");
      sb.append("}\n");
    }
    return sb.toString();
  }

  static String getJointAccessCodeForClass(Class<?> resourceClass) {
    StringBuilder sb = new StringBuilder();
    String className = resourceClass.getName();
    for (String id : getExistingJointIds(resourceClass)) {
      sb.append("public org.lgna.story.Joint get").append(AliceResourceClassUtilities.getAliceMethodNameForEnum(id)).append("() {\n");
      sb.append("\treturn org.lgna.story.Joint.getJoint( this, ").append(className).append(".").append(id).append(" );\n");
      sb.append("}\n");
    }
    return sb.toString();
  }
}

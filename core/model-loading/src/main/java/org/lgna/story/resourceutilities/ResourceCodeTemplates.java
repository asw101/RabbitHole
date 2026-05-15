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

import edu.cmu.cs.dennisc.pattern.Tuple2;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.UnitQuaternion;
import org.lgna.story.JointedModelPose;
import org.lgna.story.resources.ImplementationAndVisualType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;

/**
 * Package-private helper containing code-generation templates extracted from
 * {@link ModelResourceJavaGenerator#buildJavaCodeBody}. Each static method
 * appends a logical section of the generated Java enum to the supplied
 * {@link StringBuilder}. All methods are stateless and side-effect free.
 */
final class ResourceCodeTemplates {

  private ResourceCodeTemplates() {
  }

  static void appendPreambleAndEnumConstants(StringBuilder sb, ModelResourceExporter exporter, String javaClassName) {
    ModelClassData classData = exporter.getClassData();
    sb.append(JavaCodeUtilities.getCopyrightComment());
    sb.append(JavaCodeUtilities.LINE_RETURN);
    sb.append("package " + classData.packageString + ";" + JavaCodeUtilities.LINE_RETURN + JavaCodeUtilities.LINE_RETURN);
    sb.append("import org.lgna.project.annotations.*;" + JavaCodeUtilities.LINE_RETURN);
    sb.append("import org.lgna.story.implementation.JointIdTransformationPair;" + JavaCodeUtilities.LINE_RETURN);
    sb.append("import org.lgna.story.Orientation;" + JavaCodeUtilities.LINE_RETURN);
    sb.append("import org.lgna.story.Position;" + JavaCodeUtilities.LINE_RETURN);
    sb.append("import org.lgna.story.resources.ImplementationAndVisualType;" + JavaCodeUtilities.LINE_RETURN + JavaCodeUtilities.LINE_RETURN);
    if (exporter.isDeprecated()) {
      sb.append("@Deprecated" + JavaCodeUtilities.LINE_RETURN);
    }
    sb.append("public enum " + javaClassName + " implements " + classData.superClass.getCanonicalName() + " {" + JavaCodeUtilities.LINE_RETURN);
    appendEnumConstants(sb, exporter);
    sb.append(";" + JavaCodeUtilities.LINE_RETURN);
  }

  private static void appendEnumConstants(StringBuilder sb, ModelResourceExporter exporter) {
    assert !exporter.getSubResources().isEmpty();
    boolean isFirst = true;
    for (ModelSubResourceExporter resource : exporter.getSubResources()) {
      String resourceEnumName = ModelResourceJavaGenerator.createResourceEnumName(exporter, resource);
      if (ModelResourceJavaGenerator.isValidEnumName(exporter, resource.getModelName(), resourceEnumName)) {
        if (!isFirst) {
          sb.append("," + JavaCodeUtilities.LINE_RETURN);
        }
        String typeString = "";
        if (!resource.getTypeString().equals(ImplementationAndVisualType.ALICE.toString())) {
          typeString = "( ImplementationAndVisualType." + resource.getTypeString() + " )";
        }
        sb.append("\t" + resourceEnumName + typeString);
        isFirst = false;
      } else {
        System.out.println("SKIPPING ENUM NAME: " + resourceEnumName);
      }
    }
  }

  /**
   * Appends JointId field declarations for each joint in the skeleton that is not
   * already declared in a parent class. Returns the list of root joint names found.
   */
  static List<String> appendJointDeclarations(
      StringBuilder sb,
      List<Tuple2<String, String>> trimmedSkeleton,
      Set<String> existingIds,
      Map<String, String> jointToArrayName,
      Set<String> suppressJointIds,
      Set<String> hideElementArrays,
      Set<String> exposeFirstArrays,
      String javaClassName) {

    List<String> rootJoints = new ArrayList<>();
    sb.append(JavaCodeUtilities.LINE_RETURN);
    for (Tuple2<String, String> entry : trimmedSkeleton) {
      String jointString = entry.getA();
      String parentString = entry.getB();
      if (existingIds.contains(jointString)) {
        continue;
      }
      String arrayNameForJoint = jointToArrayName.get(jointString);
      boolean hiddenInArray = (arrayNameForJoint != null) && hideElementArrays.contains(arrayNameForJoint);
      if (!hiddenInArray) {
        if ((parentString == null) || (parentString.length() == 0)) {
          parentString = "null";
          rootJoints.add(jointString);
        }
        boolean suppressJoint = suppressJointIds.contains(jointString) || ModelResourceJointTreeUtilities.isRootJoint(jointString);
        boolean suppressInArray = (arrayNameForJoint != null)
            && !(exposeFirstArrays.contains(arrayNameForJoint) && ModelResourceArrayUtilities.getArrayIndexForJoint(jointString) == 0);
        if (suppressJoint || suppressInArray) {
          sb.append("@FieldTemplate(visibility=Visibility.COMPLETELY_HIDDEN)" + JavaCodeUtilities.LINE_RETURN);
        } else {
          if (arrayNameForJoint != null) {
            sb.append("@FieldTemplate(visibility=Visibility.PRIME_TIME, methodNameHint=\"" + ModelResourceJavaGenerator.getJointAccessMethodNameForArrayJoint(jointString) + "\")" + JavaCodeUtilities.LINE_RETURN);
          } else {
            sb.append("@FieldTemplate(visibility=Visibility.PRIME_TIME)" + JavaCodeUtilities.LINE_RETURN);
          }
        }
        sb.append("\tpublic static final org.lgna.story.resources.JointId " + jointString + " = new org.lgna.story.resources.JointId( " + parentString + ", " + javaClassName + ".class );" + JavaCodeUtilities.LINE_RETURN);
      }
    }
    return rootJoints;
  }

  static void appendRootJointIds(StringBuilder sb, List<String> rootJoints) {
    sb.append("\n@FieldTemplate( visibility = org.lgna.project.annotations.Visibility.COMPLETELY_HIDDEN )");
    sb.append("\n\tpublic static final org.lgna.story.resources.JointId[] " + ModelResourceJavaGenerator.ROOT_IDS_FIELD_NAME + " = { ");
    for (int i = 0; i < rootJoints.size(); i++) {
      sb.append(rootJoints.get(i));
      if (i < (rootJoints.size() - 1)) {
        sb.append(", ");
      }
    }
    sb.append(" };" + JavaCodeUtilities.LINE_RETURN);
  }

  static void appendPoseFields(
      StringBuilder sb,
      Map<String, Map<String, AffineMatrix4x4>> poseEntries,
      List<String> mandatoryPoseNames,
      ModelClassData classData,
      String javaClassName) throws DataFormatException {

    if (poseEntries.isEmpty() && mandatoryPoseNames.isEmpty()) {
      return;
    }
    for (String mandatoryPose : mandatoryPoseNames) {
      if (!poseEntries.containsKey(mandatoryPose)) {
        throw new DataFormatException("Missing pose definition for " + mandatoryPose + " on class " + classData.superClass);
      }
    }
    for (Map.Entry<String, Map<String, AffineMatrix4x4>> poseEntry : poseEntries.entrySet()) {
      Map<String, AffineMatrix4x4> poseData = poseEntry.getValue();
      if (poseData.isEmpty()) {
        throw new DataFormatException("No pose data for " + poseEntry.getKey() + " on class " + classData.superClass);
      }
      String fullPoseName = poseEntry.getKey() + "_POSE";

      boolean needsAccessor = ModelResourceJavaGenerator.needsAccessorMethodForFieldName(classData, fullPoseName);

      if (needsAccessor) {
        sb.append("\n\t@FieldTemplate( visibility = org.lgna.project.annotations.Visibility.COMPLETELY_HIDDEN )");
      }

      Class poseType = JointedModelPose.class;
      Class poseBuilderType = ModelResourceJavaGenerator.getPoseBuilderTypeForSuperClass(classData.superClass);
      String poseTypeString = poseType.getName();
      sb.append("\n\tpublic static final " + poseTypeString + " " + fullPoseName + " = new " + poseTypeString + "( ");
      sb.append(JavaCodeUtilities.LINE_RETURN);
      int count = 0;
      for (Map.Entry<String, AffineMatrix4x4> poseDataEntry : poseData.entrySet()) {
        count++;
        UnitQuaternion quat = poseDataEntry.getValue().orientation().asUnitQuaternion();
        Point3 pos = poseDataEntry.getValue().translation();
        sb.append("\t\tnew JointIdTransformationPair( " + poseDataEntry.getKey() + ", new Orientation(" + quat.x() + ", " + quat.y() + ", " + quat.z() + ", " + quat.w() + "), new Position(" + pos.x() + ", " + pos.y() + ", " + pos.z() + ") )");
        if (count != poseData.size()) {
          sb.append(",");
        }
        sb.append(JavaCodeUtilities.LINE_RETURN);
      }
      sb.append("\t);" + JavaCodeUtilities.LINE_RETURN + JavaCodeUtilities.LINE_RETURN);
      if (needsAccessor) {
        String poseAccessorName = ModelResourceJavaGenerator.getAccessorMethodName(fullPoseName);
        sb.append("\tpublic " + poseType.getName() + " " + poseAccessorName + "(){" + JavaCodeUtilities.LINE_RETURN);
        sb.append("\t\treturn " + javaClassName + "." + fullPoseName + ";" + JavaCodeUtilities.LINE_RETURN);
        sb.append("\t}" + JavaCodeUtilities.LINE_RETURN);
      }
    }
  }

  static void appendArrayFields(
      StringBuilder sb,
      Map<String, List<String>> arrayEntries,
      List<String> mandatoryArrayNames,
      List<String> declaredArrays,
      List<Tuple2<String, String>> trimmedSkeleton,
      Set<String> hideElementArrays,
      ModelClassData classData,
      String javaClassName) {

    if (arrayEntries.isEmpty() && mandatoryArrayNames.isEmpty()) {
      return;
    }
    for (Map.Entry<String, List<String>> arrayEntry : arrayEntries.entrySet()) {
      if (mandatoryArrayNames.contains(arrayEntry.getKey())) {
        mandatoryArrayNames.remove(arrayEntry.getKey());
      }
    }
    for (String mandatoryArray : mandatoryArrayNames) {
      arrayEntries.put(mandatoryArray, new ArrayList<>());
    }
    for (Map.Entry<String, List<String>> arrayEntry : arrayEntries.entrySet()) {
      List<String> arrayElements = arrayEntry.getValue();
      String fullArrayName = arrayEntry.getKey() + "_ARRAY";

      if (declaredArrays.contains(fullArrayName) || declaredArrays.contains(arrayEntry.getKey())) {
        continue;
      }

      boolean needsAccessor = ModelResourceJavaGenerator.needsAccessorMethodForFieldName(classData, fullArrayName);

      if (needsAccessor) {
        sb.append("\n\t@FieldTemplate( visibility = org.lgna.project.annotations.Visibility.COMPLETELY_HIDDEN )");
      }

      if (hideElementArrays.contains(fullArrayName) || hideElementArrays.contains(arrayEntry.getKey())) {
        String firstEntry = arrayElements.getFirst();
        String parentString = "null";
        for (Tuple2<String, String> entry : trimmedSkeleton) {
          if (entry.getA().equals(firstEntry)) {
            parentString = entry.getB();
            break;
          }
        }
        sb.append("@FieldTemplate(visibility=Visibility.PRIME_TIME)" + JavaCodeUtilities.LINE_RETURN);
        sb.append("\tpublic static final org.lgna.story.resources.JointArrayId " + fullArrayName + " = new org.lgna.story.resources.JointArrayId( \"" + arrayEntry.getKey() + "\", " + parentString + ", " + javaClassName + ".class );" + JavaCodeUtilities.LINE_RETURN);
      } else {
        sb.append("\n\tpublic static final org.lgna.story.resources.JointId[] " + fullArrayName + " = { ");
        for (int i = 0; i < arrayElements.size(); i++) {
          sb.append(arrayElements.get(i));
          if (i < (arrayElements.size() - 1)) {
            sb.append(", ");
          }
        }
        sb.append(" };" + JavaCodeUtilities.LINE_RETURN);
      }
      if (needsAccessor) {
        String arrayAccessorName = ModelResourceJavaGenerator.getAccessorMethodName(fullArrayName);
        sb.append("\tpublic org.lgna.story.resources.JointId[] " + arrayAccessorName + "(){" + JavaCodeUtilities.LINE_RETURN);
        sb.append("\t\treturn " + javaClassName + "." + fullArrayName + ";" + JavaCodeUtilities.LINE_RETURN);
        sb.append("\t}" + JavaCodeUtilities.LINE_RETURN);
      }
    }
  }

  static void appendConstructorsAndMethods(
      StringBuilder sb,
      boolean addedRoots,
      ModelClassData classData,
      String javaClassName) {

    sb.append(JavaCodeUtilities.LINE_RETURN);
    sb.append("\tprivate final ImplementationAndVisualType resourceType;" + JavaCodeUtilities.LINE_RETURN);
    sb.append("\tprivate " + javaClassName + "() {" + JavaCodeUtilities.LINE_RETURN);
    sb.append("\t\tthis( ImplementationAndVisualType.ALICE );" + JavaCodeUtilities.LINE_RETURN);
    sb.append("\t}" + JavaCodeUtilities.LINE_RETURN + JavaCodeUtilities.LINE_RETURN);
    sb.append("\tprivate " + javaClassName + "( ImplementationAndVisualType resourceType ) {" + JavaCodeUtilities.LINE_RETURN);
    sb.append("\t\tthis.resourceType = resourceType;" + JavaCodeUtilities.LINE_RETURN);
    sb.append("\t}" + JavaCodeUtilities.LINE_RETURN + JavaCodeUtilities.LINE_RETURN);
    if (ModelResourceJavaGenerator.needsToDefineRootsMethod(classData.superClass)) {
      sb.append("\tpublic org.lgna.story.resources.JointId[] " + ModelResourceJavaGenerator.ROOT_IDS_METHOD_NAME + "(){" + JavaCodeUtilities.LINE_RETURN);
      if (addedRoots) {
        sb.append("\t\treturn " + javaClassName + "." + ModelResourceJavaGenerator.ROOT_IDS_FIELD_NAME + ";" + JavaCodeUtilities.LINE_RETURN);
      } else {
        Field rootsField = ModelResourceJavaGenerator.getJointRootsField(classData.superClass);
        if (rootsField != null) {
          sb.append("\t\treturn " + rootsField.getDeclaringClass().getCanonicalName() + "." + rootsField.getName() + ";" + JavaCodeUtilities.LINE_RETURN);
        } else {
          sb.append("\t\treturn new org.lgna.story.resources.JointId[0];" + JavaCodeUtilities.LINE_RETURN);
        }
      }
      sb.append("\t}" + JavaCodeUtilities.LINE_RETURN);
    }
    sb.append("\n\tpublic org.lgna.story.implementation.JointedModelImp.JointImplementationAndVisualDataFactory<org.lgna.story.resources.JointedModelResource> getImplementationAndVisualFactory() {" + JavaCodeUtilities.LINE_RETURN);
    sb.append("\t\treturn this.resourceType.getFactory( this );" + JavaCodeUtilities.LINE_RETURN);
    sb.append("\t}" + JavaCodeUtilities.LINE_RETURN);
    sb.append("\tpublic " + classData.implementationClass.getCanonicalName() + " createImplementation( " + classData.abstractionClass.getCanonicalName() + " abstraction ) {" + JavaCodeUtilities.LINE_RETURN);
    sb.append("\t\treturn new " + classData.implementationClass.getCanonicalName() + "( abstraction, this.resourceType.getFactory( this ) );" + JavaCodeUtilities.LINE_RETURN);
    sb.append("\t}" + JavaCodeUtilities.LINE_RETURN);
    sb.append("}" + JavaCodeUtilities.LINE_RETURN);
  }
}

package org.lgna.project.io;

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.UnitQuaternion;
import org.alice.tweedle.file.ModelManifest;
import org.lgna.project.annotations.FieldTemplate;
import org.lgna.project.annotations.Visibility;
import org.lgna.story.JointedModelPose;
import org.lgna.story.Pose;
import org.lgna.story.implementation.JointIdTransformationPair;
import org.lgna.story.resources.JointArrayId;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final class ModelManifestResourceData {
  private ModelManifestResourceData() {
  }

  static void addModelDataFromResource(ModelManifest manifest, JointedModelResource modelResource) {
    for (Field field : modelResource.getClass().getDeclaredFields()) {
      if (Pose.class.isAssignableFrom(field.getType()) && isFieldVisible(field)) {
        manifest.poses.add(createPose(field, modelResource));
      } else if (JointId.class.isAssignableFrom(field.getType())) {
        manifest.additionalJoints.add(createJoint(field, modelResource));
      } else if (JointId[].class.isAssignableFrom(field.getType())) {
        manifest.additionalJointArrays.add(createJointArray(field, modelResource));
      } else if (JointArrayId.class.isAssignableFrom(field.getType())) {
        manifest.additionalJointArrayIds.add(createJointArrayId(field, modelResource));
      }
    }
    addRootJoints(manifest, modelResource);
  }

  private static boolean isFieldVisible(Field field) {
    if (field.isAnnotationPresent(FieldTemplate.class)) {
      FieldTemplate propertyFieldTemplate = field.getAnnotation(FieldTemplate.class);
      return propertyFieldTemplate.visibility() != Visibility.COMPLETELY_HIDDEN;
    } else {
      return true;
    }
  }

  private static List<Float> getOrientationAsFloatList(OrthogonalMatrix3x3 orientation) {
    UnitQuaternion quaternion = orientation.asUnitQuaternion();
    List<Float> orientationList = new ArrayList<>(4);

    orientationList.add((float) quaternion.w());
    orientationList.add((float) quaternion.x());
    orientationList.add((float) quaternion.y());
    orientationList.add((float) quaternion.z());

    return orientationList;
  }

  private static ModelManifest.Pose createPose(Field poseField, JointedModelResource modelResource) {
    ModelManifest.Pose newPose = new ModelManifest.Pose();
    newPose.name = poseField.getName();
    JointedModelPose modelPose = (JointedModelPose) getRequiredFieldValue(poseField, modelResource);
    for (JointIdTransformationPair jointData : modelPose.getJointIdTransformationPairs()) {
      ModelManifest.JointTransform jointTransform = new ModelManifest.JointTransform();
      jointTransform.jointName = jointData.getJointId().toString();
      jointTransform.orientation = getOrientationAsFloatList(jointData.getTransformation().orientation());
      jointTransform.position = jointData.getTransformation().translation().asFloatList();
      newPose.transforms.add(jointTransform);
    }
    return newPose;
  }

  private static ModelManifest.Joint createJoint(Field jointField, JointedModelResource modelResource) {
    ModelManifest.Joint newJoint = new ModelManifest.Joint();
    newJoint.name = jointField.getName();
    if (jointField.isAnnotationPresent(FieldTemplate.class)) {
      FieldTemplate propertyFieldTemplate = jointField.getAnnotation(FieldTemplate.class);
      newJoint.visibility = propertyFieldTemplate.visibility();
    }
    JointId jointId = (JointId) getRequiredFieldValue(jointField, modelResource);
    JointId parent = jointId.getParent();
    newJoint.parent = parent != null ? parent.toString() : null;

    return newJoint;
  }

  private static ModelManifest.JointArray createJointArray(Field jointArrayField, JointedModelResource modelResource) {
    ModelManifest.JointArray newJointArray = new ModelManifest.JointArray();
    newJointArray.name = jointArrayField.getName();
    if (jointArrayField.isAnnotationPresent(FieldTemplate.class)) {
      FieldTemplate propertyFieldTemplate = jointArrayField.getAnnotation(FieldTemplate.class);
      newJointArray.visibility = propertyFieldTemplate.visibility();
    }
    JointId[] jointIds = (JointId[]) getRequiredFieldValue(jointArrayField, modelResource);
    for (JointId id : jointIds) {
      newJointArray.jointIds.add(id.toString());
    }

    return newJointArray;
  }

  private static ModelManifest.JointArrayId createJointArrayId(Field jointArrayIdField, JointedModelResource modelResource) {
    ModelManifest.JointArrayId newJointArrayId = new ModelManifest.JointArrayId();
    newJointArrayId.name = jointArrayIdField.getName();
    if (jointArrayIdField.isAnnotationPresent(FieldTemplate.class)) {
      FieldTemplate propertyFieldTemplate = jointArrayIdField.getAnnotation(FieldTemplate.class);
      newJointArrayId.visibility = propertyFieldTemplate.visibility();
    }
    JointArrayId jointArrayId = (JointArrayId) getRequiredFieldValue(jointArrayIdField, modelResource);
    newJointArrayId.patternId = jointArrayId.getElementNamePattern();
    newJointArrayId.rootJoint = jointArrayId.getRoot().toString();

    return newJointArrayId;
  }

  private static Object getRequiredFieldValue(Field field, JointedModelResource modelResource) {
    try {
      return field.get(modelResource);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(
          "Unable to read model resource field " + field.getName() + " from " + modelResource.getClass().getName(),
          e);
    }
  }

  private static void addRootJoints(ModelManifest manifest, JointedModelResource modelResource) {
    // This handles only BasicResource (Props) where getRootJointIds is defined
    // TODO Add JOINT_ID_ROOTS, add a common access pattern on JointedModelResource, or replace resources and revisit this code
    try {
      Method rootJointsMethod = modelResource.getClass().getMethod("getRootJointIds");
      JointId[] rootJointIds = (JointId[]) rootJointsMethod.invoke(modelResource);
      for (JointId jointId : rootJointIds) {
        manifest.rootJoints.add(jointId.toString());
      }
    } catch (NoSuchMethodException e) {
      // forbidden-pattern: intentional-log-and-continue
      Logger.info("No getRootJointIds found on model " + manifest.description.name);
    } catch (InvocationTargetException | IllegalAccessException e) {
      throw new IllegalStateException("Unable to read root joints for model " + manifest.description.name, e);
    }
  }
}

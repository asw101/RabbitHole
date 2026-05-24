package org.alice.stageide.gallerybrowser;

import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.tweedle.file.ModelManifest;
import org.alice.tweedle.file.StructureReference;
import org.lgna.project.ProjectVersion;
import org.lgna.project.annotations.Visibility;
import org.lgna.story.resources.JointId;

import java.util.ArrayList;
import java.util.List;

final class ImportGalleryResourceLogic {
  private ImportGalleryResourceLogic() {
    throw new AssertionError();
  }

  static void addMissingJoints(Joint skeletonRoot, List<ModelManifest.Joint> missingJoints) {
    int index = 0;
    while (!missingJoints.isEmpty()) {
      ModelManifest.Joint currentJoint = missingJoints.get(index);
      if (currentJoint.parent != null) {
        Joint parentJoint = skeletonRoot.getJoint(currentJoint.parent);
        if (parentJoint != null) {
          Joint newJoint = new Joint();
          newJoint.jointID.setValue(currentJoint.name);
          newJoint.setParent(parentJoint);
          missingJoints.remove(index);
        } else {
          index = (index + 1) % missingJoints.size();
        }
      } else {
        break;
      }
    }
  }

  static void addJointsToList(Joint sgJoint, List<ModelManifest.Joint> jointList) {
    if (sgJoint != null) {
      jointList.add(createJoint(sgJoint));
      for (Component c : sgJoint.getComponents()) {
        if (c instanceof Joint joint) {
          addJointsToList(joint, jointList);
        }
      }
    }
  }

  static List<ModelManifest.Joint> getModelJoints(Joint skeletonRoot) {
    List<ModelManifest.Joint> joints = new ArrayList<>();
    addJointsToList(skeletonRoot, joints);
    return joints;
  }

  static List<ModelManifest.Joint> getRootJoints(List<ModelManifest.Joint> jointList) {
    List<ModelManifest.Joint> rootJoints = new ArrayList<>();
    for (ModelManifest.Joint joint : jointList) {
      if (joint.parent == null) {
        rootJoints.add(joint);
      }
    }
    return rootJoints;
  }

  static ModelManifest.Joint createJoint(JointId jointId) {
    ModelManifest.Joint joint = new ModelManifest.Joint();
    joint.name = jointId.toString();
    if (jointId.getParent() != null) {
      joint.parent = jointId.getParent().toString();
    }
    joint.visibility = jointId.getVisibility();
    return joint;
  }

  static ModelManifest.Joint createJoint(Joint sgJoint) {
    ModelManifest.Joint joint = new ModelManifest.Joint();
    joint.name = sgJoint.jointID.getValue();
    if (sgJoint.getParent() instanceof Joint) {
      joint.parent = ((Joint) sgJoint.getParent()).jointID.getValue();
    } else {
      joint.parent = null;
    }
    joint.visibility = null;
    return joint;
  }

  static List<ModelManifest.Joint> getBaseJoints(List<JointId> baseJointIds) {
    List<ModelManifest.Joint> baseJoints = new ArrayList<>();
    for (JointId jointId : baseJointIds) {
      baseJoints.add(createJoint(jointId));
    }
    return baseJoints;
  }

  static List<ModelManifest.Joint> getExtraJoints(List<ModelManifest.Joint> modelJoints, List<ModelManifest.Joint> baseJoints, boolean setVisible) {
    List<ModelManifest.Joint> extraJoints = new ArrayList<>();
    for (ModelManifest.Joint joint : modelJoints) {
      if (!baseJoints.contains(joint)) {
        if (setVisible) {
          joint.visibility = Visibility.PRIME_TIME;
        }
        extraJoints.add(joint);
      }
    }
    return extraJoints;
  }

  static List<ModelManifest.Joint> getMissingJoints(List<ModelManifest.Joint> modelJoints, List<ModelManifest.Joint> requiredJoints) {
    List<ModelManifest.Joint> missingJoints = new ArrayList<>();
    for (ModelManifest.Joint joint : requiredJoints) {
      if (!modelJoints.contains(joint)) {
        missingJoints.add(joint);
      }
    }
    return missingJoints;
  }

  static ModelManifest createSimpleManifest(String modelName, String creatorName, String parentClassName, AxisAlignedBox boundingBox) {
    ModelManifest modelManifest = new ModelManifest();
    modelManifest.parentClass = parentClassName;
    modelManifest.description.name = modelName;
    modelManifest.provenance.creator = creatorName;
    modelManifest.provenance.aliceVersion = ProjectVersion.getCurrentVersionText();

    modelManifest.boundingBox = new ModelManifest.BoundingBox();
    modelManifest.boundingBox.max = boundingBox.maximum().asFloatList();
    modelManifest.boundingBox.min = boundingBox.minimum().asFloatList();

    StructureReference structureReference = new StructureReference();
    structureReference.name = modelName;
    modelManifest.resources.add(structureReference);

    ModelManifest.TextureSet textureSet = new ModelManifest.TextureSet();
    textureSet.name = modelName;
    modelManifest.textureSets.add(textureSet);

    ModelManifest.ModelVariant modelVariant = new ModelManifest.ModelVariant();
    modelVariant.name = modelName;
    modelVariant.structure = structureReference.name;
    modelVariant.textureSet = textureSet.name;
    modelManifest.models.add(modelVariant);

    return modelManifest;
  }
}

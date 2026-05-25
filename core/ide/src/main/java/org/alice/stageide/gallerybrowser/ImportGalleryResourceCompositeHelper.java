package org.alice.stageide.gallerybrowser;

import org.alice.tweedle.file.ModelManifest;

import java.util.List;
import java.util.stream.Collectors;

final class ImportGalleryResourceCompositeHelper {
  private ImportGalleryResourceCompositeHelper() {
    throw new AssertionError();
  }

  static boolean isSkeletonMissing(Object skeletonValue, List<ModelManifest.Joint> baseJoints) {
    return skeletonValue == null && !baseJoints.isEmpty();
  }

  static String buildMissingJointsMessage(String template, String aliceClassName, List<ModelManifest.Joint> missingJoints) {
    return template.replace("</class/>", aliceClassName)
        .replace("</joints/>", missingJoints.stream().map(ModelManifest.Joint::toString).collect(Collectors.joining(", ")));
  }

  static double calculateScaleChange(double newScale, double appliedScale) {
    return newScale / appliedScale;
  }
}

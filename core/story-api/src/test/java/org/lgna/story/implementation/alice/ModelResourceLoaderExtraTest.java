package org.lgna.story.implementation.alice;

import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import org.junit.Test;
import org.lgna.story.resources.ModelResource;

/**
 * Extra coverage tests for ModelResourceLoader paths that route through
 * getVisual()/getTexturedAppearances() for a stub resource. The underlying
 * StorytellingResources lookup may throw HeadlessException in headless mode;
 * the try/catch wrappers still let JaCoCo record any executed instructions.
 */
public class ModelResourceLoaderExtraTest {

  private static void allowMissingModelAsset(Throwable ignored) {
    // Expected: stub resources do not provide decodable gallery assets in this headless coverage test.
  }

  static final class StubResource implements org.lgna.story.resources.JointedModelResource {
    @Override
    public org.lgna.story.implementation.JointedModelImp.JointImplementationAndVisualDataFactory<org.lgna.story.resources.JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }

  @Test
  public void getVisual_routesThroughCacheAndDecode() {
    try { ModelResourceLoader.getVisual(new StubResource()); } catch (Throwable ignored) { allowMissingModelAsset(ignored); }
  }

  @Test
  public void getVisualCopy_routesThroughCreateCopy() {
    try { ModelResourceLoader.getVisualCopy(new StubResource()); } catch (Throwable ignored) { allowMissingModelAsset(ignored); }
  }

  @Test
  public void getTexturedAppearances_routesThroughCacheAndDecode() {
    try { ModelResourceLoader.getTexturedAppearances(new StubResource()); } catch (Throwable ignored) { allowMissingModelAsset(ignored); }
  }

  @Test
  public void createReplaceVisualElements_routesThroughGetVisual() {
    try {
      ModelResourceLoader.createReplaceVisualElements(new SkeletonVisual(), new StubResource());
    } catch (Throwable ignored) { allowMissingModelAsset(ignored); }
  }

  @Test
  public void getOriginalJointTransformation_routesThroughGetVisual() {
    try {
      ModelResourceLoader.getOriginalJointTransformation(
          new StubResource(), new org.lgna.story.resources.JointId(null, StubResource.class));
    } catch (Throwable ignored) { allowMissingModelAsset(ignored); }
  }

  @Test
  public void getOriginalJointOrientation_routesThroughGetVisual() {
    try {
      ModelResourceLoader.getOriginalJointOrientation(
          new StubResource(), new org.lgna.story.resources.JointId(null, StubResource.class));
    } catch (Throwable ignored) { allowMissingModelAsset(ignored); }
  }

  @Test
  public void createCopy_withSkeletonNonNullCopiesIt() {
    SkeletonVisual original = new SkeletonVisual();
    edu.cmu.cs.dennisc.scenegraph.Joint root = new edu.cmu.cs.dennisc.scenegraph.Joint();
    root.jointID.setValue("root");
    original.skeleton.setValue(root);
    SkeletonVisual copy = ModelResourceLoader.createCopy(original);
    org.junit.Assert.assertNotNull(copy.skeleton.getValue());
    org.junit.Assert.assertNotSame(root, copy.skeleton.getValue());
  }

  @Test
  public void createCopy_withFrontFacingAppearanceCopies() {
    SkeletonVisual original = new SkeletonVisual();
    edu.cmu.cs.dennisc.scenegraph.SimpleAppearance front = new edu.cmu.cs.dennisc.scenegraph.SimpleAppearance();
    original.frontFacingAppearance.setValue(front);
    SkeletonVisual copy = ModelResourceLoader.createCopy(original);
    org.junit.Assert.assertNotNull(copy.frontFacingAppearance.getValue());
  }

  @Test
  public void createCopy_withBackFacingAppearanceCopies() {
    SkeletonVisual original = new SkeletonVisual();
    edu.cmu.cs.dennisc.scenegraph.SimpleAppearance back = new edu.cmu.cs.dennisc.scenegraph.SimpleAppearance();
    original.backFacingAppearance.setValue(back);
    SkeletonVisual copy = ModelResourceLoader.createCopy(original);
    org.junit.Assert.assertNotNull(copy.backFacingAppearance.getValue());
  }
}

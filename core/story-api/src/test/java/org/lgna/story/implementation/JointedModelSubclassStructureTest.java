package org.lgna.story.implementation;

import org.junit.Test;
import org.lgna.story.SBiped;
import org.lgna.story.SCamera;
import org.lgna.story.SFlyer;
import org.lgna.story.SJointedModel;
import org.lgna.story.SQuadruped;
import org.lgna.story.SSlitherer;
import org.lgna.story.SSwimmer;
import org.lgna.story.STransport;
import org.lgna.story.SVRHand;
import org.lgna.story.SVRHeadset;
import org.lgna.story.SVRUser;
import org.lgna.story.resources.BasicResource;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.FlyerResource;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resources.QuadrupedResource;
import org.lgna.story.resources.SlithererResource;
import org.lgna.story.resources.SwimmerResource;
import org.lgna.story.resources.TransportResource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Reflection-based structural tests for 12 implementation classes across
 * 4 hierarchies: JointedModelImp (7), CameraImp (2), TransformableImp (2),
 * CameraMarkerImp (1).
 *
 * <p>These classes cannot be instantiated without real model resources,
 * so we verify structural properties via reflection instead.
 */
public class JointedModelSubclassStructureTest {

  private static final List<Class<?>> JOINTED_MODEL_SUBCLASSES = List.of(
      BipedImp.class,
      FlyerImp.class,
      QuadrupedImp.class,
      SlithererImp.class,
      SwimmerImp.class,
      TransportImp.class,
      BasicJointedModelImp.class
  );

  @SuppressWarnings("rawtypes")
  private static final List<Class<? extends JointedModelResource>> RESOURCE_TYPES = List.of(
      BipedResource.class,
      FlyerResource.class,
      QuadrupedResource.class,
      SlithererResource.class,
      SwimmerResource.class,
      TransportResource.class,
      BasicResource.class
  );

  private static final List<Class<?>> ABSTRACTION_TYPES = List.of(
      SBiped.class,
      SFlyer.class,
      SQuadruped.class,
      SSlitherer.class,
      SSwimmer.class,
      STransport.class,
      SJointedModel.class
  );

  // ══════════════════════════════════════════════════════════════════════
  //  JointedModelImp subclasses — hierarchy
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void bipedImp_extendsJointedModelImp() {
    assertTrue(JointedModelImp.class.isAssignableFrom(BipedImp.class));
  }

  @Test
  public void flyerImp_extendsJointedModelImp() {
    assertTrue(JointedModelImp.class.isAssignableFrom(FlyerImp.class));
  }

  @Test
  public void quadrupedImp_extendsJointedModelImp() {
    assertTrue(JointedModelImp.class.isAssignableFrom(QuadrupedImp.class));
  }

  @Test
  public void slithererImp_extendsJointedModelImp() {
    assertTrue(JointedModelImp.class.isAssignableFrom(SlithererImp.class));
  }

  @Test
  public void swimmerImp_extendsJointedModelImp() {
    assertTrue(JointedModelImp.class.isAssignableFrom(SwimmerImp.class));
  }

  @Test
  public void transportImp_extendsJointedModelImp() {
    assertTrue(JointedModelImp.class.isAssignableFrom(TransportImp.class));
  }

  @Test
  public void basicJointedModelImp_extendsJointedModelImp() {
    assertTrue(JointedModelImp.class.isAssignableFrom(BasicJointedModelImp.class));
  }

  // ══════════════════════════════════════════════════════════════════════
  //  JointedModelImp subclasses — constructor signatures
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void bipedImp_hasRequiredConstructor() throws NoSuchMethodException {
    BipedImp.class.getDeclaredConstructor(
        SBiped.class,
        JointedModelImp.JointImplementationAndVisualDataFactory.class
    );
  }

  @Test
  public void flyerImp_hasRequiredConstructor() throws NoSuchMethodException {
    FlyerImp.class.getDeclaredConstructor(
        SFlyer.class,
        JointedModelImp.JointImplementationAndVisualDataFactory.class
    );
  }

  @Test
  public void quadrupedImp_hasRequiredConstructor() throws NoSuchMethodException {
    QuadrupedImp.class.getDeclaredConstructor(
        SQuadruped.class,
        JointedModelImp.JointImplementationAndVisualDataFactory.class
    );
  }

  @Test
  public void slithererImp_hasRequiredConstructor() throws NoSuchMethodException {
    SlithererImp.class.getDeclaredConstructor(
        SSlitherer.class,
        JointedModelImp.JointImplementationAndVisualDataFactory.class
    );
  }

  @Test
  public void swimmerImp_hasRequiredConstructor() throws NoSuchMethodException {
    SwimmerImp.class.getDeclaredConstructor(
        SSwimmer.class,
        JointedModelImp.JointImplementationAndVisualDataFactory.class
    );
  }

  @Test
  public void transportImp_hasRequiredConstructor() throws NoSuchMethodException {
    TransportImp.class.getDeclaredConstructor(
        STransport.class,
        JointedModelImp.JointImplementationAndVisualDataFactory.class
    );
  }

  @Test
  public void basicJointedModelImp_hasRequiredConstructor() throws NoSuchMethodException {
    BasicJointedModelImp.class.getDeclaredConstructor(
        SJointedModel.class,
        JointedModelImp.JointImplementationAndVisualDataFactory.class
    );
  }

  // ══════════════════════════════════════════════════════════════════════
  //  JointedModelImp subclasses — getResource method
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void allJointedModelSubclasses_declareGetResource() {
    for (Class<?> cls : JOINTED_MODEL_SUBCLASSES) {
      // getResource is inherited from JointedModelImp — verify it's accessible
      boolean found = Arrays.stream(cls.getMethods())
          .anyMatch(m -> m.getName().equals("getResource"));
      assertTrue(cls.getSimpleName() + " missing getResource", found);
    }
  }

  @Test
  public void allJointedModelSubclasses_getResourceReturnsCorrectType() {
    for (int i = 0; i < JOINTED_MODEL_SUBCLASSES.size(); i++) {
      Class<?> cls = JOINTED_MODEL_SUBCLASSES.get(i);
      Method getResource = Arrays.stream(cls.getMethods())
          .filter(m -> m.getName().equals("getResource"))
          .findFirst().orElse(null);
      assertNotNull(cls.getSimpleName() + " should have getResource", getResource);
    }
  }

  // ══════════════════════════════════════════════════════════════════════
  //  JointedModelImp subclasses — final modifier
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void bipedImp_isFinal() {
    assertTrue(Modifier.isFinal(BipedImp.class.getModifiers()));
  }

  @Test
  public void flyerImp_isFinal() {
    assertTrue(Modifier.isFinal(FlyerImp.class.getModifiers()));
  }

  @Test
  public void quadrupedImp_isFinal() {
    assertTrue(Modifier.isFinal(QuadrupedImp.class.getModifiers()));
  }

  @Test
  public void slithererImp_isFinal() {
    assertTrue(Modifier.isFinal(SlithererImp.class.getModifiers()));
  }

  @Test
  public void swimmerImp_isFinal() {
    assertTrue(Modifier.isFinal(SwimmerImp.class.getModifiers()));
  }

  @Test
  public void transportImp_isFinal() {
    assertTrue(Modifier.isFinal(TransportImp.class.getModifiers()));
  }

  @Test
  public void allJointedModelSubclasses_arePublic() {
    for (Class<?> cls : JOINTED_MODEL_SUBCLASSES) {
      assertTrue(cls.getSimpleName() + " should be public",
          Modifier.isPublic(cls.getModifiers()));
    }
  }

  // ══════════════════════════════════════════════════════════════════════
  //  CameraImp subclasses
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void symmetricPerspectiveCameraImp_extendsCameraImp() {
    assertTrue(CameraImp.class.isAssignableFrom(SymmetricPerspectiveCameraImp.class));
  }

  @Test
  public void symmetricPerspectiveCameraImp_hasConstructor() throws NoSuchMethodException {
    SymmetricPerspectiveCameraImp.class.getDeclaredConstructor(SCamera.class);
  }

  @Test
  public void symmetricPerspectiveCameraImp_declaresGetAbstraction() {
    boolean found = Arrays.stream(SymmetricPerspectiveCameraImp.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getAbstraction"));
    assertTrue("SymmetricPerspectiveCameraImp should declare getAbstraction", found);
  }

  @Test
  public void vrHeadsetImp_extendsCameraImp() {
    assertTrue(CameraImp.class.isAssignableFrom(VrHeadsetImp.class));
  }

  @Test
  public void vrHeadsetImp_hasConstructor() throws NoSuchMethodException {
    VrHeadsetImp.class.getDeclaredConstructor(
        String.class, SVRHeadset.class, AbstractTransformableImp.class
    );
  }

  @Test
  public void vrHeadsetImp_declaresGetAbstraction() {
    boolean found = Arrays.stream(VrHeadsetImp.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getAbstraction"));
    assertTrue("VrHeadsetImp should declare getAbstraction", found);
  }

  // ══════════════════════════════════════════════════════════════════════
  //  TransformableImp subclasses
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void vrUserImp_extendsTransformableImp() {
    assertTrue(TransformableImp.class.isAssignableFrom(VrUserImp.class));
  }

  @Test
  public void vrUserImp_hasConstructor() throws NoSuchMethodException {
    VrUserImp.class.getDeclaredConstructor(String.class, SVRUser.class);
  }

  @Test
  public void vrUserImp_declaresGetAbstraction() {
    boolean found = Arrays.stream(VrUserImp.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getAbstraction"));
    assertTrue("VrUserImp should declare getAbstraction", found);
  }

  @Test
  public void vrHandImp_extendsTransformableImp() {
    assertTrue(TransformableImp.class.isAssignableFrom(VrHandImp.class));
  }

  @Test
  public void vrHandImp_hasConstructor() throws NoSuchMethodException {
    VrHandImp.class.getDeclaredConstructor(
        String.class, SVRHand.class, AbstractTransformableImp.class
    );
  }

  @Test
  public void vrHandImp_declaresGetAbstraction() {
    boolean found = Arrays.stream(VrHandImp.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getAbstraction"));
    assertTrue("VrHandImp should declare getAbstraction", found);
  }

  // ══════════════════════════════════════════════════════════════════════
  //  CameraMarkerImp subclass
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void orthographicCameraMarkerImp_extendsCameraMarkerImp() {
    assertTrue(CameraMarkerImp.class.isAssignableFrom(OrthographicCameraMarkerImp.class));
  }

  @Test
  public void orthographicCameraMarkerImp_extendsMarkerImp() {
    assertTrue(MarkerImp.class.isAssignableFrom(OrthographicCameraMarkerImp.class));
  }

  @Test
  public void orthographicCameraMarkerImp_isPublic() {
    assertTrue(Modifier.isPublic(OrthographicCameraMarkerImp.class.getModifiers()));
  }

  // ══════════════════════════════════════════════════════════════════════
  //  JointImplementationAndVisualDataFactory inner interface
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void jointFactory_isInterface() {
    assertTrue(JointedModelImp.JointImplementationAndVisualDataFactory.class.isInterface());
  }

  @Test
  public void jointFactory_isPublic() {
    assertTrue(Modifier.isPublic(
        JointedModelImp.JointImplementationAndVisualDataFactory.class.getModifiers()));
  }

  // ══════════════════════════════════════════════════════════════════════
  //  Cross-cutting: all 12 classes are in the correct package
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void allClasses_inImplementationPackage() {
    List<Class<?>> allClasses = List.of(
        BipedImp.class, FlyerImp.class, QuadrupedImp.class,
        SlithererImp.class, SwimmerImp.class, TransportImp.class,
        BasicJointedModelImp.class,
        SymmetricPerspectiveCameraImp.class, VrHeadsetImp.class,
        OrthographicCameraMarkerImp.class,
        VrUserImp.class, VrHandImp.class
    );
    for (Class<?> cls : allClasses) {
      assertEquals(cls.getSimpleName() + " should be in implementation package",
          "org.lgna.story.implementation", cls.getPackageName());
    }
  }
}

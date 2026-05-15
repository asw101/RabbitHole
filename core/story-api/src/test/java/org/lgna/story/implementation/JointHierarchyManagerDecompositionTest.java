package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.UnitQuaternion;
import org.junit.Before;
import org.junit.Test;
import org.lgna.ik.core.solver.Bone;
import org.lgna.story.implementation.JointedModelImp.JointImplementationAndVisualDataFactory;
import org.lgna.story.implementation.JointedModelImp.VisualData;
import org.lgna.story.resources.JointArrayId;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * TDD tests for the JointHierarchyManager internal decomposition.
 *
 * <p>Specifies contracts for two classes to be extracted:
 * <ul>
 *   <li>{@code JointImpWrapper} — inner class extracted to package-private top-level;
 *       constructor-injected {@code JointedModelResourceBinder<?>} replaces
 *       {@code JointHierarchyManager.this.resourceBinder} outer-class access</li>
 *   <li>{@code IkChainHelper} — static IK chain computation (AddOp enum + 3 methods)
 *       extracted from JointHierarchyManager; parameterized with
 *       {@code Function<JointId, JointImp>} for joint lookup</li>
 * </ul>
 *
 * <p><b>RED PHASE:</b> Tests referencing {@code JointImpWrapper} as a top-level
 * class and {@code IkChainHelper} will not compile until extraction is complete.
 * Reflection-based structural tests compile but fail at runtime with
 * {@code ClassNotFoundException}.</p>
 *
 * @see JointHierarchyManager
 */
public class JointHierarchyManagerDecompositionTest {

  // ════════════════════════════════════════════════════════════════════════════
  //  Test resource hierarchy
  //
  //  ROOT
  //  └── SPINE
  //      ├── HEAD
  //      └── LEFT_ARM
  //  TAIL_ROOT (disconnected tree)
  // ════════════════════════════════════════════════════════════════════════════

  public static class IkTestResource implements JointedModelResource {
    public static final JointId ROOT = new JointId(null, IkTestResource.class);
    public static final JointId SPINE = new JointId(ROOT, IkTestResource.class);
    public static final JointId HEAD = new JointId(SPINE, IkTestResource.class);
    public static final JointId LEFT_ARM = new JointId(SPINE, IkTestResource.class);
    public static final JointId TAIL_ROOT = new JointId(null, IkTestResource.class);

    @Override
    public JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }

  /** Minimal factory for building test hierarchies. */
  static class MinimalStubFactory implements JointImplementationAndVisualDataFactory<JointedModelResource> {
    private JointedModelResource resource;
    private final boolean sims;

    MinimalStubFactory(JointedModelResource resource, boolean sims) {
      this.resource = resource;
      this.sims = sims;
    }

    @Override
    public JointedModelResource getResource() { return resource; }

    @Override
    public JointImp createJointImplementation(JointedModelImp<?, JointedModelResource> impl, JointId jointId) {
      Joint sgJoint = new Joint();
      sgJoint.jointID.setValue(jointId.toString());
      return new org.lgna.story.implementation.alice.JointImplementation(impl, jointId, sgJoint) {
        @Override
        protected void copyOnto(JointImp newJoint) {
          if (getJointedModelImplementation() != null) {
            super.copyOnto(newJoint);
          }
        }
      };
    }

    @Override
    public boolean hasJointImplementation(JointedModelImp<?, JointedModelResource> impl, JointId jointId) {
      return true;
    }

    @Override
    public JointId[] getJointArrayIds(JointedModelImp<?, JointedModelResource> impl, JointArrayId arrayId) {
      return new JointId[0];
    }

    @Override
    public VisualData<JointedModelResource> createVisualData() {
      return new VisualData<>() {
        final Visual v = new Visual();
        final SimpleAppearance a = new SimpleAppearance();
        Composite parent;

        @Override public Visual[] getSgVisuals() { return new Visual[]{v}; }
        @Override public SkeletonVisual getSgVisualForExporting(JointedModelResource r) { return null; }
        @Override public SimpleAppearance[] getSgAppearances() { return new SimpleAppearance[]{a}; }
        @Override public void setSGParent(Composite p) { parent = p; }
        @Override public Composite getSGParent() { return parent; }
      };
    }

    @Override
    public UnitQuaternion getOriginalJointOrientation(JointId id) { return UnitQuaternion.IDENTITY; }

    @Override
    public AffineMatrix4x4 getOriginalJointTransformation(JointId id) { return AffineMatrix4x4.IDENTITY; }

    @Override
    public boolean isSims() { return sims; }
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  Fixtures
  // ════════════════════════════════════════════════════════════════════════════

  private JointedModelResourceBinder<JointedModelResource> binder;
  private JointHierarchyManager<JointedModelResource> hierarchy;

  // Raw (non-wrapper) joints with proper vehicle chains for IkChainHelper tests
  private Map<JointId, JointImp> rawJoints;
  private Function<JointId, JointImp> rawLookup;

  /** Creates a JointImplementation with null owner and a no-op copyOnto. */
  private static JointImp createTestJoint(JointId jointId) {
    Joint sgJoint = new Joint();
    sgJoint.jointID.setValue(jointId.toString());
    return new org.lgna.story.implementation.alice.JointImplementation(null, jointId, sgJoint) {
      @Override
      protected void copyOnto(JointImp newJoint) {
        // No-op: null owner would cause NPE in super.copyOnto
      }
    };
  }

  @Before
  public void setUp() {
    // Integration fixtures: ResourceBinder + JointHierarchyManager with wrappers
    IkTestResource resource = new IkTestResource();
    MinimalStubFactory factory = new MinimalStubFactory(resource, false);
    binder = new JointedModelResourceBinder<>(factory);
    hierarchy = new JointHierarchyManager<>(binder);
    hierarchy.buildJointHierarchy(null);

    // Unit-test fixtures: raw joints with vehicle chain (isDescendantOf works)
    rawJoints = new HashMap<>();
    JointImp rootImp = createTestJoint(IkTestResource.ROOT);
    JointImp spineImp = createTestJoint(IkTestResource.SPINE);
    JointImp headImp = createTestJoint(IkTestResource.HEAD);
    JointImp leftArmImp = createTestJoint(IkTestResource.LEFT_ARM);
    JointImp tailRootImp = createTestJoint(IkTestResource.TAIL_ROOT);

    // Parent-child relationships (list-based)
    spineImp.setJointParent(rootImp);
    headImp.setJointParent(spineImp);
    leftArmImp.setJointParent(spineImp);

    // Vehicle chain (scenegraph-based, needed for isDescendantOf)
    spineImp.setVehicle(rootImp);
    headImp.setVehicle(spineImp);
    leftArmImp.setVehicle(spineImp);

    rawJoints.put(IkTestResource.ROOT, rootImp);
    rawJoints.put(IkTestResource.SPINE, spineImp);
    rawJoints.put(IkTestResource.HEAD, headImp);
    rawJoints.put(IkTestResource.LEFT_ARM, leftArmImp);
    rawJoints.put(IkTestResource.TAIL_ROOT, tailRootImp);

    rawLookup = rawJoints::get;
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  IkChainHelper — structural contract (reflection-based, compiles now)
  // ════════════════════════════════════════════════════════════════════════════

  @Test
  public void ikChainHelper_classExists() {
    try {
      Class<?> clazz = Class.forName("org.lgna.story.implementation.IkChainHelper");
      assertNotNull("IkChainHelper class should exist", clazz);
    } catch (ClassNotFoundException e) {
      fail("IkChainHelper class not found — extraction not complete");
    }
  }

  @Test
  public void ikChainHelper_isPackagePrivate() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.story.implementation.IkChainHelper");
    int mod = clazz.getModifiers();
    assertFalse("Should not be public", java.lang.reflect.Modifier.isPublic(mod));
    assertFalse("Should not be private", java.lang.reflect.Modifier.isPrivate(mod));
    assertFalse("Should not be protected", java.lang.reflect.Modifier.isProtected(mod));
  }

  @Test
  public void ikChainHelper_hasStaticGetInclusiveMethod() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.story.implementation.IkChainHelper");
    var method = clazz.getDeclaredMethod("getInclusiveListOfJointsBetween",
        JointImp.class, JointImp.class, List.class, EntityImp.class, Function.class);
    assertTrue("Should be static",
        java.lang.reflect.Modifier.isStatic(method.getModifiers()));
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  IkChainHelper — behavioral contract
  //  (won't compile until IkChainHelper is extracted as a top-level class)
  // ════════════════════════════════════════════════════════════════════════════

  @Test
  public void ikChainHelper_sameJoint_returnsSingleElement() {
    JointImp head = rawJoints.get(IkTestResource.HEAD);
    List<Bone.Direction> directions = new ArrayList<>();

    List<JointImp> result = IkChainHelper.getInclusiveListOfJointsBetween(
        head, head, directions, null, rawLookup);

    assertEquals("Same joint → single element", 1, result.size());
    assertSame(head, result.get(0));
    assertEquals(1, directions.size());
    assertEquals(Bone.Direction.DOWNSTREAM, directions.get(0));
  }

  @Test
  public void ikChainHelper_descendantToAncestor_includesBothEndpoints() {
    JointImp head = rawJoints.get(IkTestResource.HEAD);
    JointImp root = rawJoints.get(IkTestResource.ROOT);
    List<Bone.Direction> directions = new ArrayList<>();

    List<JointImp> result = IkChainHelper.getInclusiveListOfJointsBetween(
        head, root, directions, null, rawLookup);

    // HEAD→ROOT inclusive: HEAD, SPINE, ROOT (3 joints via PREPEND order)
    assertEquals("HEAD→ROOT should have 3 joints", 3, result.size());
    List<JointId> ids = result.stream().map(JointImp::getJointId).collect(Collectors.toList());
    assertTrue("Should contain HEAD", ids.contains(IkTestResource.HEAD));
    assertTrue("Should contain SPINE", ids.contains(IkTestResource.SPINE));
    assertTrue("Should contain ROOT", ids.contains(IkTestResource.ROOT));
    assertEquals("Directions count matches joints", 3, directions.size());
  }

  @Test
  public void ikChainHelper_ancestorToDescendant_ordersRootFirst() {
    JointImp root = rawJoints.get(IkTestResource.ROOT);
    JointImp head = rawJoints.get(IkTestResource.HEAD);
    List<Bone.Direction> directions = new ArrayList<>();

    List<JointImp> result = IkChainHelper.getInclusiveListOfJointsBetween(
        root, head, directions, null, rawLookup);

    // ROOT→HEAD inclusive: ROOT, SPINE, HEAD (3 joints via APPEND order)
    assertEquals("ROOT→HEAD should have 3 joints", 3, result.size());
    assertSame("First should be ROOT", root, result.get(0));
    assertSame("Last should be HEAD", head, result.get(2));
  }

  @Test
  public void ikChainHelper_adjacentJoints_includesBoth() {
    JointImp head = rawJoints.get(IkTestResource.HEAD);
    JointImp spine = rawJoints.get(IkTestResource.SPINE);
    List<Bone.Direction> directions = new ArrayList<>();

    List<JointImp> result = IkChainHelper.getInclusiveListOfJointsBetween(
        head, spine, directions, null, rawLookup);

    assertEquals("Adjacent HEAD→SPINE should have 2 joints", 2, result.size());
    assertEquals("Directions count matches", 2, directions.size());
  }

  @Test
  public void ikChainHelper_siblings_excludesCommonAncestor() {
    JointImp head = rawJoints.get(IkTestResource.HEAD);
    JointImp leftArm = rawJoints.get(IkTestResource.LEFT_ARM);
    List<Bone.Direction> directions = new ArrayList<>();

    List<JointImp> result = IkChainHelper.getInclusiveListOfJointsBetween(
        head, leftArm, directions, null, rawLookup);

    // HEAD and LEFT_ARM are siblings under SPINE; common ancestor excluded
    List<JointId> ids = result.stream().map(JointImp::getJointId).collect(Collectors.toList());
    assertTrue("Should contain HEAD", ids.contains(IkTestResource.HEAD));
    assertTrue("Should contain LEFT_ARM", ids.contains(IkTestResource.LEFT_ARM));
    assertFalse("Should NOT contain common ancestor SPINE",
        ids.contains(IkTestResource.SPINE));
    assertEquals("Directions count matches joints", result.size(), directions.size());
  }

  @Test
  public void ikChainHelper_siblings_hasMixedDirections() {
    JointImp head = rawJoints.get(IkTestResource.HEAD);
    JointImp leftArm = rawJoints.get(IkTestResource.LEFT_ARM);
    List<Bone.Direction> directions = new ArrayList<>();

    IkChainHelper.getInclusiveListOfJointsBetween(
        head, leftArm, directions, null, rawLookup);

    // One leg UPSTREAM, other leg DOWNSTREAM
    assertTrue("Should contain UPSTREAM", directions.contains(Bone.Direction.UPSTREAM));
    assertTrue("Should contain DOWNSTREAM", directions.contains(Bone.Direction.DOWNSTREAM));
  }

  @Test(expected = RuntimeException.class)
  public void ikChainHelper_disconnectedJoints_throwsRuntimeException() {
    JointImp head = rawJoints.get(IkTestResource.HEAD);
    JointImp tailRoot = rawJoints.get(IkTestResource.TAIL_ROOT);
    List<Bone.Direction> directions = new ArrayList<>();

    // HEAD and TAIL_ROOT are in disconnected trees — no common ancestor
    IkChainHelper.getInclusiveListOfJointsBetween(
        head, tailRoot, directions, null, rawLookup);
  }

  @Test
  public void ikChainHelper_usesProvidedLookupFunction() {
    JointImp spine = rawJoints.get(IkTestResource.SPINE);
    JointImp head = rawJoints.get(IkTestResource.HEAD);
    List<Bone.Direction> directions = new ArrayList<>();
    List<JointId> lookedUp = new ArrayList<>();
    Function<JointId, JointImp> tracking = id -> {
      lookedUp.add(id);
      return rawJoints.get(id);
    };

    IkChainHelper.getInclusiveListOfJointsBetween(
        spine, head, directions, null, tracking);

    assertFalse("Lookup function should be called at least once", lookedUp.isEmpty());
  }

  @Test
  public void ikChainHelper_directionsAlwaysMatchResultSize() {
    JointImp root = rawJoints.get(IkTestResource.ROOT);
    JointImp head = rawJoints.get(IkTestResource.HEAD);
    List<Bone.Direction> directions = new ArrayList<>();

    List<JointImp> result = IkChainHelper.getInclusiveListOfJointsBetween(
        root, head, directions, null, rawLookup);

    assertEquals("Directions list must match result list size",
        result.size(), directions.size());
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  JointImpWrapper — structural contract (reflection-based, compiles now)
  // ════════════════════════════════════════════════════════════════════════════

  @Test
  public void jointImpWrapper_classExistsAsTopLevel() {
    try {
      Class<?> clazz = Class.forName("org.lgna.story.implementation.JointImpWrapper");
      assertNotNull(clazz);
      assertNull("Should not be an inner class (no enclosing class)",
          clazz.getEnclosingClass());
    } catch (ClassNotFoundException e) {
      fail("JointImpWrapper not found as top-level class — extraction not complete");
    }
  }

  @Test
  public void jointImpWrapper_constructorAcceptsResourceBinder() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.story.implementation.JointImpWrapper");
    // 3-arg constructor: (JointedModelImp, JointImp, JointedModelResourceBinder)
    clazz.getDeclaredConstructor(
        JointedModelImp.class, JointImp.class, JointedModelResourceBinder.class);
    // No exception means the constructor exists with the expected signature
  }

  @Test
  public void jointImpWrapper_isPackagePrivate() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.story.implementation.JointImpWrapper");
    int mod = clazz.getModifiers();
    assertFalse("Should not be public", java.lang.reflect.Modifier.isPublic(mod));
    assertFalse("Should not be private", java.lang.reflect.Modifier.isPrivate(mod));
    assertFalse("Should not be protected", java.lang.reflect.Modifier.isProtected(mod));
  }

  @Test
  public void jointImpWrapper_extendsJointImp() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.story.implementation.JointImpWrapper");
    assertTrue("Should extend JointImp", JointImp.class.isAssignableFrom(clazz));
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  JointImpWrapper — behavioral contract
  //  (won't compile until JointImpWrapper is extracted as a top-level class)
  // ════════════════════════════════════════════════════════════════════════════

  @Test
  public void jointImpWrapper_delegatesGetJointId() {
    JointImp internal = createTestJoint(IkTestResource.HEAD);

    JointImpWrapper wrapper = new JointImpWrapper(null, internal, binder);

    assertSame("getJointId should delegate to internal joint",
        IkTestResource.HEAD, wrapper.getJointId());
  }

  @Test
  public void jointImpWrapper_delegatesGetName() {
    JointImp internal = createTestJoint(IkTestResource.SPINE);

    JointImpWrapper wrapper = new JointImpWrapper(null, internal, binder);

    assertEquals("getName should return internal joint's ID string",
        IkTestResource.SPINE.toString(), wrapper.getName());
  }

  @Test
  public void jointImpWrapper_delegatesGetSgComposite() {
    Joint sgJoint = new Joint();
    sgJoint.jointID.setValue("TEST_JOINT");
    JointImp internal = new org.lgna.story.implementation.alice.JointImplementation(
        null, IkTestResource.ROOT, sgJoint) {
      @Override
      protected void copyOnto(JointImp newJoint) {}
    };

    JointImpWrapper wrapper = new JointImpWrapper(null, internal, binder);

    assertSame("getSgComposite should delegate to internal joint",
        sgJoint, wrapper.getSgComposite());
  }

  @Test
  public void jointImpWrapper_replaceWithJoint_swapsInternal() {
    Joint sg1 = new Joint();
    sg1.jointID.setValue("JOINT_V1");
    JointImp j1 = new org.lgna.story.implementation.alice.JointImplementation(
        null, IkTestResource.HEAD, sg1) {
      @Override
      protected void copyOnto(JointImp newJoint) {}
    };
    Joint sg2 = new Joint();
    sg2.jointID.setValue("JOINT_V2");
    JointImp j2 = new org.lgna.story.implementation.alice.JointImplementation(
        null, IkTestResource.HEAD, sg2) {
      @Override
      protected void copyOnto(JointImp newJoint) {}
    };

    JointImpWrapper wrapper = new JointImpWrapper(null, j1, binder);
    assertSame("Before replace: should delegate to first joint", sg1, wrapper.getSgComposite());

    wrapper.replaceWithJoint(j2);
    assertSame("After replace: should delegate to second joint", sg2, wrapper.getSgComposite());
  }

  @Test
  public void jointImpWrapper_parentChildRelationships() {
    JointImp internalRoot = createTestJoint(IkTestResource.ROOT);
    JointImp internalSpine = createTestJoint(IkTestResource.SPINE);

    JointImpWrapper rootW = new JointImpWrapper(null, internalRoot, binder);
    JointImpWrapper spineW = new JointImpWrapper(null, internalSpine, binder);

    spineW.setJointParent(rootW);

    assertSame("SPINE parent should be ROOT wrapper", rootW, spineW.getJointParent());
    assertTrue("ROOT children should contain SPINE wrapper",
        rootW.getJointChildren().contains(spineW));
  }

  @Test
  public void jointImpWrapper_initialChildrenListEmpty() {
    JointImp internal = createTestJoint(IkTestResource.ROOT);

    JointImpWrapper wrapper = new JointImpWrapper(null, internal, binder);

    assertNotNull("Children list should not be null", wrapper.getJointChildren());
    assertTrue("Children list should start empty", wrapper.getJointChildren().isEmpty());
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  JointHierarchyManager — post-extraction verification
  // ════════════════════════════════════════════════════════════════════════════

  @Test
  public void hierarchyManager_lineCount_underTarget() {
    // JointHierarchyManager should be under 500 lines after extracting
    // JointImpWrapper and IkChainHelper
    try {
      java.io.File sourceFile = new java.io.File(
          "src/main/java/org/lgna/story/implementation/JointHierarchyManager.java");
      if (sourceFile.exists()) {
        long lineCount = java.nio.file.Files.lines(sourceFile.toPath()).count();
        assertTrue(
            "JointHierarchyManager.java should be under 500 lines after extraction, but has "
                + lineCount,
            lineCount < 500);
      }
    } catch (Exception e) {
      // Source access may vary by test runner working directory
    }
  }

  @Test
  public void hierarchyManager_ikChain_sameJointShortCircuit() {
    // Regression: same-joint case should return single element after extraction
    JointImp spine = hierarchy.getJointImplementation(IkTestResource.SPINE);
    List<Bone.Direction> directions = new ArrayList<>();

    List<JointImp> result = hierarchy.getInclusiveListOfJointsBetween(
        spine, spine, directions, null);

    assertEquals("Same joint → single element", 1, result.size());
    assertSame(spine, result.get(0));
    assertEquals(Bone.Direction.DOWNSTREAM, directions.get(0));
  }

  @Test
  public void hierarchyManager_ikChain_returnsNonEmptyForRelatedJoints() {
    // Regression: IK chain between parent and child should return joints
    JointImp head = hierarchy.getJointImplementation(IkTestResource.HEAD);
    JointImp spine = hierarchy.getJointImplementation(IkTestResource.SPINE);
    List<Bone.Direction> directions = new ArrayList<>();

    List<JointImp> result = hierarchy.getInclusiveListOfJointsBetween(
        head, spine, directions, null);

    assertNotNull("Result should not be null", result);
    assertFalse("Should return at least one joint", result.isEmpty());
    assertEquals("Directions count should match", result.size(), directions.size());
  }

  @Test
  public void hierarchyManager_ikChain_siblingsWork() {
    // Regression: IK chain between siblings should not throw
    JointImp head = hierarchy.getJointImplementation(IkTestResource.HEAD);
    JointImp leftArm = hierarchy.getJointImplementation(IkTestResource.LEFT_ARM);
    List<Bone.Direction> directions = new ArrayList<>();

    List<JointImp> result = hierarchy.getInclusiveListOfJointsBetween(
        head, leftArm, directions, null);

    assertNotNull(result);
    assertFalse("Sibling chain should not be empty", result.isEmpty());
    assertEquals(result.size(), directions.size());
  }

  @Test
  public void hierarchyManager_wrappersAreJointImpWrapperInstances() {
    // After extraction, wrappers in the hierarchy should be instances of
    // the top-level JointImpWrapper class
    JointImp root = hierarchy.getJointImplementation(IkTestResource.ROOT);

    assertNotNull(root);
    assertTrue("Joint wrapper should be an instance of top-level JointImpWrapper",
        root instanceof JointImpWrapper);
  }
}

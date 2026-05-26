package org.alice.ide.coverage;

import org.alice.ide.IDE;
import org.alice.ide.cascade.ExpressionCascadeContext;
import org.alice.ide.cascade.ExpressionCascadeManager;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.instancefactory.InstanceFactory;
import org.alice.ide.instancefactory.ThisInstanceFactory;
import org.alice.ide.instancefactory.croquet.InstanceFactoryState;
import org.alice.interact.DragAdapter.CameraView;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Vector3;
import org.alice.stageide.gallerybrowser.uri.UriGalleryDragModel;
import org.alice.stageide.sceneeditor.interact.GlobalDragAdapter;
import org.alice.stageide.sceneeditor.interact.manipulators.CameraZoomMouseWheelManipulator;
import org.alice.stageide.sceneeditor.interact.manipulators.OmniDirectionalBoundingBoxManipulator;
import org.junit.Test;
import org.lgna.croquet.CascadeMenuModel;
import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.AbstractNode;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.Node;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.virtualmachine.UserInstance;
import org.lgna.project.virtualmachine.VirtualMachine;
import org.lgna.story.MutableRider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

public class TopRemainingNonGuiHeadlessSweepTest {

  @Test
  public void setUpMethodGeneratorSupportsHeadlessReflectionPaths() throws Exception {
    Class<?> generator = Class.forName("org.alice.stageide.sceneeditor.SetUpMethodGenerator");
    Method getSetupStatements = generator.getDeclaredMethod(
        "getSetupStatementsForField", boolean.class, AbstractField.class, UserInstance.class, AbstractField.class, AffineMatrix4x4.class);

    Statement[] statements = (Statement[]) getSetupStatements.invoke(
        null,
        true,
        new UserField("thing", Object.class),
        null,
        null,
        null);

    assertNotNull(statements);
    assertEquals(0, statements.length);

    Method shouldPlaceModelAboveGround = generator.getDeclaredMethod("shouldPlaceModelAboveGround", AbstractType.class);
    shouldPlaceModelAboveGround.setAccessible(true);
    assertEquals(Boolean.FALSE, shouldPlaceModelAboveGround.invoke(null, JavaType.getInstance(Object.class)));
  }

  @Test
  public void sceneEditorFieldManagerHeadlessNoOpPathsAreSafe() throws Exception {
    Class<?> editorClass = Class.forName("org.alice.stageide.sceneeditor.StorytellingSceneEditor");
    Object editor = unsafe().allocateInstance(editorClass);
    Class<?> managerClass = Class.forName("org.alice.stageide.sceneeditor.SceneEditorFieldManager");
    Constructor<?> constructor = managerClass.getDeclaredConstructor(editorClass);
    constructor.setAccessible(true);
    Object manager = constructor.newInstance(editor);

    assertSame(editor, getField(manager, managerClass, "editor"));
    assertNotNull(getField(manager, managerClass, "codeGenerator"));

    Method setSelectedFieldOnManipulator = managerClass.getDeclaredMethod("setSelectedFieldOnManipulator", UserField.class);
    setSelectedFieldOnManipulator.setAccessible(true);
    setSelectedFieldOnManipulator.invoke(manager, new UserField("ship", Object.class));

    Method setSelectedExpressionOnManipulator = managerClass.getDeclaredMethod("setSelectedExpressionOnManipulator", Expression.class);
    setSelectedExpressionOnManipulator.setAccessible(true);
    setSelectedExpressionOnManipulator.invoke(manager, new MethodInvocation());
  }

  @Test
  public void expressionCascadeManagerHandlesNullContextAndLocals() {
    TestExpressionCascadeManager manager = new TestExpressionCascadeManager();
    manager.pushNullContext();
    assertNull(manager.getPreviousExpression());
    manager.popAndCheckNullContext();

    BlockStatement block = new BlockStatement();
    UserLocal local = new UserLocal("value", Object.class, false);
    block.statements.add(new LocalDeclarationStatement(local, null));
    List<UserLocal> locals = manager.toList(manager.getAccessibleLocals(new BlockStatementIndexPair(block, 1)));
    assertEquals(1, locals.size());
    assertSame(local, locals.get(0));
  }

  @Test
  public void cameraZoomManipulatorExposesHeadlessFriendlyBasics() throws Exception {
    CameraZoomMouseWheelManipulator manipulator = new CameraZoomMouseWheelManipulator();
    assertNull(manipulator.getAnimator());
    manipulator.setAnimator(null);
    assertEquals("Camera Zoom", manipulator.getUndoRedoDescription());
    assertEquals(CameraView.PICK_CAMERA, manipulator.getDesiredCameraView());

    Method interpolate = CameraZoomMouseWheelManipulator.class.getDeclaredMethod(
        "interpolateNormalizedVector", Vector3.class, Vector3.class, double.class);
    interpolate.setAccessible(true);
    Vector3 vector = (Vector3) interpolate.invoke(null, new Vector3(1, 0, 0), new Vector3(0, 1, 0), 0.5d);
    assertEquals(1.0d, vector.magnitude(), 0.00001d);
  }

  @Test
  public void globalDragAdapterHasSceneEditorDependsOnlyOnFieldPresence() throws Exception {
    GlobalDragAdapter adapter = (GlobalDragAdapter) unsafe().allocateInstance(GlobalDragAdapter.class);
    assertFalse(adapter.hasSceneEditor());

    Class<?> editorClass = Class.forName("org.alice.stageide.sceneeditor.StorytellingSceneEditor");
    setField(adapter, GlobalDragAdapter.class, "sceneEditor", unsafe().allocateInstance(editorClass));
    assertTrue(adapter.hasSceneEditor());
  }

  @Test
  public void instanceFactoryStateSupportsHeadlessValueAndIgnoreTracking() throws Exception {
    InstanceFactoryState state = (InstanceFactoryState) unsafe().allocateInstance(InstanceFactoryState.class);
    Method setSwingValue = InstanceFactoryState.class.getDeclaredMethod("setSwingValue", InstanceFactory.class);
    setSwingValue.setAccessible(true);
    Method getSwingValue = InstanceFactoryState.class.getDeclaredMethod("getSwingValue");
    getSwingValue.setAccessible(true);

    InstanceFactory value = ThisInstanceFactory.getInstance();
    setSwingValue.invoke(state, value);
    assertSame(value, getSwingValue.invoke(state));

    state.pushIgnoreAstChanges();
    assertEquals(1, getIntField(state, InstanceFactoryState.class, "ignoreCount"));
  }

  @Test
  public void ideAccessorsRemainHeadlessSafe() throws Exception {
    IDE ide = (IDE) unsafe().allocateInstance(Class.forName("org.alice.stageide.StageIDE"));
    assertEquals(IDE.AccessorAndMutatorDisplayStyle.ACCESS_AND_ASSIGNMENT,
        ide.getAccessorAndMutatorDisplayStyle(new UserField("field", Object.class)));

    Comment comment = new Comment();
    ide.setCommentThatWantsFocus(comment);
    assertSame(comment, ide.getCommentThatWantsFocus());

    java.io.File file = new java.io.File("target/test-artifacts/project.a3p");
    ide.setProjectFileToLoadOnWindowOpened(file);
    assertEquals(file, getField(ide, IDE.class, "projectFileToLoadOnWindowOpened"));

    Method getAncestor = IDE.class.getDeclaredMethod("getAncestor", Node.class, Class.class);
    getAncestor.setAccessible(true);
    BlockStatement block = new BlockStatement();
    Comment nested = new Comment();
    block.statements.add(nested);
    assertSame(block, getAncestor.invoke(null, nested, BlockStatement.class));
  }

  @Test
  public void uriGalleryDragModelHandlesEmptyArchivesWithoutGui() throws Exception {
    Path zipPath = createZip("uri-gallery-empty.zip");
    URI uri = zipPath.toUri();

    UriGalleryDragModel first = UriGalleryDragModel.getInstance(uri);
    UriGalleryDragModel second = UriGalleryDragModel.getInstance(uri);

    assertSame(first, second);
    assertNull(first.getResourceKey());
    assertTrue(first.getNodeChildren().isEmpty());
    assertEquals("unknown", first.getTypeSummaryToolTipText());
  }

  @Test
  public void sceneEditorLifecycleManagerRewritesNullVehicleToScene() throws Exception {
    Class<?> managerClass = Class.forName("org.alice.stageide.sceneeditor.SceneEditorLifecycleManager");
    Constructor<?> constructor = managerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    Object manager = constructor.newInstance(new Object[] { null });

    Class<?> generator = Class.forName("org.alice.stageide.sceneeditor.SetUpMethodGenerator");
    Method createNullVehicle = generator.getDeclaredMethod("createSetVehicleNullStatement", AbstractField.class);
    createNullVehicle.setAccessible(true);
    UserField rider = new UserField();
    rider.name.setValue("rider");
    rider.valueType.setValue(JavaType.getInstance(MutableRider.class));
    Statement statement = (Statement) createNullVehicle.invoke(null, rider);

    UserMethod generatedSetUp = new UserMethod();
    generatedSetUp.body.setValue(new BlockStatement());
    generatedSetUp.body.getValue().statements.add(statement);

    Method useSceneAsVehicle = managerClass.getDeclaredMethod("useSceneAsVehicleForDisconnectedModels", UserMethod.class);
    useSceneAsVehicle.setAccessible(true);
    useSceneAsVehicle.invoke(manager, generatedSetUp);

    MethodInvocation invocation = (MethodInvocation) ((org.lgna.project.ast.ExpressionStatement) statement).expression.getValue();
    assertTrue(invocation.requiredArguments.get(0).expression.getValue() instanceof ThisExpression);
  }

  @Test
  public void omniDirectionalBoundingBoxManipulatorExposesBasicHeadlessState() throws Exception {
    OmniDirectionalBoundingBoxManipulator manipulator = new OmniDirectionalBoundingBoxManipulator();
    assertEquals(AffineMatrix4x4.IDENTITY, manipulator.getTargetTransformation());
    assertFalse(manipulator.isUndoable());

    Method getInitialTransformable = OmniDirectionalBoundingBoxManipulator.class.getDeclaredMethod(
        "getInitialTransformable", Class.forName("org.alice.interact.InputState"));
    getInitialTransformable.setAccessible(true);
    Object initial = getInitialTransformable.invoke(manipulator, new Object[] { null });
    assertSame(getField(manipulator, OmniDirectionalBoundingBoxManipulator.class, "sgBoundingBoxTransformable"), initial);
  }

  private static Path createZip(String fileName) throws IOException {
    Path dir = Path.of("target", "test-artifacts");
    Files.createDirectories(dir);
    Path path = dir.resolve(fileName);
    try (OutputStream raw = Files.newOutputStream(path); ZipOutputStream zip = new ZipOutputStream(raw)) {
      zip.putNextEntry(new ZipEntry("version.txt"));
      zip.write("3.10.0.0".getBytes(java.nio.charset.StandardCharsets.UTF_8));
      zip.closeEntry();
    }
    return path;
  }

  private static Object getField(Object target, Class<?> owner, String name) throws Exception {
    Field field = owner.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(target);
  }

  private static void setField(Object target, Class<?> owner, String name, Object value) throws Exception {
    Field field = owner.getDeclaredField(name);
    field.setAccessible(true);
    field.set(target, value);
  }

  private static int getIntField(Object target, Class<?> owner, String name) throws Exception {
    Field field = owner.getDeclaredField(name);
    field.setAccessible(true);
    return field.getInt(target);
  }

  private static sun.misc.Unsafe unsafe() throws Exception {
    Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
    field.setAccessible(true);
    return (sun.misc.Unsafe) field.get(null);
  }

  private static final class TestExpressionCascadeManager extends ExpressionCascadeManager {
    @Override
    protected boolean isApplicableForPartFillIn(AbstractType<?, ?, ?> desiredType, AbstractType<?, ?, ?> expressionType) {
      return false;
    }

    @Override
    protected CascadeMenuModel<Expression> createPartMenuModel(Expression expression, AbstractType<?, ?, ?> desiredType,
        AbstractType<?, ?, ?> expressionType, boolean isOwnedByCascadeItemMenuCombo) {
      return null;
    }

    private List<UserLocal> toList(Iterable<UserLocal> locals) {
      java.util.ArrayList<UserLocal> list = new java.util.ArrayList<>();
      for (UserLocal local : locals) {
        list.add(local);
      }
      return list;
    }
  }
}

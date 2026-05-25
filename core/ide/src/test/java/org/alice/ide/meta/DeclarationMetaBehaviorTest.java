package org.alice.ide.meta;

import edu.cmu.cs.dennisc.pattern.Criterion;
import org.alice.ide.IDE;
import org.alice.ide.ProjectDocumentFrame;
import org.alice.ide.cascade.ExpressionCascadeManager;
import org.alice.ide.declarationseditor.DeclarationComposite;
import org.alice.ide.declarationseditor.DeclarationTabState;
import org.alice.ide.declarationseditor.DeclarationsEditorComposite;
import org.alice.ide.declarationseditor.components.DeclarationView;
import org.alice.ide.frametitle.IdeFrameTitleGenerator;
import org.alice.ide.perspectives.ProjectPerspective;
import org.alice.ide.sceneeditor.AbstractSceneEditor;
import org.alice.ide.testing.ProjectContextFixture;
import org.alice.ide.testing.TestIdeBootstrap;
import org.alice.stageide.perspectives.PerspectiveState;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.ItemState;
import org.lgna.croquet.Operation;
import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Declaration;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.SimpleArgumentListProperty;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.virtualmachine.VirtualMachine;
import sun.misc.Unsafe;

import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class DeclarationMetaBehaviorTest {
  private ProjectContextFixture fixture;
  private Application<?> previousApplication;
  private FakeDeclarationsEditorComposite declarationsEditorComposite;
  private FakeProjectDocumentFrame documentFrame;
  private FakeIde ide;

  @Before
  public void setUp() throws Exception {
    fixture = ProjectContextFixture.create();
    previousApplication = Application.getActiveInstance();

    declarationsEditorComposite = allocate(FakeDeclarationsEditorComposite.class);
    declarationsEditorComposite.tabState = new DeclarationTabState();

    documentFrame = allocate(FakeProjectDocumentFrame.class);
    documentFrame.perspectiveState = new PerspectiveState();
    documentFrame.declarationsEditorComposite = declarationsEditorComposite;
    documentFrame.inSetupScenePerspective = false;

    ide = allocate(FakeIde.class);
    ide.documentFrame = documentFrame;
    ide.performGeneratedSetUpMethod = fixture.performGeneratedSetUpMethod;

    TestIdeBootstrap.setActiveApplication(ide);
    clearListenerList("declarationListeners");
    clearListenerList("typeListeners");
    setPrevDeclaration(DeclarationMeta.getDeclaration());
  }

  @After
  public void tearDown() throws Exception {
    clearListenerList("declarationListeners");
    clearListenerList("typeListeners");
    TestIdeBootstrap.setActiveApplication(previousApplication);
  }

  @Test
  public void getDeclarationUsesSetupMethodInSetupScenePerspective() {
    documentFrame.inSetupScenePerspective = true;

    assertSame(fixture.performGeneratedSetUpMethod, DeclarationMeta.getDeclaration());
    assertSame(fixture.sceneType, DeclarationMeta.getType());
  }

  @Test
  public void privateGetTypeResolvesCodeAndTypeDeclarations() throws Exception {
    Method method = DeclarationMeta.class.getDeclaredMethod("getType", AbstractDeclaration.class);
    method.setAccessible(true);

    assertSame(fixture.sceneType, method.invoke(null, fixture.sceneProcedure));
    assertSame(fixture.actorType, method.invoke(null, fixture.actorType));
    assertNull(method.invoke(null, new Object[] {null}));
  }

  @Test
  public void addAndRemoveListenersUpdateListenerCollections() throws Exception {
    AtomicReference<ValueEvent<AbstractDeclaration>> declarationEvent = new AtomicReference<>();
    AtomicReference<ValueEvent<AbstractType<?, ?, ?>>> typeEvent = new AtomicReference<>();
    ValueListener<AbstractDeclaration> declarationListener = declarationEvent::set;
    ValueListener<AbstractType<?, ?, ?>> typeListener = typeEvent::set;

    assertEquals(0, getListenerListSize("declarationListeners"));
    assertEquals(0, getListenerListSize("typeListeners"));

    DeclarationMeta.addDeclarationMetaStateValueListener(declarationListener);
    DeclarationMeta.addTypeMetaStateValueListener(typeListener);
    assertEquals(1, getListenerListSize("declarationListeners"));
    assertEquals(1, getListenerListSize("typeListeners"));

    DeclarationMeta.removeDeclarationMetaStateValueListener(declarationListener);
    DeclarationMeta.removeTypeMetaStateValueListener(typeListener);
    assertEquals(0, getListenerListSize("declarationListeners"));
    assertEquals(0, getListenerListSize("typeListeners"));
  }

  @SuppressWarnings("unchecked")
  private static void clearListenerList(String fieldName) throws Exception {
    Field field = DeclarationMeta.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    ((List<ValueListener<?>>) field.get(null)).clear();
  }

  private static int getListenerListSize(String fieldName) throws Exception {
    Field field = DeclarationMeta.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return ((List<?>) field.get(null)).size();
  }

  private static void setPrevDeclaration(AbstractDeclaration declaration) throws Exception {
    Field field = DeclarationMeta.class.getDeclaredField("prevDeclaration");
    field.setAccessible(true);
    field.set(null, declaration);
  }

  @SuppressWarnings("unchecked")
  private static <T> T allocate(Class<T> type) {
    try {
      Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      Unsafe unsafe = (Unsafe) unsafeField.get(null);
      return (T) unsafe.allocateInstance(type);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private static final class FakeDeclarationsEditorComposite extends DeclarationsEditorComposite {
    private DeclarationTabState tabState;

    private FakeDeclarationsEditorComposite() {
      super();
    }

    @Override
    public DeclarationTabState getTabState() {
      return this.tabState;
    }
  }

  private static final class FakeProjectDocumentFrame extends ProjectDocumentFrame {
    private ItemState<ProjectPerspective> perspectiveState;
    private FakeDeclarationsEditorComposite declarationsEditorComposite;
    private boolean inSetupScenePerspective;

    private FakeProjectDocumentFrame() {
      super(null);
    }

    @Override
    public ItemState<ProjectPerspective> getPerspectiveState() {
      return this.perspectiveState;
    }

    @Override
    public DeclarationsEditorComposite getDeclarationsEditorComposite() {
      return this.declarationsEditorComposite;
    }

    @Override
    public boolean isInSetupScenePerspective() {
      return this.inSetupScenePerspective;
    }
  }

  private static final class FakeIde extends IDE {
    private ProjectDocumentFrame documentFrame;
    private UserMethod performGeneratedSetUpMethod;

    private FakeIde() {
      super(null, null);
    }

    @Override
    public List<SimpleArgumentListProperty> getArgumentLists(org.lgna.project.ast.UserCode code) {
      return List.of();
    }

    @Override
    public ProjectDocumentFrame getDocumentFrame() {
      return this.documentFrame;
    }

    @Override
    public AbstractSceneEditor getSceneEditor() {
      return null;
    }

    @Override
    public UserMethod getPerformEditorGeneratedSetUpMethod() {
      return this.performGeneratedSetUpMethod;
    }

    @Override
    protected Criterion<Declaration> getDeclarationFilter() {
      return declaration -> true;
    }

    @Override
    public ExpressionCascadeManager getExpressionCascadeManager() {
      return null;
    }

    @Override
    protected void promptForLicenseAgreements() {
    }

    @Override
    protected void registerAdaptersForSceneEditorVm(VirtualMachine vm) {
    }

    @Override
    protected String getInnerCommentForMethodName(String methodName) {
      return null;
    }

    @Override
    public boolean isInstanceCreationAllowableFor(NamedUserType userType) {
      return true;
    }

    @Override
    protected IdeFrameTitleGenerator createFrameTitleGenerator() {
      return null;
    }

    @Override
    protected BufferedImage createThumbnail() {
      return null;
    }

    @Override
    public void forceProjectCodeUpToDate() {
    }

    @Override
    public void ensureProjectCodeUpToDate() {
    }

    @Override
    protected Operation getAboutOperation() {
      return null;
    }

    @Override
    protected void handleOpenFiles(List<File> files) {
    }

    @Override
    protected void handleWindowOpened(WindowEvent e) {
    }
  }
}

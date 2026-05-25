package org.alice.stageide.ast.source;

import edu.cmu.cs.dennisc.crash.CrashDetector;
import org.alice.ide.testing.TestIdeBootstrap;
import org.alice.stageide.StageIDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.common.resources.AudioResource;
import org.lgna.common.resources.ImageResource;
import org.lgna.croquet.Application;
import org.lgna.project.Project;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.story.AudioSource;
import org.lgna.story.ImageSource;
import sun.misc.Unsafe;

import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.*;

public class SourceImportValueCreatorBehaviorTest {
  private Application<?> previousApplication;
  private Project project;

  @Before
  public void setUp() throws Exception {
    this.previousApplication = Application.getActiveInstance();
    this.project = TestIdeBootstrap.createMinimalProject();
    ProjectBackedIde ide = (ProjectBackedIde) unsafe().allocateInstance(ProjectBackedIde.class);
    ide.project = this.project;
    TestIdeBootstrap.setActiveApplication(ide);
  }

  @After
  public void tearDown() {
    TestIdeBootstrap.setActiveApplication(this.previousApplication);
  }

  @Test
  public void audioSourceImportAddsResourceAndBuildsInstanceCreation() throws Exception {
    AudioResource resource = new AudioResource(UUID.randomUUID());

    InstanceCreation creation = invokeCreate(AudioSourceImportValueCreator.getInstance(), resource);

    assertTrue(this.project.getResources().contains(resource));
    assertEquals(JavaType.getInstance(AudioSource.class), creation.constructor.getValue().getDeclaringType());
    assertEquals(1, creation.requiredArguments.size());
    JavaConstructor constructor = (JavaConstructor) creation.constructor.getValue();
    assertEquals(JavaType.getInstance(AudioResource.class), constructor.getRequiredParameters().getFirst().getValueType());
    ResourceExpression expression = (ResourceExpression) creation.requiredArguments.get(0).expression.getValue();
    assertSame(resource, expression.resource.getValue());
    assertEquals(JavaType.getInstance(AudioResource.class), expression.type.getValue());
  }

  @Test
  public void imageSourceImportAddsResourceAndBuildsInstanceCreation() throws Exception {
    ImageResource resource = new ImageResource(UUID.randomUUID());

    InstanceCreation creation = invokeCreate(ImageSourceImportValueCreator.getInstance(), resource);

    assertTrue(this.project.getResources().contains(resource));
    assertEquals(JavaType.getInstance(ImageSource.class), creation.constructor.getValue().getDeclaringType());
    assertEquals(1, creation.requiredArguments.size());
    JavaConstructor constructor = (JavaConstructor) creation.constructor.getValue();
    assertEquals(JavaType.getInstance(ImageResource.class), constructor.getRequiredParameters().getFirst().getValueType());
    ResourceExpression expression = (ResourceExpression) creation.requiredArguments.get(0).expression.getValue();
    assertSame(resource, expression.resource.getValue());
    assertEquals(JavaType.getInstance(ImageResource.class), expression.type.getValue());
  }

  private static InstanceCreation invokeCreate(SourceImportValueCreator<?, ?> creator, Resource resource) throws Exception {
    Method method = SourceImportValueCreator.class.getDeclaredMethod("createValueFromImportedValue", Resource.class);
    method.setAccessible(true);
    return (InstanceCreation) method.invoke(creator, resource);
  }

  private static Unsafe unsafe() throws Exception {
    Field field = Unsafe.class.getDeclaredField("theUnsafe");
    field.setAccessible(true);
    return (Unsafe) field.get(null);
  }

  private static final class ProjectBackedIde extends StageIDE {
    private Project project;

    private ProjectBackedIde() {
      super(new CrashDetector(ProjectBackedIde.class));
    }

    @Override
    public Project getProject() {
      return this.project;
    }

    @Override
    protected void promptForLicenseAgreements() {
    }

    @Override
    protected void handleWindowOpened(WindowEvent e) {
    }
  }
}

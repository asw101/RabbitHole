package org.alice.ide;

import org.alice.ide.testing.ProjectContextFixture;
import org.alice.ide.testing.ProjectContextTestCase;
import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.Test;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertSame;

public class SceneSetupManagerProjectContextTest extends ProjectContextTestCase {
  @Test
  public void reorganizeFieldsUsesLoadedProjectContext() throws Exception {
    ProjectContextFixture custom = ProjectContextFixture.create();
    UserField dependsOnLater = new UserField("dependsOnLater", JavaType.getInstance(Object.class), null);
    UserField later = new UserField("later", JavaType.getInstance(Object.class), new NullLiteral());
    dependsOnLater.initializer.setValue(new FieldAccess(new ThisExpression(), later));
    custom.sceneType.fields.clear();
    custom.sceneType.fields.add(dependsOnLater);
    custom.sceneType.fields.add(later);
    TestIdeBootstrap.loadProject(custom.project);

    Field field = IDE.class.getDeclaredField("sceneSetupManager");
    field.setAccessible(true);
    Object manager = field.get(IDE.getActiveInstance());
    Method method = SceneSetupManager.class.getDeclaredMethod("reorganizeFieldsIfNecessary");
    method.setAccessible(true);
    method.invoke(manager);

    assertSame(later, custom.sceneType.fields.get(0));
    assertSame(dependsOnLater, custom.sceneType.fields.get(1));
  }
}

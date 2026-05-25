package org.alice.stageide.ast.declaration;

import org.alice.ide.identifier.IdentifierNameGenerator;
import org.alice.stageide.gallerybrowser.shapes.AxesDragModel;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.stageide.modelresource.InstanceCreatorKey;
import org.alice.stageide.modelresource.ResourceNode;
import org.junit.Assume;
import org.junit.Test;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.SingleSelectTreeState;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.story.SGround;
import org.lgna.story.SScene;
import org.lgna.story.implementation.alice.AliceResourceUtilities;
import org.lgna.story.resources.ModelResource;

import javax.swing.JComponent;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.Assert.*;

public class ManagedFieldCompositeBehaviorTest {
  private static Method findMethod(Class<?> type, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
    Class<?> current = type;
    while (current != null) {
      try {
        Method method = current.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method;
      } catch (NoSuchMethodException e) {
        current = current.getSuperclass();
      }
    }
    throw new NoSuchMethodException(name);
  }

  private static Object invoke(Object target, String name, Class<?>[] parameterTypes, Object... args) throws Exception {
    return findMethod(target.getClass(), name, parameterTypes).invoke(target, args);
  }

  private static Object invoke(Object target, String name) throws Exception {
    return invoke(target, name, new Class<?>[0]);
  }

  private static Object invokeStatic(Class<?> type, String name, Class<?>[] parameterTypes, Object... args) throws Exception {
    Method method = type.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return method.invoke(null, args);
  }

  private static InstanceCreation createInstanceCreation(Class<?> type) {
    return AstUtilities.createInstanceCreation(type);
  }

  @Test
  public void addGroundManagedFieldComposite_usesLocalizedGroundNameAndDefaultPaintExpression() throws Exception {
    try {
      AddGroundManagedFieldComposite composite = AddGroundManagedFieldComposite.getInstance();

      assertEquals(
          AliceResourceUtilities.getLocalizedTag("ground", JComponent.getDefaultLocale()),
          invoke(composite, "generateName")
      );
      assertNotNull(composite.getPaintState().getValue());
    } catch (ExceptionInInitializerError | NoClassDefFoundError error) {
      Assume.assumeNoException(error);
    }
  }

  @Test
  public void addAxesManagedFieldComposite_usesIdentifierNameGeneratorForDefaultName() throws Exception {
    try {
      AddAxesManagedFieldComposite composite = AddAxesManagedFieldComposite.getInstance();

      assertEquals(
          IdentifierNameGenerator.SINGLETON.createIdentifierNameFromClassName(AxesDragModel.getInstance().getLocalizedClassName()),
          invoke(composite, "generateName")
      );
    } catch (ExceptionInInitializerError | NoClassDefFoundError error) {
      Assume.assumeNoException(error);
    }
  }

  @Test
  public void addResourceKeyManagedFieldComposite_derivesInitializerTypeAndGeneratedNameFromResourceKey() throws Exception {
    AddResourceKeyManagedFieldComposite composite = AddResourceKeyManagedFieldComposite.getInstance();
    StubResourceKey resourceKey = new StubResourceKey(
        "Friendly Ground",
        createInstanceCreation(SGround.class)
    );

    composite.getLaunchOperationToCreateValue(resourceKey, true);
    InstanceCreation initializer = (InstanceCreation) invoke(composite, "getInitializerInitialValue");
    AbstractType<?, ?, ?> derivedType = (AbstractType<?, ?, ?>) invokeStatic(
        AddResourceKeyManagedFieldComposite.class,
        "getDeclaringTypeFromInitializer",
        new Class<?>[]{Expression.class},
        initializer
    );

    assertNotNull(initializer);
    assertSame(initializer.constructor.getValue().getDeclaringType(), derivedType);
    assertEquals(
        IdentifierNameGenerator.SINGLETON.createIdentifierNameFromResourceKey(resourceKey),
        invoke(composite, "generateName")
    );

    composite.getLaunchOperationToCreateValue(null, false);
    assertNull(invoke(composite, "getInitializerInitialValue"));
  }

  @Test
  public void addCopiedManagedFieldComposite_copiesInitializerAndStripsTrailingDigitsFromCopiedFieldName() throws Exception {
    AddCopiedManagedFieldComposite composite = AddCopiedManagedFieldComposite.getInstance();
    InstanceCreation sourceInitializer = createInstanceCreation(SGround.class);
    NamedUserType sceneType = AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
    UserField fieldToCopy = new UserField(
        "ground12",
        sourceInitializer.constructor.getValue().getDeclaringType(),
        sourceInitializer
    );
    sceneType.fields.add(fieldToCopy);

    composite.setFieldToBeCopied(fieldToCopy);
    InstanceCreation copiedInitializer = (InstanceCreation) invoke(composite, "getInitializerInitialValue");
    AbstractType<?, ?, ?> derivedType = (AbstractType<?, ?, ?>) invokeStatic(
        AddCopiedManagedFieldComposite.class,
        "getDeclaringTypeFromInitializer",
        new Class<?>[]{Expression.class},
        copiedInitializer
    );

    assertNotNull(copiedInitializer);
    assertNotSame(sourceInitializer, copiedInitializer);
    assertSame(copiedInitializer.constructor.getValue().getDeclaringType(), derivedType);
    assertEquals("ground", invoke(composite, "generateName"));
  }

  private static final class StubResourceKey extends InstanceCreatorKey {
    private final String localizedName;
    private final InstanceCreation instanceCreation;

    private StubResourceKey(String localizedName, InstanceCreation instanceCreation) {
      this.localizedName = localizedName;
      this.instanceCreation = instanceCreation;
    }

    @Override
    public String getSearchText() {
      return this.localizedName;
    }

    @Override
    public String getInternalName() {
      return this.localizedName;
    }

    @Override
    public String getLocalizedName() {
      return this.localizedName;
    }

    @Override
    public String getLocalizedCreationText() {
      return this.localizedName;
    }

    @Override
    public IconFactory getIconFactory() {
      return null;
    }

    @Override
    public boolean isLeaf() {
      return true;
    }

    @Override
    protected void appendRep(StringBuilder sb) {
      sb.append(this.localizedName);
    }

    @Override
    public InstanceCreation createInstanceCreation(Set<NamedUserType> typeCache) {
      return this.instanceCreation;
    }

    @Override
    public String[] getTags() {
      return new String[0];
    }

    @Override
    public String[] getGroupTags() {
      return new String[0];
    }

    @Override
    public String[] getThemeTags() {
      return new String[0];
    }

    @Override
    public Class<? extends ModelResource> getModelResourceCls() {
      return ModelResource.class;
    }

    @Override
    public AxisAlignedBox getBoundingBox() {
      return null;
    }

    @Override
    public boolean getPlaceOnGround() {
      return true;
    }

    @Override
    public Triggerable getLeftClickOperation(ResourceNode node, SingleSelectTreeState<ResourceNode> controller) {
      return null;
    }

    @Override
    public Triggerable getDropOperation(ResourceNode node, DragStep step, DropSite dropSite) {
      return null;
    }
  }
}

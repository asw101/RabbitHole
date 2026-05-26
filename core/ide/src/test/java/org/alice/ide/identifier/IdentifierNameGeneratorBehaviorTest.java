package org.alice.ide.identifier;

import org.alice.stageide.modelresource.ResourceKey;
import org.alice.stageide.modelresource.ResourceNode;
import org.junit.Test;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.SingleSelectTreeState;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.AbstractConstructor;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.SuperConstructorInvocationStatement;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class IdentifierNameGeneratorBehaviorTest {
  private final IdentifierNameGenerator generator = IdentifierNameGenerator.SINGLETON;

  private static AbstractConstructor firstConstructor(Class<?> cls) {
    return JavaType.getInstance(cls).getDeclaredConstructors().getFirst();
  }

  @Test
  public void createIdentifierNameFromResourceKey_prefersJavaTypeNameAndStripsStoryPrefix() {
    ResourceKey key = new StubResourceKey("Localized Biped", new InstanceCreation(firstConstructor(org.lgna.story.SBiped.class)));

    assertEquals("biped", generator.createIdentifierNameFromResourceKey(key));
  }

  @Test
  public void createIdentifierNameFromResourceKey_fallsBackToLocalizedNameWhenCreationIsMissing() {
    ResourceKey key = new StubResourceKey("Friendly Thing", null);

    assertEquals("friendly Thing", generator.createIdentifierNameFromResourceKey(key));
  }

  @Test
  public void createIdentifierNameFromResourceKey_fallsBackToLocalizedNameWhenConstructorIsMissing() {
    ResourceKey key = new StubResourceKey("Fallback Hero", new InstanceCreation());

    assertEquals("fallback Hero", generator.createIdentifierNameFromResourceKey(key));
  }

  @Test
  public void createIdentifierNameFromResourceKey_returnsEmptyWhenNoNameIsAvailable() {
    ResourceKey key = new StubResourceKey(null, null);

    assertEquals("", generator.createIdentifierNameFromResourceKey(key));
  }

  @Test
  public void createIdentifierNameFromResourceKey_usesLocalizedNameWhenCreationIsBackedByNamedUserType() {
    NamedUserType userType = new NamedUserType();
    userType.name.setValue("SCustomThing");
    userType.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserConstructor constructor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    body.constructorInvocationStatement.setValue(new SuperConstructorInvocationStatement());
    constructor.body.setValue(body);
    userType.constructors.add(constructor);

    ResourceKey key = new StubResourceKey("Friendly Custom Thing", new InstanceCreation(constructor));

    assertEquals("friendly Custom Thing", generator.createIdentifierNameFromResourceKey(key));
  }

  private static final class StubResourceKey extends ResourceKey {
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
      return false;
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
    public boolean isInstanceCreator() {
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

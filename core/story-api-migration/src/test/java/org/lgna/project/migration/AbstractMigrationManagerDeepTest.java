package org.lgna.project.migration;

import org.lgna.project.ProjectVersion;
import org.lgna.project.Version;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.Node;
import org.lgna.story.resources.ModelResource;
import org.lgna.story.resources.biped.CheshireCatResource;
import org.lgna.story.resourceutilities.ResourceTypeHelper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lgna.project.migration.TextMigrationRule.replace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AbstractMigrationManagerDeepTest {
  private static final class RecordingAstMigration extends AstMigration {
    private final String label;
    private final List<String> calls;

    private RecordingAstMigration(String label, String resultVersion, List<String> calls) {
      super(new Version(resultVersion));
      this.label = label;
      this.calls = calls;
    }

    @Override
    public void migrate(Node node, MigrationManager manager) {
      this.calls.add("migrate:" + label + ":" + node.getClass().getSimpleName());
      manager.addFinalization(() -> this.calls.add("finalize:" + label));
    }
  }

  private static final class TestMigrationManager extends AbstractMigrationManager {
    private final TextMigration[] textMigrations;
    private final AstMigration[] astMigrations;

    private TestMigrationManager(TextMigration[] textMigrations, AstMigration[] astMigrations) {
      super(ProjectVersion.getCurrentVersion());
      this.textMigrations = textMigrations;
      this.astMigrations = astMigrations;
    }

    @Override
    protected TextMigration[] getTextMigrations() {
      return this.textMigrations;
    }

    @Override
    protected AstMigration[] getAstMigrations() {
      return this.astMigrations;
    }
  }

  private static final class FakeResourceTypeHelper implements ResourceTypeHelper {
    private final InstanceCreation instanceCreation = new InstanceCreation();
    private ModelResource lastResource;
    private Set<NamedUserType> lastTypeCache = Set.of();

    @Override
    public InstanceCreation createInstanceCreation(ModelResource resourceClass, Set<NamedUserType> typeCache) {
      this.lastResource = resourceClass;
      this.lastTypeCache = new HashSet<>(typeCache);
      return this.instanceCreation;
    }
  }

  @Test
  public void managerReportsPendingTextAndAstMigrationsByVersion() {
    TextMigration textMigration = new TextMigration(new Version("3.1.1.0.0"), replace("legacy", "modern"));
    RecordingAstMigration astMigration = new RecordingAstMigration("ast", "3.1.2.0.0", new ArrayList<>());
    TestMigrationManager manager = new TestMigrationManager(new TextMigration[]{textMigration}, new AstMigration[]{astMigration});

    assertTrue(manager.hasTextMigrationsFor(new Version("3.1.0.0.0")));
    assertFalse(manager.hasTextMigrationsFor(new Version("3.1.1.0.0")));
    assertTrue(manager.hasAstMigrationsFor(new Version("3.1.0.0.0")));
    assertFalse(manager.hasAstMigrationsFor(new Version("3.1.2.0.0")));
  }

  @Test
  public void managerClearsCachedTypesBeforeTextMigration() {
    TextMigration textMigration = new TextMigration(new Version("3.1.1.0.0"), replace("legacy", "modern"));
    TestMigrationManager manager = new TestMigrationManager(new TextMigration[]{textMigration}, new AstMigration[0]);
    NamedUserType cachedType = new NamedUserType();
    cachedType.setName("Hero");
    manager.cacheType(cachedType);

    String migrated = manager.migrate("legacy text", new Version("3.1.0.0.0"));

    assertEquals("modern text", migrated);
    assertNull(manager.getCachedType("Hero"));
  }

  @Test
  public void managerRunsAstMigrationsThenFinalizersInRegistrationOrder() {
    List<String> calls = new ArrayList<>();
    RecordingAstMigration first = new RecordingAstMigration("first", "3.1.1.0.0", calls);
    RecordingAstMigration second = new RecordingAstMigration("second", "3.1.2.0.0", calls);
    TestMigrationManager manager = new TestMigrationManager(new TextMigration[0], new AstMigration[]{first, second});

    manager.migrate(new NamedUserType(), new FakeResourceTypeHelper(), new Version("3.1.0.0.0"));

    assertEquals(Arrays.asList(
        "migrate:first:NamedUserType",
        "migrate:second:NamedUserType",
        "finalize:first",
        "finalize:second"
    ), calls);
  }

  @Test
  public void managerDelegatesInstanceCreationThroughHelperUsingCachedTypes() {
    FakeResourceTypeHelper helper = new FakeResourceTypeHelper();
    List<String> calls = new ArrayList<>();
    RecordingAstMigration astMigration = new RecordingAstMigration("helper", "3.1.1.0.0", calls);
    TestMigrationManager manager = new TestMigrationManager(new TextMigration[0], new AstMigration[]{astMigration});
    NamedUserType cachedType = new NamedUserType();
    cachedType.setName("Hero");
    manager.cacheType(cachedType);
    manager.migrate(new NamedUserType(), helper, new Version("3.1.0.0.0"));

    InstanceCreation instanceCreation = manager.createInstanceCreation(CheshireCatResource.DEFAULT);

    assertSame(helper.instanceCreation, instanceCreation);
    assertSame(CheshireCatResource.DEFAULT, helper.lastResource);
    assertEquals(1, helper.lastTypeCache.size());
    assertSame(cachedType, helper.lastTypeCache.iterator().next());
  }

  @Test
  public void optionalMigrationManagerExposesProvidedAstMigrationsAndNoTextMigrations() {
    RecordingAstMigration astMigration = new RecordingAstMigration("optional", "999.0.0.0", new ArrayList<>());
    OptionalMigrationManager manager = new OptionalMigrationManager(astMigration);

    assertFalse(manager.hasTextMigrationsFor(new Version("0.0.0.0")));
    assertTrue(manager.hasAstMigrationsFor(ProjectVersion.getCurrentVersion()));
  }
}

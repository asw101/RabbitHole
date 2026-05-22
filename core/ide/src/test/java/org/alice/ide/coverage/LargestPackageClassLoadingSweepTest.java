package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LargestPackageClassLoadingSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  @Test
  public void sweep_org_alice_ide_croquet_models_cascade() {
    assertExactPackageSweep("org.alice.ide.croquet.models.cascade");
  }

  @Test
  public void sweep_org_alice_ide_icons() {
    assertExactPackageSweep("org.alice.ide.icons");
  }

  @Test
  public void sweep_org_alice_ide_croquet_models_ast_cascade_statement() {
    assertExactPackageSweep("org.alice.ide.croquet.models.ast.cascade.statement");
  }

  @Test
  public void sweep_org_alice_ide_common() {
    assertExactPackageSweep("org.alice.ide.common");
  }

  @Test
  public void sweep_org_alice_ide_declarationseditor_type() {
    assertExactPackageSweep("org.alice.ide.declarationseditor.type");
  }

  @Test
  public void sweep_org_alice_ide_ast_type_merge_croquet() {
    assertExactPackageSweep("org.alice.ide.ast.type.merge.croquet");
  }

  @Test
  public void sweep_org_alice_ide() {
    assertExactPackageSweep("org.alice.ide");
  }

  @Test
  public void sweep_org_alice_ide_croquet_models_projecturi() {
    assertExactPackageSweep("org.alice.ide.croquet.models.projecturi");
  }

  @Test
  public void sweep_org_alice_stageide_cascade_fillerinners() {
    assertExactPackageSweep("org.alice.stageide.cascade.fillerinners");
  }

  @Test
  public void sweep_org_alice_ide_declarationseditor() {
    assertExactPackageSweep("org.alice.ide.declarationseditor");
  }

  @Test
  public void sweep_org_alice_ide_ast_declaration() {
    assertExactPackageSweep("org.alice.ide.ast.declaration");
  }

  @Test
  public void sweep_org_alice_stageide_sceneeditor_viewmanager() {
    assertExactPackageSweep("org.alice.stageide.sceneeditor.viewmanager");
  }

  @Test
  public void sweep_org_alice_stageide_member() {
    assertExactPackageSweep("org.alice.stageide.member");
  }

  @Test
  public void sweep_org_alice_stageide_oneshot() {
    assertExactPackageSweep("org.alice.stageide.oneshot");
  }

  @Test
  public void sweep_org_alice_ide_member() {
    assertExactPackageSweep("org.alice.ide.member");
  }

  @Test
  public void sweep_org_alice_ide_croquet_models_ast() {
    assertExactPackageSweep("org.alice.ide.croquet.models.ast");
  }

  @Test
  public void sweep_org_alice_stageide_modelresource() {
    assertExactPackageSweep("org.alice.stageide.modelresource");
  }

  @Test
  public void sweep_org_alice_ide_croquet_edits_ast() {
    assertExactPackageSweep("org.alice.ide.croquet.edits.ast");
  }

  @Test
  public void sweep_org_alice_ide_x_components() {
    assertExactPackageSweep("org.alice.ide.x.components");
  }

  @Test
  public void sweep_org_alice_stageide_properties() {
    assertExactPackageSweep("org.alice.stageide.properties");
  }

  private void assertExactPackageSweep(String packageName) {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepExactPackage(packageName);
    assertTrue(packageName + " should resolve at least one class", result.discovered > 0);
    assertTrue(packageName + " should initialize at least one class", result.loaded > 0);
  }
}

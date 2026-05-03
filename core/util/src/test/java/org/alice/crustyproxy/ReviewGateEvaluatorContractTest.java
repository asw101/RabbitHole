package org.alice.crustyproxy;

import org.junit.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReviewGateEvaluatorContractTest {

  @Test
  public void sourceChangingRefactorIsBlockedWithoutCharacterizationTests() {
    ReviewCandidate candidate = ReviewCandidate.builder("feat/alice-source-tests-refactor")
        .sourceChanging(true)
        .touchedModule("core/story-api-migration")
        .namedSeam("ProjectMigrationManager migration compatibility")
        .touchedFile(Path.of("core/story-api-migration/src/main/java/org/lgna/project/migration/ProjectMigrationManager.java"))
        .touchesBehavior(true)
        .largestTouchedClassLines(5914)
        .focusedValidation("mvn -pl core/story-api-migration -am test")
        .build();

    GateReport report = new ReviewGateEvaluator().evaluate(candidate, readyEvidence());

    assertFalse(report.passes());
    assertEquals(GateStatus.BLOCKED, report.gate("Characterization gate").status());
    assertTrue(report.gate("Characterization gate").evidence().contains("before-state tests"));
    assertEquals(GateStatus.BLOCKED, report.gate("Approval gate").status());
  }

  @Test
  public void inventedCoveragePercentageIsBlockedUntilReportingBaselineExists() {
    ReviewCandidate candidate = ReviewCandidate.builder("loop64-coverage")
        .sourceChanging(true)
        .touchedModule("core/ide")
        .namedSeam("project IO recovery")
        .characterizationTests(List.of("ProjectLoadFailurePlanTest", "ProjectBackupRecoveryIoTest"))
        .coveragePolicy(CoveragePolicy.numericFloorBeforeBaseline(80))
        .build();

    GateReport report = new ReviewGateEvaluator().evaluate(candidate, readyEvidence());

    assertFalse(report.passes());
    assertEquals(GateStatus.BLOCKED, report.gate("Coverage baseline gate").status());
    assertTrue(report.gate("Coverage baseline gate").evidence().contains("reporting-only baseline"));
    assertTrue(report.gate("Coverage baseline gate").evidence().contains("no invented percentages"));
  }

  @Test
  public void largeClassReductionIsBlockedWhenLocReductionIsThePrimaryGoal() {
    ReviewCandidate candidate = ReviewCandidate.builder("loop64-split-migration-manager")
        .sourceChanging(true)
        .touchedModule("core/story-api-migration")
        .namedSeam("ProjectMigrationManager migration compatibility")
        .touchedFile(Path.of("core/story-api-migration/src/main/java/org/lgna/project/migration/ProjectMigrationManager.java"))
        .largestTouchedClassLines(5914)
        .primaryClaim("class got smaller")
        .characterizationTests(List.of("ProjectMigrationManagerTest"))
        .focusedValidation("mvn -pl core/story-api-migration -am test")
        .build();

    GateReport report = new ReviewGateEvaluator().evaluate(candidate, readyEvidence());

    assertFalse(report.passes());
    assertEquals(GateStatus.BLOCKED, report.gate("Class-size gate").status());
    assertTrue(report.gate("Class-size gate").evidence().contains("behavior seam"));
    assertTrue(report.gate("Class-size gate").evidence().contains("not LOC reduction"));
  }

  @Test
  public void documentationOnlyReviewArtifactSkipsRuntimeApiDatabaseAndExternalServiceGatesButStillRequiresApproval() {
    ReviewCandidate candidate = ReviewCandidate.builder("feat/alice-crusty-proxy-review")
        .sourceChanging(false)
        .documentationOnly(true)
        .artifactPath(Path.of("drinkme/2026-05-03-crusty-modernization-review.md"))
        .namedSeam("skeptical modernization review lane")
        .build();

    GateReport report = new ReviewGateEvaluator().evaluate(candidate, readyEvidence());

    assertEquals(GateStatus.NOT_APPLICABLE, report.gate("Runtime API gate").status());
    assertEquals(GateStatus.NOT_APPLICABLE, report.gate("Database gate").status());
    assertEquals(GateStatus.NOT_APPLICABLE, report.gate("External service gate").status());
    assertEquals(GateStatus.BLOCKED, report.gate("Approval gate").status());
    assertFalse(report.passes());
  }

  private static CrustyProxyEvidenceSnapshot readyEvidence() {
    return CrustyProxyEvidenceSnapshot.builder()
        .currentBranch("feat/alice-crusty-proxy-review")
        .hasTweedleGrammar(true)
        .mavenVersion("3.9.9")
        .javaVersion("21")
        .activeHooks(List.of("post-checkout", "post-commit", "post-merge", "pre-push"))
        .build();
  }
}

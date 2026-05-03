package org.alice.crustyproxy;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CrustyProxyLane {
  private final Path repositoryRoot;
  private final Path artifactPath;
  private final CrustyProxyCommandRunner commandRunner;
  private final ReviewArtifactStore artifactStore;
  private final ReviewGateEvaluator gateEvaluator;
  private final WorkstreamClassifier workstreamClassifier;

  public CrustyProxyLane(
      Path repositoryRoot,
      Path artifactPath,
      CrustyProxyCommandRunner commandRunner,
      ReviewArtifactStore artifactStore,
      ReviewGateEvaluator gateEvaluator,
      WorkstreamClassifier workstreamClassifier) {
    this.repositoryRoot = Objects.requireNonNull(repositoryRoot);
    this.artifactPath = Objects.requireNonNull(artifactPath);
    this.commandRunner = Objects.requireNonNull(commandRunner);
    this.artifactStore = Objects.requireNonNull(artifactStore);
    this.gateEvaluator = Objects.requireNonNull(gateEvaluator);
    this.workstreamClassifier = Objects.requireNonNull(workstreamClassifier);
  }

  public ReviewResult review(ReviewRequest request) {
    Objects.requireNonNull(request);
    validateBranch(request.branch());

    CrustyProxyEvidenceSnapshot evidence = new CrustyProxyEvidenceCollector(
        repositoryRoot,
        artifactPath,
        commandRunner,
        EnvironmentSettings.fromSystem()).collect();

    ReviewCandidate candidate = ReviewCandidate.builder(request.branch())
        .documentationOnly(request.documentationOnly())
        .sourceChanging(!request.documentationOnly())
        .artifactPath(artifactPath)
        .namedSeam(request.namedSeam())
        .build();

    GateReport gateReport = gateEvaluator.evaluate(candidate, evidence);
    List<WorkstreamClassification> classifications = workstreamClassifier.classify(
        WorkstreamEvidence.fromSnapshot(evidence));

    artifactStore.write(artifactPath, renderArtifact(evidence, gateReport, classifications));
    return new ReviewResult(false, artifactPath, gateReport, classifications);
  }

  private static void validateBranch(String branch) {
    if (branch == null
        || branch.isBlank()
        || branch.contains("..")
        || branch.startsWith("/")
        || branch.contains("\\")
        || branch.indexOf('\0') >= 0) {
      throw new UnsafeReviewInputException("branch input is untrusted: " + branch);
    }
  }

  private static String renderArtifact(
      CrustyProxyEvidenceSnapshot evidence,
      GateReport gateReport,
      List<WorkstreamClassification> classifications) {
    StringBuilder builder = new StringBuilder();
    builder.append("# Crusty proxy review lane for Alice modernization\n\n");
    builder.append("## Evidence snapshot\n\n");
    builder.append("- Branch: ").append(evidence.currentBranch()).append('\n');
    builder.append("- Tweedle grammar present: ").append(evidence.hasTweedleGrammar()).append('\n');
    builder.append("- NODE_OPTIONS max old space MB: ")
        .append(evidence.nodeMaxOldSpaceSizeMegabytes()).append("\n\n");
    builder.append("## Workstream classification\n\n");
    for (WorkstreamClassification classification : classifications) {
      builder.append("- ").append(classification.branch()).append(": ")
          .append(classification.status()).append(" (")
          .append(classification.evidence()).append(")\n");
    }
    builder.append("\n## Gate matrix\n\n");
    for (GateResult gate : gateReport.gates()) {
      builder.append("- ").append(gate.name()).append(": ")
          .append(gate.status()).append(" - ")
          .append(gate.evidence()).append('\n');
    }
    builder.append("\n## Coverage policy\n\n");
    builder.append("Coverage gates require reporting-only module baselines first; no invented percentages.\n\n");
    builder.append("## Class-size policy\n\n");
    builder.append("Class size is a risk signal tied to behavior seams, not a standalone goal.\n\n");
    builder.append("## External service applicability\n\n");
    builder.append("No external API client, service adapter, retry policy, or remote resilience layer is required for this local review lane.\n\n");
    builder.append("## Security and artifact hygiene\n\n");
    builder.append("Treat branch names, paths, archives, XML/JSON/Tweedle inputs, command output, and logs as untrusted.\n\n");
    builder.append("## Next high-value targets\n\n");
    builder.append("1. Initialize Tweedle grammar before broad Maven validation.\n");
    builder.append("2. Classify active, stale, superseded, and remote-delta workstreams before merge discussion.\n");
    builder.append("3. Establish reporting-only coverage baselines before numeric gates.\n");
    return builder.toString();
  }
}

interface CrustyProxyCommandRunner {
  CrustyProxyCommandResult run(String command);
}

record CrustyProxyCommandResult(String command, int exitCode, String output, String error) {
}

interface ReviewArtifactStore {
  void write(Path path, String content);
}

final class CrustyProxyEvidenceCollector {
  private static final Pattern NODE_MAX_OLD_SPACE =
      Pattern.compile("(?:^|\\s)--max-old-space-size=(\\d+)(?:\\s|$)");

  private final Path repositoryRoot;
  private final Path artifactPath;
  private final CrustyProxyCommandRunner commandRunner;
  private final EnvironmentSettings environmentSettings;

  CrustyProxyEvidenceCollector(
      Path repositoryRoot,
      Path artifactPath,
      CrustyProxyCommandRunner commandRunner,
      EnvironmentSettings environmentSettings) {
    this.repositoryRoot = Objects.requireNonNull(repositoryRoot);
    this.artifactPath = Objects.requireNonNull(artifactPath);
    this.commandRunner = Objects.requireNonNull(commandRunner);
    this.environmentSettings = Objects.requireNonNull(environmentSettings);
  }

  CrustyProxyEvidenceSnapshot collect() {
    validateArtifactPath();

    CitationSet citations = new CitationSet();
    CrustyProxyCommandResult branch = run("git --no-pager branch --show-current", citations);
    CrustyProxyCommandResult status = run("git --no-pager status --short --", citations);
    CrustyProxyCommandResult worktrees = run("git --no-pager worktree list --porcelain", citations);
    run("git submodule status tweedle-lang", citations);
    CrustyProxyCommandResult grammar = run("test -d tweedle-lang/Grammar", citations);
    CrustyProxyCommandResult commonDir = run("git rev-parse --git-common-dir", citations);
    String hooksDirectory = commonDir.output().strip() + "/hooks";
    CrustyProxyCommandResult hooks = run(
        "find " + hooksDirectory + " -maxdepth 1 -type f -perm -111 -printf %f\\n",
        citations);

    return CrustyProxyEvidenceSnapshot.builder()
        .currentBranch(branch.output().strip())
        .dirtyState(lines(status.output()))
        .worktrees(WorktreeCollection.parse(worktrees.output()))
        .hasTweedleGrammar(grammar.exitCode() == 0)
        .activeHooks(lines(hooks.output()))
        .nodeMaxOldSpaceSizeMegabytes(parseNodeMaxOldSpace())
        .citations(citations)
        .build();
  }

  private CrustyProxyCommandResult run(String command, CitationSet citations) {
    citations.addCommand(command);
    return commandRunner.run(command);
  }

  private void validateArtifactPath() {
    Path normalizedRoot = repositoryRoot.toAbsolutePath().normalize();
    Path normalizedArtifact = artifactPath.toAbsolutePath().normalize();
    Path drinkmeRoot = normalizedRoot.resolve("drinkme").normalize();
    if (!normalizedArtifact.startsWith(normalizedRoot) || !normalizedArtifact.startsWith(drinkmeRoot)) {
      throw new InvalidReviewArtifactPathException(
          "review artifact path must stay under repository root drinkme: " + artifactPath);
    }
  }

  private int parseNodeMaxOldSpace() {
    Matcher matcher = NODE_MAX_OLD_SPACE.matcher(environmentSettings.value("NODE_OPTIONS"));
    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }
    return 0;
  }

  private static List<String> lines(String output) {
    if (output == null || output.isBlank()) {
      return List.of();
    }
    return output.lines().filter(line -> !line.isBlank()).toList();
  }
}

final class EnvironmentSettings {
  private final Map<String, String> values;

  EnvironmentSettings(Map<String, String> values) {
    this.values = Map.copyOf(values);
  }

  static EnvironmentSettings empty() {
    return new EnvironmentSettings(Map.of());
  }

  static EnvironmentSettings fromSystem() {
    return new EnvironmentSettings(System.getenv());
  }

  String value(String name) {
    return values.getOrDefault(name, "");
  }
}

final class CrustyProxyEvidenceSnapshot {
  private final String currentBranch;
  private final List<String> dirtyState;
  private final WorktreeCollection worktrees;
  private final boolean hasTweedleGrammar;
  private final String mavenVersion;
  private final String javaVersion;
  private final Set<String> activeHooks;
  private final int nodeMaxOldSpaceSizeMegabytes;
  private final CitationSet citations;

  private CrustyProxyEvidenceSnapshot(Builder builder) {
    this.currentBranch = builder.currentBranch;
    this.dirtyState = List.copyOf(builder.dirtyState);
    this.worktrees = builder.worktrees;
    this.hasTweedleGrammar = builder.hasTweedleGrammar;
    this.mavenVersion = builder.mavenVersion;
    this.javaVersion = builder.javaVersion;
    this.activeHooks = Set.copyOf(builder.activeHooks);
    this.nodeMaxOldSpaceSizeMegabytes = builder.nodeMaxOldSpaceSizeMegabytes;
    this.citations = builder.citations;
  }

  static Builder builder() {
    return new Builder();
  }

  String currentBranch() {
    return currentBranch;
  }

  List<String> dirtyState() {
    return dirtyState;
  }

  WorktreeCollection worktrees() {
    return worktrees;
  }

  boolean hasTweedleGrammar() {
    return hasTweedleGrammar;
  }

  String mavenVersion() {
    return mavenVersion;
  }

  String javaVersion() {
    return javaVersion;
  }

  Set<String> activeHooks() {
    return activeHooks;
  }

  int nodeMaxOldSpaceSizeMegabytes() {
    return nodeMaxOldSpaceSizeMegabytes;
  }

  CitationSet citations() {
    return citations;
  }

  boolean isBroadMavenValidationAllowed() {
    return hasTweedleGrammar;
  }

  static final class Builder {
    private String currentBranch = "";
    private List<String> dirtyState = List.of();
    private WorktreeCollection worktrees = new WorktreeCollection(List.of());
    private boolean hasTweedleGrammar;
    private String mavenVersion = "";
    private String javaVersion = "";
    private Set<String> activeHooks = Set.of();
    private int nodeMaxOldSpaceSizeMegabytes;
    private CitationSet citations = new CitationSet();

    Builder currentBranch(String currentBranch) {
      this.currentBranch = currentBranch;
      return this;
    }

    Builder dirtyState(List<String> dirtyState) {
      this.dirtyState = dirtyState;
      return this;
    }

    Builder worktrees(WorktreeCollection worktrees) {
      this.worktrees = worktrees;
      return this;
    }

    Builder hasTweedleGrammar(boolean hasTweedleGrammar) {
      this.hasTweedleGrammar = hasTweedleGrammar;
      return this;
    }

    Builder mavenVersion(String mavenVersion) {
      this.mavenVersion = mavenVersion;
      return this;
    }

    Builder javaVersion(String javaVersion) {
      this.javaVersion = javaVersion;
      return this;
    }

    Builder activeHooks(List<String> activeHooks) {
      this.activeHooks = new LinkedHashSet<>(activeHooks);
      return this;
    }

    Builder activeHooks(Set<String> activeHooks) {
      this.activeHooks = new LinkedHashSet<>(activeHooks);
      return this;
    }

    Builder nodeMaxOldSpaceSizeMegabytes(int nodeMaxOldSpaceSizeMegabytes) {
      this.nodeMaxOldSpaceSizeMegabytes = nodeMaxOldSpaceSizeMegabytes;
      return this;
    }

    Builder citations(CitationSet citations) {
      this.citations = citations;
      return this;
    }

    CrustyProxyEvidenceSnapshot build() {
      return new CrustyProxyEvidenceSnapshot(this);
    }
  }
}

final class WorktreeCollection {
  private final List<WorktreeInfo> worktrees;

  WorktreeCollection(List<WorktreeInfo> worktrees) {
    this.worktrees = List.copyOf(worktrees);
  }

  static WorktreeCollection parse(String porcelain) {
    List<WorktreeInfo> worktrees = new ArrayList<>();
    Path path = null;
    String head = "";
    for (String line : porcelain.lines().toList()) {
      if (line.startsWith("worktree ")) {
        path = Path.of(line.substring("worktree ".length()));
      } else if (line.startsWith("HEAD ")) {
        head = line.substring("HEAD ".length());
      } else if (line.startsWith("branch refs/heads/") && path != null) {
        worktrees.add(new WorktreeInfo(
            line.substring("branch refs/heads/".length()), path, head));
        path = null;
        head = "";
      }
    }
    return new WorktreeCollection(worktrees);
  }

  boolean containsBranch(String branch) {
    return worktrees.stream().anyMatch(worktree -> worktree.branch().equals(branch));
  }

  List<WorktreeInfo> entries() {
    return worktrees;
  }
}

record WorktreeInfo(String branch, Path path, String head) {
}

final class CitationSet {
  private final Set<String> commands = new LinkedHashSet<>();

  void addCommand(String command) {
    commands.add(command);
  }

  boolean containsCommand(String command) {
    return commands.contains(command);
  }
}

final class ReviewCandidate {
  private final String branch;
  private final boolean sourceChanging;
  private final boolean documentationOnly;
  private final String touchedModule;
  private final String namedSeam;
  private final Path artifactPath;
  private final Path touchedFile;
  private final boolean touchesBehavior;
  private final int largestTouchedClassLines;
  private final String focusedValidation;
  private final List<String> characterizationTests;
  private final CoveragePolicy coveragePolicy;
  private final String primaryClaim;
  private final String maintainerApproval;

  private ReviewCandidate(Builder builder) {
    this.branch = builder.branch;
    this.sourceChanging = builder.sourceChanging;
    this.documentationOnly = builder.documentationOnly;
    this.touchedModule = builder.touchedModule;
    this.namedSeam = builder.namedSeam;
    this.artifactPath = builder.artifactPath;
    this.touchedFile = builder.touchedFile;
    this.touchesBehavior = builder.touchesBehavior;
    this.largestTouchedClassLines = builder.largestTouchedClassLines;
    this.focusedValidation = builder.focusedValidation;
    this.characterizationTests = List.copyOf(builder.characterizationTests);
    this.coveragePolicy = builder.coveragePolicy;
    this.primaryClaim = builder.primaryClaim;
    this.maintainerApproval = builder.maintainerApproval;
  }

  static Builder builder(String branch) {
    return new Builder(branch);
  }

  boolean sourceChanging() {
    return sourceChanging;
  }

  boolean documentationOnly() {
    return documentationOnly;
  }

  String namedSeam() {
    return namedSeam;
  }

  boolean touchesBehavior() {
    return touchesBehavior;
  }

  int largestTouchedClassLines() {
    return largestTouchedClassLines;
  }

  List<String> characterizationTests() {
    return characterizationTests;
  }

  CoveragePolicy coveragePolicy() {
    return coveragePolicy;
  }

  String primaryClaim() {
    return primaryClaim;
  }

  String maintainerApproval() {
    return maintainerApproval;
  }

  static final class Builder {
    private final String branch;
    private boolean sourceChanging;
    private boolean documentationOnly;
    private String touchedModule = "";
    private String namedSeam = "";
    private Path artifactPath;
    private Path touchedFile;
    private boolean touchesBehavior;
    private int largestTouchedClassLines;
    private String focusedValidation = "";
    private List<String> characterizationTests = List.of();
    private CoveragePolicy coveragePolicy = CoveragePolicy.none();
    private String primaryClaim = "";
    private String maintainerApproval = "";

    Builder(String branch) {
      this.branch = branch;
    }

    Builder sourceChanging(boolean sourceChanging) {
      this.sourceChanging = sourceChanging;
      return this;
    }

    Builder documentationOnly(boolean documentationOnly) {
      this.documentationOnly = documentationOnly;
      return this;
    }

    Builder touchedModule(String touchedModule) {
      this.touchedModule = touchedModule;
      return this;
    }

    Builder namedSeam(String namedSeam) {
      this.namedSeam = namedSeam;
      return this;
    }

    Builder artifactPath(Path artifactPath) {
      this.artifactPath = artifactPath;
      return this;
    }

    Builder touchedFile(Path touchedFile) {
      this.touchedFile = touchedFile;
      return this;
    }

    Builder touchesBehavior(boolean touchesBehavior) {
      this.touchesBehavior = touchesBehavior;
      return this;
    }

    Builder largestTouchedClassLines(int largestTouchedClassLines) {
      this.largestTouchedClassLines = largestTouchedClassLines;
      return this;
    }

    Builder focusedValidation(String focusedValidation) {
      this.focusedValidation = focusedValidation;
      return this;
    }

    Builder characterizationTests(List<String> characterizationTests) {
      this.characterizationTests = characterizationTests;
      return this;
    }

    Builder coveragePolicy(CoveragePolicy coveragePolicy) {
      this.coveragePolicy = coveragePolicy;
      return this;
    }

    Builder primaryClaim(String primaryClaim) {
      this.primaryClaim = primaryClaim;
      return this;
    }

    Builder maintainerApproval(String maintainerApproval) {
      this.maintainerApproval = maintainerApproval;
      return this;
    }

    ReviewCandidate build() {
      return new ReviewCandidate(this);
    }
  }
}

record CoveragePolicy(boolean numericFloorBeforeBaseline, int numericFloor) {
  static CoveragePolicy none() {
    return new CoveragePolicy(false, 0);
  }

  static CoveragePolicy numericFloorBeforeBaseline(int numericFloor) {
    return new CoveragePolicy(true, numericFloor);
  }
}

final class ReviewGateEvaluator {
  GateReport evaluate(ReviewCandidate candidate, CrustyProxyEvidenceSnapshot evidence) {
    List<GateResult> gates = new ArrayList<>();
    gates.add(scopeGate(candidate));
    gates.add(characterizationGate(candidate));
    gates.add(mavenEnvironmentGate(candidate, evidence));
    gates.add(coverageGate(candidate));
    gates.add(classSizeGate(candidate));
    gates.add(runtimeApiGate(candidate));
    gates.add(databaseGate(candidate));
    gates.add(externalServiceGate(candidate));
    gates.add(approvalGate(candidate));
    return new GateReport(gates);
  }

  private GateResult scopeGate(ReviewCandidate candidate) {
    if (candidate.namedSeam().isBlank()) {
      return GateResult.blocked("Scope gate", "candidate must name one modernization seam");
    }
    return GateResult.pass("Scope gate", "named modernization seam: " + candidate.namedSeam());
  }

  private GateResult characterizationGate(ReviewCandidate candidate) {
    if (candidate.sourceChanging()
        && candidate.touchesBehavior()
        && candidate.characterizationTests().isEmpty()) {
      return GateResult.blocked(
          "Characterization gate",
          "behavior refactors need before-state tests before extraction");
    }
    if (!candidate.sourceChanging()) {
      return GateResult.notApplicable("Characterization gate", "documentation-only review lane");
    }
    return GateResult.pass("Characterization gate", "characterization evidence is present or behavior is not touched");
  }

  private GateResult mavenEnvironmentGate(
      ReviewCandidate candidate,
      CrustyProxyEvidenceSnapshot evidence) {
    if (candidate.sourceChanging() && !evidence.hasTweedleGrammar()) {
      return GateResult.blocked(
          "Maven environment gate",
          "run git submodule update --init tweedle-lang and verify tweedle-lang/Grammar before broad Maven validation");
    }
    return GateResult.pass("Maven environment gate", "validation environment is usable for this candidate");
  }

  private GateResult coverageGate(ReviewCandidate candidate) {
    if (candidate.coveragePolicy().numericFloorBeforeBaseline()) {
      return GateResult.blocked(
          "Coverage baseline gate",
          "establish a reporting-only baseline first; no invented percentages before measured module data");
    }
    return GateResult.pass("Coverage baseline gate", "no premature numeric coverage floor");
  }

  private GateResult classSizeGate(ReviewCandidate candidate) {
    String claim = candidate.primaryClaim().toLowerCase();
    if (candidate.largestTouchedClassLines() >= 500
        && (claim.contains("class got smaller")
        || claim.contains("loc")
        || claim.contains("line count"))) {
      return GateResult.blocked(
          "Class-size gate",
          "large-class work must be justified by a behavior seam, not LOC reduction");
    }
    return GateResult.pass("Class-size gate", "class size is treated as risk evidence, not the goal");
  }

  private GateResult runtimeApiGate(ReviewCandidate candidate) {
    if (candidate.documentationOnly()) {
      return GateResult.notApplicable("Runtime API gate", "proxy review lane adds no runtime API");
    }
    return GateResult.pass("Runtime API gate", "no runtime API change claimed");
  }

  private GateResult databaseGate(ReviewCandidate candidate) {
    if (candidate.documentationOnly()) {
      return GateResult.notApplicable("Database gate", "proxy review lane adds no database surface");
    }
    return GateResult.pass("Database gate", "no database change claimed");
  }

  private GateResult externalServiceGate(ReviewCandidate candidate) {
    if (candidate.documentationOnly()) {
      return GateResult.notApplicable(
          "External service gate",
          "proxy review lane uses local repository evidence and adds no external service integration");
    }
    return GateResult.pass("External service gate", "no external service integration claimed");
  }

  private GateResult approvalGate(ReviewCandidate candidate) {
    if (!candidate.maintainerApproval().isBlank()) {
      return GateResult.pass("Approval gate", "maintainer approval recorded");
    }
    return GateResult.blocked("Approval gate", "maintainer approval is required before merge discussion");
  }
}

enum GateStatus {
  PASS,
  BLOCKED,
  NOT_APPLICABLE
}

record GateResult(String name, GateStatus status, String evidence) {
  static GateResult pass(String name, String evidence) {
    return new GateResult(name, GateStatus.PASS, evidence);
  }

  static GateResult blocked(String name, String evidence) {
    return new GateResult(name, GateStatus.BLOCKED, evidence);
  }

  static GateResult notApplicable(String name, String evidence) {
    return new GateResult(name, GateStatus.NOT_APPLICABLE, evidence);
  }
}

final class GateReport {
  private final Map<String, GateResult> gates;

  GateReport(List<GateResult> gates) {
    Map<String, GateResult> byName = new LinkedHashMap<>();
    for (GateResult gate : gates) {
      byName.put(gate.name(), gate);
    }
    this.gates = Collections.unmodifiableMap(byName);
  }

  boolean passes() {
    return gates.values().stream().noneMatch(gate -> gate.status() == GateStatus.BLOCKED);
  }

  GateResult gate(String name) {
    GateResult gate = gates.get(name);
    if (gate == null) {
      throw new IllegalArgumentException("unknown gate: " + name);
    }
    return gate;
  }

  List<GateResult> gates() {
    return List.copyOf(gates.values());
  }
}

final class WorkstreamEvidence {
  final Map<String, WorktreeInfo> activeWorktrees;
  final Map<String, BranchInfo> localBranches;
  final Map<String, BranchInfo> remoteBranches;
  final Map<String, String> supersededBy;
  final Map<String, String> branchPurposes;
  final Map<String, String> maintainerApprovals;
  final Set<String> gateEvidenceBranches;

  private WorkstreamEvidence(Builder builder) {
    this.activeWorktrees = Map.copyOf(builder.activeWorktrees);
    this.localBranches = Map.copyOf(builder.localBranches);
    this.remoteBranches = Map.copyOf(builder.remoteBranches);
    this.supersededBy = Map.copyOf(builder.supersededBy);
    this.branchPurposes = Map.copyOf(builder.branchPurposes);
    this.maintainerApprovals = Map.copyOf(builder.maintainerApprovals);
    this.gateEvidenceBranches = Set.copyOf(builder.gateEvidenceBranches);
  }

  static Builder builder() {
    return new Builder();
  }

  static WorkstreamEvidence fromSnapshot(CrustyProxyEvidenceSnapshot snapshot) {
    Builder builder = builder();
    for (WorktreeInfo worktree : snapshot.worktrees().entries()) {
      builder.activeWorktree(worktree.branch(), worktree.path(), worktree.head());
    }
    return builder.build();
  }

  static final class Builder {
    private final Map<String, WorktreeInfo> activeWorktrees = new LinkedHashMap<>();
    private final Map<String, BranchInfo> localBranches = new LinkedHashMap<>();
    private final Map<String, BranchInfo> remoteBranches = new LinkedHashMap<>();
    private final Map<String, String> supersededBy = new LinkedHashMap<>();
    private final Map<String, String> branchPurposes = new LinkedHashMap<>();
    private final Map<String, String> maintainerApprovals = new LinkedHashMap<>();
    private final Set<String> gateEvidenceBranches = new LinkedHashSet<>();

    Builder activeWorktree(String branch, Path path, String head) {
      activeWorktrees.put(branch, new WorktreeInfo(branch, path, head));
      localBranches.putIfAbsent(branch, new BranchInfo(branch, head, null));
      return this;
    }

    Builder localBranch(String branch, String head, java.time.Instant updatedAt) {
      localBranches.put(branch, new BranchInfo(branch, head, updatedAt));
      return this;
    }

    Builder remoteBranch(String branch, String head, java.time.Instant updatedAt) {
      remoteBranches.put(branch, new BranchInfo(branch, head, updatedAt));
      return this;
    }

    Builder supersededBy(String branch, String replacement) {
      supersededBy.put(branch, replacement);
      return this;
    }

    Builder branchPurpose(String branch, String purpose) {
      branchPurposes.put(branch, purpose);
      return this;
    }

    Builder maintainerApproval(String branch, String approval) {
      maintainerApprovals.put(branch, approval);
      return this;
    }

    Builder gateEvidence(String branch) {
      gateEvidenceBranches.add(branch);
      return this;
    }

    WorkstreamEvidence build() {
      return new WorkstreamEvidence(this);
    }
  }
}

record BranchInfo(String branch, String head, java.time.Instant updatedAt) {
}

final class WorkstreamClassifier {
  List<WorkstreamClassification> classify(WorkstreamEvidence evidence) {
    Set<String> branches = new LinkedHashSet<>();
    branches.addAll(evidence.activeWorktrees.keySet());
    branches.addAll(evidence.localBranches.keySet());
    for (String remoteBranch : evidence.remoteBranches.keySet()) {
      branches.add(normalizeRemote(remoteBranch));
    }
    branches.addAll(evidence.supersededBy.keySet());

    List<WorkstreamClassification> classifications = new ArrayList<>();
    for (String branch : branches) {
      classifications.add(classifyBranch(branch, evidence));
    }
    return classifications;
  }

  private WorkstreamClassification classifyBranch(String branch, WorkstreamEvidence evidence) {
    WorkstreamStatus status;
    String reason;

    if (evidence.supersededBy.containsKey(branch)) {
      status = WorkstreamStatus.SUPERSEDED_LANE;
      reason = "superseded by " + evidence.supersededBy.get(branch);
    } else if (hasRemoteDelta(branch, evidence)) {
      status = WorkstreamStatus.REMOTE_ONLY_DELTA;
      reason = "local branch differs from origin/" + branch;
    } else if (evidence.activeWorktrees.containsKey(branch)) {
      status = WorkstreamStatus.ACTIVE_LOCAL_WORKTREE;
      reason = "active local worktree at " + evidence.activeWorktrees.get(branch).path();
    } else if (evidence.localBranches.containsKey(branch)) {
      status = WorkstreamStatus.CANDIDATE_BRANCH;
      reason = "local branch exists without active worktree";
    } else {
      status = WorkstreamStatus.CANDIDATE_BRANCH;
      reason = "remote branch exists without local worktree";
    }

    List<String> blockers = new ArrayList<>();
    if (!evidence.maintainerApprovals.containsKey(branch)) {
      blockers.add("maintainer approval");
    }
    if (!evidence.gateEvidenceBranches.contains(branch)) {
      blockers.add("gate evidence");
    }

    boolean mergeReady = blockers.isEmpty();
    if (mergeReady) {
      status = WorkstreamStatus.MERGE_READY;
      reason = "gate evidence and maintainer approval are present";
    }

    return new WorkstreamClassification(branch, status, reason, blockers, mergeReady);
  }

  private boolean hasRemoteDelta(String branch, WorkstreamEvidence evidence) {
    BranchInfo local = evidence.localBranches.get(branch);
    BranchInfo remote = evidence.remoteBranches.get("origin/" + branch);
    return local != null && remote != null && !Objects.equals(local.head(), remote.head());
  }

  private static String normalizeRemote(String remoteBranch) {
    if (remoteBranch.startsWith("origin/")) {
      return remoteBranch.substring("origin/".length());
    }
    return remoteBranch;
  }
}

enum WorkstreamStatus {
  ACTIVE_LOCAL_WORKTREE,
  CANDIDATE_BRANCH,
  SUPERSEDED_LANE,
  STALE_BRANCH,
  REMOTE_ONLY_DELTA,
  MERGE_READY
}

record WorkstreamClassification(
    String branch,
    WorkstreamStatus status,
    String evidence,
    List<String> blockers,
    boolean mergeReady) {
  WorkstreamClassification {
    blockers = List.copyOf(blockers);
  }
}

final class ReviewRequest {
  private final String branch;
  private final boolean documentationOnly;
  private final String namedSeam;

  private ReviewRequest(Builder builder) {
    this.branch = builder.branch;
    this.documentationOnly = builder.documentationOnly;
    this.namedSeam = builder.namedSeam;
  }

  static Builder forBranch(String branch) {
    return new Builder(branch);
  }

  String branch() {
    return branch;
  }

  boolean documentationOnly() {
    return documentationOnly;
  }

  String namedSeam() {
    return namedSeam;
  }

  static final class Builder {
    private final String branch;
    private boolean documentationOnly;
    private String namedSeam = "";

    Builder(String branch) {
      this.branch = branch;
    }

    Builder documentationOnly(boolean documentationOnly) {
      this.documentationOnly = documentationOnly;
      return this;
    }

    Builder namedSeam(String namedSeam) {
      this.namedSeam = namedSeam;
      return this;
    }

    ReviewRequest build() {
      return new ReviewRequest(this);
    }
  }
}

record ReviewResult(
    boolean mergeApproved,
    Path artifactPath,
    GateReport gateReport,
    List<WorkstreamClassification> workstreamClassifications) {
  ReviewResult {
    workstreamClassifications = List.copyOf(workstreamClassifications);
  }
}

class UnsafeReviewInputException extends RuntimeException {
  UnsafeReviewInputException(String message) {
    super(message);
  }
}

class InvalidReviewArtifactPathException extends RuntimeException {
  InvalidReviewArtifactPathException(String message) {
    super(message);
  }
}

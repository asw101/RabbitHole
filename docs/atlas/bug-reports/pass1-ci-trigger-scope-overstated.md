## Bug: CI journey overstates which pushes trigger test and coverage workflows

**Layer**: user-journeys x test architecture
**Severity**: Low
**Pass**: 1

**Evidence**:

- `docs/atlas/user-journeys/README.md:315-317` says a developer can `push branch / PR` and that the same push triggers the coverage workflow.
- `.github/workflows/alice-test-ci.yml:3-7` and `.github/workflows/alice-coverage-ci.yml:3-7` only trigger on `push.branches: [develop]` and `pull_request.branches: [develop]`.
- code_quote: `docs/atlas/user-journeys/README.md:315-317`
  ```text
  Developer->>TestCI: push branch / PR
  TestCI->>Maven: git submodule update, setup-java, clean test
  Developer->>CoverageCI: same push triggers coverage workflow
  ```
- code_quote: `.github/workflows/alice-test-ci.yml:3-7`
  ```yaml
  on:
    push:
      branches: [develop]
    pull_request:
      branches: [develop]
  ```

**Impact**: The atlas promises CI behavior that feature-branch pushes do not currently get. That can lead developers to assume coverage/test automation ran when the workflows were never eligible to start.

**Fix**: Narrow Journey 5 to `push to develop or open a PR targeting develop`, or broaden the workflow triggers if the atlas behavior is the intended contract.

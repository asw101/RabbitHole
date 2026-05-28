# Contributing

Keep changes small, tested, and easy to review.

## Basic process

1. Start from a fresh branch.
2. If you are changing behavior, add or update characterization tests first.
3. Make one focused change at a time.
4. Run local validation before you ask for review.
5. Send the change through your normal team or COE review path before merge.

## Install the local pre-push hook

RabbitHole keeps a `pre-push` hook in `hooks/`.

```bash
cp hooks/pre-push .git/hooks/pre-push
chmod +x .git/hooks/pre-push
```

That hook runs Git LFS checks and Maven Checkstyle before a push.

## Required checks

Run these from the repository root:

```bash
mvn checkstyle:check -Dcheckstyle.config.location=checkstyle.xml
mvn -DincludeSims=false -Dinstall4j.skip -Dcheckstyle.skip -Djava.awt.headless=true clean test
```

Use targeted module tests when you are iterating, then finish with the broader
validation lane before review.

## Checkstyle rules

Checkstyle is part of the normal contribution flow. The repository-wide config
lives in `checkstyle.xml`, and CI runs the same command used by the pre-push
hook.

## Module size guidance

Modernization work favors small helpers over giant classes. Many refactoring
docs and contract tests use a **sub-500-line target** for extracted helpers so
one file does not carry too many responsibilities.

A good rule of thumb:

- keep new helpers focused on one job
- split large files once they become hard to test or review
- prefer another small package-private helper over adding one more mixed concern

## Test expectations

- Add tests for any behavior change.
- Prefer headless-safe tests when possible.
- Use explicit `Assume` guards for display-dependent tests.
- Do not widen a change without widening the matching docs and test coverage.

## Documentation expectations

The `docs/` directory contains durable project documentation — content that
remains useful regardless of which PR introduced it or which sprint produced it.

### What belongs in docs/

- **Getting started, architecture, testing, and contributing guides** that help
  new contributors get oriented.
- **Concept explanations** that describe *why* a design exists, not which PR
  created it.
- **Architecture atlas diagrams** generated from the codebase.

### What does not belong in docs/

- Per-PR evidence, extraction traces, or validation proofs.
- Sprint-specific coverage push notes or ratchet-by-ratchet logs.
- How-to guides that reference a specific issue number or PR as the reason
  they exist.
- Tutorials that walk through one refactoring step that has already landed.

These artifacts are useful during review but become noise once the work merges.
Keep them in `drinkme/` (investigation artifacts) or PR descriptions instead.

### Adding documentation

1. Write the content in `docs/`.
2. Add a nav entry in `mkdocs.yml`.
3. Link from `docs/index.md` if the page is a top-level entry point.
4. Run `mkdocs build --strict` if you have mkdocs installed, or verify
   the YAML is valid and all nav paths resolve to existing files.

### Site structure

The docs site uses four sections:

| Section | Purpose |
| --- | --- |
| **Start here** | Onboarding: clone, build, test, contribute |
| **Concepts** | Durable design explanations |
| **Architecture Atlas** | Machine-generated architecture diagrams |
| *(future sections)* | Add new sections when content does not fit the above |

Keep the site small and navigable. Every page should be useful to someone
arriving six months after the page was written.

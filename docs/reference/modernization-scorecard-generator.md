# Modernization scorecard generator reference

The modernization scorecard generator is the repository-owned CLI for producing the review snapshot at [`modernization-scorecard.md`](./modernization-scorecard.md).

## CLI contract

Run the generator from the repository root:

```sh
python3 scripts/generate-modernization-scorecard.py \
  --output docs/reference/modernization-scorecard.md
```

The generator reads deterministic repository inputs: coverage ratchets from `.github/workflows/alice-coverage-ci.yml`, available JaCoCo CSV files, tracked Java source paths, the QA scenario catalog, and the corpus manifest.

## Output-path safety

`--output` is always constrained to the resolved `--root`. Relative output paths are resolved under the selected root, absolute paths must also remain inside that root, and traversal outside the root is rejected before any file is written.

## Review workflow

Regenerate the scorecard before claiming modernization status changes, then review the diff. The checked-in scorecard is a snapshot of current evidence, not a hand-maintained policy document and not a substitute for the underlying tests or CI artifacts.

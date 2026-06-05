# Modernization scorecard generator reference

The modernization scorecard generator is the repository-owned CLI for producing a local review report.

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

Generate the scorecard when you need a local modernization review report, then inspect the output alongside the underlying tests and CI artifacts. Do not commit generated scorecard reports as durable documentation.

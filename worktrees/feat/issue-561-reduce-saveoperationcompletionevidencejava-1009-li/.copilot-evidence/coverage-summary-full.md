# JaCoCo line coverage

## Aggregate no-Sims coverage

| Scope | Line coverage | Covered | Missed | Total |
| --- | ---: | ---: | ---: | ---: |
| no-Sims reactor | 12.11% | 14661 | 106454 | 121115 |

## Per-module reports with JaCoCo CSV output

| Module | Line coverage | Covered | Missed | Total |
| --- | ---: | ---: | ---: | ---: |
| alice-ide | 21.21% | 49 | 182 | 231 |
| core/ast | 24.06% | 1650 | 5209 | 6859 |
| core/croquet | 0.32% | 35 | 10738 | 10773 |
| core/ide | 4.23% | 1627 | 36881 | 38508 |
| core/model-loading | 17.56% | 584 | 2742 | 3326 |
| core/scenegraph | 11.17% | 438 | 3484 | 3922 |
| core/story-api | 4.55% | 713 | 14952 | 15665 |
| core/story-api-migration | 81.96% | 3158 | 695 | 3853 |
| core/tweedle | 54.66% | 1852 | 1536 | 3388 |
| core/util | 1.85% | 182 | 9658 | 9840 |
| netbeans | 38.74% | 399 | 631 | 1030 |

## Evidence inventory

Run with `--evidence-manifest coverage-evidence-manifest.json` to write a deterministic JSON inventory of JaCoCo reports, diagnostic artifacts, gate results, and target status.

Coverage gate details appear below when a threshold is requested.

## Aggregate coverage gate

Required aggregate line coverage: 8.00%
Actual aggregate line coverage: 12.11%

Result: PASS

## Long-term aggregate coverage target

Required aggregate line coverage: 70.00%
Actual aggregate line coverage: 12.11%

Result: NOT MET

## Module coverage gates

| Module | Required line coverage | Actual line coverage | Result |
| --- | ---: | ---: | --- |
| core/ast | 18.00% | 24.06% | PASS |
| core/model-loading | 10.00% | 17.56% | PASS |
| core/story-api-migration | 75.00% | 81.96% | PASS |
| core/tweedle | 50.00% | 54.66% | PASS |
| core/scenegraph | 10.00% | 11.17% | PASS |
| netbeans | 25.00% | 38.74% | PASS |

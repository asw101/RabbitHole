# Agent guardrails for Alice modernization

This repository is `rysweet/alice3-modernization`, a standalone repository with preserved Alice 3 history.

Hard rules for humans and agents:

- Do not open issues or pull requests against `TheAliceProject/alice3`.
- Do not use the upstream issue database for modernization tracking.
- Do not push to the `upstream-source` remote.
- Do not copy Alice source into `drinkme`; `drinkme` is for investigation artifacts only.
- Keep behavior compatible with the current Alice 3 baseline unless a change is explicitly documented and tested.
- Add characterization tests before refactoring behavior.

The upstream remote is for fetch/reference only.


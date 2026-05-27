# drinkme — Investigation & Refactoring Artifacts

This directory contains design documents and investigation notes produced
during the Alice 3 modernization effort. Each file records a specific
refactoring that was analyzed, planned, or completed.

**This is a reference directory, not source code.** Nothing here is compiled
or executed. It exists so that future contributors can understand *why*
specific refactorings were done and what trade-offs were considered.

## Contents

| Document | What it covers |
|----------|---------------|
| [asgencoder-decomposition](asgencoder-decomposition.md) | Extracting `BinaryArrayEncoder` from the scene graph serializer |
| [ast-utilities-decomposition](ast-utilities-decomposition.md) | Breaking `AstUtilities` into focused helper classes |
| [awtcomponentview-hierarchy-extraction](awtcomponentview-hierarchy-extraction.md) | Extracting UI hierarchy lifecycle into `AwtHierarchyHandler` |
| [dragadapter-selection-extraction](dragadapter-selection-extraction.md) | Extracting selection logic from `DragAdapter` into `DragSelectionController` |
| [formal-spec-save-load-export-evaluation](formal-spec-save-load-export-evaluation.md) | Formal specification for Alice project save/load/export |
| [glrender-coverage-push-test-suite](glrender-coverage-push-test-suite.md) | Test suite design for the OpenGL rendering module |
| [ide-scene-setup-extraction](ide-scene-setup-extraction.md) | Extracting scene setup logic from `IDE.java` into `SceneSetupManager` |
| [ikprogram-dead-code-extraction](ikprogram-dead-code-extraction.md) | Removing dead code from `IkProgram` and extracting `IkProgramLauncher` |
| [javatype-decomposition](javatype-decomposition.md) | Breaking `JavaType` into focused helper classes |
| [singleselectliststate-decomposition](singleselectliststate-decomposition.md) | Breaking `SingleSelectListState` into focused helper classes |

## How to use these

- **Before refactoring a large class**, check if there's already an
  investigation document here. It may save you hours of analysis.
- **After completing a refactoring**, add a document here recording what
  was done and why, so the next person doesn't have to reverse-engineer it.
- **Do not put source code here.** Source code belongs in `src/`. This
  directory is for investigation artifacts only.

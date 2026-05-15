# IkProgram dead-code removal and main() extraction into IkProgramLauncher

The `test.ik.IkProgram` class has been reduced from 527 lines to under 500 lines by removing ~82 lines of commented-out dead code (8 blocks), deleting the unused `createDragProp()` method (25 lines including trailing blank), removing 6 orphaned imports, and extracting the `main()` method (25 lines) into a new `IkProgramLauncher` class.

After the changes, `IkProgram` retains its role as the IK test program — scene setup, constraint management, enforcer lifecycle, and drag-adapter wiring. The application entry point now lives in `IkProgramLauncher`, which is the only caller of `IkProgram.initializeTest()`.

## Finished behavior

### IkProgramLauncher

`IkProgramLauncher` is a package-private (no `public` modifier) final class in `test.ik`. It owns only the application bootstrap logic previously embedded at the bottom of `IkProgram`:

1. **Application bootstrap** — creates `IkSplitComposite` and `IkTestApplication`, initializes the application with command-line arguments, and sets the main composite on the document frame.
2. **Default joint configuration** — sets the initial anchor (`RIGHT_CLAVICLE`) and end (`RIGHT_WRIST`) joints, and enables linear IK while disabling angular IK.
3. **Program initialization** — instantiates `IkProgram`, initializes it in the AWT container via `IkSplitComposite`, calls `initializeTest()`, and shows the window at 1200×800.

#### Public API

```java
final class IkProgramLauncher {
    public static void main(String[] args);
}
```

- **`main(String[] args)`** — Exact code extracted from the former `IkProgram.main()`. Creates the UI scaffolding, sets default IK state, instantiates `IkProgram`, calls `program.initializeTest()`, and makes the frame visible. No logic changes from the original.

#### Dependencies

`IkProgramLauncher` has a one-way dependency on:

| Dependency | Usage |
| --- | --- |
| `IkProgram` | Instantiation and `initializeTest()` call |
| `IkSplitComposite` | UI layout composite |
| `IkTestApplication` | Application lifecycle |
| `AnchorJointIdState` | Set initial anchor joint |
| `EndJointIdState` | Set initial end joint |
| `IsLinearEnabledState` | Enable linear IK |
| `IsAngularEnabledState` | Disable angular IK |
| `BipedResource` | Joint ID constants (`RIGHT_CLAVICLE`, `RIGHT_WRIST`) |
| `JointId` | Local variable type for anchor and end joint IDs |

`IkProgram` has no dependency on `IkProgramLauncher`. The dependency is strictly one-directional.

### IkProgram (reduced)

#### Removed from IkProgram

| Item | Lines | Disposition |
| --- | --- | --- |
| Commented-out `chain`/`solver` fields (L129–130) | 2 | Deleted — dead code, replaced by enforcer-based approach |
| Commented-out `Constraints` inner class (L134–140) | 7 | Deleted — dead code, never used |
| Commented-out `currentSpeeds` field (L145) | 1 | Deleted — dead code |
| `createDragProp()` method + trailing blank (L151–175) | 25 | Deleted — zero callers in entire codebase |
| Commented-out `createChain()` method (L220–224) | 5 | Deleted — dead code, replaced by enforcer chain management |
| Commented-out `handleChainChanged_old()` method (L266–293) | 28 | Deleted — dead code, superseded by active `handleChainChanged()` at L240 |
| Commented-out `SwingUtilities.invokeLater` block in `initializeOldIkEnforcer()` (L398–407) | 10 | Deleted — dead code, orphaned UI update attempt |
| Commented-out `System.in.read()` debug block in `initializeTightIkEnforcer()` (L429–435) | 7 | Deleted — dead code, debug pause removed |
| Commented-out constraints loop in `initializeTightIkEnforcer()` (L452–473) | 22 | Deleted — dead code, superseded by direct constraint usage at L450 |
| `main(String[] args)` method (L503–527) | 25 | Extracted to `IkProgramLauncher.main()` |

#### Retained in IkProgram (not moved)

| Item | Reason |
| --- | --- |
| All listener fields (`linearAngularEnabledListener`, `jointIdListener`, `boneListener`, `targetTransformListener`) | Active — used by `initializeTest()` and runtime callbacks |
| `ikEnforcer` and `tightIkEnforcer` fields | Active — core IK state |
| `useTightIkEnforcer` and `myPositionConstraint` fields | Active — constraint management state |
| `getTargetImp()`, `getSubjectImp()`, `getAnchorImp()`, `getEndImp()` | Active — accessors used by multiple methods |
| `initializeTest()` | Active — called by `IkProgramLauncher` |
| `handleChainChanging()`, `handleChainChanged()` | Active — joint-change response |
| `targetDragStarted()`, `handleTargetTransformChanged()` | Active — drag interaction |
| `initializeOldIkEnforcer()`, `initializeTightIkEnforcer()` | Active — enforcer lifecycle |
| `updateInfo()`, `handleBoneChanged()` | Active — UI refresh |
| Inline TODOs at L232–233, L246, L258, L421, L487–488 | Active — mark future work on live code paths |
| Inline commented-out code at L191–192, L203, L207–210, L263, L345 | Retained — small developer-context hints within active methods |

#### Changed methods in IkProgram

**`initializeTest()`** — Visibility changed from `private` to package-private (no access modifier). This allows `IkProgramLauncher` (same package `test.ik`) to call it after constructing the program instance. No other callers exist outside the package — grep confirms zero references from other packages.

#### Removed imports from IkProgram

| Import | Reason |
| --- | --- |
| `org.lgna.story.Color` | Only used by deleted `createDragProp()` |
| `org.lgna.story.SCone` | Only used by deleted `createDragProp()` |
| `org.lgna.story.SModel` | Only used as return type of deleted `createDragProp()` |
| `org.lgna.story.MoveDirection` | Only used by deleted `createDragProp()` |
| `org.lgna.story.Turn` | Only used by deleted `createDragProp()` |
| `org.lgna.story.TurnDirection` | Only used by deleted `createDragProp()` |

#### Retained imports in IkProgram

All other imports remain — they are used by active code paths. Key retained imports include:

- `org.lgna.story.SBiped`, `org.lgna.story.SCamera`, `org.lgna.story.SSphere` — scene objects
- `org.lgna.ik.core.enforcer.*` — IK enforcer infrastructure
- `org.lgna.story.implementation.*` — implementation accessors
- `test.ik.croquet.*` — state management (wildcard import covers `AnchorJointIdState`, `EndJointIdState`, etc.)

Note: `BipedResource` import remains in `IkProgram` because it is used by the active `initializeOldIkEnforcer()` method's joint chain setup (L351), independent of its use in the extracted `main()`.

## Test coverage

No existing unit tests cover `IkProgram` or its `main()` method. This is a visual IK test harness, not a unit-tested component. The extraction is verified by:

1. **Compilation gate** — `mvn compile -pl core/ide -am` passes with zero errors.
2. **No behavioral change** — `IkProgramLauncher.main()` contains the exact code previously in `IkProgram.main()`, character-for-character (modulo the class-qualification change from `new IkProgram()` which remains identical since it's in the same package).

## Line-count accounting

| Source | Lines removed | Lines added | Net |
| --- | --- | --- | --- |
| Commented-out `chain`/`solver` fields (L129–130) | −2 | 0 | −2 |
| Commented-out `Constraints` class (L134–140) | −7 | 0 | −7 |
| Commented-out `currentSpeeds` (L145) | −1 | 0 | −1 |
| `createDragProp()` + trailing blank (L151–175) | −25 | 0 | −25 |
| Commented-out `createChain()` (L220–224) | −5 | 0 | −5 |
| Commented-out `handleChainChanged_old()` (L266–293) | −28 | 0 | −28 |
| Commented-out `SwingUtilities.invokeLater` block (L398–407) | −10 | 0 | −10 |
| Commented-out `System.in.read()` debug block (L429–435) | −7 | 0 | −7 |
| Commented-out constraints loop (L452–473) | −22 | 0 | −22 |
| `main()` method extracted (L503–527) | −25 | 0 | −25 |
| `initializeTest()` visibility change | 0 | 0 | 0 |
| 6 removed imports | −6 | 0 | −6 |
| **Total removed from IkProgram** | **−138** | **0** | **−138** |

Some blank-line cleanup may adjust the count slightly. Final `IkProgram.java`: ~389 lines (target: under 500 ✓).

`IkProgramLauncher.java`: ~70 lines (license header + package + imports + class body).

## Module and build impact

Both files reside in `test.ik` within `core/ide`. No changes to `pom.xml`, `module-info.java`, or any other module descriptor. `IkProgramLauncher` is package-private, so no new public API surface is exposed outside the package.

Build verification: `mvn compile -pl core/ide -am` must pass with zero new errors. The `tweedle-lang` submodule must be initialized before building: `git submodule update --init tweedle-lang`.

## Usage

### Running the IK test program

Before this change:
```bash
# Entry point was IkProgram.main()
java -cp ... test.ik.IkProgram
```

After this change:
```bash
# Entry point is now IkProgramLauncher.main()
java -cp ... test.ik.IkProgramLauncher
```

The runtime behavior is identical. The IK test window opens at 1200×800 with the ogre biped model, right arm chain (RIGHT_CLAVICLE → RIGHT_WRIST), linear IK enabled, and angular IK disabled. The camera navigation and target-drag adapters function exactly as before.

### For developers modifying IkProgram

- **Adding new IK test setup logic** — add to `IkProgram.initializeTest()` (package-private, called from launcher).
- **Changing default joint configuration** — edit `IkProgramLauncher.main()` where `AnchorJointIdState` and `EndJointIdState` are set.
- **Adding new enforcer types** — add fields and initialization in `IkProgram`, following the pattern of `ikEnforcer` / `tightIkEnforcer`.
- **Re-enabling commented-out features** — the deleted commented-out blocks are preserved in git history. If the `Constraints` class, `currentSpeeds` map, `createChain()` method, or `handleChainChanged_old()` method are needed in the future, recover them from the commit prior to this refactoring.

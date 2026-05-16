# IngredientsComposite HairStyleManager and OutfitFactory Extraction

This reference documents the extraction of hair style resolution and outfit
construction logic from `IngredientsComposite` into two new package-private
delegate classes (issue #723). The extraction also removes ~100 lines of dead
code (commented-out methods, unused fields) and consolidates 11 boilerplate
`State.ValueListener` anonymous classes into a generic helper.

The extraction is a pure internal refactor. The public API surface —
`IngredientsComposite` — is unchanged. All existing person-editor behavior,
hair selection, outfit switching, and state management are preserved
identically.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [File inventory](#file-inventory)
- [Class responsibilities](#class-responsibilities)
  - [HairStyleManager](#hairstylemanager)
  - [OutfitFactory](#outfitfactory)
  - [IngredientsComposite changes](#ingredientscomposite-changes)
- [Public API](#public-api)
- [Delegation pattern](#delegation-pattern)
- [Dead code removed](#dead-code-removed)
- [Listener consolidation](#listener-consolidation)
- [Visibility changes](#visibility-changes)
- [Validation commands](#validation-commands)
- [Characterization tests](#characterization-tests)
- [Acceptance criteria](#acceptance-criteria)

## Motivation

`IngredientsComposite.java` is the largest remaining non-migration,
non-library Java file at 789 lines. It manages the entire person-editor
ingredient panel: life stage, gender, skin color, hair, face, eyes, outfit,
and obesity. Three distinct concerns are interleaved:

| Concern | Lines | Methods |
| --- | --- | --- |
| Hair color priority tracking and resolution | ~80 | `addHairColorNameToFront()`, `getHairForHairHatStyle()`, `updateHairHatStyleHairColorName()`, hair-related logic in `popAtomic()` |
| Outfit construction from life stage + gender | ~86 | `getOutfit()`, `updateOutfit()`, `updateFullBodyOutfit()`, `updateTopAndBottomOutfit()` |
| Dead code (commented-out methods, unused fields) | ~100 | Lines 466–540 (two commented-out methods), lines 677–699 (commented-out block in `popAtomic()`), lines 778–779 (commented-out calls in `setStates()`), unused `FORCE_GRAY_SKIN_TONE` field |

After extraction and dead code removal, `IngredientsComposite.java` drops
from 789 to ~490 lines — under the 500-line target. The public API surface
is unchanged: all public methods remain on `IngredientsComposite` with
identical signatures. No external caller changes.

## Architecture

```text
IngredientsComposite (public facade — thin delegation for hair + outfit)
├── HairStyleManager (package-private — hair color priority + resolution)
├── OutfitFactory (package-private, stateless — outfit construction switch)
├── FullBodyOutfitTabComposite (unchanged — full body outfit tab)
├── TopAndBottomOutfitTabComposite (unchanged — top/bottom outfit tab)
├── HairTabComposite (unchanged — hair selection tab)
└── FaceTabComposite (unchanged — face/eye selection tab)
```

`HairStyleManager` is stored as a field on `IngredientsComposite`. It owns
the `hairColorNames` priority list and all hair resolution methods.

`OutfitFactory` is a stateless utility class with static methods. It receives
the current life stage, gender, and outfit piece values as method parameters
— no mutable state, no constructor references to `IngredientsComposite`.

Both classes are package-private and live in
`org.alice.stageide.personresource`.

## File inventory

| File | Role | Approx lines |
| --- | --- | --- |
| `IngredientsComposite.java` | Parent class — state fields, listener management, activation lifecycle, thin delegation stubs. | ~490 |
| `HairStyleManager.java` | Hair color priority tracking, hair-for-style resolution, hair state updates. | ~110 |
| `OutfitFactory.java` | Stateless outfit construction from life stage + gender + selected pieces. | ~100 |
| `IngredientsCompositeTest.java` | Characterization tests for `HairStyleManager` and `OutfitFactory`. | ~120 |
| `IngredientsCompositeApiSurfaceTest.java` | Reflection-based API surface guard — ensures public method signatures are not accidentally changed. | ~60 |

All production source files reside in:

```text
core-nonfree/ide-nonfree/src/main/java/org/alice/stageide/personresource/
```

Test files reside in:

```text
core-nonfree/ide-nonfree/src/test/java/org/alice/stageide/personresource/
```

## Class responsibilities

### HairStyleManager

| Property | Value |
| --- | --- |
| New file | `HairStyleManager.java` |
| Visibility | Package-private (no access modifier) |
| Constructor | `HairStyleManager()` |
| Lifecycle | Stored as `hairStyleManager` field on `IngredientsComposite` |
| Approximate lines | ~110 |

This class owns the hair color priority list and all methods that resolve
which `Hair` enum constant to use for a given `HairHatStyle`. Previously,
these methods and the `hairColorNames` field were on `IngredientsComposite`.

| Responsibility | Method/Field |
| --- | --- |
| Priority list | `List<HairColorName> hairColorNames` — linked list of recently-used hair color names, most-recent first |
| Record color use | `addHairColorNameToFront(HairColorName)` — moves the given color to the front of the priority list (thread-safe via synchronized block) |
| Resolve hair for style | `getHairForHairHatStyle(HairHatStyle)` — walks the priority list and returns the first `Hair` available for that style; falls back to the style's first combo if no priority match |
| Update hair state | `updateHairHatStyleHairColorName(LifeStage, Gender, HairHatStyleHairColorName, HairTabComposite)` — sets the hair hat style data, repopulates the color name list, and sets both states transactionlessly |
| Handle life stage/gender change | `handleLifeStageOrGenderChange(LifeStage, Gender, HairTabComposite)` — randomizes hair hat style for the new life stage/gender, resolves a color, and updates the state |
| Handle hair change | `handleHairChange(LifeStage, Gender, Hair, HairTabComposite)` — resolves hair hat style and color from a given `Hair`, updates the state |

The constructor is trivial:

```java
HairStyleManager() {
  // hairColorNames starts empty; populated via addHairColorNameToFront()
}
```

**Thread safety:** The `hairColorNames` list is accessed under
`synchronized (this.hairColorNames)` in `addHairColorNameToFront()` and
`getHairForHairHatStyle()`, matching the original behavior.

**Public delegation:** `IngredientsComposite.getHairForHairHatStyle()` remains
as a public delegation method because `HairListCellRenderer` (in sub-package
`views.renderers`) calls it. The delegation is a one-liner:

```java
public Hair getHairForHairHatStyle(HairHatStyle hairHatStyle) {
  return this.hairStyleManager.getHairForHairHatStyle(hairHatStyle);
}
```

### OutfitFactory

| Property | Value |
| --- | --- |
| New file | `OutfitFactory.java` |
| Visibility | Package-private (no access modifier) |
| Constructor | None (utility class — private constructor) |
| Lifecycle | Stateless — all methods are static |
| Approximate lines | ~100 |

This class owns the outfit construction switch expression that maps
`(LifeStage, Gender, TopPiece, BottomPiece)` to the correct
`TopAndBottomOutfit` subclass. It also contains the outfit selection logic
that decides between full-body and top-and-bottom outfits.

| Responsibility | Method |
| --- | --- |
| Select active outfit | `getOutfit(LifeStage, Gender, FullBodyOutfit, TopPiece, BottomPiece, boolean topsAndBottomsAvailable, boolean lastActiveIsTopAndBottom)` — returns the appropriate `Outfit` based on tab state and data availability |
| Construct top-and-bottom outfit | `createTopAndBottomOutfit(LifeStage, Gender, TopPiece, BottomPiece)` — switch expression creating the correct `TopAndBottomOutfit` subclass for the given life stage and gender |
| Update full body outfit data | `updateFullBodyOutfit(LifeStage, Gender, FullBodyOutfit, FullBodyOutfitTabComposite)` — clears and repopulates the full body outfit data, sets state |
| Update top/bottom outfit data | `updateTopAndBottomOutfit(LifeStage, Gender, TopAndBottomOutfit, TopAndBottomOutfitTabComposite)` — clears and repopulates both top piece and bottom piece data, sets state |
| Update all outfit data | `updateOutfit(LifeStage, Gender, Outfit, MapToMap, FullBodyOutfitTabComposite, TopAndBottomOutfitTabComposite)` — decomposes an `Outfit` and delegates to the full-body and top-and-bottom update methods |

**Statelessness:** All `OutfitFactory` methods are static. Mutable state
(the `RefreshableDataSingleSelectListState` objects and tab composites) is
passed as method parameters. This makes the factory trivially testable —
no Croquet context required for the switch expression logic.

The switch expression for `createTopAndBottomOutfit`:

> **Note:** The original `getOutfit()` returns `fullbody` for TODDLER as a
> defensive fallback. In the extracted design, `createTopAndBottomOutfit`
> returns `null` for TODDLER because `getOutfit()` handles the fallback to
> full-body outfit before calling the switch. The guard clause
> `if (!topsAndBottomsAvailable ... && fullbody != null) return fullbody`
> prevents TODDLER from reaching the switch in practice.

```java
static Outfit createTopAndBottomOutfit(LifeStage lifeStage, Gender gender,
    TopPiece top, BottomPiece bottom) {
  return switch (lifeStage) {
    case TODDLER -> null; // Toddlers have no top-and-bottom option
    case CHILD -> switch (gender) {
      case MALE -> new MaleChildTopAndBottomOutfit(
          (MaleChildTopPiece) top, (MaleChildBottomPiece) bottom);
      case FEMALE -> new FemaleChildTopAndBottomOutfit(
          (FemaleChildTopPiece) top, (FemaleChildBottomPiece) bottom);
    };
    case TEEN -> switch (gender) {
      case MALE -> new MaleTeenTopAndBottomOutfit(
          (MaleTeenTopPiece) top, (MaleTeenBottomPiece) bottom);
      case FEMALE -> new FemaleTeenTopAndBottomOutfit(
          (FemaleTeenTopPiece) top, (FemaleTeenBottomPiece) bottom);
    };
    case ADULT -> switch (gender) {
      case MALE -> new MaleAdultTopAndBottomOutfit(
          (MaleAdultTopPiece) top, (MaleAdultBottomPiece) bottom);
      case FEMALE -> new FemaleAdultTopAndBottomOutfit(
          (FemaleAdultTopPiece) top, (FemaleAdultBottomPiece) bottom);
    };
    case ELDER -> switch (gender) {
      case MALE -> new MaleElderTopAndBottomOutfit(
          (MaleElderTopPiece) top, (MaleElderBottomPiece) bottom);
      case FEMALE -> new FemaleElderTopAndBottomOutfit(
          (FemaleElderTopPiece) top, (FemaleElderBottomPiece) bottom);
    };
  };
}
```

### IngredientsComposite changes

| Change | Detail |
| --- | --- |
| New field | `final HairStyleManager hairStyleManager = new HairStyleManager()` |
| `hairColorNames` removed | Field moved to `HairStyleManager.hairColorNames` |
| `FORCE_GRAY_SKIN_TONE` removed | Unused field deleted — never read anywhere in the codebase |
| `addHairColorNameToFront()` removed | Method moved to `HairStyleManager` |
| `getHairForHairHatStyle()` retained | Body replaced with `hairStyleManager.getHairForHairHatStyle(hairHatStyle)` (public delegation for `HairListCellRenderer`) |
| `updateHairHatStyleHairColorName()` removed | Method moved to `HairStyleManager.updateHairHatStyleHairColorName()` |
| `getOutfit()` removed | Method moved to `OutfitFactory.getOutfit()` |
| `updateOutfit()` removed | Method moved to `OutfitFactory.updateOutfit()` |
| `updateFullBodyOutfit()` removed | Method moved to `OutfitFactory.updateFullBodyOutfit()` |
| `updateTopAndBottomOutfit()` removed | Method moved to `OutfitFactory.updateTopAndBottomOutfit()` |
| Lines 466–540 deleted | Two commented-out methods (`updateHairColorName`, `updateHair`) — dead code |
| Lines 677–699 deleted | Commented-out block in `popAtomic()` — dead code |
| Lines 778–779 deleted | Commented-out calls in `setStates()` — dead code |
| 11 anonymous listener classes replaced | Consolidated into `AtomicValueListener<T>` generic inner class (with `beforePop` and `afterPop` consumer slots) |
| `popAtomic()` hair logic | Delegates to `hairStyleManager.handleLifeStageOrGenderChange()` and `hairStyleManager.handleHairChange()` |
| `setStates()` hair logic | Delegates to `hairStyleManager.addHairColorNameToFront()` and `hairStyleManager.updateHairHatStyleHairColorName()` |
| `createResourceFromStates()` outfit call | Delegates to `OutfitFactory.getOutfit()` |

## Public API

The public API is exclusively on `IngredientsComposite`. No API changes are
made by this extraction. All 22 public methods remain on `IngredientsComposite`
with identical signatures:

| Method | Signature |
| --- | --- |
| `getRandomize()` | `() → Operation` |
| `getLifeStageState()` | `() → ImmutableDataSingleSelectListState<LifeStage>` |
| `getGenderState()` | `() → ImmutableDataSingleSelectListState<Gender>` |
| `getBaseFaceState()` | `() → ImmutableDataSingleSelectListState<BaseFace>` |
| `getSkinColorState()` | `() → SkinColorState` |
| `getHairHatStyleState()` | `() → RefreshableDataSingleSelectListState<HairHatStyle>` |
| `getHairColorNameState()` | `() → RefreshableDataSingleSelectListState<HairColorName>` |
| `getBaseEyeColorState()` | `() → ImmutableDataSingleSelectListState<BaseEyeColor>` |
| `getFullBodyOutfitState()` | `() → RefreshableDataSingleSelectListState<FullBodyOutfit>` |
| `getTopPieceState()` | `() → RefreshableDataSingleSelectListState<TopPiece>` |
| `getBottomPieceState()` | `() → RefreshableDataSingleSelectListState<BottomPiece>` |
| `getObesityLevelState()` | `() → BoundedDoubleState` |
| `getBodyHeadHairTabState()` | `() → ImmutableDataTabState<SimpleTabComposite<?>>` |
| `getBodyTab()` | `() → FullBodyOutfitTabComposite` |
| `getHairTab()` | `() → HairTabComposite` |
| `getFaceTab()` | `() → FaceTabComposite` |
| `getClosestBaseSkinTone()` | `() → BaseSkinTone` |
| `getHairForHairHatStyle()` | `(HairHatStyle) → Hair` |
| `pushAtomic()` | `()` |
| `popAtomic()` | `()` |
| `createResourceFromStates()` | `() → PersonResource` |
| `setStates()` | `(PersonResource)` |

Neither `HairStyleManager` nor `OutfitFactory` exposes any public API.
External callers (e.g., `PersonResourceComposite`, `PreviewComposite`,
`RandomPersonUtilities`, `HairListCellRenderer`) continue to call
`IngredientsComposite` methods. They are unaware of the delegate classes.

## Delegation pattern

`IngredientsComposite` retains all public method signatures. Extracted logic
becomes thin delegation stubs:

```java
// Before (20-line body on IngredientsComposite):
public Hair getHairForHairHatStyle(HairHatStyle hairHatStyle) {
  if (hairHatStyle != null) {
    synchronized (this.hairColorNames) {
      for (HairColorName hairColorName : this.hairColorNames) {
        Hair rv = hairHatStyle.getHair(hairColorName);
        if (rv != null) {
          return rv;
        }
      }
    }
    List<HairColorNameHairCombo> combos = hairHatStyle.getHairColorNameHairCombos();
    if (!combos.isEmpty()) {
      HairColorNameHairCombo combo = combos.getFirst();
      if (combo != null) {
        return combo.getHair();
      }
    }
  }
  return null;
}

// After (thin delegation stub):
public Hair getHairForHairHatStyle(HairHatStyle hairHatStyle) {
  return this.hairStyleManager.getHairForHairHatStyle(hairHatStyle);
}
```

The `createResourceFromStates()` outfit call changes from an instance
method call to a static factory call:

```java
// Before:
Outfit outfit = getOutfit(lifeStage, gender);

// After:
boolean topsAndBottomsAvailable = (topAndBottomTab.getBottomPieceData().getItemCount() > 0)
    && (topAndBottomTab.getTopPieceData().getItemCount() > 0);
Outfit outfit = OutfitFactory.getOutfit(lifeStage, gender,
    getFullBodyOutfitState().getValue(),
    getTopPieceState().getValue(), getBottomPieceState().getValue(),
    topsAndBottomsAvailable, lastActiveOutfitTab == topAndBottomTab);
```

## Dead code removed

| Item | Lines removed | Reason |
| --- | --- | --- |
| `updateHairColorName()` commented-out method | ~38 (lines 466–503) | Fully commented out since original commit. Never executed. Superseded by `HairUtilities.getHairHatStyleColorNameFromHair()`. |
| `updateHair()` commented-out method | ~36 (lines 505–540) | Fully commented out since original commit. Never executed. Superseded by `HairUtilities` and `HairHatStyle.getHair()`. |
| Commented-out block in `popAtomic()` | ~23 (lines 677–699) | Dead alternative hair-update logic, fully commented out. |
| Commented-out calls in `setStates()` | 2 (lines 778–779) | Dead calls to the removed `updateHairColorName()` and `updateHair()` methods. |
| `FORCE_GRAY_SKIN_TONE` field | 1 (line 543) | `private final boolean FORCE_GRAY_SKIN_TONE = true` — never read anywhere in the codebase. No method references it. |

**Total dead code removed:** ~100 lines.

The dead code removal alone brings the file from 789 to ~689 lines.
Combined with the two extractions and listener consolidation, the final
line count is ~490.

## Listener consolidation

The original file contains 11 nearly-identical `State.ValueListener<T>`
anonymous classes (lines 92–215). Each follows the same pattern:

```java
private final State.ValueListener<SomeType> someListener = new State.ValueListener<SomeType>() {
  @Override
  public void changing(State<SomeType> state, SomeType prevValue, SomeType nextValue) {
    pushAtomic();
  }

  @Override
  public void changed(State<SomeType> state, SomeType prevValue, SomeType nextValue) {
    popAtomic();
  }
};
```

Eight of the eleven listeners are pure push/pop-atomic with no extra logic.
Three have additional behavior in `changed()`, but with **different ordering**
relative to `popAtomic()`:

| Listener | Extra `changed()` behavior | Order |
| --- | --- | --- |
| `hairColorNameListener` | `addHairColorNameToFront(nextValue)` | **Before** `popAtomic()` |
| `lifeStageListener` | `updateCameraPointOfView()` | **After** `popAtomic()` |
| `skinColorListener` | `handleSkinColorChange(nextValue)` | **After** `popAtomic()` |

**⚠ Ordering matters.** `popAtomic()` can trigger `syncPersonImpAndMaps()`
when `atomicCount` reaches 0. Running extra logic before vs after the pop
is semantically different. The generic `AtomicValueListener<T>` must support
both orderings.

These are consolidated into a generic `AtomicValueListener<T>` inner class
with separate `beforePop` and `afterPop` consumer slots:

```java
private class AtomicValueListener<T> implements State.ValueListener<T> {
  private final java.util.function.Consumer<T> beforePop;
  private final java.util.function.Consumer<T> afterPop;

  AtomicValueListener() {
    this(null, null);
  }

  AtomicValueListener(java.util.function.Consumer<T> beforePop,
      java.util.function.Consumer<T> afterPop) {
    this.beforePop = beforePop;
    this.afterPop = afterPop;
  }

  @Override
  public void changing(State<T> state, T prevValue, T nextValue) {
    pushAtomic();
  }

  @Override
  public void changed(State<T> state, T prevValue, T nextValue) {
    if (beforePop != null) {
      beforePop.accept(nextValue);
    }
    popAtomic();
    if (afterPop != null) {
      afterPop.accept(nextValue);
    }
  }
}
```

Usage:

```java
// Pure push/pop listener (8 instances):
private final AtomicValueListener<Gender> genderListener = new AtomicValueListener<>();

// Listener with before-pop behavior (1 instance):
private final AtomicValueListener<HairColorName> hairColorNameListener =
    new AtomicValueListener<>(hairStyleManager::addHairColorNameToFront, null);

// Listeners with after-pop behavior (2 instances):
private final AtomicValueListener<LifeStage> lifeStageListener =
    new AtomicValueListener<>(null, v -> updateCameraPointOfView());
private final AtomicValueListener<java.awt.Color> skinColorListener =
    new AtomicValueListener<>(null, this::handleSkinColorChange);
```

The `tabListener` (line 217) is a `ValueListener<SimpleTabComposite<?>>`,
not a `State.ValueListener<T>`. It is excluded from this consolidation and
remains as-is.

**Line savings:** 11 anonymous classes (~121 lines) → 1 inner class + 11
one-liner field declarations (~30 lines) = ~91 lines saved.

## Visibility changes

| Member | Before | After | Reason |
| --- | --- | --- | --- |
| `hairColorNames` | `private` field on IngredientsComposite | `private` field on HairStyleManager | Moved to delegate |
| `addHairColorNameToFront()` | `private` on IngredientsComposite | Package-private on HairStyleManager | Called by IngredientsComposite from same package |
| `getHairForHairHatStyle()` | `public` on IngredientsComposite | `public` delegation stub on IngredientsComposite + package-private on HairStyleManager | External caller (`HairListCellRenderer`) goes through IngredientsComposite |
| `updateHairHatStyleHairColorName()` | `private` on IngredientsComposite | Package-private on HairStyleManager | Called by IngredientsComposite from same package |
| `getOutfit()` | `private` on IngredientsComposite | Package-private static on OutfitFactory | Called by IngredientsComposite from same package |
| `updateOutfit()` | `private` on IngredientsComposite | Package-private static on OutfitFactory | Called by IngredientsComposite from same package |
| `updateFullBodyOutfit()` | `private` on IngredientsComposite | Package-private static on OutfitFactory | Called by OutfitFactory internally |
| `updateTopAndBottomOutfit()` | `private` on IngredientsComposite | Package-private static on OutfitFactory | Called by OutfitFactory internally |

No public visibility increases. No protected-to-public changes. The public
API surface is identical.

## Validation commands

```bash
# Full Maven verify (from repo root)
cd core-nonfree/ide-nonfree && mvn verify -pl . -am

# Run only the characterization tests
mvn test -pl core-nonfree/ide-nonfree \
  -Dtest="IngredientsCompositeTest,IngredientsCompositeApiSurfaceTest"

# Verify line count is under 500
wc -l core-nonfree/ide-nonfree/src/main/java/org/alice/stageide/personresource/IngredientsComposite.java
```

## Characterization tests

Three test files are added before extraction to characterize existing behavior:

### HairStyleManager tests (`IngredientsCompositeTest.java`)

| Test | What it verifies |
| --- | --- |
| `getHairForHairHatStyle_returnsNull_whenStyleIsNull` | Null input returns null |
| `getHairForHairHatStyle_returnsPriorityMatch_whenColorInList` | Priority-list lookup returns the correct `Hair` for a previously-used color |
| `getHairForHairHatStyle_fallsBackToFirstCombo_whenNoPriorityMatch` | When the priority list has no matching color, returns the first available combo's hair |
| `addHairColorNameToFront_movesExistingToFront` | Re-adding a color moves it to position 0 |
| `addHairColorNameToFront_addsNewToFront` | A new color is inserted at position 0 |
| `getHairForHairHatStyle_isThreadSafe` | Concurrent add + resolve calls do not throw or deadlock |

### OutfitFactory tests (`IngredientsCompositeTest.java`)

| Test | What it verifies |
| --- | --- |
| `createTopAndBottomOutfit_adultMale` | `ADULT + MALE` produces `MaleAdultTopAndBottomOutfit` |
| `createTopAndBottomOutfit_adultFemale` | `ADULT + FEMALE` produces `FemaleAdultTopAndBottomOutfit` |
| `createTopAndBottomOutfit_childMale` | `CHILD + MALE` produces `MaleChildTopAndBottomOutfit` |
| `createTopAndBottomOutfit_teenFemale` | `TEEN + FEMALE` produces `FemaleTeenTopAndBottomOutfit` |
| `createTopAndBottomOutfit_elderMale` | `ELDER + MALE` produces `MaleElderTopAndBottomOutfit` |
| `createTopAndBottomOutfit_toddler_returnsNull` | `TODDLER` returns null (no top-and-bottom option) |
| `getOutfit_prefersFullBody_whenTopAndBottomUnavailable` | When top/bottom data is empty, returns the full-body outfit |
| `getOutfit_prefersTopAndBottom_whenLastActiveTabIsTopAndBottom` | When the last active tab was top-and-bottom, returns the constructed outfit |

### API surface guard (`IngredientsCompositeApiSurfaceTest.java`)

| Test | What it verifies |
| --- | --- |
| `publicMethodSignaturesAreStable` | Reflection-based check that all 22 public methods exist with expected parameter types and return types. Fails if a method is accidentally removed or signature-changed during refactoring. |

All tests run without a Croquet context or Alice IDE runtime. They test
extracted classes directly, using mock/stub data objects where needed.

## Acceptance criteria

1. `IngredientsComposite.java` is under 500 lines (target: ~490).
2. `HairStyleManager.java` exists in the same package with hair logic.
3. `OutfitFactory.java` exists in the same package with outfit construction.
4. All dead code (commented-out methods, unused fields) is deleted.
5. 11 anonymous listener classes are consolidated into `AtomicValueListener<T>`.
6. All characterization tests pass.
7. `mvn verify` passes for `core-nonfree/ide-nonfree`.
8. The public API surface of `IngredientsComposite` is unchanged.
9. `HairListCellRenderer` compiles without changes (uses the retained
   `getHairForHairHatStyle()` delegation method).
10. No external caller changes required.

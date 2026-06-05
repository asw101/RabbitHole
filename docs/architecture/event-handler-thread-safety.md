# Event Handler Thread Safety

The Alice 3 event system dispatches listener callbacks on background threads
via `ComponentExecutor`. The `AbstractEventHandler` base class coordinates
concurrent delivery using a per-listener, per-lock **isFiringMap** that
prevents re-entrant or overlapping event dispatch when the
`MultipleEventPolicy` is `IGNORE`.

## How isFiringMap works

```
isFiringMap : Map<Listener, Map<EventLock, Boolean>>
```

When `fireEvent()` dispatches to a listener:

1. It sets `isFiringMap[listener][eventLock] = true`.
2. It starts a `ComponentExecutor` thread that calls `fire()`.
3. After `fire()` (and `fireDequeue()` for ENQUEUE policy) returns, the
   thread sets `isFiringMap[listener][eventLock] = false`.

## The bug (fixed)

Previously, the cleanup in step 3 was **not** wrapped in `try-finally`:

```java
// BEFORE — the original code (the bug)
private ComponentExecutor newEventCall(L listener, E event, Object eventLock) {
  return new ComponentExecutor(() -> {
    fire(listener, event);
    if (policyMap.get(listener).equals(MultipleEventPolicy.ENQUEUE)) {
      fireDequeue(listener);
    }
    isFiringMap.get(listener).put(eventLock, false);   // never reached if fire() throws
  }, "eventThread");
}
```

If `fire()` or `fireDequeue()` threw, the flag remained `true` forever,
and the listener never received another event for that lock — a silent,
permanent failure.

## The fix

The dispatch body is now wrapped in `try-finally` so the flag is **always** cleared:

```java
// AFTER — current code
private ComponentExecutor newEventCall(L listener, E event, Object eventLock,
                                        Map<Object, Boolean> activeThings) {
  MultipleEventPolicy policy = policyMap.get(listener);
  return new ComponentExecutor(() -> {
    try {
      fire(listener, event);
      if (policy == MultipleEventPolicy.ENQUEUE) {
        fireDequeue(listener);
      }
    } finally {
      activeThings.put(eventLock, false);
    }
  }, "eventThread");
}
```

The `finally` block is intentionally minimal — a single `ConcurrentHashMap.put()`
that is O(1), idempotent, and cannot throw. This guarantees that a
misbehaving listener cannot permanently block future event delivery to itself.

## Affected subclasses

All direct subclasses inherit the fix through normal Java inheritance.
`TransformationChangedHandler` is itself **abstract** and has its own
concrete subclass tree.

| Direct subclass | Event type | Notes |
| --- | --- | --- |
| `KeyPressedHandler` | Keyboard events | Concrete |
| `MouseClickedHandler` | Mouse click events | Concrete |
| `TimerEventHandler` | Periodic timer events | Concrete; also implements `SceneActivationListener` |
| `SceneActivationHandler` | Scene activate/deactivate events | Concrete |
| `TransformationChangedHandler` | Position/orientation change events | **Abstract** — see below |

### TransformationChangedHandler subtree

| Subclass | Event type |
| --- | --- |
| `TransformationHandler` | Point-of-view change events |
| `ViewEventHandler` | View events |
| `AbstractBinaryEventHandler` | Abstract base for proximity/occlusion events |

No subclass at any level overrides `newEventCall()`, so no per-subclass
changes are needed.

## Multiple event policies

The `MultipleEventPolicy` enum controls what happens when a second event
arrives while a listener is still processing the first:

| Policy | Behavior |
| --- | --- |
| `IGNORE` | Second event is silently dropped while `isFiringMap` flag is `true` |
| `ENQUEUE` | Second event is queued; after `fire()` returns, `fireDequeue()` drains the queue |
| `COMBINE` | Second event starts a new `ComponentExecutor` immediately (concurrent) |

The `isFiringMap` flag matters most for `IGNORE` and `ENQUEUE`. For `COMBINE`,
dispatch proceeds regardless of the flag, but the flag is still cleaned up
correctly to avoid stale state.

## Thread safety guarantees

- `isFiringMap` is a `ConcurrentHashMap<L, ConcurrentHashMap<Object, Boolean>>`.
- `policyMap` is a `ConcurrentHashMap<L, MultipleEventPolicy>`.
- The event queue (`CopyOnWriteArrayList<E>`) is thread-safe for concurrent
  reads during `fireDequeue()`.
- Each `ComponentExecutor` runs on its own thread, so listener callbacks
  do not block the thread that called `fireEvent()`.

## Testing the guarantee

A characterization test `AbstractEventHandlerAsyncTest.isFiringMapClearedEvenWhenFireThrows`
validates the fix. It:

1. Registers a listener that throws `RuntimeException`.
2. Dispatches an event.
3. Polls `isFiringMap` for up to 5 seconds.
4. Asserts the flag is `false` — proving cleanup ran despite the exception.

Two other tests exist in `AbstractEventHandlerAsyncTest`:

- `enqueuePolicyDeliversQueuedEventsAfterActiveListenerCompletes` — verifies
  ENQUEUE policy delivers queued events and clears the firing flag.
- `silenceAndRestoreToggleEventDelivery` — verifies `silenceListeners()` /
  `restoreListeners()` toggles delivery.

Run all event handler tests with:

```bash
mvn -pl core/story-api -am -Dtest=AbstractEventHandlerAsyncTest test
```

See the
[core/story-api TESTING.md](https://github.com/rysweet/RabbitHole/blob/develop/core/story-api/TESTING.md)
file for the full story-api test inventory.

## Design rationale

- **try-finally over try-catch**: We do not swallow or re-throw the exception.
  The `ComponentExecutor` already handles thread-level exceptions. Our only
  responsibility is cleanup.
- **No new concurrency primitives**: The fix uses the existing
  `ConcurrentHashMap.put()`. No locks, atomics, or latches are added.
- **No public API changes**: The fix is entirely internal to
  `AbstractEventHandler`. Student-facing APIs (`SScene`, `SBiped`, etc.)
  are unaffected.
- **Characterization test first**: The exception-safety test was written
  before the fix to confirm the bug, then verified again after the fix to
  confirm the behavior change.

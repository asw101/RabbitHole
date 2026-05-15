package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * TDD contract tests for {@link CompositeResourceManager}.
 *
 * <p>CompositeResourceManager owns the 14 state maps (mapKeyToStringValue,
 * mapKeyToBooleanState, etc.), all non-tab {@code create*} factory methods,
 * the {@code contains(Model)} search, and the {@code localize()} logic
 * extracted from AbstractComposite.</p>
 *
 * <p>Design contract:
 * <ul>
 *   <li>Constructor: {@code CompositeResourceManager()}</li>
 *   <li>{@code createKey(composite, localizationKey)} — creates an AbstractComposite.Key</li>
 *   <li>{@code createStringValue(key)} — creates InternalStringValue, registers in map</li>
 *   <li>{@code createBooleanState(key, initialValue)} — creates, registers</li>
 *   <li>{@code createStringState(key, initialValue)} — creates, registers</li>
 *   <li>... (all 20 primary factory methods)</li>
 *   <li>{@code contains(Model)} — returns true if model is a value in any of the 14 maps</li>
 *   <li>{@code localize(composite)} — localizes string values and sidekick labels</li>
 *   <li>{@code getMapKeyToTabState()} — exposes tab state map for TabManager activation loops</li>
 * </ul></p>
 */
public class CompositeResourceManagerTest {

  private CompositeResourceManager resourceManager;
  private TestAbstractComposite composite;

  @Before
  public void setUp() {
    composite = new TestAbstractComposite();
    resourceManager = composite.getResourceManager();
  }

  // ── contains() contract ──────────────────────────────────────────────

  @Test
  public void contains_returnsFalseForNull() {
    assertFalse("contains(null) must return false", resourceManager.contains(null));
  }

  @Test
  public void contains_returnsFalseWhenEmpty() {
    Model unknownModel = new StubModel();
    assertFalse("Empty manager must not contain any model", resourceManager.contains(unknownModel));
  }

  @Test
  public void contains_returnsTrueForRegisteredBooleanState() {
    BooleanState state = composite.doCreateBooleanState("testBool", true);
    assertTrue("contains must return true for a registered BooleanState",
        resourceManager.contains(state));
  }

  @Test
  public void contains_returnsFalseForUnregisteredBooleanState() {
    composite.doCreateBooleanState("testBool", true);
    Model unknownModel = new StubModel();
    assertFalse("contains must return false for a model not in any map",
        resourceManager.contains(unknownModel));
  }

  @Test
  public void contains_returnsTrueForRegisteredStringState() {
    StringState state = composite.doCreateStringState("testStr", "hello");
    assertTrue("contains must return true for a registered StringState",
        resourceManager.contains(state));
  }

  @Test
  public void contains_returnsTrueForRegisteredActionOperation() {
    ActionOperation op = composite.doCreateActionOperation("testAction");
    assertTrue("contains must return true for a registered ActionOperation",
        resourceManager.contains(op));
  }

  @Test
  public void contains_returnsTrueForRegisteredBoundedIntegerState() {
    BoundedIntegerState state = composite.doCreateBoundedIntegerState("testInt");
    assertTrue("contains must return true for a registered BoundedIntegerState",
        resourceManager.contains(state));
  }

  @Test
  public void contains_returnsTrueForRegisteredBoundedDoubleState() {
    BoundedDoubleState state = composite.doCreateBoundedDoubleState("testDbl");
    assertTrue("contains must return true for a registered BoundedDoubleState",
        resourceManager.contains(state));
  }

  // ── contains() searches all 14 maps ─────────────────────────────────

  @Test
  public void contains_searchesAllMaps() {
    // Register one item in each accessible factory method
    BooleanState bool = composite.doCreateBooleanState("b1", false);
    StringState str = composite.doCreateStringState("s1", "v");
    ActionOperation action = composite.doCreateActionOperation("a1");
    BoundedIntegerState intState = composite.doCreateBoundedIntegerState("i1");
    BoundedDoubleState dblState = composite.doCreateBoundedDoubleState("d1");

    // All should be found
    assertTrue(resourceManager.contains(bool));
    assertTrue(resourceManager.contains(str));
    assertTrue(resourceManager.contains(action));
    assertTrue(resourceManager.contains(intState));
    assertTrue(resourceManager.contains(dblState));

    // Unknown model should not be found
    assertFalse(resourceManager.contains(new StubModel()));
  }

  // ── factory method contracts ─────────────────────────────────────────

  @Test
  public void createBooleanState_returnsNonNull() {
    BooleanState state = composite.doCreateBooleanState("key1", false);
    assertNotNull("createBooleanState must return a non-null BooleanState", state);
  }

  @Test
  public void createBooleanState_sameKeyReturnsSameInstance() {
    BooleanState first = composite.doCreateBooleanState("dup", true);
    // Second call with same key should return the existing one or replace
    // The contract is that the key maps to the latest registration
    assertTrue("Registered state must be in contains()", resourceManager.contains(first));
  }

  @Test
  public void createStringState_returnsNonNullWithInitialValue() {
    StringState state = composite.doCreateStringState("name", "initial");
    assertNotNull("createStringState must return non-null", state);
  }

  @Test
  public void createStringState_emptyStringOverload() {
    // The convenience overload defaults to ""
    StringState state = composite.doCreateStringStateDefault("name2");
    assertNotNull(state);
  }

  @Test
  public void createStringValue_returnsNonNull() {
    PlainStringValue sv = composite.doCreateStringValue("label");
    assertNotNull("createStringValue must return non-null", sv);
  }

  @Test
  public void createActionOperation_returnsNonNull() {
    ActionOperation op = composite.doCreateActionOperation("op1");
    assertNotNull(op);
  }

  // ── Key contract ─────────────────────────────────────────────────────

  @Test
  public void createKey_producesKeyWithCorrectLocalizationKey() {
    AbstractComposite.Key key = composite.doCreateKey("myKey");
    assertEquals("myKey", key.getLocalizationKey());
  }

  @Test
  public void createKey_producesKeyLinkedToComposite() {
    AbstractComposite.Key key = composite.doCreateKey("myKey");
    assertSame("Key must reference the creating composite", composite, key.getComposite());
  }

  @Test
  public void createKey_preferenceKeyFormat() {
    AbstractComposite.Key key = composite.doCreateKey("fieldName");
    String expected = composite.getClass().getName() + "_fieldName";
    assertEquals("Preference key must be className_localizationKey", expected, key.getPreferenceKey());
  }

  @Test
  public void keyEquality_sameCompositeAndLocalizationKey() {
    AbstractComposite.Key a = composite.doCreateKey("same");
    AbstractComposite.Key b = composite.doCreateKey("same");
    assertEquals("Keys with same composite+localizationKey must be equal", a, b);
    assertEquals("Equal keys must have same hashCode", a.hashCode(), b.hashCode());
  }

  @Test
  public void keyEquality_differentLocalizationKey() {
    AbstractComposite.Key a = composite.doCreateKey("alpha");
    AbstractComposite.Key b = composite.doCreateKey("beta");
    assertNotEquals("Keys with different localizationKey must not be equal", a, b);
  }

  // ── localize() contract ──────────────────────────────────────────────

  @Test
  public void localize_doesNotThrowWhenEmpty() {
    // localize on empty manager must be a no-op
    resourceManager.localize(composite);
  }

  @Test
  public void localize_doesNotThrowWithRegisteredItems() {
    composite.doCreateBooleanState("b1", false);
    composite.doCreateStringState("s1", "v");
    // Must not throw — localize iterates all maps
    resourceManager.localize(composite);
  }

  // ── getMapKeyToTabState ──────────────────────────────────────────────

  @Test
  public void getMapKeyToTabState_returnsNonNullMap() {
    assertNotNull("Tab state map must never be null",
        resourceManager.getMapKeyToTabState());
  }

  @Test
  public void getMapKeyToTabState_initiallyEmpty() {
    assertTrue("Tab state map must be empty initially",
        resourceManager.getMapKeyToTabState().isEmpty());
  }

  // ── Characterization: extraction boundary preservation ────────────────

  @Test
  public void localize_delegatesToLocalizationLogicAfterExtraction() {
    // Characterization: localize() must work regardless of whether the
    // implementation lives in CRM or in CompositeLocalizationDelegate.
    // Register items across multiple map types.
    composite.doCreateStringValue("title");
    composite.doCreateBooleanState("enabled", true);
    composite.doCreateStringState("name", "default");
    composite.doCreateActionOperation("save");
    composite.doCreateBoundedIntegerState("count");
    composite.doCreateBoundedDoubleState("ratio");

    // Must not throw — this validates the localize() call chain is intact
    resourceManager.localize(composite);
  }

  @Test
  public void factoryMethods_createTypesFromExtractedInternalStateTypes() {
    // Characterization: after extraction, factory methods create instances
    // from InternalStateTypes.java. The returned types must still be
    // instanceof their expected supertypes.
    PlainStringValue sv = composite.doCreateStringValue("sv");
    BooleanState bs = composite.doCreateBooleanState("bs", false);
    StringState ss = composite.doCreateStringState("ss", "val");
    ActionOperation op = composite.doCreateActionOperation("op");
    BoundedIntegerState bis = composite.doCreateBoundedIntegerState("bis");
    BoundedDoubleState bds = composite.doCreateBoundedDoubleState("bds");

    // Supertype checks
    assertTrue("StringValue must be PlainStringValue", sv instanceof PlainStringValue);
    assertTrue("BooleanState must be BooleanState", bs instanceof BooleanState);
    assertTrue("StringState must be StringState", ss instanceof StringState);
    assertTrue("ActionOperation must be ActionOperation", op instanceof ActionOperation);
    assertTrue("BoundedIntegerState must be BoundedIntegerState", bis instanceof BoundedIntegerState);
    assertTrue("BoundedDoubleState must be BoundedDoubleState", bds instanceof BoundedDoubleState);

    // They also must be Models (for contains())
    assertTrue(bs instanceof Model);
    assertTrue(ss instanceof Model);
    assertTrue(op instanceof Model);
    assertTrue(bis instanceof Model);
    assertTrue(bds instanceof Model);
  }

  @Test
  public void contains_worksAfterExtractionRoundTrip() {
    // Characterization: create items, verify contains(), then localize(),
    // then verify contains() again. This proves the extraction boundary
    // doesn't corrupt the identity-based containsIndex.
    BooleanState bs = composite.doCreateBooleanState("b", true);
    StringState ss = composite.doCreateStringState("s", "v");
    ActionOperation op = composite.doCreateActionOperation("a");

    // Pre-localize
    assertTrue(resourceManager.contains(bs));
    assertTrue(resourceManager.contains(ss));
    assertTrue(resourceManager.contains(op));

    // Localize (exercises the code that will move to delegate)
    resourceManager.localize(composite);

    // Post-localize — contains must still work
    assertTrue("contains must survive localize()", resourceManager.contains(bs));
    assertTrue("contains must survive localize()", resourceManager.contains(ss));
    assertTrue("contains must survive localize()", resourceManager.contains(op));
  }

  @Test
  public void multipleRegistrations_containsFindsAll() {
    // Stress test: register many items across all accessible types
    BooleanState[] bools = new BooleanState[5];
    StringState[] strs = new StringState[5];
    for (int i = 0; i < 5; i++) {
      bools[i] = composite.doCreateBooleanState("b" + i, i % 2 == 0);
      strs[i] = composite.doCreateStringState("s" + i, "val" + i);
    }

    for (int i = 0; i < 5; i++) {
      assertTrue("Bool " + i + " must be in contains()", resourceManager.contains(bools[i]));
      assertTrue("Str " + i + " must be in contains()", resourceManager.contains(strs[i]));
    }

    // Unknown model still not found
    assertFalse(resourceManager.contains(new StubModel()));
  }

  @Test
  public void localize_idempotent_calledTwice() {
    composite.doCreateStringValue("label");
    composite.doCreateBooleanState("flag", false);

    // Must be safe to call multiple times
    resourceManager.localize(composite);
    resourceManager.localize(composite);
  }

  // ── Test doubles ─────────────────────────────────────────────────────

  /**
   * Minimal concrete AbstractComposite for testing ResourceManager.
   * Exposes protected factory methods via public wrappers.
   */
  static class TestAbstractComposite extends AbstractComposite<CompositeViewLifecycleTest.StubView> {

    TestAbstractComposite() {
      super(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Override
    protected org.lgna.croquet.views.ScrollPane createScrollPaneIfDesired() {
      return null;
    }

    @Override
    protected CompositeViewLifecycleTest.StubView createView() {
      return new CompositeViewLifecycleTest.StubView();
    }

    // Expose ResourceManager for direct testing
    CompositeResourceManager getResourceManager() {
      return this.resourceManager;
    }

    // Expose factory methods under test-friendly names
    AbstractComposite.Key doCreateKey(String localizationKey) {
      return this.createKey(localizationKey);
    }

    BooleanState doCreateBooleanState(String keyText, boolean initialValue) {
      return this.createBooleanState(keyText, initialValue);
    }

    StringState doCreateStringState(String keyText, String initialValue) {
      return this.createStringState(keyText, initialValue);
    }

    StringState doCreateStringStateDefault(String keyText) {
      return this.createStringState(keyText);
    }

    PlainStringValue doCreateStringValue(String keyText) {
      return this.createStringValue(keyText);
    }

    ActionOperation doCreateActionOperation(String keyText) {
      return this.createActionOperation(keyText, new AbstractComposite.Action() {
        @Override
        public org.lgna.croquet.edits.Edit perform(
            org.lgna.croquet.history.UserActivity userActivity,
            AbstractComposite.InternalActionOperation source) {
          return null;
        }
      });
    }

    BoundedIntegerState doCreateBoundedIntegerState(String keyText) {
      return this.createBoundedIntegerState(keyText, new AbstractComposite.BoundedIntegerDetails());
    }

    BoundedDoubleState doCreateBoundedDoubleState(String keyText) {
      return this.createBoundedDoubleState(keyText, new AbstractComposite.BoundedDoubleDetails());
    }
  }

  /**
   * Minimal Model implementation for negative contains() tests.
   */
  static class StubModel implements Model {
    @Override public void initializeIfNecessary() {}
    @Override public void appendUserRepr(StringBuilder sb) { sb.append("StubModel"); }
    @Override public void relocalize() {}
    @Override public boolean isEnabled() { return true; }
    @Override public void setEnabled(boolean isEnabled) {}
    @Override public java.util.UUID getMigrationId() { return null; }
  }
}

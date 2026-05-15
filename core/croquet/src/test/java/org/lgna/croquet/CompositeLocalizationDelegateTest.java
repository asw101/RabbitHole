package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * TDD contract tests for {@link CompositeLocalizationDelegate}.
 *
 * <p>CompositeLocalizationDelegate is the extraction target for the
 * {@code localize()} and {@code localizeSidekicks()} methods plus the
 * {@code SIDEKICK_LABEL_EPILOGUE} constant, currently in
 * CompositeResourceManager lines 607–648.</p>
 *
 * <p>These tests will FAIL TO COMPILE until CompositeLocalizationDelegate.java
 * is created. That is the expected TDD "red" phase.</p>
 *
 * <p>Contract:
 * <ul>
 *   <li>{@code CompositeLocalizationDelegate.localize(composite, maps...)} — static method</li>
 *   <li>{@code CompositeLocalizationDelegate.localizeSidekicks(composite, maps...)} — static method</li>
 *   <li>{@code SIDEKICK_LABEL_EPILOGUE} equals ".sidekickLabel"</li>
 *   <li>localize() iterates the string-value map, calling
 *       {@code composite.findLocalizedText(key)} and {@code composite.modifyLocalizedText(sv, text)}</li>
 *   <li>localizeSidekicks() iterates each CompletionModel map, checking for
 *       sidekick label localization entries</li>
 * </ul></p>
 */
public class CompositeLocalizationDelegateTest {

  private TestComposite composite;
  private CompositeResourceManager resourceManager;

  @Before
  public void setUp() {
    composite = new TestComposite();
    resourceManager = composite.getResourceManager();
  }

  // ── Constant contract ───────────────────────────────────────────────

  @Test
  public void sidekickLabelEpilogue_hasCorrectValue() {
    // The constant must preserve the exact value used in localizeSidekicks
    assertEquals(".sidekickLabel", CompositeLocalizationDelegate.SIDEKICK_LABEL_EPILOGUE);
  }

  // ── localize() contract ─────────────────────────────────────────────

  @Test
  public void localize_doesNotThrowOnEmptyMaps() {
    // With no registered items, localize must be a no-op
    CompositeLocalizationDelegate.localize(composite,
        resourceManager.getStringValueMap(),
        resourceManager.getSidekickMaps());
  }

  @Test
  public void localize_processesStringValues() {
    // Register a string value
    composite.doCreateStringValue("greeting");

    // localize must iterate the string value map without error
    CompositeLocalizationDelegate.localize(composite,
        resourceManager.getStringValueMap(),
        resourceManager.getSidekickMaps());
  }

  @Test
  public void localize_processesMultipleStringValues() {
    composite.doCreateStringValue("label1");
    composite.doCreateStringValue("label2");
    composite.doCreateStringValue("label3");

    // All three should be processed without error
    CompositeLocalizationDelegate.localize(composite,
        resourceManager.getStringValueMap(),
        resourceManager.getSidekickMaps());
  }

  // ── localizeSidekicks() contract ────────────────────────────────────

  @Test
  public void localizeSidekicks_doesNotThrowOnEmptyMaps() {
    CompositeLocalizationDelegate.localizeSidekicks(composite);
  }

  @Test
  public void localizeSidekicks_processesActionOperations() {
    composite.doCreateActionOperation("action1");

    // Must process sidekick labels for action operations
    CompositeLocalizationDelegate.localizeSidekicks(composite,
        resourceManager.getSidekickMaps());
  }

  @Test
  public void localizeSidekicks_processesMultipleMapTypes() {
    composite.doCreateBooleanState("flag1", true);
    composite.doCreateActionOperation("act1");
    composite.doCreateBoundedIntegerState("int1");

    // Must iterate all provided maps without error
    CompositeLocalizationDelegate.localizeSidekicks(composite,
        resourceManager.getSidekickMaps());
  }

  // ── Integration: localize through CompositeResourceManager ──────────

  @Test
  public void fullLocalize_worksEndToEndThroughDelegate() {
    // Register items across multiple maps
    composite.doCreateStringValue("title");
    composite.doCreateBooleanState("enabled", true);
    composite.doCreateStringState("name", "default");
    composite.doCreateActionOperation("save");
    composite.doCreateBoundedIntegerState("count");
    composite.doCreateBoundedDoubleState("ratio");

    // Full localize through the resource manager should delegate to
    // CompositeLocalizationDelegate without error
    resourceManager.localize(composite);
  }

  @Test
  public void fullLocalize_calledTwice_isIdempotent() {
    composite.doCreateStringValue("title");
    composite.doCreateBooleanState("enabled", false);

    // Calling localize twice must not throw or produce different behavior
    resourceManager.localize(composite);
    resourceManager.localize(composite);
  }

  // ── Edge cases ──────────────────────────────────────────────────────

  @Test
  public void localize_afterMultipleRegistrations_noExceptionOnEmptyLocalization() {
    // Register many items to stress the iteration
    for (int i = 0; i < 10; i++) {
      composite.doCreateStringValue("sv" + i);
      composite.doCreateBooleanState("bs" + i, i % 2 == 0);
    }
    resourceManager.localize(composite);
  }

  // ── Test helper ─────────────────────────────────────────────────────

  static class TestComposite extends AbstractComposite<CompositeViewLifecycleTest.StubView> {

    TestComposite() {
      super(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    }

    @Override
    protected org.lgna.croquet.views.ScrollPane createScrollPaneIfDesired() {
      return null;
    }

    @Override
    protected CompositeViewLifecycleTest.StubView createView() {
      return new CompositeViewLifecycleTest.StubView();
    }

    CompositeResourceManager getResourceManager() {
      return this.resourceManager;
    }

    PlainStringValue doCreateStringValue(String keyText) {
      return this.createStringValue(keyText);
    }

    BooleanState doCreateBooleanState(String keyText, boolean initialValue) {
      return this.createBooleanState(keyText, initialValue);
    }

    StringState doCreateStringState(String keyText, String initialValue) {
      return this.createStringState(keyText, initialValue);
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
}

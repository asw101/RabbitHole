/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.lgna.croquet;

import edu.cmu.cs.dennisc.java.util.Maps;
import org.lgna.croquet.codecs.EnumCodec;
import org.lgna.croquet.data.ListData;
import org.lgna.croquet.data.MutableListData;
import org.lgna.croquet.data.RefreshableListData;
import org.lgna.croquet.preferences.PreferenceBooleanState;
import org.lgna.croquet.preferences.PreferenceStringState;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Owns state maps, factory methods, contains(), and localize() logic
 * extracted from {@link AbstractComposite}.
 *
 * <p>Internal state types live in {@link InternalStateTypes} (same package).
 * Localization logic is delegated to {@link CompositeLocalizationDelegate}.</p>
 */
class CompositeResourceManager {

  // ── State maps ──────────────────────────────────────────────────────

  private final Map<AbstractComposite.Key, AbstractComposite.AbstractInternalStringValue> mapKeyToStringValue = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalBooleanState> mapKeyToBooleanState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalPreferenceBooleanState> mapKeyToPreferenceBooleanState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalStringState> mapKeyToStringState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalPreferenceStringState> mapKeyToPreferenceStringState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalSingleSelectListState> mapKeyToSingleSelectListState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalImmutableDataSingleSelectListState> mapKeyToImmutableSingleSelectListState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalRefreshableDataSingleSelectListState> mapKeyToRefreshableSingleSelectListState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalMutableDataSingleSelectListState> mapKeyToMutableSingleSelectListState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalTabState> mapKeyToTabState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalBoundedIntegerState> mapKeyToBoundedIntegerState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalBoundedDoubleState> mapKeyToBoundedDoubleState = Maps.newHashMap();
  private final Map<AbstractComposite.Key, AbstractComposite.InternalActionOperation> mapKeyToActionOperation = Maps.newHashMap();
  private final Map<AbstractComposite.Key, InternalCascadeWithInternalBlank> mapKeyToCascade = Maps.newHashMap();
  private final Map<AbstractComposite.Key, AbstractComposite.InternalCustomItemState> mapKeyToItemState = Maps.newHashMap();

  // O(1) identity-based lookup index for contains()
  private final Set<Object> containsIndex = Collections.newSetFromMap(new IdentityHashMap<>());

  // Cached array of sidekick maps — avoids allocation on every localize() call
  private Map<AbstractComposite.Key, ? extends CompletionModel>[] cachedSidekickMaps;

  // ── Map accessors ───────────────────────────────────────────────────

  Map<AbstractComposite.Key, InternalTabState> getMapKeyToTabState() {
    return this.mapKeyToTabState;
  }

  // ── Registration (for types whose constructors are in AC) ───────────

  void registerStringValue(AbstractComposite.AbstractInternalStringValue stringValue) {
    this.mapKeyToStringValue.put(stringValue.getKey(), stringValue);
  }

  void registerActionOperation(AbstractComposite.Key key, AbstractComposite.InternalActionOperation operation) {
    this.mapKeyToActionOperation.put(key, operation);
    this.containsIndex.add(operation);
  }

  void registerCustomItemState(AbstractComposite.Key key, AbstractComposite.InternalCustomItemState<?> itemState) {
    this.mapKeyToItemState.put(key, itemState);
    this.containsIndex.add(itemState);
  }

  // ── Factory methods ─────────────────────────────────────────────────

  PlainStringValue createStringValue(AbstractComposite.Key key) {
    InternalStringValue rv = new InternalStringValue(key);
    this.mapKeyToStringValue.put(key, rv);
    return rv;
  }

  StringState createStringState(AbstractComposite.Key key, String initialValue) {
    InternalStringState rv = new InternalStringState(initialValue, key);
    this.mapKeyToStringState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  PreferenceStringState createPreferenceStringState(AbstractComposite.Key key, String initialValue, BooleanState isStoringPreferenceDesiredState, UUID encryptionId) {
    InternalPreferenceStringState rv = new InternalPreferenceStringState(initialValue, key, isStoringPreferenceDesiredState, encryptionId);
    this.mapKeyToPreferenceStringState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  BooleanState createBooleanState(AbstractComposite.Key key, boolean initialValue) {
    InternalBooleanState rv = new InternalBooleanState(initialValue, key);
    this.mapKeyToBooleanState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  PreferenceBooleanState createPreferenceBooleanState(AbstractComposite.Key key, boolean initialValue) {
    InternalPreferenceBooleanState rv = new InternalPreferenceBooleanState(initialValue, key);
    this.mapKeyToPreferenceBooleanState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  BoundedIntegerState createBoundedIntegerState(AbstractComposite.Key key, BoundedIntegerState.Details details) {
    InternalBoundedIntegerState rv = new InternalBoundedIntegerState(details, key);
    this.mapKeyToBoundedIntegerState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  BoundedDoubleState createBoundedDoubleState(AbstractComposite.Key key, BoundedDoubleState.Details details) {
    InternalBoundedDoubleState rv = new InternalBoundedDoubleState(details, key);
    this.mapKeyToBoundedDoubleState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  <T> Cascade<T> createCascadeWithInternalBlank(AbstractComposite.Key key, Class<T> cls, AbstractComposite.CascadeCustomizer<T> customizer) {
    InternalCascadeWithInternalBlank<T> rv = new InternalCascadeWithInternalBlank<T>(customizer, cls, key);
    this.mapKeyToCascade.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  <T> SingleSelectListState<T, ListData<T>> createGenericListState(AbstractComposite.Key key, ListData<T> data, int selectionIndex) {
    InternalSingleSelectListState<T> rv = new InternalSingleSelectListState<T>(selectionIndex, data, key);
    this.mapKeyToSingleSelectListState.put(key, rv);
    return rv;
  }

  <T> ImmutableDataSingleSelectListState<T> createImmutableListState(AbstractComposite.Key key, int selectionIndex, ItemCodec<T> codec, T[] values) {
    InternalImmutableDataSingleSelectListState<T> rv = new InternalImmutableDataSingleSelectListState<T>(selectionIndex, codec, values, key);
    this.mapKeyToImmutableSingleSelectListState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  <T extends Enum<T>> ImmutableDataSingleSelectListState<T> createImmutableListStateForEnum(AbstractComposite.Key key, Class<T> valueCls, EnumCodec.LocalizationCustomizer<T> localizationCustomizer, T initialValue) {
    T[] constants = valueCls.getEnumConstants();
    int selectionIndex = initialValue != null ? initialValue.ordinal() : -1;
    EnumCodec<T> enumCodec = localizationCustomizer != null ? EnumCodec.createInstance(valueCls, localizationCustomizer) : EnumCodec.getInstance(valueCls);
    InternalImmutableDataSingleSelectListState<T> rv = new InternalImmutableDataSingleSelectListState<T>(selectionIndex, enumCodec, constants, key);
    this.mapKeyToImmutableSingleSelectListState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  <T> RefreshableDataSingleSelectListState<T> createRefreshableListState(AbstractComposite.Key key, RefreshableListData<T> data, int selectionIndex) {
    InternalRefreshableDataSingleSelectListState<T> rv = new InternalRefreshableDataSingleSelectListState<T>(selectionIndex, data, key);
    this.mapKeyToRefreshableSingleSelectListState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  <T> MutableDataSingleSelectListState<T> createMutableListState(AbstractComposite.Key key, ItemCodec<T> codec, int selectionIndex, T[] values) {
    InternalMutableDataSingleSelectListState<T> rv = new InternalMutableDataSingleSelectListState<T>(selectionIndex, new MutableListData<T>(codec, values), key);
    this.mapKeyToMutableSingleSelectListState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  <C extends SimpleTabComposite<?>> ImmutableDataTabState<C> createImmutableTabState(AbstractComposite.Key key, int selectionIndex, Class<C> cls, C[] tabComposites) {
    InternalTabState<C> rv = new InternalTabState<C>(selectionIndex, cls, tabComposites, key);
    this.mapKeyToTabState.put(key, rv);
    this.containsIndex.add(rv);
    return rv;
  }

  // ── contains() ──────────────────────────────────────────────────────

  boolean contains(Model model) {
    return this.containsIndex.contains(model);
  }

  // ── Map accessors for localization delegate ──────────────────────────

  Map<AbstractComposite.Key, AbstractComposite.AbstractInternalStringValue> getStringValueMap() {
    return this.mapKeyToStringValue;
  }

  @SuppressWarnings("unchecked")
  Map<AbstractComposite.Key, ? extends CompletionModel>[] getSidekickMaps() {
    if (this.cachedSidekickMaps == null) {
      this.cachedSidekickMaps = new Map[] {
          this.mapKeyToActionOperation,
          this.mapKeyToBooleanState,
          this.mapKeyToPreferenceBooleanState,
          this.mapKeyToBoundedDoubleState,
          this.mapKeyToBoundedIntegerState,
          this.mapKeyToCascade,
          this.mapKeyToItemState,
          this.mapKeyToImmutableSingleSelectListState,
          this.mapKeyToRefreshableSingleSelectListState,
          this.mapKeyToMutableSingleSelectListState,
          this.mapKeyToTabState,
          this.mapKeyToPreferenceStringState,
          this.mapKeyToStringState,
      };
    }
    return this.cachedSidekickMaps;
  }

  // ── localize() ──────────────────────────────────────────────────────

  void localize(AbstractComposite<?> composite) {
    CompositeLocalizationDelegate.localize(composite, this.mapKeyToStringValue, this.getSidekickMaps());
  }
}

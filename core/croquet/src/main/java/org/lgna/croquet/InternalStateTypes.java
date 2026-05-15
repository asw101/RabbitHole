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

import org.lgna.croquet.data.ListData;
import org.lgna.croquet.data.MutableListData;
import org.lgna.croquet.data.RefreshableListData;
import org.lgna.croquet.imp.cascade.BlankNode;
import org.lgna.croquet.preferences.PreferenceBooleanState;
import org.lgna.croquet.preferences.PreferenceStringState;

import java.util.List;
import java.util.UUID;

/**
 * Package-private marker class — file anchor for the 13 internal state types
 * extracted from {@link CompositeResourceManager}.
 *
 * <p>Each type was previously a {@code private static final} inner class of
 * CompositeResourceManager. After extraction they are package-private top-level
 * classes in the same package, preserving all behavior.</p>
 */
final class InternalStateTypes {
  private InternalStateTypes() {
    // never instantiated
  }
}

// ── Extracted internal state types ─────────────────────────────────────────

final class InternalStringValue extends AbstractComposite.AbstractInternalStringValue {
  InternalStringValue(AbstractComposite.Key key) {
    super(UUID.fromString("142b66a2-0b95-42d0-8ea4-a22a79c8ff8c"), key);
  }
}

final class InternalStringState extends StringState {
  private final AbstractComposite.Key key;

  InternalStringState(String initialValue, AbstractComposite.Key key) {
    super(Application.INHERIT_GROUP, UUID.fromString("ed65869f-8d26-48b1-8240-cf74ba403a2f"), initialValue);
    this.key = key;
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalPreferenceStringState extends PreferenceStringState {
  private final AbstractComposite.Key key;
  private final BooleanState isStoringPreferenceDesiredState;

  InternalPreferenceStringState(String initialValue, AbstractComposite.Key key, BooleanState isStoringPreferenceDesiredState, UUID encryptionId) {
    super(Application.INHERIT_GROUP, UUID.fromString("ad98acf0-db41-43a6-8619-269a7d8cbc2d"), initialValue, key.getPreferenceKey(), getEncryptionKey(encryptionId != null ? encryptionId.toString() : null));
    this.key = key;
    this.isStoringPreferenceDesiredState = isStoringPreferenceDesiredState;
  }

  @Override
  protected boolean isStoringPreferenceDesired() {
    return (this.isStoringPreferenceDesiredState == null) || this.isStoringPreferenceDesiredState.getValue();
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalBooleanState extends BooleanState {
  private final AbstractComposite.Key key;

  InternalBooleanState(boolean initialValue, AbstractComposite.Key key) {
    super(Application.INHERIT_GROUP, UUID.fromString("5053e40f-9561-41c8-835d-069bd106723c"), initialValue);
    this.key = key;
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalPreferenceBooleanState extends PreferenceBooleanState {
  private final AbstractComposite.Key key;

  InternalPreferenceBooleanState(boolean initialValue, AbstractComposite.Key key) {
    super(Application.INHERIT_GROUP, UUID.fromString("034f99f7-74ec-4a89-8396-3c187d9684a2"), initialValue, key.getPreferenceKey());
    this.key = key;
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalSingleSelectListState<T> extends SingleSelectListState<T, ListData<T>> {
  private final AbstractComposite.Key key;

  InternalSingleSelectListState(int selectionIndex, ListData<T> data, AbstractComposite.Key key) {
    super(Application.INHERIT_GROUP, UUID.fromString("4f0640c9-eceb-4801-a8bb-bf8e282cef0f"), selectionIndex, data);
    this.key = key;
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalImmutableDataSingleSelectListState<T> extends ImmutableDataSingleSelectListState<T> {
  private final AbstractComposite.Key key;

  InternalImmutableDataSingleSelectListState(int selectionIndex, ItemCodec<T> codec, T[] values, AbstractComposite.Key key) {
    super(Application.INHERIT_GROUP, UUID.fromString("091d5251-d278-4eb1-8214-a27c154f5378"), selectionIndex, codec, values);
    this.key = key;
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalRefreshableDataSingleSelectListState<T> extends RefreshableDataSingleSelectListState<T> {
  private final AbstractComposite.Key key;

  InternalRefreshableDataSingleSelectListState(int selectionIndex, RefreshableListData<T> data, AbstractComposite.Key key) {
    super(Application.INHERIT_GROUP, UUID.fromString("4d7ef91c-a8ae-4b17-9d8a-91ffac4ba12e"), selectionIndex, data);
    this.key = key;
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalMutableDataSingleSelectListState<T> extends MutableDataSingleSelectListState<T> {
  private final AbstractComposite.Key key;

  InternalMutableDataSingleSelectListState(int selectionIndex, MutableListData<T> data, AbstractComposite.Key key) {
    super(Application.INHERIT_GROUP, UUID.fromString("6cc16988-0fc8-476b-9026-b19fd15748ea"), selectionIndex, data);
    this.key = key;
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalTabState<T extends SimpleTabComposite<?>> extends SimpleTabState<T> {
  private final AbstractComposite.Key key;

  InternalTabState(int selectionIndex, Class<T> cls, T[] values, AbstractComposite.Key key) {
    super(Application.INHERIT_GROUP, UUID.fromString("bea99c2f-45ad-40a8-a99c-9c125a72f0be"), selectionIndex, cls, values);
    this.key = key;
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalBoundedIntegerState extends BoundedIntegerState {
  private final AbstractComposite.Key key;

  InternalBoundedIntegerState(BoundedIntegerState.Details details, AbstractComposite.Key key) {
    super(details);
    this.key = key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalBoundedDoubleState extends BoundedDoubleState {
  private final AbstractComposite.Key key;

  InternalBoundedDoubleState(BoundedDoubleState.Details details, AbstractComposite.Key key) {
    super(details);
    this.key = key;
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

final class InternalCascadeWithInternalBlank<T> extends CascadeWithInternalBlank<T> {
  private final AbstractComposite.CascadeCustomizer<T> customizer;
  private final AbstractComposite.Key key;

  InternalCascadeWithInternalBlank(AbstractComposite.CascadeCustomizer<T> customizer, Class<T> componentType, AbstractComposite.Key key) {
    super(Application.INHERIT_GROUP, UUID.fromString("165e65a4-fd9b-4a09-921d-ecc3cc808de0"), componentType);
    this.customizer = customizer;
    this.key = key;
  }

  public AbstractComposite.Key getKey() {
    return this.key;
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return this.key.getComposite().getClass();
  }

  @Override
  protected String getSubKeyForLocalization() {
    return this.key.getLocalizationKey();
  }

  @Override
  protected org.lgna.croquet.edits.Edit createEdit(org.lgna.croquet.history.UserActivity userActivity, T[] values) {
    return this.customizer.createEdit(values);
  }

  @Override
  protected List<CascadeBlankChild> updateBlankChildren(List<CascadeBlankChild> rv, BlankNode<T> blankNode) {
    this.customizer.appendBlankChildren(rv, blankNode);
    return rv;
  }

  @Override
  protected void appendRepr(StringBuilder sb) {
    super.appendRepr(sb);
    sb.append(";key=");
    sb.append(this.key);
  }
}

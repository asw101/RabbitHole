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

import edu.cmu.cs.dennisc.java.awt.datatransfer.ClipboardUtilities;
import edu.cmu.cs.dennisc.java.util.logging.Logger;

import java.util.Map;

/**
 * Static utility delegate for composite localization logic extracted from
 * {@link CompositeResourceManager}.
 *
 * <p>Receives maps as parameters at call time rather than storing references,
 * keeping all mutable state in CompositeResourceManager.</p>
 */
final class CompositeLocalizationDelegate {

  static final String SIDEKICK_LABEL_EPILOGUE = ".sidekickLabel";

  private CompositeLocalizationDelegate() {
    // static utility — never instantiated
  }

  /**
   * Localizes all string values and sidekick labels for the given composite.
   *
   * @param composite        the composite whose localization resources to apply
   * @param stringValueMap   the key→string-value map to localize
   * @param sidekickMaps     all completion-model maps whose sidekick labels to localize
   */
  @SafeVarargs
  @SuppressWarnings("unchecked")
  static void localize(AbstractComposite<?> composite,
      Map<AbstractComposite.Key, AbstractComposite.AbstractInternalStringValue> stringValueMap,
      Map<AbstractComposite.Key, ? extends CompletionModel>... sidekickMaps) {
    for (Map.Entry<AbstractComposite.Key, AbstractComposite.AbstractInternalStringValue> entry : stringValueMap.entrySet()) {
      AbstractComposite.AbstractInternalStringValue stringValue = entry.getValue();
      stringValue.setText(composite.modifyLocalizedText(stringValue, composite.findLocalizedText(entry.getKey().getLocalizationKey())));
    }
    localizeSidekicks(composite, sidekickMaps);
  }

  /**
   * Localizes sidekick labels for all completion models in the given maps.
   *
   * @param composite the composite whose localization resources to apply
   * @param maps      completion-model maps whose sidekick labels to localize
   */
  @SafeVarargs
  static void localizeSidekicks(AbstractComposite<?> composite, Map<AbstractComposite.Key, ? extends CompletionModel>... maps) {
    for (Map<AbstractComposite.Key, ? extends CompletionModel> map : maps) {
      for (Map.Entry<AbstractComposite.Key, ? extends CompletionModel> entry : map.entrySet()) {
        AbstractComposite.Key key = entry.getKey();
        CompletionModel model = entry.getValue();
        String text = composite.findLocalizedText(key.getLocalizationKey() + SIDEKICK_LABEL_EPILOGUE);
        if (text != null) {
          StringValue sidekickLabel = model.getSidekickLabel();
          text = composite.modifyLocalizedText(sidekickLabel, text);
          sidekickLabel.setText(text);
        } else {
          if (model.hasSidekickLabel()) {
            Class<?> cls = composite.getClassUsedForLocalization();
            String localizationKey = cls.getSimpleName() + "." + key.getLocalizationKey() + SIDEKICK_LABEL_EPILOGUE;
            Logger.errln();
            Logger.errln("WARNING: could not find localization for sidekick label");
            Logger.errln("looking for:");
            Logger.errln();
            Logger.errln("   ", localizationKey);
            Logger.errln();
            Logger.errln("in croquet.properties file in package:", cls.getPackage().getName());
            Logger.errln();
            Logger.errln(localizationKey, "has been copied to the clipboard for your convenience.");
            Logger.errln("if this does not solve your problem please feel free to ask dennis for help.");
            ClipboardUtilities.setClipboardContents(localizationKey);
          }
        }
      }
    }
  }
}

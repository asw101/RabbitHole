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
package org.lgna.project.ast;

import edu.cmu.cs.dennisc.java.util.ResourceBundleUtilities;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Package-private delegate for block comment formatting and localized
 * comment resolution in {@link JavaCodeGenerator}.
 *
 * Extracted from JavaCodeGenerator to reduce file size while preserving
 * identical comment-generation behaviour.
 */
class JavaCommentFormatter {

  private final String commentsLocalizationBundleName;

  JavaCommentFormatter(String commentsLocalizationBundleName) {
    this.commentsLocalizationBundleName = commentsLocalizationBundleName;
  }

  String formatBlockComment(String commentText) {
    String[] commentLines = SourceCodeGenerator.splitIntoLines(commentText);
    StringBuilder sb = new StringBuilder();

    sb.append("/* ");
    for (int i = 0; i < commentLines.length; i++) {
      sb.append(commentLines[i]);
      if (i < (commentLines.length - 1)) {
        sb.append("\n * ");
      }
    }
    sb.append(" */");
    return sb.toString();
  }

  String getLocalizedComment(AbstractType<?, ?, ?> type, String itemName, Locale locale) {
    if (commentsLocalizationBundleName != null) {
      ResourceBundle resourceBundle = ResourceBundleUtilities.getUtf8Bundle(commentsLocalizationBundleName, locale);
      String key;
      AbstractType<?, ?, ?> t = type;
      boolean done = false;
      String returnVal = null;
      do {
        if (t != null) {
          key = t.getName() + "." + itemName;
          t = t.getSuperType();
        } else {
          key = itemName;
          done = true;
        }
        try {
          returnVal = resourceBundle.getString(key);
          break;
        } catch (RuntimeException re) {
          //pass;
        }
      } while (!done);
      if (returnVal != null) {
        returnVal = returnVal.replaceAll("<classname>", type.getName());
        returnVal = returnVal.replaceAll("<objectname>", itemName);
      }
      return returnVal;
    }
    return null;
  }

  String getLocalizedMultiLineComment(AbstractType<?, ?, ?> type, String sectionName) {
    String comment = getLocalizedComment(type, sectionName, Locale.getDefault());
    if (comment != null) {
      comment = formatBlockComment(comment);
    }
    return comment;
  }

}

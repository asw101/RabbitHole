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
package org.alice.ide;

import edu.cmu.cs.dennisc.java.util.Sets;
import edu.cmu.cs.dennisc.javax.swing.option.Dialogs;
import edu.cmu.cs.dennisc.pattern.IsInstanceCrawler;
import org.lgna.project.Project;
import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.CrawlPolicy;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.StatementListProperty;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import java.util.List;
import java.util.Set;

/**
 * Delegate that owns scene-setup code generation and field reorganization
 * logic, extracted from {@link IDE} to reduce its size.
 */
final class SceneSetupManager {

  private final IDE ide;

  SceneSetupManager(IDE ide) {
    this.ide = ide;
  }

  void generateCodeForSceneSetUp() {
    UserMethod userMethod = ide.getPerformEditorGeneratedSetUpMethod();
    StatementListProperty bodyStatementsProperty = userMethod.body.getValue().statements;
    bodyStatementsProperty.clear();
    String innerComment = ide.getInnerCommentForMethodName(userMethod.getName());
    if (innerComment != null) {
      bodyStatementsProperty.add(new Comment(innerComment));
    }
    ide.getSceneEditor().generateCodeForSetUp(bodyStatementsProperty);
  }

  void reorganizeFieldsIfNecessary() {
    Project project = ide.getProject();
    if (project != null) {
      for (NamedUserType namedUserType : project.getNamedUserTypes()) {
        Set<UserField> alreadyMovedFields = Sets.newHashSet();
        String message = reorganizeTypeFieldsIfNecessary(namedUserType, 0, alreadyMovedFields);
        if (message != null) {
          //TODO I18n
          Dialogs.showError("Unable to Recover", message);
        }
      }
    }
  }

  String reorganizeTypeFieldsIfNecessary(NamedUserType namedUserType, int startIndex, Set<UserField> alreadyMovedFields) {
    List<UserField> fields = namedUserType.fields.getValue().subList(startIndex, namedUserType.fields.size());
    Set<UserField> unacceptableFields = Sets.newHashSet(fields);
    UserField fieldToMoveToTheEnd = null;
    List<FieldAccess> accessesForFieldToMoveToTheEnd = null;
    for (UserField field : fields) {
      Expression initializer = field.initializer.getValue();
      UnacceptableFieldAccessCrawler crawler = new UnacceptableFieldAccessCrawler(unacceptableFields);
      initializer.crawl(crawler, CrawlPolicy.EXCLUDE_REFERENCES_ENTIRELY);
      List<FieldAccess> fieldAccesses = crawler.getList();
      if (!fieldAccesses.isEmpty()) {
        fieldToMoveToTheEnd = field;
        accessesForFieldToMoveToTheEnd = fieldAccesses;
        break;
      }
      unacceptableFields.remove(field);
    }
    if (fieldToMoveToTheEnd != null) {
      if (alreadyMovedFields.contains(fieldToMoveToTheEnd)) {
        //todo: better cycle detection?
        StringBuilder sb = new StringBuilder();
        // TODO I18n
        sb.append("<html>Possible cycle detected.<br>The field <strong>\"");
        sb.append(fieldToMoveToTheEnd.getName());
        sb.append("\"</strong> on type <strong>\"");
        sb.append(fieldToMoveToTheEnd.getDeclaringType().getName());
        sb.append("\"</strong> is referencing: ");
        String prefix = "<strong>\"";
        for (FieldAccess fieldAccess : accessesForFieldToMoveToTheEnd) {
          AbstractField accessedField = fieldAccess.field.getValue();
          sb.append(prefix);
          sb.append(accessedField.getName());
          prefix = "\"</strong>, <strong>\"";
        }
        sb.append("\"</strong><br>");
        sb.append(ide.getApplicationName());
        sb.append(" already attempted to move it once.");
        sb.append("<br><br><strong>Your program may fail.</strong></html>");
        return sb.toString();
      } else {
        for (FieldAccess fieldAccess : accessesForFieldToMoveToTheEnd) {
          AbstractField accessedField = fieldAccess.field.getValue();
          if (accessedField == fieldToMoveToTheEnd) {
            StringBuilder sb = new StringBuilder();
            // TODO I18n
            sb.append("<html>The field <strong>\"");
            sb.append(fieldToMoveToTheEnd.getName());
            sb.append("\"</strong> on type <strong>\"");
            sb.append(fieldToMoveToTheEnd.getDeclaringType().getName());
            sb.append("\"</strong> is referencing <strong>itself</strong>.");
            sb.append("<br><br><strong>Your program may fail.</strong></html>");
            return sb.toString();
          }
        }
        int prevIndex = namedUserType.fields.indexOf(fieldToMoveToTheEnd);
        int nextIndex = namedUserType.fields.size() - 1;
        namedUserType.fields.slide(prevIndex, nextIndex);
        alreadyMovedFields.add(fieldToMoveToTheEnd);
        return reorganizeTypeFieldsIfNecessary(namedUserType, prevIndex, alreadyMovedFields);
      }
    } else {
      return null;
    }
  }

  private static class UnacceptableFieldAccessCrawler extends IsInstanceCrawler<FieldAccess> {
    private final Set<UserField> unacceptableFields;

    public UnacceptableFieldAccessCrawler(Set<UserField> unacceptableFields) {
      super(FieldAccess.class);
      this.unacceptableFields = unacceptableFields;
    }

    @Override
    protected boolean isAcceptable(FieldAccess fieldAccess) {
      return this.unacceptableFields.contains(fieldAccess.field.getValue());
    }
  }
}

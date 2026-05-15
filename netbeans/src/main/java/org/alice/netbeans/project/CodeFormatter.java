/*******************************************************************************
 * Copyright (c) 2006, 2016, Carnegie Mellon University. All rights reserved.
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

package org.alice.netbeans.project;

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.io.IOException;
import java.util.List;

/**
 * Formats generated Java source files using the NetBeans editor infrastructure.
 * Extracted from {@link ProjectCodeGenerator} to reduce file size.
 */
final class CodeFormatter {

  static void formatGeneratedFiles(List<FileObject> fileObjectsToFormat, ProgressHandle progressHandle) {
    if (progressHandle != null) {
      progressHandle.switchToDeterminate(fileObjectsToFormat.size());
    }
    int formatWorkUnit = 0;
    for (FileObject fileObjectToFormat : fileObjectsToFormat) {
      try {
        DataObject dobj = DataObject.find(fileObjectToFormat);
        EditorCookie ec = dobj.getCookie(EditorCookie.class);
        final StyledDocument doc = ec.openDocument();
        final Reformat rf = Reformat.get(doc);
        rf.lock();
        try {
          NbDocument.runAtomicAsUser(doc, new Runnable() {
            @Override
            public void run() {
              try {
                rf.reformat(0, doc.getLength());
              } catch (BadLocationException ble) {
                Logger.throwable(ble);
              }
            }
          });
        } finally {
          rf.unlock();
        }
        ec.saveDocument();
        ProjectCodeGenerator.progress(progressHandle, "format: ", fileObjectToFormat, formatWorkUnit);
      } catch (BadLocationException ble) {
        Logger.throwable(ble);
      } catch (IOException ioe) {
        Logger.throwable(ioe);
      }
      formatWorkUnit++;
    }
  }

  private CodeFormatter() {
  }
}

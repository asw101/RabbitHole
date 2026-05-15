/*******************************************************************************
 * Copyright (c) 2019 Carnegie Mellon University. All rights reserved.
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
package org.alice.ide.croquet.models.html;

import edu.cmu.cs.dennisc.java.awt.ComponentUtilities;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGIDGenerator;
import org.lgna.croquet.views.SwingComponentView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.*;

/**
 * Manages SVG rendering lifecycle for HTML export.
 * Extracted from HtmlEncoder to isolate SVG-specific state and logic.
 */
class SvgEncoder {

  private final Document document;
  private SVGGraphics2D activeSVG = null;
  private SVGIDGenerator firstIDGenerator = null;

  SvgEncoder(Document document) {
    this.document = document;
  }

  SVGGraphics2D getActiveSVG() {
    return activeSVG;
  }

  void pushSvg(org.w3c.dom.Node parentNode, Runnable content) {
    if (activeSVG != null) {
      throw new RuntimeException("Attempted to create an SVG inside another SVG.");
    }
    activeSVG = new SVGGraphics2D(document);
    activeSVG.setSVGCanvasSize(new Dimension(0, 0));
    useCommonIdGenerator();

    content.run();

    final Element svgRoot = activeSVG.getRoot();
    svgRoot.setAttribute("class", "alice-generated-svg");
    parentNode.appendChild(svgRoot);
    activeSVG = null;
  }

  private void useCommonIdGenerator() {
    if (firstIDGenerator == null) {
      firstIDGenerator = activeSVG.getGeneratorContext().getIDGenerator();
    } else {
      activeSVG.getGeneratorContext().setIDGenerator(firstIDGenerator);
    }
  }

  void addToSvg(SwingComponentView<?> view) {
    if (activeSVG == null) {
      throw new RuntimeException("Attempted to add to SVG before creating it.");
    }
    JComponent awt = view.getAwtComponent();
    ComponentUtilities.doLayoutTree(awt);
    ComponentUtilities.setSizeToPreferredSizeTree(awt);
    Dimension addedSize = awt.getPreferredSize();
    Dimension svgSize = activeSVG.getSVGCanvasSize();
    activeSVG.setSVGCanvasSize(new Dimension(
        Math.max(addedSize.width, svgSize.width),
        Math.max(addedSize.height, svgSize.height)));
    awt.paint(activeSVG);
  }
}

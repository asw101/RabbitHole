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

package org.alice.stageide;

import edu.cmu.cs.dennisc.java.util.Maps;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.story.*;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

/**
 * Type ordering comparator extracted from StoryApiConfigurationManager.
 * Assigns numeric priority to well-known Alice story types so declaration
 * dialogs present them in a stable, user-friendly order.
 */
enum StoryTypeComparator implements Comparator<AbstractType<?, ?, ?>> {
  SINGLETON;
  private static final double DEFAULT_VALUE = 50.0;
  private final Map<AbstractType<?, ?, ?>, Double> mapTypeToValue = Maps.newHashMap();

  StoryTypeComparator() {
    mapTypeToValue.put(JavaType.BOOLEAN_OBJECT_TYPE, 1.1);
    mapTypeToValue.put(JavaType.DOUBLE_OBJECT_TYPE, 1.2);
    mapTypeToValue.put(JavaType.INTEGER_OBJECT_TYPE, 1.3);
    mapTypeToValue.put(JavaType.STRING_TYPE, 1.4);

    mapTypeToValue.put(JavaType.getInstance(SThing.class), 10.1);

    mapTypeToValue.put(JavaType.getInstance(Color.class), 20.1);
    mapTypeToValue.put(JavaType.getInstance(Paint.class), 20.2);

    mapTypeToValue.put(JavaType.getInstance(Position.class), 30.1);
    mapTypeToValue.put(JavaType.getInstance(Orientation.class), 30.2);
    mapTypeToValue.put(JavaType.getInstance(VantagePoint.class), 30.3);

    mapTypeToValue.put(JavaType.getInstance(SJoint.class), 99.9);
  }

  private double getValue(AbstractType<?, ?, ?> type) {
    Double value = mapTypeToValue.get(type);
    return Objects.requireNonNullElse(value, DEFAULT_VALUE);
  }

  @Override
  public int compare(AbstractType<?, ?, ?> typeA, AbstractType<?, ?, ?> typeB) {
    double valueA = getValue(typeA);
    double valueB = getValue(typeB);
    if (valueA == valueB) {
      return typeA.getName().compareTo(typeB.getName());
    } else {
      return Double.compare(valueA, valueB);
    }
  }
}

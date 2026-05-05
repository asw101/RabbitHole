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
package org.lgna.project.migration;

final class ProjectMigrationTextSnippets {
  private static final String PATTERN_WHITESPACE = "\\s*";
  private static final String REPLACEMENT_WHITESPACE = " ";

  private static String createMoreSpecificFieldString(String fieldName, String clsName, String whitespace) {
    StringBuilder sb = new StringBuilder();
    sb.append("name=\"");
    sb.append(fieldName);
    sb.append("\">");
    sb.append(whitespace);
    sb.append("<declaringClass name=\"");
    sb.append(clsName);
    sb.append("\"");
    return sb.toString();
  }

  static String createMoreSpecificFieldPattern(String fieldName, String clsName) {
    return createMoreSpecificFieldString(fieldName, clsName, PATTERN_WHITESPACE);
  }

  static String createMoreSpecificFieldReplacement(String fieldName, String clsName) {
    return createMoreSpecificFieldString(fieldName, clsName, REPLACEMENT_WHITESPACE);
  }

  private static String createPrevJointString(String prevFieldName, String packageSubName) {
    StringBuilder sb = new StringBuilder();
    sb.append("name=\"");
    sb.append(prevFieldName);
    sb.append("\">");
    sb.append(PATTERN_WHITESPACE);
    sb.append("<declaringClass name=\"org\\.lgna\\.story\\.resources.");
    sb.append(packageSubName);
    sb.append("\\.[A-Za-z]*\"");
    return sb.toString();
  }

  private static String createNextJointString(String prevFieldName, String clsName) {
    StringBuilder sb = new StringBuilder();
    sb.append("name=\"");
    sb.append(prevFieldName);
    sb.append("\">");
    sb.append(REPLACEMENT_WHITESPACE);
    sb.append("<declaringClass name=\"org.lgna.story.resources.");
    sb.append(clsName);
    sb.append("\"");
    return sb.toString();
  }

  static String createPrevBipedJointString(String prevFieldName) {
    return createPrevJointString(prevFieldName, "biped");
  }

  static String createNextBipedJointString(String prevFieldName) {
    return createNextJointString(prevFieldName, "BipedResource");
  }

  static String createPrevQuadrupedJointString(String prevFieldName) {
    return createPrevJointString(prevFieldName, "quadruped");
  }

  static String createNextQuadrupedJointString(String prevFieldName) {
    return createNextJointString(prevFieldName, "QuadrupedResource");
  }

  static String createPrevFlyerJointString(String prevFieldName) {
    return createPrevJointString(prevFieldName, "flyer");
  }

  static String createNextFlyerJointString(String prevFieldName) {
    return createNextJointString(prevFieldName, "FlyerResource");
  }

  static String createPrevSwimmerJointString(String prevFieldName) {
    return createPrevJointString(prevFieldName, "swimmer");
  }

  static String createNextSwimmerJointString(String prevFieldName) {
    return createNextJointString(prevFieldName, "SwimmerResource");
  }

  private static String createJointAccessorString(String accessorName, String clsName, String whitespace) {
    StringBuilder sb = new StringBuilder();
    sb.append("name=\"");
    sb.append(accessorName);
    sb.append("\">");
    sb.append(whitespace);
    sb.append("<declaringClass name=\"org.lgna.story.");
    sb.append(clsName);
    sb.append("\"");
    return sb.toString();
  }

  static String createJointAccessorPattern(String accessorName, String clsName) {
    return createJointAccessorString(accessorName, clsName, PATTERN_WHITESPACE);
  }

  static String createJointAccessorReplacement(String accessorName, String clsName) {
    return createJointAccessorString(accessorName, clsName, REPLACEMENT_WHITESPACE);
  }

  private static String createJointIdString(String fieldName, String subPackageAndClassName, String whitespace) {
    StringBuilder sb = new StringBuilder();
    sb.append("name=\"");
    sb.append(fieldName);
    sb.append("\">");
    sb.append(PATTERN_WHITESPACE);
    sb.append("<declaringClass name=\"org.lgna.story.resources.");
    sb.append(subPackageAndClassName);
    sb.append("\"");
    return sb.toString();
  }

  private static String CACHE_FROM_PREVIOUS_CALL_subPackageAndClassName;

  static String createJointIdPattern(String prevFieldName, String subPackageAndClassName) {
    CACHE_FROM_PREVIOUS_CALL_subPackageAndClassName = subPackageAndClassName;
    return createJointIdString(prevFieldName, subPackageAndClassName, PATTERN_WHITESPACE);
  }

  static String createJointIdReplacement(String nextFieldName) {
    assert CACHE_FROM_PREVIOUS_CALL_subPackageAndClassName != null : nextFieldName;
    try {
      return createJointIdString(nextFieldName, CACHE_FROM_PREVIOUS_CALL_subPackageAndClassName, PATTERN_WHITESPACE);
    } finally {
      CACHE_FROM_PREVIOUS_CALL_subPackageAndClassName = null;
    }
  }

  private ProjectMigrationTextSnippets() {
  }
}

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

import edu.cmu.cs.dennisc.java.util.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Package-private delegate that tracks and builds Java import statements
 * for {@link JavaCodeGenerator}.
 */
class ImportCollector {

  private final Set<JavaPackage> packagesToImportOnDemand = Sets.newHashSet();
  private final Set<JavaType> typesToImport = Sets.newHashSet();
  private final Set<JavaMethod> methodsToImportStatic = Sets.newHashSet();

  private final List<JavaPackage> packagesMarkedForOnDemandImport;
  private final List<JavaMethod> staticMethodsMarkedForImport;

  ImportCollector(List<JavaPackage> packagesMarkedForOnDemandImport,
                  List<JavaMethod> staticMethodsMarkedForImport) {
    this.packagesMarkedForOnDemandImport = Collections.unmodifiableList(packagesMarkedForOnDemandImport);
    this.staticMethodsMarkedForImport = Collections.unmodifiableList(staticMethodsMarkedForImport);
  }

  void trackType(JavaType javaType) {
    if (!javaType.isPrimitive()) {
      JavaPackage javaPackage = javaType.getPackage();
      if (javaPackage != null) {
        JavaType enclosingType = javaType.getEnclosingType();
        if (enclosingType != null || !packagesMarkedForOnDemandImport.contains(javaPackage)) {
          typesToImport.add(javaType);
        } else {
          packagesToImportOnDemand.add(javaPackage);
        }
      }
    }
  }

  boolean isStaticMethodImported(AbstractMethod method) {
    return staticMethodsMarkedForImport.contains(method);
  }

  void trackStaticMethodImport(JavaMethod method) {
    methodsToImportStatic.add(method);
  }

  StringBuilder buildImports(String prefix, String postfix) {
    StringBuilder sb = new StringBuilder();
    sb.append(prefix);
    for (JavaPackage packageToImportOnDemand : packagesToImportOnDemand) {
      sb.append("import ");
      sb.append(packageToImportOnDemand.getName());
      sb.append(".*;");
    }
    for (JavaType typeToImport : typesToImport) {
      JavaPackage pack = typeToImport.getPackage();
      if (!"java.lang".contentEquals(pack.getName())) {
        sb.append("import ");
        sb.append(typeToImport.getPackage().getName());
        sb.append('.');
        JavaType enclosingType = typeToImport.getEnclosingType();
        if (enclosingType != null) {
          sb.append(enclosingType.getName());
          sb.append('.');
        }
        sb.append(typeToImport.getName());
        sb.append(';');
      }
    }
    for (JavaMethod methodToImportStatic : methodsToImportStatic) {
      sb.append("import static ");
      sb.append(methodToImportStatic.getDeclaringType().getPackage().getName());
      sb.append('.');
      sb.append(methodToImportStatic.getDeclaringType().getName());
      sb.append('.');
      sb.append(methodToImportStatic.getName());
      sb.append(';');
    }
    sb.append(postfix);
    return sb;
  }
}

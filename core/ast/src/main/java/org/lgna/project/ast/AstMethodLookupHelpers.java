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

import edu.cmu.cs.dennisc.java.util.Lists;
import edu.cmu.cs.dennisc.java.util.Sets;
import edu.cmu.cs.dennisc.pattern.IsInstanceCrawler;

import java.util.List;
import java.util.Set;

/**
 * Method lookup and traversal helpers extracted from AstUtilities.
 *
 * @author Dennis Cosgrove
 */
public final class AstMethodLookupHelpers {
  private AstMethodLookupHelpers() {
    throw new AssertionError();
  }

  public static JavaMethod lookupMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
    return JavaMethod.getInstance(cls, methodName, parameterTypes);
  }

  public static <M extends AbstractMethod> M getSingleAbstractMethod(AbstractType<?, M, ?> type) {
    List<M> methods = type.getDeclaredMethods();
    assert methods.size() == 1 : type;
    M singleAbstractMethod = methods.getFirst();
    assert singleAbstractMethod.isAbstract() : singleAbstractMethod;
    return singleAbstractMethod;
  }

  private static AbstractType<?, ?, ?>[] getParameterTypes(AbstractMethod method) {
    AbstractParameter[] parameters = method.getAllParameters();
    AbstractType<?, ?, ?>[] rv = new AbstractType<?, ?, ?>[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      rv[i] = parameters[i].getValueType();
    }
    return rv;
  }

  private static AbstractMethod getOverridenMethod(AbstractType<?, ?, ?> type, String methodName, AbstractType<?, ?, ?>[] parameterTypes) {
    if (type != null) {
      AbstractMethod rv = type.getDeclaredMethod(methodName, parameterTypes);
      if (rv != null) {
        return rv;
      } else {
        return getOverridenMethod(type.getSuperType(), methodName, parameterTypes);
      }
    } else {
      return null;
    }
  }

  public static AbstractMethod getOverridenMethod(AbstractMethod method) {
    AbstractType<?, ?, ?> type = method.getDeclaringType();
    return getOverridenMethod(type.getSuperType(), method.getName(), getParameterTypes(method));
  }

  private static void addInvokedMethods(Set<UserMethod> set, UserMethod from) {
    IsInstanceCrawler<MethodInvocation> crawler = new IsInstanceCrawler<MethodInvocation>(MethodInvocation.class) {
      @Override
      protected boolean isAcceptable(MethodInvocation methodInvocation) {
        return true;
      }
    };
    from.body.getValue().crawl(crawler, CrawlPolicy.EXCLUDE_REFERENCES_ENTIRELY);
    for (MethodInvocation methodInvocation : crawler.getList()) {
      AbstractMethod m = methodInvocation.method.getValue();
      if (m instanceof UserMethod userMethod) {
        if (!set.contains(userMethod)) {
          set.add(userMethod);
          addInvokedMethods(set, userMethod);
        }
      }
    }
  }

  public static Set<UserMethod> getAllInvokedMethods(UserMethod seed) {
    Set<UserMethod> set = Sets.newHashSet();
    addInvokedMethods(set, seed);
    return set;
  }

  private static void updateAllMethods(List<AbstractMethod> allMethods, AbstractType<?, ?, ?> type) {
    allMethods.addAll(type.getDeclaredMethods());
    AbstractType<?, ?, ?> superType = type.getSuperType();
    if (superType != null) {
      updateAllMethods(allMethods, superType);
    }
  }

  public static List<AbstractMethod> getAllMethods(AbstractType<?, ?, ?> type) {
    List<AbstractMethod> rv = Lists.newLinkedList();
    updateAllMethods(rv, type);
    return rv;
  }
}

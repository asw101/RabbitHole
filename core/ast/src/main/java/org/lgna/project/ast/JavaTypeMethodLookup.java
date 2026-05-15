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
import edu.cmu.cs.dennisc.property.PropertyUtilities;
import org.lgna.project.annotations.GetterTemplate;
import org.lgna.project.annotations.MethodTemplate;
import org.lgna.project.annotations.ValueTemplate;
import org.lgna.project.annotations.Visibility;
import org.lgna.project.reflect.ClassInfoManager;
import org.lgna.project.reflect.MethodInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extracted from JavaType — builds the declared-method list for a Java class,
 * including @MethodTemplate chain linking and setter value-template wiring.
 *
 * @author Dennis Cosgrove
 */
final class JavaTypeMethodLookup {

  private JavaTypeMethodLookup() {
    throw new AssertionError("non-instantiable");
  }

  private static Class<?>[] trimLast(Class<?>[] src) {
    Class<?>[] rv = new Class<?>[src.length - 1];
    System.arraycopy(src, 0, rv, 0, rv.length);
    return rv;
  }

  private static Method getNextShorterInChain(Method src) {
    Method rv;
    Class<?> srcReturnCls = src.getReturnType();
    String name = src.getName();
    Class<?>[] srcParameterClses = src.getParameterTypes();
    if (srcParameterClses.length > 0) {
      Class<?>[] dstParameterClses = trimLast(srcParameterClses);
      try {
        rv = src.getDeclaringClass().getMethod(name, dstParameterClses);
        if (rv.getReturnType() != srcReturnCls) {
          rv = null;
        }
      } catch (NoSuchMethodException nsme) {
        rv = null;
      }
    } else {
      rv = null;
    }
    return rv;
  }

  private static void handleMthd(Method mthd, List<JavaMethod> methods) {
    int modifiers = mthd.getModifiers();
    if ((modifiers & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
      JavaMethod methodDeclaredInJava = JavaMethod.getInstance(mthd);
      if (mthd.isAnnotationPresent(MethodTemplate.class)) {
        MethodTemplate methodTemplate = mthd.getAnnotation(MethodTemplate.class);
        if ((methodTemplate.visibility() == Visibility.PRIME_TIME) && (methodTemplate.isFollowedByLongerMethod() == false)) {
          JavaMethod longer = methodDeclaredInJava;
          Method _mthd = mthd;
          while (true) {
            _mthd = getNextShorterInChain(_mthd);
            if (_mthd != null) {
              JavaMethod shorter = JavaMethod.getInstance(_mthd);
              if (_mthd.isAnnotationPresent(MethodTemplate.class)) {
                MethodTemplate shorterMethodTemplate = _mthd.getAnnotation(MethodTemplate.class);
                if (shorterMethodTemplate.isFollowedByLongerMethod()) {
                  longer.setNextShorterInChain(shorter);
                  shorter.setNextLongerInChain(longer);
                  longer = shorter;
                } else {
                  break;
                }
              }
            } else {
              break;
            }
          }
        }
      }
      methods.add(methodDeclaredInJava);
    }
  }

  static List<JavaMethod> buildMethodList(Class<?> cls) {
    if (cls == null) {
      return Collections.emptyList();
    }
    List<JavaMethod> methods = Lists.newLinkedList();
    Set<Method> methodSet = null;
    Iterable<MethodInfo> methodInfos = ClassInfoManager.getMethodInfos(cls);
    if (methodInfos != null) {
      methodSet = new HashSet<>();
      for (MethodInfo methodInfo : methodInfos) {
        try {
          Method mthd = methodInfo.getMthd();
          if (mthd != null) {
            handleMthd(mthd, methods);
            methodSet.add(mthd);
          }
        } catch (RuntimeException re) {
          //edu.cmu.cs.dennisc.print.PrintUtilities.println( "no such method", methodInfo, "on ", cls );
          //re.printStackTrace();
        }
      }
    }
    for (Method mthd : cls.getDeclaredMethods()) {
      if ((methodSet != null) && methodSet.contains(mthd)) {
        //pass
      } else {
        handleMthd(mthd, methods);
      }
    }

    //update value templates for setters
    for (JavaMethod method : methods) {
      Method mthd = method.getMethodReflectionProxy().getReification();
      GetterTemplate propertyGetterTemplate = mthd.getAnnotation(GetterTemplate.class);
      if (propertyGetterTemplate != null) {
        Method sttr = PropertyUtilities.getSetterForGetter(mthd);
        JavaMethod setter;
        if (sttr != null) {
          setter = (JavaMethod) JavaMethod.getInstance(sttr).getLongestInChain();
        } else {
          setter = null;
        }
        if (setter != null) {
          ValueTemplate valueTemplate = mthd.getAnnotation(ValueTemplate.class);
          if (valueTemplate != null) {
            JavaMethod m = setter;
            while (m != null) {
              JavaMethodParameter parameter0 = (JavaMethodParameter) m.getRequiredParameters().getFirst();
              parameter0.setValueTemplate(valueTemplate);
              m = m.getNextShorterInChain();
            }
          }
        }
      }
    }

    return Collections.unmodifiableList(methods);
  }
}

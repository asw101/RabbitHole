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

import edu.cmu.cs.dennisc.java.lang.reflect.ReflectionUtilities;
import edu.cmu.cs.dennisc.java.util.InitializingIfAbsentMap;
import edu.cmu.cs.dennisc.java.util.Lists;
import edu.cmu.cs.dennisc.java.util.Maps;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.pattern.Lazy;
import edu.cmu.cs.dennisc.property.PropertyUtilities;
import edu.cmu.cs.dennisc.property.StringProperty;
import org.lgna.project.annotations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * @author Dennis Cosgrove
 */
public class JavaType extends AbstractType<JavaConstructor, JavaMethod, JavaField> {
  private static InitializingIfAbsentMap<ClassReflectionProxy, JavaType> mapReflectionProxyToInstance = Maps.newInitializingIfAbsentHashMap();
  public static final JavaType VOID_TYPE = getInstance(Void.TYPE);

  public static final JavaType BOOLEAN_PRIMITIVE_TYPE = getInstance(Boolean.TYPE);
  public static final JavaType BOOLEAN_OBJECT_TYPE = getInstance(Boolean.class);

  public static final JavaType NUMBER_OBJECT_TYPE = getInstance(Number.class);

  public static final JavaType INTEGER_PRIMITIVE_TYPE = getInstance(Integer.TYPE);
  public static final JavaType INTEGER_OBJECT_TYPE = getInstance(Integer.class);
  public static final JavaType DOUBLE_PRIMITIVE_TYPE = getInstance(Double.TYPE);
  public static final JavaType DOUBLE_OBJECT_TYPE = getInstance(Double.class);

  public static final JavaType[] BOOLEAN_TYPES = {BOOLEAN_PRIMITIVE_TYPE, BOOLEAN_OBJECT_TYPE};
  public static final JavaType[] INTEGER_TYPES = {INTEGER_PRIMITIVE_TYPE, INTEGER_OBJECT_TYPE};
  public static final JavaType[] DOUBLE_TYPES = {DOUBLE_PRIMITIVE_TYPE, DOUBLE_OBJECT_TYPE};

  public static final JavaType OBJECT_TYPE = getInstance(Object.class);
  public static final JavaType STRING_TYPE = getInstance(String.class);

  public static boolean isWrapperType(AbstractType<?, ?, ?> type) {
    return JavaTypePrimitiveMapping.isWrapperType(type);
  }

  /* package-private */
  static AbstractType<?, ?, ?> getWrapperTypeIfNecessary(AbstractType<?, ?, ?> type) {
    return JavaTypePrimitiveMapping.getWrapperTypeIfNecessary(type);
  }

  @Override
  protected boolean isAssignableFromType(AbstractType<?, ?, ?> other) {
    if (other != null) {
      //todo: handle arrays
      JavaType otherTypeDeclaredInJava = other.getFirstEncounteredJavaType();
      if (otherTypeDeclaredInJava != null) {
        Class<?> cls = this.getClassReflectionProxy().getReification();
        Class<?> otherCls = otherTypeDeclaredInJava.getClassReflectionProxy().getReification();
        if ((cls != null) && (otherCls != null)) {
          return cls.isAssignableFrom(otherCls);
        } else {
          Logger.severe(this, cls, otherTypeDeclaredInJava, otherCls);
          return false;
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public static JavaType getInstance(final ClassReflectionProxy classReflectionProxy) {
    return classReflectionProxy != null ? mapReflectionProxyToInstance.get(classReflectionProxy, JavaType::new) : null;
  }

  public static JavaType getInstance(Class<?> cls) {
    if (cls != null) {
      return getInstance(new ClassReflectionProxy(cls));
    } else {
      return null;
    }
  }

  public static JavaType[] getInstances(Class<?>[] clses) {
    JavaType[] rv = new JavaType[clses.length];
    for (int i = 0; i < clses.length; i++) {
      rv[i] = getInstance(clses[i]);
    }
    return rv;
  }

  public static JavaType[] getInstances(ClassReflectionProxy[] classReflectionProxies) {
    JavaType[] rv = new JavaType[classReflectionProxies.length];
    for (int i = 0; i < classReflectionProxies.length; i++) {
      rv[i] = getInstance(classReflectionProxies[i]);
    }
    return rv;
  }

  private JavaType(ClassReflectionProxy classReflectionProxy) {
    this.classReflectionProxy = classReflectionProxy;
  }

  @Override
  public AbstractType<?, ?, ?> getKeywordFactoryType() {
    Class<?> cls = this.classReflectionProxy.getReification();
    if (cls != null) {
      if (cls.isAnnotationPresent(ClassTemplate.class)) {
        ClassTemplate classTemplate = cls.getAnnotation(ClassTemplate.class);
        Class<?> keywordFactoryCls = classTemplate.keywordFactoryCls();
        if (keywordFactoryCls == ClassTemplate.VOID_ACTS_AS_NULL) {
          return null;
        } else {
          return JavaType.getInstance(keywordFactoryCls);
        }
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  @Override
  public boolean isFollowToSuperClassDesired() {
    Class<?> cls = this.classReflectionProxy.getReification();
    if (cls != null) {
      if (cls.isAnnotationPresent(ClassTemplate.class)) {
        ClassTemplate classTemplate = cls.getAnnotation(ClassTemplate.class);
        return classTemplate.isFollowToSuperClassDesired();
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

  @Override
  public boolean isConsumptionBySubClassDesired() {
    Class<?> cls = this.classReflectionProxy.getReification();
    if (cls != null) {
      if (cls.isAnnotationPresent(ClassTemplate.class)) {
        ClassTemplate classTemplate = cls.getAnnotation(ClassTemplate.class);
        return classTemplate.isConsumptionBySubClassDesired();
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public String getName() {
    return this.classReflectionProxy.getSimpleName();
  }

  @Override
  public StringProperty getNamePropertyIfItExists() {
    return null;
  }

  @Override
  public JavaPackage getPackage() {
    return JavaPackage.getInstance(this.classReflectionProxy.getPackageReflectionProxy());
  }

  @Override
  public JavaType getSuperType() {
    Class<?> cls = this.classReflectionProxy.getReification();
    if (cls != null) {
      //      if( cls.isInterface() ) {
      //        Class<?>[] superInterfaces = cls.getInterfaces();
      //        if( superInterfaces.length == 1 ) {
      //          return JavaType.getInstance( superInterfaces[ 0 ] );
      //        } else {
      //          return null;
      //        }
      //      } else {
      return JavaType.getInstance(cls.getSuperclass());
      //      }
    } else {
      return null;
    }
  }

  @Override
  public JavaType[] getInterfaces() {
    Class<?> cls = this.classReflectionProxy.getReification();
    assert cls != null : this.classReflectionProxy;
    return JavaType.getInstances(cls.getInterfaces());
  }

  @Override
  public List<JavaConstructor> getDeclaredConstructors() {
    return this.constructors.get();
  }

  @Override
  public List<JavaMethod> getDeclaredMethods() {
    return this.methods.get();
  }

  @Override
  public List<JavaField> getDeclaredFields() {
    return this.fields.get();
  }

  public List<JavaGetterSetterPair> getGetterSetterPairs() {
    return this.getterSetterPairs.get();
  }

  public ClassReflectionProxy getClassReflectionProxy() {
    return this.classReflectionProxy;
  }

  public boolean contentEquals(Class<?> cls) {
    if (this.classReflectionProxy != null) {
      Class<?> reification = this.classReflectionProxy.getReification();
      return cls == reification;
    } else {
      return false;
    }
  }

  @Override
  public boolean isUserAuthored() {
    return false;
  }

  @Override
  public AccessLevel getAccessLevel() {
    Class<?> cls = this.classReflectionProxy.getReification();
    assert cls != null : this.classReflectionProxy;
    return AccessLevel.getValueFromModifiers(cls.getModifiers());
  }

  @Override
  public boolean isPrimitive() {
    Class<?> cls = this.classReflectionProxy.getReification();
    if (cls != null) {
      return cls.isPrimitive();
    } else {
      return false;
    }
  }

  @Override
  public boolean isInterface() {
    Class<?> cls = this.classReflectionProxy.getReification();
    assert cls != null : this.classReflectionProxy;
    return cls.isInterface();
  }

  @Override
  public boolean isStatic() {
    Class<?> cls = this.classReflectionProxy.getReification();
    assert cls != null : this.classReflectionProxy;
    return Modifier.isStatic(cls.getModifiers());
  }

  @Override
  public boolean isAbstract() {
    Class<?> cls = this.classReflectionProxy.getReification();
    assert cls != null : this.classReflectionProxy;
    return Modifier.isAbstract(cls.getModifiers());
  }

  @Override
  public boolean isFinal() {
    Class<?> cls = this.classReflectionProxy.getReification();
    assert cls != null : this.classReflectionProxy;
    return Modifier.isFinal(cls.getModifiers());
  }

  @Override
  public boolean isStrictFloatingPoint() {
    Class<?> cls = this.classReflectionProxy.getReification();
    assert cls != null : this.classReflectionProxy;
    return Modifier.isStrict(cls.getModifiers());
  }

  @Override
  public boolean isArray() {
    return this.classReflectionProxy.isArray();
  }

  @Override
  public boolean isEnum() {
    Class<?> cls = this.classReflectionProxy.getReification();
    assert cls != null : this.classReflectionProxy;
    return cls.isEnum();
  }

  public JavaType getEnclosingType() {
    Class<?> cls = this.classReflectionProxy.getReification();
    assert cls != null : this.classReflectionProxy;
    return JavaType.getInstance(cls.getEnclosingClass());
  }

  @Override
  public JavaType getComponentType() {
    return JavaType.getInstance(this.classReflectionProxy.getComponentClassReflectionProxy());
  }

  @Override
  public JavaType getArrayType() {
    Class<?> cls = this.classReflectionProxy.getReification();
    assert cls != null : this.classReflectionProxy;
    return JavaType.getInstance(ReflectionUtilities.getArrayClass(cls));
  }

  @Override
  public boolean isEquivalentTo(Object other) {
    if (other instanceof JavaType type) {
      return classReflectionProxy.equals(type.classReflectionProxy);
    } else {
      return false;
    }
  }

  private final ClassReflectionProxy classReflectionProxy;
  private final Lazy<List<JavaConstructor>> constructors = new Lazy<List<JavaConstructor>>() {
    @Override
    protected List<JavaConstructor> create() {
      Class<?> cls = classReflectionProxy.getReification();
      if (cls != null) {
        List<JavaConstructor> constructors = Lists.newLinkedList();
        for (Constructor<?> cnstrctr : cls.getDeclaredConstructors()) {
          constructors.add(JavaConstructor.getInstance(cnstrctr));
        }
        return Collections.unmodifiableList(constructors);
      } else {
        return Collections.emptyList();
      }
    }
  };
  private final Lazy<List<JavaMethod>> methods = new Lazy<List<JavaMethod>>() {
    @Override
    protected List<JavaMethod> create() {
      return JavaTypeMethodLookup.buildMethodList(classReflectionProxy.getReification());
    }
  };
  private final Lazy<List<JavaField>> fields = new Lazy<List<JavaField>>() {
    @Override
    protected List<JavaField> create() {
      Class<?> cls = classReflectionProxy.getReification();
      if (cls != null) {
        List<JavaField> fields = Lists.newLinkedList();
        for (Field fld : cls.getDeclaredFields()) {
          fields.add(JavaField.getInstance(fld));
        }
        return Collections.unmodifiableList(fields);
      } else {
        return Collections.emptyList();
      }
    }
  };
  private final Lazy<List<JavaGetterSetterPair>> getterSetterPairs = new Lazy<List<JavaGetterSetterPair>>() {
    @Override
    protected List<JavaGetterSetterPair> create() {
      Class<?> cls = classReflectionProxy.getReification();
      if (cls != null) {
        List<JavaGetterSetterPair> getterSetterPairs = Lists.newLinkedList();
        for (JavaMethod method : getDeclaredMethods()) {
          java.lang.reflect.Method mthd = method.getMethodReflectionProxy().getReification();
          GetterTemplate propertyGetterTemplate = mthd.getAnnotation(GetterTemplate.class);
          if (propertyGetterTemplate != null) {
            java.lang.reflect.Method sttr = PropertyUtilities.getSetterForGetter(mthd);
            JavaMethod setter;
            if (sttr != null) {
              setter = (JavaMethod) JavaMethod.getInstance(sttr).getLongestInChain();
            } else {
              setter = null;
            }
            getterSetterPairs.add(new JavaGetterSetterPair(method, setter));
          }
        }
        return Collections.unmodifiableList(getterSetterPairs);
      } else {
        return Collections.emptyList();
      }
    }
  };

  @Override
  protected String formatTypeName(BinaryOperator<String> localizer) {
    Class<?> cls = getClassReflectionProxy().getReification();
    return localizer.apply(cls.getName(), cls.getSimpleName());
  }
}

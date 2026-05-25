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

import edu.cmu.cs.dennisc.java.util.ResourceBundleUtilities;
import org.alice.ide.common.BodyPane;
import org.alice.ide.common.TypeComponent;
import org.alice.ide.x.ProjectEditorAstI18nFactory;
import org.alice.ide.x.components.StatementListPropertyView;
import org.lgna.project.ast.*;
import org.lgna.project.code.CodeOrganizer;
import org.lgna.project.code.ProcessableNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HtmlEncoder implements AstProcessor {

  private final Document document;
  private final SvgEncoder svgEncoder;
  private final Deque<org.w3c.dom.Element> activeElements = new ArrayDeque<>();
  private static final Map<String, CodeOrganizer.CodeOrganizerDefinition> codeOrganizerDefinitionMap = Map.of(
      "Scene", CodeOrganizer.sceneClassCodeOrganizer,
      "Program", CodeOrganizer.programClassCodeOrganizer);
  private static final Set<String> sectionsToSkip = Set.of("ConstructorSection", "GettersAndSettersSection", "StaticMethodsSection");

  HtmlEncoder(Document doc) {
    document = doc;
    svgEncoder = new SvgEncoder(doc);
  }

  public void encode(ProcessableNode node, org.w3c.dom.Element root) {
    activeElements.push(root);
    node.process(this);
  }

  /** Document creation **/

  private void pushDiv(String classes, Runnable appender) {
    final org.w3c.dom.Element element = addDiv(classes);
    activeElements.push(element);
    appender.run();
    assert element == activeElements.pop();
  }

  private void pushSpan(String classes, Runnable appender) {
    final org.w3c.dom.Element element = addSpan(classes, " ");
    activeElements.push(element);
    appender.run();
    assert element == activeElements.pop();
  }

  private org.w3c.dom.Node parentNode() {
    org.w3c.dom.Node parent = parentElement();
    return parent == null ? document : parent;
  }

  private org.w3c.dom.Element parentElement() {
    return activeElements.peek();
  }

  private Element addDiv(String classes) {
    return addElement("div", classes, " ");
  }

  private Element addSpan(String classes, String value) {
    return addElement("span", classes, value);
  }

  private Element addElement(String elementType, String classes, String content) {
    final org.w3c.dom.Element element = document.createElement(elementType);
    element.setAttribute("class", classes);
    element.setTextContent(content);
    parentNode().appendChild(element);
    return element;
  }

  private void pushSvg(Runnable content) {
    svgEncoder.pushSvg(parentNode(), content);
  }

  private void addToSvg(org.lgna.croquet.views.SwingComponentView<?> view) {
    svgEncoder.addToSvg(view);
  }

  private void addTypeSvgInline(AbstractType<?, ?, ?> type) {
    pushSvg(() -> addToSvg(TypeComponent.createInstance(type)));
  }

  private void addExpressionToSvg(Expression expression) {
    addToSvg(ProjectEditorAstI18nFactory.getInstance().createExpressionPane(expression));
  }

  /** Localization **/

  private String localizedCodeEditorTerm(String key) {
    return ResourceBundleUtilities.getStringForKey(key, "org.alice.ide.codeeditor.CodeEditor");
  }

  /** Class structure **/

  @Override
  public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
    return new CodeOrganizer(codeOrganizerDefinitionMap.getOrDefault(typeName, CodeOrganizer.defaultCodeOrganizer));
  }

  @Override
  public void processClass(CodeOrganizer codeOrganizer, NamedUserType userType) {
    Map<String, List<ProcessableNode>> sections = codeOrganizer.getOrderedSections();
    if (HtmlEncoderLogic.isClassEmpty(sections, sectionsToSkip)) {
      return;
    }
    pushDiv("alice-class", () -> {
      pushDiv("alice-class-header alice-code-header", () -> {
        addSpan("alice-class-name", userType.getName());
        addSpan("alice-code-header-detail", "extends");
        addSpan("alice-class-superType", userType.getSuperType().getName());
      });
      for (Map.Entry<String, List<ProcessableNode>> entry : sections.entrySet()) {
          if (isSectionToInclude(entry.getKey()) && !entry.getValue().isEmpty()) {
            appendSection(entry.getValue());
        }
      }
    });
  }

  private boolean isClassEmpty(Map<String, List<ProcessableNode>> sections) {
    return HtmlEncoderLogic.isClassEmpty(sections, sectionsToSkip);
  }

  private boolean isSectionToInclude(String key) {
    return HtmlEncoderLogic.isSectionToInclude(key, sectionsToSkip);
  }

  private void appendSection(List<ProcessableNode> items) {
    pushDiv("alice-class-section", () -> {
      for (ProcessableNode item : items) {
        item.process(this);
      }
    });
  }

  /** Methods and Fields **/

  @Override
  public void processField(UserField field) {
    pushDiv("alice-field-definition alice-code-header", () -> {
      addTypeSvgInline(field.valueType.getValue());
      addSpan("alice-field-name", field.name.getValue());
      addSpan("alice-assignment", "<-");
      pushSvg(() -> addExpressionToSvg(field.initializer.getValue()));
    });
  }

  @Override
  public void processMethod(UserMethod method) {
    // Skip code that is generated or managed, but this does not include Program.main, so there is an extra check to skip it
    if (HtmlEncoderLogic.shouldSkipMethod(method)) {
      return;
    }
    pushDiv("alice-method", () -> {
      if ("initializeEventListeners".equals(method.getName())) {
        pushDiv("alice-method-header alice-code-header", () -> addSpan("alice-method-name alice-code-header", method.getName()));
        splitUpListeners(method);
      } else {
        appendMethodHeader(method);
        pushSvg(() -> statementsAsSvg(method.body.getValue().statements));
      }
    });
  }

  private void splitUpListeners(UserMethod initializeEventListeners) {
    ArrayList<Statement> listeners = initializeEventListeners.body.getValue().statements.getValue();
    for (Statement listener : listeners) {
      MethodInvocation invocation = HtmlEncoderLogic.getListenerInvocation(listener);
      if (invocation != null) {
        addListener(invocation, !listener.isEnabled.getValue());
      }
    }
  }

  private void addListener(MethodInvocation addListenerCall, boolean isDisabled) {
    pushDiv("alice-listener-addition", () -> {
      if (isDisabled) {
        addDiv("alice-disabled");
      }
      pushDiv("alice-method-header alice-code-header", () -> {
        pushSvg(() -> addExpressionToSvg(addListenerCall.expression.getValue()));  // generally a reference to "this"
        final AbstractMethod listenerMethod = addListenerCall.method.getValue();
        addSpan("alice-method-name alice-code-header", listenerMethod.getName());
        appendLambdaArguments(addListenerCall);
      });
      SimpleArgument listenerArgument = HtmlEncoderLogic.getRequiredListenerArgument(addListenerCall);
      UserLambda userLambda = HtmlEncoderLogic.getUserLambda(listenerArgument);
      if (listenerArgument != null && userLambda != null) {
        List<? extends AbstractMethod> listenerTypeMethods = listenerArgument.parameter.getValue().getValueType().getDeclaredMethods();
        AbstractMethod first = listenerTypeMethods.getFirst();

        pushDiv("alice-listener-declaration", () -> {
          appendLambdaMethodHeader(first.getName());
          pushSvg(() -> statementsAsSvg(userLambda.body.getValue().statements));
        });
      }
    });
  }

  private void statementsAsSvg(StatementListProperty statements) {
    ProjectEditorAstI18nFactory factory = ProjectEditorAstI18nFactory.getInstance();
    StatementListPropertyView statementListComponent = new StatementListPropertyView(factory, statements, 0);
    BodyPane bodyPane = new BodyPane(statementListComponent);
    addToSvg(bodyPane);
  }

  private void appendMethodHeader(AbstractUserMethod method) {
    pushDiv("alice-method-header alice-code-header", () -> {
      addSpan("alice-code-header-detail", localizedCodeEditorTerm("declare"));
      if (method.isStatic()) {
        addSpan("alice-code-header-detail", localizedCodeEditorTerm("static"));
      }
      if (method.isFunction()) {
        addTypeSvgInline(method.getReturnType());
        addSpan("alice-code-header-detail", localizedCodeEditorTerm("function"));
      } else {
        addSpan("alice-code-header-detail", localizedCodeEditorTerm("procedure"));
      }
      addSpan("alice-method-name alice-code-header", method.getName());
      appendParameters(method);
    });
  }

  private void appendLambdaMethodHeader(String name) {
    pushDiv("alice-method-header alice-code-header", () -> {
      addSpan("alice-code-header-detail", localizedCodeEditorTerm("declare"));
      addSpan("alice-code-header-detail", localizedCodeEditorTerm("procedure"));
      addSpan("alice-method-name alice-code-header", name);
    });
  }

  private void appendLambdaArguments(MethodInvocation code) {
    pushSpan("alice-parameters alice-code-header", () -> {
      for (SimpleArgument arg : code.requiredArguments.getValue()) {
        if (!HtmlEncoderLogic.isListenerArgument(arg)) {
          appendLambdaArgument(arg);
        }
      }
      for (SimpleArgument arg : code.variableArguments.getValue()) {
        appendLambdaArgument(arg);
      }
      for (JavaKeyedArgument arg : code.keyedArguments.getValue()) {
        appendLambdaArgument(arg);
      }
    });
  }

  private void appendLambdaArgument(AbstractArgument arg) {
    pushSvg(() -> addExpressionToSvg(arg.expression.getValue()));
  }

  private void appendParameters(Code code) {
    pushSpan("alice-parameters alice-code-header", () -> {
      final List<? extends AbstractParameter> requiredParameters = code.getRequiredParameters();
      if (requiredParameters.size() == 1) {
        addSpan("alice-code-header-detail", localizedCodeEditorTerm("withParameter"));
      }
      if (requiredParameters.size() > 1) {
        addSpan("alice-code-header-detail", localizedCodeEditorTerm("withParameters"));
      }
      for (AbstractParameter parameter : requiredParameters) {
        appendParameter(parameter);
      }
    });
  }

  private void appendParameter(AbstractParameter parameter) {
    pushSpan("alice-parameter alice-code-header", () -> {
      addTypeSvgInline(parameter.getValueType());
      String parameterName = parameter.getValidName();
      if (parameterName != null) {
        addSpan("alice-parameter-label", parameterName);
      }
    });
  }
}

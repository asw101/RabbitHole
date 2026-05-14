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

package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.java.lang.DoubleUtilities;
import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import org.lgna.common.ProgramClosedException;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormatSymbols;

/**
 * Delegate that handles all user-prompt dialog UI for {@link EntityImp}.
 *
 * <p>Extracted from EntityImp to reduce class size. All dialog-related
 * inner classes and methods live here. EntityImp delegates to this class
 * so that its public API is unchanged.
 *
 * @author Dennis Cosgrove
 */
class UserDialogDelegate {

  private final EntityImp owner;

  UserDialogDelegate(EntityImp owner) {
    this.owner = owner;
  }

  private Component getParentComponent() {
    SceneImp scene = this.owner.getScene();
    if (scene != null) {
      OnscreenRenderTarget onscreenRenderTarget = this.owner.getOnscreenRenderTarget();
      if (onscreenRenderTarget != null) {
        return onscreenRenderTarget.getAwtComponent();
      }
    }
    return null;
  }

  private void promptUserWithOptionToCancel() {
    String optionA = "return to the previous dialog";
    String optionB = "exit the running program";
    StringBuilder sb = new StringBuilder();
    sb.append("Invalid dialog input.\n\nWould you like to:\n    ");
    sb.append(optionA);
    sb.append("\n        or\n    ");
    sb.append(optionB);
    sb.append("\n?");
    int returnResult;
    Object[] options;
    if (SystemUtilities.isWindows()) {
      returnResult = JOptionPane.YES_OPTION;
      options = new Object[] {optionA, optionB};
    } else {
      returnResult = JOptionPane.NO_OPTION;
      options = new Object[] {optionB, optionA};
    }
    int result = JOptionPane.showOptionDialog(this.getParentComponent(), sb.toString(), "Invalid Dialog Input", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, optionA);
    if (result == returnResult) {
      return;
    } else {
      throw new ProgramClosedException("user dialog");
    }
  }

  public double getDoubleFromUser(String message) {
    String title = null;
    DoubleNumberModel model = new DoubleNumberModel(message);
    while (true) {
      JNumPad numPad = model.createComponent();
      numPad.addListeners();
      JOptionPane.showMessageDialog(this.getParentComponent(), numPad, title, JOptionPane.QUESTION_MESSAGE);
      numPad.removeListeners();
      Double value = model.getValue();
      if (value != null) {
        return value;
      } else {
        this.promptUserWithOptionToCancel();
      }
    }
  }

  public int getIntegerFromUser(String message) {
    String title = null;
    IntegerNumberModel model = new IntegerNumberModel(message);
    while (true) {
      JNumPad numPad = model.createComponent();
      numPad.addListeners();
      JOptionPane.showMessageDialog(this.getParentComponent(), numPad, title, JOptionPane.QUESTION_MESSAGE);
      numPad.removeListeners();
      Integer value = model.getValue();
      if (value != null) {
        return value;
      } else {
        this.promptUserWithOptionToCancel();
      }
    }
  }

  public boolean getBooleanFromUser(String message) {
    Component parentComponent = this.getParentComponent();
    String title = null;
    Object[] selectionValues = {"True", "False"};
    Icon icon = null;
    Object initialSelectionValue = null;
    while (true) {
      int option = JOptionPane.showOptionDialog(parentComponent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon, selectionValues, initialSelectionValue);
      switch (option) {
      case JOptionPane.YES_OPTION:
        return true;
      case JOptionPane.NO_OPTION:
        return false;
      default:
        this.promptUserWithOptionToCancel();
      }
    }
  }

  public String getStringFromUser(String message) {
    Component parentComponent = this.getParentComponent();
    String title = null;

    JOptionPane optionPane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION);
    optionPane.setWantsInput(true);

    JDialog dialog = optionPane.createDialog(parentComponent, title);

    while (true) {
      dialog.setVisible(true);
      Object value = optionPane.getInputValue();
      if (JOptionPane.UNINITIALIZED_VALUE.equals(value)) {
        this.promptUserWithOptionToCancel();
      } else {
        dialog.dispose();
        return (String) value;
      }
    }
  }

  // ════════════════════════════════════════════════════════════════════════════
  //  Inner classes — formerly nested inside EntityImp
  // ════════════════════════════════════════════════════════════════════════════

  private static class JNumPad extends JPanel {

    private final AncestorListener ancestorListener = new AncestorListener() {
      @Override
      public void ancestorAdded(AncestorEvent event) {
        textField.requestFocusInWindow();
      }

      @Override
      public void ancestorMoved(AncestorEvent event) {
      }

      @Override
      public void ancestorRemoved(AncestorEvent event) {
      }
    };

    private final JTextField textField;

    public JNumPad(NumberModel<?> numberModel) {
      JPanel gridBagPanel = new JPanel();
      gridBagPanel.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.BOTH;
      gbc.weightx = 1.0;
      gridBagPanel.add(new JButton(numberModel.numeralActions[7]), gbc);
      gridBagPanel.add(new JButton(numberModel.numeralActions[8]), gbc);
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gridBagPanel.add(new JButton(numberModel.numeralActions[9]), gbc);

      gbc.weightx = 0.0;
      gbc.gridwidth = 1;
      gridBagPanel.add(new JButton(numberModel.numeralActions[4]), gbc);
      gridBagPanel.add(new JButton(numberModel.numeralActions[5]), gbc);
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gridBagPanel.add(new JButton(numberModel.numeralActions[6]), gbc);

      gbc.gridwidth = 1;
      gridBagPanel.add(new JButton(numberModel.numeralActions[1]), gbc);
      gridBagPanel.add(new JButton(numberModel.numeralActions[2]), gbc);
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gridBagPanel.add(new JButton(numberModel.numeralActions[3]), gbc);

      DecimalPointAction decimalPointAction = numberModel.getDecimalPointAction();
      if (decimalPointAction != null) {
        gbc.gridwidth = 1;
        gridBagPanel.add(new JButton(numberModel.numeralActions[0]), gbc);
        gridBagPanel.add(new JButton(decimalPointAction), gbc);
      } else {
        gbc.gridwidth = 2;
        gridBagPanel.add(new JButton(numberModel.numeralActions[0]), gbc);
      }
      gridBagPanel.add(new JButton(numberModel.negateAction), gbc);

      this.textField = new JTextField(numberModel.document, "", 0);
      JPanel lineAxisPanel = new JPanel();
      lineAxisPanel.setLayout(new BoxLayout(lineAxisPanel, BoxLayout.LINE_AXIS));
      lineAxisPanel.add(this.textField);
      lineAxisPanel.add(new JButton(numberModel.backspaceAction));

      JLabel messageLabel = new JLabel(numberModel.message);
      messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      lineAxisPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      gridBagPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      this.add(messageLabel);
      this.add(lineAxisPanel);
      this.add(gridBagPanel);
    }

    public void addListeners() {
      this.textField.addAncestorListener(this.ancestorListener);
    }

    public void removeListeners() {
      this.textField.removeAncestorListener(this.ancestorListener);
    }
  }

  private abstract static class NumberModel<N extends Number> {
    private final String message;
    private final Document document = new PlainDocument();
    private final NumeralAction[] numeralActions = new NumeralAction[10];
    private final NegateAction negateAction = new NegateAction(this);
    private final BackspaceAction backspaceAction = new BackspaceAction(this);

    public NumberModel(String message) {
      this.message = message;
      for (int i = 0; i < numeralActions.length; i++) {
        numeralActions[i] = new NumeralAction(this, (short) i);
      }
    }

    protected abstract DecimalPointAction getDecimalPointAction();

    private void append(String s) {
      try {
        this.document.insertString(this.document.getLength(), s, null);
      } catch (BadLocationException ble) {
        throw new RuntimeException(ble);
      }
    }

    public void appendDigit(short numeral) {
      this.append(Short.toString(numeral));
    }

    public void appendDecimalPoint() {
      Action action = this.getDecimalPointAction();
      if (action != null) {
        this.append((String) action.getValue(Action.NAME));
      }
    }

    public void negate() {
      final int N = this.document.getLength();
      try {
        boolean isNegative;
        if (N > 0) {
          String s0 = this.document.getText(0, 1);
          isNegative = s0.charAt(0) == '-';
        } else {
          isNegative = false;
        }
        if (isNegative) {
          this.document.remove(0, 1);
        } else {
          this.document.insertString(0, "-", null);
        }
      } catch (BadLocationException ble) {
        throw new RuntimeException(ble);
      }
    }

    public void deleteLastCharacter() {
      final int N = this.document.getLength();
      if (this.document.getLength() > 0) {
        try {
          this.document.remove(N - 1, 1);
        } catch (BadLocationException ble) {
          throw new RuntimeException(ble);
        }
      }
    }

    protected abstract N getValue(String text);

    public N getValue() {
      try {
        final int N = this.document.getLength();
        String text = this.document.getText(0, N);
        try {
          N rv = this.getValue(text);
          return rv;
        } catch (NumberFormatException nfe) {
          return null;
        }
      } catch (BadLocationException ble) {
        throw new RuntimeException(ble);
      }
    }

    public JNumPad createComponent() {
      return new JNumPad(this);
    }
  }

  private static class DoubleNumberModel extends NumberModel<Double> {
    private final DecimalPointAction decimalPointAction = new DecimalPointAction(this);

    public DoubleNumberModel(String message) {
      super(message);
    }

    @Override
    protected Double getValue(String text) {
      double d = DoubleUtilities.parseDoubleInCurrentDefaultLocale(text);
      if (Double.isNaN(d)) {
        return null;
      } else {
        return d;
      }
    }

    @Override
    public DecimalPointAction getDecimalPointAction() {
      return this.decimalPointAction;
    }
  }

  private static class IntegerNumberModel extends NumberModel<Integer> {
    public IntegerNumberModel(String message) {
      super(message);
    }

    @Override
    protected Integer getValue(String text) {
      return Integer.valueOf(text);
    }

    @Override
    public DecimalPointAction getDecimalPointAction() {
      return null;
    }
  }

  private static class BackspaceAction extends AbstractAction {
    private final NumberModel<?> numberModel;

    public BackspaceAction(NumberModel<?> numberModel) {
      this.numberModel = numberModel;
      this.putValue(NAME, "←");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      this.numberModel.deleteLastCharacter();
    }
  }

  private static class NumeralAction extends AbstractAction {
    private final NumberModel<?> numberModel;
    private final short digit;

    public NumeralAction(NumberModel<?> numberModel, short digit) {
      this.numberModel = numberModel;
      this.digit = digit;
      this.putValue(NAME, Short.toString(this.digit));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      this.numberModel.appendDigit(digit);
    }
  }

  private static class NegateAction extends AbstractAction {
    private final NumberModel<?> numberModel;

    public NegateAction(NumberModel<?> numberModel) {
      this.numberModel = numberModel;
      this.putValue(NAME, "±");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      this.numberModel.negate();
    }
  }

  private static class DecimalPointAction extends AbstractAction {
    private final NumberModel<?> numberModel;

    public DecimalPointAction(NumberModel<?> numberModel) {
      this.numberModel = numberModel;
      DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
      this.putValue(NAME, "" + decimalFormatSymbols.getDecimalSeparator());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      this.numberModel.appendDecimalPoint();
    }
  }
}

package org.lgna.croquet.data;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * No-op {@link ListDataListener} base for list-data tests.
 * Subclass and override only the callback(s) you want to verify.
 */
public class TestListDataListener implements ListDataListener {

  @Override
  public void intervalAdded(ListDataEvent e) {}

  @Override
  public void intervalRemoved(ListDataEvent e) {}

  @Override
  public void contentsChanged(ListDataEvent e) {}
}

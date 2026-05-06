/*
 * Copyright (c) 2006-2011, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 */

package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.pattern.Tuple2;

import java.util.ArrayList;
import java.util.List;

final class ModelResourceJointTreeUtilities {
  static List<Tuple2<String, String>> makeCodeReadyTree(List<Tuple2<String, String>> sourceList, boolean removeRootJoints) {
    if (sourceList != null) {
      List<Tuple2<String, String>> cleaned = new ArrayList<Tuple2<String, String>>();
      for (Tuple2<String, String> entry : sourceList) {
        if (removeRootJoints) {
          if (isRootJoint(entry.getA()) && ((entry.getB() == null) || (entry.getB().length() == 0))) {
            continue;
          } else if ((entry.getB() != null) && isRootJoint(entry.getB())) {
            entry.setB(null);
          }
        }
        cleaned.add(entry);
      }
      List<Tuple2<String, String>> sorted = new ArrayList<Tuple2<String, String>>();
      while (sorted.size() != cleaned.size()) {
        for (Tuple2<String, String> entry : cleaned) {
          if (!sorted.contains(entry) && hasParent(sorted, entry.getB())) {
            sorted.add(entry);
          }
        }
      }

      return sorted;
    }
    return null;
  }

  private static boolean hasParent(List<Tuple2<String, String>> listToCheck, String parent) {
    if ((parent == null) || (parent.length() == 0)) {
      return true;
    }
    for (Tuple2<String, String> entry : listToCheck) {
      if (entry.getA().equalsIgnoreCase(parent)) {
        return true;
      }
    }
    return false;
  }

  static boolean isRootJoint(String jointName) {
    return jointName.equalsIgnoreCase("root");
  }

  private ModelResourceJointTreeUtilities() {
  }
}

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

final class ModelResourceArrayUtilities {
  private static final Pattern ARRAY_PATTERN = Pattern.compile("(_\\d*$)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  static int getArrayIndexForJoint(String jointName) {
    Matcher match = ARRAY_PATTERN.matcher(jointName);
    if (match.find()) {
      String indexStr = jointName.substring(jointName.lastIndexOf('_') + 1);
      //Remove all leading 0s
      indexStr = indexStr.replaceAll("^0+", "");
      //If we've removed all the leading 0s and ended up with an empty string, return the number 0
      if (indexStr.length() == 0) {
        return 0;
      }
      try {
        return Integer.decode(indexStr);
      } catch (NumberFormatException e) {
        System.err.println("Error decoding array index in joint " + jointName);
        return -1;
      }
    } else {
      return -1;
    }
  }

  static boolean hasArray(String arrayName, List<Tuple2<String, String>> jointList) {
    for (Tuple2<String, String> joint : jointList) {
      if (arrayName.equals(getArrayNameForJoint(joint.getA(), null, null))) {
        return true;
      }
    }
    return false;
  }

  static String getArrayNameForJoint(String jointName, Map<String, String> customArrayNameMap, String[] namesToSkip) {
    Matcher match = ARRAY_PATTERN.matcher(jointName);
    if (match.find()) {
      String nameStr = jointName.substring(0, jointName.lastIndexOf('_'));
      if ((customArrayNameMap != null) && customArrayNameMap.containsKey(nameStr)) {
        nameStr = customArrayNameMap.get(nameStr);
      }
      if (namesToSkip != null) {
        for (String toSkip : namesToSkip) {
          if (nameStr.equalsIgnoreCase(toSkip)) {
            return null;
          }
        }
      }
      return nameStr;
    }
    //    int index = jointName.lastIndexOf( '_' );
    //    if( index != -1 ) {
    //      String nameStr = jointName.substring( 0, index );
    //      if( ( customArrayNameMap != null ) && customArrayNameMap.containsKey( nameStr ) ) {
    //        nameStr = customArrayNameMap.get( nameStr );
    //        return nameStr;
    //      }
    //
    //    }
    return null;
  }

  static Map<String, List<String>> getArrayEntriesFromJointList(List<Tuple2<String, String>> jointList, Map<String, String> customArrayNameMap, List<String> jointsToSuppress, String[] arrayNamesToSkip) throws DataFormatException {
    List<String> jointNames = new LinkedList<String>();
    for (Tuple2<String, String> joint : jointList) {
      jointNames.add(joint.getA());
    }
    return getArrayEntries(jointNames, customArrayNameMap, jointsToSuppress, arrayNamesToSkip);
  }

  static Map<String, List<String>> getArrayEntries(List<String> jointNames, Map<String, String> customArrayNameMap, List<String> jointsToSuppress, String[] arrayNamesToSkip) throws DataFormatException {
    //Array name, joint name entries
    Map<String, List<String>> arrayEntries = new HashMap<String, List<String>>();
    for (String jointName : jointNames) {
      if ((jointsToSuppress == null) || !jointsToSuppress.contains(jointName)) {
        String arrayName = getArrayNameForJoint(jointName, customArrayNameMap, arrayNamesToSkip);
        if (arrayName != null) {
          if (arrayEntries.containsKey(arrayName)) {
            arrayEntries.get(arrayName).add(jointName);
          } else {
            List<String> arrayElements = new ArrayList<String>();
            arrayElements.add(jointName);
            arrayEntries.put(arrayName, arrayElements);
          }
        }
      }
    }
    //How to sort array entry names that are not really comparable?
    // Like entries with different names (ENGINE_WHEEL_1 vs COAL_BOX_WHEEL_1) but have a clear spatial ordering
    // We don't handle cases like this
    for (Entry<String, List<String>> arrayEntry : arrayEntries.entrySet()) {
      try {
        Collections.sort(arrayEntry.getValue(), new Comparator<String>() {
          @Override
          public int compare(String o1, String o2) {
            int index1 = getArrayIndexForJoint(o1);
            int index2 = getArrayIndexForJoint(o2);
            if (index1 == index2) {
              System.err.println("ERROR COMPARING ARRAY NAME INDICES: " + o1 + " == " + o2);
              //We don't handle cases where the index is the same.
              throw new RuntimeException("ERROR COMPARING ARRAY NAME INDICES: " + o1 + " == " + o2 + ". These names resolve to the same index (" + index1 + ") and that is not allowed.");
            }
            return index1 - index2;
          }
        });
      } catch (RuntimeException e) {
        e.printStackTrace();
        throw new DataFormatException(e.toString());
      }
    }

    return arrayEntries;
  }

  private ModelResourceArrayUtilities() {
  }
}

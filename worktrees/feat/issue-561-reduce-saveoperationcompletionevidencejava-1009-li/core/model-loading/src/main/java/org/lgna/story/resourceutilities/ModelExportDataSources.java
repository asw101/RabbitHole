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

import edu.cmu.cs.dennisc.java.util.zip.DataSource;

import java.io.IOException;
import java.io.OutputStream;

final class ModelExportDataSources {
  private ModelExportDataSources() {
  }

  static DataSource create(String name, Writer writer) {
    return new DataSource() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public void write(OutputStream os) throws IOException {
        writer.write(os);
      }
    };
  }

  static String structureFileNameRelativeTo(String resourcePath, DataSource structureDataSource) {
    return structureDataSource.getName().substring(resourcePath.length() + 1);
  }

  @FunctionalInterface
  interface Writer {
    void write(OutputStream os) throws IOException;
  }
}

/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.contextpropagation.wireadapters;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

//import mockit.Deencapsulation;

//import org.glassfish.contextpropagation.adaptors.MockLoggerAdapter;
import org.glassfish.contextpropagation.internal.Utils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PositionAwareObjectOutputStreamTest {
//
//  @Test
//  public void testSimple() throws IOException {
//    Populateable p = new Populateable() {
//      @Override  public void populate(ObjectOutputStream oos, List<Short> positions) throws IOException {
//        oos.writeLong(1);
//        oos.writeObject("foo");
//        oos.writeLong(2);
//        oos.writeObject("bar");
//      }
//    };
//    assertArrayEquals(populate(ObjectOutputStream.class, p, null),
//        populate(PositionAwareObjectOutputStream.class, p, null));
//  }
//
//  @Test public void testPositions() throws IOException {
//    LinkedList<Short> positions = new LinkedList<Short>();
//    Populateable p = new Populateable() {
//      @Override  public void populate(ObjectOutputStream oos, List<Short> positions) throws IOException {
//        byte[] buf = "Some Bytes".getBytes();
//        addPosition(positions, oos); oos.writeObject("foo");
//        addPosition(positions, oos); oos.writeShort(1);
//        addPosition(positions, oos); oos.writeLong(2);
//        addPosition(positions, oos); oos.writeBoolean(true);
//        addPosition(positions, oos); oos.writeDouble(2.1E5);
//        addPosition(positions, oos); oos.writeFloat(2.1F);
//        addPosition(positions, oos); oos.write(buf);
//        addPosition(positions, oos); oos.writeByte((byte) 'b');
//        addPosition(positions, oos); oos.writeChar('c');
//        addPosition(positions, oos); oos.writeChars("chars");
//        addPosition(positions, oos); oos.writeInt(1);
//        addPosition(positions, oos); oos.writeObject("bar");
//        addPosition(positions, oos);
//      }
//    };
//    byte[] bytes = populate(PositionAwareObjectOutputStream.class, p, positions);
//    MockLoggerAdapter.debug("bytes size: " + bytes.length + ", positions: " + positions);
//    byte [] expectedBytes = populate(ObjectOutputStream.class, p, null);
//    MockLoggerAdapter.debug(Utils.toString(expectedBytes));
//    MockLoggerAdapter.debug(Utils.toString(bytes));
//    assertArrayEquals(expectedBytes, bytes);
//  }
//
//
//  private void addPosition(List<Short> positions, ObjectOutputStream oos) throws IOException {
//    if (oos instanceof PositionAwareObjectOutputStream) {
//      positions.add(((PositionAwareObjectOutputStream) oos).position());
//    }
//  }
//
//  private interface Populateable {
//    void populate(ObjectOutputStream oos, List<Short> positions) throws IOException;
//  }
//
//  private <T extends ObjectOutputStream> byte[] populate(Class<T> clz, Populateable p, List<Short> positions) throws IOException {
//    StreamWrapper<T> sw = new StreamWrapper<T>(clz);
//    ObjectOutputStream oos = sw.getObjectOutputStream();
//    p.populate(oos, positions);
//    return sw.getBytes();
//  }
//
//  private static class StreamWrapper<T extends ObjectOutputStream> {
//    ByteArrayOutputStream baos;
//    ObjectOutputStream oos;
//
//    private StreamWrapper(Class<T> clz) throws IOException {
//    baos = new ByteArrayOutputStream();
//    //oos = clz == PositionAwareObjectOutputStream.class ? new PositionAwareObjectOutputStream(baos) : new ObjectOutputStream(baos);
//    oos = Deencapsulation.newInstance(clz, baos); // Above approach produces the same results. Something strange with the instantiation of this stream.
//    }
//
//    private ObjectOutputStream getObjectOutputStream() {
//      return oos;
//    }
//
//    private byte[] getBytes() throws IOException {
//      oos.flush();
//      return baos.toByteArray();
//    }
//  }

}

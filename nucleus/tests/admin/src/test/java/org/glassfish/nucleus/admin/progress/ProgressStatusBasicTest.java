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

package org.glassfish.nucleus.admin.progress;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import static org.glassfish.tests.utils.NucleusTestUtils.*;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 *
 * @author martinmares
 */
@Test(testName="ProgressStatusBasicTest")
public class ProgressStatusBasicTest {

    public void simple() {
        NadminReturn result = nadminWithOutput("progress-simple");
        assertTrue(result.returnValue);
        List<ProgressMessage> prgs = ProgressMessage.grepProgressMessages(result.out);
        assertEquals(12, prgs.size());
        for (int i = 0; i < 11; i++) {
            assertEquals(10 * i, prgs.get(i + 1).getValue());
            assertTrue(prgs.get(i + 1).isPercentage());
        }
        assertTrue(ProgressMessage.isNonDecreasing(prgs));
    }

    public void simpleNoTotal() {
        NadminReturn result = nadminWithOutput("progress-simple", "--nototalsteps");
        assertTrue(result.returnValue);
        List<ProgressMessage> prgs = ProgressMessage.grepProgressMessages(result.out);
        boolean nonPercentageExists = false;
        for (ProgressMessage prg : prgs) {
            if (prg.getValue() != 0 && prg.getValue() != 100) {
                assertFalse(prg.isPercentage());
                nonPercentageExists = true;
            }
        }
        assertTrue(nonPercentageExists);
        assertTrue(ProgressMessage.isNonDecreasing(prgs));
    }

    public void simpleSpecInAnnotation() {
        NadminReturn result = nadminWithOutput("progress-full-annotated");
        assertTrue(result.returnValue);
        List<ProgressMessage> prgs = ProgressMessage.grepProgressMessages(result.out);
        assertEquals(12, prgs.size());
        for (int i = 0; i < 11; i++) {
            assertEquals(10 * i, prgs.get(i + 1).getValue());
            assertTrue(prgs.get(i + 1).isPercentage());
        }
        assertTrue(ProgressMessage.isNonDecreasing(prgs));
        assertEquals("annotated:", prgs.get(5).getScope());
    }

    public void simpleTerse() {
        NadminReturn result = nadminWithOutput("--terse", "progress-simple");
        assertTrue(result.returnValue);
        List<ProgressMessage> prgs = ProgressMessage.grepProgressMessages(result.out);
        assertTrue(prgs.isEmpty());
    }

//    public void commandWithPayloud() throws IOException {
//        File tmp = File.createTempFile(String.valueOf(System.currentTimeMillis()) + "ms_", "test");
//        FileWriter writer = null;
//        try {
//            writer = new FileWriter(tmp);
//            writer.write("This is testing file for nucleus admin tests.\n");
//            writer.write("Created - " + System.currentTimeMillis() + "\n");
//            writer.close();
//            NadminReturn result = nadminWithOutput("progress-payload", tmp.getCanonicalPath());
//            assertTrue(result.returnValue);
//            List<ProgressMessage> prgs = ProgressMessage.grepProgressMessages(result.out);
//            assertTrue(prgs.size() > 0);
//        } finally {
//            try {tmp.delete();} catch (Exception ex) {}
//        }
//    }

}

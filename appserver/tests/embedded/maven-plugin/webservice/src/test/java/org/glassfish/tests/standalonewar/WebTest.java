/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.tests.ejbwar;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import javax.naming.InitialContext;

public class WebTest {

    private static int count = 0;
    private static int EXPECTED_COUNT = 3;

    private String contextPath = "test";

    @BeforeClass
    public static void setup() throws IOException {
    }

    @Test
    public void testWeb() throws Exception {
        goGet("localhost", 8080, "SimpleWebServicePort", contextPath);
    }

    private static void goGet(String host, int port,
                              String result, String contextPath) throws Exception {
        try {
            URL servlet = new URL("http://localhost:8080/test/SimpleWebServiceService?wsdl");
            URLConnection yc = servlet.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String line = null;
            int index;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                index = line.indexOf(result);
                if (index != -1) {
                    index = line.indexOf(":");
                    String status = line.substring(index+1);

                    if (status.equalsIgnoreCase("PASS")){
                        count++;
                    } else {
                        return;
                    }
                }
            }
            Assert.assertTrue(count==3);

        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
   }

}

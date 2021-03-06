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

import java.io.*;
import java.net.*;
import com.sun.ejte.ccl.reporter.*;

/**
 * Unit test for GLASSFISH-18808 [osgi/http] regression of GLASSFISH_13284
 *
 * See also http://java.net/jira/browse/GLASSFISH-13284
 */
public class WebTest{

    private static final String TEST_NAME = "osgi-servlet";

    private static final SimpleReporterAdapter stat=
        new SimpleReporterAdapter("appserv-tests");

    private String host;
    private String port;
    private String contextRoot;

    public WebTest(String[] args) {
        host = args[0];
        port = args[1];
        contextRoot = args[2];
    }

    public static void main(String[] args) {

        stat.addDescription("Unit test for 18808");
        WebTest webTest = new WebTest(args);

        try {
            webTest.doTest(webTest.port, 200);
            stat.addStatus(TEST_NAME, stat.PASS);
        } catch (Exception ex) {
            ex.printStackTrace();
            stat.addStatus(TEST_NAME, stat.FAIL);
        }

        stat.printSummary();
    }

    public void doTest(String port, int expectedResponseStatus) throws Exception {

        URL url = new URL("http://" + host  + ":" + port + "/osgi/aa/bb");
        System.out.println("Connecting to: " + url.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();

        int responseCode = conn.getResponseCode();
        System.out.println("Response code "+responseCode);
        if (responseCode != expectedResponseStatus) {
            throw new Exception("Unexpected return code: " + responseCode +
                ", expected: " + expectedResponseStatus);
        }
    }
}

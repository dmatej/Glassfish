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

import java.io.*;
import java.net.*;
import com.sun.ejte.ccl.reporter.*;

/*
 * Unit test for:
 *
 *  https://glassfish.dev.java.net/issues/show_bug.cgi?id=3823
 *  ("Make 'HttpOnly' flag of JSESSIONIDSSO cookie configurable")
 *
 * This test sets both the 'sso-enabled' and 'sso-cookie-http-only' attribute
 * of the virtual-server named 'server' to true, and expects that
 * the response contains a JSESSIONIDSSO cookie with a 'HttpOnly' flag.
 */
public class WebTest {

    private static final String TEST_NAME = "single-sign-on-cookie-http-only";
    private static final String SSO_COOKIE = "Set-Cookie: JSESSIONIDSSO";

    private static SimpleReporterAdapter stat
        = new SimpleReporterAdapter("appserv-tests");

    private String host;
    private String port;
    private String contextRoot;
    private String adminUser;
    private String adminPassword;

    public WebTest(String[] args) {
        host = args[0];
        port = args[1];
        contextRoot = args[2];
        adminUser = args[3];
        adminPassword = args[4];
    }

    public static void main(String[] args) {

        stat.addDescription("Unit test for GlassFish Issue 3823");
        WebTest webTest = new WebTest(args);

        try {
            webTest.run();
            stat.addStatus(TEST_NAME, stat.PASS);
        } catch( Exception ex) {
            ex.printStackTrace();
            stat.addStatus(TEST_NAME, stat.FAIL);
        }

        stat.printSummary();
    }

    public void run() throws Exception {

        Socket sock = new Socket(host, new Integer(port).intValue());
        OutputStream os = sock.getOutputStream();
        String get = "GET " + contextRoot
            + "/j_security_check?j_username=" + adminUser
            + "&j_password=" + adminPassword
            + " HTTP/1.0\n";
        System.out.println(get);
        os.write(get.getBytes());
        os.write("\n".getBytes());

        InputStream is = sock.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
            if (line.startsWith(SSO_COOKIE)) {
                if (line.indexOf("HttpOnly") == -1) {
                    throw new Exception("Missing 'HttpOnly' flag in '" +
                                        SSO_COOKIE + "' response header");

                }
                break;
            }
        }

        if (line == null) {
            throw new Exception("Missing '" + SSO_COOKIE + "' response header");
        }
    }
}

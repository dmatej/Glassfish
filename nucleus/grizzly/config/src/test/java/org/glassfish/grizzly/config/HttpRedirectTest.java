/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.grizzly.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;

import org.junit.Assert;
import org.junit.Test;

public class HttpRedirectTest extends BaseTestGrizzlyConfig {


    // ------------------------------------------------------------ Test Methods

    @Test
    public void legacyHttpToHttpsRedirect() throws IOException {
        doTest(SocketFactory.getDefault(),
               "legacy-http-https-redirect.xml",
                "/",
                "localhost",
                48480,
                "location: https://localhost:48480/");
    }

    @Test
    public void legacyHttpsToHttpRedirect() throws IOException {
        doTest(getSSLSocketFactory(),
               "legacy-https-http-redirect.xml",
                "/",
                "localhost",
                48480,
                "location: http://localhost:48480/");
    }

    @Test
    public void httpToHttpsSamePortRedirect() throws IOException {
        doTest(SocketFactory.getDefault(),
               "http-https-redirect-same-port.xml",
                "/",
                "localhost",
                48480,
                "location: https://localhost:48480/");
    }

    @Test
    public void httpsToHttpSamePortRedirect() throws IOException {
        doTest(getSSLSocketFactory(),
               "https-http-redirect-same-port.xml",
                "/",
                "localhost",
                48480,
                "location: http://localhost:48480/");
    }

    @Test
    public void httpToHttpsDifferentPortRedirect() throws IOException {
        doTest(getSSLSocketFactory(),
               "http-https-redirect-different-port.xml",
                "/",
                "localhost",
                48480,
                "location: https://localhost:48481/");
    }

    @Test
    public void httpToHttpsWithAttributesRedirect() throws IOException {
        doTest(getSSLSocketFactory(),
                "http-https-redirect-different-port.xml",
                "/index.html?DEFAULT=D:%5Cprojects%5Ceclipse%5CSimpleWAR.war&name=SimpleWAR&contextroot=SimpleWAR&force=true&keepstate=true",
                "localhost",
                48480,
                "location: https://localhost:48481/index.html?DEFAULT=D:%5Cprojects%5Ceclipse%5CSimpleWAR.war&name=SimpleWAR&contextroot=SimpleWAR&force=true&keepstate=true");
    }

    @Test
    public void httpsToHttpDifferentPortRedirect() throws IOException {
        doTest(SocketFactory.getDefault(),
               "https-http-redirect-different-port.xml",
                "/",
                "localhost",
                48480,
                "location: http://localhost:48481/");
    }

    // --------------------------------------------------------- Private Methods


    private void doTest(SocketFactory socketFactory,
                        String configFile,
                        String resourceURL,
                        String host,
                        int port,
                        String expectedLocation) throws IOException {
        GrizzlyConfig grizzlyConfig = new GrizzlyConfig(configFile);
        grizzlyConfig.setupNetwork();
            int count = 0;
            for (GrizzlyListener listener : grizzlyConfig.getListeners()) {
                addStaticHttpHandler((GenericGrizzlyListener) listener, count++);
            }

        try {
            Socket s = socketFactory.createSocket("localhost", 48480);
            OutputStream out = s.getOutputStream();
            out.write(("GET " + resourceURL + " HTTP/1.1\n").getBytes());
            out.write(("Host: " + host + ':' + Integer.toString(port) + '\n').getBytes());
            out.write("\n".getBytes());
            out.flush();
            InputStream in = s.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            boolean found = false;

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.length() > 0 && line.toLowerCase().charAt(0) == 'l') {
                    final String lineLC = line.toLowerCase();

                    if (lineLC.equals(expectedLocation)) {
                        found = true;
                        break;
                    }

                    if (tryAlias(expectedLocation, lineLC)) {
                        found = true;
                        break;
                    }

                    // will fail here
                    Assert.assertEquals(expectedLocation, line.toLowerCase());
                }
            }
            if (!found) {
                Assert.fail("Unable to find Location header in response - no redirect occurred.");
            }
        } catch (Exception e) {
            Assert.fail(e.toString());
        } finally {
            grizzlyConfig.shutdownNetwork();
        }
    }

//    @Override
//    protected void setRootFolder(final GrizzlyServiceListener listener, final int count) {
//
//        final StaticResourcesAdapter adapter = (StaticResourcesAdapter) listener.getEmbeddedHttp().getAdapter();
//        final String name = System.getProperty("java.io.tmpdir", "/tmp") + "/"
//            + Dom.convertName(getClass().getSimpleName()) + count;
//        File dir = new File(name);
//        dir.mkdirs();
//
//        adapter.addRootFolder(name);
//
//    }

    /**
     * Check the localhost aliases, cause server might return not localhost, but 127.0.0.1
     */
    private boolean tryAlias(String expectedLocation, String line) throws IOException {
        final InetAddress[] ias = InetAddress.getAllByName("localhost");
        if (ias != null) {
            for (InetAddress ia : ias) {
                String byHost = ia.getHostName();
                String byAddr = ia.getHostAddress();

                String alias1 = expectedLocation.replace("localhost", byHost).toLowerCase();
                if (alias1.equals(line)) {
                    return true;
                }

                String alias2 = expectedLocation.replace("localhost", byAddr).toLowerCase();
                if (alias2.equals(line)) {
                    return true;
                }
            }
        }

        return false;
    }
}

/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.tests.embedded.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.URLConnection;
import org.apache.catalina.logger.SystemOutLogger;
import org.glassfish.api.deployment.DeployCommandParameters;
import org.glassfish.embeddable.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Amy Roh
 */
public class EmbeddedJSPTest {

    static GlassFish glassfish;
    static File path;

    @BeforeClass
    public static void setupServer() throws GlassFishException {
        GlassFishProperties gp = new GlassFishProperties();
        gp.setPort("http-listener", 8080);
        glassfish = GlassFishRuntime.bootstrap().newGlassFish(gp);
        glassfish.start();
        System.out.println("================ Embedded JSP Test");
    }

    @Test
    public void testDefaultStart() throws Exception {

        Deployer deployer = glassfish.getDeployer();

        path = new File("src/main/resources/embedded-webapi-tests.war");

        String name = null;

        if (path.getName().lastIndexOf('.') != -1) {
            name = path.getName().substring(0, path.getName().lastIndexOf('.'));
        } else {
            name = path.getName();
        }

        System.out.println("Deploying " + path + ", name = " + name);

        String appName = deployer.deploy(path.toURI(), "--name=" + name);

        System.out.println("Deployed " + appName);

        Assert.assertTrue(appName != null);

        /*
        URL servlet = new URL("http://localhost:8080/hellojsp/index.jsp");
        URLConnection yc = servlet.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null){
            sb.append(inputLine);
        }
        in.close();
        System.out.println(inputLine);
        */

        if (appName!=null)
            deployer.undeploy(appName);

        System.out.println("Undeployed "+appName);
    }

    @AfterClass
    public static void shutdownServer() throws GlassFishException {
        System.out.println("Stopping server " + glassfish);
        if (glassfish != null) {
            glassfish.stop();
            glassfish.dispose();
            glassfish = null;
        }
    }

}

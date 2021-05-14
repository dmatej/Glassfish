/*
 * Copyright (c) 2009, 2020 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.tests.embedded.cooked;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.internal.embedded.*;
import org.glassfish.api.deployment.DeployCommandParameters;
import org.glassfish.api.admin.*;
import org.glassfish.api.container.Sniffer;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.AfterClass;

import java.io.File;
import java.util.Enumeration;

/**
 * Test embedded API with an existing domain.xml
 *
 * @author Jerome Dochez
 */
public class ExistingDomainTest {
    static Server server;

    @BeforeClass
    public static void setupServer() throws Exception {
        System.out.println("setup started with gf installation " + System.getProperty("basedir"));
        File f = new File(System.getProperty("basedir"));
        f = new File(f, "target");
        f = new File(f, "dependency");
        f = new File(f, "glassfish6");
        f = new File(f, "glassfish");
        if (f.exists()) {
            System.out.println("Using gf at " + f.getAbsolutePath());
        } else {
            System.out.println("GlassFish not found at " + f.getAbsolutePath());
            Assert.assertTrue(f.exists());
        }
        try {
            EmbeddedFileSystem.Builder efsb = new EmbeddedFileSystem.Builder();
            efsb.installRoot(f, false);
            // find the domain root.
            f = new File(f,"domains");
            f = new File(f, "domain1");
            Assert.assertTrue(f.exists());
            efsb.instanceRoot(f);

            Server.Builder builder = new Server.Builder("inplanted");
            builder.embeddedFileSystem(efsb.build());
            server = builder.build();
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //@Test
    public void testWeb() throws Exception {
        System.out.println("test web");
        File f = new File(System.getProperty("basedir"));
        f = new File(f, "target");
        f = new File(f, "test-classes");
        ScatteredArchive.Builder builder = new ScatteredArchive.Builder("hello", f);
        builder.addClassPath(f.toURI().toURL());
        builder.resources(f);
        server.createPort(8080);
        server.addContainer(server.createConfig(ContainerBuilder.Type.web));
        DeployCommandParameters dp = new DeployCommandParameters(f);
        ScatteredArchive war = builder.buildWar();
        System.out.println("War content");
        Enumeration<String> contents = war.entries();
        while(contents.hasMoreElements()) {
            System.out.println(contents.nextElement());
        }
        String appName = server.getDeployer().deploy(builder.buildWar(), dp);
        server.getDeployer().undeploy(appName, null);
    }

    @Test
    public void Test() {

        ServiceLocator habitat = server.getHabitat();
        System.out.println("Process type is " + habitat.<ProcessEnvironment>getService(ProcessEnvironment.class).getProcessType());
        for (Sniffer s : habitat.<Sniffer>getAllServices(Sniffer.class)) {
            System.out.println("Got sniffer " + s.getModuleType());
        }
    }

    @AfterClass
    public static void shutdownServer() throws Exception {
        System.out.println("shutdown initiated for server " + server);
        if (server!=null) {
            try {
                server.stop();
            } catch (LifecycleException e) {
                e.printStackTrace();
                throw e;
            }
        }


    }

}

/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.configapi.tests.extensibility;

import com.sun.enterprise.config.serverbeans.Application;
import com.sun.enterprise.configapi.tests.ConfigApiTest;
import com.sun.enterprise.config.serverbeans.Config;
import com.sun.enterprise.config.serverbeans.Domain;
import org.glassfish.tests.utils.Utils;
import org.glassfish.api.admin.config.Container;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * @author Jerome Dochez
 */
public class ContainerExtensionTest extends ConfigApiTest {


    ServiceLocator habitat = Utils.instance.getHabitat(this);

    @Override
    public String getFileName() {
        return "Extensibility";
    }

    @Test
    public void existenceTest() {

        Config config = habitat.<Domain>getService(Domain.class).getConfigs().getConfig().get(0);
        List<Container> containers = config.getContainers();
        assertTrue(containers.size()==2);
        RandomContainer container = (RandomContainer) containers.get(0);
        assertEquals("random", container.getName());
        assertEquals("1243", container.getNumberOfRuntime());
        RandomElement element = container.getRandomElement();
        assertNotNull(element);
        assertEquals("foo", element.getAttr1());
    }

    @Test
    public void extensionTest() {
        Config config = habitat.<Domain>getService(Domain.class).getConfigs().getConfig().get(0);
        RandomExtension extension = config.getExtensionByType(RandomExtension.class);
        assertNotNull(extension);
        assertEquals("foo", extension.getSomeAttribute());
    }

    @Test
    public void applicationExtensionTest() {
        Application a = habitat.getService(Application.class);
        List<AnApplicationExtension> taes = a.getExtensionsByType(AnApplicationExtension.class);
        assertEquals(taes.size(), 2);
    }
}

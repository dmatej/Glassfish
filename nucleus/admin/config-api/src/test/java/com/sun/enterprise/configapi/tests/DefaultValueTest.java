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

package com.sun.enterprise.configapi.tests;

import org.glassfish.grizzly.config.dom.*;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.Dom;

/**
 * Test attribute and raw attribute access *
 */
public class DefaultValueTest extends ConfigApiTest {

    NetworkListener listener;

    public String getFileName() {
        return "DomainTest";
    }

    @Before
    public void setup() {
        NetworkListeners httpService = getHabitat().getService(NetworkListeners.class);
        listener = httpService.getNetworkListener().get(0);

    }

    @Test
    public void rawAttributeTest() throws NoSuchMethodException {

        String address = listener.getAddress();

        Dom raw = Dom.unwrap(listener);
        Attribute attr = raw.getProxyType().getMethod("getAddress").getAnnotation(Attribute.class);
        assertEquals(attr.defaultValue(), address);

        assertEquals(raw.attribute("address"), address);
        assertEquals(raw.rawAttribute("address"), address);

    }

    @Test
    public void defaultValueTest() {
        Protocols protocols = getHabitat().getService(Protocols.class);
        for (Protocol protocol : protocols.getProtocol()) {
            Http http = protocol.getHttp();
            System.out.println(http.getCompressableMimeType());
        }

    }

}

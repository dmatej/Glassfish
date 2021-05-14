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

package org.glassfish.nucleus.admin.rest;

import java.util.Map;
import jakarta.ws.rs.core.Response;
import org.glassfish.admin.rest.client.utils.MarshallingUtils;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 *
 * @author jasonlee
 */
public class MetadataTest extends RestTestBase {
    protected static final String URL_CONFIG = "/domain/configs/config.json";
    protected static final String URL_UPTIMECOMMAND = "/domain/uptime.json";

    @Test
    public void configParameterTest() {
        Response response = options(URL_CONFIG);
        assertTrue(isSuccess(response));
        // Really dumb test.  Should be good enough for now

        Map extraProperties = MarshallingUtils.buildMapFromDocument(response.readEntity(String.class));
        assertNotNull(extraProperties);

        // Another dumb test to make sure that "name" shows up on the HTML page
        response = getClient().target(getAddress(URL_CONFIG)).request().get(Response.class);
        assertTrue(response.readEntity(String.class).contains("extraProperties"));
    }

    @Test
    public void UpTimeMetadaDataTest() {
        Response response = options(URL_UPTIMECOMMAND);
        assertTrue(isSuccess(response));

        Map extraProperties = MarshallingUtils.buildMapFromDocument(response.readEntity(String.class));
        assertNotNull(extraProperties);

        // Another dumb test to make sure that "extraProperties" shows up on the HTML page
        response = getClient().target(getAddress(URL_UPTIMECOMMAND)).request().get(Response.class);
        String resp = response.readEntity(String.class);
        assertTrue(resp.contains("extraProperties"));
        // test to see if we get the milliseconds parameter description which is an
        //optional param metadata for the uptime command
        assertTrue(resp.contains("milliseconds"));
        assertTrue(resp.contains("GET"));
    }
}

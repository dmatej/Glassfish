/*
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.config.support;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.*;
import javax.xml.stream.*;

/**
 * Instances and DAS' are quite different
 *
 * @author Byron Nevins
 */
abstract class ServerReaderFilter extends XMLStreamReaderFilter {
    ServerReaderFilter(URL theDomainXml, XMLInputFactory theXif) throws XMLStreamException {

        try {
            domainXml = theDomainXml;
            xif = theXif;
            stream = domainXml.openStream();
            setParent(xif.createXMLStreamReader(stream, Charset.defaultCharset().toString()));
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    final public void close() throws XMLStreamException {
        try {
            super.close();
            stream.close();
        } catch (Exception e) {
            throw new XMLStreamException(e);
        }
    }

    /**
     * Report on whether parsing was a success or not. If there is a missing config for a server just return a String
     * message.
     *
     * @return a String error message if there was an error else return null for all-ok
     */
    abstract String configWasFound();

    final URL domainXml;
    final XMLInputFactory xif;
    final InputStream stream;
}

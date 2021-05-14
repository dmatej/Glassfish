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

package com.sun.enterprise.v3.server;

import org.glassfish.config.support.GlassFishDocument;
import org.glassfish.config.support.DomainXml;
import org.glassfish.internal.api.*;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.config.DomDocument;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Subclass of domain.xml loader service to ensure that hk2 threads have access
 * to the common class loader classes.
 *
 * @author Jerome Dochez
 */
@Service
public class GFDomainXml extends DomainXml {

    /**
     * Returns the DomDocument implementation used to create config beans and persist
     * the DOM tree.
     *
     * @return an instance of a DomDocument (or subclass)
     */
    protected DomDocument getDomDocument() {
        return new GlassFishDocument(habitat,
                    Executors.newCachedThreadPool(new ThreadFactory() {

                        public Thread newThread(Runnable r) {
                            Thread t = Executors.defaultThreadFactory().newThread(r);
                            t.setDaemon(true);
                            t.setContextClassLoader(habitat.<ServerContext>getService(ServerContext.class).getCommonClassLoader());
                            return t;
                        }

                    }));
    }
}

/*
 * Copyright (c) 2002, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jndi.ldap.ext;

import java.io.IOException;

import javax.naming.ConfigurationException;
import javax.naming.NamingException;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;

/**
 * This class implements the LDAPv3 Extended Request for BulkImportStart. The
 * BulkImportStartRequest is used to mark the beginning of a bulk
 * import operation.
 * <p>
 * The bulk import extended operations allow importing entries
 * remotely with a series of LDAP add operations. When the bulk import
 * operation starts it must be finished by sending the
 * {@link BulkImportFinishedRequest} before the normal LDAP operations
 * can resume. Only LDAP add operations are legal between BulkImportStart and
 * BulkImportFinished operations.
 * <p>
 * Note that to add entries using JNDI, use the context methods {@link javax.naming.Context#createSubcontext(javax.naming.Name) Context.createSubcontext}
 * or {@link javax.naming.Context#bind(javax.naming.Name, java.lang.Object) Context.bind}.
 * <p>
 * <b>WARNING:</b> Users have to be extremely careful when using bulk import
 * operations. Once a bulk import has begun, the previous contents under the
 * naming context tree are erased. When a bulk import is started, if the
 * connection is aborted before the bulk import finished is sent, no entries are
 * imported and the previous contents under the naming context tree are wiped
 * out of the directory.
 * <p>
 * The object identifier for BulkImportStart is 2.16.840.1.113730.3.5.7
 * and the extended request value is the naming context to import to.
 *
 * <p>
 * The following code sample shows how the extended operation may be used:
 * <pre>
 *     // create an initial context using the supplied environment properties
 *     LdapContext ctx = new InitialLdapContext(env, null);
 *
 *     // The naming context to import to
 *     String namingContext;
 *
 *     // Bulk import starts
 *     ctx.extendedOperation(new BulkImportStartRequest(
 *                     namingContext));
 *     System.out.println("Bulk import operation begins");
 *
 *     // Add entries
 *     ctx.createSubcontext(entryName, entryAttrs);
 *           :
 *         :
 *     // Bulk import done
 *     ctx.extendedOperation(new BulkImportFinishedRequest());
 *     System.out.println("Bulk import operation finished");
 * </pre>
 *
 * @see BulkImportFinishedRequest
 * @author Jayalaxmi Hangal
 */

public class BulkImportStartRequest implements ExtendedRequest {

    /**
     * The BulkImportStart extended operation's assigned object identifier
     * is  2.16.840.1.113730.3.5.7
     */
    public static final String OID = "2.16.840.1.113730.3.5.7";

    /**
     * ASN1 Ber encoded value of the extended request
     * @serial
     */
    private byte[] value;

    private static final long serialVersionUID = 8280455967681862705L;

    /**
     * Constructs a BulkImportStart extended request.
     *
     * @param importName The naming context to import to.
     * The naming context is one of the values of the <tt>namingContexts</tt>
     * attribute contained in the servers' rootDSE entry.
     * @exception IOException If a BER encoding error occurs.
     */
    public BulkImportStartRequest(String importName)
        throws IOException {
        value = importName.getBytes("UTF8");
    }

    /**
     * Retrieves the BulkImportStart request's object identifier string.
     *
     * @return The non-null object identifier string.
     */
    @Override
    public String getID() {
        return OID;
    }

    /**
     * Retrieves the BulkImportStart request's ASN.1 BER encoded value.
     *
     * @return The ASN.1 BER encoded value of the LDAP extended request.
     */
    @Override
    public byte[] getEncodedValue() {
        return value;
    }

    /**
     * Creates an extended response object that corresponds to the
     * LDAP BulkImportStart extended request.
     * <p>
     */
    @Override
    public ExtendedResponse createExtendedResponse(String id, byte[] berValue,
        int offset, int length) throws NamingException {

        // Confirm that the object identifier is correct
        if ((id != null) && (!id.equals(OID))) {
            throw new ConfigurationException(
                "BulkImportStart received the following response instead of " + OID + ": " + id);
        }
        return new EmptyExtendedResponse(id);
    }
}

/*
 * Copyright (c) 2003, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jndi.ldap.obj;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;

/**
 * A representation of the LDAP groupOfUniqueNames object class.
 * This is a static group: its members are listed in the group's
 * uniqueMember LDAP attribute.
 * <p>
 * Note that when a <tt>GroupOfUniqueNames</tt> object is created by the
 * application program then most of its methods throw
 * {@link IllegalStateException}
 * until the program binds the object in the directory. However, when a
 * <tt>GroupOfUniqueNames</tt> object is returned to the application program
 * then the object is already bound in the directory and its methods function
 * normally.
 * <p>
 * A <tt>GroupOfUniqueNames</tt> instance is not synchronized against concurrent
 * multithreaded access. Multiple threads trying to access and modify a
 * <tt>GroupOfUniqueNames</tt> should lock the object.
 * <p>
 * In order to bind a <tt>GroupOfUniqueNames</tt> object in the directory, the
 * following LDAP object class definition (RFC 2256) must be supported in the
 * directory schema:
 * <pre>
 *     ( 2.5.6.17 NAME 'groupOfUniqueNames'
 *        SUP top
 *        STRUCTURAL
 *        MUST ( uniqueMember $
 *               cn )
 *        MAY ( businessCategory $
 *              seeAlso $
 *              owner $
 *              ou $
 *              o $
 *              description ) )
 * </pre>
 * See
 * {@link javax.naming.directory.DirContext#bind(javax.naming.Name,
 * java.lang.Object, javax.naming.directory.Attributes) DirContext.bind}
 * for details on binding an object in the directory.
 * <p>
 * The code sample in {@link GroupOfNames} shows how the class may be used.
 *
 * @author Vincent Ryan
 */
public class GroupOfUniqueNames extends GroupOfNames {

    private static final boolean debug = false;
    private static final String OBJECT_CLASS = "groupOfUniqueNames";
    private static final String MEMBER_ATTR_ID = "uniqueMember";
    private static final String MEMBER_FILTER_EXPR = "(uniquemember={0})";
    private static final Attribute OBJECT_CLASS_ATTR;
    static {
        OBJECT_CLASS_ATTR = new BasicAttribute("objectClass", "top");
        OBJECT_CLASS_ATTR.add(OBJECT_CLASS);
    }

    /**
     * Create an empty group object.
     * <p>
     * Note that the newly constructed object does not represent a group in
     * the directory until it is bound by using
     * {@link javax.naming.directory.DirContext#bind(javax.naming.Name,
     * java.lang.Object, javax.naming.directory.Attributes) DirContext.bind}.
     */
    public GroupOfUniqueNames() {
        super(OBJECT_CLASS_ATTR, MEMBER_ATTR_ID, MEMBER_FILTER_EXPR, null);
    }

    /**
     * Create a group object with an initial set of members.
     * <p>
     * Note that the newly constructed object does not represent a group in
     * the directory until it is bound by using
     * {@link javax.naming.directory.DirContext#bind(javax.naming.Name,
     * java.lang.Object, javax.naming.directory.Attributes) DirContext.bind}.
     *
     * @param members The set of initial members. It may be null.
     *                Each element is of class {@link String} or
     *                {@link java.security.Principal}
     */
    public GroupOfUniqueNames(Set members) {
        super(OBJECT_CLASS_ATTR, MEMBER_ATTR_ID, MEMBER_FILTER_EXPR, members);
    }

    /**
     * Create a group object from its entry in the directory.
     */
    private GroupOfUniqueNames(String groupDN, DirContext ctx, Name name, Hashtable env, Attributes attributes) {
        super(OBJECT_CLASS_ATTR, MEMBER_ATTR_ID, MEMBER_FILTER_EXPR, null, groupDN, ctx, name, env, attributes);
    }

    /**
     * Create a group object from its entry in the directory.
     * This method is called by {@link LdapGroupFactory}
     *
     * @param groupDN The group's distinguished name.
     * @param ctx An LDAP context.
     * @param name The group's name relative to the context.
     * @param env The context's environment properties.
     * @param attributes The group's LDAP attributes.
     * @return Object The new object instance.
     */
    // package private (used by LdapGroupFactory)
    static Object getObjectInstance(String groupDN, DirContext ctx, Name name,
        Hashtable env, Attributes attributes) {
        if (debug) {
            System.out.println("[debug] creating a group named: " + name);
        }
        return new GroupOfUniqueNames(groupDN, ctx, name, env, attributes);
    }

    /**
     * Determines whether the supplied LDAP objectClass attribute matches that
     * of the group. A match occurs if the argument contains the value
     * "GroupOfUniqueNames".
     *
     * @param objectClass The non-null objectClass attribute to check against.
     * @return true if the objectClass attributes match; false otherwise.
     */
    // package private (used by LdapGroupFactory)
    static boolean matches(Attribute objectClass) {
        try {
            for (Enumeration values = objectClass.getAll();
                values.hasMoreElements(); ) {
                if (OBJECT_CLASS.equalsIgnoreCase(
                    (String)values.nextElement())) {
                    return true;
                }
            }
        } catch (NamingException e) {
            if (debug) {
                System.out.println("[debug] error matching objectClass: " + e);
            }
        }
        return false;
    }
}

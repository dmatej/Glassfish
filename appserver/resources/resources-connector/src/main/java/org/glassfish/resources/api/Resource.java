/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.resources.api;

import java.util.*;

import static org.glassfish.resources.admin.cli.ResourceConstants.*;

/**
 * Class which represents the Resource.
 */
public class Resource {
    public static final String CUSTOM_RESOURCE          = "custom-resource";
    public static final String CONNECTOR_RESOURCE       = "connector-resource";
    public static final String ADMIN_OBJECT_RESOURCE    = "admin-object-resource";
    public static final String JDBC_RESOURCE            = "jdbc-resource";
    public static final String MAIL_RESOURCE            = "mail-resource";
    public static final String EXTERNAL_JNDI_RESOURCE   = "external-jndi-resource";

    public static final String JDBC_CONNECTION_POOL     = "jdbc-connection-pool";
    public static final String CONNECTOR_CONNECTION_POOL = "connector-connection-pool";

    public static final String RESOURCE_ADAPTER_CONFIG  = "resource-adapter-config";
    public static final String PERSISTENCE_MANAGER_FACTORY_RESOURCE = "persistence-manager-factory-resource";
    public static final String CONNECTOR_SECURITY_MAP    = "security-map";
    public static final String CONNECTOR_WORK_SECURITY_MAP    = "work-security-map";

    public static final List BINDABLE_RESOURCES = Collections.unmodifiableList(
            Arrays.asList(
                CUSTOM_RESOURCE,
                CONNECTOR_RESOURCE,
                ADMIN_OBJECT_RESOURCE,
                JDBC_RESOURCE,
                MAIL_RESOURCE,
                EXTERNAL_JNDI_RESOURCE
            ));

    public static final List RESOURCE_POOL = Collections.unmodifiableList(
            Arrays.asList(
                JDBC_CONNECTION_POOL,
                CONNECTOR_CONNECTION_POOL
            ));

    private String resType;
    private HashMap attrList = new HashMap();
    private Properties props = new Properties();
    private String sDescription = null;

    public Resource(String type) {
        resType = type;
    }

    public String getType() {
        return resType;
    }

//Commented from 9.1 as it is not used
/*
    public void setType(String type) {
        resType = type;
    }
*/

    public HashMap getAttributes() {
        return attrList;
    }

    public void setAttribute(String name, String value) {
        attrList.put(name, value);
    }

    public void setAttribute(String name, String[] value) {
        attrList.put(name, value);
    }

    public void setAttribute(String name, Properties value) {
        attrList.put(name, value);
    }

    public void setDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    public String getDescription() {
        return sDescription;
    }

    public void setProperty(String name, String value) {
        props.setProperty(name, value);
    }

//Commented from 9.1 as it is not used
 /*   public void setProperty(String name, String value, String desc) {
        // TO DO:
    }*/

    public Properties getProperties() {
        return props;
    }

    //Used to figure out duplicates in a List<Resource>
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if ( !(obj instanceof Resource) ) return false;
        Resource r = (Resource)obj;
        return r.getType().equals(this.getType()) &&
                //No need to compare description for equality
                //r.getDescription().equals(this.getDescription()) &&
                r.getProperties().equals(this.getProperties()) &&
                r.getAttributes().equals(this.getAttributes());
    }

    //when a class overrides equals, override hashCode as well.
    @Override
    public int hashCode() {
        return this.getAttributes().hashCode() +
        this.getProperties().hashCode() +
        this.getType().hashCode();
        //description is not used to generate hashcode
        //this.getDescription().hashCode();
    }

    //Used to figure out conflicts in a List<Resource>
    //A Resource is said to be in conflict with another Resource if the two
    //Resources have the same Identity [attributes that uniquely identify a Resource]
    //but different properties
    public boolean isAConflict(Resource r) {
        //If the two resource have the same identity
        if (hasSameIdentity(r)) {
            //If the two resources are not equal, then there is
            //conflict
            if (!r.equals(this))
                return true;
        }
        return false;
    }

    /**
     * Checks if the specified resource has the same identity as
     * this resource.
     */
    private boolean hasSameIdentity(Resource r) {

        //For two resources to have the same identity, atleast their types should match

        if(BINDABLE_RESOURCES.contains(this.getType())){
            if(!BINDABLE_RESOURCES.contains(r.getType())){
                return false;
            }
        }else if (RESOURCE_POOL.contains(this.getType())){
            if(!RESOURCE_POOL.contains(r.getType())){
                return false;
            }
        }else if (!(r.getType().equals(this.getType()))) {
            return false;
        }

        String rType = r.getType();

        //For all resources, their identity is their jndi-name
        if (rType.equals(CUSTOM_RESOURCE)|| rType.equals(EXTERNAL_JNDI_RESOURCE)
             || rType.equals(JDBC_RESOURCE)|| rType.equals(PERSISTENCE_MANAGER_FACTORY_RESOURCE)
             || rType.equals(CONNECTOR_RESOURCE)|| rType.equals(ADMIN_OBJECT_RESOURCE) || rType.equals(MAIL_RESOURCE)) {
            return isEqualAttribute(r, JNDI_NAME);
        }

        //For pools the identity is limited to pool name
        if (rType.equals(JDBC_CONNECTION_POOL) || rType.equals(CONNECTOR_CONNECTION_POOL)) {
            return isEqualAttribute(r, CONNECTION_POOL_NAME);
        }

        if (rType.equals(RESOURCE_ADAPTER_CONFIG)) {
            return isEqualAttribute(r, RES_ADAPTER_NAME);
        }

        if(rType.equals(CONNECTOR_WORK_SECURITY_MAP)){
            return isEqualAttribute(r,WORK_SECURITY_MAP_NAME) && isEqualAttribute(r, WORK_SECURITY_MAP_RA_NAME);
        }

        return false;
    }

    /**
     * Compares the attribute with the specified name
     * in this resource with the passed in resource and checks
     * if they are <code>equal</code>
     */
    private boolean isEqualAttribute(Resource r, String name) {
        return (getAttribute(r, name).equals(getAttribute(this, name)));
    }

    /**
     * Utility method to get an <code>Attribute</code> of the given name
     * in the specified resource
     */
    private String getAttribute(Resource r, String name) {
        return (String) r.getAttributes().get(name);
    }

    @Override
    public String toString(){

        String rType = getType();
        String identity = "";
        if (rType.equals(CUSTOM_RESOURCE)|| rType.equals(EXTERNAL_JNDI_RESOURCE)
             || rType.equals(JDBC_RESOURCE)|| rType.equals(PERSISTENCE_MANAGER_FACTORY_RESOURCE)
             || rType.equals(CONNECTOR_RESOURCE)|| rType.equals(ADMIN_OBJECT_RESOURCE) || rType.equals(MAIL_RESOURCE)) {
            identity =  getAttribute(this, JNDI_NAME);
        }else if (rType.equals(JDBC_CONNECTION_POOL) || rType.equals(CONNECTOR_CONNECTION_POOL)) {
            identity =  getAttribute(this, CONNECTION_POOL_NAME);
        }else if (rType.equals(RESOURCE_ADAPTER_CONFIG)) {
            identity =  getAttribute(this, RESOURCE_ADAPTER_CONFIG_NAME);
        }else if(rType.equals(CONNECTOR_WORK_SECURITY_MAP)){
            identity = getAttribute(this, WORK_SECURITY_MAP_NAME);
        }

        return identity + " of type " + resType;
    }

}

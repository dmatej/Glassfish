/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.admingui.devtests;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConnectorsTest extends BaseSeleniumTestClass {
    private static final String TRIGGER_CONNECTOR_CONNECTION_POOLS = "i18njca.connectorConnectionPools.pageTitleHelp";
    private static final String TRIGGER_NEW_CONNECTOR_CONNECTION_POOL_STEP_1 = "i18njca.connectorConnectionPool.step1PageTitle";
    private static final String TRIGGER_NEW_CONNECTOR_CONNECTION_POOL_STEP_2 = "i18njca.connectorConnectionPool.step2PageTitle";
    public static final String TRIGGER_EDIT_CONNECTOR_CONNECTION_POOL = "i18njca.connectorConnectionPool.editPageTitleHelp";
    public static final String TRIGGER_ADVANCE_CONNECTOR_CONNECTION_POOL = "i18njca.connectorConnectionPool.advancePageTitleHelp";
    public static final String TRIGGER_PROPS_CONNECTOR_CONNECTION_POOL = "i18njca.connectorConnectionPool.propertyPageTitleHelp";
    private static final String TRIGGER_CONNECTOR_RESOURCE = "i18njca.connectorResources.pageTitleHelp";
    private static final String TRIGGER_NEW_CONNECTOR_RESOURCE = "i18njca.connectorResource.newPageTitle";
    public static final String TRIGGER_EDIT_CONNECTOR_RESOURCE = "i18njca.connectorResource.editPageTitle";

    public static final String TRIGGER_CONNECTOR_SECURITY_MAPS = "i18njca.connectorSecurityMaps.pageTitle";
    private static final String TRIGGER_NEW_CONNECTOR_SECURITY_MAP = "i18njca.connectorSecurityMap.newPageTitle";
    private static final String TRIGGER_EDIT_CONNECTOR_SECURITY_MAP = "i18njca.connectorSecurityMap.editPageTitle";

    @Test
    public void testConnectorResources() {
        String testPool = "connectorPool" + generateRandomString();
        String testConnector = "connectorResource" + generateRandomString();

        StandaloneTest standaloneTest = new StandaloneTest();
        ClusterTest clusterTest = new ClusterTest();
        standaloneTest.deleteAllStandaloneInstances();
        clusterTest.deleteAllClusters();

        clickAndWait("treeForm:tree:resources:Connectors:connectorConnectionPools:connectorConnectionPools_link", TRIGGER_CONNECTOR_CONNECTION_POOLS);

        // Create new connection connection pool
        clickAndWait("propertyForm:poolTable:topActionsGroup1:newButton", TRIGGER_NEW_CONNECTOR_CONNECTION_POOL_STEP_1);

        setFieldValue("propertyForm:propertySheet:generalPropertySheet:jndiProp:name", testPool);
        selectDropdownOption("propertyForm:propertySheet:generalPropertySheet:resAdapterProp:db", "jmsra");
        waitForCondition("document.getElementById('propertyForm:propertySheet:generalPropertySheet:connectionDefProp:db').value != ''", 10000);

        selectDropdownOption("propertyForm:propertySheet:generalPropertySheet:connectionDefProp:db", "jakarta.jms.QueueConnectionFactory");
        waitForButtonEnabled("propertyForm:title:topButtons:nextButton");

        clickAndWait("propertyForm:title:topButtons:nextButton", TRIGGER_NEW_CONNECTOR_CONNECTION_POOL_STEP_2);

        selectDropdownOption("propertyForm:propertySheet:poolPropertySheet:transprop:trans", "NoTransaction");
        clickAndWait("propertyForm:propertyContentPage:topButtons:finishButton", TRIGGER_CONNECTOR_CONNECTION_POOLS);
        assertTrue(isTextPresent(testPool));

        // Create new connector resource which uses this new pool
        clickAndWait("treeForm:tree:resources:Connectors:connectorResources:connectorResources_link", TRIGGER_CONNECTOR_RESOURCE);

        clickAndWait("propertyForm:resourcesTable:topActionsGroup1:newButton", TRIGGER_NEW_CONNECTOR_RESOURCE);

        setFieldValue("form:propertySheet:propertSectionTextField:jndiTextProp:jnditext", testConnector);
        selectDropdownOption("form:propertySheet:propertSectionTextField:poolNameProp:PoolName", testPool);

        clickAndWait("form:propertyContentPage:topButtons:newButton", TRIGGER_CONNECTOR_RESOURCE);

        // Disable resource
        testDisableButton(testConnector,
                "propertyForm:resourcesTable",
                "propertyForm:resourcesTable:topActionsGroup1:button3",
                "propertyForm:propertySheet:propertSectionTextField:statusProp:enabled",
                "propertyForm:propertyContentPage:topButtons:cancelButton",
                TRIGGER_CONNECTOR_RESOURCE,
                TRIGGER_EDIT_CONNECTOR_RESOURCE,
                "off");

        // Enable resource
        testEnableButton(testConnector,
                "propertyForm:resourcesTable",
                "propertyForm:resourcesTable:topActionsGroup1:button2",
                "propertyForm:propertySheet:propertSectionTextField:statusProp:enabled",
                "propertyForm:propertyContentPage:topButtons:cancelButton",
                TRIGGER_CONNECTOR_RESOURCE,
                TRIGGER_EDIT_CONNECTOR_RESOURCE,
                "on");

        // Delete connector resource
        deleteRow("propertyForm:resourcesTable:topActionsGroup1:button1", "propertyForm:resourcesTable", testConnector);

        // Delete connector connection pool
        clickAndWait("treeForm:tree:resources:Connectors:connectorConnectionPools:connectorConnectionPools_link", TRIGGER_CONNECTOR_CONNECTION_POOLS);

        deleteRow("propertyForm:poolTable:topActionsGroup1:button1", "propertyForm:poolTable", testPool);
    }

    @Test
    public void testConnectorResourcesWithTargets() {
        String testPool = "connectorPool" + generateRandomString();
        String testConnector = "connectorResource" + generateRandomString();
        final String instanceName = "standalone" + generateRandomString();
        final String enableStatus = "Enabled on 2 of 2 Target(s)";
        final String disableStatus = "Enabled on 0 of 2 Target(s)";

        clickAndWait("treeForm:tree:resources:Connectors:connectorConnectionPools:connectorConnectionPools_link", TRIGGER_CONNECTOR_CONNECTION_POOLS);

        // Create new connection connection pool
        clickAndWait("propertyForm:poolTable:topActionsGroup1:newButton", TRIGGER_NEW_CONNECTOR_CONNECTION_POOL_STEP_1);

        setFieldValue("propertyForm:propertySheet:generalPropertySheet:jndiProp:name", testPool);
        selectDropdownOption("propertyForm:propertySheet:generalPropertySheet:resAdapterProp:db", "jmsra");
        waitForCondition("document.getElementById('propertyForm:propertySheet:generalPropertySheet:connectionDefProp:db').value != ''", 10000);

        selectDropdownOption("propertyForm:propertySheet:generalPropertySheet:connectionDefProp:db", "jakarta.jms.QueueConnectionFactory");
        waitForButtonEnabled("propertyForm:title:topButtons:nextButton");

        clickAndWait("propertyForm:title:topButtons:nextButton", TRIGGER_NEW_CONNECTOR_CONNECTION_POOL_STEP_2);

        selectDropdownOption("propertyForm:propertySheet:poolPropertySheet:transprop:trans", "NoTransaction");
        clickAndWait("propertyForm:propertyContentPage:topButtons:finishButton", TRIGGER_CONNECTOR_CONNECTION_POOLS);
        assertTrue(isTextPresent(testPool));

        StandaloneTest instanceTest = new StandaloneTest();
        instanceTest.createStandAloneInstance(instanceName);

        // Create new connector resource which uses this new pool
        clickAndWait("treeForm:tree:resources:Connectors:connectorResources:connectorResources_link", TRIGGER_CONNECTOR_RESOURCE);

        clickAndWait("propertyForm:resourcesTable:topActionsGroup1:newButton", TRIGGER_NEW_CONNECTOR_RESOURCE);

        setFieldValue("form:propertySheet:propertSectionTextField:jndiTextProp:jnditext", testConnector);
        selectDropdownOption("form:propertySheet:propertSectionTextField:poolNameProp:PoolName", testPool);

        int count = addTableRow("form:basicTable", "form:basicTable:topActionsGroup1:addSharedTableButton");
        setFieldValue("form:basicTable:rowGroup1:0:col2:col1St", "property" + generateRandomString());
        setFieldValue("form:basicTable:rowGroup1:0:col3:col1St", "value");
        setFieldValue("form:basicTable:rowGroup1:0:col4:col1St", "description");

        addSelectSelection("form:targetSection:targetSectionId:addRemoveProp:commonAddRemove_available", instanceName);
        addSelectSelection("form:targetSection:targetSectionId:addRemoveProp:commonAddRemove_available", "server");
        pressButton("form:targetSection:targetSectionId:addRemoveProp:commonAddRemove:commonAddRemove_addButton");

        clickAndWait("form:propertyContentPage:topButtons:newButton", TRIGGER_CONNECTOR_RESOURCE);

        assertTrue(isTextPresent(testConnector));

        clickAndWait(getLinkIdByLinkText("propertyForm:resourcesTable", testConnector), TRIGGER_EDIT_CONNECTOR_RESOURCE);

        assertTableRowCount("propertyForm:basicTable", count);
        clickAndWait("propertyForm:propertyContentPage:topButtons:cancelButton", TRIGGER_CONNECTOR_RESOURCE);

        // Disable resource
        testDisableButton(testConnector,
                "propertyForm:resourcesTable",
                "propertyForm:resourcesTable:topActionsGroup1:button3",
                "propertyForm:propertySheet:propertSectionTextField:statusProp2:enabledStr",
                "propertyForm:propertyContentPage:topButtons:cancelButton",
                TRIGGER_CONNECTOR_RESOURCE,
                TRIGGER_EDIT_CONNECTOR_RESOURCE,
                disableStatus);
        // Enable resource
        testEnableButton(testConnector,
                "propertyForm:resourcesTable",
                "propertyForm:resourcesTable:topActionsGroup1:button2",
                "propertyForm:propertySheet:propertSectionTextField:statusProp2:enabledStr",
                "propertyForm:propertyContentPage:topButtons:cancelButton",
                TRIGGER_CONNECTOR_RESOURCE,
                TRIGGER_EDIT_CONNECTOR_RESOURCE,
                enableStatus);

        testManageTargets("treeForm:tree:resources:Connectors:connectorResources:connectorResources_link",
                          "propertyForm:resourcesTable",
                          "propertyForm:targetTable:topActionsGroup1:button2",
                          "propertyForm:targetTable:topActionsGroup1:button3",
                          "propertyForm:propertySheet:propertSectionTextField:statusProp2:enabledStr",
                          "propertyForm:resEditTabs:general",
                          "propertyForm:resEditTabs:targetTab",
                          TRIGGER_CONNECTOR_RESOURCE,
                          TRIGGER_EDIT_CONNECTOR_RESOURCE,
                          testConnector,
                          instanceName);

        // Delete connector resource
        deleteRow("propertyForm:resourcesTable:topActionsGroup1:button1", "propertyForm:resourcesTable", testConnector);

        // Delete connector connection pool
        clickAndWait("treeForm:tree:resources:Connectors:connectorConnectionPools:connectorConnectionPools_link", TRIGGER_CONNECTOR_CONNECTION_POOLS);
        deleteRow("propertyForm:poolTable:topActionsGroup1:button1", "propertyForm:poolTable", testPool);

        //Delete the instance
        clickAndWait("treeForm:tree:standaloneTreeNode:standaloneTreeNode_link", instanceTest.TRIGGER_INSTANCES_PAGE);
        deleteRow("propertyForm:instancesTable:topActionsGroup1:button1", "propertyForm:instancesTable", instanceName);
    }

    @Test
    public void testConnectorSecurityMaps() {
        String testPool = generateRandomString();
        String testSecurityMap = generateRandomString();
        String testGroup = generateRandomString();
        String testPassword = generateRandomString();
        String testUserName = generateRandomString();

        clickAndWait("treeForm:tree:resources:Connectors:connectorConnectionPools:connectorConnectionPools_link", TRIGGER_CONNECTOR_CONNECTION_POOLS);

        // Create new connection connection pool
        clickAndWait("propertyForm:poolTable:topActionsGroup1:newButton", TRIGGER_NEW_CONNECTOR_CONNECTION_POOL_STEP_1);

        setFieldValue("propertyForm:propertySheet:generalPropertySheet:jndiProp:name", testPool);
        selectDropdownOption("propertyForm:propertySheet:generalPropertySheet:resAdapterProp:db", "jmsra");
        waitForCondition("document.getElementById('propertyForm:propertySheet:generalPropertySheet:connectionDefProp:db').value != ''", 10000);

        selectDropdownOption("propertyForm:propertySheet:generalPropertySheet:connectionDefProp:db", "jakarta.jms.QueueConnectionFactory");
        waitForButtonEnabled("propertyForm:title:topButtons:nextButton");

        clickAndWait("propertyForm:title:topButtons:nextButton", TRIGGER_NEW_CONNECTOR_CONNECTION_POOL_STEP_2);

        selectDropdownOption("propertyForm:propertySheet:poolPropertySheet:transprop:trans", "NoTransaction");
        clickAndWait("propertyForm:propertyContentPage:topButtons:finishButton", TRIGGER_CONNECTOR_CONNECTION_POOLS);
        assertTrue(isTextPresent(testPool));
        //Create Connector Security Map
        clickAndWait(getLinkIdByLinkText("propertyForm:poolTable", testPool), TRIGGER_EDIT_CONNECTOR_CONNECTION_POOL);
        clickAndWait("propertyForm:connectorPoolSet:securityMapTab", TRIGGER_CONNECTOR_SECURITY_MAPS);
        clickAndWait("propertyForm:resourcesTable:topActionsGroup1:newButton", TRIGGER_NEW_CONNECTOR_SECURITY_MAP);

        setFieldValue("propertyForm:propertySheet:propertSectionTextField:mapNameNew:mapName", testSecurityMap);
        setFieldValue("propertyForm:propertySheet:propertSectionTextField:groupProp:group", testGroup);
        setFieldValue("propertyForm:propertySheet:propertSectionTextField2:userNameEdit:userNameEdit", testUserName);
        setFieldValue("propertyForm:propertySheet:propertSectionTextField2:passwordEdit:passwordEdit", testPassword);
        clickAndWait("propertyForm:propertyContentPage:topButtons:newButton", TRIGGER_CONNECTOR_SECURITY_MAPS);

        clickAndWait(getLinkIdByLinkText("propertyForm:resourcesTable", testSecurityMap), TRIGGER_EDIT_CONNECTOR_SECURITY_MAP);
        Assert.assertEquals(testGroup, getFieldValue("propertyForm:propertySheet:propertSectionTextField:groupProp:group"));
        clickAndWait("propertyForm:propertyContentPage:topButtons:cancelButton", TRIGGER_CONNECTOR_SECURITY_MAPS);

        //Delete Connector Security Maps
        deleteRow("propertyForm:resourcesTable:topActionsGroup1:button1", "propertyForm:resourcesTable", testSecurityMap);

        // Delete connector connection pool
        clickAndWait("treeForm:tree:resources:Connectors:connectorConnectionPools:connectorConnectionPools_link", TRIGGER_CONNECTOR_CONNECTION_POOLS);
        deleteRow("propertyForm:poolTable:topActionsGroup1:button1", "propertyForm:poolTable", testPool);
    }
}

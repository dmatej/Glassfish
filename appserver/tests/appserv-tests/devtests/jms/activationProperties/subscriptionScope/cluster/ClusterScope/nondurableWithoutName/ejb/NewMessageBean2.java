/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.test.jms.activationproperties.ejb;

import java.util.logging.*;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.jms.*;

@MessageDriven(mappedName = "jms/jms_unit_test_Topic", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionScope", propertyValue = "Cluster"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "NonDurable")
})
public class NewMessageBean2 implements MessageListener {
    private static final Logger logger = Logger.getLogger(NewMessageBean2.class.getName());

    @Resource
    private MessageDrivenContext mdc;

    @Resource(mappedName = "jms/jms_unit_result_Queue")
    private Queue resultQueue;

    @Inject
    @JMSConnectionFactory("jms/jms_unit_test_QCF")
    @JMSSessionMode(JMSContext.AUTO_ACKNOWLEDGE)
    private JMSContext jmsContext;

    public NewMessageBean2() {
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onMessage(Message message) {
        sendMsg(message);
    }

    private void sendMsg(Message msg) {
        JMSProducer producer = jmsContext.createProducer();
        producer.send(resultQueue, msg);
    }
}

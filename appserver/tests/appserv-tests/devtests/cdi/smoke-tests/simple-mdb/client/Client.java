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

package com.sun.s1asdev.cdi.hello.mdb.client;
import java.io.*;
import java.util.*;
import jakarta.ejb.EJBHome;
import jakarta.jms.*;
import jakarta.annotation.Resource;
import javax.naming.InitialContext;

import com.sun.ejte.ccl.reporter.SimpleReporterAdapter;

public class Client {

    // in milli-seconds
    private static long TIMEOUT = 90000;

    private static SimpleReporterAdapter stat =
        new SimpleReporterAdapter("appserv-tests");

    public static void main (String[] args) {
        Client client = new Client(args);

        stat.addDescription("cdi-hello-mdb");
        client.doTest();
        stat.printSummary("cdi-hello-mdbID");
        System.exit(0);
    }


    @Resource(name="FooCF", mappedName="jms/cdi_hello_mdb_QCF")
    private static QueueConnectionFactory queueConFactory;

    @Resource(name="MsgBeanQueue", mappedName="jms/cdi_hello_mdb_InQueue")
    private static jakarta.jms.Queue msgBeanQueue;

    @Resource(name="ClientQueue", mappedName="foo")
    private static jakarta.jms.Queue clientQueue;

    private QueueConnection queueCon;
    private QueueSession queueSession;
    private QueueSender queueSender;
    private QueueReceiver queueReceiver;


    private int numMessages = 2;
    public Client(String[] args) {

        if( args.length == 1 ) {
            numMessages = new Integer(args[0]).intValue();
        }

    }

    public void doTest() {
        try {
            if( queueConFactory == null ) {

                System.out.println("Java SE mode...");
                InitialContext ic = new InitialContext();
                queueConFactory = (jakarta.jms.QueueConnectionFactory) ic.lookup("jms/cdi_hello_mdb_QCF");
                msgBeanQueue = (jakarta.jms.Queue) ic.lookup("jms/cdi_hello_mdb_InQueue");
                clientQueue = (jakarta.jms.Queue) ic.lookup("jms/cdi_hello_mdb_OutQueue");

            }

            setup();
            doTest(numMessages);
            stat.addStatus("cmt main", stat.PASS);
        } catch(Throwable t) {
            stat.addStatus("cmt main", stat.FAIL);
            t.printStackTrace();
        } finally {
            cleanup();
        }
    }

    public void setup() throws Exception {

        queueCon = queueConFactory.createQueueConnection();

        queueSession = queueCon.createQueueSession
            (false, Session.AUTO_ACKNOWLEDGE);

        // Producer will be specified when actual msg is sent.
        queueSender = queueSession.createSender(null);

        queueReceiver = queueSession.createReceiver(clientQueue);

        queueCon.start();

    }

    public void cleanup() {
        try {
            if( queueCon != null ) {
                queueCon.close();
            }
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    public void sendMsgs(jakarta.jms.Queue queue, Message msg, int num)
        throws JMSException {
        for(int i = 0; i < num; i++) {
            System.out.println("Sending message " + i + " to " + queue +
                               " at time " + System.currentTimeMillis());
            queueSender.send(queue, msg);
            System.out.println("Sent message " + i + " to " + queue +
                               " at time " + System.currentTimeMillis());
        }
    }

    public void doTest(int num)
        throws Exception {

        Destination dest = msgBeanQueue;

        Message message = queueSession.createTextMessage("foo");

        message.setBooleanProperty("flag", true);
        message.setIntProperty("num", 2);
        sendMsgs((jakarta.jms.Queue) dest, message, num);

        System.out.println("Waiting for queue message");
        Message recvdmessage = queueReceiver.receive(TIMEOUT);
        if( recvdmessage != null ) {
            System.out.println("Received message : " +
                                   ((TextMessage)recvdmessage).getText());
        } else {
            System.out.println("timeout after " + TIMEOUT + " seconds");
            throw new JMSException("timeout" + TIMEOUT + " seconds");
        }
    }
}


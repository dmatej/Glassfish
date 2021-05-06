/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.s1peqe.transaction.txglobal.ejb.beanB;

import jakarta.ejb.SessionBean;
import jakarta.ejb.SessionContext;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.rmi.RemoteException;

import jakarta.jms.Queue;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.jms.QueueSender;
import jakarta.jms.QueueSession;
import jakarta.jms.JMSException;
import jakarta.jms.QueueReceiver;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueConnectionFactory;


public class TxBeanB implements SessionBean {

    private Queue queue = null;
    private String user = null;
    private String dbURL1 = null;
    private String dbURL2 = null;
    private String password = null;
    private SessionContext ctx = null;
    private QueueConnectionFactory qfactory = null;

    // ------------------------------------------------------------------------
    // Container Required Methods
    // ------------------------------------------------------------------------
    public void setSessionContext(SessionContext sc) {
        System.out.println("setSessionContext in BeanB");
        try {
            ctx = sc;
            Context ic = new InitialContext();
            user = (String) ic.lookup("java:comp/env/user");
            password = (String) ic.lookup("java:comp/env/password");
            dbURL1 = (String) ic.lookup("java:comp/env/dbURL1");
            dbURL2 = (String) ic.lookup("java:comp/env/dbURL2");
        } catch (Exception ex) {
            System.out.println("Exception in setSessionContext: " +
                               ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void ejbCreate() {
        System.out.println("ejbCreate in BeanB");
        try {
            Context context = new InitialContext();

            qfactory = (QueueConnectionFactory)
            context.lookup("java:comp/env/jms/QCFactory");

            queue = (Queue) context.lookup("java:comp/env/jms/SampleQueue");

            System.out.println("QueueConnectionFactory & " +
                               "Queue Initialized Successfully");
        } catch (Exception ex) {
            System.out.println("Exception in ejbCreate: " + ex.toString());
            ex.printStackTrace();
        }
    }

    public void ejbDestroy() {
        System.out.println("ejbDestroy in BeanB");
    }

    public void ejbActivate() {
        System.out.println("ejbActivate in BeanB");
    }

    public void ejbPassivate() {
        System.out.println("ejbPassivate in BeanB");
    }

    public void ejbRemove() {
        System.out.println("ejbRemove in BeanB");
    }

    // ------------------------------------------------------------------------
    // Business Logic Methods
    // ------------------------------------------------------------------------
    public void insert(String acc, float bal) throws RemoteException {
        Connection con1 = null;
        Connection con2 = null;
        System.out.println("insert in BeanB");
        try {
            con1 = getConnection(dbURL1);
            Statement stmt1 = con1.createStatement();
            stmt1.executeUpdate("INSERT INTO txAccount VALUES ('" + acc +
                               "', " + bal + ")");
            System.out.println("Account added Successfully in DB1...");

            con2 = getConnection(dbURL2);
            Statement stmt2 = con2.createStatement();
            stmt2.executeUpdate("INSERT INTO txAccount VALUES ('" + acc +
                                "', " + bal + ")");
            System.out.println("Account added Successfully in DB2...");

            stmt1.close();
            stmt2.close();
        } catch (Exception ex) {
            System.out.println("Exception in insert: " + ex.toString());
            ex.printStackTrace();
        } finally {
            try {
                con1.close();
                con2.close();
            } catch (java.sql.SQLException ex) {
            }
        }
    }

    public void delete(String account) throws RemoteException {
        Connection con1 = null;
        Connection con2 = null;
        System.out.println("delete in BeanB");
        try {
            con1 = getConnection(dbURL1);
            Statement stmt1 = con1.createStatement();
            stmt1.executeUpdate("DELETE FROM txAccount WHERE account = '"
                               + account + "'");
            System.out.println("Account deleted Successfully in DB1...");

            con2 = getConnection(dbURL2);
            Statement stmt2 = con2.createStatement();
            stmt2.executeUpdate("DELETE FROM txAccount WHERE account = '"
                               + account + "'");
            System.out.println("Account deleted Successfully in DB2...");

            stmt1.close();
            stmt2.close();
        } catch (Exception ex) {
            System.out.println("Exception in delete: " + ex.toString());
            ex.printStackTrace();
        } finally {
            try {
                con1.close();
                con2.close();
            } catch (java.sql.SQLException ex) {
            }
        }
    }

    public void sendJMSMessage(String msg) throws RemoteException {
        System.out.println("sendJMSMessage in BeanB");
        try {
            QueueConnection qconn = qfactory.createQueueConnection();
            QueueSession qsession = qconn.createQueueSession(true, 0);
            QueueSender sender = qsession.createSender(queue);
            TextMessage message = qsession.createTextMessage();
            message.setText(msg);
            sender.send(message);
            sender.send(qsession.createMessage());
            System.out.println("Message added Successfully in Queue: " + msg);
            qsession.close();
            qconn.close();
        } catch (JMSException ex) {
            ex.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }


    public boolean verifyResults(String account, String resource)
                                throws RemoteException {
        boolean result = false;
        System.out.println("verifyResults in BeanB");
        try {
            if (resource.equals("DB1")) {
                result = checkResult(getConnection(dbURL1), account);
            } else if (resource.equals("DB2")) {
                result = checkResult(getConnection(dbURL2), account);
            } else if (resource.equals("JMS")) {
                QueueConnection qconn = qfactory.createQueueConnection();
                QueueSession session = qconn.createQueueSession(true, 0);
                QueueReceiver receiver = session.createReceiver(queue);
                qconn.start();

                Message message = receiver.receive(5000);
                if (message != null) {
                    if (message instanceof TextMessage) {
                        TextMessage msg = (TextMessage) message;
                        String str = msg.getText();
                        if ( str.equals(account) ) {
                            result = true;
                        }
                    }
                }

                // close the QueueSession
                session.close();
                qconn.close();
            }
        } catch (Exception ex) {
            System.out.println("Exception in verifyResults: " + ex.toString());
            ex.printStackTrace();
        }
        return result;
    }

    private boolean checkResult(Connection conn, String account) {
        boolean result = false;
        System.out.println("checkResult in BeanB");
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM txAccount WHERE " +
                                             "account = '"+ account + "'");

            if ( rs.next() ) {
                result = true;
            }
        } catch (Exception ex) {
            System.out.println("Exception in checkResult: " + ex.toString());
            ex.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (java.sql.SQLException ex) { }
        }
        return result;
    }

    private Connection getConnection(String dbURL) {
        Connection con = null;
        System.out.println("getConnection in BeanB");
        try{
            Context context = new InitialContext();
            DataSource ds = (DataSource) context.lookup(dbURL);
            con = ds.getConnection(user, password);
            System.out.println("Got DB Connection Successfully...");
        } catch (Exception ex) {
            System.out.println("Exception in getConnection: " + ex.toString());
            ex.printStackTrace();
        }
        return con;
    }
}

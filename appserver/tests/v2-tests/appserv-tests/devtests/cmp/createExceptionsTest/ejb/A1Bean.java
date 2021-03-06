/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

package create;

import jakarta.ejb.*;
import javax.naming.*;

/**
 * 1.1 bean.
 * @author mvatkina
 */


public class A1Bean implements jakarta.ejb.EntityBean {

    private jakarta.ejb.EntityContext context;


    /**
     * @see jakarta.ejb.EntityBean#setEntityContext(jakarta.ejb.EntityContext)
     */
    public void setEntityContext(jakarta.ejb.EntityContext aContext) {
        context=aContext;
    }


    /**
     * @see jakarta.ejb.EntityBean#ejbActivate()
     */
    public void ejbActivate() {

    }


    /**
     * @see jakarta.ejb.EntityBean#ejbPassivate()
     */
    public void ejbPassivate() {

    }


    /**
     * @see jakarta.ejb.EntityBean#ejbRemove()
     */
    public void ejbRemove() {
        System.out.println("Debug: A1Bean ejbRemove");
    }


    /**
     * @see jakarta.ejb.EntityBean#unsetEntityContext()
     */
    public void unsetEntityContext() {
        context=null;
    }


    /**
     * @see jakarta.ejb.EntityBean#ejbLoad()
     */
    public void ejbLoad() {

    }


    /**
     * @see jakarta.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {
    }

    public java.lang.String name;

    /** This ejbCreate/ejbPostCreate combination tests CreateException
     * thrown from ejbPostCreate.
     */
    public java.lang.String ejbCreate(java.lang.String name) throws jakarta.ejb.CreateException {

        this.name = name;
        return null;
    }

    public void ejbPostCreate(java.lang.String name) throws jakarta.ejb.CreateException {
        throw new jakarta.ejb.CreateException("A1Bean.ejbPostCreate");
    }

    /** This ejbCreate/ejbPostCreate combination tests CreateException
     * thrown from either ejbCreate (if b is false) or ejbPostCreate.
     * Executed in a non-transactional context to test both options.
     */
    public java.lang.String ejbCreate(java.lang.String name, boolean b)  throws jakarta.ejb.CreateException {
        if (b) {
             this.name = name;
        } else {
             throw new jakarta.ejb.CreateException("A1Bean.ejbCreate");
        }
        return null;
    }

    public void ejbPostCreate(java.lang.String name, boolean b)   throws jakarta.ejb.CreateException {
        if (b) {
             throw new jakarta.ejb.CreateException("A1Bean.ejbPostCreate");
       }
    }

    /** This ejbCreate/ejbPostCreate combination tests that bean state is
     * reset prior to call to ejbCreate.
     */
    public java.lang.String ejbCreate(int i)  throws jakarta.ejb.CreateException {
        if (this.name != null) {
             throw new java.lang.IllegalStateException("A1Bean.ejbCreate not reset");
        }

        this.name = "A1Bean_" + i;
        return null;
    }

    public void ejbPostCreate(int i)   throws jakarta.ejb.CreateException {
    }

}

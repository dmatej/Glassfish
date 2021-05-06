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

package cascadeDelete;

import jakarta.ejb.*;

/**
 * Created Dec 23, 2002 12:43:05 PM
 * Code generated by the Forte For Java EJB Builder
 * @author mvatkina
 */


public abstract class CBean implements jakarta.ejb.EntityBean {

    private jakarta.ejb.EntityContext context;
    private boolean cascadeDeleteFromA = false;
    private boolean cascadeDeleteFromB = false;


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
        System.out.println("Debug: C ejbRemove");

        cascadeDelete.LocalA a = getA();
        System.out.println("A: " + ((a==null)? "null" : a.getName()));
        if (cascadeDeleteFromA && (a != null) )
            throw new EJBException("In C - getA() not NULL in CascadeDeleteFromA!");

        cascadeDelete.LocalB b = getB();
        System.out.println("B: " + ((b==null)? "null" : b.getName()));
        if (b != null) {
            if (cascadeDeleteFromB) {
                throw new EJBException("In C - getB() not NULL in CascadeDeleteFromB!");
            } else {
                b.cascadeDeleteFromC();
            }
        }

        System.out.println("Ds: " + getDs().size());


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
        cascadeDeleteFromA = false;
        cascadeDeleteFromB = false;

    }


    /**
     * @see jakarta.ejb.EntityBean#ejbStore()
     */
    public void ejbStore() {

    }

    public abstract java.lang.Integer getId();
    public abstract void setId(java.lang.Integer id);

    public abstract java.lang.String getName();
    public abstract void setName(java.lang.String name);

    public abstract java.util.Collection getDs();

    public abstract void setDs(java.util.Collection ds);

    public abstract cascadeDelete.LocalA getA();

    public abstract void setA(cascadeDelete.LocalA a);

    public abstract cascadeDelete.LocalB getB();

    public abstract void setB(cascadeDelete.LocalB b);

    public java.lang.Integer ejbCreate(java.lang.Integer id, java.lang.String name) throws jakarta.ejb.CreateException {
        setId(id);
        setName(name);
        return null;
    }

    public void ejbPostCreate(java.lang.Integer id, java.lang.String name) throws jakarta.ejb.CreateException {
    }

    public void cascadeDeleteFromA() {
        cascadeDeleteFromA = true;
    }

    public void cascadeDeleteFromB() {
        cascadeDeleteFromB = true;
    }
}

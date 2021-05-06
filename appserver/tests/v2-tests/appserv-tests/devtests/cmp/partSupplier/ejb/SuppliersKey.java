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

package Data;

/**
 * Created Dec 16, 2002 1:22:07 PM
 * Code generated by the Forte For Java EJB Builder
 * @author mvatkina
 */

public final class SuppliersKey implements java.io.Serializable {

    public java.lang.Integer partid;
    public java.lang.Integer supplierid;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(java.lang.Object otherOb) {

        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof Data.SuppliersKey)) {
            return false;
        }
        Data.SuppliersKey other = (Data.SuppliersKey) otherOb;
        return (

        (partid==null?other.partid==null:partid.equals(other.partid))
        &&
        (supplierid==null?other.supplierid==null:supplierid.equals(other.supplierid))

        );
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (

        (partid==null?0:partid.hashCode())
        ^
        (supplierid==null?0:supplierid.hashCode())

        );
    }

}

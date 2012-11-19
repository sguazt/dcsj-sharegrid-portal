/*
 * Copyright (C) 2008  Distributed Computing System (DCS) Group, Computer
 * Science Department - University of Piemonte Orientale, Alessandria (Italy).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.unipmn.di.dcs.sharegrid.web.management.naming;

/**
 * Represents a binding in a NamingContext.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ManagementNamingEntry {


    // -------------------------------------------------------------- Constants


    public static final int ENTRY = 0;
    public static final int LINK_REF = 1;
    public static final int REFERENCE = 2;
    
    public static final int CONTEXT = 10;


    // ----------------------------------------------------------- Constructors


    public ManagementNamingEntry(String name, Object value, int type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The type instance variable is used to avoid unsing RTTI when doing
     * lookups.
     */
    public int type;
    public String name;
    public Object value;


    // --------------------------------------------------------- Object Methods


    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof ManagementNamingEntry)) {
            return name.equals(((ManagementNamingEntry) obj).name);
        } else {
            return false;
        }
    }


    public int hashCode() {
        return name.hashCode();
    }


}

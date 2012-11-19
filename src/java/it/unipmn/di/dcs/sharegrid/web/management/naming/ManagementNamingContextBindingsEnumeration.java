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

import java.util.Iterator;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Naming enumeration implementation.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */

public class ManagementNamingContextBindingsEnumeration implements NamingEnumeration<Binding>
{
    public ManagementNamingContextBindingsEnumeration(Iterator entries, Context ctx) {
    	iterator = entries;
        this.ctx = ctx;
    }

    // -------------------------------------------------------------- Variables


    /**
     * Underlying enumeration.
     */
    protected Iterator iterator;

    
    /**
     * The context for which this enumeration is being generated.
     */
    private Context ctx;

    /**
     * Retrieves the next element in the enumeration.
     */
    public Binding next() throws NamingException {
        return nextElementInternal();
    }

    /**
     * Determines whether there are any more elements in the enumeration.
     */
    public boolean hasMore() throws NamingException {
        return iterator.hasNext();
    }

    /**
     * Closes this enumeration.
     */
    public void close() throws NamingException {
    }

    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    public Binding nextElement() {
        try {
            return nextElementInternal();
        } catch (NamingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    private Binding nextElementInternal() throws NamingException {
        ManagementNamingEntry entry = (ManagementNamingEntry) iterator.next();
        
        // If the entry is a reference, resolve it
        if (entry.type == ManagementNamingEntry.REFERENCE
                || entry.type == ManagementNamingEntry.LINK_REF) {
            try {
                // A lookup will resolve the entry
                ctx.lookup(new CompositeName(entry.name));
            } catch (NamingException e) {
                throw e;
            } catch (Exception e) {
                NamingException ne = new NamingException(e.getMessage());
                ne.initCause(e);
                throw ne;
            }
        }
        
        return new Binding(entry.name, entry.value.getClass().getName(), entry.value, true);
    }
}

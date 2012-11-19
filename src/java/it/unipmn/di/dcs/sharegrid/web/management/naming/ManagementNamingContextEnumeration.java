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

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Naming enumeration implementation.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */

public class ManagementNamingContextEnumeration implements NamingEnumeration<NameClassPair>
{
    public ManagementNamingContextEnumeration(Iterator entries) {
    	iterator = entries;
    }

    /**
     * Underlying enumeration.
     */
    protected Iterator iterator;

    /**
     * Retrieves the next element in the enumeration.
     */
    public NameClassPair next() throws NamingException {
        return nextElement();
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


    public NameClassPair nextElement() {
        ManagementNamingEntry entry = (ManagementNamingEntry) iterator.next();
        return new NameClassPair(entry.name, entry.value.getClass().getName());
    }
}


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

import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

/**
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ManagementContextFactoryBuilder implements InitialContextFactoryBuilder, ObjectFactoryBuilder
{
	private ManagementContextFactory factory = new ManagementContextFactory();

	//@{ InitialContextFactoryBuilder implementation

        public InitialContextFactory createInitialContextFactory(Hashtable<?,?> environment) throws NamingException
        {
                return this.factory;
        }

	//@} InitialContextFactoryBuilder implementation

	//@{ ObjectFactoryBuilder implementation

        public ObjectFactory createObjectFactory(Object obj, Hashtable<?,?> environment) throws NamingException
        {
                return this.factory;
        }

	//@} ObjectFactoryBuilder implementation
}

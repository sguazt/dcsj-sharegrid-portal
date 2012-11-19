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
import javax.naming.Context;

/**
 * Handles the associations :
 * <ul>
 * <li>Catalina context name with the NamingContext</li>
 * <li>Calling thread with the NamingContext</li>
 * </ul>
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class ManagementContextBindings
{
	/**
	 * Bindings name - naming context. Keyed by name.
	 */
	private static Hashtable<String,Context> contextNameBindings = new Hashtable<String,Context>();

	/**
	 * Bindings thread - naming context. Keyed by thread id.
	 */
	private static Hashtable<Thread,Context> threadBindings = new Hashtable<Thread,Context>();

	/**
	 * Bindings thread - name. Keyed by thread id.
	 */
	private static Hashtable<Thread,String> threadNameBindings = new Hashtable<Thread,String>();

	/**
	 * Bindings class loader - naming context. Keyed by CL id.
	 */
	private static Hashtable<ClassLoader,Context> clBindings = new Hashtable<ClassLoader,Context>();

	/**
	 * Bindings class loader - name. Keyed by CL id.
	 */
	private static Hashtable<ClassLoader,String> clNameBindings = new Hashtable<ClassLoader,String>();

	/**
	 * The string manager for this package.
	 */
	protected static StringManager sm = StringManager.getManager( ManagementContextBindings.class.getPackage().getName() );

	/**
	 * Binds a context name.
	 * 
	 * @param name Name of the context
	 * @param context Associated naming context instance
	 */
	public static void bindContext(String name, Context context)
	{
		ManagementContextBindings.bindContext(name, context, null);
	}

	/**
	 * Binds a context name.
	 * 
	 * @param name Name of the context
	 * @param context Associated naming context instance
	 * @param token Security token
	 */
	public static void bindContext(String name, Context context, Object token)
	{
//		if (ContextAccessController.checkSecurityToken(name, token)) //TODO
		ManagementContextBindings.contextNameBindings.put(name, context);
	}

	/**
	 * Unbind context name.
	 * 
	 * @param name Name of the context
	 */
	public static void unbindContext(String name)
	{
		ManagementContextBindings.unbindContext(name, null);
	}

	/**
	 * Unbind context name.
	 * 
	 * @param name Name of the context
	 * @param token Security token
	 */
	public static void unbindContext(String name, Object token)
	{
//		if (ContextAccessController.checkSecurityToken(name, token)) //TODO
		ManagementContextBindings.contextNameBindings.remove(name);
	}

	/**
	 * Retrieve a naming context.
	 * 
	 * @param name Name of the context
	 */
	static Context getContext(String name)
	{
		return (Context) ManagementContextBindings.contextNameBindings.get(name);
	}

	/**
	 * Binds a naming context to a thread.
	 * 
	 * @param name Name of the context
	 */
	public static void bindThread(String name) throws NamingException
	{
		ManagementContextBindings.bindThread(name, null);
	}

	/**
	 * Binds a naming context to a thread.
	 * 
	 * @param name Name of the context
	 * @param token Security token
	 */
	public static void bindThread(String name, Object token) throws NamingException
	{
//		if (ContextAccessController.checkSecurityToken(name, token)) {//TODO
		Context context = (Context) ManagementContextBindings.contextNameBindings.get(name);
		if (context == null)
		{
			throw new NamingException(sm.getString("contextBindings.unknownContext", name));
		}
		threadBindings.put(Thread.currentThread(), context);
		threadNameBindings.put(Thread.currentThread(), name);
//		}//TODO
	}

	/**
	 * Unbinds a naming context to a thread.
	 * 
	 * @param name Name of the context
	 */
	public static void unbindThread(String name)
	{
		ManagementContextBindings.unbindThread(name, null);
	}

	/**
	 * Unbinds a naming context to a thread.
	 * 
	 * @param name Name of the context
	 * @param token Security token
	 */
	public static void unbindThread(String name, Object token)
	{
//		if (ContextAccessController.checkSecurityToken(name, token)) { //TODO
		ManagementContextBindings.threadBindings.remove(Thread.currentThread());
		ManagementContextBindings.threadNameBindings.remove(Thread.currentThread());
//		} //TODO
	}

	/**
	 * Retrieves the naming context bound to a thread.
	 */
	public static Context getThread() throws NamingException
	{
		Context context = (Context) ManagementContextBindings.threadBindings.get(Thread.currentThread());
		if (context == null)
		{
			throw new NamingException(sm.getString("contextBindings.noContextBoundToThread"));
		}
		return context;
	}

	/**
	 * Retrieves the naming context name bound to a thread.
	 */
	static Object getThreadName() throws NamingException
	{
		Object name = ManagementContextBindings.threadNameBindings.get(Thread.currentThread());
		if (name == null)
		{
			throw new NamingException(sm.getString("contextBindings.noContextBoundToThread"));
		}
		return name;
	}

	/**
	 * Tests if current thread is bound to a context.
	 */
	public static boolean isThreadBound()
	{
		return ManagementContextBindings.threadBindings.containsKey(Thread.currentThread());
	}

	/**
	 * Binds a naming context to a class loader.
	 * 
	 * @param name Name of the context
	 */
	public static void bindClassLoader(String name) throws NamingException
	{
		ManagementContextBindings.bindClassLoader(name, null);
	}

	/**
	 * Binds a naming context to a thread.
	 * 
	 * @param name Name of the context
	 * @param token Security token
	 */
	public static void bindClassLoader(String name, Object token) throws NamingException
	{
		ManagementContextBindings.bindClassLoader(name, token, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Binds a naming context to a thread.
	 * 
	 * @param name Name of the context
	 * @param token Security token
	 */
	public static void bindClassLoader(String name, Object token, ClassLoader classLoader) throws NamingException
	{
//		if (ContextAccessController.checkSecurityToken(name, token)) { //TODO
		Context context = (Context) contextNameBindings.get(name);
		if (context == null)
		{
			throw new NamingException(sm.getString("contextBindings.unknownContext", name));
		}
		clBindings.put(classLoader, context);
		clNameBindings.put(classLoader, name);
//		} //TODO
	}

	/**
	 * Unbinds a naming context to a class loader.
	 * 
	 * @param name Name of the context
	 */
	public static void unbindClassLoader(String name)
	{
		ManagementContextBindings.unbindClassLoader(name, null);
	}

	/**
	 * Unbinds a naming context to a class loader.
	 * 
	 * @param name Name of the context
	 * @param token Security token
	 */
	public static void unbindClassLoader(String name, Object token)
	{
		ManagementContextBindings.unbindClassLoader(name, token, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Unbinds a naming context to a class loader.
	 * 
	 * @param name Name of the context
	 * @param token Security token
	 */
	public static void unbindClassLoader(String name, Object token, ClassLoader classLoader)
	{
//		if (ContextAccessController.checkSecurityToken(name, token)) { //TODO
		Object n = clNameBindings.get(classLoader);
		if ((n==null) || !(n.equals(name)))
		{
			return;
		}
		clBindings.remove(classLoader);
		clNameBindings.remove(classLoader);
//		} //TODO
	}

	/**
	 * Retrieves the naming context bound to a class loader.
	 */
	public static Context getClassLoader() throws NamingException
	{
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Context context = null;
		do
		{
			context = (Context) clBindings.get(cl);
			if (context != null)
			{
				return context;
			}
		} while ((cl = cl.getParent()) != null);

		throw new NamingException(sm.getString("contextBindings.noContextBoundToCL"));
	}

	/**
	 * Retrieves the naming context name bound to a class loader.
	 */
	static Object getClassLoaderName() throws NamingException
	{
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Object name = null;
		do
		{
			name = clNameBindings.get(cl);
			if (name != null)
			{
				return name;
			}
		} while ((cl = cl.getParent()) != null);

		throw new NamingException(sm.getString("contextBindings.noContextBoundToCL"));
	}

	/**
	 * Tests if current class loader is bound to a context.
	 */
	public static boolean isClassLoaderBound()
	{
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		do {
			if (clBindings.containsKey(cl))
			{
				return true;
			}
		} while ((cl = cl.getParent()) != null);

		return false;
	}
}

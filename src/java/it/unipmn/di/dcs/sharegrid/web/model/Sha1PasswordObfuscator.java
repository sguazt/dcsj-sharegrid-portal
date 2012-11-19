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

package it.unipmn.di.dcs.sharegrid.web.model;

import it.unipmn.di.dcs.common.conversion.Convert;

import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password obfuscator based on the SHA-1 algorithm.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public class Sha1PasswordObfuscator implements IPasswordObfuscator
{
	//@{ IPasswordObfuscator ///////////////////////////////////////////////

	public String obfuscate(String value) throws ModelException
	{
		byte[] digest = null;

		try
		{
			MessageDigest alg = MessageDigest.getInstance("SHA-1");
			digest = alg.digest( value.getBytes() );
		}
		catch (NoSuchAlgorithmException nae)
		{
			throw new ModelException("Error while obfuscating a password with SHA-1 algorithm.", nae);
		}

		return Convert.BytesToBase64( digest );
	}

	//@} IPasswordObfuscator ///////////////////////////////////////////////
}

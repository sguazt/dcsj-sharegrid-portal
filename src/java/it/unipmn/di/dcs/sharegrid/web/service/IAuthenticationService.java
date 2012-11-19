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

package it.unipmn.di.dcs.sharegrid.web.service;

import it.unipmn.di.dcs.common.util.Pair;

import it.unipmn.di.dcs.sharegrid.web.model.LoginInfo;
import it.unipmn.di.dcs.sharegrid.web.model.RegistrationInfo;
import it.unipmn.di.dcs.sharegrid.web.model.User;

/**
 * Interface for authentication services.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IAuthenticationService
{
	/**
	 * Try to perform the login against the given parameters.
	 *
	 * @return {@code true} if the login parameters are valid;
	 * {@code false} otherwise.
	 */
	boolean login(LoginInfo loginInfo);

	/**
	 * Try to perform the login against the given parameters and load the
	 * associated user.
	 *
	 * @return The user object if the login parameters are valid;
	 * {@code null} otherwise.
	 */
	User loginLoad(LoginInfo loginInfo);

	/**
	 * Try to perform the logout against the given parameters.
	 *
	 * @return {@code true} if the login parameters are valid;
	 * {@code false} otherwise.
	 */
	boolean logout(LoginInfo loginInfo);

	/**
	 * Register the user related to the given parameters.
	 *
	 * @return A pair containing the user id and the activation key.
	 * @throws ServiceException if the registration fails.
	 */
	Pair<Integer,String> register(RegistrationInfo regInfo) throws ServiceException;

	/**
	 * Register the given user against the given activation key.
	 *
	 * @return {@code true} if the activation parameters are valid;
	 * {@code false} otherwise.
	 * @throws ServiceException if the activation fails.
	 */
	boolean activate(int userId, String key) throws ServiceException;

	/**
	 * Register the given user against the given activation key.
	 *
	 * @return {@code true} if the activation parameters are valid;
	 * {@code false} otherwise.
	 * @throws ServiceException if the activation fails.
	 */
	boolean activate(User user, String key) throws ServiceException;

	/** Change the password of the given user. */
	void changePassword(User user, String oldPassword, String newPassword) throws ServiceException;
}

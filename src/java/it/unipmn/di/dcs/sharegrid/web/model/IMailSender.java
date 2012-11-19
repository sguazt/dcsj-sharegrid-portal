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

/**
 * Interface for email senders.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public interface IMailSender
{
	/**
	 * Sends an email message according to the given parameters.
	 *
	 * @param sender The email address of the sender.
	 * @param recipients Array of recipient email addresses.
	 * @param subject The subject of the email message.
	 * @param body The body of the email message.
	 */
	void send(String sender, String[] recipients, String subject, String body);

	/**
	 * Sends an email message according to the given parameters.
	 *
	 * @param from The "Fron:" email address.
	 * @param to Array of "To:" email addresses.
	 * @param subject The subject of the email message.
	 * @param body The body of the email message.
	 * @param cc Array of "CC:" email addresses.
	 * @param bcc Array of "BCC:" email addresses.
	 */
	void send(String from, String[] to, String subject, String body, String[] cc, String[] bcc);
}

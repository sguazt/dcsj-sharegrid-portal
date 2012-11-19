#!/bin/sh

#
# Create the ShareGrid database.
#
# Author: Marco Guazzone (marco.guazzone@gmail.com)
#
# Copyright (C) 2010  Distributed Computing System (DCS) Group, Computer
# Science Department - University of Piemonte Orientale, Alessandria (Italy).
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published
# by the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

dbroot=root
dbname=sharegrid
sghome=..

function createForMysql()
{
	mysqladmin -f -u "$dbroot" drop "$dbname"
	mysqladmin -u "$dbroot" create "$dbname"
	mysql -u "$dbroot" < "$sghome/setup/sql/mysql/sharegrid-dbusers.sql"
	mysql -u "$dbroot" "$dbname" < "$sghome/setup/sql/mysql/sharegrid-db.sql"
}

## Main ##

case "$1" in
	'mysql')
		createForMysql
		;;
	*)
		echo "Usage: $0 {mysql}"
		exit 1
		;;
esac

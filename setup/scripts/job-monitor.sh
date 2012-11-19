#!/bin/sh

#
# Monitor ShareGrid jobs.
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

if [ -z "$CATALINA_HOME" ]; then
	CATALINA_HOME=/opt/apache-tomcat
	export CATALINA_HOME
fi
if [ -z "$MGROOT" ]; then
	MGROOT=/usr/local/opt/OurGrid/v3/mygrid
	export MGROOT
fi

subsys="sharegrid"
me=`basename $0`
basepath="$CATALINA_HOME/webapps/sgportal"
logpath="/var/log/$subsys"
pidpath="/var/run"
logfile="$logpath/$me.log"
pidfile="$pidpath/$me.pid"

classpath="$basepath/WEB-INF/classes"
for j in $basepath/WEB-INF/lib/*.jar; do
    classpath+=":$j"
done

if [ ! -e "$logpath" ]; then
	mkdir -p $logpath
fi
if [ ! -e "$pidpath" ]; then
	mkdir -p $pidpath
fi

minutes=5
#ageThreshold=2592000 # 1 month
#ageWarn=1728000 # 1 month - 10 days

case "$1" in
	start)
		java -Djava.util.logging.config.file="$basepath/WEB-INF/classes/META-INF/logging.properties" \
		-cp "$classpath" \
		it.unipmn.di.dcs.sharegrid.web.management.grid.GridJobUpdaterTask \
		$minutes \
		>"$logfile" 2>&1 &

		echo $! > "$pidfile"

		echo "Started"
		;;
	stop)
		if [ -e "$pidfile" ]; then
			pid=`cat $pidfile`
			if [ ! -z "$pid" ]; then
				kill "$pid"
			fi
			echo "Stopped"
		else
			echo "It's seem '$me' it is already stopped!"
		fi
		;;
	*)
		echo "Usage: $me {start|stop}"
		exit 1
esac

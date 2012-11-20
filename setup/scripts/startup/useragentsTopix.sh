#!/bin/sh

##
## Authors:
## - Marco Guazzone (marco.guazzone@gmail.com)
##
## ----------------------------------------------------------------------------
##
## Copyright (C) 2010       Marco Guazzone
##                          [Distributed Computing System (DCS) Group,
##                           Computer Science Institute,
##                           Department of Science and Technological Innovation,
##                           University of Piemonte Orientale,
##                           Alessandria (Italy)]
##
## This file is part of dcssh-sharegrid-startup.
##
## dcssh-sharegrid-startup is free software: you can redistribute it and/or modify
## it under the terms of the GNU General Public License as published
## by the Free Software Foundation, either version 3 of the License, or
## (at your option) any later version.
##
## dcssh-sharegrid-startup is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with dcssh-sharegrid-startup.  If not, see <http://www.gnu.org/licenses/>.
##

from_host=2
to_host=4
#to_host=247
host_prefix='10.10.9'
other_hosts=""
ports="10800 20800 30800 40800"

ops=

case "$1" in
        start)
                ops=start
                ;;
        stop)
                ops=stop
                ;;
        status)
                ops=status
                ;;
        restart)
                ops="stop start"
                ;;
        *)
                echo "Usage $0 {stop|start|restart|status}"
                exit 1
esac

# Variant #1
for i in `seq $from_host $to_host`; do
# Variant #2
#for ((i=$from_host; i <= $to_host; i++)); do
	host="$host_prefix.$i"
	#echo "$host"
	for port in $ports; do
		for op in $ops; do
			echo "Going to '$op' useragent $host:$port"
			#ssh -x "sharegrid@${host}" ".ourgrid/${host}_${port}/useragent" "$op"
			ssh -x -f "sharegrid@${host}" ".ourgrid/${host}_${port}/useragent" "$op"
			# take a breath
			sleep 5
		done
	done
done
for host in $other_hosts; do
	for port in $ports; do
		for op in $ops; do
			echo "Going to '$op' useragent $host:$port"
			ssh -x -f "sharegrid@${host}" ".ourgrid/${host}_${port}/useragent" "$op"
			# take a breath
			sleep 5
		done
	done
done


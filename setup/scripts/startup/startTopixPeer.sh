#!/bin/sh

##
## Authors:
## - Marco Guazzone (marco.guazzone@gmail.com)
##
## ----------------------------------------------------------------------------
##
## Copyright (C) 2008-2012  Marco Guazzone
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

user="$1"

basedir=/home/$user/shareGrid/topixPeer

$basedir/bin/peer start
$basedir/bin/peer setgums $basedir/mysite.sdf

##
## Copyright (C) 2008-2012  Distributed Computing System (DCS) Group, Computer
## Science Department - University of Piemonte Orientale, Alessandria (Italy).
##
## This program is free software: you can redistribute it and/or modify
## it under the terms of the GNU Lesser General Public License as published
## by the Free Software Foundation, either version 3 of the License, or
## (at your option) any later version.
##
## This program is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
## GNU Lesser General Public License for more details.
##
## You should have received a copy of the GNU Lesser General Public License
## along with this program.  If not, see <http://www.gnu.org/licenses/>.
##

use strict;
use warnings;

my $from = 194;
my $to = 241;
my $prefix = '193.206.63';

my $cmd = 'ssh';
#my @params = ( 'ls -l /usr/java' );
my @params = ( @ARGV );

for my $i ( $from..$to )
{
	my $host = "$prefix.$i";

	warn( "Executing $cmd $host ", join(',', @params), "\n" );
	system( "$cmd", $host, @params );
}

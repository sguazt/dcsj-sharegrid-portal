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

(function(){

if ( typeof DcsShareGrid == "undefined" )
{

var DcsShareGrid = window.DcsShareGrid = function(selector, context) {
	// If the context is a namespace object, return a new object
	return	this instanceof DcsShareGrid
		?  this.init(selector,context)
		: new DcsShareGrid(selector, context);
};

DcsShareGrid.fn = DcsShareGrid.prototype = {
	version: "1.0.0",
	init: function(selector, context) {
		// Make sure that a selection was provided
		selector = selector || document;
		return this;
	},
	addClass: function(elem, klass) {
		if ( klass != undefined )
		{
			elem.className += ( elem.className ? " " : "" ) + klass;
		}
	},
	removeClass: function(elem, klass) {
		if ( klass != undefined )
		{
			kk = elem.className.split(/\s+/);

			elem.className = '';
			for ( var i = 0; i < kk.length; i++ )
			{
				if ( kk[i] != klass )
				{
					elem.className += ( elem.className ? " " : "" ) + kk[i];
				}
			}
		}
	}
};

}
})();

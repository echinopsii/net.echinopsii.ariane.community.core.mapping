// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - TOOLS - ext.string                            │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure mapping graph engine                             │ \\
// │ Copyright (C) 2013  Mathilde Ffrench												  │ \\
// │ 																					  │ \\
// │ This program is free software: you can redistribute it and/or modify                 │ \\
// │ it under the terms of the GNU Affero General Public License as                       │ \\
// │ published by the Free Software Foundation, either version 3 of the                   │ \\
// │ License, or (at your option) any later version.									  │ \\
// │																					  │ \\
// │ This program is distributed in the hope that it will be useful,					  │ \\
// │ but WITHOUT ANY WARRANTY; without even the implied warranty of						  │ \\
// │ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the						  │ \\
// │ GNU Affero General Public License for more details.								  │ \\
// │																					  │ \\
// │ You should have received a copy of the GNU Affero General Public License			  │ \\
// │ along with this program.  If not, see <http://www.gnu.org/licenses/>.				  │ \\
// └──────────────────────────────────────────────────────────────────────────────────────┘ \\
define(
    function () {
        String.prototype.width = function(font) {
            var f = font || '12px arial',
                o = $('<div>' + this + '</div>')
                    .css({'position': 'absolute', 'float': 'left', 'white-space': 'nowrap', 'visibility': 'hidden',
                        'font-size': font['font-size'], 'font-family': font['font-family'], 'font-weight': font['font-weight']})
                    .appendTo($('body')),
                w = o.width();

            o.remove();

            return w;
        };

        String.prototype.height = function (font) {
            var f = font || '12px arial',
                o = $('<div>' + this + '</div>')
                    .css({'position': 'absolute', 'float': 'left', 'white-space': 'nowrap', 'visibility': 'hidden',
                        'font-size': font['font-size'], 'font-family': font['font-family'], 'font-weight': font['font-weight']})
                    .appendTo($('body')),
                h = o.height();

            o.remove();

            return h;
        };

        String.prototype.contains = function (text) {
            return this.indexOf(text) != -1;
        };
});

// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - CORE module - Map Options                     │ \\
// │ Use Raphael.js                                                                       │ \\
// │ -------------------------------------------------------------------------------------│ \\
// │ Taitale - provide an infrastructure mapping graph engine                             │ \\
// │ Copyright (C) 2013  Mathilde Ffrench												  │ \\
// │										 											  │ \\
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
        function mapOptions() {
            var layout,
                mode,
                uri,
                view;

            var rootTreeSorting = 1,
                subTreesSorting = 1;

            this.getLayout = function() {
                return layout;
            };
            this.setLayout = function(layout_) {
                layout = layout_;
            };
            this.getMode = function () {
                return mode;
            };
            this.setMode = function(mode_) {
                mode = mode_;
            };
            this.getURI = function () {
                return uri;
            };
            this.setURI = function (uri_) {
                uri = uri_;
            };
            this.getView = function() {
                view;
            };
            this.setView = function(view_) {
                view = view_;
            };
            this.setRootTreeSorting = function(sort) {
                rootTreeSorting = sort;
            };
            this.getRootTreeSorting = function() {
                return rootTreeSorting;
            };
            this.setSubTreesSorting = function(sort) {
                subTreesSorting = sort;
            };
            this.getSubTreesSorting = function() {
                return subTreesSorting;
            }
        }

        return mapOptions;
    });

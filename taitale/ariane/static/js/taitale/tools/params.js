// ┌──────────────────────────────────────────────────────────────────────────────────────┐ \\
// │ Taitale - JavaScript Taitale Library - TOOLS - params                                │ \\
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

define({
    //TODO : CSS refactoring
    map_menuFillColor   : "#000",
    map_menuStrokeColor : "#666",
    map_menuOpacity     : 0.5,
    map_menuStrokeWidth : 1,
    map_menuMainTitle   : {'font-size': '12px', 'font-family': 'Arial', 'font-weight': 'bold', fill: "#dddddd"},
    map_menuFields      : {'font-size': '12px', 'font-family': 'Arial', fill: "#fff"},
    map_mbrdSpan        : 20,
    map_zoneSpan        : 5,
    map_linkColor       : '#fff',
    map_linkBckg        : '#000|1.5',

    dc_areaSpan          : 0,  /*space between 2 area*/
    dc_dbrdSpan          : 20, /*space between 1 area and DC border*/
    dc_dbrdResz          : 10, /*resizing border*/
    dc_txtTitle          : {font: '14px Helvetica, Arial', 'font-weight': 'bold', fill: "#000"},
    dc_opacSelec         : 0.2,
    dc_opacUnselec       : 0,
    dc_strokeWidthShow   : 1.5,
    dc_color             : "#000",
    dc_strokeDasharray   : "--.",
    dc_split_lineColor   : "#000",
    dc_split_moverSpan   : 5,
    dc_split_moverLSpan  : 5,
    dc_split_moverHeight : 20,
    dc_split_moverCRad   : 5,
    dc_split_moverOSel   : 0.2,
    dc_split_moverOUnsel : 0,
    dc_split_title       : {font: '13px Helvetica, Arial', 'font-weight': 'bold', fill: "#000"},

    area_lanSpan         : 0,  /*space between 2 lan*/
    area_abrdSpan        : 20, /*space between 1 lan and area border*/
    area_abrdResz        : 10,
    area_txtTitle        : {font: '13px Helvetica, Arial', 'font-weight': 'bold', fill: "#000"},
    area_opacSelec       : 0.2,
    area_opacUnselec     : 0,
    area_strokeDasharray : "- .",
    area_color           : "#000",
    area_strokeWidthShow : 1.5,

    lan_contSpan        : 5,  /*space between 2 container*/
    lan_lbrdSpan        : 20, /*space between 1 container and lan border*/
    lan_lbrdResz        : 10,
    lan_txtTitle        : {font: '12px Helvetica, Arial', 'font-weight': 'bold', fill: "#000"},
    lan_opacSelec       : 0.2,
    lan_opacUnselec     : 0,
    lan_strokeDasharray : "-..",
    lan_color           : "#000",
    lan_strokeWidthShow : 1,

    container_menuFillColor   : "#000",
    container_menuStrokeColor : "#666",
    container_menuOpacity     : 0.5,
    container_menuStrokeWidth : 1,
    container_menuMainTitle   : {'font-size': '12px', 'font-family': 'Arial', 'font-weight': 'bold', fill: "#fff"},
    container_menuFields      : {'font-size': '12px', 'font-family': 'Arial', fill: "#fff"},
    container_fitTextPadding  : 50,
    container_fitTxtTitleMinFont : 13,
    container_cornerRad       : 15,
    container_interSpan       : 20,
    container_opacSelec       : 0.2,
    container_opacUnselec     : 0.1,
    container_strokeWidth     : 4,
    container_txtTitle        : {'font-size': '14px', 'font-family': 'Arial', 'font-weight': 'bold'},
    containerHat_txtFont      : {'font-size': '11px', 'font-family': 'Arial', 'font-weight': 'bold'},

    node_menuFillColor   : "#000",
    node_menuStrokeColor : "#666",
    node_menuOpacity     : 0.5,
    node_menuStrokeWidth : 1,
    node_menuMainTitle   : {'font-size': '12px', 'font-family': 'Arial', 'font-weight': 'bold', fill: "#fff"},
    node_menuFields      : {'font-size': '12px', 'font-family': 'Arial', fill: "#fff"},
    node_cornerRad   : 25,
    node_minWidth    : 180,
    node_minHeight   : 40,
    node_opacSelec   : 0.3,
    node_opacUnselec : 0.2,
    node_strokeWidth : 2,
    node_titleHeight : 34,
    node_txtTitle    : {'font-size': '13px', 'font-family': 'Arial', fill: '#000'},
    node_txtDesc     : {'font-size': '12px', 'font-family': 'Arial', fill: '#000'},

    endpoint_menuFillColor   : "#000",
    endpoint_menuStrokeColor : "#666",
    endpoint_menuOpacity     : 0.5,
    endpoint_menuStrokeWidth : 1,
    endpoint_menuMainTitle   : {'font-size': '12px', 'font-family': 'Arial', 'font-weight': 'bold', fill: "#fff"},
    endpoint_menuFields      : {'font-size': '12px', 'font-family': 'Arial', fill: "#fff"},
    endpoint_frmFillColor   : "#000",
    endpoint_frmStrokeColor : "#666",
    endpoint_frmOpacity     : 0.7,
    endpoint_radUnselec     : 7,
    endpoint_radSelec       : 9,
    endpoint_opacUnselec    : 0.5,
    endpoint_opacSelec      : 0.8,
    endpoint_strokeWidth    : 1,
    endpoint_txtBxURLTitle  : {'font-size': '12px', 'font-family': 'Arial', 'font-weight': 'bold', fill: "#fff"},
    endpoint_txtBxURLDef    : {'font-size': '12px', 'font-family': 'Arial', fill: "#fff"}
});
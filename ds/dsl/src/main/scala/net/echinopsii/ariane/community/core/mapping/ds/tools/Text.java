/**
 * Ariane DSL Tools
 * Ariane DSL Tools
 * Copyright (C) 01/04/16 echinopsii
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.echinopsii.ariane.community.core.mapping.ds.tools;

import com.tinkerpop.blueprints.Predicate;

public enum Text implements Predicate {
    REGEX;

    @Override
    public boolean evaluate(Object value, Object regex) {
        if (regex instanceof String && value instanceof String) {
            return ((String) value).matches((String)regex);
        } else {
            String msg = "The both arguments must be a String; ";
            msg += "\nvalue is : (" + value.toString() + ", " + value.getClass() + ")";
            msg += "\nregex is : (" + regex.toString() + ", " + regex.getClass() + ")";
            throw new IllegalArgumentException(msg);
        }
    }
}

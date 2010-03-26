/*
 * JPAContainer
 * Copyright (C) 2010 Oy IT Mill Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.vaadin.addon.jpacontainer;

import java.io.Serializable;

/**
 * Data structure class representing a field to sort by and the direction of the
 * sort (ascending or descending). Once created, the instances of this class are
 * immutable.
 * 
 * @author Petter Holmström (IT Mill)
 * @since 1.0
 */
public final class SortBy implements Serializable {

	private static final long serialVersionUID = -6308560006578484770L;

	/**
	 * The property ID to sort by.
	 */
	private final Object propertyId;

	/**
	 * True to sort ascendingly, false to sort descendingly.
	 */
	private final boolean ascending;

	/**
	 * Gets the property ID to sort by.
	 */
	public Object getPropertyId() {
		return propertyId;
	}

	/**
	 * Returns true to sort ascendingly, false to sort descendingly.
	 */
	public boolean isAscending() {
		return ascending;
	}

	/**
	 * Creates a new <code>SortBy</code> instance.
	 * @param propertyId the property ID to sort by (must not be null).
	 * @param ascending true to sort ascendingly, false to sort descendingly.
	 */
	public SortBy(Object propertyId, boolean ascending) {
		assert propertyId != null : "propertyId must not be null";
		this.propertyId = propertyId;
		this.ascending = ascending;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() == getClass()) {
			SortBy o = (SortBy) obj;
			return o.propertyId.equals(propertyId) && o.ascending == ascending;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = propertyId.hashCode();
		hash = hash * 7 + new Boolean(ascending).hashCode();
		return hash;
	}
}

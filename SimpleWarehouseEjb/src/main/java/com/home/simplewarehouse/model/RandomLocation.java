package com.home.simplewarehouse.model;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Location with access limit RANDOM.
 */
@XmlRootElement(name = "RandomLocation")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@DiscriminatorValue("RANDOM")
public class RandomLocation extends Location {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(RandomLocation.class);

	/**
	 * Default Random Location with default LogicalPosition
	 */
	public RandomLocation() {
		super();
	}

	/**
	 * Random Location with id
	 * 
	 * @param id the given id
	 */
	public RandomLocation(String id) {
		super(id);
	}

	/**
	 * Random Location with id and user
	 * 
	 * @param id   the given id
	 * @param user the given user
	 */
	public RandomLocation(String id, String user) {
		super(id, user);
	}

	/**
	 * Random Location with id, user and timestamp
	 * 
	 * @param id        the given id
	 * @param user      the given user
	 * @param timestamp the given timestamp
	 */
	public RandomLocation(String id, String user, Timestamp timestamp) {
		super(id, user, timestamp);
	}

	/**
	 * Default Random Location with specific Position
	 * 
	 * @param pos the given Position
	 */
	public RandomLocation(Position pos) {
		super(pos);
	}

	/**
	 * Random Location with id
	 * 
	 * @param pos the given Position
	 * @param id  the given id
	 */
	public RandomLocation(Position pos, String id) {
		super(pos, id);
	}

	/**
	 * Random Location with id and user
	 * 
	 * @param pos  the given Position
	 * @param id   the given id
	 * @param user the given user
	 */
	public RandomLocation(Position pos, String id, String user) {
		super(pos, id, user);
	}

	/**
	 * Random Location with id, user and timestamp
	 * 
	 * @param pos       the given Position
	 * @param id        the given id
	 * @param user      the given user
	 * @param timestamp the given timestamp
	 */
	public RandomLocation(Position pos, String id, String user, Timestamp timestamp) {
		super(pos, id, user, timestamp);
	}

	@Override
	public int hashCode() {
		// Only locationId; this is a must. Otherwise stack overflow
		return Objects.hash(getLocationId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RandomLocation other = (RandomLocation) obj;

		// Only locationId; this is a must. Otherwise stack overflow
		return Objects.equals(getLocationId(), other.getLocationId());
	}
}

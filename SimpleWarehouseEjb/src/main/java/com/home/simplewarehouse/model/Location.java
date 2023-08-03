package com.home.simplewarehouse.model;

import static javax.persistence.LockModeType.NONE;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Location with access limit RANDOM.
 */
@XmlRootElement(name = "Location")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "LOCATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ACCESS_LIMIT", discriminatorType = DiscriminatorType.STRING, length = 20)
@DiscriminatorValue("RANDOM")
@NamedQuery(name = "findAllLocations", query = "select l from Location l", lockMode = NONE)
public class Location extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(Location.class);

	private static final String ID_FORMATTER = "locationId={0}";

	/**
	 * The Location id
	 */
	@Id
	@Column(name = "LOCATION_ID", nullable = false, length = 80)
	private String locationId;
	/**
	 * Version number for optimistic locking
	 */
	@Version
	private int version;
	/**
	 * The associated LocationStatus
	 */
	@OneToOne(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn(name = "LOCATION_ID")
	private LocationStatus locationStatus;
	/**
	 * The associated Dimension
	 */
	@OneToOne(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn(name = "LOCATION_ID")
	private Dimension dimension;
	/**
	 * The associated Position
	 */
	@OneToOne(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn(name = "LOCATION_ID")
	private Position position;
	/**
	 * The associated HandlingUnits
	 */
	@OneToMany(mappedBy = "location", cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.REFRESH }, fetch = FetchType.EAGER)
	private Set<HandlingUnit> handlingUnits = new HashSet<>();

	private void initAssociated() {
		Dimension dim = new Dimension();
		dim.setLocation(this);
		this.setDimension(dim);
		LocationStatus ls = new LocationStatus();
		ls.setLocation(this);
		this.setLocationStatus(ls);
		Position pos = new LogicalPosition(this.getLocationId());
		pos.setLocation(this);
		this.setPosition(pos);
	}

	private void initAssociated(String id) {
		Dimension dim = new Dimension(id);
		dim.setLocation(this);
		this.setDimension(dim);
		LocationStatus ls = new LocationStatus(id);
		ls.setLocation(this);
		this.setLocationStatus(ls);
		Position pos = new LogicalPosition(id);
		pos.setLocation(this);
		this.setPosition(pos);
	}

	/**
	 * Default Random Location
	 */
	public Location() {
		super();
		initAssociated();
	}

	/**
	 * Random Location with id
	 * 
	 * @param id the given id
	 */
	public Location(String id) {
		super();
		this.locationId = id;
		initAssociated(id);
	}

	/**
	 * Random Location with id and user
	 * 
	 * @param id   the given id
	 * @param user the given user
	 */
	public Location(String id, String user) {
		super(user);
		this.locationId = id;
		initAssociated(id);
	}

	/**
	 * Random Location with id, user and timestamp
	 * 
	 * @param id        the given id
	 * @param user      the given user
	 * @param timestamp the given timestamp
	 */
	public Location(String id, String user, Timestamp timestamp) {
		super(user, timestamp);
		this.locationId = id;
		initAssociated(id);
	}

	/**
	 * Gets the Location id
	 * 
	 * @return the id
	 */
	public String getLocationId() {
		LOG.debug(ID_FORMATTER, this.locationId);
		return this.locationId;
	}

	/**
	 * Sets the Location id
	 * 
	 * @param id the given id
	 */
	public void setLocationId(final String id) {
		this.locationId = id;
		LOG.debug(ID_FORMATTER, this.locationId);
	}

	/**
	 * Gets the version
	 * 
	 * @return the version number
	 */
	public int getVersion() {
		return version;
	}

	protected void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Gets the Location status of this Location
	 * 
	 * @return the Location status
	 */
	public LocationStatus getLocationStatus() {
		return locationStatus;
	}

	/**
	 * Sets the Location status for this locationService
	 * 
	 * @param locationStatus the Location status
	 */
	public void setLocationStatus(LocationStatus locationStatus) {
		this.locationStatus = locationStatus;
	}

	/**
	 * Gets the Dimension assigned to this Location
	 * 
	 * @return the assigned Dimension
	 */
	public Dimension getDimension() {
		return dimension;
	}

	/**
	 * Assigns the Dimension to this Location
	 * 
	 * @param dimension the Dimension to assign
	 */
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	/**
	 * Gets the Position assigned to this Location
	 * 
	 * @return the assigned Position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Assigns the Position to this Location
	 * 
	 * @param position the Position to assign
	 */
	public void setPosition(Position position) {
		this.position = position;

		if (position != null) {
			position.setLocation(this);
			position.setLocationId(locationId);
		}
	}

	/**
	 * Gets all HandlingUnits located on this Location
	 * 
	 * @return the List of HandlingUnits
	 */
	public List<HandlingUnit> getHandlingUnits() {
		LOG.trace("--> getHandlingUnits()");

		List<HandlingUnit> ret = new ArrayList<>();

		handlingUnits.forEach(ret::add);

		LOG.trace("<-- getHandlingUnits()");

		return ret;
	}

	/**
	 * Sets the list of HandlingUnits for this Location
	 * 
	 * @param handlingUnits the list of HandlingUnits
	 */
	public void setHandlingUnits(List<HandlingUnit> handlingUnits) {
		LOG.trace("--> setHandlingUnits()");

		this.handlingUnits.clear();

		if (handlingUnits != null) {
			handlingUnits.forEach(this.handlingUnits::add);
		}

		LOG.trace("<-- setHandlingUnits()");
	}

	/**
	 * Add this HandlingUnit to the Location
	 * 
	 * @param handlingUnit the HandlingUnit
	 * 
	 * @return true if add succeeded, else false
	 */
	public boolean addHandlingUnit(HandlingUnit handlingUnit) {
		LOG.trace("--> addHandlingUnit()");

		boolean ret = false;

		if (handlingUnit != null) {
			handlingUnit.setLocation(this);
			handlingUnit.setLocaPos(null);

			List<HandlingUnit> list = getHandlingUnits();
			ret = list.add(handlingUnit);
			setHandlingUnits(list);
		}

		LOG.trace("<-- addHandlingUnit()");

		return ret;
	}

	/**
	 * Remove this HandlingUnit from the Location
	 * 
	 * @param handlingUnit the HandlingUnit
	 * 
	 * @return true if remove succeeded, else false
	 */
	public boolean removeHandlingUnit(HandlingUnit handlingUnit) {
		LOG.trace("--> removeHandlingUnit()");

		boolean ret = false;

		if (handlingUnit != null) {
			List<HandlingUnit> list = getHandlingUnits();
			ret = list.remove(handlingUnit);
			setHandlingUnits(list);

			if (ret) {
				handlingUnit.setLocation(null);
				handlingUnit.setLocaPos(null);
			}
		}

		LOG.trace("<-- removeHandlingUnit()");

		return ret;
	}

	/**
	 * Gets all the HandlingUnits possible to Pick from the Location
	 * 
	 * @return the List of HandlingUnits
	 */
	public List<HandlingUnit> getAvailablePicks() {
		LOG.trace("--> getAvailablePicks()");

		List<HandlingUnit> ret = getHandlingUnits().stream().collect(Collectors.toList());

		LOG.trace("<-- getAvailablePicks()");

		return ret;
	}

	@Override
	public int hashCode() {
		// Only locationId; this is a must. Otherwise stack overflow
		return Objects.hash(locationId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;

		// Only locationId; this is a must. Otherwise stack overflow
		return Objects.equals(locationId, other.locationId);
	}

	protected String toString(List<HandlingUnit> list) {
		StringBuilder builder = new StringBuilder();

		builder.append("RANDOM=[");

		if (list == null) {
			builder.append("null");
		} else {
			list.stream().forEach(item -> builder.append('"').append(item.getId()).append('"').append(" "));
		}

		// Replace trailing " " by "]" (see above) or just append "]"
		int idx = builder.lastIndexOf(" ");
		if (idx > 0) {
			builder.replace(idx, idx + 1, "]");
		} else {
			builder.append("]");
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Location [").append(System.lineSeparator() + "\tlocationId=").append(locationId)
				.append(", version=").append(version).append(", " + System.lineSeparator() + '\t' + '\t')
				.append(locationStatus).append(", " + System.lineSeparator() + '\t' + '\t').append(dimension)
				.append(", " + System.lineSeparator() + '\t' + '\t').append(position)
				.append(", " + System.lineSeparator() + '\t' + '\t').append("HandlingUnits ")
				.append(toString(getHandlingUnits())).append(", " + System.lineSeparator() + '\t')
				.append(super.toString()).append("]");

		return builder.toString();
	}
}

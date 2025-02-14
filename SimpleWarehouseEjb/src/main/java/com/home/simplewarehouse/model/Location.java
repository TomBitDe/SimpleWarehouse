package com.home.simplewarehouse.model;

import static javax.persistence.LockModeType.NONE;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@NamedQuery(name = "findAllLocations", query = "select l from Location l", lockMode = NONE)
public abstract class Location extends EntityBase implements Serializable {
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
	protected LocationStatus locationStatus;
	/**
	 * The associated Dimension
	 */
	@OneToOne(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn(name = "LOCATION_ID")
	protected Dimension dimension;
	/**
	 * The associated Position
	 */
	@OneToOne(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn(name = "LOCATION_ID")
	protected Position position;
	/**
	 * The associated HandlingUnits
	 */
	@OneToMany(mappedBy = "location", cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE,
			CascadeType.REFRESH }, fetch = FetchType.EAGER)
	private Set<HandlingUnit> handlingUnits = new HashSet<>();
	/**
	 * The associated Zone
	 */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "Location_Zone",
        joinColumns = @JoinColumn(name = "LOCATION_ID"),
        inverseJoinColumns = @JoinColumn(name = "ZONE_ID")
    )
    private Set<Zone> zones = new HashSet<>();

    private void initAssociated(Position pos) {
		Dimension dim = new Dimension(this);
		this.setDimension(dim);
		LocationStatus ls = new LocationStatus(this);
		this.setLocationStatus(ls);
		
		if (pos == null) {
			Position tmp = new LogicalPosition(this.getLocationId());
			this.setPosition(tmp);
			tmp.setLocation(this);
			tmp.setLocationId(this.getLocationId());
		}
		else {
			this.setPosition(pos);
			pos.setLocation(this);
			pos.setLocationId(this.getLocationId());
		}
		
		this.setZones(zones);
	}

	/**
	 * Default Random Location with default LogicalPosition
	 */
	public Location() {
		super();
		initAssociated(null);
	}

	/**
	 * Random Location with id
	 * 
	 * @param id the given id
	 */
	public Location(String id) {
		super();
		this.locationId = id;
		initAssociated(null);
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
		initAssociated(null);
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
		initAssociated(null);
	}

	/**
	 * Default Random Location with specific Position
	 * 
	 * @param pos the given Position
	 */
	public Location(Position pos) {
		super();
		initAssociated(pos);
	}

	/**
	 * Random Location with id
	 * 
	 * @param pos the given Position
	 * @param id  the given id
	 */
	public Location(Position pos, String id) {
		super();
		this.locationId = id;
		initAssociated(pos);
	}

	/**
	 * Random Location with id and user
	 * 
	 * @param pos  the given Position
	 * @param id   the given id
	 * @param user the given user
	 */
	public Location(Position pos, String id, String user) {
		super(user);
		this.locationId = id;
		initAssociated(pos);
	}

	/**
	 * Random Location with id, user and timestamp
	 * 
	 * @param pos       the given Position
	 * @param id        the given id
	 * @param user      the given user
	 * @param timestamp the given timestamp
	 */
	public Location(Position pos, String id, String user, Timestamp timestamp) {
		super(user, timestamp);
		this.locationId = id;
		initAssociated(pos);
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

	/**
	 * Gets the Location status of this Location
	 * 
	 * @return the Location status
	 */
	public LocationStatus getLocationStatus() {
		return locationStatus;
	}

	private void setLocationStatus(LocationStatus ls) {
		this.locationStatus = ls;
	}
	
	/**
	 * Sets the Location status values for this Location
	 * 
	 * @param e the ErrorStatus
	 * @param lt the LtosStatus
	 * @param lo the LockStatus
	 */
	public void setLocationStatus(ErrorStatus e, LtosStatus lt, LockStatus lo) {
		this.getLocationStatus().setErrorStatus(e);
		this.getLocationStatus().setLtosStatus(lt);
		this.getLocationStatus().setLockStatus(lo);
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
	private void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
	
	/**
	 * Assigns the Dimension to this Location
	 * 
	 * @param maxCapacity the maximum capacity of the Location
	 * @param maxWeight the maximum weight allowed for the Location
	 * @param maxHeight the maximum height allowed for the Location
	 * @param maxLength the maximum length allowed for the Location
	 * @param maxWidth the maximum width allowed for the Location
	 */
	public void setDimension(int maxCapacity, int maxWeight, HeightCategory maxHeight
			, LengthCategory maxLength, WidthCategory maxWidth) {
		this.getDimension().setMaxCapacity(maxCapacity);
		this.getDimension().setMaxWeight(maxWeight);
		this.getDimension().setMaxHeight(maxHeight);
		this.getDimension().setMaxLength(maxLength);
		this.getDimension().setMaxWidth(maxWidth);
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
		position.setLocation(this);
		position.setLocationId(getLocationId());
	}
	
	/**
	 * Gets all HandlingUnits located on this Location
	 * 
	 * @return the List of HandlingUnits
	 */
	public Set<HandlingUnit> getHandlingUnits() {
		LOG.trace("--> getHandlingUnits()");

		Set<HandlingUnit> ret = new HashSet<>();

		handlingUnits.forEach(ret::add);

		LOG.trace("<-- getHandlingUnits()");

		return ret;
	}

	/**
	 * Sets the list of HandlingUnits for this Location
	 * 
	 * @param handlingUnits the list of HandlingUnits
	 */
	public void setHandlingUnits(Set<HandlingUnit> handlingUnits) {
		LOG.trace("--> setHandlingUnits()");

		this.handlingUnits.clear();

		if (handlingUnits != null) {
			handlingUnits.forEach(this.handlingUnits::add);
		}

		LOG.trace("<-- setHandlingUnits()");
	}

	/**
	 * Gets the Zones the Location belongs to
	 * 
	 * @return the Zone
	 */
    public Set<Zone> getZones() {
        return zones;
    }

    /**
	 * Sets the Zones the Location belongs to

     * @param zone the zone to assign
     */
    public void setZones(Set<Zone> zones) {
        this.zones = zones;  
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

			Set<HandlingUnit> list = getHandlingUnits();
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
			Set<HandlingUnit> list = getHandlingUnits();
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

	protected String toString(Set<HandlingUnit> list) {
		StringBuilder builder = new StringBuilder();

		builder.append("RANDOM=[");

		list.stream().forEach(item -> builder.append('"').append(item.getId()).append('"').append(" "));

		// Replace trailing " " by "]" (see above) or just append "]"
		int idx = builder.lastIndexOf(" ");
		if (idx > 0) {
			builder.replace(idx, idx + 1, "]");
		}
		else {
			builder.append("]");
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Location [").append(System.lineSeparator() + "\tlocationId=").append(locationId)
				.append(", version=").append(getVersion()).append(", " + System.lineSeparator() + '\t' + '\t')
				.append(locationStatus).append(", " + System.lineSeparator() + '\t' + '\t').append(dimension)
				.append(", " + System.lineSeparator() + '\t' + '\t').append(position)
				.append(", " + System.lineSeparator() + '\t' + '\t').append("HandlingUnits ")
				.append(toString(getHandlingUnits())).append(", " + System.lineSeparator() + '\t' + '\t')
				.append("Zones=").append(zones == null ? "null" : zones.stream().map(Zone::getId)
                        .collect(Collectors.toList())).append(", " + System.lineSeparator() + '\t')
				.append(super.toString()).append("]");

		return builder.toString();
	}
}

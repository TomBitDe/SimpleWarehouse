package com.home.simplewarehouse.model;

import static javax.persistence.LockModeType.NONE;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Any kind of handling unit.
 */
@XmlRootElement(name = "HandlingUnit")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@Entity
@Table(name="HANDLING_UNIT")
@NamedQuery(name = "findAllHandlingUnits", query = "select h from HandlingUnit h", lockMode = NONE)
public class HandlingUnit extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(HandlingUnit.class);
    
    /**
     * The default weight value
     */
    public static final int WEIGHT_DEFAULT = 0;
    /**
     * The default volume value
     */
    public static final float VOLUME_DEFAULT = 0.0f;
    private static final HeightCategory HEIGHT_DEFAULT = HeightCategory.NOT_RELEVANT;
    private static final LengthCategory LENGTH_DEFAULT = LengthCategory.NOT_RELEVANT;
    private static final WidthCategory WIDTH_DEFAULT = WidthCategory.NOT_RELEVANT;
    
    private static final String ID_FORMATTER = "id={0}";
    private static final String ID_WEIGHT_FORMATTER = "id={0} weight={1}";
    private static final String ID_VOLUME_FORMATTER = "id={0} volume={1}";
    private static final String ID_HEIGHT_FORMATTER = "id={0} height={1}";
    private static final String ID_LENGTH_FORMATTER = "id={0} length={1}";
    private static final String ID_WIDTH_FORMATTER = "id={0} width={1}";

    /**
     * The HandlingUnit id
     */
    @Id
    @Column(name = "ID", nullable = false, length = 80)
    private String id;
    /**
     * The position of the HandlingUnit in a Location
     */
    @Column(name = "LOCAPOS", nullable = true)
    private Integer locaPos = null;
    /**
     * The HandlingUnits weight
     */
    @Column(name = "WEIGHT", nullable = false)
    private Integer weight;
    /**
     * The HandlingUnits volume
     */
    @Column(name = "VOLUME", nullable = false)
    private Float volume;
    /**
     * The HandlingUnits height
     */
    @Column(name = "HEIGHT", nullable = false, length = 80)
    private String height;
    /**
     * The HandlingUnits length
     */
    @Column(name = "LENGTH", nullable = false, length = 80)
    private String length;
    /**
     * The HandlingUnits width
     */
    @Column(name = "WIDTH", nullable = false, length = 80)
    private String width;
    /**
     * The version number for optimistic locking
     */
    @Version
    private int version;
    
    /**
     * The associated Location
     */
    @ManyToOne(targetEntity = Location.class
    		, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }
    		, fetch = FetchType.LAZY) // LAZY for better performance)
	@JoinColumn(name = "LOCATION_ID", nullable = true)
    private Location location = null;
    
    /**
     * The base HandlingUnit of the HandlingUnit
     */
    @ManyToOne(targetEntity = HandlingUnit.class
    		, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }
    		, fetch = FetchType.EAGER) // LAZY for better performance
	@JoinColumn(name = "BASE_HU", nullable = true)
    private HandlingUnit baseHU = null;
    
    /**
     * The container for other HandlingUnits
     */
    @OneToMany(fetch = FetchType.EAGER) // LAZY for better performance
    @JoinTable
    (
        name="HU_CONTAINS"
        , joinColumns={ @JoinColumn(name="ID", referencedColumnName="ID") }
        , inverseJoinColumns={ @JoinColumn(name="CONTAINS", referencedColumnName="ID"/*, unique=true*/) }
    )
    private Set<HandlingUnit> contains = new HashSet<>();
    
    /**
     * Default constructor
     */
    public HandlingUnit() {
    	super();
    	setDefaults();
    }
    
    /**
     * Create this HandlingUnit
     * 
     * @param id the given id 
     */
    public HandlingUnit(String id) {
    	super();
    	this.id = id;
    	setDefaults();
    }

    /**
     * Create this HandlingUnit
     * 
     * @param id the given id
     * @param user the given user name
     */
    public HandlingUnit(String id, String user) {
    	super(user);
    	this.id = id;
    	setDefaults();
    }

    /**
     * Create this HandlingUnit
     * 
     * @param id the given id
     * @param user the given user name
     * @param timestamp the given timestamp
     */
    public HandlingUnit(String id, String user, Timestamp timestamp) {
    	super(user, timestamp);
    	this.id = id;
    	setDefaults();
    }

    /**
     * Create this HandlingUnit
     * 
     * @param id the given id
     * @param weight the given weight
     */
    public HandlingUnit(String id, int weight) {
    	super();
    	this.id = id;
    	setDefaults();
    	setWeight(weight);
    }

    /**
     * Create this HandlingUnit
     * 
     * @param id the given id
     * @param weight the given weight
     * @param volume the given volume
     */
    public HandlingUnit(String id, int weight, float volume) {
    	super();
    	this.id = id;
    	setDefaults();
    	setWeight(weight);
    	setVolume(volume);
    }

    /**
     * Create this HandlingUnit
     * 
     * @param id the given id
     * @param weight the given weight
     * @param volume the given volume
     * @param height the given height
     */
    public HandlingUnit(String id, int weight, float volume, HeightCategory height) {
    	super();
    	this.id = id;
    	setDefaults();
    	setWeight(weight);
    	setVolume(volume);
    	setHeight(height);
    }

    /**
     * Create this HandlingUnit
     * 
     * @param id the given id
     * @param weight the given weight
     * @param volume the given volume
     * @param height the given height
     * @param length the given length
     */
    public HandlingUnit(String id, int weight, float volume, HeightCategory height, LengthCategory length) {
    	super();
    	this.id = id;
    	setDefaults();
    	setWeight(weight);
    	setVolume(volume);
    	setHeight(height);
    	setLength(length);
    }

    /**
     * Create this HandlingUnit
     * 
     * @param id the given id
     * @param weight the given weight
     * @param volume the given volume
     * @param height the given height
     * @param length the given length
     * @param width the given width
     */
    public HandlingUnit(String id, int weight, float volume, HeightCategory height, LengthCategory length, WidthCategory width) {
    	super();
    	this.id = id;
    	setDefaults();
    	setWeight(weight);
    	setVolume(volume);
    	setHeight(height);
    	setLength(length);
    	setWidth(width);
    }

    private void setDefaults() {
    	this.weight = WEIGHT_DEFAULT;
    	this.volume = VOLUME_DEFAULT;
    	this.height = HEIGHT_DEFAULT.name();
    	this.length = LENGTH_DEFAULT.name();
    	this.width = WIDTH_DEFAULT.name();
    }

    /**
     * Gets the HandlingUnits id
     * 
     * @return the id
     */
    public String getId() {
    	LOG.debug(ID_FORMATTER, this.id);
        return this.id;
    }

    /**
     * Sets the HandlingUnits id
     * 
     * @param id the given id
     */
    public void setId(String id) {
        this.id = id;
    	LOG.debug(ID_FORMATTER, this.id);
    }

    /**
     * Gets the HandlingUnits weight
     * 
     * @return the weight
     */
    public int getWeight() {
    	LOG.debug(ID_WEIGHT_FORMATTER, this.id, this.weight);
        return this.weight;
    }

    /**
     * Sets HandlingUnits weight
     * 
     * @param weight the given weight, if <code>null</code> set to WEIGHT_DEFAULT
     */
    public void setWeight(int weight) {
    	if (weight < 0) {
    		this.weight = WEIGHT_DEFAULT;
    	}
    	else {
            this.weight = weight;
    	}
    	LOG.debug(ID_WEIGHT_FORMATTER, this.id, this.weight);
    }
    
    /**
     * Gets the HandlingUnits volume
     * 
     * @return the volume
     */
    public float getVolume() {
    	LOG.debug(ID_VOLUME_FORMATTER, this.id, this.volume);
        return this.volume;
    }
    
    /**
     * Sets HandlingUnits volume
     * 
     * @param volume the given volume, if <code>null</code> set to VOLUME_DEFAULT
     */
    public void setVolume(float volume) {
    	if (volume < 0.0f) {
    		this.volume = VOLUME_DEFAULT;
    	}
    	else {
    		this.volume = volume;
    	}
    	LOG.debug(ID_VOLUME_FORMATTER, this.id, this.volume);
    }

    /**
     * Gets the HandlingUnits height
     * 
     * @return the height
     */
    public HeightCategory getHeight() {
    	LOG.debug(ID_HEIGHT_FORMATTER, this.id, this.height);
        return HeightCategory.valueOf(this.height);
    }
    
    /**
     * Sets HandlingUnits height
     * 
     * @param height the given height, if <code>null</code> set to {@link HeightCategory HeightCategory.NOT_RELEVANT}
     */
    public void setHeight(HeightCategory height) {
		if (height == null) {
			this.height = HeightCategory.NOT_RELEVANT.name();
		}
		else {
			this.height = height.name();
		}
    }

    /**
     * Gets the HandlingUnits length
     * 
     * @return the length
     */
    public LengthCategory getLength() {
    	LOG.debug(ID_LENGTH_FORMATTER, this.id, this.length);
        return LengthCategory.valueOf(this.length);
    }
    
    /**
     * Sets HandlingUnits length
     * 
     * @param length the given length, if <code>null</code> set to {@link LengthCategory LengthCategory.NOT_RELEVANT}
     */
    public void setLength(LengthCategory length) {
		if (length == null) {
			this.length = LengthCategory.NOT_RELEVANT.name();
		}
		else {
			this.length = length.name();
		}
    }

    /**
     * Gets the HandlingUnits width
     * 
     * @return the width
     */
    public WidthCategory getWidth() {
    	LOG.debug(ID_WIDTH_FORMATTER, this.id, this.width);
        return WidthCategory.valueOf(this.width);
    }
    
    /**
     * Sets HandlingUnits width
     * 
     * @param width the given width, if <code>null</code> set to {@link WidthCategory WidthCategory.NOT_RELEVANT}
     */
    public void setWidth(WidthCategory width) {
		if (width == null) {
			this.width = WidthCategory.NOT_RELEVANT.name();
		}
		else {
			this.width = width.name();
		}
    }

    /**
     * Gets the version
     * 
     * @return the version number
     */
	private int getVersion() {
		return version;
	}

    /**
     * Gets the HandlingUnits associated Location
     * 
     * @return the Location
     */
	@XmlTransient
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets the HandlingUnits associated Location
	 * 
	 * @param location the Location
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	
	/**
	 * Only needed for JAXB
	 * 
	 * @param locationId the Location id of this Handling Unit
	 */
	public void setLocationId(String locationId) {
		// No code needed so far
	}

	/**
	 * Only needed for JAXB
	 * 
	 * @return the Location ids of this Handling Unit
	 */
	public String getLocationId() {
		if (this.location != null && this.location.getLocationId() != null) {
			return this.location.getLocationId();
		}
		else {
			return "";
		}
	}
	
    /**
     * Gets the HandlingUnits location position
     * 
     * @return the location position
     */	
	public Integer getLocaPos() {
		return locaPos;
	}

    /**
     * Sets the HandlingUnits location position
     * 
     * @param locaPos the location position
     */	
	public void setLocaPos(Integer locaPos) {
		this.locaPos = locaPos;
	}

    /**
     * Gets the baseHU for this HandlingUnit
     * 
	 * @return the baseHU
	 */
	public HandlingUnit getBaseHU() {
		return baseHU;
	}

	/**
     * Sets the baseHU for this HandlingUnit
	 *
	 * @param baseHU the baseHU to set
	 */
	public void setBaseHU(HandlingUnit baseHU) {
		this.baseHU = baseHU;
	}

	/**
	 * Gets the HandlingUnits this one contains
	 * 
	 * @return the containing HandlingUnits
	 */
	@XmlTransient
	public Set<HandlingUnit> getContains() {
		return contains;
	}

	/**
	 * Sets the HandlingUnits this one contains
	 * 
	 * @param contains the containing HandlingUnits to set
	 */
	public void setContains(Set<HandlingUnit> contains) {
		this.contains = contains;
	}
	
	/**
	 * Only needed for JAXB
	 * 
	 * @return the Set of containing Handling Unit ids
	 */
	public Set<String> getContainsId() {
		Set<String> ret = new HashSet<>();
		
		Set<HandlingUnit> containsSet = this.getContains();
		
		containsSet.stream().forEach(elem -> ret.add(elem.getId()));
		
		return ret;
	}
	
	/**
	 * Only needed for JAXB
	 * 
	 * @param containsId the containing Handling Unit id
	 */
	public void setContainsId(Set<String> containsId) {
		// No code needed so far
	}

	@Override
	public int hashCode() {
		// Only id; this is a must. Otherwise stack overflow
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HandlingUnit other = (HandlingUnit) obj;
		
		// Only id; this is a must. Otherwise stack overflow
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HandlingUnit [")
			.append(System.lineSeparator() + "\tid=")
		    .append(id)
		    .append(", weight=")
		    .append(weight)		    
		    .append(", volume=")
		    .append(volume)		    
		    .append(", height=")
		    .append(height)		    
		    .append(", length=")
		    .append(length)		    
		    .append(", width=")
		    .append(width)		    
		    .append(", locaPos=")
		    .append(locaPos == null ? "null" : locaPos)		    
		    .append(", baseHU=")
		    .append(baseHU == null ? "null" : baseHU.getId())
		    .append(", version=")
		    .append(getVersion())
		    .append(", ")
		    .append(location == null ? "Location=null" : location)
			.append(", " + System.lineSeparator() + '\t' + '\t')
			.append("contains=")
			.append(getContains())
		    .append(", " + System.lineSeparator() + '\t')
			.append(super.toString())			
			.append("]");
		
		return builder.toString();
	}
}

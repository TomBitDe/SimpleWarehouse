package com.home.simplewarehouse.model;

import static javax.persistence.LockModeType.NONE;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Any dimension.
 * <p>
 * A maxCapacity of 0 means that an undefined number of goods can be stored in the location.<br>
 * A maxWeight of 0 means that the weight is not relevant for storing goods in the location.<br>
 * A maxHeight of NOT_RELEVANT means that the height is not relevant for storing goods in the location.<br>
 * A maxLength of NOT_RELEVANT means that the length is not relevant for storing goods in the location.<br>
 * A maxWidth of NOT_RELEVANT means that the width is not relevant for storing goods in the location.<br>
 */
@XmlRootElement(name = "Dimension")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name="DIMENSION")
@NamedQuery(name = "findAllDimensions", query = "select d from Dimension d", lockMode = NONE)
public class Dimension implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(Dimension.class);
    
    private static final String ID_FORMATTER = "locationId={0}";
    
    /**
     * Default capacity value
     */
    public static final int CAPACITY_DEFAULT = 0;
    /**
     * Default weight value
     */
    public static final int WEIGHT_DEFAULT = 0;
    
    /**
     * Default height category
     */
    public static final HeightCategory HEIGHT_DEFAULT = HeightCategory.NOT_RELEVANT;
    /**
     * Default length category
     */
    public static final LengthCategory LENGTH_DEFAULT = LengthCategory.NOT_RELEVANT;
    /**
     * Default width category
     */
    public static final WidthCategory WIDTH_DEFAULT = WidthCategory.NOT_RELEVANT;
    
    /**
     * The location id
     */
	@Id
	private String locationId;
	
	/**
	 * The maximum capacity
	 */
	@Basic(optional = false)
	@Column(name = "MAX_CAPACITY", nullable = false)
	private int maxCapacity;
		
	/**
	 * The maximum weight
	 */
	@Basic(optional = false)
	@Column(name = "MAX_WEIGHT", nullable = false)
	private int maxWeight;
		
	/**
	 * The maximum height
	 */
	@Basic(optional = false)
    @Column(name = "MAX_HEIGHT", nullable = false, length = 80)
    private String maxHeight;
    
	/**
	 * The maximum length
	 */
	@Basic(optional = false)
    @Column(name = "MAX_LENGTH", nullable = false, length = 80)
    private String maxLength;

	/**
	 * The maximum width
	 */
	@Basic(optional = false)
    @Column(name = "MAX_WIDTH", nullable = false, length = 80)
    private String maxWidth;

	/**
	 * The version number for optimistic locking
	 */
    @Version
    private int version;
    
    /**
     * The associated location
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "LOCATION_ID")
    @XmlTransient
    private Location location;
    
    /**
     * Sets all dimensionService default values
     */
    private void setDimensionDefaults() {
    	this.maxCapacity = CAPACITY_DEFAULT;
    	this.maxWeight = WEIGHT_DEFAULT;
    	this.maxHeight = HEIGHT_DEFAULT.name();
    	this.maxLength = LENGTH_DEFAULT.name();
    	this.maxWidth = WIDTH_DEFAULT.name();
    }
 
    /**
     * Sets the defaults for the Dimension in default constructor
     */
    public Dimension() {
    	super();
    	
    	setDimensionDefaults();
    }
    
    /**
     * Create this Dimension
     * 
     * @param location the given Location id
     */
    public Dimension(Location location) {
    	super();
    	
     	setLocationId(location.getLocationId());
     	setLocation(location);
    	
    	setDimensionDefaults();
    }
    
    /**
     * Create this Dimension
     * 
     * @param location the given Location 
     * @param user the given user
     */
    public Dimension(Location location, String user) {
    	this(location);
    	
    	location.setUpdateUserId(user);
    }
    
    /**
     * Create this Dimension
     * 
     * @param location the given Location
     * @param maxCapacity the given maximum capacity
     * @param user the given user
     */
	public Dimension(Location location, int maxCapacity, String user) {
    	this(location, user);
		
    	setMaxCapacity(maxCapacity);
    }

    /**
     * Create this Dimension
     * 
     * @param location the given Location 
     * @param maxCapacity the given maximum capacity
     * @param maxWeight the given maximum weight
     * @param user the given user
     */
	public Dimension(Location location, int maxCapacity, int maxWeight, String user) {
    	this(location, maxCapacity, user);
		
    	setMaxWeight(maxWeight);
	}

    /**
     * Create this Dimension
     * 
     * @param location the given Location 
     * @param maxCapacity the given maximum capacity
     * @param maxWeight the given maximum weight
     * @param maxHeight the given maximum height
     * @param user the given user
     */
	public Dimension(Location location, int maxCapacity, int maxWeight, HeightCategory maxHeight, String user) {
    	this(location, maxCapacity, maxWeight, user);
		
    	setMaxHeight(maxHeight);
	}

    /**
     * Create this Dimension
     * 
     * @param location the given Location 
     * @param maxCapacity the given maximum capacity
     * @param maxWeight the given maximum weight
     * @param maxHeight the given maximum height
     * @param maxLength the given maximum length
     * @param user the given user
     */
	public Dimension(Location location, int maxCapacity, int maxWeight, HeightCategory maxHeight
			, LengthCategory maxLength, String user) {
    	this(location, maxCapacity, maxWeight, maxHeight, user);
		
    	setMaxLength(maxLength);
	}

    /**
     * Create this Dimension
     * 
     * @param location the given Location 
     * @param maxCapacity the given maximum capacity
     * @param maxWeight the given maximum weight
     * @param maxHeight the given maximum height
     * @param maxLength the given maximum length
     * @param maxWidth the given maximum width
     * @param user the given user
     */
	public Dimension(Location location, int maxCapacity, int maxWeight, HeightCategory maxHeight
			, LengthCategory maxLength, WidthCategory maxWidth, String user) {
    	this(location, maxCapacity, maxWeight, maxHeight, maxLength, user);

    	setMaxWidth(maxWidth);
	}

	/**
	 * Gets the location id
	 * 
	 * @return the location id
	 */
	public String getLocationId() {
		LOG.debug(ID_FORMATTER, this.locationId);
		return this.locationId;
	}

	/**
	 * Sets the location id
	 * 
	 * @param locationId the id
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
		LOG.debug(ID_FORMATTER, this.locationId);
	}

	/**
	 * Gets the maximum capacity
	 * 
	 * @return the maximum capacity
	 */
	public int getMaxCapacity() {
		return this.maxCapacity;
	}

	/**
	 * Sets the maximum capacity
	 * 
	 * @param maxCapacity the maximum capacity. A value less than zero is not allowed and sets
	 * maxCapacity to {@value CAPACITY_DEFAULT}
	 */
	public void setMaxCapacity(int maxCapacity) {
    	if (maxCapacity < 0) {
			LOG.info("Invalid parameter maxCapacity ({}); set DEFAULT value ({})", maxCapacity
					, CAPACITY_DEFAULT);
			this.maxCapacity = CAPACITY_DEFAULT;
		}
		else {
		    this.maxCapacity = maxCapacity;
		}
	}

	/**
	 * Gets the maximum weight
	 * 
	 * @return the maximum weight
	 */
	public int getMaxWeight() {
		return this.maxWeight;
	}

	/**
	 * Sets the maximum weight
	 * 
	 * @param maxWeight the maximum weight. A value less than zero is not allowed and sets
	 * maxWeight to {@value WEIGHT_DEFAULT}
	 */
	public void setMaxWeight(int maxWeight) {
    	if (maxWeight < 0) {
			LOG.info("Invalid parameter maxWeight ({}); set DEFAULT value ({})", maxWeight
					, WEIGHT_DEFAULT);
			this.maxWeight = WEIGHT_DEFAULT;
		}
		else {
		    this.maxWeight = maxWeight;
		}
	}

	/**
	 * Gets the maximum height
	 * 
	 * @return the maximum height
	 */
	public HeightCategory getMaxHeight() {
		return HeightCategory.valueOf(this.maxHeight);
	}

	/**
	 * Sets the maximum height
	 * 
	 * @param maxHeight the maximum height. A value of {@code null} is not allowed and sets
	 * maxHeight to {@link HEIGHT_DEFAULT}
	 */
	public void setMaxHeight(HeightCategory maxHeight) {
    	if (maxHeight == null) {
			LOG.info("Invalid parameter maxHeight ({}); set DEFAULT value ({})", maxHeight
					, HEIGHT_DEFAULT);
			this.maxHeight = HEIGHT_DEFAULT.name();
		}
		else {
			this.maxHeight = maxHeight.name();
		}
	}

	/**
	 * Gets the maximum length
	 * 
	 * @return the maximum length
	 */
	public LengthCategory getMaxLength() {
		return LengthCategory.valueOf(this.maxLength);
	}

	/**
	 * Sets the maximum length
	 * 
	 * @param maxLength the maximum length. A value of {@code null} is not allowed and sets
	 * maxLength to {@link LENGTH_DEFAULT}
	 */
	public void setMaxLength(LengthCategory maxLength) {
    	if (maxLength == null) {
			LOG.info("Invalid parameter maxLength ({}); set DEFAULT value ({})", maxLength
					, LENGTH_DEFAULT);
			this.maxLength = LENGTH_DEFAULT.name();
		}
		else {
			this.maxLength = maxLength.name();
		}
	}

	/**
	 * Gets the maximum width
	 * 
	 * @return the maximum width
	 */
	public WidthCategory getMaxWidth() {
		return WidthCategory.valueOf(this.maxWidth);
	}

	/**
	 * Sets the maximum width
	 * 
	 * @param maxWidth the maximum width. A value of {@code null} is not allowed and sets
	 * maxWidth to {@link WIDTH_DEFAULT}
	 */
	public void setMaxWidth(WidthCategory maxWidth) {
    	if (maxWidth == null) {
			LOG.info("Invalid parameter maxWidth ({}); set DEFAULT value ({})", maxWidth
					, WIDTH_DEFAULT);
			this.maxWidth = WIDTH_DEFAULT.name();
		}
		else {
			this.maxWidth = maxWidth.name();
		}
	}

	/**
	 * Gets the entity version number
	 * 
	 * @return the version value
	 */
	private int getVersion() {
		return version;
	}

	/**
	 * Gets the related location
	 * 
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets the related location
	 * 
	 * @param location the location entity
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(location, locationId, maxCapacity, maxHeight, maxLength, maxWeight, maxWidth, version);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dimension other = (Dimension) obj;
		return Objects.equals(location, other.location) && Objects.equals(locationId, other.locationId)
				&& maxCapacity == other.maxCapacity && Objects.equals(maxHeight, other.maxHeight)
				&& Objects.equals(maxLength, other.maxLength) && maxWeight == other.maxWeight
				&& Objects.equals(maxWidth, other.maxWidth) && version == other.version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Dimension [")
		    .append("locationId=")
		    .append(getLocationId())
		    .append(", maxCapacity=")
		    .append(getMaxCapacity())
		    .append(", maxWeight=")
		    .append(getMaxWeight())
		    .append(", maxHeight=")
		    .append(getMaxHeight())
		    .append(", maxLength=")
		    .append(getMaxLength())
		    .append(", maxWidth=")
		    .append(getMaxWidth())
		    .append(", version=")
		    .append(getVersion())
		    .append(", ")
			.append(super.toString())
			.append("]");
		
		return builder.toString();
	}
}

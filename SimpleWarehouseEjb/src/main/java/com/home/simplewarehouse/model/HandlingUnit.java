package com.home.simplewarehouse.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import static javax.persistence.LockModeType.NONE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Any kind of handling unit.
 */
@Entity
@Table(name="HANDLING_UNIT")
@NamedQuery(name = "findAllHandlingUnits", query = "select h from HandlingUnit h", lockMode = NONE)
public class HandlingUnit extends EntityBase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(HandlingUnit.class);
    
    private static final int WEIGHT_DEFAULT = 0;
    private static final HeightCategory HEIGHT_DEFAULT = HeightCategory.NOT_RELEVANT;
    private static final LengthCategory LENGTH_DEFAULT = LengthCategory.NOT_RELEVANT;
    private static final WidthCategory WIDTH_DEFAULT = WidthCategory.NOT_RELEVANT;
    
    private static final String ID_FORMATTER = "id={0}";
    private static final String ID_WEIGHT_FORMATTER = "id={0} weight={1}";
    private static final String ID_HEIGHT_FORMATTER = "id={0} height={1}";
    private static final String ID_LENGTH_FORMATTER = "id={0} length={1}";
    private static final String ID_WIDTH_FORMATTER = "id={0} width={1}";

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Column(name = "LOCAPOS", nullable = true)
    private Integer locaPos = null;
    
    @Column(name = "WEIGHT", nullable = false)
    private Integer weight;
    
    @Column(name = "HEIGHT", nullable = false)
    private String height;
    
    @Column(name = "LENGTH", nullable = false)
    private String length;

    @Column(name = "WIDTH", nullable = false)
    private String width;

    @Version
    private int version;
    
    @ManyToOne(targetEntity = Location.class
    		, cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }
    		, fetch = FetchType.LAZY) // LAZY for better performance)
	@JoinColumn(name = "LOCATION_ID", nullable = true)
    private Location location;
    
    public HandlingUnit() {
    	super();
    	setDefaults();
    }
    
    public HandlingUnit(String id) {
    	super();
    	this.id = id;
    	setDefaults();
    }

    public HandlingUnit(String id, String user) {
    	super(user);
    	this.id = id;
    	setDefaults();
    }
    
    public HandlingUnit(String id, String user, Timestamp timestamp) {
    	super(user, timestamp);
    	this.id = id;
    	setDefaults();
    }

    public HandlingUnit(String id, int weight) {
    	super();
    	this.id = id;
    	setDefaults();
    	setWeight(weight);
    }

    public HandlingUnit(String id, int weight, HeightCategory height) {
    	super();
    	this.id = id;
    	setDefaults();
    	setWeight(weight);
    	setHeight(height);
    }

    public HandlingUnit(String id, int weight, HeightCategory height, LengthCategory length) {
    	super();
    	this.id = id;
    	setDefaults();
    	setWeight(weight);
    	setHeight(height);
    	setLength(length);
    }

    public HandlingUnit(String id, int weight, HeightCategory height, LengthCategory length, WidthCategory width) {
    	super();
    	this.id = id;
    	setDefaults();
    	setWeight(weight);
    	setHeight(height);
    	setLength(length);
    	setWidth(width);
    }

    private void setDefaults() {
    	this.weight = WEIGHT_DEFAULT;
    	this.height = HEIGHT_DEFAULT.name();
    	this.length = LENGTH_DEFAULT.name();
    	this.width = WIDTH_DEFAULT.name();
    }

    public String getId() {
    	LOG.debug(ID_FORMATTER, this.id);
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    	LOG.debug(ID_FORMATTER, this.id);
    }

    public int getWeight() {
    	LOG.debug(ID_WEIGHT_FORMATTER, this.id, this.weight);
        return this.weight;
    }

    public void setWeight(int weight) {
    	if (weight < 0) {
    		this.weight = WEIGHT_DEFAULT;
    	}
    	else {
            this.weight = weight;
    	}
    	LOG.debug(ID_WEIGHT_FORMATTER, this.id, this.weight);
    }
    
    public HeightCategory getHeight() {
    	LOG.debug(ID_HEIGHT_FORMATTER, this.id, this.height);
        return HeightCategory.valueOf(this.height);
    }
    
    public void setHeight(HeightCategory height) {
		if (height == null) {
			this.height = HeightCategory.NOT_RELEVANT.name();
		}
		else {
			this.height = height.name();
		}
    }

    public LengthCategory getLength() {
    	LOG.debug(ID_LENGTH_FORMATTER, this.id, this.length);
        return LengthCategory.valueOf(this.length);
    }
    
    public void setLength(LengthCategory length) {
		if (length == null) {
			this.length = LengthCategory.NOT_RELEVANT.name();
		}
		else {
			this.length = length.name();
		}
    }

    public WidthCategory getWidth() {
    	LOG.debug(ID_WIDTH_FORMATTER, this.id, this.width);
        return WidthCategory.valueOf(this.width);
    }
    
    public void setWidth(WidthCategory width) {
		if (width == null) {
			this.width = WidthCategory.NOT_RELEVANT.name();
		}
		else {
			this.width = width.name();
		}
    }

	public int getVersion() {
		return version;
	}

	protected void setVersion(int version) {
		this.version = version;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Integer getLocaPos() {
		return locaPos;
	}

	public void setLocaPos(Integer locaPos) {
		this.locaPos = locaPos;
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
		    .append(", height=")
		    .append(height)		    
		    .append(", length=")
		    .append(length)		    
		    .append(", width=")
		    .append(width)		    
		    .append(", locaPos=")
		    .append(locaPos == null ? "null" : locaPos)		    
		    .append(", version=")
		    .append(version)
		    .append(", ")
		    .append(location == null ? "Location=null" : location)
		    .append(", " + System.lineSeparator() + '\t')
			.append(super.toString())			
			.append("]");
		
		return builder.toString();
	}
	
	
}

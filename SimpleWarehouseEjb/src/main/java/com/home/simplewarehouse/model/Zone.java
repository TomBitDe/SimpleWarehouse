package com.home.simplewarehouse.model;

import static javax.persistence.LockModeType.NONE;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement(name = "Zone")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name="ZONE")
@NamedQuery(name = "findAllZones", query = "select zo from Zone zo", lockMode = NONE)
public class Zone extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(Zone.class);
    
    public static final int RATING_DEFAULT = 0;

    private static final String ID_FORMATTER = "id={0}";
    
    /**
     * The zone identifier
     */
    @Id
    String id;
    
	/**
	 * The zones rating
	 */
	@Basic(optional = false)
	@Column(name = "RATING", nullable = false)
	private int rating = RATING_DEFAULT;    
    
	/**
	 * The version number for optimistic locking
	 */
    @Version
    private int version;

    public Zone() {
    	super();
    	rating = RATING_DEFAULT;
    }
    
    public Zone(String id) {
    	super();
    	this.id = id;
    	rating = RATING_DEFAULT;
    }
    
    public Zone(String id, int rating) {
    	super();
    	this.id = id;
    	this.rating = rating;
    }

	/**
	 * Gets the zone id
	 * 
	 * @return the zone id
	 */
	public String getId() {
		LOG.debug(ID_FORMATTER, this.id);
		return this.id;
	}

	/**
	 * Sets the zone id
	 * 
	 * @param id the id
	 */
	public void setId(String id) {
		this.id = id;
		LOG.debug(ID_FORMATTER, this.id);
	}
	
	/**
	 * Gets the version
	 * 
	 * @return the version number
	 */
	protected int getVersion() {
		return version;
	}
	
	/**
	 * Gets the zone rating
	 * 
	 * @return the rating
	 */
	public int getRating() {
		return this.rating;
	}

	/**
	 * Sets the zone rating
	 * 
	 * @param rating the zone rating
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}

	@Override
	public int hashCode() {
		return Objects.hash(rating, version, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Zone))
			return false;
		Zone other = (Zone) obj;
		return rating == other.rating && version == other.version
				&& Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Zone [id=").append(id)
		        .append(", rating=").append(rating)
		        .append(", version=").append(version)
		        .append(super.toString()).append("]");
		
		return builder.toString();
	}
}
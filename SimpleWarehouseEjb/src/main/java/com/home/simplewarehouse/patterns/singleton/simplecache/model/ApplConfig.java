package com.home.simplewarehouse.patterns.singleton.simplecache.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity implementation class for Entity: ApplConfig.
 *
 */
@XmlRootElement(name = "ApplConfig")
@Entity
@Table(name="APPL_CONFIG")
public class ApplConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "KEY_VAL", nullable = false)
	private String keyVal;

	@Column(name = "PARAM_VAL", nullable = false)
	private String paramVal;

	@Version
	private int version;

	/**
	 * Default constructor
	 */
	public ApplConfig() {
		super();
	}

	/**
	 * Configuration with access key and given value
	 * 
	 * @param keyVal the access key
	 * @param paramVal the given value
	 */
	public ApplConfig(String keyVal, String paramVal) {
		this.keyVal = keyVal;
		this.paramVal = paramVal;
	}

	/**
	 * Get the key value
	 * 
	 * @return the key value
	 */
	public String getKeyVal() {
		return this.keyVal;
	}

	/**
	 * Set the key to the given value
	 * 
	 * @param KeyVal the value
	 */
	public void setKeyVal(String KeyVal) {
		this.keyVal = KeyVal;
	}

	/**
	 * Get the parameters value
	 * 
	 * @return the value
	 */
	public String getParamVal() {
		return this.paramVal;
	}

	/**
	 * Set the parameters value
	 * 
	 * @param ParamVal the value
	 */
	public void setParamVal(String ParamVal) {
		this.paramVal = ParamVal;
	}

	/**
	 * The entity version
	 * 
	 * @return the version value
	 */
	public int getVersion() {
		return this.version;
	}

	protected void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(keyVal, paramVal, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ApplConfig other = (ApplConfig) obj;
		return Objects.equals(keyVal, other.keyVal) && Objects.equals(paramVal, other.paramVal)
				&& version == other.version;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApplConfig [keyVal=");
		builder.append(keyVal);
		builder.append(", paramVal=");
		builder.append(paramVal);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");

		return builder.toString();
	}
}

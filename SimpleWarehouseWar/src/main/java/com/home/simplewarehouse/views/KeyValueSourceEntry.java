package com.home.simplewarehouse.views;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A key, value combined with the values source class.
 */
@XmlRootElement(name = "KeyValueSourceEntry")
public class KeyValueSourceEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The key
	 */
	private String key = "";
	/**
	 * The value
	 */
	private String value = "";
	/**
	 * The source of the value
	 */
	private String source = "";
	
	/**
	 * Mandatory DEFAULT constructor
	 */
	public KeyValueSourceEntry() {
		super();
	}
	
	/**
	 * Only valid constructor
	 * 
	 * @param key the key
	 * @param value the value
	 * @param source the source of the value
	 */
	public KeyValueSourceEntry(String key, String value, String source) {
		this.key = key;
		this.value = value;
		this.source = source;
	}

	/**
	 * Gets the key
	 * 
	 * @return the key
	 */
	@XmlElement
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key
	 * 
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the value
	 * 
	 * @return the value
	 */
	@XmlElement
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the source of the value e.g. DbTable or Properties
	 * 
	 * @return the source
	 */
	@XmlElement
	public String getSource() {
		return source;
	}

	/**
	 * Sets the source of the value
	 * 
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, source, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyValueSourceEntry other = (KeyValueSourceEntry) obj;
		return Objects.equals(key, other.key) && Objects.equals(source, other.source)
				&& Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KeyValueSourceEntry [key=").append(key).append(", value=").append(value).append(", source=")
				.append(source).append("]");
		return builder.toString();
	}
}

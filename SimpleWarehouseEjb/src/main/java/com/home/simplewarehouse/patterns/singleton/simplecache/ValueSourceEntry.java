package com.home.simplewarehouse.patterns.singleton.simplecache;

import java.io.Serializable;
import java.util.Objects;

/**
 * A value combined with the values source class.
 */
public class ValueSourceEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The value
	 */
	private String value = "";
	/**
	 * The source of the value
	 */
	private String source = "";
	
	/**
	 * Only valid constructor
	 * 
	 * @param value the value
	 * @param source the source of the value
	 */
	public ValueSourceEntry(String value, String source) {
		this.value = value;
		this.source = source;
	}

	/**
	 * Gets the value
	 * 
	 * @return the value
	 */
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
		return Objects.hash(source, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueSourceEntry other = (ValueSourceEntry) obj;
		return Objects.equals(source, other.source) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ValueSourceEntry [value=").append(value).append(", source=").append(source).append("]");
		return builder.toString();
	}
}

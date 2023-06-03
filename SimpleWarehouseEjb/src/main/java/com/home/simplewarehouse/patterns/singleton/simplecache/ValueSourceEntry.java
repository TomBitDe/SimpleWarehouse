package com.home.simplewarehouse.patterns.singleton.simplecache;

import java.io.Serializable;
import java.util.Objects;

public class ValueSourceEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private String value = "";
	private String source = "";
	
	public ValueSourceEntry(String value, String source) {
		this.value = value;
		this.source = source;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
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

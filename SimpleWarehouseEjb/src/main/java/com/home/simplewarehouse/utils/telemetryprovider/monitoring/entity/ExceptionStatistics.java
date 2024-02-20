package com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Exception Statistics entry class.
 *
 */
@XmlRootElement(name = "ExceptionStatistics")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExceptionStatistics {
	@XmlAttribute
	private String exception;
	@XmlAttribute
	private Integer num;

	/**
	 * Default constructor to keep JAXB happy
	 */
	public ExceptionStatistics() { /* JAXB... */ }
	
	/**
	 * Create an exception statistics entry
	 * 
	 * @param exception the exception method
	 * @param num the number of related exceptions
	 */
	public ExceptionStatistics(String exception, int num) {
		super();
		
		this.exception = exception;
		this.num = num;
	}

	/**
	 * Gets the exception
	 * 
	 * @return the exception
	 */
	public String getException() {
		return exception;
	}

	/**
	 * Sets the exception
	 * 
	 * @param exception the exception
	 */
	public void setException(String exception) {
		this.exception = exception;
	}

	/**
	 * Gets the number of related exceptions
	 * 
	 * @return the number of related exceptions
	 */
	public Integer getNum() {
		return num;
	}

	/**
	 * Sets the number of related exceptions
	 * 
	 * @param num the number of related exceptions
	 */
	public void setNum(Integer num) {
		this.num = num;
	}

	@Override
	public int hashCode() {
		return Objects.hash(exception, num);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ExceptionStatistics))
			return false;
		ExceptionStatistics other = (ExceptionStatistics) obj;
		return Objects.equals(exception, other.exception) && Objects.equals(num, other.num);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExceptionStatistics [exception=").append(exception)
		    .append(", num=").append(num).append("]");
		
		return builder.toString();
	}	
}

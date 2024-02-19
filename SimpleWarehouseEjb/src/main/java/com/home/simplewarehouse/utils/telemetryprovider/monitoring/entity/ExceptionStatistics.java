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
	
	public ExceptionStatistics(String exception, int num) {
		super();
		
		this.exception = exception;
		this.num = num;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public Integer getNum() {
		return num;
	}

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

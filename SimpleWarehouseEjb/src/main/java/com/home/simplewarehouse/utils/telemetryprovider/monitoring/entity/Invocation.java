package com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * the Invocation class as XML element. 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Invocation implements Comparable<Invocation> {

	@XmlAttribute
	private String methodName;
	@XmlAttribute
	private Long invocationPerformance;

	/**
	 * Create an Invocation item
	 * 
	 * @param methodName the method name
	 * @param invocationPerformance the method execution performance 
	 */
	public Invocation(String methodName, long invocationPerformance) {
		this.methodName = methodName;
		this.invocationPerformance = invocationPerformance;
	}

	/**
	 * Default constructor to keep JAXB happy
	 */
	public Invocation() { /* JAXB... */ }

	/**
	 * Gets the method name
	 * 
	 * @return the method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Gets the invocation performance
	 * 
	 * @return the performance value
	 */
	public Long getInvocationPerformance() {
		return invocationPerformance;
	}

	/**
	 * Check if this invocation is slower than the given invocation
	 * 
	 * @param invocation the given invocation
	 * 
	 * @return true if slower else false
	 */
	public boolean isSlowerThan(Invocation invocation) {
		return this.compareTo(invocation) > 0;
	}

	@Override
	public int compareTo(Invocation otherInvocation) {
		return this.invocationPerformance.compareTo(otherInvocation.invocationPerformance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(invocationPerformance, methodName);
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
		Invocation other = (Invocation) obj;
		return Objects.equals(invocationPerformance, other.invocationPerformance)
				&& Objects.equals(methodName, other.methodName);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Invocation [methodName=");
		builder.append(methodName);
		builder.append(", invocationPerformance=");
		builder.append(invocationPerformance);
		builder.append("]");
		return builder.toString();
	}
}

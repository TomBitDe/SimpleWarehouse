package com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Invocation class as XML element. 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InvocationCount implements Comparable<InvocationCount> {

	@XmlAttribute
	private String methodName;
	@XmlAttribute
	private Long invocationCount;

	/**
	 * Create an InvocationCount item
	 * 
	 * @param methodName the method name
	 * @param invocationCount the method execution count 
	 */
	public InvocationCount(String methodName, long invocationCount) {
		this.methodName = methodName;
		this.invocationCount = invocationCount;
	}

	/**
	 * Default constructor to keep JAXB happy
	 */
	public InvocationCount() { /* JAXB... */ }

	@Override
	public int compareTo(InvocationCount otherInvocationCount) {
		return this.invocationCount.compareTo(otherInvocationCount.invocationCount);
	}

	/**
	 * Gets the method name
	 * 
	 * @return the method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Gets the invocation count
	 * 
	 * @return the performance value
	 */
	public Long getInvocationCount() {
		return invocationCount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(invocationCount, methodName);
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
		InvocationCount other = (InvocationCount) obj;
		return Objects.equals(invocationCount, other.invocationCount)
				&& Objects.equals(methodName, other.methodName);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Invocation [methodName=");
		builder.append(methodName);
		builder.append(", invocationCount=");
		builder.append(invocationCount);
		builder.append("]");
		return builder.toString();
	}
}

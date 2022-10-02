package com.home.simplewarehouse.utils.telemetryprovider.monitoring.entity;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Invocation implements Comparable<Invocation> {

	@XmlAttribute
	private String methodName;
	@XmlAttribute
	private Long invocationPerformance;

	public Invocation(String methodName, long invocationPerformance) {
		this.methodName = methodName;
		this.invocationPerformance = invocationPerformance;
	}

	public Invocation() { /* JAXB... */ }

	public String getMethodName() {
		return methodName;
	}

	public Long getInvocationPerformance() {
		return invocationPerformance;
	}

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

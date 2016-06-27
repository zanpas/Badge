package it.sintegra.badge.service.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Response implements Serializable {

	private static final long serialVersionUID = 2119335767856483132L;

	protected Boolean isError;
	protected String message;
	protected Integer numResults;

	public Boolean getIsError() {
		return isError;
	}

	public void setIsError(Boolean isError) {
		this.isError = isError;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getNumResults() {
		return numResults;
	}

	public void setNumResults(Integer numResults) {
		this.numResults = numResults;
	}

}

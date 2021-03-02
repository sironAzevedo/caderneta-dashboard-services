package com.caderneta.handler;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class StandardError implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer status;
	private String mensagem;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", locale = "pt-BR", timezone = "America/Sao_Paulo")
	private Date timestamp;

	private List<FieldMessage> fieldMessages;

	public StandardError() {
		super();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<FieldMessage> getFieldMessages() {
		return fieldMessages;
	}

	public void setFieldMessages(List<FieldMessage> fieldMessages) {
		this.fieldMessages = fieldMessages;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static StandardError builder(int status, String message, Date date) {
		StandardError error = new StandardError();
		error.setStatus(status);
		error.setMensagem(message);
		error.setTimestamp(date);
		return error;
	}

	public static StandardError builder(int status, String message, Date date, List<FieldError> fieldErrors) {
		StandardError error = new StandardError();
		error.setStatus(status);
		error.setMensagem(message);
		error.setTimestamp(date);
		error.setFieldMessages(FieldMessage.error(fieldErrors));		
		return error; 
	}
}

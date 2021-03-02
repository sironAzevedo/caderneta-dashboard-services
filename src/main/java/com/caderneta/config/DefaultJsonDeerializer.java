package com.caderneta.config;

import static com.caderneta.util.Utils.unescapeUntilNoHtmlEntityFound;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

@JsonComponent
public class DefaultJsonDeerializer extends JsonDeserializer<String> implements ContextualDeserializer {

	@Override
	public String deserialize(JsonParser jsonParser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		String value = jsonParser.getValueAsString();
		if (StringUtils.isEmpty(value)) {
			return value;
		} else {
			return unescapeUntilNoHtmlEntityFound(value);
		} 
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
			throws JsonMappingException {
		return this;
	}

}

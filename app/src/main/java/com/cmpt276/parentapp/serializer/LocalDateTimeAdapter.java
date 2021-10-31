package com.cmpt276.parentapp.serializer;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
	@Override
	public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
		if (localDate == null) {
			jsonWriter.nullValue();
		} else {
			jsonWriter.value(localDate.toString());
		}
	}

	@Override
	public LocalDateTime read(final JsonReader jsonReader) throws IOException {
		if (jsonReader.peek() == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else {
			return LocalDateTime.parse(jsonReader.nextString());
		}
	}
}

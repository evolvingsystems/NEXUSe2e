package org.nexuse2e.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;

public class DateWithTimezoneSerializer implements JsonSerializer<Date> {
    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        if (date == null) {
            return null;
        } else {
            String dateString = DateUtil.localTimeToTimezone(date, DateUtil.getTimezone(), "yyyy-MM-dd HH:mm:ss z");
            return new JsonPrimitive(dateString);
        }
    }
}


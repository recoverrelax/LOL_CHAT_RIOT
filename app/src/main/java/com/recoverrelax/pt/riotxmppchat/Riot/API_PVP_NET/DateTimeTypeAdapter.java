package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeTypeAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {
    private static final String DATE_TIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_ONLY_DEFAULT_FORMAT = "yyyy-MM-dd";
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat dateOnlyFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public DateTimeTypeAdapter() {
    }

    public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        String date = json.getAsString();
        Date mDate = new Date(0L);

        try {
            mDate = this.formatter.parse(date);
        } catch (ParseException var9) {
            //noinspection EmptyCatchBlock
            try {
                mDate = this.dateOnlyFormatter.parse(date);
            } catch (ParseException var8) {

            }
        }

        return mDate;
    }

    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        String jsonDate = this.formatter.format(date);
        return new JsonPrimitive(jsonDate);
    }
}

package dk.tohjuler.mcutils.data;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.util.Date;

/**
 * This class contains the data adapters used by the Gson library to serialize and deserialize data.
 */
public class DataAdapters {

    /**
     * The date serializer used to serialize dates to longs.
     */
    public static final JsonSerializer<Date> dateSerializer = (src, typeOfSrc, context) ->
            src == null
                    ? null
                    : new JsonPrimitive(src.getTime());

    /**
     * The date deserializer used to deserialize dates from longs.
     */
    public static final JsonDeserializer<Date> dateDeserializer = (jSon, typeOfT, context) ->
            jSon == null
                    ? null
                    : new Date(jSon.getAsLong());
}

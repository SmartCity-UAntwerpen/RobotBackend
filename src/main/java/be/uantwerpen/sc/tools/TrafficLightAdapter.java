package be.uantwerpen.sc.tools;

import be.uantwerpen.rc.models.TrafficLight;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class TrafficLightAdapter implements JsonSerializer<TrafficLight> {

    @Override
    public JsonElement serialize(TrafficLight src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("id", src.getId());
        object.addProperty("state", src.getState());
        object.addProperty("point_id", src.getPoint().getId());
        return object;
    }
}

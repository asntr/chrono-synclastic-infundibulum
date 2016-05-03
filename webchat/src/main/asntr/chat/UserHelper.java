package main.asntr.chat;

import org.json.simple.JSONObject;

/**
 * Created by ш on 03.05.16.
 */
public class UserHelper {
    public static User jsonObjectToUser(JSONObject jsonObject) {
        User user = new User();
        String name = ((String) jsonObject.get("name"));
        String password = ((String) jsonObject.get("password"));
        user.setUser(name);
        user.setPassword(password);
        return user;
    }
}

package by.bsu.up.chat.utils;

import by.bsu.up.chat.Constants;
import by.bsu.up.chat.InvalidTokenException;
import by.bsu.up.chat.common.models.Message;
import by.bsu.up.chat.logging.Logger;
import by.bsu.up.chat.logging.impl.Log;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageHelper {

    public static final String MESSAGE_PART_ALL_MSG = "messages";
    public static final String MESSAGE_PART_SINGLE_MSG = "message";
    public static final String MESSAGE_PART_TOKEN = "token";
    public static final String TOKEN_TEMPLATE = "TN%dEN";
    public static final String TOKEN_FORMAT = "TN[0-9]{2,}EN";

    private static final JSONParser jsonParser = new JSONParser();
    private static final Logger logger = Log.create(MessageHelper.class);

    /**
     * Builds token based on amount of messages, which are
     * already stored on server side or client side.
     * <p>
     *     E.g. Client has 5 messages. It does not want to
     *     retrieve messages it already has. So, client
     *     passes 5 as argument to this method, and this method
     *     will return a token, which says to server: Just give
     *     me all messages, but skip first 5.
     *
     * <p>
     *     On the other hand, server passes amount of messages it has
     *     (size of messages collection). So, client can parse
     *     token and understand how many messages are on server side
     * @param receivedMessagesCount amount of messages to skip.
     * @return generated token
     */
    public static String buildToken(int receivedMessagesCount) {
        Integer stateCode = encodeIndex(receivedMessagesCount);
        return String.format(TOKEN_TEMPLATE, stateCode);
    }

    /**
     * Parses token and extract encoded amount of messages (typically - index)
     * @param token the token to be parsed
     * @return decoded amount messages (index)
     */
    public static int parseToken(String token) {
        if (!token.matches(TOKEN_FORMAT)) {
            throw new InvalidTokenException("Incorrect format of token");
        }
        String encodedIndex = token.substring(2, token.length() - 2);
        try {
            int stateCode = Integer.valueOf(encodedIndex);
            return decodeIndex(stateCode);
        } catch (NumberFormatException e) {
            logger.error("Could not parse token", e);
            throw new InvalidTokenException("Invalid encoded value: " + encodedIndex);
        }
    }

    private static int encodeIndex(int receivedMessagesCount) {
        return receivedMessagesCount * 8 + 11;
    }

    private static int decodeIndex(int stateCode) {
        return (stateCode - 11) / 8;
    }

    @SuppressWarnings("unchecked")      //allows to suppress warning of unchecked parameter type for generics
    public static String buildServerResponseBody(List<Message> messages, int lastPosition) {
        JSONArray array = getJsonArrayOfMessages(messages);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGE_PART_ALL_MSG, array);
        jsonObject.put(MESSAGE_PART_TOKEN, buildToken(lastPosition));
        return jsonObject.toJSONString();
    }

    public static JSONArray getJsonArrayOfMessages(List<Message> messages) {

        // Java * approach
        /*
            List<JSONObject> jsonMessages = messages.stream()
                    .map(MessageHelper::messageToJSONObject)
                    .collect(Collectors.toList());
        */

        List<JSONObject> jsonMessages = new LinkedList<>();
        for (Message message : messages) {
            jsonMessages.add(messageToJSONObject(message));
        }

        JSONArray array = new JSONArray();
        array.addAll(jsonMessages);
        return array;
    }

    @SuppressWarnings("unchecked")
    public static String buildSendMessageRequestBody(String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGE_PART_SINGLE_MSG, message);
        return jsonObject.toJSONString();
    }

    public static Message getClientMessage(InputStream inputStream) throws ParseException {
        JSONObject jsonObject = stringToJsonObject(inputStreamToString(inputStream));
        return jsonObjectToMessage(jsonObject);
    }

    public static Message getMessageToUpdateTemplate(InputStream inputStream) throws ParseException {
        JSONObject jsonObject = stringToJsonObject(inputStreamToString(inputStream));
        Message template = new Message();
        template.setId((String)jsonObject.get("id"));
        template.setText((String)jsonObject.get("text"));
        return template;
    }

    public static String getMessageID(InputStream inputStream) throws ParseException {
        JSONObject jsonObject = stringToJsonObject(inputStreamToString(inputStream));
        return (String)jsonObject.get("id");
    }

    public static JSONObject stringToJsonObject(String json) throws ParseException {
        // The same as (JSONObject) jsonParser.parse(json.trim());
        return JSONObject.class.cast(jsonParser.parse(json.trim()));
    }

    public static String inputStreamToString(InputStream in) {
        byte[] buffer = new byte[1024];
        int length = 0;
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            while ((length = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            return outStream.toString();
        } catch (IOException e) {
            logger.error("An error occurred while reading input stream", e);
            throw new RuntimeException(e);
        }
    }

    public static JSONObject messageToJSONObject(Message message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.Message.FIELD_ID, message.getId());
        jsonObject.put(Constants.Message.FIELD_AUTHOR, message.getAuthor());
        jsonObject.put(Constants.Message.FIELD_TIMESTAMP, message.getTimestamp());
        jsonObject.put(Constants.Message.FIELD_TEXT, message.getText());
        jsonObject.put(Constants.Message.FIELD_MESSAGE_MARK, message.getMessageMark());
        jsonObject.put(Constants.Message.FIELD_IS_DELETED, message.isDeleted());
        return jsonObject;
    }

    public static Message jsonObjectToMessage( JSONObject jsonObject) {
        String id = ((String) jsonObject.get(Constants.Message.FIELD_ID));
        String author = ((String) jsonObject.get(Constants.Message.FIELD_AUTHOR));
        long timestamp = ((long) jsonObject.get(Constants.Message.FIELD_TIMESTAMP));
        String text = ((String) jsonObject.get(Constants.Message.FIELD_TEXT));
        String messageMark = ((String) jsonObject.get(Constants.Message.FIELD_MESSAGE_MARK));
        boolean isDeleted = ((boolean) jsonObject.get(Constants.Message.FIELD_IS_DELETED));
        Message message = new Message();
        message.setId(id);
        message.setAuthor(author);
        message.setTimestamp(timestamp);
        message.setText(text);
        message.setMessageMark(messageMark);
        message.setDeleted(isDeleted);
        return message;
    }


}

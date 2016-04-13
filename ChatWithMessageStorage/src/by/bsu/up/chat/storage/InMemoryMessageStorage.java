package by.bsu.up.chat.storage;

import by.bsu.up.chat.common.models.Message;
import by.bsu.up.chat.logging.Logger;
import by.bsu.up.chat.logging.impl.Log;
import by.bsu.up.chat.utils.MessageHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class InMemoryMessageStorage implements MessageStorage {

    private static final String DEFAULT_PERSISTENCE_FILE = "messages.srg";

    private static final Logger logger = Log.create(InMemoryMessageStorage.class);

    private List<Message> messages = new ArrayList<>();

    private static final JSONParser jsonParser = new JSONParser();

    public InMemoryMessageStorage() {
        try {
            File storageFile = new File(DEFAULT_PERSISTENCE_FILE);
            storageFile.createNewFile();
            FileReader reader = new FileReader(storageFile);
            if(reader.ready()) {
                Object obj = jsonParser.parse(reader);
                JSONArray jsonMessages = (JSONArray) obj;
                Iterator<JSONObject> iterator = jsonMessages.iterator();
                while (iterator.hasNext()) {
                    messages.add(MessageHelper.jsonObjectToMessage(iterator.next()));
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to access the storage");
        } catch (ParseException e) {
            System.err.println("Unable to parse the storage");
        }
    }

    @Override
    public synchronized List<Message> getPortion(Portion portion) {
        int from = portion.getFromIndex();
        if (from < 0) {
            throw new IllegalArgumentException(String.format("Portion from index %d can not be less then 0", from));
        }
        int to = portion.getToIndex();
        if (to != -1 && to < portion.getFromIndex()) {
            throw new IllegalArgumentException(String.format("Porting last index %d can not be less then start index %d", to, from));
        }
        to = Math.max(to, messages.size());
        return messages.subList(from, to);
    }

    @Override
    public void addMessage(Message message) {
        messages.add(message);
        updateStorage();
    }

    @Override
    public boolean updateMessage(Message message) {
        boolean res = false;
        for(Message messageToUpdate : messages) {
            if(messageToUpdate.isDeleted()) {
                continue;
            }
            if(messageToUpdate.getId().equals(message.getId())) {
                messageToUpdate.setText(message.getText());
                messageToUpdate.setMessageMark(String.format("(edited at %d)", new Date().getTime()));
                res = true;
            }
        }
        updateStorage();
        return res;
    }

    @Override
    public synchronized boolean removeMessage(String messageId) {
        boolean res = false;
        for(Message messageToRemove : messages) {
            if(messageToRemove.getId().equals(messageId)) {
                messageToRemove.setText("");
                messageToRemove.setMessageMark(String.format("(deleted at %d)", new Date().getTime()));
                messageToRemove.setDeleted(true);
                res = true;
            }
        }
        updateStorage();
        return res;
    }

    @Override
    public int size() {
        return messages.size();
    }

    private void updateStorage() {
        try (FileWriter writer = new FileWriter(DEFAULT_PERSISTENCE_FILE)) {
            JSONArray array = MessageHelper.getJsonArrayOfMessages(messages);
            writer.write(array.toJSONString());
        } catch (IOException e) {
            System.err.println("Unable to access the storage");
        }
    }
}

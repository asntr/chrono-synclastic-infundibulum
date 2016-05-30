package by.bsu.asntr.chat.servlets;

import by.bsu.asntr.chat.storage.*;
import by.bsu.asntr.chat.storage.utils.Constants;
import by.bsu.asntr.chat.storage.utils.MessageHelper;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by ш on 16.05.16.
 */
@WebServlet(value = "/chat")
public class ChatServlet extends HttpServlet {

    private static MessageStorage messageStorage = new FileMessageStorage();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String token = req.getParameter("token");
            int index = MessageHelper.parseToken(token);
            if (index > messageStorage.size()) {
               resp.sendError(Constants.RESPONSE_CODE_BAD_REQUEST);
            }
            Portion portion = new Portion(index);
            List<Message> messages = messageStorage.getPortion(portion);
            String responseBody = MessageHelper.buildServerResponseBody(messages, messageStorage.size());
            resp.getOutputStream().println(responseBody);
        } catch (InvalidTokenException e) {
            resp.sendError(Constants.RESPONSE_CODE_BAD_REQUEST);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Message message = MessageHelper.getClientMessage(req.getInputStream());
            messageStorage.addMessage(message);
        } catch (ParseException e) {
            resp.sendError(Constants.RESPONSE_CODE_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Message message = MessageHelper.getMessageToUpdateTemplate(req.getInputStream());
            messageStorage.updateMessage(message);
        } catch (ParseException e) {
            resp.sendError(Constants.RESPONSE_CODE_NOT_MODIFIED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String idToDelete = MessageHelper.getMessageID(req.getInputStream());
            messageStorage.removeMessage(idToDelete);
        } catch (ParseException e) {
            resp.sendError(Constants.RESPONSE_CODE_BAD_REQUEST);
        }
    }
}

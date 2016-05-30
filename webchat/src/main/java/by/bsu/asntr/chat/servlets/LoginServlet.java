package by.bsu.asntr.chat.servlets;

import by.bsu.asntr.chat.User;
import by.bsu.asntr.chat.UserHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ш on 03.05.16.
 */
@WebServlet(value = "/login")

public class LoginServlet extends HttpServlet {
    private static List<User> users = new ArrayList<User>();

    @Override
    public void init() throws ServletException {
        super.init();
        JSONParser jsonParser = new JSONParser();
        try {
            File storageFile = new File(getProjectPath() + "/EncryptedUserData.txt");
            storageFile.createNewFile();
            FileReader reader = new FileReader(storageFile);
            if(reader.ready()) {
                Object obj = jsonParser.parse(reader);
                JSONArray jsonMessages = (JSONArray) obj;
                Iterator<JSONObject> iterator = jsonMessages.iterator();
                while (iterator.hasNext()) {
                    users.add(UserHelper.jsonObjectToUser(iterator.next()));
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to access the storage");
        } catch (ParseException e) {
            System.err.println("Unable to parse the storage");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("username");
        String password = req.getParameter("password");
        User user = new User();
        user.setPassword(password);
        user.setUser(name);
        if(isUserExists(user)) {
            resp.sendRedirect("/pages/chat.html");
        } else {
            req.setAttribute("errorMsg", "Wrong username or password!!! Try again!");
            getServletContext().getRequestDispatcher("/pages/login.jsp").forward(req, resp);
        }
    }

    private String getProjectPath() {
        String path = getServletContext().getRealPath("/");
        return path.substring(0, path.length() - 16);
    }

    public static boolean isUserExists(User user) {
        for (User realUser: users
             ) {
            try {
                if(realUser.getUser().equals(user.getUser()) && realUser.getPassword().equals(encryptPassword(user.getPassword()))) {
                    return true;
                }
            } catch (NoSuchAlgorithmException e) {
                return false;
            } catch (UnsupportedEncodingException e) {
                return false;
            }
        }
        return false;
    }

    private static String encryptPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String sha1 = "";
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(password.getBytes("UTF-8"));
        Formatter formatter = new Formatter();
        for (byte b : crypt.digest()) {
            formatter.format("%02x", b);
        }
        sha1 = formatter.toString();
        formatter.close();
        return sha1;
    }
}

package main.asntr.chat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ш on 26.04.16.
 */

@WebServlet(value = "/vars")

public class EnvVars extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String[] vars = {"JAVA_HOME", "M2_HOME", "CATALINA_HOME","USERNAME", "PATH"};
        resp.setCharacterEncoding("Windows-1251");
        for (String var : vars) {
            resp.getWriter().println(String.format("%s=%s", var, System.getenv(var)));
        }
    }
}

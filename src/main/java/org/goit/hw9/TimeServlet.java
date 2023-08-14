package org.goit.hw9;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet(name = "TimeServlet", value = "/time")
public class TimeServlet extends HttpServlet {

    public static final String TIMEZONE = "timezone";
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("C:\\Users\\Ivan\\IdeaProjects\\HorbatkoIvan-JD-HW9\\src\\main\\webapp\\templates\\");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        LocalDateTime currentTime;
        String timezone = req.getParameter(TIMEZONE);

        if (timezone != null && !timezone.isEmpty()) {
            if (timezone.contains(" ")) {
                timezone = timezone.replace(" ", "+");
            }
            resp.addCookie(new Cookie(TIMEZONE, timezone));
            ZoneId zone = ZoneId.of(timezone);
            currentTime = LocalDateTime.now(zone);

            Cookie lastTimeZone = new Cookie("lastTimeZone", timezone);
            resp.addCookie(lastTimeZone);
            lastTimeZone.setMaxAge(60 * 60);

        } else {
            currentTime = LocalDateTime.now();

            Cookie[] cookies = req.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("lastTimeZone")) {
                        timezone = cookie.getValue();

                        ZoneId zone = ZoneId.of(timezone);
                        currentTime = LocalDateTime.now(zone);
                    }
                }
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);

        Context simpleContext = new Context(req.getLocale(), Map.of("formattedTime", formattedTime, TIMEZONE, timezone != null ? timezone : "Local system timezone"));

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();

    }
}

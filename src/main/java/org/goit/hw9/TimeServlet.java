package org.goit.hw9;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class TimeServlet extends HttpServlet {

    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("C:\\goit\\dev\\dev-mod-9\\templates\\");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        LocalDateTime currentTime ;
        String timezone = req.getParameter("timezone");

        if (timezone != null && !timezone.isEmpty()) {
            if (timezone.contains(" ")){
                timezone = timezone.replace(" ","+");
            }
            resp.addCookie(new Cookie("timezone", timezone));
            ZoneId zone = ZoneId.of(timezone);
            currentTime = LocalDateTime.now(zone);

            Cookie lastTimeZone = new Cookie("lastTimeZone", timezone);
            resp.addCookie(lastTimeZone);
            lastTimeZone.setMaxAge(60*60);

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

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("formattedTime",formattedTime,
                        "timezone", timezone != null ? timezone : "Local system timezone")
        );

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();

    }

}

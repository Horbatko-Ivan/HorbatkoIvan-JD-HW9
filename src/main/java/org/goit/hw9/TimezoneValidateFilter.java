package org.goit.hw9;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TimeZone;

import static org.goit.hw9.TimeServlet.TIMEZONE;

@WebFilter("/time")
public class    TimezoneValidateFilter extends HttpFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String timezone = httpRequest.getParameter(TIMEZONE);

        if (timezone != null && !timezone.isEmpty()) {
            boolean validTimezone = false;
            String[] timezoneIDs = TimeZone.getAvailableIDs();
            for (String timezoneID : timezoneIDs) {
                if (timezoneID.equals(timezoneCheck(timezone))) {
                    validTimezone = true;
                    break;
                }
            }

            if (!validTimezone) {
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                httpResponse.getWriter().write("Invalid timezone");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    String timezoneCheck(String timezone) {
        if (timezone.contains("UTC")) {
            timezone = timezone.replace("UTC", "Etc/GMT");
        }
        if (timezone.contains(" ")) {
            timezone = timezone.replace(" ", "+");
        }
        return timezone;
    }
}

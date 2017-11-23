package t9.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import t9.mobile.util.T9MobileUtility;

/**
 * Created by chenyu on 2017/3/8.
 */
public class CrossDomainFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            try {
                T9MobileUtility.output((HttpServletResponse) servletResponse,
                        T9MobileUtility.getResultJson(0, "系统异常:" + e.getMessage(), null));
            } catch (Exception e1) {
                e.printStackTrace();
            }
            return;
        }
    }

    public void destroy() {

    }
}

package com.fastweb.core.result;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.web.context.support.WebApplicationContextUtils;


public class SpringContextInitServlet extends HttpServlet {
    
    /**
     * 
     */
    private static final long serialVersionUID = -4111466960247389386L;
    
    @Override
    public void init(ServletConfig config)  throws ServletException {
        ServletContext sc = config.getServletContext();
        ApplicationConextUtil.setAc(WebApplicationContextUtils.getWebApplicationContext(sc));

    }
    
}

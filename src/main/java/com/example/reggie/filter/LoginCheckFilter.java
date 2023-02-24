package com.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import com.fasterxml.jackson.databind.JsonSerializable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查是否登录
@Component
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter { //过滤器根据类名来实现过滤
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的url
        String requestURI = request.getRequestURI();
        log.info("拦截到请求"+requestURI);

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        //2.判断本次请求是否处理
        boolean check = check(urls,requestURI);

        //3.如果不需要处理，则直接放行
        if(check){
            filterChain.doFilter(request,response);//filterChain.doFilter将请求转发给过滤器链下一个filter , 如果没有filter那就是你请求的资源
            return;//方法结束
        }

        //4.判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee")!= null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;//方法结束
        }

        //5.如果未登录则放回登录结果，并且放回
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean check(String[] urls,String requestURI){
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url,requestURI);

            if(match){
                return true;
            }
        }
        return false;
    }
}

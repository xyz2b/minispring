package com.minis.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface View {
    // 这个 render() 方法的思路很简单，就是获取 HTTP 请求的 request 和 response，以及中间产生的业务数据 Model，最后写到 response 里面。
    // request 和 response 是 HTTP 访问时由服务器创建的，ModelAndView 是由我们的 MiniSpring 创建的。
    void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
    default String getContentType() {
        return null;
    }
    void setContentType(String contentType);
    void setUrl(String url);
    String getUrl();
    void setRequestContextAttribute(String requestContextAttribute);
    String getRequestContextAttribute();
}

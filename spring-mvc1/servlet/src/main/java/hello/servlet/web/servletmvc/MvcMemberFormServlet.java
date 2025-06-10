package hello.servlet.web.servletmvc;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
public class MvcMemberFormServlet extends HttpServlet {

    // service() 메서드는 모든 HTTP 요청(GET, POST 등)을 공통으로 처리할 수 있는 메서드.
    // 여기선 단순히 JSP 뷰로 포워딩하는 작업만 하므로 GET/POST를 구분할 필요가 없어서 service를 사용.
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 클라이언트에게 보여줄 JSP 뷰의 경로를 지정한다.
        // WEB-INF에 있으므로 사용자는 직접 접근할 수 없고, 반드시 서블릿이 forward 해줘야만 볼 수 있다.
        String viewPath = "/WEB-INF/views/new-form.jsp";

        // JSP로 요청을 넘기기 위한 RequestDispatcher 객체를 생성.
        // 이 dispatcher는 JSP 페이지로 요청(request)과 응답(response)을 넘겨주는 역할을 함.
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);

        // 실제로 JSP로 요청을 전달함. 이 때 서블릿은 실행을 멈추고, JSP가 응답을 생성.
        dispatcher.forward(request, response);
    }
}
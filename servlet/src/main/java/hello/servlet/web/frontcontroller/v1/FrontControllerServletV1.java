package hello.servlet.web.frontcontroller.v1;

import hello.servlet.web.frontcontroller.v1.controller.MemberFormControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberListControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberSaveControllerV1;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 프론트 컨트롤러: 모든 요청을 하나의 서블릿에서 받아서 각 컨트롤러로 분기함
@WebServlet(name = "frontControllerServletV1", urlPatterns = "/front-controller/v1/*")
public class FrontControllerServletV1 extends HttpServlet {

    // URL과 실제 컨트롤러 객체를 매핑하는 맵
    private Map<String, ControllerV1> controllerMap = new HashMap<>();

    // 생성자에서 URL 패턴별로 사용할 컨트롤러를 등록
    public FrontControllerServletV1() {
        controllerMap.put("/front-controller/v1/members/new-form", new MemberFormControllerV1());
        controllerMap.put("/front-controller/v1/members/save", new MemberSaveControllerV1());
        controllerMap.put("/front-controller/v1/members", new MemberListControllerV1());
    }

    // 모든 HTTP 요청은 이 메서드에서 처리됨
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FrontControllerServletV1.service");

        // 요청 URI 추출
        String requestURI = request.getRequestURI();

        // URI에 맞는 컨트롤러 찾기
        ControllerV1 controller = controllerMap.get(requestURI);

        // 해당 URI에 대한 컨트롤러가 없으면 404 반환
        if (controller == null) {
            System.out.println("controller null");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 해당 컨트롤러 실행
        controller.process(request, response);
    }
}

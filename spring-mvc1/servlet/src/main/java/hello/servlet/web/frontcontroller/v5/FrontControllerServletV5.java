package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV4HandlerAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {

    // 요청 URI와 매핑되는 컨트롤러(핸들러)를 저장해두는 Map
    private final Map<String, Object> handlerMappingMap = new HashMap<>();
    // 지원 가능한 핸들러 어댑터들을 담아두는 리스트
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    // 서블릿이 생성될 때 생성자에서 매핑정보 초기화 및 핸들러 어댑터들을 등록
    public FrontControllerServletV5() {
        initHandlerMappingMap();   // 핸들러(컨트롤러) 매핑 정보를 등록
        initHandlerAdapters();     // 핸들러 어댑터 리스트를 초기화
    }

    // V3 버전 컨트롤러들을 URI와 매핑
    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());

        // add v4
        handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerV3());
    }

    // V3 핸들러를 처리할 어댑터를 리스트에 추가
    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdapter());
        handlerAdapters.add(new ControllerV4HandlerAdapter());
    }

    /**
     * HTTP 요청이 들어오면 service 메서드가 호출된다.
     * 1) 요청 URI에 매핑된 handler(컨트롤러)를 찾는다.
     * 2) 해당 handler를 지원하는 핸들러 어댑터를 찾는다.
     * 3) 어댑터를 통해 비즈니스 로직 실행 후 ModelView를 반환받는다.
     * 4) viewResolver로 뷰 경로를 결정하고 뷰를 렌더링한다.
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1) 현재 요청을 처리할 핸들러(컨트롤러) 조회
        Object handler = getHandler(request);

        // 핸들러를 못 찾으면 404 응답
        if (handler == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 2) 해당 핸들러를 처리할 수 있는 어댑터 조회
        MyHandlerAdapter adapter = getHandlerAdapter(handler);

        // 3) 어댑터를 사용해 실제 컨트롤러 로직을 수행하고 ModelView 반환
        ModelView mv = adapter.handle(request, response, handler);

        // 4) 뷰 이름 조회 후, 뷰를 생성
        String viewName = mv.getViewName();
        MyView view = viewResolver(viewName);
        // 최종적으로 뷰 렌더링
        view.render(mv.getModel(), request, response);
    }

    /**
     * 주어진 handler를 지원하는 핸들러 어댑터를 찾아 반환한다.
     * handlerAdapters 리스트에 있는 각 어댑터를 순회하며
     * adapter.supports(handler)가 true인 어댑터를 찾는다.
     */
    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        for (MyHandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(handler)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("handler adapter를 찾을 수 없습니다. handler = " + handler);
    }

    /**
     * 요청 URI를 기준으로 handlerMappingMap에서 핸들러(컨트롤러)를 찾아 반환한다.
     */
    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }

    /**
     * 뷰 이름을 바탕으로 /WEB-INF/views/{viewName}.jsp 경로를 가지는 MyView 객체를 생성한다.
     */
    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}

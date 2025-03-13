package hello.core.singleton;

public class SingletonService {

    // static: 클래스 로드 시점에 메모리에 올라감. 따라서 instance는 프로그램 시작부터 종료까지 하나만 존재
    // final: 한 번 할당된 참조값은 변경할 수 없음을 의미. instance가 다른 객체를 참조하지 못하게 함

    private static final SingletonService instance = new SingletonService();

    // 외부에서 싱글톤 객체에 접근할 유일한 방법
    // static 메서드이므로 클래스 이름으로 호출 가능하며, 별도의 객체 생성 없이도 사용할 수 있음
    // 호출 시 항상 instance 필드의 동일한 객체를 반환
    public static SingletonService getInstance() {
        return instance;
    }

    // 생성자를 private으로 선언하여 외부에서 new SingletonService()를 통해 객체를 생성하지 못하도록 막음
    // 이는 싱글톤 패턴의 핵심으로, 싱글톤 인스턴스가 두 개 이상 생성되는 것을 방지.
    private SingletonService() {
    }

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}

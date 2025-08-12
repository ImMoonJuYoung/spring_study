package hellojpa;

import jakarta.persistence.*;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // 비영속
            Member member = new Member();
            member.setId(100L);
            member.setUsername("A");
            member.setRoleType(RoleType.USER);

            // 영속 (바로 DB에 쿼리 날라가지 않음)
//            em.persist(member); // 변경감지(Dirty Checking) 기능 덕분에 필요 없음
//            em.detach(member); // 엔티티를 영속성 컨텍스트에서 분, 준영속 상태
//            em.remove(member); // 엔티티 삭세

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}

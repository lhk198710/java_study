package com.example.study.api.community.board.repository;

import com.example.study.api.community.board.DocentEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DocentCommandRepository extends CrudRepository<DocentEntity, Long> {
    @Modifying
    @Query("update docent.docents set status = :status, updated = now() where docent_id = :docentId")
    boolean docentStatusUpdate(
            @Param("docentId") long docentId,
            @Param("status") int status);

    @Modifying
    @Query("update docent.docents set password = :password, email = :email, mobile = :mobile, address = :address, zipcode = :zipcode, thumbnail = :thumbnail, job = :job, updated = now() where docent_id = :docentId")
    int updateDocentInfo(@Param("password") String password,
                         @Param("email") String email,
                         @Param("mobile") String mobile,
                         @Param("address") String address,
                         @Param("zipcode") String zipcode,
                         @Param("thumbnail") String thumbnail,
                         @Param("job") String job,
                         @Param("docentId") long docentId);

    @Modifying
    @Query("update docent.docents set `like` = `like` + 1 where docent_id = :docentId")
    boolean docentLike(@Param("docentId") long docentId);

    @Modifying
    @Query("update docent.docents set `follower` = `follower` + 1 where docent_id = :docentId")
    boolean docentFollow(@Param("docentId") long docentId);

    @Modifying
    @Query("update docent.docents set `like` case when `like` = 0 then 0 else `like` - 1 end where docent_id = :docentId")
    boolean docentDisLike(@Param("docentId") long docentId);

    @Modifying
    @Query("update docent.docents set `follower` = case when `follower` = 0 then 0 else `follower` - 1 end where docent_id = :docentId")
    boolean docentUnfollow(@Param("docentId") long docentId);
}

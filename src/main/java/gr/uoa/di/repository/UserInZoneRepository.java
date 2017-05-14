package gr.uoa.di.repository;

import gr.uoa.di.entities.DataCollectionServiceStatus;
import gr.uoa.di.entities.UserInZone;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Repository
public interface UserInZoneRepository extends CrudRepository<UserInZone,Long> {

    @Query("select u from UserInZone u where u.elderlyUser.userId=:userId order by u.id desc")
    public List<UserInZone> getCurrentZoneForUser(@Param("userId") Long userId);

    @Query("select u from UserInZone u where u.elderlyUser.userId=:userId and u.timestamp>=:timestamp order by u.id desc")
    public List<UserInZone> getZonesAfterImpact(@Param("userId") Long userId, @Param("timestamp") Date timestamp);

}

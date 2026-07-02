package com.telco.members.tcos.repository;

import com.telco.members.tcos.dto.UserregisterDTO;
import com.telco.members.tcos.model.AccountSettlement;
import com.telco.members.tcos.model.AccountStatus;
import com.telco.members.tcos.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberReportRepository extends JpaRepository<User,Integer> {

    User findByEmpId(String empId);
    User findByUserId(String userId);

    @Query(value = "select * from users u where u.user_id = :userId", nativeQuery = true)
    Optional<User> findByUserIDs(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.empId = :empId")
    void deleteByEmpId(@Param("empId") String empId);

    User save(UserregisterDTO userregisterDTO);

    List<User> findByCompanyIdAndAccountStatus(int companyId, AccountStatus accountStatus);

    List <User> findByAccountStatus(AccountStatus accountStatus);

    @Query(value="select user_id from users where account_status = 1", nativeQuery = true)
    List<String> findAllUserId();

    List <User> findByAccountStatusAndAccountSettlement(AccountStatus accountStatus, AccountSettlement accountSettlement);

    User findByUserIdAndAccountStatus(String userId, AccountStatus accountStatus);

    @Query(value = "SELECT name FROM users WHERE user_id = :userId", nativeQuery = true)
    String findNameByUserId(@Param("userId") String userId);

    @Query(value = "SELECT company_id FROM users WHERE user_id = :userId", nativeQuery = true)
    int getCompanyIdByuserId(@Param("userId")String useerId);

    @Query(value = "SELECT company_id FROM users WHERE user_id = :userId", nativeQuery = true)
    Object getBankIdByUserId(@Param("userId")String useerId);

    @Query(value = "SELECT COUNT(*) FROM users WHERE account_status = 1", nativeQuery = true)
    int getActiveUsers();

    @Query(value = "SELECT COUNT(*) FROM users WHERE account_status = 0", nativeQuery = true)
    int getInActiveUsers();
}
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
import java.util.Date;

public interface usersRepository extends JpaRepository<User,Integer> {

    @Query("SELECT u FROM User u ORDER BY u.id DESC")
    List<User> findAllMembers();

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

    List<User> findByAccountStatusOrderByIdDesc(AccountStatus accountStatus);

    @Query(value="select user_id from users where account_status = 1 or user_type = 'Existing Member' ", nativeQuery = true)
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

    @Query("SELECT u.userId FROM User u WHERE u.userId = :userId")
    String findTicketByUserId(String userId);

    @Query("SELECT u.companyId FROM User u WHERE u.userId = :userId")
    Integer findCompanyIdByUserId(String userId);
    
  
    User save(User user);
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET dob = :_dob, date_of_retirement = :dateofretirement,approved_at = CURRENT_DATE() WHERE user_id = :userid", nativeQuery = true)
    void updateRetirementdt(@Param("_dob") Date _dob,
                              @Param("dateofretirement") Date dateofretirement,
                              @Param("userid") String userid); 
    

}

package com.telco.members.tcos.repository;

import com.telco.members.tcos.model.*;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AdviceRepository extends JpaRepository<LoanVoucher, Integer> {

    @Procedure(procedureName = "ProcessAdvice")
    void callProcessAdvice(@Param("input_bid") String bondId);

    @Procedure(procedureName = "ProcessCashier")
    void callProcessCashier(@Param("uid") String bondId);

    @Procedure(procedureName = "ProcessCancelAdvice")
    void callProcessCancleAdvice(@Param("input_uid") String bondId);

    @Procedure(procedureName = "CancleAdvice2")
    void callProcessCancleAdvice2(@Param("input_uid") String bondId);

    LoanVoucher findByUserId(String userId);

    @Query(value = "SELECT * FROM loan_voucher WHERE bond_id = :bondId LIMIT 1", nativeQuery = true)
    LoanVoucher findByBondId(@Param("bondId") String Id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE loan_voucher SET cheque_no = :chequeNo, gid = :gId, advice_date = :adviceDate, advice_downloaded = :adviceDownloaded WHERE advice_id = :adviceId", nativeQuery = true)
    void updateAdviceDownload(@Param("chequeNo") String chequeNo,
                              @Param("adviceDate") Date adviceDate,
                              @Param("adviceDownloaded") Integer adviceDownloaded,
                              @Param("adviceId") int adviceId,
                              @Param("gId") String gId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE loan_voucher SET advice_date = :adviceDate, gid = :gId, advice_downloaded = :adviceDownloaded WHERE advice_id = :adviceId", nativeQuery = true)
    void updateAdviceNeft(@Param("adviceDate") Date adviceDate,
                              @Param("adviceDownloaded") Integer adviceDownloaded,
                              @Param("adviceId") int adviceId,
                              @Param("gId") String gid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE loan_voucher SET posting_date = :posting_date, payment_status = :payment_status WHERE bond_id = :bond_id", nativeQuery = true)
    void updateCashAdvice(@Param("bond_id") String uid, @Param("posting_date") Date d1, @Param("payment_status") paymentStatus p1);

    List<LoanVoucher> findByPaymentTypeAndAdviceDownloadedAndPreparedBy(Paymenttype paymentType, int download, String preparedBy);

    List<LoanVoucher> findByAdviceDownloadedAndAdviceApproved(int download, int approved);

    LoanVoucher findByAdviceId(int id);

    List<LoanVoucher> findByAdviceApproved(int approved);

    List<LoanVoucher> findByPaymentTypeAndAdviceDownloadedAndPaymentBankAndPreparedBy(Paymenttype paymenttype, int download, String bankname, String preparedBy);

    @Procedure(procedureName = "AssignBondID")
    void callCreateBondId(@Param("input_id") int loanId);

//    @Query(value = "SELECT bond_id FROM loan_voucher lv where loan_type = :loanType ORDER BY lv.bond_id DESC LIMIT 1", nativeQuery = true)
//    Optional<String> findByLoanType(LoanType loanType);

    @Query(value = "SELECT gid FROM loan_voucher ORDER BY updated_at DESC LIMIT 1", nativeQuery = true)
    String findLatestGid();

    @Query(value = "SELECT COUNT(*) FROM loan_voucher WHERE advice_approved = 0", nativeQuery = true)
    int countAP();

    List<LoanVoucher> findByAdviceDownloadedAndAdviceApprovedAndPaymentStatusAndPaymentBank(int download, int approved, paymentStatus paymentStatus, String paymentBank);

}

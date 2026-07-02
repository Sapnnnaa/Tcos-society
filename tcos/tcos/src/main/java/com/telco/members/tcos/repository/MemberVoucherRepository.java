package com.telco.members.tcos.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.telco.members.tcos.dto.TableDto;
import com.telco.members.tcos.model.MemberReceipt;
import com.telco.members.tcos.model.Paymenttype;
import com.telco.members.tcos.model.paymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.telco.members.tcos.model.MemberVoucher;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface MemberVoucherRepository extends JpaRepository<MemberVoucher, Integer> {

    @Procedure(procedureName = "ProcessVoucher")
    void callProcessVoucher(@Param("input_vid") String voucherId);

	Optional<MemberVoucher> findByUserId(String userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE member_voucher SET voucher_downloaded = :voucherDownloaded, updated_date = :updatedDate, gid = :gId WHERE v_id = :vId", nativeQuery = true)
    void updateVoucherDownloadedByVId(@Param("voucherDownloaded") int voucherDownloaded,
                                      @Param("updatedDate") Date updatedDate,
                                      @Param("vId") String vId,
                                      @Param("gId") String gId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE member_voucher SET cheque_no = :chequeNo, voucher_downloaded = :voucherDownloaded, updated_date = :updatedDate, gid = :gId WHERE v_id = :vId", nativeQuery = true)
    void updateVoucherDownloadedByVIdCheque(@Param("chequeNo") String chequeNo,
                                      @Param("voucherDownloaded") int voucherDownloaded,
                                      @Param("updatedDate") Date updatedDate,
                                      @Param("vId") String vId, @Param("gId") String gid);



    MemberVoucher findById(int id);

    List<MemberVoucher> findByRegenerated(int regenrated);

    @Procedure(procedureName = "AssignVOCID")
    void setLatestVid(@Param("input_id") String vid);

//    @Query(value = "SELECT v_id FROM member_voucher mv ORDER BY mv.v_id DESC LIMIT 1", nativeQuery = true)
//    Optional<String> findLatestvid();

    MemberVoucher findByvId(String VId);

    @Modifying
    @Transactional
    void deleteByvId(String vId);

    List<MemberVoucher> findByPaymenttypeAndVoucherDownloadedAndPaymentBankAndPreparedBy(Paymenttype paymenttype, int download, String bankname, String preparedBy);

    List<MemberVoucher> findByPaymenttypeAndVoucherDownloadedAndPreparedBy(Paymenttype paymenttype, int download, String preparedBy);

    @Query(value = "Select generated_times from member_voucher mv where mv.v_id = :vId", nativeQuery = true)
    int findBygeneratedtimes(@Param("vId") String vId);

    List<MemberReceipt> findByVoucherApproved(int stage);

    List<MemberVoucher> findByVoucherDownloadedAndVoucherApproved(int download, int approved);

    @Query(value = "SELECT gid FROM member_voucher ORDER BY gid DESC LIMIT 1", nativeQuery = true)
    String findLatestGid();

    @Procedure(procedureName = "ProcessCancelVoc")
    void callProcessCancelVoc(@Param("input_vid") String vid);

    @Procedure(procedureName = "ProcessCancelVoc2")
    void callProcessCancelVoc2(@Param("input_vid") String vid);

    @Procedure(procedureName = "ProcessCashier1")
    void callProcessCashier1(@Param("uid") String vid);

    @Query(value = "SELECT m.v_id, m.category, m.user_id, m.name, v.vouchertype, v.amount, m.voucher_approved_date " +
            "FROM member_voucher m JOIN voucher v ON m.v_id = v.v_id " +
            "WHERE m.voucher_approved = 2", nativeQuery = true)
    List<Object[]> findApprovedReportRaw();

    @Modifying
    @Transactional
    @Query(value = "UPDATE member_voucher SET posting_date = :posting_date, payment_status = :payment_status WHERE v_id = :v_id", nativeQuery = true)
    void updateCashVoucher(@Param("v_id") String uid, @Param("posting_date") Date d1, @Param("payment_status") paymentStatus p1);

    List<MemberVoucher> findByVoucherDownloadedAndVoucherApprovedAndPaymentStatusAndPaymentBank(int download, int approved, paymentStatus paymentStatus, String selectedBank);
}
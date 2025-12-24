package com.inn.cafe.service;

import com.inn.cafe.POJO.Bill;
import com.inn.cafe.POJO.DraftOrder;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BillService {
    ResponseEntity<String> generateReport(Map<String, Object> requestMap);

    ResponseEntity<List<Bill>> getBills();

    ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap);

    ResponseEntity<String> deleteBill(Integer id);

    ResponseEntity<String> saveDraft(Map<String, Object> requestMap);

    ResponseEntity<List<DraftOrder>> getDrafts();

    ResponseEntity<DraftOrder> getDraftById(Integer id);

    ResponseEntity<String> deleteDraft(Integer id);
}

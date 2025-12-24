package com.inn.cafe.dao;

import com.inn.cafe.POJO.DraftOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DraftOrderDao extends JpaRepository<DraftOrder, Integer> {

    List<DraftOrder> getAllDraftOrders();
}

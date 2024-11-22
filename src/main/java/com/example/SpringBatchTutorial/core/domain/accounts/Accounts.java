package com.example.SpringBatchTutorial.core.domain.accounts;

import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/*
    프레임워크가 객체를 생성할 때 기본 생성자를 사용하여 인스턴스를 생성하고,
    그 후에 필드에 값을 설정하기 때문.
    기본 생성자가 없으면 JPA 에서 해당 엔티티 클래스를 제대로 다룰 수 없게 됨.
    -> JPA 와 연동하려면 기본 생성자가 필수로 요구.
    기본 생성자 없으면 JPA 엔티티 객체 생성 못함.
 */


@NoArgsConstructor // JPA 엔티티 클래스에서 기본 생성자가 필요
@Getter
@ToString
@Entity
public class Accounts {

    @Id
    private Integer id;

    private String orderItem;

    private Integer price;

    private Date orderDate;

    private Date accountDate;

    public Accounts(Orders orders){
        this.id = orders.getId();
        this.orderDate = orders.getOrderDate();
        this.orderItem = orders.getOrderItem();
        this.price = orders.getPrice();
        this.accountDate = new Date();
    }
}

package com.example.SpringBatchTutorial.core.domain.orders;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@ToString
public class Orders {

    @Id
    private Integer id;

    private String orderItem;

    private Integer price;

    private Date orderDate;
}

package com.example.SpringBatchTutorial.core.domain.orders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    private Integer id;

    private String orderItem;

    private Integer price;

    private Date orderDate;
}

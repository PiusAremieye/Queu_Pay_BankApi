package com.dummy.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "BankOtps")
@Entity
public class BankOTP extends AuditModel {
    @ManyToOne
    private BankCard bankCard;

    private Integer token;
}

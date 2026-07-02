package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_closure")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountClosure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String userId;

}

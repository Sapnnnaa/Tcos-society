package com.telco.members.tcos.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "access_control")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessControl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private accessDec AccessDeclare;
    private int accessId;
}

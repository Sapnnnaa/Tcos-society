package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "temp_rid")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TempRid {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String tRid;
}

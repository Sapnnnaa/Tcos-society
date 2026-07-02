package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "deparments")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Deparments {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int deptId;
    private String name;

}

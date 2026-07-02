package com.telco.members.tcos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "temp_vid")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TempVid {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String tVid;
}

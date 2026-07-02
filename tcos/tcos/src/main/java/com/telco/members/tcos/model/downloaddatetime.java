package com.telco.members.tcos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class downloaddatetime {
    @Id
    private int id;
    private LocalDateTime  download_date;
}

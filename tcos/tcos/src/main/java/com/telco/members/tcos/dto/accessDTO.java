package com.telco.members.tcos.dto;

import com.telco.members.tcos.model.AccessControl;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class accessDTO {
    List<AccessControl> access;
}

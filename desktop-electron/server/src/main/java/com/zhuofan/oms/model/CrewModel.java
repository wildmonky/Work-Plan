package com.zhuofan.oms.model;

import com.zhuofan.oms.orm.model.Crew;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrewModel extends Crew {

    // 班组简称，如：变一继一
    private String shortNameWithUnit;

}

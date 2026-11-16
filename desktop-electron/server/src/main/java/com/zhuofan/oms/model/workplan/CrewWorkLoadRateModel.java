package com.zhuofan.oms.model.workplan;

import com.zhuofan.oms.entity.workplan.CrewWorkLoadRateDayRecord;
import com.zhuofan.oms.orm.model.Crew;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 班组饱和度
 */
@Getter
@Setter
public class CrewWorkLoadRateModel extends CrewWorkLoadRateDayRecord {

    /**
     * 班组
     */
    private Crew crew;

    /**
     * 班组简称，如：变一继一
     */
    private String unitCrewShortName;

    public static CrewWorkLoadRateModel build(Crew crew, LocalDate date, String memberName, int workerNum) {
        CrewWorkLoadRateModel model = new CrewWorkLoadRateModel();
        model.crew = crew;
        model.setDate(date);
        model.setCrewId(crew.getId());
        model.setMemberName(memberName);
        model.setSumRatio(BigDecimal.ZERO);
        model.setWorkerNum(workerNum);
        model.setRate(BigDecimal.ZERO);
        return model;
    }

}
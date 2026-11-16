package com.zhuofan.oms.model.workplan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiAnalysisParam {

    private String planCode;

    private String content;

    private String risk;

    private String profession;

    // 重新生成
    private Boolean reCreate;

}

package com.zhuofan.oms.service.workplan2;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.zhuofan.oms.entity.workplan.extra.WorkPlanRiskAndMeasureTemplate;
import com.zhuofan.oms.mapper.workplan.WorkPlanRiskAndMeasureTemplateMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class WorkPlanRiskAndMeasureTemplateService {

    @Resource
    private WorkPlanRiskAndMeasureTemplateMapper workPlanRiskAndMeasureTemplateMapper;

    public void importDataFromTemplateFile(InputStream inputStream) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            List<WorkPlanRiskAndMeasureTemplate> templates = EasyExcel.read(bufferedInputStream)
                    .head(WorkPlanRiskAndMeasureTemplate.class)
                    .doReadAllSync();

            if (templates.isEmpty()) {
                return;
            }

            // 删除所有
            workPlanRiskAndMeasureTemplateMapper.delete(Wrappers.emptyWrapper());
            List<List<WorkPlanRiskAndMeasureTemplate>> partition = Lists.partition(templates, 500);
            partition.forEach(l -> {
                workPlanRiskAndMeasureTemplateMapper.insertBatchSomeColumn(l);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

package com.zhuofan.oms.mapper;

import com.zhuofan.oms.entity.Device;
import com.zhuofan.oms.model.Substation;

import java.util.List;

public interface DeviceMapper extends CommonMapper<Device> {

    List<Substation> findAllSubstation();

}

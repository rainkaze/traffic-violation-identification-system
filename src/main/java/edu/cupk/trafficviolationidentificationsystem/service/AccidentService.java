
package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.AccidentDto;
import edu.cupk.trafficviolationidentificationsystem.repository.AccidentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccidentService {

    @Autowired
    private AccidentMapper accidentMapper;

    public List<AccidentDto> getDevicesWithLocation(String status, String districtName) {
        return accidentMapper.getDevicesWithLocation(status, districtName);
    }
}
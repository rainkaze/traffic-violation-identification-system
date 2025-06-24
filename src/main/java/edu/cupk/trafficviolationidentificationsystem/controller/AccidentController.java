
package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.AccidentDto;
import edu.cupk.trafficviolationidentificationsystem.service.AccidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accidents")
public class AccidentController {

    @Autowired
    private AccidentService accidentService;

    @GetMapping("/devices")
    public List<AccidentDto> getDeviceLocations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String districtName) {
        return accidentService.getDevicesWithLocation(status, districtName);
    }
}
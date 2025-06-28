package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.model.EnforcementCase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EnforcementCaseMapper {
    void insert(EnforcementCase enforcementCase);
}
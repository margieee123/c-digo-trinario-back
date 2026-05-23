package com.spa.manager.logs.application.ports.output;

import com.spa.manager.logs.domain.model.Log;
import java.util.List;

public interface LogRepositoryPort {
    void save(Log log);
    List<Log> findRecientes(int limit);
}
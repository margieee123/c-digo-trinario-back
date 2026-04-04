package com.spa.manager.logs.application.ports.output;
import com.spa.manager.logs.domain.model.Log;

public interface LogRepositoryPort {
    void save(Log log);
}
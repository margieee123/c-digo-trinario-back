package com.spa.manager.logs.application.ports.input;

import com.spa.manager.logs.application.dto.LogResponse;
import java.util.List;

public interface ListarLogsUseCase {
    List<LogResponse> listarRecientes(int limit);
}
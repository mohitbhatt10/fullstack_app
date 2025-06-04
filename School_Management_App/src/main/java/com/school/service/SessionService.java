package com.school.service;

import com.school.dto.SessionDTO;
import java.util.List;

public interface SessionService {
    SessionDTO createSession(SessionDTO sessionDTO);
    SessionDTO updateSession(Long id, SessionDTO sessionDTO);
    void deleteSession(Long id);
    SessionDTO getSessionById(Long id);
    SessionDTO getSessionByName(String name);
    SessionDTO getActiveSession();
    List<SessionDTO> getAllSessions();
    SessionDTO activateSession(Long id);
}

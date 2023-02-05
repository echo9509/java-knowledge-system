package session.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author sh
 */
@RestController
public class TestController {

    @GetMapping("/mockSet")
    public ResponseEntity<Boolean> mockSet(@RequestParam("key") String key, @RequestParam("value") String value) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        session.setAttribute(key, value);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/mockGet")
    public ResponseEntity<Object> mockSet(@RequestParam("key") String key) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        return ResponseEntity.ok(session.getAttribute(key));
    }
}

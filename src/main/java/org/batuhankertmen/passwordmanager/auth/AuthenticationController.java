package org.batuhankertmen.passwordmanager.auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.batuhankertmen.passwordmanager.common.ErrorType;
import org.batuhankertmen.passwordmanager.common.exception.FieldValidationException;
import org.batuhankertmen.passwordmanager.common.exception.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@Valid @RequestBody RegistryRequestDto request, BindingResult result) {
        if(result.hasErrors()) {
            throw FieldValidationException.invalidCredentialsException(
                    ErrorType.INVALID_CREDENTIALS,
                    result.getAllErrors().toString()
            );
        }

        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@Valid @RequestBody AuthenticationRequestDto request, BindingResult result) {
        if(result.hasErrors()) {
            throw FieldValidationException.invalidCredentialsException(
                    ErrorType.INVALID_CREDENTIALS,
                    result.getAllErrors().toString()
            );
        }

        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDto> refresh(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.refresh(request, response));
    }
}

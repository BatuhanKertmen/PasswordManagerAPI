package org.batuhankertmen.oauthserver.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<ClientRegisterResponseDto> register(@RequestBody ClientRegisterRequestDto dto) throws URISyntaxException {
        ClientRegisterResponseDto client = clientService.saveClient(dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(client);
    }
}

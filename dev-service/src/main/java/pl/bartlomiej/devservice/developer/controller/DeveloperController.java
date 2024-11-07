package pl.bartlomiej.devservice.developer.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.devservice.developer.domain.AppDeveloperEntity;
import pl.bartlomiej.devservice.developer.domain.dto.DeveloperRegisterDto;
import pl.bartlomiej.devservice.developer.service.DeveloperService;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

@RestController
@RequestMapping("/v1/developers")
public class DeveloperController {

    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @PostMapping
    public ResponseEntity<ResponseModel<AppDeveloperEntity>> create(@RequestBody @Valid final DeveloperRegisterDto developerRegisterDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseModel.Builder<AppDeveloperEntity>(HttpStatus.CREATED, true)
                        .body(developerService.create(developerRegisterDto, "127.0.0.1"))
                        .build()
                );
    }
}

package eng.project.peoplehubdemo.controller;

import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.model.dto.PersonDto;
import eng.project.peoplehubdemo.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public Page<PersonDto> search(@RequestParam(value = "search", required = false) String search, @PageableDefault Pageable pageable) {
        return personService.findAll(search, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonDto create(@Valid @RequestBody CreatePersonCommand command) {
        return personService.create(command);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PersonDto updatePerson(@PathVariable int id, @RequestBody @Valid UpdatePersonCommand command) {
        return personService.updateById(id, command);
    }
}

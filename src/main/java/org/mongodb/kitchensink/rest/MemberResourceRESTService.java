package org.mongodb.kitchensink.rest;

import jakarta.validation.*;
import org.mongodb.kitchensink.model.Member;
import org.mongodb.kitchensink.service.MemberRegistration;
import org.mongodb.kitchensink.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/rest/members")
public class MemberResourceRESTService {

    private static final Logger log = Logger.getLogger(MemberResourceRESTService.class.getName());

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRegistration registration;

    @Autowired
    private Validator validator;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Member> listAllMembers() {
        return memberService.findAllOrderedByName();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Member>> lookupMemberById(@PathVariable String id) {
        Optional<Member> member = memberService.findById(id);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(member);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        ResponseEntity<?> response;

        try {
            System.out.println("createMember gets called");
            validateMember(member);
            registration.register(member);
            response = ResponseEntity.status(HttpStatus.CREATED).body(member);
        } catch (ConstraintViolationException ce) {
            response = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
        } catch (Exception e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
        }

        return response;
    }

    private void validateMember(Member member) throws ValidationException {
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }

        if (emailAlreadyExists(member.getEmail())) {
            throw new ValidationException("Unique Email Violation");
        }
    }

    private ResponseEntity<Map<String, String>> createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();
        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
    }

    public boolean emailAlreadyExists(String email) {
        System.out.println("emailAlreadyExists: " + email);
        Member member = memberService.findByEmail(email);
        return member != null; // NoResultException is not needed in Spring Data JPA
    }
}

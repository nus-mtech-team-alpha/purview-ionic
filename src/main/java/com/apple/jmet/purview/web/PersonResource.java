package com.apple.jmet.purview.web;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import com.apple.ist.locksmith.oidc.LocksmithUser;
// import com.apple.ist.locksmith.oidc.LocksmithUserService;
import com.apple.jmet.purview.domain.Person;
import com.apple.jmet.purview.domain.Role;
import com.apple.jmet.purview.dto.PersonListDto;
import com.apple.jmet.purview.repository.PersonListDtoRepository;
import com.apple.jmet.purview.repository.PersonRepository;
import com.apple.jmet.purview.repository.RoleRepository;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing {@link com.apple.jmet.purview.domain.Person}.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class PersonResource {

    private static final String SESSION_USER_INFO = "SESSION_USER_INFO";
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PersonListDtoRepository personListDtoRepository;

    @Value("${app.environment:PROD}")
    private String appEnvironment;

    @GetMapping("/persons/current")
    public Person getCurrentUser(HttpSession session) {

        return TestUtils.getTestPerson();

        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if(authentication == null) {
        //     return null;
        // }
        // Object sessionUser = session.getAttribute(SESSION_USER_INFO);
        // if(sessionUser != null) {
        //     Person sessionPerson = (Person) sessionUser;
        //     log.debug("Returning user from session: {}", sessionPerson.getEmail());
        //     return sessionPerson;
        // }else{
        //     LocksmithUser locksmithUser = LocksmithUserService.getUser();
        //     if(locksmithUser != null && hasValidGroups(locksmithUser)) {
        //         Person person = saveOrUpdatePerson(locksmithUser);
        //         session.setAttribute(SESSION_USER_INFO, person);
        //         return person;
        //     }
        // }
        // return null;
    }

    public Person getCurrentUserInternally() {
        return TestUtils.getTestPerson();

        // LocksmithUser locksmithUser = LocksmithUserService.getUser();
        // return this.personRepository.findByEmail(locksmithUser.getEmailAddress()).orElseThrow();
    }

    // private Person saveOrUpdatePerson(LocksmithUser locksmithUser){
    //     List<Role> roles = this.roleRepository.findByGroupIdIn(locksmithUser.getGroupIds());
    //     Person existingUser = this.personRepository.findByEmail(locksmithUser.getEmailAddress()).orElse(null);
    //     if(existingUser != null) {
    //         log.info("Updating existing user: {}", existingUser.getEmail());
    //         existingUser.setFirstName(locksmithUser.getGivenName());
    //         existingUser.setLastName(locksmithUser.getFamilyName());
    //         existingUser.setEmail(locksmithUser.getEmailAddress());
    //         existingUser.setRoles(roles);
    //         this.personRepository.save(existingUser);
    //         return existingUser;
    //     }else{
    //         log.info("Saving new user: {}", locksmithUser.getEmailAddress());
    //         Person person = Person.builder()
    //             .firstName(locksmithUser.getGivenName())
    //             .lastName(locksmithUser.getFamilyName())
    //             .email(locksmithUser.getEmailAddress())
    //             .username(locksmithUser.getEmailAddress().split("@")[0])
    //             .team(getPreferredRole(locksmithUser))
    //             .status("ACTIVE")
    //             .accountNonLocked(true)
    //             .roles(roles)
    //             .build();
    //         this.personRepository.save(person);
    //         return person;
    //     }
    // }

    // private boolean hasValidGroups(LocksmithUser locksmithUser) {
    //     List<Long> validGroupIds = this.roleRepository.findAll().stream().map(Role::getGroupId).toList();
    //     return CollectionUtils.containsAny(validGroupIds, locksmithUser.getGroupIds());
    // }

    // private String getPreferredRole(LocksmithUser locksmithUser) {
    //     List<Long> groupIds = locksmithUser.getGroupIds();
    //     if(groupIds.contains(this.roleRepository.findByCode("SRE").getGroupId())){
    //         return "SRE";
    //     }else if(groupIds.contains(this.roleRepository.findByCode("EPM").getGroupId())){
    //         return "EPM";
    //     }else if(groupIds.contains(this.roleRepository.findByCode("OPS").getGroupId())){
    //         return "OPS";
    //     }else if(groupIds.contains(this.roleRepository.findByCode("APS").getGroupId())){
    //         return "APS";
    //     }
    //     return "NON_JMET";
    // }

    //@PreAuthorize("hasAnyRole('ROLE_SRE')")
    @PostMapping("/persons")
    public Person createPerson(@RequestBody Person person) {
        return personRepository.save(person);
    }

    //@PreAuthorize("hasAnyRole('ROLE_SRE')")
    @PutMapping("/persons/{id}")
    public Person updatePerson(@PathVariable(value = "id", required = false) final Long id, @RequestBody Person person) {
        return personRepository.save(person);
    }

    @GetMapping("/persons")
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @GetMapping("/persons-by-team/{team}")
    public List<Person> getAllPersonsByTeam(@PathVariable String team) {
        return personRepository.findByTeam(team);
    }

    @GetMapping("/persons/{id}")
    public Person getPerson(@PathVariable Long id) {
        Optional<Person> appOpt = personRepository.findById(id);
        if(appOpt.isPresent()) {
            return appOpt.get();
        } else {
            return null;
        }
    }

    @GetMapping("/persons-list-dto")
    public List<PersonListDto> getAllPersonsDto() {
        return personListDtoRepository.findAllBy();
    }
    
}

package com.apple.jmet.purview;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.apple.jmet.purview.domain.Person;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PersonResourceTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testAddPerson() throws Exception {

        Person personToAdd = Person.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@apple.com")
                .team("JMET")
                .username("opm1")
                .roles(List.of())
                .status("ACTIVE").build();

        mvc.perform(post("/api/persons")
                        .content(TestUtils.asJsonString(personToAdd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("JohnDoe@apple.com")))
                .andExpect(jsonPath("$.team", is("JMET")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        mvc.perform(get("/api/persons/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("JohnDoe@apple.com")))
                .andExpect(jsonPath("$.team", is("JMET")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        mvc.perform(get("/api/persons")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[0].email", is("JohnDoe@apple.com")))
                .andExpect(jsonPath("$[0].team", is("JMET")))
                .andExpect(jsonPath("$[0].status", is("ACTIVE")));
    }

    @Test
    public void testEditPerson() throws Exception {

        Person personToAddAndEdit = Person.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@apple.com")
                .team("JMET")
                .username("opm1")
                .roles(List.of())
                .status("ACTIVE").build();

        mvc.perform(post("/api/persons")
                        .content(TestUtils.asJsonString(personToAddAndEdit))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("JohnDoe@apple.com")))
                .andExpect(jsonPath("$.team", is("JMET")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));


        personToAddAndEdit.setId(1L);
        personToAddAndEdit.setFirstName("Adam");
        personToAddAndEdit.setLastName("Young");
        personToAddAndEdit.setEmail("AdamYoung@apple.com");
        personToAddAndEdit.setTeam("SRE");
        personToAddAndEdit.setStatus("INACTIVE");

        mvc.perform(put("/api/persons/1")
                        .content(TestUtils.asJsonString(personToAddAndEdit))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get("/api/persons")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Adam")))
                .andExpect(jsonPath("$[0].lastName", is("Young")))
                .andExpect(jsonPath("$[0].email", is("AdamYoung@apple.com")))
                .andExpect(jsonPath("$[0].team", is("SRE")))
                .andExpect(jsonPath("$[0].status", is("INACTIVE")));
    }
    @Test
    public void testDeletePerson() throws Exception {

        Person personToAddAndDelete = Person.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@apple.com")
                .team("JMET")
                .username("opm1")
                .roles(List.of())
                .status("ACTIVE").build();

        mvc.perform(post("/api/persons")
                        .content(TestUtils.asJsonString(personToAddAndDelete))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("JohnDoe@apple.com")))
                .andExpect(jsonPath("$.team", is("JMET")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        mvc.perform(delete("/api/persons/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get("/api/persons")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());

    }
}

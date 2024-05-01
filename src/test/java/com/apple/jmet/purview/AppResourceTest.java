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

import com.apple.jmet.purview.domain.App;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AppResourceTest {

        @Autowired
        private MockMvc mvc;

        @Test
        public void testAddApp() throws Exception {

                App appToAdd = App.builder()
                                .id(1L)
                                .internalName("ABC")
                                .externalName("BCD")
                                .status("ACTIVE").build();

                mvc.perform(post("/api/apps")
                                .content(TestUtils.asJsonString(appToAdd))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.internalName", is("ABC")))
                                .andExpect(jsonPath("$.externalName", is("BCD")))
                                .andExpect(jsonPath("$.status", is("ACTIVE")));

                mvc.perform(get("/api/apps/{id}", 1)
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.internalName", is("ABC")))
                                .andExpect(jsonPath("$.externalName", is("BCD")))
                                .andExpect(jsonPath("$.status", is("ACTIVE")));

                mvc.perform(get("/api/apps")
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id", is(1)))
                                .andExpect(jsonPath("$[0].internalName", is("ABC")))
                                .andExpect(jsonPath("$[0].externalName", is("BCD")))
                                .andExpect(jsonPath("$[0].status", is("ACTIVE")));
        }

        @Test
        public void testEditApp() throws Exception {

                App appToAddAndEdit = App.builder()
                                .id(1L)
                                .internalName("ABC")
                                .externalName("BCD")
                                .status("ACTIVE").build();

                mvc.perform(post("/api/apps")
                                .content(TestUtils.asJsonString(appToAddAndEdit))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.internalName", is("ABC")))
                                .andExpect(jsonPath("$.externalName", is("BCD")))
                                .andExpect(jsonPath("$.status", is("ACTIVE")));

                appToAddAndEdit.setId(1L);
                appToAddAndEdit.setInternalName("AAA");
                appToAddAndEdit.setExternalName("BBB");
                appToAddAndEdit.setStatus("INACTIVE");

                mvc.perform(put("/api/apps/1")
                                .content(TestUtils.asJsonString(appToAddAndEdit))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk());

                mvc.perform(get("/api/apps")
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].internalName", is("ABC"))) // internalName must not be changed
                                                                                     // via API
                                .andExpect(jsonPath("$[0].externalName", is("BBB")))
                                .andExpect(jsonPath("$[0].status", is("INACTIVE")));
        }

        @Test
        public void testDeleteApp() throws Exception {

                App appToAddAndDelete = App.builder()
                                .id(1L)
                                .internalName("ABC")
                                .externalName("BCD")
                                .status("ACTIVE").build();

                mvc.perform(post("/api/apps")
                                .content(TestUtils.asJsonString(appToAddAndDelete))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(1)))
                                .andExpect(jsonPath("$.internalName", is("ABC")))
                                .andExpect(jsonPath("$.externalName", is("BCD")))
                                .andExpect(jsonPath("$.status", is("ACTIVE")));

                mvc.perform(delete("/api/apps/1")
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk());

                mvc.perform(get("/api/apps")
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());

        }
}

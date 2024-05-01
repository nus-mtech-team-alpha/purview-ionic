package com.apple.jmet.purview;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
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

import com.apple.jmet.purview.domain.Request;
import com.apple.jmet.purview.enums.RequestStatus;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestResourceTest {
    
    @Autowired
    private MockMvc mvc;
    
    @Test
    public void testAddRequest() throws Exception{

        Request requestToAdd = Request.builder()
                .description("PLEASE HELP WITH JKL")
                .siteIdToSave(1L)
                .productIdToSave(1L)
                .needByDate(new Date())
                .userId(1L)
                .build();

        mvc.perform(post("/api/requests")
                        .content(TestUtils.asJsonString(requestToAdd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.referenceId").exists())
                .andExpect(jsonPath("$.description", is("PLEASE HELP WITH JKL")))
                .andExpect(jsonPath("$.radar").exists())
                .andExpect(jsonPath("$.status", is("OPEN")));

        mvc.perform(get("/api/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].referenceId").exists())
                .andExpect(jsonPath("$[0].description", is("PLEASE HELP WITH JKL")))
                .andExpect(jsonPath("$[0].radar").exists())
                .andExpect(jsonPath("$[0].status", is("OPEN")));

    }

    @Test
    public void testEditRequest() throws Exception {

        Request requestToAddAndEdit = Request.builder()
                .description("PLEASE HELP WITH JKL")
                .siteIdToSave(1L)
                .productIdToSave(1L)
                .needByDate(new Date())
                .userId(1L)
                .build();

        mvc.perform(post("/api/requests")
                .content(TestUtils.asJsonString(requestToAddAndEdit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.referenceId").exists())
                .andExpect(jsonPath("$.description", is("PLEASE HELP WITH JKL")))
                .andExpect(jsonPath("$.radar").exists())
                .andExpect(jsonPath("$.status", is("OPEN")));

        requestToAddAndEdit.setId(1L);
        requestToAddAndEdit.setDescription("PLEASE HELP WITH JKL2");
        requestToAddAndEdit.setRequestStatus(RequestStatus.REJECTED);

        mvc.perform(put("/api/requests/1")
                        .content(TestUtils.asJsonString(requestToAddAndEdit))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get("/api/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].referenceId").exists())
                .andExpect(jsonPath("$[0].description", is("PLEASE HELP WITH JKL2")))
                .andExpect(jsonPath("$[0].radar").exists())
                .andExpect(jsonPath("$[0].status", is("REJECTED")));
    }

    @Test
    public void testDeleteRequest() throws Exception {

        Request requestToAddAndDelete = Request.builder()
                .description("PLEASE HELP WITH JKL")
                .siteIdToSave(1L)
                .productIdToSave(1L)
                .needByDate(new Date())
                .userId(1L)
                .build();

        mvc.perform(post("/api/requests")
                .content(TestUtils.asJsonString(requestToAddAndDelete))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.referenceId").exists())
                .andExpect(jsonPath("$.description", is("PLEASE HELP WITH JKL")))
                .andExpect(jsonPath("$.radar").exists())
                .andExpect(jsonPath("$.status", is("OPEN")));

        mvc.perform(delete("/api/requests/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get("/api/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }
}

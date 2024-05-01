package com.apple.jmet.purview;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.apple.jmet.purview.domain.Site;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SiteResourceTest {
    
    @Autowired
    private MockMvc mvc;

    @Test
    public void testAddSite() throws Exception {

        Site siteToAdd = Site.builder()
                .code("ABCD")
                .company("COCOMELON")
                .country("US")
                .city("NEW YORK")
                .category("CM")
                .infra("LOCAL")
                .status("ACTIVE")
                .build();

        mvc.perform(MockMvcRequestBuilders
                .post("/api/sites")
                .content(TestUtils.asJsonString(siteToAdd))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                .get("/api/sites")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].code").value("ABCD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].company").value("COCOMELON"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].country").value("US"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("NEW YORK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].category").value("CM"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].infra").value("LOCAL"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    public void testEditSite() throws Exception {

        Site siteToAddAndEdit = Site.builder()
                .code("ABCD")
                .company("COCOMELON")
                .country("US")
                .city("NEW YORK")
                .category("CM")
                .infra("LOCAL")
                .status("ACTIVE")
                .build();

        mvc.perform(MockMvcRequestBuilders
                .post("/api/sites")
                .content(TestUtils.asJsonString(siteToAddAndEdit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        siteToAddAndEdit.setId(1L);
        siteToAddAndEdit.setCompany("COCOMELON2");
        siteToAddAndEdit.setCity("NEW YORK2");
        mvc.perform(MockMvcRequestBuilders
                .put("/api/sites/1")
                .content(TestUtils.asJsonString(siteToAddAndEdit))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                .get("/api/sites")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].code").value("ABCD"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].company").value("COCOMELON2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].country").value("US"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("NEW YORK2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].category").value("CM"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].infra").value("LOCAL"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    public void testDeleteSite() throws Exception {

        Site siteToAddAndDelete = Site.builder()
                .code("ABCD")
                .company("COCOMELON")
                .country("US")
                .city("NEW YORK")
                .category("CM")
                .infra("LOCAL")
                .status("ACTIVE")
                .build();

        mvc.perform(MockMvcRequestBuilders
                .post("/api/sites")
                .content(TestUtils.asJsonString(siteToAddAndDelete))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                .delete("/api/sites/1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                .get("/api/sites")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

}

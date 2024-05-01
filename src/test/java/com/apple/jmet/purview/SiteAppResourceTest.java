package com.apple.jmet.purview;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.apple.jmet.purview.domain.App;
import com.apple.jmet.purview.domain.Site;
import com.apple.jmet.purview.domain.SiteApp;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SiteAppResourceTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testGetSiteApp() throws Exception{

        Site siteToTest = Site.builder()
                .id(1L)
                .code("ABC")
                .company("FOXCONN")
                .city("TAIWAN")
                .category("CHIP")
                .infra("FACTORY")
                .status("ACTIVE")
                .productCategories("INTERNAL").build();

        App appToTest = App.builder()
                .id(1L)
                .internalName("ABC")
                .externalName("BCD")
                .status("ACTIVE").build();

        mvc.perform(post("/api/sites")
                .content(TestUtils.asJsonString(siteToTest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(post("/api/apps")
                .content(TestUtils.asJsonString(appToTest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        SiteApp siteAppToTest = SiteApp.builder()
                .site(siteToTest)
                .app(appToTest).build();

        mvc.perform(get("/api/site-apps/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }
}

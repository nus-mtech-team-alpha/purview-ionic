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

import com.apple.jmet.purview.domain.Product;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductResourceTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testAddProduct() throws Exception{

        Product productToAdd = Product.builder()
                .code("M88")
                .category("MAC")
                .yearStarted(2023)
                .status("NPI")
                .build();

        mvc.perform(post("/api/products")
                        .content(TestUtils.asJsonString(productToAdd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("M88")))
                .andExpect(jsonPath("$.category", is("MAC")))
                .andExpect(jsonPath("$.yearStarted", is(2023)))
                .andExpect(jsonPath("$.status", is("NPI")));

        mvc.perform(get("/api/products/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("M88"))
                .andExpect(jsonPath("$.category", is("MAC")))
                .andExpect(jsonPath("$.yearStarted", is(2023)))
                .andExpect(jsonPath("$.status", is("NPI")));

        mvc.perform(get("/api/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("M88"))
                .andExpect(jsonPath("$[0].category", is("MAC")))
                .andExpect(jsonPath("$[0].yearStarted", is(2023)))
                .andExpect(jsonPath("$[0].status", is("NPI")));
    }

    @Test
    public void testEditProduct() throws Exception {

        Product productToAddAndEdit = Product.builder()
                .code("M88")
                .category("MAC")
                .yearStarted(2023)
                .status("NPI")
                .build();

        mvc.perform(post("/api/products")
                        .content(TestUtils.asJsonString(productToAddAndEdit))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("M88")))
                .andExpect(jsonPath("$.category", is("MAC")))
                .andExpect(jsonPath("$.yearStarted", is(2023)))
                .andExpect(jsonPath("$.status", is("NPI")));

        productToAddAndEdit.setId(1L);
        productToAddAndEdit.setCode("M89");
        productToAddAndEdit.setCategory("MAC");
        productToAddAndEdit.setYearStarted(2024);
        productToAddAndEdit.setStatus("MP");

        mvc.perform(put("/api/products/1")
                        .content(TestUtils.asJsonString(productToAddAndEdit))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get("/api/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("M89")))
                .andExpect(jsonPath("$[0].category", is("MAC")))
                .andExpect(jsonPath("$[0].yearStarted", is(2024)))
                .andExpect(jsonPath("$[0].status", is("MP")));
    }

    @Test
    public void testDeleteRequest() throws Exception {

        Product productToAddAndDelete = Product.builder()
                .code("M88")
                .category("MAC")
                .yearStarted(2023)
                .status("NPI")
                .build();

        mvc.perform(post("/api/products")
                        .content(TestUtils.asJsonString(productToAddAndDelete))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("M88")))
                .andExpect(jsonPath("$.category", is("MAC")))
                .andExpect(jsonPath("$.yearStarted", is(2023)))
                .andExpect(jsonPath("$.status", is("NPI")));

        mvc.perform(delete("/api/products/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(get("/api/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());

    }
}

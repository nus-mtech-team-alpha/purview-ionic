package com.apple.jmet.purview.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JmetAnsibleHelper {
    
    @Value("${jmet.ansible.version.script.path}")
    private String command;

    public String getLatestJmetAnsibleMainPublishVersion(){
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            InputStream inputStream = process.getInputStream();
            return StringUtils.chomp(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Error calling script ["+ command +"]", e);
            return StringUtils.EMPTY; 
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}



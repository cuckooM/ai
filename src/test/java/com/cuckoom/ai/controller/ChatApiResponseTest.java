package com.cuckoom.ai.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatApiResponseTest {

    @Test
    void testDefaultConstructor() {
        ChatApiResponse response = new ChatApiResponse();
        assertNull(response.getReply());
    }

    @Test
    void testParameterizedConstructor() {
        ChatApiResponse response = new ChatApiResponse("Hello!");
        assertEquals("Hello!", response.getReply());
    }

    @Test
    void testSetters() {
        ChatApiResponse response = new ChatApiResponse();
        response.setReply("Test response");

        assertEquals("Test response", response.getReply());
    }

    @Test
    void testEmptyReply() {
        ChatApiResponse response = new ChatApiResponse();
        response.setReply("");

        assertEquals("", response.getReply());
    }
}

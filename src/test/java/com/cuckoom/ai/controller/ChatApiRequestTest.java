package com.cuckoom.ai.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatApiRequestTest {

    @Test
    void testDefaultConstructor() {
        ChatApiRequest request = new ChatApiRequest();
        assertNull(request.getMessage());
        assertNull(request.getModel());
    }

    @Test
    void testParameterizedConstructor() {
        ChatApiRequest request = new ChatApiRequest("Hello", "qwen");
        assertEquals("Hello", request.getMessage());
        assertEquals("qwen", request.getModel());
    }

    @Test
    void testSetters() {
        ChatApiRequest request = new ChatApiRequest();
        request.setMessage("Test message");
        request.setModel("test-model");

        assertEquals("Test message", request.getMessage());
        assertEquals("test-model", request.getModel());
    }

    @Test
    void testMessageWithoutModel() {
        ChatApiRequest request = new ChatApiRequest();
        request.setMessage("Hello");

        assertEquals("Hello", request.getMessage());
        assertNull(request.getModel());
    }

    @Test
    void testEmptyMessage() {
        ChatApiRequest request = new ChatApiRequest();
        request.setMessage("");
        request.setModel("qwen");

        assertEquals("", request.getMessage());
        assertEquals("qwen", request.getModel());
    }
}

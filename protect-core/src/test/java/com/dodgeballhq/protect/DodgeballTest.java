package com.dodgeballhq.protect;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class DodgeballTest {
    @Test
    public void emptyKeyThrows() {
        try {
            Dodgeball db = Dodgeball.builder().build();
            fail();
        } catch (IllegalStateException exc) {
            assertNotNull(exc.getMessage());
            assertNotEquals(exc.getMessage(), "");
        }
    }

    @Test
    public void defaultUrlProd() {
        Dodgeball db = Dodgeball.builder().setApiKeys("NotValidated").build();
        assertEquals(db.m_url, "https://api.dodgeballhq.com");
    }
}

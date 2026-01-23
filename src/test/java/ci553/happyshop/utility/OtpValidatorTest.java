package ci553.happyshop.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OtpValidatorTest {

    @Test
    void validOtpExamples() {
        assertTrue(OtpValidator.isValid("123456"));
        assertTrue(OtpValidator.isValid("000000"));
        assertTrue(OtpValidator.isValid("999999"));
    }

    @Test
    void boundaryLengthCases() {
        assertFalse(OtpValidator.isValid("12345"));    // 5
        assertTrue(OtpValidator.isValid("123456"));    // 6
        assertFalse(OtpValidator.isValid("1234567"));  // 7
    }

    @Test
    void invalidCharacters() {
        assertFalse(OtpValidator.isValid("12a456"));
        assertFalse(OtpValidator.isValid("12-456"));
        assertFalse(OtpValidator.isValid("12 456"));
    }

    @Test
    void emptyOrNull() {
        assertFalse(OtpValidator.isValid(""));
        assertFalse(OtpValidator.isValid(null));
    }
}

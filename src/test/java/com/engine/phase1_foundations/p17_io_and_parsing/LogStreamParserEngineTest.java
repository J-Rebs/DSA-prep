package com.engine.phase1_foundations.p17_io_and_parsing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class LogStreamParserEngineTest {

    private LogStreamParserEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new LogStreamParserEngine();
    }

    // ==========================================
    // 1. FAST LOG TOKEN SCANNER TESTS
    // ==========================================
    @Test
    public void testFastLogTokenScan() throws Exception {
        String logData = "100 INFO UserLoggedIn\n200 WARN DiskSpaceLow   300 ERROR OutOfMemory";
        InputStream stream = new ByteArrayInputStream(logData.getBytes(StandardCharsets.UTF_8));
        List<String> expected = Arrays.asList("100", "INFO", "UserLoggedIn", "200", "WARN", "DiskSpaceLow", "300", "ERROR", "OutOfMemory");
        assertEquals(expected, engine.fastLogTokenScan(stream));
    }

    // ==========================================
    // 2. ATOI STRING TO INTEGER TESTS
    // ==========================================
    static Stream<Object[]> atoiCases() {
        return Stream.of(
            new Object[]{"42", 42},
            new Object[]{"   -42", -42},
            new Object[]{"4193 with words", 4193},
            new Object[]{"words and 987", 0},
            new Object[]{"-91283472332", Integer.MIN_VALUE},
            new Object[]{"91283472332", Integer.MAX_VALUE},
            new Object[]{"", 0},
            new Object[]{null, 0}
        );
    }

    @ParameterizedTest
    @MethodSource("atoiCases")
    public void testParseStringToInteger(String input, int expected) {
        assertEquals(expected, engine.parseStringToInteger(input));
    }

    // ==========================================
    // 3. VALID IP ADDRESS AND PORT TESTS
    // ==========================================
    static Stream<Object[]> ipCases() {
        return Stream.of(
            new Object[]{"192.168.1.1", "IPv4"},
            new Object[]{"192.168.1.1:8080", "IPv4"},
            new Object[]{"256.1.2.3", "Invalid"},
            new Object[]{"192.168.01.1", "Invalid"},
            new Object[]{"2001:0db8:85a3:0000:0000:8a2e:0370:7334", "IPv6"},
            new Object[]{"2001:0db8:85a3::8a2e:0370:7334", "Invalid"},
            new Object[]{"1.1.1.1:99999", "Invalid"},
            new Object[]{"InvalidIP", "Invalid"},
            new Object[]{null, "Invalid"}
        );
    }

    @ParameterizedTest
    @MethodSource("ipCases")
    public void testValidateIpAddressAndPort(String input, String expected) {
        assertEquals(expected, engine.validateIpAddressAndPort(input));
    }

    // ==========================================
    // 4. CSV PARSER TESTS
    // ==========================================
    @Test
    public void testParseCsvLine() {
        String csvLine = "101,\"Smith, John\",Developer,\"Line1\nLine2\",\"He said \"\"Hello\"\"\"";
        List<String> expected = Arrays.asList("101", "Smith, John", "Developer", "Line1\nLine2", "He said \"Hello\"");
        assertEquals(expected, engine.parseCsvLine(csvLine));
    }

    // ==========================================
    // 5. RPN EXPRESSION EVALUATOR TESTS
    // ==========================================
    static Stream<Object[]> rpnCases() {
        return Stream.of(
            new Object[]{new String[]{"2", "1", "+", "3", "*"}, 9},
            new Object[]{new String[]{"4", "13", "5", "/", "+"}, 6},
            new Object[]{new String[]{"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"}, 22}
        );
    }

    @ParameterizedTest
    @MethodSource("rpnCases")
    public void testEvaluateRpnExpression(String[] tokens, int expected) {
        assertEquals(expected, engine.evaluateRpnExpression(tokens));
    }

    // ==========================================
    // 6. BASIC CALCULATOR TESTS
    // ==========================================
    static Stream<Object[]> calculatorCases() {
        return Stream.of(
            new Object[]{"1 + 1", 2},
            new Object[]{" 2-1 + 2 ", 3},
            new Object[]{"(1+(4+5+2)-3)+(6+8)", 23},
            new Object[]{"3 + 2 * 2", 7},
            new Object[]{" 3/2 ", 1},
            new Object[]{" 3+5 / 2 ", 5}
        );
    }

    @ParameterizedTest
    @MethodSource("calculatorCases")
    public void testEvaluateBasicCalculator(String expression, int expected) {
        assertEquals(expected, engine.evaluateBasicCalculator(expression));
    }

    // ==========================================
    // 7. BINARY TREE CODEC TESTS
    // ==========================================
    @Test
    public void testTreeCodec() {
        LogStreamParserEngine.TreeNode root = new LogStreamParserEngine.TreeNode(1);
        root.left = new LogStreamParserEngine.TreeNode(2);
        root.right = new LogStreamParserEngine.TreeNode(3);
        root.right.left = new LogStreamParserEngine.TreeNode(4);
        root.right.right = new LogStreamParserEngine.TreeNode(5);

        String serialized = engine.serializeBinaryTree(root);
        LogStreamParserEngine.TreeNode deserialized = engine.deserializeBinaryTree(serialized);

        assertEquals(1, deserialized.val);
        assertEquals(2, deserialized.left.val);
        assertEquals(3, deserialized.right.val);
        assertEquals(4, deserialized.right.left.val);
        assertEquals(5, deserialized.right.right.val);
    }

    // ==========================================
    // 8. NESTED LIST PARSER TESTS
    // ==========================================
    @Test
    public void testParseNestedList() {
        String input = "[1,[4,[6]]]";
        List<Integer> expected = Arrays.asList(1, 4, 6);
        assertEquals(expected, engine.parseNestedList(input));

        String input2 = "[-1, [2, -3]]";
        List<Integer> expected2 = Arrays.asList(-1, 2, -3);
        assertEquals(expected2, engine.parseNestedList(input2));
    }

    // ==========================================
    // 9. READ N CHARACTERS READ4 TESTS
    // ==========================================
    @Test
    public void testReadNCharactersRead4() {
        String streamContent = "abcdefghijklm";
        LogStreamParserEngine.Read4Api mockReader = new LogStreamParserEngine.Read4Api() {
            private int ptr = 0;
            @Override
            public int read4(char[] buf4) {
                int count = 0;
                while (count < 4 && ptr < streamContent.length()) {
                    buf4[count++] = streamContent.charAt(ptr++);
                }
                return count;
            }
        };

        engine.resetRead4State();
        char[] buf = new char[5];
        int readCount = engine.readNCharactersRead4(buf, 5, mockReader);
        assertEquals(5, readCount);
        assertEquals("abcde", new String(buf, 0, readCount));

        char[] buf2 = new char[5];
        int readCount2 = engine.readNCharactersRead4(buf2, 5, mockReader);
        assertEquals(5, readCount2);
        assertEquals("fghij", new String(buf2, 0, readCount2));
    }

    // ==========================================
    // 10. EXPRESSION ADD OPERATORS TESTS
    // ==========================================
    @Test
    public void testExpressionAddOperators() {
        List<String> res1 = engine.expressionAddOperators("123", 6);
        assertTrue(res1.contains("1+2+3"));
        assertTrue(res1.contains("1*2*3"));

        List<String> res2 = engine.expressionAddOperators("232", 8);
        assertTrue(res2.contains("2*3+2"));
        assertTrue(res2.contains("2+3*2"));
    }

    // ==========================================
    // 11. LENGTH-PREFIXED STREAM CODEC TESTS
    // ==========================================
    @Test
    public void testStreamCodec() {
        List<String> original = Arrays.asList("lint", "code", "love", "you#too", "123#456");
        String encoded = engine.encodeStreamList(original);
        assertEquals("4#lint4#code4#love7#you#too7#123#456", encoded);
        List<String> decoded = engine.decodeStreamList(encoded);
        assertEquals(original, decoded);
    }

    // ==========================================
    // RIGOROUS GATEKEEPER NESTED SUITE
    // ==========================================
    @Nested
    class RigorousGatekeeper {

        @Test
        public void testNullAndEmptyBoundaries() throws Exception {
            assertEquals(Collections.emptyList(), engine.fastLogTokenScan(null));
            assertEquals(0, engine.parseStringToInteger(null));
            assertEquals("Invalid", engine.validateIpAddressAndPort(null));
            assertEquals(Collections.emptyList(), engine.parseCsvLine(null));
            assertEquals(0, engine.evaluateRpnExpression(null));
            assertEquals(0, engine.evaluateBasicCalculator(null));
            assertEquals("null", engine.serializeBinaryTree(null));
            assertNull(engine.deserializeBinaryTree("null"));
            assertEquals(Collections.emptyList(), engine.parseNestedList(null));
            assertEquals(Collections.emptyList(), engine.expressionAddOperators("", 5));
            assertEquals("", engine.encodeStreamList(Collections.emptyList()));
            assertEquals(Collections.emptyList(), engine.decodeStreamList(""));
        }

        @Test
        public void testPerformanceStressLinearTime() {
            assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
                // Stress test encode/decode with 100,000 strings
                List<String> largeList = Arrays.asList(new String[100000]);
                Arrays.fill(largeList.toArray(), "payload_string_data_12345");
                String encoded = engine.encodeStreamList(largeList);
                List<String> decoded = engine.decodeStreamList(encoded);
                assertEquals(100000, decoded.size());
            });
        }
    }
}

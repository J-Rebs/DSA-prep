package com.engine.phase1_foundations.p17_io_and_parsing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Log File & Stream Codec Parser Engine
 * 
 * Provides high-throughput fast I/O, state-machine tokenization, expression parsing,
 * codec serialization/deserialization, and length-prefixed stream framing.
 */
public class LogStreamParserEngine {

    // --- DATA STRUCTURE DEFINITIONS ---

    public static class TreeNode {
        public int val;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }

        public TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public interface Read4Api {
        int read4(char[] buf4);
    }

    // --- READ4 STREAM BUFFER STATE ---
    private final char[] buf4 = new char[4];
    private int buf4Ptr = 0;
    private int buf4Count = 0;

    /**
     * Resets internal buffer state for Read4 stream.
     */
    public void resetRead4State() {
        this.buf4Ptr = 0;
        this.buf4Count = 0;
    }

    // --- 11-PROBLEM MASTERY LADDER METHODS ---

    /**
     * Problem 1: Fast I/O & Multi-Line Tokenizer Scanner
     * High-throughput log ingest scanner using BufferedReader and zero-regex tokenization.
     */
    public List<String> fastLogTokenScan(InputStream stream) throws Exception {
        if (stream == null) return Collections.emptyList();
        List<String> tokens = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            int len = line.length();
            int i = 0;
            while (i < len) {
                while (i < len && Character.isWhitespace(line.charAt(i))) {
                    i++;
                }
                if (i >= len) break;
                int start = i;
                while (i < len && !Character.isWhitespace(line.charAt(i))) {
                    i++;
                }
                tokens.add(line.substring(start, i));
            }
        }
        return tokens;
    }

    /**
     * Problem 2: String to Integer (atoi) State Machine Parser
     * Converts string to 32-bit signed integer with whitespace trimming, sign detection, and overflow guards.
     */
    public int parseStringToInteger(String s) {
        if (s == null) return 0;
        int n = s.length();
        int i = 0;

        while (i < n && s.charAt(i) == ' ') {
            i++;
        }
        if (i >= n) return 0;

        int sign = 1;
        char firstChar = s.charAt(i);
        if (firstChar == '+') {
            i++;
        } else if (firstChar == '-') {
            sign = -1;
            i++;
        }

        long result = 0;
        while (i < n && Character.isDigit(s.charAt(i))) {
            int digit = s.charAt(i) - '0';
            result = result * 10 + digit;
            if (sign == 1 && result > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            if (sign == -1 && -result < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }
            i++;
        }

        return (int) (result * sign);
    }

    /**
     * Problem 3: Valid IP Address & Port Format Parser
     * Validates socket address formats (IPv4 with optional :port, IPv6, or Invalid).
     */
    public String validateIpAddressAndPort(String input) {
        if (input == null || input.isEmpty()) return "Invalid";

        // Check for IPv4 / IPv4:Port
        if (input.contains(".")) {
            String ipPart = input;
            if (input.contains(":")) {
                String[] socketParts = input.split(":", -1);
                if (socketParts.length != 2) return "Invalid";
                ipPart = socketParts[0];
                try {
                    int port = Integer.parseInt(socketParts[1]);
                    if (port < 1 || port > 65535) return "Invalid";
                } catch (NumberFormatException e) {
                    return "Invalid";
                }
            }
            String[] tokens = ipPart.split("\\.", -1);
            if (tokens.length != 4) return "Invalid";
            for (String token : tokens) {
                if (token.isEmpty() || token.length() > 3) return "Invalid";
                if (token.charAt(0) == '0' && token.length() > 1) return "Invalid";
                for (char c : token.toCharArray()) {
                    if (!Character.isDigit(c)) return "Invalid";
                }
                int val = Integer.parseInt(token);
                if (val < 0 || val > 255) return "Invalid";
            }
            return "IPv4";
        }

        // Check for IPv6
        if (input.contains(":")) {
            String[] tokens = input.split(":", -1);
            if (tokens.length != 8) return "Invalid";
            for (String token : tokens) {
                if (token.isEmpty() || token.length() > 4) return "Invalid";
                for (char c : token.toCharArray()) {
                    boolean isHex = (c >= '0' && c <= '9') ||
                                    (c >= 'a' && c <= 'f') ||
                                    (c >= 'A' && c <= 'F');
                    if (!isHex) return "Invalid";
                }
            }
            return "IPv6";
        }

        return "Invalid";
    }

    /**
     * Problem 4: CSV & Delimiter Log Line Parser with Quotes & Escaping
     * Parses CSV record into fields handling quotes, commas, and escaped quotes ("").
     */
    public List<String> parseCsvLine(String line) {
        if (line == null) return Collections.emptyList();
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int n = line.length();

        for (int i = 0; i < n; i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < n && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; // Skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields;
    }

    /**
     * Problem 5: Reverse Polish Notation (RPN) Stream Evaluator
     * Evaluates postfix arithmetic expressions with +, -, *, / using operand stack.
     */
    public int evaluateRpnExpression(String[] tokens) {
        if (tokens == null || tokens.length == 0) return 0;
        int[] stack = new int[tokens.length];
        int top = -1;

        for (String token : tokens) {
            if (token.equals("+")) {
                int b = stack[top--];
                int a = stack[top--];
                stack[++top] = a + b;
            } else if (token.equals("-")) {
                int b = stack[top--];
                int a = stack[top--];
                stack[++top] = a - b;
            } else if (token.equals("*")) {
                int b = stack[top--];
                int a = stack[top--];
                stack[++top] = a * b;
            } else if (token.equals("/")) {
                int b = stack[top--];
                int a = stack[top--];
                stack[++top] = a / b;
            } else {
                stack[++top] = Integer.parseInt(token);
            }
        }

        return stack[top];
    }

    /**
     * Problem 6: Basic Calculator / Expression Parser with Parentheses
     * Evaluates math expressions with +, -, *, /, (, ) adhering to standard operator precedence.
     */
    public int evaluateBasicCalculator(String s) {
        if (s == null || s.isEmpty()) return 0;
        int[] index = new int[]{0};
        return parseCalcExpression(s, index);
    }

    private int parseCalcExpression(String s, int[] index) {
        Deque<Integer> stack = new ArrayDeque<>();
        char sign = '+';
        int num = 0;
        int n = s.length();

        while (index[0] < n) {
            char c = s.charAt(index[0]);
            index[0]++;

            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }
            if (c == '(') {
                num = parseCalcExpression(s, index);
            }

            if ((!Character.isDigit(c) && c != ' ') || index[0] == n) {
                if (sign == '+') stack.push(num);
                else if (sign == '-') stack.push(-num);
                else if (sign == '*') stack.push(stack.pop() * num);
                else if (sign == '/') stack.push(stack.pop() / num);

                sign = c;
                num = 0;
            }

            if (c == ')') break;
        }

        int result = 0;
        for (int val : stack) {
            result += val;
        }
        return result;
    }

    /**
     * Problem 7: Binary Tree Serialization & Deserialization Codec
     */
    public String serializeBinaryTree(TreeNode root) {
        if (root == null) return "null";
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("null,");
            return;
        }
        sb.append(node.val).append(",");
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    public TreeNode deserializeBinaryTree(String data) {
        if (data == null || data.equals("null")) return null;
        String[] nodes = data.split(",");
        Deque<String> queue = new ArrayDeque<>(Arrays.asList(nodes));
        return deserializeHelper(queue);
    }

    private TreeNode deserializeHelper(Deque<String> queue) {
        if (queue.isEmpty()) return null;
        String val = queue.poll();
        if (val.equals("null") || val.isEmpty()) return null;
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserializeHelper(queue);
        node.right = deserializeHelper(queue);
        return node;
    }

    /**
     * Problem 8: Nested List / JSON Structure Parser Iterator
     * Flattens nested string structure like "[1,[4,[6]]]" into flat integer list.
     */
    public List<Integer> parseNestedList(String nestedStr) {
        if (nestedStr == null || nestedStr.isEmpty()) return Collections.emptyList();
        List<Integer> result = new ArrayList<>();
        int n = nestedStr.length();
        int i = 0;
        while (i < n) {
            char c = nestedStr.charAt(i);
            if (Character.isDigit(c) || c == '-') {
                int start = i;
                if (c == '-') i++;
                while (i < n && Character.isDigit(nestedStr.charAt(i))) {
                    i++;
                }
                result.add(Integer.parseInt(nestedStr.substring(start, i)));
            } else {
                i++;
            }
        }
        return result;
    }

    /**
     * Problem 9: Stream Chunk Splitter & Read N Characters Given Read4
     * Reads up to N characters using underlying Read4 API, preserving buffer state.
     */
    public int readNCharactersRead4(char[] buf, int n, Read4Api reader) {
        int totalRead = 0;
        while (totalRead < n) {
            if (buf4Ptr == buf4Count) {
                buf4Count = reader.read4(buf4);
                buf4Ptr = 0;
                if (buf4Count == 0) break;
            }
            while (totalRead < n && buf4Ptr < buf4Count) {
                buf[totalRead++] = buf4[buf4Ptr++];
            }
        }
        return totalRead;
    }

    /**
     * Problem 10: State-Machine Expression Add Operators & AST Parser
     * Inserts +, -, * into digits string evaluating to target.
     */
    public List<String> expressionAddOperators(String num, int target) {
        if (num == null || num.isEmpty()) return Collections.emptyList();
        List<String> result = new ArrayList<>();
        addOperatorsBacktrack(num, target, 0, 0, 0, "", result);
        return result;
    }

    private void addOperatorsBacktrack(String num, long target, int index, long eval, long mult, String expr, List<String> result) {
        if (index == num.length()) {
            if (eval == target) {
                result.add(expr);
            }
            return;
        }

        for (int i = index; i < num.length(); i++) {
            if (i != index && num.charAt(index) == '0') break; // Leading zero check
            String curStr = num.substring(index, i + 1);
            long cur = Long.parseLong(curStr);

            if (index == 0) {
                addOperatorsBacktrack(num, target, i + 1, cur, cur, curStr, result);
            } else {
                addOperatorsBacktrack(num, target, i + 1, eval + cur, cur, expr + "+" + curStr, result);
                addOperatorsBacktrack(num, target, i + 1, eval - cur, -cur, expr + "-" + curStr, result);
                addOperatorsBacktrack(num, target, i + 1, eval - mult + mult * cur, mult * cur, expr + "*" + curStr, result);
            }
        }
    }

    /**
     * Problem 11: High-Throughput Log File Streaming Lexer & Length-Prefixed Codec
     * Encodes string list into length-prefixed stream format ("<len>#<str>") and decodes back.
     */
    public String encodeStreamList(List<String> strs) {
        if (strs == null || strs.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            int len = (s == null) ? 0 : s.length();
            sb.append(len).append('#');
            if (s != null) {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    public List<String> decodeStreamList(String s) {
        if (s == null || s.isEmpty()) return Collections.emptyList();
        List<String> result = new ArrayList<>();
        int i = 0;
        int n = s.length();

        while (i < n) {
            int hashIdx = s.indexOf('#', i);
            if (hashIdx == -1) break;
            int len = Integer.parseInt(s.substring(i, hashIdx));
            i = hashIdx + 1;
            String str = s.substring(i, i + len);
            result.add(str);
            i += len;
        }

        return result;
    }
}

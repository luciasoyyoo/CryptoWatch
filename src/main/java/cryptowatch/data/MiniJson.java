package cryptowatch.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MiniJson {
    private final String s;
    private int i;

    private MiniJson(String s) { this.s = s; }

    public static Object parse(String text) {
        if (text == null) throw new IllegalArgumentException("JSON nulo");
        MiniJson p = new MiniJson(text);
        p.ws();
        Object v = p.value();
        p.ws();
        if (p.i != p.s.length()) throw p.error("contenido inesperado");
        return v;
    }

    public static double num(Map<?, ?> m, String key, double def) {
        return m.get(key) instanceof Number n ? n.doubleValue() : def;
    }

    public static String str(Map<?, ?> m, String key, String def) {
        return m.get(key) instanceof String x ? x : def;
    }

    private Object value() {
        if (i >= s.length()) throw error("fin inesperado");
        return switch (s.charAt(i)) {
            case '{' -> object();
            case '[' -> array();
            case '"' -> string();
            case 't' -> { word("true"); yield Boolean.TRUE; }
            case 'f' -> { word("false"); yield Boolean.FALSE; }
            case 'n' -> { word("null"); yield null; }
            default -> number();
        };
    }

    private Map<String,Object> object() {
        Map<String,Object> m = new LinkedHashMap<>();
        i++; ws();
        if (peek() == '}') { i++; return m; }
        while (true) {
            ws(); String key = string(); ws(); expect(':'); ws();
            m.put(key, value()); ws();
            char c = next();
            if (c == '}') return m;
            if (c != ',') throw error("se esperaba ',' o '}'");
        }
    }

    private List<Object> array() {
        List<Object> list = new ArrayList<>();
        i++; ws();
        if (peek() == ']') { i++; return list; }
        while (true) {
            ws(); list.add(value()); ws();
            char c = next();
            if (c == ']') return list;
            if (c != ',') throw error("se esperaba ',' o ']'");
        }
    }

    private String string() {
        expect('"');
        StringBuilder b = new StringBuilder();
        while (i < s.length()) {
            char c = s.charAt(i++);
            if (c == '"') return b.toString();
            if (c != '\\') { b.append(c); continue; }
            if (i >= s.length()) throw error("escape incompleto");
            char e = s.charAt(i++);
            switch (e) {
                case '"', '\\', '/' -> b.append(e);
                case 'b' -> b.append('\b');
                case 'f' -> b.append('\f');
                case 'n' -> b.append('\n');
                case 'r' -> b.append('\r');
                case 't' -> b.append('\t');
                case 'u' -> {
                    if (i + 4 > s.length()) throw error("unicode incompleto");
                    String hex = s.substring(i, i + 4);
                    try { b.append((char) Integer.parseInt(hex, 16)); }
                    catch (NumberFormatException ex) { throw error("unicode inválido"); }
                    i += 4;
                }
                default -> throw error("escape inválido");
            }
        }
        throw error("cadena sin cerrar");
    }

    private Double number() {
        int start = i;
        if (peek() == '-') i++;
        while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
        if (i < s.length() && s.charAt(i) == '.') {
            i++; while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
        }
        if (i < s.length() && (s.charAt(i) == 'e' || s.charAt(i) == 'E')) {
            i++; if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) i++;
            while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
        }
        if (start == i) throw error("valor JSON inválido");
        try { return Double.valueOf(s.substring(start, i)); }
        catch (NumberFormatException ex) { throw error("número inválido"); }
    }

    private void word(String word) {
        if (!s.startsWith(word, i)) throw error("token inválido");
        i += word.length();
    }
    private char next() {
        if (i >= s.length()) throw error("fin inesperado");
        return s.charAt(i++);
    }
    private char peek() { return i < s.length() ? s.charAt(i) : '\0'; }
    private void expect(char c) { if (next() != c) throw error("se esperaba '" + c + "'"); }
    private void ws() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }
    private IllegalArgumentException error(String msg) { return new IllegalArgumentException(msg + " en posición " + i); }
}

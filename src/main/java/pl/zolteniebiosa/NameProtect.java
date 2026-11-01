package pl.zolteniebiosa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;

/**
 * Podmienia Twoj prawdziwy nick w kazdym tekscie (czat, TAB, scoreboard, nad glowa, wiadomosci systemowe).
 * Nie rozroznia wielkosci liter i obsluguje wiele wejsc renderowania tekstu.
 */
public class NameProtect {
    private static String realCached = "";
    private static Pattern pat;

    private static String real() {
        try {
            String n = MinecraftClient.getInstance().getSession().getUsername();
            if (n != null && !n.isEmpty() && !n.equals(realCached)) {
                realCached = n;
                pat = Pattern.compile(Pattern.quote(n), Pattern.CASE_INSENSITIVE);
            }
        } catch (Throwable ignored) { }
        return realCached;
    }

    public static String apply(String s) {
        if (s == null || s.isEmpty() || !Mods.NAMEPROTECT.enabled) return s;
        String fake = Mods.NAMEPROTECT.txt("name").trim();
        if (fake.isEmpty()) return s;
        String r = real();
        if (r.isEmpty() || pat == null || s.length() < r.length()) return s;
        Matcher m = pat.matcher(s);
        if (!m.find()) return s;
        return m.replaceAll(Matcher.quoteReplacement(fake));
    }
}

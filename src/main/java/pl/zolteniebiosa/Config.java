package pl.zolteniebiosa;

import net.minecraft.item.Item;

public class Config {
    public static boolean tracers = false;
    public static boolean blockEsp = false;
    public static boolean autoLeave = false;
    public static boolean night = false;

    public static int tracerDist = 100;   // 1-1000
    public static int tracerMode = 0;     // 0 gracze, 1 moby, 2 oba
    public static int espDist = 32;       // 1-48
    public static int espPreset = 0;
    public static int leaveDist = 20;     // 1-100
    public static int theme = 0;          // 0 teczowy, 1 czerwony, 2 niebieski, 3 zielony, 4 fioletowy

    public static Item[] savedInv = null; // indeksy = id slotow 9..44
}

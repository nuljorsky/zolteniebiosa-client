package pl.zolteniebiosa;

import java.util.HashMap;
import java.util.Map;

public class Lang {
    public static int cur = 0; // 0 PL, 1 EN, 2 DE, 3 RU
    public static final String[] NAMES = {"Polski", "English", "Deutsch", "Русский"};
    private static final Map<String, String[]> M = new HashMap<>();

    private static void a(String k, String pl, String en, String de, String ru) {
        M.put(k, new String[]{pl, en, de, ru});
    }

    public static String t(String k) {
        String[] v = M.get(k);
        return v == null ? k : v[cur];
    }

    static {
        // moduly
        a("m.theme", "Motyw", "Theme", "Design", "Тема");
        a("m.viewmodel", "ViewModel (miecz i ręka)", "ViewModel (sword and hand)", "ViewModel (Schwert und Hand)", "ViewModel (меч и рука)");
        a("m.night", "Noc", "Night", "Nacht", "Ночь");
        a("m.fullbright", "Jasność (Fullbright)", "Fullbright", "Fullbright", "Яркость");
        a("m.weather", "Pogoda", "Weather", "Wetter", "Погода");
        a("m.hitsound", "Dźwięk trafienia", "Hit sound", "Treffer-Sound", "Звук удара");
        a("m.hud", "Neonowy HUD", "Neon HUD", "Neon-HUD", "Неоновый HUD");
        a("m.voice", "Głos lektorki", "Voice assistant", "Sprachassistentin", "Голосовая помощница");
        a("m.pet", "Pet", "Pet", "Haustier", "Питомец");
        a("m.tracers", "Tracery", "Tracers", "Tracer", "Трейсеры");
        a("m.nametags", "NameTags", "NameTags", "NameTags", "NameTags");
        a("m.nameprotect", "NameProtect", "NameProtect", "NameProtect", "NameProtect");
        a("m.skin", "Skin (widzisz tylko ty)", "Skin (only you see it)", "Skin (nur du siehst ihn)", "Скин (видишь только ты)");
        a("m.blockesp", "Block ESP", "Block ESP", "Block ESP", "Block ESP");
        a("m.xray", "Xray", "Xray", "Xray", "Xray");
        a("m.baritone", "Baritone", "Baritone", "Baritone", "Baritone");
        a("m.antikostka", "Anty kostka", "Anti-scramble", "Anti-Durcheinander", "Анти-путаница");
        a("m.autoleave", "Auto Leave", "Auto Leave", "Auto Leave", "Авто-выход");
        // opisy
        a("d.theme", "Kolor interfejsu i HUD.", "Interface and HUD color.", "Farbe von Menü und HUD.", "Цвет меню и HUD.");
        a("d.viewmodel", "Pozycja, rozmiar i obrót miecza oraz ręki.", "Position, size and rotation of sword and hand.", "Position, Größe und Drehung von Schwert und Hand.", "Позиция, размер и поворот меча и руки.");
        a("d.night", "Wieczna noc (tylko wizualnie).", "Eternal night (visual only).", "Ewige Nacht (nur visuell).", "Вечная ночь (только визуально).");
        a("d.fullbright", "Rozjaśnia cały świat.", "Brightens the whole world.", "Hellt die ganze Welt auf.", "Освещает весь мир.");
        a("d.weather", "Deszcz albo śnieg wokół ciebie.", "Rain or snow around you.", "Regen oder Schnee um dich herum.", "Дождь или снег вокруг вас.");
        a("d.hitsound", "Własny dźwięk przy uderzeniu.", "Custom sound when you hit.", "Eigener Sound beim Treffen.", "Свой звук при ударе.");
        a("d.hud", "Znak wodny, lista modów, neonowa ramka.", "Watermark, module list, neon border.", "Wasserzeichen, Modulliste, Neonrahmen.", "Водяной знак, список модов, неоновая рамка.");
        a("d.voice", "Polski głos mówi co wybierasz (Windows).", "A voice tells you what you pick (Windows).", "Eine Stimme sagt, was du wählst (Windows).", "Голос говорит, что вы выбрали (Windows).");
        a("d.pet", "Mały zwierzak zawsze obok ciebie.", "A small pet always next to you.", "Ein kleines Haustier immer bei dir.", "Маленький питомец всегда рядом.");
        a("d.tracers", "Linie do graczy i mobów przez ściany.", "Lines to players and mobs through walls.", "Linien zu Spielern und Mobs durch Wände.", "Линии к игрокам и мобам сквозь стены.");
        a("d.nametags", "Nick, serca, set i itemy przez ściany.", "Name, hearts, armor and items through walls.", "Name, Herzen, Rüstung und Items durch Wände.", "Ник, здоровье, броня и предметы сквозь стены.");
        a("d.nameprotect", "Podmienia twój nick wszędzie.", "Replaces your name everywhere.", "Ersetzt deinen Namen überall.", "Заменяет ваш ник везде.");
        a("d.skin", "Dowolny skin po nicku, widzisz go tylko ty.", "Any skin by name, only you see it.", "Beliebiger Skin per Name, nur du siehst ihn.", "Любой скин по нику, видите только вы.");
        a("d.blockesp", "Podświetla wybrane bloki przez ściany.", "Highlights chosen blocks through walls.", "Hebt gewählte Blöcke durch Wände hervor.", "Подсвечивает выбранные блоки сквозь стены.");
        a("d.xray", "Wypełnione podświetlenie rud przez ściany.", "Filled ore highlight through walls.", "Gefüllte Erz-Markierung durch Wände.", "Заливка руд сквозь стены.");
        a("d.baritone", "Automatyczne kopanie (wymaga moda Baritone).", "Automatic mining (needs the Baritone mod).", "Automatisches Abbauen (braucht Baritone).", "Автоматическая добыча (нужен Baritone).");
        a("d.antikostka", "Przywraca ułożenie ekwipunku.", "Restores your inventory layout.", "Stellt dein Inventar wieder her.", "Восстанавливает инвентарь.");
        a("d.autoleave", "Wychodzi z serwera, gdy gracz jest blisko.", "Leaves the server when a player is near.", "Verlässt den Server, wenn ein Spieler nah ist.", "Выходит с сервера, если рядом игрок.");
        // ustawienia
        a("s.color", "Kolor", "Color", "Farbe", "Цвет");
        a("s.x", "Pozycja X", "Position X", "Position X", "Позиция X");
        a("s.y", "Pozycja Y", "Position Y", "Position Y", "Позиция Y");
        a("s.z", "Pozycja Z", "Position Z", "Position Z", "Позиция Z");
        a("s.scale", "Skala (%)", "Scale (%)", "Skalierung (%)", "Масштаб (%)");
        a("s.rx", "Obrót X", "Rotation X", "Drehung X", "Поворот X");
        a("s.ry", "Obrót Y", "Rotation Y", "Drehung Y", "Поворот Y");
        a("s.rz", "Obrót Z", "Rotation Z", "Drehung Z", "Поворот Z");
        a("s.type", "Rodzaj", "Type", "Art", "Тип");
        a("s.density", "Gęstość", "Density", "Dichte", "Плотность");
        a("s.sound", "Dźwięk", "Sound", "Sound", "Звук");
        a("s.volume", "Głośność", "Volume", "Lautstärke", "Громкость");
        a("s.target", "Cel", "Target", "Ziel", "Цель");
        a("s.watermark", "Znak wodny", "Watermark", "Wasserzeichen", "Водяной знак");
        a("s.arraylist", "Lista modów", "Module list", "Modulliste", "Список модов");
        a("s.glow", "Neonowa ramka", "Neon border", "Neonrahmen", "Неоновая рамка");
        a("s.pitch", "Ton głosu", "Voice pitch", "Stimmhöhe", "Тон голоса");
        a("s.pet", "Zwierzak", "Pet", "Haustier", "Питомец");
        a("s.pos", "Pozycja", "Position", "Position", "Позиция");
        a("s.size", "Rozmiar (%)", "Size (%)", "Größe (%)", "Размер (%)");
        a("s.dist", "Dystans", "Distance", "Entfernung", "Дистанция");
        a("s.width", "Grubość", "Thickness", "Dicke", "Толщина");
        a("s.armor", "Pokaż set", "Show armor", "Rüstung zeigen", "Показать броню");
        a("s.items", "Pokaż itemy w rękach", "Show held items", "Gehaltene Items zeigen", "Показать предметы в руках");
        a("s.hearts", "Pokaż serca", "Show hearts", "Herzen zeigen", "Показать здоровье");
        a("s.showdist", "Pokaż odległość", "Show distance", "Entfernung zeigen", "Показать дистанцию");
        a("s.nick", "Wpisz nick...", "Type a nick...", "Nick eingeben...", "Введите ник...");
        a("s.skinname", "Skin - wpisz nick i Enter...", "Skin - type name and Enter...", "Skin - Name und Enter...", "Скин - ник и Enter...");
        a("s.blocks", "Bloki", "Blocks", "Blöcke", "Блоки");
        a("s.colormode", "Tryb koloru", "Color mode", "Farbmodus", "Режим цвета");
        a("s.fill", "Wypełnienie", "Fill", "Füllung", "Заливка");
        a("s.addblock", "Dodaj blok (np. diamond_ore) i Enter...", "Add block (e.g. diamond_ore) and Enter...", "Block hinzufügen (z.B. diamond_ore) + Enter...", "Добавить блок (напр. diamond_ore) + Enter...");
        a("s.block", "Blok do kopania, np. diamond_ore", "Block to mine, e.g. diamond_ore", "Block zum Abbauen, z.B. diamond_ore", "Блок для добычи, напр. diamond_ore");
        a("s.save", "Zapisz układ EQ", "Save inventory layout", "Inventar-Layout speichern", "Сохранить раскладку");
        a("s.restore", "Przywróć EQ teraz", "Restore inventory now", "Inventar jetzt wiederherstellen", "Восстановить сейчас");
        a("s.leavedist", "Dystans gracza", "Player distance", "Spielerabstand", "Дистанция игрока");
        // opcje
        a("o.players", "Gracze", "Players", "Spieler", "Игроки");
        a("o.mobs", "Moby", "Mobs", "Mobs", "Мобы");
        a("o.both", "Gracze + Moby", "Players + Mobs", "Spieler + Mobs", "Игроки + мобы");
        a("o.rain", "Deszcz", "Rain", "Regen", "Дождь");
        a("o.snow", "Śnieg", "Snow", "Schnee", "Снег");
        a("o.parrot", "PAPUGA", "PARROT", "PAPAGEI", "ПОПУГАЙ");
        a("o.cat", "KOT", "CAT", "KATZE", "КОТ");
        a("o.dog", "PIES", "DOG", "HUND", "СОБАКА");
        a("o.panda", "PANDA", "PANDA", "PANDA", "ПАНДА");
        a("o.shoulder", "Ramię", "Shoulder", "Schulter", "Плечо");
        a("o.side", "Obok (dół)", "Beside (low)", "Daneben (unten)", "Рядом (внизу)");
        a("o.head", "Nad głową", "Above head", "Über dem Kopf", "Над головой");
        a("o.auto", "Auto (wg bloku)", "Auto (by block)", "Auto (nach Block)", "Авто (по блоку)");
        a("o.custom", "Własny", "Custom", "Eigene", "Свой");
        a("o.normal", "Normalny", "Normal", "Normal", "Обычный");
        a("o.cute", "Słodki", "Cute", "Süß", "Милый");
        a("o.high", "Wysoki", "High", "Hoch", "Высокий");
        // presety blokow
        a("p.diamond", "Diamenty", "Diamonds", "Diamanten", "Алмазы");
        a("p.netherite", "Netherite", "Netherite", "Netherit", "Незерит");
        a("p.gold", "Złoto", "Gold", "Gold", "Золото");
        a("p.iron", "Żelazo", "Iron", "Eisen", "Железо");
        a("p.emerald", "Szmaragdy", "Emeralds", "Smaragde", "Изумруды");
        a("p.ores", "Wszystkie rudy", "All ores", "Alle Erze", "Все руды");
        a("p.clear", "Wyczyść", "Clear", "Leeren", "Очистить");
        // info
        a("i.np", "Wpisz dowolny nick, a zastąpi twój prawdziwy nick na czacie, w TAB, na scoreboardzie i nad głową. Działa tylko u ciebie.",
                "Type any nick and it replaces your real name in chat, TAB, scoreboard and above your head. Only you see it.",
                "Gib einen beliebigen Nick ein, er ersetzt deinen Namen im Chat, TAB, Scoreboard und über dem Kopf. Nur du siehst es.",
                "Введите любой ник, он заменит ваш настоящий в чате, TAB, скорборде и над головой. Видите только вы.");
        a("i.skin", "Wpisz nick gracza i wciśnij Enter, a potem włącz moda. Skin widzisz tylko ty. Bez limitu skinów.",
                "Type a player name, press Enter, then enable the mod. Only you see the skin. No limit.",
                "Spielername eingeben, Enter drücken, dann den Mod aktivieren. Nur du siehst den Skin.",
                "Введите ник игрока, нажмите Enter и включите мод. Скин видите только вы.");
        a("i.xray", "To podświetlenie przez ściany (wypełnione). Prawdziwe znikające bloki wymagają osobnego moda renderera.",
                "This is a filled see-through highlight. True disappearing blocks need a separate renderer mod.",
                "Gefüllte Markierung durch Wände. Echtes Xray braucht einen separaten Renderer-Mod.",
                "Это заливка сквозь стены. Настоящий xray требует отдельного мода рендера.");
        a("i.baritone", "Wymaga zainstalowanego moda Baritone. Włącz przełącznik: kopie wpisany blok (#mine), wyłącz: #stop. Na czacie możesz też: #baritone diamond_ore on / off.",
                "Needs the Baritone mod. Switch on: mines the block (#mine), switch off: #stop. In chat you can also type: #baritone diamond_ore on / off.",
                "Braucht den Baritone-Mod. Einschalten: baut den Block ab (#mine), ausschalten: #stop. Im Chat: #baritone diamond_ore on / off.",
                "Нужен мод Baritone. Включить: добывает блок (#mine), выключить: #stop. В чате: #baritone diamond_ore on / off.");
        a("i.anti", "Zapisz układ ekwipunku. Gdy się pomiesza, wciśnij klawisz J (zmienisz go w Sterowaniu).",
                "Save your inventory layout. If it gets scrambled, press J (rebind in Controls).",
                "Speichere dein Inventar. Wenn es durcheinander ist, drücke J (in Steuerung änderbar).",
                "Сохраните раскладку. Если всё перепуталось, нажмите J (меняется в управлении).");
        a("i.leave", "Gdy gracz zbliży się na wybrany dystans, automatycznie wychodzisz z serwera. Dotyczy tylko graczy, nie mobów.",
                "When a player gets within the distance you leave the server automatically. Players only, not mobs.",
                "Kommt ein Spieler in die Nähe, verlässt du automatisch den Server. Nur Spieler, keine Mobs.",
                "Если игрок подойдёт ближе, вы автоматически выйдете с сервера. Только игроки, не мобы.");
        a("i.help", "N - menu | J - przywróć EQ | Kółko myszy - przewijanie | Klik w kartę - włącz/wyłącz | + / - rozwija ustawienia.",
                "N - menu | J - restore inventory | Mouse wheel - scroll | Click a card - on/off | + / - expands settings.",
                "N - Menü | J - Inventar | Mausrad - scrollen | Klick auf Karte - an/aus | + / - Einstellungen.",
                "N - меню | J - инвентарь | Колесо - прокрутка | Клик по карте - вкл/выкл | + / - настройки.");
        // glos i menu
        a("v.sel", "Wybrałeś moda", "You selected the mod", "Du hast den Mod gewählt", "Вы выбрали мод");
        a("v.on", "włączony", "enabled", "aktiviert", "включён");
        a("v.off", "wyłączony", "disabled", "deaktiviert", "выключен");
        a("v.welcome", "Witaj w Zolte Niebiosa klient! Wybierz moda.", "Welcome to Zolte Niebiosa client! Pick a mod.", "Willkommen im Zolte Niebiosa Client! Wähle einen Mod.", "Добро пожаловать в Zolte Niebiosa клиент! Выберите мод.");
        a("v.lang", "Wybrano język polski.", "Language set to English.", "Sprache auf Deutsch gestellt.", "Выбран русский язык.");
        a("menu.lang", "Język / Language", "Language / Język", "Sprache / Language", "Язык / Language");
        a("menu.hint", "Kółko myszy - przewijanie  |  N - zamknij", "Mouse wheel - scroll  |  N - close", "Mausrad - scrollen  |  N - schließen", "Колесо - прокрутка  |  N - закрыть");
        a("menu.author", "Autor", "Author", "Autor", "Автор");
    }
}

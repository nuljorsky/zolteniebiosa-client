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

        // ===== etap 3 =====
        a("m.skin", "Skin po nicku (widzą go znajomi z ZTN)", "Skin by name (ZTN friends see it)", "Skin per Name (ZTN-Freunde sehen ihn)", "Скин по нику (видят друзья с ZTN)");
        a("d.skin", "Wpisz nick premium - dostajesz jego skina. Widzą go też gracze z ZTN CLIENT.", "Type a premium name to get its skin. ZTN CLIENT players see it too.", "Premium-Name eingeben, um den Skin zu bekommen. ZTN-Spieler sehen ihn auch.", "Введите ник - получите его скин. Игроки с ZTN CLIENT тоже его видят.");
        a("m.crosshair", "Celownik PvP", "PvP crosshair", "PvP-Fadenkreuz", "Прицел для PvP");
        a("m.cape", "Pelerynka ZTN", "ZTN cape", "ZTN-Umhang", "Плащ ZTN");
        a("m.keystrokes", "Keystrokes + CPS", "Keystrokes + CPS", "Keystrokes + CPS", "Клавиши + CPS");
        a("m.armorhud", "Status pancerza", "Armor status", "Rüstungsstatus", "Состояние брони");
        a("m.sprint", "Auto sprint", "Toggle sprint", "Auto-Sprint", "Авто-спринт");
        a("m.zoom", "Zoom", "Zoom", "Zoom", "Зум");
        a("m.music", "Muzyka do PvP", "PvP music", "PvP-Musik", "Музыка для PvP");
        a("m.discord", "Discord (status w grze)", "Discord (in-game status)", "Discord (Spielstatus)", "Discord (статус в игре)");
        a("m.ztnsync", "Synchronizacja ZTN (skiny i peleryny)", "ZTN sync (skins and capes)", "ZTN-Sync (Skins und Umhänge)", "Синхронизация ZTN (скины и плащи)");
        a("d.crosshair", "5 własnych celowników - bez paczki tekstur.", "5 custom crosshairs - no texture pack needed.", "5 eigene Fadenkreuze - ohne Texturpaket.", "5 своих прицелов - без текстур-пака.");
        a("d.cape", "Wybierz pelerynkę. Widzą ją tylko gracze z ZTN CLIENT.", "Pick a cape. Only ZTN CLIENT players see it.", "Wähle einen Umhang. Nur ZTN-Spieler sehen ihn.", "Выберите плащ. Видят только игроки с ZTN CLIENT.");
        a("d.keystrokes", "WASD, myszka i liczba kliknięć na sekundę.", "WASD, mouse and clicks per second.", "WASD, Maus und Klicks pro Sekunde.", "WASD, мышь и клики в секунду.");
        a("d.armorhud", "Pancerz z wytrzymałością na ekranie.", "Armor with durability on screen.", "Rüstung mit Haltbarkeit auf dem Bildschirm.", "Броня с прочностью на экране.");
        a("d.sprint", "Sam biegniesz, gdy idziesz do przodu.", "You sprint automatically when moving forward.", "Du sprintest automatisch beim Vorwärtsgehen.", "Вы бежите автоматически при ходьбе вперёд.");
        a("d.zoom", "Przytrzymaj klawisz C, żeby przybliżyć.", "Hold the C key to zoom in.", "Halte die Taste C zum Zoomen.", "Удерживайте C для приближения.");
        a("d.music", "Wyszukaj piosenkę i słuchaj w grze - bez przeglądarki.", "Search a song and listen in-game - no browser.", "Song suchen und im Spiel hören - ohne Browser.", "Ищите песню и слушайте в игре - без браузера.");
        a("d.discord", "Znajomi widzą na Discordzie, że grasz w ZolteNiebiosa CLIENT.", "Friends see on Discord that you play ZolteNiebiosa CLIENT.", "Freunde sehen auf Discord, dass du ZolteNiebiosa CLIENT spielst.", "Друзья видят в Discord, что вы играете в ZolteNiebiosa CLIENT.");
        a("d.ztnsync", "Dzięki temu gracze z ZTN CLIENT widzą Twój skin i pelerynkę.", "Lets ZTN CLIENT players see your skin and cape.", "Damit sehen ZTN-Spieler deinen Skin und Umhang.", "Так игроки с ZTN CLIENT видят ваш скин и плащ.");
        a("s.opacity", "Przezroczystość menu (%)", "Menu opacity (%)", "Menü-Deckkraft (%)", "Непрозрачность меню (%)");
        a("s.bootanim", "Animacja ZTN przy starcie gry", "ZTN animation at game start", "ZTN-Animation beim Spielstart", "Анимация ZTN при запуске");
        a("s.style", "Celownik", "Crosshair", "Fadenkreuz", "Прицел");
        a("s.size2", "Rozmiar", "Size", "Größe", "Размер");
        a("s.thick", "Grubość", "Thickness", "Dicke", "Толщина");
        a("s.gap", "Przerwa", "Gap", "Lücke", "Зазор");
        a("s.outline", "Czarny kontur", "Black outline", "Schwarze Kontur", "Чёрный контур");
        a("s.cape", "Pelerynka", "Cape", "Umhang", "Плащ");
        a("s.cps", "Pokaż CPS", "Show CPS", "CPS anzeigen", "Показать CPS");
        a("s.zoomf", "Siła zoomu", "Zoom strength", "Zoom-Stärke", "Сила зума");
        a("s.autoplay", "Następny utwór automatycznie", "Autoplay next track", "Nächsten Titel automatisch", "Следующий трек автоматически");
        a("s.shownow", "Pokaż co gra na ekranie", "Show now playing on screen", "Aktuellen Titel anzeigen", "Показывать трек на экране");
        a("s.appid", "Application ID z discord.com/developers", "Application ID from discord.com/developers", "Application ID von discord.com/developers", "Application ID с discord.com/developers");
        a("s.showserver", "Pokaż znajomym na jakim serwerze grasz", "Show friends which server you play on", "Freunden den Server zeigen", "Показывать друзьям сервер");
        a("s.reconnect", "Połącz z Discordem ponownie", "Reconnect to Discord", "Mit Discord neu verbinden", "Переподключиться к Discord");
        a("s.createapp", "Otwórz discord.com/developers (załóż Application ID)", "Open discord.com/developers (create Application ID)", "discord.com/developers öffnen (Application ID erstellen)", "Открыть discord.com/developers (создать Application ID)");
        a("s.syncurl", "Adres serwera ZTN Sync (https://...)", "ZTN Sync server URL (https://...)", "ZTN-Sync-Server-URL (https://...)", "Адрес сервера ZTN Sync (https://...)");
        a("o.cr0", "Minecraft", "Minecraft", "Minecraft", "Minecraft");
        a("o.cr1", "Kropka", "Dot", "Punkt", "Точка");
        a("o.cr2", "Krzyż PvP", "PvP cross", "PvP-Kreuz", "Крест PvP");
        a("o.cr3", "Okrąg", "Ring", "Ring", "Круг");
        a("o.cr4", "Narożniki", "Corners", "Ecken", "Углы");
        a("o.cr5", "Krzyż X", "X cross", "X-Kreuz", "Крест X");
        a("i.skin", "Wpisz nick gracza premium i wciśnij Enter, potem włącz moda. Ty i gracze z ZTN CLIENT widzicie ten skin. Reszta serwera widzi normalny.",
                "Type a premium player name and press Enter, then enable the mod. You and ZTN CLIENT players see this skin. Everyone else sees the normal one.",
                "Premium-Namen eingeben, Enter drücken, Mod aktivieren. Du und ZTN-Spieler seht den Skin. Alle anderen sehen den normalen.",
                "Введите ник, нажмите Enter и включите мод. Этот скин видите вы и игроки с ZTN CLIENT. Остальные видят обычный.");
        a("i.cape", "Pelerynkę widzisz Ty i gracze z ZTN CLIENT, którzy są obok Ciebie. Wymaga ustawionego serwera ZTN Sync (zakładka DISCORD).",
                "You and nearby ZTN CLIENT players see the cape. Needs the ZTN Sync server set (DISCORD tab).",
                "Du und ZTN-Spieler in deiner Nähe sehen den Umhang. Braucht den ZTN-Sync-Server (Tab DISCORD).",
                "Плащ видите вы и игроки с ZTN CLIENT рядом. Нужен сервер ZTN Sync (вкладка DISCORD).");
        a("i.zoom", "Przytrzymaj C (zmienisz w Sterowaniu).", "Hold C (rebind in Controls).", "C halten (in Steuerung änderbar).", "Удерживайте C (меняется в управлении).");
                a("i.discord", "1) Kliknij przycisk wyżej i załóż darmową aplikację (nazwij ją np. ZolteNiebiosa CLIENT). 2) Skopiuj jej Application ID. 3) Wklej tu i naciśnij Enter. 4) Upewnij się, że Discord jest otwarty na TYM komputerze. Status wyżej pokaże czy się połączyło.",
                "1) Click the button above and create a free app (name it e.g. ZolteNiebiosa CLIENT). 2) Copy its Application ID. 3) Paste it here and press Enter. 4) Make sure Discord is open on THIS computer. The status above shows if it connected.",
                "1) Klicke oben auf den Button und erstelle eine kostenlose App (z.B. ZolteNiebiosa CLIENT). 2) Kopiere die Application ID. 3) Hier einfügen und Enter drücken. 4) Discord muss auf DIESEM PC offen sein. Der Status oben zeigt den Verbindungsstatus.",
                "1) Нажмите кнопку выше и создайте бесплатное приложение (назовите, например, ZolteNiebiosa CLIENT). 2) Скопируйте Application ID. 3) Вставьте сюда и нажмите Enter. 4) Discord должен быть открыт на ЭТОМ компьютере. Статус выше покажет подключение.");
        a("i.sync", "Mały serwer z folderu backend/. Bez niego skiny i peleryny widzisz tylko Ty. Wpisz tu jego adres, albo wpisz go na stałe w Net.java.",
                "A tiny server from the backend/ folder. Without it only you see your skin and cape. Enter its address here or hardcode it in Net.java.",
                "Ein kleiner Server aus dem Ordner backend/. Ohne ihn siehst nur du Skin und Umhang. Adresse hier eintragen oder in Net.java.",
                "Маленький сервер из папки backend/. Без него скин и плащ видите только вы. Введите адрес здесь или в Net.java.");
        a("n.off", "Wyłączone", "Off", "Aus", "Выключено");
        a("n.nourl", "Brak adresu serwera ZTN Sync", "No ZTN Sync server address", "Keine ZTN-Sync-Adresse", "Нет адреса ZTN Sync");
        a("n.wait", "Łączenie...", "Connecting...", "Verbinde...", "Подключение...");
        a("n.ok", "Połączono z serwerem ZTN Sync", "Connected to ZTN Sync", "Mit ZTN Sync verbunden", "Подключено к ZTN Sync");
        a("n.fail", "Serwer ZTN Sync nie odpowiada - ponawiam", "ZTN Sync not responding - retrying", "ZTN Sync antwortet nicht", "ZTN Sync не отвечает");
        a("mu.label", "Wyszukaj swojej piosenki do pvp byqu POZDRO!", "Search your PvP song, byqu CHEERS!", "Suche deinen PvP-Song, byqu GRÜSSE!", "Найди свою песню для PvP, byqu ПРИВЕТ!");
        a("mu.hint", "np. Malik Montana + Enter", "e.g. Malik Montana + Enter", "z.B. Malik Montana + Enter", "напр. Malik Montana + Enter");
        a("mu.searching", "Szukam...", "Searching...", "Suche...", "Поиск...");
        a("mu.none", "Nic nie znaleziono", "Nothing found", "Nichts gefunden", "Ничего не найдено");
        a("mu.err", "Błąd - sprawdź internet", "Error - check your internet", "Fehler - Internet prüfen", "Ошибка - проверьте интернет");
        a("mu.nolocal", "Brak plików MP3 w folderze:", "No MP3 files in folder:", "Keine MP3-Dateien im Ordner:", "Нет MP3 в папке:");
        a("mu.local", "Moje pliki MP3", "My MP3 files", "Meine MP3-Dateien", "Мои MP3-файлы");
        a("mu.stop", "Stop", "Stop", "Stopp", "Стоп");
        a("mu.next", "Następny", "Next", "Weiter", "Далее");
        a("mu.now", "Teraz gra:", "Now playing:", "Spielt jetzt:", "Сейчас играет:");
        a("mu.info", "Wyniki to 30-sekundowe fragmenty (Deezer), po nich leci następny utwór. Pełne piosenki: wrzuć własne .mp3 do config/zolteniebiosa/music i kliknij \"Moje pliki MP3\".",
                "Results are 30-second previews (Deezer), then the next track plays. Full songs: put your own .mp3 files in config/zolteniebiosa/music and click \"My MP3 files\".",
                "Ergebnisse sind 30-Sekunden-Vorschauen (Deezer), dann der nächste Titel. Volle Songs: eigene .mp3 in config/zolteniebiosa/music legen.",
                "Результаты - 30-секундные отрывки (Deezer), затем следующий трек. Полные песни: свои .mp3 в config/zolteniebiosa/music.");
        a("i.help", "N - menu | J - przywróć EQ | C - zoom | Kółko myszy - przewijanie | Klik w kartę - włącz/wyłącz | + / - rozwija ustawienia.",
                "N - menu | J - restore inventory | C - zoom | Mouse wheel - scroll | Click a card - on/off | + / - expands settings.",
                "N - Menü | J - Inventar | C - Zoom | Mausrad - scrollen | Klick auf Karte - an/aus | + / - Einstellungen.",
                "N - меню | J - инвентарь | C - зум | Колесо - прокрутка | Клик по карте - вкл/выкл | + / - настройки.");
        a("d.baritone", "Automatyczne kopanie - Baritone jest wbudowany (wersja z GitHub Actions).", "Automatic mining - Baritone is built in (GitHub Actions build).", "Automatisches Abbauen - Baritone ist eingebaut (GitHub-Actions-Build).", "Автоматическая добыча - Baritone встроен (сборка GitHub Actions).");
        a("i.baritone", "Włącz przełącznik: kopie wpisany blok (#mine), wyłącz: #stop. Na czacie możesz też: #baritone diamond_ore on / off. Nie musisz instalować osobnego moda Baritone.",
                "Switch on: mines the block (#mine), off: #stop. In chat you can also type: #baritone diamond_ore on / off. No separate Baritone mod needed.",
                "Einschalten: baut den Block ab (#mine), aus: #stop. Im Chat: #baritone diamond_ore on / off. Kein extra Baritone-Mod nötig.",
                "Включить: добывает блок (#mine), выключить: #stop. В чате: #baritone diamond_ore on / off. Отдельный мод Baritone не нужен.");

        // ===== etap 5: auto rynek, ulatwienia, mody pvp =====
        a("m.automarket", "Auto Rynek", "Auto Market", "Auto-Markt", "Авто-рынок");
        a("d.automarket", "Wystawia trzymany przedmiot na rynku jedną komendą.", "Lists the item in your hand on the market with one command.", "Listet den gehaltenen Gegenstand mit einem Befehl auf dem Markt.", "Выставляет предмет в руке на рынок одной командой.");
        a("s.am.open", "Komenda otwierająca rynek (np. ah)", "Command that opens the market (e.g. ah)", "Befehl zum Öffnen des Marktes (z.B. ah)", "Команда открытия рынка (напр. ah)");
        a("s.am.list", "Komenda wystawiająca ({cena} = cena, {ilosc} = ilość)", "Listing command ({cena} = price, {ilosc} = amount)", "Befehl zum Auflisten ({cena} = Preis, {ilosc} = Menge)", "Команда выставления ({cena} = цена, {ilosc} = количество)");
        a("s.am.fallback", "Cena bazowa (musisz ją znać - bot nie czyta cen z GUI)", "Base price (you must know it - the bot doesn't read GUI prices)", "Basispreis (musst du kennen - Bot liest keine GUI-Preise)", "Базовая цена (нужно знать самому - бот не читает GUI)");
        a("s.am.mode", "Tryb obniżki", "Discount mode", "Rabatt-Modus", "Режим скидки");
        a("s.am.amount", "O ile obniżyć cenę", "How much to lower the price", "Um wie viel senken", "Насколько снизить цену");
        a("s.am.perunit", "Cena podana jest za sztukę (pomnóż przez ilość)", "Price is per item (multiply by amount)", "Preis pro Stück (mit Menge multiplizieren)", "Цена за штуку (умножить на количество)");
        a("s.am.floor", "Minimalna cena (nigdy nie zejdzie niżej)", "Minimum price (never goes lower)", "Mindestpreis (nie niedriger)", "Минимальная цена (никогда не ниже)");
        a("s.am.start", "Wystaw trzymany przedmiot TERAZ", "List held item NOW", "Gehaltenen Gegenstand JETZT auflisten", "Выставить предмет в руке СЕЙЧАС");
        a("o.pct", "Procent (%)", "Percent (%)", "Prozent (%)", "Процент (%)");
        a("o.flat", "Stała kwota", "Flat amount", "Fester Betrag", "Фиксированная сумма");
        a("i.automarket", "To NIE skanuje cen innych graczy - każdy serwer ma inny GUI rynku, więc nie da się tego zrobić uniwersalnie. Ty wpisujesz Cenę bazową (np. ile zwykle kosztuje dany przedmiot) - bot obniży ją o ustawiony % albo kwotę i wystawi. Koniecznie dopasuj komendę listcmd do SWOJEGO serwera - sprawdź jak wygląda u Ciebie wystawianie (np. /ah sell 100, /rynek wystaw 100) i wpisz to z {cena} w miejscu liczby.",
                "This does NOT scan other players' prices - every server has a different market GUI, so it can't be done universally. You type the Base price (what the item usually sells for) - the bot lowers it by the set % or amount and lists it. Make sure to match the listcmd command to YOUR server - check how listing works there (e.g. /ah sell 100, /market list 100) and type it with {cena} instead of the number.",
                "Dies scannt KEINE Preise anderer Spieler - jeder Server hat eine andere Markt-GUI. Gib den Basispreis ein - der Bot senkt ihn um den eingestellten %/Betrag. Passe den listcmd-Befehl an DEINEN Server an.",
                "Это НЕ сканирует цены других игроков - у каждого сервера свой GUI рынка. Введите базовую цену - бот снизит её на заданный % и выставит. Настройте команду listcmd под СВОЙ сервер.");
        a("am.idle", "Gotowy - kliknij \"Wystaw TERAZ\"", "Ready - click \"List NOW\"", "Bereit - klicke \"Jetzt auflisten\"", "Готово - нажмите \"Выставить СЕЙЧАС\"");
        a("am.wait", "Czekaj chwilę (ostatnie wystawienie było niedawno)", "Wait a moment (last listing was recent)", "Warte kurz (letztes Listing war kürzlich)", "Подождите (недавно уже выставляли)");
        a("am.empty", "Nic nie trzymasz w ręce", "You're not holding anything", "Du hältst nichts in der Hand", "Вы ничего не держите в руке");
        a("am.nofallback", "Wpisz Cenę bazową w ustawieniach", "Type a Base price in the settings", "Gib einen Basispreis in den Einstellungen ein", "Введите базовую цену в настройках");
        a("am.listed", "Wystawiono:", "Listed:", "Aufgelistet:", "Выставлено:");

        a("m.nopush", "NoPush - łatwiejsze perły", "NoPush - easier pearls", "NoPush - einfachere Perlen", "NoPush - лёгкие жемчужины");
        a("d.nopush", "Nic Cię nie odpycha - łatwiej cykać perły w trapie i wychodzić z ciasnych miejsc.", "Nothing pushes you - easier to pearl-cycle in traps and escape tight spots.", "Nichts stößt dich ab - leichter in Fallen und engen Stellen.", "Вас ничто не отталкивает - легче в трапах и тесных местах.");
        a("s.np.players", "Nie odpychają gracze/moby", "Players/mobs don't push you", "Spieler/Mobs stoßen nicht", "Игроки/мобы не толкают");
        a("s.np.blocks", "Nie wypycha z bloków", "Blocks don't push you out", "Blöcke stoßen nicht raus", "Блоки не выталкивают");
        a("s.np.water", "Woda/lawa nie znoszą", "Water/lava don't carry you", "Wasser/Lava tragen nicht weg", "Вода/лава не сносят");
        a("i.nopush", "Działa cały czas gdy włączone. Najbardziej przydatne w trapach z perłami i przy chowaniu się w ciasnych szczelinach.", "Works all the time when enabled. Most useful in pearl traps and hiding in tight gaps.", "Funktioniert immer wenn aktiviert. Am nützlichsten in Perlenfallen und engen Spalten.", "Работает постоянно. Полезно в жемчужных трапах и узких щелях.");

        a("m.targethud", "Target HUD", "Target HUD", "Ziel-HUD", "HUD цели");
        a("d.targethud", "Pokazuje na ekranie gracza, w którego aktualnie celujesz, i jego HP.", "Shows the player you're aiming at and their HP on screen.", "Zeigt den anvisierten Spieler und sein HP an.", "Показывает цель прицеливания и её HP.");
        a("m.combo", "Combo counter", "Combo counter", "Combo-Zähler", "Счётчик комбо");
        a("d.combo", "Liczy trafienia pod rząd w jednego gracza - jak w sieciach PvP.", "Counts consecutive hits on one player - like PvP networks.", "Zählt aufeinanderfolgende Treffer - wie auf PvP-Servern.", "Считает подряд идущие удары - как на PvP-сетях.");
        a("s.reach", "Pokaż też dystans do celu", "Also show distance to target", "Zeige auch Distanz zum Ziel", "Показывать дистанцию до цели");
        a("m.dmgpop", "Damage popup", "Damage popup", "Schaden-Popup", "Всплывающий урон");
        a("d.dmgpop", "Liczby obrażeń wyskakują nad trafionym graczem.", "Damage numbers pop up over the hit player.", "Schadenszahlen erscheinen über dem getroffenen Spieler.", "Числа урона всплывают над игроком.");
        a("m.lowhp", "Alarm niskiego HP", "Low HP alert", "Niedrig-HP-Alarm", "Сигнал низкого HP");
        a("d.lowhp", "Czerwona poświata na ekranie, gdy masz mało zdrowia - nie przeoczysz śmierci.", "Red screen glow when your HP is low - never miss it.", "Roter Bildschirm-Glow bei wenig HP - übersiehst du nie.", "Красное свечение экрана при малом HP.");
        a("s.lowhp", "Próg HP (serduszka)", "HP threshold (hearts)", "HP-Schwelle (Herzen)", "Порог HP (сердца)");
        a("m.compass", "Kompas PvP", "PvP compass", "PvP-Kompass", "PvP компас");
        a("d.compass", "Strzałka na ekranie wskazuje najbliższego gracza.", "An on-screen arrow points to the nearest player.", "Ein Bildschirmpfeil zeigt zum nächsten Spieler.", "Стрелка на экране указывает на ближайшего игрока.");
        a("s.speed", "Pokaż prędkość gracza (bloki/s)", "Show player speed (blocks/s)", "Zeige Spielergeschwindigkeit (Blöcke/s)", "Показывать скорость игрока (блоков/с)");

        a("s.blur", "Rozmyte tło za menu", "Blurred background behind menu", "Unscharfer Hintergrund hinter Menü", "Размытый фон за меню");
        a("s.parallax", "Tło reaguje na ruch myszki", "Background reacts to mouse movement", "Hintergrund reagiert auf Mausbewegung", "Фон реагирует на мышь");
        a("s.titlescreen", "Motyw na ekranie głównym (snieg itp.)", "Theme on the main menu (snow etc.)", "Motiv im Hauptmenü (Schnee usw.)", "Тема в главном меню (снег и т.д.)");
        a("s.frost", "Szron na krawędziach ekranu", "Frost on screen edges", "Frost an Bildschirmrändern", "Иней по краям экрана");
        a("s.lightning", "Błyskawice podczas burzy", "Lightning flashes during storms", "Blitze bei Gewitter", "Молнии во время грозы");
        a("o.vg", "Głos męski", "Male voice", "Männliche Stimme", "Мужской голос");
        a("o.vw", "Głos żeński", "Female voice", "Weibliche Stimme", "Женский голос");
    }
}

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
        // etap 3
        a("m.crosshair", "Celownik PvP", "PvP crosshair", "PvP-Fadenkreuz", "Прицел для PvP");
        a("d.crosshair", "5 własnych celowników, bez resource packa.", "5 built-in crosshairs, no resource pack.", "5 eigene Fadenkreuze, ohne Resource Pack.", "5 прицелов без ресурспака.");
        a("m.cape", "Pelerynka (widzą ją gracze z klientem)", "Cape (seen by client users)", "Umhang (für Client-Nutzer sichtbar)", "Плащ (видят игроки с клиентом)");
        a("d.cape", "Wybierz pelerynkę. Widzą ją tylko osoby z tym klientem.", "Pick a cape. Only people using this client see it.", "Wähle einen Umhang. Nur Nutzer dieses Clients sehen ihn.", "Выберите плащ. Видят только игроки с этим клиентом.");
        a("i.cape", "Pelerynka i skin są widoczne dla innych graczy z ZolteNiebiosa CLIENT, gdy są w pobliżu (włącz też moduł Widoczność dla znajomych).", "Cape and skin are visible to other ZolteNiebiosa CLIENT users nearby (also enable the Friends visibility module).", "Umhang und Skin sind für andere ZolteNiebiosa-CLIENT-Nutzer in der Nähe sichtbar (auch das Freunde-Modul aktivieren).", "Плащ и скин видят другие игроки с ZolteNiebiosa CLIENT рядом (включите также модуль видимости).");
        a("m.zoom", "Zoom (klawisz C)", "Zoom (key C)", "Zoom (Taste C)", "Зум (клавиша C)");
        a("d.zoom", "Przybliżenie na przytrzymanie klawisza C.", "Hold C to zoom in.", "C gedrückt halten zum Zoomen.", "Удерживайте C для приближения.");
        a("i.zoom", "Przytrzymaj klawisz C w grze. Klawisz zmienisz w Sterowaniu.", "Hold C in game. Rebind it in Controls.", "C im Spiel gedrückt halten. In Steuerung änderbar.", "Удерживайте C в игре. Меняется в управлении.");
        a("m.discord", "Discord (status gry)", "Discord (game status)", "Discord (Spielstatus)", "Discord (статус игры)");
        a("d.discord", "Znajomi widzą na Discordzie, że grasz w ZolteNiebiosa CLIENT.", "Friends see you playing ZolteNiebiosa CLIENT on Discord.", "Freunde sehen auf Discord, dass du ZolteNiebiosa CLIENT spielst.", "Друзья видят в Discord, что вы играете в ZolteNiebiosa CLIENT.");
        a("i.discord", "1) Wejdź na discord.com/developers/applications i kliknij New Application. 2) Nazwij ją ZolteNiebiosa CLIENT. 3) Skopiuj Application ID i wklej tutaj, potem Enter. 4) Włącz moduł. Discord musi być uruchomiony na komputerze (Windows).", "1) Open discord.com/developers/applications and click New Application. 2) Name it ZolteNiebiosa CLIENT. 3) Copy the Application ID and paste it here, then Enter. 4) Enable the module. Discord must be running (Windows).", "1) Öffne discord.com/developers/applications, New Application. 2) Nenne sie ZolteNiebiosa CLIENT. 3) Application ID kopieren, hier einfügen, Enter. 4) Modul aktivieren. Discord muss laufen (Windows).", "1) Откройте discord.com/developers/applications, New Application. 2) Назовите ZolteNiebiosa CLIENT. 3) Скопируйте Application ID, вставьте сюда, Enter. 4) Включите модуль. Discord должен быть запущен (Windows).");
        a("m.sync", "Widoczność dla znajomych z klientem", "Friends with client", "Freunde mit Client", "Видимость для друзей с клиентом");
        a("d.sync", "Wysyła twój skin i pelerynkę innym graczom z klientem.", "Sends your skin and cape to other client users.", "Sendet deinen Skin und Umhang an andere Client-Nutzer.", "Отправляет ваш скин и плащ другим игрокам с клиентом.");
        a("i.sync", "Wymiana działa przez publiczny przekaźnik ntfy.sh (bez własnego serwera). Wysyłany jest tylko twój UUID, nick skina i numer pelerynki. Odświeża się co ok. 20 sekund.", "Sync uses the public ntfy.sh relay (no own server). Only your UUID, skin name and cape number are sent. Refreshes about every 20 seconds.", "Der Abgleich läuft über das öffentliche ntfy.sh (ohne eigenen Server). Gesendet werden nur UUID, Skin-Name und Umhang-Nummer. Aktualisierung alle ca. 20 Sekunden.", "Обмен идёт через публичный ntfy.sh (без своего сервера). Отправляются только UUID, ник скина и номер плаща. Обновление раз в ~20 секунд.");
        a("m.nopush", "NoPush", "NoPush", "NoPush", "NoPush");
        a("d.nopush", "Nie popchną cię gracze, woda ani bloki (przydatne w trapach).", "Players, water and blocks can't push you (useful in traps).", "Spieler, Wasser und Blöcke schieben dich nicht (nützlich in Traps).", "Игроки, вода и блоки вас не толкают (полезно в ловушках).");
        a("i.nopush", "Działa po stronie klienta. Serwer nadal może cię przesunąć pakietami ruchu.", "Client-side only. A server can still move you with movement packets.", "Nur clientseitig. Ein Server kann dich weiterhin per Bewegungspaketen bewegen.", "Только на стороне клиента. Сервер всё ещё может двигать вас пакетами.");
        a("m.autosprint", "AutoSprint", "AutoSprint", "AutoSprint", "АвтоСпринт");
        a("d.autosprint", "Biegniesz bez trzymania klawisza sprintu.", "Sprint without holding the sprint key.", "Sprinten ohne Sprint-Taste.", "Бег без удержания клавиши спринта.");
        a("m.music", "Odtwarzacz muzyki", "Music player", "Musikplayer", "Музыкальный плеер");
        a("d.music", "Muzyka w launcherze, bez przeglądarki.", "Music inside the client, no browser.", "Musik im Client, ohne Browser.", "Музыка в клиенте, без браузера.");
        a("d.baritone", "Wbudowany kopacz - nie potrzebujesz moda Baritone.", "Built-in miner - you don't need the Baritone mod.", "Eingebauter Miner - kein Baritone-Mod nötig.", "Встроенный копатель - мод Baritone не нужен.");
        a("i.baritone", "Wpisz blok (np. diamond_ore, można kilka po przecinku), potem włącz przełącznik. Kopacz szuka bloku w zasięgu i drąży do niego tunel. Trzymaj kilof w ręce. Wyłączysz go przełącznikiem albo na czacie: #baritone off. To uproszczona wersja, nie prawdziwy pathfinding.", "Type a block (e.g. diamond_ore, several with commas), then switch it on. The miner looks for it in range and digs a tunnel to it. Hold a pickaxe. Switch it off or type #baritone off in chat. Simplified version, not real pathfinding.", "Block eingeben (z.B. diamond_ore, mehrere mit Komma), dann einschalten. Der Miner sucht ihn in Reichweite und gräbt einen Tunnel. Spitzhacke halten. Ausschalten oder #baritone off im Chat. Vereinfachte Version, kein echtes Pathfinding.", "Введите блок (напр. diamond_ore, несколько через запятую) и включите. Копатель ищет его рядом и роет туннель. Держите кирку. Выключить: переключатель или #baritone off в чате. Упрощённая версия, не настоящий pathfinding.");
        a("s.style", "Styl", "Style", "Stil", "Стиль");
        a("s.size2", "Rozmiar", "Size", "Größe", "Размер");
        a("s.thick", "Grubość", "Thickness", "Dicke", "Толщина");
        a("s.gap", "Przerwa", "Gap", "Lücke", "Зазор");
        a("s.dot", "Kropka w środku", "Center dot", "Mittelpunkt", "Точка в центре");
        a("s.cape", "Pelerynka", "Cape", "Umhang", "Плащ");
        a("s.appid", "Wklej Application ID z Discorda...", "Paste Discord Application ID...", "Discord Application ID einfügen...", "Вставьте Application ID из Discord...");
        a("s.showserver", "Pokaż adres serwera znajomym", "Show server address to friends", "Serveradresse Freunden zeigen", "Показывать адрес сервера друзьям");
        a("s.fov", "FOV zoomu", "Zoom FOV", "Zoom-FOV", "FOV зума");
        a("s.opacity", "Przezroczystość menu", "Menu opacity", "Menü-Deckkraft", "Прозрачность меню");
        a("s.bganim", "Animowane tło menu", "Animated menu background", "Animierter Menühintergrund", "Анимированный фон меню");
        a("s.radius", "Zasięg szukania", "Search radius", "Suchradius", "Радиус поиска");
        a("s.safe", "Tryb bezpieczny (lawa, przepaść)", "Safe mode (lava, cliffs)", "Sicherer Modus (Lava, Klippen)", "Безопасный режим (лава, обрывы)");
        a("s.query", "Wyszukaj swojej piosenki do pvp byqu POZDRO!", "Search your PvP song, bro!", "Suche deinen PvP-Song!", "Найди свою песню для PvP!");
        a("mu.hint", "np. Malik Montana + Enter", "e.g. Malik Montana + Enter", "z.B. Malik Montana + Enter", "напр. Malik Montana + Enter");
        a("mu.note", "Odtwarzane są 30-sekundowe podglądy utworów (iTunes), kolejne utwory grają po kolei. Tylko Windows.", "Plays 30-second track previews (iTunes), one after another. Windows only.", "Spielt 30-Sekunden-Vorschauen (iTunes) nacheinander. Nur Windows.", "Играют 30-секундные превью (iTunes) по очереди. Только Windows.");
        a("s.mstop", "Zatrzymaj muzykę", "Stop music", "Musik stoppen", "Остановить музыку");
        a("s.mnext", "Następny utwór", "Next track", "Nächster Titel", "Следующий трек");
        a("o.none", "Brak", "None", "Keine", "Нет");
    }
}

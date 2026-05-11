Deník

Jednoduchá osobní deníková aplikace v Javě (Swing + Maven).

Spuštění:
  ./mvnw exec:java
  (nebo přímo z IDE - hlavní třída diary.Main)

Funkce:
- první spuštění se zeptá na jméno uživatele
- při dalších spuštěních se zobrazí krátká uvítací obrazovka a poté kalendář
- v kalendáři lze prohlížet měsíce, klikem na měsíc/rok přejít rovnou na jiný měsíc
- vlevo nahoře vyhledávání slov v zápisech
- tlačítko "Přidej zápis" rovnou otevře zápis pro dnešek
- klikem na den se otevře přehled všech zápisů pro daný den
- dny se zápisy jsou barevně odlišené podle počtu zápisů
- dny obsahující obrázky jsou označené ikonkou
- okno zápisu zobrazuje obrázek jako náhled a umí přepnout den vpřed / vzad

Data se ukládají do složky data/ podle dne (YYYY-MM-DD/entry_xxxxx.txt + případně .png).

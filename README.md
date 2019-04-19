# Első feladat - Fejlesztés (specifikáció)

Mike szeretné CSV-ből feltölteni adatokkal a Jira-ját issuekkal. Azonban a betöltés lassú, arra gondolt, hogy párhuzamosan hamarabb a végére jutna.

A feladat egy olyan Java standalone alkalmazás készítése (jar), ami egy CSV fájlt dolgoz fel, párhuzamosan több szálon, és a benne lévő sorokat konvertálja át Jira issue-kká, JSON-ban elküldve a Jira REST API-val.

A betöltéskor egy opcionálisan megadott reguláris kifejezés segítségével lehet szűrni a importálandó rekordokat. 
A rekordot akkor kell betölteni, ha a reguláris kifejezés az adott sor tetszőleges mezőjére egyezést ad.

A betöltés után jelenítse meg, hogy mennyi volt a végrehajtással töltött idő minimális, maximális és átlagos értéke, illetve a várakozási queue-ban töltött idő, valamint az összes feldolgozott, illetve a sikeresen feltöltött sorok száma

Az alkalmazás parancssorból legyen paraméterezhető, Mike ezeket adhassa meg:

    - A párhuzamos szálak száma
    - Reguláris kifejezés a sorok filterezéséhez

Csatolmányban megtalálható Mike CSV-je

Azért, hogy a fejlesztést gyorsabban el lehessen végezni, nincs szükség valódi Jira-ra, illetve valódi REST kérésekre.
Elegendő, ha a valódi HTTP kérés helyett egy mock Java metódus van (azaz a HTTP kérést nem kell kiküldeni), ami az alábbiak szerint működjön:

    - 10 és 100ms közötti random ideig várakozik
    - visszatérési értékként HTTP 200 vagy 403-mas hibakódot szimulál, utóbbit szintén véletlenszerűen, 1% eséllyel
    - loggolja az elküldendő Jira issue-nak megfelelő JSON-t
    - JSON struktúra: Egyszerű JSON objektum, a JSON kulcsok (mezőnevek) a CSV első sorában lévő fejlécben szerepelnek

# Első feladat - Szállítandó

Egy Java standalone alkalmazás, amely Java 8/9 nyelvi elemeket használ, önállóan futtatható és Mavennel buildelhető
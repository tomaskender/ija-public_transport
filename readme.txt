Public Transport Tracker
---------------------------

Aplikácia slúži na simulovanie fiktívnych cestovných poriadkov v čase.

Aplikácia sa dá preložiť a spustiť pomocou priloženého súboru build.xml príkazom sudo ant run.
Pre vygenerovanie dokumentácie sa môže použiť ant doc, len pre zloženie jar súboru sudo ant compile. Pre odstránenie zloženého programu ant clean.
Po spustení aplikácia vyzve používateľa, aby si vybral súbor, v ktorom sa nachádza želaná mapa aj s cestovnými poriadkami vo formáte xml.
Následne v aplikácii užívateľ nastaví počiatočný čas simulácie a spustí simuláciu jej odpauzovaním.
Po kliknutí na niektorú ulicu sa nám sprístupní možnosť nastaviť vyššie vyťaženie ulice (dropdown selektor na pravom dolnom spodku okna), ktorý zmení rýchlosť,
ktorou sa vozidlá po danej ulici budú pohybovať- toto môže spôsobiť meškanie spojov voči cestovnému poriadku.
LOW nepredstavuje žiadne oneskorenie (1x), MEDIUM mierne spomalí vozdilá (0.75x) a HIGH ich výrazne spomalí (0.5x).
Taktiež je možné uzavrieť ulicu umiestnením prekážky (tlačidlo Close Street). Následne sa prepočítaju trasy vozidiel a pre vybrané vozidlá,
ktoré by daným bodom mali v budúcnosti prechádzať, vyznačíme obchádzku a nastavíme jej čas. Keď nastavíme každej položke cestu
(autá sa zgrupujú, aby sme ciest museli nastavovať čo najmenej), potom odpauzujeme aplikáciu a alternatívne cesty sa zapíšu do pamäte.
Počas simulácie sa taktiež dá meniť jej rýchlosť od 1x až po 5000x. Fungovali by aj vyššie hodnoty, ale už pri rýchlosti 5000 sú vozidlá ťažko sledovateľné pre ľudské oko.

---------------------------

Členovia tímu:
Tomáš Kender, xkende01
Tomáš Ďuriš, xduris05
# Full Discs (Fabric, 1.21.11, только клиент)

Музыкальные диски доигрывают трек до конца, даже если он длиннее лимита
`length_in_seconds` у диска. Работает только у тебя на клиенте, сервер менять не нужно.

## Сборка
Нужен JDK 21 или новее.

    ./gradlew build        (Windows: gradlew.bat build)

Готовый мод: `build/libs/full-discs-1.0.0.jar` (не `-sources`).
Положи его в `.minecraft/mods/`. Fabric API не требуется.

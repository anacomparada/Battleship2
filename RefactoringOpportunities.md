| Local | Nome do Cheiro (Fowler) | Nome da Refabricação (IntelliJ) | Nº Aluno |
|---|---|---|---|
| Game::randomEnemyFire / Game::randomPlayerFire | Duplicated Code | Introduce Constant (`RAJADA_PREFIX`) | 93263 |
| Game::fireShots / Game::fireShotsAtAlien / Game::randomEnemyFire / Game::randomPlayerFire | Duplicated Code | Replace Type with Diamond (`<>`) | 93263 |
| Game::printBoard / Game::printMyBoard / Game::printAlienBoard | Long Parameter List | Rename (`show_shots` → `showShots`, `show_legend` → `showLegend`, `ship_pos` → `shipPos`, `adjacent_pos` → `adjacentPos`) | 93263 |
| Game::randomEnemyFire / Game::randomPlayerFire | Duplicated Code | Extract Method (`generateShots()`) | 93263 |
| Game::readEnemyFire / Game::readAlienFire | Duplicated Code | Extract Method (`parseClassicShots()`) | 93263 |
| Game::buildMap / Game::printBoard | Long Method | Extract Method (`createEmptyMap()`, `markShipsOnMap()`, `markShotsOnMap()`, `hideShipsOnMap()`) | 93263 |
| Game::buildMap / Game::printBoard / Game::fireShots / Game::fireSingleShot | Data Flow & Redundancy | Replace assert with proper check (if + throw `IllegalArgumentException`) | 93263 |
| Game::jsonShots | Inappropriate Intimacy | Replace generic exception (`RuntimeException` → `IllegalStateException`) | 93263 |
| Game::generateShots | Long Method | Extract Method (`fillShotsRandomly()`, `fillShotsFromCandidates()`, `padShots()`) | 93263 |

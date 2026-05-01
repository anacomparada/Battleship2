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
| BargeTest, BoardWindowTest, CarrackTest, FrigateTest, GalleonTest, PositionTest, ShipTest | Excessive Visibility | Change Visibility: Remoção do modificador public | 123762 |
| BoardWindow | Improper Static Access | Use Static Access: Acesso via WindowConstants | 123762 | 
| BoardWindow | Complex Method | Extract Method / Refactor: Redução da Complexidade Cognitiva | 123762 | 
| BoardWindow | Utility Class Smell | Hide Utility Constructor: Adição de construtor privado | 123762 | 
| BoardWindowTest, CarrackTest, GalleonTest | Redundant Code | Replace Lambda with Method Reference | 123762 | 
| BoardWindowTest, Carrack | Unused Imports | Optimize Imports: Remoção de bibliotecas não utilizadas | 123762 | 
| CaravelTest, CarrackTest, ShipTest | Assertion Roulette | Refactor Lambda: Apenas uma invocação no assertThrows | 123762 |
| Position, Ship | Primitive Obsession | Use Diamond Operator: Substituição por <> | 123762 | 
| Position | Math Smell | Use Dedicated Random: Substituição por nextInt() | 123762 | 
| Position | Variable Shadowing | Rename Variable: Evitar ocultação de campos de classe | 123762 | 
| Position | Feature Envy | Pattern Matching for instanceof: Simplificar verificação e cast | 123762 | 
| Position | Commented-out Code | Safe Delete: Remoção de código comentado | 123762 | 
| PositionTest | Boolean Assertion Simplification | Simplify Assertion: Uso de assertEquals / assertNotEquals | 123762 | 
| CaravelTest, ShipTest, PositionTest | Assertion Roulette | Assertion Grouping: Uso de assertAll | 123762 | 
| Ship | Redundant Assignment | Remove Redundant Assignment: Eliminar atribuições inúteis | 123762 | 
| Ship | Improper Indentation | Reformat Code: Indentação correta de blocos | 123762 | 
| Ship | Abstract Constructor Visibility | Change Visibility: Alterar para protected | 123762 | 
| Ship | Defensive Programming Smell | Replace Assert: Uso de verificações formais e exceções | 123762 | 
| Ship | Duplicated Code | Introduce Constant: Para literais de String repetidos | 123762 | 
| PositionTest | Assertion Arguments Order | Swap Assertion Arguments: Corrigir ordem esperado/atual | 123762 |

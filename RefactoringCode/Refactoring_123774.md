| Local | Nome do Cheiro (Fowler) | Nome da Refabricação (IntelliJ) | Nº Aluno |
|---|---|---|---|
| Fleet::createRandom | Primitive Obsession / Magic Strings | Introduce Constant (`DEFAULT_SHIP_TYPES`) | 123774 |
| Fleet::getFloatingShips / Fleet::getSunkShips | Duplicated Code | Replace Type with Diamond (`<>`) | 123774 |
| Fleet::addShip / Fleet::getShipsLike / Fleet::shipAt / Fleet::isInsideBoard / Fleet::collisionRisk / Fleet::printShips / Fleet::printShipsByCategory | Data Flow & Redundancy | Replace assert with proper check (if + throw `IllegalArgumentException`) | 123774 |
| Fleet::colisionRisk | Naming Convention | Rename (`colisionRisk` → `collisionRisk`) | 123774 |
| Fleet::printStatus | Comments (Dead Code) | Remove code / Delete commented lines | 123774 |
| PdfExporter | Bad Practice (Utility Class) / Encapsulation | Add Private Constructor | 123774 |
| Tasks | Bad Practice (Utility Class) / Encapsulation | Add Private Constructor | 123774 |
| Tasks::menu | Long Method / Brain Method | Extract Method (`processCommand`, `handleRajada`, `handleSimula`, etc.) e Extract Class (`GameSession`) | 123774 |
| Tasks::handleRajada / Tasks::handleSimula | Null Dereference Risk | Add Null Check (`!= null`) | 123774 |
| Tasks::buildFleet / Tasks::readShip / Tasks::readPosition | Data Flow & Redundancy | Replace assert with proper check (if + throw `IllegalArgumentException`) | 123774 |

package battleship;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PdfExporterTest {
    private PdfExporter pdfExporter;

    @BeforeEach
    void setUp() {
        // Instancia a classe sob teste (mesmo sendo maioritariamente estática, cumprimos o requisito de cobertura)
        pdfExporter = new PdfExporter();
    }

    @AfterEach
    void tearDown() {
        // Limpa e anula a instância
        pdfExporter = null;
    }

    // --- TESTES DE CONSTRUTORES (CC: 1) ---

    @Test
    void testPdfExporterConstructor() {
        assertNotNull(pdfExporter, "Error: expected PdfExporter instance to be initialized");
    }

    @Test
    void testPdfStateConstructor() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            PDPageContentStream stream = new PDPageContentStream(doc, page);

            Object state = createPdfState(page, stream, 750f);
            assertNotNull(state, "Error: expected internal PdfState instance to be correctly instantiated");
            stream.close();
        }
    }

    // --- TESTES: exportGameReport (CC: 3) ---

    @Test
    void testExportGameReport_Success() throws IOException {
        // Caminho 1: Tudo válido, ficheiro criado com sucesso
        Map<String, Object> gameReturns = new HashMap<>();
        gameReturns.put("getMyMoves", new ArrayList<>());
        gameReturns.put("getAlienMoves", new ArrayList<>());
        IGame mockGame = mockInterface(IGame.class, gameReturns);

        File tempFile = File.createTempFile("battleship_report_test", ".pdf");
        tempFile.deleteOnExit();

        assertDoesNotThrow(() -> PdfExporter.exportGameReport(mockGame, tempFile.getAbsolutePath()),
                "Error: expected no exceptions during a successful PDF generation");

        assertTrue(tempFile.exists(), "Error: expected the generated PDF file to exist on disk");
        assertTrue(tempFile.length() > 0, "Error: expected the generated PDF file to contain bytes");
    }

    @Test
    void testExportGameReport_NullGame() {
        // Caminho 2: game é null, o que lança NullPointerException quando tenta chamar game.getMyMoves()
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            PdfExporter.exportGameReport(null, "dummy.pdf");
        }, "Error: expected NullPointerException but it was not thrown for null IGame parameter");

        assertNotNull(exception, "Error: expected a valid exception instance");
    }

    @Test
    void testExportGameReport_IOException() {
        // Caminho 3: Caminho de ficheiro inválido para forçar a captura do IOException no try-catch
        Map<String, Object> gameReturns = new HashMap<>();
        gameReturns.put("getMyMoves", new ArrayList<>());
        gameReturns.put("getAlienMoves", new ArrayList<>());
        IGame mockGame = mockInterface(IGame.class, gameReturns);

        // Um caminho garantidamente inválido para despoletar erro no PDDocument.save()
        String invalidPath = System.getProperty("os.name").toLowerCase().contains("win")
                ? "Z:\\\\?\\<>|\"*" : "/dev/null/invalid_path/file.pdf";

        assertDoesNotThrow(() -> PdfExporter.exportGameReport(mockGame, invalidPath),
                "Error: expected internal IOException to be caught and handled implicitly without throwing");
    }

    // --- TESTES: printMoveSection (CC: 8 - Cobertura Total de Branches e Cores) ---

    @Test
    void testPrintMoveSection_NullMoves() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            PDPageContentStream stream = new PDPageContentStream(doc, page);
            Object state = createPdfState(page, stream, 750f);

            assertDoesNotThrow(() -> invokePrintMoveSection(doc, state, "Title", null),
                    "Error: expected no exception when moves list is null");
            stream.close();
        }
    }

    @Test
    void testPrintMoveSection_EmptyMoves() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            PDPageContentStream stream = new PDPageContentStream(doc, page);
            Object state = createPdfState(page, stream, 750f);

            assertDoesNotThrow(() -> invokePrintMoveSection(doc, state, "Title", new ArrayList<>()),
                    "Error: expected no exception when moves list is empty");
            stream.close();
        }
    }

    @Test
    void testPrintMoveSection_Outcome_Invalid() throws Exception {
        IGame.ShotResult mockResult = createShotResult(false, false, null, false);
        runPrintMoveSectionTestWithCondition(mockResult);
    }

    @Test
    void testPrintMoveSection_Outcome_Repeated() throws Exception {
        IGame.ShotResult mockResult = createShotResult(true, true, null, false);
        runPrintMoveSectionTestWithCondition(mockResult);
    }

    @Test
    void testPrintMoveSection_Outcome_Water() throws Exception {
        IGame.ShotResult mockResult = createShotResult(true, false, null, false);
        runPrintMoveSectionTestWithCondition(mockResult);
    }

    @Test
    void testPrintMoveSection_Outcome_Sunk() throws Exception {
        Map<String, Object> shipMap = new HashMap<>();
        shipMap.put("getCategory", "Submarino");
        IShip mockShip = mockInterface(IShip.class, shipMap);

        IGame.ShotResult mockResult = createShotResult(true, false, mockShip, true);
        runPrintMoveSectionTestWithCondition(mockResult);
    }

    @Test
    void testPrintMoveSection_Outcome_Hit() throws Exception {
           Map<String, Object> shipMap = new HashMap<>();
        shipMap.put("getCategory", "Porta-Aviões");
        IShip mockShip = mockInterface(IShip.class, shipMap);

        IGame.ShotResult mockResult = createShotResult(true, false, mockShip, false);
        runPrintMoveSectionTestWithCondition(mockResult);
    }

    // --- TESTES: checkPagination (CC: 2) ---

    @Test
    void testCheckPagination_NoNewPageNeeded() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(doc, page);

            // y = 150, limite = 100. Não precisa de página nova
            Object state = createPdfState(page, stream, 150f);

            invokeCheckPagination(doc, state, 100f);

            assertEquals(1, doc.getNumberOfPages(), "Error: expected exactly 1 page as limit was not reached");
            stream.close();
        }
    }

    @Test
    void testCheckPagination_NewPageCreated() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(doc, page);

            // y = 50, limite = 100. Precisa de página nova!
            Object state = createPdfState(page, stream, 50f);

            invokeCheckPagination(doc, state, 100f);

            assertAll(
                    () -> assertEquals(2, doc.getNumberOfPages(), "Error: expected exactly 2 pages after auto-pagination"),
                    () -> assertNotEquals(stream, getStreamFromState(state), "Error: expected a new PDPageContentStream to be instantiated")
            );

            // Libertar stream nova
            ((PDPageContentStream) getStreamFromState(state)).close();
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES (Reflection e Mocking em Java Puro - Sem Mockito)
    // =========================================================================

    /**
     * Utilitário mágico que simula interfaces dinamicamente usando Java Reflection.
     * Isto evita a necessidade de bibliotecas externas como o Mockito.
     */
    @SuppressWarnings("unchecked")
    private static <T> T mockInterface(Class<T> interfaceClass, Map<String, Object> methodReturns) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                (proxy, method, args) -> {
                    if (methodReturns != null && methodReturns.containsKey(method.getName())) {
                        return methodReturns.get(method.getName());
                    }
                    // Retornos default seguros para evitar NullPointerExceptions
                    Class<?> retType = method.getReturnType();
                    if (retType == boolean.class) return false;
                    if (retType == int.class) return 0;
                    if (retType == String.class) return "MockString";
                    if (retType == List.class) return new ArrayList<>();
                    return null;
                }
        );
    }

    private Object createPdfState(PDPage page, PDPageContentStream stream, float y) throws Exception {
        Class<?> clazz = Class.forName("battleship.PdfExporter$PdfState");
        Constructor<?> constructor = clazz.getDeclaredConstructor(PDPage.class, PDPageContentStream.class, float.class);
        constructor.setAccessible(true);
        return constructor.newInstance(page, stream, y);
    }

    private PDPageContentStream getStreamFromState(Object state) throws Exception {
        Field field = state.getClass().getDeclaredField("contentStream");
        field.setAccessible(true);
        return (PDPageContentStream) field.get(state);
    }

    private void invokePrintMoveSection(PDDocument document, Object state, String title, List<IMove> moves) throws Exception {
        Method method = PdfExporter.class.getDeclaredMethod("printMoveSection", PDDocument.class, state.getClass(), String.class, List.class);
        method.setAccessible(true);
        method.invoke(null, document, state, title, moves);
    }

    private void invokeCheckPagination(PDDocument document, Object state, float limit) throws Exception {
        Method method = PdfExporter.class.getDeclaredMethod("checkPagination", PDDocument.class, state.getClass(), float.class);
        method.setAccessible(true);
        method.invoke(null, document, state, limit);
    }

    /**
     * Motor principal para testar o 'printMoveSection'. Injeta uma jogada e um tiro com
     * o resultado esperado, varrendo todos os IF/ELSE das cores do PDF.
     */
    private void runPrintMoveSectionTestWithCondition(IGame.ShotResult mockResult) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            PDPageContentStream stream = new PDPageContentStream(doc, page);
            Object state = createPdfState(page, stream, 750f);

            // Mock da Posição (IPosition é interface, então funciona com Proxy)
            Map<String, Object> posMap = new HashMap<>();
            posMap.put("toString", "C3");
            IPosition mockPos = mockInterface(IPosition.class, posMap);

            // Mock do Movimento (IMove é interface, então funciona com Proxy)
            Map<String, Object> moveMap = new HashMap<>();
            moveMap.put("getNumber", 1);
            moveMap.put("getShots", Collections.singletonList(mockPos));
            moveMap.put("getShotResults", Collections.singletonList(mockResult));
            IMove mockMove = mockInterface(IMove.class, moveMap);

            List<IMove> moves = Collections.singletonList(mockMove);

            // Executa o método privado
            assertDoesNotThrow(() -> invokePrintMoveSection(doc, state, "Test Shots", moves),
                    "Error: expected no exception during single shot logic execution");

            stream.close();
        }
    }

    private IGame.ShotResult createShotResult(boolean valid, boolean repeated, IShip ship, boolean sunk) throws Exception {
        Class<?> clazz = Class.forName("battleship.IGame$ShotResult");

        // Vai buscar o construtor exato do record: (boolean, boolean, IShip, boolean)
        Constructor<?> constructor = clazz.getDeclaredConstructor(boolean.class, boolean.class, IShip.class, boolean.class);
        constructor.setAccessible(true);

        // Instancia o record com os valores exatos que pedimos
        return (IGame.ShotResult) constructor.newInstance(valid, repeated, ship, sunk);
    }
}

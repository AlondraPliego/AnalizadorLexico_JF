package CODIGO;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.*;

public class Analizador {

    public static class TokenInfo {

        public final String lexema;
        public final String tipo;

        public TokenInfo(String lexema, String tipo) {
            this.lexema = lexema;
            this.tipo = tipo;
        }
    }

    private static final Map<String, Boolean> robotsIniciados = new HashMap<>();

    public static List<TokenInfo> analizarTextoCompleto(String texto) throws Exception {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto no puede estar vacío");
        }

        robotsIniciados.clear();

        Lexer lexer = new Lexer(new StringReader(texto));
        List<TokenInfo> tokens = new ArrayList<>();
        String robotActual = null;
        String accionActual = null;

        Tokens token;
        while ((token = lexer.yylex()) != null) {
            switch (token) {
                case Palabra_r:
                    // Nuevo robot detectado
                    robotActual = lexer.lexeme;
                    robotsIniciados.putIfAbsent(robotActual, false);
                    tokens.add(new TokenInfo(lexer.lexeme, "Palabra_r"));
                    break;

                case Identificador:
                    tokens.add(new TokenInfo(lexer.lexeme, "Identificador"));
                    break;

                case Metodo:
                    accionActual = lexer.lexeme;
                    tokens.add(new TokenInfo(lexer.lexeme, "Método"));
                    break;

                case Accion:
                    if ("iniciar".equals(lexer.lexeme)) {
                        robotsIniciados.put(robotActual, true);
                    } else {
                    }
                    tokens.add(new TokenInfo(lexer.lexeme, "Acción"));
                    break;

                case Numero:
                    tokens.add(new TokenInfo(lexer.lexeme, "Número"));
                    break;

                case Igual:
                    tokens.add(new TokenInfo(lexer.lexeme, "Operador"));
                    break;

                case Punto:
                    tokens.add(new TokenInfo(lexer.lexeme, "Punto"));
                    break;

                default:
                    tokens.add(new TokenInfo(lexer.lexeme, "No válido"));
            }
        }

        return tokens;
    }

    public static String generarReporte(List<TokenInfo> tokens) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== ANÁLISIS LÉXICO ===\n\n");

        if (tokens.isEmpty()) {
            reporte.append("No se encontraron tokens válidos\n");
            return reporte.toString();
        }

        int anchoLexema = Math.max(
                tokens.stream().mapToInt(t -> t.lexema.length()).max().orElse(0),
                "LEXEMA".length()
        );
        int anchoTipo = Math.max(
                tokens.stream().mapToInt(t -> t.tipo.length()).max().orElse(0),
                "TIPO".length()
        );

        anchoLexema = Math.max(Math.min(anchoLexema, 25), 10);
        anchoTipo = Math.max(Math.min(anchoTipo, 25), 10);

        
        String formatoEncabezado = "%-" + anchoLexema + "s %-" + anchoTipo + "s%n";
        reporte.append(String.format(formatoEncabezado, "LEXEMA", "TIPO"));

        reporte.append(String.format("%-" + (anchoLexema + anchoTipo + 1) + "s%n",
                "").replace(' ', '-'));

        String formatoFila = "%-" + anchoLexema + "s %-" + anchoTipo + "s%n";

        for (TokenInfo t : tokens) {
            String lexema = t.lexema.length() > anchoLexema
                    ? t.lexema.substring(0, anchoLexema - 3) + "..."
                    : t.lexema;

            String tipo = t.tipo.length() > anchoTipo
                    ? t.tipo.substring(0, anchoTipo - 3) + "..."
                    : t.tipo;

            reporte.append(String.format(formatoFila, lexema, tipo));
        }

        reporte.append("\nTotal tokens: ").append(tokens.size());

        return reporte.toString();
    }

    static class TokenTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                                                     boolean isSelected, boolean hasFocus, 
                                                     int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 1 && "No válido".equals(value)) {
                c.setForeground(Color.RED);
            } else {
                c.setForeground(Color.BLACK);
            }
            
            return c;
        }
    }

    static class HeaderRenderer implements TableCellRenderer {
        private final TableCellRenderer defaultRenderer;

        public HeaderRenderer(TableCellRenderer defaultRenderer) {
            this.defaultRenderer = defaultRenderer;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                                                     boolean isSelected, boolean hasFocus, 
                                                     int row, int column) {
            JLabel header = (JLabel) defaultRenderer.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            header.setBackground(new Color(0, 102, 153));  // Color azul que coincide con el botón
            header.setForeground(Color.WHITE);
            header.setHorizontalAlignment(SwingConstants.CENTER);
            header.setBorder(BorderFactory.createEtchedBorder());
            header.setFont(header.getFont().deriveFont(Font.BOLD));
            
            return header;
        }
    }

    public static void generarReporteEnPane(List<TokenInfo> tokens, JTextPane textPane) {
        // Crear modelo para la tabla
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
        
        model.addColumn("LEXEMA");
        model.addColumn("TIPO");
        
        for (TokenInfo token : tokens) {
            model.addRow(new Object[]{token.lexema, token.tipo});
        }
        
        JTable table = new JTable(model);
        
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setRowSelectionAllowed(true);
        
        TokenTableCellRenderer cellRenderer = new TokenTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, cellRenderer);
        
        HeaderRenderer headerRenderer = new HeaderRenderer(table.getTableHeader().getDefaultRenderer());
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        
        JLabel totalLabel = new JLabel("Total tokens: " + tokens.size(), SwingConstants.CENTER);
        totalLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        totalLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        totalLabel.setForeground(new Color(0, 102, 153));
        totalLabel.setOpaque(true);
        totalLabel.setBackground(new Color(240, 240, 240));
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        
        JPanel tablePanel = new JPanel(new BorderLayout(0, 0));
        tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(table, BorderLayout.CENTER);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(totalLabel, BorderLayout.SOUTH);
       
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        
        try {
            StyledDocument doc = textPane.getStyledDocument();
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        textPane.insertComponent(mainPanel);
    }
}
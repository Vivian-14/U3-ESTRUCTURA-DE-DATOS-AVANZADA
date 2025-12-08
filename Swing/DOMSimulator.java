/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package simuladordom;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;

/*
 * PROYECTO: Simulador de DOM (Estructura HTML)
 * ENTREGA: Martes 9 de Diciembre 2025
 * AUTORA: Alondra Vianney Hernández Torres — GTID141
 * EMAIL: 1224100684.avht@gmail.com
 *
//Descripcion General
 * Esta aplicación en Java Swing permite simular el funcionamiento de un DOM (Document Object Model)
 * tal como funciona en una página web. El usuario puede:
 *
 *   - Crear elementos HTML (div, p, h1, img, etc.)
 *   - Editar nodos existentes (cambiar etiqueta o texto)
 *   - Eliminar nodos
 *   - Ver cómo se genera el HTML en tiempo real
 *   - Exportar la estructura final a un archivo .html válido
 *
 * El programa está dividido en:
 *
 *   1) Árbol JTree ( Representa la estructura DOM con nodos. )
 *   2) Vista HTML ( Muestra cómo se vería la página real.
 *   3) Panel de controles (Permite agregar, editar y eliminar elementos)
 *
 * Cada nodo contiene un objeto "ElementNode", que almacena la etiqueta (tag)
 * y el contenido o atributos del HTML.
 */

public class DOMSimulator extends JFrame {
    // Modelo de árbol
    private DefaultTreeModel treeModel;
    private JTree tree;
    // Vista HTML
    private JEditorPane htmlView;
    // Controles
    private JComboBox<String> tagCombo;
    private JTextField textField;
    private JButton addButton, deleteButton, editButton, exportButton;

    public DOMSimulator() {
        super("Simulador de DOM - Creación de Página Web");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        // Root element (html)
        ElementNode rootElement = new ElementNode("html", "");
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootElement);
        treeModel = new DefaultTreeModel(rootNode);

       
        DefaultMutableTreeNode pNode = new DefaultMutableTreeNode(new ElementNode("p", "Bienvenido"));
        DefaultMutableTreeNode h1Node = new DefaultMutableTreeNode(new ElementNode("h1", "Bienvenidos"));
        DefaultMutableTreeNode footerNode = new DefaultMutableTreeNode(new ElementNode("footer", "Copyright"));

        treeModel.insertNodeInto(pNode, rootNode, rootNode.getChildCount());
        treeModel.insertNodeInto(h1Node, rootNode, rootNode.getChildCount());
        treeModel.insertNodeInto(footerNode, rootNode, rootNode.getChildCount());

        // JTree
        tree = new JTree(treeModel);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(true);
        tree.setCellRenderer(new ElementTreeCellRenderer());
        JScrollPane treeScroll = new JScrollPane(tree);

        // HTML view (JEditorPane)
        htmlView = new JEditorPane();
        htmlView.setContentType("text/html");
        htmlView.setEditable(false);
        JScrollPane htmlScroll = new JScrollPane(htmlView);

        // Controls panel (bottom)
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.gridx = 0; c.gridy = 0;
        controls.add(new JLabel("Etiqueta:"), c);

        tagCombo = new JComboBox<>(new String[] {"div","p","h1","h2","footer","header","section","span","ul","li","img","a","input"});
        c.gridx = 1; controls.add(tagCombo, c);

        c.gridx = 2; controls.add(new JLabel("Texto / atributos (ej: href='#'):"), c);

        textField = new JTextField(30);
        c.gridx = 3; controls.add(textField, c);

        addButton = new JButton("Agregar Nodo");
        c.gridx = 4; controls.add(addButton, c);

        deleteButton = new JButton("Eliminar Nodo");
        c.gridx = 5; controls.add(deleteButton, c);

        editButton = new JButton("Editar Nodo");
        c.gridx = 6; controls.add(editButton, c);

        exportButton = new JButton("Exportar HTML");
        c.gridx = 7; controls.add(exportButton, c);

        // Layout split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, htmlScroll);
        split.setDividerLocation(300);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(split, BorderLayout.CENTER);
        getContentPane().add(controls, BorderLayout.SOUTH);

        // Listeners
        addButton.addActionListener(e -> onAddNode());
        deleteButton.addActionListener(e -> onDeleteNode());
        editButton.addActionListener(e -> onEditNode());
        exportButton.addActionListener(e -> onExportHTML());

     
        treeModel.addTreeModelListener(new TreeModelListener() {
            @Override public void treeNodesChanged(TreeModelEvent e) { refreshHTML(); }
            @Override public void treeNodesInserted(TreeModelEvent e) { refreshHTML(); }
            @Override public void treeNodesRemoved(TreeModelEvent e) { refreshHTML(); }
            @Override public void treeStructureChanged(TreeModelEvent e) { refreshHTML(); }
        });

        // Double-click to edit
        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onEditNode();
                }
            }
        });

        // Inicializar vista HTML
        refreshHTML();
    }

    // Validación simple antes de agregar
    private boolean validateAdd(DefaultMutableTreeNode parent, String tag, String text) {
        if (parent == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un nodo padre en el árbol.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (tag == null || tag.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una etiqueta válida.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        // Ejemplo: img no debe tener texto visible (podemos permitir atributos)
        if (tag.equalsIgnoreCase("img") && text.trim().isEmpty()) {
            int opt = JOptionPane.showConfirmDialog(this, "Etiqueta <img> sin atributos. ¿Desea agregarla con src vacío?", "Confirmar", JOptionPane.YES_NO_OPTION);
            return opt == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private void onAddNode() {
        TreePath sel = tree.getSelectionPath();
        DefaultMutableTreeNode parent = sel == null ? (DefaultMutableTreeNode) treeModel.getRoot() : (DefaultMutableTreeNode) sel.getLastPathComponent();
        String tag = (String) tagCombo.getSelectedItem();
        String text = textField.getText();

        if (!validateAdd(parent, tag, text)) return;

        ElementNode newElem = new ElementNode(tag, text);
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newElem);
        treeModel.insertNodeInto(newNode, parent, parent.getChildCount());
        tree.scrollPathToVisible(new TreePath(newNode.getPath()));
        textField.setText("");
    }

    private void onDeleteNode() {
        TreePath sel = tree.getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un nodo a eliminar.", "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) sel.getLastPathComponent();
        if (node.isRoot()) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar la raíz.", "Eliminar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar el nodo seleccionado?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            treeModel.removeNodeFromParent(node);
        }
    }

    private void onEditNode() {
        TreePath sel = tree.getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un nodo para editar.", "Editar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) sel.getLastPathComponent();
        ElementNode elem = (ElementNode) node.getUserObject();

        String newTag = (String) JOptionPane.showInputDialog(this, "Etiqueta:", "Editar nodo", JOptionPane.PLAIN_MESSAGE, null, null, elem.tag);
        if (newTag == null) return; // cancelar
        String newText = JOptionPane.showInputDialog(this, "Texto / Atributos:", elem.text);
        if (newText == null) return;

        elem.tag = newTag.trim();
        elem.text = newText;
        treeModel.nodeChanged(node);
    }

    private void onExportHTML() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("pagina.html"));
        int r = chooser.showSaveDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(f, "UTF-8")) {
                String html = generateHTML();
                pw.write(html);
                JOptionPane.showMessageDialog(this, "Exportado a: " + f.getAbsolutePath(), "Exportar", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generateHTML() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        StringBuilder sb = new StringBuilder();
        // Si el root es <html>, envolver con doctype y body
        sb.append("<!DOCTYPE html>\n");
        sb.append(recursiveToHTML(root, 0));
        return sb.toString();
    }

 
    private void refreshHTML() {
        try {
            String html = generateHTML();
            // Podemos aplicar un estilo simple en cabeza para ver mejor
            String styled = html.replaceFirst("(?i)(<html>)", "<html><head><meta charset='utf-8'><style>body{font-family:Arial,Helvetica,sans-serif;padding:8px;} footer{font-size:0.9em;color:gray;margin-top:10px;}</style></head>$1");
            htmlView.setText(styled);
            htmlView.setCaretPosition(0);
        } catch (Exception ex) {
            htmlView.setText("<html><body><pre>Error generando vista: " + ex.getMessage() + "</pre></body></html>");
        }
    }

    // Recursivo: convierte DefaultMutableTreeNode en HTML
    private String recursiveToHTML(DefaultMutableTreeNode node, int indent) {
        Object obj = node.getUserObject();
        StringBuilder sb = new StringBuilder();
        String pad = "  ".repeat(Math.max(0, indent));
        if (obj instanceof ElementNode) {
            ElementNode elem = (ElementNode) obj;
            String tag = elem.tag;
            String txt = elem.text == null ? "" : elem.text;
            // Tratamiento especial para etiquetas vacías (img, input)
            if (isSelfClosing(tag)) {
                // allow attributes in text field e.g. src="img.png" alt="x"
                sb.append(pad).append("<").append(tag);
                if (!txt.trim().isEmpty()) sb.append(" ").append(txt.trim());
                sb.append(" />\n");
            } else {
                sb.append(pad).append("<").append(tag).append(">");
                // Si tiene texto directo y no tiene hijos, lo coloca inline
                boolean hasChildren = node.getChildCount() > 0;
                if (!txt.isEmpty() && !hasChildren) {
                    sb.append(escapeHtml(txt));
                }
                sb.append("\n");
                // hijom
                for (Enumeration e = node.children(); e.hasMoreElements();) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.nextElement();
                    sb.append(recursiveToHTML(child, indent + 1));
                }
                if (hasChildren && !txt.isEmpty()) {
                    sb.append(pad).append("  ").append(escapeHtml(txt)).append("\n");
                }
                sb.append(pad).append("</").append(tag).append(">\n");
            }
        } else {
            // Fallback: toString
            sb.append(pad).append(escapeHtml(node.toString())).append("\n");
        }
        return sb.toString();
    }

    private boolean isSelfClosing(String tag) {
        String t = tag.toLowerCase();
        return t.equals("img") || t.equals("input") || t.equals("br") || t.equals("hr") || t.equals("meta") || t.equals("link");
    }

    // Escapa caracteres básicos para insertar como contenido
    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // Nodo de elemento con etiqueta y texto   
    static class ElementNode {
        String tag;
        String text;
        ElementNode(String tag, String text) {
            this.tag = tag;
            this.text = text == null ? "" : text;
        }
        @Override
        public String toString() {
            if (text == null || text.isEmpty()) return "<" + tag + ">";
            // limitar longitud en la vista del árbol
            String display = text.length() > 20 ? text.substring(0, 20) + "..." : text;
            return "<" + tag + "> " + display;
        }
    }

    // Renderer para mostrar etiquetas bonitamente
    static class ElementTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object u = node.getUserObject();
            if (u instanceof ElementNode) {
                ElementNode en = (ElementNode) u;
                setText(en.toString());
                setToolTipText("Etiqueta: " + en.tag + (en.text.isEmpty() ? "" : " | " + en.text));
            } else {
                setToolTipText(null);
            }
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DOMSimulator d = new DOMSimulator();
            d.setVisible(true);
        });
    }
}

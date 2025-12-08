/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package abb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Visualizador de Árbol Binario de Búsqueda (ABB)
 * Esta aplicación es un Visualizador de Árbol Binario de Búsqueda (ABB) creado con Swing.
 * Permite insertar, buscar, eliminar nodos y mostrar los recorridos Inorden, Preorden y Postorden.
 * Comprendi visualmente cómo funciona un ABB de forma mas sencilla.
 * Simulador DOM 
 * ENTREGA:Martes 9 de Diciembre 2025
 * Alondra Vianney Hernandez Torres GTID141
 * @1224100684.avht@gmail.com
 */
public class ABBVisualizer extends JFrame {

    // Estructura de datos del árbol
    static class Node {
        int key;
        Node left, right, parent;
        int x, y; // coordenadas para dibujar
        Node(int k) { key = k; }
    }

    static class BST {
        Node root;
        boolean allowDuplicates = false; // controlar duplicados 

        boolean insert(int k) {
            if (root == null) {
                root = new Node(k);
                return true;
            }
            Node cur = root;
            Node parent = null;
            while (cur != null) {
                parent = cur;
                if (k < cur.key) cur = cur.left;
                else if (k > cur.key) cur = cur.right;
                else { // igual
                    if (allowDuplicates) { // si permitimos, insertamos a la derecha
                        cur = cur.right;
                    } else {
                        return false; // duplicado ignorado
                    }
                }
            }
            Node nuevo = new Node(k);
            nuevo.parent = parent;
            if (k < parent.key) parent.left = nuevo;
            else parent.right = nuevo;
            return true;
        }

        Node search(int k) {
            Node cur = root;
            while (cur != null) {
                if (k == cur.key) return cur;
                else if (k < cur.key) cur = cur.left;
                else cur = cur.right;
            }
            return null;
        }

        // Recorridos que devuelven listas
        List<Integer> inorder() {
            List<Integer> res = new ArrayList<>();
            inorderRec(root, res);
            return res;
        }
        private void inorderRec(Node n, List<Integer> r) {
            if (n == null) return;
            inorderRec(n.left, r);
            r.add(n.key);
            inorderRec(n.right, r);
        }
        List<Integer> preorder() {
            List<Integer> r = new ArrayList<>();
            preorderRec(root, r);
            return r;
        }
        private void preorderRec(Node n, List<Integer> r) {
            if (n == null) return;
            r.add(n.key);
            preorderRec(n.left, r);
            preorderRec(n.right, r);
        }
        List<Integer> postorder() {
            List<Integer> r = new ArrayList<>();
            postorderRec(root, r);
            return r;
        }
        private void postorderRec(Node n, List<Integer> r) {
            if (n == null) return;
            postorderRec(n.left, r);
            postorderRec(n.right, r);
            r.add(n.key);
        }

        // Delete: 3 casos
        boolean delete(int k) {
            Node z = search(k);
            if (z == null) return false;
            deleteNode(z);
            return true;
        }

        private void transplant(Node u, Node v) {
            if (u.parent == null) root = v;
            else if (u == u.parent.left) u.parent.left = v;
            else u.parent.right = v;
            if (v != null) v.parent = u.parent;
        }

        private Node minimum(Node n) {
            while (n.left != null) n = n.left;
            return n;
        }

        private void deleteNode(Node z) {
            // caso 1: sin hijos
            if (z.left == null) transplant(z, z.right);
            else if (z.right == null) transplant(z, z.left);
            else {
                // dos hijos: usar sucesor inorden (minimo del subárbol derecho)
                Node y = minimum(z.right);
                if (y.parent != z) {
                    transplant(y, y.right);
                    y.right = z.right;
                    if (y.right != null) y.right.parent = y;
                }
                transplant(z, y);
                y.left = z.left;
                if (y.left != null) y.left.parent = y;
            }
        }

        void clear() { root = null; }

        // utilidad para contar nodos (usada en layout)
        int size() {
            return inorder().size();
        }
    }

    // Componentes GUI
    private BST tree = new BST();
    private DrawPanel drawPanel;
    private JTextField tfValue;
    private JButton btnInsert, btnDelete, btnSearch, btnClear;
    private JButton btnIn, btnPre, btnPost;
    private JLabel lblStatus;

    // Para resaltar nodo buscado
    private Node highlighted = null;

    public ABBVisualizer() {
        super("Visualizador de Árbol Binario de Búsqueda (ABB)");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 640);
        setLocationRelativeTo(null);

        // Top control panel
        JPanel top = new JPanel();
        top.add(new JLabel("Valor:"));
        tfValue = new JTextField(6);
        top.add(tfValue);
        btnInsert = new JButton("+ Insertar");
        btnDelete = new JButton("− Eliminar");
        btnSearch = new JButton("🔍 Buscar");
        btnClear = new JButton("Limpiar Árbol");
        top.add(btnInsert); top.add(btnDelete); top.add(btnSearch); top.add(btnClear);
        top.add(new JLabel("   Recorridos:"));
        btnIn = new JButton("Recorrido Inorden");
        btnPre = new JButton("Recorrido Preorden");
        btnPost = new JButton("Recorrido Postorden");
        top.add(btnIn); top.add(btnPre); top.add(btnPost);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);

        drawPanel = new DrawPanel();
        drawPanel.setBackground(Color.lightGray);
        getContentPane().add(drawPanel, BorderLayout.CENTER);

        lblStatus = new JLabel("Lista de recorrido / Mensajes...");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        getContentPane().add(lblStatus, BorderLayout.SOUTH);

     
        btnInsert.addActionListener(e -> actionInsert());
        btnDelete.addActionListener(e -> actionDelete());
        btnSearch.addActionListener(e -> actionSearch());
        btnClear.addActionListener(e -> { tree.clear(); highlighted = null; drawPanel.repaint(); lblStatus.setText("Árbol limpiado."); });
        btnIn.addActionListener(e -> lblStatus.setText("Recorrido Inorden: " + join(tree.inorder())));
        btnPre.addActionListener(e -> lblStatus.setText("Recorrido Preorden: " + join(tree.preorder())));
        btnPost.addActionListener(e -> lblStatus.setText("Recorrido Postorden: " + join(tree.postorder())));

       
        tfValue.addActionListener(e -> actionInsert());
    }

    private void actionInsert() {
        String s = tfValue.getText().trim();
        if (s.isEmpty()) return;
        try {
            int v = Integer.parseInt(s);
            boolean ok = tree.insert(v);
            if (!ok) lblStatus.setText("Insertar: valor duplicado ("+v+") ignorado.");
            else lblStatus.setText("Insertado: " + v);
            highlighted = null;
            tfValue.setText("");
            drawPanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actionDelete() {
        String s = tfValue.getText().trim();
        if (s.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese valor a eliminar.", "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int v = Integer.parseInt(s);
            boolean ok = tree.delete(v);
            if (ok) lblStatus.setText("Eliminado: " + v);
            else lblStatus.setText("No encontrado: " + v);
            highlighted = null;
            tfValue.setText("");
            drawPanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actionSearch() {
        String s = tfValue.getText().trim();
        if (s.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese valor a buscar.", "Buscar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int v = Integer.parseInt(s);
            Node found = tree.search(v);
            if (found != null) {
                highlighted = found;
                lblStatus.setText("Encontrado: " + v);
               
            } else {
                highlighted = null;
                lblStatus.setText("No encontrado: " + v);
            }
            tfValue.setText("");
            drawPanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String join(List<Integer> L) {
        if (L.isEmpty()) return "(vacío)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < L.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(L.get(i));
        }
        return sb.toString();
    }

    // Panel que dibuja el árbol
    class DrawPanel extends JPanel {
        final int NODE_RADIUS = 18;
        final int LEVEL_V_SPACING = 60;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (tree.root == null) {
                g.setColor(Color.DARK_GRAY);
                g.drawString("Árbol vacío. Inserte valores (ej: 50,30,70...)", 10, 20);
                return;
            }
            // preparar posiciones
            int width = getWidth();
            // calcular x por nivel: usar layout simple: en cada nivel espaciar por subárboles
            computeNodePositions(tree.root, 0, width, NODE_RADIUS*2, 30);

            // dibujar aristas y nodos (recursivo)
            drawEdgesAndNodes(g, tree.root);
        }

       
        private void computeNodePositions(Node n, int regionLeft, int regionRight, int minWidth, int startY) {
            if (n == null) return;
            int mid = (regionLeft + regionRight) / 2;
            n.x = mid;
        
            int depth = depth(n);
            n.y = startY + depth * LEVEL_V_SPACING;

            // Si hay hijos, subdividir regiones proporcionalmente por tamaño de subárbol
            int leftSize = (n.left == null) ? 0 : subtreeSize(n.left);
            int rightSize = (n.right == null) ? 0 : subtreeSize(n.right);
            // evitar división por cero: si ambos 0 asignar mitades
            if (n.left != null && n.right != null) {
                // dividir región según tamaño
                int total = Math.max(1, leftSize + rightSize);
                int leftRegionWidth = Math.max(minWidth, (int) ((regionRight - regionLeft) * ((double) leftSize / total)));
             
                computeNodePositions(n.left, regionLeft, regionLeft + leftRegionWidth, minWidth, startY);
                computeNodePositions(n.right, regionLeft + leftRegionWidth + 1, regionRight, minWidth, startY);
            } else if (n.left != null) {
                computeNodePositions(n.left, regionLeft, mid, minWidth, startY);
            } else if (n.right != null) {
                computeNodePositions(n.right, mid, regionRight, minWidth, startY);
            }
        }

        private int subtreeSize(Node n) {
            if (n == null) return 0;
            int left = subtreeSize(n.left);
            int right = subtreeSize(n.right);
            return 1 + left + right;
        }

       
        private int depth(Node n) {
            int d = 0;
            while (n.parent != null) {
                d++;
                n = n.parent;
            }
            return d;
        }

        private void drawEdgesAndNodes(Graphics g, Node n) {
            if (n == null) return;
            // dibujar aristas primero (líneas a hijos)
            g.setColor(Color.DARK_GRAY);
            if (n.left != null) {
                g.drawLine(n.x, n.y, n.left.x, n.left.y);
            }
            if (n.right != null) {
                g.drawLine(n.x, n.y, n.right.x, n.right.y);
            }
            // dibujar hijos
            if (n.left != null) drawEdgesAndNodes(g, n.left);
            if (n.right != null) drawEdgesAndNodes(g, n.right);

            // dibujar nodo (círculo)
            Graphics2D g2 = (Graphics2D) g;
            int r = NODE_RADIUS;
            int cx = n.x - r;
            int cy = n.y - r;

            if (n == highlighted) {
                g2.setColor(Color.RED);
                g2.fillOval(cx-2, cy-2, r*2+4, r*2+4);
            }
            // círculo principal
            g2.setColor(new Color(0, 102, 204));
            g2.fillOval(cx, cy, r*2, r*2);
            g2.setColor(Color.WHITE);
            String s = String.valueOf(n.key);
            FontMetrics fm = g2.getFontMetrics();
            int sw = fm.stringWidth(s);
            int sh = fm.getAscent();
            g2.drawString(s, n.x - sw/2, n.y + sh/2 - 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ABBVisualizer v = new ABBVisualizer();
            v.setVisible(true);
        });
    }
}

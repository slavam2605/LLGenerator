package ru.ifmo.ctddev.parsing;

import gen.Lab2Lexer;
import gen.Lab2Parser;

import javax.swing.*;
import java.awt.*;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Моклев Вячеслав
 */
public class TreeDrawer extends JPanel {
    private Tree tree;
    private int R = 15;

    private static void createWindow(int w, int h, Tree tree) {
        JFrame jFrame = new JFrame("Tree Drawer");
        TreeDrawer drawer = new TreeDrawer(w, h, tree);
        jFrame.add(drawer);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    public TreeDrawer(int w, int h, Tree tree) {
        this.tree = tree;
        setPreferredSize(new Dimension(w, h));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        ((Graphics2D) g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        drawTree(((Graphics2D) g), 0, getWidth(), 2 * R, tree);
    }

    private static int leafCount(Tree tree) {
        if (tree.getChildren().isEmpty()) {
            return 1;
        } else {
            return tree.getChildren().stream().mapToInt(TreeDrawer::leafCount).sum();
        }
    }

    private void drawTree(Graphics2D g, int left, int right, int y, Tree tree) {
        // calculating size
        int n = tree.getChildren().size();
        List<Integer> size = tree.getChildren().stream()
                .map(TreeDrawer::leafCount)
                .collect(Collectors.toList());
        int allSize = size.stream().mapToInt(Integer::intValue).sum();
        size = size.stream().map(s -> (right - left) * s / allSize).collect(Collectors.toList());
        // drawing
        int curLeft = left;
        for (int i = 0; i < tree.getChildren().size(); i++) {
            g.drawLine((right + left) / 2, y, curLeft + size.get(i) / 2, y + 3 * R);
            drawTree(g, curLeft, curLeft + size.get(i), y + 3 * R, tree.getChildren().get(i));
            curLeft += size.get(i);
        }
        g.setColor(Color.WHITE);
        g.fillOval((right + left) / 2 - R, y - R, 2 * R, 2 * R);
        g.setColor(Color.BLACK);
        g.drawOval((right + left) / 2 - R, y - R, 2 * R, 2 * R);

        String s = tree.getNode().toString();
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(s, g);
        int xx = (right + left) / 2 - (int) r.getWidth() / 2;
        int yy = y  - (int) r.getHeight() / 2 + fm.getAscent();
        g.drawString(s, xx, yy);
    }

    public static void main(String[] args) {
        createWindow(1440, 900, new Lab2Parser(new Lab2Lexer("a & (b | c ^ !!!a) | c & d & c")).parseExpr().t);
    }
}

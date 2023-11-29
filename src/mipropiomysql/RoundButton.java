package mipropiomysql;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;

public class RoundButton extends JButton {
    private Color backgroundColor;
    private Color hoverColor;
    private Color textColor;

    public RoundButton(String text, Color backgroundColor, Color hoverColor, Color textColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(textColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        RoundRectangle2D.Float background = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, 20, 20);

        if (getModel().isPressed()) {
            g2.setColor(hoverColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(backgroundColor);
        }

        g2.fill(background);
        g2.setColor(textColor);

        // Ajusta el color del texto según el estado del botón
        if (getModel().isPressed()) {
            g2.setColor(textColor.darker());
        } else if (getModel().isRollover()) {
            g2.setColor(textColor.brighter());
        } else {
            g2.setColor(textColor);
        }

        // Centra el texto en el botón
        FontMetrics metrics = g2.getFontMetrics();
        int x = (width - metrics.stringWidth(getText())) / 2;
        int y = ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g2.drawString(getText(), x, y);

        super.paintComponent(g);
    }
}

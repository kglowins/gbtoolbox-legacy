package com.github.kglowins.gbtoolbox.kindergarten.ui.bricks;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ButtonFactory {

  private ButtonFactory() {
  }

  private static final String START = "Start";

  public static JButton createButton(String text, String iconName) {
    JButton button = new JButton();
    if (text != null) {
      button.setText(text);
    }
    if (iconName != null) {
      button.setIcon(new ImageIcon(ButtonFactory.class.getResource(
          String.format("/icons/%s.png", iconName))));
    }
    return button;
  }

  public static JButton createStartButton() {
    return createButton(START, "start");
  }

  public static JButton createCancelButton() {
    return createButton(null, "cancel");
  }

  public static JButton createFolderButton() {
    return createButton(null, "folder");
  }

}

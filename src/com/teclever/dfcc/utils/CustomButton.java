package com.teclever.dfcc.utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class CustomButton extends Button{

	
	
public CustomButton(String text, EventHandler<ActionEvent> eventHandler) {
       super(text);
       this.setOnAction(eventHandler);
   }
public void setButtonStyle(String prefWidth,String prefHeight,String fontSize,String fontFamily,String fontWeight, String textColor, String backgroundColor, String borderColor,String borderWidth,
String borderRadius ) {
       StringBuilder styleBuilder = new StringBuilder();
       styleBuilder.append("-fx-pref-width: ").append(prefWidth).append("; ");
       styleBuilder.append("-fx-pref-height: ").append(prefHeight).append("; ");
       styleBuilder.append("-fx-font-size: ").append(fontSize).append("; ");
       styleBuilder.append("-fx-font-family: ").append(fontFamily).append("; ");
       styleBuilder.append("-fx-font-weight: ").append(fontWeight).append("; ");
       styleBuilder.append("-fx-text-fill: ").append(textColor).append("; ");
//       styleBuilder.append(" -fx-wrap-text: ").append(textWrap).append("; ");
       styleBuilder.append("-fx-background-color: ").append(backgroundColor).append("; ");
       styleBuilder.append("-fx-border-color: ").append(borderColor).append("; ");
       styleBuilder.append("-fx-border-width: ").append(borderWidth).append("; ");
       styleBuilder.append("-fx-border-radius: ").append(borderRadius).append("; ");
       styleBuilder.append("-fx-background-radius: ").append(borderRadius).append("; ");
       this.setStyle(styleBuilder.toString());
   }

} 
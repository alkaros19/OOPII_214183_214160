package gr.mycities.recommendation.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/*
 * custm field with a placeholder as appears in html pages
 */
public class PromptTextField extends JTextField {

    /**
	 * 
	 */
	private static final long serialVersionUID = -524441881454281462L;

	public PromptTextField(final String proptText) {
        super(proptText);
        addFocusListener(new FocusListener() { // vazoume enan listener sto focus

            @Override
            public void focusLost(FocusEvent e) {
            	// an i timi einai keni deixnoume to keimeno pou theloume
                if(getText().isEmpty()) {
                    setText(proptText);
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                // if the value is equals with the propt text, we set it to empty -> acts as a placeholder
                if(getText().equals(proptText)) {
                    setText("");
                }
            }
        });

    }

}
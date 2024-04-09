package Controller;

import java.io.File;
import javax.swing.UIManager;
import Model.Model;
import View.View;

public class Controller {
	private Model model;
	private View view;

	public Controller(Model model, View view) {
		this.model = model;
		this.view = view;
	}

	public void openButtonClicked() {
		File file = view.showOpenDialog();
		if (file != null) {
			model.loadFile(file);
			view.setText(model.getText());
		}
	}

	public void saveButtonClicked() {
		File file = view.showSaveDialog();
		if (file != null) {
			model.saveFile(file, view.getText());
		}
	}

	public static void main(String[] args) {

			Model model = new Model();
			View view = new View(new Controller(model, null));
			view.setVisible(true);
			view.setController(new Controller(model, view));
	}
}
package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartamentoService;

public class MainViewController implements Initializable{
	
	@FXML
	private MenuItem menuItemVendedor;
	@FXML
	private MenuItem menuItemDepartamento;
	@FXML
	private MenuItem menuItemSobre;
	
	@FXML
	public void onMenuItemVendedorAction() {
		System.out.println("teste de ação com botão menuItemVendedor");
	}
	@FXML
	public void onMenuItemDepartamentoAction() {
		carregarView("/gui/ListarDepartamentos.fxml", (ListarDepartamentosController controller) -> {
			controller.setDepartamentoService(new DepartamentoService());
			controller.atualizarTableView();
		});
		
	}
	@FXML
	public void onMenuItemSobreAction() {
		carregarView("/gui/sobre.fxml", x-> {});
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		
	}
	
	//carrega a view na no scroll pane principal
	private synchronized <T> void carregarView(String nomeCompletoView, Consumer<T> acaoDeInicianilizacao) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeCompletoView));
			VBox novoVbox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox)((ScrollPane)mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(novoVbox.getChildren());
			
			T controller = loader.getController();
			acaoDeInicianilizacao.accept(controller);
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro ao carregar a View", e.getMessage(), AlertType.ERROR);
		}
		
	}
}

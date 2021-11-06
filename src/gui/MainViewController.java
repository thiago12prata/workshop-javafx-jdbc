package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

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
		System.out.println("teste de ação com botão menuItemDepartamento");
	}
	@FXML
	public void onMmenuItemSobreAction() {
		System.out.println("teste de ação com botão menuItemSobre");
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		
	}
}

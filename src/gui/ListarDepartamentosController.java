package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Departamento;
import model.services.DepartamentoService;

public class ListarDepartamentosController implements Initializable {

	
	private DepartamentoService service;
	@FXML
	private TableView<Departamento> tableViewDepartamento;
	@FXML
	private TableColumn<Departamento, Integer> tableCollumnId;
	@FXML
	private TableColumn<Departamento, String> tableCollumnNome;
	@FXML
	private Button btNovo;
	private ObservableList<Departamento> obsList;
	
	@FXML
	public void onBtNovoAction() {
		System.out.println("bt novo");
	}
	public void setDepartamentoService(DepartamentoService service) {
		this.service=service;
	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarNodes();
	}
	private void inicializarNodes() {
		tableCollumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableCollumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		
		// Ajusta a tabela com a tela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartamento.prefHeightProperty().bind(stage.heightProperty());
	}
	public void atualizarTableView() {
		if (service==null) {
			throw new IllegalStateException("Serviço é igual a nulo");
		}
		
		List<Departamento> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartamento.setItems(obsList);
	}
}

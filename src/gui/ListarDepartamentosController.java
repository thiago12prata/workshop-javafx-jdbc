package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Departamento;
import model.services.DepartamentoService;

public class ListarDepartamentosController implements Initializable, DataChangeListener {

	private DepartamentoService service;
	@FXML
	private TableView<Departamento> tableViewDepartamento;
	@FXML
	private TableColumn<Departamento, Integer> tableCollumnId;
	@FXML
	private TableColumn<Departamento, String> tableCollumnNome;
	@FXML
	private Button btNovo;
	@FXML
	private TableColumn<Departamento, Departamento> tableColumnEDIT;
	
	private ObservableList<Departamento> obsList;

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage stagePai = Utils.stageAtual(event);
		Departamento obj = new Departamento();
		criarJanelaDialogo(obj, stagePai, "/gui/CadastroDepartamento.fxml");
	}

	public void setDepartamentoService(DepartamentoService service) {
		this.service = service;
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
		if (service == null) {
			throw new IllegalStateException("Serviço é igual a nulo");
		}
		
		List<Departamento> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartamento.setItems(obsList);
		iniciarEditButtons();
	}

	private void criarJanelaDialogo(Departamento obj, Stage stagePai, String nomeCompletoView) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeCompletoView));
			Pane pane = loader.load();
			Stage stageDialogo = new Stage();

			CadastroDepartamentosController controller = loader.getController();
			controller.setDepartamento(obj);
			controller.setDepartamentoService(new DepartamentoService());
			controller.subscribeDataChangeListener(this);
			controller.atualizarFormulario();

			stageDialogo.setTitle("Digite os dados do Departamento");
			stageDialogo.setScene(new Scene(pane));
			stageDialogo.setResizable(false);
			stageDialogo.initOwner(stagePai);
			stageDialogo.initModality(Modality.WINDOW_MODAL);
			stageDialogo.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro ao Carregar a tela", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		atualizarTableView();

	}

	private void iniciarEditButtons() {
		
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> criarJanelaDialogo(obj, Utils.stageAtual(event), "/gui/CadastroDepartamento.fxml"));
			}
		});
	}

}
